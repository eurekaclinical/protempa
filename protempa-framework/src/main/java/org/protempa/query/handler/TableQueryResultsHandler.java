package org.protempa.query.handler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private final String rowPropositionId;
    private final TableColumnSpec[] columnSpecs;
    private final boolean headerWritten;
    private KnowledgeSource knowledgeSource;

    public TableQueryResultsHandler(Writer out, char columnDelimiter,
            String rowPropositionId, TableColumnSpec[] columnSpecs,
            boolean headerWritten) {
        super(out);
        checkConstructorArgs(rowPropositionId, columnSpecs);
        this.columnDelimiter = columnDelimiter;
        this.rowPropositionId = rowPropositionId;
        this.columnSpecs = columnSpecs.clone();
        this.headerWritten = headerWritten;
    }

    public TableQueryResultsHandler(OutputStream out, char columnDelimiter,
            String rowPropositionId, TableColumnSpec[] columnSpecs,
            boolean headerWritten) {
        super(out);
        checkConstructorArgs(rowPropositionId, columnSpecs);
        this.columnDelimiter = columnDelimiter;
        this.rowPropositionId = rowPropositionId;
        this.columnSpecs = columnSpecs.clone();
        this.headerWritten = headerWritten;
    }

    public TableQueryResultsHandler(String fileName, char columnDelimiter,
            String rowPropositionId, TableColumnSpec[] columnSpecs,
            boolean headerWritten)
            throws IOException {
        super(fileName);
        checkConstructorArgs(rowPropositionId, columnSpecs);
        this.columnDelimiter = columnDelimiter;
        this.rowPropositionId = rowPropositionId;
        this.columnSpecs = columnSpecs.clone();
        this.headerWritten = headerWritten;
    }

    public TableQueryResultsHandler(File file, char columnDelimiter,
            String rowPropositionId, TableColumnSpec[] columnSpecs,
            boolean headerWritten)
            throws IOException {
        super(file);
        checkConstructorArgs(rowPropositionId, columnSpecs);
        this.columnDelimiter = columnDelimiter;
        this.rowPropositionId = rowPropositionId;
        this.columnSpecs = columnSpecs.clone();
        this.headerWritten = headerWritten;
    }

    private void checkConstructorArgs(String rowPropositionId,
            TableColumnSpec[] columnSpecs) {
        if (rowPropositionId == null) {
            throw new IllegalArgumentException(
                    "rowPropositionId cannot be null");
        }
        ProtempaUtil.checkArray(columnSpecs, "columnSpecs");
    }

    public String getRowPropositionId() {
        return rowPropositionId;
    }

    public char getColumnDelimiter() {
        return columnDelimiter;
    }

    public TableColumnSpec[] getColumnSpecs() {
        return columnSpecs.clone();
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
                    String[] colNames = columnSpec.columnNames(
                            this.rowPropositionId, knowledgeSource);
                    String[] escapedColNames =
                            StringUtil.escapeDelimitedColumns(colNames,
                            this.columnDelimiter);
                    for (String colName : escapedColNames) {
                        columnNames.add(colName);
                    }
                }

                write(StringUtils.join(columnNames, this.columnDelimiter));
                newLine();
            } catch (KnowledgeSourceReadException ex1) {
                throw new FinderException("Error reading knowledge source",
                        ex1);
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
            if (prop.getId().equals(this.rowPropositionId)) {
                filtered.add(prop);
            }
        }
        for (Proposition prop : filtered) {
            for (int i = 0; i < n; i++) {
                TableColumnSpec columnSpec = this.columnSpecs[i];
                try {

                    List<String> columnValues = new ArrayList<String>();
                    String[] colValues = columnSpec.columnValues(key,
                            prop, derivations, references, this.knowledgeSource);
                    if (i == 0)
                        columnValues.add(key);
                    for (String colVal : colValues) {
                        columnValues.add(colVal);
                    }
                    List<String> escapedColumnValues =
                            StringUtil.escapeDelimitedColumns(columnValues,
                            this.columnDelimiter);
                    write(StringUtils.join(escapedColumnValues,
                            this.columnDelimiter));
                    if (i < n - 1) {
                        write(this.columnDelimiter);
                    } else {
                        newLine();
                    }
                } catch (KnowledgeSourceReadException ex1) {
                    throw new FinderException("Could not read knowledge source",
                            ex1);
                } catch (IOException ex) {
                    throw new FinderException("Could not write row" + ex);
                }
            }
        }
    }
}
