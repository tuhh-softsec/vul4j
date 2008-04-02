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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.functor.BinaryPredicate;

/**
 * Abstract base class for {@link BinaryPredicate BinaryPredicates}
 * composed of a list of {@link BinaryPredicate BinaryPredicates}.
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
abstract class BaseBinaryPredicateList implements BinaryPredicate, Serializable {

    // constructor
    // ------------------------------------------------------------------------
    protected BaseBinaryPredicateList() {
    }

    protected BaseBinaryPredicateList(BinaryPredicate p) {
        addBinaryPredicate(p);
    }

    protected BaseBinaryPredicateList(BinaryPredicate p, BinaryPredicate q) {
        addBinaryPredicate(p);
        addBinaryPredicate(q);
    }

    protected BaseBinaryPredicateList(BinaryPredicate p, BinaryPredicate q, BinaryPredicate r) {
        addBinaryPredicate(p);
        addBinaryPredicate(q);
        addBinaryPredicate(r);
    }
    
    // abstract
    // ------------------------------------------------------------------------ 
    public abstract boolean equals(Object that);
    public abstract int hashCode();
    public abstract String toString();
    public abstract boolean test(Object left, Object right);

    // modifiers
    // ------------------------------------------------------------------------ 
    protected void addBinaryPredicate(BinaryPredicate p) {
        list.add(p);
    }
 
    // protected
    // ------------------------------------------------------------------------

    protected Iterator getBinaryPredicateIterator() {
        return list.iterator();
    }
    
    protected boolean getBinaryPredicateListEquals(BaseBinaryPredicateList that) {
        return (null != that && this.list.equals(that.list));
    }
    
    protected int getBinaryPredicateListHashCode() {
        return list.hashCode();
    }
    
    protected String getBinaryPredicateListToString() {
        return String.valueOf(list);
    }
    
    // attributes
    // ------------------------------------------------------------------------
    private List list = new ArrayList();

}
