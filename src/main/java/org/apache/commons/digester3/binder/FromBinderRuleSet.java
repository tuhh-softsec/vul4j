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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.Rule;
import org.apache.commons.digester3.RuleSet;

/**
 * {@link RuleSet} implementation that allows register {@link RuleProvider} instances
 * and add rules to the {@link Digester}.
 *
 * @since 3.0
 */
final class FromBinderRuleSet
    implements RuleSet
{

    /**
     * The data structure where storing the providers binding.
     */
    private final Collection<AbstractBackToLinkedRuleBuilder<? extends Rule>> providers =
        new LinkedList<AbstractBackToLinkedRuleBuilder<? extends Rule>>();

    /**
     * Index for quick-retrieve provider.
     */
    private final Map<Key, Collection<AbstractBackToLinkedRuleBuilder<? extends Rule>>> providersIndex =
        new HashMap<Key, Collection<AbstractBackToLinkedRuleBuilder<? extends Rule>>>();

    /**
     * Register the given rule builder and returns it.
     *
     * @param <R> The Digester rule type
     * @param <RB> The Digester rule builder type
     * @param ruleBuilder The input rule builder instance.
     */
    public <R extends Rule, RB extends AbstractBackToLinkedRuleBuilder<R>> void registerProvider( RB ruleBuilder )
    {
        this.providers.add( ruleBuilder );

        Key key = new Key( ruleBuilder.getPattern(), ruleBuilder.getNamespaceURI() );

        // O(1)
        Collection<AbstractBackToLinkedRuleBuilder<? extends Rule>> indexedProviders = this.providersIndex.get( key );
        if ( indexedProviders == null )
        {
            indexedProviders = new ArrayList<AbstractBackToLinkedRuleBuilder<? extends Rule>>();
            this.providersIndex.put( key, indexedProviders ); // O(1)
        }
        indexedProviders.add( ruleBuilder );
    }

    /**
     * Returns the first instance of {@link RuleProvider} assignable to the input type.
     *
     * This method is useful for rules that requires be unique in the pattern,
     * like {@link org.apache.commons.digester3.SetPropertiesRule}
     * and {@link org.apache.commons.digester3.SetNestedPropertiesRule}.
     *
     * @param <R> The Digester rule type
     * @param <RB> The Digester rule builder type
     * @param keyPattern the rule pattern
     * @param namespaceURI the namespace URI (can be null)
     * @param type the rule builder type the client is looking for
     * @return the rule builder of input type, if any
     */
    public <R extends Rule, RB extends AbstractBackToLinkedRuleBuilder<R>> RB getProvider( String keyPattern,
    /* @Nullable */String namespaceURI, Class<RB> type )
    {
        Key key = new Key( keyPattern, namespaceURI );

        // O(1)
        Collection<AbstractBackToLinkedRuleBuilder<? extends Rule>> indexedProviders = this.providersIndex.get( key );

        if ( indexedProviders == null || indexedProviders.isEmpty() )
        {
            return null;
        }

        // FIXME O(n) not so good
        for ( AbstractBackToLinkedRuleBuilder<? extends Rule> ruleProvider : indexedProviders )
        {
            if ( type.isInstance( ruleProvider ) )
            {
                return type.cast( ruleProvider );
            }
        }

        return null;
    }

    /**
     * Clean the provider index.
     */
    public void clear()
    {
        providers.clear();
        providersIndex.clear();
    }

    /**
     * {@inheritDoc}
     */
    public void addRuleInstances( Digester digester )
    {
        for ( AbstractBackToLinkedRuleBuilder<? extends Rule> provider : providers )
        {
            digester.addRule( provider.getPattern(), provider.get() );
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getNamespaceURI()
    {
        return null;
    }

    /**
     * Used to associate pattern/namespaceURI
     */
    private static final class Key
    {

        private final String pattern;

        private final String namespaceURI;

        public Key( String pattern, String namespaceURI )
        {
            this.pattern = pattern;
            this.namespaceURI = namespaceURI;
        }

        public String getPattern()
        {
            return pattern;
        }

        public String getNamespaceURI()
        {
            return namespaceURI;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + ( ( namespaceURI == null ) ? 0 : namespaceURI.hashCode() );
            result = prime * result + ( ( pattern == null ) ? 0 : pattern.hashCode() );
            return result;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals( Object obj )
        {
            if ( this == obj )
            {
                return true;
            }

            if ( obj == null )
            {
                return false;
            }

            if ( getClass() != obj.getClass() )
            {
                return false;
            }

            Key other = (Key) obj;
            if ( namespaceURI == null )
            {
                if ( other.getNamespaceURI() != null )
                {
                    return false;
                }
            }
            else if ( !namespaceURI.equals( other.getNamespaceURI() ) )
            {
                return false;
            }

            if ( pattern == null )
            {
                if ( other.getPattern() != null )
                {
                    return false;
                }
            }
            else if ( !pattern.equals( other.getPattern() ) )
            {
                return false;
            }

            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString()
        {
            return "Key [pattern=" + pattern + ", namespaceURI=" + namespaceURI + "]";
        }

    }

}
