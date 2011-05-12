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
package org.apache.commons.digester3.annotations.rss;

import org.apache.commons.digester3.annotations.rules.BeanPropertySetter;
import org.apache.commons.digester3.annotations.rules.ObjectCreate;

/**
 * @since 2.1
 */
@ObjectCreate( pattern = "rss/channel/item" )
public final class Item
{

    @BeanPropertySetter( pattern = "rss/channel/item/description" )
    private String description;

    @BeanPropertySetter( pattern = "rss/channel/item/link" )
    private String link;

    @BeanPropertySetter( pattern = "rss/channel/item/title" )
    private String title;

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public String getLink()
    {
        return link;
    }

    public void setLink( String link )
    {
        this.link = link;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle( String title )
    {
        this.title = title;
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
        Item other = (Item) obj;
        if ( description == null )
        {
            if ( other.description != null )
                return false;
        }
        else if ( !description.equals( other.description ) )
            return false;
        if ( link == null )
        {
            if ( other.link != null )
                return false;
        }
        else if ( !link.equals( other.link ) )
            return false;
        if ( title == null )
        {
            if ( other.title != null )
                return false;
        }
        else if ( !title.equals( other.title ) )
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "Item [description=" + description + ", link=" + link + ", title=" + title + "]";
    }

}
