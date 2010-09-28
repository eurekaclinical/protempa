/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.protempa.proposition;

import java.io.Serializable;

import org.protempa.SourceId;

/**
 * Implements a global unique identifier for {@link Proposition}s. It
 * implements {@link Serializable} so that unique identifiers
 * can be serialized along with {@link Proposition}s.
 *
 * @author Andrew Post
 */
public final class UniqueIdentifier implements Serializable {
    private static final long serialVersionUID = 396847781133676191L;
    private final SourceId sourceId;
    private final LocalUniqueIdentifier localUniqueId;

    /**
     * Copy constructor.
     *
     * @param other another {@link UniqueIdentifier}.
     */
    public UniqueIdentifier(UniqueIdentifier other) {
        if (other == null)
            throw new IllegalArgumentException("other cannot be null");
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
        if (newSourceId == null)
            throw new IllegalArgumentException(
                    "dataSourceBackendId cannot be null");
        if (localUniqueId == null)
            throw new IllegalArgumentException("localUniqueId cannot be null");
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
        if ((this.sourceId == null) ?
            (other.sourceId != null) :
            !this.sourceId.equals(other.sourceId)) {
            return false;
        }
        if (this.localUniqueId != other.localUniqueId &&
                (this.localUniqueId == null ||
                !this.localUniqueId.equals(other.localUniqueId))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.sourceId != null ?
            this.sourceId.hashCode() : 0);
        hash = 79 * hash + (this.localUniqueId != null ?
            this.localUniqueId.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "UniqueIdentifier [dataSourceBackendId=" + sourceId
                + ", localUniqueId=" + localUniqueId + "]";
    }


}
