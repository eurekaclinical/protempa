package org.protempa.proposition;

/*-
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2018 Emory University
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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.protempa.SourceSystem;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.visitor.AbstractPropositionVisitor;

/**
 * Makes an exact copy of a proposition, including the original proposition's
 * unique id. To copy a proposition, pass an instance of this class into 
 * {@link Proposition#accept(org.protempa.proposition.visitor.PropositionVisitor) }
 * and call {@link #getCopy() }.
 *
 * You may override the copy's field values by setting the corresponding fields
 * of this class (e.g., <code>deleteDate</code>). The default behavior will use
 * the original proposition's values for all fields.
 *
 * @author Andrew Post
 */
public final class CopyPropositionVisitor extends AbstractPropositionVisitor {

    private Proposition theCopy;
    private Map<String, Value> properties;
    private Map<String, List<UniqueId>> references;
    private UniqueId uniqueId; // not final because of custom deserialization
    // but there is no public modification access
    private SourceSystem sourceSystem;
    private Date downloadDate;
    private Date createDate;
    private Date updateDate;
    private Date deleteDate;

    /**
     * Returns the properties for copies made with this visitor.
     *
     * @return a map of property name to value pairs, or <code>null</code>. A
     * value of <code>null</code> means that this visitor is using the original
     * proposition's value for this field.
     */
    public Map<String, Value> getProperties() {
        return new HashMap<>(properties);
    }

    /**
     * Overrides the properties for copies made with this visitor.
     *
     * @param properties a map of property name to value pairs, or
     * <code>null</code> to use the original proposition's value for this field.
     */
    public void setProperties(Map<String, Value> properties) {
        if (properties != null) {
            this.properties = new HashMap<>(properties);
        } else {
            this.properties = null;
        }
    }

    /**
     * Returns the references for copies made with this visitor.
     *
     * @return a map of reference name to reference pairs, or <code>null</code>.
     * A value of <code>null</code> means that this visitor is using the
     * original proposition's value for this field.
     */
    public Map<String, List<UniqueId>> getReferences() {
        return new HashMap<>(references);
    }

    /**
     * Overrides the references for copies made with this visitor.
     *
     * @param references a map of reference name to reference pairs, or
     * <code>null</code> to use the original proposition's value for this field.
     */
    public void setReferences(Map<String, List<UniqueId>> references) {
        if (references != null) {
            this.references = new HashMap<>(references);
        } else {
            this.references = null;
        }
    }

    /**
     * Returns the unique id for copies made with this visitor.
     *
     * @return an unique id, or <code>null</code>. A value of <code>null</code>
     * means that this visitor is using the original proposition's value for
     * this field.
     */
    public UniqueId getUniqueId() {
        return uniqueId;
    }

    /**
     * Overrides the unique id for copies made with this visitor.
     *
     * @param uniqueId an unique id, or <code>null</code> to use the original
     * proposition's value for this field.
     */
    public void setUniqueId(UniqueId uniqueId) {
        this.uniqueId = uniqueId;
    }

    /**
     * Returns the source system for copies made with this visitor.
     *
     * @return a source system, or <code>null</code>. A value of
     * <code>null</code> means that this visitor is using the original
     * proposition's value for this field.
     */
    public SourceSystem getSourceSystem() {
        return sourceSystem;
    }

