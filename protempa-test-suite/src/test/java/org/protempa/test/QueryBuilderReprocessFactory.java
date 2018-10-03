package org.protempa.test;

/*-
 * #%L
 * Protempa Test Suite
 * %%
 * Copyright (C) 2012 - 2018 Emory University
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
import org.protempa.CompoundLowLevelAbstractionDefinition;
import org.protempa.ContextDefinition;
import org.protempa.EventDefinition;
import org.protempa.ExtendedPropositionDefinition;
import org.protempa.HighLevelAbstractionDefinition;
import org.protempa.LowLevelAbstractionDefinition;
import org.protempa.LowLevelAbstractionValueDefinition;
import org.protempa.PrimitiveParameterDefinition;
import org.protempa.PropositionDefinition;
import org.protempa.SimpleGapFunction;
import org.protempa.SlidingWindowWidthMode;
import org.protempa.TemporalExtendedParameterDefinition;
import org.protempa.TemporalExtendedPropositionDefinition;
import org.protempa.ValueClassification;
import org.protempa.proposition.interval.Relation;
import org.protempa.proposition.value.AbsoluteTimeUnit;
import org.protempa.proposition.value.NominalValue;
import org.protempa.proposition.value.NumberValue;
import org.protempa.proposition.value.ValueComparator;
import org.protempa.query.DefaultQueryBuilder;

/**
 *
 * @author Andrew Post
 */
class QueryBuilderReprocessFactory {

    private static final String ICD9_013_82 = "ICD9:013.82";
    private static final String ICD9_804 = "ICD9:804";

    /**
     * All proposition IDs in the sample data
     */
    private static final String[] RETRIEVE_PROP_IDS = {"Patient", "PatientAll",
        "Encounter", ICD9_013_82, ICD9_804, "VitalSign",
        "HELLP_FIRST_RECOVERING_PLATELETS", "LDH_TREND", "AST_STATE",
        "30DayReadmission", "No30DayReadmission", "MyDiagnosis",
        "MyVitalSign", "MyTemporalPattern", "MyAndLikePattern",
        "DiastolicBloodPressure", "SystolicBloodPressure",
        "MySystolicClassification", "MyDiastolicClassification",
        "MyBloodPressureClassificationAny",
        "MyBloodPressureClassificationConsecutiveAny",
        "MyBloodPressureClassificationAll",
        "MyTwoConsecutiveHighBloodPressure", "MySystolicClassification3",
        "MyDiastolicClassification3", "MyBloodPressureClassification3Any",
        "MyContext1", "MySystolicClassificationMyContext1"
    };

    private static final String[] UPDATE_PROP_IDS = {"MyBloodPressureClassificationAll"
    };

    private static final String[] CREATE_PROP_IDS = {
        "MyBloodPressureClassificationAll2"
    };

    private static final String[] DELETE_PROP_IDS = {
        "MyBloodPressureClassification3Any"
    };

