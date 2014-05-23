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

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.DataHolder;
import com.thoughtworks.xstream.converters.basic.DateConverter;
import com.thoughtworks.xstream.core.MapBackedDataHolder;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import org.protempa.AlgorithmSource;
import org.protempa.AlgorithmSourceImpl;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceImpl;
import org.protempa.backend.asb.AlgorithmSourceBackend;
import org.protempa.backend.dsb.filter.AbstractFilter;
import org.protempa.backend.dsb.filter.DateTimeFilter;
import org.protempa.backend.dsb.filter.PositionFilter;
import org.protempa.backend.dsb.filter.PropertyValueFilter;
import org.protempa.backend.ksb.KnowledgeSourceBackend;
import org.protempa.proposition.value.BooleanValue;
import org.protempa.proposition.value.DateValue;
import org.protempa.proposition.value.InequalityNumberValue;
import org.protempa.proposition.value.NominalValue;
import org.protempa.proposition.value.NumberValue;
import org.protempa.query.Query;
import org.protempa.query.QueryBuildException;
import org.protempa.query.QueryBuilder;
import org.protempa.dest.table.AtLeastNColumnSpec;
import org.protempa.dest.table.CountColumnSpec;
import org.protempa.dest.table.Derivation;
import org.protempa.dest.table.DistanceBetweenColumnSpec;
import org.protempa.dest.table.Link;
import org.protempa.dest.table.OutputConfig;
import org.protempa.dest.table.PropositionColumnSpec;
import org.protempa.dest.table.PropositionValueColumnSpec;
import org.protempa.dest.table.Reference;
import org.protempa.dest.table.ValueOutputConfig;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.TimeZone;
import java.util.logging.Logger;
import org.protempa.dest.table.TableDestination;

/**
 * This class takes a Protempa configuration information such as a query and
 * generates equivalent XML that conforms to the schema in protempa_query.xsd.
 * <p/>
 * There are two ways to use this class to create queries from XML.
 * <ul>
 * <li>You can create a query directly from an XML file by calling the static
 * method {@link #readQueryAsXML(File)}.
 * <li>You can instantiate this class and then use its
 * {@link #build(KnowledgeSource, AlgorithmSource)} method to create a query.
 * </ul>
 *
 * @author mgrand
 */
public class XMLConfiguration implements QueryBuilder {

    static final DateConverter STANDARD_DATE_CONVERTER = new DateConverter("yyyy-MM-dd'T'HH:mm:ss.S", new String[0], TimeZone.getDefault());

    private static final Logger myLogger = Logger.getLogger(XMLConfiguration.class.getName());

    private static final ThreadLocal<Boolean> surpressSchemaReference = new ThreadLocal<>();

    private final File file;

    /**
     * Call this constructor to use this class as a QueryBuilder.
     *
     * @param file The XML file that contains the query description.
     */
    public XMLConfiguration(File file) {
        super();
        this.file = file;
    }

