package org.protempa.bp.commons.dsb.sqlgen;

/**
 *
 * @author Andrew Post
 */
public class EntitySpec {
    private String name;
    private ColumnSpec keySpec;

    public EntitySpec(String name, ColumnSpec keySpec) {
        this.name = name;
        this.keySpec = keySpec;
    }

    public ColumnSpec getKeySpec() {
        return keySpec;
    }

    public String getName() {
        return name;
    }

    
}
