/* 
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons-sandbox//functor/src/java/org/apache/commons/functor/core/Attic/InstanceOfPredicate.java,v 1.2 2003/01/28 12:00:28 rwaldhoff Exp $
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

import org.apache.commons.functor.UnaryPredicate;

/**
 * {@link #test Tests} 
 * <code>true</code> iff its argument 
 * {@link Class#isInstance is an instance} 
 * of some specified {@link Class Class}.
 * 
 * @version $Revision: 1.2 $ $Date: 2003/01/28 12:00:28 $
 * @author Rodney Waldhoff
 */
public final class InstanceOfPredicate implements UnaryPredicate, Serializable {

    // constructor
    // ------------------------------------------------------------------------
    public InstanceOfPredicate(Class klass) {
        this.klass = klass;
    }
 
    // predicate interface
    // ------------------------------------------------------------------------

    public boolean test(Object obj) {
        return klass.isInstance(obj);
    }

    public boolean equals(Object that) {
        if(that instanceof InstanceOfPredicate) {
            return equals((InstanceOfPredicate)that);
        } else {
            return false;
        }
    }
    
    public boolean equals(InstanceOfPredicate that) {
        return (null != that && (null == this.klass ? null == that.klass : this.klass.equals(that.klass)));
    }
    
    public int hashCode() {
        int hash = "InstanceOfPredicate".hashCode();
        if(null != klass) {
            hash ^= klass.hashCode();
        }
        return hash;
    }
    
    public String toString() {
        return "InstanceOfPredicate<" + klass + ">";
    }
    
    // attributes
    // ------------------------------------------------------------------------
    private Class klass;

}
