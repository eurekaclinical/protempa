package org.arp.javautil.stat;

/**
 * A class for calculating the covariance of a list of points. This algorithm is
 * derived from the updating variance algorithm due to West (Comm. ACM, Sep-79,
 * Volume 22, Number 9, pp. 532-5).
 * 
 * @author Andrew Post
 */
public class UpdatingCovarCalc {

    private double sumsq = 0.0;
    private double meanx = 0.0;
    private double meany = 0.0;
    private int numItems = 1;

    /**
     * Creates new UpdatingCovarianceCalculator. At least one point is necessary
     * for calculated a covariance, and it should be specified in this
     * constructor.
     * 
     * @param x
     *            first x value
     * @param y
     *            first y value
     */
    public UpdatingCovarCalc(double x, double y) {
        meanx = x;
        meany = y;
    }

    /**
     * Update the covariance with another point.
     * 
     * @param x
     *            x value
     * @param y
     *            y value
     */
    public void addPoint(double x, double y) {
        numItems++;
        double xMinusMeanX = x - meanx;
        double yMinusMeanY = y - meany;
        sumsq += xMinusMeanX * yMinusMeanY * (numItems - 1) / numItems;
        meanx += xMinusMeanX / numItems;
        meany += yMinusMeanY / numItems;
    }

    /**
     * Return the covariance of the list of points specified to this object.
     * 
     * @return the covariance of the list of points specified to this object.
     */
    public double getCovariance() {
        return sumsq / (numItems - 1);
    }

    /**
     * Return the sum squared of the list of points specified to this object.
     * 
     * @return the sum squared of the list of points specified to this object.
     */
    public double getSumSquaredDeviations() {
        return sumsq;
    }
}
