/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//digester/src/java/org/apache/commons/digester/CallMethodRule.java,v 1.14 2002/01/23 21:25:22 sanders Exp $
 * $Revision: 1.14 $
 * $Date: 2002/01/23 21:25:22 $
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


import java.lang.reflect.Method;
import java.lang.ClassLoader;

import org.xml.sax.Attributes;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.MethodUtils;


/**
 * Rule implementation that calls a method on the top (parent)
 * object, passing arguments collected from subsequent
 * <code>CallParamRule</code> rules or from the body of this
 * element.
 *
 * @author Craig McClanahan
 * @author Scott Sanders
 * @version $Revision: 1.14 $ $Date: 2002/01/23 21:25:22 $
 */

public class CallMethodRule extends Rule {


    // ----------------------------------------------------------- Constructors


    /**
     * Construct a "call method" rule with the specified method name.  The
     * parameter types (if any) default to java.lang.String.
     *
     * @param digester The associated Digester
     * @param methodName Method name of the parent method to call
     * @param paramCount The number of parameters to collect, or
     *  zero for a single argument from the body of this element.
     */
    public CallMethodRule(Digester digester, String methodName,
                          int paramCount) {

        this(digester, methodName, paramCount, (Class[]) null);

    }


    /**
     * Construct a "call method" rule with the specified method name.
     *
     * @param digester The associated Digester
     * @param methodName Method name of the parent method to call
     * @param paramCount The number of parameters to collect, or
     *  zero for a single argument from the body of ths element
     * @param paramTypes The Java class names of the arguments
     *  (if you wish to use a primitive type, specify the corresonding
     *  Java wrapper class instead, such as <code>java.lang.Boolean</code>
     *  for a <code>boolean</code> parameter)
     */
    public CallMethodRule(Digester digester, String methodName,
                          int paramCount, String paramTypes[]) {

        super(digester);
        this.methodName = methodName;
        this.paramCount = paramCount;
        if (paramTypes == null) {
            this.paramTypes = new Class[paramCount];
            for (int i = 0; i < this.paramTypes.length; i++) {
                this.paramTypes[i] = "abc".getClass();
            }
        } else {
            this.paramTypes = new Class[paramTypes.length];
            for (int i = 0; i < this.paramTypes.length; i++) {
                try {
                    this.paramTypes[i] =
                            digester.getClassLoader().loadClass(paramTypes[i]);
                } catch (ClassNotFoundException e) {
                    this.paramTypes[i] = null; // Will cause NPE later
                }
            }
        }

    }


    /**
     * Construct a "call method" rule with the specified method name.
     *
     * @param digester The associated Digester
     * @param methodName Method name of the parent method to call
     * @param paramCount The number of parameters to collect, or
     *  zero for a single argument from the body of ths element
     * @param paramTypes The Java classes that represent the
     *  parameter types of the method arguments
     *  (if you wish to use a primitive type, specify the corresonding
     *  Java wrapper class instead, such as <code>java.lang.Boolean.TYPE</code>
     *  for a <code>boolean</code> parameter)
     */
    public CallMethodRule(Digester digester, String methodName,
                          int paramCount, Class paramTypes[]) {

        super(digester);
        this.methodName = methodName;
        this.paramCount = paramCount;
        if (paramTypes == null) {
            this.paramTypes = new Class[paramCount];
            for (int i = 0; i < this.paramTypes.length; i++) {
                this.paramTypes[i] = "abc".getClass();
            }
        } else {
            this.paramTypes = new Class[paramTypes.length];
            for (int i = 0; i < this.paramTypes.length; i++) {
                this.paramTypes[i] = paramTypes[i];
            }
        }

    }


    // ----------------------------------------------------- Instance Variables


    /**
     * The body text collected from this element.
     */
    protected String bodyText = null;


    /**
     * The method name to call on the parent object.
     */
    protected String methodName = null;


    /**
     * The number of parameters to collect from <code>MethodParam</code> rules.
     * If this value is zero, a single parameter will be collected from the
     * body of this element.
     */
    protected int paramCount = 0;


    /**
     * The parameter types of the parameters to be collected.
     */
    protected Class paramTypes[] = null;


    // --------------------------------------------------------- Public Methods


    /**
     * Process the start of this element.
     *
     * @param attributes The attribute list for this element
     */
    public void begin(Attributes attributes) throws Exception {

        // Push an array to capture the parameter values if necessary
        if (paramCount > 0) {
            String parameters[] = new String[paramCount];
            for (int i = 0; i < parameters.length; i++) {
                parameters[i] = null;
            }
            digester.pushParams(parameters);
        }

    }


    /**
     * Process the body text of this element.
     *
     * @param bodyText The body text of this element
     */
    public void body(String bodyText) throws Exception {

        if (paramCount == 0) {
            this.bodyText = bodyText.trim();
        }

    }


    /**
     * Process the end of this element.
     */
    public void end() throws Exception {

        // Retrieve or construct the parameter values array
        String parameters[] = null;
        if (paramCount > 0) {

            parameters = (String[]) digester.popParams();

            // In the case where the parameter for the method
            // is taken from an attribute, and that attribute
            // isn't actually defined in the source XML file,
            // skip the method call
            if (paramCount == 1 && parameters[0] == null) {
                return;
            }

        } else {

            // In the case where the parameter for the method
            // is taken from the body text, but there is no
            // body text included in the source XML file,
            // skip the method call
            if (bodyText == null) {
                return;
            }

            parameters = new String[1];
            parameters[0] = bodyText;
            if (paramTypes.length == 0) {
                paramTypes = new Class[1];
                paramTypes[0] = "abc".getClass();
            }

        }

        // Construct the parameter values array we will need
        Object paramValues[] = new Object[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            paramValues[i] =
                    ConvertUtils.convert(parameters[i], paramTypes[i]);
        }

        // Invoke the required method on the top object
        Object top = digester.peek();
        if (digester.log.isDebugEnabled()) {
            StringBuffer sb = new StringBuffer("[CallMethodRule]{");
            sb.append(digester.match);
            sb.append("} Call ");
            if (top == null) {
                sb.append("[NULL TOP]");
            } else {
                sb.append(top.getClass().getName());
            }
            sb.append(".");
            sb.append(methodName);
            sb.append("(");
            for (int i = 0; i < paramValues.length; i++) {
                if (i > 0) {
                    sb.append(",");
                }
                if (paramValues[i] == null) {
                    sb.append("null");
                } else {
                    sb.append(paramValues[i].toString());
                }
                sb.append("/");
                if (paramTypes[i] == null) {
                    sb.append("null");
                } else {
                    sb.append(paramTypes[i].getName());
                }
            }
            sb.append(")");
            digester.log.debug(sb.toString());
        }
        MethodUtils.invokeExactMethod(top, methodName,
                paramValues, paramTypes);

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

        StringBuffer sb = new StringBuffer("CallMethodRule[");
        sb.append("methodName=");
        sb.append(methodName);
        sb.append(", paramCount=");
        sb.append(paramCount);
        sb.append(", paramTypes={");
        if (paramTypes != null) {
            for (int i = 0; i < paramTypes.length; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(paramTypes[i].getName());
            }
        }
        sb.append("}");
        sb.append("]");
        return (sb.toString());

    }


}
