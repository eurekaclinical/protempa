package org.protempa.bp.commons.dsb.sqlgen;

import org.arp.javautil.sql.SQLExecutor.ResultProcessor;

/**
 *
 * @author Andrew Post
 */
interface SQLGenResultProcessor extends ResultProcessor {
    void setCasePresent(boolean casePresent);
    boolean isCasePresent();
    EntitySpec getEntitySpec();
}