    private static synchronized XStream getXStream(KnowledgeSource knowledgeSource) {
        XStream xstream = new XStream(new StaxDriver());
        xstream.setMode(XStream.NO_REFERENCES);
        xstream.registerConverter(STANDARD_DATE_CONVERTER, XStream.PRIORITY_VERY_HIGH);

        // booleanValue
        xstream.alias("booleanValue", BooleanValue.class);
        xstream.registerConverter(new BooleanValueObjectConverter(knowledgeSource));

        // comparator
        xstream.useAttributeFor(PropertyValueFilter.class, "valueComparator");
        xstream.aliasField("comparator", PropertyValueFilter.class, "valueComparator");
        xstream.registerConverter(new ValueComparatorValueConverter());

        // dateTimeFilter
        xstream.alias("dateTimeFilter", DateTimeFilter.class);
        xstream.registerConverter(new DateTimeFilterConverter(knowledgeSource));

        // dateValue
        xstream.alias("dateValue", DateValue.class);
        xstream.registerConverter(new DateValueObjectConverter());

        // filters
        // We want to use custom logic to traverse the links between filters
        // so tell XStream reflection to ignore the AbstractFilter class's
        // "and" field.
        xstream.omitField(AbstractFilter.class, "and");
        xstream.aliasField("propositionIDs", AbstractFilter.class, "propositionIds");
        xstream.registerLocalConverter(AbstractFilter.class, "propositionIds", new PropIDsConverter(knowledgeSource));

        // finish
        xstream.useAttributeFor(PositionFilter.class, "finish");

        // finishGranularity
        xstream.useAttributeFor(PositionFilter.class, "finishGran");
        xstream.aliasField("finishGranularity", PositionFilter.class, "finishGran");

        // finishSide
        xstream.useAttributeFor(PositionFilter.class, "finishSide");

        // granularityType
        xstream.registerConverter(new GranularityValueConverter());

        // incomparableNumberValue
        xstream.alias("incomparableNumberValue", InequalityNumberValue.class);
        xstream.registerConverter(new InequalityValueObjectConverter(knowledgeSource));

        // nominialValue
        xstream.alias("nominalValue", NominalValue.class);
        xstream.registerConverter(new NominalValueObjectConverter(knowledgeSource));

        // numberValue
        xstream.alias("numberValue", NumberValue.class);
        xstream.registerConverter(new NumberValueObjectConverter(knowledgeSource));

        // positionFilter
        xstream.omitField(PositionFilter.class, "ival");
        xstream.alias("positionFilter", PositionFilter.class);

        // property
        xstream.useAttributeFor(PropertyValueFilter.class, "property");
        xstream.aliasField("propertyName", PropertyValueFilter.class, "property");

        // propertyValueFilter
        xstream.alias("propertyValueFilter", PropertyValueFilter.class);

        // propertyValueFilter
        xstream.alias("propertyValuesFilter", PropertyValueFilter.class);
        xstream.addImplicitArray(PropertyValueFilter.class, "values");

        // start
        xstream.useAttributeFor(PositionFilter.class, "start");

        // startGranularity
        xstream.useAttributeFor(PositionFilter.class, "startGran");
        xstream.aliasField("startGranularity", PositionFilter.class, "startGran");

        // startSide
        xstream.useAttributeFor(PositionFilter.class, "startSide");

        // tableQueryResultsHandler
        xstream.alias("tableQueryResultsHandler", TableDestination.class);
        xstream.registerConverter(new TableQueryResultsHandlerConverter(knowledgeSource));

        return xstream;
    }

    private static synchronized XStream getXStream(KnowledgeSource knowledgeSource, AlgorithmSource algorithmSource) {
        XStream xstream = getXStream(knowledgeSource);
        // protempaQuery
        xstream.alias("protempaQuery", Query.class);
        xstream.registerConverter(new QueryConverter(knowledgeSource, algorithmSource));
        return xstream;
    }

    /**
     * Read XML from the specified file that describes a Protempa query and
     * create the query.
     *
     * @param file            The file to read. The top level element of the XML in the file
     *                        must be "protempaQuery" and the XML must conform to the
     *                        Protempa XML schema.
     * @param knowledgeSource
     * @param algorithmSource
     * @return The described query.
     * @throws IOException If there is a problem.
     */
    public static Query readQueryAsXML(File file, KnowledgeSource knowledgeSource, AlgorithmSource algorithmSource) throws IOException {
        myLogger.entering(XMLConfiguration.class.getName(), "readQueryAsXML");
        Query query = (Query) getXStream(knowledgeSource, algorithmSource).fromXML(file);
        return query;
    }

    /**
     * Read XML from the specified URL that describes a Protempa query and
     * create the query.
     *
     * @param url             The url to read. The top level element of the XML must be
     *                        "protempaQuery" and the XML must conform to the Protempa XML
     *                        schema.
     * @param knowledgeSource
     * @param algorithmSource
     * @return The described query.
     * @throws IOException If there is a problem.
     */
    public static Query readQueryAsXML(URL url, KnowledgeSource knowledgeSource, AlgorithmSource algorithmSource) throws IOException {
        myLogger.entering(XMLConfiguration.class.getName(), "readQueryAsXML");
        Query query = (Query) getXStream(knowledgeSource, algorithmSource).fromXML(url);
        return query;
    }

    /**
     * Read XML from the specified {@link Reader} that describes a Protempa
     * query and create the query.
     *
     * @param reader          The Reader to read. The top level element of the XML must be
     *                        "protempaQuery" and the XML must conform to the Protempa XML
     *                        schema.
     * @param knowledgeSource
     * @param algorithmSource
     * @return The described query.
     * @throws IOException If there is a problem.
     */
    public static Query readQueryAsXML(Reader reader, KnowledgeSource knowledgeSource, AlgorithmSource algorithmSource) throws IOException {
        myLogger.entering(XMLConfiguration.class.getName(), "readQueryAsXML");
        Query query = (Query) getXStream(knowledgeSource, algorithmSource).fromXML(reader);
        return query;
    }

