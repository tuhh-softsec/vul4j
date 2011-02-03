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




/**
 * A simple Subentries Control implementation. This control is described in 
 * RFC 3672 :
 *    The subentries control MAY be sent with a searchRequest to control
 *    the visibility of entries and subentries which are within scope.
 *    Non-visible entries or subentries are not returned in response to the
 *    request.
 * 
 *    The subentries control is an LDAP Control whose controlType is
 *    1.3.6.1.4.1.4203.1.10.1, criticality is TRUE or FALSE (hence absent),
 *    and controlValue contains a BER-encoded BOOLEAN indicating
 *    visibility.  A controlValue containing the value TRUE indicates that
 *    subentries are visible and normal entries are not.  A controlValue
 *    containing the value FALSE indicates that normal entries are visible
 *    and subentries are not.
 * 
 *    Note that TRUE visibility has the three octet encoding { 01 01 FF }
 *    and FALSE visibility has the three octet encoding { 01 01 00 }.
 * 
 *    The controlValue SHALL NOT be absent.
 * 
 *    In absence of this control, subentries are not visible to singleLevel
 *    and wholeSubtree scope Search requests but are visible to baseObject
 *    scope Search requests.
 * 
 *    There is no corresponding response control.
 * 
 *    This control is not appropriate for non-Search operations.
 * 
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SubentriesImpl extends AbstractControl implements Subentries
{
    private boolean visibility = false;


    /**
     * Default constructor
     */
    public SubentriesImpl()
    {
        super( OID );
    }


    /**
     * returns Tells if the Subentry values are visible or not
     */
    public boolean isVisible()
    {
        return visibility;
    }


    /**
     * @param visibility Set the visibility flag
     */
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
