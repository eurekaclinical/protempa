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
import org.protempa.PrimitiveParameterDefinition;
import org.protempa.PropositionDefinition;
import org.protempa.SliceDefinition;
import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.AbstractPropositionVisitor;
import org.protempa.proposition.Constant;
import org.protempa.proposition.Context;
import org.protempa.proposition.Event;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueIdentifier;
import org.protempa.proposition.value.Value;

public class PropositionColumnSpec implements TableColumnSpec {

    private String[] propertyNames;

    public PropositionColumnSpec(String[] propertyNames) {
        if (propertyNames == null) {
            propertyNames = new String[0];
        }
        this.propertyNames = propertyNames.clone();
    }

    private class NamesPropositionDefinitionVisitor extends AbstractPropositionDefinitionVisitor {

        private String[] result;

        @Override
        public void visit(EventDefinition eventDefinition) {
            String[] propertyColumnNames = propertyColumnNames(eventDefinition);
            this.result = new String[propertyColumnNames.length + 2];
            this.result[0] = "start";
            this.result[1] = "finish";
            int i = 2;
            for (; i < this.result.length; i++) {
                this.result[i] = propertyColumnNames[i - 2];
            }
        }

        @Override
        public void visit(HighLevelAbstractionDefinition highLevelAbstractionDefinition) {
            visitAbstractionDefinition(highLevelAbstractionDefinition);
        }

        @Override
        public void visit(LowLevelAbstractionDefinition lowLevelAbstractionDefinition) {
            visitAbstractionDefinition(lowLevelAbstractionDefinition);
        }

        @Override
        public void visit(PrimitiveParameterDefinition primitiveParameterDefinition) {
            String[] propertyColumnNames = propertyColumnNames(primitiveParameterDefinition);
            result = new String[propertyColumnNames.length + 2];
            result[0] = "value";
            result[1] = "tstamp";
            int i = 2;
            for (; i < result.length; i++) {
                result[i] = propertyColumnNames[i - 2];
            }
        }

        @Override
        public void visit(SliceDefinition sliceAbstractionDefinition) {
            visitAbstractionDefinition(sliceAbstractionDefinition);
        }

        @Override
        public void visit(ConstantDefinition constantDefinition) {
            String[] propertyColumnNames = propertyColumnNames(constantDefinition);
            this.result = new String[propertyColumnNames.length];
            for (int i = 0; i < this.result.length; i++) {
                this.result[i] = propertyColumnNames[i];
            }
        }

        String[] getResult() {
            return this.result;
        }

        private String[] propertyColumnNames(PropositionDefinition propositionDefinition) {
            String[] propertyColumnNames = new String[propertyNames.length];
            for (int i = 0; i < propertyColumnNames.length; i++) {
                String propName = propertyNames[i];
                propertyColumnNames[i] = propName;
            }
            return propertyColumnNames;
        }

        private void visitAbstractionDefinition(AbstractionDefinition abstractionDefinition) {
            String[] propertyColumnNames = propertyColumnNames(abstractionDefinition);
            this.result = new String[propertyColumnNames.length + 3];
            this.result[0] = "value";
            this.result[1] = "start";
            this.result[2] = "finish";
            int i = 3;
            for (; i < this.result.length; i++) {
                this.result[i] = propertyColumnNames[i - 3];
            }
        }
    }

    protected String[] columnNames(PropositionDefinition propositionDefinition) {
        if (propositionDefinition == null) {
            throw new IllegalArgumentException("refProp cannot be null");
        }
        NamesPropositionDefinitionVisitor propositionDefinitionVisitor = new NamesPropositionDefinitionVisitor();
        propositionDefinition.accept(propositionDefinitionVisitor);
        return propositionDefinitionVisitor.getResult();
    }

    private class ValuesPropositionVisitor extends AbstractPropositionVisitor {

        private final int numProperties;
        private String[] result;

        ValuesPropositionVisitor() {
            this.numProperties = propertyNames.length;
        }

        @Override
        public void visit(AbstractParameter abstractParameter) {
            this.result = new String[3 + this.numProperties];
            this.result[0] = abstractParameter.getStartFormattedShort();
            this.result[1] = abstractParameter.getFinishFormattedShort();
            this.result[2] = abstractParameter.getValueFormatted();
            processProperties(abstractParameter, 3);
        }

        @Override
        public void visit(Event event) {
            this.result = new String[2 + this.numProperties];
            this.result[0] = event.getStartFormattedShort();
            this.result[1] = event.getFinishFormattedShort();
            processProperties(event, 2);
        }

        @Override
        public void visit(PrimitiveParameter primitiveParameter) {
            this.result = new String[2 + numProperties];
            this.result[0] = primitiveParameter.getTimestampFormattedShort();
            this.result[1] = primitiveParameter.getValueFormatted();
            processProperties(primitiveParameter, 2);
        }

        @Override
        public void visit(Constant constantParameter) {
            result = new String[numProperties];
            processProperties(constantParameter, 0);
        }

        @Override
        public void visit(Context context) {
            throw new UnsupportedOperationException("Contexts not supported yet");
        }

        String[] getResult() {
            return this.result;
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
        ValuesPropositionVisitor propositionVisitor = new ValuesPropositionVisitor();
        proposition.accept(propositionVisitor);
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