    /**
     * Overrides the source system for copies made with this visitor.
     *
     * @param sourceSystem a source system, or <code>null</code> to use the
     * original proposition's value for this field.
     */
    public void setSourceSystem(SourceSystem sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

    /**
     * Returns the download date for copies made with this visitor.
     *
     * @return a date, or <code>null</code>. A value of <code>null</code> means
     * that this visitor is using the original proposition's value for this
     * field.
     */
    public Date getDownloadDate() {
        return downloadDate;
    }

    /**
     * Overrides the download date for copies made with this visitor.
     *
     * @param downloadDate a date, or <code>null</code> to use the original
     * proposition's value for this field.
     */
    public void setDownloadDate(Date downloadDate) {
        this.downloadDate = downloadDate;
    }

    /**
     * Returns the create date for copies made with this visitor.
     *
     * @return a date, or <code>null</code>. A value of <code>null</code> means
     * that this visitor is using the original proposition's value for this
     * field.
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * Overrides the create date for copies made with this visitor.
     *
     * @param createDate a date, or <code>null</code> to use the original
     * proposition's value for this field.
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * Returns the update date for copies made with this visitor.
     *
     * @return a date, or <code>null</code>. A value of <code>null</code> means
     * that this visitor is using the original proposition's value for this
     * field.
     */
    public Date getUpdateDate() {
        return updateDate;
    }

    /**
     * Overrides the update date for copies made with this visitor.
     *
     * @param updateDate a date, or <code>null</code> to use the original
     * proposition's value for this field.
     */
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    /**
     * Returns the delete date for copies made with this visitor.
     *
     * @return a date, or <code>null</code>. A value of <code>null</code> means
     * that this visitor is using the original proposition's value for this
     * field.
     */
    public Date getDeleteDate() {
        return deleteDate;
    }

    /**
     * Overrides the delete date for copies made with this visitor.
     *
     * @param deleteDate a date, or <code>null</code> to use the original
     * proposition's value for this field.
     */
    public void setDeleteDate(Date deleteDate) {
        this.deleteDate = deleteDate;
    }

    /**
     * Copies a {@link Context} proposition. Retrieve it by subsequently calling {@link #getCopy()
     * }.
     *
     * @param context the context to copy. Cannot be <code>null</code>.
     */
    @Override
    public void visit(Context context) {
        Context copy = new Context(context.getId(), this.uniqueId != null ? this.uniqueId : context.getUniqueId());
        copy.setCreateDate(this.createDate != null ? this.createDate : context.getCreateDate());
        copy.setUpdateDate(this.updateDate != null ? this.updateDate : context.getUpdateDate());
        copy.setDeleteDate(this.deleteDate != null ? this.deleteDate : context.getDeleteDate());
        copy.setDownloadDate(this.downloadDate != null ? this.downloadDate : context.getDownloadDate());
        copy.setInterval(context.getInterval());
        copy.setSourceSystem(this.sourceSystem != null ? this.sourceSystem : context.getSourceSystem());
        if (this.references != null) {
            for (String refName : this.references.keySet()) {
                copy.setReferences(refName, this.references.get(refName));
            }
        } else {
            for (String refName : context.getReferenceNames()) {
                copy.setReferences(refName, context.getReferences(refName));
            }
        }
        if (this.properties != null) {
            for (String propName : this.properties.keySet()) {
                copy.setProperty(propName, this.properties.get(propName));
            }
        } else {
            for (String propName : context.getPropertyNames()) {
                copy.setProperty(propName, context.getProperty(propName));
            }
        }
        this.theCopy = copy;
    }

    /**
     * Copies a {@link Constant} proposition. Retrieve it by subsequently
     * calling {@link #getCopy() }.
     *
     * @param constant the constant to copy. Cannot be <code>null</code>.
     */
    @Override
    public void visit(Constant constant) {
        Constant copy = new Constant(constant.getId(), this.uniqueId != null ? this.uniqueId : constant.getUniqueId());
        copy.setCreateDate(this.createDate != null ? this.createDate : constant.getCreateDate());
        copy.setUpdateDate(this.updateDate != null ? this.updateDate : constant.getUpdateDate());
        copy.setDeleteDate(this.deleteDate != null ? this.deleteDate : constant.getDeleteDate());
        copy.setDownloadDate(this.downloadDate != null ? this.downloadDate : constant.getDownloadDate());
        copy.setSourceSystem(this.sourceSystem != null ? this.sourceSystem : constant.getSourceSystem());
        if (this.references != null) {
            for (String refName : this.references.keySet()) {
                copy.setReferences(refName, this.references.get(refName));
            }
        } else {
            for (String refName : constant.getReferenceNames()) {
                copy.setReferences(refName, constant.getReferences(refName));
            }
        }
        if (this.properties != null) {
            for (String propName : this.properties.keySet()) {
                copy.setProperty(propName, this.properties.get(propName));
            }
        } else {
            for (String propName : constant.getPropertyNames()) {
                copy.setProperty(propName, constant.getProperty(propName));
            }
        }
        this.theCopy = copy;
    }

    /**
     * Copies a {@link PrimitiveParameter} proposition. Retrieve it by
     * subsequently calling {@link #getCopy() }.
     *
     * @param primitiveParameter the primitive parameter to copy. Cannot be
     * <code>null</code>.
     */
    @Override
    public void visit(PrimitiveParameter primitiveParameter) {
        PrimitiveParameter copy = new PrimitiveParameter(primitiveParameter.getId(), this.uniqueId != null ? this.uniqueId : primitiveParameter.getUniqueId());
        copy.setCreateDate(this.createDate != null ? this.createDate : primitiveParameter.getCreateDate());
        copy.setUpdateDate(this.updateDate != null ? this.updateDate : primitiveParameter.getUpdateDate());
        copy.setDeleteDate(this.deleteDate != null ? this.deleteDate : primitiveParameter.getDeleteDate());
        copy.setDownloadDate(this.downloadDate != null ? this.downloadDate : primitiveParameter.getDownloadDate());
        copy.setInterval(primitiveParameter.getInterval());
        copy.setSourceSystem(this.sourceSystem != null ? this.sourceSystem : primitiveParameter.getSourceSystem());
        if (this.references != null) {
            for (String refName : this.references.keySet()) {
                copy.setReferences(refName, this.references.get(refName));
            }
        } else {
            for (String refName : primitiveParameter.getReferenceNames()) {
                copy.setReferences(refName, primitiveParameter.getReferences(refName));
            }
        }
        if (this.properties != null) {
            for (String propName : this.properties.keySet()) {
                copy.setProperty(propName, this.properties.get(propName));
            }
        } else {
            for (String propName : primitiveParameter.getPropertyNames()) {
                copy.setProperty(propName, primitiveParameter.getProperty(propName));
            }
        }
        copy.setValue(primitiveParameter.getValue());
        this.theCopy = copy;
    }

    /**
     * Copies a {@link Event} proposition. Retrieve it by subsequently calling {@link #getCopy()
     * }.
     *
     * @param event the event to copy. Cannot be <code>null</code>.
     */
    @Override
    public void visit(Event event) {
        Event copy = new Event(event.getId(), this.uniqueId != null ? this.uniqueId : event.getUniqueId());
        copy.setCreateDate(this.createDate != null ? this.createDate : event.getCreateDate());
        copy.setUpdateDate(this.updateDate != null ? this.updateDate : event.getUpdateDate());
        copy.setDeleteDate(this.deleteDate != null ? this.deleteDate : event.getDeleteDate());
        copy.setDownloadDate(this.downloadDate != null ? this.downloadDate : event.getDownloadDate());
        copy.setInterval(event.getInterval());
        copy.setSourceSystem(this.sourceSystem != null ? this.sourceSystem : event.getSourceSystem());
        if (this.references != null) {
            for (String refName : this.references.keySet()) {
                copy.setReferences(refName, this.references.get(refName));
            }
        } else {
            for (String refName : event.getReferenceNames()) {
                copy.setReferences(refName, event.getReferences(refName));
            }
        }
        if (this.properties != null) {
            for (String propName : this.properties.keySet()) {
                copy.setProperty(propName, this.properties.get(propName));
            }
        } else {
            for (String propName : event.getPropertyNames()) {
                copy.setProperty(propName, event.getProperty(propName));
            }
        }
        this.theCopy = copy;
    }

    /**
     * Copies an {@link AbstractParameter} proposition. Retrieve it by
     * subsequently calling {@link #getCopy() }.
     *
     * @param abstractParameter the abstract parameter to copy. Cannot be
     * <code>null</code>.
     */
    @Override
    public void visit(AbstractParameter abstractParameter) {
        AbstractParameter copy = new AbstractParameter(abstractParameter.getId(), this.uniqueId != null ? this.uniqueId : abstractParameter.getUniqueId());
        copy.setCreateDate(this.createDate != null ? this.createDate : abstractParameter.getCreateDate());
        copy.setUpdateDate(this.updateDate != null ? this.updateDate : abstractParameter.getUpdateDate());
        copy.setDeleteDate(this.deleteDate != null ? this.deleteDate : abstractParameter.getDeleteDate());
        copy.setDownloadDate(this.downloadDate != null ? this.downloadDate : abstractParameter.getDownloadDate());
        copy.setInterval(abstractParameter.getInterval());
        copy.setSourceSystem(this.sourceSystem != null ? this.sourceSystem : abstractParameter.getSourceSystem());
        if (this.references != null) {
            for (String refName : this.references.keySet()) {
                copy.setReferences(refName, this.references.get(refName));
            }
        } else {
            for (String refName : abstractParameter.getReferenceNames()) {
                copy.setReferences(refName, abstractParameter.getReferences(refName));
            }
        }
        if (this.properties != null) {
            for (String propName : this.properties.keySet()) {
                copy.setProperty(propName, this.properties.get(propName));
            }
        } else {
            for (String propName : abstractParameter.getPropertyNames()) {
                copy.setProperty(propName, abstractParameter.getProperty(propName));
            }
        }
        copy.setValue(abstractParameter.getValue());
        this.theCopy = copy;
    }

    /**
     * Gets the stored copy.
     *
     * @return a proposition.
     */
    public Proposition getCopy() {
        return this.theCopy;
    }

    /**
     * Clears the stored copy.
     */
    public void clear() {
        this.theCopy = null;
    }

}
