/* 
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons-sandbox//functor/src/java/org/apache/commons/functor/core/collection/Size.java,v 1.3 2003/11/24 21:29:28 rwaldhoff Exp $
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
package org.apache.commons.functor.core.collection;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collection;

import org.apache.commons.functor.UnaryFunction;

/**
 * Returns the size of the specified Collection, or the length
 * of the specified array or String.
 * @version $Revision: 1.3 $ $Date: 2003/11/24 21:29:28 $
 * @author Rodney Waldhoff
 */
public final class Size implements UnaryFunction, Serializable {

    // constructor
    // ------------------------------------------------------------------------
    
    public Size() { }
    
    public Object evaluate(Object obj) {
        if(obj instanceof Collection) {
            return evaluate((Collection)obj);
        } else if(obj instanceof String) {
            return evaluate((String)obj);
        } else if(null != obj && obj.getClass().isArray()) {
            return evaluateArray(obj);
        } else if(null == obj){
            throw new NullPointerException("Argument must not be null");
        } else {
            throw new ClassCastException("Expected Collection, String or Array, found " + obj);
        } 
    }

    /**
     * @see java.lang.Object#equals(Object)
     */
    public boolean equals(Object that) {
        return that instanceof Size;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return "Size".hashCode();
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Size()";
    }

    public static final Size instance() {
        return INSTANCE;
    }

    private Object evaluate(Collection col) {
        return new Integer(col.size());
    }
    
    private Object evaluate(String str) {
        return new Integer(str.length());
    }
    
    private Object evaluateArray(Object array) {
        return new Integer(Array.getLength(array));
    }
    
    private static final Size INSTANCE = new Size();
    
}
