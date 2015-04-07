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

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import org.protempa.SourceSystem;
import org.protempa.proposition.value.AbsoluteTimeGranularityUtil;
import org.protempa.proposition.value.Granularity;
import org.protempa.proposition.value.Value;

/**
 * Generate instances of {@link PrimitiveParameter} based on the provided date
 * format and granularity. <b>NOTE:</b> This class is not thread-safe, as it
 * uses a {@link DateFormat} instance field to parse dates from strings.
 */
public final class TemporalPrimitiveParameterFactory {

    private final DateFormat dateFormat;
    private final Granularity granularity;
    private final UniqueIdFactory uniqueIdFactory;

    public TemporalPrimitiveParameterFactory(DateFormat dateFormat,
            Granularity granularity, UniqueIdFactory uniqueIdFactory) {
        if (dateFormat == null) {
            this.dateFormat = DateFormat.getDateTimeInstance();
        } else {
            this.dateFormat = dateFormat;
        }
        this.granularity = granularity;
        if (uniqueIdFactory != null) {
            this.uniqueIdFactory = uniqueIdFactory;
        } else {
            this.uniqueIdFactory = new DefaultUniqueIdFactory();
        }
    }

    public PrimitiveParameter getInstance(String id, String timestamp,
            SourceSystem dataSourceType) throws ParseException {
        return getInstance(id,
                timestamp != null ? this.dateFormat.parse(timestamp) : null,
                dataSourceType);
    }

    public PrimitiveParameter getInstance(String id, String timestamp,
            Value value, SourceSystem dataSourceType) throws ParseException {
        PrimitiveParameter result = getInstance(id, timestamp, dataSourceType);
        result.setValue(value);
        return result;
    }

    public PrimitiveParameter getInstance(String id, Date timestamp,
            SourceSystem dataSourceType) {
        Long tstampAsPos = AbsoluteTimeGranularityUtil.asPosition(timestamp);
        return getInstance(id, tstampAsPos, dataSourceType);
    }

    private PrimitiveParameter getInstance(String id, Long pos,
            SourceSystem dataSourceType) {
        PrimitiveParameter pp = new PrimitiveParameter(id, 
                this.uniqueIdFactory.getInstance());
        pp.setSourceSystem(dataSourceType);
        if (pos != null) {
            pp.setPosition(pos);
        }
        pp.setGranularity(this.granularity);
        return pp;
    }
}
