package org.protempa.query.handler.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.arp.javautil.arrays.Arrays;
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
import org.protempa.ProtempaUtil;
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

public class PropositionColumnSpec extends AbstractTableColumnSpec {

    private final Link[] links;
    private final String[] propertyNames;
    private final boolean showDisplayName;
    private final boolean showAbbrevDisplayName;
    private final int numInstances;
    private final boolean showPosition;
    private final String columnNamePrefixOverride;

    public PropositionColumnSpec(String[] propertyNames) {
        this(propertyNames, false, false, false);
    }

    public PropositionColumnSpec(String[] propertyNames,
            boolean showDisplayName, boolean showAbbrevDisplayName,
            boolean showPosition) {
        this(propertyNames, showDisplayName, showAbbrevDisplayName, 
                showPosition, null);
    }

    public PropositionColumnSpec(String[] propertyNames,
            boolean showDisplayName, boolean showAbbrevDisplayName,
            boolean showPosition, Link[] links) {
        this(propertyNames, showDisplayName, showAbbrevDisplayName, 
                showPosition, links, 1);
    }

    public PropositionColumnSpec(String[] propertyNames,
            boolean showDisplayName, boolean showAbbrevDisplayName,
            boolean showPosition, Link[] links, int numInstances) {
        this(null, propertyNames, showDisplayName, showAbbrevDisplayName,
                showPosition, links, numInstances);
    }

    public PropositionColumnSpec(String columnNamePrefixOverride,
            String[] propertyNames,
            boolean showDisplayName, boolean showAbbrevDisplayName,
            boolean showPosition, Link[] links, int numInstances) {
        if (propertyNames == null) {
            propertyNames = new String[0];
        }
        this.propertyNames = propertyNames.clone();
        this.showDisplayName = showDisplayName;
        this.showAbbrevDisplayName = showAbbrevDisplayName;
        this.showPosition = showPosition;

        if (links == null) {
            this.links = new Link[0];
        } else {
            ProtempaUtil.checkArrayForNullElement(links, "links");
            this.links = links.clone();
        }

        if (numInstances < 1) {
            throw new IllegalArgumentException("numInstances cannot be < 1");
        }
        this.numInstances = numInstances;
        this.columnNamePrefixOverride = columnNamePrefixOverride;
    }

    private class NamesPropositionDefinitionVisitor
            extends AbstractPropositionDefinitionVisitor {

        private String[] result;
        private String prefix;

        NamesPropositionDefinitionVisitor(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public void visit(EventDefinition eventDefinition) {
            String[] propertyColumnNames =
                    propertyColumnNames();
            this.result = new String[numColumns(propertyColumnNames, 0, 3)];
            int i = displayNames(0);
            if (PropositionColumnSpec.this.showPosition) {
                this.result[i++] = this.prefix + "_start";
                this.result[i++] = this.prefix + "_finish";
                this.result[i++] = this.prefix + "_length";
            }
            populatePropertyColumnNames(i, propertyColumnNames);
        }

        @Override
        public void visit(
              HighLevelAbstractionDefinition highLevelAbstractionDefinition) {
            visitAbstractionDefinition(highLevelAbstractionDefinition);
        }

        @Override
        public void visit(
                LowLevelAbstractionDefinition lowLevelAbstractionDefinition) {
            visitAbstractionDefinition(lowLevelAbstractionDefinition);
        }

        @Override
        public void visit(
                PrimitiveParameterDefinition primitiveParameterDefinition) {
            String[] propertyColumnNames = propertyColumnNames();
            result = new String[numColumns(propertyColumnNames, 1, 1)];
            int i = displayNames(0);
            System.err.println("primitiveParameterDefinition: " +
                    primitiveParameterDefinition);
            result[i++] = this.prefix + "_value";
            if (showPosition) {
                result[i++] = this.prefix + "_tstamp";
            }
            populatePropertyColumnNames(i, propertyColumnNames);
        }

        @Override
        public void visit(SliceDefinition sliceAbstractionDefinition) {
            visitAbstractionDefinition(sliceAbstractionDefinition);
        }

        @Override
        public void visit(ConstantDefinition constantDefinition) {
            String[] propertyColumnNames = propertyColumnNames();
            this.result = new String[numColumns(propertyColumnNames, 0, 0)];
            int i = displayNames(0);
            populatePropertyColumnNames(i, propertyColumnNames);
        }

        String[] getResult() {
            return this.result;
        }

        private void visitAbstractionDefinition(
                AbstractionDefinition abstractionDefinition) {
            String[] propertyColumnNames = propertyColumnNames();
            this.result = new String[numColumns(propertyColumnNames, 1, 3)];
            int i = displayNames(0);
            System.err.println("abstractionDefinition: " +
                    abstractionDefinition);
            this.result[i++] = this.prefix + "_value";
            if (showPosition) {
                this.result[i++] = this.prefix + "_start";
                this.result[i++] = this.prefix + "_finish";
                this.result[i++] = this.prefix + "_length";
            }
            populatePropertyColumnNames(i, propertyColumnNames);
        }

        @Override
        public void visit(PairDefinition def) {
            visitAbstractionDefinition(def);
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
                result[i++] = this.prefix + "_displayName";
            }
            if (showAbbrevDisplayName) {
                result[i++] = this.prefix + "_abbrevDisplayName";
            }
            return i;
        }

        private int numColumns(String[] propertyColumnNames, int m, int n) {
            return propertyColumnNames.length + m + (showDisplayName ? 1 : 0)
                    + (showAbbrevDisplayName ? 1 : 0)
                    + (showPosition ? n : 0);
        }

        private String[] propertyColumnNames() {
            String[] propertyColumnNames = new String[propertyNames.length];
            for (int i = 0; i < propertyColumnNames.length; i++) {
                String propName = propertyNames[i];
                propertyColumnNames[i] = this.prefix + "." + propName;
            }
            return propertyColumnNames;
        }
    }

