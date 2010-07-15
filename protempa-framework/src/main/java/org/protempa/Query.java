package org.protempa;

import org.protempa.proposition.value.Granularity;

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

    void setStartGranularity(Granularity startGranularity);
    Granularity getStartGranularity();

    void setFinishGranularity(Granularity finishGranularity);
    Granularity getFinishGranularity();


    Long getMinimumStart();
    Long getMaximumStart();
    Long getMinimumFinish();
    Long getMaximumFinish();

    /**
     *
     * @param propIds an array of proposition id {@link String}s.
     */
    void setPropIds(String[] propIds);

    String[] getPropIds();
}
