package org.protempa.bp.commons;

import org.protempa.backend.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.protempa.backend.Backend;

/**
 *
 * @author Andrew Post
 */
final class BackendSpecFactory {

    private BackendSpecFactory() {
        
    }

    static <B extends Backend<?, ?>> BackendSpec<B> newInstance(
            BackendProvider backendProvider, Class<B> backendCls) 
            throws InvalidBackendException {
        BackendInfo backendAnnotation =
                backendCls.getAnnotation(BackendInfo.class);
        if (backendAnnotation == null)
            throw new InvalidBackendException(
                    "No @BackendInfo annotation found");
        Method[] methods = backendCls.getMethods();
        List<BackendProperty> backendPropertyAnnotations =
                new ArrayList<BackendProperty>();
        for (Method method : methods) {
            BackendProperty backendPropertyAnnotation =
                    method.getAnnotation(BackendProperty.class);
            if (backendPropertyAnnotation != null) {
                backendPropertyAnnotations.add(backendPropertyAnnotation);
            }
        }
        ArrayList<BackendPropertySpec> propSpecs =
                new ArrayList<BackendPropertySpec>(
                backendPropertyAnnotations.size());
        String baseName = backendAnnotation.propertiesBaseName();
        ResourceBundle bundle = null;
        if (!BackendInfo.PROPERTIES_BASE_NAME_NULL.equals(baseName)) {
            try {
                bundle =
                        ResourceBundle.getBundle(baseName, Locale.getDefault(),
                        backendCls.getClassLoader());
            } catch (MissingResourceException mre) {
                throw new AssertionError(mre);
            }
        }
        for (Method method : methods) {
            BackendProperty backendPropertyAnnotation =
                    method.getAnnotation(BackendProperty.class);
            if (backendPropertyAnnotation != null) {
                Class<? extends BackendPropertyValidator> validatorCls =
                        backendPropertyAnnotation.validator();
                String name = null;
                String propertyName = backendPropertyAnnotation.propertyName();
                if (propertyName.isEmpty()) {
                    name = method.getName();
                    name = name.substring(3);
                } else {
                    name = propertyName;
                }
                char[] nameArr = name.toCharArray();
                nameArr[0] = Character.toLowerCase(nameArr[0]);
                name = String.valueOf(nameArr);
                String displayName = backendPropertyAnnotation.displayName();
                String description = backendPropertyAnnotation.description();
                Class<?> type = method.getParameterTypes()[0];
                try {
                    propSpecs.add(new BackendPropertySpec(
                            name,
                            bundle != null ? bundle.getString(displayName) :
                                displayName,
                            bundle != null ? bundle.getString(description) :
                                description,
                            type,
                            validatorCls != null ? validatorCls.newInstance() :
                                null));
                } catch (InstantiationException ex) {
                    throw new AssertionError(ex);
                } catch (IllegalAccessException ex) {
                    throw new AssertionError(ex);
                }
            }
        }
        String displayName = backendAnnotation.displayName();
        if (BackendInfo.DISPLAY_NAME_NULL.equals(displayName)) {
            displayName = null;
        }
        
        return new BackendSpec<B>(backendProvider,
                backendCls.getName(), displayName, propSpecs);
    }
}
