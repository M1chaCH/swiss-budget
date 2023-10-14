package ch.michu.tech.swissbudget.framework.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * if a value in a DTO is null than the validator throws an exception. but if you annotate it with this, then null is accepted. if a value
 * is null then all other validators are skipped, but if a value is not null then all the other validators for the field are executed
 * normally
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Nullable {

}
