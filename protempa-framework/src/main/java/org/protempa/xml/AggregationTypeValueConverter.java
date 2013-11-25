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
import org.protempa.proposition.value.Unit;
import org.protempa.query.handler.table.PropositionValueColumnSpec.Type;

/**
 * Convert {@link Unit} objects to/from a string.
 *
 * @author mgrand
 */
class AggregationTypeValueConverter implements SingleValueConverter {
    private static final DualHashBidiMap<Type, String> aggregationTypeToStringMap = new DualHashBidiMap<>();

    static {
        aggregationTypeToStringMap.put(Type.MAX, "max");
        aggregationTypeToStringMap.put(Type.MIN, "min");
        aggregationTypeToStringMap.put(Type.FIRST, "first");
        aggregationTypeToStringMap.put(Type.LAST, "last");
        aggregationTypeToStringMap.put(Type.SUM, "sum");
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
     */
    @Override
    public boolean canConvert(@SuppressWarnings("rawtypes") Class type) {
        return Type.class.isAssignableFrom(type);
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.xstream.converters.SingleValueConverter#toString(java.lang.Object)
     */
    @Override
    public String toString(Object obj) {
        String unitString = aggregationTypeToStringMap.get(obj);
        if (unitString == null) {
            String msg = "Unable to convert unexpected Type object to an aggregationType attribute value: " + obj.toString();
            throw new ConversionException(msg);
        }
        return unitString;
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.xstream.converters.SingleValueConverter#fromString(java.lang.String)
     */
    @Override
    public Object fromString(String str) {
        Type type = aggregationTypeToStringMap.getKey(str);
        if (type == null) {
            String msg = "Unknown aggregation type value string: " + str;
            throw new ConversionException(msg);
        }
        return type;
    }

}
