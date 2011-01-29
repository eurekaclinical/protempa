package org.protempa;

import java.io.Serializable;

public interface DataSourceType extends Serializable {

    public boolean isDerived();

    public String getStringRepresentation();
}
