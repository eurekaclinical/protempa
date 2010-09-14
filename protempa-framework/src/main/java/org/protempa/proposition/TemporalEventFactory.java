package org.protempa.proposition;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import org.protempa.DataSourceType;
import org.protempa.proposition.value.Granularity;

public final class TemporalEventFactory {

    private static final IntervalFactory intervalFactory =
            new IntervalFactory();
    private DateFormat dateFormat;
    private Granularity granularity;

    public TemporalEventFactory(DateFormat dateFormat,
            Granularity granularity) {
        if (dateFormat == null) {
            this.dateFormat = DateFormat.getDateTimeInstance();
        } else {
            this.dateFormat = dateFormat;
        }
        this.granularity = granularity;
    }

    public Event getInstance(String id, String timestamp,
            DataSourceType dataSourceType) throws ParseException {
        return getInstance(id, timestamp != null ?
            this.dateFormat.parse(timestamp) : null, dataSourceType);
    }

    public Event getInstance(String id, Date timestamp,
            DataSourceType dataSourceType) {
        return getInstance(id, timestamp != null ? timestamp.getTime() : null,
                dataSourceType);
    }

    public Event getInstance(String id, Long timestamp,
            DataSourceType dataSourceType) {
        Event pp = new Event(id);
        pp.setDataSourceType(dataSourceType);
        pp.setInterval(intervalFactory.getInstance(timestamp, this.granularity,
                timestamp, this.granularity));
        return pp;
    }

    public Event getInstance(String id, String start, String finish,
            DataSourceType dataSourceType)
            throws ParseException {

        return getInstance(id, start != null ? this.dateFormat.parse(start)
                : null, finish != null ? this.dateFormat.parse(finish) : null,
                dataSourceType);
    }

    public Event getInstance(String id, Date start, Date finish,
            DataSourceType dataSourceType) {
        return getInstance(id, start != null ? start.getTime() : null,
                finish != null ? finish.getTime() : null, dataSourceType);
    }

    public Event getInstance(String id, Long start, Long finish,
            DataSourceType dataSourceType) {
        Event e = new Event(id);
        e.setDataSourceType(dataSourceType);
        e.setInterval(intervalFactory.getInstance(start,
                this.granularity, finish, this.granularity));
        return e;
    }
}
