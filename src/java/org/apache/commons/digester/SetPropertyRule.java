/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//digester/src/java/org/apache/commons/digester/SetPropertyRule.java,v 1.4 2001/08/20 18:28:40 craigmcc Exp $
 * $Revision: 1.4 $
 * $Date: 2001/08/20 18:28:40 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
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
 * Rule implementation that sets an individual property on the object at the
 * top of the stack, based on attributes with specified names.
 *
 * @author Craig McClanahan
 * @version $Revision: 1.4 $ $Date: 2001/08/20 18:28:40 $
 */

public class SetPropertyRule extends Rule {


    // ----------------------------------------------------------- Constructors


    /**
     * Construct a "set property" rule with the specified name and value
     * attributes.
     *
     * @param digester The digester with which this rule is associated
     * @param name Name of the attribute that will contain the name of the
     *  property to be set
     * @param value Name of the attribute that will contain the value to which
     *  the property should be set
     */
    public SetPropertyRule(Digester digester, String name, String value) {

	super(digester);
	this.name = name;
	this.value = value;

    }


    // ----------------------------------------------------- Instance Variables


    /**
     * The attribute that will contain the property name.
     */
    protected String name = null;


    /**
     * The attribute that will contain the property value.
     */
    protected String value = null;


    // --------------------------------------------------------- Public Methods


    /**
     * Process the beginning of this element.
     *
     * @param context The associated context
     * @param attributes The attribute list of this element
     */
    public void begin(Attributes attributes) throws Exception {

	// Identify the actual property name and value to be used
	String actualName = null;
	String actualValue = null;
	HashMap values = new HashMap();
	for (int i = 0; i < attributes.getLength(); i++) {
	    String name = attributes.getLocalName(i);
            if ("".equals(name))
                name = attributes.getQName(i);
	    String value = attributes.getValue(i);
	    if (name.equals(this.name))
		actualName = value;
	    else if (name.equals(this.value))
		actualValue = value;
	}
	values.put(actualName, actualValue);

	// Populate the corresponding property of the top object
	Object top = digester.peek();
	if (digester.getDebug() >= 1)
	    digester.log("Set " + top.getClass().getName() + " property " +
			 actualName + " to " + actualValue);
	BeanUtils.populate(top, values);

    }


    /**
     * Render a printable version of this Rule.
     */
    public String toString() {

        StringBuffer sb = new StringBuffer("SetPropertyRule[");
        sb.append("name=");
        sb.append(name);
        sb.append(", value=");
        sb.append(value);
        sb.append("]");
        return (sb.toString());

    }


}
