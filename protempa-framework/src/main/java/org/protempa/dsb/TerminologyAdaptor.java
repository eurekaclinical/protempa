package org.protempa.dsb;

import java.util.Set;

import org.protempa.backend.BackendInstanceSpec;
import org.protempa.proposition.value.Value;


/**
 * Translates local database queries in the standard interface into the local
 * database's schema.
 * 
 * @author Andrew Post
 */
public interface TerminologyAdaptor {

	/**
	 * Initializes a terminology adaptor. This method must be called before any
	 * others.
	 * 
	 * @param config
	 *            configuration <code>Properties</code>, specific to an
	 *            implementation of this interface.
	 * @return <code>true</code> if initialization succeeded,
	 *         <code>false</code> otherwise.
	 */
	void initialize(BackendInstanceSpec config)
            throws TerminologyAdaptorInitializationException;

	/**
	 * Translates a standard term to equivalent local terms.
	 * 
	 * @param standardTerm
	 *            a standard term id <code>String</code>.
	 * @return a <code>Set</code> of local term id <code>String</code>s.
	 */
	Set<String> standardToLocalTerms(String standardTerm);

	/**
	 * Translates the values of a set of parameters from local units to standard
	 * units.
	 * 
	 * @param parameters
	 *            a <code>List</code> of <code>Parameter</code> objects.
	 * @return a <code>List</code> of <code>Parameter</code> objects.
	 */
	Value localToStandardUnits(String propId, Value value);

	/**
	 * Cleanup any resources created by this terminology adaptor.
	 */
	void close();

	/**
	 * Registers a listener that get called whenever the terminology adaptor
	 * changes.
	 * 
	 * @param listener
	 *            a <code>TerminologyAdaptorListener</code>.
	 */
	void addTerminologyAdaptorUpdatedListener(
			TerminologyAdaptorListener listener);

	/**
	 * Unregisters a listener so that changes to the terminology adapor are no
	 * longer sent.
	 * 
	 * @param listener
	 *            a <code>TerminologyAdaptorListener</code>.
	 */
	void removeTerminologyAdaptorUpdatedListener(
			TerminologyAdaptorListener listener);
}
