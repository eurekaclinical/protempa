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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.arp.javautil.sql.ConnectionSpec;
import java.util.logging.Logger;

/**
 *
 * @author Andrew Post
 */
class ListRecordHandler extends RecordHandler<ArrayList<?>> {
	
	Logger logger = Logger.getLogger(ListRecordHandler.class.getName());
	private String inStatement = null;
	private Integer colCount = null;
	private boolean isParametersSet = true;

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
    
    ListRecordHandler(ConnectionSpec connSpec, String statement, Integer colCount) throws SQLException {
        super(connSpec, statement, colCount);
        this.inStatement = statement;
        this.colCount = colCount;
    }
    
    @Override
    protected void setParameters(PreparedStatement statement, ArrayList<?> record) throws SQLException {
      int recSize = 0;
      logger.log(Level.INFO, "Calculating Record Size");
      	if(record instanceof List) {
      		recSize = ((List) record).size();
      	}
      	logger.log(Level.INFO, "Record Size:{0}", recSize);
      	logger.log(Level.INFO, "Number of Parameters:{0}", statement.getParameterMetaData().getParameterCount());
      	if (statement.getParameterMetaData().getParameterCount() == recSize) {
	    	for (int i = 0, n = record.size(); i < n; i++) {
	            int pos = i+1;
	            statement.setString(pos, (record.get(i) == null? "NULL" : record.get(i).toString()));
	        }
	    	setParametersSet(true);
      	}
      	else
      		setParametersSet(false);
    }
    
    public String getInStatement() {
		return inStatement;
	}

	public void setInStatement(String inStatement) {
		this.inStatement = inStatement;
	}
	
    public void close() throws SQLException {
		logger.log(Level.FINER, "Closing by punting to super");
		super.close();
	}
    
}
