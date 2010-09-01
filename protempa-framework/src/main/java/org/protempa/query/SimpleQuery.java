package org.protempa.query;

/**
 * The basic query implementation.
 * 
 * @author Andrew Post
 */
public class SimpleQuery extends AbstractQuery {

    public SimpleQuery() {
        super();
    }
    
    public static SimpleQuery newInstance (SimpleQuery originalQuery) {
        SimpleQuery query = new SimpleQuery();
        query.setFilters(originalQuery.getFilters());
        return query;
    }

    public static SimpleQuery newInstance(String keyId, String... propIds) {
        SimpleQuery result = new SimpleQuery();
        result.setKeyIds(new String[] { keyId });
        result.setPropIds(propIds);
        return result;
    }

    public static SimpleQuery newFindAllPropsInstance(String keyId) {
        SimpleQuery result = new SimpleQuery();
        result.setKeyIds(new String[] { keyId });
        return result;
    }

    public static SimpleQuery newFindForAllKeysInstance(String... propIds) {
        SimpleQuery result = new SimpleQuery();
        result.setPropIds(propIds);
        return result;
    }

    public static SimpleQuery newFindAllPropsForAllKeysInstance() {
        return new SimpleQuery();
    }
}
