package org.protempa;

/**
 * Represents a terminology in PROTEMPA
 * 
 * @author Michel Mansour
 */

public final class Terminology {
    private final String name;
    private String description;
    private String version;
    
    private Terminology(String name) {
        this.name = name;
    }
    
    static Terminology withName(String name) {
        return new Terminology(name);
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
}
