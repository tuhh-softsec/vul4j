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
package org.apache.commons.digester3;

import java.util.Formatter;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.digester3.Rule;

/**
 * Abstract Rule for setNext(), setRoot() and setTop() rules.
 */
abstract class AbstractMethodRule extends Rule {

    /**
     * The method name to call on the parent object.
     */
    private final String methodName;

    /**
     * The Java class name of the parameter type expected by the method.
     */
    private final String paramType;

    /**
     * Should we use exact matching.
     */
    private final boolean useExactMatch;

    /**
     * The sole constructor
     *
     * @param methodName
     * @param paramType
     * @param useExactMatch
     */
    public AbstractMethodRule(String methodName, String paramType, boolean useExactMatch) {
        this.methodName = methodName;
        this.paramType = paramType;
        this.useExactMatch = useExactMatch;
    }

    public final String getMethodName() {
        return this.methodName;
    }

    public final String getParamType() {
        return this.paramType;
    }

    /**
     * <p>Is exact matching being used.</p>
     *
     * <p>This rule uses <code>org.apache.commons.beanutils.MethodUtils</code> 
     * to introspect the relevent objects so that the right method can be called.
     * Originally, <code>MethodUtils.invokeExactMethod</code> was used.
     * This matches methods very strictly 
     * and so may not find a matching method when one exists.
     * This is still the behaviour when exact matching is enabled.</p>
     *
     * <p>When exact matching is disabled, <code>MethodUtils.invokeMethod</code> is used.
     * This method finds more methods but is less precise when there are several methods 
     * with correct signatures.
     * So, if you want to choose an exact signature you might need to enable this property.</p>
     *
     * <p>The default setting is to disable exact matches.</p>
     *
     * @return true if exact matching is enabled
     */
    public final boolean isExactMatch() {
        return this.useExactMatch;
    }

    /**
     * 
     *
     * @param target
     * @param arg
     * @param logType
     * @throws Exception
     */
    protected final void invoke(Object target, Object arg, String logType) throws Exception {
        if (this.getDigester().getLog().isDebugEnabled()) {
            Formatter formatter = new Formatter().format("[%s]{%s}",
                    this.getClass().getSimpleName(),
                    this.getDigester().getMatch());

            if (target == null) {
                formatter.format(" Call [NULL %s]", logType);
            } else {
                formatter.format(" Call %s", target.getClass().getName());
            }

            formatter.format(".%s(%s)", this.getMethodName(), arg);

            this.getDigester().getLog().debug(formatter.toString());
        }

        Class<?> paramTypes[] = new Class<?>[1];
        if (this.getParamType() != null) {
            paramTypes[0] = this.getDigester().getClassLoader().loadClass(this.getParamType());
        } else {
            paramTypes[0] = arg.getClass();
        }

        if (this.isExactMatch()) {
            MethodUtils.invokeExactMethod(target, this.getMethodName(), new Object[]{ arg }, paramTypes);
        } else {
            MethodUtils.invokeMethod(target, this.getMethodName(), new Object[]{ arg }, paramTypes);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        return String.format("%s[methodName=%s, paramType=%s, useExactMatch=%s]",
                        this.getClass().getSimpleName(),
                        this.methodName,
                        this.paramType,
                        this.useExactMatch);
    }

}
