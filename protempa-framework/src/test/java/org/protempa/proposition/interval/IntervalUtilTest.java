package org.protempa.proposition.interval;

import org.protempa.proposition.value.AbsoluteTimeUnit;

/**
 *
 * @author Andrew Post
 */
public class IntervalUtilTest extends AbstractIntervalUtilTestBase {
    
    @Override
    protected long distanceBetween(AbsoluteTimeUnit absoluteTimeUnit) {
        return IntervalUtil.distanceBetween(ival1(), ival2(), 
                absoluteTimeUnit);
    }
    
    @Override
    protected String distanceBetweenFormattedShort() {
        return IntervalUtil.distanceBetweenFormattedShort(ival1(), ival2());
    }
    
    @Override
    protected String distanceBetweenFormattedShort(
            AbsoluteTimeUnit absoluteTimeUnit) {
        return IntervalUtil.distanceBetweenFormattedShort(ival1(), ival2(),
                absoluteTimeUnit);
    }
    
    @Override
    protected String distanceBetweenFormattedMedium() {
        return IntervalUtil.distanceBetweenFormattedMedium(ival1(), ival2());
    }
    
    @Override
    protected String distanceBetweenFormattedMedium(
            AbsoluteTimeUnit absoluteTimeUnit) {
        return IntervalUtil.distanceBetweenFormattedMedium(ival1(), ival2(),
                absoluteTimeUnit);
    }
    
    @Override
    protected String distanceBetweenFormattedLong() {
        return IntervalUtil.distanceBetweenFormattedLong(ival1(), ival2());
    }
    
    @Override
    protected String distanceBetweenFormattedLong(
            AbsoluteTimeUnit absoluteTimeUnit) {
        return IntervalUtil.distanceBetweenFormattedLong(ival1(), ival2(),
                absoluteTimeUnit);
    }

}
