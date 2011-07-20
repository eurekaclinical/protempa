package org.protempa.query.handler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.arp.javautil.string.StringUtil;
import org.protempa.FinderException;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.ProtempaUtil;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueIdentifier;
import org.protempa.query.handler.table.TableColumnSpec;

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
                    logger.log(Level.FINE,"Processing columnSpec type {0}",
                            columnSpec.getClass().getName());
                    String[] colNames =
                            columnSpec.columnNames(knowledgeSource);

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
    public void handleQueryResult(String key, List<Proposition> propositions,
            Map<Proposition, List<Proposition>> forwardDerivations,
            Map<Proposition, List<Proposition>> backwardDerivations,
            Map<UniqueIdentifier, Proposition> references)
            throws FinderException {
        int n = this.columnSpecs.length;
        Util.logger().log(Level.FINE, "Processing patient {0}", key);
        for (Proposition prop : propositions) {
            if (!org.arp.javautil.arrays.Arrays.contains(
                    this.rowPropositionIds, prop.getId())) {
                continue;
            }
            for (int i = 0; i < n; i++) {
                TableColumnSpec columnSpec = this.columnSpecs[i];
                try {
                    String[] colValues = columnSpec.columnValues(key, prop,
                            forwardDerivations, backwardDerivations, 
                            references, this.knowledgeSource);
                    String[] columnValues;
                    if (i == 0) {
                        columnValues = new String[colValues.length + 1];
                        columnValues[0] = key;
                        System.arraycopy(colValues, 0, columnValues, 1,
                                colValues.length);
                    } else {
                        columnValues = new String[colValues.length];
                        System.arraycopy(colValues, 0, columnValues, 0,
                                colValues.length);
                    }

                    for (int index = 0; index < columnValues.length; index++) {
                        if (columnValues[index] == null) {
                            columnValues[index] = "(null)";
                        } else if (columnValues[index].length() == 0) {
                            columnValues[index] = "(empty)";
                        }
                    }

                    for (int index = 0; index < columnValues.length; index++) {
                        StringUtil.escapeDelimitedColumn(columnValues[index],
                                this.columnDelimiter, this.out);
                        if (index < columnValues.length - 1) {
                            this.out.write(this.columnDelimiter);
                        }
                    }
                    if (i < n - 1) {
                        this.out.write(this.columnDelimiter);
                    } else {
                        this.out.newLine();
                    }
                } catch (KnowledgeSourceReadException ex1) {
                    throw new FinderException(
                            "Could not read knowledge source", ex1);
                } catch (IOException ex) {
                    throw new FinderException("Could not write row" + ex);
                }
            }
        }
    }
}
