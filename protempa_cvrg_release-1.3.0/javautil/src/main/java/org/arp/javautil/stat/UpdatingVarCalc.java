/*
 * #%L
 * JavaUtil
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
package org.arp.javautil.stat;

/**
 * A class for calculating the variance and standard deviation of a list of
 * values. Algorithm is due to West (Comm. ACM, Sep-79, Volume 22, Number 9, pp.
 * 532-5).
 * 
 * @author Andrew Post
 */
public class UpdatingVarCalc {

    private double sumsq = 0.0;
    private double mean = 0.0;
    private int numItems = 1;

    /**
     * Creates new UpdatingVarCalc. At least one point must be specified in
     * order to calculate the variance or standard deviation. The first point
     * should be specified in this constructor.
     * 
     * @param point
     *            the first of a list of points.
     */
    public UpdatingVarCalc(double val) {
        mean = val;
    }

    /**
     * Update the variance with a new value.
     * 
     * @param val
     *            a new value.
     */
    public void addValue(double val) {
        numItems++;
        double valMinusMean = val - mean;
        sumsq += valMinusMean * valMinusMean * (numItems - 1) / numItems;
        mean += valMinusMean / numItems;
    }

    /**
     * Return the sum squared of the list of values specified to this object.
     * 
     * @return the sum squared of the list of values specified to this object.
     */
    public double getSumSquaredDeviations() {
        return sumsq;
    }

    /**
     * Return the variance of the list of values specified to this object.
     * 
     * @return the variance of the list of values specified to this object.
     */
    public double getVariance() {
        return sumsq / (numItems - 1);
    }

    /**
     * Return the standard deviation of the list of values specified to this
     * object.
     * 
     * @return the standard deviation of the list of values specified to this
     *         object.
     */
    public double getStdDev() {
        return Math.sqrt(sumsq / (numItems - 1));
    }
}
