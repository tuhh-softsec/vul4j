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
package org.apache.commons.functor.core.collection;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collection;

import org.apache.commons.functor.BinaryPredicate;
import org.apache.commons.functor.UnaryPredicate;
import org.apache.commons.functor.adapter.RightBoundPredicate;

/**
 * A {@link BinaryPredicate} that checks to see if the
 * specified object is an element of the specified
 * Collection.
 *
 * @since 1.0
 * @version $Revision$ $Date$
 * @author  Jason Horman (jason@jhorman.org)
 * @author  Rodney Waldhoff
 */
public final class IsElementOf<L, R> implements BinaryPredicate<L, R>, Serializable {
    // static members
    //---------------------------------------------------------------

    private static IsElementOf<Object, Object> INSTANCE = new IsElementOf<Object, Object>();

    // constructors
    //---------------------------------------------------------------
    /**
     * Create a new IsElementOf.
     */
    public IsElementOf() {
    }

    // instance methods
    //---------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public boolean test(L obj, R col) {
        if (col instanceof Collection<?>) {
            return testCollection(obj, (Collection<?>) col);
        }
        if (null != col && col.getClass().isArray()) {
            return testArray(obj, col);
        }
        if (null == col) {
            throw new IllegalArgumentException("Right side argument must not be null.");
        }
        throw new IllegalArgumentException("Expected Collection or Array, found " + col.getClass());
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        return (obj instanceof IsElementOf<?, ?>);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return "IsElementOf".hashCode();
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "IsElementOf";
    }

    /**
     * Test a collection.
     * @param obj to find
     * @param col to search
     * @return boolean
     */
    private boolean testCollection(Object obj, Collection<?> col) {
        return col.contains(obj);
    }

    /**
     * Test an array.
     * @param obj to find
     * @param array to search
     * @return boolean
     */
    private boolean testArray(Object obj, Object array) {
        for (int i = 0, m = Array.getLength(array); i < m; i++) {
            Object value = Array.get(array, i);
            if (obj == value) {
                return true;
            }
            if (obj != null && obj.equals(value)) {
                return true;
            }
        }
        return false;
    }

    // static methods
    //---------------------------------------------------------------
    /**
     * Get an IsElementOf instance.
     * @return IsElementOf
     */
    public static IsElementOf<Object, Object> instance() {
        return INSTANCE;
    }

    /**
     * Get an IsElementOf(collection|array) UnaryPredicate.
     * @param obj collection/array to search
     * @return UnaryPredicate
     */
    public static <A> UnaryPredicate<A> instance(Object obj) {
        if (null == obj) {
            throw new NullPointerException("Argument must not be null");
        } else if (obj instanceof Collection<?>) {
            return new RightBoundPredicate<A, Object>(new IsElementOf<A, Object>(), obj);
        } else if (obj.getClass().isArray()) {
            return new RightBoundPredicate<A, Object>(new IsElementOf<A, Object>(), obj);
        } else {
            throw new IllegalArgumentException("Expected Collection or Array, found " + obj.getClass());
        }
    }

}