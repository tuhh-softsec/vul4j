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

import org.apache.commons.digester3.Rule;
import org.apache.commons.digester3.RulesBinder;
import org.apache.commons.digester3.rulesbinder.BackToLinkedRuleBuilder;
import org.apache.commons.digester3.rulesbinder.LinkedRuleBuilder;

/**
 * Abstract {@link BackToLinkedRuleBuilder} implementation.
 *
 * @param <R>
 */
abstract class AbstractBackToLinkedRuleBuilder<R extends Rule> implements BackToLinkedRuleBuilder<R> {

    private final String keyPattern;

    private final String namespaceURI;

    private final RulesBinder mainBinder;

    private final LinkedRuleBuilderImpl mainBuilder;

    public AbstractBackToLinkedRuleBuilder(final String keyPattern,
            final String namespaceURI,
            final RulesBinder mainBinder,
            final LinkedRuleBuilderImpl mainBuilder) {
        this.keyPattern = keyPattern;
        this.namespaceURI = namespaceURI;
        this.mainBinder = mainBinder;
        this.mainBuilder = mainBuilder;
    }

    /**
     * Come back to the main {@link LinkedRuleBuilderImpl}.
     *
     * @return the main {@link LinkedRuleBuilderImpl}
     */
    public final LinkedRuleBuilder then() {
        return this.mainBuilder;
    }

    /**
     * {@inheritDoc}
     */
    public final R get() {
        R rule = this.createRule();
        if (rule != null && this.namespaceURI != null) {
            rule.setNamespaceURI(this.namespaceURI);
        }
        return rule;
    }

    protected final void reportError(String methodChain, String message) {
        this.mainBinder.addError("{forPattern(\"%s\").%s} %s", this.keyPattern, methodChain, message);
    }

    /**
     * @see #get()
     * @return
     */
    protected abstract R createRule();

}
