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
package org.protempa.backend;

import org.protempa.backend.annotations.BackendProperty;
import org.protempa.backend.annotations.BackendInfo;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
