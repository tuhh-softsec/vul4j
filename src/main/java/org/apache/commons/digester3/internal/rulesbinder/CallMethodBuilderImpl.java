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

    private Class<?>[] paramTypes = new Class<?>[]{};

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
     * {@inheritDoc}
     */
    public CallMethodBuilderImpl withTargetOffset(int targetOffset) {
        this.targetOffset = targetOffset;
        return this;
    }

    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    public CallMethodBuilderImpl withParamTypes(Class<?>...paramTypes) {
        this.paramTypes = paramTypes;

        if (paramTypes != null) {
            this.paramCount = paramTypes.length;
        }

        return this;
    }

    /**
     * {@inheritDoc}
     */
    public CallMethodBuilderImpl useExactMatch(boolean useExactMatch) {
        this.useExactMatch = useExactMatch;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public CallMethodBuilderImpl withParamCount(int paramCount) {
        if (paramCount < 0) {
            this.reportError(String.format("callMethod(\"%s\").withParamCount(int)", this.methodName),
                    "negative parameters counter not allowed");
        }

        this.paramCount = paramCount;

        if (this.paramCount == 0) {
            if (this.paramTypes == null || this.paramTypes.length != 1) {
                this.paramTypes = new Class<?>[] { String.class };
            }
        } else {
            this.paramTypes = new Class<?>[this.paramCount];
            for (int i = 0; i < paramTypes.length; i++) {
                this.paramTypes[i] = String.class;
            }
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public CallMethodBuilder usingElementBodyAsArgument() {
        return this.withParamCount(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected CallMethodRule createRule() {
        return new CallMethodRule(this.targetOffset,
                this.methodName,
                this.paramCount,
                this.paramTypes,
                this.useExactMatch);
    }

}
