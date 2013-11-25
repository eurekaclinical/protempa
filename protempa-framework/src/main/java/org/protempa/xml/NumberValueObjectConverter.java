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

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.basic.BigDecimalConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.protempa.proposition.value.NumberValue;

import java.math.BigDecimal;

/**
 * Convert a NumberValue object to/from XML
 *
 * @author mgrand
 */
class NumberValueObjectConverter extends AbstractConverter {
    private final BigDecimalConverter bdconv = new BigDecimalConverter();

    public NumberValueObjectConverter() {
        super();
    }

    /**
     * Determine this class can convert the given object.
     */
    @Override
    public boolean canConvert(@SuppressWarnings("rawtypes") Class clazz) {
        return clazz == NumberValue.class;
    }

    /**
     * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object,
     *      com.thoughtworks.xstream.io.HierarchicalStreamWriter,
     *      com.thoughtworks.xstream.converters.MarshallingContext)
     */
    @Override
    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        NumberValue nval = (NumberValue) value;
        writer.setValue(bdconv.toString(nval.getBigDecimal()));
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks
     * .xstream.io.HierarchicalStreamReader,
     * com.thoughtworks.xstream.converters.UnmarshallingContext)
     */
    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        BigDecimal nval = (BigDecimal) bdconv.fromString(reader.getValue());
        return NumberValue.getInstance(nval);
    }
}
