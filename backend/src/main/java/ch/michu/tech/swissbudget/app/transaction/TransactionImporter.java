package ch.michu.tech.swissbudget.app.transaction;

import static ch.michu.tech.swissbudget.generated.jooq.tables.RegisteredUser.REGISTERED_USER;
import static ch.michu.tech.swissbudget.generated.jooq.tables.Transaction.TRANSACTION;
import static ch.michu.tech.swissbudget.generated.jooq.tables.TransactionMail.TRANSACTION_MAIL;
import static ch.michu.tech.swissbudget.generated.jooq.tables.TransactionMetaData.TRANSACTION_META_DATA;

import ch.michu.tech.swissbudget.app.exception.BankNotSupportedException;
import ch.michu.tech.swissbudget.app.exception.UnexpectedDbException;
import ch.michu.tech.swissbudget.app.transaction.mail.MailContentHandler;
import ch.michu.tech.swissbudget.framework.data.DataProvider;
import ch.michu.tech.swissbudget.framework.error.ErrorReporter;
import ch.michu.tech.swissbudget.framework.error.exception.ResourceNotFoundException;
import ch.michu.tech.swissbudget.framework.error.exception.UnexpectedServerException;
import ch.michu.tech.swissbudget.framework.mail.MailReader;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.TransactionMailRecord;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.TransactionRecord;
import io.helidon.microprofile.scheduling.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.Result;
import org.jooq.exception.DataAccessException;

@ApplicationScoped
public class TransactionImporter {

    public static final int MAX_IMPORT_SINCE_YEARS = 2;

    private static final Logger LOGGER = Logger.getLogger(TransactionImporter.class.getSimpleName());

    private final DataProvider db;
    private final MailReader mailReader;
    private final ErrorReporter errorReporter;
    private final int userAmountMultithreadingBreakpoint;

    @Inject
    public TransactionImporter(DataProvider db, MailReader mailReader, ErrorReporter errorReporter,
        @ConfigProperty(name = "ch.michu.tech.threads.user-amount-breakpoint", defaultValue = "8") int userAmountMultithreadingBreakpoint) {
        this.db = db;
        this.mailReader = mailReader;
        this.errorReporter = errorReporter;
        this.userAmountMultithreadingBreakpoint = userAmountMultithreadingBreakpoint;
    }

    public Map<TransactionMailRecord, TransactionRecord> importTransactions(String userId) {
        Result<?> result = db.getContext()
            .select(REGISTERED_USER.MAIL, REGISTERED_USER.MAIL_PASSWORD, REGISTERED_USER.ID, TRANSACTION_META_DATA.LAST_TRANSACTION_IMPORT,
                TRANSACTION_META_DATA.TRANSACTIONS_FOLDER, TRANSACTION_META_DATA.BANK).from(REGISTERED_USER).join(TRANSACTION_META_DATA)
            .on(TRANSACTION_META_DATA.USER_ID.eq(REGISTERED_USER.ID)).where(REGISTERED_USER.ID.eq(userId)).fetch();

        if (result.size() != 1) {
            throw new ResourceNotFoundException("user", userId);
        }

        LocalDateTime lastImport = result.getValue(0, TRANSACTION_META_DATA.LAST_TRANSACTION_IMPORT);
        lastImport = lastImport == null ? LocalDateTime.now().minusYears(MAX_IMPORT_SINCE_YEARS) : lastImport;

        ImportDbData data = new ImportDbData(result.getValue(0, REGISTERED_USER.ID), result.getValue(0, REGISTERED_USER.MAIL),
            result.getValue(0, REGISTERED_USER.MAIL_PASSWORD), lastImport, result.getValue(0, TRANSACTION_META_DATA.TRANSACTIONS_FOLDER),
            result.getValue(0, TRANSACTION_META_DATA.BANK));

        LOGGER.log(Level.FINE, "selected transaction import data for {0}", new Object[]{userId});
        return importTransactions(data);
    }


