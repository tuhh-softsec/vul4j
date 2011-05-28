package org.apache.commons.digester3;

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
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * Default implementation of the <code>Rules</code> interface that supports the standard rule matching behavior. This
 * class can also be used as a base class for specialized <code>Rules</code> implementations.
 * </p>
 * <p>
 * The matching policies implemented by this class support two different types of pattern matching rules:
 * </p>
 * <ul>
 * <li><em>Exact Match</em> - A pattern "a/b/c" exactly matches a <code>&lt;c&gt;</code> element, nested inside a
 * <code>&lt;b&gt;</code> element, which is nested inside an <code>&lt;a&gt;</code> element.</li>
 * <li><em>Tail Match</em> - A pattern "&#42;/a/b" matches a <code>&lt;b&gt;</code> element, nested inside an
 * <code>&lt;a&gt;</code> element, no matter how deeply the pair is nested.</li>
 * </ul>
 * <p>
 * Note that wildcard patterns are ignored if an explicit match can be found (and when multiple wildcard patterns match,
 * only the longest, ie most explicit, pattern is considered a match).
 * </p>
 * <p>
 * See the package documentation for package org.apache.commons.digester3 for more information.
 * </p>
 */

public class RulesBase
    implements Rules
{

    // ----------------------------------------------------- Instance Variables

    /**
     * The set of registered Rule instances, keyed by the matching pattern. Each value is a List containing the Rules
     * for that pattern, in the order that they were orginally registered.
     */
    protected HashMap<String, List<Rule>> cache = new HashMap<String, List<Rule>>();

    /**
     * The Digester instance with which this Rules instance is associated.
     */
    protected Digester digester = null;

    /**
     * The namespace URI for which subsequently added <code>Rule</code> objects are relevant, or <code>null</code> for
     * matching independent of namespaces.
     */
    protected String namespaceURI = null;

    /**
     * The set of registered Rule instances, in the order that they were originally registered.
     */
    protected ArrayList<Rule> rules = new ArrayList<Rule>();

    // ------------------------------------------------------------- Properties

    /**
     * {@inheritDoc}
     */
    public Digester getDigester()
    {
        return ( this.digester );
    }

    /**
     * {@inheritDoc}
     */
    public void setDigester( Digester digester )
    {
        this.digester = digester;
        for ( Rule rule : rules )
        {
            rule.setDigester( digester );
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getNamespaceURI()
    {
        return ( this.namespaceURI );
    }

    /**
     * {@inheritDoc}
     */
    public void setNamespaceURI( String namespaceURI )
    {
        this.namespaceURI = namespaceURI;
    }

    // --------------------------------------------------------- Public Methods

    /**
     * {@inheritDoc}
     */
    public void add( String pattern, Rule rule )
    {
        // to help users who accidently add '/' to the end of their patterns
        int patternLength = pattern.length();
        if ( patternLength > 1 && pattern.endsWith( "/" ) )
        {
            pattern = pattern.substring( 0, patternLength - 1 );
        }

        List<Rule> list = cache.get( pattern );
        if ( list == null )
        {
            list = new ArrayList<Rule>();
            cache.put( pattern, list );
        }
        list.add( rule );
        rules.add( rule );
        if ( this.digester != null )
        {
            rule.setDigester( this.digester );
        }
        if ( this.namespaceURI != null )
        {
            rule.setNamespaceURI( this.namespaceURI );
        }
    }

    /**
     * {@inheritDoc}
     */
    public void clear()
    {
        cache.clear();
        rules.clear();
    }

    /**
     * {@inheritDoc}
     */
    public List<Rule> match( String namespaceURI, String pattern )
    {
        // List rulesList = (List) this.cache.get(pattern);
        List<Rule> rulesList = lookup( namespaceURI, pattern );
        if ( ( rulesList == null ) || ( rulesList.size() < 1 ) )
        {
            // Find the longest key, ie more discriminant
            String longKey = "";
            for ( String key : cache.keySet() )
            {
                if ( key.startsWith( "*/" ) )
                {
                    if ( pattern.equals( key.substring( 2 ) ) || pattern.endsWith( key.substring( 1 ) ) )
                    {
                        if ( key.length() > longKey.length() )
                        {
                            // rulesList = (List) this.cache.get(key);
                            rulesList = lookup( namespaceURI, key );
                            longKey = key;
                        }
                    }
                }
            }
        }
        if ( rulesList == null )
        {
            rulesList = new ArrayList<Rule>();
        }
        return ( rulesList );
    }

    /**
     * {@inheritDoc}
     */
    public List<Rule> rules()
    {
        return ( this.rules );
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Return a List of Rule instances for the specified pattern that also match the specified namespace URI (if any).
     * If there are no such rules, return <code>null</code>.
     * 
     * @param namespaceURI Namespace URI to match, or <code>null</code> to select matching rules regardless of namespace
     *            URI
     * @param pattern Pattern to be matched
     * @return a List of Rule instances for the specified pattern that also match the specified namespace URI (if any)
     */
    protected List<Rule> lookup( String namespaceURI, String pattern )
    {
        // Optimize when no namespace URI is specified
        List<Rule> list = this.cache.get( pattern );
        if ( list == null )
        {
            return ( null );
        }
        if ( ( namespaceURI == null ) || ( namespaceURI.length() == 0 ) )
        {
            return ( list );
        }

        // Select only Rules that match on the specified namespace URI
        ArrayList<Rule> results = new ArrayList<Rule>();
        for ( Rule item : list )
        {
            if ( ( namespaceURI.equals( item.getNamespaceURI() ) ) || ( item.getNamespaceURI() == null ) )
            {
                results.add( item );
            }
        }
        return ( results );
    }

}
