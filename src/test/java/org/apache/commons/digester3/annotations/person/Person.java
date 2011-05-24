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
package org.apache.commons.digester3.annotations.person;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.digester3.annotations.rules.BeanPropertySetter;
import org.apache.commons.digester3.annotations.rules.CallMethod;
import org.apache.commons.digester3.annotations.rules.CallParam;
import org.apache.commons.digester3.annotations.rules.ObjectCreate;
import org.apache.commons.digester3.annotations.rules.SetProperty;

/**
 * @since 2.1
 */
@ObjectCreate( pattern = "person" )
public class Person
{

    private final Map<String, String> emails = new HashMap<String, String>();

    @SetProperty( pattern = "person" )
    private int id;

    @SetProperty( pattern = "person" )
    private String category;

    @BeanPropertySetter( pattern = "person/name" )
    private String name;

    @CallMethod( pattern = "person/email" )
    public void addEmail( @CallParam( pattern = "person/email", attributeName = "type" ) String type,
                          @CallParam( pattern = "person/email" ) String address )
    {
        this.emails.put( type, address );
    }

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory( String category )
    {
        this.category = category;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public Map<String, String> getEmails()
    {
        return emails;
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
        Person other = (Person) obj;
        if ( category == null )
        {
            if ( other.category != null )
                return false;
        }
        else if ( !category.equals( other.category ) )
            return false;
        if ( emails == null )
        {
            if ( other.emails != null )
                return false;
        }
        else if ( !emails.equals( other.emails ) )
            return false;
        if ( id != other.id )
            return false;
        if ( name == null )
        {
            if ( other.name != null )
                return false;
        }
        else if ( !name.equals( other.name ) )
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "Person [category=" + category + ", emails=" + emails + ", id=" + id + ", name=" + name + "]";
    }

}
