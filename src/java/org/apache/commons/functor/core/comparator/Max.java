/*
 * Copyright 2003,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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
    public Max() {
        this(null);
    }

    public Max(Comparator comparator) {
        this.comparator = null == comparator ? ComparableComparator.instance() : comparator;
    }
    
    /**
     * @see org.apache.commons.functor.BinaryFunction#evaluate(Object, Object)
     */
    public Object evaluate(Object left, Object right) {
        return (comparator.compare(left,right) >= 0) ? left : right; 
    }

    /**
     * @see java.lang.Object#equals(Object)
     */
    public boolean equals(Object that) {
        if(that instanceof Max) {
            return equals((Max)that);
        } else {
            return false;
        }
    }

    /**
     * @see #equals(Object)
     */
    public boolean equals(Max that) {
        return null != that && comparator.equals(that.comparator);
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return "Max".hashCode() ^ comparator.hashCode();
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Max<" + comparator + ">";
    }

    public static Max instance() {
        return INSTANCE;
    }
    
    private Comparator comparator = null;
    private static final Max INSTANCE = new Max();
}
