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
 * Restricts the maximum number of attribute values allowed for a specified
 * attribute type. It is examined if the protected item is an attribute
 * value of the specified type and the permission sought is add. Values of
 * that attribute in the entry are counted without regard to context or
 * access control and as though the operation which adds the values were
 * successful. If the number of values in the attribute exceeds maxCount,
 * the ACI item is treated as not granting add access.
 */
public class MaxValueCountItem extends ProtectedItem
{
    /** The set of elements to protect */
    private final Set<MaxValueCountElem> items;


    /**
     * Creates a new instance.
     * 
     * @param items the collection of {@link MaxValueCountElem}s.
     */
    public MaxValueCountItem( Set<MaxValueCountElem> items )
    {
        this.items = Collections.unmodifiableSet( items );
    }


    /**
     * Gets an iterator of all {@link MaxValueCountElem}s.
     *
     * @return an iterator of all {@link MaxValueCountElem}s
     */
    public Iterator<MaxValueCountElem> iterator()
    {
        return items.iterator();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        int hash = 37;
        hash = hash * 17 + items.hashCode();
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

        if ( o instanceof MaxValueCountItem )
        {
            MaxValueCountItem that = ( MaxValueCountItem ) o;
            return this.items.equals( that.items );
        }

        return false;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder();

        buf.append( "maxValueCount {" );

        boolean isFirst = true;

        for ( MaxValueCountElem item : items )
        {
            if ( isFirst )
            {
                isFirst = false;
            }
            else
            {
                buf.append( ", " );
            }

            buf.append( item.toString() );
        }

        buf.append( "}" );

        return buf.toString();
    }
}
