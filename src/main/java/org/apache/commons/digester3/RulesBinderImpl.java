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
import java.util.List;

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

/**
 * The Digester EDSL implementation.
 */
final class RulesBinderImpl implements RulesBinder {

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

            public ParamTypeBuilder<SetTopRule> setTop(String methodName) {
                return null;
            }

            public ParamTypeBuilder<SetRootRule> setRoot(String methodName) {
                return null;
            }

            public SetPropertyBuilder setProperty(String attributePropertyName) {
                return null;
            }

            public SetPropertiesBuilder setProperties() {
                return null;
            }

            public ParamTypeBuilder<SetNextRule> setNext(String methodName) {
                return null;
            }

            public NestedPropertiesBuilder setNestedProperties() {
                return null;
            }

            public BeanPropertySetterBuilder setBeanProperty() {
                return null;
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

                        return new ObjectCreateRule(this.className, this.attributeName);
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
                            addError("{forPattern(\"%s\").createObject().ofType(Class<?>)} When specifying 'className', NULL Java type is not admitted",
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

        };
    }

    /**
     * {@inheritDoc}
     */
    public <T> ConverterBuilder<T> convert(Class<T> type) {
        return null;
    }

}
