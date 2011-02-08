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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.digester3.rulesbinder.BackToLinkedRuleBuilder;
import org.apache.commons.digester3.rulesbinder.BeanPropertySetterBuilder;
import org.apache.commons.digester3.rulesbinder.CallMethodBuilder;
import org.apache.commons.digester3.rulesbinder.CallParamBuilder;
import org.apache.commons.digester3.rulesbinder.ConverterBuilder;
import org.apache.commons.digester3.rulesbinder.FactoryCreateBuilder;
import org.apache.commons.digester3.rulesbinder.LinkedRuleBuilder;
import org.apache.commons.digester3.rulesbinder.NestedPropertiesBuilder;
import org.apache.commons.digester3.rulesbinder.ObjectCreateBuilder;
import org.apache.commons.digester3.rulesbinder.ObjectParamBuilder;
import org.apache.commons.digester3.rulesbinder.ParamTypeBuilder;
import org.apache.commons.digester3.rulesbinder.PathCallParamBuilder;
import org.apache.commons.digester3.rulesbinder.SetPropertiesBuilder;
import org.apache.commons.digester3.rulesbinder.SetPropertyBuilder;
import org.apache.commons.digester3.spi.ObjectCreationFactory;
import org.apache.commons.digester3.spi.RuleProvider;
import org.apache.commons.digester3.spi.Rules;
import org.apache.commons.digester3.spi.TypeConverter;

/**
 * The Digester EDSL implementation.
 */
final class RulesBinderImpl implements RulesBinder {

    /**
     * Errors that can occur during binding time or rules creation.
     */
    private final List<ErrorMessage> errors = new ArrayList<ErrorMessage>();

    /**
     * The data structure where storing the providers binding.
     */
    private final Collection<RegisteredProvider> providers = new ArrayList<RegisteredProvider>();

    /**
     * {@inheritDoc}
     */
    public void addError(String messagePattern, Object... arguments) {
        this.addError(new ErrorMessage(messagePattern, arguments));
    }

    /**
     * {@inheritDoc}
     */
    public void addError(Throwable t) {
        String message = "An exception was caught and reported. Message: " + t.getMessage();
        this.addError(new ErrorMessage(message, t));
    }

    /**
     * 
     *
     * @param errorMessage
     */
    private void addError(ErrorMessage errorMessage) {
        this.errors.add(errorMessage);
    }

    /**
     * 
     *
     * @return
     */
    public boolean containsErrors() {
        return !this.errors.isEmpty();
    }

    /**
     * 
     *
     * @return
     */
    public List<ErrorMessage> getErrors() {
        return errors;
    }

    /**
     * {@inheritDoc}
     */
    public void install(RulesModule rulesModule) {
        rulesModule.configure(this);
    }

