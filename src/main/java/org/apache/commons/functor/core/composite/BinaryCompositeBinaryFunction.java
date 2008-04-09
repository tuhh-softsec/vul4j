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

import org.apache.commons.functor.BinaryFunction;

/**
 * A {@link BinaryFunction BinaryFunction} composed of
 * three binary functions, <i>f</i>, <i>g</i> and <i>h</i>,
 * evaluating the ordered parameters <i>x</i>, <i>y</i>
 * to <code><i>f</i>(<i>g</i>(<i>x</i>,<i>y</i>),<i>h</i>(<i>x</i>,<i>y</i>))</code>.
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
public class BinaryCompositeBinaryFunction implements BinaryFunction, Serializable {
    // attributes
    // ------------------------------------------------------------------------
    private BinaryFunction binary = null;
    private BinaryFunction leftBinary = null;
    private BinaryFunction rightBinary = null;

    // constructor
    // ------------------------------------------------------------------------
    /**
     * Create a new BinaryCompositeBinaryFunction.
     * @param f function
     * @param g function
     * @param h function
     */
    public BinaryCompositeBinaryFunction(BinaryFunction f, BinaryFunction g, BinaryFunction h) {
        binary = f;
        leftBinary = g;
        rightBinary = h;
    }

    // function interface
    // ------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public Object evaluate(Object left, Object right) {
        return binary.evaluate(leftBinary.evaluate(left, right), rightBinary.evaluate(left, right));
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return that == this
                || (that instanceof BinaryCompositeBinaryFunction && equals((BinaryCompositeBinaryFunction) that));
    }

    /**
     * Learn whether another BinaryCompositeBinaryFunction is equal to this.
     * @param that BinaryCompositeBinaryFunction to test
     * @return boolean
     */
    public boolean equals(BinaryCompositeBinaryFunction that) {
        return (null != that)
                && (null == binary ? null == that.binary : binary.equals(that.binary))
                && (null == leftBinary ? null == that.leftBinary : leftBinary.equals(that.leftBinary))
                && (null == rightBinary ? null == that.rightBinary : rightBinary.equals(that.rightBinary));
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int hash = "BinaryCompositeBinaryFunction".hashCode();
        if (null != binary) {
            hash <<= 4;
            hash ^= binary.hashCode();
        }
        if (null != leftBinary) {
            hash <<= 4;
            hash ^= leftBinary.hashCode();
        }
        if (null != rightBinary) {
            hash <<= 4;
            hash ^= rightBinary.hashCode();
        }
        return hash;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "BinaryCompositeBinaryFunction<" + binary + ";" + leftBinary + ";" + rightBinary + ">";
    }

}
