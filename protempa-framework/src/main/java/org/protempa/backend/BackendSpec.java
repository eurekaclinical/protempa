package org.protempa.backend;

import java.util.List;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 *
 * @author Andrew Post
 */
public final class BackendSpec<B extends Backend> {
    private BackendProvider backendProvider;
    private String id;
    private String displayName;
    private List<BackendPropertySpec> propertySpecs;

    /**
     *
     * @param backendProvider
     * @param id a unique id, cannot have the | character.
     * @param displayName
     * @param propertySpecs
     */
    public BackendSpec(BackendProvider backendProvider, String id,
            String displayName,
            List<BackendPropertySpec> propertySpecs) {
       if (backendProvider == null)
           throw new IllegalArgumentException("backendProvider cannot be null");
       if (id == null)
           throw new IllegalArgumentException("id cannot be null");
       if (id.contains("|"))
           throw new IllegalArgumentException("id cannot have the | character");
       this.id = id;
       this.backendProvider = backendProvider;
       this.displayName = displayName;
       this.propertySpecs = propertySpecs;
    }

    public String getId() {
        return this.id;
    }

    public BackendProvider getBackendProvider() {
        return this.backendProvider;
    }
    
    public String getDisplayName() {
        return this.displayName;
    }

    public BackendInstanceSpec<B> newBackendInstanceSpec() {
         return new BackendInstanceSpec<B>(this, propertySpecs);
    }

    B newBackendInstance() throws BackendNewInstanceException {
        return (B) this.backendProvider.newInstance(id);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
