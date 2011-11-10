/**
 * 
 */
package org.protempa.xml;

import org.castor.xml.UnmarshalListener;
import org.protempa.backend.dsb.filter.PositionFilter;

/**
 * Handler for castor's unmarshall events.
 * 
 * @author mgrand
 */
public class UnmarshalHandler implements UnmarshalListener {

	/**
	 * Constructor
	 */
	public UnmarshalHandler() {
		super();
	}

	/**
	 * This method is called when an object has just been initialized by the
	 * Unmarshaller.
	 * 
	 * @param target
	 *            the Object that was initialized.
	 * @param parent
	 *            the parent of the target that was initialized
	 */
	@Override
	public void initialized(Object target, Object parent) {
		// Nothing to do
	}

	/**
	 * This method is called once the attributes have been processed. It
	 * indicates that the the fields of the given object corresponding to
	 * attributes in the XML document have been set.
	 * 
	 * @param target
	 *            the Object the object being unmarshalled.
	 * @param parent
	 *            the parent of the target being unmarshalled
	 */
	@Override
	public void attributesProcessed(Object target, Object parent) {
		// Nothing to do
	}

	/**
	 * This method is called after a child object has been added during the
	 * unmarshalling. This method will be called after #unmarshalled(Object) has
	 * been called for the child.
	 * 
	 * @param fieldName
	 *            The Name of the field the child is being added to.
	 * @param parent
	 *            The Object being unmarshalled.
	 * @param child
	 *            The Object that was just added.
	 */
	@Override
	public void fieldAdded(String fieldName, Object parent, Object child) {
		// Nothing to do
	}

	/**
	 * This method is called after an object has been completely unmarshalled,
	 * including all of its children (if any).
	 * 
	 * @param target
	 *            the Object that was unmarshalled.
	 * @param parent
	 *            the parent of the target that was unmarshalled
	 */
	@Override
	public void unmarshalled(Object target, Object parent) {
		if (target instanceof PositionFilter) {
			((PositionFilter)target).init();
		}
	}

}
