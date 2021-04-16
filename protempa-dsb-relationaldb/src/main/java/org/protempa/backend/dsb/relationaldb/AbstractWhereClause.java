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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.arp.javautil.arrays.Arrays;
import org.protempa.backend.dsb.filter.Filter;
import org.protempa.backend.dsb.filter.PropertyValueFilter;
import org.protempa.backend.dsb.relationaldb.mappings.Mappings;
import org.protempa.proposition.value.BooleanValue;
import org.protempa.proposition.value.DateValue;
import org.protempa.proposition.value.InequalityNumberValue;
import org.protempa.proposition.value.NominalValue;
import org.protempa.proposition.value.NumberValue;
import org.protempa.proposition.value.OrdinalValue;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueComparator;
import org.protempa.proposition.value.ValueList;
import org.protempa.proposition.value.ValueVisitor;

public abstract class AbstractWhereClause implements WhereClause {

    private static final Logger LOGGER = Logger.getLogger(AbstractWhereClause.class.toString());
    
    private final Set<String> propIds;
    private final ColumnSpecInfo info;
    private final List<EntitySpec> entitySpecs;
    private final Set<Filter> filters;
    private final TableAliaser referenceIndices;
    private final Set<String> keyIds;
    private final SQLOrderBy order;
    private final SQLGenResultProcessor resultProcessor;
    private final SelectClause selectClause;

    protected AbstractWhereClause(Set<String> propIds, ColumnSpecInfo info,
            List<EntitySpec> entitySpecs, Set<Filter> filters,
            TableAliaser referenceIndices, Set<String> keyIds,
            SQLOrderBy order, SQLGenResultProcessor resultProcessor,
            SelectClause selectClause) {
        this.propIds = propIds;
        this.info = info;
        this.entitySpecs = Collections.unmodifiableList(entitySpecs);
        this.filters = Collections.unmodifiableSet(filters);
        this.referenceIndices = referenceIndices;
        this.keyIds = Collections.unmodifiableSet(keyIds);
        this.order = order;
        this.resultProcessor = resultProcessor;
        this.selectClause = selectClause;
    }

    protected Set<String> getPropIds() {
        return propIds;
    }

    protected ColumnSpecInfo getInfo() {
        return info;
    }

    protected List<EntitySpec> getEntitySpecs() {
        return entitySpecs;
    }

    protected Set<Filter> getFilters() {
        return filters;
    }

    protected TableAliaser getReferenceIndices() {
        return referenceIndices;
    }

    protected Set<String> getKeyIds() {
        return keyIds;
    }

    protected SQLOrderBy getOrder() {
        return order;
    }

    protected SQLGenResultProcessor getResultProcessor() {
        return resultProcessor;
    }

    protected SelectClause getSelectClause() {
        return selectClause;
    }

    @Override
    public abstract InClause getInClause(ColumnSpec columnSpec,
            Object[] elements, boolean not);

    @Override
    public abstract OrderByClause getOrderByClause(ColumnSpec keyIdSpec);

    @Override
    public String generateClause() {
        StringBuilder wherePart = new StringBuilder();

        EntitySpec prevEntitySpec = null;
        boolean first = true;
        boolean inGroup = false;
        for (int j = 0, n = entitySpecs.size(); j < n; j++) {
            EntitySpec entitySpec = entitySpecs.get(j);
            if (n > 1 && j > 0) {
                if (prevEntitySpec.getName().equals(entitySpec.getName())) {
                    if (!inGroup) {
                        if (!first) {
                            wherePart.append(" AND ");
                            first = true;
                        }
                        wherePart.append(" ((");
                        inGroup = true;
                    } else {
                        wherePart.append(") OR (");
                        first = true;
                    }
                    int wherePartLength = wherePart.length();
                    wherePart.append(processForWhereClause(prevEntitySpec,
                            first));
                    if (wherePart.length() > wherePartLength) {
                        first = false;
                    }
                } else if (inGroup) {
                    first = true;
                    int wherePartLength = wherePart.length();
                    wherePart.append(") OR (");
                    wherePart.append(processForWhereClause(prevEntitySpec,
                            first));
                    wherePart.append(")) ");
                    if (wherePart.length() > wherePartLength) {
                        first = false;
                    }
                    inGroup = false;
                } else {
                    int wherePartLength = wherePart.length();
                    wherePart.append(processForWhereClause(prevEntitySpec,
                            first));
                    if (wherePart.length() > wherePartLength) {
                        first = false;
                    }
                }

            }
            prevEntitySpec = entitySpec;
        }
        if (inGroup) {
            first = true;
            wherePart.append(") OR (");
            wherePart.append(processForWhereClause(prevEntitySpec, first));
            wherePart.append(")) ");
        } else {
            wherePart.append(processForWhereClause(prevEntitySpec, first));
        }

        processKeyIdConstraintsForWhereClause(info, wherePart, keyIds);

        if (wherePart.length() > 0) {
            wherePart.insert(0, "WHERE ");
        }

        wherePart.append(processOrder(info));

        return wherePart.toString();
    }

