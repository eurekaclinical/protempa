package org.protempa.asb.java;

import org.protempa.AbstractAlgorithm;
import org.protempa.AlgorithmArguments;
import org.protempa.AlgorithmParameter;
import org.protempa.Algorithms;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.Segment;
import org.protempa.proposition.TemporalParameter;
import org.protempa.proposition.value.BooleanValue;
import org.protempa.proposition.value.NumberValue;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueComparator;
import org.protempa.proposition.value.ValueType;

/**
 * Detects whether the slope of a linear regression line drawn through the
 * values of a time sequence segment is above and/or below a specified value. We
 * do a variability check so as not to detect slope thresholds in wildly varying
 * data. It handles missing (<code>null</code>) values (see {@link #compute}
 * below), but does not handle nan yet.
 * 
 * @author Andrew Post
 */
public final class TrendAlgorithm extends AbstractAlgorithm {

    private static final long serialVersionUID = 7080565221625383147L;

    private static double sumDiffY(
            Segment<PrimitiveParameter> points) {
        double sumDiff = 0.0;
        TemporalParameter p = points.first();
        for (int i = 1, n = points.size(); i < n; i++) {
            TemporalParameter nextp = points.get(i);
            double y = ((NumberValue) p.getValue()).doubleValue();
            double nexty = ((NumberValue) nextp.getValue()).doubleValue();
            sumDiff += Math.abs(y - nexty);
            p = nextp;
        }
        return sumDiff;
    }

    public TrendAlgorithm(Algorithms algorithms, String id) {
        super(algorithms, id);
        setParameters(new AlgorithmParameter[]{
                    new AlgorithmParameter("minThreshold", new ValueComparator[]{
                        ValueComparator.LESS_THAN,
                        ValueComparator.LESS_THAN_OR_EQUAL_TO,
                        ValueComparator.EQUAL_TO,
                        ValueComparator.GREATER_THAN_OR_EQUAL_TO,
                        ValueComparator.GREATER_THAN}, ValueType.NUMERICALVALUE),
                    new AlgorithmParameter("maxThreshold", new ValueComparator[]{
                        ValueComparator.LESS_THAN,
                        ValueComparator.LESS_THAN_OR_EQUAL_TO,
                        ValueComparator.EQUAL_TO,
                        ValueComparator.GREATER_THAN_OR_EQUAL_TO,
                        ValueComparator.GREATER_THAN}, ValueType.NUMERICALVALUE)});
        setMinimumNumberOfValues(2);
        setMaximumNumberOfValues(2);
    }

    /**
     * Computes whether the slope of a linear regression line drawn through the
     * values of a time sequence segment is above and/or below a specified
     * value.
     *
     * @param segment
     *            a {@link Segment<PrimitiveParameter>} object.
     * @return a {@link Value} if the slope of the <code>segment</code> is
     *         above and/or below the specified thresholds, <code>null</code>
     *         otherwise. If any of the values of <code>segment</code> are
     *         <code>null</code>, this methods also returns <code>null</code>.
     * @see org.protempa.AbstractAlgorithm#compute(org.protempa.proposition.Segment)
     */
    @Override
    public Value compute(
            Segment<PrimitiveParameter> segment, AlgorithmArguments args) {
        Value minSlopeThreshold = args.value("minThreshold");
        ValueComparator minSlopeThresholdComparator = args.valueComp("minThreshold");
        Value maxSlopeThreshold = args.value("maxThreshold");
        ValueComparator maxSlopeThresholdComparator = args.valueComp("maxThreshold");

        int size = segment.size();

        if (size < 2) {
            return null;
        }

        NumberValue firstValue = (NumberValue) segment.first().getValue();
        NumberValue lastValue = (NumberValue) segment.last().getValue();
        if (firstValue == null || lastValue == null) {
            return null;
        }

        double lastPointY = lastValue.doubleValue();

        // Scatter test
        RegressionLine line = null;
        try {
            line = new RegressionLine(segment);
        } catch (RegressionLine.NullValueException e) {
            return null;
        }

        /*
         * If the average differences between individual points are greater than
         * the 2 divided by the total number of points (67% for 3 points, 50%
         * for 4, 40% for 5) times the difference between the first and last
         * points, don't call this a trend -- it's high variability (return
         * FALSE)
         */
        double avgDiff = sumDiffY(segment) / (size - 1);

        double firstPointY = firstValue.doubleValue();
        double diffEnds = Math.abs(firstPointY - lastPointY);
        if (avgDiff > (2.0 / size) * diffEnds) {
            return null;
        }

        double slopeAsDouble = line.getm();
        if ((minSlopeThresholdComparator != null && minSlopeThreshold != null)
                || (maxSlopeThresholdComparator != null && maxSlopeThreshold != null)) {
            if (Double.isNaN(slopeAsDouble)) {
                return null;
            }

            NumberValue slope = new NumberValue(slopeAsDouble);

            if ((minSlopeThresholdComparator != null && !minSlopeThresholdComparator.subsumes(slope.compare(minSlopeThreshold)))
                    || (maxSlopeThresholdComparator != null && !maxSlopeThresholdComparator.subsumes(slope.compare(maxSlopeThreshold)))) {
                return null;
            }
        }

        return BooleanValue.TRUE;
    }
}
