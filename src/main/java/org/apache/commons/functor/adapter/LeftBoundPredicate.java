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
package org.apache.commons.functor.adapter;

import java.io.Serializable;

import org.apache.commons.functor.BinaryPredicate;
import org.apache.commons.functor.UnaryPredicate;

/**
 * Adapts a
 * {@link BinaryPredicate BinaryPredicate}
 * to the
 * {@link UnaryPredicate UnaryPredicate} interface
 * using a constant left-side argument.
 * <p/>
 * Note that although this class implements
 * {@link Serializable}, a given instance will
 * only be truly <code>Serializable</code> if the
 * underlying objects are.  Attempts to serialize
 * an instance whose delegates are not
 * <code>Serializable</code> will result in an exception.
 *
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public final class LeftBoundPredicate implements UnaryPredicate, Serializable {
    /**
     * @param predicate the predicate to adapt
     * @param arg the constant argument to use
     */
    public LeftBoundPredicate(BinaryPredicate predicate, Object arg) {
        this.predicate = predicate;
        this.param = arg;
    }

    public boolean test(Object obj) {
        return predicate.test(param,obj);
    }

    public boolean equals(Object that) {
        if (that instanceof LeftBoundPredicate) {
            return equals((LeftBoundPredicate) that);
        } else {
            return false;
        }
    }

    public boolean equals(LeftBoundPredicate that) {
        return that == this || (
                (null != that) &&
                (null == predicate ? null == that.predicate : predicate.equals(that.predicate)) &&
                (null == param ? null == that.param : param.equals(that.param)) );

    }

    public int hashCode() {
        int hash = "LeftBoundPredicate".hashCode();
        if (null != predicate) {
            hash <<= 2;
            hash ^= predicate.hashCode();
        }
        if (null != param) {
            hash <<= 2;
            hash ^= param.hashCode();
        }
        return hash;
    }

    public String toString() {
        return "LeftBoundPredicate<" + predicate + "(" + param + ",?)>";
    }

    public static LeftBoundPredicate bind(BinaryPredicate predicate, Object arg) {
        return null == predicate ? null : new LeftBoundPredicate(predicate,arg);
    }

    /** The {@link BinaryPredicate BinaryPredicate} I'm wrapping. */
    private BinaryPredicate predicate = null;
    /** The parameter to pass to that predicate. */
    private Object param = null;
}
