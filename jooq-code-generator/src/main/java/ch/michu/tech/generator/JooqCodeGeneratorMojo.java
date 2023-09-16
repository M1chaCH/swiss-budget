package ch.michu.tech.generator;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.jooq.codegen.GenerationTool;
import org.jooq.meta.jaxb.Configuration;
import org.jooq.meta.jaxb.Database;
import org.jooq.meta.jaxb.Generator;
import org.jooq.meta.jaxb.Jdbc;
import org.jooq.meta.jaxb.Target;

@Mojo(name = "generate-code", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class JooqCodeGeneratorMojo extends AbstractMojo {

    public static final String GENERATION_PATH_PROP = "ch.michu.tech.jooq.generator.dir";
    public static final String GENERATION_DB_DIALECT_PROP = "ch.michu.tech.jooq.generator.dialect";
    public static final String GENERATION_DB_PACKAGE_PROP = "ch.michu.tech.jooq.generator.package";
    public static final String GENERATION_DB_INIT_SCRIPT_PROP = "ch.michu.tech.jooq.generator.init.script";
    public static final String DB_USER_PROP = "db.user";
    public static final String DB_PASSWORD_PROP = "db.password";
    public static final String DB_URL_PROP = "db.url";
    public static final String DB_SCHEMA_PROP = "db.schema";
    public static final String DB_DRIVER_PROP = "db.driver";
    private final Properties config = new Properties();

    private Log log;
    @Parameter(defaultValue = "false", readonly = true)
    private boolean skip;
    @Parameter()
    private String generationPath;
    @Parameter(required = true)
    private String dbConfigPath;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        log = getLog();
        log.info("Welcome to michu-tech.com custom jOOQ code generator");
        if (skip) {
            log.info("plugin skipped");
            return;
        }

        log.info(String.format("load datasource config from %s", dbConfigPath));
        try (FileInputStream input = new FileInputStream(dbConfigPath)) {
            config.load(input);
            log.debug(String.format("successfully loaded configs from %s", dbConfigPath));
        } catch (IOException e) {
            log.error("could not load given config: %s", e);
        }

        //ch.michu.tech.jooq.generator.dir
        if (generationPath == null) {
            log.debug(String.format(
                "file generation path not provided as parameter, searching for %s in given properties",
                GENERATION_PATH_PROP));
            generationPath = config.getProperty(GENERATION_PATH_PROP);
            if (generationPath == null) {
                throw new MojoFailureException(
                    String.format("parameter never found: %s", GENERATION_PATH_PROP));
            }
        }
        log.info(String.format("generating files to %s", generationPath));
        generateFiles();
        log.info("jOOQ File Generator completed!");
    }

    private void generateFiles() throws MojoExecutionException {
        String url = config.getProperty(DB_URL_PROP);
        String user = config.getProperty(DB_USER_PROP);
        String password = config.getProperty(DB_PASSWORD_PROP);
        String schema = config.getProperty(DB_SCHEMA_PROP, "public");
        String driver = config.getProperty(DB_DRIVER_PROP);
        String jooqDialect = config.getProperty(GENERATION_DB_DIALECT_PROP);
        String jooqPackage = config.getProperty(GENERATION_DB_PACKAGE_PROP);
        String initScript = loadInitScript(config);

        try {
            GenerationTool.generate(new Configuration()
                .withJdbc(new Jdbc()
                    .withUser(user)
                    .withUrl(url)
                    .withPassword(password)
                    .withDriver(driver)
                    .withInitScript(initScript))
                .withGenerator(new Generator()
                    .withName("org.jooq.codegen.JavaGenerator")
                    .withDatabase(new Database()
                        .withName(jooqDialect)
                        .withInputSchema(schema))
                    .withTarget(new Target()
                        .withPackageName(jooqPackage)
                        .withDirectory(generationPath))));
            log.info("successfully generated code");
        } catch (Exception e) {
            throw new MojoExecutionException(
                String.format("failed to generate code: %s", e.getClass().getSimpleName()), e);
        }
    }

    private String loadInitScript(Properties config) {
        String jooqInitScriptSource = config.getProperty(GENERATION_DB_INIT_SCRIPT_PROP);
        if (jooqInitScriptSource == null) {
            return "";
        }

        try {
            Path path = Path.of(jooqInitScriptSource);
            byte[] byteScript = Files.readAllBytes(path);
            String script = new String(byteScript, StandardCharsets.UTF_8);
            log.info(String.format("loaded init script at %s", path.toAbsolutePath()));
            return script;
        } catch (IOException e) {
            log.warn(String.format("could not find init script at %s", jooqInitScriptSource));
        }
        return "";
    }
}
