package ch.michu.tech.swissbudget.app.service;

import ch.michu.tech.swissbudget.app.dto.TransactionDto;
import ch.michu.tech.swissbudget.app.provider.TransactionProvider;
import ch.michu.tech.swissbudget.app.transaction.TransactionImporter;
import ch.michu.tech.swissbudget.framework.data.RequestSupport;
import ch.michu.tech.swissbudget.framework.error.exception.InvalidSessionTokenException;
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
        RequestSupport support = supportProvider.get();
        String userId = support.getSessionToken().orElseThrow(InvalidSessionTokenException::new).getUserId();
        return provider.selectTransactionsWithDependenciesByUserIdAsDto(userId);
    }

    public List<TransactionDto> importTransactions() {
        RequestSupport support = supportProvider.get();
        String userId = support.getSessionToken().orElseThrow(InvalidSessionTokenException::new).getUserId();
        return importer.importTransactions(userId).stream().map(TransactionDto::new).toList();
    }
}
