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
    private static final Calendar CAL = Calendar.getInstance();
    private static final Calendar AGE_OVER_89_CAL = Calendar.getInstance();
    private static final Date AGE_OVER_89;
    static {
        AGE_OVER_89_CAL.add(Calendar.YEAR, -90);
        AGE_OVER_89 = AGE_OVER_89_CAL.getTime();
    }
    private String keyId;

    PropositionDeidentifierVisitor(Encryption encryption, Map<String, PropositionDefinition> propDefCache, Integer offsetInSeconds) {
        assert encryption != null : "encryption cannot be null";
        assert propDefCache != null : "propDefCache cannot be null";

        this.encryption = encryption;
        this.propDefCache = propDefCache;
        this.offsetInSeconds = offsetInSeconds;
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
            synchronized (CAL) {
                CAL.setTime(AbsoluteTimeGranularityUtil.asDate(primitiveParameter.getPosition()));
                CAL.add(Calendar.SECOND, this.offsetInSeconds);
                Date time = CAL.getTime();
                deidentifiedPrimitiveParameter.setPosition(AbsoluteTimeGranularityUtil.asPosition(time));
            }
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
            if (minStartDate != null) {
                synchronized (CAL) {
                    CAL.setTime(minStartDate);
                    CAL.add(Calendar.SECOND, this.offsetInSeconds);
                    minStartDate = CAL.getTime();
                }
            }

            Date maxStartDate = AbsoluteTimeGranularityUtil.asDate(interval.getMaxStart());
            if (maxStartDate != null) {
                synchronized (CAL) {
                    CAL.setTime(maxStartDate);
                    CAL.add(Calendar.SECOND, this.offsetInSeconds);
                    maxStartDate = CAL.getTime();
                }
            }

            Date minFinishDate = AbsoluteTimeGranularityUtil.asDate(interval.getMinFinish());
            if (minFinishDate != null) {
                synchronized (CAL) {
                    CAL.setTime(minFinishDate);
                    CAL.add(Calendar.SECOND, this.offsetInSeconds);
                    minFinishDate = CAL.getTime();
                }
            }

            Date maxFinishDate = AbsoluteTimeGranularityUtil.asDate(interval.getMaxFinish());
            if (maxFinishDate != null) {
                synchronized (CAL) {
                    CAL.setTime(maxFinishDate);
                    CAL.add(Calendar.SECOND, this.offsetInSeconds);
                    maxFinishDate = CAL.getTime();
                }
            }

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
        PropositionDefinition propDef = this.propDefCache.get(prop.getId());
        for (String name : prop.getPropertyNames()) {
            PropertyDefinition propertyDefinition = propDef.propertyDefinition(name);
            if (propertyDefinition != null) {
                Attribute hipaaIdAttr = propertyDefinition.getAttribute(DeidAttributes.IS_HIPAA_IDENTIFIER);
                Attribute hipaaIdTypeAttr = propertyDefinition.getAttribute(DeidAttributes.HIPAA_IDENTIFIER_TYPE);
                Value propertyValue = prop.getProperty(name);
                if (propertyValue == null) {
                    deidentifiedProp.setProperty(name, propertyValue);
                } else if (this.offsetInSeconds != null && propertyDefinition.getValueType() == ValueType.DATEVALUE) {
                    synchronized (CAL) {
                        CAL.setTime(((DateValue) propertyValue).getDate());
                        if (CAL.before(AGE_OVER_89_CAL)) {
                            deidentifiedProp.setProperty(name, DateValue.getInstance(AGE_OVER_89));
                        } else {
                            CAL.add(Calendar.SECOND, this.offsetInSeconds);
                            deidentifiedProp.setProperty(name, DateValue.getInstance(CAL.getTime()));
                        }
                    }
                } else if (hipaaIdTypeAttr != null && DeidAttributes.AGE.equals(hipaaIdTypeAttr.getValue())) {
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
                } else {
                    deidentifiedProp.setProperty(name, propertyValue);
                }
            }
        }
        for (String name : prop.getReferenceNames()) {
            deidentifiedProp.setReferences(name, prop.getReferences(name));
        }
        this.deidentifiedProp = deidentifiedProp;
    }

}
