package org.protempa.backend.dsb.filter;

import java.util.Iterator;

/**
 *
 * @author Andrew Post
 */
public abstract class AbstractFilterVisitor
        implements FilterVisitor {

    @Override
    public void visitAll(Filter filters) {
        if (filters != null) {
            for (Iterator<Filter> itr =
                    filters.andIterator(); itr.hasNext();) {
                filters.accept(this);
            }
        }
    }

    @Override
    public void visit(PositionFilter constraint) {
        
    }

    @Override
    public void visit(ValueFilter constraint) {

    }

    @Override
    public void visit(PropertyValueFilter constraint) {

    }
}
