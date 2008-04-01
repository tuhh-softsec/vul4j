/*
 * Copyright 2003,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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
import org.apache.commons.functor.BinaryProcedure;

/**
 * Adapts a
 * {@link BinaryProcedure BinaryProcedure} 
 * to the 
 * {@link BinaryFunction BinaryFunction} interface
 * by always returning <code>null</code>.
 * <p/>
 * Note that although this class implements 
 * {@link Serializable}, a given instance will
 * only be truly <code>Serializable</code> if the
 * underlying procedure is.  Attempts to serialize
 * an instance whose delegate is not 
 * <code>Serializable</code> will result in an exception.
 * 
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public final class BinaryProcedureBinaryFunction implements BinaryFunction, Serializable {
    public BinaryProcedureBinaryFunction(BinaryProcedure procedure) {
        this.procedure = procedure;
    }
 
    public Object evaluate(Object left, Object right) {
        procedure.run(left,right);
        return null;
    }   

    public boolean equals(Object that) {
        if(that instanceof BinaryProcedureBinaryFunction) {
            return equals((BinaryProcedureBinaryFunction)that);
        } else {
            return false;
        }
    }
        
    public boolean equals(BinaryProcedureBinaryFunction that) {
        return that == this || (null != that && (null == procedure ? null == that.procedure : procedure.equals(that.procedure)));
    }
    
    public int hashCode() {
        int hash = "BinaryProcedureBinaryFunction".hashCode();
        if(null != procedure) {
            hash ^= procedure.hashCode();
        }
        return hash;
    }
    
    public String toString() {
        return "BinaryProcedureBinaryFunction<" + procedure + ">";
    }

    /**
     * Adapt the given, possibly-<code>null</code>, 
     * {@link BinaryProcedure BinaryProcedure} to the
     * {@link BinaryFunction BinaryFunction} interface.
     * When the given <code>BinaryProcedure</code> is <code>null</code>,
     * returns <code>null</code>.
     * 
     * @param procedure the possibly-<code>null</code> 
     *        {@link BinaryFunction BinaryFunction} to adapt
     * @return a <code>BinaryProcedureBinaryFunction</code> wrapping the given
     *         {@link BinaryFunction BinaryFunction}, or <code>null</code>
     *         if the given <code>BinaryFunction</code> is <code>null</code>
     */
    public static BinaryProcedureBinaryFunction adapt(BinaryProcedure procedure) {
        return null == procedure ? null : new BinaryProcedureBinaryFunction(procedure);
    }

    /** The {@link BinaryProcedure BinaryProcedure} I'm wrapping. */
    private BinaryProcedure procedure = null;
}
