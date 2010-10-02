package org.protempa.bp.commons.dsb.sqlgen;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import org.arp.javautil.arrays.Arrays;
import org.protempa.DataSourceBackendFailedValidationException;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.PropertyDefinition;
import org.protempa.PropositionDefinition;
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
    private static final long serialVersionUID = -7404642542962229266L;

    private static final EntitySpec[] EMPTY_ARR = new EntitySpec[0];

    private EntitySpec[] primitiveParameterSpecs = EMPTY_ARR;
    private EntitySpec[] eventSpecs = EMPTY_ARR;
    private EntitySpec[] constantSpecs = EMPTY_ARR;
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
     * @param constantSpecs a {@link PropertySpec[]} containing
     * mappings from constants to relational database tables.
     * @param units what {@link UnitFactory} from which to get units.
     * @param granularities what {@link GranularityFactory} from which to get
     * granularities.
     */
    public RelationalDatabaseSpec(
            EntitySpec[] primitiveParameterSpecs,
            EntitySpec[] eventSpecs,
            EntitySpec[] constantSpecs,
            UnitFactory units,
            GranularityFactory granularities) {
        setPrimitiveParameterSpecs(primitiveParameterSpecs);
        setConstantSpecs(constantSpecs);
        setEventSpecs(eventSpecs);
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

    public EntitySpec[] getConstantSpecs() {
        return constantSpecs.clone();
    }

    public void setConstantSpecs(
            EntitySpec[] constantParameterSpecs) {
        if (constantParameterSpecs == null)
            this.constantSpecs = EMPTY_ARR;
        else
            this.constantSpecs = constantParameterSpecs.clone();
    }

    public EntitySpec[] getEventSpecs() {
        return eventSpecs.clone();
    }

    public void setEventSpecs(EntitySpec[] eventSpecs) {
        if (eventSpecs == null)
            this.eventSpecs = EMPTY_ARR;
        else
            this.eventSpecs = eventSpecs.clone();
    }

    public EntitySpec[] getPrimitiveParameterSpecs() {
        return primitiveParameterSpecs.clone();
    }

    public void setPrimitiveParameterSpecs(
            EntitySpec[] primitiveParameterSpecs) {
        if (primitiveParameterSpecs == null)
            this.primitiveParameterSpecs = EMPTY_ARR;
        else
            this.primitiveParameterSpecs = primitiveParameterSpecs.clone();
    }

    public void validate(KnowledgeSource knowledgeSource) 
            throws KnowledgeSourceReadException,
            DataSourceBackendFailedValidationException {
        List<EntitySpec> allSpecs = Arrays.asList(this.eventSpecs,
                this.constantSpecs, this.primitiveParameterSpecs);

        Logger logger = SQLGenUtil.logger();
        for (EntitySpec entitySpec : allSpecs) {
            String entitySpecName = entitySpec.getName();
            logger.finer("Validating entity spec " + entitySpecName);
            String[] propIds = entitySpec.getPropositionIds();
            Set<String> propNames = new HashSet<String>();
            PropertySpec[] propSpecs = entitySpec.getPropertySpecs();
            logger.finer("Checking for duplicate properties");
            for (PropertySpec propSpec : propSpecs) {
                String propSpecName = propSpec.getName();
                if (!propNames.add(propSpecName))
                    throw new DataSourceBackendFailedValidationException(
                            "Duplicate property name " + propSpecName +
                            " in entity spec " + entitySpecName);
            }
            logger.finer("No duplicate properties found");
            logger.finer(
                    "Checking for invalid proposition ids and properties");
            for (String propId : propIds) {
                PropositionDefinition propDef =
                        knowledgeSource.readPropositionDefinition(propId);
                if (!knowledgeSource.hasPropositionDefinition(propId)) {
                    throw new DataSourceBackendFailedValidationException(
                            "Invalid proposition id named in entity spec "
                            + entitySpecName + ": " + propId);
                }
                PropertyDefinition[] propertyDefs =
                        propDef.getPropertyDefinitions();
                for (PropertyDefinition propertyDef : propertyDefs) {
                    String propName = propertyDef.getName();
                    if (!propNames.contains(propName)) {
                        throw new DataSourceBackendFailedValidationException(
                                "Required property " + propName +
                                " missing from entity spec " + entitySpecName);
                    }

                }
            }
            logger.finer("No invalid proposition ids or properties found");
        }
    }
}
