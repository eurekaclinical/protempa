/*
 * #%L
 * Protempa Commons Backend Provider
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
package org.protempa.backend.dsb.relationaldb;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import org.protempa.proposition.value.DateValue;
import org.protempa.proposition.value.Value;

/**
 *
 * @author Andrew Post
 */
public class JDBCDateTimeTimestampDateValueFormat implements JDBCValueFormat {

    @Override
    public Value toValue(ResultSet resultSet, int columnIndex, int columnType) 
            throws SQLException {
        Date date;
        
        switch (columnType) {
            case Types.DATE:
                date = resultSet.getDate(columnIndex);
                break;
            case Types.TIME:
                date = resultSet.getTime(columnIndex);
                break;
            default:
                /*
                 * We'll end up here for Types.TIMESTAMP and non-date SQL data
                 * types. For the latter, we'll let the JDBC driver try to 
                 * parse a date.
                 */
            	try {
                    date = resultSet.getTimestamp(columnIndex);
            	}
            	catch (Exception ne){
            		return null;
            	}
        }
        if (date != null) {
            return DateValue.getInstance(date);
        } else {
            return null;
        }
    }
    
}
