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
package org.apache.directory.shared.ldap.schema.comparators;


import org.apache.directory.shared.ldap.schema.LdapComparator;
import org.apache.directory.shared.ldap.util.StringTools;


/**
 * A comparator that compares the objectClass type with values: AUXILIARY,
 * ABSTRACT, and STRUCTURAL.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ObjectClassTypeComparator<T> extends LdapComparator<T> 
{
    private static final long serialVersionUID = 1L;

    
    public ObjectClassTypeComparator( String oid )
    {
        super( oid );
    }
    
    public int compare( T o1, T o2 )
    {
        String s1 = getString( o1 );
        String s2 = getString( o2 );
        
        if ( s1 == null && s2 == null )
        {
            return 0;
        }
        
        if ( s1 == null )
        {
            return -1;
        }
        
        if ( s2 == null )
        {
            return 1;
        }
        
        return s1.compareTo( s2 );
    }
    
    
    String getString( T obj )
    {
        String strValue;

        if ( obj == null )
        {
            return null;
        }
        
        if ( obj instanceof String )
        {
            strValue = ( String ) obj;
        }
        else if ( obj instanceof byte[] )
        {
            strValue = StringTools.utf8ToString( ( byte[] ) obj ); 
        }
        else
        {
            strValue = obj.toString();
        }

        return strValue;
    }
}
