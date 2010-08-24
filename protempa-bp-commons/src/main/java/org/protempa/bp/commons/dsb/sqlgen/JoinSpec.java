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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final JoinSpec other = (JoinSpec) obj;
        if ((this.fromKey == null) ? (other.fromKey != null) :
            !this.fromKey.equals(other.fromKey)) {
            return false;
        }
        if ((this.toKey == null) ? (other.toKey != null) :
            !this.toKey.equals(other.toKey)) {
            return false;
        }
        if (this.nextColumnSpec != other.nextColumnSpec &&
                (this.nextColumnSpec == null ||
                !this.nextColumnSpec.equals(other.nextColumnSpec))) {
            return false;
        }
        if (this.prevColumnSpec != other.prevColumnSpec &&
                (this.prevColumnSpec == null ||
                !this.prevColumnSpec.equals(other.prevColumnSpec))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.fromKey != null ? this.fromKey.hashCode() : 0);
        hash = 59 * hash + (this.toKey != null ? this.toKey.hashCode() : 0);
        hash = 59 * hash + (this.nextColumnSpec != null ?
            this.nextColumnSpec.hashCode() : 0);
        hash = 59 * hash + (this.prevColumnSpec != null ?
            this.prevColumnSpec.hashCode() : 0);
        return hash;
    }



}
