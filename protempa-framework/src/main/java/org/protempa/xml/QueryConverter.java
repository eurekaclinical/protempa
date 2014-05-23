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
import org.protempa.AlgorithmSource;
import org.protempa.KnowledgeSource;
import org.protempa.backend.dsb.filter.Filter;
import org.protempa.query.And;
import org.protempa.query.DefaultQueryBuilder;
import org.protempa.query.Query;
import org.protempa.query.QueryBuildException;

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Convert Protempa Query object to/from XML
 *
 * @author mgrand
 */
class QueryConverter extends AbstractConverter {
    private static final String PROPOSITION_IDS = "propositionIDs";
    private static final String KEY_IDS = "keyIDs";
    private static final String KEY_ID = "keyID";
    public static URL querySchemaUrl = null;
    private static Logger myLogger = Logger.getLogger(TableQueryResultsHandlerConverter.class.getName());

    private AlgorithmSource algorithmSource;

    /**
     * Constructor
     */
    public QueryConverter(KnowledgeSource knowledgeSource, AlgorithmSource algorithmSource) {
        super(knowledgeSource);
        this.algorithmSource = algorithmSource;
    }

    /**
     * Determine this class can convert the given object.
     */
    @Override
    public boolean canConvert(@SuppressWarnings("rawtypes") Class clazz) {
        return clazz == Query.class;
    }

    /**
     * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object,
     *      com.thoughtworks.xstream.io.HierarchicalStreamWriter,
     *      com.thoughtworks.xstream.converters.MarshallingContext)
     */
    @Override
    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        // Reference Schema
        writer.addAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        if (!XMLConfiguration.isSurpressSchemaReferenceRequested()) {
            writer.addAttribute("xsi:noNamespaceSchemaLocation", getUrl("query.url").toExternalForm());
        }

        Query query = (Query) value;
        // TODO marshal dataSourceBackend
        // TODO marshal knowledgeSourceBackend
        // TODO marshal algorithmSourceBackend
        // TODO marshal termSourceBackend
        String[] keyIDs = query.getKeyIds();
        if (keyIDs != null && keyIDs.length > 0) {
            StringArrayConverter keyIDsConverter = new StringArrayConverter(KEY_ID, getKnowledgeSource());
            writer.startNode(KEY_IDS);
            context.convertAnother(keyIDs, keyIDsConverter);
            writer.endNode();
        }
        String[] propIDs = query.getPropositionIds();
        PropIDsConverter propIDsConverter = new PropIDsConverter(getKnowledgeSource());
        writer.startNode(PROPOSITION_IDS);
        context.convertAnother(propIDs, propIDsConverter);
        writer.endNode();

        Filter filters = query.getFilters();
        if (filters != null) {
            FiltersConverter filtersConverter = new FiltersConverter(getKnowledgeSource());
            writer.startNode("filters");
            filtersConverter.marshal(filters, writer, context);
            writer.endNode();
        }

        And<String>[] termIds = query.getTermIds();
        if (termIds != null && termIds.length > 0) {
            TermIDsConverter termIDsConverter = new TermIDsConverter(getKnowledgeSource());
            writer.startNode("termIDs");
            termIDsConverter.marshal(termIds, writer, context);
            writer.endNode();
        }
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
        if (!reader.hasMoreChildren()) {
            throw new ConversionException("protempaQuery element has no children.");
        }
        reader.moveDown();
        // TODO unmarshal dataSourceBackend
        // TODO unmarshal knowledgeSourceBackend
        // TODO unmarshal algorithmSourceBackend
        // TODO unmarshal termSourceBackend

        DefaultQueryBuilder queryBuilder = new DefaultQueryBuilder();

        if (KEY_IDS.equals(reader.getNodeName())) {
            StringArrayConverter keyIDsConverter = new StringArrayConverter(KEY_ID, getKnowledgeSource());
            String[] keyIds = (String[]) context.convertAnother(null, String[].class, keyIDsConverter);
            queryBuilder.setKeyIds(keyIds);
            reader.moveUp();
            reader.moveDown();
        }
        expect(reader, PROPOSITION_IDS);
        PropIDsConverter propIDsConverter = new PropIDsConverter(getKnowledgeSource());
        String[] propIds = (String[]) context.convertAnother(null, String[].class, propIDsConverter);
        queryBuilder.setPropositionIds(propIds);
        reader.moveUp();

        if (reader.hasMoreChildren()) {
            do { // do loop to allow break; not for iteration
                reader.moveDown();
                if (reader.getNodeName().equals("filters")) {
                    FiltersConverter filtersConverter = new FiltersConverter(getKnowledgeSource());
                    Filter filters = (Filter) context.convertAnother(null, Filter.class, filtersConverter);
                    queryBuilder.setFilters(filters);
                    reader.moveUp();
                    if (!reader.hasMoreChildren()) {
                        break;
                    }
                    reader.moveDown();
                }
                if (reader.getNodeName().equals("termIDs")) {
                    And<String>[] termIds = unmarshalTermIds(reader, context);
                    queryBuilder.setTermIds(termIds);
                }
            } while (false);
        }
        try {
            return queryBuilder.build(getKnowledgeSource(), algorithmSource);
        } catch (QueryBuildException e) {
            myLogger.log(Level.SEVERE, "Error building query", e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private And<String>[] unmarshalTermIds(HierarchicalStreamReader reader, UnmarshallingContext context) {
        TermIDsConverter termIDsConverter = new TermIDsConverter(getKnowledgeSource());
        return (And<String>[]) context.convertAnother(null, And[].class, termIDsConverter);
    }

    /**
     * @return the URL of the schema to use for validating the XML
     *         representation of a query.
     */
    static URL getQuerySchemaUrl() {
        return getUrl("query.url");
    }
}
