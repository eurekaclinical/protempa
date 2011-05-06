package org.protempa.bp.commons.dsb;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Translates between a date string with format <code>yyyyMMdd</code> and a
 * position with a granularity of
 * {@link org.protempa.proposition.value.AbsoluteTimeGranularity} with a
 * granularity of DAY.
 * 
 * @author Andrew Post
 */
public class JDBCDecimalDayParser implements PositionFormat {

    private static final Calendar calendar = Calendar.getInstance();
    private static final DateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyyMMdd");

    /**
     * Parses strings with format <code>yyyyMMdd</code> into a position with a
     * granularity of
     * {@link org.protempa.proposition.value.AbsoluteTimeGranularity} with a
     * granularity of DAY.
     *
     * @param resultSet a {@link ResultSet}.
     * @param columnIndex the column to parse.
     * @param colType the type of the column as a {@link java.sql.Types}.
     * @return a position with a granularity of
     * {@link org.protempa.proposition.value.AbsoluteTimeGranularity} with a
     * granularity of DAY.
     * @throws SQLException if an error occurred retrieving the value from
     * the result set.
     */
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
            calendar.set(year, month - 1, day);
            return calendar.getTimeInMillis();
        }
    }

    /**
     * Formats positions with a granularity of
     * {@link org.protempa.proposition.value.AbsoluteTimeGranularity} with at 
     * least a granularity of DAY into an number with format
     * <code>yyyyMMdd</code>.
     * 
     * @param position a position {@link Long} with a granularity of
     * {@link org.protempa.proposition.value.AbsoluteTimeGranularity}.
     * @return a {@link String} with format <code>yyyyMMdd</code>.
     */
    @Override
    public String format(Long position) {
        return DATE_FORMAT.format(position);
    }
}
