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
package org.protempa.backend.asb.java;

import org.protempa.backend.asb.java.AbstractJavaAlgorithmBackend;
import org.protempa.backend.annotations.BackendInfo;
import org.protempa.backend.annotations.BackendProperty;
import org.protempa.backend.CommonsBackend;

/**
 *
 * @author Andrew Post
 */
@BackendInfo(
    displayName="Java Algorithm Backend"
)
public class JavaAlgorithmBackend extends AbstractJavaAlgorithmBackend {
    
    private String algorithms;

    public String getAlgorithms() {
        return algorithms;
    }

    @BackendProperty(
        displayName="Algorithms"
    )
    public void setAlgorithms(String algorithms) {
        this.algorithms = algorithms;
    }

    @Override
    protected String getAlgorithmsPropertiesResourceName() {
        return this.algorithms != null ? this.algorithms
                : super.getAlgorithmsPropertiesResourceName();
    }

    @Override
    public String getDisplayName() {
        return CommonsBackend.backendInfo(this).displayName();
    }


}
