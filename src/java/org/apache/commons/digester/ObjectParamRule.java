/*
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights
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

import org.xml.sax.Attributes;

/**
 * <p>Rule implementation that saves a parameter for use by a surrounding
 * <code>CallMethodRule<code>.</p>
 *
 * <p>This parameter may be:
 * <ul>
 * <li>an arbitrary Object defined programatically, assigned when the element pattern associated with the Rule is matched
 * See {@link #ObjectParamRule(int paramIndex, Object param)}
 * <li>an arbitrary Object defined programatically, assigned if the element pattern AND specified attribute name are matched
 * See {@link #ObjectParamRule(int paramIndex, String attributeName, Object param)}
 * </ul>
 * </p>
 *
 * @author Mark Huisman
 */

public class ObjectParamRule extends Rule {
    // ----------------------------------------------------------- Constructors
    /**
     * Construct a "call parameter" rule that will save the given Object as
     * the parameter value.
     *
     * @param paramIndex The zero-relative parameter number
     * @param param the parameter to pass along
     */
    public ObjectParamRule(int paramIndex, Object param) {
        this(paramIndex, null, param);
    }


    /**
     * Construct a "call parameter" rule that will save the given Object as
     * the parameter value, provided that the specified attribute exists.
     *
     * @param paramIndex The zero-relative parameter number
     * @param attributeName The name of the attribute to match
     * @param param the parameter to pass along
     */
    public ObjectParamRule(int paramIndex, String attributeName, Object param) {
        this.paramIndex = paramIndex;
        this.attributeName = attributeName;
        this.param = param;
    }


    // ----------------------------------------------------- Instance Variables

    /**
     * The attribute which we are attempting to match
     */
    protected String attributeName = null;

    /**
     * The zero-relative index of the parameter we are saving.
     */
    protected int paramIndex = 0;

    /**
     * The parameter we wish to pass to the method call
     */
    protected Object param = null;


    // --------------------------------------------------------- Public Methods

    /**
     * Process the start of this element.
     *
     * @param attributes The attribute list for this element
     */
    public void begin(String namespace, String name,
                      Attributes attributes) throws Exception {
        Object anAttribute = null;
        Object parameters[] = (Object[]) digester.peekParams();

        if (attributeName != null) {
            anAttribute = attributes.getValue(attributeName);
            if(anAttribute != null) {
                parameters[paramIndex] = param;
            }
            // note -- if attributeName != null and anAttribute == null, this rule
            // will pass null as its parameter!
        }else{
            parameters[paramIndex] = param;
        }
    }

    /**
     * Render a printable version of this Rule.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer("ObjectParamRule[");
        sb.append("paramIndex=");
        sb.append(paramIndex);
        sb.append(", attributeName=");
        sb.append(attributeName);
        sb.append(", param=");
        sb.append(param);
        sb.append("]");
        return (sb.toString());
    }
}
