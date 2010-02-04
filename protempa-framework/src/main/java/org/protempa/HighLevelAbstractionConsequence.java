package org.protempa;

import org.drools.WorkingMemory;
import org.drools.spi.Consequence;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.Tuple;
import org.protempa.proposition.Segment;
import org.protempa.proposition.Sequence;
import org.protempa.proposition.TemporalProposition;


/**
 * @author Andrew Post
 */
class HighLevelAbstractionConsequence implements Consequence {

	private static final long serialVersionUID = -833609244124008166L;

	private final HighLevelAbstractionDefinition cad;

	private final int columns;

    /**
	 *
	 * @param def
	 *            a {@link HighLevelAbstractionDefinition}, cannot be
	 *            <code>null</code>.
	 * @param columns
	 *            the number of parameters, must be greater than zero.
	 */
	HighLevelAbstractionConsequence(HighLevelAbstractionDefinition def,
			int columns) {
        assert def != null : "def cannot be null";
		assert columns > 0 : "columns must be > 0, was " + columns;
		this.cad = def;
		this.columns = columns;
	}

	public void evaluate(KnowledgeHelper arg0, WorkingMemory arg1)
			throws Exception {
        Segment<TemporalProposition> segment = new Segment<TemporalProposition>(
                parameters(arg0.getTuple(), arg1));
        arg0.getWorkingMemory().insert(
                AbstractParameterFactory.getFromAbstraction(cad.getId(),
                        segment, null, cad.getTemporalOffset()));
	}

	/**
	 * @param arg0
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Sequence<TemporalProposition> parameters(Tuple arg0,
            WorkingMemory arg1) {
		Sequence<TemporalProposition> sequences = new Sequence<TemporalProposition>(
				cad.getId(), columns);
		for (int i = 0; i < columns; i++) {
			sequences.add((TemporalProposition) arg1.getObject(arg0.get(i)));
		}
		return sequences;
	}
}