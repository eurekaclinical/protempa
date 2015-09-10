package org.protempa.dest.deid;

/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2015 Emory University
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
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import org.protempa.Attribute;
import org.protempa.PropertyDefinition;
import org.protempa.PropositionDefinition;
import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.AbstractProposition;
import org.protempa.proposition.Constant;
import org.protempa.proposition.Context;
import org.protempa.proposition.Event;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.interval.AbsoluteTimeIntervalFactory;
import org.protempa.proposition.interval.Interval;
import org.protempa.proposition.value.AbsoluteTimeGranularityUtil;
import org.protempa.proposition.value.BooleanValue;
import org.protempa.proposition.value.DateValue;
import org.protempa.proposition.value.InequalityNumberValue;
import org.protempa.proposition.value.NominalValue;
import org.protempa.proposition.value.NumberValue;
import org.protempa.proposition.value.NumericalValue;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueComparator;
import org.protempa.proposition.value.ValueType;
import org.protempa.proposition.visitor.AbstractPropositionVisitor;

/**
 *
 * @author Andrew Post
 */
class PropositionDeidentifierVisitor extends AbstractPropositionVisitor {

    private static final AbsoluteTimeIntervalFactory INTERVAL_FACTORY = new AbsoluteTimeIntervalFactory();

    private AbstractProposition deidentifiedProp;
    private final Encryption encryption;
    private final Integer offsetInSeconds;
    private final Map<String, PropositionDefinition> propDefCache;
    private final Calendar cal;
    private String keyId;

