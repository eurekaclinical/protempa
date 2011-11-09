/**
 * 
 */
package org.protempa.xml.castor;

import org.exolab.castor.mapping.FieldHandler;
import org.exolab.castor.mapping.ValidityException;

/**
 * Customer field handler to make Castor think that Protempa queries have a
 * dataSourceBackend property.
 * 
 * @author mgrand
 */
public class KnowledgeSourceBackendHandler implements FieldHandler {

	/**
	 * Returns the value of the field from the object.
	 * 
	 * @param object
	 *            The object
	 * @return The value of the field
	 * @throws IllegalStateException
	 *             The Java object has changed and is no longer supported by
	 *             this handler, or the handler is not compatible with the Java
	 *             object
	 */
	@Override
	public Object getValue(Object arg0) throws IllegalStateException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Sets the value of the field on the object.
	 * 
	 * @param object
	 *            The object
	 * @param value
	 *            The new value
	 * @throws IllegalStateException
	 *             The Java object has changed and is no longer supported by
	 *             this handler, or the handler is not compatible with the Java
	 *             object
	 * @throws IllegalArgumentException
	 *             The value passed is not of a supported type
	 */
	@Override
	public void setValue(Object arg0, Object arg1) throws IllegalStateException, IllegalArgumentException {
		// TODO Auto-generated method stub

	}

	/**
	 * Creates a new instance of the object described by this field.
	 * 
	 * @param parent
	 *            The object for which the field is created
	 * @return A new instance of the field's value
	 * @throws IllegalStateException
	 *             This field is a simple type and cannot be instantiated
	 */
	@Override
	public Object newInstance(Object arg0) throws IllegalStateException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Sets the value of the field to a default value.
	 * 
	 * Reference fields are set to null, primitive fields are set to their
	 * default value, collection fields are emptied of all elements.
	 * 
	 * @param object
	 *            The object
	 * @throws IllegalStateException
	 *             The Java object has changed and is no longer supported by
	 *             this handler, or the handler is not compatible with the Java
	 *             object
	 */
	@Override
	public void resetValue(Object arg0) throws IllegalStateException, IllegalArgumentException {
		// TODO Auto-generated method stub

	}

	/**
	 * @deprecated No longer supported
	 */
	public void checkValidity(Object object) throws ValidityException, IllegalStateException {
		// do nothing
	}
}
