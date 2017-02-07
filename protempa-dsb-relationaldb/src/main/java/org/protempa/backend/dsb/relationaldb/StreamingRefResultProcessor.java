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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.protempa.proposition.Proposition;

abstract class StreamingRefResultProcessor<P extends Proposition> 
        extends AbstractResultProcessor implements StreamingResultProcessor<P> {
    
    private final ReferenceSpec referenceSpec;
    private ReferenceResultSetIterator itr;
    private Statement statement;

    protected StreamingRefResultProcessor(
            RelationalDbDataSourceBackend backend,
            ReferenceSpec referenceSpec, EntitySpec entitySpec,
            String dataSourceBackendId) {
        super(backend, entitySpec, dataSourceBackendId);
        assert referenceSpec != null : "referenceSpec cannot be null";
        this.referenceSpec = referenceSpec;
    }

    @Override
    public final void process(ResultSet resultSet) throws SQLException {
        this.itr = newIterator(resultSet, this.referenceSpec, getEntitySpec(),
                getDataSourceBackendId(), this);
    }

    abstract ReferenceResultSetIterator newIterator(ResultSet resultSet,
            ReferenceSpec referenceSpec, EntitySpec entitySpec,
            String dataSourceBackendId, StreamingRefResultProcessor<P> processor) throws SQLException;

    ReferenceResultSetIterator getResult() {
        return this.itr;
    }

    @Override
    public Statement getStatement() {
        return this.statement;
    }

    @Override
    public void setStatement(Statement statement) {
        this.statement = statement;
    }
    
    
}
