package ch.michu.tech.swissbudget.app.service;

import ch.michu.tech.swissbudget.app.dto.transaction.TransactionDto;
import ch.michu.tech.swissbudget.app.provider.TransactionProvider;
import ch.michu.tech.swissbudget.app.transaction.TransactionImporter;
import ch.michu.tech.swissbudget.framework.data.RequestSupport;
import ch.michu.tech.swissbudget.framework.dto.PaginationResultDto;
import ch.michu.tech.swissbudget.framework.error.exception.ResourceNotFoundException;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.TransactionRecord;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class TransactionService {

    private final Provider<RequestSupport> supportProvider;
    private final TransactionImporter importer;
    private final TransactionProvider provider;

    @Inject
    public TransactionService(Provider<RequestSupport> supportProvider, TransactionImporter importer, TransactionProvider provider) {
        this.supportProvider = supportProvider;
        this.importer = importer;
        this.provider = provider;
    }

    public PaginationResultDto<TransactionDto> getTransactions(
        String query, UUID[] tagIds, LocalDate from, LocalDate to, boolean needAttention, int page
    ) {
        final RequestSupport support = supportProvider.get();
        List<TransactionDto> data = provider.selectTransactionsWithDependenciesWithFilterWithPageAsDto(support.db(),
                                                                                                       support.getUserIdOrThrow(),
                                                                                                       query,
                                                                                                       tagIds,
                                                                                                       from,
                                                                                                       to,
                                                                                                       needAttention,
                                                                                                       page);
        long count = provider.countTransactions(support.db(), support.getUserIdOrThrow(), query, tagIds, from, to, needAttention);

        return new PaginationResultDto<>(provider.getPageSize(), count, data);
    }

    /**
     * imports all new transactions for a user.
     *
     * @return true: if some transactions were imported
     */
    public boolean importTransactions() {
        final RequestSupport support = supportProvider.get();
        return !importer.importTransactions(support.db(), support.getUserIdOrThrow()).isEmpty();
    }

    public void updateTransactionUserInput(TransactionDto toUpdate) {
        final RequestSupport support = supportProvider.get();
        TransactionRecord transaction = provider.selectTransaction(support.db(), support.getUserIdOrThrow(), toUpdate.getId())
                                                .orElseThrow(() -> new ResourceNotFoundException("transaction", toUpdate.getId()));

        transaction.setAlias(toUpdate.getAlias());
        transaction.setNote(toUpdate.getNote());
        provider.updateTransactionUserInput(transaction);
    }
}
