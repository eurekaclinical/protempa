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
package org.protempa.query.handler.xml;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.lang3.ArrayUtils;

import org.apache.commons.lang3.StringUtils;
import org.protempa.DataSourceType;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.ProtempaException;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueId;
import org.protempa.query.Query;
import org.protempa.query.handler.AbstractQueryResultsHandler;
import org.protempa.query.handler.QueryResultsHandlerProcessingException;
import org.protempa.query.handler.QueryResultsHandlerValidationFailedException;
import org.protempa.query.handler.table.Reference;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class XmlQueryResultsHandler extends AbstractQueryResultsHandler {

    private final Map<String, String> order;
    private KnowledgeSource knowledgeSource;
    private final String initialPropId;
    private final Writer out;
    private final String[] propIds;
    private final boolean inferPropositionIdsNeeded;

    // //////////////////////////////////////////////////////////////////////////
    // Mark Grand:
    // Commented out this constructor because of a previous change by Andrew
    // that handles a null or empty array of proposition IDs by producing no
    // results. Because of that change, this constructor serves no purpose.
    // //////////////////////////////////////////////////////////////////////////
    // public XmlQueryResultsHandler(Writer writer, Map<String, String>
    // propOrder,
    // String initialProp) {
    // this(writer, propOrder, initialProp, null);
    // }
    /**
     * Constructor
     *
     * @param writer The {@link Writer} object that will be used to write the
     * output.
     * @param propOrder A {@link Map} object that will be used to guide the
     * navigation between some propositions. If this results handler visits a
     * proposition, the ID of the proposition equals a key in the Map and the
     * value associated with the key is a name in a {@link Reference} associated
     * with the proposition, then the Reference is used to navigate from the
     * proposition. This has the effect of imposing a partial ordering on the
     * nesting of XML elements in the output.
     * @param initialPropId The ID of the proposition that will be the root of
     * the output.
     * @param propIds The IDs of propositions that are to be included in the
     * output. If a proposition's ID is not included in this array, it will not
     * be included in the output.
     */
    public XmlQueryResultsHandler(Writer writer, Map<String, String> propOrder,
            String initialPropId, String[] propIds) {
        this(writer, propOrder, initialPropId, propIds, true);
    }

    public XmlQueryResultsHandler(Writer writer, Map<String, String> propOrder,
            String initialPropId, String[] propIds,
            boolean inferPropositionIdsNeeded) {
        this.out = writer;
        this.order = propOrder;
        this.initialPropId = initialPropId;
        if (propIds == null) {
            this.propIds = new String[0];
        } else {
            this.propIds = propIds.clone();
        }
        this.inferPropositionIdsNeeded = inferPropositionIdsNeeded;
    }

    @Override
    public void init(KnowledgeSource knowledgeSource, Query query) {
        this.knowledgeSource = knowledgeSource;
    }

    @Override
    public void finish() throws QueryResultsHandlerProcessingException {
        try {
            this.out.write("</patients>");
            this.out.flush();
        } catch (IOException ioe) {
            throw new QueryResultsHandlerProcessingException(ioe);
        }
    }

    @Override
    public void start() throws QueryResultsHandlerProcessingException {
        try {
            this.out.write("<patients>");
            this.out.flush();
        } catch (IOException ioe) {
            throw new QueryResultsHandlerProcessingException(ioe);
        }
    }

    @Override
    public void handleQueryResult(String key, List<Proposition> propositions,
            Map<Proposition, List<Proposition>> forwardDerivations,
            Map<Proposition, List<Proposition>> backwardDerivations,
            Map<UniqueId, Proposition> references)
            throws QueryResultsHandlerProcessingException {
        try {
            Set<UniqueId> handled = new HashSet<>();
            XmlPropositionVisitor visitor = new XmlPropositionVisitor(this.knowledgeSource);
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

            Element rootNode = document.createElement("patient");
            rootNode.setAttribute("id", key);

            Element abstractionNode = document.createElement("derived");
            Set<String> propIdsAsSet = org.arp.javautil.arrays.Arrays.asSet(this.propIds);
            DataSourceType ddst = DataSourceType.DERIVED;
            for (Proposition prop : propositions) {
                if (/*prop.getDataSourceType().equals(ddst) &&*/ propIdsAsSet.contains(prop.getId())) {
                    Element elem = handleProposition(handled, forwardDerivations, backwardDerivations, references, prop, visitor, document);
                    if (elem != null) {
                        abstractionNode.appendChild(elem);
                    }
                }
            }
            rootNode.appendChild(abstractionNode);

            if (this.initialPropId != null) {
                Proposition firstProp = findInitialProposition(this.initialPropId, propositions);
                if (firstProp != null) {
                    Element elem = handleProposition(handled, forwardDerivations, backwardDerivations, references, firstProp, visitor, document);
                    if (null != elem) {
                        rootNode.appendChild(elem);
                    }
                } else {
                    Util.logger().log(Level.SEVERE, "Initial proposition {0} not found in proposition list", this.initialPropId);
                }
            } else {
                Util.logger().log(Level.WARNING, "No initial proposition defined, printing all propositions");
                for (Proposition proposition : propositions) {
                    Element elem = handleProposition(handled, forwardDerivations, backwardDerivations, references, proposition, visitor, document);
                    if (null != elem) {
                        rootNode.appendChild(elem);
                    }
                }
            }

            document.appendChild(rootNode);
            printDocument(document);

        } catch (IOException | TransformerException | ParserConfigurationException | ProtempaException e) {
            Util.logger().log(Level.SEVERE, e.getMessage(), e);
            throw new QueryResultsHandlerProcessingException(e);
        }
    }

    @Override
    public void validate() throws QueryResultsHandlerValidationFailedException, KnowledgeSourceReadException {
        if (this.initialPropId != null
                && !this.knowledgeSource.hasPropositionDefinition(this.initialPropId)) {
            throw new QueryResultsHandlerValidationFailedException("initialPropId is invalid: " + this.initialPropId);
        }
        List<String> invalidPropIds = new ArrayList<>();
        for (String propId : this.propIds) {
            if (!this.knowledgeSource.hasPropositionDefinition(propId)) {
                invalidPropIds.add(propId);
            }
        }
        if (!invalidPropIds.isEmpty()) {
            throw new QueryResultsHandlerValidationFailedException("propIds is invalid: " + StringUtils.join(invalidPropIds, ", "));
        }
    }

    /**
     * Returns the proposition ids specified in the constructor.
     *
     * @return an array of proposition id {@link String}s. Guaranteed
     * not <code>null</code>.
     */
    @Override
    public String[] getPropositionIdsNeeded() {
        if (inferPropositionIdsNeeded) {
            Set<String> result = new HashSet<>();
            result.add(this.initialPropId);
            org.arp.javautil.arrays.Arrays.addAll(result, this.propIds);
            return result.toArray(new String[result.size()]);
        } else {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
    }

    private List<Proposition> createReferenceList(List<UniqueId> uids, Map<UniqueId, Proposition> references) {
        List<Proposition> propositions = new ArrayList<>();
        if (uids != null) {
            for (UniqueId uid : uids) {
                Proposition refProp = references.get(uid);
                if (refProp != null) {
                    propositions.add(refProp);
                }
            }
        }
        return propositions;
    }

    private List<Proposition> filterHandled(Collection<Proposition> propositions, Set<UniqueId> handled) {
        List<Proposition> filtered = new ArrayList<>();
        if (propositions != null) {
            for (Proposition proposition : propositions) {
                if (!handled.contains(proposition.getUniqueId())) {
                    filtered.add(proposition);
                }
            }
        }
        return filtered;
    }

    private Element handleProperties(Map<String, Map<String, String>> properties, Document document) {
        Element propertiesElem = document.createElement("properties");
        for (String propertyName : properties.keySet()) {
            Map<String, String> m = properties.get(propertyName);
            Element propertyElem = document.createElement("property");
            propertyElem.setAttribute("name", propertyName);
            for (String s : m.keySet()) {
                Element vElem = document.createElement(s);
                Text vText = document.createTextNode(m.get(s));
                vElem.appendChild(vText);
                propertyElem.appendChild(vElem);
            }
            propertiesElem.appendChild(propertyElem);
        }
        return propertiesElem;
    }

    private List<Element> handleValues(Map<String, String> values, Document document) {
        List<Element> valueElems = new ArrayList<>();
        for (String key : values.keySet()) {
            Element valElem = document.createElement(key);
            Text valTextElem = document.createTextNode(values.get(key));
            valElem.appendChild(valTextElem);
            valueElems.add(valElem);
        }
        return valueElems;
    }

    private Collection<String> orderReferences(Proposition proposition) {
        Collection<String> orderedRefs = null;
        String firstReference = this.order.get(proposition.getId());
        if (firstReference != null) {
            orderedRefs = Collections.singletonList(firstReference);
        } else {
            orderedRefs = Arrays.asList(proposition.getReferenceNames());
        }

        return orderedRefs;
    }

    private Element handleReferences(Set<UniqueId> handled, Map<Proposition, List<Proposition>> forwardDerivations,
            Map<Proposition, List<Proposition>> backwardDerivations, Map<UniqueId, Proposition> references, Proposition proposition,
            XmlPropositionVisitor visitor, Document document) throws ProtempaException {
        Element referencesElem = document.createElement("references");
        Collection<String> orderedReferences = orderReferences(proposition);
        Logger logger = Util.logger();
        if (logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, "Ordered References for proposition {0}: {1}", new Object[]{proposition.getId(), orderedReferences});
        }
        if (orderedReferences != null) {
            for (String refName : orderedReferences) {
                logger.log(Level.FINEST, "Processing reference {0}", refName);
                List<UniqueId> uids = proposition.getReferences(refName);
                logger.log(Level.FINEST, "Total unique identifiers: {0}", uids.size());
                logger.log(Level.FINEST, "UniqueIdentifiers: {0}", uids);
                List<Proposition> refProps = createReferenceList(uids, references);
                logger.log(Level.FINEST, "Total referred propositions:  {0}", refProps.size());
                if (!refProps.isEmpty()) {
                    List<Proposition> filteredReferences = filterHandled(refProps, handled);
                    logger.log(Level.FINEST, "Total filtered referred propositions: {0}", filteredReferences.size());
                    if (!filteredReferences.isEmpty()) {
                        Element refElem = document.createElement("reference");
                        refElem.setAttribute("name", refName);
                        for (Proposition refProp : filteredReferences) {
                            Element e = handleProposition(handled, forwardDerivations, backwardDerivations, references, refProp, visitor, document);
                            if (e != null) {
                                refElem.appendChild(e);
                            }
                        }
                        referencesElem.appendChild(refElem);
                    } else {
                        logger.log(Level.FINEST, "Skipping reference {0} because all propositions were handled", refName);
                    }
                }
            }
        }
        return referencesElem;
    }

    private Element handleDerivations(Set<UniqueId> handled, Map<Proposition, List<Proposition>> forwardDerivations,
            Map<Proposition, List<Proposition>> backwardDerivations, Map<UniqueId, Proposition> references, Proposition proposition,
            XmlPropositionVisitor visitor, Document document) throws ProtempaException {
        Element derivationsElem = document.createElement("derivations");
        List<Proposition> derived = new ArrayList<>();

//        List<Proposition> fd = forwardDerivations.get(proposition);
//        if (fd != null) {
//            derived.addAll(fd);
//        }

        List<Proposition> bd = backwardDerivations.get(proposition);
        if (bd != null) {
            derived.addAll(bd);
        }

        List<Proposition> derivedPropositions = filterHandled(derived, handled);
        if (derivedPropositions != null) {
            for (Proposition derivedProposition : derivedPropositions) {
                Element derivedElem = handleProposition(handled, forwardDerivations, backwardDerivations, references, derivedProposition, visitor, document);
                if (derivedElem != null) {
                    derivationsElem.appendChild(derivedElem);
                }
            }
        }
        return derivationsElem;
    }

    private Element handleProposition(Set<UniqueId> handled, Map<Proposition, List<Proposition>> forwardDerivations,
            Map<Proposition, List<Proposition>> backwardDerivations, Map<UniqueId, Proposition> references, Proposition proposition,
            XmlPropositionVisitor visitor, Document document) throws ProtempaException {

        if (!handled.contains(proposition.getUniqueId())) {
            Util.logger().log(Level.FINEST, "Processing proposition {0} with unique id {1}", new Object[]{proposition.getId(), proposition.getUniqueId()});

            // create a new set to pass down to the "children" (references and
            // derivations) of this proposition
            Set<UniqueId> tempHandled = new HashSet<>(handled);
            tempHandled.add(proposition.getUniqueId());
            Element propElem = document.createElement("proposition");
            propElem.setAttribute("id", proposition.getId());
            proposition.acceptChecked(visitor);

            for (Element elem : handleValues(visitor.getPropositionValues(), document)) {
                propElem.appendChild(elem);
            }
            propElem.appendChild(handleProperties(visitor.getPropositionProperties(), document));

            visitor.clear();

            propElem.appendChild(handleReferences(tempHandled, forwardDerivations, backwardDerivations, references, proposition, visitor, document));

            propElem.appendChild(handleDerivations(tempHandled, forwardDerivations, backwardDerivations, references, proposition, visitor, document));

            handled.add(proposition.getUniqueId());
            return propElem;
        } else {
            Util.logger().log(Level.FINEST, "Skipping proposition {0}", proposition.getId());
            return null;
        }
    }

    private void printDocument(Document doc) throws IOException, TransformerException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        transformer.transform(new DOMSource(doc), new StreamResult(this.out));
        this.out.flush();
    }

    private Proposition findInitialProposition(String propName, List<Proposition> props) {
        for (Proposition prop : props) {
            if (prop.getId().equals(propName)) {
                return prop;
            }
        }
        return null;
    }
}
