/*
 * #%L
 * Protempa Commons Backend Provider
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
package org.protempa.backend.dsb.relationaldb;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.protempa.proposition.value.AbsoluteTimeGranularityUtil;

/**
 * Translates between a date string with format <code>yyyyMMdd</code> and a
 * position with a granularity of
 * {@link org.protempa.proposition.value.AbsoluteTimeGranularity} with a
 * granularity of DAY.
 * 
 * @author Andrew Post
 */
public class JDBCDecimalDayParser implements JDBCPositionFormat {

    private static final Calendar calendar = Calendar.getInstance();
    private static final ThreadLocal<DateFormat> DATE_FORMAT =
            new ThreadLocal<DateFormat> () {
                @Override
                protected DateFormat initialValue() {
                    return new SimpleDateFormat("yyyyMMdd");
                }
    };

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
    public Long toPosition(ResultSet resultSet, int columnIndex, int colType)
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
            return AbsoluteTimeGranularityUtil.asPosition(calendar.getTime());
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
        Date date = AbsoluteTimeGranularityUtil.asDate(position);
        return DATE_FORMAT.get().format(date);
    }
}
