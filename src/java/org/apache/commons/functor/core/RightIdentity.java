/* 
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons-sandbox//functor/src/java/org/apache/commons/functor/core/RightIdentity.java,v 1.1 2003/12/02 16:50:52 rwaldhoff Exp $
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

/**
 * {@link #evaluate Evaluates} to its second argument.
 * 
 * {@link #test Tests} to the <code>boolean</code>
 * value of the <code>Boolean</code>-valued second
 * argument. The {@link #test test} method 
 * throws an exception if the parameter isn't a 
 * non-<code>null</code> <code>Boolean</code>.
 * 
 * @version $Revision: 1.1 $ $Date: 2003/12/02 16:50:52 $
 * @author Rodney Waldhoff
 */
public final class RightIdentity implements BinaryPredicate, BinaryFunction, Serializable {
    
    // constructor
    // ------------------------------------------------------------------------
    public RightIdentity() {
    }
 
    // functor interface
    // ------------------------------------------------------------------------

    public Object evaluate(Object left, Object right) {
        return right;
    }

    public boolean test(Object left, Object right) {
        return test((Boolean)right);
    }

    private boolean test(Boolean bool) {
        return bool.booleanValue();
    }

    public boolean equals(Object that) {
        return (that instanceof RightIdentity);
    }
    
    public int hashCode() {
        return "RightIdentity".hashCode();
    }
    
    public String toString() {
        return "RightIdentity";
    }
    
    // static methods
    // ------------------------------------------------------------------------
    public static RightIdentity instance() {
        return INSTANCE;
    }
    
    // static attributes
    // ------------------------------------------------------------------------
    private static final RightIdentity INSTANCE = new RightIdentity();
}
