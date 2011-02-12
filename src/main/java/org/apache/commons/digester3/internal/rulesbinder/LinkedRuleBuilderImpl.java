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
import org.apache.commons.digester3.rulesbinder.BeanPropertySetterBuilder;
import org.apache.commons.digester3.rulesbinder.ByRuleBuilder;
import org.apache.commons.digester3.rulesbinder.ByRuleProviderBuilder;
import org.apache.commons.digester3.rulesbinder.CallMethodBuilder;
import org.apache.commons.digester3.rulesbinder.CallParamBuilder;
import org.apache.commons.digester3.rulesbinder.FactoryCreateBuilder;
import org.apache.commons.digester3.rulesbinder.LinkedRuleBuilder;
import org.apache.commons.digester3.rulesbinder.NestedPropertiesBuilder;
import org.apache.commons.digester3.rulesbinder.ObjectCreateBuilder;
import org.apache.commons.digester3.rulesbinder.ObjectParamBuilder;
import org.apache.commons.digester3.rulesbinder.PathCallParamBuilder;
import org.apache.commons.digester3.rulesbinder.SetNextBuilder;
import org.apache.commons.digester3.rulesbinder.SetPropertiesBuilder;
import org.apache.commons.digester3.rulesbinder.SetPropertyBuilder;
import org.apache.commons.digester3.rulesbinder.SetRootBuilder;
import org.apache.commons.digester3.rulesbinder.SetTopBuilder;
import org.apache.commons.digester3.spi.RuleProvider;

/**
 * Builder invoked to bind one or more rules to a pattern.
 */
final class LinkedRuleBuilderImpl implements LinkedRuleBuilder {

    private final RulesBinder mainBinder;

    /**
     * The data structure where storing the providers binding.
     */
    private final ProvidersRegistry providersRegistry;

    private final ClassLoader classLoader;

    private final String keyPattern;

    private String namespaceURI;

    public LinkedRuleBuilderImpl(final RulesBinder mainBinder,
            final ProvidersRegistry providersRegistry,
            final ClassLoader classLoader,
            final String keyPattern) {
        this.mainBinder = mainBinder;
        this.providersRegistry = providersRegistry;
        this.classLoader = classLoader;
        this.keyPattern = keyPattern;
    }

    /**
     * {@inheritDoc}
     */
    public LinkedRuleBuilder withNamespaceURI(/* @Nullable */String namespaceURI) {
        if (namespaceURI == null || namespaceURI.length() > 0) {
            this.namespaceURI = namespaceURI; 
        } else {
            // ignore empty namespaces, null is better
            this.namespaceURI = null;
        }

        return this;
    }

    /**
     * {@inheritDoc}
     */
    public BeanPropertySetterBuilder setBeanProperty() {
        return this.addProvider(
                new BeanPropertySetterBuilderImpl(this.keyPattern, this.namespaceURI, this.mainBinder, this));
    }

    /**
     * {@inheritDoc}
     */
    public CallMethodBuilder callMethod(String methodName) {
        if (methodName == null || methodName.length() == 0) {
            this.mainBinder.addError("{forPattern(\"%s\").callMethod(String)} empty 'methodName' not allowed", keyPattern);
        }

        return this.addProvider(
                new CallMethodBuilderImpl(this.keyPattern, this.namespaceURI, this.mainBinder, this, methodName, this.classLoader));
    }

    /**
     * {@inheritDoc}
     */
    public CallParamBuilder callParam() {
        return this.addProvider(new CallParamBuilderImpl(this.keyPattern, this.namespaceURI, this.mainBinder, this));
    }

    /**
     * {@inheritDoc}
     */
    public PathCallParamBuilder callParamPath() {
        return this.addProvider(new PathCallParamBuilderImpl(this.keyPattern, this.namespaceURI, this.mainBinder, this));
    }

    /**
     * {@inheritDoc}
     */
    public FactoryCreateBuilder factoryCreate() {
        return this.addProvider(
                new FactoryCreateBuilderImpl(this.keyPattern, this.namespaceURI, this.mainBinder, this, this.classLoader));
    }

    /**
     * {@inheritDoc}
     */
    public ObjectCreateBuilder createObject() {
        return this.addProvider(
                new ObjectCreateBuilderImpl(this.keyPattern, this.namespaceURI, this.mainBinder, this, this.classLoader));
    }

    /**
     * {@inheritDoc}
     */
    public <T> ObjectParamBuilder<T> objectParam(/* @Nullable */T paramObj) {
        return this.addProvider(
                new ObjectParamBuilderImpl<T>(this.keyPattern, this.namespaceURI, this.mainBinder, this, paramObj));
    }

