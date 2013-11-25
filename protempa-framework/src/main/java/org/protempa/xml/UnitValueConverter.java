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
package org.protempa.xml;

import com.thoughtworks.xstream.converters.SingleValueConverter;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.mvel.ConversionException;
import org.protempa.proposition.value.AbsoluteTimeUnit;
import org.protempa.proposition.value.RelativeDayUnit;
import org.protempa.proposition.value.RelativeHourUnit;
import org.protempa.proposition.value.Unit;

/**
 * Convert {@link Unit} objects to/from a string.
 *
 * @author mgrand
 */
class UnitValueConverter implements SingleValueConverter {
    private static final DualHashBidiMap<Unit, String> unitToStringMap = new DualHashBidiMap<>();

    static {
        unitToStringMap.put(AbsoluteTimeUnit.SECOND, "absoluteSecond");
        unitToStringMap.put(AbsoluteTimeUnit.MINUTE, "absoluteMinute");
        unitToStringMap.put(AbsoluteTimeUnit.HOUR, "absoluteHour");
        unitToStringMap.put(AbsoluteTimeUnit.DAY, "absoluteDay");
        unitToStringMap.put(AbsoluteTimeUnit.WEEK, "absoluteWeek");
        unitToStringMap.put(AbsoluteTimeUnit.MONTH, "absoluteMonth");
        unitToStringMap.put(AbsoluteTimeUnit.YEAR, "absoluteYear");
        unitToStringMap.put(RelativeDayUnit.DAY, "relativeDay");
        unitToStringMap.put(RelativeHourUnit.HOUR, "relativeHour");
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
     */
    @Override
    public boolean canConvert(@SuppressWarnings("rawtypes") Class type) {
        return Unit.class.isAssignableFrom(type);
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.xstream.converters.SingleValueConverter#toString(java.lang.Object)
     */
    @Override
    public String toString(Object obj) {
        String unitString = unitToStringMap.get(obj);
        if (unitString == null) {
            String msg = "Unable to convert unexpected Unit object to an unit attribute value: " + obj.toString();
            throw new ConversionException(msg);
        }
        return unitString;
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.xstream.converters.SingleValueConverter#fromString(java.lang.String)
     */
    @Override
    public Object fromString(String str) {
        Unit unit = unitToStringMap.getKey(str);
        if (unit == null) {
            String msg = "Unknown unit value string: " + str;
            throw new ConversionException(msg);
        }
        return unit;
    }

}
