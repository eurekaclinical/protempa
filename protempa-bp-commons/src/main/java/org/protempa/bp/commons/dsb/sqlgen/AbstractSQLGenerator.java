package org.protempa.bp.commons.dsb.sqlgen;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.arp.javautil.arrays.Arrays;
import org.protempa.dsb.datasourceconstraint.AbstractDataSourceConstraintVisitor;
import org.protempa.dsb.datasourceconstraint.DataSourceConstraint;
import org.protempa.dsb.datasourceconstraint.PositionDataSourceConstraint;
import org.protempa.bp.commons.dsb.SQLOrderBy;
import org.protempa.proposition.value.AbsoluteTimeGranularity;

/**
 *
 * @author Andrew Post
 */
public abstract class AbstractSQLGenerator implements SQLGenerator {

    public abstract boolean checkCompatibility(Driver driver,
            Connection connection) throws SQLException;

    public abstract boolean isLimitingSupported();

    private static final String getPropIdsSQL =
            "select {0} from {4} " +
            "where {5}";

    public final String generateGetAllKeyIdsQuery(int start, int count,
            DataSourceConstraint dataSourceConstraints, 
            Map<PropertySpec, List<String>> specs) {
        ColumnSpecInfo info = new ColumnSpecInfoFactory().newInstance(specs);
        Map<ColumnSpec, Integer> referenceIndices =
                computeReferenceIndices(info.getColumnSpecs());
        ColumnSpecInfo info2 = new ColumnSpecInfo();
        info2.setDistinct(true);
        info2.setColumnSpecs(
                Collections.singletonList(info.getColumnSpecs().get(0)));
        StringBuilder selectClause = generateSelectClause(info2, referenceIndices);
        StringBuilder fromClause = generateFromClause(info.getColumnSpecs());
        StringBuilder whereClause = generateWhereClause(info, specs,
                dataSourceConstraints, selectClause, referenceIndices, null,
                null);
        String result = assembleGetAllKeyIdsQuery(
                selectClause, fromClause, whereClause, start, count);
        return result;
    }

    public final String generateReadPropositionsQuery(
            DataSourceConstraint dataSourceConstraints,
            Map<PropertySpec, List<String>> propositionSpecsForConstraints,
            Set<String> keyIds,
            SQLOrderBy order) {
        ColumnSpecInfo info = new ColumnSpecInfoFactory().newInstance(
                propositionSpecsForConstraints);
        Map<ColumnSpec, Integer> referenceIndices =
                computeReferenceIndices(info.getColumnSpecs());
        StringBuilder selectClause = generateSelectClause(info,
                referenceIndices);
        StringBuilder fromClause = generateFromClause(info.getColumnSpecs());
        StringBuilder whereClause = generateWhereClause(info,
                propositionSpecsForConstraints,
                dataSourceConstraints, selectClause, referenceIndices,
                keyIds, order);
        String result = assembleReadPropositionsQuery(
                selectClause, fromClause, whereClause);
        return result;
    }

    public abstract String assembleGetAllKeyIdsQuery(StringBuilder selectClause,
            StringBuilder fromClause,
            StringBuilder whereClause, int start, int count);

    public abstract String assembleReadPropositionsQuery(
            StringBuilder selectClause, StringBuilder fromClause,
            StringBuilder whereClause);

