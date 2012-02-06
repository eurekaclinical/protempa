/**
 * 
 */
package org.protempa.xml;


/**
 * Convert String array object to/from XML <keyIDs></keyIDs>
 * 
 * @author mgrand
 */
class AndConverter extends StringArrayConverter {

	private static final String TERM_ID = "termID";

	/**
	 * Constructor
	 */
	public AndConverter() {
		super(TERM_ID);
	}
}
