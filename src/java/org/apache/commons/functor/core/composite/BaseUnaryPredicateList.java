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
package org.apache.commons.functor.core.composite;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.functor.UnaryPredicate;

/**
 * Abstract base class for {@link UnaryPredicate UnaryPredicates}
 * composed of a list of {@link UnaryPredicate UnaryPredicates}.
 * <p>
 * Note that although this class implements 
 * {@link Serializable}, a given instance will
 * only be truly <code>Serializable</code> if all the
 * underlying functors are.  Attempts to serialize
 * an instance whose delegates are not all 
 * <code>Serializable</code> will result in an exception.
 * </p>
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
abstract class BaseUnaryPredicateList implements UnaryPredicate, Serializable {

    // constructor
    // ------------------------------------------------------------------------
    protected BaseUnaryPredicateList() {
    }

    protected BaseUnaryPredicateList(UnaryPredicate p) {
        addUnaryPredicate(p);
    }

    protected BaseUnaryPredicateList(UnaryPredicate p, UnaryPredicate q) {
        addUnaryPredicate(p);
        addUnaryPredicate(q);
    }

    protected BaseUnaryPredicateList(UnaryPredicate p, UnaryPredicate q, UnaryPredicate r) {
        addUnaryPredicate(p);
        addUnaryPredicate(q);
        addUnaryPredicate(r);
    }
    
    // abstract
    // ------------------------------------------------------------------------ 
    public abstract boolean equals(Object that);
    public abstract int hashCode();
    public abstract String toString();
    public abstract boolean test(Object obj);

    // modifiers
    // ------------------------------------------------------------------------ 
    protected void addUnaryPredicate(UnaryPredicate p) {
        list.add(p);
    }
 
    // protected
    // ------------------------------------------------------------------------

    protected Iterator getUnaryPredicateIterator() {
        return list.iterator();
    }
    
    protected boolean getUnaryPredicateListEquals(BaseUnaryPredicateList that) {
        return (null != that && this.list.equals(that.list));
    }
    
    protected int getUnaryPredicateListHashCode() {
        return list.hashCode();
    }
    
    protected String getUnaryPredicateListToString() {
        return String.valueOf(list);
    }
    
    // attributes
    // ------------------------------------------------------------------------
    private List list = new ArrayList();

}