    DefaultQueryBuilder getInstanceRetrieve() {
        DefaultQueryBuilder q = new DefaultQueryBuilder();
        q.setPropositionIds(RETRIEVE_PROP_IDS);
        EventDefinition ed = new EventDefinition("MyDiagnosis");
        ed.setDisplayName("My Diagnosis");
        ed.setInverseIsA("ICD9:907.1");
        PrimitiveParameterDefinition pd = new PrimitiveParameterDefinition(
                "MyVitalSign");
        pd.setDisplayName("My Vital Sign");
        pd.setInverseIsA("HeartRate");
        HighLevelAbstractionDefinition hd = new HighLevelAbstractionDefinition(
                "MyTemporalPattern");
        hd.setDisplayName("My Temporal Pattern");
        TemporalExtendedPropositionDefinition td1 = new TemporalExtendedPropositionDefinition(
                ed.getId());
        TemporalExtendedPropositionDefinition td2 = new TemporalExtendedPropositionDefinition(
                pd.getId());
        hd.add(td1);
        hd.add(td2);
        Relation rel = new Relation();
        hd.setRelation(td1, td2, rel);
        HighLevelAbstractionDefinition hd2 = new HighLevelAbstractionDefinition(
                "MyAndLikePattern");
        hd.setDisplayName("My Or-like Pattern");
        ExtendedPropositionDefinition epd1 = new ExtendedPropositionDefinition(
                "ICD9:V-codes");
        ExtendedPropositionDefinition epd2 = new ExtendedPropositionDefinition(
                "ICD9:35.83");
        hd2.add(epd1);
        hd2.add(epd2);
        HighLevelAbstractionDefinition highBp = new HighLevelAbstractionDefinition(
                "MyTwoConsecutiveHighBloodPressure");
        highBp.setDisplayName("My Two Consecutive High Blood Pressure");
        TemporalExtendedParameterDefinition highBpTpd = new TemporalExtendedParameterDefinition(
                "MyBloodPressureClassificationConsecutiveAny");
        highBpTpd.setValue(NominalValue.getInstance("MYBP_HIGH"));
        highBp.add(highBpTpd);
        Relation highBpRel = new Relation();
        highBp.setRelation(highBpTpd, highBpTpd, highBpRel);
        q.setPropositionDefinitions(new PropositionDefinition[]{ed, pd, hd,
            hd2, systolicClassification(), diastolicClassification(),
            bloodPressureClassificationAny(),
            bloodPressureClassificationConsecutiveAny(), highBp,
            systolicClassification3(),
            diastolicClassification3(), bloodPressureClassification3Any(),
            context1(), systolicClassificationMyContext1(),
            bloodPressureClassificationAll()});
        return q;
    }

    DefaultQueryBuilder getInstanceUpdate() {
        DefaultQueryBuilder q = new DefaultQueryBuilder();
        q.setPropositionIds(UPDATE_PROP_IDS);
        q.setPropositionDefinitions(new PropositionDefinition[]{
            systolicClassification(), diastolicClassification(),
            bloodPressureClassificationAllUpdated()
        });
        return q;
    }

    DefaultQueryBuilder getInstanceCreate() {
        DefaultQueryBuilder q = new DefaultQueryBuilder();
        q.setPropositionIds(CREATE_PROP_IDS);
        q.setPropositionDefinitions(new PropositionDefinition[]{
            systolicClassification(), diastolicClassification(),
            bloodPressureClassificationAll2()
        });
        return q;
    }

    DefaultQueryBuilder getInstanceDelete() {
        DefaultQueryBuilder q = new DefaultQueryBuilder();
        q.setPropositionIds(DELETE_PROP_IDS);
        q.setPropositionDefinitions(new PropositionDefinition[]{
            systolicClassification3(),
            diastolicClassification3(), 
            bloodPressureClassification3Any(),
        });
        return q;
    }

    private ContextDefinition context1() {
        ContextDefinition cd = new ContextDefinition("MyContext1");
        TemporalExtendedParameterDefinition tepd
                = new TemporalExtendedParameterDefinition("MyDiastolicClassification");
        tepd.setValue(NominalValue.getInstance("My Diastolic High"));
        cd.setInducedBy(new TemporalExtendedPropositionDefinition[]{tepd});
        return cd;
    }

    private PropositionDefinition systolicClassificationMyContext1() {
        LowLevelAbstractionDefinition systolic
                = new LowLevelAbstractionDefinition(
                        "MySystolicClassificationMyContext1");
        systolic.setPropositionId("MySystolicClassification");
        systolic.setContextId("MyContext1");
        systolic.setDisplayName("My Systolic Classification");
        systolic.setAlgorithmId("stateDetector");
        systolic.addPrimitiveParameterId("SystolicBloodPressure");
        systolic.setSlidingWindowWidthMode(SlidingWindowWidthMode.DEFAULT);
        systolic.setGapFunction(new SimpleGapFunction(168,
                AbsoluteTimeUnit.HOUR));
        systolic.setMaximumGapBetweenValues(24);
        systolic.setMaximumGapBetweenValuesUnits(AbsoluteTimeUnit.HOUR);

        LowLevelAbstractionValueDefinition sysHigh = new LowLevelAbstractionValueDefinition(
                systolic, "MY_SYSTOLIC_HIGH");
        sysHigh.setValue(NominalValue.getInstance("My Systolic High"));
        sysHigh.setParameterValue("minThreshold", NumberValue.getInstance(130));
        sysHigh.setParameterComp("minThreshold",
                ValueComparator.GREATER_THAN_OR_EQUAL_TO);

        LowLevelAbstractionValueDefinition sysNormal = new LowLevelAbstractionValueDefinition(
                systolic, "MY_SYSTOLIC_NORMAL");
        sysNormal.setValue(NominalValue.getInstance("My Systolic Normal"));
        sysNormal.setParameterValue("maxThreshold",
                NumberValue.getInstance(130));
        sysNormal.setParameterComp("maxThreshold", ValueComparator.LESS_THAN);

        return systolic;
    }

