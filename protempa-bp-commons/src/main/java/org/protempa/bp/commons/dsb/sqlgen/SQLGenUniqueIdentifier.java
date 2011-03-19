package org.protempa.bp.commons.dsb.sqlgen;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.protempa.proposition.LocalUniqueIdentifier;

final class SQLGenUniqueIdentifier implements LocalUniqueIdentifier {

    private static final long serialVersionUID = 3956023315666447630L;
    private String name;
    private String[] dbIds;
    private volatile int hashCode;

    SQLGenUniqueIdentifier(String name, String[] dbIds) {
        assert name != null : "name cannot be null";
        assert dbIds != null : "dbIds cannot be null";
        assert !ArrayUtils.contains(dbIds, null) :
            "dbIds cannot contain a null element";
        this.name = name;
        this.dbIds = dbIds.clone();
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
        if (!this.name.equals(other.name)) {
            return false;
        }
        if (!Arrays.equals(this.dbIds, other.dbIds)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        if (this.hashCode == 0) {
            int hash = 3;
            hash = 53 * hash + this.name.hashCode();
            hash = 53 * hash + Arrays.hashCode(this.dbIds);
            this.hashCode = hash;
        }
        return this.hashCode;
    }

    @Override
    public LocalUniqueIdentifier clone() {
        SQLGenUniqueIdentifier result;
        try {
            result = (SQLGenUniqueIdentifier) super.clone();
            result.dbIds = this.dbIds;
            return result;
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError("Never reached!");
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.writeObject(this.name);
        s.writeInt(this.dbIds.length);
        for (String dbId : this.dbIds) {
            s.writeObject(dbId);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException,
            ClassNotFoundException {
        this.name = (String) s.readObject();
        if (this.name == null) {
            throw new InvalidObjectException(
                    "name cannot be null. Can't restore");
        }
        int dbIdsLen = s.readInt();
        if (dbIdsLen < 0) {
            throw new InvalidObjectException("dbIds length invalid (" +
                    dbIdsLen + "). Can't restore");
        }
        this.dbIds = new String[dbIdsLen];
        for (int i = 0; i < dbIdsLen; i++) {
            String dbId = (String) s.readObject();
            if (dbId == null) {
                throw new InvalidObjectException(
                        "dbIds cannot contain a null value. Can't restore");
            }
            this.dbIds[i] = dbId;
        }
    }
}
