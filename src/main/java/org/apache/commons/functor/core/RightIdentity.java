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

/**
 * {@link #evaluate Evaluates} to its second argument.
 *
 * {@link #test Tests} to the <code>boolean</code>
 * value of the <code>Boolean</code>-valued second
 * argument. The {@link #test test} method
 * throws an exception if the parameter isn't a
 * non-<code>null</code> <code>Boolean</code>.
 *
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public final class RightIdentity implements BinaryPredicate, BinaryFunction, Serializable {

    // static attributes
    // ------------------------------------------------------------------------
    private static final RightIdentity INSTANCE = new RightIdentity();

    // constructor
    // ------------------------------------------------------------------------
    /**
     * Create a new RightIdentity.
     */
    public RightIdentity() {
    }

    // functor interface
    // ------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public Object evaluate(Object left, Object right) {
        return right;
    }

    /**
     * {@inheritDoc}
     */
    public boolean test(Object left, Object right) {
        return test((Boolean) right);
    }

    /**
     * Test a Boolean.
     * @param bool to test
     * @return boolean
     */
    private boolean test(Boolean bool) {
        return bool.booleanValue();
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return (that instanceof RightIdentity);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return "RightIdentity".hashCode();
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "RightIdentity";
    }

    // static methods
    // ------------------------------------------------------------------------
    /**
     * Get a RightIdentity instance.
     * @return RightIdentity
     */
    public static RightIdentity instance() {
        return INSTANCE;
    }

}
