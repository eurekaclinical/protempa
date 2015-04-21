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

    public ColumnSpecInfo newInstance(Set<String> propIds, EntitySpec entitySpec,
            Collection<EntitySpec> entitySpecs, Map<String, ReferenceSpec>
            inboundRefSpecs, Collection<Filter> filters, ReferenceSpec referenceSpec,
            boolean streamingMode) {
        ColumnSpecInfo columnSpecInfo = new ColumnSpecInfo();
        if (referenceSpec == null || streamingMode) {
            columnSpecInfo.setUsingKeyIdIndex(true);
        }
        EntitySpec refEntitySpec = null;
        if (referenceSpec != null) {
            refEntitySpec = findRefEntitySpec(entitySpecs, referenceSpec);
            columnSpecInfo.setUnique(refEntitySpec.isUnique()
                    && entitySpec.isUnique()
                    && hasNoXToManyReferences(entitySpecs, entitySpec));
        } else {
            columnSpecInfo.setUnique(entitySpec.isUnique());
        }
        List<IntColumnSpecWrapper> columnSpecs = new ArrayList<>();
        int i = 0;
        i = processBaseSpec(entitySpec, columnSpecs, i);
        i = processUniqueIds(entitySpec, columnSpecs, i,
                columnSpecInfo, referenceSpec);
        i = processStartTimeOrTimestamp(entitySpec,
                columnSpecs, i, columnSpecInfo, referenceSpec);
        i = processFinishTimeSpec(entitySpec, columnSpecs,
                i, columnSpecInfo, referenceSpec);
        if (referenceSpec == null) {
            i = processPropertyAndValueSpecs(entitySpec, columnSpecs, i,
                    columnSpecInfo);
        }
        i = processCodeSpec(propIds, entitySpec, columnSpecs,
                i, columnSpecInfo, referenceSpec);
        i = processConstraintSpecs(entitySpec, entitySpecs, columnSpecs, i);
        i = processFilters(entitySpec, entitySpecs, filters, columnSpecs, i);
        
        int refNum = 0;
        for (EntitySpec entitySpec2 : entitySpecs) {
            for (Map.Entry<String, ReferenceSpec> inboundRef : inboundRefSpecs.entrySet()) {
                if (inboundRef.getKey().equals(entitySpec2.getName())) {
                    if (entitySpec2 != entitySpec && referenceSpec == null &&
                            entitySpec2.hasReferenceTo(entitySpec)) {
                        i = processReferenceSpecs(entitySpec2, entitySpec, columnSpecs,
                                refNum, i, columnSpecInfo);
                        refNum++;
                    }
                    break;
                }
            }
        }
        columnSpecInfo.setColumnSpecs(columnSpecs);
        return columnSpecInfo;
    }

    private static boolean hasNoXToManyReferences(
            Collection<EntitySpec> entitySpecs, EntitySpec entitySpec) {
        for (EntitySpec es : entitySpecs) {
            if (es.hasReferenceTo(entitySpec)) {
                for (ReferenceSpec refSpec : entitySpec.referencesTo(es)) {
                    if (refSpec.getType() == ReferenceSpec.Type.MANY) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static EntitySpec findRefEntitySpec(
            Collection<EntitySpec> entitySpecs, ReferenceSpec referenceSpec) {
        String referenceSpecEntityName = referenceSpec.getEntityName();
        for (EntitySpec es : entitySpecs) {
            if (es.getName().equals(referenceSpecEntityName)) {
                return es;
            }
        }
        throw new AssertionError("invalid entity spec name in reference spec "
                + referenceSpec);
    }

    private static int processUniqueIds(EntitySpec entitySpec,
            List<IntColumnSpecWrapper> columnSpecs, int i,
            ColumnSpecInfo columnSpecInfo, ReferenceSpec referenceSpec) {
        ColumnSpec[] codeSpecs = entitySpec.getUniqueIdSpecs();
        ColumnSpec[] refSpecs = null;
        if (referenceSpec != null) {
            refSpecs = referenceSpec.getUniqueIdSpecs();
        }
        int numUniqueIndices = codeSpecs.length;
        if (refSpecs != null) {
            numUniqueIndices += refSpecs.length;
        }
        int[] uniqueIndices =  new int[numUniqueIndices];
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

    private static int processCodeSpec(Set<String> propIds, EntitySpec entitySpec,
            List<IntColumnSpecWrapper> columnSpecs, int i,
            ColumnSpecInfo columnSpecInfo, ReferenceSpec referenceSpec) {
        ColumnSpec codeSpec = entitySpec.getCodeSpec();
        if (codeSpec != null) {
            List<ColumnSpec> specAsList = codeSpec.asList();
            int specAsListSize = specAsList.size();
            ColumnSpec lastColumnSpec = specAsList.get(specAsListSize - 1);
            if (referenceSpec == null
                    || !(lastColumnSpec.getConstraint() == Operator.EQUAL_TO
                    && lastColumnSpec.isPropositionIdsComplete()
                    && !AbstractSQLGenerator.needsPropIdInClause(propIds,
                    entitySpec.getPropositionIds()))) {
                i += wrapColumnSpec(codeSpec, columnSpecs);
            } else {
                codeSpec = null;
            }
        }
        if (codeSpec != null && referenceSpec == null) {
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
            if (es.hasReferenceTo(l.get(0))) {
                l.add(0, es);
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
        Map<String, Integer> propertyIndices =
                new HashMap<>();
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
                                             EntitySpec rhsEntitySpec,
                                             List<IntColumnSpecWrapper> columnSpecs, int refNum,
                                             int i, ColumnSpecInfo columnSpecInfo) {

        if (lhsEntitySpec.hasReferenceTo(rhsEntitySpec)) {
            for (ColumnSpec referringUniqueIdSpec : lhsEntitySpec.getUniqueIdSpecs()) {
                i += wrapColumnSpec(referringUniqueIdSpec, columnSpecs);
            }

            if (columnSpecInfo.getReferenceIndices() == null) {
                columnSpecInfo.setReferenceIndices(new HashMap<String, Integer>());
            }
            columnSpecInfo.getReferenceIndices().put("ref" + refNum, i - 1);
        }

        return i;
    }

    private static int processFinishTimeSpec(EntitySpec entitySpec,
            List<IntColumnSpecWrapper> columnSpecs, int i,
            ColumnSpecInfo columnSpecInfo, ReferenceSpec referenceSpec) {
        ColumnSpec spec = entitySpec.getFinishTimeSpec();
        if (spec != null) {
            i += wrapColumnSpec(spec, columnSpecs);
            if (referenceSpec == null) {
                columnSpecInfo.setFinishTimeIndex(i - 1);
            }
        }
        return i;
    }

    private static int processStartTimeOrTimestamp(EntitySpec entitySpec,
            List<IntColumnSpecWrapper> columnSpecs, int i,
            ColumnSpecInfo columnSpecInfo, ReferenceSpec referenceSpec) {
        ColumnSpec spec = entitySpec.getStartTimeSpec();
        if (spec != null) {
            i += wrapColumnSpec(spec, columnSpecs);
            if (referenceSpec == null) {
                columnSpecInfo.setStartTimeIndex(i - 1);
            }
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
