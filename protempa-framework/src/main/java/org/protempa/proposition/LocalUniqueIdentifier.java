package org.protempa.proposition;

import java.io.Serializable;

/**
 * For implementations of unique identifier specific to a data source backend
 * or to derived values. It implements {@link Cloneable} because
 * {@link UniqueIdentifier}'s constructor clones the instance of it that is
 * passed in. It implements {@link Serializable} so that unique identifiers
 * can be serialized along with {@link Proposition}s.
 *
 * @author Andrew Post
 */
public interface LocalUniqueIdentifier extends Cloneable, Serializable {

    /**
     * Makes a shallow copy of this instance.
     *
     * @return a {@link LocalUniqueIdentifier} that is equal to this instance.
     */
    LocalUniqueIdentifier clone();
}