    protected String[] columnNames(String prefix,
            PropositionDefinition propositionDefinition) {
        if (propositionDefinition == null) {
            throw new IllegalArgumentException(
                    "propositionDefinition cannot be null");
        }
        NamesPropositionDefinitionVisitor propositionDefinitionVisitor =
                new NamesPropositionDefinitionVisitor(prefix);
        propositionDefinition.accept(propositionDefinitionVisitor);
        return propositionDefinitionVisitor.getResult();
    }

    private class ValuesPropositionVisitor extends AbstractPropositionCheckedVisitor {

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
            this.result = new String[numColumns(1, 3)];
            int i = displayNames(0, abstractParameter);
            if (showPosition) {
                this.result[i++] = abstractParameter.getStartFormattedShort();
                this.result[i++] = abstractParameter.getFinishFormattedShort();
                this.result[i++] = abstractParameter.getLengthFormattedShort();
            }
            this.result[i++] = abstractParameter.getValueFormatted();
            processProperties(abstractParameter, i);
        }

        @Override
        public void visit(Event event) throws KnowledgeSourceReadException {
            this.result = new String[numColumns(0, 3)];
            int i = displayNames(0, event);
            if (showPosition) {
                this.result[i++] = event.getStartFormattedShort();
                this.result[i++] = event.getFinishFormattedShort();
                this.result[i++] = event.getLengthFormattedShort();
            }
            processProperties(event, i);
        }

        @Override
        public void visit(PrimitiveParameter primitiveParameter)
                throws KnowledgeSourceReadException {
            this.result = new String[numColumns(1, 1)];
            int i = displayNames(0, primitiveParameter);
            if (showPosition)
                this.result[i++] =
                        primitiveParameter.getTimestampFormattedShort();
            this.result[i++] = primitiveParameter.getValueFormatted();
            processProperties(primitiveParameter, i);
        }

        @Override
        public void visit(Constant constantParameter)
                throws KnowledgeSourceReadException {
            result = new String[numColumns(0, 0)];
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

        private int numColumns(int m, int n) {
            return this.numProperties + m + (showDisplayName ? 1 : 0)
                    + (showAbbrevDisplayName ? 1 : 0)
                    + (showPosition ? n : 0);
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
        Collection<Proposition> propositions =
                this.traverseLinks(this.links, proposition, derivations,
                references, knowledgeSource);
        List<String> result = new ArrayList<String>();
        int i = 0;
        for (Proposition prop : propositions) {
            if (i < this.numInstances) {
                ValuesPropositionVisitor propositionVisitor =
                        new ValuesPropositionVisitor(knowledgeSource);
                try {
                    prop.acceptChecked(propositionVisitor);
                } catch (ProtempaException ex) {
                    throw new KnowledgeSourceReadException(
                            "Error writing column values", ex);
                }
                Arrays.addAll(result, propositionVisitor.getResult());
                i++;
            } else {
                break;
            }
        }
        return result.toArray(new String[result.size()]);
    }

    @Override
    public String[] columnNames(String propId, KnowledgeSource knowledgeSource)
            throws KnowledgeSourceReadException {
        PropositionDefinition propDef =
                knowledgeSource.readPropositionDefinition(propId);
        String headerString = this.columnNamePrefixOverride != null ?
            this.columnNamePrefixOverride :
            generateLinksHeaderString(this.links);
        String[] one = columnNames(headerString, propDef);
        String[] result = new String[one.length * this.numInstances];
        for (int i = 0; i < result.length; i++) {
            result[i] = one[i % one.length];
        }
        return result;
    }
}
