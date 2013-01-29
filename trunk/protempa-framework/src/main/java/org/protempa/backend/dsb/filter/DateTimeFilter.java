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
package org.protempa.backend.dsb.filter;

import java.util.Date;
import static 
        org.protempa.proposition.value.AbsoluteTimeGranularityUtil.asPosition;
import org.protempa.proposition.value.Granularity;

/**
 * A filter for date/time ranges.
 *
 * @author Andrew Post
 */
public class DateTimeFilter extends PositionFilter {

    /**
     * Creates a filter with a date/time range.
     *
     * @param propIds a {@link String[]} of proposition ids on which to filter.
     * @param start the start {@link Date} (<code>null</code> for unspecified).
     * @param startGran the {@link Granularity} with which to interpret the
     * start date.
     * @param finish the finish {@link Date} (<code>null</code> for
     * unspecified).
     * @param finishGran the {@link Granularity with which to interpret the finish
     *            date.
     */
    public DateTimeFilter(String[] propIds, Date start, Granularity startGran, 
            Date finish, Granularity finishGran) {
        super(propIds, 
                asPosition(start), startGran,
                asPosition(finish), finishGran);
    }

    /**
     * Creates a filter with a date/time range.
     *
     * @param propIds a {@link String[]} of proposition ids on which to filter.
     * @param start the start {@link Date} (<code>null</code> for unspecified).
     * @param startGran the {@link Granularity} with which to interpret the
     * start date.
     * @param finish the finish {@link Date} (<code>null</code> for
     * unspecified).
     * @param finishGran the {@link Granularity with which to interpret the
     * finish date.
     * @param startSide the {@link Side} of the proposition to which to apply
     * the start bound. The default is {@link Side.START} (if
     * <code>null</code> is specified).
     * @param finishSide the {@link Side} of the proposition to which to
     * apply the finish bound. The default is {@link Side.FINISH} (if
     * <code>null</code> is specified).
     */
    public DateTimeFilter(String[] propIds, Date start, Granularity startGran, 
            Date finish, Granularity finishGran, 
            Side startSide, Side finishSide) {
        super(propIds, 
                asPosition(start), startGran, 
                asPosition(finish), finishGran, 
                startSide, finishSide);
    }
}
