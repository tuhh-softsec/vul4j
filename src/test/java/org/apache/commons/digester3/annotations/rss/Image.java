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
@ObjectCreate( pattern = "rss/channel/image" )
public final class Image
{

    @BeanPropertySetter( pattern = "rss/channel/image/description" )
    private String description;

    @BeanPropertySetter( pattern = "rss/channel/image/width" )
    private int width;

    @BeanPropertySetter( pattern = "rss/channel/image/height" )
    private int height;

    @BeanPropertySetter( pattern = "rss/channel/image/link" )
    private String link;

    @BeanPropertySetter( pattern = "rss/channel/image/title" )
    private String title;

    @BeanPropertySetter( pattern = "rss/channel/image/url" )
    private String url;

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public int getWidth()
    {
        return width;
    }

    public void setWidth( int width )
    {
        this.width = width;
    }

    public int getHeight()
    {
        return height;
    }

    public void setHeight( int height )
    {
        this.height = height;
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

    public String getUrl()
    {
        return url;
    }

    public void setUrl( String url )
    {
        this.url = url;
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
        Image other = (Image) obj;
        if ( description == null )
        {
            if ( other.description != null )
                return false;
        }
        else if ( !description.equals( other.description ) )
            return false;
        if ( height != other.height )
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
        if ( url == null )
        {
            if ( other.url != null )
                return false;
        }
        else if ( !url.equals( other.url ) )
            return false;
        if ( width != other.width )
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "Image [description=" + description + ", height=" + height + ", link=" + link + ", title=" + title
            + ", url=" + url + ", width=" + width + "]";
    }

}
