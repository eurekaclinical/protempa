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

import org.protempa.DataSourceType;
import org.protempa.proposition.value.Granularity;
import org.protempa.proposition.value.Value;

public final class TemporalPrimitiveParameterFactory {
    private final DateFormat dateFormat;
    private final Granularity granularity;

    public TemporalPrimitiveParameterFactory(DateFormat dateFormat,
            Granularity granularity) {
        if (dateFormat == null)
            this.dateFormat = DateFormat.getDateTimeInstance();
        else
            this.dateFormat = dateFormat;
        this.granularity = granularity;
    }

    public PrimitiveParameter getInstance(String id, String timestamp,
            DataSourceType dataSourceType) throws ParseException {
        return getInstance(id,
                timestamp != null ? this.dateFormat.parse(timestamp) : null,
                dataSourceType);
    }

    public PrimitiveParameter getInstance(String id, String timestamp,
            Value value, DataSourceType dataSourceType) throws ParseException {
        PrimitiveParameter result = getInstance(id, timestamp, dataSourceType);
        result.setValue(value);
        return result;
    }

    public PrimitiveParameter getInstance(String id, Date timestamp,
            DataSourceType dataSourceType) {
        return getInstance(id, timestamp != null ? timestamp.getTime() : null,
                dataSourceType);
    }

    public PrimitiveParameter getInstance(String id, Long timestamp,
            DataSourceType dataSourceType) {
        PrimitiveParameter pp = new PrimitiveParameter(id, new UniqueId(
                DerivedSourceId.getInstance(),
                new DerivedUniqueId(UUID.randomUUID().toString())));
        pp.setDataSourceType(dataSourceType);
        if (timestamp != null)
            pp.setTimestamp(timestamp);
        pp.setGranularity(this.granularity);
        return pp;
    }
}
