/* 
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons-sandbox//functor/src/java/org/apache/commons/functor/core/composite/ConditionalUnaryProcedure.java,v 1.4 2003/03/04 23:11:15 rwaldhoff Exp $
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
import org.apache.commons.functor.UnaryProcedure;

/**
 * A {@link UnaryProcedure UnaryProcedure} 
 * similiar to Java's "ternary" 
 * or "conditional" operator (<code>&#x3F; &#x3A;</code>).
 * Given a {@link UnaryPredicate predicate}
 * <i>p</i> and {@link UnaryProcedure procedures}
 * <i>q</i> and <i>r</i>, {@link #run runs}
 * <code>if(p.test(x)) { q.run(x); } else { r.run(x); }</code>.
 * <p>
 * Note that although this class implements 
 * {@link Serializable}, a given instance will
 * only be truly <code>Serializable</code> if all the
 * underlying functors are.  Attempts to serialize
 * an instance whose delegates are not all 
 * <code>Serializable</code> will result in an exception.
 * </p>
 * @version $Revision: 1.4 $ $Date: 2003/03/04 23:11:15 $
 * @author Rodney Waldhoff
 */
public final class ConditionalUnaryProcedure implements UnaryProcedure, Serializable {

    // constructor
    // ------------------------------------------------------------------------

    public ConditionalUnaryProcedure(UnaryPredicate ifPred, UnaryProcedure thenPred, UnaryProcedure elsePred) {
        this.ifPred = ifPred;
        this.thenProc = thenPred;
        this.elseProc = elsePred;
    }
    
    // predicate interface
    // ------------------------------------------------------------------------
    public void run(Object obj) {
        if(ifPred.test(obj)) {
            thenProc.run(obj);
        } else {
            elseProc.run(obj);
        }
    }

    public boolean equals(Object that) {
        if(that instanceof ConditionalUnaryProcedure) {
            return equals((ConditionalUnaryProcedure)that);
        } else {
            return false;
        }
    }
    
    public boolean equals(ConditionalUnaryProcedure that) {
        return null != that && 
                (null == ifPred ? null == that.ifPred : ifPred.equals(that.ifPred)) &&
                (null == thenProc ? null == that.thenProc : thenProc.equals(that.thenProc)) &&
                (null == elseProc ? null == that.elseProc : elseProc.equals(that.elseProc));
    }
    
    public int hashCode() {
        int hash = "ConditionalUnaryProcedure".hashCode();
        if(null != ifPred) {
            hash <<= 4;
            hash ^= ifPred.hashCode();            
        }
        if(null != thenProc) {
            hash <<= 4;
            hash ^= thenProc.hashCode();            
        }
        if(null != elseProc) {
            hash <<= 4;
            hash ^= elseProc.hashCode();            
        }
        return hash;
    }
    
    public String toString() {
        return "ConditionalUnaryProcedure<" + ifPred + "?" + thenProc + ":" + elseProc + ">";
    }

    // attributes
    // ------------------------------------------------------------------------
    private UnaryPredicate ifPred = null;
    private UnaryProcedure thenProc = null;
    private UnaryProcedure elseProc = null;
}
