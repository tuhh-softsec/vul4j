/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//digester/src/java/org/apache/commons/digester/CallParamRule.java,v 1.5 2001/08/20 19:18:42 craigmcc Exp $
 * $Revision: 1.5 $
 * $Date: 2001/08/20 19:18:42 $
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


import java.lang.reflect.Method;
import org.xml.sax.Attributes;


/**
 * Rule implementation that saves a parameter from either an attribute of
 * this element, or from the element body, to be used in a call generated
 * by a surrounding CallMethodRule rule.
 *
 * @author Craig McClanahan
 * @version $Revision: 1.5 $ $Date: 2001/08/20 19:18:42 $
 */

public class CallParamRule extends Rule {


    // ----------------------------------------------------------- Constructors


    /**
     * Construct a "call parameter" rule that will save the body text of this
     * element as the parameter value.
     *
     * @param digester The associated Digester
     * @param paramIndex The zero-relative parameter number
     */
    public CallParamRule(Digester digester, int paramIndex) {

	this(digester, paramIndex, null);

    }


    /**
     * Construct a "call parameter" rule that will save the value of the
     * specified attribute as the parameter value.
     *
     * @param digester The associated Digester
     * @param paramIndex The zero-relative parameter number
     * @param attributeName The name of the attribute to save
     */
    public CallParamRule(Digester digester, int paramIndex,
    			 String attributeName) {

	super(digester);
	this.paramIndex = paramIndex;
	this.attributeName = attributeName;

    }


    // ----------------------------------------------------- Instance Variables


    /**
     * The attribute from which to save the parameter value
     */
    protected String attributeName = null;


    /**
     * The body text collected from this element.
     */
    protected String bodyText = null;


    /**
     * The zero-relative index of the parameter we are saving.
     */
    protected int paramIndex = 0;


    // --------------------------------------------------------- Public Methods


    /**
     * Process the start of this element.
     *
     * @param attributes The attribute list for this element
     */
    public void begin(Attributes attributes) throws Exception {

	if (attributeName != null)
	    bodyText = attributes.getValue(attributeName);

    }


    /**
     * Process the body text of this element.
     *
     * @param bodyText The body text of this element
     */
    public void body(String bodyText) throws Exception {

	if (attributeName == null)
	    this.bodyText = bodyText.trim();

    }


    /**
     * Process the end of this element.
     */
    public void end() throws Exception {

	String parameters[] = (String[]) digester.peekParams();
	parameters[paramIndex] = bodyText;

    }


    /**
     * Clean up after parsing is complete.
     */
    public void finish() throws Exception {

	bodyText = null;

    }


    /**
     * Render a printable version of this Rule.
     */
    public String toString() {

        StringBuffer sb = new StringBuffer("CallParamRule[");
        sb.append("paramIndex=");
        sb.append(paramIndex);
        sb.append(", attributeName=");
        sb.append(attributeName);
        sb.append("]");
        return (sb.toString());

    }


}
