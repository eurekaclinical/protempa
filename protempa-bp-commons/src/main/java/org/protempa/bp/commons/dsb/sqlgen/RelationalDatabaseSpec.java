package org.protempa.bp.commons.dsb.sqlgen;

import java.io.Serializable;
import org.protempa.proposition.value.GranularityFactory;
import org.protempa.proposition.value.UnitFactory;

/**
 *
 * @author Andrew Post
 */
public class RelationalDatabaseSpec implements Serializable {
    private PropertySpec[] primitiveParameterSpecs;
    private PropertySpec[] eventSpecs;
    private PropertySpec[] constantParameterSpecs;
    private UnitFactory units;
    private GranularityFactory granularities;

    public RelationalDatabaseSpec() {
    }

    public RelationalDatabaseSpec(
            PropertySpec[] primitiveParameterSpecs,
            PropertySpec[] eventSpecs,
            PropertySpec[] constantParameterSpecs,
            UnitFactory units,
            GranularityFactory granularities) {
        this.primitiveParameterSpecs = primitiveParameterSpecs;
        this.eventSpecs = eventSpecs;
        this.constantParameterSpecs = constantParameterSpecs;
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

    public PropertySpec[] getConstantParameterSpecs() {
        return constantParameterSpecs;
    }

    public void setConstantParameterSpecs(
            PropertySpec[] constantParameterSpecs) {
        this.constantParameterSpecs = constantParameterSpecs;
    }

    public PropertySpec[] getEventSpecs() {
        return eventSpecs;
    }

    public void setEventSpecs(PropertySpec[] eventSpecs) {
        this.eventSpecs = eventSpecs;
    }

    public PropertySpec[] getPrimitiveParameterSpecs() {
        return primitiveParameterSpecs;
    }

    public void setPrimitiveParameterSpecs(
            PropertySpec[] primitiveParameterSpecs) {
        this.primitiveParameterSpecs = primitiveParameterSpecs;
    }
    
}
