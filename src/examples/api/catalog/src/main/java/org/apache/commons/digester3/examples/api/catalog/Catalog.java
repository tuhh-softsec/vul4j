package org.apache.commons.digester3.examples.api.catalog;

/*
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

import java.util.LinkedList;
import java.util.Iterator;

/**
 * See Main.java.
 */
public class Catalog
{

    private final LinkedList<Item> items = new LinkedList<Item>();

    public void addItem( Item item )
    {
        items.addLast( item );
    }

    public void print()
    {
        System.out.println( "This catalog has " + items.size() + " items" );

        for ( Iterator<Item> i = items.iterator(); i.hasNext(); )
        {
            Item item = i.next();
            item.print();
        }
    }

}
