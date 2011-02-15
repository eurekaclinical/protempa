package org.protempa.proposition;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.protempa.DataSourceType;
import org.protempa.proposition.value.Value;

/**
 * Abstract class for implementing the various kinds of propositions.
 * 
 * @author Andrew Post
 */
public abstract class AbstractProposition implements Proposition {

    private static final long serialVersionUID = -6210974161591587259L;
    /**
     * An identification <code>String</code> for this proposition.
     */
    private final String id;
    private static volatile int nextHashCode = 17;
    protected transient volatile int hashCode;
    protected final PropertyChangeSupport changes;
    private final Map<String, Value> properties;
    private final Map<String, List<UniqueIdentifier>> references;
    private UniqueIdentifier key;
    private DataSourceType dataSourceType;

    /**
     * Creates a proposition with an id.
     *
     * @param id
     *            an identification <code>String</code> for this proposition.
     */
    AbstractProposition(String id) {
        if (id == null) {
            this.id = "";
        } else {
            this.id = id.intern();
        }
        this.changes = new PropertyChangeSupport(this);
        this.properties = new HashMap<String, Value>();
        this.references = new HashMap<String, List<UniqueIdentifier>>();
    }

    @Override
    public String getId() {
        return this.id;
    }

    public final void setProperty(String name, Value value) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        this.properties.put(name.intern(), value);
    }

    @Override
    public final Value getProperty(String name) {
        return this.properties.get(name);
    }

    @Override
    public final Set<String> getPropertyNames() {
        return this.properties.keySet();
    }

    public final void setUniqueIdentifier(UniqueIdentifier o) {
        this.key = o;
    }

    @Override
    public DataSourceType getDataSourceType() {
        return this.dataSourceType;
    }

    public void setDataSourceType(DataSourceType type) {
        this.dataSourceType = type;
    }

    @Override
    public final UniqueIdentifier getUniqueIdentifier() {
        return this.key;
    }

    public final void setReferences(String name, List<UniqueIdentifier> refs) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        if (refs != null) {
            refs = new ArrayList<UniqueIdentifier>(refs);
        }
        this.references.put(name.intern(), refs);
    }

    public final void addReference(String name, UniqueIdentifier ref) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        if (ref == null) {
            throw new IllegalArgumentException("ref cannot be null");
        }
        List<UniqueIdentifier> refs = this.references.get(name);
        if (refs == null) {
            refs = new ArrayList<UniqueIdentifier>();
            refs.add(ref);
            this.references.put(name.intern(), refs);
        } else {
            refs.add(ref);
        }
    }

    @Override
    public final List<UniqueIdentifier> getReferences(String name) {
        List<UniqueIdentifier> result = this.references.get(name);
        if (result != null) {
            return Collections.unmodifiableList(result);
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public final Set<String> getReferenceNames() {
        return this.references.keySet();
    }

    @Override
    public final void addPropertyChangeListener(PropertyChangeListener l) {
        this.changes.addPropertyChangeListener(l);
    }

    @Override
    public final void removePropertyChangeListener(PropertyChangeListener l) {
        this.changes.removePropertyChangeListener(l);
    }

    @Override
    public int hashCode() {
        if (this.hashCode == 0) {
            this.hashCode = nextHashCode;
            nextHashCode *= 37;
        }
        return this.hashCode;
    }

    @Override
    public boolean isEqual(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof AbstractProposition)) {
            return false;
        }

        AbstractProposition p = (AbstractProposition) other;
        return (id == p.id || id.equals(p.id))
                && this.properties.equals(p.properties);

    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", this.id).append("properties", this.properties).append("references", this.references).append("uniqueIdentifier", this.key).append("dataSourceType", this.dataSourceType).toString();

    }
    // The following code implements hashCode() and equals() using unique 
    // identifiers, as well as the datasource backend identifiers.
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    /*
    @Override
    public int hashCode() {
    if (this.hashCode == 0) {
    if (this.key == null
    || this.datasourceBackendId == null) {
    this.hashCode = super.hashCode();
    } else {
    this.hashCode = 17;
    this.hashCode += 37 * this.key.hashCode();
    this.hashCode += 37 * this.datasourceBackendId.hashCode();
    }
    }
    return this.hashCode;
    }

    @Override
    public boolean equals(Object obj) {
    if (this == obj) {
    return true;
    } else {
    if (!(obj instanceof Proposition)) {
    return false;
    }
    Proposition prop = (Proposition) obj;
    if (prop.getUniqueIdentifier() == null
    || this.key == null
    || prop.getDataSourceBackendId() == null
    || this.datasourceBackendId == null) {
    return false;
    } else {
    return prop.getUniqueIdentifier().equals(
    this.key)
    && prop.getDataSourceBackendId().equals(
    this.datasourceBackendId);
    }
    }
    }
     */
}
