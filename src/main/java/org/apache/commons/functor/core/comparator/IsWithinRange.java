/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.functor.core.comparator;

import java.io.Serializable;
import org.apache.commons.functor.UnaryPredicate;

/**
 * A {@link UnaryPredicate} that tests whether a {@link Comparable} object is
 * within a range. The range is defined in the constructor.
 *
 * @since 1.0
 * @version $Revision$ $Date$
 * @author  Jason Horman (jason@jhorman.org)
 */

public class IsWithinRange<A extends Comparable<A>> implements UnaryPredicate<A>, Serializable {
    /** Hashcode of the name of this Predicate. */
    private static final int nameHashCode = "IsWithinRange".hashCode();

    /***************************************************
     *  Instance variables
     ***************************************************/

    /** The minimum value of the range. */
    private A min = null;
    /** The maximum value of the range. */
    private A max = null;

    /***************************************************
     *  Constructors
     ***************************************************/

    /**
     * Create a new IsWithinRange by passing in the range that will
     * be used in the {@link #test}.
     * @param min Comparable
     * @param max Comparable
     */
    public IsWithinRange(A min, A max) {
        if (min == null || max == null) {
            throw new IllegalArgumentException("min and max must not be null");
        }
        if (min.compareTo(max) > 0) {
            throw new IllegalArgumentException("min must be <= max");
        }
        this.min = min;
        this.max = max;
    }

    /***************************************************
     *  Instance methods
     ***************************************************/

    /**
     * {@inheritDoc}
     * Test if the passed in object is within the specified range.
     */
    public boolean test(A o) {
        return o.compareTo(min) >= 0 && o.compareTo(max) <= 0;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IsWithinRange<?>)) {
            return false;
        }
        final IsWithinRange<?> isWithinRange = (IsWithinRange<?>) o;
        return max.equals(isWithinRange.max) && min.equals(isWithinRange.min);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return 29 * min.hashCode() + max.hashCode() + nameHashCode;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "IsWithinRange(" + min + ", " + max + ")";
    }

    /**
     * Obtain an IsWithinRange instance.
     * @param <A>
     * @param min A
     * @param max A
     * @return IsWithinRange<A>
     */
    public static <A extends Comparable<A>> IsWithinRange<A> instance(A min, A max) {
        return new IsWithinRange<A>(min, max);
    }
}