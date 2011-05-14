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
package org.apache.commons.digester3.binder;

import org.apache.commons.digester3.Rule;

/**
 * Builder invoked to bind one or more rules to a pattern.
 *
 * @since 3.0
 */
public final class LinkedRuleBuilder
{

    private final RulesBinder mainBinder;

    private final FromBinderRuleSet fromBinderRuleSet;

    private final ClassLoader classLoader;

    private final String keyPattern;

    private String namespaceURI;

    LinkedRuleBuilder( final RulesBinder mainBinder, final FromBinderRuleSet fromBinderRuleSet,
                       final ClassLoader classLoader, final String keyPattern )
    {
        this.mainBinder = mainBinder;
        this.fromBinderRuleSet = fromBinderRuleSet;
        this.classLoader = classLoader;
        this.keyPattern = keyPattern;
    }

    /**
     * Construct rule that automatically sets a property from the body text, taking the property
     * name the same as the current element.
     */
    public BeanPropertySetterBuilder setBeanProperty()
    {
        return addProvider( new BeanPropertySetterBuilder( this.keyPattern, this.namespaceURI, this.mainBinder, this ) );
    }

    /**
     * Sets the namespace URI for the current rule pattern.
     *
     * @param namespaceURI the namespace URI associated to the rule pattern.
     * @return this {@link LinkedRuleBuilder} instance
     */
    public LinkedRuleBuilder withNamespaceURI( /* @Nullable */String namespaceURI )
    {
        if ( namespaceURI == null || namespaceURI.length() > 0 )
        {
            this.namespaceURI = namespaceURI;
        }
        else
        {
            // ignore empty namespaces, null is better
            this.namespaceURI = null;
        }

        return this;
    }

    /**
     * Add a provider in the data structure where storing the providers binding.
     *
     * @param <R> The rule will be created by the given provider
     * @param provider The provider has to be stored in the data structure
     * @return The provider itself has to be stored in the data structure
     */
    private <R extends Rule, RP extends RuleProvider<R>> RP addProvider( RP provider )
    {
        fromBinderRuleSet.registerProvider( provider );
        return provider;
    }

}
