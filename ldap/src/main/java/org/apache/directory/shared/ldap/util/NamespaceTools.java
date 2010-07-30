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
package org.apache.directory.shared.ldap.util;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.exception.LdapInvalidDnException;
import org.apache.directory.shared.ldap.name.DN;


/**
 * Tools dealing with common Naming operations.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class NamespaceTools
{
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    
    /**
     * Gets the attribute of a single attribute rdn or name component.
     * 
     * @param rdn the name component
     * @return the attribute name TODO the name rdn is misused rename refactor
     *         this method
     */
    public static String getRdnAttribute( String rdn )
    {
        int index = rdn.indexOf( '=' );
        return rdn.substring( 0, index );
    }


    /**
     * Gets the value of a single name component of a distinguished name.
     * 
     * @param rdn the name component to get the value from
     * @return the value of the single name component TODO the name rdn is
     *         misused rename refactor this method
     */
    public static String getRdnValue( String rdn )
    {
        int index = rdn.indexOf( '=' );
        return rdn.substring( index + 1, rdn.length() );
    }


    /**
     * Gets the relative name between an ancestor and a potential descendant.
     * Both name arguments must be normalized. The returned name is also
     * normalized.
     * 
     * @param ancestor the normalized distinguished name of the ancestor context
     * @param descendant the normalized distinguished name of the descendant context
     * @return the relative normalized name between the ancestor and the
     *         descendant contexts
     * @throws LdapInvalidDnException if the contexts are not related in the ancestual sense
     */
    public static DN getRelativeName( DN ancestor, DN descendant ) throws LdapInvalidDnException
    {
        DN rdn = descendant;
        
        if ( rdn.isChildOf( ancestor ) )
        {
            for ( int ii = 0; ii < ancestor.size(); ii++ )
            {
                rdn = rdn.remove( 0 );
            }
        }
        else
        {
            LdapInvalidDnException e = new LdapInvalidDnException( I18n.err( I18n.ERR_04417, descendant, ancestor ) );

            throw e;
        }

        return rdn;
    }


    /**
     * Gets the '+' appended components of a composite name component.
     * 
     * @param compositeNameComponent a single name component not a whole name
     * @return the components of the complex name component in order
     * @throws LdapInvalidDnException
     *             if nameComponent is invalid (starts with a +)
     */
    public static String[] getCompositeComponents( String compositeNameComponent ) throws LdapInvalidDnException
    {
        int lastIndex = compositeNameComponent.length() - 1;
        List<String> comps = new ArrayList<String>();

        for ( int ii = compositeNameComponent.length() - 1; ii >= 0; ii-- )
        {
            if ( compositeNameComponent.charAt( ii ) == '+' )
            {
                if ( ii == 0 )
                {
                    throw new LdapInvalidDnException( I18n.err( I18n.ERR_04418, compositeNameComponent ) );
                }
                
                if ( compositeNameComponent.charAt( ii - 1 ) != '\\' )
                {
                    if ( lastIndex == compositeNameComponent.length() - 1 )
                    {
                        comps.add( 0, compositeNameComponent.substring( ii + 1, lastIndex + 1 ) );
                    }
                    else
                    {
                        comps.add( 0, compositeNameComponent.substring( ii + 1, lastIndex ) );
                    }

                    lastIndex = ii;
                }
            }
            
            if ( ii == 0 )
            {
                if ( lastIndex == compositeNameComponent.length() - 1 )
                {
                    comps.add( 0, compositeNameComponent );
                }
                else
                {
                    comps.add( 0, compositeNameComponent.substring( ii, lastIndex ) );
                }

                lastIndex = 0;
            }
        }

        if ( comps.size() == 0 )
        {
            comps.add( compositeNameComponent );
        }

        return comps.toArray( EMPTY_STRING_ARRAY );
    }
}
