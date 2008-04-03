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

import org.apache.commons.functor.Function;
import org.apache.commons.functor.UnaryFunction;

/**
 * Adapts a
 * {@link Function Function}
 * to the
 * {@link UnaryFunction UnaryFunction} interface
 * by ignoring the unary argument.
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
public final class FunctionUnaryFunction implements UnaryFunction, Serializable {
    public FunctionUnaryFunction(Function function) {
        this.function = function;
    }

    public Object evaluate(Object obj) {
        return function.evaluate();
    }

    public boolean equals(Object that) {
        if(that instanceof FunctionUnaryFunction) {
            return equals((FunctionUnaryFunction)that);
        } else {
            return false;
        }
    }

    public boolean equals(FunctionUnaryFunction that) {
        return that == this || (null != that && (null == function ? null == that.function : function.equals(that.function)));
    }

    public int hashCode() {
        int hash = "FunctionUnaryFunction".hashCode();
        if(null != function) {
            hash ^= function.hashCode();
        }
        return hash;
    }

    public String toString() {
        return "FunctionUnaryFunction<" + function + ">";
    }

    public static FunctionUnaryFunction adapt(Function function) {
        return null == function ? null : new FunctionUnaryFunction(function);
    }

    /** The {@link Function Function} I'm wrapping. */
    private Function function = null;
}
