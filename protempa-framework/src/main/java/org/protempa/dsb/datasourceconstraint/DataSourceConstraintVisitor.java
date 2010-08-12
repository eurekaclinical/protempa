package org.protempa.dsb.datasourceconstraint;

/**
 *
 * @author Andrew Post
 */
public interface DataSourceConstraintVisitor {

    void visit(PositionDataSourceConstraint constraint);

    void visit(ThresholdDataSourceConstraint constraint);

    void visitAll(DataSourceConstraint constraints);

}
