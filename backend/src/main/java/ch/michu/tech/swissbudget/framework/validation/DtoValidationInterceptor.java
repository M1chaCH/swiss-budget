package ch.michu.tech.swissbudget.framework.validation;

import ch.michu.tech.swissbudget.framework.error.exception.DtoValidationException;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

// TODO load DTO definitions on startup for faster checks during runtime
@ValidateDtos
@Interceptor
public class DtoValidationInterceptor {

    private static final Logger LOGGER = Logger.getLogger(DtoValidationInterceptor.class.getSimpleName());

    private final Pattern mailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    /**
     * how to use:
     * <ol>
     *     <li>annotate method where parameters should be validated with {@link ValidateDtos}</li>
     *     <li>annotate class of parameter (dto parameter) with {@link ValidatedDto}</li>
     *     <li>annotate fields in dto class with validator annotations</li>
     *
     * </ol>
     * this will intercept all methods annotated with {@link ValidateDtos} and validate all parameters of these methods where the class of
     * the parameter is annotated with {@link ValidatedDto}.
     * if the validation fails then a {@link DtoValidationException} is thrown
     *
     * @param context the CDI InvocationContext
     * @return the return object of the originally called method
     * @throws DtoValidationException if the validation fails
     * @throws Exception              the exceptions thrown in the original method
     */
    @AroundInvoke
    public Object intercept(InvocationContext context) throws Exception {
        LOGGER.log(Level.FINE, "validating parameters of {0}->{1}",
            new Object[]{context.getTarget().getClass().getSimpleName(), context.getMethod().getName()});
        for (Object arg : context.getParameters()) {
            if (arg != null && arg.getClass().isAnnotationPresent(ValidatedDto.class)) {
                validateArg(arg);
            }
        }

        return context.proceed();
    }

    /**
     * validates an argument of a method. it is expected that the argument's class is annotated with {@link ValidatedDto}
     *
     * @param arg the argument annotated with {@link ValidatedDto} to validate
     */
    @SuppressWarnings("java:S3011") // don't care about access warning
    protected void validateArg(Object arg) {
        LOGGER.log(Level.FINE, "validating argument: {0}", new Object[]{arg.getClass().getSimpleName()});
        for (Field fieldInArg : arg.getClass().getDeclaredFields()) {
            try {
                fieldInArg.setAccessible(true);

                boolean nullable = fieldInArg.isAnnotationPresent(Nullable.class);

                Object fieldValue = fieldInArg.get(arg);
                if (fieldValue == null) {
                    if (!nullable) {
                        throw new DtoValidationException(arg, fieldInArg, Nullable.class);
                    }

                    continue;
                }

                String fieldStringValue = fieldValue.toString();

                if (fieldInArg.isAnnotationPresent(ValidateMail.class) && !mailPattern.matcher(fieldStringValue).matches()) {
                    throw new DtoValidationException(arg, fieldInArg, ValidateMail.class);
                }

                ValidateLength validateLengthAnnotation = fieldInArg.getAnnotation(ValidateLength.class);
                if (validateLengthAnnotation != null && (fieldStringValue.length() < validateLengthAnnotation.min()
                    || fieldStringValue.length() > validateLengthAnnotation.max())) {
                    throw new DtoValidationException(arg, fieldInArg, ValidateLength.class);
                }

                if (!validateAmount(fieldInArg, fieldStringValue)) {
                    throw new DtoValidationException(arg, fieldInArg, ValidateAmount.class);
                }

            } catch (IllegalAccessException e) {
                LOGGER.log(Level.WARNING, "IllegalAccess while trying to validate field {0} of type {1}",
                    new Object[]{fieldInArg.getName(), arg.getClass().getSimpleName()});
            }
        }
        LOGGER.log(Level.FINE, "{0} is valid", new Object[]{arg.getClass().getSimpleName()});
    }

    private boolean validateAmount(Field field, String fieldValue) {
        ValidateAmount validateAmountAnnotation = field.getAnnotation(ValidateAmount.class);
        if (validateAmountAnnotation != null) {
            try {
                double value = Double.parseDouble(fieldValue);
                if (value < validateAmountAnnotation.min() || value > validateAmountAnnotation.max()) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }
}
