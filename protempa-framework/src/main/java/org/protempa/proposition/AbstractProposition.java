/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2013 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.protempa.proposition;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import org.protempa.DataSourceType;
import org.protempa.proposition.value.Value;

/**
 * Abstract class for implementing the various kinds of propositions.
 *
 * @author Andrew Post
 */
public abstract class AbstractProposition implements Proposition {

    private static final int DEFAULT_REFERENCE_LIST_SIZE = 100;
    private static final DataSourceType DEFAULT_DATA_SOURCE_TYPE =
            DataSourceType.UNKNOWN;
    /**
     * An identification
     * <code>String</code> for this proposition.
     */
    private String id;
    private PropertyChangeSupport changes;
    private Map<String, Value> properties;
    private Map<String, List<UniqueId>> references;
    private UniqueId uniqueId; // not final because of custom deserialization
    // but there is no public modification access
    private DataSourceType dataSourceType;

    /**
     * Creates a proposition with an id and unique identifier. The unique
     * identifier cannot be null.
     *
     * @param id an identification <code>String</code> for this proposition.
     */
    AbstractProposition(String id, UniqueId uniqueId) {
        this();
        if (uniqueId == null) {
            throw new IllegalArgumentException("The unique ID cannot be null");
        }
        initializeAbstractProposition(id, uniqueId);
    }

    /**
     * Here only for use by deserialization of {@link java.util.Serializable}
     * subclasses of this class. Do not call this for any other reason because
     * id and uniqueId are left uninitialized! Subclasses that are serializable
     * should implement a
     * <code>readObject</code> method that calls {@link #readAbstractProposition(java.io.ObjectInputStream)
     * }.
     */
    protected AbstractProposition() {
        this.dataSourceType = DEFAULT_DATA_SOURCE_TYPE;
    }

    /**
     * Assigns fields specified in the constructor. This is pulled into a method
     * so that deserialization can initialize those fields correctly.
     *
     * @param id the proposition id.
     * @param uniqueId the proposition's unique id.
     */
    protected final void initializeAbstractProposition(String id,
            UniqueId uniqueId) {
        this.uniqueId = uniqueId;
        if (id == null) {
            this.id = "";
        } else {
            this.id = id.intern();
        }
    }

    protected void initializeProperties() {
        if (this.properties == null) {
            this.properties = new LinkedHashMap<String, Value>();
        }
    }

    protected void initializeReferences() {
        if (this.references == null) {
            this.references = new LinkedHashMap<String, List<UniqueId>>();
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
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        } else {
            if (this.properties == null) {
                return null;
            } else {
                return this.properties.get(name);
            }
        }
    }

