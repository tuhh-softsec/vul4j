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

import org.apache.commons.functor.UnaryPredicate;

/**
 * {@link #test Tests}
 * <code>true</code> iff its argument
 * {@link Class#isInstance is an instance}
 * of some specified {@link Class Class}.
 *
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public final class IsInstanceOf implements UnaryPredicate, Serializable {

    // attributes
    // ------------------------------------------------------------------------
    private Class klass;

    // constructor
    // ------------------------------------------------------------------------
    /**
     * Create a new IsInstanceOf.
     * @param klass Class of which a tested object must be an instance.
     */
    public IsInstanceOf(Class klass) {
        this.klass = klass;
    }

    // predicate interface
    // ------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public boolean test(Object obj) {
        return klass.isInstance(obj);
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return that == this || (that instanceof IsInstanceOf && equals((IsInstanceOf) that));
    }

    /**
     * Learn whether another IsInstanceOf is equal to this.
     * @param that IsInstanceOf to test
     * @return boolean
     */
    public boolean equals(IsInstanceOf that) {
        return (null != that && (null == this.klass ? null == that.klass : this.klass.equals(that.klass)));
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int hash = "IsInstanceOf".hashCode();
        if (null != klass) {
            hash ^= klass.hashCode();
        }
        return hash;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "IsInstanceOf<" + klass + ">";
    }

}
