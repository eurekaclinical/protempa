package org.protempa.backend.dsb.filter;

/**
 *
 * @author Andrew Post
 */
public interface FilterVisitor {

    void visit(PropertyValueFilter constraint);

    void visit(PositionFilter constraint);

    void visit(ValueFilter constraint);

    void visitAll(Filter constraints);

}
