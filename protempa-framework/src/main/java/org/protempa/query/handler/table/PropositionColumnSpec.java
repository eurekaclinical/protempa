package org.protempa.query.handler.table;

import java.util.List;
import java.util.Map;
import org.protempa.AbstractPropositionDefinitionVisitor;
import org.protempa.AbstractionDefinition;
import org.protempa.ConstantDefinition;
import org.protempa.EventDefinition;
import org.protempa.HighLevelAbstractionDefinition;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.LowLevelAbstractionDefinition;
import org.protempa.PairDefinition;
import org.protempa.PrimitiveParameterDefinition;
import org.protempa.PropositionDefinition;
import org.protempa.ProtempaException;
import org.protempa.SliceDefinition;
import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.AbstractPropositionCheckedVisitor;
import org.protempa.proposition.Constant;
import org.protempa.proposition.Context;
import org.protempa.proposition.Event;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueIdentifier;
import org.protempa.proposition.value.Value;

public class PropositionColumnSpec implements TableColumnSpec {

    private final String[] propertyNames;
    private final boolean showDisplayName;
    private final boolean showAbbrevDisplayName;

    public PropositionColumnSpec(String[] propertyNames) {
        this(propertyNames, false, false);
    }

    public PropositionColumnSpec(String[] propertyNames,
            boolean showDisplayName, boolean showAbbrevDisplayName) {
        if (propertyNames == null) {
            propertyNames = new String[0];
        }
        this.propertyNames = propertyNames.clone();
        this.showDisplayName = showDisplayName;
        this.showAbbrevDisplayName = showAbbrevDisplayName;
    }

    private class NamesPropositionDefinitionVisitor
            extends AbstractPropositionDefinitionVisitor {

        private String[] result;

        @Override
        public void visit(EventDefinition eventDefinition) {
            String[] propertyColumnNames =
                    propertyColumnNames();
            this.result = new String[numColumns(propertyColumnNames, 2)];
            int i = displayNames(0);
            this.result[i++] = "start";
            this.result[i++] = "finish";
            populatePropertyColumnNames(i, propertyColumnNames);
        }

        @Override
        public void visit(HighLevelAbstractionDefinition
                highLevelAbstractionDefinition) {
            visitAbstractionDefinition(highLevelAbstractionDefinition);
        }

        @Override
        public void visit(LowLevelAbstractionDefinition
                lowLevelAbstractionDefinition) {
            visitAbstractionDefinition(lowLevelAbstractionDefinition);
        }

        @Override
        public void visit(PrimitiveParameterDefinition
                primitiveParameterDefinition) {
            String[] propertyColumnNames = propertyColumnNames();
            result = new String[numColumns(propertyColumnNames, 2)];
            int i = displayNames(0);
            result[i++] = "value";
            result[i++] = "tstamp";
            populatePropertyColumnNames(i, propertyColumnNames);
        }

        @Override
        public void visit(SliceDefinition sliceAbstractionDefinition) {
            visitAbstractionDefinition(sliceAbstractionDefinition);
        }

        @Override
        public void visit(ConstantDefinition constantDefinition) {
            String[] propertyColumnNames = propertyColumnNames();
            this.result = new String[numColumns(propertyColumnNames, 0)];
            int i = displayNames(0);
            populatePropertyColumnNames(i, propertyColumnNames);
        }

        String[] getResult() {
            return this.result;
        }

        private void visitAbstractionDefinition(
                AbstractionDefinition abstractionDefinition) {
            String[] propertyColumnNames = propertyColumnNames();
            this.result = new String[numColumns(propertyColumnNames, 3)];
            int i = displayNames(0);
            this.result[i++] = "value";
            this.result[i++] = "start";
            this.result[i++] = "finish";
            populatePropertyColumnNames(i, propertyColumnNames);
        }

        private void populatePropertyColumnNames(int i,
                String[] propertyColumnNames) {
            int j = i;
            for (; i < this.result.length; i++) {
                this.result[i] = propertyColumnNames[i - j];
            }
        }

        private int displayNames(int i) {
            if (showDisplayName) {
                result[i++] = "displayName";
            }
            if (showAbbrevDisplayName) {
                result[i++] = "abbrevDisplayName";
            }
            return i;
        }

        private int numColumns(String[] propertyColumnNames, int n) {
            return propertyColumnNames.length + n + (showDisplayName ? 1 : 0) +
                    (showAbbrevDisplayName ? 1 : 0);
        }

        private String[] propertyColumnNames() {
            String[] propertyColumnNames = new String[propertyNames.length];
            for (int i = 0; i < propertyColumnNames.length; i++) {
                String propName = propertyNames[i];
                propertyColumnNames[i] = propName;
            }
            return propertyColumnNames;
        }

        @Override
        public void visit(PairDefinition def) {
            // TODO Auto-generated method stub
            
        }
    }

