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

import org.apache.commons.functor.BinaryProcedure;

/**
 * Transposes (swaps) the arguments to some other
 * {@link BinaryProcedure procedure}.
 * For example, given a procedure <i>p</i>
 * and the ordered pair of arguments <i>a</i>,
 * <i>b</i>.
 * {@link #run runs}
 * <code>p.run(b,a)</code>.
 * <p>
 * Note that although this class implements
 * {@link Serializable}, a given instance will
 * only be truly <code>Serializable</code> if the
 * underlying functor is.  Attempts to serialize
 * an instance whose delegate is not
 * <code>Serializable</code> will result in an exception.
 * </p>
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class TransposedProcedure implements BinaryProcedure, Serializable {

    // constructor
    // ------------------------------------------------------------------------
    public TransposedProcedure(BinaryProcedure p) {
        procedure = p;
    }

    // functor interface
    // ------------------------------------------------------------------------
    public void run(Object left, Object right) {
        procedure.run(right,left);
    }

    public boolean equals(Object that) {
        if (that instanceof TransposedProcedure) {
            return equals((TransposedProcedure) that);
        } else {
            return false;
        }
    }

    public boolean equals(TransposedProcedure that) {
        return null != that && (null == procedure ? null == that.procedure : procedure.equals(that.procedure));
    }

    public int hashCode() {
        int hash = "TransposedProcedure".hashCode();
        if (null != procedure) {
            hash ^= procedure.hashCode();
        }
        return hash;
    }

    public String toString() {
        return "TransposedProcedure<" + procedure + ">";
    }

    // static
    // ------------------------------------------------------------------------
    public static TransposedProcedure transpose(BinaryProcedure p) {
        return null == p ? null : new TransposedProcedure(p);
    }

    // attributes
    // ------------------------------------------------------------------------
    private BinaryProcedure procedure = null;

}
