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
import java.util.NoSuchElementException;

import org.apache.commons.functor.UnaryPredicate;

/**
 * Iterator that filters another Iterator by only passing through those elements
 * that are matched by a specified UnaryPredicate.
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public final class FilteredIterator<T> implements Iterator<T> {
    // attributes
    // ------------------------------------------------------------------------

    private UnaryPredicate<? super T> predicate = null;
    private Iterator<? extends T> iterator = null;
    private T next = null;
    private boolean nextSet = false;
    private boolean canRemove = false;

    // constructor
    // ------------------------------------------------------------------------
    /**
     * Create a new FilteredIterator.
     * @param iterator to filter
     * @param predicate to apply
     */
    public FilteredIterator(Iterator<? extends T> iterator, UnaryPredicate<? super T> predicate) {
        if (null == iterator) {
            throw new IllegalArgumentException("Iterator argument was null");
        }
        if (null == predicate) {
            throw new IllegalArgumentException("filtering UnaryPredicate argument was null");
        }
        this.predicate = predicate;
        this.iterator = iterator;
    }

    // iterator methods
    // ------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
        return nextSet || setNext();
    }

    /**
     * {@inheritDoc}
     * @see java.util.Iterator#next()
     */
    public T next() {
        if (hasNext()) {
            return returnNext();
        }
        throw new NoSuchElementException();
    }

    /**
     * {@inheritDoc}
     * @see java.util.Iterator#remove()
     */
    public void remove() {
        if (canRemove) {
            canRemove = false;
            iterator.remove();
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof FilteredIterator<?> == false) {
            return false;
        }
        FilteredIterator<?> that = (FilteredIterator<?>) obj;
        return predicate.equals(that.predicate) && iterator.equals(that.iterator);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int hash = "FilteredIterator".hashCode();
        hash <<= 2;
        hash ^= predicate.hashCode();
        hash <<= 2;
        hash ^= iterator.hashCode();
        return hash;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "FilteredIterator<" + iterator + "," + predicate + ">";
    }

    // static methods
    // ------------------------------------------------------------------------
    /**
     * Get a filtered Iterator instance applying <code>pred</code> to <code>iter</code>.
     * @param iter to filter
     * @param pred to apply
     * @return Iterator
     */
    @SuppressWarnings("unchecked")
    public static <T> Iterator<T> filter(Iterator<? extends T> iter, UnaryPredicate<? super T> pred) {
        return null == pred ? (Iterator<T>) iter : (null == iter ? null : new FilteredIterator<T>(iter, pred));
    }

    // private
    // ------------------------------------------------------------------------
    /**
     * Set next element.
     * @return whether the current iterator position is valid
     */
    private boolean setNext() {
        while (iterator.hasNext()) {
            canRemove = false;
            T obj = iterator.next();
            if (predicate.test(obj)) {
                nextSet = true;
                next = obj;
                return true;
            }
        }
        next = null;
        nextSet = false;
        return false;
    }

    /**
     * Get the next element.
     * @return next element.
     */
    private T returnNext() {
        T temp = next;
        canRemove = true;
        next = null;
        nextSet = false;
        return temp;
    }

}
