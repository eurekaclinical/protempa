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
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.protempa.query.handler.table.Link;
import org.protempa.query.handler.table.PropositionValueColumnSpec;
import org.protempa.query.handler.table.PropositionValueColumnSpec.Type;

/**
 * @author mgrand
 */
class PropositionValueColumnSpecConverter extends AbstractConverter {
    private static final String AGGREGATION_TYPE = "aggregationType";
    private static final String COLUMN_NAME_PREFIX_OVERRIDE = "columnNamePrefixOverride";
    private static final String LINKS = "links";

    /*
     * (non-Javadoc)
     *
     * @see
     * com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.
     * lang.Class)
     */
    @Override
    public boolean canConvert(@SuppressWarnings("rawtypes") Class type) {
        return PropositionValueColumnSpec.class.equals(type);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object,
     * com.thoughtworks.xstream.io.HierarchicalStreamWriter,
     * com.thoughtworks.xstream.converters.MarshallingContext)
     */
    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        PropositionValueColumnSpec columnSpec = (PropositionValueColumnSpec) source;

        String columnNameOverride = columnSpec.getColumnNamePrefixOverride();
        if (columnNameOverride != null) {
            writer.addAttribute(COLUMN_NAME_PREFIX_OVERRIDE, columnNameOverride);
        }
        AggregationTypeValueConverter aggregationConverter = new AggregationTypeValueConverter();
        writer.addAttribute(AGGREGATION_TYPE, aggregationConverter.toString(columnSpec.getType()));

        writer.startNode(LINKS);
        context.convertAnother(columnSpec.getLinks());
        writer.endNode();
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
        String columnNameOverride = reader.getAttribute(COLUMN_NAME_PREFIX_OVERRIDE);

        String aggregationTypeString = requiredAttributeValue(reader, AGGREGATION_TYPE);
        Type aggregationType = (Type) new AggregationTypeValueConverter().fromString(aggregationTypeString);

        reader.moveDown();
        expect(reader, LINKS);
        Link[] links = (Link[]) context.convertAnother(null, Link[].class);
        reader.moveUp();
        expectNoMore(reader);

        return new PropositionValueColumnSpec(columnNameOverride, links, aggregationType);
    }

}
