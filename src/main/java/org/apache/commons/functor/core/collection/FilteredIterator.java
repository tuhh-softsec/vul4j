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
package org.apache.commons.functor.core.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.commons.functor.UnaryPredicate;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public final class FilteredIterator implements Iterator {

    // constructor
    // ------------------------------------------------------------------------
    
    public FilteredIterator(Iterator iterator, UnaryPredicate predicate) {
        if(null == iterator || null == predicate) {
            throw new NullPointerException();
        } else {
            this.predicate = predicate;
            this.iterator = iterator;
        }
    }
    
    // iterator methods
    // ------------------------------------------------------------------------
    
    /**
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
        if(nextSet) {
            return true;
        } else {
            return setNext();
        }
    }

    /**
     * @see java.util.Iterator#next()
     */
    public Object next() {
        if(hasNext()) {            
            return returnNext();
        } else {
            throw new NoSuchElementException();
        }
    }

    /**
     * @see java.util.Iterator#remove()
     */
    public void remove() {
        if(canRemove) {
            canRemove = false;
            iterator.remove();
        } else {
            throw new IllegalStateException();
        }
    }
    

    public boolean equals(Object obj) {
        if(obj instanceof FilteredIterator) {
            FilteredIterator that = (FilteredIterator)obj;
            return predicate.equals(that.predicate) && iterator.equals(that.iterator);  
        } else {
            return false;
        }
    }

    public int hashCode() {
        int hash = "FilteredIterator".hashCode();
        hash <<= 2;
        hash ^= predicate.hashCode();
        hash <<= 2;
        hash ^= iterator.hashCode();
        return hash;
    }

    public String toString() {
        return "FilteredIterator<" + iterator + "," + predicate + ">";
    }
    
    // class methods
    // ------------------------------------------------------------------------
    
    public static Iterator filter(Iterator iter, UnaryPredicate pred) {
        return null == pred ? iter : (null == iter ? null : new FilteredIterator(iter,pred));
    }
 
    // private
    // ------------------------------------------------------------------------
    
    private boolean setNext() {
        while(iterator.hasNext()) {
            canRemove = false;
            Object obj = iterator.next();
            if(predicate.test(obj)) {
                next = obj;
                nextSet = true;
                return true;
            }
        }
        next = null;
        nextSet = false;
        return false;
    }
 
    private Object returnNext() {
        Object temp = next;
        canRemove = true;
        next = null;
        nextSet = false;
        return temp;
    }
 
    // attributes
    // ------------------------------------------------------------------------
    
    private UnaryPredicate predicate = null;
    private Iterator iterator = null;
    private Object next = null;
    private boolean nextSet = false;
    private boolean canRemove = false;
    

}
