package org.protempa.proposition;

import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    protected volatile int hashCode = 0;
    protected final PropertyChangeSupport changes;
    private final Map<String, Value> properties;
    private final Map<String, List<Value>> references;
    private String datasourceBackendId;
    private Object key;

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
            this.id = id;
        }
        this.changes = new PropertyChangeSupport(this);
        this.properties = new HashMap<String, Value>();
        this.references = new HashMap<String, List<Value>>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.virginia.pbhs.parameters.Proposition#getId()
     */
    public String getId() {
        return this.id;
    }

    public final void setProperty(String name, Value value) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        this.properties.put(name, value);
    }

    public final void setReferences(String name, List<Value> values) {
        this.references.put(name, values);
    }

    public final Value getProperty(String name) {
        return this.properties.get(name);
    }

    public final List<Value> getReferences(String name) {
        return this.references.get(name);
    }

    public final Set<String> propertyNames() {
        return this.properties.keySet();
    }

    public final String getDataSourceBackendId() {
        return this.datasourceBackendId;
    }

    public final void setDataSourceBackendId(String id) {
        this.hashCode = 0;
        this.datasourceBackendId = id;
    }

    public final void setUniqueIdentifier(Object o) {
        this.hashCode = 0;
        this.key = o;
    }

    @Override
    public final Object getUniqueIdentifier() {
        return this.key;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
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

    /*
     * (non-Javadoc)
     * 
     * @see org.protempa.proposition.Proposition#isEqual(java.lang.Object)
     */
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
}