    /**
     * Read XML from the specified input stream that describes a Protempa query
     * and create the query.
     *
     * @param inputStream     The InputStream to read. The top level element of the XML must
     *                        be "protempaQuery" and the XML must conform to the Protempa
     *                        XML schema.
     * @param knowledgeSource
     * @param algorithmSource
     * @return The described query.
     * @throws IOException If there is a problem.
     */
    public static Query readQueryAsXML(InputStream inputStream, KnowledgeSource knowledgeSource, AlgorithmSource algorithmSource) throws IOException {
        myLogger.entering(XMLConfiguration.class.getName(), "readQueryAsXML");
        Query query = (Query) getXStream(knowledgeSource, algorithmSource).fromXML(inputStream);
        return query;
    }

    /**
     * Read XML from the specified string that describes a Protempa query and
     * create the query.
     *
     * @param str             The string to read. The top level element of the XML must be
     *                        "protempaQuery" and the XML must conform to the Protempa XML
     *                        schema.
     * @param knowledgeSource
     * @param algorithmSource
     * @return The described query.
     * @throws IOException If there is a problem.
     */
    public static Query readQueryAsXML(String str, KnowledgeSource knowledgeSource, AlgorithmSource algorithmSource) throws IOException {
        myLogger.entering(XMLConfiguration.class.getName(), "readQueryAsXML");
        Query query = (Query) getXStream(knowledgeSource, algorithmSource).fromXML(str);
        return query;
    }

    /**
     * Write the given Protempa query to the specified file.
     *
     * @param query The query to be written as XML.
     * @param file  The file to write the XML to.
     * @throws IOException If there is a problem writing the file.
     */
    public static void writeQueryAsXML(Query query, File file) throws IOException {
        writeQueryAsXML(query, file, false);
    }

    /**
     * Write the given protempa query to the specified file.
     *
     * @param query                   The query to be written as XML.
     * @param file                    The file to write the XML to.
     * @param surpressSchemaReference If true, don't include a reference to the schema in the
     *                                generated XML file.
     * @throws IOException If there is a problem writing the file.
     */
    public static void writeQueryAsXML(Query query, File file, boolean surpressSchemaReference) throws IOException {
        Writer writer = new FileWriter(file);
        writeQueryAsXML(query, writer, surpressSchemaReference);
    }

    /**
     * Write the given protempa query to the specified file.
     *
     * @param query                   The query to be written as XML.
     * @param writer                  The Writer to us to write the XML.
     * @param surpressSchemaReference If true, don't include a reference to the schema in the
     *                                generated XML file.
     * @throws IOException If there is a problem writing the file.
     */
    public static void writeQueryAsXML(Query query, Writer writer, boolean surpressSchemaReference) throws IOException {
        XMLConfiguration.surpressSchemaReference.set(Boolean.valueOf(surpressSchemaReference));
        myLogger.entering(XMLConfiguration.class.getName(), "writeQueryAsXML");
        getXStream(new KnowledgeSourceImpl(new KnowledgeSourceBackend[0]), new AlgorithmSourceImpl(new AlgorithmSourceBackend[0])).toXML(query, writer);
        writer.close();
        myLogger.exiting(XMLConfiguration.class.getName(), "writeQueryAsXML");
    }

    private static XStream tableQueryResultsHandlerXStream;

    static XStream getTableQueryResultsHandlerXStream(KnowledgeSource knowledgeSource) {
        if (tableQueryResultsHandlerXStream != null) {
            return tableQueryResultsHandlerXStream;
        }
        XStream xstream = getXStream(knowledgeSource);

        xstream.registerLocalConverter(AbstractFilter.class, "rowPropositionIds", new TableColumnSpecsConverter(knowledgeSource));

        xstream.alias("atLeastNColumnSpec", AtLeastNColumnSpec.class);
        xstream.registerConverter(new AtLeastNColumnSpecConverter(knowledgeSource));

        xstream.alias("countColumnSpec", CountColumnSpec.class);
        xstream.registerConverter(new CountColumnSpecConverter(knowledgeSource));

        xstream.alias("distanceBetweenColumnSpec", DistanceBetweenColumnSpec.class);
        xstream.registerConverter(new DistanceBetweenColumnSpecConverter(knowledgeSource));

        xstream.alias("propositionColumnSpec", PropositionColumnSpec.class);
        xstream.registerConverter(new PropositionColumnSpecConverter(knowledgeSource));

        xstream.alias("propositionValueColumnSpec", PropositionValueColumnSpec.class);
        xstream.registerConverter(new PropositionValueColumnSpecConverter(knowledgeSource));

        xstream.alias("links", Link[].class);
        xstream.registerConverter(new LinksConverter(knowledgeSource));

        xstream.alias("outputConfig", OutputConfig.class);
        xstream.registerConverter(new OutputConfigConverter(knowledgeSource));

        xstream.alias("valueOutputConfig", ValueOutputConfig.class);
        xstream.registerConverter(new ValueOutputConfigConverter(knowledgeSource));

        xstream.alias("derivation", Derivation.class);
        xstream.registerConverter(new DerivationConverter(knowledgeSource));

        xstream.alias("reference", Reference.class);
        xstream.registerConverter(new ReferenceConverter(knowledgeSource));

        tableQueryResultsHandlerXStream = new XStreamWrapper(xstream);
        return tableQueryResultsHandlerXStream;
    }

