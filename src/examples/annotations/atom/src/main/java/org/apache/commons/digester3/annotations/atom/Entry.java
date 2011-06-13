package org.apache.commons.digester3.annotations.atom;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.net.URL;
import java.util.Date;

import org.apache.commons.digester3.annotations.rules.BeanPropertySetter;
import org.apache.commons.digester3.annotations.rules.ObjectCreate;
import org.apache.commons.digester3.annotations.rules.SetProperty;

@ObjectCreate( pattern = "feed/entry" )
public final class Entry
{

    @BeanPropertySetter( pattern = "feed/entry/title" )
    private String title;

    @SetProperty( pattern = "feed/entry/link", attributeName = "href" )
    private URL link;

    @BeanPropertySetter( pattern = "feed/entry/updated" )
    private Date updated;

    @BeanPropertySetter( pattern = "feed/entry/id" )
    private String id;

    @BeanPropertySetter( pattern = "feed/entry/content" )
    private String content;

    public String getTitle()
    {
        return title;
    }

    public void setTitle( String title )
    {
        this.title = title;
    }

    public URL getLink()
    {
        return link;
    }

    public void setLink( URL link )
    {
        this.link = link;
    }

    public Date getUpdated()
    {
        return updated;
    }

    public void setUpdated( Date updated )
    {
        this.updated = updated;
    }

    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent( String content )
    {
        this.content = content;
    }

    @Override
    public String toString()
    {
        return "\n    Entry [title=" + title + ", link=" + link + ", updated=" + updated + ", id=" + id + ", content="
            + content + "]\n";
    }

}
