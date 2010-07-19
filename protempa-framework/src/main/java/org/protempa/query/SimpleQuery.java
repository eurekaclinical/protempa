package org.protempa.query;

/**
 *
 * @author Andrew Post
 */
public class SimpleQuery extends AbstractQuery {

    public SimpleQuery() {

    }

    public SimpleQuery(String keyId) {
        setKeyIds(new String[] {keyId});
    }

    public static SimpleQuery newInstance(String keyId, String... propIds) {
        SimpleQuery result = new SimpleQuery();
        result.setKeyIds(new String[] {keyId});
        result.setPropIds(propIds);
        return result;
    }

    public static SimpleQuery newFindAllPropsInstance(String keyId) {
        SimpleQuery result = new SimpleQuery();
        result.setKeyIds(new String[] {keyId});
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
