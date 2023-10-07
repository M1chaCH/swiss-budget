package ch.michu.tech.swissbudget.app.service;

import static ch.michu.tech.swissbudget.generated.jooq.tables.Transaction.TRANSACTION;

import ch.michu.tech.swissbudget.app.dto.TransactionDto;
import ch.michu.tech.swissbudget.app.transaction.TransactionImporter;
import ch.michu.tech.swissbudget.framework.data.DataProvider;
import ch.michu.tech.swissbudget.framework.data.RequestSupport;
import ch.michu.tech.swissbudget.framework.error.exception.InvalidSessionTokenException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import java.util.List;

@ApplicationScoped
public class TransactionService {

    private final Provider<RequestSupport> supportProvider;
    private final TransactionImporter transactionImporter;
    private final DataProvider db;

    @Inject
    public TransactionService(Provider<RequestSupport> supportProvider, TransactionImporter transactionImporter, DataProvider db) {
        this.supportProvider = supportProvider;
        this.transactionImporter = transactionImporter;
        this.db = db;
    }

    public List<TransactionDto> getTransactions(boolean includeImport) {
        RequestSupport support = supportProvider.get();
        String userId = support.getSessionToken().orElseThrow(InvalidSessionTokenException::new).getUserId();

        if (includeImport) {
            transactionImporter.importTransactions(userId);
        }

        List<TransactionDto> transactions = db.getContext()
            .selectFrom(TRANSACTION)
            .where(TRANSACTION.USER_ID.eq(userId))
            .orderBy(TRANSACTION.TRANSACTION_DATE.desc())
            .fetch().map(TransactionDto::new);
        support.logFine(this, "selected %s transactions", transactions.size());
        return transactions;
    }
}
