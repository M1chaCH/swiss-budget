package ch.michu.tech.swissbudget.test;

import static org.junit.jupiter.api.Assertions.fail;

import io.helidon.microprofile.testing.junit5.AddConfig;
import io.helidon.microprofile.testing.junit5.HelidonTest;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;

@HelidonTest()
@AddConfig(key = "mp.config.profile", value = "test")
public abstract class AppIntegrationTest {

    protected final Logger LOGGER = Logger.getLogger(getClass().getSimpleName());
    protected final TestDataManager data;
    protected final TestHttpClient client;

    protected AppIntegrationTest(String path, TestDataManager data, TestHttpClient client) {
        this.data = data;
        this.client = client;
        client.setDefaultPath(path);

        try {
            data.initDb(); // init DB on the very first run
        } catch (IOException | SQLException e) {
            fail(e);
        }
    }

    @AfterEach
    public void cleanup() throws SQLException {
        if (wasDataModified()) {
            data.applyTestData();
        }
        client.closeResponses();
    }

    protected boolean wasDataModified() {
        return true;
    }
}
