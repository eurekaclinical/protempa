/**
 * 
 */
package org.protempa.query;

import org.exolab.castor.mapping.FieldHandler;
import org.exolab.castor.mapping.ValidityException;
import org.protempa.backend.dsb.filter.Filter;

/**
 * Castor field handler for Query.filters
 * 
 * @author mgrand
 */
public class QueryCastorFilterHandler implements FieldHandler {

	/**
	 * Constructor
	 */
	public QueryCastorFilterHandler() {
		// TODO Auto-generated constructor stub
	}

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
	public Object getValue(Object object) throws IllegalStateException {
		Query query = (Query) object;
		return query.getFiltersArray();
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
	public void setValue(Object object, Object value) throws IllegalStateException, IllegalArgumentException {
		Query query = (Query) object;
		query.setFilterArray((Filter[]) value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.exolab.castor.mapping.FieldHandler#newInstance(java.lang.Object)
	 */
	@Override
	public Object newInstance(Object arg0) throws IllegalStateException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.exolab.castor.mapping.FieldHandler#resetValue(java.lang.Object)
	 */
	@Override
	public void resetValue(Object arg0) throws IllegalStateException, IllegalArgumentException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.exolab.castor.mapping.FieldHandler#checkValidity(java.lang.Object)
	 */
	@Override
	public void checkValidity(Object arg0) throws ValidityException, IllegalStateException {
		// TODO Auto-generated method stub

	}

}
