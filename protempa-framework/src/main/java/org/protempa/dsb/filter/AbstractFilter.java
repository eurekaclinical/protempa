package org.protempa.dsb.filter;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import org.protempa.ProtempaUtil;

/**
 * An abstract class that makes implementing filters relatively easy.
 *
 * @author Andrew Post.
 */
public abstract class AbstractFilter implements Filter {

    private final String[] propositionIds;
    private Filter and;

    /**
     * Instantiates the filter with the proposition ids that it is valid for.
     *
     * @param propositionIds a proposition id {@link String[]}. Cannot be
     * <code>null</code>, empty or contain <code>null</code> values.
     */
    public AbstractFilter(String[] propositionIds) {
        ProtempaUtil.checkArray(propositionIds, "propositionIds");
        this.propositionIds = propositionIds;
    }
    
    @Override
    public String[] getPropositionIds() {
        return propositionIds;
    }

    public void setAnd(Filter and) {
        this.and = and;
    }

    @Override
    public Filter getAnd() {
        return and;
    }

    private static class DataSourceConstraintAndIterator
            implements Iterator<Filter> {

        private Filter dataSourceConstraint;

        private DataSourceConstraintAndIterator(Filter dataSourceConstraint) {
            assert dataSourceConstraint != null :
                    "dataSourceConstraint cannot be null";
            this.dataSourceConstraint = dataSourceConstraint;
        }

        @Override
        public boolean hasNext() {
            if (this.dataSourceConstraint != null) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public Filter next() {
            if (this.dataSourceConstraint != null) {
                Filter dsc = this.dataSourceConstraint;
                this.dataSourceConstraint = this.dataSourceConstraint.getAnd();
                return dsc;
            } else {
                throw new NoSuchElementException();
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public Iterator<Filter> andIterator() {
        return new DataSourceConstraintAndIterator(this);
    }

    protected Map<String,Object> toStringFields() {
        Map<String,Object> result = new LinkedHashMap<String,Object>();
        result.put("propositionIds", this.propositionIds);
        result.put("and", this.and);
        return result;
    }

    
}
