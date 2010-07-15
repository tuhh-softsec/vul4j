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
 * The contents of entries (possibly a family member) which are restricted
 * to those that have object class values that satisfy the predicate defined
 * by Refinement (see 12.3.5), together (in the case of an ancestor or other
 * family member) with the entry contents as a whole of each subordinate
 * family member entry; it does not necessarily include the information in
 * these entries.
 */
public class ClassesItem extends ProtectedItem
{
    private final ExprNode classes;


    /**
     * Creates a new instance.
     * 
     * @param classes refinement
     */
    public ClassesItem( ExprNode classes )
    {
        this.classes = classes;
    }


    public ExprNode getClasses()
    {
        return classes;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        int hash = 37;
        hash = hash * 17 + getClass().getName().hashCode();
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

        if ( o instanceof ClassesItem )
        {
            ClassesItem that = ( ClassesItem ) o;
            return this.classes.equals( that.classes );
        }

        return false;
    }


    /**
     * @see Object#toString()
     */
    public String toString()
    {
        StringBuilder buf = new StringBuilder();

        buf.append( "classes " );
        classes.printRefinementToBuffer( buf );

        return buf.toString();
    }
}

