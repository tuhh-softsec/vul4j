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

import org.apache.commons.functor.UnaryFunction;
import org.apache.commons.functor.UnaryPredicate;

/**
 * {@link #evaluate Evaluates} to its input argument.
 *
 * {@link #test Tests} to the <code>boolean</code>
 * value of the <code>Boolean</code>-valued parameter.
 * The {@link #test} method throws an exception if
 * the parameter isn't a non-<code>null</code>
 * <code>Boolean</code>.
 *
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public final class Identity<T> implements UnaryFunction<T, T>, UnaryPredicate<T>, Serializable {

    // static attributes
    // ------------------------------------------------------------------------
    public static final Identity<Object> INSTANCE = new Identity<Object>();

    // constructor
    // ------------------------------------------------------------------------

    /**
     * Create a new Identity.
     */
    public Identity() {
    }

    // function interface
    // ------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    public T evaluate(T obj) {
        return obj;
    }

    /**
     * {@inheritDoc}
     */
    public boolean test(Object obj) {
        return test((Boolean) obj);
    }

    /**
     * Test a Boolean object by returning its <code>booleanValue</code>.
     * @param bool Boolean
     * @return boolean
     */
    public boolean test(Boolean bool) {
        return bool.booleanValue();
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return (that instanceof Identity<?>);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return "Identity".hashCode();
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "Identity";
    }

    // static methods
    // ------------------------------------------------------------------------

    /**
     * Get an Identity instance.
     * @return Identity
     */
    public static <T> Identity<T> instance() {
        return new Identity<T>();
    }

}
