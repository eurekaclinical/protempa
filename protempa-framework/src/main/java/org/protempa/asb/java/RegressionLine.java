package org.protempa.asb.java;

import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.Segment;
import org.protempa.proposition.value.NumericalValue;

import org.arp.javautil.stat.Line;
import org.arp.javautil.stat.UpdatingCovarCalc;
import org.arp.javautil.stat.UpdatingVarCalc;

/**
 * A class for manipulating regression lines.
 * 
 * @author Andrew Post
 */
public class RegressionLine extends Line {
	private UpdatingVarCalc varCalcX = null;

	private UpdatingVarCalc varCalcY = null;

	private UpdatingCovarCalc covarCalc = null;

	static class NullValueException extends Exception {
		
		private PrimitiveParameter point;

		NullValueException(PrimitiveParameter point) {
			this.point = point;
		}

		PrimitiveParameter getPoint() {
			return this.point;
		}
	}

	/**
	 * Creates a new regression line of points. Calculation of the line occurs
	 * within this constructor.
	 * 
	 * @param points
	 *            an array of Point objects.
	 * @throws NullValueException
	 *             if one of the points has a <code>null</code> value.
	 */
	public RegressionLine(Segment<PrimitiveParameter> points)
			throws NullValueException {

		double sumY = 0;
		double sumX = 0;
		PrimitiveParameter curPoint = points.first();
		sumX = curPoint.getTimestamp();
		if (curPoint.getValue() == null) {
			throw new NullValueException(curPoint);
		}
		sumY = ((NumericalValue) curPoint.getValue()).doubleValue();
		varCalcX = new UpdatingVarCalc(sumX);
		varCalcY = new UpdatingVarCalc(sumY);
		covarCalc = new UpdatingCovarCalc(sumX, sumY);
		for (int i = 1, n = points.size(); i < n; i++) {
			curPoint = points.get(i);
			double x = curPoint.getTimestamp();
			sumX += x;
			if (curPoint.getValue() == null) {
				throw new NullValueException(curPoint);
			}
			double y = ((NumericalValue) curPoint.getValue()).doubleValue();
			sumY += y;
			varCalcX.addValue(x);
			varCalcY.addValue(y);
			covarCalc.addPoint(x, y);
		}

		setm(covarCalc.getSumSquaredDeviations()
				/ varCalcX.getSumSquaredDeviations());
		setb((sumY - m * sumX) / points.size());
	}

	/**
	 * Returns the r.m.s. error of this regression line.
	 * 
	 * @return the r.m.s. error of this regression line.
	 */
	public double getRMSError() {
		double r = covarCalc.getSumSquaredDeviations()
				/ Math.sqrt(varCalcX.getSumSquaredDeviations()
						* varCalcY.getSumSquaredDeviations());
		return Math.sqrt(1 - r) * varCalcX.getStdDev();
	}
}