    /**
     * {@inheritDoc}
     */
    public NestedPropertiesBuilder setNestedProperties() {
        // that would be useful when adding rules via automatically generated rules binding (such annotations)
        NestedPropertiesBuilder nestedPropertiesBuilder = this.providersRegistry.getProvider(this.keyPattern, NestedPropertiesBuilder.class);
        if (nestedPropertiesBuilder != null) {
            return nestedPropertiesBuilder;
        }

        return this.addProvider(new NestedPropertiesBuilderImpl(this.keyPattern, this.namespaceURI, this.mainBinder, this));
    }

    /**
     * {@inheritDoc}
     */
    public SetNextBuilder setNext(String methodName) {
        if (methodName == null || methodName.length() == 0) {
            this.mainBinder.addError("{forPattern(\"%s\").setNext(String)} empty 'methodName' not allowed",
                    this.keyPattern);
        }
        return this.addProvider(
                new SetNextBuilderImpl(this.keyPattern, this.namespaceURI, this.mainBinder, this, methodName));
    }

    /**
     * {@inheritDoc}
     */
    public SetPropertiesBuilder setProperties() {
        // that would be useful when adding rules via automatically generated rules binding (such annotations)
        SetPropertiesBuilder setPropertiesBuilder = this.providersRegistry.getProvider(this.keyPattern, SetPropertiesBuilder.class);
        if (setPropertiesBuilder != null) {
            return setPropertiesBuilder;
        }

        return this.addProvider(new SetPropertiesBuilderImpl(this.keyPattern, this.namespaceURI, this.mainBinder, this));
    }

    /**
     * {@inheritDoc}
     */
    public SetPropertyBuilder setProperty(String attributePropertyName) {
        if (attributePropertyName == null || attributePropertyName.length() == 0) {
            this.mainBinder.addError("{forPattern(\"%s\").setProperty(String)} empty 'attributePropertyName' not allowed",
                    this.keyPattern);
        }

        return this.addProvider(
                new SetPropertyBuilderImpl(this.keyPattern, this.namespaceURI, this.mainBinder, this, attributePropertyName));
    }

    /**
     * {@inheritDoc}
     */
    public SetRootBuilder setRoot(String methodName) {
        if (methodName == null || methodName.length() == 0) {
            this.mainBinder.addError("{forPattern(\"%s\").setRoot(String)} empty 'methodName' not allowed",
                    this.keyPattern);
        }

        return this.addProvider(
                new SetRootBuilderImpl(this.keyPattern, this.namespaceURI, this.mainBinder, this, methodName));
    }

    /**
     * {@inheritDoc}
     */
    public SetTopBuilder setTop(String methodName) {
        if (methodName == null || methodName.length() == 0) {
            this.mainBinder.addError("{forPattern(\"%s\").setTop(String)} empty 'methodName' not allowed",
                    this.keyPattern);
        }

        return this.addProvider(
                new SetTopBuilderImpl(this.keyPattern, this.namespaceURI, this.mainBinder, this, methodName));
    }

    /**
     * {@inheritDoc}
     */
    public <R extends Rule> ByRuleBuilder<R> addRule(R rule) {
        if (rule == null) {
            this.mainBinder.addError("{forPattern(\"%s\").addRule(R)} NULL rule not valid", this.keyPattern);
        }

        return this.addProvider(
                new ByRuleBuilderImpl<R>(this.keyPattern, this.namespaceURI, this.mainBinder, this, rule));
    }

    /**
     * {@inheritDoc}
     */
    public <R extends Rule> ByRuleProviderBuilder<R> addRuleCreatedBy(RuleProvider<R> provider) {
        if (provider == null) {
            this.mainBinder.addError("{forPattern(\"%s\").addRuleCreatedBy()} null rule not valid", this.keyPattern);
        }

        return this.addProvider(
                new ByRuleProviderBuilderImpl<R>(this.keyPattern, this.namespaceURI, this.mainBinder, this, provider));
    }

    /**
     * Add a provider in the data structure where storing the providers binding.
     *
     * @param <R> The rule will be created by the given provider
     * @param provider The provider has to be stored in the data structure
     * @return The provider itself has to be stored in the data structure
     */
    private <R extends Rule, RP extends RuleProvider<R>> RP addProvider(RP provider) {
        if (this.keyPattern == null) {
            return provider;
        }

        this.providersRegistry.registerProvider(this.keyPattern, provider);

        return provider;
    }

}
