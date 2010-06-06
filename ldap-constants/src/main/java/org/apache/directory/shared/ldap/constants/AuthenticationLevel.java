/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package org.apache.directory.shared.ldap.constants;

import org.apache.directory.shared.i18n.I18n;


/**
 * An enumeration that represents the level of authentication. We have 5 
 * different levels :
 * <ul>
 * <li>NONE : anonymous</li>
 * <li>SIMPLE : Simple authentication</li>
 * <li>STRONG : SASL or external authentication</li>
 * <li>UNAUTHENT>A special case when just doing some auditing</li>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum AuthenticationLevel
{
    /**
     * No authentication (anonymous access)
     */
    NONE( 0, "none" ),

    /**
     * Simple authentication (bound with plain-text credentials)
     */
    SIMPLE( 1, "simple" ),

    /**
     * Strong authentication (bound with encrypted credentials)
     */
    STRONG( 2, "strong" ),
    
    /**
     * Unauthentication, if the BIND contains a DN but no credentials
     */
    UNAUTHENT( 3, "unauthent" );
    
    /** The internal numeric value */
    private int level;
    
    /** The level name */
    private final String name;

    private AuthenticationLevel( int level, String name )
    {
        this.level = level;
        this.name = name;
    }

    /**
     * Returns the integer value of this level (greater value, stronger level).
     */
    public int getLevel()
    {
        return level;
    }


    /**
     * Returns the name of this level.
     */
    public String getName()
    {
        return name;
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return name;
    }


    /**
     * Return the AuthenticationLevel  associated with the given numeric level. This
     * is used by the serialization process.
     *
     * @param val The numeric level we are looking at
     * @return The associated AuthenticationLevel
     */
    public static AuthenticationLevel getLevel( int val )
    {
        switch( val )
        {
            case 0: return NONE;
            
            case 1: return SIMPLE;
            
            case 2: return STRONG;
            
            case 3: return UNAUTHENT;
            
            default:
                throw new IllegalArgumentException( I18n.err(I18n.ERR_05001, val ) );
        }
    }
}
