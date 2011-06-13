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

import org.apache.commons.digester3.Rule;

/**
 * Builder invoked to back to main {@link LinkedRuleBuilder}.
 *
 * @since 3.0
 */
abstract class AbstractBackToLinkedRuleBuilder<R extends Rule>
    implements RuleProvider<R>
{

    private final String keyPattern;

    private final String namespaceURI;

    private final RulesBinder mainBinder;

    private final LinkedRuleBuilder mainBuilder;

    AbstractBackToLinkedRuleBuilder( final String keyPattern, final String namespaceURI,
                                            final RulesBinder mainBinder, final LinkedRuleBuilder mainBuilder )
    {
        this.keyPattern = keyPattern;
        this.namespaceURI = namespaceURI;
        this.mainBinder = mainBinder;
        this.mainBuilder = mainBuilder;
    }

    /**
     * Come back to the main {@link LinkedRuleBuilder}.
     *
     * @return the main {@link LinkedRuleBuilder}
     */
    public final LinkedRuleBuilder then()
    {
        return this.mainBuilder;
    }

    /**
     * Returns the namespace URI for which this Rule is relevant, if any.
     *
     * @return The namespace URI for which this Rule is relevant, if any
     */
    public final String getNamespaceURI()
    {
        return this.namespaceURI;
    }

    /**
     * {@inheritDoc}
     */
    public final R get()
    {
        R rule = this.createRule();
        if ( rule != null && this.namespaceURI != null )
        {
            rule.setNamespaceURI( this.namespaceURI );
        }
        return rule;
    }

    protected final void reportError( String methodChain, String message )
    {
        this.mainBinder.addError( "{ forPattern( \"%s\" ).%s } %s", this.keyPattern, methodChain, message );
    }

    /**
     * Returns the rule pattern associated to this builder.
     *
     * @return The rule pattern associated to this builder
     */
    public final String getPattern()
    {
        return keyPattern;
    }

    /**
     * Provides an instance of {@link Rule}. Must never return null.
     *
     * @return an instance of {@link Rule}.
     * @see #get()
     */
    protected abstract R createRule();

}
