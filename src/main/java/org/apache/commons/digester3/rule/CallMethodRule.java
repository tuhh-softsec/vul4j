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
package org.apache.commons.digester3.rule;

import java.util.Formatter;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.digester3.Rule;
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
 * of this.getDigester(). Note, however, that this configuration of CallMethodRule
 * instances is not commonly required.</p>
 */
public class CallMethodRule extends Rule {

    /**
     * The location of the target object for the call, relative to the
     * top of the digester object stack. The default value of zero
     * means the target object is the one on top of the stack.
     */
    private final int targetOffset;

    private final int paramCount;

    /**
     * The method name to call on the parent object.
     */
    private final String methodName;

    /**
     * The parameter types of the parameters to be collected.
     */
    private final Class<?> paramTypes[];

    /**
     * Should <code>MethodUtils.invokeExactMethod</code> be used for reflection.
     */
    private final boolean useExactMatch;

    /**
     * The body text collected from this element.
     */
    private String bodyText = null;

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
     * @param useExactMatch Should <code>MethodUtils.invokeExactMethod</code>
     *  be used for the reflection.
     */
    public CallMethodRule(int targetOffset, String methodName, int paramCount, Class<?>[] paramTypes, boolean useExactMatch) {
        this.targetOffset = targetOffset;
        this.methodName = methodName;
        this.paramCount = paramCount;

        // copy the parameter class into an array
        this.paramTypes = new Class[paramTypes.length];
        System.arraycopy(paramTypes, 0, this.paramTypes, 0, paramTypes.length);

        this.useExactMatch = useExactMatch;
    }

    /**
     * Process the start of this element.
     *
     * @param attributes The attribute list for this element
     */
    @Override
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        // Push an array to capture the parameter values if necessary
        if (this.paramCount > 0) {
            Object parameters[] = new Object[this.paramCount];
            for (int i = 0; i < parameters.length; i++) {
                parameters[i] = null;
            }
            this.getDigester().pushParams(parameters);
        }
    }

    /**
     * Process the body text of this element.
     *
     * @param bodyText The body text of this element
     */
    @Override
    public void body(String namespace, String name, String text) throws Exception {
        if (this.paramCount == 0) {
            this.bodyText = text.trim();
        }
    }

    /**
     * Process the end of this element.
     */
    @Override
    public void end(String namespace, String name) throws Exception {
        // Retrieve or construct the parameter values array
        Object parameters[] = null;
        if (this.paramCount > 0) {

            parameters = (Object[]) this.getDigester().popParams();

            if (this.getDigester().getLog().isTraceEnabled()) {
                Formatter formatter = new Formatter("[CallMethodRule]");

                for (int i = 0, size = parameters.length; i < size; i++) {
                    if (i > 0) {
                        formatter.format(", ");
                    }
                    formatter.format("(%s) %s", i, parameters[i]);
                }

                this.getDigester().getLog().trace(formatter.toString());
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
            if (this.paramCount == 1 && parameters[0] == null) {
                return;
            }

        } else if (this.paramTypes != null && this.paramTypes.length != 0) {
            // Having paramCount == 0 and paramTypes.length == 1 indicates
            // that we have the special case where the target method has one
            // parameter being the body text of the current element.

            // There is no body text included in the source XML file,
            // so skip the method call
            if (this.bodyText == null) {
                return;
            }

            parameters = new Object[]{ this.bodyText };
        } else {
            // When paramCount is zero and paramTypes.length is zero it
            // means that we truly are calling a method with no parameters.
            // Nothing special needs to be done here.
        }

        // Construct the parameter values array we will need
        // We only do the conversion if the param value is a String and
        // the specified paramType is not String. 
        Object paramValues[] = new Object[this.paramTypes.length];
        for (int i = 0; i < this.paramTypes.length; i++) {
            // convert nulls and convert stringy parameters 
            // for non-stringy param types
            if (parameters[i] == null
                    || (parameters[i] instanceof String && !String.class.isAssignableFrom(this.paramTypes[i]))) {

                paramValues[i] = ConvertUtils.convert((String) parameters[i], paramTypes[i]);
            } else {
                paramValues[i] = parameters[i];
            }
        }

        // Determine the target object for the method call
        Object target;
        if (this.targetOffset >= 0) {
            target = this.getDigester().peek(this.targetOffset);
        } else {
            target = this.getDigester().peek(this.getDigester().getCount() + this.targetOffset);
        }

        if (target == null) {
            throw this.getDigester().createSAXException(
                    String.format("[CallMethodRule]{%s} Call target is null (targetOffset=%s, stackdepth=%s)",
                    this.getDigester().getMatch(),
                    this.targetOffset,
                    this.getDigester().getCount()));
        }

        // Invoke the required method on the top object
        if (this.getDigester().getLog().isDebugEnabled()) {
            Formatter formatter = new Formatter()
                                    .format("[CallMethodRule]{%s} Call %s.%s(",
                                            this.getDigester().getMatch(),
                                            target.getClass().getName(),
                                            this.methodName);

            for (int i = 0; i < paramValues.length; i++) {
                if (i > 0) {
                    formatter.format(", ");
                }

                formatter.format("%s/%s",
                        (paramValues[i] == null ? "null" : paramValues[i].toString()),
                        (this.paramTypes[i] == null ? "null" : this.paramTypes[i].getName()));
            }

            formatter.format(")");
            this.getDigester().getLog().debug(formatter.toString());
        }

        Object result = null;
        if (this.useExactMatch) {
            // invoke using exact match
            result = MethodUtils.invokeExactMethod(target, this.methodName, paramValues, this.paramTypes);
        } else {
            // invoke using fuzzier match
            result = MethodUtils.invokeMethod(target, this.methodName, paramValues, this.paramTypes);
        }

        processMethodCallResult(result);
    }

    /**
     * Clean up after parsing is complete.
     */
    @Override
    public void finish() throws Exception {
        this.bodyText = null;
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
    @Override
    public String toString() {
        Formatter formatter = new Formatter().format("CallMethodRule[methodName=%s, paramCount=%s, paramTypes={",
                this.methodName,
                this.paramTypes.length);

        if (this.paramTypes != null) {
            for (int i = 0; i < this.paramTypes.length; i++) {
                if (i > 0) {
                    formatter.format(", ");
                }
                formatter.format(this.paramTypes[i].getName());
            }
        }

        return formatter.format("}]").toString();
    }

}