    private StringBuilder generateSelectClause(ColumnSpecInfo info,
            Map<ColumnSpec, Integer> referenceIndices) {
        StringBuilder selectPart = new StringBuilder();
        int i = 0;
        if (info.getFinishTimeIndex() > 0) {
            i++;
        }
        if (info.getPropertyValueIndices() != null) {
            i += info.getPropertyValueIndices().size();
        }
        if (info.getCodeIndex() > 0) {
            i++;
        }
        if (info.getStartTimeIndex() > 0) {
            i++;
        }
        i++; //key id
        int[] indices = new int[i];
        String[] names = new String[i];
        int k = 0;
        indices[k] = 0;
        names[k++] = "keyid";
        if (info.getCodeIndex() > 0) {
            indices[k] = info.getCodeIndex();
            names[k++] = "code";
        }
        if (info.getStartTimeIndex() > 0) {
            indices[k] = info.getStartTimeIndex();
            names[k++] = "starttime";
        }
        if (info.getFinishTimeIndex() > 0) {
            indices[k] = info.getFinishTimeIndex();
            names[k++] = "finishtime";
        }
        if (info.getPropertyValueIndices() != null) {
            for (Map.Entry<String, Integer> e :
                    info.getPropertyValueIndices().entrySet()) {
                indices[k] = e.getValue();
                names[k++] = e.getKey() + "_value";
            }
        }
        
        for (int j = 0; j < indices.length; j++) {
            ColumnSpec cs = info.getColumnSpecs().get(indices[j]);
            int index = referenceIndices.get(cs);
            String column = cs.getColumn();
            String name = names[j];
            boolean distinctRequested = j == 0 && info.isDistinct() &&
                    (indices.length > 1 || !cs.isUnique());
            boolean hasNext = j < indices.length - 1;
            if (column == null)
                throw new AssertionError("column cannot be null: " +
                    "index=" + index + "; name=" + name + "; " + cs);
            if (name == null)
                throw new AssertionError("name cannot be null");
            generateSelectColumn(distinctRequested, selectPart, index, column,
                    name, hasNext);
        }
        return selectPart;
    }

    public abstract void generateSelectColumn(boolean distinctRequested,
            StringBuilder selectPart, int index, String column, String name,
            boolean hasNext);

    public abstract void generateFromTable( String schema, String table,
        StringBuilder fromPart, int index);

    public abstract void generateFromTableReference(
            int i, StringBuilder fromPart);

    public abstract void generateOn(StringBuilder fromPart, int fromIndex,
            int toIndex, String fromKey, String toKey);
    
    public abstract void generateJoin(StringBuilder fromPart);

    private StringBuilder generateFromClause(List<ColumnSpec> columnSpecs) {
        Map<Integer, ColumnSpec> columnSpecCache =
                new HashMap<Integer, ColumnSpec>();
        int i = 1;
        StringBuilder fromPart = new StringBuilder();
        JoinSpec currentJoin = null;
        boolean begin = true;
        boolean firstJoin = false;
        int mainJoinTableIndex = -1;
        for (int j = 0, n = columnSpecs.size(); j < n; j++) {
            ColumnSpec cs = columnSpecs.get(j);
            boolean shouldGenerateTable = begin || currentJoin != null;
            if (shouldGenerateTable) {
                ColumnSpec colSpecFromCache = columnSpecCache.get(i);
                if (colSpecFromCache != null) {
                    generateFromTableReference(i, fromPart);
                } else {
                    String schema = cs.getSchema();
                    String table = cs.getTable();
                    generateFromTable(schema, table, fromPart,i);
                    columnSpecCache.put(i, cs);
                }
                begin = false;
            }

            if (currentJoin != null) {
                int k;
                if (firstJoin) {
                    k = mainJoinTableIndex;
                    firstJoin = false;
                } else {
                    k = i - 1;
                }
                generateOn(fromPart, k, i, currentJoin.getFromKey(),
                        currentJoin.getToKey());
            }

            if (cs.getJoin() != null) {
                generateJoin(fromPart);
                currentJoin = cs.getJoin();
                i++;
            } else {
                currentJoin = null;
                if (mainJoinTableIndex < 1)
                    mainJoinTableIndex = i;
                firstJoin = true;
            }
        }
        return fromPart;
    }

