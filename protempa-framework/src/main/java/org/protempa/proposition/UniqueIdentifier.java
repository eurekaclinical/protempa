/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.protempa.proposition;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * A global unique identifier for {@link Proposition}s. It
 * implements {@link Serializable} so that unique identifiers
 * can be serialized along with {@link Proposition}s.
 *
 * @author Andrew Post
 */
public final class UniqueIdentifier implements Serializable {

    private static final long serialVersionUID = 396847781133676191L;
    private SourceId sourceId;
    private LocalUniqueIdentifier localUniqueId;
    private transient volatile int hashCode;

    /**
     * Copy constructor.
     *
     * @param other another {@link UniqueIdentifier}.
     */
    public UniqueIdentifier(UniqueIdentifier other) {
        if (other == null) {
            throw new IllegalArgumentException("other cannot be null");
        }
        this.sourceId = other.sourceId;
        this.localUniqueId = other.localUniqueId;
    }

    /**
     * Instantiates a new unique identifier.
     *
     * @param dataSourceBackendId the data source backend it from which the
     * {@link Proposition} came. Cannot be <code>null</code>.
     * @param localUniqueId the {@link LocalUniqueIdentifier}, a data source
     * backend-specific unique id for a {@link Proposition}.
     */
    public UniqueIdentifier(SourceId newSourceId,
            LocalUniqueIdentifier localUniqueId) {
        if (newSourceId == null) {
            throw new IllegalArgumentException(
                    "dataSourceBackendId cannot be null");
        }
        if (localUniqueId == null) {
            throw new IllegalArgumentException("localUniqueId cannot be null");
        }
        this.sourceId = newSourceId;
        this.localUniqueId = localUniqueId.clone();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UniqueIdentifier other = (UniqueIdentifier) obj;
        if (!this.sourceId.equals(other.sourceId)) {
            return false;
        }
        if (!this.localUniqueId.equals(other.localUniqueId)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        if (this.hashCode == 0) {
            int result = 17;
            /*
             * We weight localUniqueId higher because it is the primary source
             * of variability (there only will be a few different source ids
             * being used at a time).
             */
            result = 37 * result + this.localUniqueId.hashCode();
            result = 104743 * result + this.sourceId.hashCode();
            this.hashCode = result;
        }
        return this.hashCode;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        boolean derived = (this.sourceId instanceof DerivedSourceId);
        if (derived) {
            s.writeObject("DERIVED");
        } else {
            s.writeObject("DATASOURCEBACKEND");
        }
        if (!derived) {
            s.writeObject(this.sourceId.getId());
        }
        s.writeObject(this.localUniqueId);
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        String type = (String) s.readObject();
        if ("DERIVED".equals(type)) {
            this.sourceId = DerivedSourceId.getInstance();
        } else if ("DATASOURCEBACKEND".equals(type)) {
            String id = (String) s.readObject();
            this.sourceId = DataSourceBackendId.getInstance(id);
        } else {
            throw new InvalidObjectException("Invalid source id type " + type +
                    ". Can't restore");
        }
        this.localUniqueId = (LocalUniqueIdentifier) s.readObject();
    }
}
