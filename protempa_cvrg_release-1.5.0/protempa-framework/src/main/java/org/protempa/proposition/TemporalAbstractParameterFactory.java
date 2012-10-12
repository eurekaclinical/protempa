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
package org.protempa.proposition;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

import org.protempa.DerivedDataSourceType;
import org.protempa.proposition.interval.IntervalFactory;
import org.protempa.proposition.value.AbsoluteTimeGranularityUtil;
import org.protempa.proposition.value.Granularity;

/**
 * Generate instances of {@link AbstractParameter} based on the provided date 
 * format, and time granularity.  
 * <b>NOTE:</b> This class is not thread-safe, as it uses a {@link DateFormat} 
 * instance field to parse dates from strings.
 */
public final class TemporalAbstractParameterFactory {
    private static final IntervalFactory intervalFactory =
            new IntervalFactory();

    private final DateFormat dateFormat;
    private final Granularity granularity;

    public TemporalAbstractParameterFactory(DateFormat dateFormat,
            Granularity granularity) {
        if (dateFormat == null) {
            this.dateFormat = DateFormat.getDateTimeInstance();
        } else {
            this.dateFormat = dateFormat;
        }
        this.granularity = granularity;
    }

    public AbstractParameter getInstance(String id, String start,
            String finish)
            throws ParseException {

        return getInstance(id, start != null ? this.dateFormat.parse(start)
                : null, finish != null ? this.dateFormat.parse(finish) : null);
    }

    public AbstractParameter getInstance(String id, Date start, Date finish) {
        Long startAsPos = AbsoluteTimeGranularityUtil.asPosition(start);
        Long finishAsPos = AbsoluteTimeGranularityUtil.asPosition(finish);
        return getInstance(id, startAsPos, finishAsPos);
    }

    private AbstractParameter getInstance(String id, Long start, Long finish) {
        AbstractParameter e = new AbstractParameter(id, new UniqueId(
                DerivedSourceId.getInstance(),
                new DerivedUniqueId(UUID.randomUUID().toString())));
        e.setDataSourceType(DerivedDataSourceType.getInstance());
        e.setInterval(intervalFactory.getInstance(start,
                this.granularity, finish, this.granularity));
        return e;
    }
}