    /**
     * Read XML from the specified file that describes a Protempa
     * TableQueryResultsHandler and create the {@link TableQueryResultsHandler}.
     *
     * @param file       The file to read. The top level element of the XML in the file
     *                   must be "tableQueryResultsHandler" and the XML must conform to
     *                   the Protempa XML schema.
     * @param dataWriter The Writer that the described TableQueryResultsHandler will
     *                   use to write its output.
     * @return The described TableQueryResultsHandler.
     * @throws IOException If there is a problem.
     */
    public static TableDestination readTableQueryResultsHandlerFactoryAsXML(File file, BufferedWriter dataWriter, KnowledgeSource knowledgeSource) throws IOException {
        myLogger.entering(XMLConfiguration.class.getName(), "readTableQueryResultsHandlerAsXML");
        return (TableDestination) getTableQueryResultsHandlerXStream(knowledgeSource).fromXML(file);
    }

    /**
     * Read XML from the specified URL that describes a Protempa
     * TableQueryResultsHandler and create the {@link TableQueryResultsHandler}.
     *
     * @param url        The URL to read. The top level element of the XML in the file
     *                   must be "tableQueryResultsHandler" and the XML must conform to
     *                   the Protempa XML schema.
     * @param dataWriter The Writer that the described TableQueryResultsHandler will
     *                   use to write its output.
     * @return The described TableQueryResultsHandler.
     * @throws IOException If there is a problem.
     */
    public static TableDestination readTableQueryResultsHandlerAsXML(URL url, BufferedWriter dataWriter, KnowledgeSource knowledgeSource) throws IOException {
        myLogger.entering(XMLConfiguration.class.getName(), "readTableQueryResultsHandlerAsXML");
        return (TableDestination) getTableQueryResultsHandlerXStream(knowledgeSource).fromXML(url);
    }

    /**
     * Read XML from the specified {@link Reader} that describes a Protempa
     * TableQueryResultsHandler and create the {@link TableQueryResultsHandler}.
     *
     * @param reader     The {@link Reader} to read. The top level element of the XML
     *                   must be "tableQueryResultsHandler" and the XML must conform to
     *                   the Protempa XML schema.
     * @param dataWriter The Writer that the described TableQueryResultsHandler will
     *                   use to write its output.
     * @return The described TableQueryResultsHandler.
     * @throws IOException If there is a problem.
     */
    public static TableDestination readTableQueryResultsHandlerAsXML(Reader reader, BufferedWriter dataWriter, KnowledgeSource knowledgeSource) throws IOException {
        myLogger.entering(XMLConfiguration.class.getName(), "readTableQueryResultsHandlerAsXML");
        return (TableDestination) getTableQueryResultsHandlerXStream(knowledgeSource).fromXML(reader);
    }

    /**
     * Read XML from the specified {@link InputStream} that describes a Protempa
     * TableQueryResultsHandler and create the {@link TableQueryResultsHandler}.
     *
     * @param inputStream The {@link InputStream} to read. The top level element of the
     *                    XML must be "tableQueryResultsHandler" and the XML must
     *                    conform to the Protempa XML schema.
     * @param dataWriter  The Writer that the described TableQueryResultsHandler will
     *                    use to write its output.
     * @return The described TableQueryResultsHandler.
     * @throws IOException If there is a problem.
     */
    public static TableDestination readTableQueryResultsHandlerAsXML(InputStream inputStream, BufferedWriter dataWriter, KnowledgeSource knowledgeSource) throws IOException {
        myLogger.entering(XMLConfiguration.class.getName(), "readTableQueryResultsHandlerAsXML");
        return (TableDestination) getTableQueryResultsHandlerXStream(knowledgeSource).fromXML(inputStream);
    }

