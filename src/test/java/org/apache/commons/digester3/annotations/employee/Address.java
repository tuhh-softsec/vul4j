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
package org.apache.commons.digester3.annotations.employee;

import org.apache.commons.digester3.annotations.rules.BeanPropertySetter;
import org.apache.commons.digester3.annotations.rules.ObjectCreate;
import org.apache.commons.digester3.annotations.rules.SetProperty;
import org.apache.commons.digester3.annotations.rules.SetTop;

/**
 * @since 2.1
 */
@ObjectCreate( pattern = "employee/address" )
public class Address
{

    @BeanPropertySetter( pattern = "employee/address/city" )
    private String city;

    @BeanPropertySetter( pattern = "employee/address/state" )
    private String state;

    @BeanPropertySetter( pattern = "employee/address/street" )
    private String street;

    @SetProperty( pattern = "employee/address", attributeName = "place" )
    private String type;

    @BeanPropertySetter( pattern = "employee/address/zip-code" )
    private String zipCode;

    @SetTop( pattern = "employee/address" )
    public void setEmployee( Employee employee )
    {
        employee.addAddress( this );
    }

    public String getCity()
    {
        return this.city;
    }

    public void setCity( String city )
    {
        this.city = city;
    }

    public String getState()
    {
        return this.state;
    }

    public void setState( String state )
    {
        this.state = state;
    }

    public String getStreet()
    {
        return this.street;
    }

    public void setStreet( String street )
    {
        this.street = street;
    }

    public String getType()
    {
        return this.type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public String getZipCode()
    {
        return this.zipCode;
    }

    public void setZipCode( String zipCode )
    {
        this.zipCode = zipCode;
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
        if ( this.city == null )
        {
            if ( other.getCity() != null )
                return false;
        }
        else if ( !this.city.equals( other.getCity() ) )
            return false;
        if ( this.state == null )
        {
            if ( other.getState() != null )
                return false;
        }
        else if ( !this.state.equals( other.getState() ) )
            return false;
        if ( this.street == null )
        {
            if ( other.getStreet() != null )
                return false;
        }
        else if ( !this.street.equals( other.getStreet() ) )
            return false;
        if ( this.type == null )
        {
            if ( other.getType() != null )
                return false;
        }
        else if ( !this.type.equals( other.getType() ) )
            return false;
        if ( this.zipCode == null )
        {
            if ( other.getZipCode() != null )
                return false;
        }
        else if ( !this.zipCode.equals( other.getZipCode() ) )
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "Address [city=" + city + ", state=" + state + ", street=" + street + ", type=" + type + ", zipCode="
            + zipCode + "]";
    }

}
