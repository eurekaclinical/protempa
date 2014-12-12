/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.protempa.backend.dsb.relationaldb;

/*
 * #%L
 * Protempa Relational Database Data Source Backend
 * %%
 * Copyright (C) 2012 - 2014 Emory University
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

import java.util.Set;
import org.protempa.backend.dsb.filter.Filter;
import org.protempa.backend.dsb.filter.PositionFilter;
import org.protempa.proposition.interval.Interval;

/**
 *
 * @author arpost
 */
class StartTimeSpecProcessor extends AbstractTimeSpecProcessor {

    StartTimeSpecProcessor(EntitySpec entitySpec, Set<Filter> filters, boolean first, TableAliaser referenceIndices) {
        super(entitySpec, filters, first, referenceIndices);
    }

    @Override
    protected ColumnSpec getTimeSpec() {
        return getEntitySpec().getStartTimeSpec();
    }

    @Override
    protected boolean outputStart(PositionFilter filter) {
        return filter.getMinimumStart() != null && (filter.getStartSide() == Interval.Side.START || getEntitySpec().getFinishTimeSpec() == null);
    }

    @Override
    protected boolean outputFinish(PositionFilter filter) {
        return filter.getMaximumFinish() != null && (filter.getFinishSide() == Interval.Side.START || getEntitySpec().getFinishTimeSpec() == null);
    }
    
}
