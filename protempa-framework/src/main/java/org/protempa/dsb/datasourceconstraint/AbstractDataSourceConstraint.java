package org.protempa.dsb.datasourceconstraint;

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class AbstractDataSourceConstraint implements
		DataSourceConstraint {

    private final String propositionId;

    private DataSourceConstraint and;

    public AbstractDataSourceConstraint(String propositionId) {
        if (propositionId == null)
            throw new IllegalArgumentException("propositionId cannot be null");
        this.propositionId = propositionId;
    }

    public String getPropositionId() {
        return propositionId;
    }

    public void setAnd(DataSourceConstraint and) {
        this.and = and;
    }

    public DataSourceConstraint getAnd() {
        return and;
    }

    private static class DataSourceConstraintAndIterator 
            implements Iterator<DataSourceConstraint> {

        private DataSourceConstraint dataSourceConstraint;

        private DataSourceConstraintAndIterator(DataSourceConstraint
                dataSourceConstraint) {
            assert dataSourceConstraint != null :
                "dataSourceConstraint cannot be null";
            this.dataSourceConstraint = dataSourceConstraint;
        }

        public boolean hasNext() {
            if (this.dataSourceConstraint != null) {
                return true;
            } else {
                return false;
            }
        }

        public DataSourceConstraint next() {
            if (this.dataSourceConstraint != null) {
                DataSourceConstraint dsc = this.dataSourceConstraint;
                this.dataSourceConstraint = this.dataSourceConstraint.getAnd();
                return dsc;
            } else {
                throw new NoSuchElementException();
            }
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

    public Iterator<DataSourceConstraint> andIterator() {
        return new DataSourceConstraintAndIterator(this);
    }
}
