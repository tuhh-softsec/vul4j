/* 
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons-sandbox//functor/src/java/org/apache/commons/functor/core/composite/ConditionalUnaryPredicate.java,v 1.3 2003/02/02 21:46:19 rwaldhoff Exp $
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

import org.apache.commons.functor.UnaryPredicate;

/**
 * A {@link UnaryPredicate UnaryPredicate} 
 * similiar to Java's "ternary" 
 * or "conditional" operator (<code>&#x3F; &#x3A;</code>).
 * Given three {@link UnaryPredicate predicate}
 * <i>p</i>, <i>q</i>, <i>r</i>,
 * {@link #test tests}
 * to 
 * <code>p.test(x) ? q.test(x) : r.test(x)</code>.
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
public final class ConditionalUnaryPredicate implements UnaryPredicate, Serializable {

    // constructor
    // ------------------------------------------------------------------------

    public ConditionalUnaryPredicate(UnaryPredicate ifPred, UnaryPredicate thenPred, UnaryPredicate elsePred) {
        this.ifPred = ifPred;
        this.thenPred = thenPred;
        this.elsePred = elsePred;
    }
    
    // predicate interface
    // ------------------------------------------------------------------------
    public boolean test(Object obj) {
        return ifPred.test(obj) ? thenPred.test(obj) : elsePred.test(obj);
    }

    public boolean equals(Object that) {
        if(that instanceof ConditionalUnaryPredicate) {
            return equals((ConditionalUnaryPredicate)that);
        } else {
            return false;
        }
    }
    
    public boolean equals(ConditionalUnaryPredicate that) {
        return null != that && 
                (null == ifPred ? null == that.ifPred : ifPred.equals(that.ifPred)) &&
                (null == thenPred ? null == that.thenPred : thenPred.equals(that.thenPred)) &&
                (null == elsePred ? null == that.elsePred : elsePred.equals(that.elsePred));
    }
    
    public int hashCode() {
        int hash = "ConditionalUnaryPredicate".hashCode();
        if(null != ifPred) {
            hash <<= 4;
            hash ^= ifPred.hashCode();            
        }
        if(null != thenPred) {
            hash <<= 4;
            hash ^= thenPred.hashCode();            
        }
        if(null != elsePred) {
            hash <<= 4;
            hash ^= elsePred.hashCode();            
        }
        return hash;
    }
    
    public String toString() {
        return "ConditionalUnaryPredicate<" + ifPred + "?" + thenPred + ":" + elsePred + ">";
    }

    // attributes
    // ------------------------------------------------------------------------
    private UnaryPredicate ifPred = null;
    private UnaryPredicate thenPred = null;
    private UnaryPredicate elsePred = null;
}