    private void processKeyIdConstraintsForWhereClause(ColumnSpecInfo info,
            StringBuilder wherePart, Set<String> keyIds) {
        if (keyIds != null && !keyIds.isEmpty()) {
            if (wherePart.length() > 0) {
                wherePart.append(" AND ");
            }
            ColumnSpec keySpec = info.getColumnSpecs().get(0).getColumnSpec();

            wherePart.append(getInClause(keySpec, keyIds.toArray(), false)
                    .generateClause());
        }
    }

    private String processForWhereClause(EntitySpec entitySpec,
            boolean first) {
        StringBuilder wherePart = new StringBuilder();
        int wherePartLength = wherePart.length();
        wherePart.append(TimeSpecProcessor.processStartTimeSpec(entitySpec,
                filters, first, referenceIndices));
        if (wherePart.length() > wherePartLength) {
            first = false;
        }
        wherePartLength = wherePart.length();
        wherePart.append(TimeSpecProcessor.processFinishTimeSpec(
                entitySpec, filters, first, referenceIndices));
        if (wherePart.length() > wherePartLength) {
            first = false;
        }
        wherePartLength = wherePart.length();
        if (wherePart.length() > wherePartLength) {
            first = false;
        }
        wherePartLength = wherePart.length();
        wherePart.append(processConstraintSpecsForWhereClause(entitySpec,
                first));

        return wherePart.toString();
    }

    private StringBuilder processConstraintSpecs(EntitySpec entitySpec,
            boolean first) {
        StringBuilder wherePart = new StringBuilder();

        CONSTRAINT_LOOP:
        for (ColumnSpec constraintSpec : entitySpec
                .getConstraintSpecs()) {
            int wherePartLength = wherePart.length();
            wherePart.append(processConstraintSpecForWhereClause(
                    constraintSpec, null, first));
            if (wherePart.length() > wherePartLength) {
                first = false;
            }
        }

        return wherePart;
    }

    private StringBuilder processPropertySpecs(PropertySpec[] propertySpecs,
            boolean first) {
        StringBuilder wherePart = new StringBuilder();

        for (PropertySpec ps : propertySpecs) {
            ColumnSpec constraintSpec = ps.getConstraintSpec();
            if (constraintSpec != null) {
                int wherePartLength = wherePart.length();
                wherePart.append(processConstraint(constraintSpec, null, null, first));
                if (wherePart.length() > wherePartLength) {
                    first = false;
                }
            }
        }

        for (Filter filter : filters) {
            for (PropertySpec ps : propertySpecs) {
                if (filter instanceof PropertyValueFilter) {
                    PropertyValueFilter pvf = (PropertyValueFilter) filter;

                    if (pvf.getProperty().equals(ps.getName())) {
                        ColumnSpec colSpec = ps.getCodeSpec();
                        int wherePartLength = wherePart.length();
                        wherePart.append(processPropertyValueFilter(colSpec,
                                pvf.getValueComparator(), pvf.getValues(),
                                first));
                        if (wherePart.length() > wherePartLength) {
                            first = false;
                        }

                        break;
                    }
                }
            }
        }

        return wherePart;
    }

    private StringBuilder processCodeSpec(EntitySpec entitySpec, boolean first) {
        StringBuilder wherePart = new StringBuilder();
        ColumnSpec codeSpec = entitySpec.getCodeSpec();

        if (codeSpec != null) {
            List<ColumnSpec> codeSpecL = codeSpec.asList();
            if (codeSpecL.get(codeSpecL.size() - 1).isPropositionIdsComplete()
                    && !needsPropIdInClause(entitySpec.getPropositionIds())) {
                setCaseClauseIfNeeded(codeSpec, this.propIds);
            } else {
                int wherePartLength = wherePart.length();
                wherePart.append(processConstraintSpecForWhereClause(codeSpec,
                        this.propIds, first));
                if (wherePart.length() > wherePartLength) {
                    first = false;
                }
            }
        }

        return wherePart;
    }

