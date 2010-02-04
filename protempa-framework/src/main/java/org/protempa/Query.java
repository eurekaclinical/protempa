package org.protempa;

/**
 *
 * @author Andrew Post
 */
public interface Query {
    void setKeyIds(String[] keyIds);
    String[] getKeyIds();

    void setStart(Long start);
    Long getStart();

    void setFinish(Long finish);
    Long getFinish();

    /**
     *
     * @param propIds an array of proposition id {@link String}s.
     */
    void setPropIds(String[] propIds);

    String[] getPropIds();
}