    /**
     * Read XML from the specified string that describes a Protempa
     * TableQueryResultsHandler and create the {@link TableQueryResultsHandler}.
     *
     * @param str        The string to read. The top level element of the XML must be
     *                   "tableQueryResultsHandler" and the XML must conform to the
     *                   Protempa XML schema.
     * @param dataWriter The Writer that the described TableQueryResultsHandler will
     *                   use to write its output.
     * @return The described TableQueryResultsHandler.
     * @throws IOException If there is a problem.
     */
    public static TableDestination readTableQueryResultsHandlerAsXML(String str, BufferedWriter dataWriter, KnowledgeSource knowledgeSource) throws IOException {
        myLogger.entering(XMLConfiguration.class.getName(), "readTableQueryResultsHandlerAsXML");
        return (TableDestination) getTableQueryResultsHandlerXStream(knowledgeSource).fromXML(str);
    }

    /**
     * Read XML from the specified HierarchicalStreamReader that describes a
     * Protempa TableDestination and create the
     * {@link TableDestination}.
     *
     * @param hsr        The hierarchical stream from which to read the XML. The top
     *                   level element of the XML must be "tableQueryResultsHandler"
     *                   and the XML must conform to the Protempa XML schema.
     * @param dataWriter The Writer that the described TableQueryResultsHandler will
     *                   use to write its output.
     * @return The described TableDestination.
     * @throws IOException If there is a problem.
     */
    static TableDestination readTableDestinationAsXML(HierarchicalStreamReader hsr, BufferedWriter dataWriter, KnowledgeSource knowledgeSource) {
        myLogger.entering(XMLConfiguration.class.getName(), "readTableQueryResultsHandlerAsXML");
        DataHolder dataHolder = new MapBackedDataHolder();
        dataHolder.put("writer", dataWriter);
        TableDestination destination = (TableDestination) getTableQueryResultsHandlerXStream(knowledgeSource).unmarshal(hsr, null, dataHolder);
        myLogger.exiting(XMLConfiguration.class.getName(), "readTableQueryResultsHandlerAsXML");
        return destination;
    }

    /**
     * Write the given {@link TableDestination} query to the specified
     * file.
     *
     * @param destination The TableDestination to be written as XML.
     * @param file           The file to write the XML to.
     * @throws IOException If there is a problem writing the file.
     */
    public static void writeTableDestinationAsXML(TableDestination destination, File file, KnowledgeSource knowledgeSource) throws IOException {
        writeTableDestinationAsXML(destination, file, false, knowledgeSource);
    }

    /**
     * Write the given {@link TableDestination} query to the specified
     * file.
     *
     * @param destination          The TableDestination to be written as XML.
     * @param file                    The file to write the XML to.
     * @param surpressSchemaReference If true, don't include a reference to the schema in the
     *                                generated XML file.
     * @throws IOException If there is a problem writing the file.
     */
    public static void writeTableDestinationAsXML(TableDestination destination, File file, boolean surpressSchemaReference, KnowledgeSource knowledgeSource)
            throws IOException {
        Writer writer = new FileWriter(file);
        writeTableDestinationAsXML(destination, writer, surpressSchemaReference, knowledgeSource);
    }

    /**
     * Write the given {@link TableDestination} query using the
     * specified {@link Writer}.
     *
     * @param destination          The TableDestination to be written as XML.
     * @param writer                  The Writer to us to write the XML.
     * @param surpressSchemaReference If true, don't include a reference to the schema in the
     *                                generated XML file.
     * @throws IOException If there is a problem writing the file.
     */
    public static void writeTableDestinationAsXML(TableDestination destination, Writer writer, boolean surpressSchemaReference, KnowledgeSource knowledgeSource)
            throws IOException {
        XMLConfiguration.surpressSchemaReference.set(Boolean.valueOf(surpressSchemaReference));
        myLogger.entering(XMLConfiguration.class.getName(), "writeQueryAsXML");
        XStream xstream = getTableQueryResultsHandlerXStream(knowledgeSource);
        CharacterToReferenceWriter ctrw = new CharacterToReferenceWriter(writer);
        xstream.toXML(destination, ctrw);
        ctrw.close();
        myLogger.exiting(XMLConfiguration.class.getName(), "writeQueryAsXML");
    }

    static boolean isSurpressSchemaReferenceRequested() {
        return surpressSchemaReference.get().equals(Boolean.TRUE);
    }

    @Override
    public Query build(KnowledgeSource knowledgeSource, AlgorithmSource algorithmSource) throws QueryBuildException {
        try {
            return readQueryAsXML(file, knowledgeSource, algorithmSource);
        } catch (IOException e) {
            throw new QueryBuildException("Error building query from XML", e);
        }
    }
}
