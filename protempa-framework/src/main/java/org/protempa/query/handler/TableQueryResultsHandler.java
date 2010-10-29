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
public class TableQueryResultsHandler extends WriterQueryResultsHandler
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
        this.knowledgeSource = knowledgeSource;
        if (this.headerWritten) {
            try {
                List<String> columnNames = new ArrayList<String>();
                columnNames.add("KeyId");
                for (TableColumnSpec columnSpec : this.columnSpecs) {
                    Util.logger().log(
                            Level.FINE,
                            "Processing columnSpec type "
                                    + columnSpec.getClass().getName());
                    String[] colNames =
                            columnSpec.columnNames(knowledgeSource);

                    for (int index = 0; index < colNames.length; index++) {
                        if (colNames[index] == null) {
                            colNames[index] = "(null)";
                        } else if (colNames[index].length() == 0) {
                            colNames[index] = "(empty)";
                        }
                    }

                    String[] escapedColNames = StringUtil
                            .escapeDelimitedColumns(colNames,
                                    this.columnDelimiter);
                    Util.logger().log(
                            Level.FINE,
                            "Got the following columns for proposition "
                                    + Arrays.toString(this.rowPropositionIds)
                                    + ": "
                                    + StringUtils.join(escapedColNames, ","));
                    for (String colName : escapedColNames) {
                        columnNames.add(colName);
                    }
                }

                write(StringUtils.join(columnNames, this.columnDelimiter));
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
        List<Proposition> filtered = new ArrayList<Proposition>();
        for (Proposition prop : propositions) {
            if (org.arp.javautil.arrays.Arrays.contains(this.rowPropositionIds,
                    prop.getId())) {
                filtered.add(prop);
            }
        }
        for (Proposition prop : filtered) {
            for (int i = 0; i < n; i++) {
                TableColumnSpec columnSpec = this.columnSpecs[i];
                try {

                    List<String> columnValues = new ArrayList<String>();
                    String[] colValues = columnSpec.columnValues(key, prop,
                            derivations, references, this.knowledgeSource);
                    if (i == 0)
                        columnValues.add(key);
                    for (String colVal : colValues) {
                        columnValues.add(colVal);
                    }

                    for (int index = 0; index < columnValues.size(); index++) {
                        if (columnValues.get(index) == null) {
                            columnValues.set(index, "(null)");
                        } else if (columnValues.get(index).length() == 0) {
                            columnValues.set(index, "(empty)");
                        }
                    }

                    List<String> escapedColumnValues = StringUtil
                            .escapeDelimitedColumns(columnValues,
                                    this.columnDelimiter);

                    write(StringUtils.join(escapedColumnValues,
                            this.columnDelimiter));
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
