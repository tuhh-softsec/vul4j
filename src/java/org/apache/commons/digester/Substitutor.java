/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//digester/src/java/org/apache/commons/digester/Substitutor.java,v 1.2 2003/12/03 23:36:13 rdonkin Exp $
 * $Revision: 1.2 $
 * $Date: 2003/12/03 23:36:13 $
 *
 * ====================================================================
 * 
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
 *    if any, must include the following acknowledgement:  
 *       "This product includes software developed by the 
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "Apache", "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior 
 *    written permission of the Apache Software Foundation.
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

package org.apache.commons.digester;

import org.xml.sax.Attributes;

/**
 * <p>(Logical) Interface for substitution strategies.
 * (It happens to be implemented as a Java abstract class to allow
 * future additions to be made without breaking backwards compatibility.)
 * </p>
 * <p>
 * Usage: When {@link Digester#setSubstitutor} is set, <code>Digester</code>
 * calls the methods in this interface to create substitute values which will
 * be passed into the Rule implementations.
 * Of course, it is perfectly acceptable for implementations not to make 
 * substitutions and simply return the inputs.
 * </p>
 * <p>Different strategies are supported for attributes and body text.</p> 
 *
 * @author Robert Burrell Donkin
 * @version $Revision: 1.2 $ $Date: 2003/12/03 23:36:13 $
 */
public abstract class Substitutor {
    
    /**
     * <p>Substitutes the attributes (before they are passed to the 
     * <code>Rule</code> implementations's).</p>
     *
     * <p><code>Digester</code> will only call this method a second time 
     * once the original <code>Attributes</code> instance can be safely reused. 
     * The implementation is therefore free to reuse the same <code>Attributes</code> instance
     * for all calls.</p>
     *
     * @param attributes the <code>Attributes</code> passed into <code>Digester</code> by the SAX parser, 
     * not null (but may be empty)
     * @return <code>Attributes</code> to be passed to the <code>Rule</code> implementations. 
     * This method may pass back the Attributes passed in.
     * Not null but possibly empty.
     */
    public abstract Attributes substitute(Attributes attributes);
    
    /**
     * Substitutes for the body text.
     * This method may substitute values into the body text of the
     * elements that Digester parses.
     *
     * @param the body text (as passed to <code>Digester</code>)
     * @return the body text to be passed to the <code>Rule</code> implementations
     */
    public abstract String substitute(String bodyText);
}