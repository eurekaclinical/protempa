package org.protempa;

public interface DataSourceConstraint {
    String getPropositionId();

    DataSourceConstraint getAnd();
    
    void accept(AbstractDataSourceConstraintVisitor visitor);
}
