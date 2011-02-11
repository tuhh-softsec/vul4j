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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Formatter;
import java.util.List;

import org.apache.commons.digester3.DigesterLoadingException;
import org.apache.commons.digester3.Rule;
import org.apache.commons.digester3.RulesBinder;
import org.apache.commons.digester3.RulesModule;
import org.apache.commons.digester3.rulesbinder.ConverterBuilder;
import org.apache.commons.digester3.rulesbinder.LinkedRuleBuilder;
import org.apache.commons.digester3.spi.Rules;
import org.apache.commons.digester3.spi.TypeConverter;

/**
 * The Digester EDSL implementation.
 */
public final class RulesBinderImpl implements RulesBinder {

    /**
     * The default head when reporting an errors list.
     */
    private static final String HEADING = "Digester creation errors:%n%n";

    /**
     * Errors that can occur during binding time or rules creation.
     */
    private final List<ErrorMessage> errors = new ArrayList<ErrorMessage>();

    private final ProvidersRegistry providersRegistry = new ProvidersRegistry();

    private final ClassLoader classLoader;

    public RulesBinderImpl(final ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

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

        return new LinkedRuleBuilderImpl(this, this.providersRegistry, this.classLoader, keyPattern);
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

                // TODO register the type converter!!!
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
        if (!this.errors.isEmpty()) {
            Formatter fmt = new Formatter().format(HEADING);
            int index = 1;

            for (ErrorMessage errorMessage : this.errors) {
                fmt.format("%s) %s%n", index++, errorMessage.getMessage());

                Throwable cause = errorMessage.getCause();
                if (cause != null) {
                    StringWriter writer = new StringWriter();
                    cause.printStackTrace(new PrintWriter(writer));
                    fmt.format("Caused by: %s", writer.getBuffer());
                }

                fmt.format("%n");
            }

            if (this.errors.size() == 1) {
                fmt.format("1 error");
            } else {
                fmt.format("%s errors", this.errors.size());
            }

            throw new DigesterLoadingException(fmt.toString());
        }

        this.providersRegistry.registerRules(rules);
    }

}