    private PropositionDefinition systolicClassification() {
        LowLevelAbstractionDefinition systolic = new LowLevelAbstractionDefinition(
                "MySystolicClassification");
        systolic.setDisplayName("My Systolic Classification");
        systolic.setAlgorithmId("stateDetector");
        systolic.addPrimitiveParameterId("SystolicBloodPressure");
        systolic.setSlidingWindowWidthMode(SlidingWindowWidthMode.DEFAULT);
        systolic.setGapFunction(new SimpleGapFunction(168,
                AbsoluteTimeUnit.HOUR));
        systolic.setMaximumGapBetweenValues(24);
        systolic.setMaximumGapBetweenValuesUnits(AbsoluteTimeUnit.HOUR);

        LowLevelAbstractionValueDefinition sysHigh = new LowLevelAbstractionValueDefinition(
                systolic, "MY_SYSTOLIC_HIGH");
        sysHigh.setValue(NominalValue.getInstance("My Systolic High"));
        sysHigh.setParameterValue("minThreshold", NumberValue.getInstance(140));
        sysHigh.setParameterComp("minThreshold",
                ValueComparator.GREATER_THAN_OR_EQUAL_TO);

        LowLevelAbstractionValueDefinition sysNormal = new LowLevelAbstractionValueDefinition(
                systolic, "MY_SYSTOLIC_NORMAL");
        sysNormal.setValue(NominalValue.getInstance("My Systolic Normal"));
        sysNormal.setParameterValue("maxThreshold",
                NumberValue.getInstance(140));
        sysNormal.setParameterComp("maxThreshold", ValueComparator.LESS_THAN);

        return systolic;
    }

    private PropositionDefinition systolicClassification3() {
        LowLevelAbstractionDefinition systolic = new LowLevelAbstractionDefinition(
                "MySystolicClassification3");

        systolic.setDisplayName("My Systolic Classification 3");
        systolic.setAlgorithmId("stateDetector");
        systolic.addPrimitiveParameterId("SystolicBloodPressure");
        systolic.setSlidingWindowWidthMode(SlidingWindowWidthMode.DEFAULT);
        systolic.setGapFunction(new SimpleGapFunction(168,
                AbsoluteTimeUnit.HOUR));
        systolic.setMaximumGapBetweenValues(24);
        systolic.setMaximumGapBetweenValuesUnits(AbsoluteTimeUnit.HOUR);

        LowLevelAbstractionValueDefinition sysHigh = new LowLevelAbstractionValueDefinition(
                systolic, "MY_SYSTOLIC_HIGH_3");
        sysHigh.setValue(NominalValue.getInstance("My Systolic High 3"));
        sysHigh.setParameterValue("minThreshold", NumberValue.getInstance(140));
        sysHigh.setParameterComp("minThreshold",
                ValueComparator.GREATER_THAN_OR_EQUAL_TO);

        LowLevelAbstractionValueDefinition sysLow = new LowLevelAbstractionValueDefinition(
                systolic, "MY_SYSTOLIC_LOW_3");
        sysLow.setValue(NominalValue.getInstance("My Systolic Low 3"));
        sysLow.setParameterValue("maxThreshold", NumberValue.getInstance(60));
        sysLow.setParameterComp("maxThreshold",
                ValueComparator.LESS_THAN_OR_EQUAL_TO);

        LowLevelAbstractionValueDefinition sysNormal = new LowLevelAbstractionValueDefinition(
                systolic, "MY_SYSTOLIC_NORMAL_3");
        sysNormal.setValue(NominalValue.getInstance("My Systolic Normal 3"));
        sysNormal.setParameterValue("maxThreshold",
                NumberValue.getInstance(140));
        sysNormal.setParameterComp("maxThreshold", ValueComparator.LESS_THAN);
        sysNormal
                .setParameterValue("minThreshold", NumberValue.getInstance(60));
        sysNormal
                .setParameterComp("minThreshold", ValueComparator.GREATER_THAN);

        return systolic;
    }

