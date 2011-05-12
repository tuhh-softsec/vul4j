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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.digester3.annotations.rules.ObjectCreate;
import org.apache.commons.digester3.annotations.rules.SetProperty;

/**
 * @since 2.1
 */
@ObjectCreate( pattern = "employee" )
public class Employee
{

    private final List<Address> addresses = new ArrayList<Address>();

    @SetProperty( pattern = "employee", attributeName = "name" )
    private String firstName;

    @SetProperty( pattern = "employee", attributeName = "surname" )
    private String lastName;

    public void addAddress( Address address )
    {
        this.addresses.add( address );
    }

    public String getFirstName()
    {
        return this.firstName;
    }

    public void setFirstName( String firstName )
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return this.lastName;
    }

    public void setLastName( String lastName )
    {
        this.lastName = lastName;
    }

    public List<Address> getAddresses()
    {
        return this.addresses;
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
        Employee other = (Employee) obj;
        if ( this.addresses == null )
        {
            if ( other.getAddresses() != null )
                return false;
        }
        else if ( !this.addresses.equals( other.getAddresses() ) )
            return false;
        if ( this.firstName == null )
        {
            if ( other.getFirstName() != null )
                return false;
        }
        else if ( !this.firstName.equals( other.getFirstName() ) )
            return false;
        if ( this.lastName == null )
        {
            if ( other.getLastName() != null )
                return false;
        }
        else if ( !this.lastName.equals( other.getLastName() ) )
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "Employee [addresses=" + addresses + ", firstName=" + firstName + ", lastName=" + lastName + "]";
    }

}
