package ch.michu.tech.swissbudget.app.service;

import ch.michu.tech.swissbudget.framework.data.loading.DataLoader;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import java.nio.file.Path;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

@ApplicationScoped
public class DataLoaderService {

    private static final Path DEFAULT_USER_DATA = Path.of(DataLoaderService.class
        .getClassLoader()
        .getResource("sql/default-user-data.csv")
        .getPath());

    private final Provider<DataLoader> dataLoaderProvider;

    @Inject
    public DataLoaderService(Provider<DataLoader> dataLoaderProvider) {
        this.dataLoaderProvider = dataLoaderProvider;
    }

    public void insertUserDefaultData(UUID userId) {
        DataLoader loader = dataLoaderProvider.get();
        Queue<String> statements = loader.load(DEFAULT_USER_DATA, Map.of("user_id", userId));
        loader.store(statements);
    }
}
