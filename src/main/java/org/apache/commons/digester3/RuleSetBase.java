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

/**
 * <p>
 * Convenience base class that implements the {@link RuleSet} interface. Concrete implementations should list all of
 * their actual rule creation logic in the <code>addRuleSet()</code> implementation.
 * </p>
 */
public abstract class RuleSetBase
    implements RuleSet
{

    // ----------------------------------------------------- Instance Variables

    /**
     * The namespace URI that all Rule instances created by this RuleSet will be associated with.
     */
    private final String namespaceURI;

    // ----------------------------------------------------------- Constructors

    /**
     * Build a new RuleSetBase with a null namespaceURI
     */
    public RuleSetBase()
    {
        this( null );
    }

    /**
     * Build a new RuleSetBase with the given namespaceURI
     *
     * @param namespaceURI The namespace URI that all Rule instances will be associated with.
     * @since 3.0
     */
    public RuleSetBase( String namespaceURI )
    {
        this.namespaceURI = namespaceURI;
    }

    // ------------------------------------------------------------- Properties

    /**
     * {@inheritDoc}
     */
    public String getNamespaceURI()
    {
        return ( this.namespaceURI );
    }

}
