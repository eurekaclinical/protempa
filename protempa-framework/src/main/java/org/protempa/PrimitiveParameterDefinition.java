package org.protempa;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.protempa.proposition.value.ValueFactory;


/**
 * Defines measurable or observable time-stamped data types.
 * 
 * @author Andrew Post
 */
public final class PrimitiveParameterDefinition extends
		AbstractPropositionDefinition {

	private static final long serialVersionUID = 4469613843480322419L;

	/**
	 * The default value factory (<code>ValueFactory.NOMINAL</code>).
	 */
	public static final ValueFactory DEFAULT_VALUE_FACTORY = ValueFactory.NOMINAL;

	/**
	 * The allowed types of values for this primitive parameter.
	 */
	private ValueFactory valueFactory;

	/**
	 * The units for values of this primitive parameter.
	 */
	private String units;

	private final Set<String> attributes;

	public PrimitiveParameterDefinition(KnowledgeBase kb, String id) {
		super(kb, id);
		this.valueFactory = DEFAULT_VALUE_FACTORY;
		kb.addPrimitiveParameterDefinition(this);
		this.attributes = new HashSet<String>();
	}

	/**
	 * Gets the value factory for this primitive parameter definition.
	 * 
	 * @return a {@link ValueFactory}, guaranteed not to be
	 *         <code>null</code>.
	 */
	public ValueFactory getValueFactory() {
		return valueFactory;
	}

	/**
	 * Sets the value factory for this primitive parameter definition.
	 * 
	 * @param vf
	 *            a {@link ValueFactory}. If <code>null</code>, the
	 *            default value factory (defined by
	 *            <code>DEFAULT_VALUE_FACTORY</code> is set.
	 */
	public void setValueFactory(ValueFactory vf) {
		if (vf == null) {
			this.valueFactory = DEFAULT_VALUE_FACTORY;
		} else {
			this.valueFactory = vf;
		}
	}

	public boolean addAttribute(String attr) {
		if (attr != null) {
			return attributes.add(attr);
		} else {
			return false;
		}
	}

	public Set<String> getAttributes() {
		return Collections.unmodifiableSet(attributes);
	}

	/**
	 * Returns the units for values of this primitive parameter.
	 * 
	 * @return a units {@link String}.
	 */
	public String getUnits() {
		return units;
	}

	/**
	 * Sets the units for values of this primitive parameter.
	 * 
	 * @param units
	 *            a units {@link String}.
	 */
	public void setUnits(String units) {
		this.units = units;
	}

	@Override
	public void reset() {
		super.reset();
		this.units = null;
		this.attributes.clear();
		this.valueFactory = DEFAULT_VALUE_FACTORY;
	}

	public void accept(PropositionDefinitionVisitor processor) {
		processor.visit(this);
	}

    public void acceptChecked(PropositionDefinitionCheckedVisitor processor)
            throws ProtempaException {
        processor.visit(this);
    }

	/**
	 * By definition, primitive parameters are not concatenable.
	 * 
	 * @return <code>false</code>.
	 * @see org.protempa.PropositionDefinition#isConcatenable()
	 */
	public boolean isConcatenable() {
		return false;
	}

	/**
	 * By definition, primitive parameters are solid.
	 * @return <code>true</code>.
	 * @see org.protempa.PropositionDefinition#isSolid()
	 */
	public boolean isSolid() {
		return true;
	}

	@Override
	protected void recalculateDirectChildren() {
		// Do nothing.
	}
}