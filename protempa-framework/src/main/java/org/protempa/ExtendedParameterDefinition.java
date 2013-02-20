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

import org.protempa.proposition.Parameter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.value.Value;

/**
 * Represents an instance of an abstraction definition with optional constraints
 * on the instance's duration.
 * 
 * @author Andrew Post
 */
public class ExtendedParameterDefinition extends ExtendedPropositionDefinition {
	
	private static final long serialVersionUID = -3587086756622794815L;

	private Value value;

	public ExtendedParameterDefinition(String parameterId) {
		super(parameterId);
	}

	public Value getValue() {
		return value;
	}

	/**
	 * Returns whether a parameter has the same id and value, and consistent
	 * duration as specified by this extended parameter definition.
	 * 
	 * @param parameter
	 *            a <code>Parameter</code>
	 * @return <code>true</code> if <code>parameter</code> has the same id
	 *         and value, and consistent duration as specified by this extended
	 *         parameter definition, or <code>false</code> if not, or if
	 *         <code>parameter</code> is <code>null</code>.
	 */
	@Override
	public boolean getMatches(Proposition proposition) {
		if (!super.getMatches(proposition)) {
			return false;
		}

		if (!(proposition instanceof Parameter)) {
			return false;
		}

		if (this.value != null) {
			Value pValue = ((Parameter) proposition).getValue();
			if (this.value != pValue && !this.value.equals(pValue)) {
				return false;
			}
		}

		return true;
	}

	public void setValue(Value value) {
		this.value = value;
	}

	@Override
	public boolean hasEqualFields(ExtendedPropositionDefinition obj) {
		if (!super.hasEqualFields(obj)) {
			return false;
		}

		if (!(obj instanceof ExtendedParameterDefinition)) {
			return false;
		}

		ExtendedParameterDefinition other = (ExtendedParameterDefinition) obj;
		return (value == other.value || value.equals(other.value));
	}

	@Override
	public String toString() {
		return "Extended parameter: " + this.getPropositionId() + "; " + value;
	}

}