    /**
     * {@inheritDoc}
     */
    public LinkedRuleBuilder forPattern(String pattern) {
        final String keyPattern;

        if (pattern == null || pattern.length() == 0) {
            this.addError(new IllegalArgumentException("Null or empty pattern is not valid"));
            keyPattern = null;
        } else {
            if (pattern.endsWith("/")) {
                // to help users who accidently add '/' to the end of their patterns
                keyPattern = pattern.substring(0, pattern.length() - 1);
            } else {
                keyPattern = pattern;
            }
        }

        return new LinkedRuleBuilder() {

            private final LinkedRuleBuilder mainBuilder = this;

            private String namespaceURI;

            public LinkedRuleBuilder withNamespaceURI(/* @Nullable */ String namespaceURI) {
                this.namespaceURI = namespaceURI;
                return this;
            }

            /**
             * 
             */
            public ParamTypeBuilder<SetTopRule> setTop(final String methodName) {
                if (methodName == null || methodName.length() == 0) {
                    addError("{forPattern(\"%s\").setTop(String)} empty 'methodName' not allowed", keyPattern);
                }

                return this.addProvider(
                        new AbstractParamTypeBuilder<SetTopRule>(keyPattern, methodName, RulesBinderImpl.this, this) {

                    @Override
                    protected String getCalledMethodName() {
                        return "setTop";
                    }

                    public SetTopRule get() {
                        return setNamespaceAndReturn(
                                new SetTopRule(this.getMethodName(), this.getParamType(), this.isUseExactMatch()));
                    }

                });
            }

            /**
             * 
             */
            public ParamTypeBuilder<SetRootRule> setRoot(final String methodName) {
                if (methodName == null || methodName.length() == 0) {
                    addError("{forPattern(\"%s\").setRoot(String)} empty 'methodName' not allowed", keyPattern);
                }

                return this.addProvider(
                        new AbstractParamTypeBuilder<SetRootRule>(keyPattern, methodName, RulesBinderImpl.this, this) {

                    @Override
                    protected String getCalledMethodName() {
                        return "setRoot";
                    }

                    public SetRootRule get() {
                        return setNamespaceAndReturn(
                                new SetRootRule(this.getMethodName(), this.getParamType(), this.isUseExactMatch()));
                    }

                });
            }

            /**
             * 
             */
            public SetPropertyBuilder setProperty(final String attributePropertyName) {
                if (attributePropertyName == null || attributePropertyName.length() == 0) {
                    addError("{forPattern(\"%s\").setProperty(String)} empty 'attributePropertyName' not allowed",
                            keyPattern);
                }

                return this.addProvider(new SetPropertyBuilder() {

                    private String valueAttributeName;

                    public SetPropertyRule get() {
                        return setNamespaceAndReturn(new SetPropertyRule(attributePropertyName, valueAttributeName));
                    }

                    public LinkedRuleBuilder then() {
                        return mainBuilder;
                    }

                    public SetPropertyBuilder extractingValueFromAttribute(String valueAttributeName) {
                        if (attributePropertyName == null || attributePropertyName.length() == 0) {
                            addError("{forPattern(\"%s\").setProperty(\"%s\").extractingValueFromAttribute(String)} empty 'valueAttributeName' not allowed",
                                    keyPattern,
                                    attributePropertyName);
                        }

                        this.valueAttributeName = valueAttributeName;
                        return this;
                    }

                });
            }

            /**
             * 
             */
            public SetPropertiesBuilder setProperties() {
                return this.addProvider(new SetPropertiesBuilder() {

                    private final Map<String, String> aliases = new HashMap<String, String>();

                    private boolean ignoreMissingProperty = true;

                    public SetPropertiesRule get() {
                        return setNamespaceAndReturn(new SetPropertiesRule(this.aliases, this.ignoreMissingProperty));
                    }

                    public LinkedRuleBuilder then() {
                        return mainBuilder;
                    }

                    public SetPropertiesBuilder ignoreMissingProperty(boolean ignoreMissingProperty) {
                        this.ignoreMissingProperty = ignoreMissingProperty;
                        return this;
                    }

                    public SetPropertiesBuilder ignoreAttribute(String attributeName) {
                        if (attributeName == null) {
                            addError("{forPattern(\"%s\").setProperties().ignoreAttribute(String)} empty 'attributeName' not allowed",
                                    keyPattern);
                        } else {
                            this.aliases.put(attributeName, null);
                        }
                        return this;
                    }

                    public SetPropertiesBuilder addAlias(String attributeName, /* @Nullable */String propertyName) {
                        if (attributeName == null) {
                            addError("{forPattern(\"%s\").setProperties().addAlias(String,String)} empty 'attributeName' not allowed",
                                    keyPattern);
                        } else {
                            this.aliases.put(attributeName, propertyName);
                        }
                        return this;
                    }

                });
            }

            /**
             * 
             */
            public ParamTypeBuilder<SetNextRule> setNext(final String methodName) {
                if (methodName == null || methodName.length() == 0) {
                    addError("{forPattern(\"%s\").setNext(String)} empty 'methodName' not allowed", keyPattern);
                }

                return this.addProvider(
                        new AbstractParamTypeBuilder<SetNextRule>(keyPattern, methodName, RulesBinderImpl.this, this) {

                    @Override
                    protected String getCalledMethodName() {
                        return "setNext";
                    }

                    public SetNextRule get() {
                        return setNamespaceAndReturn(
                                new SetNextRule(this.getMethodName(), this.getParamType(), this.isUseExactMatch()));
                    }

                });
            }

            /**
             * 
             */
            public NestedPropertiesBuilder setNestedProperties() {
                return this.addProvider(new NestedPropertiesBuilder() {

                    private final Map<String, String> elementNames = new HashMap<String, String>();

                    private boolean trimData = true;

                    private boolean allowUnknownChildElements = false;

                    public SetNestedPropertiesRule get() {
                        return setNamespaceAndReturn(
                                new SetNestedPropertiesRule(elementNames, trimData, allowUnknownChildElements));
                    }

                    public LinkedRuleBuilder then() {
                        return mainBuilder;
                    }

                    public NestedPropertiesBuilder trimData(boolean trimData) {
                        this.trimData = trimData;
                        return this;
                    }

                    public NestedPropertiesBuilder addAlias(String elementName, String propertyName) {
                        if (elementName == null) {
                            addError("{forPattern(\"%s\").setNestedProperties().addAlias(String,String)} empty 'methodName' not allowed",
                                    keyPattern);
                        } else {
                            this.elementNames.put(elementName, propertyName);
                        }
                        return this;
                    }

                });
            }

            /**
             * 
             */
            public BeanPropertySetterBuilder setBeanProperty() {
                return this.addProvider(new BeanPropertySetterBuilder() {

                    private String propertyName;

                    public BeanPropertySetterRule get() {
                        return setNamespaceAndReturn(new BeanPropertySetterRule(this.propertyName));
                    }

                    public LinkedRuleBuilder then() {
                        return mainBuilder;
                    }

                    public BeanPropertySetterBuilder withName(String propertyName) {
                        this.propertyName = propertyName;
                        return this;
                    }

                });
            }

            /**
             * 
             */
            public <T> ObjectParamBuilder objectParam(/* @Nullable */final T paramObj) {
                return this.addProvider(new ObjectParamBuilder() {

                    private int paramIndex = 0;

                    private String attributeName;

                    public ObjectParamRule get() {
                        return setNamespaceAndReturn(new ObjectParamRule(paramIndex, attributeName, paramObj));
                    }

                    public LinkedRuleBuilder then() {
                        return mainBuilder;
                    }

                    public ObjectParamBuilder ofIndex(int paramIndex) {
                        if (paramIndex < 0) {
                            addError("{forPattern(\"%s\").objectParam(%s).ofIndex(int)} negative index argument not allowed",
                                    keyPattern,
                                    String.valueOf(paramObj));
                        }

                        this.paramIndex = paramIndex;
                        return this;
                    }

                    public ObjectParamBuilder matchingAttribute(/* @Nullable */String attributeName) {
                        this.attributeName = attributeName;
                        return this;
                    }

                });
            }

            /**
             * 
             */
            public FactoryCreateBuilder factoryCreate() {
                return this.addProvider(new FactoryCreateBuilder() {

                    private String className;

                    private String attributeName;

                    private boolean ignoreCreateExceptions;

                    private ObjectCreationFactory<?> creationFactory;

                    public FactoryCreateRule get() { // loading error, the rest are binding errors
                        if (className == null && attributeName == null && creationFactory == null) {
                            throw new DigesterLoadingException("{forPattern(\"%s\").factoryCreate()} at least one between 'className' ar 'attributeName' or 'creationFactory' has to be specified",
                                    keyPattern);
                        }

                        return setNamespaceAndReturn(
                                new FactoryCreateRule(className, attributeName, creationFactory, ignoreCreateExceptions));
                    }

                    public LinkedRuleBuilder then() {
                        return mainBuilder;
                    }

                    public <T> FactoryCreateBuilder usingFactory(/* @Nullable */ObjectCreationFactory<T> creationFactory) {
                        this.creationFactory = creationFactory;
                        return this;
                    }

                    public FactoryCreateBuilder overriddenByAttribute(/* @Nullable */String attributeName) {
                        this.attributeName = attributeName;
                        return this;
                    }

                    public FactoryCreateBuilder ofType(Class<?> type) {
                        if (type == null) {
                            addError("{forPattern(\"%s\").factoryCreate().ofType(Class<?>)} NULL Java type not allowed",
                                    keyPattern);
                            return this;
                        }

                        return this.ofType(type.getName());
                    }

                    public FactoryCreateBuilder ofType(/* @Nullable */String className) {
                        this.className = className;
                        return this;
                    }

                    public FactoryCreateBuilder ignoreCreateExceptions(boolean ignoreCreateExceptions) {
                        this.ignoreCreateExceptions = ignoreCreateExceptions;
                        return this;
                    }

                });
            }

            /**
             * 
             */
            public ObjectCreateBuilder createObject() {
                return this.addProvider(new ObjectCreateBuilder() {

                    private String className;

                    private String attributeName;

                    public ObjectCreateRule get() {
                        if (this.className == null && this.attributeName == null) {
                            throw new DigesterLoadingException("{forPattern(\"%s\").createObject()} At least one between 'className' or 'attributeName' has to be specified",
                                    keyPattern);
                        }

                        return setNamespaceAndReturn(new ObjectCreateRule(this.className, this.attributeName));
                    }

                    public LinkedRuleBuilder then() {
                        return mainBuilder;
                    }

                    public ObjectCreateBuilder ofTypeSpecifiedByAttribute(String attributeName) {
                        this.attributeName = attributeName;
                        return this;
                    }

                    public ObjectCreateBuilder ofType(Class<?> type) {
                        if (type == null) {
                            addError("{forPattern(\"%s\").createObject().ofType(Class<?>)} NULL Java type not allowed",
                                    keyPattern);
                            return this;
                        }

                        return this.ofType(type.getName());
                    }

                    public ObjectCreateBuilder ofType(String className) {
                        this.className = className;
                        return this;
                    }

                });
            }

            /**
             * 
             */
            public PathCallParamBuilder callParamPath() {
                return this.addProvider(new PathCallParamBuilder() {

                    private int paramIndex = 0;

                    public PathCallParamRule get() {
                        return setNamespaceAndReturn(new PathCallParamRule(this.paramIndex));
                    }

                    public LinkedRuleBuilder then() {
                        return mainBuilder;
                    }

                    public PathCallParamBuilder ofIndex(int paramIndex) {
                        if (paramIndex < 0) {
                            addError("{forPattern(\"%s\").callParamPath().ofIndex(int)} negative index argument not allowed",
                                    keyPattern);
                        }

                        this.paramIndex = paramIndex;
                        return this;
                    }

                });
            }

            /**
             * 
             */
            public CallParamBuilder callParam() {
                return this.addProvider(new CallParamBuilder() {

                    private int paramIndex = 0;

                    private int stackIndex = 0;

                    private boolean fromStack = false;

                    private String attributeName;

                    public CallParamRule get() {
                        return setNamespaceAndReturn(new CallParamRule(paramIndex, fromStack, stackIndex, attributeName));
                    }

                    public LinkedRuleBuilder then() {
                        return mainBuilder;
                    }

                    public CallParamBuilder withStackIndex(int stackIndex) {
                        this.stackIndex = stackIndex;
                        return this;
                    }

                    public CallParamBuilder ofIndex(int paramIndex) {
                        if (paramIndex < 0) {
                            addError("{forPattern(\"%s\").callParam().ofIndex(int)} negative index argument not allowed",
                                    keyPattern);
                        }

                        this.paramIndex = paramIndex;
                        return this;
                    }

                    public CallParamBuilder fromStack(boolean fromStack) {
                        this.fromStack = fromStack;
                        return this;
                    }

                    public CallParamBuilder fromAttribute(/* @Nullable */String attributeName) {
                        this.attributeName = attributeName;
                        return this;
                    }

                });
            }

            /**
             * 
             */
            public CallMethodBuilder callMethod(final String methodName) {
                if (methodName == null || methodName.length() == 0) {
                    addError("{forPattern(\"%s\").callMethod(String)} empty 'methodName' not allowed", keyPattern);
                }

                return this.addProvider(new CallMethodBuilder() {

                    private int targetOffset;

                    private int paramCount = 0;

                    private Class<?>[] paramTypes;

                    private boolean useExactMatch = false;

                    public CallMethodRule get() {
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

                        return setNamespaceAndReturn(
                                new CallMethodRule(targetOffset, methodName, paramCount, paramTypes, useExactMatch));
                    }

                    public LinkedRuleBuilder then() {
                        return mainBuilder;
                    }

                    public CallMethodBuilder useExactMatch(boolean useExactMatch) {
                        this.useExactMatch = useExactMatch;
                        return this;
                    }

                    public CallMethodBuilder withTargetOffset(int targetOffset) {
                        this.targetOffset = targetOffset;
                        return this;
                    }

                    public CallMethodBuilder withParamCount(int paramCount) {
                        if (paramCount < 0) {
                            addError("{forPattern(\"%s\").callMethod().withParamCount(int)} negative parameters counter not allowed",
                                    keyPattern);
                        }

                        this.paramCount = paramCount;
                        return this;
                    }

                    public CallMethodBuilder withParamTypes(/* @Nullable */Class<?>... paramTypes) {
                        this.paramTypes = paramTypes;
                        return this;
                    }

                });
            }

            /**
             * 
             */
            public <R extends Rule> BackToLinkedRuleBuilder<R> addRule(final R rule) {
                if (rule == null) {
                    addError("{forPattern(\"%s\").addRule()} null rule not valid", keyPattern);
                }

                return this.addRuleCreatedBy(new RuleProvider<R>() {

                    public R get() {
                        return rule;
                    }

                });
            }

            /**
             * 
             */
            public <R extends Rule> BackToLinkedRuleBuilder<R> addRuleCreatedBy(final RuleProvider<R> provider) {
                if (provider == null) {
                    addError("{forPattern(\"%s\").addRuleCreatedBy()} null rule not valid", keyPattern);
                }

                return this.addProvider(new BackToLinkedRuleBuilder<R>() {

                    public LinkedRuleBuilder then() {
                        return mainBuilder;
                    }

                    public R get() {
                        return setNamespaceAndReturn(provider.get());
                    }

                });
            }

            /**
             * Add a provider in the data structure where storing the providers binding.
             *
             * @param <R> The rule will be created by the given provider
             * @param provider The provider has to be stored in the data structure
             * @return The provider itself has to be stored in the data structure
             */
            private <R extends Rule, RP extends RuleProvider<R>> RP addProvider(RP provider) {
                if (keyPattern == null) {
                    return provider;
                }

                providers.add(new RegisteredProvider(keyPattern, provider));

                return provider;
            }

            /**
             * Set the namespaceURI to the given rule and return it.
             *
             * @param <R> The rule type to apply the namespaceURI
             * @param rule The rule to apply the namespaceURI
             * @return  The rule type to apply the namespaceURI
             */
            private <R extends Rule> R setNamespaceAndReturn(R rule) {
                rule.setNamespaceURI(namespaceURI);
                return rule;
            }

        };
    }

