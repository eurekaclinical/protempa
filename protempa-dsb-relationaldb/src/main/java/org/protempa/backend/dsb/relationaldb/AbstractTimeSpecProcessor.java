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
import org.arp.javautil.arrays.Arrays;
import org.arp.javautil.collections.Collections;
import org.protempa.backend.dsb.filter.Filter;
import org.protempa.backend.dsb.filter.PositionFilter;

/**
 *
 * @author arpost
 */
abstract class AbstractTimeSpecProcessor {
    private final EntitySpec entitySpec;
    private final Set<Filter> filters;
    private final boolean first;
    private final TableAliaser referenceIndices;

    protected AbstractTimeSpecProcessor(EntitySpec entitySpec, Set<Filter> filters, boolean first, TableAliaser referenceIndices) {
        this.entitySpec = entitySpec;
        this.filters = filters;
        this.first = first;
        this.referenceIndices = referenceIndices;
    }

    protected EntitySpec getEntitySpec() {
        return this.entitySpec;
    }

    protected abstract ColumnSpec getTimeSpec();

    protected abstract boolean outputStart(PositionFilter filter);

    protected abstract boolean outputFinish(PositionFilter filter);

    String process() {
        StringBuilder wherePart = new StringBuilder();
        ColumnSpec ts = getTimeSpec();
        if (ts != null) {
            ColumnSpec timeSpec = ts.getLastSpec();
            if (timeSpec != null && referenceIndices.getIndex(timeSpec) > -1) {
                for (Filter filter : filters) {
                    if (filter instanceof PositionFilter) {
                        Set<String> entitySpecPropIds = Arrays.asSet(entitySpec.getPropositionIds());
                        if (Collections.containsAny(entitySpecPropIds, filter.getPropositionIds())) {
                            PositionFilter pdsc2 = (PositionFilter) filter;
                            boolean outputStart = outputStart(pdsc2);
                            boolean outputFinish = outputFinish(pdsc2);
                            if (outputStart) {
                                if (!first) {
                                    wherePart.append(" AND ");
                                }
                                wherePart.append(referenceIndices.generateColumnReferenceWithOp(timeSpec));
                                wherePart.append(" >= ");
                                wherePart.append(entitySpec.getPositionParser().format(pdsc2.getMinimumStart()));
                            }
                            if (outputFinish) {
                                if (!first || outputStart) {
                                    wherePart.append(" AND ");
                                }
                                wherePart.append(referenceIndices.generateColumnReferenceWithOp(timeSpec));
                                wherePart.append(" <= ");
                                wherePart.append(entitySpec.getPositionParser().format(pdsc2.getMaximumFinish()));
                            }
                        }
                    }
                }
            }
        }
        return wherePart.toString();
    }
    
}
