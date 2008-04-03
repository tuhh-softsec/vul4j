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
    public ComparatorFunction() {
        this(null);
    }

    public ComparatorFunction(Comparator comparator) {
        this.comparator = null == comparator ? ComparableComparator.instance() : comparator;
    }

    /**
     * @see org.apache.commons.functor.BinaryFunction#evaluate(Object, Object)
     */
    public Object evaluate(Object left, Object right) {
        return new Integer(comparator.compare(left,right));
    }

    /**
     * @see java.lang.Object#equals(Object)
     */
    public boolean equals(Object that) {
        if (that instanceof ComparatorFunction) {
            return equals((ComparatorFunction) that);
        } else {
            return false;
        }
    }

    /**
     * @see #equals(Object)
     */
    public boolean equals(ComparatorFunction that) {
        return null != that && comparator.equals(that.comparator);
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return "ComparatorFunction".hashCode() ^ comparator.hashCode();
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "ComparatorFunction<" + comparator + ">";
    }

    private Comparator comparator = null;
}
