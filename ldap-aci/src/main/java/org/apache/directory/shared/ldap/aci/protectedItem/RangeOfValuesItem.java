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
import org.apache.directory.shared.ldap.filter.ExprNode;

/**
 * Any attribute value which matches the specified filter, i.e. for which
 * the specified filter evaluated on that attribute value would return TRUE.
 */
public class RangeOfValuesItem extends ProtectedItem
{
    private final ExprNode refinement;


    /**
     * Creates a new instance.
     * 
     * @param filter the expression
     */
    public RangeOfValuesItem( ExprNode refinement )
    {
        if ( refinement == null )
        {
            throw new IllegalArgumentException( "refinement" );
        }

        this.refinement = refinement;
    }


    /**
     * Returns the expression.
     */
    public ExprNode getRefinement()
    {
        return refinement;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        int hash = 37;
        hash = hash * 17 + refinement.hashCode();
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

        if ( o instanceof RangeOfValuesItem )
        {
            RangeOfValuesItem that = ( RangeOfValuesItem ) o;
            return this.refinement.equals( that.refinement );
        }

        return false;
    }


    /**
     * @see Object#toString()
     */
    public String toString()
    {
        StringBuilder buf = new StringBuilder();

        buf.append( "rangeOfValues " );
        buf.append( refinement.toString() );

        return buf.toString();
    }
}
