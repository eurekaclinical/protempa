/*
 * #%L
 * Protempa Relational Database Data Source Backend
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

import java.sql.ResultSet;
import java.sql.SQLException;
import org.protempa.proposition.Constant;

/**
 *
 * @author Andrew Post
 */
public class ConstantStreamingRefResultProcessor extends StreamingRefResultProcessor<Constant> {

    public ConstantStreamingRefResultProcessor(ReferenceSpec referenceSpec, EntitySpec entitySpec, String dataSourceBackendId) {
        super(referenceSpec, entitySpec, dataSourceBackendId);
    }
    
    @Override
    ReferenceResultSetIterator newIterator(ResultSet resultSet, 
            ReferenceSpec referenceSpec, EntitySpec entitySpec, 
            String dataSourceBackendId, 
            StreamingRefResultProcessor<Constant> processor) throws SQLException {
        return new ReferenceResultSetIterator(resultSet, referenceSpec, 
                entitySpec, dataSourceBackendId, processor);
    }
    
    
}
