/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//digester/src/java/org/apache/commons/digester/SetPropertiesRule.java,v 1.8 2002/03/23 17:45:58 rdonkin Exp $
 * $Revision: 1.8 $
 * $Date: 2002/03/23 17:45:58 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
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
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
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


import java.util.HashMap;

import org.xml.sax.Attributes;
import org.apache.commons.beanutils.BeanUtils;


/**
 * Rule implementation that sets properties on the object at the top of the
 * stack, based on attributes with corresponding names.
 *
 * @author Craig McClanahan
 * @version $Revision: 1.8 $ $Date: 2002/03/23 17:45:58 $
 */

public class SetPropertiesRule extends Rule {


    // ----------------------------------------------------------- Constructors


    /**
     * Default constructor sets only the the associated Digester.
     *
     * @param digester The digester with which this rule is associated
     *
     * @deprecated The digester instance is now set in the {@link Digester#addRule} method. 
     * Use {@link #SetPropertiesRule()} instead.
     */
    public SetPropertiesRule(Digester digester) {

        this();

    }
    

    /**
     * Base constructor.
     */
    public SetPropertiesRule() {

        // nothing to set up 

    }


    // --------------------------------------------------------- Public Methods


    /**
     * Process the beginning of this element.
     *
     * @param context The associated context
     * @param attributes The attribute list of this element
     */
    public void begin(Attributes attributes) throws Exception {

        // Build a set of attribute names and corresponding values
        HashMap values = new HashMap();
        for (int i = 0; i < attributes.getLength(); i++) {
            String name = attributes.getLocalName(i);
            if ("".equals(name)) {
                name = attributes.getQName(i);
            }
            String value = attributes.getValue(i);
            if (digester.log.isDebugEnabled()) {
                digester.log.debug("[SetPropertiesRule]{" + digester.match +
                        "} Setting property '" + name + "' to '" +
                        value + "'");
            }
            values.put(name, value);
        }

        // Populate the corresponding properties of the top object
        Object top = digester.peek();
        if (digester.log.isDebugEnabled()) {
            digester.log.debug("[SetPropertiesRule]{" + digester.match +
                    "} Set " + top.getClass().getName() + " properties");
        }
        BeanUtils.populate(top, values);


    }


    /**
     * Render a printable version of this Rule.
     */
    public String toString() {

        StringBuffer sb = new StringBuffer("SetPropertiesRule[");
        sb.append("]");
        return (sb.toString());

    }


}
