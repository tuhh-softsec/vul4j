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
package org.apache.commons.functor.core.composite;

import java.io.Serializable;

import org.apache.commons.functor.Predicate;

/**
 * {@link #test Tests} to the logical inverse
 * of some other predicate.
 * <p>
 * Note that although this class implements
 * {@link Serializable}, a given instance will
 * only be truly <code>Serializable</code> if the
 * underlying functor is.  Attempts to serialize
 * an instance whose delegate is not
 * <code>Serializable</code> will result in an exception.
 * </p>
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public final class Not implements Predicate, Serializable {

    // attributes
    // ------------------------------------------------------------------------
    private Predicate predicate = null;

    // constructor
    // ------------------------------------------------------------------------
    /**
     * Create a new Not.
     * @param predicate Predicate to negate
     */
    public Not(Predicate predicate) {
        if (predicate == null) {
            throw new IllegalArgumentException("Predicate argument was null");
        }
        this.predicate = predicate;
    }

    // predicate interface
    // ------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public boolean test() {
        return !(predicate.test());
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return that == this || (that instanceof Not && equals((Not) that));
    }

    /**
     * Learn whether another Not is equal to this.
     * @param that the Not to test
     * @return boolean
     */
    public boolean equals(Not that) {
        return null != that && (null == predicate ? null == that.predicate : predicate.equals(that.predicate));
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int hash = "Not".hashCode();
        if (null != predicate) {
            hash ^= predicate.hashCode();
        }
        return hash;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "Not<" + predicate + ">";
    }

    // static
    // ------------------------------------------------------------------------
    /**
     * Get a Not instance for <code>that</code>.
     * @param that Predicate to negate
     * @return Not
     */
    public static Predicate not(Predicate that) {
        return null == that ? null : new Not(that);
    }
}
