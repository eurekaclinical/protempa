package org.protempa.backend.asb.java;

import org.protempa.AbstractAlgorithm;
import org.protempa.AlgorithmArguments;
import org.protempa.Algorithms;
import org.protempa.AlgorithmParameter;
import org.protempa.proposition.Parameter;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.Segment;
import org.protempa.proposition.value.BooleanValue;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueComparator;
import org.protempa.proposition.value.ValueType;

/**
 * Determines if the minimum value of a segment is at most a defined threshold.
 * The threshold is specified as the <code>minThreshold</code> parameter.
 * 
 * @author Andrew Post
 */
public final class MinAlgorithm extends AbstractAlgorithm {

    private static final long serialVersionUID = 6131613237861460023L;

    public MinAlgorithm(Algorithms algorithms, String id) {
        super(algorithms, id);
        setParameters(new AlgorithmParameter[]{
                    new AlgorithmParameter("maxThreshold", new ValueComparator[]{
                        ValueComparator.LESS_THAN,
                        ValueComparator.LESS_THAN_OR_EQUAL_TO,
                        ValueComparator.EQUAL_TO}, ValueType.NUMERICALVALUE),
                    new AlgorithmParameter("minThreshold", new ValueComparator[]{
                        ValueComparator.GREATER_THAN,
                        ValueComparator.GREATER_THAN_OR_EQUAL_TO,
                        ValueComparator.EQUAL_TO}, ValueType.NUMERICALVALUE)});
        setMinimumNumberOfValues(-1);
    }

    @Override
    public Value compute(Segment<PrimitiveParameter> segment,
            AlgorithmArguments args) {
        Value minVal = null;
        Value minThreshold = args.value("minThreshold");
        ValueComparator minThresholdComp = args.valueComp("minThreshold");
        Value maxThreshold = args.value("maxThreshold");
        ValueComparator maxThresholdComp = args.valueComp("maxThreshold");

        // Calculate minVal.
        for (int i = 0, n = segment.size(); i < n; i++) {
            Parameter param = segment.get(i);
            Value val = param.getValue();
            if (minVal == null
                    || minVal.compare(val) == ValueComparator.GREATER_THAN) {
                minVal = val;
            }
        }

        if (minVal != null
                && (minThresholdComp == null || minThreshold == null || minThresholdComp.test(minVal.compare(minThreshold)))
                && (maxThresholdComp == null || maxThreshold == null || maxThresholdComp.test(minVal.compare(maxThreshold)))) {
            return BooleanValue.TRUE;
        } else {
            return null;
        }
    }
    // @Override
    // public DataSourceConstraint createDataSourceConstraint() {
    // ThresholdConstraint dataSourceConstraint1 = new ThresholdConstraint();
    // dataSourceConstraint1
    // .setComparator(getParameterComparator("maxThreshold"));
    // dataSourceConstraint1.setValue(getParameterValue("maxThreshold"));
    //
    // ThresholdConstraint dataSourceConstraint2 = new ThresholdConstraint();
    // dataSourceConstraint2
    // .setComparator(getParameterComparator("minThreshold"));
    // dataSourceConstraint2.setValue(getParameterValue("minThreshold"));
    //
    // dataSourceConstraint1.setAnd(dataSourceConstraint2);
    //
    // return dataSourceConstraint1;
    // }
}
