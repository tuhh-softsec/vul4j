/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package org.apache.directory.shared.ldap.model.message.controls;


import org.apache.directory.shared.ldap.codec.controls.BasicControlImpl;


/**
 * A simple Subentries Control implementation.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SubentriesImpl extends BasicControlImpl implements Subentries
{
    private boolean visibility = false;


    /**
     * Default constructor
     */
    public SubentriesImpl()
    {
        super( OID );
    }


    public boolean isVisible()
    {
        return visibility;
    }


    public void setVisibility( boolean visibility )
    {
        this.visibility = visibility;
    }


    /**
     * @see Object#equals(Object)
     */
    public boolean equals( Object o )
    {
        if ( !super.equals( o ) )
        {
            return false;
        }

        Subentries otherDecorator = ( Subentries ) o;

        return ( visibility == otherDecorator.isVisible() );
    }


    /**
     * Return a String representing this EntryChangeControl.
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        sb.append( "    Subentries Control\n" );
        sb.append( "        oid : " ).append( getOid() ).append( '\n' );
        sb.append( "        critical : " ).append( isCritical() ).append( '\n' );
        sb.append( "        Visibility   : '" ).append( visibility ).append( "'\n" );

        return sb.toString();
    }
}
