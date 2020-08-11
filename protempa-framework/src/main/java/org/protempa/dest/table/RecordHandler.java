package org.protempa.dest.table;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.arp.javautil.sql.ConnectionSpec;

/*
 * #%L
 * AIW i2b2 ETL
 * %%
 * Copyright (C) 2012 - 2015 Emory University
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

/**
 * Inserts a record into a database using prepared statements in batch mode. The
 * actual batch inserts occur in a separate thread.
 *
 * @author Andrew Post
 */
public abstract class RecordHandler<E> implements AutoCloseable {

    private static final Logger LOGGER = Logger.getLogger(RecordHandler.class.getName());
    private static final String SQL_RUNNER_BATCH_SIZE_PROPERTY = "aiw.i2b2Etl.sqlRunner.batchSize";
    private static final String SQL_RUNNER_COMMIT_SIZE_PROPERTY = "aiw.i2b2Etl.sqlRunner.commitSize";

    private final int batchSize = Integer.getInteger(SQL_RUNNER_BATCH_SIZE_PROPERTY, 1000);
    private final int commitSize = Integer.getInteger(SQL_RUNNER_COMMIT_SIZE_PROPERTY, 10000);

    private int commitCounter;
    private int counter;
    private volatile PreparedStatement ps;
    private final String statement;
    private Connection cn;
    private final Timestamp importTimestamp;
    private final boolean commit;
    private final List<E> records;
    private final int maxTries;
    private ConnectionSpec connSpec;
    private Integer colCount;
    private boolean isParametersSet;

    public RecordHandler(Connection connection, String statement) throws SQLException {
        this(connection, statement, true);
    }

    public RecordHandler(Connection connection, String statement, boolean commit) throws SQLException {
        if (connection == null) {
            throw new IllegalArgumentException("connection cannot be null");
        }
        if (statement == null) {
            throw new IllegalArgumentException("statement cannot be null");
        }
        this.cn = connection;
        this.statement = statement;
        this.importTimestamp = new Timestamp(System.currentTimeMillis());
        this.commit = commit;
        this.records = new ArrayList<>();
        this.maxTries = 1;
        this.counter = 0;
        this.commitCounter = 0;
        init();
    }
    
    public RecordHandler(ConnectionSpec connSpec, String statement) throws SQLException {
        if (connSpec == null) {
            throw new IllegalArgumentException("connection cannot be null");
        }
        if (statement == null) {
            throw new IllegalArgumentException("statement cannot be null");
        }
        this.connSpec = connSpec;
        this.statement = statement;
        this.importTimestamp = new Timestamp(System.currentTimeMillis());
        this.commit = true;
        this.records = new ArrayList<>();
        this.maxTries = 3;
        this.counter = 0;
        this.commitCounter = 0;
        init();
    }
    