    protected String[] columnNames(PropositionDefinition
            propositionDefinition) {
        if (propositionDefinition == null) {
            throw new IllegalArgumentException("refProp cannot be null");
        }
        NamesPropositionDefinitionVisitor propositionDefinitionVisitor =
                new NamesPropositionDefinitionVisitor();
        propositionDefinition.accept(propositionDefinitionVisitor);
        return propositionDefinitionVisitor.getResult();
    }

    private class ValuesPropositionVisitor extends 
            AbstractPropositionCheckedVisitor {

        private final int numProperties;
        private final KnowledgeSource knowledgeSource;
        private String[] result;

        ValuesPropositionVisitor(KnowledgeSource knowledgeSource) {
            this.numProperties = propertyNames.length;
            this.knowledgeSource = knowledgeSource;
        }

        @Override
        public void visit(AbstractParameter abstractParameter) 
                throws KnowledgeSourceReadException {
            this.result = new String[3 + this.numProperties +
                    (showDisplayName ? 1 : 0) +
                    (showAbbrevDisplayName ? 1 : 0)];
            int i = displayNames(0, abstractParameter);
            this.result[i++] = abstractParameter.getStartFormattedShort();
            this.result[i++] = abstractParameter.getFinishFormattedShort();
            this.result[i++] = abstractParameter.getValueFormatted();
            processProperties(abstractParameter, i);
        }

        @Override
        public void visit(Event event) throws KnowledgeSourceReadException {
            this.result = new String[2 + this.numProperties +
                    (showDisplayName ? 1 : 0) +
                    (showAbbrevDisplayName ? 1 : 0)];
            int i = displayNames(0, event);
            this.result[i++] = event.getStartFormattedShort();
            this.result[i++] = event.getFinishFormattedShort();
            processProperties(event, i);
        }

        @Override
        public void visit(PrimitiveParameter primitiveParameter) 
                throws KnowledgeSourceReadException {
            this.result = new String[2 + numProperties +
                    (showDisplayName ? 1 : 0) +
                    (showAbbrevDisplayName ? 1 : 0)];
            int i = displayNames(0, primitiveParameter);
            this.result[i++] = primitiveParameter.getTimestampFormattedShort();
            this.result[i++] = primitiveParameter.getValueFormatted();
            processProperties(primitiveParameter, i);
        }

        @Override
        public void visit(Constant constantParameter) 
                throws KnowledgeSourceReadException {
            result = new String[numProperties +
                    (showDisplayName ? 1 : 0) +
                    (showAbbrevDisplayName ? 1 : 0)];
            int i = displayNames(0, constantParameter);
            processProperties(constantParameter, i);
        }

        @Override
        public void visit(Context context) {
            throw new UnsupportedOperationException("Contexts not supported yet");
        }

        String[] getResult() {
            return this.result;
        }

        private int displayNames(int i,
                Proposition proposition) throws KnowledgeSourceReadException {
            PropositionDefinition propositionDefinition =
                    knowledgeSource.readPropositionDefinition(
                    proposition.getId());
            if (showDisplayName) {
                result[i++] = propositionDefinition.getDisplayName();
            }
            if (showAbbrevDisplayName) {
                result[i++] =
                        propositionDefinition.getAbbreviatedDisplayName();
            }
            return i;
        }

        private void processProperties(Proposition proposition, int j) {
            for (int i = j; i < this.result.length; i++) {
                Value pval = proposition.getProperty(propertyNames[i - j]);
                if (pval != null) {
                    this.result[i] = pval.getFormatted();
                } else {
                    this.result[i] = Util.NULL_COLUMN;
                }
            }
        }
    }

    @Override
    public String[] columnValues(String key, Proposition proposition, 
            Map<Proposition, List<Proposition>> derivations,
            Map<UniqueIdentifier, Proposition> references,
            KnowledgeSource knowledgeSource)
            throws KnowledgeSourceReadException {
        ValuesPropositionVisitor propositionVisitor = 
                new ValuesPropositionVisitor(knowledgeSource);
        try {
            proposition.acceptChecked(propositionVisitor);
        } catch (ProtempaException ex) {
            throw new KnowledgeSourceReadException(
                    "Error writing column values", ex);
        }
        return propositionVisitor.getResult();
    }

    @Override
    public String[] columnNames(String propId, KnowledgeSource knowledgeSource)
            throws KnowledgeSourceReadException {
        PropositionDefinition propDef =
                knowledgeSource.readPropositionDefinition(propId);
        return columnNames(propDef);
    }
}
