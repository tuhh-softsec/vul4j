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

import org.apache.commons.functor.BinaryFunction;
import org.apache.commons.functor.UnaryFunction;

/**
 * Adapts a
 * {@link UnaryFunction UnaryFunction}
 * to the
 * {@link BinaryFunction BinaryFunction} interface
 * by ignoring the first binary argument.
 * <p/>
 * Note that although this class implements
 * {@link Serializable}, a given instance will
 * only be truly <code>Serializable</code> if the
 * underlying functor is.  Attempts to serialize
 * an instance whose delegate is not
 * <code>Serializable</code> will result in an exception.
 *
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public final class IgnoreLeftFunction implements BinaryFunction, Serializable {
    public IgnoreLeftFunction(UnaryFunction function) {
        this.function = function;
    }

    public Object evaluate(Object left, Object right) {
        return function.evaluate(right);
    }

    public boolean equals(Object that) {
        if (that instanceof IgnoreLeftFunction) {
            return equals((IgnoreLeftFunction) that);
        } else {
            return false;
        }
    }

    public boolean equals(IgnoreLeftFunction that) {
        return that == this || (null != that && (null == function ? null == that.function : function.equals(that.function)));
    }

    public int hashCode() {
        int hash = "IgnoreLeftFunction".hashCode();
        if (null != function) {
            hash ^= function.hashCode();
        }
        return hash;
    }

    public String toString() {
        return "IgnoreLeftFunction<" + function + ">";
    }

    public static BinaryFunction adapt(UnaryFunction function) {
        return null == function ? null : new IgnoreLeftFunction(function);
    }

    /** The {@link UnaryFunction UnaryFunction} I'm wrapping. */
    private UnaryFunction function = null;
}
