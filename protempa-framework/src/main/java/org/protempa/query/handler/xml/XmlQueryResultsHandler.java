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
import org.apache.commons.lang.StringUtils;
import org.protempa.DerivedDataSourceType;

import org.protempa.FinderException;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.ProtempaException;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueId;
import org.protempa.query.handler.QueryResultsHandler;
import org.protempa.query.handler.QueryResultsHandlerValidationFailedException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class XmlQueryResultsHandler implements QueryResultsHandler {

    private final Map<String, String> order;
    private KnowledgeSource knowledgeSource;
    private final String initialPropId;
    private final Writer out;
    private final String[] propIds;

    public XmlQueryResultsHandler(Writer writer, Map<String, String> propOrder,
            String initialProp) {
        this(writer, propOrder, initialProp, null);
    }

    public XmlQueryResultsHandler(Writer writer, Map<String, String> propOrder,
            String initialPropId, String[] propIds) {
        this.out = writer;
        this.order = propOrder;
        this.initialPropId = initialPropId;
        if (propIds == null) {
            this.propIds = new String[0];
        } else {
            this.propIds = propIds.clone();
        }
    }

    @Override
    public void finish() throws FinderException {
        try {
            this.out.write("</patients>");
            this.out.flush();
        } catch (IOException ioe) {
            throw new FinderException(ioe);
        }
    }

    @Override
    public void init(KnowledgeSource knowledgeSource) throws FinderException {
        this.knowledgeSource = knowledgeSource;
        try {
            this.out.write("<patients>");
            this.out.flush();
        } catch (IOException ioe) {
            throw new FinderException(ioe);
        }
    }

    private List<Proposition> createReferenceList(List<UniqueId> uids,
            Map<UniqueId, Proposition> references) {
        List<Proposition> propositions = new ArrayList<Proposition>();
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

    private List<Proposition> filterHandled(
            Collection<Proposition> propositions,
            Set<UniqueId> handled) {
        List<Proposition> filtered = new ArrayList<Proposition>();
        if (propositions != null) {
            for (Proposition proposition : propositions) {
                if (!handled.contains(proposition.getUniqueId())) {
                    filtered.add(proposition);
                }
            }
        }
        return filtered;
    }

    private Element handleProperties(
            Map<String, Map<String, String>> properties, Document document) {
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

    private List<Element> handleValues(Map<String, String> values,
            Document document) {
        List<Element> valueElems = new ArrayList<Element>();
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

    private Element handleReferences(Set<UniqueId> handled,
            Map<Proposition, List<Proposition>> forwardDerivations,
            Map<Proposition, List<Proposition>> backwardDerivations,
            Map<UniqueId, Proposition> references,
            Proposition proposition, XmlPropositionVisitor visitor,
            Document document) throws ProtempaException {
        Element referencesElem = document.createElement("references");
        Collection<String> orderedReferences = orderReferences(proposition);
        Logger logger = Util.logger();
        if (logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST,
                    "Ordered References for proposition {0}: {1}",
                    new Object[] { proposition.getId(), orderedReferences });
        }
        if (orderedReferences != null) {
            for (String refName : orderedReferences) {
                logger.log(Level.FINEST, "Processing reference {0}", refName);
                List<UniqueId> uids = proposition
                        .getReferences(refName);
                logger.log(Level.FINEST, "Total unique identifiers: {0}",
                        uids.size());
                logger.log(Level.FINEST, "UniqueIdentifiers: {0}", uids);
                List<Proposition> refProps = createReferenceList(uids,
                        references);
                logger.log(Level.FINEST, "Total referred propositions:  {0}",
                        refProps.size());
                if (!refProps.isEmpty()) {
                    List<Proposition> filteredReferences = filterHandled(
                            refProps, handled);
                    logger.log(Level.FINEST,
                            "Total filtered referred propositions: {0}",
                            filteredReferences.size());
                    if (!filteredReferences.isEmpty()) {
                        Element refElem = document.createElement("reference");
                        refElem.setAttribute("name", refName);
                        for (Proposition refProp : filteredReferences) {
                            Element e = handleProposition(handled, 
                                    forwardDerivations, backwardDerivations,
                                    references, refProp, visitor, document);
                            if (e != null) {
                                refElem.appendChild(e);
                            }
                        }
                        referencesElem.appendChild(refElem);
                    } else {
                        logger.log(
                                Level.FINEST,
                                "Skipping reference {0} because all propositions were handled",
                                refName);
                    }
                }
            }
        }
        return referencesElem;
    }

    private Element handleDerivations(Set<UniqueId> handled,
            Map<Proposition, List<Proposition>> forwardDerivations,
            Map<Proposition, List<Proposition>> backwardDerivations,
            Map<UniqueId, Proposition> references,
            Proposition proposition, XmlPropositionVisitor visitor,
            Document document) throws ProtempaException {
        Element derivationsElem = document.createElement("derivations");
        List<Proposition> derived = new ArrayList<Proposition>();
        
        List<Proposition> fd = forwardDerivations.get(proposition);
        if( fd != null )
        	derived.addAll(fd);
        
        List<Proposition> bd = backwardDerivations.get(proposition);
        if( bd != null )
        	derived.addAll(bd);
        
        List<Proposition> derivedPropositions = filterHandled(
                derived, handled);
        if (derivedPropositions != null) {
            for (Proposition derivedProposition : derivedPropositions) {
                Element derivedElem = handleProposition(handled, 
                        forwardDerivations, backwardDerivations,
                        references, derivedProposition, visitor, document);
                if (derivedElem != null) {
                    derivationsElem.appendChild(derivedElem);
                }
            }
        }
        return derivationsElem;
    }

    private Element handleProposition(Set<UniqueId> handled,
            Map<Proposition, List<Proposition>> forwardDerivations,
            Map<Proposition, List<Proposition>> backwardDerivations,
            Map<UniqueId, Proposition> references,
            Proposition proposition, XmlPropositionVisitor visitor,
            Document document) throws ProtempaException {

        if (!handled.contains(proposition.getUniqueId())) {
            Util.logger().log(
                    Level.FINEST,
                    "Processing proposition {0} with unique id {1}",
                    new Object[] { proposition.getId(),
                            proposition.getUniqueId() });

            // create a new set to pass down to the "children" (references and
            // derivations) of this proposition
            Set<UniqueId> tempHandled = new HashSet<UniqueId>(
                    handled);
            tempHandled.add(proposition.getUniqueId());
            Element propElem = document.createElement("proposition");
            propElem.setAttribute("id", proposition.getId());
            proposition.acceptChecked(visitor);

            for (Element elem : handleValues(visitor.getPropositionValues(),
                    document)) {
                propElem.appendChild(elem);
            }
            propElem.appendChild(handleProperties(
                    visitor.getPropositionProperties(), document));

            visitor.clear();

            propElem.appendChild(handleReferences(tempHandled, 
                    forwardDerivations, backwardDerivations,
                    references, proposition, visitor, document));

            propElem.appendChild(handleDerivations(tempHandled, 
                    forwardDerivations, backwardDerivations,
                    references, proposition, visitor, document));

            return propElem;
        } else {
            Util.logger().log(Level.FINEST, "Skipping proposition {0}",
                    proposition.getId());
            return null;
        }
    }

    private void printDocument(Document doc) throws IOException,
            TransformerException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(
                "{http://xml.apache.org/xslt}indent-amount", "4");

        transformer.transform(new DOMSource(doc), new StreamResult(this.out));
        this.out.flush();
    }

    private Proposition findInitialProposition(String propName,
            List<Proposition> props) {
        for (Proposition prop : props) {
            if (prop.getId().equals(propName)) {
                return prop;
            }
        }
        return null;
    }

    @Override
    public void handleQueryResult(String key, List<Proposition> propositions,
            Map<Proposition, List<Proposition>> forwardDerivations,
            Map<Proposition, List<Proposition>> backwardDerivations,
            Map<UniqueId, Proposition> references)
            throws FinderException {
        try {
            Set<UniqueId> handled = new HashSet<UniqueId>();
            XmlPropositionVisitor visitor = new XmlPropositionVisitor(
                    this.knowledgeSource);
            Document document = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder().newDocument();

            Element rootNode = document.createElement("patient");
            rootNode.setAttribute("id", key);

            Element abstractionNode = document.createElement("derived");
            Set<String> propIdsAsSet =
                    org.arp.javautil.arrays.Arrays.asSet(this.propIds);
            DerivedDataSourceType ddst = DerivedDataSourceType.getInstance();
            for (Proposition prop : propositions) {
                if (prop.getDataSourceType().equals(ddst)
                        && propIdsAsSet.contains(prop.getId())) {
                    Element elem = handleProposition(handled, 
                            forwardDerivations, backwardDerivations,
                            references, prop, visitor, document);
                    if (elem != null) {
                        abstractionNode.appendChild(elem);
                    }
                }
            }
            rootNode.appendChild(abstractionNode);

            if (this.initialPropId != null) {
                Proposition firstProp = findInitialProposition(
                        this.initialPropId, propositions);
                if (firstProp != null) {
                    Element elem = handleProposition(handled, 
                            forwardDerivations, backwardDerivations,
                            references, firstProp, visitor, document);
                    if (null != elem) {
                        rootNode.appendChild(elem);
                    }
                } else {
                    Util.logger()
                            .log(Level.SEVERE,
                                    "Initial proposition {0} not found in proposition list",
                                    this.initialPropId);
                }
            } else {
                Util.logger()
                        .log(Level.WARNING,
                                "No initial proposition defined, printing all propositions");
                for (Proposition proposition : propositions) {
                    Element elem = handleProposition(handled, 
                            forwardDerivations, backwardDerivations,
                            references, proposition, visitor, document);
                    if (null != elem) {
                        rootNode.appendChild(elem);
                    }
                }
            }

            document.appendChild(rootNode);
            printDocument(document);

        } catch (IOException e) {
            Util.logger().log(Level.SEVERE, e.getMessage(), e);
            throw new FinderException(e);
        } catch (ProtempaException e) {
            Util.logger().log(Level.SEVERE, e.getMessage(), e);
            throw new FinderException(e);
        } catch (ParserConfigurationException e) {
            Util.logger().log(Level.SEVERE, e.getMessage(), e);
            throw new FinderException(e);
        } catch (TransformerException e) {
            Util.logger().log(Level.SEVERE, e.getMessage(), e);
            throw new FinderException(e);
        }
    }

    @Override
    public void validate(KnowledgeSource knowledgeSource) throws 
            QueryResultsHandlerValidationFailedException, 
            KnowledgeSourceReadException {
        if (this.initialPropId != null && 
                !knowledgeSource.hasPropositionDefinition(
                this.initialPropId)) {
            throw new QueryResultsHandlerValidationFailedException(
                    "initialPropId is invalid: " + this.initialPropId);
        }
        List<String> invalidPropIds = new ArrayList<String>();
        for (String propId : this.propIds) {
            if (!knowledgeSource.hasPropositionDefinition(propId)) {
                invalidPropIds.add(propId);
            }
        }
        if (!invalidPropIds.isEmpty()) {
            throw new QueryResultsHandlerValidationFailedException(
                    "propIds is invalid: " + 
                    StringUtils.join(invalidPropIds, ", "));
        }
    }
}
