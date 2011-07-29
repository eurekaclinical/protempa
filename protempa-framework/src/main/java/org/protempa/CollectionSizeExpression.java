package org.protempa;

import java.util.Collection;

import org.drools.WorkingMemory;
import org.drools.rule.Declaration;
import org.drools.spi.PredicateExpression;
import org.drools.spi.Tuple;

/**
 * Sets a lower limit on the size of a collection.
 * 
 * @author Andrew Post
 * 
 */
public class CollectionSizeExpression implements PredicateExpression {

	private static final long serialVersionUID = -4559177903829641968L;

	private int minSize;

	public CollectionSizeExpression(int minSize) {
		this.minSize = minSize;
	}

	@Override
    public boolean evaluate(Object arg0, Tuple arg1, Declaration[] arg2,
			Declaration[] arg3, WorkingMemory arg4, Object context)
			throws Exception {
		boolean result = ((Collection<?>) arg0).size() >= this.minSize;
		return result;
	}

	@Override
    public Object createContext() {
		return null;
	}

}
