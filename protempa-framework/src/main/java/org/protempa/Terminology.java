package org.protempa;

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
}
