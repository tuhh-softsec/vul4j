/* 
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons-sandbox//functor/src/java/org/apache/commons/functor/core/Constant.java,v 1.2 2003/12/03 01:04:12 rwaldhoff Exp $
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
package org.apache.commons.functor.core;

import java.io.Serializable;

import org.apache.commons.functor.BinaryFunction;
import org.apache.commons.functor.BinaryPredicate;
import org.apache.commons.functor.Function;
import org.apache.commons.functor.Predicate;
import org.apache.commons.functor.UnaryFunction;
import org.apache.commons.functor.UnaryPredicate;

/**
 * {@link #evaluate Evaluates} to constant value.
 * <p>
 * {@link #test Tests} to a constant value, assuming
 * a boolean of Boolean value is supplied.
 *
 * Note that although this class implements 
 * {@link Serializable}, a given instance will
 * only be truly <code>Serializable</code> if the
 * constant <code>Object</code> is.  Attempts to serialize
 * an instance whose value is not 
 * <code>Serializable</code> will result in an exception.
 * </p>
 * @version $Revision: 1.2 $ $Date: 2003/12/03 01:04:12 $
 * @author Rodney Waldhoff
 */
public final class Constant implements Function, UnaryFunction, BinaryFunction, Predicate, UnaryPredicate, BinaryPredicate, Serializable {

    // constructor
    // ------------------------------------------------------------------------
    public Constant(boolean value) {
        this(new Boolean(value));
    }

    public Constant(Object value) {
        this.value = value;
    }
 
    // function interface
    // ------------------------------------------------------------------------
    public Object evaluate() {
        return value;
    }

    public Object evaluate(Object obj) {
        return evaluate();
    }

    public Object evaluate(Object left, Object right) {
        return evaluate();
    }

    public boolean test() {
        return ((Boolean)evaluate()).booleanValue();
    }

    public boolean test(Object obj) {
        return test();
    }

    public boolean test(Object left, Object right) {
        return test();
    }

    public boolean equals(Object that) {
        if(that instanceof Constant) {
            return equals((Constant)that);
        } else {
            return false;
        }
    }
    
    public boolean equals(Constant that) {
        return (null != that && (null == this.value ? null == that.value : this.value.equals(that.value)));
    }
    
    public int hashCode() {
        int hash = "Constant".hashCode();
        if(null != value) {
            hash ^= value.hashCode();
        }
        return hash;
    }
    
    public String toString() {
        return "Constant<" + String.valueOf(value) + ">";
    }
    
    // attributes
    // ------------------------------------------------------------------------
    private Object value;

    // static methods
    // ------------------------------------------------------------------------
    
    /** 
     * Get a <code>Constant</code> that always
     * returns <code>true</code>
     * @return a <code>Constant</code> that always
     *         returns <code>true</code>
     */
    public static Constant truePredicate() {
        return TRUE_PREDICATE;
    }

    /** 
     * Get a <code>Constant</code> that always
     * returns <code>false</code>
     * @return a <code>Constant</code> that always
     *         returns <code>false</code>
     */
    public static Constant falsePredicate() {
        return FALSE_PREDICATE;
    }
    
    /** 
     * Get a <code>Constant</code> that always
     * returns <i>value</i>
     * @param value the constant value
     * @return a <code>Constant</code> that always
     *         returns <i>value</i>
     */
    public static Constant predicate(boolean value) {
        return value ? TRUE_PREDICATE : FALSE_PREDICATE;
    }

    public static Constant instance(Object value) {
        return new Constant(value);
    }
    
    // static attributes
    // ------------------------------------------------------------------------
    private static final Constant TRUE_PREDICATE = new Constant(true);
    private static final Constant FALSE_PREDICATE = new Constant(false);
}
