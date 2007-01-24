/* $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 


package org.apache.commons.digester;


import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.MethodUtils;
import org.xml.sax.Attributes;


/**
 * <p>Rule implementation that calls a method on an object on the stack
 * (normally the top/parent object), passing arguments collected from 
 * subsequent <code>CallParamRule</code> rules or from the body of this
 * element. </p>
 *
 * <p>By using {@link #CallMethodRule(String methodName)} 
 * a method call can be made to a method which accepts no
 * arguments.</p>
 *
 * <p>Incompatible method parameter types are converted 
 * using <code>org.apache.commons.beanutils.ConvertUtils</code>.
 * </p>
 *
 * <p>This rule now uses {@link MethodUtils#invokeMethod} by default.
 * This increases the kinds of methods successfully and allows primitives
 * to be matched by passing in wrapper classes.
 * There are rare cases when {@link MethodUtils#invokeExactMethod} 
 * (the old default) is required.
 * This method is much stricter in it's reflection.
 * Setting the <code>UseExactMatch</code> to true reverts to the use of this 
 * method.</p>
 *
 * <p>Note that the target method is invoked when the  <i>end</i> of
 * the tag the CallMethodRule fired on is encountered, <i>not</i> when the
 * last parameter becomes available. This implies that rules which fire on
 * tags nested within the one associated with the CallMethodRule will 
 * fire before the CallMethodRule invokes the target method. This behaviour is
 * not configurable. </p>
 *
 * <p>Note also that if a CallMethodRule is expecting exactly one parameter
 * and that parameter is not available (eg CallParamRule is used with an
 * attribute name but the attribute does not exist) then the method will
 * not be invoked. If a CallMethodRule is expecting more than one parameter,
 * then it is always invoked, regardless of whether the parameters were
 * available or not; missing parameters are converted to the appropriate target
 * type by calling ConvertUtils.convert. Note that the default ConvertUtils
 * converters for the String type returns a null when passed a null, meaning
 * that CallMethodRule will passed null for all String parameters for which
 * there is no parameter info available from the XML. However parameters of
 * type Float and Integer will be passed a real object containing a zero value
 * as that is the output of the default ConvertUtils converters for those
 * types when passed a null. You can register custom converters to change
 * this behaviour; see the beautils library documentation for more info.</p>
 *
 * <p>Note that when a constructor is used with paramCount=0, indicating that
 * the body of the element is to be passed to the target method, an empty 
 * element will cause an <i>empty string</i> to be passed to the target method,
 * not null. And if automatic type conversion is being applied (ie if the 
 * target function takes something other than a string as a parameter) then 
 * the conversion will fail if the converter class does not accept an empty 
 * string as valid input.</p>
 * 
 * <p>CallMethodRule has a design flaw which can cause it to fail under
 * certain rule configurations. All CallMethodRule instances share a single
 * parameter stack, and all CallParamRule instances simply store their data
 * into the parameter-info structure that is on the top of the stack. This
 * means that two CallMethodRule instances cannot be associated with the
 * same pattern without getting scrambled parameter data. This same issue
 * also applies when a CallMethodRule matches some element X, a different 
 * CallMethodRule matches a child element Y and some of the CallParamRules 
 * associated with the first CallMethodRule match element Y or one of its 
 * child elements. This issue has been present since the very first release
 * of Digester. Note, however, that this configuration of CallMethodRule
 * instances is not commonly required.</p>
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
     *
     *
     * @deprecated The digester instance is now set in the {@link Digester#addRule} method. 
     * Use {@link #CallMethodRule(String methodName,int paramCount)} instead.
     */
    public CallMethodRule(Digester digester, String methodName,
                          int paramCount) {

        this(methodName, paramCount);

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
     *
     * @deprecated The digester instance is now set in the {@link Digester#addRule} method. 
     * Use {@link #CallMethodRule(String methodName,int paramCount, String [] paramTypes)} instead.
     */
    public CallMethodRule(Digester digester, String methodName,
                          int paramCount, String paramTypes[]) {

        this(methodName, paramCount, paramTypes);

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
     *
     * @deprecated The digester instance is now set in the {@link Digester#addRule} method. 
     * Use {@link #CallMethodRule(String methodName,int paramCount, Class [] paramTypes)} instead.
     */
    public CallMethodRule(Digester digester, String methodName,
                          int paramCount, Class paramTypes[]) {

        this(methodName, paramCount, paramTypes);
    }


    /**
     * Construct a "call method" rule with the specified method name.  The
     * parameter types (if any) default to java.lang.String.
     *
     * @param methodName Method name of the parent method to call
     * @param paramCount The number of parameters to collect, or
     *  zero for a single argument from the body of this element.
     */
    public CallMethodRule(String methodName,
                          int paramCount) {
        this(0, methodName, paramCount);
    }

    /**
     * Construct a "call method" rule with the specified method name.  The
     * parameter types (if any) default to java.lang.String.
     *
     * @param targetOffset location of the target object. Positive numbers are
     * relative to the top of the digester object stack. Negative numbers 
     * are relative to the bottom of the stack. Zero implies the top
     * object on the stack.
     * @param methodName Method name of the parent method to call
     * @param paramCount The number of parameters to collect, or
     *  zero for a single argument from the body of this element.
     */
    public CallMethodRule(int targetOffset,
                          String methodName,
                          int paramCount) {

        this.targetOffset = targetOffset;
        this.methodName = methodName;
        this.paramCount = paramCount;        
        if (paramCount == 0) {
            this.paramTypes = new Class[] { String.class };
        } else {
            this.paramTypes = new Class[paramCount];
            for (int i = 0; i < this.paramTypes.length; i++) {
                this.paramTypes[i] = String.class;
            }
        }

    }

    /**
     * Construct a "call method" rule with the specified method name.  
     * The method should accept no parameters.
     *
     * @param methodName Method name of the parent method to call
     */
    public CallMethodRule(String methodName) {
    
        this(0, methodName, 0, (Class[]) null);
    
    }
    

    /**
     * Construct a "call method" rule with the specified method name.  
     * The method should accept no parameters.
     *
     * @param targetOffset location of the target object. Positive numbers are
     * relative to the top of the digester object stack. Negative numbers 
     * are relative to the bottom of the stack. Zero implies the top
     * object on the stack.
     * @param methodName Method name of the parent method to call
     */
    public CallMethodRule(int targetOffset, String methodName) {
    
        this(targetOffset, methodName, 0, (Class[]) null);
    
    }
    

    /**
     * Construct a "call method" rule with the specified method name and
     * parameter types. If <code>paramCount</code> is set to zero the rule
     * will use the body of this element as the single argument of the
     * method, unless <code>paramTypes</code> is null or empty, in this
     * case the rule will call the specified method with no arguments.
     *
     * @param methodName Method name of the parent method to call
     * @param paramCount The number of parameters to collect, or
     *  zero for a single argument from the body of ths element
     * @param paramTypes The Java class names of the arguments
     *  (if you wish to use a primitive type, specify the corresonding
     *  Java wrapper class instead, such as <code>java.lang.Boolean</code>
     *  for a <code>boolean</code> parameter)
     */
    public CallMethodRule(
                            String methodName,
                            int paramCount, 
                            String paramTypes[]) {
        this(0, methodName, paramCount, paramTypes);
    }

    /**
     * Construct a "call method" rule with the specified method name and
     * parameter types. If <code>paramCount</code> is set to zero the rule
     * will use the body of this element as the single argument of the
     * method, unless <code>paramTypes</code> is null or empty, in this
     * case the rule will call the specified method with no arguments.
     *
     * @param targetOffset location of the target object. Positive numbers are
     * relative to the top of the digester object stack. Negative numbers 
     * are relative to the bottom of the stack. Zero implies the top
     * object on the stack.
     * @param methodName Method name of the parent method to call
     * @param paramCount The number of parameters to collect, or
     *  zero for a single argument from the body of ths element
     * @param paramTypes The Java class names of the arguments
     *  (if you wish to use a primitive type, specify the corresonding
     *  Java wrapper class instead, such as <code>java.lang.Boolean</code>
     *  for a <code>boolean</code> parameter)
     */
    public CallMethodRule(  int targetOffset,
                            String methodName,
                            int paramCount, 
                            String paramTypes[]) {

        this.targetOffset = targetOffset;
        this.methodName = methodName;
        this.paramCount = paramCount;
        if (paramTypes == null) {
            this.paramTypes = new Class[paramCount];
            for (int i = 0; i < this.paramTypes.length; i++) {
                this.paramTypes[i] = String.class;
            }
        } else {
            // copy the parameter class names into an array
            // the classes will be loaded when the digester is set 
            this.paramClassNames = new String[paramTypes.length];
            for (int i = 0; i < this.paramClassNames.length; i++) {
                this.paramClassNames[i] = paramTypes[i];
            }
        }

    }


    /**
     * Construct a "call method" rule with the specified method name and
     * parameter types. If <code>paramCount</code> is set to zero the rule
     * will use the body of this element as the single argument of the
     * method, unless <code>paramTypes</code> is null or empty, in this
     * case the rule will call the specified method with no arguments.
     *
     * @param methodName Method name of the parent method to call
     * @param paramCount The number of parameters to collect, or
     *  zero for a single argument from the body of ths element
     * @param paramTypes The Java classes that represent the
     *  parameter types of the method arguments
     *  (if you wish to use a primitive type, specify the corresonding
     *  Java wrapper class instead, such as <code>java.lang.Boolean.TYPE</code>
     *  for a <code>boolean</code> parameter)
     */
    public CallMethodRule(
                            String methodName,
                            int paramCount, 
                            Class paramTypes[]) {
        this(0, methodName, paramCount, paramTypes);
    }

    /**
     * Construct a "call method" rule with the specified method name and
     * parameter types. If <code>paramCount</code> is set to zero the rule
     * will use the body of this element as the single argument of the
     * method, unless <code>paramTypes</code> is null or empty, in this
     * case the rule will call the specified method with no arguments.
     *
     * @param targetOffset location of the target object. Positive numbers are
     * relative to the top of the digester object stack. Negative numbers 
     * are relative to the bottom of the stack. Zero implies the top
     * object on the stack.
     * @param methodName Method name of the parent method to call
     * @param paramCount The number of parameters to collect, or
     *  zero for a single argument from the body of ths element
     * @param paramTypes The Java classes that represent the
     *  parameter types of the method arguments
     *  (if you wish to use a primitive type, specify the corresonding
     *  Java wrapper class instead, such as <code>java.lang.Boolean.TYPE</code>
     *  for a <code>boolean</code> parameter)
     */
    public CallMethodRule(  int targetOffset,
                            String methodName,
                            int paramCount, 
                            Class paramTypes[]) {

        this.targetOffset = targetOffset;
        this.methodName = methodName;
        this.paramCount = paramCount;
        if (paramTypes == null) {
            this.paramTypes = new Class[paramCount];
            for (int i = 0; i < this.paramTypes.length; i++) {
                this.paramTypes[i] = String.class;
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
     * location of the target object for the call, relative to the
     * top of the digester object stack. The default value of zero
     * means the target object is the one on top of the stack.
     */
    protected int targetOffset = 0;

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

    /**
     * The names of the classes of the parameters to be collected.
     * This attribute allows creation of the classes to be postponed until the digester is set.
     */
    private String paramClassNames[] = null;
    
    /**
     * Should <code>MethodUtils.invokeExactMethod</code> be used for reflection.
     */
    protected boolean useExactMatch = false;
    
    // --------------------------------------------------------- Public Methods
    
    /**
     * Should <code>MethodUtils.invokeExactMethod</code>
     * be used for the reflection.
     */
    public boolean getUseExactMatch() {
        return useExactMatch;
    }
    
    /**
     * Set whether <code>MethodUtils.invokeExactMethod</code>
     * should be used for the reflection.
     */    
    public void setUseExactMatch(boolean useExactMatch)
    { 
        this.useExactMatch = useExactMatch;
    }

    /**
     * Set the associated digester.
     * If needed, this class loads the parameter classes from their names.
     */
    public void setDigester(Digester digester)
    {
        // call superclass
        super.setDigester(digester);
        // if necessary, load parameter classes
        if (this.paramClassNames != null) {
            this.paramTypes = new Class[paramClassNames.length];
            for (int i = 0; i < this.paramClassNames.length; i++) {
                try {
                    this.paramTypes[i] =
                            digester.getClassLoader().loadClass(this.paramClassNames[i]);
                } catch (ClassNotFoundException e) {
                    // use the digester log
                    digester.getLogger().error("(CallMethodRule) Cannot load class " + this.paramClassNames[i], e);
                    this.paramTypes[i] = null; // Will cause NPE later
                }
            }
        }
    }

    /**
     * Process the start of this element.
     *
     * @param attributes The attribute list for this element
     */
    public void begin(Attributes attributes) throws Exception {

        // Push an array to capture the parameter values if necessary
        if (paramCount > 0) {
            Object parameters[] = new Object[paramCount];
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
        Object parameters[] = null;
        if (paramCount > 0) {

            parameters = (Object[]) digester.popParams();
            
            if (digester.log.isTraceEnabled()) {
                for (int i=0,size=parameters.length;i<size;i++) {
                    digester.log.trace("[CallMethodRule](" + i + ")" + parameters[i]) ;
                }
            }
            
            // In the case where the target method takes a single parameter
            // and that parameter does not exist (the CallParamRule never
            // executed or the CallParamRule was intended to set the parameter
            // from an attribute but the attribute wasn't present etc) then
            // skip the method call.
            //
            // This is useful when a class has a "default" value that should
            // only be overridden if data is present in the XML. I don't
            // know why this should only apply to methods taking *one*
            // parameter, but it always has been so we can't change it now.
            if (paramCount == 1 && parameters[0] == null) {
                return;
            }

        } else if (paramTypes != null && paramTypes.length != 0) {
            // Having paramCount == 0 and paramTypes.length == 1 indicates
            // that we have the special case where the target method has one
            // parameter being the body text of the current element.

            // There is no body text included in the source XML file,
            // so skip the method call
            if (bodyText == null) {
                return;
            }

            parameters = new Object[1];
            parameters[0] = bodyText;
            if (paramTypes.length == 0) {
                paramTypes = new Class[1];
                paramTypes[0] = String.class;
            }

        } else {
            // When paramCount is zero and paramTypes.length is zero it
            // means that we truly are calling a method with no parameters.
            // Nothing special needs to be done here.
            ;
        }

        // Construct the parameter values array we will need
        // We only do the conversion if the param value is a String and
        // the specified paramType is not String. 
        Object paramValues[] = new Object[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            // convert nulls and convert stringy parameters 
            // for non-stringy param types
            if(
                parameters[i] == null ||
                 (parameters[i] instanceof String && 
                   !String.class.isAssignableFrom(paramTypes[i]))) {
                
                paramValues[i] =
                        ConvertUtils.convert((String) parameters[i], paramTypes[i]);
            } else {
                paramValues[i] = parameters[i];
            }
        }

        // Determine the target object for the method call
        Object target;
        if (targetOffset >= 0) {
            target = digester.peek(targetOffset);
        } else {
            target = digester.peek( digester.getCount() + targetOffset );
        }
        
        if (target == null) {
            StringBuffer sb = new StringBuffer();
            sb.append("[CallMethodRule]{");
            sb.append(digester.match);
            sb.append("} Call target is null (");
            sb.append("targetOffset=");
            sb.append(targetOffset);
            sb.append(",stackdepth=");
            sb.append(digester.getCount());
            sb.append(")");
            throw new org.xml.sax.SAXException(sb.toString());
        }
        
        // Invoke the required method on the top object
        if (digester.log.isDebugEnabled()) {
            StringBuffer sb = new StringBuffer("[CallMethodRule]{");
            sb.append(digester.match);
            sb.append("} Call ");
            sb.append(target.getClass().getName());
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
        
        Object result = null;
        if (useExactMatch) {
            // invoke using exact match
            result = MethodUtils.invokeExactMethod(target, methodName,
                paramValues, paramTypes);
                
        } else {
            // invoke using fuzzier match
            result = MethodUtils.invokeMethod(target, methodName,
                paramValues, paramTypes);            
        }
        
        processMethodCallResult(result);
    }


    /**
     * Clean up after parsing is complete.
     */
    public void finish() throws Exception {

        bodyText = null;

    }

    /**
     * Subclasses may override this method to perform additional processing of the 
     * invoked method's result.
     *
     * @param result the Object returned by the method invoked, possibly null
     */
    protected void processMethodCallResult(Object result) {
        // do nothing
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
