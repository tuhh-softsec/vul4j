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
package org.apache.commons.functor.core;

import java.io.Serializable;

import org.apache.commons.functor.BinaryFunction;
import org.apache.commons.functor.BinaryPredicate;
import org.apache.commons.functor.Function;
import org.apache.commons.functor.Predicate;
import org.apache.commons.functor.UnaryFunction;
import org.apache.commons.functor.UnaryPredicate;

/**
 * {@link #evaluate Evaluates} to constant value.
 * <p>
 * {@link #test Tests} to a constant value, assuming
 * a boolean of Boolean value is supplied.
 *
 * Note that although this class implements
 * {@link Serializable}, a given instance will
 * only be truly <code>Serializable</code> if the
 * constant <code>Object</code> is.  Attempts to serialize
 * an instance whose value is not
 * <code>Serializable</code> will result in an exception.
 * </p>
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public final class Constant implements Function, UnaryFunction, BinaryFunction, Predicate, UnaryPredicate, BinaryPredicate, Serializable {

    // constructor
    // ------------------------------------------------------------------------
    public Constant(boolean value) {
        this(new Boolean(value));
    }

    public Constant(Object value) {
        this.value = value;
    }

    // function interface
    // ------------------------------------------------------------------------
    public Object evaluate() {
        return value;
    }

    public Object evaluate(Object obj) {
        return evaluate();
    }

    public Object evaluate(Object left, Object right) {
        return evaluate();
    }

    public boolean test() {
        return ((Boolean) evaluate()).booleanValue();
    }

    public boolean test(Object obj) {
        return test();
    }

    public boolean test(Object left, Object right) {
        return test();
    }

    public boolean equals(Object that) {
        if (that instanceof Constant) {
            return equals((Constant) that);
        } else {
            return false;
        }
    }

    public boolean equals(Constant that) {
        return (null != that && (null == this.value ? null == that.value : this.value.equals(that.value)));
    }

    public int hashCode() {
        int hash = "Constant".hashCode();
        if (null != value) {
            hash ^= value.hashCode();
        }
        return hash;
    }

    public String toString() {
        return "Constant<" + String.valueOf(value) + ">";
    }

    // attributes
    // ------------------------------------------------------------------------
    private Object value;

    // static methods
    // ------------------------------------------------------------------------

    /**
     * Get a <code>Constant</code> that always
     * returns <code>true</code>
     * @return a <code>Constant</code> that always
     *         returns <code>true</code>
     */
    public static Constant truePredicate() {
        return TRUE_PREDICATE;
    }

    /**
     * Get a <code>Constant</code> that always
     * returns <code>false</code>
     * @return a <code>Constant</code> that always
     *         returns <code>false</code>
     */
    public static Constant falsePredicate() {
        return FALSE_PREDICATE;
    }

    /**
     * Get a <code>Constant</code> that always
     * returns <i>value</i>
     * @param value the constant value
     * @return a <code>Constant</code> that always
     *         returns <i>value</i>
     */
    public static Constant predicate(boolean value) {
        return value ? TRUE_PREDICATE : FALSE_PREDICATE;
    }

    public static Constant instance(Object value) {
        return new Constant(value);
    }

    // static attributes
    // ------------------------------------------------------------------------
    private static final Constant TRUE_PREDICATE = new Constant(true);
    private static final Constant FALSE_PREDICATE = new Constant(false);
}
