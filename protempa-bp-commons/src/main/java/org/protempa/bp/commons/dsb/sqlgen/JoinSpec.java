package org.protempa.bp.commons.dsb.sqlgen;

import java.io.Serializable;

/**
 * Specifies a relational join between two tables.
 *
 * @author Andrew Post
 */
public final class JoinSpec implements Serializable {
    private final String fromKey;
    private final String toKey;
    private final ColumnSpec nextColumnSpec;
    private ColumnSpec prevColumnSpec;

    
    public JoinSpec(String fromKey, String toKey, ColumnSpec nextColumnSpec) {
        if (fromKey == null)
            throw new IllegalArgumentException("fromKey cannot be null");
        if (toKey == null)
            throw new IllegalArgumentException("toKey cannot be null");
        if (nextColumnSpec == null)
            throw new IllegalArgumentException("nextColumnSpec cannot be null");
        this.fromKey = fromKey;
        this.toKey = toKey;
        this.nextColumnSpec = nextColumnSpec;
    }

    public ColumnSpec getPrevColumnSpec() {
        return prevColumnSpec;
    }

    void setPrevColumnSpec(ColumnSpec prevColumnSpec) {
        this.prevColumnSpec = prevColumnSpec;
    }

    public String getFromKey() {
        return this.fromKey;
    }

    public String getToKey() {
        return this.toKey;
    }

    public ColumnSpec getNextColumnSpec() {
        return this.nextColumnSpec;
    }

}
