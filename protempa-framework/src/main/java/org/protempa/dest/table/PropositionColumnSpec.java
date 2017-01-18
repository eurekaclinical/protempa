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

import java.io.IOException;
import java.io.Writer;
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
import org.arp.javautil.string.StringUtil;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceCache;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.PropertyDefinition;
import org.protempa.PropositionDefinition;
import org.protempa.ProtempaUtil;
import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.Constant;
import org.protempa.proposition.Context;
import org.protempa.proposition.Event;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueId;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.visitor.AbstractPropositionVisitor;
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

    private class ValuesPropositionVisitor extends AbstractPropositionVisitor {

        private KnowledgeSourceCache ksCache;
        private String[] result;
        private int i = 0;

        ValuesPropositionVisitor() {
        }

        void setKnowledgeSource(KnowledgeSourceCache ksCache) {
            this.ksCache = ksCache;
        }

//        KnowledgeSource getKnowledgeSource() {
//            return this.knowledgeSource;
//        }
        void setResult(String[] result) {
            this.result = result;
        }

        @Override
        public void visit(AbstractParameter abstractParameter) {
            Format positionFormat = outputConfig.getPositionFormat();
            if (outputConfig.showId()) {
                result[i++] = abstractParameter.getId();
            }
            if (outputConfig.showValue()) {
                result[i++] = abstractParameter.getValueFormatted();
            }
            displayNames(abstractParameter);
            if (outputConfig.showStartOrTimestamp()) {
                result[i++] = positionFormat != null ? abstractParameter.formatStart(positionFormat) : abstractParameter.getStartFormattedShort();
            }
            if (outputConfig.showFinish()) {
                result[i++] = positionFormat != null ? abstractParameter.formatFinish(positionFormat) : abstractParameter.getFinishFormattedShort();
            }
            if (outputConfig.showLength()) {
                //result[i++] = abstractParameter.getLengthFormattedShort();
                /*
                 * This is a hack until we have an API in PROTEMPA to get a 
                 * length string without units.
                 */
                result[i++] = numberFormat.get().format(
                        abstractParameter.getInterval().getMinLength());
            }
            processProperties(abstractParameter);
        }

        @Override
        public void visit(Event event) {
            Format positionFormat = outputConfig.getPositionFormat();
            if (outputConfig.showId()) {
                result[i++] = event.getId();
            }
            if (outputConfig.showValue()) {
                result[i++] = null;
            }
            displayNames(event);
            if (outputConfig.showStartOrTimestamp()) {
                result[i++] = positionFormat != null ? event.formatStart(positionFormat) : event.getStartFormattedShort();
            }
            if (outputConfig.showFinish()) {
                result[i++] = positionFormat != null ? event.formatFinish(positionFormat) : event.getFinishFormattedShort();
            }
            if (outputConfig.showLength()) {
                //result[i++] = event.getLengthFormattedShort();
                /*
                 * This is a hack until we have an API in PROTEMPA to get a 
                 * length string without units.
                 */
                Long minLength = event.getInterval().getMinLength();
                if (minLength != null) {
                    result[i++] = numberFormat.get().format(minLength);
                } else {
                    result[i++] = "";
                }
            }
            processProperties(event);
        }

        @Override
        public void visit(PrimitiveParameter primitiveParameter) {
            Format positionFormat = outputConfig.getPositionFormat();
            if (outputConfig.showId()) {
                result[i++] = primitiveParameter.getId();
            }
            if (outputConfig.showValue()) {
                result[i++] = primitiveParameter.getValueFormatted();
            }
            displayNames(primitiveParameter);
            if (outputConfig.showStartOrTimestamp()) {
                result[i++] = positionFormat != null ? primitiveParameter.formatStart(positionFormat) : primitiveParameter.getStartFormattedShort();
            }
            if (outputConfig.showFinish()) {
                result[i++] = positionFormat != null ? primitiveParameter.formatFinish(positionFormat) : primitiveParameter.getFinishFormattedShort();
            }
            if (outputConfig.showLength()) {
                //result[i++] = primitiveParameter.getLengthFormattedShort();
                /*
                 * This is a hack until we have an API in PROTEMPA to get a 
                 * length string without units.
                 */
                Long minLength =
                        primitiveParameter.getInterval().getMinLength();
                if (minLength != null) {
                    result[i++] = numberFormat.get().format(minLength);
                } else {
                    result[i++] = "";
                }
            }
            processProperties(primitiveParameter);
        }

        @Override
        public void visit(Constant constant) {
            if (outputConfig.showId()) {
                result[i++] = constant.getId();
            }
            if (outputConfig.showValue()) {
                result[i++] = null;
            }
            displayNames(constant);
            if (outputConfig.showStartOrTimestamp()) {
                result[i++] = null;
            }
            if (outputConfig.showFinish()) {
                result[i++] = null;
            }
            if (outputConfig.showLength()) {
                result[i++] = null;
            }
            processProperties(constant);
        }

        @Override
        public void visit(Context context) {
            throw new UnsupportedOperationException(
                    "Contexts not supported yet");
        }

        void clear() {
            this.result = null;
            this.ksCache = null;
            this.i = 0;
        }

        private void displayNames(Proposition proposition) {
            boolean showDisplayName = outputConfig.showDisplayName();
            boolean showAbbrevDisplayName = outputConfig.showAbbrevDisplayName();
            if (showDisplayName || showAbbrevDisplayName) {
                PropositionDefinition propositionDefinition =
                        ksCache.get(proposition.getId());
                if (propositionDefinition != null) {
                    if (showDisplayName) {
                        result[i++] = propositionDefinition.getDisplayName();
                    }
                    if (showAbbrevDisplayName) {
                        result[i++] =
                                propositionDefinition.getAbbreviatedDisplayName();
                    }
                } else {
                    result[i++] = null;
                    Util.logger().log(Level.WARNING,
                            "Cannot write display name for {0} because it is not in the knowledge source", proposition.getId());
                }
            }
        }

        private String getOutputPropertyValue(Proposition proposition,
                String propertyName, Value propertyValue) {
            String outputValue = null;
            boolean showDisplayName =
                    valueOutputConfig.isShowPropertyValueDisplayName();
            boolean showAbbrevDisplayName =
                    valueOutputConfig.isShowPropertyValueAbbrevDisplayName();
            if (showDisplayName || showAbbrevDisplayName) {
                    PropositionDefinition propositionDef =
                            ksCache.get(proposition.getId());
                    if (propositionDef != null) {
                        PropertyDefinition propertyDef =
                                propositionDef.propertyDefinition(propertyName);
                        ValueSet valueSet =
                                ksCache.getValueSet(propertyDef.getValueSetId());
                        if (valueSet != null) {
                            if (showAbbrevDisplayName) {

                                outputValue = valueSet.abbrevDisplayName(propertyValue);
                            } else if (showDisplayName) {
                                outputValue = valueSet.displayName(propertyValue);
                            }
                        } else {
                            Util.logger().log(Level.WARNING,
                                    "Cannot write value set display name because value set {0} is not in the knowledge source", propertyDef.getValueSetId());
                            outputValue = propertyValue.getFormatted();
                        }
                    } else {
                        Util.logger().log(Level.WARNING,
                                "Cannot write value set display name because proposition {0} is not in the knowledgeSource", proposition.getId());
                        outputValue = propertyValue.getFormatted();
                    }

            } else {
                outputValue = propertyValue.getFormatted();
            }
            return outputValue;
        }

        private void processProperties(Proposition proposition) {
            for (String propertyName : propertyNames) {
                Value value = proposition.getProperty(propertyName);
                if (value != null) {
                    result[i++] = getOutputPropertyValue(proposition,
                            propertyName, value);
                } else {
                    result[i++] = null;
                }
            }
        }
    }

    @Override
    public void columnValues(String key, Proposition proposition,
            Map<Proposition, List<Proposition>> forwardDerivations,
            Map<Proposition, List<Proposition>> backwardDerivations,
            Map<UniqueId, Proposition> references,
            KnowledgeSourceCache propDefCache, Map<String, String> replace,
            char delimiter, Writer writer) throws IOException {
        Collection<Proposition> propositions = this.traverseLinks(this.links,
                proposition, forwardDerivations, backwardDerivations,
                references, propDefCache);
        propositionVisitor.setKnowledgeSource(propDefCache);
        String[] result = new String[(this.outputConfig.numActiveColumns()
                + this.propertyNames.length) * this.numInstances];
        propositionVisitor.setResult(result);
        int i = 0;
        for (Proposition prop : propositions) {
            if (i < this.numInstances) {
                prop.accept(propositionVisitor);
                i++;
            } else {
                break;
            }
        }
        propositionVisitor.clear();
        
        StringUtil.escapeAndWriteDelimitedColumns(result, replace, delimiter, writer);
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

    public ValuesPropositionVisitor getPropositionVisitor() {
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
