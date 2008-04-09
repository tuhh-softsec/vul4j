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
import java.util.Comparator;

import org.apache.commons.functor.BinaryFunction;

/**
 * Adapts a {@link Comparator Comparator} to the
 * {@link BinaryFunction} interface.
 *
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public final class Max implements BinaryFunction, Serializable {
    private static final Max INSTANCE = new Max();

    private Comparator comparator = null;

    /**
     * Create a new Max.
     */
    public Max() {
        this(null);
    }

    /**
     * Create a new Max.
     * @param comparator Comparator to use
     */
    public Max(Comparator comparator) {
        this.comparator = null == comparator ? ComparableComparator.instance() : comparator;
    }

    /**
     * {@inheritDoc}
     */
    public Object evaluate(Object left, Object right) {
        return (comparator.compare(left, right) >= 0) ? left : right;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return that == this || (that instanceof Max && equals((Max) that));
    }

    /**
     * Learn whether another Max is equal to this.
     * @param that Max to test
     * @return boolean
     */
    public boolean equals(Max that) {
        return null != that && comparator.equals(that.comparator);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return "Max".hashCode() ^ comparator.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "Max<" + comparator + ">";
    }

    /**
     * Get a Max instance.
     * @return Max
     */
    public static Max instance() {
        return INSTANCE;
    }

}
