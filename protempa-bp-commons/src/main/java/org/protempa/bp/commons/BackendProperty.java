package org.protempa.bp.commons;

import org.protempa.backend.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author Andrew Post
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BackendProperty {
    String propertyName() default "";
    String displayName() default "";
    String description() default "";
    Class<? extends BackendPropertyValidator> validator()
            default DefaultBackendPropertyValidator.class;
}
