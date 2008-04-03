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

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collection;

import org.apache.commons.functor.BinaryPredicate;
import org.apache.commons.functor.UnaryPredicate;
import org.apache.commons.functor.adapter.RightBoundPredicate;

/**
 * A {@link BinaryPredicate} that checks to see if the
 * specified object is an element of the specified
 * Collection.
 *
 * @since 1.0
 * @version $Revision$ $Date$
 * @author  Jason Horman (jason@jhorman.org)
 * @author  Rodney Waldhoff
 */
public final class IsElementOf implements BinaryPredicate, Serializable {

    // constructors
    //---------------------------------------------------------------
    public IsElementOf() {
    }

    // instance methods
    //---------------------------------------------------------------

    public boolean test(Object obj, Object col) {
        if (col instanceof Collection) {
            return testCollection(obj,(Collection) col);
        } else if (null != col && col.getClass().isArray()) {
            return testArray(obj,col);
        } else if (null == col) {
            throw new NullPointerException("Right side argument must not be null.");
        } else {
            throw new IllegalArgumentException("Expected Collection or Array, found " + col.getClass());
        }
    }

    public boolean equals(Object obj) {
        return (obj instanceof IsElementOf);
    }

    public int hashCode() {
        return "IsElementOf".hashCode();
    }

    public String toString() {
        return "IsElementOf";
    }

    private boolean testCollection(Object obj, Collection col) {
        return col.contains(obj);
    }

    private boolean testArray(Object obj, Object array) {
        for (int i=0,m=Array.getLength(array);i<m;i++) {
            Object value = Array.get(array,i);
            if (null == obj) {
                if (null == value) {
                    return true;
                }
            } else if (obj.equals(value)) {
                return true;
            }
        }
        return false;
    }


    // class methods
    //---------------------------------------------------------------

    public static IsElementOf instance() {
        return INSTANCE;
    }

    public static UnaryPredicate instance(Object obj) {
        if (null == obj) {
            throw new NullPointerException("Argument must not be null");
        } else if (obj instanceof Collection) {
            return new RightBoundPredicate(instance(),obj);
        } else if (obj.getClass().isArray()) {
            return new RightBoundPredicate(instance(),obj);
        } else {
            throw new IllegalArgumentException("Expected Collection or Array, found " + obj.getClass());
        }
    }

    // class variables
    //---------------------------------------------------------------

    private static IsElementOf INSTANCE = new IsElementOf();

}