    private StringBuilder generateWhereClause(ColumnSpecInfo info,
            Map<PropertySpec,List<String>> specs,
            DataSourceConstraint dataSourceConstraints,
            StringBuilder selectPart,
            Map<ColumnSpec, Integer> referenceIndices,
            Set<String> keyIds, SQLOrderBy order) {
        StringBuilder wherePart = new StringBuilder();
        int i = 1;
        for (PropertySpec propositionSpec : specs.keySet()) {
            i = processKeySpecForWhereClause(propositionSpec, i);
            i = processStartTimeSpecForWhereClause(propositionSpec, i,
                    dataSourceConstraints, wherePart, referenceIndices);
            i = processFinishTimeSpecForWhereClause(propositionSpec, i,
                    dataSourceConstraints, wherePart);
            i = processPropertyValueSpecsForWhereClause(propositionSpec, i,
                    dataSourceConstraints, wherePart);
            i = processConstraintSpecsForWhereClause(propositionSpec, i,
                    wherePart, selectPart, referenceIndices);
        }
        processKeyIdConstraintsForWhereClause(info, wherePart, keyIds);

        if (order != null && info.getStartTimeIndex() >= 0) {
            ColumnSpec startColSpec =
                    info.getColumnSpecs().get(info.getStartTimeIndex());
            int start = referenceIndices.get(startColSpec);
            String startCol = startColSpec.getColumn();

            ColumnSpec finishColSpec;
            String finishCol;
            if (info.getFinishTimeIndex() >= 0) {
                finishColSpec =
                        info.getColumnSpecs().get(info.getFinishTimeIndex());
                finishCol = finishColSpec.getColumn();
            } else {
                finishColSpec = null;
                finishCol = null;
            }

            int finish;
            if (info.getFinishTimeIndex() >= 0) {
                 finish = referenceIndices.get(
                         info.getColumnSpecs().get(info.getFinishTimeIndex()));
            } else {
                finish = -1;
            }
            processOrderBy(start, startCol, finish, finishCol, wherePart,
                    order);
        }

        return wherePart;
    }

    private int processPropertyValueSpecsForWhereClause(
            PropertySpec propositionSpec,
            int i, DataSourceConstraint dataSourceConstraints,
            StringBuilder wherePart) {
        Map<String, ColumnSpec> propertyValueSpecs =
                propositionSpec.getPropertyValueSpecs();
        if (propertyValueSpecs != null) {
            Map<String, Integer> propertyValueIndices =
                    new HashMap<String, Integer>();
            for (Map.Entry<String, ColumnSpec> e :
                propertyValueSpecs.entrySet()) {
                ColumnSpec spec = e.getValue();
                if (spec != null) {
                    i += spec.asList().size();
                    propertyValueIndices.put(e.getKey(), i);
                }
            }
        }
        return i;
    }

    private int processConstraintSpecsForWhereClause(
            PropertySpec propositionSpec,
            int i, StringBuilder wherePart, StringBuilder selectPart,
            Map<ColumnSpec, Integer> referenceIndices) {
        //SECTION FOR NON-TIME DATA SOURCE CONSTRAINTS.
        boolean selectPartHasCaseStmt = false;
        ColumnSpec[] constraintSpecs = propositionSpec.getConstraintSpecs();
        if (constraintSpecs != null) {
            for (ColumnSpec cs3 : constraintSpecs) {
                i = processConstraintSpecForWhereClause(cs3, i, wherePart,
                        selectPart, referenceIndices, selectPartHasCaseStmt);
            }
        }
        ColumnSpec codeSpec = propositionSpec.getCodeSpec();
        i = processConstraintSpecForWhereClause(codeSpec, i, wherePart,
                        selectPart, referenceIndices, selectPartHasCaseStmt);
        if (selectPartHasCaseStmt) {
            selectPart.append(" else 'OTHER' end ");
            selectPart.append("code ");
        }
        return i;
    }