    /**
     * Invokes the bound providers, then create the rule and associate it to the related pattern,
     * storing them in the proper {@link Rules} implementation data structure.
     *
     * @param rules The {@link Rules} implementation to store the produced {@link Rule}s
     */
    public void populateRules(Rules rules) {
        for (RegisteredProvider registeredProvider : this.providers) {
            rules.add(registeredProvider.getPattern(), registeredProvider.getProvider().get());
        }
    }

    /**
     * Used to associate rule providers with paths in the rules binder.
     */
    private static class RegisteredProvider {

        private final String pattern;

        private final RuleProvider<? extends Rule> provider;

        public <R extends Rule> RegisteredProvider(String pattern, RuleProvider<R> provider) {
            this.pattern = pattern;
            this.provider = provider;
        }

        public String getPattern() {
            return pattern;
        }

        public RuleProvider<? extends Rule> getProvider() {
            return provider;
        }

    }

    /**
     * Abstract {@link ParamTypeBuilder} implementation for {@code setNext()}, {@code setRoot()} and {@code setTop()}
     *
     * @param <R> The rule type has to be created
     */
    private static abstract class AbstractParamTypeBuilder<R extends Rule> implements ParamTypeBuilder<R> {

        private final String keyPattern;

        private final String methodName;

        private final RulesBinder binder;

