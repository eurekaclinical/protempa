/*
 * #%L
 * Protempa Commons Backend Provider
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
package org.protempa.backend.dsb.relationaldb;

import org.protempa.backend.dsb.filter.Filter;
import org.protempa.backend.dsb.filter.PropertyValueFilter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.arp.javautil.arrays.Arrays;
import org.arp.javautil.collections.Collections;
import org.protempa.backend.dsb.filter.PositionFilter;
import org.protempa.proposition.interval.Interval.Side;

/**
 * Aggregates info for generating the SQL statement.
 *
 * @author Andrew Post
 */
public final class ColumnSpecInfoFactory {

    public ColumnSpecInfo newInstance(Set<String> propIds,
            EntitySpec entitySpec, Collection<EntitySpec> entitySpecs,
            Map<String, ReferenceSpec> inboundRefSpecs,
            Collection<Filter> filters) {
        ColumnSpecInfo columnSpecInfo = new ColumnSpecInfo();
        columnSpecInfo.setUsingKeyIdIndex(true);
        columnSpecInfo.setUnique(entitySpec.isUnique());
        List<IntColumnSpecWrapper> columnSpecs = new ArrayList<>();
        int i = 0;
        i = processBaseSpec(entitySpec, columnSpecs, i);
        i = processUniqueIds(entitySpec, columnSpecs, i, columnSpecInfo);
        i = processStartTimeOrTimestamp(entitySpec,
                columnSpecs, i, columnSpecInfo);
        i = processFinishTimeSpec(entitySpec, columnSpecs,
                i, columnSpecInfo);
        i = processPropertyAndValueSpecs(entitySpec, columnSpecs, i,
                columnSpecInfo);
        i = processCodeSpec(entitySpec, columnSpecs, i, columnSpecInfo);
        i = processConstraintSpecs(entitySpec, entitySpecs, columnSpecs, i);
        i = processFilters(entitySpec, entitySpecs, filters, columnSpecs, i);
        i = processCreateDate(entitySpec, columnSpecs, i, columnSpecInfo);
        i = processUpdateDate(entitySpec, columnSpecs, i, columnSpecInfo);
        i = processDeleteDate(entitySpec, columnSpecs, i, columnSpecInfo);
        processReferenceSpecs(entitySpec, columnSpecs, i, columnSpecInfo,
                entitySpecs, inboundRefSpecs);
        columnSpecInfo.setColumnSpecs(columnSpecs);
        return columnSpecInfo;
    }

    private static int processReferenceSpecs(EntitySpec rhsEntitySpec,
            List<IntColumnSpecWrapper> columnSpecs,
            int i, ColumnSpecInfo columnSpecInfo,
            Collection<EntitySpec> entitySpecs,
            Map<String, ReferenceSpec> inboundRefSpecs) {
        int refNum = 0;
        for (EntitySpec lhsEntitySpec : entitySpecs) {
            if (inboundRefSpecs.containsKey(lhsEntitySpec.getName())) {
                if (lhsEntitySpec != rhsEntitySpec
                        && lhsEntitySpec.hasReferenceTo(rhsEntitySpec)) {
                    i = processReferenceSpecs(lhsEntitySpec, columnSpecs,
                            refNum, i, columnSpecInfo);
                    refNum++;
                }
            }
        }
        return i;
    }

    private static int processUniqueIds(EntitySpec entitySpec,
            List<IntColumnSpecWrapper> columnSpecs, int i,
            ColumnSpecInfo columnSpecInfo) {
        ColumnSpec[] codeSpecs = entitySpec.getUniqueIdSpecs();
        ColumnSpec[] refSpecs = null;
        int numUniqueIndices = codeSpecs.length;
        if (refSpecs != null) {
            numUniqueIndices += refSpecs.length;
        }
        int[] uniqueIndices = new int[numUniqueIndices];
        int j = 0;
        if (codeSpecs != null && uniqueIndices != null) {
            for (ColumnSpec uniqueIdSpec : codeSpecs) {
                i += wrapColumnSpec(uniqueIdSpec, columnSpecs);
                uniqueIndices[j++] = i - 1;
            }
        }
        if (refSpecs != null && uniqueIndices != null) {
            for (ColumnSpec uniqueIdSpec : refSpecs) {
                i += wrapColumnSpec(uniqueIdSpec, columnSpecs);
                uniqueIndices[j++] = i - 1;
            }
        }
        if (uniqueIndices != null) {
            columnSpecInfo.setUniqueIdIndices(uniqueIndices);
        }
        return i;
    }

