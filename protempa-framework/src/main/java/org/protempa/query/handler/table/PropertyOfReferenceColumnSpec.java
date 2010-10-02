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

    public PropertyOfReferenceColumnSpec(String referenceName,
            String[] propertyNames) {
        this(referenceName, propertyNames, null);
    }

    public PropertyOfReferenceColumnSpec(String referenceName,
            String[] propertyNames, ColumnSpecConstraint[] constraints) {
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
        this.columnNames = new String[this.propertyNames.length];
        String constraintsStr = constraintHeaderString(this.constraints);
        for (int i = 0; i < this.columnNames.length; i++) {
            this.columnNames[i] = this.referenceName + "." +
                    this.propertyNames[i] + "(" + constraintsStr + ")";
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
        }
        return props.toArray(new String[props.size()]);
    }
}