        private final LinkedRuleBuilder mainLinkedBuilder;

        private boolean useExactMatch = false;

        private String paramType;

        public AbstractParamTypeBuilder(String keyPattern,
                String methodName,
                RulesBinder binder,
                LinkedRuleBuilder mainBuilder) {
            this.keyPattern = keyPattern;
            this.methodName = methodName;
            this.binder = binder;
            this.mainLinkedBuilder = mainBuilder;
        }

        /**
         * {@inheritDoc}
         */
        public final LinkedRuleBuilder then() {
            return this.mainLinkedBuilder;
        }

        /**
         * {@inheritDoc}
         */
        public final ParamTypeBuilder<R> useExactMatch(boolean useExactMatch) {
            this.useExactMatch = useExactMatch;
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public final ParamTypeBuilder<R> withParameterType(Class<?> paramType) {
            if (paramType == null) {
                this.binder.addError("{forPattern(\"%s\").%s.withParameterType(Class<?>)} NULL Java type not allowed",
                        this.keyPattern,
                        this.getCalledMethodName());
                return this;
            }
            return this.withParameterType(paramType.getName());
        }

        /**
         * {@inheritDoc}
         */
        public ParamTypeBuilder<R> withParameterType(String paramType) {
            this.paramType = paramType;
            return this;
        }

        protected abstract String getCalledMethodName();

        public String getMethodName() {
            return methodName;
        }

        public String getParamType() {
            return paramType;
        }

        public boolean isUseExactMatch() {
            return useExactMatch;
        }

    }

    /**
     * {@inheritDoc}
     */
    public <T> ConverterBuilder<T> convert(final Class<T> type) {
        if (type == null) {
            this.addError(new IllegalArgumentException("NULL type is not allowed to be converted"));
        }
        return new ConverterBuilder<T>() {

            public void withConverter(TypeConverter<T> typeConverter) {
                if (typeConverter == null) {
                    addError(new IllegalArgumentException(
                            String.format("NULL TypeConverter is not allowed for converting '%s' type",
                                    type.getName())));
                }

                ConvertUtils.register(new BeanUtilsConverterFacade(typeConverter), type);
            }

        };
    }

}
