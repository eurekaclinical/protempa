/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 Emory University
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
package org.protempa.proposition.value;

import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Utilities for the time units project.
 * 
 * @author Andrew Post
 */
final class ValueUtil {

    private ValueUtil() {
    }

    private static class LazyResourceBundleHolder {

        private static ResourceBundle instance =
                ResourceBundle.getBundle(
                "org.protempa.proposition.value.resources.bundles.Messages");
    }

    private static class LazyLoggerHolder {

        private static Logger instance =
                Logger.getLogger(ValueUtil.class.getPackage().getName());
    }

    /**
     * Gets the messages for this project's resource bundle.
     *
     * @return a <code>ResourceBundle</code>.
     */
    static ResourceBundle resourceBundle() {
        return LazyResourceBundleHolder.instance;
    }

    static Logger logger() {
        return LazyLoggerHolder.instance;
    }
}
