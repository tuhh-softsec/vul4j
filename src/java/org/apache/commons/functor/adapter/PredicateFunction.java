/* 
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons-sandbox//functor/src/java/org/apache/commons/functor/adapter/PredicateFunction.java,v 1.2 2003/02/18 20:35:40 rwaldhoff Exp $
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

import org.apache.commons.functor.Function;
import org.apache.commons.functor.Predicate;

/**
 * Adapts a
 * {@link Predicate Predicate} 
 * to the 
 * {@link Function Function} interface.
 * <p/>
 * Note that although this class implements 
 * {@link Serializable}, a given instance will
 * only be truly <code>Serializable</code> if the
 * underlying predicate is.  Attempts to serialize
 * an instance whose delegate is not 
 * <code>Serializable</code> will result in an exception.
 * 
 * @version $Revision: 1.2 $ $Date: 2003/02/18 20:35:40 $
 * @author Rodney Waldhoff
 */
public final class PredicateFunction implements Function, Serializable {
    public PredicateFunction(Predicate predicate) {
        this.predicate = predicate;
    }
 
    /**
     * Returns <code>Boolean.TRUE</code> (<code>Boolean.FALSE</code>)
     * when the {@link Predicate#test test} method of my underlying 
     * predicate returns <code>true</code> (<code>false</code>).
     * 
     * @return a non-<code>null</code> <code>Boolean</code> instance
     */
    public Object evaluate() {
        return predicate.test() ? Boolean.TRUE : Boolean.FALSE;
    }   

    public boolean equals(Object that) {
        if(that instanceof PredicateFunction) {
            return equals((PredicateFunction)that);
        } else {
            return false;
        }
    }
        
    public boolean equals(PredicateFunction that) {
        return that == this || (null != that && (null == predicate ? null == that.predicate : predicate.equals(that.predicate)));
    }
    
    public int hashCode() {
        int hash = "PredicateFunction".hashCode();
        if(null != predicate) {
            hash ^= predicate.hashCode();
        }
        return hash;
    }
    
    public String toString() {
        return "PredicateFunction<" + predicate + ">";
    }

    public static PredicateFunction adapt(Predicate predicate) {
        return null == predicate ? null : new PredicateFunction(predicate);
    }

    /** The {@link Predicate Predicate} I'm wrapping. */
    private Predicate predicate = null;
}
