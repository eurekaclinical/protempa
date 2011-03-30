package org.protempa.query.handler.xml;

import java.io.IOException;
import java.io.Writer;
import java.util.logging.Level;

import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.PropertyDefinition;
import org.protempa.PropositionDefinition;
import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.AbstractPropositionCheckedVisitor;
import org.protempa.proposition.Constant;
import org.protempa.proposition.Event;
import org.protempa.proposition.Parameter;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.TemporalProposition;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueSet;

public class XmlPropositionVisitor extends AbstractPropositionCheckedVisitor {

    private final Writer writer;
    private final KnowledgeSource knowledgeSource;

    public XmlPropositionVisitor(KnowledgeSource ks, Writer newWriter) {
        this.writer = newWriter;
        this.knowledgeSource = ks;
    }

    @Override
    public void visit(AbstractParameter abstractParameter)
            throws XmlQueryResultsHandlerException {
        try {
            doWriteName(abstractParameter);
            doWriteValue(abstractParameter);
            doWriteTime(abstractParameter);
            doWriteProperties(abstractParameter);
            this.writer.flush();
        } catch (IOException ioe) {
            throw new XmlQueryResultsHandlerException(ioe);
        }
    }

    @Override
    public void visit(Event event) throws XmlQueryResultsHandlerException {
        try {
            doWriteName(event);
            doWriteTime(event);
            doWriteProperties(event);
            this.writer.flush();
        } catch (IOException ioe) {
            throw new XmlQueryResultsHandlerException(ioe);
        }
    }

    @Override
    public void visit(PrimitiveParameter primitiveParameter)
            throws XmlQueryResultsHandlerException {
        try {
            doWriteName(primitiveParameter);
            doWriteValue(primitiveParameter);
            doWriteTime(primitiveParameter);
            doWriteProperties(primitiveParameter);
            this.writer.flush();
        } catch (IOException ioe) {
            throw new XmlQueryResultsHandlerException(ioe);
        }
    }

    @Override
    public void visit(Constant constant) throws XmlQueryResultsHandlerException {
        try {
            doWriteName(constant);
            doWriteProperties(constant);
            this.writer.flush();
        } catch (IOException ioe) {
            throw new XmlQueryResultsHandlerException(ioe);
        }
    }

    private void doWriteProperties(Proposition proposition)
            throws XmlQueryResultsHandlerException {
        try {
            for (String propName : proposition.getPropertyNames()) {
                Value value = proposition.getProperty(propName);
                this.writer.write("<" + propName + ">");
                if (value != null) {
                    String valueType = value.getType().toString();
                    this.writer.write("<valueType>" + valueType
                            + "</valueType>");
                    this.writer.write("<value>" + value.getFormatted()
                            + "</value>");
                    this.writer.write("<valueDisplayName>");
                    this.writer.write(getOutputPropertyValue(proposition,
                            propName, value));
                    this.writer.write("</valueDisplayName>");
                } else {
                    this.writer.write("(null)");
                }
                this.writer.write("</" + propName + ">");
            }
        } catch (IOException ioe) {
            throw new XmlQueryResultsHandlerException(ioe);
        }
    }

    private void doWriteName(Proposition proposition)
            throws XmlQueryResultsHandlerException {
        try {
            String name = getDisplayName(proposition);
            this.writer.write("<displayName>" + name + "</displayName>");
            this.writer.flush();
        } catch (IOException ioe) {
            throw new XmlQueryResultsHandlerException(ioe);
        } catch (KnowledgeSourceReadException e) {
            throw new XmlQueryResultsHandlerException(e);
        }
    }

    private void doWriteValue(Parameter parameter)
            throws XmlQueryResultsHandlerException {
        try {
            Value value = parameter.getValue();
            String valueType = "(null)";
            String valueFormatted = "(null)";
            if (value != null) {
                valueType = value.getType().toString();
                valueFormatted = value.getFormatted();
            }
            this.writer.write("<valueType>" + valueType + "</valueType>");
            this.writer.write("<value>" + valueFormatted + "</value>");
        } catch (IOException ioe) {
            throw new XmlQueryResultsHandlerException(ioe);
        }
    }

    private void doWriteTime(TemporalProposition proposition)
            throws XmlQueryResultsHandlerException {
        try {
            String start = proposition.getStartFormattedShort();
            String finish = proposition.getFinishFormattedShort();
            if (finish.isEmpty()) {
                this.writer.write("<date>" + start + "</date>");
            } else {
                this.writer.write("<start>"
                        + proposition.getStartFormattedShort() + "</start>");
                this.writer.write("<end>"
                        + proposition.getStartFormattedShort() + "</end>");
            }
        } catch (IOException ioe) {
            throw new XmlQueryResultsHandlerException(ioe);
        }
    }

    private String getDisplayName(Proposition proposition)
            throws KnowledgeSourceReadException {
        PropositionDefinition propositionDefinition = this.knowledgeSource
                .readPropositionDefinition(proposition.getId());
        String name = "(null)";
        if (propositionDefinition != null) {
            name = propositionDefinition.getDisplayName();
        } else {
            Util.logger()
                    .log(Level.WARNING,
                            "Cannot write display name for {0} because it is not in the knowledge source",
                            proposition.getId());
        }
        return name;
    }

    private String getOutputPropertyValue(Proposition proposition,
            String propertyName, Value propertyValue) {
        String outputValue = propertyValue.getFormatted();
        Util.logger().log(Level.FINE, "Getting property display name for {0}",
                propertyValue.getFormatted());
        try {
            PropositionDefinition propositionDef = this.knowledgeSource
                    .readPropositionDefinition(proposition.getId());
            if (propositionDef != null) {
                PropertyDefinition propertyDef = propositionDef
                        .propertyDefinition(propertyName);
                if (propertyDef != null) {
                    ValueSet valueSet = this.knowledgeSource
                            .readValueSet(propertyDef.getValueSetId());
                    if (valueSet != null) {
                        outputValue = valueSet.displayName(propertyValue);
                    }
                }
            }
        } catch (KnowledgeSourceReadException e) {
            Util.logger().log(Level.SEVERE, e.getMessage(), e);
        }
        return outputValue;
    }
}
