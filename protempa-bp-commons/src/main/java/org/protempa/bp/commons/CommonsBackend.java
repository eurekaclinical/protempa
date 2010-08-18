package org.protempa.bp.commons;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.protempa.Backend;
import org.protempa.backend.BackendInstanceSpec;
import org.protempa.backend.InvalidPropertyNameException;

/**
 *
 * @author Andrew Post
 */
public class CommonsBackend {
    
    public static BackendInfo backendInfo(Backend backend) {
        return backend.getClass().getAnnotation(BackendInfo.class);
    }

    public static String nameForErrors(Backend backend) {
        BackendInfo backendInfo = backendInfo(backend);
        if (backendInfo != null)
            return backendInfo.displayName() + " (" + 
                    backend.getClass().getName() + ")";
        else
            return backend.getClass().getName();
    }

    /**
     * Sets the fields corresponding to the properties in the configuration.
     *
     * @param backend a backend.
     * @param backendInstanceSpec a {@link BackendInstanceSpec}
     */
    public static void initialize(Object backend,
            BackendInstanceSpec backendInstanceSpec)  {
        assert backend != null : "backend cannot be null";
        assert backendInstanceSpec != null :
            "backendInstanceSpec cannot be null";
        for (Method method : backend.getClass().getMethods()) {
            if (method.isAnnotationPresent(BackendProperty.class)) {
                try {
                    BackendProperty annotation =
                            method.getAnnotation(BackendProperty.class);
                    String propertyName = propertyName(annotation, method);
                    Object propertyValue =
                            backendInstanceSpec.getProperty(propertyName);
                    if (propertyValue != null) {
                            method.invoke(backend,
                                    new Object[]{propertyValue
                                });
                    }
                } catch (IllegalAccessException ex) {
                    throw new AssertionError(ex);
                } catch (InvocationTargetException ex) {
                    throw new AssertionError(ex);
                } catch (InvalidPropertyNameException ex) {
                    throw new AssertionError(ex);
                }
            }
        }
    }

    private static String propertyName(BackendProperty annotation,
            Method method) {
        String propertyName;
        String propertyNameInAnnotation = annotation.propertyName();
        if (propertyNameInAnnotation.isEmpty()) {
            String setterName = method.getName();
            propertyName = setterName.substring(3);
            char[] propertyNameArr = propertyName.toCharArray();
            propertyNameArr[0] = Character.toLowerCase(propertyNameArr[0]);
            propertyName = String.valueOf(propertyNameArr);
        } else {
            propertyName = propertyNameInAnnotation;
        }
        return propertyName;
    }
}