    public RecordHandler(ConnectionSpec connSpec, String statement, Integer colCount) throws SQLException {
        if (connSpec == null) {
            throw new IllegalArgumentException("connection cannot be null");
        }
        if (statement == null) {
            throw new IllegalArgumentException("statement cannot be null");
        }
        this.connSpec = connSpec;
        this.statement = statement;
        this.colCount = colCount;
        this.importTimestamp = new Timestamp(System.currentTimeMillis());
        this.commit = true;
        this.records = new ArrayList<>();
        this.maxTries = 3;
        this.counter = 0;
        this.commitCounter = 0;
        init();
    }
    public void insert(E record) throws SQLException {
        if (record != null) {
        	setParametersSet(true);
            try {
                this.records.add(record);
                setParameters(this.ps, record);
                if(isParametersSet()) {
                	this.counter++;
                    this.commitCounter++;
                	this.ps.addBatch();
                    if (this.counter >= this.batchSize) {
                        executeBatch();
                    }
                    if (this.commitCounter >= this.commitSize) {
                        commit();
                    }
                }
                else {
                	this.records.remove(record);
                }
                
            } catch (SQLException e) {
                rollback(e);
                if (this.ps != null) {
                    try {
                        this.ps.close();
                    } catch (SQLException sqle) {
                        e.addSuppressed(sqle);
                    }
                }
                if (!this.records.isEmpty() && this.connSpec != null) {
                    retry(e, false);
                }
            }
        }
    }

//    public void insert(E record) throws SQLException {
//        int recSize = 0;
//        LOGGER.log(Level.INFO, "Calculating Record Size");
//    	if (record != null) {
//    		LOGGER.log(Level.INFO, "Record Not Null");
//        	if(record instanceof List) {
//        		recSize = ((List) record).size();
//        	}
//        	LOGGER.log(Level.INFO, "Record Size:{0}", recSize);
//        	LOGGER.log(Level.INFO, "Number of Parameters:{0}", this.ps.getParameterMetaData().getParameterCount());
//            try {
//                if (this.ps.getParameterMetaData().getParameterCount() == recSize) {
//                	LOGGER.log(Level.INFO, "Enough Parameters Supplied");
//                	this.records.add(record);
//                    this.counter++;
//                    this.commitCounter++;
//            		setParameters(this.ps, record);
//            		this.ps.addBatch();
//            		if (this.counter >= this.batchSize) {
//            			executeBatch();
//            		}
//            		if (this.commitCounter >= this.commitSize) {
//            			commit();
//            		}
//                }
//                else {
//                	LOGGER.log(Level.INFO, "Not Enough Parameters Supplied, ignoring record");
//                }
//            } catch (SQLException e) {
//                rollback(e);
//                if (this.ps != null) {
//                    try {
//                        this.ps.close();
//                    } catch (SQLException sqle) {
//                        e.addSuppressed(sqle);
//                    }
//                }
//                if (!this.records.isEmpty() && this.connSpec != null) {
//                	//Dont retry
//                    //retry(e, false);
//                	this.records.clear();
//                	LOGGER.log(Level.FINE, "Not Inserting this record");
//                }
//            }
//        }
//    }

    protected abstract void setParameters(PreparedStatement statement, E record) throws SQLException;

    public boolean isParametersSet() {
		return isParametersSet;
	}

	public void setParametersSet(boolean isParametersSet) {
		this.isParametersSet = isParametersSet;
	}

	protected Connection getConnection() {
        return this.cn;
    }


    @Override
    public void close() throws SQLException {
        SQLException exceptionThrown = null;
        if (this.ps != null) {
            try {
                try {
                    executeBatch();
                    commit();
                } catch (SQLException ex) {
                    rollback(ex);
                    exceptionThrown = ex;
                    if (!this.records.isEmpty() && this.connSpec != null) {
                        retry(exceptionThrown, true);
                    }
                }
                this.ps.close();
                this.ps = null;
            } finally {
                if (this.ps != null) {
                    try {
                        this.ps.close();
                    } catch (SQLException ignore) {
                        if (exceptionThrown != null) {
                            exceptionThrown.addSuppressed(ignore);
                        } else {
                            exceptionThrown = ignore;
                        }
                    }
                }
                if (this.connSpec != null && this.cn != null) {
                    try {
                        this.cn.close();
                    } catch (SQLException ignore) {
                        if (exceptionThrown != null) {
                            exceptionThrown.addSuppressed(ignore);
                        } else {
                            exceptionThrown = ignore;
                        }
                    }
                }
            }
        }
        if (exceptionThrown != null) {
            throw exceptionThrown;
        }
    }
//    @Override
//    public void close() throws SQLException {
//        SQLException exceptionThrown = null;
//        if (this.ps != null) {
//            try {
//                try {
//                    executeBatch();
//                    commit();
//                } catch (SQLException ex) {
//                    rollback(ex);
//                    exceptionThrown = ex;
//                    if (!this.records.isEmpty() && this.connSpec != null) {
////                        retry(exceptionThrown, true);
//                        this.records.clear();
//                    	LOGGER.log(Level.FINE, "Not Inserting this record");
//                    }
//                }
//                this.ps.close();
//                this.ps = null;
//            } finally {
//                if (this.ps != null) {
//                    try {
//                        this.ps.close();
//                    } catch (SQLException ignore) {
//                        if (exceptionThrown != null) {
//                            exceptionThrown.addSuppressed(ignore);
//                        } else {
//                            exceptionThrown = ignore;
//                        }
//                    }
//                }
//                if (this.connSpec != null && this.cn != null) {
//                    try {
//                        this.cn.close();
//                    } catch (SQLException ignore) {
//                        if (exceptionThrown != null) {
//                            exceptionThrown.addSuppressed(ignore);
//                        } else {
//                            exceptionThrown = ignore;
//                        }
//                    }
//                }
//            }
//        }
//        if (exceptionThrown != null) {
//            throw exceptionThrown;
//        }
//    }

