package org.protempa.backend.dsb.relationaldb;

import java.util.List;
import org.arp.javautil.string.StringUtil;

/**
 *
 * @author Andrew Post
 */
final class IntColumnSpecWrapper implements IColumnSpec {
    private final ColumnSpec columnSpec;
    private boolean newInFromClause;

    public IntColumnSpecWrapper(ColumnSpec columnSpec) {
        this.columnSpec = columnSpec;
    }
    
    @Override
    public String getColumn() {
        return this.columnSpec.getColumn();
    }

    @Override
    public ColumnOp getColumnOp() {
        return this.columnSpec.getColumnOp();
    }

    @Override
    public Operator getConstraint() {
        return this.columnSpec.getConstraint();
    }

    @Override
    public JoinSpec getJoin() {
        return this.columnSpec.getJoin();
    }

    @Override
    public KnowledgeSourceIdToSqlCode[] getPropositionIdToSqlCodes() {
        return this.columnSpec.getPropositionIdToSqlCodes();
    }

    @Override
    public String getSchema() {
        return this.columnSpec.getSchema();
    }

    @Override
    public String getTable() {
        return this.columnSpec.getTable();
    }

    @Override
    public boolean isPropositionIdsComplete() {
        return this.columnSpec.isPropositionIdsComplete();
    }

    public boolean isNewInFromClause() {
        return this.newInFromClause;
    }

    public void setNewInFromClause(boolean newInFromClause) {
        this.newInFromClause = newInFromClause;
    }
    
    ColumnSpec getColumnSpec() {
        return this.columnSpec;
    }
    
    /**
     * Returns whether the given column specification has the same schema and
     * table as this one.
     * 
     * @param columnSpec
     *            a {@link ColumnSpec}.
     * @return <code>true</code> if the given column specification has the same
     *         schema and table as this one, <code>false</code> if not.
     */
    boolean isSameSchemaAndTable(IntColumnSpecWrapper columnSpec) {
        return StringUtil.equals(columnSpec.getSchema(), getSchema())
                && StringUtil.equals(columnSpec.getTable(), getTable());
    }

    /**
     * Returns whether the given column specification has the same schema and
     * table as this one.
     * 
     * @param tableSpec
     *            a {@link TableSpec}.
     * @return <code>true</code> if the given table specification has the same
     *         schema and table as this one, <code>false</code> if not.
     */
    boolean isSameSchemaAndTable(TableSpec tableSpec) {
        return StringUtil.equals(tableSpec.getSchema(), getSchema())
                && StringUtil.equals(tableSpec.getTable(), getTable());
    }
    
    List<ColumnSpec> asList() {
        return this.columnSpec.asList();
    }
    
}
