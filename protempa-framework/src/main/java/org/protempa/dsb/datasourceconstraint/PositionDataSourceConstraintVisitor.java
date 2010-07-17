package org.protempa.dsb.datasourceconstraint;

import org.protempa.AbstractPropositionDefinitionVisitor;
import org.protempa.EventDefinition;
import org.protempa.ExtendedPropositionDefinition;
import org.protempa.HighLevelAbstractionDefinition;
import org.protempa.KnowledgeSource;
import org.protempa.PrimitiveParameterDefinition;
import org.protempa.PropositionDefinition;
import org.protempa.TemporalExtendedPropositionDefinition;

/**
 *
 * @author Andrew Post
 */
class PositionDataSourceConstraintVisitor 
        extends AbstractPropositionDefinitionVisitor {
    private PositionDataSourceConstraint constraints;
    private final KnowledgeSource knowledgeSource;

    PositionDataSourceConstraintVisitor(
            KnowledgeSource knowledgeSource) {
        assert knowledgeSource != null : "knowledgeSource cannot be null";
        
        this.knowledgeSource = knowledgeSource;
    }

    PositionDataSourceConstraint getConstraints() {
        return this.constraints;
    }

    @Override
    public void visit(HighLevelAbstractionDefinition def) {
        //Extract any time range constraints from the abstraction definitions.
        //MOVE THIS DOWN TO PRIMITIVE AND EVENT DEFINITIONS
        for (ExtendedPropositionDefinition ext :
            def.getExtendedPropositionDefinitions()) {
            if (ext instanceof TemporalExtendedPropositionDefinition) {
                TemporalExtendedPropositionDefinition text =
                        (TemporalExtendedPropositionDefinition) ext;
                //Add a constraint if needed that propages down to actual database elements.
            }
        }

        //Recurse to children.
        String[] children = def.getDirectChildren();
        for (String child : children) {
            PropositionDefinition pd =
                    this.knowledgeSource.readAbstractionDefinition(child);
            pd.accept(this);
        }
    }

    @Override
    public void visit(EventDefinition eventDefinition) {
        if (eventDefinition.getDirectChildren().length == 0) {
            newPositionDataSourceConstraint(eventDefinition);
        }
    }

    @Override
    public void visit(
            PrimitiveParameterDefinition primitiveParameterDefinition) {
        newPositionDataSourceConstraint(primitiveParameterDefinition);
    }

    private void newPositionDataSourceConstraint(
            PropositionDefinition primitiveParameterDefinition) {
        PositionDataSourceConstraint newConstraint =
                new PositionDataSourceConstraint(
                primitiveParameterDefinition.getId());
        //newConstraint.setStart(this.start);
        //newConstraint.setFinish(this.finish);
        this.constraints.setAnd(newConstraint);
        this.constraints = newConstraint;
    }





}
