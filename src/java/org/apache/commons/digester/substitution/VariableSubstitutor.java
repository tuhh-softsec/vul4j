/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//digester/src/java/org/apache/commons/digester/substitution/VariableSubstitutor.java,v 1.2 2003/12/03 23:21:52 rdonkin Exp $
 * $Revision: 1.2 $
 * $Date: 2003/12/03 23:21:52 $
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
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache" nor may "Apache" appear in their names without prior 
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

package org.apache.commons.digester.substitution;

import org.apache.commons.digester.Substitutor;

import org.xml.sax.Attributes;

/**
 * Substitutor implementation that support variable replacement
 * for both attributes and body text.
 * The actual expansion of variables into text is delegated to {@link VariableExpander}
 * implementations.
 * Supports setting an expander just for body text or just for attributes.
 * Also supported is setting no expanders for body text and for attributes. 
 *
 * @author Robert Burrell Donkin
 * @version $Revision: 1.2 $ $Date: 2003/12/03 23:21:52 $
 */
public class VariableSubstitutor extends Substitutor {

    /** 
     * The expander to be used to expand variables in the attributes.
     * Null when no expansion should be performed.
     */
    private VariableExpander attributesExpander;
    
    /** 
     * Attributes implementation that (lazily) performs variable substitution.
     * Will be lazily created when needed then reused.
     */
    private VariableAttributes variableAttributes;
    
    /** 
     * The expander to be used to expand variables in the body text.
     * Null when no expansion should be performed.
     */
    private VariableExpander bodyTextExpander;
    
    /**
     * Constructs a Substitutor which uses the same VariableExpander for both
     * body text and attibutes.
     * @param expander VariableExpander implementation, 
     * null if no substitutions are to be performed
     */
    public VariableSubstitutor(VariableExpander expander) {
        this(expander, expander);
    }
    
    /**
     * Constructs a Substitutor.
     * @param attributesExpander VariableExpander implementation to be used for attributes, 
     * null if no attribute substitutions are to be performed
     * @param bodyTextExpander VariableExpander implementation to be used for bodyTextExpander, 
     * null if no attribute substitutions are to be performed     
     */
    public VariableSubstitutor(VariableExpander attributesExpander, VariableExpander bodyTextExpander) {
        this.attributesExpander = attributesExpander;
        this.bodyTextExpander = bodyTextExpander;
        variableAttributes = new VariableAttributes();
    }    

    /**
     * Substitutes the attributes (before they are passed to the 
     * <code>Rule</code> implementations's)
     */
    public Attributes substitute(Attributes attributes) {
        Attributes results = attributes;
        if (attributesExpander != null) {
            variableAttributes.init(attributes, attributesExpander);
            results = variableAttributes;
        }
        return results;
    }
    
    /**
     * Substitutes for the body text.
     * This method may substitute values into the body text of the
     * elements that Digester parses.
     *
     * @param the body text (as passed to <code>Digester</code>)
     * @return the body text to be passed to the <code>Rule</code> implementations
     */
    public String substitute(String bodyText) {
        String result = bodyText;
        if (bodyTextExpander != null) {
            result = bodyTextExpander.expand(bodyText);
        }
        return result;
    }
}
