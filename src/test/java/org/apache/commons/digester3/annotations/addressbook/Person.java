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
package org.apache.commons.digester3.annotations.addressbook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.digester3.annotations.rules.BeanPropertySetter;
import org.apache.commons.digester3.annotations.rules.CallMethod;
import org.apache.commons.digester3.annotations.rules.CallParam;
import org.apache.commons.digester3.annotations.rules.ObjectCreate;
import org.apache.commons.digester3.annotations.rules.SetNext;
import org.apache.commons.digester3.annotations.rules.SetProperty;

/**
 * @since 2.1
 */
@ObjectCreate( pattern = "address-book/person" )
public class Person
{

    private final Map<String, String> emails = new HashMap<String, String>();

    private final List<Address> addresses = new ArrayList<Address>();

    @SetProperty( pattern = "address-book/person" )
    private int id;

    @SetProperty( pattern = "address-book/person" )
    private String category;

    @BeanPropertySetter( pattern = "address-book/person/name" )
    private String name;

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

    public List<Address> getAddresses()
    {
        return addresses;
    }

    @CallMethod( pattern = "address-book/person/email" )
    public void addEmail( @CallParam( pattern = "address-book/person/email", attributeName = "type" ) String type,
                          @CallParam( pattern = "address-book/person/email" ) String address )
    {
        this.emails.put( type, address );
    }

    @SetNext( fireOnBegin = true )
    public void addAddress( Address addr )
    {
        this.addresses.add( addr );
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
        if ( addresses == null )
        {
            if ( other.addresses != null )
                return false;
        }
        else if ( !addresses.equals( other.addresses ) )
            return false;
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
        return "Person [addresses=" + addresses + ", category=" + category + ", emails=" + emails + ", id=" + id
            + ", name=" + name + "]";
    }

}
