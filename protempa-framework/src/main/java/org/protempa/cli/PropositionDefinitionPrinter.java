package org.protempa.cli;

import org.protempa.AbstractPropositionDefinitionVisitor;
import org.protempa.ConstantDefinition;
import org.protempa.EventDefinition;
import org.protempa.HighLevelAbstractionDefinition;
import org.protempa.LowLevelAbstractionDefinition;
import org.protempa.PairDefinition;
import org.protempa.PrimitiveParameterDefinition;
import org.protempa.PropertyDefinition;
import org.protempa.PropositionDefinition;
import org.protempa.ReferenceDefinition;
import org.protempa.SliceDefinition;

/**
 * A helper class for {@link PrintPropositionDefinition} for printing a
 * proposition definition to the console.
 * 
 * @author Andrew Post
 */
class PropositionDefinitionPrinter
        extends AbstractPropositionDefinitionVisitor {

    @Override
    public void visit(EventDefinition eventDefinition) {
        System.out.println("Event definition " + eventDefinition.getId());
        printDisplayNames(eventDefinition);
        printProperties(eventDefinition);
        printReferences(eventDefinition);
    }

    @Override
    public void visit(
            HighLevelAbstractionDefinition highLevelAbstractionDefinition) {
        System.out.println("High level abstraction definition " +
                highLevelAbstractionDefinition.getId());
        printDisplayNames(highLevelAbstractionDefinition);
        printProperties(highLevelAbstractionDefinition);
        printReferences(highLevelAbstractionDefinition);
    }

    @Override
    public void visit(
            LowLevelAbstractionDefinition lowLevelAbstractionDefinition) {
        System.out.println("Low level abstraction definition " +
                lowLevelAbstractionDefinition.getId());
        printDisplayNames(lowLevelAbstractionDefinition);
        System.out.println("\tvalue: " +
                lowLevelAbstractionDefinition.getValueType());
        printProperties(lowLevelAbstractionDefinition);
        printReferences(lowLevelAbstractionDefinition);
    }

    @Override
    public void visit(
            PrimitiveParameterDefinition primitiveParameterDefinition) {
        System.out.println("Primitive parameter definition " +
                primitiveParameterDefinition.getId());
        printDisplayNames(primitiveParameterDefinition);
        System.out.println("\tvalue: " +
                primitiveParameterDefinition.getValueType());
        System.out.println("\tunits: " +
                primitiveParameterDefinition.getUnits());
        printProperties(primitiveParameterDefinition);
        printReferences(primitiveParameterDefinition);
    }

    @Override
    public void visit(SliceDefinition sliceAbstractionDefinition) {
        System.out.println("Slice abstraction definition " +
                sliceAbstractionDefinition.getId());
        printDisplayNames(sliceAbstractionDefinition);
        printProperties(sliceAbstractionDefinition);
        printReferences(sliceAbstractionDefinition);
    }

    @Override
    public void visit(ConstantDefinition constantDefinition) {
        System.out.println("Event definition " + constantDefinition.getId());
        printDisplayNames(constantDefinition);
        printProperties(constantDefinition);
        printReferences(constantDefinition);
    }

    @Override
    public void visit(PairDefinition pairDefinition) {
        System.out.println("Pair definition " + pairDefinition.getId());
        printDisplayNames(pairDefinition);
        printProperties(pairDefinition);
        printReferences(pairDefinition);
    }

    private void printReferences(PropositionDefinition propositionDefinition) {
        ReferenceDefinition[] refDefs =
                propositionDefinition.getReferenceDefinitions();
        if (refDefs.length == 0) {
            System.out.println("No references");
        } else {
            System.out.println(refDefs.length + " references:");
            for (ReferenceDefinition refDef : refDefs) {
                printReference(refDef);
            }
        }
    }

    private void printReference(ReferenceDefinition refDef) {
        System.out.println("\t" + refDef.getName() + ":");
        for (String propId : refDef.getPropositionIds()) {
            System.out.println("\t\t" + propId);
        }
    }

    private void printProperties(PropositionDefinition propositionDefinition) {
        PropertyDefinition[] propDefs =
                propositionDefinition.getPropertyDefinitions();
        if (propDefs.length == 0) {
            System.out.println("No properties");
        } else {
            System.out.println(propDefs.length + " properties:");
            for (PropertyDefinition propDef : propDefs) {
                printProperty(propDef);
            }
        }
    }

    private void printProperty(PropertyDefinition propDef) {
        String valueSetId = propDef.getValueSetId();
        if (valueSetId != null) {
            valueSetId = ", " + valueSetId;
        }
        System.out.println("\t" + propDef.getName() + ": " +
                propDef.getValueType() + valueSetId);
    }

    private void printDisplayNames(PropositionDefinition propositionDefinition) {
        System.out.println("Display name: " +
                propositionDefinition.getDisplayName());
        System.out.println("Abbreviated display name: " +
                propositionDefinition.getAbbreviatedDisplayName());
    }
}
