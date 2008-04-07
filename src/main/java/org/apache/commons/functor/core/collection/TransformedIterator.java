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

import java.util.Iterator;

import org.apache.commons.functor.UnaryFunction;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public final class TransformedIterator implements Iterator {

    // attributes
    // ------------------------------------------------------------------------

    private UnaryFunction function = null;
    private Iterator iterator = null;

    // constructor
    // ------------------------------------------------------------------------
    /**
     * Create a new TransformedIterator.
     * @param iterator Iterator to decorate
     * @param function to apply
     */
    public TransformedIterator(Iterator iterator, UnaryFunction function) {
        if (null == iterator || null == function) {
            throw new NullPointerException();
        } else {
            this.function = function;
            this.iterator = iterator;
        }
    }

    // iterator methods
    // ------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
        return iterator.hasNext();
    }

    /**
     * {@inheritDoc}
     * @see java.util.Iterator#next()
     */
    public Object next() {
        return function.evaluate(iterator.next());
    }

    /**
     * {@inheritDoc}
     * @see java.util.Iterator#remove()
     */
    public void remove() {
        iterator.remove();
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (obj instanceof TransformedIterator) {
            TransformedIterator that = (TransformedIterator) obj;
            return function.equals(that.function) && iterator.equals(that.iterator);
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int hash = "TransformedIterator".hashCode();
        hash <<= 2;
        hash ^= function.hashCode();
        hash <<= 2;
        hash ^= iterator.hashCode();
        return hash;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "TransformedIterator<" + iterator + "," + function + ">";
    }

    // class methods
    // ------------------------------------------------------------------------
    /**
     * Get a TransformedIterator instance.
     * @param iter to decorate
     * @param func transforming function
     * @return Iterator
     */
    //TODO is this the best name for this method?
    public static Iterator transform(Iterator iter, UnaryFunction func) {
        return null == func ? iter : (null == iter ? null : new TransformedIterator(iter,func));
    }

}