    PropositionDeidentifierVisitor(Encryption encryption, Map<String, PropositionDefinition> propDefCache, Integer offsetInSeconds) {
        assert encryption != null : "encryption cannot be null";
        assert propDefCache != null : "propDefCache cannot be null";

        this.encryption = encryption;
        this.propDefCache = propDefCache;
        this.offsetInSeconds = offsetInSeconds;
        this.cal = Calendar.getInstance();
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    @Override
    public void visit(Context context) {
        Context deidentifiedContext = new Context(context.getId(), context.getUniqueId());
        deidentifiedContext.setInterval(doOffsetInterval(context.getInterval()));
        visitCommon(context, deidentifiedContext);
    }

    @Override
    public void visit(Constant constant) {
        Constant deidentifiedConstant = new Constant(constant.getId(), constant.getUniqueId());
        visitCommon(constant, deidentifiedConstant);
    }

    @Override
    public void visit(PrimitiveParameter primitiveParameter) {
        PrimitiveParameter deidentifiedPrimitiveParameter = new PrimitiveParameter(primitiveParameter.getId(), primitiveParameter.getUniqueId());
        deidentifiedPrimitiveParameter.setGranularity(primitiveParameter.getGranularity());
        deidentifiedPrimitiveParameter.setValue(primitiveParameter.getValue());
        if (this.offsetInSeconds != null) {
            this.cal.setTime(AbsoluteTimeGranularityUtil.asDate(primitiveParameter.getPosition()));
            this.cal.add(Calendar.SECOND, this.offsetInSeconds);
            Date time = this.cal.getTime();
            deidentifiedPrimitiveParameter.setPosition(AbsoluteTimeGranularityUtil.asPosition(time));
        } else {
            deidentifiedPrimitiveParameter.setPosition(primitiveParameter.getPosition());
        }
        visitCommon(primitiveParameter, deidentifiedPrimitiveParameter);
    }

    @Override
    public void visit(Event event) {
        Event deidentifiedEvent = new Event(event.getId(), event.getUniqueId());
        deidentifiedEvent.setInterval(doOffsetInterval(event.getInterval()));
        visitCommon(event, deidentifiedEvent);
    }

    @Override
    public void visit(AbstractParameter abstractParameter) {
        AbstractParameter deidentifiedAbstractParameter = new AbstractParameter(abstractParameter.getId(), abstractParameter.getUniqueId());
        deidentifiedAbstractParameter.setContextId(abstractParameter.getContextId());
        deidentifiedAbstractParameter.setValue(abstractParameter.getValue());
        deidentifiedAbstractParameter.setInterval(doOffsetInterval(abstractParameter.getInterval()));
        visitCommon(abstractParameter, deidentifiedAbstractParameter);
    }

    private Interval doOffsetInterval(Interval interval) {
        if (this.offsetInSeconds != null) {
            Date minStartDate = AbsoluteTimeGranularityUtil.asDate(interval.getMinStart());
            this.cal.setTime(minStartDate);
            this.cal.add(Calendar.SECOND, this.offsetInSeconds);
            minStartDate = this.cal.getTime();

            Date maxStartDate = AbsoluteTimeGranularityUtil.asDate(interval.getMaxStart());
            this.cal.setTime(maxStartDate);
            this.cal.add(Calendar.SECOND, this.offsetInSeconds);
            maxStartDate = this.cal.getTime();

            Date minFinishDate = AbsoluteTimeGranularityUtil.asDate(interval.getMinFinish());
            this.cal.setTime(minFinishDate);
            this.cal.add(Calendar.SECOND, this.offsetInSeconds);
            minFinishDate = this.cal.getTime();

            Date maxFinishDate = AbsoluteTimeGranularityUtil.asDate(interval.getMaxFinish());
            this.cal.setTime(maxFinishDate);
            this.cal.add(Calendar.SECOND, this.offsetInSeconds);
            maxFinishDate = this.cal.getTime();

            return INTERVAL_FACTORY.getInstance(minStartDate, maxStartDate, interval.getStartGranularity(), minFinishDate, maxFinishDate, interval.getFinishGranularity());
        } else {
            return interval;
        }
    }

    public Proposition getProposition() {
        return this.deidentifiedProp;
    }

    private void visitCommon(AbstractProposition prop, AbstractProposition deidentifiedProp) {
        deidentifiedProp.setCreateDate(prop.getCreateDate());
        deidentifiedProp.setDownloadDate(prop.getDownloadDate());
        deidentifiedProp.setUpdateDate(prop.getUpdateDate());
        deidentifiedProp.setSourceSystem(prop.getSourceSystem());
        for (String name : prop.getPropertyNames()) {
            PropositionDefinition propDef = this.propDefCache.get(prop.getId());
            PropertyDefinition propertyDefinition = propDef.propertyDefinition(name);
            if (propertyDefinition != null) {
                Attribute hipaaIdAttr = propertyDefinition.getAttribute(DeidAttributes.IS_HIPAA_IDENTIFIER);
                Attribute hipaaIdTypeAttr = propertyDefinition.getAttribute(DeidAttributes.HIPAA_IDENTIFIER_TYPE);
                Value propertyValue = prop.getProperty(name);
                if (this.offsetInSeconds != null && propertyDefinition.getValueType() == ValueType.DATEVALUE) {
                    this.cal.setTime(((DateValue) propertyValue).getDate());
                    this.cal.add(Calendar.SECOND, this.offsetInSeconds);
                    deidentifiedProp.setProperty(name, DateValue.getInstance(this.cal.getTime()));
                } else if (hipaaIdTypeAttr != null && DeidAttributes.AGE_IN_YEARS.equals(hipaaIdTypeAttr.getValue())) {
                    NumericalValue numericalValue = (NumericalValue) propertyValue;
                    if (numericalValue.compare(NumberValue.getInstance(90)) == ValueComparator.GREATER_THAN_OR_EQUAL_TO) {
                        deidentifiedProp.setProperty(name, new InequalityNumberValue(ValueComparator.GREATER_THAN_OR_EQUAL_TO, 90));
                    } else {
                        deidentifiedProp.setProperty(name, propertyValue);
                    }
                } else if (hipaaIdAttr != null && BooleanValue.TRUE.equals(hipaaIdAttr.getValue())) {
                    try {
                        deidentifiedProp.setProperty(name, NominalValue.getInstance(this.encryption.encrypt(this.keyId, propertyValue.getFormatted())));
                    } catch (EncryptException ex) {
                        throw new AssertionError(ex);
                    }
                }
            }
        }
        for (String name : prop.getReferenceNames()) {
            deidentifiedProp.setReferences(name, prop.getReferences(name));
        }
        this.deidentifiedProp = deidentifiedProp;
    }

}
