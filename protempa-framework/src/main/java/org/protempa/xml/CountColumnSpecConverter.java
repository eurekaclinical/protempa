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
import org.protempa.KnowledgeSource;
import org.protempa.dest.table.CountColumnSpec;
import org.protempa.dest.table.Link;

/**
 * Convert {@link CountColumnSpec} object to/from XML
 * <countColumnSpec></countColumnSpec>
 *
 * @author mgrand
 */
class CountColumnSpecConverter extends AbstractConverter {
    private static final String COUNT_UNIQUE = "countUnique";
    private static final String COLUMN_NAME_OVERRIDE = "columnNameOverride";
    private static final String LINKS = "links";

    CountColumnSpecConverter(KnowledgeSource knowledgeSource) {
        super(knowledgeSource);
    }
    
    /*
     * (non-Javadoc) Convert {@link CountColumnSpec} object to/from XML
     * <countColumnSpec></countColumnSpec>
     *
     * @author mgrand
     */
    @Override
    public boolean canConvert(@SuppressWarnings("rawtypes") Class type) {
        return CountColumnSpec.class.equals(type);
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
        CountColumnSpec columnSpec = (CountColumnSpec) source;

        String columnNameOverride = columnSpec.getColumnNameOverride();
        if (columnNameOverride != null) {
            writer.addAttribute(COLUMN_NAME_OVERRIDE, columnNameOverride);
        }
        writer.addAttribute(COUNT_UNIQUE, Boolean.toString(columnSpec.isCountUnique()));

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
        String columnNameOverride = reader.getAttribute(COLUMN_NAME_OVERRIDE);

        String countUniqueString = requiredAttributeValue(reader, COUNT_UNIQUE);
        boolean countUnique = Boolean.valueOf(countUniqueString);

        reader.moveDown();
        expect(reader, LINKS);
        Link[] links = (Link[]) context.convertAnother(null, Link[].class);
        reader.moveUp();
        expectNoMore(reader);

        return new CountColumnSpec(columnNameOverride, links, countUnique);
    }

}
