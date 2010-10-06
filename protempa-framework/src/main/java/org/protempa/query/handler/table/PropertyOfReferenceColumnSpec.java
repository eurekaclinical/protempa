package org.protempa.query.handler.table;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.ProtempaUtil;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueIdentifier;
import org.protempa.proposition.value.Value;

public final class PropertyOfReferenceColumnSpec extends AbstractColumnSpec {

    private final String[] propertyNames;
    private final String referenceName;
    private final String[] columnNames;
    private final ColumnSpecConstraint[] constraints;
    private final boolean showDisplayName;
    private final boolean showAbbrevDisplayName;

    public PropertyOfReferenceColumnSpec(String referenceName,
            String[] propertyNames) {
        this(referenceName, propertyNames, false, false);
    }

    public PropertyOfReferenceColumnSpec(String referenceName,
            String[] propertyNames, ColumnSpecConstraint[] constraints) {
        this(referenceName, propertyNames, false, false, constraints);
    }

    public PropertyOfReferenceColumnSpec(String referenceName,
            String[] propertyNames, boolean showDisplayName,
            boolean showAbbrevDisplayName) {
        this(referenceName, propertyNames, showDisplayName, 
                showAbbrevDisplayName, null);
    }

    public PropertyOfReferenceColumnSpec(String referenceName,
            String[] propertyNames, boolean showDisplayName,
            boolean showAbbrevDisplayName,
            ColumnSpecConstraint[] constraints) {
        if (referenceName == null) {
            throw new IllegalArgumentException("referenceName cannot be null");
        }
        ProtempaUtil.checkArray(propertyNames, "propertyNames");
        if (constraints != null) {
            ProtempaUtil.checkArrayForNullElement(constraints, "constraints");
        }
        this.referenceName = referenceName;
        this.propertyNames = propertyNames;
        if (constraints != null) {
            this.constraints = constraints.clone();
        } else {
            this.constraints = new ColumnSpecConstraint[0];
        }
        this.showDisplayName = showDisplayName;
        this.showAbbrevDisplayName = showAbbrevDisplayName;
        this.columnNames = new String[this.propertyNames.length +
                (this.showDisplayName ? 1 : 0) +
                (this.showAbbrevDisplayName ? 1 : 0)];
        String constraintsStr = constraintHeaderString(this.constraints);
        int i = 0;
        for (; i < this.propertyNames.length; i++) {
            this.columnNames[i] = this.referenceName + "." +
                    this.propertyNames[i] + "(" + constraintsStr + ")";
        }
        
        if (this.showDisplayName) {
            this.columnNames[i++] = this.referenceName + "_displayName(" +
                    constraintsStr + ")";
        }
        if (this.showAbbrevDisplayName) {
            this.columnNames[i] = this.referenceName + "_abbrevDisplayName(" +
                    constraintsStr + ")";
        }
    }

    @Override
    public String[] columnNames(String propId, 
            KnowledgeSource knowledgeSource)
            throws KnowledgeSourceReadException {
        return this.columnNames.clone();
    }

    @Override
    public String[] columnValues(String key, Proposition proposition, 
            Map<Proposition, List<Proposition>> derivations,
            Map<UniqueIdentifier, Proposition> references,
            KnowledgeSource knowledgeSource)
            throws KnowledgeSourceReadException {
        List<UniqueIdentifier> uids = proposition.getReferences(
                this.referenceName);
        List<String> props = new ArrayList<String>(uids.size());
        for (UniqueIdentifier uid : uids) {
            Proposition prop = references.get(uid);
            assert prop != null : 
                "Could not find proposition with unique identifier " + uid +
                " in references " + references;
            String propId = prop.getId();
            boolean compatible = constraintsCheckCompatible(prop,
                    this.constraints);
            if (!compatible) {
                continue;
            }
            for (String propName : this.propertyNames) {
                Value value = prop.getProperty(propName);
                if (value == null) {
                    props.add(Util.NULL_COLUMN);
                } else {
                    props.add(value.getFormatted());
                }
            }
            if (this.showDisplayName) {
                props.add(knowledgeSource.readPropositionDefinition(propId)
                        .getDisplayName());
            }
            if (this.showAbbrevDisplayName) {
                props.add(knowledgeSource.readPropositionDefinition(propId)
                        .getAbbreviatedDisplayName());
            }
        }
        return props.toArray(new String[props.size()]);
    }
}
