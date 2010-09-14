package org.protempa.proposition;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import org.protempa.DerivedDataSourceType;
import org.protempa.proposition.value.Granularity;

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
        return getInstance(id, start != null ? start.getTime() : null,
                finish != null ? finish.getTime() : null);
    }

    public AbstractParameter getInstance(String id, Long start, Long finish) {
        AbstractParameter e = new AbstractParameter(id);
        e.setDataSourceType(new DerivedDataSourceType());
        e.setInterval(intervalFactory.getInstance(start,
                this.granularity, finish, this.granularity));
        return e;
    }
}
