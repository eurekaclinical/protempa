package org.protempa.dest.table;

/*-
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2017 Emory University
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.arp.javautil.sql.ConnectionSpec;

/**
 *
 * @author Andrew Post
 */
class ListRecordHandler extends RecordHandler<ArrayList<?>> {
	
	Logger logger = Util.logger();
	private String inStatement = null;

    ListRecordHandler(Connection connection, String statement) throws SQLException {
        super(connection, statement);
        this.inStatement = statement;
    }

    ListRecordHandler(Connection connection, String statement, boolean commit) throws SQLException {
        super(connection, statement, commit);
        this.inStatement = statement;
    }

    ListRecordHandler(ConnectionSpec connSpec, String statement) throws SQLException {
        super(connSpec, statement);
        this.inStatement = statement;
    }
    
    @Override
    protected void setParameters(PreparedStatement statement, ArrayList<?> record) throws SQLException {
        for (int i = 0, n = record.size(); i < n; i++) {
        	int pos = i+1;
        	statement.setString(pos, (record.get(i) == null? "NULL" : record.get(i).toString()));
        }
    }
    
    public String getInStatement() {
		return inStatement;
	}

	public void setInStatement(String inStatement) {
		this.inStatement = inStatement;
	}
    
}
