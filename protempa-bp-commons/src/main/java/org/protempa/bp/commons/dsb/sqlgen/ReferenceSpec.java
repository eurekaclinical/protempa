package org.protempa.bp.commons.dsb.sqlgen;

/**
 *
 * @author Andrew Post
 */
public class ReferenceSpec {
    private String propId;
    private String key;

    public ReferenceSpec(String propId, String key) {
        if (propId == null)
            throw new IllegalArgumentException("propId cannot be null");
        if (key == null)
            throw new IllegalArgumentException("key cannot be null");
        this.propId = propId;
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String getPropId() {
        return propId;
    }
}
