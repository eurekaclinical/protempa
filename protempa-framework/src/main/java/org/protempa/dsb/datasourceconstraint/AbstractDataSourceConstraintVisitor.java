package org.protempa.dsb.datasourceconstraint;

import java.util.Iterator;

/**
 *
 * @author Andrew Post
 */
public abstract class AbstractDataSourceConstraintVisitor
        implements DataSourceConstraintVisitor {

    public void visitAll(DataSourceConstraint constraints) {
        if (constraints != null) {
            for (Iterator<DataSourceConstraint> itr =
                    constraints.andIterator(); itr.hasNext();) {
                constraints.accept(this);
            }
        }
    }

    public void visit(PositionDataSourceConstraint constraint) {
        
    }

    public void visit(ThresholdDataSourceConstraint constraint) {

    }
}
