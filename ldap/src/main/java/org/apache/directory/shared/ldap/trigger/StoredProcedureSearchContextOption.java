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

package org.apache.directory.shared.ldap.trigger;


import org.apache.directory.shared.ldap.model.filter.SearchScope;
import org.apache.directory.shared.ldap.name.Dn;


/**
 * The search context option of the triggered stored procedure.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class StoredProcedureSearchContextOption implements StoredProcedureOption
{

    private final Dn baseObject;
    private SearchScope searchScope;


    /**
     * Instantiates a new stored procedure search context option.
     *
     * @param baseObject the base object
     */
    public StoredProcedureSearchContextOption( Dn baseObject )
    {
        // the default search scope is "base"
        this( baseObject, SearchScope.OBJECT );
    }


    /**
     * Instantiates a new stored procedure search context option.
     *
     * @param baseObject the base object
     * @param searchScope the search scope
     */
    public StoredProcedureSearchContextOption( Dn baseObject, SearchScope searchScope )
    {
        this.baseObject = baseObject;
        this.searchScope = searchScope;
    }


    /**
     * Gets the base object.
     *
     * @return the base object
     */
    public Dn getBaseObject()
    {
        return baseObject;
    }


    /**
     * Gets the search scope.
     *
     * @return the search scope
     */
    public SearchScope getSearchScope()
    {
        return searchScope;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "searchContext { scope " + searchScope + " } \"" + baseObject + "\"";
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        int h = 37;

        h = h * 17 + ( ( baseObject == null ) ? 0 : baseObject.hashCode() );
        h = h * 17 + ( ( searchScope == null ) ? 0 : searchScope.hashCode() );

        return h;
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
        final StoredProcedureSearchContextOption other = ( StoredProcedureSearchContextOption ) obj;
        if ( baseObject == null )
        {
            if ( other.baseObject != null )
            {
                return false;
            }
        }
        else if ( !baseObject.equals( other.baseObject ) )
        {
            return false;
        }
        if ( searchScope == null )
        {
            if ( other.searchScope != null )
            {
                return false;
            }
        }
        else if ( !searchScope.equals( other.searchScope ) )
        {
            return false;
        }
        return true;
    }

}
