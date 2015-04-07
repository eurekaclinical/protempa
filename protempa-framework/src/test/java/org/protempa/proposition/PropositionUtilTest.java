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
package org.protempa.proposition;

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
        TemporalProposition tp1 = new Event("foo", getUid());
        tp1.setInterval(ival1());
        return tp1;
    }
    
    private TemporalProposition tp2() {
        TemporalProposition tp2 = new Event("foo", getUid());
        tp2.setInterval(ival2());
        return tp2;
    }
    
}
