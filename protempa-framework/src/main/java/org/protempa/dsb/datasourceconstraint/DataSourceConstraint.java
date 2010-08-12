package org.protempa.dsb.datasourceconstraint;

import java.util.Iterator;

public interface DataSourceConstraint {
    String getPropositionId();

    DataSourceConstraint getAnd();
    
    void accept(DataSourceConstraintVisitor visitor);

    Iterator<DataSourceConstraint> andIterator();
}
