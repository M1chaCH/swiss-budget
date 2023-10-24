package ch.michu.tech.swissbudget.app.service;

import ch.michu.tech.swissbudget.app.dto.TransactionDto;
import ch.michu.tech.swissbudget.app.provider.TransactionProvider;
import ch.michu.tech.swissbudget.app.transaction.TransactionImporter;
import ch.michu.tech.swissbudget.framework.data.RequestSupport;
import ch.michu.tech.swissbudget.framework.error.exception.ResourceNotFoundException;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.TransactionRecord;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import java.util.List;

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

    public List<TransactionDto> getTransactions() {
        return provider.selectTransactionsWithDependenciesByUserIdAsDto(supportProvider.get().getOrThrowUserId());
    }

    public List<TransactionDto> importTransactions() {
        return importer.importTransactions(supportProvider.get().getOrThrowUserId()).stream().map(TransactionDto::new).toList();
    }

    public void updateTransaction(TransactionDto toUpdate) {
        RequestSupport support = supportProvider.get();
        TransactionRecord transaction = provider.selectTransaction(support.getOrThrowUserId(), toUpdate.getId())
            .orElseThrow(() -> new ResourceNotFoundException("transaction", toUpdate.getId()));

        transaction.setTagId(toUpdate.getTagId());
        transaction.setMatchingKeywordId(toUpdate.getMatchingKeywordId());
        transaction.setAlias(toUpdate.getAlias());
        transaction.setNote(toUpdate.getNote());
        provider.updateTransaction(transaction);
    }
}
