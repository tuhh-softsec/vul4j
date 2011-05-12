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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.digester3.annotations.rules.BeanPropertySetter;
import org.apache.commons.digester3.annotations.rules.ObjectCreate;
import org.apache.commons.digester3.annotations.rules.SetNext;

/**
 * @since 2.1
 */
@ObjectCreate( pattern = "rss/channel" )
public final class Channel
{

    private final List<Item> items = new ArrayList<Item>();

    @BeanPropertySetter( pattern = "rss/channel/title" )
    private String title;

    @BeanPropertySetter( pattern = "rss/channel/link" )
    private String link;

    @BeanPropertySetter( pattern = "rss/channel/description" )
    private String description;

    @BeanPropertySetter( pattern = "rss/channel/language" )
    private String language;

    private Image image;

    public String getTitle()
    {
        return title;
    }

    public void setTitle( String title )
    {
        this.title = title;
    }

    public String getLink()
    {
        return link;
    }

    public void setLink( String link )
    {
        this.link = link;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public String getLanguage()
    {
        return language;
    }

    public void setLanguage( String language )
    {
        this.language = language;
    }

    public List<Item> getItems()
    {
        return items;
    }

    public Image getImage()
    {
        return image;
    }

    @SetNext
    public void setImage( Image image )
    {
        this.image = image;
    }

    @SetNext
    public void addItem( Item item )
    {
        this.items.add( item );
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
        Channel other = (Channel) obj;
        if ( description == null )
        {
            if ( other.description != null )
                return false;
        }
        else if ( !description.equals( other.description ) )
            return false;
        if ( image == null )
        {
            if ( other.image != null )
                return false;
        }
        else if ( !image.equals( other.image ) )
            return false;
        if ( items == null )
        {
            if ( other.items != null )
                return false;
        }
        else if ( !items.equals( other.items ) )
            return false;
        if ( language == null )
        {
            if ( other.language != null )
                return false;
        }
        else if ( !language.equals( other.language ) )
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
        return "Channel [description=" + description + ", image=" + image + ", items=" + items + ", language="
            + language + ", link=" + link + ", title=" + title + "]";
    }

}
