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
import java.sql.SQLException;
import java.text.Format;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.arp.javautil.sql.ConnectionSpec;
import org.protempa.proposition.Parameter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.TemporalProposition;
import org.protempa.proposition.value.BooleanValue;
import org.protempa.proposition.value.DateValue;
import org.protempa.proposition.value.InequalityNumberValue;
import org.protempa.proposition.value.NominalValue;
import org.protempa.proposition.value.NumberValue;
import org.protempa.proposition.value.Value;

/**
 *
 * @author Andrew Post
 */
public class RelDbTabularWriter extends AbstractTabularWriter {

    private final RecordHandler<ArrayList<?>> recordHandler;
    private final ArrayList<Object> row;
    private int colIndex;
    private String inStatement;
    private ConnectionSpec connectionSpec;
    private Map<String, RecordHandler<ArrayList<?>>> handlerList;
    private Map<String,String> statements;
    private String tableName;
    
	
	

	Logger logger = Util.logger();
    
    public RelDbTabularWriter(ConnectionSpec inConnectionSpec, String inStatement) throws SQLException {
    	this.connectionSpec = inConnectionSpec;
        this.recordHandler = new ListRecordHandler(inConnectionSpec, inStatement);
        this.row = new ArrayList<>();
        logger.info("Creating RelDbTabularWriter");
    }
    
    public RelDbTabularWriter(ConnectionSpec inConnectionSpec, Map<String,String> inStatements) throws SQLException {
    	this.connectionSpec = inConnectionSpec;
    	this.statements = inStatements;
    	this.handlerList = new HashMap<String, RecordHandler<ArrayList<?>>>();
    	for(String tableName : inStatements.keySet()) {
    		logger.info("Creating Handler: " + tableName + "; SQL: " + inStatements.get(tableName));
    		this.handlerList.put(tableName, new ListRecordHandler(inConnectionSpec, inStatements.get(tableName)));
    	}
        //this.recordHandler = new ListRecordHandler(inConnectionSpec, inStatement);
        this.row = new ArrayList<>();
        
    }
    
    public RelDbTabularWriter(ConnectionSpec inConnectionSpec) throws SQLException {
        this.recordHandler = new ListRecordHandler(inConnectionSpec, this.inStatement);
        this.row = new ArrayList<>();
        logger.info("Creating RelDbTabularWriter");
    }
    
    public String getInStatement() {
		return inStatement;
	}

	public void setInStatement(String inStatement) throws SQLException {
		this.inStatement = inStatement;
		this.recordHandler.close();
		this.recordHandler = new ListRecordHandler(connectionSpec, inStatement);
	}
	
	public ConnectionSpec getConnectionSpec() {
		return connectionSpec;
	}

	public void setConnectionSpec(ConnectionSpec connectionSpec) {
		this.connectionSpec = connectionSpec;
	}

	public Map<String, RecordHandler<ArrayList<?>>> getHandlerList() {
		return handlerList;
	}

	public void setHandlerList(Map<String, RecordHandler<ArrayList<?>>> handlerList) {
		this.handlerList = handlerList;
	}
	
	public RecordHandler<ArrayList<?>> getRecordHandler() {
		return recordHandler;
	}

	public void setRecordHandler(RecordHandler<ArrayList<?>> recordHandler) {
		this.recordHandler = recordHandler;
	}
	
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

    @Override
    public void writeNominal(NominalValue inValue, Format inFormat) {
        if (inFormat != null) {
            this.row.add(inValue.format(inFormat));
        } else {
            this.row.add(inValue == null ? "NULL":inValue.getString());
        }
        incr();
    }

    @Override
    public void writeNumber(NumberValue inValue, Format inFormat) {
        if (inFormat != null) {
            this.row.add(inValue.format(inFormat));
        } else {
            this.row.add(inValue == null ? "NULL":inValue.getNumber());
        }
        incr();
    }

    @Override
    public void writeInequality(InequalityNumberValue inValue, Format inFormat) {
        if (inFormat != null) {
            this.row.add(inFormat.format(inValue.getComparator()));
        } else {
            this.row.add(inValue == null ? "NULL":inValue.getComparator().getComparatorString());
        }
        incr();
    }

    @Override
    public void writeNumber(InequalityNumberValue inValue, Format inFormat) {
        if (inFormat != null) {
            this.row.add(inValue.format(inFormat));
        } else {
            this.row.add(inValue == null ? "NULL":inValue.getNumber());
        }
        incr();
    }

