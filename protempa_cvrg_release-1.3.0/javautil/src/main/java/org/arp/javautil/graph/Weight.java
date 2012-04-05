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
package org.arp.javautil.graph;

import java.io.Serializable;

/**
 * Like a <code>Long</code> except that there are special values representing
 * positive infinity, negative infinity, positive epsilon (smallest positive
 * value), and negative epsilon (smallest negative value).
 * 
 * @author Andrew Post
 */
public final class Weight implements Comparable<Weight>, Serializable {

    private static final long serialVersionUID = -20812788110969523L;
    
    /**
     * Whether or not this weight has a value of infinity.
     */
    private boolean isInfinity = false;
    /**
     * <code>true</code> if this weight has a value of positive infinity,
     * <code>false</code> if this weight has a value of negative infinity.
     */
    private boolean posOrNeg = true;

    private long val;
    private transient volatile int hashCode;

    Weight() {
    }

    /**
     * Creates a weight with the given value.
     *
     * @param val
     *            a <code>long</code> value.
     */
    Weight(long val) {
        this.val = val;
    }

    /**
     * Creates a weight with the given value.
     *
     * @param val
     *            a <code>Number</code> value (if null, default value is
     *            <code>0L</code>). It is converted to a <code>long</code>
     *            using <code>Number.longValue()</code>.
     */
    Weight(Number val) {
        if (val != null) {
            this.val = val.longValue();
        }
    }

    /**
     * Creates a weight with a value of positive or negative infinity.
     *
     * @param posOrNeg
     *            <code>true</code> to create a weight with a value of
     *            positive infinity, <code>false</code> to create a weight
     *            with a value of negative infinity.
     */
    Weight(boolean posOrNeg) {
        isInfinity = true;
        this.posOrNeg = posOrNeg;
    }

    /**
     * Copy constructor.
     *
     * @param w
     *            a <code>Weight</code>. If <code>null</code>, this
     *            creates a <code>Weight</code> with the default value (<code>0</code>).
     */
    Weight(Weight w) {
        if (w != null) {
            isInfinity = w.isInfinity;
            posOrNeg = w.posOrNeg;
            val = w.val;
        }
    }

    void set(Weight w) {
        if (w != null) {
            isInfinity = w.isInfinity;
            posOrNeg = w.posOrNeg;
            val = w.val;
        } else {
            isInfinity = false;
            posOrNeg = true;
            val = 0L;
        }

        hashCode = 0;
    }

    /**
     * Gets the value of this weight.
     *
     * @return a long representing the value of this weight. If this weight has
     *         a value of positive infinity, <code>Long.MAX_VALUE</code> is
     *         returned. If this weight has a value of negative infinity,
     *         <code>Long.MIN_VALUE</code> is returned.
     */
    public long value() {
        if (isInfinity && !posOrNeg) {
            return Long.MIN_VALUE;
        } else if (isInfinity && posOrNeg) {
            return Long.MAX_VALUE;
        } else {
            return val;
        }
    }

    /**
     * Checks to see if this weight is greater than the given long value.
     *
     * @param val
     *            a long value.
     * @return true if this weight is greater than the given long value, false
     *         otherwise.
     */
    public boolean greaterThan(long val) {
        if (isInfinity && posOrNeg) {
            return true;
        } else if (isInfinity && !posOrNeg) {
            return false;
        } else {
            return this.val > val;
        }
    }

    public int compareToLong(long val) {
        if (greaterThan(val)) {
            return 1;
        } else if (lessThan(val)) {
            return -1;
        } else {
            return 0;
        }
    }

    /**
     * Checks to see if this weight is less than the given long value.
     *
     * @param val
     *            a long value.
     * @return true if this weight is less than the given long value, false
     *         otherwise.
     */
    public boolean lessThan(long val) {
        if (isInfinity && !posOrNeg) {
            return true;
        } else if (isInfinity && posOrNeg) {
            return false;
        } else {
            return this.val < val;
        }
    }

    /**
     * Checks if this weight has the same value as the given long.
     *
     * @param val
     *            a <code>long</code>.
     * @return true if this weight has the same value, false otherwise. Always
     *         returns false if the value of this weight is infinity.
     */
    public boolean isEqual(long val) {
        if (isInfinity) {
            return false;
        } else {
            return this.val == val;
        }
    }

