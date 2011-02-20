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


import org.apache.directory.shared.ldap.aci.ProtectedItem;


/**
 * Restricts the maximum number of immediate subordinates of the superior
 * entry to an entry being added or imported. It is examined if the
 * protected item is an entry, the permission sought is add or import, and
 * the immediate superior entry is in the same DSA as the entry being added
 * or imported. Immediate subordinates of the superior entry are counted
 * without regard to context or access control as though the entry addition
 * or importing were successful. If the number of subordinates exceeds
 * maxImmSub, the ACI item is treated as not granting add or import access.
 */
public class MaxImmSubItem extends ProtectedItem
{
    /** The maximum number of allowed subordinates */
    private final int value;


    /**
     * Creates a new instance.
     * 
     * @param value The maximum number of immediate subordinates
     */
    public MaxImmSubItem( int value )
    {
        this.value = value;
    }


    /**
     * Gets the maximum number of immediate subordinates.
     *
     * @return the maximum number of immediate subordinates
     */
    public int getValue()
    {
        return value;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        int hash = 37;
        hash = hash * 17 + value;
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

        if ( o instanceof MaxImmSubItem )
        {
            MaxImmSubItem that = ( MaxImmSubItem ) o;
            return this.value == that.value;
        }

        return false;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "maxImmSub " + value;
    }
}
