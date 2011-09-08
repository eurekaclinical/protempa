package org.protempa.proposition;

import java.util.UUID;
import org.protempa.proposition.interval.AbstractIntervalUtilTestBase;
import org.protempa.proposition.value.AbsoluteTimeUnit;

/**
 *
 * @author Andrew Post
 */
public class PropositionUtilTest extends AbstractIntervalUtilTestBase {

    @Override
    protected long distanceBetween(AbsoluteTimeUnit absoluteTimeUnit) {
        return PropositionUtil.distanceBetween(tp1(), tp2(), absoluteTimeUnit);
    }

    @Override
    protected String distanceBetweenFormattedShort() {
        return PropositionUtil.distanceBetweenFormattedShort(tp1(), tp2());
    }

    @Override
    protected String distanceBetweenFormattedShort(
            AbsoluteTimeUnit absoluteTimeUnit) {
        return PropositionUtil.distanceBetweenFormattedShort(tp1(), tp2(), 
                absoluteTimeUnit);
    }
    
    @Override
    protected String distanceBetweenFormattedMedium() {
        return PropositionUtil.distanceBetweenFormattedMedium(tp1(), tp2());
    }
    
    @Override
    protected String distanceBetweenFormattedMedium(
            AbsoluteTimeUnit absoluteTimeUnit) {
        return PropositionUtil.distanceBetweenFormattedMedium(tp1(), tp2(),
                absoluteTimeUnit);
    }
    
    @Override
    protected String distanceBetweenFormattedLong() {
        return PropositionUtil.distanceBetweenFormattedLong(tp1(), tp2());
    }
    
    @Override
    protected String distanceBetweenFormattedLong(
            AbsoluteTimeUnit absoluteTimeUnit) {
        return PropositionUtil.distanceBetweenFormattedLong(tp1(), tp2(),
                absoluteTimeUnit);
    }
    
    private TemporalProposition tp1() {
        TemporalProposition tp1 = new Event("foo", uid());
        tp1.setInterval(ival1());
        return tp1;
    }
    
    private TemporalProposition tp2() {
        TemporalProposition tp2 = new Event("foo", uid());
        tp2.setInterval(ival2());
        return tp2;
    }
    
    private static UniqueId uid() {
        return new UniqueId(
                DerivedSourceId.getInstance(),
                new DerivedUniqueId(UUID.randomUUID().toString()));
    }
}
