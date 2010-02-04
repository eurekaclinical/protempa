package org.protempa.bp.commons;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author Andrew Post
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface BackendInfo {
    static final String DISPLAY_NAME_NULL = "";
    static final String PROPERTIES_BASE_NAME_NULL = "";
    
    String displayName() default DISPLAY_NAME_NULL;
    String propertiesBaseName() default PROPERTIES_BASE_NAME_NULL;
}
