/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons-sandbox//functor/src/java/org/apache/commons/functor/generators/util/Attic/NumberRange.java,v 1.2 2003/06/24 15:49:57 rwaldhoff Exp $
 * ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.commons.functor.generators.util;

import org.apache.commons.functor.UnaryProcedure;
import org.apache.commons.functor.generators.Generator;

/**
 * Generator for a numeric range. If {@link #min} <= {@link #max} generation
 * will be forward, if {@link #min} > {@link #max} generation will be backward.
 *
 * @since 1.0
 * @version $Revision: 1.2 $ $Date: 2003/06/24 15:49:57 $
 * @author  Jason Horman (jason@jhorman.org)
 */

public abstract class NumberRange extends Generator {

    /***************************************************
     *  Instance variables
     ***************************************************/

    private Number min = null;
    private Number max = null;

    /***************************************************
     *  Constructors
     ***************************************************/

    /**
     * Create a range of numbers from min to max.
     */
    public NumberRange(Number min, Number max) {
        if (min == null || max == null) {
            throw new IllegalArgumentException("min and max must not be null");
        }

        this.min = min;
        this.max = max;
    }

    /***************************************************
     *  Instance methods
     ***************************************************/

    /** Get min. */
    public Number getMin() {
        return min;
    }

    /** Get max. */
    public Number getMax() {
        return max;
    }

    /** Check if a number is within range. */
    public abstract boolean isWithinRange(Number number);

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NumberRange)) return false;
        final NumberRange numberRange = (NumberRange) o;
        if (!max.equals(numberRange.max)) return false;
        if (!min.equals(numberRange.min)) return false;
        return true;
    }

    public int hashCode() {
        return 29 * min.hashCode() + max.hashCode();
    }

    /***************************************************
     *  Class methods
     ***************************************************/

    public static NumberRange from(Integer min, Integer max) {
        return new IntegerRange(min, max);
    }

    public static NumberRange from(int min, int max) {
        return new IntegerRange(min, max);
    }

    public static NumberRange from(Long min, Long max) {
        return new NumberRange.LongRange(min, max);
    }

    public static NumberRange from(long min, long max) {
        return new LongRange(min, max);
    }

    /***************************************************
     *  Inner classes
     ***************************************************/

    /**
     * Integer range.
     */
    public static class IntegerRange extends NumberRange{
        public IntegerRange(Integer min, Integer max) {
            super(min, max);
        }

        public IntegerRange(int min, int max) {
            super(new Integer(min), new Integer(max));
        }

        public void run(UnaryProcedure proc) {
            int start = getMin().intValue();
            int end = getMax().intValue();

            if (start <= end) {
                for (int i=start;i<=end;i++) {
                    proc.run(new java.lang.Integer(i));
                }
            } else {
                for (int i=start;i>=end;i--) {
                    proc.run(new java.lang.Integer(i));
                }
            }
        }

        public boolean isWithinRange(Number number) {
            int num = number.intValue();
            int min = getMin().intValue();
            int max = getMax().intValue();

            if (min <= max) {
                return num >= min && num <= max;
            } else {
                return num <= min && num >= max;
            }
        }

        public String toString() {
            return "IntegerRange(" + getMin() + ", " + getMax() + ")";
        }
    }

    /**
     * Long range.
     */
    public static class LongRange extends NumberRange{
        public LongRange(Long min, Long max) {
            super(min, max);
        }

        public LongRange(long min, long max) {
            super(new Long(min), new Long(max));
        }

        public void run(UnaryProcedure proc) {
            long start = getMin().longValue();
            long end = getMax().longValue();

            if (start <= end) {
                for (long i=start;i<=end;i++) {
                    proc.run(new java.lang.Long(i));
                }
            } else {
                for (long i=start;i>=end;i--) {
                    proc.run(new java.lang.Long(i));
                }
            }
        }

        public boolean isWithinRange(Number number) {
            long num = number.longValue();
            long min = getMin().longValue();
            long max = getMax().longValue();

            if (min <= max) {
                return num >= min && num <= max;
            } else {
                return num <= min && num >= max;
            }
        }

        public String toString() {
            return "LongRange(" + getMin() + ", " + getMax() + ")";
        }
    }
}