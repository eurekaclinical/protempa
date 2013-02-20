/*
 * #%L
 * Protempa Framework
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
package org.protempa.cli;

import org.apache.commons.lang.StringUtils;
import org.protempa.AbstractPropositionDefinitionVisitor;
import org.protempa.CompoundLowLevelAbstractionDefinition;
import org.protempa.ConstantDefinition;
import org.protempa.ContextDefinition;
import org.protempa.EventDefinition;
import org.protempa.HighLevelAbstractionDefinition;
import org.protempa.LowLevelAbstractionDefinition;
import org.protempa.SequentialTemporalPatternDefinition;
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
class PropositionDefinitionPrinter extends AbstractPropositionDefinitionVisitor {

    @Override
    public void visit(EventDefinition eventDefinition) {
        System.out.println("Event definition " + eventDefinition.getId());
        printCommon(eventDefinition);
    }
    
    @Override
    public void visit(ContextDefinition contextDefinition) {
        System.out.println("Context definition " + contextDefinition.getId());
        printCommon(contextDefinition);
    }

    @Override
    public void visit(
            HighLevelAbstractionDefinition highLevelAbstractionDefinition) {
        System.out.println("High level abstraction definition "
                + highLevelAbstractionDefinition.getId());
        printCommon(highLevelAbstractionDefinition);
    }

    @Override
    public void visit(
            LowLevelAbstractionDefinition lowLevelAbstractionDefinition) {
        System.out.println("Low level abstraction definition "
                + lowLevelAbstractionDefinition.getId());
        System.out.println("\tvalue: "
                + lowLevelAbstractionDefinition.getValueType());
        printCommon(lowLevelAbstractionDefinition);
    }

    @Override
    public void visit(
            CompoundLowLevelAbstractionDefinition compoundLowLevelAbstractionDefinition) {
        System.out.println("Extended low level abstraction definition "
                + compoundLowLevelAbstractionDefinition.getId());
        printCommon(compoundLowLevelAbstractionDefinition);
    }

    @Override
    public void visit(PrimitiveParameterDefinition primitiveParameterDefinition) {
        System.out.println("Primitive parameter definition "
                + primitiveParameterDefinition.getId());
        System.out.println("\tvalue: "
                + primitiveParameterDefinition.getValueType());
        System.out.println("\tunits: "
                + primitiveParameterDefinition.getUnits());
        printCommon(primitiveParameterDefinition);
    }

    @Override
    public void visit(SliceDefinition sliceAbstractionDefinition) {
        System.out.println("Slice abstraction definition "
                + sliceAbstractionDefinition.getId());
        printCommon(sliceAbstractionDefinition);
    }

    @Override
    public void visit(ConstantDefinition constantDefinition) {
        System.out.println("Constant definition " + constantDefinition.getId());
        printCommon(constantDefinition);
    }

    @Override
    public void visit(SequentialTemporalPatternDefinition pairDefinition) {
        System.out.println("Pair definition " + pairDefinition.getId());
        printCommon(pairDefinition);
    }

    private void printCommon(PropositionDefinition propositionDefinition) {
        System.out.println("\tpropositionId: " + 
                propositionDefinition.getPropositionId());
        printDisplayNames(propositionDefinition);
        System.out.println("\tsolid: " + propositionDefinition.isSolid());
        System.out.println("\tconcatenable: "
                + propositionDefinition.isConcatenable());
        printProperties(propositionDefinition);
        printReferences(propositionDefinition);
        printTerms(propositionDefinition);
    }

    private void printReferences(PropositionDefinition propositionDefinition) {
        ReferenceDefinition[] refDefs = propositionDefinition
                .getReferenceDefinitions();
        if (refDefs.length == 0) {
            System.out.println("\tNo references");
        } else {
            System.out.println("\t" + refDefs.length + " references:");
            for (ReferenceDefinition refDef : refDefs) {
                printReference(refDef);
            }
        }
    }

    private void printReference(ReferenceDefinition refDef) {
        System.out.print("\t\t" + refDef.getName() + ": ");
        System.out.println(StringUtils.join(refDef.getPropositionIds(), ", "));
    }

    private void printProperties(PropositionDefinition propositionDefinition) {
        PropertyDefinition[] propDefs = propositionDefinition
                .getPropertyDefinitions();
        if (propDefs.length == 0) {
            System.out.println("\tNo properties");
        } else {
            System.out.println("\t" + propDefs.length + " properties:");
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
        System.out.println("\t\t" + propDef.getName() + ": "
                + propDef.getValueType() + valueSetId);
    }

    private void printDisplayNames(PropositionDefinition propositionDefinition) {
        System.out.println("\tDisplay name: "
                + propositionDefinition.getDisplayName());
        System.out.println("\tAbbreviated display name: "
                + propositionDefinition.getAbbreviatedDisplayName());
    }

    private void printTerms(PropositionDefinition propositionDefinition) {
        System.out.println("\tAssociated terms: "
                + StringUtils.join(propositionDefinition.getTermIds(), ", "));
    }
}
