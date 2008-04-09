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
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.functor.UnaryFunction;

/**
 * A {@link UnaryFunction UnaryFunction}
 * representing the composition of
 * {@link UnaryFunction UnaryFunctions},
 * "chaining" the output of one to the input
 * of another.  For example,
 * <pre>new CompositeUnaryFunction(f).of(g)</code>
 * {@link #evaluate evaluates} to
 * <code>f.evaluate(g.evaluate(obj))</code>, and
 * <pre>new CompositeUnaryFunction(f).of(g).of(h)</pre>
 * {@link #evaluate evaluates} to
 * <code>f.evaluate(g.evaluate(h.evaluate(obj)))</code>.
 * <p>
 * When the collection is empty, this function is
 * an identity function.
 * </p>
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
public class CompositeUnaryFunction implements UnaryFunction, Serializable {

    // attributes
    // ------------------------------------------------------------------------
    private List list = new ArrayList();

    // constructor
    // ------------------------------------------------------------------------
    /**
     * Create a new CompositeUnaryFunction.
     */
    public CompositeUnaryFunction() {
    }

    /**
     * Create a new CompositeUnaryFunction.
     * @param f UnaryFunction to add
     */
    public CompositeUnaryFunction(UnaryFunction f) {
        of(f);
    }

    /**
     * Create a new CompositeUnaryFunction.
     * @param f UnaryFunction to add
     * @param g UnaryFunction to add
     */
    public CompositeUnaryFunction(UnaryFunction f, UnaryFunction g) {
        of(f);
        of(g);
    }

    // modifiers
    // ------------------------------------------------------------------------
    /**
     * Fluently prepend a UnaryFunction to the chain.
     * @param f UnaryFunction to prepend
     * @return this
     */
    public CompositeUnaryFunction of(UnaryFunction f) {
        list.add(f);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public Object evaluate(Object obj) {
        Object result = obj;
        for (ListIterator iter = list.listIterator(list.size()); iter.hasPrevious();) {
            result = ((UnaryFunction) iter.previous()).evaluate(result);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return that == this || (that instanceof CompositeUnaryFunction && equals((CompositeUnaryFunction) that));
    }

    /**
     * Learn whether another CompositeUnaryFunction is equal to this.
     * @param that CompositeUnaryFunction to test
     * @return boolean
     */
    public boolean equals(CompositeUnaryFunction that) {
        // by construction, list is never null
        return null != that && list.equals(that.list);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        // by construction, list is never null
        return "CompositeUnaryFunction".hashCode() ^ list.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "CompositeUnaryFunction<" + list + ">";
    }

}
