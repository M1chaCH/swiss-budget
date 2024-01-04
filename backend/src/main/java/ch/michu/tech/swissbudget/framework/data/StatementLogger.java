package ch.michu.tech.swissbudget.framework.data;

import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import java.lang.reflect.Parameter;
import java.time.Instant;
import java.util.Collection;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@LoggedStatement
@Interceptor
public class StatementLogger {

    private static final Logger LOGGER = Logger.getLogger(StatementLogger.class.getSimpleName());

    private final int longRunningStatement;

    @Inject
    public StatementLogger(@ConfigProperty(name = "db.time.long.breakpoint", defaultValue = "20") int longRunningStatement) {
        this.longRunningStatement = longRunningStatement;
    }

    @AroundInvoke
    public Object intercept(InvocationContext context) throws Exception {
        final long startMillis = Instant.now().toEpochMilli();

        // find a name for the statement
        StringJoiner paramJoiner = new StringJoiner(", ");
        final Parameter[] parameters = context.getMethod().getParameters();
        final Object[] parameterValues = context.getParameters();
        // start at index 1: usually the providers have the DB as the first property, we don't need to always log this.
        for (int i = 1; i < parameters.length; i++) {
            String value;
            if (parameterValues[i] instanceof Collection<?> list) {
                value = "[size:%s]".formatted(list.size());
            } else {
                if (parameterValues[i] == null) {
                    value = "null";
                } else {
                    value = parameterValues[i].toString();
                }
            }
            if (value.length() > 30) {
                value = value.substring(0, 29) + ".";
            }

            paramJoiner.add("%s: %s".formatted(parameters[i].getType().getSimpleName(), value));
        }
        final String name = "%s->%s(%s)".formatted(context.getMethod().getDeclaringClass().getSimpleName(), context.getMethod().getName(),
                                                   paramJoiner.toString());

        Object result = context.proceed();
        final long durationMillis = Instant.now().toEpochMilli() - startMillis;
        logStatement(name, result, durationMillis);
        return result;
    }

    private void logStatement(String name, Object result, long duration) {
        long amount = 1;
        if (result instanceof Collection<?> list) {
            amount = list.size();
        }

        if (duration >= longRunningStatement) {
            LOGGER.log(Level.WARNING, "(LONG RUNNING) {0} - {1}ms - {2} results", new Object[]{name, duration, amount});
        } else {
            LOGGER.log(Level.FINE, "{0} - {1}ms - {2} results", new Object[]{name, duration, amount});
        }
    }
}
