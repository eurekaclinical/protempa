/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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
