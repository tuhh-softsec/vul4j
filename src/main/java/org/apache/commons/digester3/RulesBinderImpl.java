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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.apache.commons.digester3.spi.RuleProvider;

/**
 * The Digester EDSL implementation.
 */
final class RulesBinderImpl implements RulesBinder {

    /**
     * Errors that can occur during binding time or rules creation.
     */
    private final List<ErrorMessage> errors = new ArrayList<ErrorMessage>();

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

                return new AbstractParamTypeBuilder<SetTopRule>(keyPattern, methodName, RulesBinderImpl.this, this) {

                    @Override
                    protected String getCalledMethodName() {
                        return "setTop";
                    }

                    public SetTopRule get() {
                        return new SetTopRule(this.getMethodName(), this.getParamType(), this.isUseExactMatch());
                    }

                };
            }

            /**
             * 
             */
            public ParamTypeBuilder<SetRootRule> setRoot(final String methodName) {
                if (methodName == null || methodName.length() == 0) {
                    addError("{forPattern(\"%s\").setRoot(String)} empty 'methodName' not allowed", keyPattern);
                }

                return new AbstractParamTypeBuilder<SetRootRule>(keyPattern, methodName, RulesBinderImpl.this, this) {

                    @Override
                    protected String getCalledMethodName() {
                        return "setRoot";
                    }

                    public SetRootRule get() {
                        return new SetRootRule(this.getMethodName(), this.getParamType(), this.isUseExactMatch());
                    }

                };
            }

            public SetPropertyBuilder setProperty(String attributePropertyName) {
                return null;
            }

            /**
             * 
             */
            public SetPropertiesBuilder setProperties() {
                return new SetPropertiesBuilder() {

                    private final Map<String, String> aliases = new HashMap<String, String>();

                    private boolean ignoreMissingProperty = true;

                    public SetPropertiesRule get() {
                        return new SetPropertiesRule(this.aliases, this.ignoreMissingProperty);
                    }

                    public LinkedRuleBuilder then() {
                        return null;
                    }

                    public SetPropertiesBuilder ignoreMissingProperty(boolean ignoreMissingProperty) {
                        this.ignoreMissingProperty = ignoreMissingProperty;
                        return this;
                    }

                    public SetPropertiesBuilder addAlias(String attributeName, /* @Nullable */String propertyName) {
                        if (attributeName == null) {
                            addError("{forPattern(\"%s\").setProperties().addAlias(String,String)} empty 'methodName' not allowed",
                                    keyPattern);
                        } else {
                            this.aliases.put(attributeName, propertyName);
                        }
                        return this;
                    }

                };
            }

            /**
             * 
             */
            public ParamTypeBuilder<SetNextRule> setNext(final String methodName) {
                if (methodName == null || methodName.length() == 0) {
                    addError("{forPattern(\"%s\").setNext(String)} empty 'methodName' not allowed", keyPattern);
                }

                return new AbstractParamTypeBuilder<SetNextRule>(keyPattern, methodName, RulesBinderImpl.this, this) {

                    @Override
                    protected String getCalledMethodName() {
                        return "setNext";
                    }

                    public SetNextRule get() {
                        return new SetNextRule(this.getMethodName(), this.getParamType(), this.isUseExactMatch());
                    }

                };
            }

            /**
             * 
             */
            public NestedPropertiesBuilder setNestedProperties() {
                return new NestedPropertiesBuilder() {

                    private final Map<String, String> elementNames = new HashMap<String, String>();

                    private boolean trimData = true;

                    private boolean allowUnknownChildElements = false;

                    public SetNestedPropertiesRule get() {
                        return new SetNestedPropertiesRule(elementNames, trimData, allowUnknownChildElements);
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

                };
            }

            /**
             * 
             */
            public BeanPropertySetterBuilder setBeanProperty() {
                return new BeanPropertySetterBuilder() {

                    private String propertyName;

                    public BeanPropertySetterRule get() {
                        return new BeanPropertySetterRule(this.propertyName);
                    }

                    public LinkedRuleBuilder then() {
                        return mainBuilder;
                    }

                    public BeanPropertySetterBuilder withName(String propertyName) {
                        this.propertyName = propertyName;
                        return this;
                    }

                };
            }

            public <T> ObjectParamBuilder objectParam(T paramObj) {
                return null;
            }

            public FactoryCreateBuilder factoryCreate() {
                return null;
            }

            /**
             * 
             */
            public ObjectCreateBuilder createObject() {
                return new ObjectCreateBuilder() {

                    private String className;

                    private String attributeName;

                    public ObjectCreateRule get() {
                        if (this.className == null && this.attributeName == null) {
                            addError("{forPattern(\"%s\").createObject()} At least one between 'className' or 'attributeName' has to be specified",
                                    keyPattern);
                            return null;
                        }

                        ObjectCreateRule rule = new ObjectCreateRule(this.className, this.attributeName);
                        rule.setNamespaceURI(namespaceURI);
                        return rule;
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

                };
            }

            public PathCallParamBuilder callParamPath() {
                return null;
            }

            public CallParamBuilder callParam() {
                return null;
            }

            public CallMethodBuilder callMethod(String methodName) {
                return null;
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

                return new BackToLinkedRuleBuilder<R>() {

                    public LinkedRuleBuilder then() {
                        return mainBuilder;
                    }

                    public R get() {
                        R rule = provider.get();
                        rule.setNamespaceURI(namespaceURI);
                        return rule;
                    }

                };
            }

        };
    }

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

        public final LinkedRuleBuilder then() {
            return this.mainLinkedBuilder;
        }

        public final ParamTypeBuilder<R> useExactMatch(boolean useExactMatch) {
            this.useExactMatch = useExactMatch;
            return this;
        }

        public final ParamTypeBuilder<R> withParameterType(Class<?> paramType) {
            if (paramType == null) {
                this.binder.addError("{forPattern(\"%s\").%s.withParameterType(Class<?>)} NULL Java type not allowed",
                        this.keyPattern,
                        this.getCalledMethodName());
                return this;
            }
            return this.withParameterType(paramType.getName());
        }

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
    public <T> ConverterBuilder<T> convert(Class<T> type) {
        return null;
    }

}
