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
    private final Collection<RuleProvider<? extends Rule>> providers = new LinkedList<RuleProvider<? extends Rule>>();

    /**
     * Index for quick-retrieve provider.
     */
    private final Map<Key, Collection<RuleProvider<? extends Rule>>> providersIndex =
        new HashMap<Key, Collection<RuleProvider<? extends Rule>>>();

    /**
     * 
     *
     * @param <R>
     * @param <RP>
     * @param provider
     */
    public <R extends Rule, RP extends RuleProvider<R>> void registerProvider( RP provider )
    {
        this.providers.add( provider );

        Key key = new Key( provider.getPattern(), provider.getNamespaceURI() );

        Collection<RuleProvider<? extends Rule>> indexedProviders = this.providersIndex.get( key ); // O(1)
        if ( indexedProviders == null )
        {
            indexedProviders = new ArrayList<RuleProvider<? extends Rule>>();
            this.providersIndex.put( key, indexedProviders ); // O(1)
        }
        indexedProviders.add( provider );
    }

    /**
     * 
     *
     * @param <R>
     * @param <RP>
     * @param keyPattern
     * @param namespaceURI
     * @param type
     * @return
     */
    public <R extends Rule, RP extends RuleProvider<R>> RP getProvider( String keyPattern,
    /* @Nullable */String namespaceURI, Class<RP> type )
    {
        Key key = new Key( keyPattern, namespaceURI );

        Collection<RuleProvider<? extends Rule>> indexedProviders = this.providersIndex.get( key ); // O(1)

        if ( indexedProviders == null || indexedProviders.isEmpty() )
        {
            return null;
        }

        for ( RuleProvider<? extends Rule> ruleProvider : indexedProviders ) // FIXME O(n) not so good
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
        for ( RuleProvider<? extends Rule> provider : providers ) {
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
