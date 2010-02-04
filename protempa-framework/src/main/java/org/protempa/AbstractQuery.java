package org.protempa;

/**
 *
 * @author Andrew Post
 */
public abstract class AbstractQuery implements Query{
    private String[] keyIds;
    private Long start;
    private Long finish;
    private String[] propIds;
    
    public AbstractQuery() {
        
    }

    public Long getFinish() {
        return this.finish;
    }

    public String[] getKeyIds() {
        return this.keyIds;
    }

    public Long getStart() {
        return this.start;
    }

    public void setFinish(Long finish) {
        this.finish = finish;
    }

    public void setKeyIds(String[] keyIds) {
        this.keyIds = keyIds;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public String[] getPropIds() {
        return this.propIds;
    }

    /**
     * If <code>null</code>, PROTEMPA will search the entire database.
     * @param propIds
     */
    public void setPropIds(String[] propIds) {
        this.propIds = propIds;
    }

}