    @Override
    public final String[] getPropertyNames() {
        if (this.properties == null) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        } else {
            Set<String> propNames = this.properties.keySet();
            return propNames.toArray(new String[propNames.size()]);
        }
    }

    @Override
    public DataSourceType getDataSourceType() {
        return this.dataSourceType;
    }

    public void setDataSourceType(DataSourceType type) {
        if (type != null) {
            this.dataSourceType = type;
        } else {
            this.dataSourceType = DEFAULT_DATA_SOURCE_TYPE;
        }
    }

    @Override
    public final UniqueId getUniqueId() {
        return this.uniqueId;
    }

    public final void setReferences(String name, List<UniqueId> refs) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        initializeReferences();
        this.references.put(name.intern(), new ArrayList<UniqueId>(refs));
    }

    public final void addReference(String name, UniqueId ref) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        if (ref == null) {
            throw new IllegalArgumentException("ref cannot be null");
        }
        initializeReferences();
        List<UniqueId> refs = this.references.get(name);
        if (refs == null) {
            refs = new ArrayList<UniqueId>(DEFAULT_REFERENCE_LIST_SIZE);
            refs.add(ref);
            this.references.put(name.intern(), refs);
        } else {
            refs.add(ref);
        }
    }

    @Override
    public final List<UniqueId> getReferences(String name) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        if (this.references == null) {
            return Collections.emptyList();
        } else {
            List<UniqueId> result = this.references.get(name);
            if (result != null) {
                return Collections.unmodifiableList(result);
            } else {
                return Collections.emptyList();
            }
        }
    }

    @Override
    public final String[] getReferenceNames() {
        if (this.references == null) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        } else {
            Set<String> refNames = this.references.keySet();
            return refNames.toArray(new String[refNames.size()]);
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
        return this.uniqueId.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof AbstractProposition) {
            AbstractProposition other = (AbstractProposition) o;
            return this.uniqueId.equals(other.uniqueId);
        } else {
            return false;
        }
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
                && this.properties == p.properties
                || (this.properties != null && this.properties
                .equals(p.properties));

    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.reflectionToString(this);

    }

    // The following code implements hashCode() and equals() using unique
    // identifiers, as well as the datasource backend identifiers.
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    /*
     * @Override public int hashCode() { if (this.hashCode == 0) { if (this.key
     * == null || this.datasourceBackendId == null) { this.hashCode =
     * super.hashCode(); } else { this.hashCode = 17; this.hashCode += 37 *
     * this.key.hashCode(); this.hashCode += 37 *
     * this.datasourceBackendId.hashCode(); } } return this.hashCode; }
     * 
     * @Override public boolean equals(Object obj) { if (this == obj) { return
     * true; } else { if (!(obj instanceof Proposition)) { return false; }
     * Proposition prop = (Proposition) obj; if (prop.getUniqueIdentifier() ==
     * null || this.key == null || prop.getDataSourceBackendId() == null ||
     * this.datasourceBackendId == null) { return false; } else { return
     * prop.getUniqueIdentifier().equals( this.key) &&
     * prop.getDataSourceBackendId().equals( this.datasourceBackendId); } } }
     */
    protected void writeAbstractProposition(ObjectOutputStream s)
            throws IOException {
        s.writeObject(this.id);
        s.writeObject(this.uniqueId);

        if (this.properties == null) {
            s.writeInt(0);
        } else {
            s.writeInt(this.properties.size());
            for (Map.Entry<String, Value> me : this.properties.entrySet()) {
                String propertyName = me.getKey();
                Value val = me.getValue();
                s.writeObject(propertyName);
                s.writeObject(val);
            }
        }

        if (this.references == null) {
            s.writeInt(0);
        } else {
            s.writeInt(this.references.size());
            for (Map.Entry<String, List<UniqueId>> me : this.references
                    .entrySet()) {
                s.writeObject(me.getKey());
                List<UniqueId> val = me.getValue();
                if (val == null) {
                    s.writeInt(0);
                } else {
                    s.writeInt(val.size());
                    for (UniqueId uid : val) {
                        s.writeObject(uid);
                    }
                }
            }
        }

        s.writeObject(this.dataSourceType);
        s.writeObject(this.changes);
    }

    protected void readAbstractProposition(ObjectInputStream s)
            throws IOException, ClassNotFoundException {
        String tempId = (String) s.readObject();
        UniqueId tempUniqueId = (UniqueId) s.readObject();
        if (tempUniqueId == null) {
            throw new InvalidObjectException(
                    "Can't restore. All propositions must have an unique id");
        }
        initializeAbstractProposition(tempId, tempUniqueId);

        int numProperties = s.readInt();
        if (numProperties < 0) {
            throw new InvalidObjectException(
                    "Negative properties count. Can't restore");
        }
        if (numProperties > 0) {
            for (int i = 0; i < numProperties; i++) {
                String propertyName = (String) s.readObject();
                Value val = (Value) s.readObject();
                if (val != null) {
                    val = val.replace();
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
            for (int i = 0; i < numRefs; i++) {
                String refName = (String) s.readObject();
                int numUids = s.readInt();
                if (numUids < 0) {
                    throw new InvalidObjectException(
                            "Negative unique identifier count. Can't restore");
                }
                List<UniqueId> uids = new ArrayList<UniqueId>(numUids);
                for (int j = 0; j < numUids; j++) {
                    uids.add((UniqueId) s.readObject());
                }
                setReferences(refName, uids);
            }
        }
        setDataSourceType((DataSourceType) s.readObject());
        this.changes = (PropertyChangeSupport) s.readObject();
    }
}