    private static int processCodeSpec(EntitySpec entitySpec,
            List<IntColumnSpecWrapper> columnSpecs, int i,
            ColumnSpecInfo columnSpecInfo) {
        ColumnSpec codeSpec = entitySpec.getCodeSpec();
        if (codeSpec != null) {
            i += wrapColumnSpec(codeSpec, columnSpecs);
        }
        if (codeSpec != null) {
            columnSpecInfo.setCodeIndex(i - 1);
        }
        return i;
    }

    private static int processConstraintSpecs(EntitySpec entitySpec,
            Collection<EntitySpec> entitySpecs,
            List<IntColumnSpecWrapper> columnSpecs, int i) {
        List<EntitySpec> l = new LinkedList<>();
        l.add(entitySpec);
        for (EntitySpec es : entitySpecs) {
            ReferenceSpec[] referencesTo = es.referencesTo(l.get(0));
            for (ReferenceSpec rs : referencesTo) {
                if (rs.isApplyConstraints()) {
                    l.add(0, es);
                    break;
                }
            }
        }
        for (EntitySpec es : l) {
            ColumnSpec[] constraintSpecs = es.getConstraintSpecs();
            for (ColumnSpec spec : constraintSpecs) {
                i += wrapColumnSpec(spec, columnSpecs);
            }
        }

        return i;
    }

    private static int processFilters(EntitySpec entitySpec,
            Collection<EntitySpec> entitySpecs,
            Collection<Filter> filters,
            List<IntColumnSpecWrapper> columnSpecs, int i) {
        assert !columnSpecs.isEmpty() : "columnSpecs should be populated by now";
        List<EntitySpec> l = new LinkedList<>();
        l.add(entitySpec);
        for (EntitySpec es : entitySpecs) {
            if (es.hasReferenceTo(l.get(0))) {
                l.add(0, es);
            }
        }
        for (Filter filter : filters) {
            for (EntitySpec mes : l) {
                if (Collections.containsAny(Arrays.asSet(mes.getPropositionIds()), filter.getPropositionIds())) {
                    if (filter instanceof PositionFilter) {
                        PositionFilter pf = (PositionFilter) filter;
                        ColumnSpec startTimeSpec = mes.getStartTimeSpec();
                        if (startTimeSpec != null
                                && ((pf.getStartSide() == Side.START && pf.getStart() != null)
                                || (pf.getFinish() != null
                                && (pf.getFinishSide() == Side.START
                                || (mes.getFinishTimeSpec() == null && pf.getFinishSide() == Side.FINISH))))) {
                            i += wrapColumnSpec(startTimeSpec, columnSpecs);
                        }
                        ColumnSpec finishTimeSpec = mes.getFinishTimeSpec();
                        if (finishTimeSpec != null
                                && ((pf.getStartSide() == Side.FINISH && pf.getStart() != null)
                                || (pf.getFinishSide() == Side.FINISH && pf.getFinish() != null))) {
                            i += wrapColumnSpec(finishTimeSpec, columnSpecs);
                        }
                    } else if (filter instanceof PropertyValueFilter) {
                        PropertyValueFilter pvf = (PropertyValueFilter) filter;
                        for (PropertySpec propertySpec : mes.getPropertySpecs()) {
                            if (propertySpec.getName().equals(pvf.getProperty())) {
                                i += wrapColumnSpec(propertySpec.getCodeSpec(), columnSpecs);
                            }
                        }
                    }
                }
            }
        }
        return i;
    }

    private static int processPropertyAndValueSpecs(EntitySpec entitySpec,
            List<IntColumnSpecWrapper> columnSpecs, int i,
            ColumnSpecInfo columnSpecInfo) {
        PropertySpec[] propertySpecs = entitySpec.getPropertySpecs();
        Map<String, Integer> propertyIndices
                = new HashMap<>();
        for (PropertySpec propertySpec : propertySpecs) {
            ColumnSpec codeSpec = propertySpec.getCodeSpec();
            i += wrapColumnSpec(codeSpec, columnSpecs);
            propertyIndices.put(propertySpec.getName(), i - 1);
            i += wrapPropertySpecConstraintSpec(propertySpec, columnSpecs);
        }
        if (propertySpecs.length > 0) {
            columnSpecInfo.setPropertyIndices(propertyIndices);
        }

        ColumnSpec valueSpec = entitySpec.getValueSpec();
        if (valueSpec != null) {
            i += wrapColumnSpec(valueSpec, columnSpecs);
            columnSpecInfo.setValueIndex(i - 1);
        }

        return i;
    }

