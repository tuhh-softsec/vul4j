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

import org.apache.commons.digester3.rulesbinder.ConverterBuilder;
import org.apache.commons.digester3.rulesbinder.LinkedRuleBuilder;

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
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public <T> ConverterBuilder<T> convert(Class<T> type) {
        return null;
    }

}
