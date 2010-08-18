package org.protempa.bp.commons.dsb.sqlgen;

import java.io.Serializable;
import org.protempa.proposition.value.GranularityFactory;
import org.protempa.proposition.value.UnitFactory;

/**
 * Specifies mappings from propositions to relational database tables.
 * Instances of this class tell a {@link RelationalDatabaseDataSourceBackend}
 * how to query a relational database for propositions.
 * 
 * @author Andrew Post
 */
public class RelationalDatabaseSpec implements Serializable {

    private EntitySpec[] primitiveParameterSpecs;
    private EntitySpec[] eventSpecs;
    private EntitySpec[] constantParameterSpecs;
    private UnitFactory units;
    private GranularityFactory granularities;

    /**
     * Instantiates this class with no mappings or other field values.
     */
    public RelationalDatabaseSpec() {
    }

    /**
     * Instantiates this class with the specified mappings from primitive
     * parameters, events and constant parameters to relational database
     * tables, and unit and granularity types.
     * 
     * @param primitiveParameterSpecs a {@link PropertySpec[]} containing
     * mappings from primitive parameters to relational database tables.
     * @param eventSpecs a {@link PropertySpec[]} containing mappings from
     * events to relational database tables.
     * @param constantParameterSpecs a {@link PropertySpec[]} containing
     * mappings from constant parameters to relational database tables.
     * @param units what {@link UnitFactory} from which to get units.
     * @param granularities what {@link GranularityFactory} from which to get
     * granularities.
     */
    public RelationalDatabaseSpec(
            EntitySpec[] primitiveParameterSpecs,
            EntitySpec[] eventSpecs,
            EntitySpec[] constantParameterSpecs,
            UnitFactory units,
            GranularityFactory granularities) {
        this.primitiveParameterSpecs = primitiveParameterSpecs;
        this.eventSpecs = eventSpecs;
        this.constantParameterSpecs = constantParameterSpecs;
        this.units = units;
        this.granularities = granularities;
    }

    /**
     * Gets the granularity factory from which the
     * {@link RelationalDatabaseDataSourceBackend} should get granularities.
     * @return a {@link GranularityFactory}.
     */
    public GranularityFactory getGranularities() {
        return granularities;
    }

    /**
     * Sets the granularity factory from which the
     * {@link RelationalDatabaseDataSourceBackend} should get granularities.
     * @param granularities a {@link GranularityFactory}.
     */
    public void setGranularities(GranularityFactory granularities) {
        this.granularities = granularities;
    }

    public UnitFactory getUnits() {
        return units;
    }

    public void setUnits(UnitFactory units) {
        this.units = units;
    }

    public EntitySpec[] getConstantParameterSpecs() {
        return constantParameterSpecs;
    }

    public void setConstantParameterSpecs(
            EntitySpec[] constantParameterSpecs) {
        this.constantParameterSpecs = constantParameterSpecs;
    }

    public EntitySpec[] getEventSpecs() {
        return eventSpecs;
    }

    public void setEventSpecs(EntitySpec[] eventSpecs) {
        this.eventSpecs = eventSpecs;
    }

    public EntitySpec[] getPrimitiveParameterSpecs() {
        return primitiveParameterSpecs;
    }

    public void setPrimitiveParameterSpecs(
            EntitySpec[] primitiveParameterSpecs) {
        this.primitiveParameterSpecs = primitiveParameterSpecs;
    }
}