    private void init() throws SQLException {
        if (this.connSpec != null) {
            this.cn = this.connSpec.getOrCreate();
        }
        this.ps = this.cn.prepareStatement(this.statement);
        this.counter = 0;
        this.commitCounter = 0;
    }

//    private void init() throws SQLException {
//        if (this.connSpec != null) {
//        	if (this.cn == null || this.cn.isClosed()) {
//        		this.cn = this.connSpec.getOrCreate();
//        		//this.connections.add(this.cn);
//        	}
//        }
//        LOGGER.log(Level.INFO, "Preparing Statement for SQL:{0}", this.statement);
//        this.ps = this.cn.prepareStatement(this.statement);
//        this.counter = 0;
//        this.commitCounter = 0;
//    }

    private void executeBatch() throws SQLException {
        if (counter > 0) {
            ps.executeBatch();
            counter = 0;
            if (LOGGER.isLoggable(Level.FINER)) {
                LOGGER.log(Level.FINER, "Batch executed successfully");
            }
            ps.clearBatch();
            ps.clearParameters();
        }
    }

    private void commit() throws SQLException {
        if (commitCounter > 0) {
            if (commit) {
                cn.commit();
            }
            commitCounter = 0;
            records.clear();
        }
    }

    private void retry(SQLException e, boolean inClose) throws SQLException {
        LOGGER.log(Level.WARNING, "Retrying after database error", e);
        int tried = 0;
        while (++tried <= this.maxTries) {
            try {
                reconnectAndReplay(inClose);
                break;
            } catch (SQLException ex) {
                LOGGER.log(Level.WARNING, "Retrying failed");
                e.addSuppressed(ex);
                if (tried == this.maxTries) {
                    LOGGER.log(Level.SEVERE, "Giving up after " + tried + " tries", ex);
                    throw e;
                }
            }
        }
    }
    private void reconnectAndReplay(boolean inClose) throws SQLException {
        init();
        for (E record : this.records) {
            setParameters(this.ps, record);
            this.ps.addBatch();
            this.counter++;
            this.commitCounter++;
        }
        if (!this.records.isEmpty()) {
            try {
                if (inClose) {
                    executeBatch();
                    commit();
                } else {
                    if (this.counter >= this.batchSize) {
                        executeBatch();
                    }
                    if (this.commitCounter >= this.commitSize) {
                        commit();
                    }
                }
            } catch (SQLException ex) {
                rollback(ex);
                throw ex;
            }
        }
    }


//    private void reconnectAndReplay(boolean inClose) throws SQLException {
//        init();
//        Throwable throwable = new Throwable();
//        int recSize = 0;
//        for (E record : this.records) {
//        	if(record instanceof List) {
//        		recSize = ((List) record).size();
//        	}
//        	if (this.ps.getParameterMetaData().getParameterCount() == recSize) {
//        		setParameters(this.ps, record);
//        		this.ps.addBatch();
//        		this.counter++;
//        		this.commitCounter++;
//        	}
//        	else {
//        		this.records.remove(record);
//        	}
//        }
////        if (!this.records.isEmpty()) {
//            try {
//                if (inClose) {
//                    executeBatch();
//                    commit();
//                } else {
//                    if (this.counter >= this.batchSize) {
//                        executeBatch();
//                    }
//                    if (this.commitCounter >= this.commitSize) {
//                        commit();
//                    }
//                }
//            } catch (SQLException ex) {
//            	throwable = ex;
//                rollback(ex);
//                throw(ex);
//            } finally {
//            	try {
//            		LOGGER.log(Level.SEVERE, "Closing Connection After Failed Retry");
//            		this.cn.close();
//            	}
//            	catch (SQLException ignore) {
//            		throwable.addSuppressed(ignore);
//            		throw (SQLException)throwable;
//            	}
//            }
////        }
//    }

    
    private void rollback(Throwable throwable) {
        if (commit) {
            try {
                this.cn.rollback();
            } catch (SQLException ignore) {
                throwable.addSuppressed(ignore);
            }
        }
    }
    


    public Timestamp importTimestamp() {
        return this.importTimestamp;
    }


}

