/*
 * #%L
 * Protempa Test Suite
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
package org.protempa.test;

/**
 * Holds information about an observation during a patient's visit, including
 * results of that observation.
 * 
 * @author hrathod
 * 
 */
abstract class ObservationWithResult extends Observation {
	/**
	 * The results of the observation, held as a String.
	 */
	private String resultAsStr;
	/**
	 * The results of the observation, held as a number.
	 */
	private Double resultAsNum;
	/**
	 * The units used to measure the results.
	 */
	private String units;
	/**
	 * Any flags resulting from the results.
	 */
	private String flag;

	/**
	 * Get the results as a string.
	 * 
	 * @return the results of the lab test as a string.
	 */
	public String getResultAsStr() {
		return this.resultAsStr;
	}

	/**
	 * Set the results as a string.
	 * 
	 * @param inResultAsStr the results, as a string.
	 */
	public void setResultAsStr(String inResultAsStr) {
		this.resultAsStr = inResultAsStr;
	}

	/**
	 * Get the results as a number.
	 * 
	 * @return The results as a number.
	 */
	public Double getResultAsNum() {
		return this.resultAsNum;
	}

	/**
	 * Set the results as a number
	 * 
	 * @param inResultAsNum The results as a number.
	 */
	public void setResultAsNum(Double inResultAsNum) {
		this.resultAsNum = inResultAsNum;
	}

	/**
	 * Get the units used to measure the results.
	 * 
	 * @return The units units to measure the results.
	 */
	public String getUnits() {
		return this.units;
	}

	/**
	 * Set the units used to measure the results.
	 * 
	 * @param inUnits The units units to measure the results.
	 */
	public void setUnits(String inUnits) {
		this.units = inUnits;
	}

	/**
	 * Get the flag for the result.
	 * 
	 * @return The flag for the result.
	 */
	public String getFlag() {
		return this.flag;
	}

	/**
	 * Set the flag for the result.
	 * 
	 * @param inFlag The flag for the result.
	 */
	public void setFlag(String inFlag) {
		this.flag = inFlag;
	}
}
