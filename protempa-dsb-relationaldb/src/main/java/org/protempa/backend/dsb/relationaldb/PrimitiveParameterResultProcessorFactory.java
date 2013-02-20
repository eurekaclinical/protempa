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
package org.protempa.backend.dsb.relationaldb;

import org.protempa.proposition.Event;
import org.protempa.proposition.PrimitiveParameter;

/**
 * 
 * @author Andrew Post
 */
class PrimitiveParameterResultProcessorFactory extends
        SQLGenResultProcessorFactory<PrimitiveParameter> {

    @Override
    MainResultProcessor<PrimitiveParameter> getInstance(
            String dataSourceBackendId, EntitySpec entitySpec,
            ResultCache<PrimitiveParameter> cache) {

        PrimitiveParameterResultProcessor resultProcessor = 
                new PrimitiveParameterResultProcessor(cache, entitySpec,
                        dataSourceBackendId);
        return resultProcessor;
    }

    @Override
    RefResultProcessor<PrimitiveParameter> getRefInstance(
            String dataSourceBackendId, EntitySpec entitySpec,
            ReferenceSpec referenceSpec, ResultCache<PrimitiveParameter> cache) {
        PrimitiveParameterRefResultProcessor resultProcessor = 
                new PrimitiveParameterRefResultProcessor(cache, 
                        referenceSpec, entitySpec, dataSourceBackendId);
        return resultProcessor;
    }

    @Override
    StreamingMainResultProcessor<PrimitiveParameter> getStreamingInstance(
            String dataSourceBackendId, EntitySpec entitySpec) {
        PrimitiveParameterStreamingResultProcessor resultProcessor =
                new PrimitiveParameterStreamingResultProcessor(entitySpec, dataSourceBackendId);
        return resultProcessor;
    }

    @Override
    StreamingRefResultProcessor<PrimitiveParameter> getStreamingRefInstance(ReferenceSpec referenceSpec, EntitySpec entitySpec, String dataSourceBackendId) {
        return new PrimitiveParameterStreamingRefResultProcessor(referenceSpec, entitySpec, dataSourceBackendId);
    }
    
    

}
