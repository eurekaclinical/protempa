package org.arp.javautil.stat;

/**
 * A class for manipulating lines in a cartesian plane. Lines may be expressed
 * by the formula <code>y = mx + b</code>, where m is the slope, b is the
 * y-intercept, and y and x are coordinates in the cartesian plane. Java's
 * floating point arithmetic has built-in support for infinite values as defined
 * by the IEEE 754 standard. Special methods are used to test for this
 * condition. See the API documentation for class <code>Double</code> for
 * details.
 * 
 * @author Andrew Post
 */
public class Line {
	protected double m = 0.0;

	protected double b = 0.0;

	/**
	 * Creates new Line.
	 * 
	 * @param m
	 *            the slope.
	 * @param b
	 *            the y-intercept.
	 */
	public Line(double m, double b) {
		this.m = m;
		this.b = b;
	}

	/**
	 * Creates new Line for subclasses only. Subclass writers must define m and
	 * b with the setm() and setb() manually if they don't use the above
	 * constructor. The default values for the slope and intercept are both 0.0.
	 */
	protected Line() {
	}

	/**
	 * Sets the value of the slope for this line.
	 * 
	 * @param m
	 *            the new slope.
	 */
	protected void setm(double m) {
		this.m = m;
	}

	/**
	 * Sets the value of the intercept for this line.
	 * 
	 * @param b
	 *            the new intercept.
	 */
	protected void setb(double b) {
		this.b = b;
	}

	/**
	 * Returns the slope of this line. If you're concerned that the slope might
	 * be positive or negative infinity, use <code>Double.isInfinite()</code>
	 * and <code>Double.isNaN()</code> to find out.
	 * 
	 * @return the slope of this line.
	 */
	public double getm() {
		return m;
	}

	/**
	 * Returns the y-intercept of this line. If you're concerned that the
	 * intercept might be positive or negative infinity, use
	 * <code>Double.isInfinite()</code> and <code>Double.isNaN()</code> to
	 * find out.
	 * 
	 * @return the y-intercept of this line.
	 */
	public double getb() {
		return b;
	}

	/**
	 * Returns the value of x given y. If you're concerned that the value of x
	 * may be infinite, use <code>Double.isInfinite()</code> and
	 * <code>Double.isNaN()</code> to find out.
	 * 
	 * @param y
	 *            a value for y along this line.
	 * @return the value of x given y.
	 */
	public double getx(double y) {
		return (y - b) / m;
	}

	/**
	 * Returns the value of y given x. If you're concerned that the value of y
	 * may be infinite, use <code>Double.isInfinite()</code> and
	 * <code>Double.isNaN()</code> to find out.
	 * 
	 * @param x
	 *            a value for x along this line.
	 * @return the value of y given x.
	 */
	public double gety(double x) {
		return m * x + b;
	}

}