    /**
     * Creates a new weight with value equal to the sum of both weights. Note
     * that +inf and -inf cannot be added. If you try, an
     * IllegalArgumentException will be thrown.
     *
     * @param w
     *            a weight.
     * @return a new weight.
     */
    public Weight add(Weight w) {
        if (w == null) {
            return new Weight(this);
        } else {
            boolean wIsInfinity = w.isInfinity;
            boolean wPosOrNeg = w.posOrNeg;
            if ((isInfinity && posOrNeg && wIsInfinity && !wPosOrNeg)
                    || (isInfinity && !posOrNeg && wIsInfinity && wPosOrNeg)) {
                throw new IllegalArgumentException("+inf - inf!");
            } else if ((isInfinity && posOrNeg) || (w.isInfinity && wPosOrNeg)) {
                return WeightFactory.POS_INFINITY;
            } else if ((isInfinity && !posOrNeg) || (wIsInfinity && !wPosOrNeg)) {
                return WeightFactory.NEG_INFINITY;
            } else {
                return new Weight(val + w.val);
            }
        }
    }

    void addToSelf(Weight w) {
        if (w != null) {
            boolean wIsInfinity = w.isInfinity;
            boolean wPosOrNeg = w.posOrNeg;
            if ((isInfinity && posOrNeg && wIsInfinity && !wPosOrNeg)
                    || (isInfinity && !posOrNeg && wIsInfinity && wPosOrNeg)) {
                throw new IllegalArgumentException("+inf - inf!");
            } else if ((isInfinity && posOrNeg) || (wIsInfinity && wPosOrNeg)) {
                set(WeightFactory.POS_INFINITY);
            } else if ((isInfinity && !posOrNeg) || (wIsInfinity && !wPosOrNeg)) {
                set(WeightFactory.NEG_INFINITY);
            } else {
                val += w.val;
                hashCode = 0;
            }
        }
    }

    /**
     * Creates a new weight the value equal to the difference between the two
     * weights. Note that infinity cannot be subtracted from infinity. If you
     * try, an IllegalArgumentException will be thrown.
     *
     * @param w
     *            a weight.
     * @return a new weight.
     */
    public Weight subtract(Weight w) {
        if (w == null) {
            return new Weight(this);
        } else {
            boolean wIsInfinity = w.isInfinity;
            boolean wPosOrNeg = w.posOrNeg;
            if ((isInfinity && posOrNeg && wIsInfinity && wPosOrNeg)
                    || (isInfinity && !posOrNeg && wIsInfinity && !wPosOrNeg)) {
                throw new IllegalArgumentException("+inf - inf!");
            } else if ((isInfinity && posOrNeg) || (wIsInfinity && !wPosOrNeg)) {
                return WeightFactory.POS_INFINITY;
            } else if ((isInfinity && !posOrNeg) || (wIsInfinity && wPosOrNeg)) {
                return WeightFactory.NEG_INFINITY;
            } else {
                return new Weight(val - w.val);
            }
        }
    }

    /**
     * Gets the larger of the two given weights.
     *
     * @param w1
     *            a weight. Cannot be <code>null</code>.
     * @param w2
     *            a weight. Cannot be <code>null</code>.
     * @return a weight.
     */
    public static Weight max(Weight w1, Weight w2) {
        if (w1 == null) {
            throw new IllegalArgumentException("Argument w1 cannot be null");
        }
        if (w2 == null) {
            throw new IllegalArgumentException("Argument w2 cannot be null");
        }
        if ((w1.isInfinity && w1.posOrNeg) || (w2.isInfinity && !w2.posOrNeg)) {
            return w1;
        } else if ((w2.isInfinity && w2.posOrNeg)
                || (w1.isInfinity && !w1.posOrNeg)) {
            return w2;
        } else if (w1.val >= w2.val) {
            return w1;
        } else {
            return w2;
        }
    }

