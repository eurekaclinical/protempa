/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 Emory University
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
package org.protempa.proposition.interval;

import java.util.Date;
import static 
        org.protempa.proposition.value.AbsoluteTimeGranularityUtil.asPosition;
import org.protempa.proposition.value.Granularity;

/**
 * Factory for constructing intervals from Java dates.
 * 
 * @author Andrew Post
 */
public class AbsoluteTimeIntervalFactory {
    private IntervalFactory factory;
    
    public AbsoluteTimeIntervalFactory() {
        this.factory = new IntervalFactory();
    }

    public Interval getInstance() {
        return factory.getInstance();
    }

    public Interval getInstance(Date date, Granularity gran) {
        Long dateAsPos = asPosition(date);
        return factory.getInstance(dateAsPos, gran);
    }

    public Interval getInstance(Date start, Granularity startGran, 
            Date finish, Granularity finishGran) {
        Long startAsPos = asPosition(start);
        Long finishAsPos = asPosition(finish);
        return factory.getInstance(startAsPos, startGran, finishAsPos, 
                finishGran);
    }

    public Interval getInstance(Date minStart, Date maxStart, 
            Granularity startGran, Date minFinish, Date maxFinish, 
            Granularity finishGran) {
        Long minStartAsPos = asPosition(minStart);
        Long maxStartAsPos = asPosition(maxStart);
        Long minFinishAsPos = asPosition(minFinish);
        Long maxFinishAsPos = asPosition(maxFinish);
        return factory.getInstance(minStartAsPos, maxStartAsPos, startGran, 
                minFinishAsPos, maxFinishAsPos, finishGran);
    }
}
