package org.protempa;

import java.util.EventObject;

/**
 * Source updated events.
 * 
 * @author Andrew Post
 *
 * @param <S>
 */
public abstract class SourceUpdatedEvent extends EventObject {
	private final Source<?> source;

	SourceUpdatedEvent(Source<?> protempaSource) {
		super(protempaSource);
		this.source = protempaSource;
	}
	
	/**
	 * Returns the source {@link Source} (the same as what
	 * <code>getSource()</code> returns).
	 * 
	 * @return a {@link Backend}.
	 */
	public Source<?> getPROTEMPASource() {
		return this.source;
	}

}
