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
package org.apache.commons.digester3.internal.rulesbinder;

import java.util.Arrays;

import org.apache.commons.digester3.RulesBinder;
import org.apache.commons.digester3.rule.CallMethodRule;
import org.apache.commons.digester3.rulesbinder.CallMethodBuilder;

/**
 * Builder chained when invoking {@link LinkedRuleBuilderImpl#callMethod(String)}.
 */
final class CallMethodBuilderImpl
        extends AbstractBackToLinkedRuleBuilder<CallMethodRule>
        implements CallMethodBuilder {

    private final String methodName;

    private final ClassLoader classLoader;

    private int targetOffset;

    private int paramCount = 0;

    private Class<?>[] paramTypes;

    private boolean useExactMatch = false;

    public CallMethodBuilderImpl(String keyPattern,
            String namespaceURI,
            final RulesBinder mainBinder,
            LinkedRuleBuilderImpl mainBuilder,
            String methodName,
            ClassLoader classLoader) {
        super(keyPattern, namespaceURI, mainBinder, mainBuilder);
        this.methodName = methodName;
        this.classLoader = classLoader;
    }

    /**
     * Sets the location of the target object.
     *
     * Positive numbers are relative to the top of the digester object stack.
     * Negative numbers are relative to the bottom of the stack. Zero implies the top object on the stack.
     *
     * @param targetOffset location of the target object.
     * @return this builder instance
     */
    public CallMethodBuilderImpl withTargetOffset(int targetOffset) {
        this.targetOffset = targetOffset;
        return this;
    }

    /**
     * Sets the Java classe names that represent the parameter types of the method arguments.
     *
     * If you wish to use a primitive type, specify the corresonding Java wrapper class instead,
     * such as {@code java.lang.Boolean.TYPE} for a {@code boolean} parameter.
     *
     * @param The Java classe names that represent the parameter types of the method arguments
     * @return this builder instance
     */
    public CallMethodBuilderImpl withParamTypes(String...paramTypeNames) {
        if (paramTypeNames != null) {
            this.paramTypes = new Class[paramTypeNames.length];
            for (int i = 0; i < paramTypeNames.length; i++) {
                try {
                    this.paramTypes[i] = classLoader.loadClass(paramTypeNames[i]);
                } catch (ClassNotFoundException e) {
                    this.reportError(
                            String.format("callMethod(\"%s\").withParamTypes(%s)", this.methodName, Arrays.toString(paramTypeNames)),
                            String.format("class '%s' cannot be load", paramTypeNames[i]));
                }
            }
        }

        return this;
    }

    /**
     * Sets the Java classes that represent the parameter types of the method arguments.
     *
     * If you wish to use a primitive type, specify the corresonding Java wrapper class instead,
     * such as {@code java.lang.Boolean.TYPE} for a {@code boolean} parameter.
     *
     * @param paramTypes The Java classes that represent the parameter types of the method arguments
     * @return this builder instance
     */
    public CallMethodBuilderImpl withParamTypes(Class<?>...paramTypes) {
        this.paramTypes = paramTypes;

        if (paramTypes != null) {
            this.paramCount = paramTypes.length;
        }

        return this;
    }

    /**
     * Should <code>MethodUtils.invokeExactMethod</code> be used for the reflection.
     *
     * @param useExactMatch Flag to mark exact matching or not
     * @return this builder instance
     */
    public CallMethodBuilderImpl useExactMatch(boolean useExactMatch) {
        this.useExactMatch = useExactMatch;
        return this;
    }

    /**
     * The number of parameters to collect, or zero for a single argument from the body of this element.
     *
     * @param paramCount The number of parameters to collect, or zero for a single argument
     *        from the body of this element.
     * @return this builder instance
     */
    public CallMethodBuilderImpl withParamCount(int paramCount) {
        if (paramCount < 0) {
            this.reportError(String.format("callMethod(\"%s\").withParamCount(int)", this.methodName),
                    "negative parameters counter not allowed");
        }

        this.paramCount = paramCount;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected CallMethodRule createRule() {
        Class<?>[] paramTypes = null;

        if (this.paramTypes == null) {
            if (this.paramCount == 0) {
                paramTypes = new Class<?>[] { String.class };
            } else {
                paramTypes = new Class<?>[this.paramCount];
                for (int i = 0; i < paramTypes.length; i++) {
                    paramTypes[i] = String.class;
                }
            }
        } else {
            paramTypes = this.paramTypes;
        }

        return new CallMethodRule(this.targetOffset,
                this.methodName,
                this.paramCount,
                paramTypes,
                this.useExactMatch);
    }

}
