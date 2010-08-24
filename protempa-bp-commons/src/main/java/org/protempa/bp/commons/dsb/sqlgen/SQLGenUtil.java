package org.protempa.bp.commons.dsb.sqlgen;

import java.util.Arrays;
import java.util.logging.Logger;
import org.protempa.dsb.filter.Filter;

/**
 *
 * @author Andrew Post
 */
class SQLGenUtil {

    private SQLGenUtil() {
    }

    private static class LazyLoggerHolder {

        private static Logger instance =
                Logger.getLogger(SQLGenUtil.class.getPackage().getName());
    }

    /**
     * Gets the logger for this package.
     *
     * @return a {@link Logger} object.
     */
    static Logger logger() {
        return LazyLoggerHolder.instance;
    }

    static boolean columnSpecsEquals(ColumnSpec colSpec1,
            ColumnSpec colSpec2) {
        assert colSpec1 != null : "colSpec1 cannot be null";
        assert colSpec2 != null : "colSpec2 cannot be null";
        if ((colSpec1.getSchema() == null) ? (colSpec2.getSchema() != null) :
            !colSpec1.getSchema().equals(colSpec2.getSchema())) {
            return false;
        }
        if ((colSpec1.getTable() == null) ? (colSpec2.getTable() != null) :
            !colSpec1.getTable().equals(colSpec2.getTable())) {
            return false;
        }
        if ((colSpec1.getColumn() == null) ? (colSpec2.getColumn() != null) :
            !colSpec1.getColumn().equals(colSpec2.getColumn())) {
            return false;
        }
        JoinSpec colSpec1Join = colSpec1.getJoin();
        JoinSpec colSpec2Join = colSpec2.getJoin();
        if ((colSpec1Join == null) ? (colSpec2Join != null) :
            (!colSpec1Join.getFromKey().equals(colSpec2Join.getFromKey()) ||
                    !colSpec1Join.getToKey().equals(colSpec2Join.getToKey()) ||
                    !columnSpecsEquals(colSpec1Join.getNextColumnSpec(),
                    colSpec2Join.getNextColumnSpec()))) {
                return false;
        }
        if (colSpec1.getConstraint() != colSpec2.getConstraint() &&
                (colSpec1.getConstraint() == null ||
                !colSpec1.getConstraint().equals(colSpec2.getConstraint()))) {
            return false;
        }
        if (!Arrays.deepEquals(colSpec1.getPropositionIdToSqlCodes(),
                colSpec2.getPropositionIdToSqlCodes())) {
            return false;
        }
        return true;
    }
}