    private int processConstraintSpecForWhereClause(
            ColumnSpec cs3,
            int i, StringBuilder wherePart, StringBuilder selectPart,
            Map<ColumnSpec, Integer> referenceIndices,
            boolean selectPartHasCaseStmt) {
        if (cs3 == null) {
            return i;
        }
        while (true) {
            if (cs3.getJoin() != null) {
                cs3 = cs3.getJoin().getNextColumnSpec();
                i++;
            } else {
                if (wherePart.length() > 0) {
                    wherePart.append(" and ");
                }
                wherePart.append('(');
                ColumnSpec.Constraint csc = cs3.getConstraint();
                if (csc != null) {
                    ColumnSpec.ConstraintValue[] cvs =
                            cs3.getConstraintValues();
                    switch (csc) {
                        case EQUAL_TO:
                            wherePart.append("a");
                            wherePart.append(referenceIndices.get(cs3));
                            wherePart.append(".");
                            wherePart.append(cs3.getColumn());
                            if (cvs.length > 1) {
                                generateInClause(wherePart, cs3);
                            } else {
                                wherePart.append("=");
                                appendValue(cvs[0].getValue(),
                                        wherePart);
                            }
                            break;
                        case LIKE:
                            if (!selectPartHasCaseStmt) {
                                selectPart.append(", case ");
                                selectPartHasCaseStmt = true;
                            }
                            if (cvs.length > 1) {
                                wherePart.append('(');
                            }
                            for (int k = 0; k < cvs.length; k++) {
                                wherePart.append("a");
                                wherePart.append(
                                        referenceIndices.get(cs3));
                                wherePart.append(".");
                                wherePart.append(cs3.getColumn());
                                wherePart.append(" LIKE ");
                                appendValue(cvs[k].getValue(),
                                        wherePart);
                                if (k + 1 < cvs.length) {
                                    wherePart.append(" or ");
                                }
                                selectPart.append("when ");
                                selectPart.append("a");
                                selectPart.append(
                                        referenceIndices.get(cs3));
                                selectPart.append(".");
                                selectPart.append(cs3.getColumn());
                                selectPart.append(" like ");
                                appendValue(cvs[k].getValue(),
                                        selectPart);
                                selectPart.append(" then ");
                                selectPart.append("'");
                                appendValue(cvs[k].getCode(),
                                        selectPart);
                                selectPart.append("'");
                            }
                            if (cvs.length > 1) {
                                wherePart.append(')');
                            }
                            break;
                        default:
                            throw new AssertionError(
                                    "should not happen");
                    }
                }
                wherePart.append(')');
                break;
            }
        }
        return i;
    }

    private int processFinishTimeSpecForWhereClause(
            PropertySpec propositionSpec, int i,
            DataSourceConstraint dataSourceConstraints,
            StringBuilder wherePart) {
        ColumnSpec finishTimeSpec = propositionSpec.getFinishTimeSpec();
        if (finishTimeSpec != null) {
            while (true) {
                if (finishTimeSpec.getJoin() != null) {
                    finishTimeSpec =
                            finishTimeSpec.getJoin().getNextColumnSpec();
                    i++;
                } else {
                    if (dataSourceConstraints != null) {
                        for (Iterator<DataSourceConstraint> itr =
                                dataSourceConstraints.andIterator();
                                itr.hasNext();) {
                            DataSourceConstraint dsc2 = itr.next();
                            if (dsc2 instanceof PositionDataSourceConstraint) {
                                PositionDataSourceConstraint pdsc2 =
                                        (PositionDataSourceConstraint) dsc2;
                                if (wherePart.length() > 0) {
                                    wherePart.append(" and ");
                                }
                                wherePart.append('a').append(i).append('.');
                                wherePart.append(finishTimeSpec.getColumn());
                                wherePart.append(" <= {ts '");
                                wherePart.append(
                                        AbsoluteTimeGranularity.toSQLString(
                                        pdsc2.getMaximumFinish()));
                                wherePart.append("'}");
                            }
                        }
                    }
                    break;
                }
            }
        }
        return i;
    }

