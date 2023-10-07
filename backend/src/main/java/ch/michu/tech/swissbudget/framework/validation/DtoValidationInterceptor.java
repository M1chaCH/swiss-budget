package ch.michu.tech.swissbudget.framework.validation;

import ch.michu.tech.swissbudget.framework.error.exception.DtoValidationException;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

@ValidateDtos
@Interceptor
public class DtoValidationInterceptor {

    private static final Logger LOGGER = Logger.getLogger(DtoValidationInterceptor.class.getSimpleName());

    private final Pattern mailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    @AroundInvoke
    public Object intercept(InvocationContext context) throws Exception {
        LOGGER.log(Level.INFO, "validating parameters of {0}->{1}",
            new Object[]{context.getTarget().getClass().getSimpleName(), context.getMethod().getName()});
        for (Object arg : context.getParameters()) {
            if (arg.getClass().isAnnotationPresent(ValidatedDto.class)) {
                validateArg(arg);
            }
        }

        return context.proceed();
    }

    @SuppressWarnings("java:S3011") // don't care about access warning
    protected void validateArg(Object arg) {
        LOGGER.log(Level.FINE, "validating parameter: {0}", new Object[]{arg.getClass().getSimpleName()});
        for (Field field : arg.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);

                boolean nullable = field.isAnnotationPresent(Nullable.class);

                Object fieldObject = field.get(arg);
                if (fieldObject == null) {
                    if (!nullable) {
                        throw new DtoValidationException(arg, field, Nullable.class);
                    }

                    continue;
                }

                String fieldValue = fieldObject.toString();

                if (field.isAnnotationPresent(ValidateMail.class) && !mailPattern.matcher(fieldValue).matches()) {
                    throw new DtoValidationException(arg, field, ValidateMail.class);
                }

                ValidateLength validateLengthAnnotation = field.getAnnotation(ValidateLength.class);
                if (validateLengthAnnotation != null && (fieldValue.length() < validateLengthAnnotation.min()
                    || fieldValue.length() > validateLengthAnnotation.max())) {
                    throw new DtoValidationException(arg, field, ValidateLength.class);
                }

                if (!validateAmount(field, fieldValue)) {
                    throw new DtoValidationException(arg, field, ValidateAmount.class);
                }

            } catch (IllegalAccessException e) {
                LOGGER.log(Level.WARNING, "IllegalAccess while trying to validate field {0} of type {1}",
                    new Object[]{field.getName(), arg.getClass().getSimpleName()});
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