    private LowLevelAbstractionDefinition diastolicClassification() {
        LowLevelAbstractionDefinition diastolic = new LowLevelAbstractionDefinition(
                "MyDiastolicClassification");
        diastolic.setDisplayName("My Diastolic Classification");
        diastolic.setAlgorithmId("stateDetector");
        diastolic.addPrimitiveParameterId("DiastolicBloodPressure");
        diastolic.setSlidingWindowWidthMode(SlidingWindowWidthMode.DEFAULT);
        diastolic.setGapFunction(new SimpleGapFunction(168,
                AbsoluteTimeUnit.HOUR));
        diastolic.setMaximumGapBetweenValues(24);
        diastolic.setMaximumGapBetweenValuesUnits(AbsoluteTimeUnit.HOUR);

        LowLevelAbstractionValueDefinition diasHigh = new LowLevelAbstractionValueDefinition(
                diastolic, "MY_DIASTOLIC_HIGH");
        diasHigh.setValue(NominalValue.getInstance("My Diastolic High"));
        diasHigh.setParameterValue("minThreshold", NumberValue.getInstance(90));
        diasHigh.setParameterComp("minThreshold",
                ValueComparator.GREATER_THAN_OR_EQUAL_TO);

        LowLevelAbstractionValueDefinition diasNormal = new LowLevelAbstractionValueDefinition(
                diastolic, "MY_DIASTOLIC_NORMAL");
        diasNormal.setValue(NominalValue.getInstance("My Diastolic Normal"));
        diasNormal.setParameterValue("maxThreshold",
                NumberValue.getInstance(90));
        diasNormal.setParameterComp("maxThreshold", ValueComparator.LESS_THAN);

        return diastolic;
    }

    private PropositionDefinition diastolicClassification3() {
        LowLevelAbstractionDefinition diastolic = new LowLevelAbstractionDefinition(
                "MyDiastolicClassification3");

        diastolic.setDisplayName("My Diastolic Classification 3");
        diastolic.setAlgorithmId("stateDetector");
        diastolic.addPrimitiveParameterId("DiastolicBloodPressure");
        diastolic.setSlidingWindowWidthMode(SlidingWindowWidthMode.DEFAULT);
        diastolic.setGapFunction(new SimpleGapFunction(24,
                AbsoluteTimeUnit.HOUR));
        diastolic.setMaximumGapBetweenValues(24);
        diastolic.setMaximumGapBetweenValuesUnits(AbsoluteTimeUnit.HOUR);

        LowLevelAbstractionValueDefinition sysHigh = new LowLevelAbstractionValueDefinition(
                diastolic, "MY_DIASTOLIC_HIGH_3");
        sysHigh.setValue(NominalValue.getInstance("My Diastolic High 3"));
        sysHigh.setParameterValue("minThreshold", NumberValue.getInstance(90));
        sysHigh.setParameterComp("minThreshold",
                ValueComparator.GREATER_THAN_OR_EQUAL_TO);

        LowLevelAbstractionValueDefinition sysLow = new LowLevelAbstractionValueDefinition(
                diastolic, "MY_DIASTOLIC_LOW_3");
        sysLow.setValue(NominalValue.getInstance("My Diastolic Low 3"));
        sysLow.setParameterValue("maxThreshold", NumberValue.getInstance(30));
        sysLow.setParameterComp("maxThreshold",
                ValueComparator.LESS_THAN_OR_EQUAL_TO);

        LowLevelAbstractionValueDefinition sysNormal = new LowLevelAbstractionValueDefinition(
                diastolic, "MY_DIASTOLIC_NORMAL_3");
        sysNormal.setValue(NominalValue.getInstance("My Diastolic Normal 3"));
        sysNormal
                .setParameterValue("maxThreshold", NumberValue.getInstance(90));
        sysNormal.setParameterComp("maxThreshold", ValueComparator.LESS_THAN);
        sysNormal
                .setParameterValue("minThreshold", NumberValue.getInstance(30));
        sysNormal
                .setParameterComp("minThreshold", ValueComparator.GREATER_THAN);

        return diastolic;
    }

