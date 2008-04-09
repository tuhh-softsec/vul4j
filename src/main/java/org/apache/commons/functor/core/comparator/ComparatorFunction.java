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
public final class ComparatorFunction implements BinaryFunction, Serializable {
    private Comparator comparator = null;

    /**
     * Create a new ComparatorFunction.
     */
    public ComparatorFunction() {
        this(null);
    }

    /**
     * Create a new ComparatorFunction.
     * @param comparator to wrap
     */
    public ComparatorFunction(Comparator comparator) {
        this.comparator = null == comparator ? ComparableComparator.instance() : comparator;
    }

    /**
     * {@inheritDoc}
     */
    public Object evaluate(Object left, Object right) {
        return new Integer(comparator.compare(left, right));
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return that == this || (that instanceof ComparatorFunction && equals((ComparatorFunction) that));
    }

    /**
     * Learn whether a specified ComparatorFunction is equal to this.
     * @param that the ComparatorFunction to test
     * @return boolean
     */
    public boolean equals(ComparatorFunction that) {
        return null != that && comparator.equals(that.comparator);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return "ComparatorFunction".hashCode() ^ comparator.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "ComparatorFunction<" + comparator + ">";
    }

}
