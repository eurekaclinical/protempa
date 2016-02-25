package org.protempa.test;

import java.util.Date;

/**
 *
 * @author arpost
 */
abstract class Record {
    private Date createDate;
    private Date updateDate;
    private Date deleteDate;

    Date getCreateDate() {
        return createDate;
    }

    void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    Date getUpdateDate() {
        return updateDate;
    }

    void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    Date getDeleteDate() {
        return deleteDate;
    }

    void setDeleteDate(Date deleteDate) {
        this.deleteDate = deleteDate;
    }
    
    
}
