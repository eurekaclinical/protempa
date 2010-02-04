package org.protempa;


/**
 * Definition of the constraints required to infer an abstract parameter.
 * 
 * @author Andrew Post
 */
public abstract class AbstractAbstractionDefinition extends
		AbstractPropositionDefinition implements AbstractionDefinition {

	private static final long serialVersionUID = 8075373039175465215L;

	private GapFunction gapFunction = GapFunction.DEFAULT;

	private String description;

	protected AbstractAbstractionDefinition(KnowledgeBase kb, String id) {
		super(kb, id);
		kb.addAbstractionDefinition(this);
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	protected String debugMessage() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("Abstraction: " + getId() + "\n");
		buffer.append("\tabbrevDisplayName=" + getAbbreviatedDisplayName()
				+ "\n");
		buffer.append("\tdisplayName=" + getDisplayName() + "\n");
		buffer.append("\tdescription=" + getDescription() + "\n");
		if (gapFunction != null) {
			buffer.append('\t');
			buffer.append(gapFunction.debugMessage());
		} else {
			buffer.append("\tNo gap function.\n");
		}
		return buffer.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.protempa.AbstractionDefinition#getGapFunction()
	 */
	public GapFunction getGapFunction() {
		return gapFunction;
	}

	public void setGapFunction(GapFunction gapFunction) {
		if (gapFunction == null) {
			this.gapFunction = GapFunction.DEFAULT;
		} else {
			this.gapFunction = gapFunction;
		}
	}

	@Override
	public void reset() {
		super.reset();
		gapFunction = GapFunction.DEFAULT;
	}

}