    private int processStartTimeSpecForWhereClause(
            PropertySpec propertySpec, int i,
            DataSourceConstraint dataSourceConstraints,
            StringBuilder wherePart,
            Map<ColumnSpec, Integer> referenceIndices) {
        ColumnSpec startTimeSpec = propertySpec.getStartTimeSpec();
        if (startTimeSpec != null) {
            while (true) {
                if (startTimeSpec.getJoin() != null) {
                    startTimeSpec = startTimeSpec.getJoin().getNextColumnSpec();
                    i++;
                } else {
                    if (dataSourceConstraints != null) {
                        for (Iterator<DataSourceConstraint> itr =
                                dataSourceConstraints.andIterator();
                                itr.hasNext();) {
                            DataSourceConstraint dsc = itr.next();
                            if (!Arrays.contains(propertySpec.getCodes(),
                                    dsc.getPropositionId()))
                                continue;
                            if (dsc instanceof PositionDataSourceConstraint) {
                                PositionDataSourceConstraint pdsc2 =
                                        (PositionDataSourceConstraint) dsc;

                                if (wherePart.length() > 0) {
                                    wherePart.append(" and ");
                                }

                                wherePart.append('a');
                                wherePart.append(
                                        referenceIndices.get(startTimeSpec));
                                wherePart.append('.');
                                wherePart.append(startTimeSpec.getColumn());
                                wherePart.append(" >= {ts '");
                                wherePart.append(
                                        AbsoluteTimeGranularity.toSQLString(
                                        pdsc2.getMinimumStart()));
                                wherePart.append("'}");
                                if (propertySpec.getFinishTimeSpec() == null) {
                                    wherePart.append(" and ");
                                    wherePart.append('a');
                                    wherePart.append(
                                        referenceIndices.get(startTimeSpec));
                                    wherePart.append('.');
                                    wherePart.append(startTimeSpec.getColumn());
                                    wherePart.append(" <= {ts '");
                                    wherePart.append(
                                            AbsoluteTimeGranularity.toSQLString(
                                            pdsc2.getMaximumFinish()));
                                    wherePart.append("'}");
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }
        return i;
    }

    private int processKeySpecForWhereClause(PropertySpec propositionSpec,
            int i) {
        ColumnSpec keyColumnSpec = propositionSpec.getEntitySpec().getKeySpec();
        i += keyColumnSpec.asList().size();
        return i;
    }

    public abstract void generateInClause(StringBuilder wherePart,
            ColumnSpec columnSpec);

    public abstract void appendValue(Object val, StringBuilder wherePart);

    public abstract void generateFromSeparator(StringBuilder fromPart);

    private Map<ColumnSpec, Integer> computeReferenceIndices(
            List<ColumnSpec> columnSpecs) {
        Map<ColumnSpec, Integer> result = new HashMap<ColumnSpec, Integer>();
        
        int i = 1;
        JoinSpec currentJoin = null;
        boolean begin = true;
        for (int j = 0, n = columnSpecs.size(); j < n; j++) {
            ColumnSpec cs = columnSpecs.get(j);
            boolean shouldGenerateTable = begin || currentJoin != null;
            if (shouldGenerateTable) {
                result.put(cs, i);
                begin = false;
            }

            if (cs.getJoin() != null) {
                currentJoin = cs.getJoin();
                i++;
            } else {
                currentJoin = null;
                begin = true;
            }
        }
        return result;
    }

    private void processKeyIdConstraintsForWhereClause(ColumnSpecInfo info,
            StringBuilder wherePart, Set<String> keyIds) {
        if (keyIds != null && !keyIds.isEmpty()) {
            if (wherePart.length() > 0)
                wherePart.append(" and ");
            ColumnSpec keySpec = info.getColumnSpecs().get(0);
            wherePart.append("a1.").append(keySpec.getColumn())
                    .append(" in ('");
            wherePart.append(
                    org.arp.javautil.collections.Collections.join(
                    keyIds, "','"));
            wherePart.append("')");
        }
    }

    public abstract void processOrderBy(int startReferenceIndex,
            String startColumn, int finishReferenceIndex, String finishColumn,
            StringBuilder wherePart, SQLOrderBy sQLOrderBy);



    private class GetAllKeyIdsDataSourceConstraintVisitor extends
            AbstractDataSourceConstraintVisitor {

        private Long minStart;
        private Long maxFinish;
        private String propId;

        @Override
        public void visit(PositionDataSourceConstraint constraint) {
            this.minStart = constraint.getMinimumStart();
            this.maxFinish = constraint.getMaximumFinish();
            this.propId = constraint.getPropositionId();
        }

        /**
         * @return the minStart
         */
        Long getMinimumStart() {
            return this.minStart;
        }

        /**
         * @return the minFinish
         */
        Long getMaximumFinish() {
            return this.maxFinish;
        }

        String getPropositionId() {
            return this.propId;
        }
    }

}
