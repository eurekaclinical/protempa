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
package org.protempa;


import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.interval.Interval;
import org.protempa.proposition.interval.IntervalFactory;
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
        AbstractParameter param = new AbstractParameter("TEST");
        param.setSourceSystem(SourceSystem.DERIVED);
        param.setValue(new NumberValue(13));
        Interval ival = intervalFactory.getInstance(0L,
                AbsoluteTimeGranularity.HOUR, 12L * 60 * 60 * 1000,
                AbsoluteTimeGranularity.HOUR);
        param.setInterval(ival);
        return param;
    }

    static AbstractParameter thirteenHourParameter() {
        AbstractParameter param = new AbstractParameter("TEST");
        param.setSourceSystem(SourceSystem.DERIVED);
        param.setValue(new NumberValue(13));
        param.setInterval(intervalFactory.getInstance(0L,
                AbsoluteTimeGranularity.HOUR, 13L * 60 * 60 * 1000,
                AbsoluteTimeGranularity.HOUR));
        return param;
    }

    static AbstractParameter elevenHourParameter() {
        AbstractParameter param = new AbstractParameter("TEST");
        param.setSourceSystem(SourceSystem.DERIVED);
        param.setValue(new NumberValue(13));
        param.setInterval(intervalFactory.getInstance(0L,
                AbsoluteTimeGranularity.HOUR, 11L * 60 * 60 * 1000,
                AbsoluteTimeGranularity.HOUR));
        return param;
    }
}
