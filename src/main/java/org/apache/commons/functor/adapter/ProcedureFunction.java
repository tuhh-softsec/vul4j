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
import org.apache.commons.functor.Procedure;

/**
 * Adapts a
 * {@link Procedure Procedure}
 * to the
 * {@link Function Function} interface
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
public final class ProcedureFunction implements Function, Serializable {
    public ProcedureFunction(Procedure procedure) {
        this.procedure = procedure;
    }

    public Object evaluate() {
        procedure.run();
        return null;
    }

    public boolean equals(Object that) {
        if(that instanceof ProcedureFunction) {
            return equals((ProcedureFunction)that);
        } else {
            return false;
        }
    }

    public boolean equals(ProcedureFunction that) {
        return that == this || (null != that && (null == procedure ? null == that.procedure : procedure.equals(that.procedure)));
    }

    public int hashCode() {
        int hash = "ProcedureFunction".hashCode();
        if(null != procedure) {
            hash ^= procedure.hashCode();
        }
        return hash;
    }

    public String toString() {
        return "ProcedureFunction<" + procedure + ">";
    }

    public static ProcedureFunction adapt(Procedure procedure) {
        return null == procedure ? null : new ProcedureFunction(procedure);
    }

    /** The {@link Procedure Procedure} I'm wrapping. */
    private Procedure procedure = null;
}
