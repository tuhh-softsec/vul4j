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
package org.apache.directory.shared.ldap.subtree;

import org.apache.directory.shared.ldap.schema.ObjectClass;

/**
 * A class holding a n ITEM refinement, as defined in RFC 3672
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ItemRefinement implements Refinement
{
    /** The item */
    private ObjectClass item;
    
    
    /**
     * Creates a new instance of ItemRefinement.
     *
     * @param item The ObjectClass associated with this refinement
     */
    public ItemRefinement( ObjectClass item )
    {
        this.item = item;
    }
    
    
    /**
     * @return the item
     */
    public ObjectClass getItem()
    {
        return item;
    }


    /**
     * @param item the item to set
     */
    public void setItem( ObjectClass item )
    {
        this.item = item;
    }


    /**
     * @see Object#toString()
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        
        sb.append( "item : " );
        sb.append( item.getName() );
     
        return sb.toString();
    }
}
