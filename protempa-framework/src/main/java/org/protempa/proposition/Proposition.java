package org.protempa.proposition;

import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.Set;
import org.protempa.ProtempaException;
import org.protempa.proposition.value.Value;

/**
 * A data element. All implementations of this have property change support
 * methods, but no change events are fired at present.
 * 
 * @author Andrew Post
 */
public interface Proposition extends PropositionVisitable, Serializable {

	/**
	 * Gets this proposition's data type.
	 * 
	 * @return the identification {@link String} for the type of data
	 *         represented by this proposition.
	 */
	String getId();

	/**
	 * Adds a {@link PropertyChangeListener} to the listener list. The listener
	 * is registered for all bound properties of this class (none at present).
	 * 
	 * If listener is null, no exception is thrown and no action is performed.
	 * 
	 * @param l
	 *            the {@link PropertyChangeListener} to be added.
	 */
	void addPropertyChangeListener(PropertyChangeListener l);

	/**
	 * Removes a {@link PropertyChangeListener} from the listener list. This
	 * method should be used to remove {@link PropertyChangeListener}s that
	 * were registered for all bound properties of this class.
	 * 
	 * If listener is null, no exception is thrown and no action is performed.
	 * 
	 * @param l
	 *            the {@link PropertyChangeListener} to be removed
	 */
	void removePropertyChangeListener(PropertyChangeListener l);

	boolean isEqual(Object prop);

	/**
	 * Performs some processing on this proposition.
	 * 
	 * @param visitor
	 *            a {@link PropositionVisitor}.
	 */
	void accept(PropositionVisitor propositionVisitor);

    void acceptChecked(PropositionCheckedVisitor propositionCheckedVisitor)
            throws ProtempaException;

    void setProperty(String name, Value value);

    Value getProperty(String name);

    Set<String> propertyNames();

}