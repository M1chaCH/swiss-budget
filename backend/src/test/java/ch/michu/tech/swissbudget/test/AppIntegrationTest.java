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
            data.initDbIfRequired();
            data.applyTestData(useDemoUser());
        } catch (IOException | SQLException e) {
            fail(e);
        }
    }

    @AfterEach
    public void cleanup() throws SQLException { // TODO for the future: only cleanup changed tables
        if (wasDataModified()) {
            data.applyTestData(useDemoUser());
        }
        client.closeResponses();
    }

    protected boolean wasDataModified() {
        return true;
    }

    protected boolean useDemoUser() {
        return false;
    }
}
