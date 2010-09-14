package org.protempa.bp.commons.dsb.sqlgen;

import java.util.Arrays;
import org.protempa.proposition.LocalUniqueIdentifier;

class SQLGenUniqueIdentifier implements LocalUniqueIdentifier {

    private static final long serialVersionUID = 3956023315666447630L;
    final String name;
    String[] dbIds;

    SQLGenUniqueIdentifier(String name, String[] dbIds) {
        this.name = name;
        this.dbIds = dbIds;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SQLGenUniqueIdentifier other = (SQLGenUniqueIdentifier) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if (!Arrays.deepEquals(this.dbIds, other.dbIds)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 53 * hash + Arrays.deepHashCode(this.dbIds);
        return hash;
    }

    @Override
    public LocalUniqueIdentifier clone() {
        SQLGenUniqueIdentifier result;
        try {
            result = (SQLGenUniqueIdentifier) super.clone();
            result.dbIds = this.dbIds.clone();
            return result;
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError("Never reached!");
        }
    }
}
