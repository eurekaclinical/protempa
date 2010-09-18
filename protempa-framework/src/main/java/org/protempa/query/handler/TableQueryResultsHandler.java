package org.protempa.query.handler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.arp.javautil.string.StringUtil;
import org.protempa.AbstractionDefinition;
import org.protempa.EventDefinition;
import org.protempa.FinderException;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.PrimitiveParameterDefinition;
import org.protempa.PropertyDefinition;
import org.protempa.PropositionDefinition;
import org.protempa.ProtempaUtil;
import org.protempa.ReferenceDefinition;
import org.protempa.proposition.ConstantParameter;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.TemporalParameter;
import org.protempa.proposition.TemporalProposition;
import org.protempa.proposition.UniqueIdentifier;
import org.protempa.proposition.comparator.AllPropositionIntervalComparator;
import org.protempa.proposition.value.Value;

/**
 *
 * @author Andrew Post
 */
public class TableQueryResultsHandler extends WriterQueryResultsHandler
        implements Serializable {
    private static final long serialVersionUID = -1503401944818776787L;

    private static final String NULL_COLUMN = "(empty)";
    
    private final char columnDelimiter;
    private final String rowPropositionId;
    private final ColumnSpec[] columnSpecs;
    private final boolean headerWritten;
    private KnowledgeSource knowledgeSource;

    public TableQueryResultsHandler(Writer out, char columnDelimiter,
            String rowPropositionId, ColumnSpec[] columnSpecs,
            boolean headerWritten) {
        super(out);
        checkConstructorArgs(rowPropositionId, columnSpecs);
        this.columnDelimiter = columnDelimiter;
        this.rowPropositionId = rowPropositionId;
        this.columnSpecs = columnSpecs.clone();
        this.headerWritten = headerWritten;
    }

    public TableQueryResultsHandler(OutputStream out, char columnDelimiter,
            String rowPropositionId, ColumnSpec[] columnSpecs,
            boolean headerWritten) {
        super(out);
        checkConstructorArgs(rowPropositionId, columnSpecs);
        this.columnDelimiter = columnDelimiter;
        this.rowPropositionId = rowPropositionId;
        this.columnSpecs = columnSpecs.clone();
        this.headerWritten = headerWritten;
    }

    public TableQueryResultsHandler(String fileName, char columnDelimiter,
            String rowPropositionId, ColumnSpec[] columnSpecs,
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
            String rowPropositionId, ColumnSpec[] columnSpecs,
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
            ColumnSpec[] columnSpecs) {
        if (rowPropositionId == null)
            throw new IllegalArgumentException(
                    "rowPropositionId cannot be null");
        ProtempaUtil.checkArray(columnSpecs, "columnSpecs");
    }

    public String getRowPropositionId() {
        return rowPropositionId;
    }

    public char getColumnDelimiter() {
        return columnDelimiter;
    }

    public ColumnSpec[] getColumnSpecs() {
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
                for (ColumnSpec columnSpec : this.columnSpecs) {
                    String[] colNames = columnSpec.columnNames(
                            getRowPropositionId(), knowledgeSource);
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
            Map<Proposition,List<Proposition>> derivations,
            Map<UniqueIdentifier, List<Proposition>> references)
            throws FinderException {
        int n = this.columnSpecs.length;
        List<Proposition> filtered = new ArrayList<Proposition>();
        for (Proposition prop : propositions) {
            if (prop.getId().equals(this.rowPropositionId))
                filtered.add(prop);
        }
        for (Proposition prop : filtered) {
            for (int i = 0; i < n; i++) {
                ColumnSpec columnSpec = this.columnSpecs[i];
                try {

                    List<String> columnValues = new ArrayList<String>();
                    String[] colValues = columnSpec.columnValues(key,
                        prop, derivations, references, this.knowledgeSource);
                    columnValues.add(key);
                    for (String colVal : colValues)
                        columnValues.add(colVal);
                    List<String> escapedColumnValues =
                            StringUtil.escapeDelimitedColumns(columnValues,
                            this.columnDelimiter);
                    write(StringUtils.join(escapedColumnValues,
                            this.columnDelimiter));
                    if (i < n - 1)
                        write(this.columnDelimiter);
                    else
                        newLine();
                } catch (KnowledgeSourceReadException ex1) {
                    throw new FinderException("Could not read knowledge source",
                            ex1);
                } catch (IOException ex) {
                    throw new FinderException("Could not write row" + ex);
                }
            }
        }
    }

    /**
     * Specification of a column or sequence of columns in a delimited file
     * that represent a proposition or property.
     */
    public static interface ColumnSpec {
        /**
         * Gets the names of the columns representing one instance of a
         * proposition or property. These columns may be repeated if the
         * specification results in matching more than one proposition or
         * property.
         *
         * @param knowledgeSource the active {@link KnowledgeSource}.
         * @return a {@link String[]} of column names.
         * @throws KnowledgeSourceReadException if an attempt at reading from
         * the knowledge source failed.
         */
        String[] columnNames(String propId, KnowledgeSource knowledgeSource)
                throws KnowledgeSourceReadException;

        /**
         * Gets the values of the specified propositions or properties for
         * one row of data.
         * 
         * @param key a key id {@link String}.
         * @param proposition a {@link List<Proposition>} for the specified
         * key with the specified proposition id.
         * @param knowledgeSource the active {@link KnowledgeSource}.
         * @return a {@link String[]} of column values.
         * @throws KnowledgeSourceReadException if an attempt at reading from
         * the knowledge source failed.
         */
        String[] columnValues(String key, Proposition proposition,
                Map<Proposition,List<Proposition>> derivations,
                Map<UniqueIdentifier,List<Proposition>> references,
                KnowledgeSource knowledgeSource) throws
                KnowledgeSourceReadException;
    }

    public static class CountColumnSpec implements ColumnSpec {
        private String referenceName;

        public CountColumnSpec(String referenceName) {
            this.referenceName = referenceName;
        }

        @Override
        public String[] columnNames(String propId,
                KnowledgeSource knowledgeSource) {
            return new String[] {this.referenceName + "_count"};
        }

        @Override
        public String[] columnValues(String key, Proposition proposition,
                Map<Proposition,List<Proposition>> derivations,
                Map<UniqueIdentifier, List<Proposition>> references,
                KnowledgeSource knowledgeSource) {
            return new String[] {
                "" + references.get(proposition.getUniqueIdentifier()).size()
            };

        }

    }

    public static class AtLeastNColumnSpec implements ColumnSpec {
        private int n;
        private String referenceName;

        public AtLeastNColumnSpec(String referenceName, int n) {
            if (n < 1) {
                throw new IllegalArgumentException("n must be at least 1");
            }
            this.referenceName = referenceName;
        }

        @Override
        public String[] columnNames(String propId,
                KnowledgeSource knowledgeSource) {
            return new String[] {propId + "_" + this.referenceName +
                    "_at_least_" + n};
        }

        @Override
        public String[] columnValues(String key, 
                Proposition proposition,
                Map<Proposition,List<Proposition>> derivations,
                Map<UniqueIdentifier, List<Proposition>> references,
                KnowledgeSource knowledgeSource) {
            boolean atLeastN =
                    references.get(proposition.getUniqueIdentifier()).size()
                    >= this.n;
            return new String[] {
                "" + atLeastN
            };
        }

    }

    public static class PropositionColumnSpec implements ColumnSpec {
        private String[] propertyNames;

        public PropositionColumnSpec(String[] propertyNames) {
            if (propertyNames == null)
                propertyNames = new String[0];
            this.propertyNames = propertyNames.clone();
        }

        protected String[] columnNames(PropositionDefinition refProp) {
            if (refProp == null)
                throw new IllegalArgumentException("refProp cannot be null");
            String[] propertyColumnNames =
                    new String[this.propertyNames.length];
            for (int i = 0; i < propertyColumnNames.length; i++) {
                String propName = this.propertyNames[i];
                if (refProp.propertyDefinition(propName) == null) {
                    throw new IllegalArgumentException(refProp.getId() +
                            " does not have a property named " + propName);
                }
                propertyColumnNames[i] =
                        refProp.getId() + "_" + propName;
            }
            String[] result;
            String refPropId = refProp.getId();
            if (refProp instanceof PrimitiveParameterDefinition) {
                result = new String[propertyColumnNames.length + 2];
                result[0] = refPropId + "_value";
                result[1] = refPropId + "_tstamp";
                int i = 2;
                for (; i < result.length; i++) {
                    result[i] = propertyColumnNames[i - 2];
                }
            } else if (refProp instanceof AbstractionDefinition) {
                result = new String[propertyColumnNames.length + 3];
                result[0] = refPropId + "_value";
                result[1] = refPropId + "_start";
                result[2] = refPropId + "_finish";
                int i = 3;
                for (; i < result.length; i++) {
                    result[i] = propertyColumnNames[i - 3];
                }
            } else if (refProp instanceof EventDefinition) {
                result = new String[propertyColumnNames.length + 2];
                result[0] = refPropId + "_start";
                result[1] = refPropId + "_finish";
                int i = 2;
                for (; i < result.length; i++) {
                    result[i] = propertyColumnNames[i - 2];
                }
            } else {
                result = new String[propertyColumnNames.length + 1];
            }
            return result;
        }

        @Override
        public String[] columnValues(String key, Proposition proposition,
                Map<Proposition,List<Proposition>> derivations,
                Map<UniqueIdentifier, List<Proposition>> references,
                KnowledgeSource knowledgeSource)
                throws KnowledgeSourceReadException {
            String[] result;
            PropositionDefinition pd =
                    knowledgeSource.readPropositionDefinition(
                    proposition.getId());
            PropertyDefinition[] pdefs = pd.getPropertyDefinitions();
            int j;
            if (proposition instanceof PrimitiveParameter) {
                PrimitiveParameter pp = (PrimitiveParameter) proposition;
                result = new String[2 + pdefs.length];
                result[0] = pp.getTimestampFormattedShort();
                result[1] = pp.getValueFormatted();
                j = 2;
            } else if (proposition instanceof TemporalParameter) {
                TemporalParameter tp = (TemporalParameter) proposition;
                result = new String[3 + pdefs.length];
                result[0] = tp.getStartFormattedShort();
                result[1] = tp.getFinishFormattedShort();
                result[2] = tp.getValueFormatted();
                j = 3;
            } else if (proposition instanceof TemporalProposition) {
                TemporalProposition tp = (TemporalProposition) proposition;
                result = new String[2 + pdefs.length];
                result[0] = tp.getStartFormattedShort();
                result[1] = tp.getFinishFormattedShort();
                j = 2;
            } else {
                ConstantParameter cp = (ConstantParameter) proposition;
                result = new String[1 + pdefs.length];
                result[0] = cp.getValueFormatted();
                j = 0;
            }
            for (int i = j; i < result.length; i++) {
                Value pval = proposition.getProperty(pdefs[i - j].getName());
                if (pval != null)
                    result[i] = pval.getFormatted();
                else
                    result[i] = NULL_COLUMN;
            }
            return result;
        }

        @Override
        public String[] columnNames(String propId,
                KnowledgeSource knowledgeSource)
                throws KnowledgeSourceReadException {
            PropositionDefinition propDef =
                    knowledgeSource.readPropositionDefinition(propId);
            return columnNames(propDef);
        }


    }

    public static class DirectChildrenColumnSpec extends
            PropositionColumnSpec {

        public static enum Order {
            INCREASING,
            DECREASING,
            NONE
        }

        private final String propositionId;
        private final Order order;
        /**
         *
         * @param propositionId
         * @param order the order for multiple direct children that are
         * temporal propositions and have the same id.
         * @throws KnowledgeSourceReadException
         */
        public DirectChildrenColumnSpec(String propositionId, 
                String[] propertyNames, Order order) {
            super(propertyNames);
            if (order == null)
                order = Order.NONE;
            if (propositionId == null)
                throw new IllegalArgumentException(
                        "propositionId cannot be null");
            
            this.propositionId = propositionId;
            this.order = order;
            
        }

        @Override
        public String[] columnNames(String propId,
                KnowledgeSource knowledgeSource)
                throws KnowledgeSourceReadException {
            PropositionDefinition propDef =
                    knowledgeSource.readPropositionDefinition(propositionId);
            String[] directChildren = propDef.getDirectChildren();
            String[][] colNames = new String[directChildren.length][];
            for (int i = 0; i < directChildren.length; i++) {
                String childPropId = directChildren[i];
                PropositionDefinition childPropDef =
                        knowledgeSource.readPropositionDefinition(childPropId);
                colNames[i] = super.columnNames(childPropDef);
            }
            return super.columnNames(propDef);
        }

        @Override
        public String[] columnValues(String key, Proposition proposition,
                Map<Proposition,List<Proposition>> derivations,
                Map<UniqueIdentifier,List<Proposition>> references,
                KnowledgeSource knowledgeSource)
                throws KnowledgeSourceReadException {
            List<String> result = new ArrayList<String>();
            List<Proposition> derived =
                    new ArrayList(derivations.get(proposition));
            if (this.order == Order.INCREASING) {
                Collections.sort(derived,
                        new AllPropositionIntervalComparator());
            } else if (this.order == Order.DECREASING) {
                Collections.sort(derived,
                        Collections.reverseOrder(
                        new AllPropositionIntervalComparator()));
            }
            for (Proposition prop : derived) {
                String[] vals = super.columnValues(key, prop,
                        derivations, references, knowledgeSource);
                for (String val : vals) {
                    result.add(val);
                }
            }
            return result.toArray(new String[result.size()]);
        }
    }

    public static class ReferenceColumnSpec extends PropositionColumnSpec {
        private final String referenceName;

        public ReferenceColumnSpec(String referenceName,
                String[] propertyNames) {
            super(propertyNames);
            if (referenceName == null)
                throw new IllegalArgumentException(
                        "referenceName cannot be null");
            this.referenceName = referenceName;
        }

        public String getReferenceName() {
            return this.referenceName;
        }

        @Override
        public String[] columnNames(String propId,
                KnowledgeSource knowledgeSource)
                throws KnowledgeSourceReadException {
            PropositionDefinition prop =
                    knowledgeSource.readPropositionDefinition(propId);
            ReferenceDefinition refDef = prop.referenceDefinition(
                    this.referenceName);
            PropositionDefinition refProp =
                    knowledgeSource.readPropositionDefinition(
                    refDef.getPropositionId());
            return super.columnNames(refProp);
        }

        

        @Override
        public String[] columnValues(String key, Proposition proposition,
                Map<Proposition,List<Proposition>> derivations,
                Map<UniqueIdentifier,List<Proposition>> references,
                KnowledgeSource knowledgeSource) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    public static class PropertyColumnSpec implements ColumnSpec {
        private final String propertyName;
        private final String[] columnNames;

        public PropertyColumnSpec(String propertyName) {
            if (propertyName == null)
                throw new IllegalArgumentException(
                        "propertyName cannot be null");
            this.propertyName = propertyName;
            this.columnNames = new String[] {
                this.propertyName
            };
        }

        public String getPropertyName() {
            return propertyName;
        }

        @Override
        public String[] columnNames(String propId,
                KnowledgeSource knowledgeSource) {
            return this.columnNames.clone();
        }

        @Override
        public String[] columnValues(String key, Proposition proposition,
                Map<Proposition,List<Proposition>> derivations,
                Map<UniqueIdentifier,List<Proposition>> references,
                KnowledgeSource knowledgeSource) {
            Value propertyValue = proposition.getProperty(this.propertyName);
            String result;
            if (propertyValue != null)
                result = propertyValue.getFormatted();
            else
                result = NULL_COLUMN;
            return new String[] {
                result
            };
        }


    }


}
