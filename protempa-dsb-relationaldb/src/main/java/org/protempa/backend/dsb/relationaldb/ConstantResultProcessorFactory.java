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

import java.util.LinkedHashMap;
import org.protempa.proposition.Constant;

import java.util.Map;
import java.util.Set;

/**
 *
 * @author Andrew Post
 */
class ConstantResultProcessorFactory
        extends SQLGenResultProcessorFactory<Constant> {

    private final RelationalDbDataSourceBackend backend;

    ConstantResultProcessorFactory(RelationalDbDataSourceBackend backend) {
        this.backend = backend;
    }
    
    @Override
    StreamingMainResultProcessor<Constant> getStreamingInstance(
            String dataSourceBackendId, EntitySpec entitySpec,
            LinkedHashMap<String, ReferenceSpec> inboundRefSpecs,
            Map<String, ReferenceSpec> bidirectionalRefSpecs, 
            Set<String> propIds) {
        ConstantStreamingResultProcessor resultProcessor =
                new ConstantStreamingResultProcessor(this.backend, entitySpec,
                        inboundRefSpecs, bidirectionalRefSpecs,
                        dataSourceBackendId, propIds);
        return resultProcessor;
    }
    
    @Override
    StreamingRefResultProcessor<Constant> getStreamingRefInstance(ReferenceSpec referenceSpec, EntitySpec entitySpec, String dataSourceBackendId) {
        return new ConstantStreamingRefResultProcessor(this.backend, referenceSpec, entitySpec, dataSourceBackendId);
    }

}
