package org.protempa.query.handler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.Writer;
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
import org.protempa.proposition.UniqueIdentifier;
import org.protempa.query.handler.table.TableColumnSpec;

/**
 * 
 * @author Andrew Post
 */
public final class TableQueryResultsHandler extends WriterQueryResultsHandler
        implements Serializable {

    private static final long serialVersionUID = -1503401944818776787L;
    private final char columnDelimiter;
    private final String[] rowPropositionIds;
    private final TableColumnSpec[] columnSpecs;
    private final boolean headerWritten;
    private KnowledgeSource knowledgeSource;

    public TableQueryResultsHandler(Writer out, char columnDelimiter,
            String[] rowPropositionIds, TableColumnSpec[] columnSpecs,
            boolean headerWritten) {
        super(out);
        checkConstructorArgs(rowPropositionIds, columnSpecs);
        this.columnDelimiter = columnDelimiter;
        this.rowPropositionIds = rowPropositionIds.clone();
        ProtempaUtil.internAll(this.rowPropositionIds);
        this.columnSpecs = columnSpecs.clone();
        this.headerWritten = headerWritten;
    }

    public TableQueryResultsHandler(OutputStream out, char columnDelimiter,
            String[] rowPropositionIds, TableColumnSpec[] columnSpecs,
            boolean headerWritten) {
        super(out);
        checkConstructorArgs(rowPropositionIds, columnSpecs);
        this.columnDelimiter = columnDelimiter;
        this.rowPropositionIds = rowPropositionIds.clone();
        ProtempaUtil.internAll(this.rowPropositionIds);
        this.columnSpecs = columnSpecs.clone();
        this.headerWritten = headerWritten;
    }

    public TableQueryResultsHandler(String fileName, char columnDelimiter,
            String[] rowPropositionIds, TableColumnSpec[] columnSpecs,
            boolean headerWritten) throws IOException {
        super(fileName);
        checkConstructorArgs(rowPropositionIds, columnSpecs);
        this.columnDelimiter = columnDelimiter;
        this.rowPropositionIds = rowPropositionIds.clone();
        ProtempaUtil.internAll(this.rowPropositionIds);
        this.columnSpecs = columnSpecs.clone();
        this.headerWritten = headerWritten;
    }

    public TableQueryResultsHandler(File file, char columnDelimiter,
            String[] rowPropositionIds, TableColumnSpec[] columnSpecs,
            boolean headerWritten) throws IOException {
        super(file);
        checkConstructorArgs(rowPropositionIds, columnSpecs);
        this.columnDelimiter = columnDelimiter;
        this.rowPropositionIds = rowPropositionIds.clone();
        ProtempaUtil.internAll(this.rowPropositionIds);
        this.columnSpecs = columnSpecs.clone();
        this.headerWritten = headerWritten;
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
                        this.columnDelimiter, this);
                newLine();
            } catch (KnowledgeSourceReadException ex1) {
                throw new FinderException("Error reading knowledge source", ex1);
            } catch (IOException ex) {
                throw new FinderException("Could not write header", ex);
            }
        }

    }

    @Override
    public void handleQueryResult(String key, List<Proposition> propositions,
            Map<Proposition, List<Proposition>> derivations,
            Map<UniqueIdentifier, Proposition> references)
            throws FinderException {
        int n = this.columnSpecs.length;
        for (Proposition prop : propositions) {
            if (!org.arp.javautil.arrays.Arrays.contains(
                    this.rowPropositionIds, prop.getId())) {
                continue;
            }
            for (int i = 0; i < n; i++) {
                TableColumnSpec columnSpec = this.columnSpecs[i];
                try {
                    String[] colValues = columnSpec.columnValues(key, prop,
                            derivations, references, this.knowledgeSource);
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
                                this.columnDelimiter, this);
                        if (index < columnValues.length - 1) {
                            write(this.columnDelimiter);
                        }
                    }
                    if (i < n - 1) {
                        write(this.columnDelimiter);
                    } else {
                        newLine();
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
