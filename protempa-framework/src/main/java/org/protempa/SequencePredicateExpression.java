/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2013 Emory University
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

import java.util.Set;

import org.drools.WorkingMemory;
import org.drools.rule.Declaration;
import org.drools.spi.PredicateExpression;
import org.drools.spi.Tuple;
import org.protempa.proposition.Sequence;


class SequencePredicateExpression implements PredicateExpression {

	private static final long serialVersionUID = 5478534666910990106L;

	private Set<String> dataTypes;

	SequencePredicateExpression(Set<String> dataTypes) {
		this.dataTypes = dataTypes;
	}

	@Override
    public boolean evaluate(Object arg0, Tuple arg1, Declaration[] arg2,
			Declaration[] arg3, WorkingMemory arg4, Object context)
			throws Exception {
		Sequence<?> other = (Sequence<?>) arg0;
		return dataTypes == null || dataTypes.equals(other.getPropositionIds());
	}

	@Override
    public Object createContext() {
		return null;
	}

}
