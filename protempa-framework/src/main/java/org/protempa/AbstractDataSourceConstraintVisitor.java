package org.protempa;

/**
 *
 * @author Andrew Post
 */
public abstract class AbstractDataSourceConstraintVisitor {

    public void visitAll(DataSourceConstraint constraints) {
        while (constraints != null) {
            constraints.accept(this);
            constraints = constraints.getAnd();
        }
    }

    public void visit(PositionDataSourceConstraint constraint) {
        
    }

    public void visit(ThresholdDataSourceConstraint constraint) {

    }
}
