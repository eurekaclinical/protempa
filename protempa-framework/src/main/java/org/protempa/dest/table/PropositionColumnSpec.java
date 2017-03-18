/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2013 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.protempa.dest.table;

import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.apache.commons.lang3.ArrayUtils;

import org.apache.commons.lang3.StringUtils;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceCache;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.PropertyDefinition;
import org.protempa.PropositionDefinition;
import org.protempa.ProtempaException;
import org.protempa.ProtempaUtil;
import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.Constant;
import org.protempa.proposition.Context;
import org.protempa.proposition.Event;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueId;
import org.protempa.proposition.value.DateValue;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.visitor.AbstractPropositionCheckedVisitor;
import org.protempa.valueset.ValueSet;

public class PropositionColumnSpec extends AbstractTableColumnSpec {

    private static final ThreadLocal<NumberFormat> numberFormat = new ThreadLocal<NumberFormat>() {
        @Override
        protected NumberFormat initialValue() {
            return NumberFormat.getInstance();
        }
    };
    private final Link[] links;
    private final String[] propertyNames;
    private final int numInstances;
    private final String columnNamePrefixOverride;
    private final OutputConfig outputConfig;
    private final ValueOutputConfig valueOutputConfig;
    private final ValuesPropositionVisitor propositionVisitor;
    
    public PropositionColumnSpec() {
        this(null);
    }

    public PropositionColumnSpec(String[] propertyNames) {
        this(propertyNames, null, null);
    }

    public PropositionColumnSpec(String columnNamePrefixOverride,
            String[] propertyNames) {
        this(columnNamePrefixOverride, propertyNames, null, null, null, 1);
    }

    public PropositionColumnSpec(String[] propertyNames,
            OutputConfig outputConfig, ValueOutputConfig valueOutputConfig) {
        this(propertyNames, outputConfig, valueOutputConfig, null);
    }

    public PropositionColumnSpec(String[] propertyNames,
            OutputConfig outputConfig, ValueOutputConfig valueOutputConfig,
            Link[] links) {
        this(propertyNames, outputConfig, valueOutputConfig, links, 1);
    }

    public PropositionColumnSpec(String[] propertyNames,
            OutputConfig outputConfig, ValueOutputConfig valueOutputConfig,
            Link[] links, int numInstances) {
        this(null, propertyNames, outputConfig, valueOutputConfig, links,
                numInstances);
    }

    public PropositionColumnSpec(String columnNamePrefixOverride,
            String[] propertyNames, OutputConfig outputConfig,
            ValueOutputConfig valueOutputConfig, Link[] links,
            int numInstances) {
        if (propertyNames == null) {
            this.propertyNames = ArrayUtils.EMPTY_STRING_ARRAY;
        } else {
            ProtempaUtil.checkArrayForNullElement(propertyNames,
                    "propertyNames");
            this.propertyNames = propertyNames.clone();
            ProtempaUtil.internAll(this.propertyNames);
        }

        if (outputConfig == null) {
            this.outputConfig = new OutputConfig();
        } else {
            this.outputConfig = outputConfig;
        }

        if (valueOutputConfig == null) {
            this.valueOutputConfig = (new ValueOutputConfigBuilder()).build();
        } else {
            this.valueOutputConfig = valueOutputConfig;
        }

        if (links == null) {
            this.links = Util.EMPTY_LINK_ARRAY;
        } else {
            ProtempaUtil.checkArrayForNullElement(links, "links");
            this.links = links.clone();
        }

        if (numInstances < 1) {
            throw new IllegalArgumentException("numInstances cannot be < 1");
        }
        this.numInstances = numInstances;
        this.columnNamePrefixOverride = columnNamePrefixOverride;
        propositionVisitor = new ValuesPropositionVisitor();
    }

