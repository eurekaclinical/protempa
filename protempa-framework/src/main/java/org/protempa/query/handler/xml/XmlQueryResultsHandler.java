package org.protempa.query.handler.xml;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.protempa.FinderException;
import org.protempa.KnowledgeSource;
import org.protempa.ProtempaException;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueIdentifier;
import org.protempa.query.handler.QueryResultsHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class XmlQueryResultsHandler implements QueryResultsHandler {

    private KnowledgeSource knowledgeSource;
    private final Writer out;

    public XmlQueryResultsHandler(Writer writer) {
        this.out = writer;
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

    public void init(KnowledgeSource knowledgeSource) throws FinderException {
        this.knowledgeSource = knowledgeSource;
        try {
            this.out.write("<patients>");
            this.out.flush();
        } catch (IOException ioe) {
            throw new FinderException(ioe);
        }
    }

    private List<Proposition> createReferenceList(List<UniqueIdentifier> uids,
            Map<UniqueIdentifier, Proposition> references) {
        List<Proposition> propositions = new ArrayList<Proposition>();
        if (uids != null) {
            for (UniqueIdentifier uid : uids) {
                Proposition refProp = references.get(uid);
                if (refProp != null) {
                    propositions.add(refProp);
                }
            }
        }
        return propositions;
    }

    private List<Proposition> filterHandled(List<Proposition> propositions,
            Set<Proposition> handled) {
        List<Proposition> filtered = new ArrayList<Proposition>();
        if (propositions != null) {
            for (Proposition proposition : propositions) {
                if (!handled.contains(proposition)) {
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

    private Element handleReferences(Set<Proposition> handled,
            Map<Proposition, List<Proposition>> derivations,
            Map<UniqueIdentifier, Proposition> references,
            Proposition proposition, XmlPropositionVisitor visitor,
            Document document) throws ProtempaException {
        Element referencesElem = document.createElement("references");
        Set<String> refNames = proposition.getReferenceNames();
        if (refNames != null && (!refNames.isEmpty())) {
            for (String refName : refNames) {
                List<UniqueIdentifier> uids = proposition
                        .getReferences(refName);
                List<Proposition> refProps = createReferenceList(uids,
                        references);
                if (!refProps.isEmpty()) {
                    List<Proposition> filteredReferences = filterHandled(
                            refProps, handled);
                    if (!filteredReferences.isEmpty()) {
                        Element refElem = document.createElement("reference");
                        refElem.setAttribute("type", refName);
                        for (Proposition refProp : filteredReferences) {
                            Element e = handleProposition(handled, derivations,
                                    references, refProp, visitor, document);
                            if (e != null) {
                                refElem.appendChild(e);
                            }
                        }
                        referencesElem.appendChild(refElem);
                    }
                }
            }
        }
        return referencesElem;
    }

    private Element handleDerivations(Set<Proposition> handled,
            Map<Proposition, List<Proposition>> derivations,
            Map<UniqueIdentifier, Proposition> references,
            Proposition proposition, XmlPropositionVisitor visitor,
            Document document) throws ProtempaException {
        Element derivationsElem = document.createElement("derivations");
        List<Proposition> derivedPropositions = filterHandled(
                derivations.get(proposition), handled);
        if (derivedPropositions != null) {
            for (Proposition derivedProposition : derivedPropositions) {
                Element derivedElem = handleProposition(handled, derivations,
                        references, derivedProposition, visitor, document);
                if (derivedElem != null) {
                    derivationsElem.appendChild(derivedElem);
                }
            }
        }
        return derivationsElem;
    }

    private Element handleProposition(Set<Proposition> handled,
            Map<Proposition, List<Proposition>> derivations,
            Map<UniqueIdentifier, Proposition> references,
            Proposition proposition, XmlPropositionVisitor visitor,
            Document document) throws ProtempaException {
        if (!handled.contains(proposition)) {
            handled.add(proposition);
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

            propElem.appendChild(handleReferences(handled, derivations,
                    references, proposition, visitor, document));

            propElem.appendChild(handleDerivations(handled, derivations,
                    references, proposition, visitor, document));

            return propElem;
        }
        return null;
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

    @Override
    public void handleQueryResult(String key, List<Proposition> propositions,
            Map<Proposition, List<Proposition>> derivations,
            Map<UniqueIdentifier, Proposition> references)
            throws FinderException {
        try {
            Set<Proposition> handled = new HashSet<Proposition>();
            Set<Proposition> propositionsAsSet = new HashSet<Proposition>();
            List<Proposition> propositionsCopy = new ArrayList<Proposition>(
                    propositionsAsSet);

            XmlPropositionVisitor visitor = new XmlPropositionVisitor(
                    this.knowledgeSource);

            Proposition patientAll = null;
            for (Proposition p : propositions) {
                if (p.getId().equals("PatientAll")) {
                    patientAll = p;
                    break;
                }
            }

            Document document = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder().newDocument();
            Element rootNode = document.createElement("patient");
            rootNode.setAttribute("id", key);
            if (patientAll != null) {
                Element elem = handleProposition(handled, derivations,
                        references, patientAll, visitor, document);
                if (null != elem) {
                    rootNode.appendChild(elem);
                }
            }
            for (Proposition proposition : propositionsCopy) {
                Element elem = handleProposition(handled, derivations,
                        references, proposition, visitor, document);
                if (null != elem) {
                    rootNode.appendChild(elem);
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
}
