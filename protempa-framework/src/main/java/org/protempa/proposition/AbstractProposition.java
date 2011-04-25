package org.protempa.proposition;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.protempa.DataSourceType;
import org.protempa.DataSourceBackendDataSourceType;
import org.protempa.DerivedDataSourceType;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueFactory;
import org.protempa.proposition.value.ValueType;

/**
 * Abstract class for implementing the various kinds of propositions.
 * 
 * @author Andrew Post
 */
public abstract class AbstractProposition implements Proposition {

    private static final int DEFAULT_REFERENCE_LIST_SIZE = 100;
    /**
     * An identification <code>String</code> for this proposition.
     */
    private String id;
    private static volatile int nextHashCode = 17;
    protected volatile int hashCode;
    private PropertyChangeSupport changes;
    private Map<String, Value> properties;
    private Map<String, List<UniqueIdentifier>> references;
    private UniqueIdentifier key;
    private DataSourceType dataSourceType;

    /**
     * Creates a proposition with an id.
     *
     * @param id
     *            an identification <code>String</code> for this proposition.
     */
    AbstractProposition(String id) {
        initializeAbstractProposition(id);
    }

    protected AbstractProposition() {
    }

    protected void initializeAbstractProposition(String id) {
        if (id == null) {
            this.id = "";
        } else {
            this.id = id.intern();
        }
    }

    protected void initializeProperties() {
        if (this.properties == null) {
            this.properties = new HashMap<String, Value>();
        }
    }

    protected void initializeReferences() {
        if (this.references == null) {
            this.references = new HashMap<String, List<UniqueIdentifier>>();
        }
    }

    protected void initializePropertyChangeSupport() {
        this.changes = new PropertyChangeSupport(this);
    }

    @Override
    public String getId() {
        return this.id;
    }

    public final void setProperty(String name, Value value) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        initializeProperties();
        this.properties.put(name.intern(), value);
    }

    @Override
    public final Value getProperty(String name) {
        if (this.properties == null) {
            return null;
        } else {
            return this.properties.get(name);
        }
    }

    @Override
    public final Set<String> getPropertyNames() {
        if (this.properties == null) {
            return Collections.emptySet();
        } else {
            return this.properties.keySet();
        }
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

    private void setReferences(String name, List<UniqueIdentifier> refs) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        initializeReferences();
        this.references.put(name.intern(), refs);
    }

    public final void addReference(String name, UniqueIdentifier ref) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        if (ref == null) {
            throw new IllegalArgumentException("ref cannot be null");
        }
        initializeReferences();
        List<UniqueIdentifier> refs = this.references.get(name);
        if (refs == null) {
            refs =
                    new ArrayList<UniqueIdentifier>(DEFAULT_REFERENCE_LIST_SIZE);
            refs.add(ref);
            this.references.put(name.intern(), refs);
        } else {
            refs.add(ref);
        }
    }

    @Override
    public final List<UniqueIdentifier> getReferences(String name) {
        if (this.references == null) {
            return Collections.emptyList();
        } else {
            List<UniqueIdentifier> result = this.references.get(name);
            if (result != null) {
                return Collections.unmodifiableList(result);
            } else {
                return Collections.emptyList();
            }
        }
    }

    @Override
    public final Set<String> getReferenceNames() {
        if (this.references == null) {
            return Collections.emptySet();
        } else {
            return this.references.keySet();
        }
    }

    @Override
    public final void addPropertyChangeListener(PropertyChangeListener l) {
        initializePropertyChangeSupport();
        this.changes.addPropertyChangeListener(l);
    }

    @Override
    public final void removePropertyChangeListener(PropertyChangeListener l) {
        if (this.changes != null) {
            this.changes.removePropertyChangeListener(l);
        }
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
                && this.properties == p.properties ||
                (this.properties != null && this.properties.equals(p.properties));

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

    protected void writeAbstractProposition(ObjectOutputStream s)
            throws IOException {
        s.writeObject(this.id);

        if (this.properties == null) {
            s.writeInt(0);
        } else {
            Set<String> propertyNames = this.properties.keySet();
            s.writeInt(propertyNames.size());
            for (String propertyName : propertyNames) {
                s.writeObject(propertyName);
                Value val = this.properties.get(propertyName);
                if (val != null) {
                    s.writeObject(val.getType());
                    s.writeObject(val.getRepr());
                } else {
                    s.writeObject(null);
                }
            }
        }

        if (this.references == null) {
            s.writeInt(0);
        } else {
            Set<String> refNames = this.references.keySet();
            s.writeInt(refNames.size());
            for (String refName : refNames) {
                s.writeObject(refName);
                List<UniqueIdentifier> val = this.references.get(refName);
                int valSize = val.size();
                s.writeInt(valSize);
                for (int i = 0; i < valSize; i++) {
                    s.writeObject(val.get(i));
                }
            }
        }

        s.writeObject(this.key);
        if (this.dataSourceType instanceof DerivedDataSourceType) {
            s.writeObject("DERIVED");
        } else {
            s.writeObject("DATABASE");
            s.writeObject(
                    ((DataSourceBackendDataSourceType) this.dataSourceType).getId());
        }
        s.writeObject(this.changes);
    }

    protected void readAbstractProposition(ObjectInputStream s)
            throws IOException, ClassNotFoundException {
        String tempId = (String) s.readObject();
        initializeAbstractProposition(tempId);

        int numProperties = s.readInt();
        if (numProperties < 0) {
            throw new InvalidObjectException(
                    "Negative properties count. Can't restore");
        }
        if (numProperties > 0) {
            initializeProperties();
            for (int i = 0; i < numProperties; i++) {
                String propertyName = (String) s.readObject();
                ValueType valueType = (ValueType) s.readObject();
                String valAsString = valueType != null ? (String) s.readObject() : null;
                Value val = valAsString != null
                        ? ValueFactory.parseRepr(valAsString) : null;
                if (val != null && !valueType.isInstance(val)) {
                    throw new InvalidObjectException("Inconsistent value type and value. Can't restore");
                }
                setProperty(propertyName, val);
            }
        }

        int numRefs = s.readInt();
        if (numRefs < 0) {
            throw new InvalidObjectException(
                    "Negative reference count. Can't restore");
        }
        if (numRefs > 0) {
            initializeReferences();
            for (int i = 0; i < numRefs; i++) {
                String refName = (String) s.readObject();
                int numUids = s.readInt();
                if (numUids < 0) {
                    throw new InvalidObjectException(
                            "Negative unique identifier count. Can't restore");
                }
                List<UniqueIdentifier> uids =
                        new ArrayList<UniqueIdentifier>(numUids);
                for (int j = 0; j < numUids; j++) {
                    uids.add((UniqueIdentifier) s.readObject());
                }
                setReferences(refName, uids);
            }
        }

        setUniqueIdentifier((UniqueIdentifier) s.readObject());
        String dsType = (String) s.readObject();
        if ("DERIVED".equals(dsType)) {
            setDataSourceType(DerivedDataSourceType.getInstance());
        } else {
            setDataSourceType(DataSourceBackendDataSourceType.getInstance(
                    (String) s.readObject()));
        }
        this.changes = (PropertyChangeSupport) s.readObject();
    }
}
