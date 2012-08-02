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
package org.protempa.bp.commons.dsb.relationaldb;


/**
 * Utility class for SQL generation
 * 
 * @author Michel Mansour
 *
 */
public final class SqlGeneratorUtil {
    private SqlGeneratorUtil() {
    }

    /**
     * Generates an SQL-ready string for the given value based on its type.
     * 
     * @param val the value to prepare
     * @return a <tt>String</tt> ready to be appended to an SQL statement
     */
    public static String prepareValue(Object val) {
        StringBuilder result = new StringBuilder();
        
        boolean numberOrBoolean;
        if (!(val instanceof Number) && !(val instanceof Boolean)) {
            numberOrBoolean = false;
            result.append("'");
        } else {
            numberOrBoolean = true;
        }
        if (val instanceof Boolean) {
            Boolean boolVal = (Boolean) val;
            if (boolVal.equals(Boolean.TRUE)) {
                result.append(1);
            } else {
                result.append(0);
            }
        } else {
            result.append(val);
        }
        if (!numberOrBoolean) {
            result.append("'");
        }
        return result.toString();
    }
}
