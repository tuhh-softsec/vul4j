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
package org.apache.directory.shared.ldap.aci.protectedItem;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.apache.directory.shared.ldap.aci.ProtectedItem;

/**
 * A base class for all items which protects attribute types (or its values)
 */
public abstract class AbstractAttributeTypeProtectedItem extends ProtectedItem
{
    protected final Set<String> attributeTypes;


    /**
     * Creates a new instance.
     * 
     * @param attributeTypes the collection of attirbute IDs
     */
    protected AbstractAttributeTypeProtectedItem( Set<String> attributeTypes )
    {
        this.attributeTypes = Collections.unmodifiableSet( attributeTypes );
    }


    /**
     * Returns an iterator of all attribute IDs.
     */
    public Iterator<String> iterator()
    {
        return attributeTypes.iterator();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        int hash = 37;
        hash = hash * 17 + attributeTypes.hashCode();
        return hash;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o == null )
        {
            return false;
        }

        if ( getClass().isAssignableFrom( o.getClass() ) )
        {
            AbstractAttributeTypeProtectedItem that = ( AbstractAttributeTypeProtectedItem ) o;
            return this.attributeTypes.equals( that.attributeTypes );
        }

        return false;
    }


    /**
     * @see Object#toString()
     */
    public String toString()
    {
        StringBuilder buf = new StringBuilder();

        buf.append( "{ " );
        boolean isFirst = true;

        for ( String attributeType : attributeTypes )
        {
            if ( isFirst )
            {
                isFirst = false;
            }
            else
            {
                buf.append( ", " );
            }

            buf.append( attributeType );
        }

        buf.append( " }" );

        return buf.toString();
    }
}
