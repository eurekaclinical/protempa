package org.protempa.proposition;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

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
        PrimitiveParameter pp = new PrimitiveParameter(id);
        pp.setDataSourceType(dataSourceType);
        if (timestamp != null)
            pp.setTimestamp(timestamp);
        pp.setGranularity(this.granularity);
        return pp;
    }
}