    private PropositionDefinition bloodPressureClassificationConsecutiveAny() {
        CompoundLowLevelAbstractionDefinition bp = new CompoundLowLevelAbstractionDefinition(
                "MyBloodPressureClassificationConsecutiveAny");
        bp.setDisplayName("My Blood Pressure Classification (ANY - 2)");
        bp.addValueClassification(new ValueClassification("MYBP_HIGH", "MySystolicClassification",
                "My Systolic High"));
        bp.addValueClassification(new ValueClassification("MYBP_HIGH", "MyDiastolicClassification",
                "My Diastolic High"));
        bp.addValueClassification(new ValueClassification("MYBP_NORMAL", "MySystolicClassification",
                "My Systolic Normal"));
        bp.addValueClassification(new ValueClassification("MYBP_NORMAL", "MyDiastolicClassification",
                "My Diastolic Normal"));
        bp.setValueDefinitionMatchOperator(CompoundLowLevelAbstractionDefinition.ValueDefinitionMatchOperator.ANY);
        bp.setMinimumNumberOfValues(2);
        bp.setGapFunctionBetweenValues(new SimpleGapFunction(90, AbsoluteTimeUnit.DAY));
        bp.setGapFunction(new SimpleGapFunction(0, null));

        return bp;
    }

    private PropositionDefinition bloodPressureClassificationAny() {
        CompoundLowLevelAbstractionDefinition bp = new CompoundLowLevelAbstractionDefinition(
                "MyBloodPressureClassificationAny");
        bp.setDisplayName("My Blood Pressure Classification (ANY)");
        bp.addValueClassification(new ValueClassification("MYBP_HIGH", "MySystolicClassification",
                "My Systolic High"));
        bp.addValueClassification(new ValueClassification("MYBP_HIGH", "MyDiastolicClassification",
                "My Diastolic High"));
        bp.addValueClassification(new ValueClassification("MYBP_NORMAL", "MySystolicClassification",
                "My Systolic Normal"));
        bp.addValueClassification(new ValueClassification("MYBP_NORMAL", "MyDiastolicClassification",
                "My Diastolic Normal"));
        bp.setValueDefinitionMatchOperator(CompoundLowLevelAbstractionDefinition.ValueDefinitionMatchOperator.ANY);
        bp.setMinimumNumberOfValues(1);
        bp.setGapFunctionBetweenValues(new SimpleGapFunction(90, AbsoluteTimeUnit.DAY));
        bp.setGapFunction(new SimpleGapFunction(0, null));

        return bp;
    }

    private PropositionDefinition bloodPressureClassificationAll() {
        CompoundLowLevelAbstractionDefinition bp = new CompoundLowLevelAbstractionDefinition(
                "MyBloodPressureClassificationAll");
        bp.setDisplayName("My Blood Pressure Classification (ALL)");
        bp.addValueClassification(new ValueClassification("MYBP_HIGH", "MySystolicClassification",
                "My Systolic High"));
        bp.addValueClassification(new ValueClassification("MYBP_HIGH", "MyDiastolicClassification",
                "My Diastolic High"));
        bp.addValueClassification(new ValueClassification("MYBP_NORMAL", "MySystolicClassification",
                "My Systolic Normal"));
        bp.addValueClassification(new ValueClassification("MYBP_NORMAL", "MyDiastolicClassification",
                "My Diastolic Normal"));
        bp.setValueDefinitionMatchOperator(CompoundLowLevelAbstractionDefinition.ValueDefinitionMatchOperator.ALL);
        bp.setMinimumNumberOfValues(1);
        bp.setGapFunctionBetweenValues(new SimpleGapFunction(90, AbsoluteTimeUnit.DAY));
        bp.setGapFunction(new SimpleGapFunction(0, null));

        return bp;
    }