    /**
     * Gets the smaller of the two given weights.
     *
     * @param w1
     *            a weight. Cannot be <code>null</code>.
     * @param w2
     *            a weight. Cannot be <code>null</code>.
     * @return a weight.
     */
    public static Weight min(Weight w1, Weight w2) {
        if (w1 == null) {
            throw new IllegalArgumentException("Argument w1 cannot be null");
        }
        if (w2 == null) {
            throw new IllegalArgumentException("Argument w2 cannot be null");
        }

        if ((w1.isInfinity && !w1.posOrNeg) || (w2.isInfinity && w2.posOrNeg)) {
            return w1;
        } else if ((w2.isInfinity && !w2.posOrNeg)
                || (w1.isInfinity && w1.posOrNeg)) {
            return w2;
        } else if (w1.val <= w2.val) {
            return w1;
        } else {
            return w2;
        }
    }

    /**
     * Checks if this weight has value of positive infinity.
     *
     * @return <code>true</code> if it does, <code>false</code> if it
     *         doesn't.
     */
    public boolean isPositiveInfinity() {
        return isInfinity && posOrNeg;
    }

    /**
     * Checks if this weight has value of negative infinity.
     *
     * @return <code>true</code> if it does, <code>false</code> if it
     *         doesn't.
     */
    public boolean isNegativeInfinity() {
        return isInfinity && !posOrNeg;
    }

    /**
     * Checks if this weight has value of positive or negative infinity.
     *
     * @return <code>true</code> if it does, <code>false</code> if it
     *         doesn't.
     */
    public boolean isInfinity() {
        return isInfinity;
    }

    /**
     * Creates a new weight with the opposite sign.
     *
     * @return a new <code>Weight</code>.
     */
    public Weight invertSign() {
        if (isInfinity) {
            if (posOrNeg) {
                return WeightFactory.NEG_INFINITY;
            } else {
                return WeightFactory.POS_INFINITY;
            }
        }
        return new Weight(-val);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (isInfinity) {
            if (posOrNeg) {
                return "+inf";
            } else if (!posOrNeg) {
                return "-inf";
            }
        }
        return String.valueOf(val);
    }

    /**
     * Compares two <code>Weight</code> objects numerically.
     *
     * @param anotherWeight
     *            the <code>Weight</code> to be compared.
     * @return the value <code>0</code> if this <code>Weight</code> is equal
     *         to the argument <code>Weight</code>; a value less than
     *         <code>0</code> if this <code>Weight</code> is numerically
     *         less than the argument </code>Weight</code>; and a value
     *         greater than <code>0</code> if this <code>Weight</code> is
     *         numerically greater than the argument <code>Weight</code>
     *         (signed comparison).
     */
    public int compareTo(Weight anotherWeight) {
        boolean wIsInfinity = anotherWeight.isInfinity;
        boolean wPosOrNeg = anotherWeight.posOrNeg;

        if (isInfinity && wIsInfinity
                && ((posOrNeg && wPosOrNeg) || (!posOrNeg && !wPosOrNeg))) {
            return 0;
        } else if ((isInfinity && posOrNeg) || (wIsInfinity && !wPosOrNeg)) {
            return 1;
        } else if ((isInfinity && !posOrNeg) || (wIsInfinity && wPosOrNeg)) {
            return -1;
        } else if (val < anotherWeight.val) {
            return -1;
        } else if (val > anotherWeight.val) {
            return 1;
        } else {
            return 0;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (getClass() != o.getClass()) {
            return false;
        }

        Weight w = (Weight) o;

        return isInfinity == w.isInfinity && posOrNeg == w.posOrNeg
                && val == w.val;
    }

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            int result = 17;
            result = 37 * result + (isInfinity ? 0 : 1);
            result = 37 * result + (posOrNeg ? 0 : 1);
            result = 37 * result + (int) (val ^ (val >>> 32));
            hashCode = result;
        }
        return hashCode;

    }

    /**
     * Checks if this weight has a value within the specified range.
     *
     * @param min
     *            the minimum <code>Weight</code> of the range.
     * @param max
     *            the maximum <code>Weight</code> of the range.
     * @return <code>true</code> if this weight is within the specified range,
     *         <code>false</code> if not.
     */
    boolean isWithinRange(Weight min, Weight max) {
        return compareTo(min) >= 0 && compareTo(max) <= 0;
    }
}
