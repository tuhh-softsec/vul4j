/* 
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons-sandbox//functor/src/java/org/apache/commons/functor/core/composite/UnaryCompositeBinaryFunction.java,v 1.3 2003/02/02 21:46:19 rwaldhoff Exp $
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
package org.apache.commons.functor.core.composite;

import java.io.Serializable;

import org.apache.commons.functor.BinaryFunction;
import org.apache.commons.functor.UnaryFunction;

/**
 * A {@link BinaryFunction BinaryFunction} composed of
 * one binary function, <i>f</i>, and two unary
 * functions, <i>g</i> and <i>h</i>,
 * evaluating the ordered parameters <i>x</i>, <i>y</i> 
 * to <code><i>f</i>(<i>g</i>(<i>x</i>),<i>h</i>(<i>y</i>))</code>.
 * <p>
 * Note that although this class implements 
 * {@link Serializable}, a given instance will
 * only be truly <code>Serializable</code> if all the
 * underlying functors are.  Attempts to serialize
 * an instance whose delegates are not all 
 * <code>Serializable</code> will result in an exception.
 * </p>
 * @version $Revision: 1.3 $ $Date: 2003/02/02 21:46:19 $
 * @author Rodney Waldhoff
 */
public class UnaryCompositeBinaryFunction implements BinaryFunction, Serializable {

    // constructor
    // ------------------------------------------------------------------------
    public UnaryCompositeBinaryFunction(BinaryFunction f, UnaryFunction g, UnaryFunction h) {
        binary = f;
        leftUnary = g;
        rightUnary = h;        
    }

    // function interface
    // ------------------------------------------------------------------------
    public Object evaluate(Object left, Object right) {
        return binary.evaluate(leftUnary.evaluate(left), rightUnary.evaluate(right));
    }

    public boolean equals(Object that) {
        if(that instanceof UnaryCompositeBinaryFunction) {
            return equals((UnaryCompositeBinaryFunction)that);
        } else {
            return false;
        }
    }
    
    public boolean equals(UnaryCompositeBinaryFunction that) {
        return (null != that) &&
            (null == binary ? null == that.binary : binary.equals(that.binary)) &&
            (null == leftUnary ? null == that.leftUnary : leftUnary.equals(that.leftUnary)) &&
            (null == rightUnary ? null == that.rightUnary : rightUnary.equals(that.rightUnary));
    }
    
    public int hashCode() {
        int hash = "UnaryCompositeBinaryFunction".hashCode();
        if(null != binary) {
            hash <<= 4;
            hash ^= binary.hashCode();            
        }
        if(null != leftUnary) {
            hash <<= 4;
            hash ^= leftUnary.hashCode();            
        }
        if(null != rightUnary) {
            hash <<= 4;
            hash ^= rightUnary.hashCode();            
        }
        return hash;
    }
    
    public String toString() {
        return "UnaryCompositeBinaryFunction<" + binary + ";" + leftUnary + ";" + rightUnary + ">";
    }
        
    // attributes
    // ------------------------------------------------------------------------
    private BinaryFunction binary = null;
    private UnaryFunction leftUnary = null;
    private UnaryFunction rightUnary = null;

}
