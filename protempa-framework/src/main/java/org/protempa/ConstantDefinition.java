package org.protempa;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Andrew Post
 */
public final class ConstantDefinition extends AbstractPropositionDefinition {
    private static final long serialVersionUID = 727799438356160581L;

    public ConstantDefinition(KnowledgeBase kb, String id) {
        super(kb, id);
        
        kb.addConstantDefinition(this);
    }

    @Override
    public boolean isConcatenable() {
        return false;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    protected void recalculateDirectChildren() {
        String[] old = this.directChildren;
        Set<String> c = new HashSet<String>();
        String[] inverseIsA = getInverseIsA();
        if (inverseIsA != null) {
            for (String propId : inverseIsA) {
                c.add(propId);
            }
        }
        this.directChildren = c.toArray(new String[c.size()]);
        this.changes.firePropertyChange(DIRECT_CHILDREN_PROPERTY, old,
                this.directChildren);
    }

    public void accept(PropositionDefinitionVisitor processor) {
        if (processor == null) {
            throw new IllegalArgumentException("processor cannot be null.");
        }
        processor.visit(this);
    }

    public void acceptChecked(PropositionDefinitionCheckedVisitor processor)
            throws ProtempaException {
        if (processor == null) {
            throw new IllegalArgumentException("processor cannot be null.");
        }
        processor.visit(this);
    }

}
