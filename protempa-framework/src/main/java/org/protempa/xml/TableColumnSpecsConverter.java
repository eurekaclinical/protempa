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

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.protempa.query.handler.table.AtLeastNColumnSpec;
import org.protempa.query.handler.table.CountColumnSpec;
import org.protempa.query.handler.table.DistanceBetweenColumnSpec;
import org.protempa.query.handler.table.PropositionColumnSpec;
import org.protempa.query.handler.table.PropositionValueColumnSpec;
import org.protempa.query.handler.table.TableColumnSpec;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Convert {@link TableColumnSpec} array object to/from XML
 * <propositionIDs></propositionIDs>
 *
 * @author mgrand
 */
class TableColumnSpecsConverter extends AbstractConverter {

    private static final String PROPOSITION_VALUE_COLUMN_SPEC = "propositionValueColumnSpec";
    private static final String PROPOSITION_COLUMN_SPEC = "propositionColumnSpec";
    private static final String DISTANCE_BETWEEN_COLUMN_SPEC = "distanceBetweenColumnSpec";
    private static final String COUNT_COLUMN_SPEC = "countColumnSpec";
    private static final String AT_LEAST_N_COLUMN_SPEC = "atLeastNColumnSpec";

    /**
     * Constructor
     */
    public TableColumnSpecsConverter() {
        super();
    }

    /**
     * This converter is intended to be explicitly called from other converters
     * as it corresponds to nothing more specific than an array of strings..
     */
    @Override
    public boolean canConvert(@SuppressWarnings("rawtypes") Class clazz) {
        return TableColumnSpec[].class.equals(clazz);
    }

    /**
     * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object,
     *      com.thoughtworks.xstream.io.HierarchicalStreamWriter,
     *      com.thoughtworks.xstream.converters.MarshallingContext)
     */
    @Override
    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        TableColumnSpec[] columnSpecs = (TableColumnSpec[]) value;
        for (TableColumnSpec columnSpec : columnSpecs) {
            if (columnSpec instanceof AtLeastNColumnSpec) {
                writer.startNode(AT_LEAST_N_COLUMN_SPEC);
            } else if (columnSpec instanceof CountColumnSpec) {
                writer.startNode(COUNT_COLUMN_SPEC);
            } else if (columnSpec instanceof DistanceBetweenColumnSpec) {
                writer.startNode(DISTANCE_BETWEEN_COLUMN_SPEC);
            } else if (columnSpec instanceof PropositionColumnSpec) {
                writer.startNode(PROPOSITION_COLUMN_SPEC);
            } else if (columnSpec instanceof PropositionValueColumnSpec) {
                writer.startNode(PROPOSITION_VALUE_COLUMN_SPEC);
            } else {
                throw new ConversionException("TableColumnSpecs array contains instance of unsupported class: " + columnSpec.getClass().getName());
            }
            context.convertAnother(columnSpec);
            writer.endNode();
        }
    }

    private static final HashMap<String, Class<?>> tagToClassMap = new HashMap<>();

    static {
        tagToClassMap.put(AT_LEAST_N_COLUMN_SPEC, AtLeastNColumnSpec.class);
        tagToClassMap.put(COUNT_COLUMN_SPEC, CountColumnSpec.class);
        tagToClassMap.put(DISTANCE_BETWEEN_COLUMN_SPEC, DistanceBetweenColumnSpec.class);
        tagToClassMap.put(PROPOSITION_COLUMN_SPEC, PropositionColumnSpec.class);
        tagToClassMap.put(PROPOSITION_VALUE_COLUMN_SPEC, PropositionValueColumnSpec.class);
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
        expectChildren(reader);
        ArrayList<TableColumnSpec> tablecolumnSpecList = new ArrayList<>();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            expect(reader, tagToClassMap.keySet());
            tablecolumnSpecList.add((TableColumnSpec) context.convertAnother(null, tagToClassMap.get(reader.getNodeName())));
            reader.moveUp();
        }
        return tablecolumnSpecList.toArray(new TableColumnSpec[tablecolumnSpecList.size()]);
    }
}