    public Map<TransactionMailRecord, TransactionRecord> importTransactions(ImportDbData dbData) {
        long startSeconds = Instant.now().getEpochSecond();
        LOGGER.log(Level.INFO, "importing transactions for {0}", new Object[]{dbData.mail});
        DSLContext ctx = db.getContext();

        SupportedBank bank = SupportedBank.fromKey(dbData.bank).orElseThrow(() -> new BankNotSupportedException(dbData.mail, dbData.bank));
        final Map<TransactionMailRecord, TransactionRecord> parsedTransactions = new HashMap<>();

        try (Store mailConnection = mailReader.openConnection(dbData.mail, dbData.password)) {
            LocalDateTime lastImportedMail = dbData.lastImport;

            MailContentHandler contentHandler = bank.getHandler().getDeclaredConstructor().newInstance();
            Message[] messages = mailReader.findMessages(mailConnection, dbData.folder, lastImportedMail);
            int skippedCount = 0;
            for (Message message : messages) {
                TransactionMailRecord mail = ctx.newRecord(TRANSACTION_MAIL);
                mail.setUserId(dbData.id);
                contentHandler.parseMail(mail, message);

                if (!contentHandler.validateFromBank(mail)) {
                    skippedCount++;
                    continue;
                }

                TransactionRecord transaction = ctx.newRecord(TRANSACTION);
                transaction.setUserId(dbData.id);
                contentHandler.parseTransaction(transaction, mail);

                mail.setTransactionId(transaction.getId());

                lastImportedMail = mail.getReceivedDate();
                parsedTransactions.put(mail, transaction);
            }

            updateLastImport(dbData.id, lastImportedMail);
            insertTransactions(parsedTransactions);
            long durationSeconds = Instant.now().getEpochSecond() - startSeconds;
            LOGGER.log(Level.INFO, "successfully imported {0} transactions for {1}. (skipped: {2}, took: {3}s)",
                new Object[]{parsedTransactions.size(), dbData.mail, skippedCount, durationSeconds});
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new UnexpectedServerException("could not instantiate MailContentHandler for " + bank.name(), e);
        } catch (MessagingException e) {
            throw new UnexpectedServerException("could not find or download messages", e);
        }

        return parsedTransactions;
    }

    //   cron schedule: <seconds> <minutes> <hours> <day-of-month> <month> <day-of-week> <year>
    @SuppressWarnings("unused") // is used by helidon schedule
    @Scheduled("0 0 3 ? * *")
    public void importTransactionsForAllUsers() {
        try {
            List<ImportDbData> data = selectImportData();
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

    private List<ImportDbData> selectImportData() {
        Result<?> result = db.getContext()
            .select(REGISTERED_USER.MAIL, REGISTERED_USER.MAIL_PASSWORD, REGISTERED_USER.ID, TRANSACTION_META_DATA.LAST_TRANSACTION_IMPORT,
                TRANSACTION_META_DATA.TRANSACTIONS_FOLDER, TRANSACTION_META_DATA.BANK).from(REGISTERED_USER).join(TRANSACTION_META_DATA)
            .on(TRANSACTION_META_DATA.USER_ID.eq(REGISTERED_USER.ID)).fetch();

        List<ImportDbData> importedDbData = new ArrayList<>();
        for (int i = 0; i < result.size(); i++) {
            LocalDateTime lastImport = result.getValue(i, TRANSACTION_META_DATA.LAST_TRANSACTION_IMPORT);
            lastImport = lastImport == null ? LocalDateTime.now().minusYears(MAX_IMPORT_SINCE_YEARS) : lastImport;

            importedDbData.add(new ImportDbData(result.getValue(i, REGISTERED_USER.ID), result.getValue(i, REGISTERED_USER.MAIL),
                result.getValue(i, REGISTERED_USER.MAIL_PASSWORD), lastImport,
                result.getValue(i, TRANSACTION_META_DATA.TRANSACTIONS_FOLDER), result.getValue(i, TRANSACTION_META_DATA.BANK)));
        }

        LOGGER.log(Level.FINE, "selected {0} rows (users x meta-data) for transaction import", new Object[]{importedDbData.size()});
        return importedDbData;
    }

    private void updateLastImport(String id, LocalDateTime timestamp) {
        CompletionStage<Integer> completion = db.getContext().update(TRANSACTION_META_DATA)
            .set(TRANSACTION_META_DATA.LAST_TRANSACTION_IMPORT, timestamp).where(TRANSACTION_META_DATA.USER_ID.eq(id)).executeAsync();

        completion.thenAccept(i -> LOGGER.log(Level.FINE, "successfully updated last import of {0} to {1}", new Object[]{id, timestamp}))
            .exceptionally(e -> {
                if (e instanceof DataAccessException dataAccessException) {
                    throw new UnexpectedDbException(dataAccessException);
                }
                throw new UnexpectedServerException("failed to update last import for userid:" + id, new Exception(e));
            });
    }

    private void insertTransactions(Map<TransactionMailRecord, TransactionRecord> transactions) {
        DSLContext ctx = db.getContext();
        List<Query> queries = new ArrayList<>();

        for (Map.Entry<TransactionMailRecord, TransactionRecord> mailTransaction : transactions.entrySet()) {
            queries.add(ctx.insertInto(TRANSACTION, TRANSACTION.fields()).values(mailTransaction.getValue()));
            queries.add(ctx.insertInto(TRANSACTION_MAIL, TRANSACTION_MAIL.fields()).values(mailTransaction.getKey()));
        }

        CompletionStage<int[]> completion = ctx.batch(queries).executeAsync();
        completion.thenAccept(i -> LOGGER.log(Level.FINE, "successfully inserted {0} mail transactions", new Object[]{transactions.size()}))
            .exceptionally(e -> {
                if (e instanceof DataAccessException dataAccessException) {
                    throw new UnexpectedDbException(dataAccessException);
                }
                throw new UnexpectedServerException("failed to insert mail transactions", new Exception(e));
            });
    }

    public record ImportDbData(String id, String mail, String password, LocalDateTime lastImport, String folder, String bank) {

    }
}
