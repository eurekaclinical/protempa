package org.protempa.bp.commons.dsb;

import java.io.Serializable;
import org.protempa.proposition.value.GranularityFactory;
import org.protempa.proposition.value.UnitFactory;

/**
 *
 * @author Andrew Post
 */
public class RelationalDatabaseSpec implements Serializable {
    private PropositionSpec[] primitiveParameterSpecs;
    private PropositionSpec[] eventSpecs;
    private PropositionSpec[] constantParameterSpecs;
    private TableSpec[] tableSpecs;
    private TableSpec keysTable;
    private UnitFactory units;
    private GranularityFactory granularities;

    public RelationalDatabaseSpec() {
    }

    public RelationalDatabaseSpec(PropositionSpec[] primitiveParameterSpecs,
            PropositionSpec[] eventSpecs,
            PropositionSpec[] constantParameterSpecs, 
            TableSpec[] tableSpecs, TableSpec keysTable, UnitFactory units,
            GranularityFactory granularities) {
        this.primitiveParameterSpecs = primitiveParameterSpecs;
        this.eventSpecs = eventSpecs;
        this.constantParameterSpecs = constantParameterSpecs;
        this.tableSpecs = tableSpecs;
        this.keysTable = keysTable;
        this.units = units;
        this.granularities = granularities;
    }

    public GranularityFactory getGranularities() {
        return granularities;
    }

    public void setGranularities(GranularityFactory granularities) {
        this.granularities = granularities;
    }

    public UnitFactory getUnits() {
        return units;
    }

    public void setUnits(UnitFactory units) {
        this.units = units;
    }

    public PropositionSpec[] getConstantParameterSpecs() {
        return constantParameterSpecs;
    }

    public void setConstantParameterSpecs(
            PropositionSpec[] constantParameterSpecs) {
        this.constantParameterSpecs = constantParameterSpecs;
    }

    public PropositionSpec[] getEventSpecs() {
        return eventSpecs;
    }

    public void setEventSpecs(PropositionSpec[] eventSpecs) {
        this.eventSpecs = eventSpecs;
    }

    public TableSpec getKeysTable() {
        return keysTable;
    }

    public void setKeysTable(TableSpec keysTable) {
        this.keysTable = keysTable;
    }

    public PropositionSpec[] getPrimitiveParameterSpecs() {
        return primitiveParameterSpecs;
    }

    public void setPrimitiveParameterSpecs(
            PropositionSpec[] primitiveParameterSpecs) {
        this.primitiveParameterSpecs = primitiveParameterSpecs;
    }

    public TableSpec[] getTableSpecs() {
        return tableSpecs;
    }

    public void setTableSpecs(TableSpec[] tableSpecs) {
        this.tableSpecs = tableSpecs;
    }
    
}
