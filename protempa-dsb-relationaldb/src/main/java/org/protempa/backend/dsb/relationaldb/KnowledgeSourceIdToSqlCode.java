package org.protempa.backend.dsb.relationaldb;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Represents a mapping from a proposition id to a value of the column
 * specified by a column spec. Pass an array of instances of this class into
 * a {@link ColumnSpec}'s constructor.
 */
public final class KnowledgeSourceIdToSqlCode {
    final String propositionId;
    final Object sqlCode;

    /**
     * Instantiates a mapping between a proposition id and the value of the
     * column specified by a column spec.
     *
     * @param propositionId
     *            a proposition id {@link String}. Cannot be
     *            <code>null</code>.
     * @param sqlCode
     *            a value {@link Object} in a column in a table in a
     *            database. Cannot be <code>null</code>.
     */
    public KnowledgeSourceIdToSqlCode(String propositionId, Object sqlCode) {
        if (propositionId == null) {
            throw new IllegalArgumentException("propositionId cannot be null");
        }
        if (sqlCode == null) {
            throw new IllegalArgumentException("sqlCode cannot be null");
        }
        this.propositionId = propositionId.intern();
        this.sqlCode = sqlCode;
    }

    /**
     * Returns the proposition id in the mapping.
     *
     * @return a proposition id {@link String}. Guaranteed not
     *         <code>null</code>.
     */
    public String getPropositionId() {
        return this.propositionId;
    }

    /**
     * Returns the value {@link Object} in the mapping.
     *
     * @return a code {@link Object} in a relational database. Guaranteed
     *         not <code>null</code>.
     */
    public Object getSqlCode() {
        return this.sqlCode;
    }

    /**
     * Compares the proposition id and value for equality.
     *
     * @param obj
     *            another {@link Object}.
     * @return <code>true</code> if the proposition ids and values are
     *         equal, <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final KnowledgeSourceIdToSqlCode other = (KnowledgeSourceIdToSqlCode) obj;
        if (!this.propositionId.equals(other.propositionId)) {
            return false;
        }
        if (!this.sqlCode.equals(other.sqlCode)) {
            return false;
        }
        return true;
    }

    /**
     * Generates a hash from the proposition id and value.
     *
     * @return an <code>int</code> hash.
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + this.propositionId.hashCode();
        hash = 17 * hash + this.sqlCode.hashCode();
        return hash;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
    
}
