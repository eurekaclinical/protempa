package org.protempa;

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
}