    private PropositionDefinition bloodPressureClassificationAllUpdated() {
        CompoundLowLevelAbstractionDefinition bp = new CompoundLowLevelAbstractionDefinition(
                "MyBloodPressureClassificationAll");
        bp.setDisplayName("My Blood Pressure Classification (ALL) Updated");
        bp.addValueClassification(new ValueClassification("MYBP2_HIGH", "MySystolicClassification",
                "My Systolic High"));
        bp.addValueClassification(new ValueClassification("MYBP2_HIGH", "MyDiastolicClassification",
                "My Diastolic High"));
        bp.addValueClassification(new ValueClassification("MYBP2_NORMAL", "MySystolicClassification",
                "My Systolic Normal"));
        bp.addValueClassification(new ValueClassification("MYBP2_NORMAL", "MyDiastolicClassification",
                "My Diastolic Normal"));
        bp.setValueDefinitionMatchOperator(CompoundLowLevelAbstractionDefinition.ValueDefinitionMatchOperator.ALL);
        bp.setMinimumNumberOfValues(1);
        bp.setGapFunctionBetweenValues(new SimpleGapFunction(90, AbsoluteTimeUnit.DAY));
        bp.setGapFunction(new SimpleGapFunction(0, null));

        return bp;
    }
    
    private PropositionDefinition bloodPressureClassificationAll2() {
        CompoundLowLevelAbstractionDefinition bp = new CompoundLowLevelAbstractionDefinition(
                "MyBloodPressureClassificationAll2");
        bp.setDisplayName("My Blood Pressure Classification (ALL) Updated");
        bp.addValueClassification(new ValueClassification("MYBP2_HIGH", "MySystolicClassification",
                "My Systolic High"));
        bp.addValueClassification(new ValueClassification("MYBP2_HIGH", "MyDiastolicClassification",
                "My Diastolic High"));
        bp.addValueClassification(new ValueClassification("MYBP2_NORMAL", "MySystolicClassification",
                "My Systolic Normal"));
        bp.addValueClassification(new ValueClassification("MYBP2_NORMAL", "MyDiastolicClassification",
                "My Diastolic Normal"));
        bp.setValueDefinitionMatchOperator(CompoundLowLevelAbstractionDefinition.ValueDefinitionMatchOperator.ALL);
        bp.setMinimumNumberOfValues(1);
        bp.setGapFunctionBetweenValues(new SimpleGapFunction(90, AbsoluteTimeUnit.DAY));
        bp.setGapFunction(new SimpleGapFunction(0, null));

        return bp;
    }

    private PropositionDefinition bloodPressureClassification3Any() {
        CompoundLowLevelAbstractionDefinition bp = new CompoundLowLevelAbstractionDefinition(
                "MyBloodPressureClassification3Any");
        bp.setDisplayName("My Blood Pressure Classification 3 (ANY)");
        bp.addValueClassification(new ValueClassification("MYBP3_HIGH", "MySystolicClassification3",
                "My Systolic High 3"));
        bp.addValueClassification(new ValueClassification("MYBP3_HIGH", "MyDiastolicClassification3",
                "My Diastolic High 3"));
        bp.addValueClassification(new ValueClassification("MYBP3_LOW", "MySystolicClassification3",
                "My Systolic Low 3"));
        bp.addValueClassification(new ValueClassification("MYBP3_LOW", "MyDiastolicClassification3",
                "My Diastolic Low"));
        bp.addValueClassification(new ValueClassification("MYBP3_NORMAL", "MySystolicClassification3",
                "My Systolic Normal 3"));
        bp.addValueClassification(new ValueClassification("MYBP3_NORMAL", "MyDiastolicClassification3",
                "My Diastolic Normal 3"));
        bp.setValueDefinitionMatchOperator(CompoundLowLevelAbstractionDefinition.ValueDefinitionMatchOperator.ANY);
        bp.setMinimumNumberOfValues(1);
        bp.setGapFunctionBetweenValues(new SimpleGapFunction(90, AbsoluteTimeUnit.DAY));
        bp.setGapFunction(new SimpleGapFunction(0, null));

        return bp;
    }
}
