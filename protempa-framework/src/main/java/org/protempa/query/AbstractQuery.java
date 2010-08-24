package org.protempa.query;

import org.protempa.dsb.filter.Filter;

/**
 *
 * @author Andrew Post
 */
public abstract class AbstractQuery implements Query {
    private String[] keyIds = new String[0];
    private Filter filters;
    private String[] propIds = new String[0];

    /**
     * Does nothing.
     */
    public AbstractQuery() {
        
    }

    @Override
    public Filter getFilters() {
        return this.filters;
    }

    @Override
    public void setFilters(Filter filters) {
        this.filters = filters;
    }

    @Override
    public String[] getKeyIds() {
        return this.keyIds;
    }

    @Override
    public void setKeyIds(String[] keyIds) {
        if (keyIds == null)
            keyIds = new String[0];
        this.keyIds = keyIds;
    }

    @Override
    public String[] getPropIds() {
        return this.propIds;
    }
    
    @Override
    public void setPropIds(String[] propIds) {
        if (propIds == null)
            propIds = new String[0];
        this.propIds = propIds;
    }

    @Override
    public abstract Query clone();
}
