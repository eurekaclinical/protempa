package org.protempa.asb.java;

import org.protempa.AbstractAlgorithm;
import org.protempa.AlgorithmArguments;
import org.protempa.AlgorithmParameter;
import org.protempa.Algorithms;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.Segment;
import org.protempa.proposition.TemporalParameter;
import org.protempa.proposition.value.BooleanValue;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueComparator;
import org.protempa.proposition.value.ValueType;

/**
 * Detects whether the values of a time sequence segment are above and/or below
 * a specified value. It handles missing (<code>null</code>) values (see
 * {@link #compute} below), but does not handle nan yet.
 * 
 * @author Andrew Post
 */
public final class StateAlgorithm extends AbstractAlgorithm {

    private static final long serialVersionUID = 1254880729946491923L;

    public StateAlgorithm(Algorithms algorithms, String id) {
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
        setMinimumNumberOfValues(1);
        setMaximumNumberOfValues(1);
    }

    /**
     * Computes whether the values of a time sequence segment are above and/or
     * below a specified value.
     *
     * @param segment
     *            a {@link Segment<PrimitiveParameter>} object.
     * @return a {@link Value} if the values of <code>segment</code> are above
     *         and/or below the specified thresholds, <code>null</code>
     *         otherwise. If any of the values of <code>segment</code> are
     *         <code>null</code>, this methods also returns <code>null</code>.
     * @see org.protempa.AbstractAlgorithm#compute(org.protempa.proposition.Segment)
     */
    @Override
    public Value compute(Segment<PrimitiveParameter> segment,
            AlgorithmArguments args) {
        Value minThreshold = args.value("minThreshold");
        ValueComparator minComparator = args.valueComp("minThreshold");
        Value maxThreshold = args.value("maxThreshold");
        ValueComparator maxComparator = args.valueComp("maxThreshold");
        if ((minThreshold != null && minComparator != null)
                || (maxThreshold != null && maxComparator != null)) {
            for (int i = 0, n = segment.size(); i < n; i++) {
                TemporalParameter param = segment.get(i);
                Value val = param.getValue();
                if (val == null
                        || (minComparator != null && !minComparator.is(val.compare(minThreshold)))
                        || (maxComparator != null && !maxComparator.is(val.compare(maxThreshold)))) {
                    return null;
                }
            }
        }

        return BooleanValue.TRUE;
    }
}
