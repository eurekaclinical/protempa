package org.protempa.query.handler.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.arp.javautil.arrays.Arrays;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.PropositionDefinition;
import org.protempa.ProtempaException;
import org.protempa.ProtempaUtil;
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
    private final int numInstances;
    private final String columnNamePrefixOverride;
    private final OutputConfig outputConfig;

    public PropositionColumnSpec(String[] propertyNames) {
        this(propertyNames, new OutputConfig());
    }

    public PropositionColumnSpec(String[] propertyNames,
            OutputConfig outputConfig) {
        this(propertyNames, outputConfig, null);
    }

    public PropositionColumnSpec(String[] propertyNames,
            OutputConfig outputConfig, Link[] links) {
        this(propertyNames, outputConfig, links, 1);
    }

    public PropositionColumnSpec(String[] propertyNames,
            OutputConfig outputConfig, Link[] links, int numInstances) {
        this(null, propertyNames, outputConfig, links, numInstances);
    }

    public PropositionColumnSpec(String columnNamePrefixOverride,
            String[] propertyNames, OutputConfig outputConfig, Link[] links,
            int numInstances) {
        if (propertyNames == null) {
            propertyNames = new String[0];
        }
        this.propertyNames = propertyNames.clone();
        this.outputConfig = new OutputConfig(outputConfig);

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

    protected String[] columnNames(String prefix,
            PropositionDefinition propositionDefinition) {
        if (propositionDefinition == null) {
            throw new IllegalArgumentException(
                    "propositionDefinition cannot be null");
        }
        List<String> results = new ArrayList<String>();
        if (this.outputConfig.showValue()) {
            results.add(StringUtils.defaultIfEmpty(
                    outputConfig.getValueHeading(),
                    this.columnNamePrefixOverride + "_value"));
        }
        if (this.outputConfig.showDisplayName()) {
            results.add(StringUtils.defaultIfEmpty(
                    outputConfig.getDisplayNameHeading(),
                    this.columnNamePrefixOverride + "_displayName"));
        }
        if (this.outputConfig.showAbbrevDisplayName()) {
            results.add(StringUtils.defaultIfEmpty(
                    outputConfig.getAbbrevDisplayNameHeading(),
                    this.columnNamePrefixOverride + "_abbrevDisplayName"));
        }
        if (this.outputConfig.showStartOrTimestamp()) {
            results.add(StringUtils.defaultIfEmpty(
                    outputConfig.getStartOrTimestampHeading(),
                    this.columnNamePrefixOverride + "_startOrTimeStamp"));
        }
        if (this.outputConfig.showFinish()) {
            results.add(StringUtils.defaultIfEmpty(
                    outputConfig.getFinishHeading(),
                    this.columnNamePrefixOverride + "_finish"));
        }
        if (this.outputConfig.showLength()) {
            results.add(StringUtils.defaultIfEmpty(
                    outputConfig.getLengthHeading(),
                    this.columnNamePrefixOverride + "_length"));
        }
        for (String heading : this.propertyNames) {
            results.add(this.columnNamePrefixOverride + "." + heading);
        }
        return results.toArray(new String[results.size()]);
    }

    private class ValuesPropositionVisitor extends
            AbstractPropositionCheckedVisitor {

        private final KnowledgeSource knowledgeSource;
        private String[] result;

        ValuesPropositionVisitor(KnowledgeSource knowledgeSource) {
            this.knowledgeSource = knowledgeSource;
        }

        @Override
        public void visit(AbstractParameter abstractParameter)
                throws KnowledgeSourceReadException {
            List<String> resultList = new ArrayList<String>();

            if (outputConfig.showValue()) {
                resultList.add(abstractParameter.getValueFormatted());
            }
            displayNames(abstractParameter, resultList);
            if (outputConfig.showStartOrTimestamp()) {
                resultList.add(abstractParameter.getStartFormattedShort());
            }
            if (outputConfig.showFinish()) {
                resultList.add(abstractParameter.getFinishFormattedShort());
            }
            if (outputConfig.showLength()) {
                resultList.add(abstractParameter.getLengthFormattedShort());
            }
            processProperties(abstractParameter, resultList);

            this.result = new String[resultList.size()];
            resultList.toArray(result);
        }

        @Override
        public void visit(Event event) throws KnowledgeSourceReadException {
            List<String> resultList = new ArrayList<String>();

            if (outputConfig.showValue()) {
                resultList.add(null);
            }
            displayNames(event, resultList);
            if (outputConfig.showStartOrTimestamp()) {
                resultList.add(event.getStartFormattedShort());
            }
            if (outputConfig.showFinish()) {
                resultList.add(event.getFinishFormattedShort());
            }
            if (outputConfig.showLength()) {
                resultList.add(event.getLengthFormattedShort());
            }
            processProperties(event, resultList);

            this.result = new String[resultList.size()];
            resultList.toArray(result);
        }

        @Override
        public void visit(PrimitiveParameter primitiveParameter)
                throws KnowledgeSourceReadException {
            List<String> resultList = new ArrayList<String>();

            if (outputConfig.showValue()) {
                resultList.add(primitiveParameter.getValueFormatted());
            }
            displayNames(primitiveParameter, resultList);
            if (outputConfig.showStartOrTimestamp()) {
                resultList.add(primitiveParameter.getStartFormattedShort());
            }
            if (outputConfig.showFinish()) {
                resultList.add(primitiveParameter.getFinishFormattedShort());
            }
            if (outputConfig.showLength()) {
                resultList.add(primitiveParameter.getLengthFormattedShort());
            }
            processProperties(primitiveParameter, resultList);

            this.result = new String[resultList.size()];
            resultList.toArray(result);
        }

        @Override
        public void visit(Constant constantParameter)
                throws KnowledgeSourceReadException {
            List<String> resultList = new ArrayList<String>();

            if (outputConfig.showValue()) {
                resultList.add(null);
            }
            displayNames(constantParameter, resultList);
            if (outputConfig.showStartOrTimestamp()) {
                resultList.add(null);
            }
            if (outputConfig.showFinish()) {
                resultList.add(null);
            }
            if (outputConfig.showLength()) {
                resultList.add(null);
            }
            processProperties(constantParameter, resultList);

            this.result = new String[resultList.size()];
            resultList.toArray(result);
        }

        @Override
        public void visit(Context context) {
            throw new UnsupportedOperationException(
                    "Contexts not supported yet");
        }

        String[] getResult() {
            return this.result;
        }

        private void displayNames(Proposition proposition,
                List<String> resultList) throws KnowledgeSourceReadException {
            PropositionDefinition propositionDefinition = knowledgeSource
                    .readPropositionDefinition(proposition.getId());
            if (outputConfig.showDisplayName()) {
                resultList.add(propositionDefinition.getDisplayName());
            }
            if (outputConfig.showAbbrevDisplayName()) {
                resultList.add(propositionDefinition
                        .getAbbreviatedDisplayName());
            }
        }

        private void processProperties(Proposition proposition,
                List<String> resultList) {
            for (String propertyName : propertyNames) {
                Value pval = proposition.getProperty(propertyName);
                if (pval != null) {
                    resultList.add(pval.getFormatted());
                } else {
                    resultList.add(null);
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
        Collection<Proposition> propositions = this.traverseLinks(this.links,
                proposition, derivations, references, knowledgeSource);
        List<String> result = new ArrayList<String>();
        int i = 0;
        for (Proposition prop : propositions) {
            if (i < this.numInstances) {
                ValuesPropositionVisitor propositionVisitor = new ValuesPropositionVisitor(
                        knowledgeSource);
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
        while (i < this.numInstances) {
            int j = 0;
            while (j < (this.outputConfig.numActiveColumns() + this.propertyNames.length)) {
                result.add(null);
                j++;
            }
            i++;
        }
        return result.toArray(new String[result.size()]);
    }

    @Override
    public String[] columnNames(String propId, KnowledgeSource knowledgeSource)
            throws KnowledgeSourceReadException {
        PropositionDefinition propDef = knowledgeSource
                .readPropositionDefinition(propId);
        String headerString = this.columnNamePrefixOverride != null ? this.columnNamePrefixOverride
                : generateLinksHeaderString(this.links);
        String[] one = columnNames(headerString, propDef);
        String[] result = new String[one.length * this.numInstances];
        for (int i = 0; i < result.length; i++) {
            result[i] = one[i % one.length];
        }
        return result;
    }
}