    private static int processReferenceSpecs(EntitySpec lhsEntitySpec,
            List<IntColumnSpecWrapper> columnSpecs, int refNum,
            int i, ColumnSpecInfo columnSpecInfo) {

        for (ColumnSpec referringUniqueIdSpec : lhsEntitySpec.getUniqueIdSpecs()) {
            i += wrapColumnSpec(referringUniqueIdSpec, columnSpecs);
        }

        columnSpecInfo.putReferenceIndices("ref" + refNum, i - 1);

        return i;
    }

    private static int processFinishTimeSpec(EntitySpec entitySpec,
            List<IntColumnSpecWrapper> columnSpecs, int i,
            ColumnSpecInfo columnSpecInfo) {
        ColumnSpec spec = entitySpec.getFinishTimeSpec();
        if (spec != null) {
            i += wrapColumnSpec(spec, columnSpecs);
            columnSpecInfo.setFinishTimeIndex(i - 1);
        }
        return i;
    }

    private static int processStartTimeOrTimestamp(EntitySpec entitySpec,
            List<IntColumnSpecWrapper> columnSpecs, int i,
            ColumnSpecInfo columnSpecInfo) {
        ColumnSpec spec = entitySpec.getStartTimeSpec();
        if (spec != null) {
            i += wrapColumnSpec(spec, columnSpecs);
            columnSpecInfo.setStartTimeIndex(i - 1);
        }
        return i;
    }

    private static int processCreateDate(EntitySpec entitySpec,
            List<IntColumnSpecWrapper> columnSpecs, int i,
            ColumnSpecInfo columnSpecInfo) {
        ColumnSpec spec = entitySpec.getCreateDateSpec();
        if (spec != null) {
            i += wrapColumnSpec(spec, columnSpecs);
            columnSpecInfo.setCreateDateIndex(i - 1);
        }
        return i;
    }

    private static int processUpdateDate(EntitySpec entitySpec,
            List<IntColumnSpecWrapper> columnSpecs, int i,
            ColumnSpecInfo columnSpecInfo) {
        ColumnSpec spec = entitySpec.getUpdateDateSpec();
        if (spec != null) {
            i += wrapColumnSpec(spec, columnSpecs);
            columnSpecInfo.setUpdateDateIndex(i - 1);
        }
        return i;
    }

    private static int processDeleteDate(EntitySpec entitySpec,
            List<IntColumnSpecWrapper> columnSpecs, int i,
            ColumnSpecInfo columnSpecInfo) {
        ColumnSpec spec = entitySpec.getDeleteDateSpec();
        if (spec != null) {
            i += wrapColumnSpec(spec, columnSpecs);
            columnSpecInfo.setDeleteDateIndex(i - 1);
        }
        return i;
    }

    private static int wrapPropertySpecConstraintSpec(PropertySpec propertySpec, List<IntColumnSpecWrapper> columnSpecs) {
        ColumnSpec lastSpec = propertySpec.getCodeSpec().getLastSpec();
        return wrapColumnSpecsHelper(propertySpec.getConstraintSpec(), lastSpec, columnSpecs);
    }

    private static int wrapColumnSpec(ColumnSpec spec, List<IntColumnSpecWrapper> columnSpecs) {
        return wrapColumnSpecsHelper(spec, null, columnSpecs);
    }

    private static int wrapColumnSpecsHelper(ColumnSpec spec, ColumnSpec lastSpec, List<IntColumnSpecWrapper> columnSpecs) {
        boolean first = true;
        List<ColumnSpec> asList;
        if (spec != null) {
            asList = spec.asList();
        } else {
            asList = java.util.Collections.emptyList();
        }
        for (ColumnSpec cs : asList) {
            IntColumnSpecWrapper w = new IntColumnSpecWrapper(cs);
            if (first) {
                w.setIsSameAs(lastSpec);
                first = false;
            }
            columnSpecs.add(w);
        }
        return asList.size();
    }

    private static int processBaseSpec(EntitySpec entitySpec,
            List<IntColumnSpecWrapper> columnSpecs, int i) {
        ColumnSpec spec = entitySpec.getBaseSpec();
        List<ColumnSpec> specAsList = spec.asList();
        /*
         * We assume that the first column spec of the base spec is the
         * patient id/key.
         */
        for (ColumnSpec cs : specAsList) {
            IntColumnSpecWrapper w = new IntColumnSpecWrapper(cs);
            columnSpecs.add(w);
        }
        i += specAsList.size();

        return i;
    }
}
