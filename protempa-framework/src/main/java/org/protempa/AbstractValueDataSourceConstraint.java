package org.protempa;

import org.protempa.proposition.value.Value;

/**
 *
 * @author Andrew Post
 */
public abstract class AbstractValueDataSourceConstraint
        extends AbstractDataSourceConstraint {

    public AbstractValueDataSourceConstraint(String propId) {
        super(propId);
    }

    private Value value;

    public void setValue(Value value) {
        this.value = value;
    }

    public Value getValue() {
        return value;
    }
}
