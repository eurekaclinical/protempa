/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2013 Emory University
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
package org.protempa;

import org.protempa.proposition.interval.Interval.Side;
import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.protempa.proposition.value.Unit;

/**
 *
 * @author Andrew Post
 */
public class ContextOffset implements Serializable {
    private static final long serialVersionUID = 1;
    
    private Integer startOffset;
    private Unit startOffsetUnits;
    private Integer finishOffset;
    private Unit finishOffsetUnits;
    private Side startIntervalSide;
    private Side finishIntervalSide;

    public ContextOffset() {
        this.startOffset = Integer.valueOf(0);
        this.finishOffset = Integer.valueOf(0);
        this.startIntervalSide = Side.START;
        this.finishIntervalSide = Side.FINISH;
    }
    
    public Side getFinishIntervalSide() {
        return finishIntervalSide;
    }

    public void setFinishIntervalSide(Side finishIntervalSide) {
        if (finishIntervalSide != null) {
            this.finishIntervalSide = finishIntervalSide;
        } else {
            this.finishIntervalSide = Side.FINISH;
        }
    }

    public Integer getFinishOffset() {
        return finishOffset;
    }

    public void setFinishOffset(Integer finishOffset) {
        this.finishOffset = finishOffset;
    }

    public Unit getFinishOffsetUnits() {
        return finishOffsetUnits;
    }

    public void setFinishOffsetUnits(Unit finishOffsetUnits) {
        this.finishOffsetUnits = finishOffsetUnits;
    }

    public Side getStartIntervalSide() {
        return startIntervalSide;
    }

    public void setStartIntervalSide(Side startIntervalSide) {
        if (startIntervalSide != null) {
            this.startIntervalSide = startIntervalSide;
        } else {
            this.startIntervalSide = Side.START;
        }
    }

    public Integer getStartOffset() {
        return startOffset;
    }

    public void setStartOffset(Integer startOffset) {
        this.startOffset = startOffset;
    }

    public Unit getStartOffsetUnits() {
        return startOffsetUnits;
    }

    public void setStartOffsetUnits(Unit startOffsetUnits) {
        this.startOffsetUnits = startOffsetUnits;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
