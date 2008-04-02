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
package org.apache.commons.functor.core.composite;

import java.util.Iterator;

import org.apache.commons.functor.Predicate;

/**
 * {@link #test Tests} <code>true</code> iff
 * none of its children test <code>false</code>.
 * Note that by this definition, the "and" of
 * an empty collection of predicates tests <code>true</code>.
 * <p>
 * Note that although this class implements 
 * {@link java.io.Serializable Serializable}, a given instance will
 * only be truly <code>Serializable</code> if all the
 * underlying functors are.  Attempts to serialize
 * an instance whose delegates are not all 
 * <code>Serializable</code> will result in an exception.
 * </p>
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public final class And extends BasePredicateList {

    // constructor
    // ------------------------------------------------------------------------
    public And() {
        super();
    }

    public And(Predicate p) {
        super(p);
    }

    public And(Predicate p, Predicate q) {
        super(p,q);
    }

    public And(Predicate p, Predicate q, Predicate r) {
        super(p,q,r);
    }
    
    // modifiers
    // ------------------------------------------------------------------------ 
    public And and(Predicate p) {
        super.addPredicate(p);
        return this;
    }
 
    // predicate interface
    // ------------------------------------------------------------------------
    public boolean test() {
        for(Iterator iter = getPredicateIterator(); iter.hasNext();) {
            if(!((Predicate)iter.next()).test()) {
                return false;
            }
        }
        return true;
    }

    public boolean equals(Object that) {
        if(that instanceof And) {
            return equals((And)that);
        } else {
            return false;
        }
    }
    
    public boolean equals(And that) {
        return getPredicateListEquals(that);
    }
    
    public int hashCode() {
        return "And".hashCode() ^ getPredicateListHashCode();
    }
    
    public String toString() {
        return "And<" + getPredicateListToString() + ">";
    }
    
}
