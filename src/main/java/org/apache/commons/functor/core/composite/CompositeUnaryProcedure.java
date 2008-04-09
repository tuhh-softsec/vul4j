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

import org.apache.commons.functor.UnaryFunction;
import org.apache.commons.functor.UnaryProcedure;

/**
 * A {@link UnaryProcedure UnaryProcedure}
 * representing the composition of
 * {@link UnaryFunction UnaryFunctions},
 * "chaining" the output of one to the input
 * of another.  For example,
 * <pre>new CompositeUnaryProcedure(p).of(f)</code>
 * {@link #run runs} to
 * <code>p.run(f.evaluate(obj))</code>, and
 * <pre>new CompositeUnaryProcedure(p).of(f).of(g)</pre>
 * {@link #run runs}
 * <code>p.run(f.evaluate(g.evaluate(obj)))</code>.
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
public final class CompositeUnaryProcedure implements UnaryProcedure, Serializable {
    // attributes
    // ------------------------------------------------------------------------
    private CompositeUnaryFunction function = null;
    private UnaryProcedure procedure = null;

    // constructor
    // ------------------------------------------------------------------------
    /**
     * Create a new CompositeUnaryProcedure.
     * @param p final UnaryProcedure to run
     */
    public CompositeUnaryProcedure(UnaryProcedure p) {
        if (null == p) {
            throw new NullPointerException();
        }
        this.procedure = p;
        this.function = new CompositeUnaryFunction();
    }

    /**
     * Create a new CompositeUnaryProcedure.
     * @param p final UnaryProcedure to run
     * @param f UnaryFunction to chain into <code>p</code>
     */
    public CompositeUnaryProcedure(UnaryProcedure p, UnaryFunction f) {
        if (null == p) {
            throw new NullPointerException();
        }
        if (null == f) {
            throw new NullPointerException();
        }
        this.procedure = p;
        this.function = new CompositeUnaryFunction(f);
    }

    // modifiers
    // ------------------------------------------------------------------------
    /**
     * Fluently prepend a transformation to the chain.
     * @param f UnaryFunction to prepend
     * @return this
     */
    public CompositeUnaryProcedure of(UnaryFunction f) {
        function.of(f);
        return this;
    }

    // predicate interface
    // ------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public void run(Object obj) {
        procedure.run(function.evaluate(obj));
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return that == this || (that instanceof CompositeUnaryProcedure && equals((CompositeUnaryProcedure) that));
    }

    /**
     * Learn whether another CompositeUnaryProcedure is equal to this.
     * @param that CompositeUnaryProcedure to test
     * @return boolean
     */
    public boolean equals(CompositeUnaryProcedure that) {
        return null != that && procedure.equals(that.procedure) && function.equals(that.function);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int hash = "CompositeUnaryProcedure".hashCode();
        hash <<= 2;
        hash ^= procedure.hashCode();
        hash <<= 2;
        hash ^= function.hashCode();
        return hash;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "CompositeUnaryProcedure<" + procedure + ";" + function + ">";
    }

}
