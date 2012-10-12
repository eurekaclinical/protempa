/*
 * #%L
 * Protempa Commons Backend Provider
 * %%
 * Copyright (C) 2012 Emory University
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
package org.protempa.backend.dsb.relationaldb;

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Specifies a relational join between two tables.
 *
 * @author Andrew Post
 */
public final class JoinSpec implements Serializable {

    public static enum JoinType {
        INNER,
        LEFT_OUTER
    }

    private static final long serialVersionUID = 7297631285803290163L;
    private final String fromKey;
    private final String toKey;
    private final ColumnSpec nextColumnSpec;
    private final JoinType joinType;
    private ColumnSpec prevColumnSpec;

    /**
     * Instantiates a join specification with the join keys of the two tables,
     * the path through the database from the corresponding entity's
     * main table to the right-hand-side table, and the default join type
     * ({@link JoinType.INNER}).
     *
     * @param fromKey a key {@link String} from the left-hand-side table.
     * @param toKey a key {@link String} from the right-hand-side table.
     * @param nextColumnSpec a {@link ColumnSpec} representing the path through
     * the database from the corresponding entity's main table to the
     * right-hand-side table.
     */
    public JoinSpec(String fromKey, String toKey, ColumnSpec nextColumnSpec) {
        this(fromKey, toKey, null, nextColumnSpec);
    }

    /**
     * Instantiates a join specification with the join keys of the two tables,
     * the path through the database from the corresponding entity's
     * main table to the right-hand-side table, and the join type.
     *
     * @param fromKey a key {@link String} from the left-hand-side table.
     * @param toKey a key {@link String} from the right-hand-side table.
     * @param nextColumnSpec a {@link ColumnSpec} representing the path through
     * the database from the corresponding entity's main table to the
     * right-hand-side table.
     * @param joinType a {@link JoinType}.
     */
    public JoinSpec(String fromKey, String toKey, JoinType joinType,
            ColumnSpec nextColumnSpec) {
        if (fromKey == null)
            throw new IllegalArgumentException("fromKey cannot be null");
        if (toKey == null)
            throw new IllegalArgumentException("toKey cannot be null");
        if (nextColumnSpec == null)
            throw new IllegalArgumentException(
                    "nextColumnSpec cannot be null");
        if (joinType == null)
            joinType = JoinType.INNER;
        this.fromKey = fromKey;
        this.toKey = toKey;
        this.nextColumnSpec = nextColumnSpec;
        this.joinType = joinType;
    }

    /**
     * Returns the path through the database from the corresponding entity's
     * main table to the left-hand-side table.
     *
     * @return a {@link ColumnSpec}.
     */
    public ColumnSpec getPrevColumnSpec() {
        return prevColumnSpec;
    }

    /**
     * Sets the path through the database from the corresponding entity's
     * main table to the left-hand-side table. We assume that this will not
     * be called again after the database specification is read in.
     *
     * @param prevColumnSpec a {@link ColumnSpec}.
     */
    void setPrevColumnSpec(ColumnSpec prevColumnSpec) {
        this.prevColumnSpec = prevColumnSpec;
    }

    /**
     * Gets the key from the left-hand-side table.
     *
     * @return a {@link String}.
     */
    public String getFromKey() {
        return this.fromKey;
    }

    /**
     * Gets the key from the right-hand-side table.
     *
     * @return a {@link String}.
     */
    public String getToKey() {
        return this.toKey;
    }

    /**
     * Gets the path through the database from the corresponding entity's main
     * table to the right-hand-side table.
     *
     * @return a {@link ColumnSpec}.
     */
    public ColumnSpec getNextColumnSpec() {
        return this.nextColumnSpec;
    }

    /**
     * Gets the join type. The default is {@link JoinType.INNER}.
     * 
     * @return a {@link JoinType}.
     */
    public JoinType getJoinType() {
        return this.joinType;
    }

    /**
     * Performs equality testing by comparing all of this instance's fields
     * to the fields of another instance (returns <code>false</code> if the
     * other instance is not a <code>JoinSpec</code>.
     *
     * @param obj another {@link Object}.
     * @return <code>true</code> if equal, <code>false</code> if not.
     */
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
        if (this.joinType != other.joinType) {
            return false;
        }
        return true;
    }

    /**
     * Computes a hash code from all of this instance's fields.
     *
     * @return an <code>int</code> hash code.
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash +
                (this.fromKey != null ? this.fromKey.hashCode() : 0);
        hash = 59 * hash + (this.toKey != null ? this.toKey.hashCode() : 0);
        hash = 59 * hash + (this.nextColumnSpec != null ?
            this.nextColumnSpec.hashCode() : 0);
        hash = 59 * hash + (this.prevColumnSpec != null ?
            this.prevColumnSpec.hashCode() : 0);
        hash = 59 * hash + this.joinType.hashCode();
        return hash;
    }

    boolean isSameJoin(JoinSpec other) {
        if (other == null)
            return false;
        if ((this.fromKey == null) ? (other.fromKey != null) :
            !this.fromKey.equals(other.fromKey)) {
            return false;
        }
        if ((this.toKey == null) ? (other.toKey != null) :
            !this.toKey.equals(other.toKey)) {
            return false;
        }
        if (this.nextColumnSpec == null && other.nextColumnSpec != null) {
            return false;
        }
        if (this.nextColumnSpec != null && other.nextColumnSpec == null) {
            return false;
        }
        if (!this.nextColumnSpec.isSameSchemaAndTable(other.nextColumnSpec)) {
            return false;
        }
        if (!this.prevColumnSpec.isSameSchemaAndTable(other.prevColumnSpec)) {
            return false;
        }
        if (this.joinType != other.joinType) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("fromKey", this.fromKey)
                .append("toKey", this.toKey)
                .append("nextColumnSpec", this.nextColumnSpec)
                .append("joinType", this.joinType)
                .toString();
    }

}
