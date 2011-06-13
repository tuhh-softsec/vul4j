package org.apache.commons.digester3.binder;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/**
 * A support class for RulesModule which reduces repetition and results in a more readable configuration.
 *
 * @since 3.0
 */
public abstract class AbstractRulesModule
    implements RulesModule
{

    private RulesBinder rulesBinder;

    /**
     * {@inheritDoc}
     */
    public final void configure( RulesBinder rulesBinder )
    {
        if ( this.rulesBinder != null )
        {
            throw new IllegalStateException( "Re-entry is not allowed." );
        }

        this.rulesBinder = rulesBinder;
        try
        {
            configure();
        }
        finally
        {
            this.rulesBinder = null;
        }
    }

    /**
     * Configures a {@link RulesBinder} via the exposed methods.
     */
    protected abstract void configure();

    /**
     * Records an error message which will be presented to the user at a later time.
     *
     * Uses {@link java.lang.String#format(String, Object...)} to insert the arguments into the message.
     *
     * @param messagePattern A
     *        <a href="http://download.oracle.com/javase/6/docs/api/java/util/Formatter.html#syntax">format string</a>
     * @param arguments Arguments referenced by the format specifiers in the format string
     * @see RulesBinder#addError(String, Object...)
     */
    protected void addError( String messagePattern, Object... arguments )
    {
        rulesBinder.addError( messagePattern, arguments );
    }

    /**
     * Records an exception, the full details of which will be logged, and the message of which will be presented to
     * the user at a later time.
     *
     * @param t The exception has to be recorded
     * @see RulesBinder#addError(Throwable)
     */
    protected void addError( Throwable t )
    {
        rulesBinder.addError( t );
    }

    /**
     * Uses the given module to configure more bindings.
     *
     * @param rulesModule The module used to configure more bindings
     * @see RulesBinder#install(RulesModule)
     */
    protected void install( RulesModule rulesModule )
    {
        rulesBinder.install( rulesModule );
    }

    /**
     * Allows user binding one or more Digester rules to the input pattern.
     *
     * @param pattern The pattern used to bind rules
     * @return The Digester rules builder
     * @see RulesBinder#forPattern(String)
     */
    protected LinkedRuleBuilder forPattern( String pattern )
    {
        return rulesBinder.forPattern( pattern );
    }

    /**
     * Return the wrapped {@link RulesBinder}.
     *
     * @return The wrapped {@link RulesBinder}
     */
    protected RulesBinder rulesBinder()
    {
        return rulesBinder;
    }

}
