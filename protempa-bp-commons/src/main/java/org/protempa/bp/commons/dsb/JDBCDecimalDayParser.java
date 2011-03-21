package org.protempa.bp.commons.dsb;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Andrew Post
 */
public class JDBCDecimalDayParser implements PositionFormat {

    private static final Calendar calendar = Calendar.getInstance();
    private static final DateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyyMMdd");

    @Override
    public Long toLong(ResultSet resultSet, int columnIndex, int colType)
            throws SQLException {
        int date = resultSet.getInt(columnIndex);
        if (date < 10000000) {
            return null;
        }
        int year = date / 10000;
        int monthDay = date - year * 10000;
        int month = monthDay / 100;
        int day = monthDay - month * 100;
        synchronized (calendar) {
            calendar.clear();
            calendar.set(year, month, day);
            return calendar.getTimeInMillis();
        }
    }

    @Override
    public String format(Long position) {
        Date date = new Date(position);
        return DATE_FORMAT.format(date);
    }
}
