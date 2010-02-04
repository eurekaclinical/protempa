package org.protempa;

/**
 * Methods shared by PROTEMPA's modules.
 * 
 * @author Andrew Post
 */
public interface Module {
	/**
	 * Closes all resources that were created by this module. Subsequent calls
	 * to this module's methods cannot be made.
	 */
	void close();

	/**
	 * Resets the state of this module to the default.
	 */
	void clear();
}