    private String processConstraintSpecsForWhereClause(EntitySpec entitySpec,
            boolean first) {

        StringBuilder wherePart = new StringBuilder();

        int wherePartLength = wherePart.length();
        wherePart.append(processConstraintSpecs(entitySpec, first));
        if (first && wherePart.length() > wherePartLength) {
            first = false;
            wherePartLength = wherePart.length();
        }
        wherePart.append(processPropertySpecs(entitySpec.getPropertySpecs(),
                first));
        if (first && wherePart.length() > wherePartLength) {
            first = false;
            wherePartLength = wherePart.length();
        }
        wherePart.append(processCodeSpec(entitySpec, first));

        return wherePart.toString();
    }

    private void setCaseClauseIfNeeded(ColumnSpec columnSpec, Set<?> propIds) {
        if (hasConstraint(columnSpec)) {
            if (resultProcessor != null) {
                resultProcessor
                        .setCasePresent(columnSpec.getConstraint() == Operator.LIKE);
                Mappings filteredMappings = filterMappingsByTarget(
                        this.propIds, columnSpec.getMappings());
                selectClause.setCaseClause(
                        filteredSqlCodes(propIds, filteredMappings),
                        columnSpec, filteredMappings);
            }
        }
    }

    private String processConstraintSpecForWhereClause(ColumnSpec columnSpec,
            Set<?> propIds, boolean first) {

        StringBuilder wherePart = new StringBuilder();
        String expr = columnSpec.getExpr();
        if (expr != null) {
            if (!first) {
                wherePart.append(" AND ");
            }
            wherePart.append(expr);
        } else if (hasConstraint(columnSpec)) {
            setCaseClauseIfNeeded(columnSpec, null);
            wherePart
                    .append(processConstraint(columnSpec, propIds, null, first));
        }
        return wherePart.toString();
    }

    private String processPropertyValueFilter(ColumnSpec columnSpec,
            ValueComparator comparator, Value[] values, boolean first) {

        StringBuilder wherePart = new StringBuilder();

        Operator constraint = valueComparatorToSqlOp(comparator);
        if (columnSpec != null && constraint != null) {
            ValueExtractor ve = new ValueExtractor();
            for (Value value : values) {
                value.accept(ve);
            }
            wherePart.append(processConstraint(columnSpec, ve.values,
                    constraint, first));
        }

        return wherePart.toString();
    }

    private String processConstraint(ColumnSpec columnSpec, Set<?> propIds,
            Operator constraintOverride, boolean first) {
        StringBuilder wherePart = new StringBuilder();
        ColumnSpec cs = columnSpec.getLastSpec();
        Operator constraint = cs.getConstraint();
        if (constraintOverride != null) {
            constraint = constraintOverride;
        }
        Mappings propIdToSqlCodes = cs.getMappings();
        if (constraint != null && referenceIndices.getIndex(cs) > -1) {
            Mappings filteredConstraintValues
                    = filterMappingsByTarget(propIds, propIdToSqlCodes);
            if (!first) {
                wherePart.append(" AND ");
            }
            wherePart.append('(');
            Object[] sqlCodes = filteredSqlCodes(propIds,
                    filteredConstraintValues);
            wherePart.append(
                    WhereConstraintProcessor.getInstance(cs,
                            constraint, this, sqlCodes, referenceIndices)
                    .processConstraint()).append(')');
        }

        return wherePart.toString();
    }

