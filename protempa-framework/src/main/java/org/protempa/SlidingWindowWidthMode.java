package org.protempa;

/**
 * For specifying whether a low-level abstraction definition's or aggregation
 * definition's sliding window width should be the algorithm's default
 * (DEFAULT), the entire width of a time series (ALL), or a specified minimum
 * and maximum number of values (RANGE).
 * 
 * @author Andrew Post
 */
public enum SlidingWindowWidthMode {
	DEFAULT, ALL, RANGE
}
