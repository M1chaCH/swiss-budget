package ch.michu.tech.swissbudget.app.transaction;

import ch.michu.tech.swissbudget.app.entity.CompleteTransactionEntity;
import ch.michu.tech.swissbudget.app.exception.BankNotSupportedException;
import ch.michu.tech.swissbudget.app.exception.ProcessAlreadyStartedException;
import ch.michu.tech.swissbudget.app.exception.UnexpectedDbException;
import ch.michu.tech.swissbudget.app.provider.TagProvider;
import ch.michu.tech.swissbudget.app.provider.TransactionProvider;
import ch.michu.tech.swissbudget.app.provider.TransactionProvider.ImportDbData;
import ch.michu.tech.swissbudget.app.transaction.mail.MailContentHandler;
import ch.michu.tech.swissbudget.framework.error.ErrorReporter;
import ch.michu.tech.swissbudget.framework.error.exception.UnexpectedServerException;
import ch.michu.tech.swissbudget.framework.mail.MailReader;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.KeywordRecord;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.TagRecord;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.TransactionMailRecord;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.TransactionRecord;
import io.helidon.microprofile.scheduling.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jooq.exception.DataAccessException;

@ApplicationScoped
public class TransactionImporter {

    public static final int MAX_IMPORT_SINCE_YEARS = 2;

    private static final Logger LOGGER = Logger.getLogger(TransactionImporter.class.getSimpleName());

    private final TransactionProvider transactionProvider;
    private final TagProvider tagProvider;
    private final MailReader mailReader;
    private final ErrorReporter errorReporter;
    private final TransactionTagMapper tagMapper;
    private final int userAmountMultithreadingBreakpoint;

    private final List<String> currentUserIds = new ArrayList<>();

    @Inject
    public TransactionImporter(TransactionProvider transactionProvider, TagProvider tagProvider, MailReader mailReader,
        ErrorReporter errorReporter,
        TransactionTagMapper tagMapper,
        @ConfigProperty(name = "ch.michu.tech.threads.user-amount-breakpoint", defaultValue = "8") int userAmountMultithreadingBreakpoint) {
        this.transactionProvider = transactionProvider;
        this.tagProvider = tagProvider;
        this.mailReader = mailReader;
        this.errorReporter = errorReporter;
        this.tagMapper = tagMapper;
        this.userAmountMultithreadingBreakpoint = userAmountMultithreadingBreakpoint;
    }

    /**
     * same as here {@link #importTransactions(ImportDbData)}
     * <p>
     * (also select the required data)
     *
     * @param userId the user to import the transactions for
     * @return the imported transactions
     */
    public List<CompleteTransactionEntity> importTransactions(String userId) {
        return importTransactions(transactionProvider.selectImportDataByUserId(userId));
    }

