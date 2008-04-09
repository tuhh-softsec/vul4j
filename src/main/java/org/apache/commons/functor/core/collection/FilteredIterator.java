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
public final class FilteredIterator implements Iterator {
    // attributes
    // ------------------------------------------------------------------------

    private UnaryPredicate predicate = null;
    private Iterator iterator = null;
    private Object next = null;
    private boolean nextSet = false;
    private boolean canRemove = false;

    // constructor
    // ------------------------------------------------------------------------
    /**
     * Create a new FilteredIterator.
     * @param iterator to filter
     * @param predicate to apply
     */
    public FilteredIterator(Iterator iterator, UnaryPredicate predicate) {
        if (null == iterator || null == predicate) {
            throw new NullPointerException();
        } else {
            this.predicate = predicate;
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
        if (nextSet) {
            return true;
        } else {
            return setNext();
        }
    }

    /**
     * {@inheritDoc}
     * @see java.util.Iterator#next()
     */
    public Object next() {
        if (hasNext()) {
            return returnNext();
        } else {
            throw new NoSuchElementException();
        }
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
        if (obj instanceof FilteredIterator == false) {
            return false;
        }
        FilteredIterator that = (FilteredIterator) obj;
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
    public static Iterator filter(Iterator iter, UnaryPredicate pred) {
        return null == pred ? iter : (null == iter ? null : new FilteredIterator(iter, pred));
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
            Object obj = iterator.next();
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
    private Object returnNext() {
        Object temp = next;
        canRemove = true;
        next = null;
        nextSet = false;
        return temp;
    }

}