    private String[] columnNames() {
        List<String> results = new ArrayList<>();
        if (this.outputConfig.showUniqueId()) {
            results.add(StringUtils.defaultIfEmpty(
                    outputConfig.getUniqueIdHeading(),
                    this.columnNamePrefixOverride + "_uniqueId"));
        }
        if (this.outputConfig.showId()) {
            results.add(StringUtils.defaultIfEmpty(
                    outputConfig.getIdHeading(),
                    this.columnNamePrefixOverride + "_id"));
        }
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
            results.add(StringUtils.defaultIfEmpty(
                    outputConfig.getPropertyHeading(heading),
                    this.columnNamePrefixOverride + "." + heading));
        }
        return results.toArray(new String[results.size()]);
    }

    private class ValuesPropositionVisitor extends AbstractPropositionCheckedVisitor {

        private KnowledgeSourceCache ksCache;
        private TabularWriter tabularWriter;

        ValuesPropositionVisitor() {
        }

        void setKnowledgeSource(KnowledgeSourceCache ksCache) {
            this.ksCache = ksCache;
        }

        void setTabularWriter(TabularWriter tabularWriter) {
            this.tabularWriter = tabularWriter;
        }

        TabularWriter getTabularWriter() {
            return tabularWriter;
        }

        @Override
        public void visit(AbstractParameter abstractParameter) throws TabularWriterException {
            Format positionFormat = outputConfig.getPositionFormat();
            if (outputConfig.showUniqueId()) {
                this.tabularWriter.writeUniqueId(abstractParameter);
            }
            if (outputConfig.showId()) {
                this.tabularWriter.writeId(abstractParameter);
            }
            if (outputConfig.showValue()) {
                this.tabularWriter.writeValue(abstractParameter);
            }
            displayNames(abstractParameter);
            if (outputConfig.showStartOrTimestamp()) {
                this.tabularWriter.writeStart(abstractParameter, positionFormat);
            }
            if (outputConfig.showFinish()) {
                this.tabularWriter.writeFinish(abstractParameter, positionFormat);
            }
            if (outputConfig.showLength()) {
                this.tabularWriter.writeLength(abstractParameter, numberFormat.get());
            }
            processProperties(abstractParameter);
        }

        @Override
        public void visit(Event event) throws TabularWriterException {
            Format positionFormat = outputConfig.getPositionFormat();
            if (outputConfig.showUniqueId()) {
                this.tabularWriter.writeUniqueId(event);
            }
            if (outputConfig.showId()) {
                this.tabularWriter.writeId(event);
            }
            if (outputConfig.showValue()) {
                this.tabularWriter.writeNull();
            }
            displayNames(event);
            if (outputConfig.showStartOrTimestamp()) {
                this.tabularWriter.writeStart(event, positionFormat);
            }
            if (outputConfig.showFinish()) {
                this.tabularWriter.writeFinish(event, positionFormat);
            }
            if (outputConfig.showLength()) {
                this.tabularWriter.writeLength(event, numberFormat.get());
            }
            processProperties(event);
        }

        @Override
        public void visit(PrimitiveParameter primitiveParameter) throws TabularWriterException {
            Format positionFormat = outputConfig.getPositionFormat();
            if (outputConfig.showUniqueId()) {
                this.tabularWriter.writeUniqueId(primitiveParameter);
            }
            if (outputConfig.showId()) {
                this.tabularWriter.writeId(primitiveParameter);
            }
            if (outputConfig.showValue()) {
                this.tabularWriter.writeValue(primitiveParameter);
            }
            displayNames(primitiveParameter);
            if (outputConfig.showStartOrTimestamp()) {
                this.tabularWriter.writeStart(primitiveParameter, positionFormat);
            }
            if (outputConfig.showFinish()) {
                this.tabularWriter.writeFinish(primitiveParameter, positionFormat);
            }
            if (outputConfig.showLength()) {
                this.tabularWriter.writeLength(primitiveParameter, numberFormat.get());
            }
            processProperties(primitiveParameter);
        }

        @Override
        public void visit(Constant constant) throws TabularWriterException {
            if (outputConfig.showUniqueId()) {
                this.tabularWriter.writeUniqueId(constant);
            }
            if (outputConfig.showId()) {
                this.tabularWriter.writeId(constant);
            }
            if (outputConfig.showValue()) {
                this.tabularWriter.writeNull();
            }
            displayNames(constant);
            if (outputConfig.showStartOrTimestamp()) {
                this.tabularWriter.writeNull();
            }
            if (outputConfig.showFinish()) {
                this.tabularWriter.writeNull();
            }
            if (outputConfig.showLength()) {
                this.tabularWriter.writeNull();
            }
            processProperties(constant);
        }

        @Override
        public void visit(Context context) {
            throw new UnsupportedOperationException(
                    "Contexts not supported yet");
        }

        void clear() {
            this.tabularWriter = null;
            this.ksCache = null;
        }

        private void displayNames(Proposition proposition) throws TabularWriterException {
            boolean showDisplayName = outputConfig.showDisplayName();
            boolean showAbbrevDisplayName = outputConfig.showAbbrevDisplayName();
            if (showDisplayName || showAbbrevDisplayName) {
                PropositionDefinition propositionDefinition
                        = ksCache.get(proposition.getId());
                if (propositionDefinition != null) {
                    if (showDisplayName) {
                        this.tabularWriter.writeString(propositionDefinition.getDisplayName());
                    }
                    if (showAbbrevDisplayName) {
                        this.tabularWriter.writeString(propositionDefinition.getAbbreviatedDisplayName());
                    }
                } else {
                    this.tabularWriter.writeNull();
                    Util.logger().log(Level.WARNING,
                            "Cannot write display name for {0} because it is not in the knowledge source", proposition.getId());
                }
            }
        }

        private String getOutputPropertyValue(Proposition proposition,
                String propertyName, Value propertyValue) {
            String outputValue = null;
            boolean showDisplayName
                    = valueOutputConfig.isShowPropertyValueDisplayName();
            boolean showAbbrevDisplayName
                    = valueOutputConfig.isShowPropertyValueAbbrevDisplayName();
            if (showDisplayName || showAbbrevDisplayName) {
                PropositionDefinition propositionDef
                        = ksCache.get(proposition.getId());
                if (propositionDef != null) {
                    PropertyDefinition propertyDef
                            = propositionDef.propertyDefinition(propertyName);
                    ValueSet valueSet
                            = ksCache.getValueSet(propertyDef.getValueSetId());
                    if (valueSet != null) {
                        if (showAbbrevDisplayName) {

                            outputValue = valueSet.abbrevDisplayName(propertyValue);
                        } else if (showDisplayName) {
                            outputValue = valueSet.displayName(propertyValue);
                        }
                    } else {
                        Util.logger().log(Level.WARNING,
                                "Cannot write value set display name because value set {0} is not in the knowledge source", propertyDef.getValueSetId());
                        outputValue = propertyValue instanceof DateValue ? propertyValue.format(outputConfig.getPositionFormat()) : propertyValue.getFormatted();
                    }
                } else {
                    Util.logger().log(Level.WARNING,
                            "Cannot write value set display name because proposition {0} is not in the knowledgeSource", proposition.getId());
                    outputValue = propertyValue instanceof DateValue ? propertyValue.format(outputConfig.getPositionFormat()) : propertyValue.getFormatted();
                }

            } else {
                outputValue = propertyValue instanceof DateValue ? propertyValue.format(outputConfig.getPositionFormat()) : propertyValue.getFormatted();
            }
            return outputValue;
        }

        private void processProperties(Proposition proposition) throws TabularWriterException {
            for (String propertyName : propertyNames) {
                Value value = proposition.getProperty(propertyName);
                if (value != null) {
                    this.tabularWriter.writeString(
                            getOutputPropertyValue(proposition,
                            propertyName, value));
                } else {
                    this.tabularWriter.writeNull();
                }
            }
        }
    }

    @Override
    public void columnValues(String key, Proposition proposition,
            Map<Proposition, List<Proposition>> forwardDerivations,
            Map<Proposition, List<Proposition>> backwardDerivations,
            Map<UniqueId, Proposition> references,
            KnowledgeSourceCache propDefCache,
            TabularWriter writer) throws TabularWriterException {
        Collection<Proposition> propositions = this.traverseLinks(this.links,
                proposition, forwardDerivations, backwardDerivations,
                references, propDefCache);
        propositionVisitor.setKnowledgeSource(propDefCache);
        propositionVisitor.setTabularWriter(writer);
        int i = 0;
        for (Proposition prop : propositions) {
            if (i < this.numInstances) {
                try {
                    prop.acceptChecked(propositionVisitor);
                } catch (ProtempaException ex) {
                    throw (TabularWriterException) ex;
                }
                i++;
            } else {
                break;
            }
        }
        propositionVisitor.clear();
    }

    @Override
    public String[] columnNames(KnowledgeSource knowledgeSource)
            throws KnowledgeSourceReadException {
        String[] one = columnNames();
        String[] result = new String[one.length * this.numInstances];
        for (int i = 0; i < result.length; i++) {
            result[i] = one[i % one.length];
        }
        return result;
    }

    @Override
    public void validate(KnowledgeSource knowledgeSource) throws
            TableColumnSpecValidationFailedException,
            KnowledgeSourceReadException {
        int i = 1;
        for (Link link : this.links) {
            try {
                link.validate(knowledgeSource);
            } catch (LinkValidationFailedException ex) {
                throw new TableColumnSpecValidationFailedException(
                        "Validation of link " + i + " failed", ex);
            }
            i++;
        }
    }

    public Link[] getLinks() {
        return links;
    }

    public String[] getPropertyNames() {
        return propertyNames;
    }

    public int getNumInstances() {
        return numInstances;
    }

    public String getColumnNamePrefixOverride() {
        return columnNamePrefixOverride;
    }

    public OutputConfig getOutputConfig() {
        return outputConfig;
    }

    public ValueOutputConfig getValueOutputConfig() {
        return valueOutputConfig;
    }

    ValuesPropositionVisitor getPropositionVisitor() {
        return propositionVisitor;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((columnNamePrefixOverride == null) ? 0 : columnNamePrefixOverride.hashCode());
        result = prime * result + Arrays.hashCode(links);
        result = prime * result + numInstances;
        result = prime * result + ((outputConfig == null) ? 0 : outputConfig.hashCode());
        result = prime * result + Arrays.hashCode(propertyNames);
        result = prime * result + ((valueOutputConfig == null) ? 0 : valueOutputConfig.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        PropositionColumnSpec other = (PropositionColumnSpec) obj;
        if (columnNamePrefixOverride == null) {
            if (other.columnNamePrefixOverride != null) {
                return false;
            }
        } else if (!columnNamePrefixOverride.equals(other.columnNamePrefixOverride)) {
            return false;
        }
        if (!Arrays.equals(links, other.links)) {
            return false;
        }
        if (numInstances != other.numInstances) {
            return false;
        }
        if (outputConfig == null) {
            if (other.outputConfig != null) {
                return false;
            }
        } else if (!outputConfig.equals(other.outputConfig)) {
            return false;
        }
        if (!Arrays.equals(propertyNames, other.propertyNames)) {
            return false;
        }
        if (valueOutputConfig == null) {
            if (other.valueOutputConfig != null) {
                return false;
            }
        } else if (!valueOutputConfig.equals(other.valueOutputConfig)) {
            return false;
        }
        return true;
    }

    @Override
    public String[] getInferredPropositionIds(KnowledgeSource knowledgeSource,
            String[] inPropIds) throws KnowledgeSourceReadException {
        Set<String> result = new HashSet<>();
        for (Link link : this.links) {
            inPropIds = link.getInferredPropositionIds(knowledgeSource,
                    inPropIds);
            org.arp.javautil.arrays.Arrays.addAll(result, inPropIds);
        }
        return result.toArray(new String[result.size()]);
    }
}