    @Override
    public void writeInequalityNumber(InequalityNumberValue inValue, Format inFormat) throws TabularWriterException {
        if (inFormat != null) {
            this.row.add(inValue.format(inFormat));
        } else {
            this.row.add(inValue == null ? "NULL":inValue.getNumber());
        }
        
        incr();
    }

    @Override
    public void writeDate(DateValue inValue, Format inFormat) {
        if (inFormat != null) {
            this.row.add(inValue.format(inFormat));
        } else {
            this.row.add(inValue == null ? "NULL":inValue.getDate());
        }
        incr();
    }

    @Override
    public void writeBoolean(BooleanValue inValue, Format inFormat) {
        if (inFormat != null) {
            this.row.add(inValue.format(inFormat));
        } else {
            this.row.add(inValue == null ? "NULL":inValue.getBoolean());
        }
        incr();
    }

    @Override
    public final void writeId(Proposition inProposition) throws TabularWriterException {
        String value = inProposition.getId();
        writeString(value);
    }

    @Override
    public final void writeUniqueId(Proposition inProposition) throws TabularWriterException {
        String value = inProposition.getUniqueId().getStringRepresentation();
        writeString(value);
    }

    @Override
    public final void writeLocalUniqueId(Proposition inProposition) throws TabularWriterException {
        String value = inProposition.getUniqueId().getLocalUniqueId().getId();
        writeString(value);
    }

    @Override
    public final void writeNumericalId(Proposition inProposition) throws TabularWriterException {
        String value = String.valueOf(inProposition.getUniqueId().getLocalUniqueId().getNumericalId());
        writeString(value);
    }

    @Override
    public final void writeStart(TemporalProposition inProposition, Format inFormat) throws TabularWriterException {
        String value;
        if (inFormat == null) {
            value = inProposition.getStartFormattedShort();
        } else {
            value = inProposition.formatStart(inFormat);
        }
        writeString(value);
    }

    @Override
    public final void writeFinish(TemporalProposition inProposition, Format inFormat) throws TabularWriterException {
        String value;
        if (inFormat == null) {
            value = inProposition.getFinishFormattedShort();
        } else {
            value = inProposition.formatFinish(inFormat);
        }
        writeString(value);
    }

    @Override
    public final void writeLength(TemporalProposition inProposition, Format inFormat) throws TabularWriterException {
        String value;
        if (inFormat == null) {
            value = inProposition.getLengthFormattedShort();
        } else {
            value = inProposition.formatLength(inFormat);
        }
        writeString(value);
    }

    @Override
    public final void writeParameterValue(Parameter inProposition, Format inFormat) throws TabularWriterException {
        Value value = inProposition.getValue();
        writeValue(value, inFormat);
    }

    @Override
    public final void writePropertyValue(Proposition inProposition, String inPropertyName, Format inFormat) throws TabularWriterException {
        Value value = inProposition.getProperty(inPropertyName);
        writeValue(value, inFormat);
    }

    @Override
    public final void writeNull() throws TabularWriterException {
    	Value value = new NominalValue("NULL");
        writeValue(value, null);
    }

    @Override
    public final void newRow() throws TabularWriterException {
        try {
        	this.recordHandler = this.handlerList.get(tableName);
        	if(this.recordHandler != null) {
        		this.recordHandler.insert(this.row);
        	}
        	else {
        		logger.info("NULL RECORDHANDLER: Getting from statements map" + (statements == null? 0:statements.size()));
        		this.recordHandler = new ListRecordHandler(this.connectionSpec, this.statements.get(this.tableName));
        		if(this.handlerList.containsKey(tableName))
        			this.handlerList.replace(tableName, this.recordHandler);
        		else 
        			this.handlerList.put(tableName, this.recordHandler);
        		this.recordHandler.insert(this.row);
        	}           
        } catch (SQLException | NullPointerException ex) {
        	logger.info("Statement:" + this.inStatement);
        	StringBuilder sb = new StringBuilder();
        	for(Object o: this.row) {
            	sb.append(o.toString() + ":");
        	}
        	logger.info("Row error:" + sb.toString());
            throw new TabularWriterException(ex);
        }
        this.row.clear();
        this.colIndex = 0;
    }

    @Override
    public final void close() throws TabularWriterException {
        try {
            for(String tableName: this.getHandlerList().keySet()) {
            	this.handlerList.get(tableName).close();
            }
            this.recordHandler.close();
        } catch (SQLException ex) {
            throw new TabularWriterException(ex);
        }
    }

    private int incr() {
        return this.colIndex++;
    }
    
    private void writeString(String inValue) throws TabularWriterException {
        this.row.add(inValue);
        incr();
    }

}