    /**
     * Returns whether an IN clause containing the proposition ids of interest
     * should be added to the WHERE clause.
     *
     * @param entitySpecPropIds the proposition ids corresponding to the current
     * entity spec.
     * @return <code>true</code> if the query contains < 85% of the proposition
     * ids that are known to the data source and if the where clause would
     * contain less than or equal to 2000 codes.
     */
    private boolean needsPropIdInClause(String[] entitySpecPropIds) {

        Set<String> entitySpecPropIdsSet = Arrays.asSet(entitySpecPropIds);

        // Filter propIds that are not in the entitySpecPropIds array.
        List<String> filteredPropIds = new ArrayList<>(
                entitySpecPropIds.length);
        LOGGER.log(Level.FINE, "propIds {0}: ", new Object[]{StringUtils.join(propIds, ",")});
        for (String propId : propIds) {
            if (entitySpecPropIdsSet.contains(propId)) {
                filteredPropIds.add(propId);
            }
        }
        LOGGER.log(Level.FINE, "filteredPropIds {0}: ", new Object[]{StringUtils.join(filteredPropIds, ",")});
        LOGGER.log(Level.FINE, "Sizes: filteredPropIds {0}::entitySpecPropIds {1}: ", new Object[]{filteredPropIds.size(),entitySpecPropIds.length * 0.85f});
        
        return (filteredPropIds.size() < entitySpecPropIds.length * 0.85f)
                && (filteredPropIds.size() <= 2000);
    }

    private String processOrder(ColumnSpecInfo info) {
        StringBuilder wherePart = new StringBuilder();
        if (order != null && info.isUsingKeyIdIndex()) {
            ColumnSpec keyIdSpec = info.getColumnSpecs().get(0).getColumnSpec();
            wherePart.append(getOrderByClause(keyIdSpec)
                    .generateClause());
        }
        return wherePart.toString();
    }

    private static class ValueExtractor implements ValueVisitor {

        Set<Object> values = new HashSet<>();

        @Override
        public void visit(NominalValue nominalValue) {
            values.add(nominalValue.getString());
        }

        @Override
        public void visit(OrdinalValue ordinalValue) {
            values.add(ordinalValue.getValue());
        }

        @Override
        public void visit(BooleanValue booleanValue) {
            values.add(booleanValue.getBoolean());
        }

        @Override
        public void visit(ValueList<? extends Value> listValue) {
            for (Value val : listValue) {
                val.accept(this);
            }
        }

        @Override
        public void visit(NumberValue numberValue) {
            values.add(numberValue.getNumber());
        }

        @Override
        public void visit(InequalityNumberValue inequalityNumberValue) {
            throw new UnsupportedOperationException(
                    "inequalityNumberValue not supported");
        }

        @Override
        public void visit(DateValue dateValue) {
            values.add(dateValue.getDate());
        }
    }

    private static Operator valueComparatorToSqlOp(
            ValueComparator valueComparator) throws IllegalStateException {
        Operator constraint = null;
        switch (valueComparator) {
            case GREATER_THAN:
                constraint = Operator.GREATER_THAN;
                break;
            case LESS_THAN:
                constraint = Operator.LESS_THAN;
                break;
            case EQUAL_TO:
                constraint = Operator.EQUAL_TO;
                break;
            case GREATER_THAN_OR_EQUAL_TO:
                constraint = Operator.GREATER_THAN_OR_EQUAL_TO;
                break;
            case LESS_THAN_OR_EQUAL_TO:
                constraint = Operator.LESS_THAN_OR_EQUAL_TO;
                break;
            case IN:
                constraint = Operator.EQUAL_TO;
                break;
            case NOT_IN:
                constraint = Operator.NOT_EQUAL_TO;
                break;
            default:
                throw new AssertionError("invalid valueComparator: "
                        + valueComparator);
        }
        return constraint;
    }

    private static Mappings filterMappingsByTarget(Set<?> propIds, Mappings mappings) {
        Mappings filteredMappings;
        if (propIds != null && mappings != null) {
            filteredMappings = mappings.subMappingsByTargets(propIds.toArray(new String[propIds.size()]));
        } else {
            filteredMappings = mappings;
        }
        return filteredMappings;
    }

    private static Object[] filteredSqlCodes(Set<?> codes, Mappings mappings) {
        Object[] sqlCodes = null;
        if (mappings != null && !mappings.isEmpty()) {
            sqlCodes = mappings.readSources();
        } else if (codes != null) {
            sqlCodes = codes.toArray();
        }
        return sqlCodes;
    }

    private static boolean hasConstraint(ColumnSpec columnSpec) {
        if (columnSpec != null) {
            List<ColumnSpec> columnSpecL = columnSpec.asList();
            ColumnSpec cs = columnSpecL.get(columnSpecL.size() - 1);
            return cs.getConstraint() != null;
        }

        return false;
    }

}
