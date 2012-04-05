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

    public Interval getInstance(Date position, Granularity gran) {
        return factory.getInstance(
                position != null ? position.getTime() : null, gran);
    }

    public Interval getInstance(Date start, Granularity startGran, 
            Date finish, Granularity finishGran) {
        return factory.getInstance(start != null ? start.getTime() : null, 
                startGran, finish != null ? finish.getTime() : null, 
                finishGran);
    }

    public Interval getInstance(Date minStart, Date maxStart, 
            Granularity startGran, Date minFinish, Date maxFinish, 
            Granularity finishGran) {
        return factory.getInstance(
                minStart != null ? minStart.getTime() : null,
                maxStart != null ? maxStart.getTime() : null, startGran, 
                minFinish != null ? minFinish.getTime() : null, 
                maxFinish != null ? maxFinish.getTime() : null, finishGran);
    }
}