    /**
     * imports the transactions according to the ImportDbDate "struct (:" <br> tags are also already mapped and all changes are stored in
     * the db <br> import is only done every hour (if last import was less than an hour ago then an empty map is returned)<br> mails older
     * than {@link #MAX_IMPORT_SINCE_YEARS} won't be imported
     *
     * @param dbData the data required for the import
     * @return all imported transaction mails with their transactions with their tag and the matching keyword
     */
    public List<CompleteTransactionEntity> importTransactions(ImportDbData dbData) {
        long startSeconds = Instant.now().getEpochSecond();
        handleAlreadyStarted(dbData.id());
        LOGGER.log(Level.INFO, "importing transactions for {0}", new Object[]{dbData.mail()});

        SupportedBank bank = SupportedBank.fromKey(dbData.bank())
            .orElseThrow(() -> new BankNotSupportedException(dbData.mail(), dbData.bank()));
        final List<CompleteTransactionEntity> transactions = new ArrayList<>();

        long minutesSinceLastImport = dbData.lastImportCheck().until(LocalDateTime.now(), ChronoUnit.MINUTES);
        if (minutesSinceLastImport < 60) {
            LOGGER.log(Level.INFO, "last import was {0} minutes ago -> skipping this import (needs to be more than 1 hour)",
                new Object[]{minutesSinceLastImport});
            currentUserIds.remove(dbData.id());
            return transactions;
        }

        try (Store mailConnection = mailReader.openConnection(dbData.mail(), dbData.password())) {
            LocalDateTime lastImportedMail = dbData.lastImportedTransaction();

            MailContentHandler contentHandler = bank.getHandler().getDeclaredConstructor().newInstance();
            Message[] messages = mailReader.findMessages(mailConnection, dbData.folder(), lastImportedMail);

            Map<TagRecord, List<KeywordRecord>> tags = tagProvider.selectTagsWithKeywordsByUserId(dbData.id());
            TagRecord defaultTag = tagMapper.getDefaultTag(tags.keySet());

            int skippedCount = 0;
            for (Message message : messages) {
                CompleteTransactionEntity entity = new CompleteTransactionEntity();
                TransactionMailRecord mail = transactionProvider.newTransactionMail();
                mail.setUserId(dbData.id());
                contentHandler.parseMail(mail, message);
                entity.setMail(mail);

                if (!contentHandler.validateFromBank(mail)) {
                    skippedCount++;
                    continue;
                }

                TransactionRecord transaction = transactionProvider.newTransaction();
                transaction.setUserId(dbData.id());
                contentHandler.parseTransaction(transaction, mail);

                entity.setTransaction(transaction);
                mail.setTransactionId(transaction.getId());
                lastImportedMail = mail.getReceivedDate();

                tagMapper.map(entity, tags, defaultTag);
                transactions.add(entity);
            }

            transactionProvider.updateLastImport(dbData.id(), lastImportedMail);
            transactionProvider.insertCompleteTransactions(transactions);
            long durationSeconds = Instant.now().getEpochSecond() - startSeconds;
            LOGGER.log(Level.INFO, "successfully imported {0} transactions for {1}. (skipped: {2}, took: {3}s)",
                new Object[]{transactions.size(), dbData.mail(), skippedCount, durationSeconds});
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new UnexpectedServerException("could not instantiate MailContentHandler for " + bank.name(), e);
        } catch (MessagingException e) {
            throw new UnexpectedServerException("could not find or download messages", e);
        }

        currentUserIds.remove(dbData.id());
        return transactions;
    }

    /**
     * triggered by helidon schedule! imports all transactions for all users every night at 3 AM
     */
    //   cron schedule: <seconds> <minutes> <hours> <day-of-month> <month> <day-of-week> <year>
    @SuppressWarnings("unused") // is used by helidon schedule
    @Scheduled("0 0 3 ? * *")
    public void importTransactionsForAllUsers() {
        try {
            List<ImportDbData> data = transactionProvider.selectImportData();
            int userCount = data.size();
            if (userCount >= userAmountMultithreadingBreakpoint) {
                LOGGER.log(Level.INFO, "importing transactions (Multithreaded ({0} Threads)) for {1} users",
                    new Object[]{getThreadPoolSize(userCount), userCount});
                importAllMT(data);
            } else {
                long start = Instant.now().getEpochSecond();
                LOGGER.log(Level.INFO, "importing transactions (Single thread) for {0} users", new Object[]{userCount});
                data.forEach(this::importTransactions);
                LOGGER.log(Level.INFO, "completed transactions import after {0} minutes",
                    new Object[]{(Instant.now().getEpochSecond() - start) / 60d});
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "failed to run transaction import schedule", e);
            if (e instanceof DataAccessException dataAccessException) {
                errorReporter.reportError(new UnexpectedDbException(dataAccessException));
            } else {
                errorReporter.reportError(new UnexpectedServerException("scheduled transaction import failed", e));
            }
        }
    }

    private void importAllMT(List<ImportDbData> data) {
        long start = Instant.now().getEpochSecond();
        @SuppressWarnings({"java:S2095", "resource"})
        ExecutorService executor = Executors.newFixedThreadPool(getThreadPoolSize(data.size()));

        try {
            data.forEach(row -> executor.submit(() -> importTransactions(row)));
            if (executor.awaitTermination(55, TimeUnit.MINUTES)) {
                LOGGER.log(Level.INFO, "successfully ran MT transaction importer! total duration: {0} minutes",
                    new Object[]{(Instant.now().getEpochSecond() - start) / 60d});
            } else {
                LOGGER.log(Level.WARNING, "MT transaction importer TIMED OUT after {0} minutes!",
                    new Object[]{(Instant.now().getEpochSecond() - start) / 60d});
            }
        } catch (InterruptedException e) {
            errorReporter.reportError(new Exception("MT transaction importer interrupted", e));
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
        }
    }

    private int getThreadPoolSize(int userCount) {
        return Math.min(Math.max(1, userCount / 4), 12);
    }

    private synchronized void handleAlreadyStarted(String userId) {
        if (currentUserIds.contains(userId)) {
            throw new ProcessAlreadyStartedException("transaction import");
        }
        currentUserIds.add(userId);
    }
}
