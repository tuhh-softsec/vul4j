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

import org.apache.commons.digester3.annotations.rules.BeanPropertySetter;
import org.apache.commons.digester3.annotations.rules.ObjectCreate;

/**
 * @since 2.1
 */
@ObjectCreate( pattern = "address-book/person/address" )
public class Address
{

    @BeanPropertySetter( pattern = "address-book/person/address/type" )
    private String type;

    @BeanPropertySetter( pattern = "address-book/person/address/street" )
    private String street;

    @BeanPropertySetter( pattern = "address-book/person/address/city" )
    private String city;

    @BeanPropertySetter( pattern = "address-book/person/address/state" )
    private String state;

    @BeanPropertySetter( pattern = "address-book/person/address/zip" )
    private String zip;

    @BeanPropertySetter( pattern = "address-book/person/address/country" )
    private String country;

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public String getStreet()
    {
        return street;
    }

    public void setStreet( String street )
    {
        this.street = street;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity( String city )
    {
        this.city = city;
    }

    public String getState()
    {
        return state;
    }

    public void setState( String state )
    {
        this.state = state;
    }

    public String getZip()
    {
        return zip;
    }

    public void setZip( String zip )
    {
        this.zip = zip;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry( String country )
    {
        this.country = country;
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
        Address other = (Address) obj;
        if ( city == null )
        {
            if ( other.city != null )
                return false;
        }
        else if ( !city.equals( other.city ) )
            return false;
        if ( country == null )
        {
            if ( other.country != null )
                return false;
        }
        else if ( !country.equals( other.country ) )
            return false;
        if ( state == null )
        {
            if ( other.state != null )
                return false;
        }
        else if ( !state.equals( other.state ) )
            return false;
        if ( street == null )
        {
            if ( other.street != null )
                return false;
        }
        else if ( !street.equals( other.street ) )
            return false;
        if ( type == null )
        {
            if ( other.type != null )
                return false;
        }
        else if ( !type.equals( other.type ) )
            return false;
        if ( zip == null )
        {
            if ( other.zip != null )
                return false;
        }
        else if ( !zip.equals( other.zip ) )
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "Address [city=" + city + ", country=" + country + ", state=" + state + ", street=" + street + ", type="
            + type + ", zip=" + zip + "]";
    }

}
