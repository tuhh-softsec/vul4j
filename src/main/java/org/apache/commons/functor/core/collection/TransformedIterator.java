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
 * Iterator that transforms another Iterator by applying a UnaryFunction to each returned element.
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public final class TransformedIterator<E, T> implements Iterator<T> {

    // attributes
    // ------------------------------------------------------------------------

    private UnaryFunction<? super E, ? extends T> function = null;
    private Iterator<? extends E> iterator = null;

    // constructor
    // ------------------------------------------------------------------------
    /**
     * Create a new TransformedIterator.
     * @param iterator Iterator to decorate
     * @param function to apply
     */
    public TransformedIterator(Iterator<? extends E> iterator, UnaryFunction<? super E, ? extends T> function) {
        if (null == iterator) {
            throw new IllegalArgumentException("Iterator argument was null");
        }
        if (null == function) {
            throw new IllegalArgumentException("filtering UnaryFunction argument was null");
        }
        this.function = function;
        this.iterator = iterator;
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
    public T next() {
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
        if (obj == this) {
            return true;
        }
        if (obj instanceof TransformedIterator<?, ?> == false) {
            return false;
        }
        TransformedIterator<?, ?> that = (TransformedIterator<?, ?>) obj;
        return function.equals(that.function) && iterator.equals(that.iterator);
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
     * Get a Transformed Iterator instance.
     * @param iter to decorate, if null result is null
     * @param func transforming function, cannot be null
     * @return Iterator<T>
     */
    public static <E, T> Iterator<T> transform(Iterator<? extends E> iter, UnaryFunction<? super E, ? extends T> func) {
        if (null == iter) {
            return null;
        }
        return new TransformedIterator<E, T>(iter, func);
    }

    /**
     * Get an Iterator instance that may be transformed.
     * @param iter to decorate, if null result is null
     * @param func transforming function, if null result is iter
     * @return Iterator<?>
     */
    public static <E> Iterator<?> maybeTransform(Iterator<? extends E> iter, UnaryFunction<? super E, ?> func) {
        return null == func ? (null == iter ? null : iter) : new TransformedIterator<E, Object>(iter, func);
    }
    
}
