package org.protempa.query.handler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.arp.javautil.string.StringUtil;
import org.protempa.FinderException;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.ProtempaUtil;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueId;
import org.protempa.query.handler.table.TableColumnSpec;
import org.protempa.query.handler.table.TableColumnSpecValidationFailedException;

/**
 * 
 * @author Andrew Post
 */
public final class TableQueryResultsHandler implements QueryResultsHandler {

    private static final long serialVersionUID = -1503401944818776787L;
    private final char columnDelimiter;
    private final String[] rowPropositionIds;
    private final TableColumnSpec[] columnSpecs;
    private final boolean headerWritten;
    private KnowledgeSource knowledgeSource;
    private final BufferedWriter out;

    public TableQueryResultsHandler(BufferedWriter out, char columnDelimiter,
            String[] rowPropositionIds, TableColumnSpec[] columnSpecs,
            boolean headerWritten) {
        checkConstructorArgs(rowPropositionIds, columnSpecs);
        this.columnDelimiter = columnDelimiter;
        this.rowPropositionIds = rowPropositionIds.clone();
        ProtempaUtil.internAll(this.rowPropositionIds);
        this.columnSpecs = columnSpecs.clone();
        this.headerWritten = headerWritten;
        this.out = out;
    }

    private void checkConstructorArgs(String[] rowPropositionIds,
            TableColumnSpec[] columnSpecs) {
        ProtempaUtil.checkArray(rowPropositionIds, "rowPropositionIds");
        ProtempaUtil.checkArray(columnSpecs, "columnSpecs");
    }

    public String[] getRowPropositionIds() {
        return this.rowPropositionIds.clone();
    }

    public char getColumnDelimiter() {
        return this.columnDelimiter;
    }

    public TableColumnSpec[] getColumnSpecs() {
        return this.columnSpecs.clone();
    }

    public boolean isHeaderWritten() {
        return this.headerWritten;
    }

    @Override
    public void init(KnowledgeSource knowledgeSource) throws FinderException {
        Logger logger = Util.logger();
        this.knowledgeSource = knowledgeSource;
        if (this.headerWritten) {
            try {
                List<String> columnNames = new ArrayList<String>();
                columnNames.add("KeyId");
                for (TableColumnSpec columnSpec : this.columnSpecs) {
                    logger.log(Level.FINE, "Processing columnSpec type {0}",
                            columnSpec.getClass().getName());
                    String[] colNames =
                            columnSpec.columnNames(knowledgeSource);
                    assert colNames.length > 0 : 
                            "colNames must have length > 0";

                    for (int index = 0; index < colNames.length; index++) {
                        if (colNames[index] == null) {
                            colNames[index] = "(null)";
                        } else if (colNames[index].length() == 0) {
                            colNames[index] = "(empty)";
                        }
                    }

                    if (logger.isLoggable(Level.FINE)) {
                        logger.log(
                                Level.FINE,
                                "Got the following columns for proposition {0}: {1}",
                                new Object[]{Arrays.toString(this.rowPropositionIds),
                                    colNames});
                    }
                    for (String colName : colNames) {
                        columnNames.add(colName);
                    }
                }
                StringUtil.escapeAndWriteDelimitedColumns(columnNames,
                        this.columnDelimiter, this.out);
                this.out.newLine();
            } catch (KnowledgeSourceReadException ex1) {
                throw new FinderException("Error reading knowledge source", ex1);
            } catch (IOException ex) {
                throw new FinderException("Could not write header", ex);
            }
        }

    }

    @Override
    public void finish() throws FinderException {
    }

    @Override
    public void handleQueryResult(String keyId, List<Proposition> propositions,
            Map<Proposition, List<Proposition>> forwardDerivations,
            Map<Proposition, List<Proposition>> backwardDerivations,
            Map<UniqueId, Proposition> references)
            throws FinderException {
        int n = this.columnSpecs.length;
        Util.logger().log(Level.FINER, "Processing keyId {0}", keyId);
        for (Proposition prop : propositions) {
            if (!org.arp.javautil.arrays.Arrays.contains(
                    this.rowPropositionIds, prop.getId())) {
                continue;
            }
            try {
                StringUtil.escapeAndWriteDelimitedColumn(keyId, 
                        this.columnDelimiter, this.out);
                if (n > 0) {
                    this.out.write(this.columnDelimiter);
                }
                for (int i = 0; i < n; i++) {
                    TableColumnSpec columnSpec = this.columnSpecs[i];
                    String[] colValues = columnSpec.columnValues(keyId, prop,
                            forwardDerivations, backwardDerivations,
                            references, this.knowledgeSource);

                    for (int j = 0; j < colValues.length; j++) {
                        String colValue = colValues[j];
                        if (colValue == null) {
                            this.out.write("(null)");
                        } else if (colValue.length() == 0) {
                            this.out.write("(empty)");
                        } else {
                            StringUtil.escapeAndWriteDelimitedColumn(colValue, 
                                    this.columnDelimiter, this.out);
                        }
                        if (j < colValues.length - 1) {
                            this.out.write(this.columnDelimiter);
                        }
                    }

                    if (i < n - 1) {
                        this.out.write(this.columnDelimiter);
                    } else {
                        this.out.newLine();
                    }

                }
            } catch (KnowledgeSourceReadException ex1) {
                throw new FinderException(
                        "Could not read knowledge source", ex1);
            } catch (IOException ex) {
                throw new FinderException("Could not write row" + ex);
            }
        }
    }

    @Override
    public void validate(KnowledgeSource knowledgeSource) 
            throws QueryResultsHandlerValidationFailedException,
            KnowledgeSourceReadException {
        List<String> invalidPropIds = new ArrayList<String>();
        for (String propId : this.rowPropositionIds) {
            if (!knowledgeSource.hasPropositionDefinition(propId)) {
                invalidPropIds.add(propId);
            }
        }
        if (!invalidPropIds.isEmpty()) {
            throw new QueryResultsHandlerValidationFailedException(
                        "Invalid row proposition id(s): " + 
                    StringUtils.join(invalidPropIds, ", "));
        }
        int i = 1;
        for (TableColumnSpec columnSpec : this.columnSpecs) {
            try {
                columnSpec.validate(knowledgeSource);
            } catch (TableColumnSpecValidationFailedException ex) {
                throw new QueryResultsHandlerValidationFailedException(
                        "Validation of column spec " + i + " failed", ex);
            }
            i++;
        }
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + columnDelimiter;
		result = prime * result + Arrays.hashCode(columnSpecs);
		result = prime * result + (headerWritten ? 1231 : 1237);
		result = prime * result + Arrays.hashCode(rowPropositionIds);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TableQueryResultsHandler other = (TableQueryResultsHandler) obj;
		if (columnDelimiter != other.columnDelimiter)
			return false;
		if (!Arrays.equals(columnSpecs, other.columnSpecs))
			return false;
		if (headerWritten != other.headerWritten)
			return false;
		if (!Arrays.equals(rowPropositionIds, other.rowPropositionIds))
			return false;
		return true;
	}
}
