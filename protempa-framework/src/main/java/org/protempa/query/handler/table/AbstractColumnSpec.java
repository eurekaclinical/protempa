package org.protempa.query.handler.table;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueComparator;

public abstract class AbstractColumnSpec implements TableColumnSpec {

    boolean constraintsCheckCompatible(Proposition proposition,
            ColumnSpecConstraint[] constraints) {
        for (int i = 0; i < constraints.length; i++) {
            ColumnSpecConstraint ccc = constraints[i];
            String propName = ccc.getPropertyName();
            Value value = proposition.getProperty(propName);
            ValueComparator vc = ccc.getValueComparator();
            if (!vc.subsumes(value.compare(ccc.getValue()))) {
                return false;
            }
        }
        return true;
    }

    String constraintHeaderString(ColumnSpecConstraint[] constraints) {
        List<String> constraintsL = new ArrayList<String>(constraints.length);
        for (int i = 0; i < constraints.length; i++) {
            ColumnSpecConstraint ccc = constraints[i];
            constraintsL.add(ccc.getFormatted());
        }
        return StringUtils.join(constraintsL, ',');
    }
}
