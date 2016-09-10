package org.protempa.test;

/*-
 * #%L
 * Protempa Test Suite
 * %%
 * Copyright (C) 2012 - 2016 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
