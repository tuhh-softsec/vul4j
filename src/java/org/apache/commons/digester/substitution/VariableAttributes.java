/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//digester/src/java/org/apache/commons/digester/substitution/VariableAttributes.java,v 1.2 2004/01/10 17:43:46 rdonkin Exp $
 * $Revision: 1.2 $
 * $Date: 2004/01/10 17:43:46 $
 *
 * ====================================================================
 * 
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2004 The Apache Software Foundation.  All rights
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


package org.apache.commons.digester.substitution;

import org.xml.sax.Attributes;

import java.util.Map;
import java.util.ArrayList;


/**
 * <p>Wrapper for an org.xml.sax.Attributes object which expands any 
 * "variables" referenced in the attribute value via ${foo} or similar. 
 * This is only done something actually asks for the attribute value, 
 * thereby imposing no performance penalty if the attribute is not used.</p>
 *
 * @author Simon Kitching
 * @version $Revision: 1.2 $ $Date: 2004/01/10 17:43:46 $
 */

public class VariableAttributes implements Attributes {

    // list of mapped attributes.
    private ArrayList values = new ArrayList(10);

    private Attributes attrs;
    private VariableExpander expander;
    
    // ------------------- Public Methods
    
    /**
     * Specify which attributes class this object is a proxy for.
     */
    public void init(Attributes attrs, VariableExpander expander) {
        this.attrs = attrs;
        this.expander = expander;

        // I hope this doesn't release the memory for this array; for 
        // efficiency, this should just mark the array as being size 0.
        values.clear(); 
    }

    public String getValue(int index) {
        if (index >= values.size()) {
            // Expand the values array with null elements, so the later
            // call to set(index, s) works ok.
            //
            // Unfortunately, there is no easy way to set the size of
            // an arraylist; we must repeatedly add null elements to it..
            values.ensureCapacity(index+1);
            for(int i = values.size(); i<= index; ++i) {
                values.add(null);
            }
        }
        
        String s = (String) values.get(index);
        
        if (s == null) {
            // we have never been asked for this value before.
            // get the real attribute value and perform substitution
            // on it.
            s = attrs.getValue(index);
            if (s != null) {
                s = expander.expand(s);
                values.set(index, s);
            }
        }
        
        return s;
    }
    
    public String getValue(String qname) {
        int index = attrs.getIndex(qname);
        if (index == -1) {
            return null;
        }
        return getValue(index);
    }
    
    public String getValue(String uri, String localname) {
        int index = attrs.getIndex(uri, localname);
        if (index == -1) {
            return null;
        }
        return getValue(index);
    }
    
    // plain proxy methods follow : nothing interesting :-)
    public int getIndex(String qname) {
        return attrs.getIndex(qname); 
    }
    
    public int getIndex(String uri, String localpart) {
        return attrs.getIndex(uri, localpart); 
    }
    
    public int getLength() {
        return attrs.getLength();
    }
    
    public String getLocalName(int index) {
        return attrs.getLocalName(index);
    }
    
    public String getQName(int index) {
        return attrs.getQName(index);
    }
    
    public String getType(int index) {
        return attrs.getType(index);
    }

    public String getType(String qname) {
        return attrs.getType(qname);
    }
    
    public String getType(String uri, String localname) {
        return attrs.getType(uri, localname);
    }
    
    public String getURI(int index) {
        return attrs.getURI(index);
    }
 
}
