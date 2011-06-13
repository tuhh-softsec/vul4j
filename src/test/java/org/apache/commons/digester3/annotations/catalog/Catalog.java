/* $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.digester3.annotations.catalog;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.digester3.annotations.rules.ObjectCreate;
import org.apache.commons.digester3.annotations.rules.SetNext;

/**
 * @since 2.1
 */
@ObjectCreate.List( @ObjectCreate( pattern = "catalog" ) )
public final class Catalog
{

    private final List<Item> items = new ArrayList<Item>();

    @SetNext( { AudioVisual.class, Book.class } )
    public void addItem( Item item )
    {
        this.items.add( item );
    }

    public List<Item> getItems()
    {
        return this.items;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        Catalog other = (Catalog) obj;
        if ( this.items == null )
        {
            if ( other.getItems() != null )
                return false;
        }
        else if ( !this.items.equals( other.getItems() ) )
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "Catalog [items=" + items + "]";
    }

    public void print()
    {
        System.out.println( "This catalog has " + this.items.size() + " items" );

        for ( Item item : this.items )
        {
            item.print();
        }
    }

}
