/*
 * #%L
 * Protempa Commons Backend Provider
 * %%
 * Copyright (C) 2012 - 2013 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.protempa.bp.commons;

import org.protempa.backend.Backend;
import org.protempa.backend.BackendPropertySpec;
import org.protempa.backend.BackendPropertyValidator;
import org.protempa.backend.BackendProvider;
import org.protempa.backend.BackendSpec;
import org.protempa.backend.annotations.BackendInfo;
import org.protempa.backend.annotations.BackendProperty;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.apache.commons.lang3.StringUtils;
import org.protempa.backend.BackendPropertyType;

/**
 *
 * @author Andrew Post
 */
final class BackendSpecFactory {

    private BackendSpecFactory() {
        
    }

    static <B extends Backend<?>> BackendSpec<B> newInstance(
            BackendProvider backendProvider, Class<B> backendCls) 
            throws InvalidBackendException {
        BackendInfo backendAnnotation =
                backendCls.getAnnotation(BackendInfo.class);
        if (backendAnnotation == null)
            throw new InvalidBackendException(
                    "No @BackendInfo annotation found");
        Method[] methods = backendCls.getMethods();
        List<BackendProperty> backendPropertyAnnotations =
                new ArrayList<>();
        for (Method method : methods) {
            BackendProperty backendPropertyAnnotation =
                    method.getAnnotation(BackendProperty.class);
            if (backendPropertyAnnotation != null) {
                backendPropertyAnnotations.add(backendPropertyAnnotation);
            }
        }
        ArrayList<BackendPropertySpec> propSpecs =
                new ArrayList<>(
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
                String name;
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
                Class<?> cls = method.getParameterTypes()[0];
                if (!BackendPropertyType.isAllowed(cls)) {
                    throw new InvalidBackendException(MessageFormat.format("@BackendProperty cannot annotate method with parameter type {0}; allowed types are {1}", cls.getName(), StringUtils.join(BackendPropertyType.values(), ", ")));
                }
                try {
                    propSpecs.add(new BackendPropertySpec(
                            name,
                            bundle != null ? bundle.getString(displayName) :
                                displayName,
                            bundle != null ? bundle.getString(description) :
                                description,
                            BackendPropertyType.fromCls(cls),
                            backendPropertyAnnotation.required(),
                            validatorCls != null ? validatorCls.newInstance() :
                                null));
                } catch (InstantiationException | IllegalAccessException ex) {
                    throw new AssertionError(ex);
                }
            }
        }
        String displayName = backendAnnotation.displayName();
        if (BackendInfo.DISPLAY_NAME_NULL.equals(displayName)) {
            displayName = null;
        }
        
        return new BackendSpec<>(backendProvider,
                backendCls.getName(), displayName, propSpecs.toArray(new BackendPropertySpec[propSpecs.size()]));
    }
}
