package ch.michu.tech.swissbudget.framework.interceptor.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateLength {

    int max() default Integer.MAX_VALUE;

    int min() default 1;
}
