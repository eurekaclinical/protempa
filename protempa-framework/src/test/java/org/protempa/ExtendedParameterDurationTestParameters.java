package org.protempa;

import java.util.UUID;

import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.DerivedSourceId;
import org.protempa.proposition.DerivedUniqueId;
import org.protempa.proposition.Interval;
import org.protempa.proposition.IntervalFactory;
import org.protempa.proposition.UniqueId;
import org.protempa.proposition.value.AbsoluteTimeGranularity;
import org.protempa.proposition.value.NumberValue;

/**
 * Note that we assume that we are processing data with timestamps in absolute
 * time.
 * 
 * @author Andrew Post
 */
final class ExtendedParameterDurationTestParameters {

    private static final IntervalFactory intervalFactory =
            new IntervalFactory();

    private ExtendedParameterDurationTestParameters() {
    }

    static AbstractParameter twelveHourParameter() {
        AbstractParameter param = new AbstractParameter("TEST", uid());
        param.setDataSourceType(DerivedDataSourceType.getInstance());
        param.setValue(new NumberValue(13));
        Interval ival = intervalFactory.getInstance(0L,
                AbsoluteTimeGranularity.HOUR, 12L * 60 * 60 * 1000,
                AbsoluteTimeGranularity.HOUR);
        param.setInterval(ival);
        return param;
    }

    static AbstractParameter thirteenHourParameter() {
        AbstractParameter param = new AbstractParameter("TEST", uid());
        param.setDataSourceType(DerivedDataSourceType.getInstance());
        param.setValue(new NumberValue(13));
        param.setInterval(intervalFactory.getInstance(0L,
                AbsoluteTimeGranularity.HOUR, 13L * 60 * 60 * 1000,
                AbsoluteTimeGranularity.HOUR));
        return param;
    }

    static AbstractParameter elevenHourParameter() {
        AbstractParameter param = new AbstractParameter("TEST", uid());
        param.setDataSourceType(DerivedDataSourceType.getInstance());
        param.setValue(new NumberValue(13));
        param.setInterval(intervalFactory.getInstance(0L,
                AbsoluteTimeGranularity.HOUR, 11L * 60 * 60 * 1000,
                AbsoluteTimeGranularity.HOUR));
        return param;
    }
    
    private static UniqueId uid() {
        return new UniqueId(
                DerivedSourceId.getInstance(),
                new DerivedUniqueId(UUID.randomUUID().toString()));
    }
}
