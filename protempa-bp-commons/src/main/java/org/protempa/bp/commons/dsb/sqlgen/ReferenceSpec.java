package org.protempa.bp.commons.dsb.sqlgen;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import org.arp.javautil.string.StringUtil;
import org.protempa.ProtempaUtil;

/**
 * A 1:N relationship with instances of another entity.
 * 
 * @author Andrew Post
 */
public class ReferenceSpec implements Serializable {
    private static final long serialVersionUID = -2223863541098753792L;
    private final String referenceName;
    private final String entityName;
    private final ColumnSpec[] uniqueIdSpecs;

    /**
     * Instantiates a reference instance with the reference's name, the 
     * right-hand-side entity name and the paths to the tables and columns
     * that form the right-hand-side entity's unique identifier.
     *
     * @param referenceName the name {@link String of ther reference.
     * @param entityName the name {@link String} of the entity being
     * referenced.
     * @param uniqueIdSpecs the {@link ColumnSpec[]} paths through the database
     * from an entity's main table to the tables and columns that together form
     * an unique identifier of the entities being referenced.
     */
    public ReferenceSpec(String referenceName, String entityName,
            ColumnSpec[] uniqueIdSpecs) {
        if (referenceName == null)
            throw new IllegalArgumentException("referenceName cannot be null");
        if (entityName == null)
            throw new IllegalArgumentException("entityName cannot be null");
        if (uniqueIdSpecs == null)
            throw new IllegalArgumentException("uniqueIdSpecs cannot be null");
        this.uniqueIdSpecs = uniqueIdSpecs.clone();
        ProtempaUtil.checkArray(this.uniqueIdSpecs, "uniqueIdSpecs");
        this.referenceName = referenceName;
        this.entityName = entityName;
    }

    /**
     * Gets the paths through the database from an entity's main table to
     * the tables and columns that together form an unique identifier of the
     * entities being referenced.
     *
     * @return a {@link ColumnSpec[]} representing those paths.
     */
    public ColumnSpec[] getUniqueIdSpecs() {
        return uniqueIdSpecs.clone();
    }

    /**
     * Returns the reference's name.
     *
     * @return a reference name {@link String}.
     */
    public String getReferenceName() {
        return this.referenceName;
    }

    /**
     * Returns the right-hand-side entity name.
     *
     * @return an entity name {@link String}.
     */
    public String getEntityName() {
        return entityName;
    }

    @Override
    public String toString() {
        Map<String, Object> fields = new LinkedHashMap<String, Object>();
        fields.put("referenceName", this.referenceName);
        fields.put("entityName", this.entityName);
        fields.put("uniqueIdSpecs", this.uniqueIdSpecs);
        return StringUtil.getToString(getClass(), fields);
    }
}