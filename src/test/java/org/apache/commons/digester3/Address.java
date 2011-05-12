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

package org.apache.commons.digester3;

/**
 * Bean for Digester testing.
 */

public class Address
{

    public Address()
    {
        this( "My Street", "My City", "US", "MyZip" );
    }

    public Address( String street, String city, String state, String zipCode )
    {
        super();
        setStreet( street );
        setCity( city );
        setState( state );
        setZipCode( zipCode );
    }

    private String city = null;

    public String getCity()
    {
        return ( this.city );
    }

    public void setCity( String city )
    {
        this.city = city;
    }

    private String state = null;

    public String getState()
    {
        return ( this.state );
    }

    public void setState( String state )
    {
        this.state = state;
    }

    private String street = null;

    public String getStreet()
    {
        return ( this.street );
    }

    public void setStreet( String street )
    {
        this.street = street;
    }

    private String type = null;

    public String getType()
    {
        return ( this.type );
    }

    public void setType( String type )
    {
        this.type = type;
    }

    private String zipCode = null;

    public String getZipCode()
    {
        return ( this.zipCode );
    }

    public void setZipCode( String zipCode )
    {
        this.zipCode = zipCode;
    }

    public void setEmployee( Employee employee )
    {
        employee.addAddress( this );
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder( "Address[" );
        sb.append( "street=" );
        sb.append( street );
        sb.append( ", city=" );
        sb.append( city );
        sb.append( ", state=" );
        sb.append( state );
        sb.append( ", zipCode=" );
        sb.append( zipCode );
        sb.append( "]" );
        return ( sb.toString() );
    }

}
