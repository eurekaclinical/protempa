package org.protempa;

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

    /**
     * By definition, constants are not concatenable.
     *
     * @return <code>false</code>.
     */
    @Override
    public boolean isConcatenable() {
        return false;
    }

    /**
     * By definition, there can be multiple constants with the same id.
     *
     * @return <code>false</code>.
     */
    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    protected void recalculateDirectChildren() {
        String[] old = this.directChildren;
        this.directChildren = getInverseIsA();
        if (this.changes != null) {
            this.changes.firePropertyChange(DIRECT_CHILDREN_PROPERTY, old,
                    this.directChildren);
        }
    }

    @Override
    public void accept(PropositionDefinitionVisitor processor) {
        if (processor == null) {
            throw new IllegalArgumentException("processor cannot be null.");
        }
        processor.visit(this);
    }

    @Override
    public void acceptChecked(PropositionDefinitionCheckedVisitor processor)
            throws ProtempaException {
        if (processor == null) {
            throw new IllegalArgumentException("processor cannot be null.");
        }
        processor.visit(this);
    }
}
