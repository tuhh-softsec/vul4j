/* 
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons-sandbox//functor/src/java/org/apache/commons/functor/adapter/Attic/ConstantRightBinaryProcedureUnaryProcedure.java,v 1.1 2003/01/28 12:54:36 rwaldhoff Exp $
 * ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived 
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package org.apache.commons.functor.adapter;

import java.io.Serializable;

import org.apache.commons.functor.BinaryProcedure;
import org.apache.commons.functor.UnaryProcedure;

/**
 * Adapts a
 * {@link BinaryProcedure BinaryProcedure} 
 * to the 
 * {@link UnaryProcedure UnaryProcedure} interface 
 * using a constant left-side argument.
 * <p/>
 * Note that although this class implements 
 * {@link Serializable}, a given instance will
 * only be truly <code>Serializable</code> if the
 * underlying objects are.  Attempts to serialize
 * an instance whose delegates are not 
 * <code>Serializable</code> will result in an exception.
 * 
 * @version $Revision: 1.1 $ $Date: 2003/01/28 12:54:36 $
 * @author Rodney Waldhoff
 */
public final class ConstantRightBinaryProcedureUnaryProcedure implements UnaryProcedure, Serializable {
    /**
     * @param procedure the procedure to adapt
     * @param arg the constant argument to use
     */
    public ConstantRightBinaryProcedureUnaryProcedure(BinaryProcedure procedure, Object arg) {
        this.procedure = procedure;
        this.param = arg;
    }
 
    public void run(Object obj) {
        procedure.run(obj,param);
    }   

    public boolean equals(Object that) {
        if(that instanceof ConstantRightBinaryProcedureUnaryProcedure) {
            return equals((ConstantRightBinaryProcedureUnaryProcedure)that);
        } else {
            return false;
        }
    }
        
    public boolean equals(ConstantRightBinaryProcedureUnaryProcedure that) {
        return that == this || ( 
                (null != that) && 
                (null == procedure ? null == that.procedure : procedure.equals(that.procedure)) &&
                (null == param ? null == that.param : param.equals(that.param)) );
                
    }
    
    public int hashCode() {
        int hash = "ConstantRightBinaryProcedureUnaryProcedure".hashCode();
        if(null != procedure) {
            hash <<= 2;
            hash ^= procedure.hashCode();
        }
        if(null != param) {
            hash <<= 2;
            hash ^= param.hashCode();
        }
        return hash;
    }
    
    public String toString() {
        return "ConstantRightBinaryProcedureUnaryProcedure<" + procedure + "(?," + param + ")>";
    }

    public static ConstantRightBinaryProcedureUnaryProcedure adapt(BinaryProcedure procedure, Object arg) {
        return null == procedure ? null : new ConstantRightBinaryProcedureUnaryProcedure(procedure,arg);
    }

    /** The {@link Procedure Procedure} I'm wrapping. */
    private BinaryProcedure procedure = null;
    /** The parameter to pass to that procedure. */
    private Object param = null;
}
