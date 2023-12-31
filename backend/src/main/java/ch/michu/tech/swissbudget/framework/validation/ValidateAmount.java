package ch.michu.tech.swissbudget.framework.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * uses double for the validation. if not parsable, then validation fails
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateAmount {

    double max() default Double.MAX_VALUE;

    double min() default Double.MIN_VALUE;
}
