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
package org.apache.directory.shared.ldap.message;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.apache.directory.shared.ldap.codec.MessageTypeEnum;
import org.apache.directory.shared.ldap.exception.LdapException;
import org.apache.directory.shared.ldap.message.control.Control;
import org.apache.directory.shared.ldap.message.internal.InternalModifyDnRequest;
import org.apache.directory.shared.ldap.message.internal.InternalResultResponse;
import org.apache.directory.shared.ldap.name.DN;
import org.apache.directory.shared.ldap.name.RDN;
import org.junit.Test;


/**
 * TestCase for the ModifyDnRequestImpl class.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ModifyDnRequestImplTest
{
    private static final Map<String, Control> EMPTY_CONTROL_MAP = new HashMap<String, Control>();

    /**
     * Constructs a ModifyDnrequest to test.
     * 
     * @return the request
     */
    private ModifyDnRequestImpl getRequest()
    {
        // Construct the ModifyDn request to test
        ModifyDnRequestImpl request = new ModifyDnRequestImpl( 45 );
        request.setDeleteOldRdn( true );
        
        try
        {
            request.setName( new DN( "dc=admins,dc=apache,dc=org" ) );
            request.setNewRdn( new RDN( "dc=administrators" ) );
            request.setNewSuperior( new DN( "dc=groups,dc=apache,dc=org" ) );
        }
        catch ( LdapException ine )
        {
            // do nothing
        }
        
        return request;
    }


    /**
     * Tests the same object reference for equality.
     */
    @Test
    public void testEqualsSameObj()
    {
        ModifyDnRequestImpl req = new ModifyDnRequestImpl( 5 );
        assertTrue( req.equals( req ) );
    }


    /**
     * Tests for equality using exact copies.
     */
    @Test
    public void testEqualsExactCopy0()
    {
        ModifyDnRequestImpl req0 = getRequest();
        ModifyDnRequestImpl req1 = getRequest();

        assertTrue( req0.equals( req1 ) );
    }


    /**
     * Tests for equality using exact copies.
     */
    @Test
    public void testEqualsExactCopy1()
    {
        ModifyDnRequestImpl req0 = getRequest();
        req0.setNewSuperior( null );
        ModifyDnRequestImpl req1 = getRequest();
        req1.setNewSuperior( null );

        assertTrue( req0.equals( req1 ) );
    }


    /**
    * Tests the same object reference for equal hashCode
    */
   @Test
   public void testHashCodeSameObj()
   {
       ModifyDnRequestImpl req = new ModifyDnRequestImpl( 5 );
       assertTrue( req.hashCode() == req.hashCode() );
   }


   /**
    * Tests for equal hashCode using exact copies.
    */
   @Test
   public void testHashCodeExactCopy0()
   {
       ModifyDnRequestImpl req0 = getRequest();
       ModifyDnRequestImpl req1 = getRequest();

       assertTrue( req0.hashCode() == req1.hashCode() );
   }


   /**
    * Tests for equal hashCode using exact copies.
    */
   @Test
   public void testHashCodeExactCopy1()
   {
       ModifyDnRequestImpl req0 = getRequest();
       req0.setNewSuperior( null );
       ModifyDnRequestImpl req1 = getRequest();
       req1.setNewSuperior( null );

       assertTrue( req0.hashCode() == req1.hashCode() );
   }


    /**
     * Test for inequality when only the IDs are different.
     */
    @Test
    public void testNotEqualDiffId()
    {
        ModifyDnRequestImpl req0 = new ModifyDnRequestImpl( 4 );
        ModifyDnRequestImpl req1 = new ModifyDnRequestImpl( 5 );

        assertFalse( req0.equals( req1 ) );
    }


    /**
     * Test for inequality when only the DN names are different.
     */
    @Test
    public void testNotEqualDiffName() throws LdapException
    {
        ModifyDnRequestImpl req0 = getRequest();
        req0.setName( new DN( "cn=admin,dc=example,dc=com" ) );

        ModifyDnRequestImpl req1 = getRequest();
        req1.setName( new DN( "cn=admin,dc=apache,dc=org" ) );

        assertFalse( req0.equals( req1 ) );
    }


    /**
     * Test for inequality when only the newSuperior DNs are different.
     */
    @Test
    public void testNotEqualDiffNewSuperior() throws LdapException
    {
        ModifyDnRequestImpl req0 = getRequest();
        req0.setNewSuperior( new DN( "cn=admin,dc=example,dc=com" ) );

        ModifyDnRequestImpl req1 = getRequest();
        req1.setNewSuperior( new DN( "cn=admin,dc=apache,dc=org" ) );

        assertFalse( req0.equals( req1 ) );
    }


    /**
     * Test for inequality when only the delete old Rdn properties is different.
     */
    @Test
    public void testNotEqualDiffDeleteOldRdn()
    {
        ModifyDnRequestImpl req0 = getRequest();
        req0.setDeleteOldRdn( true );

        ModifyDnRequestImpl req1 = getRequest();
        req1.setDeleteOldRdn( false );

        assertFalse( req0.equals( req1 ) );
    }


    /**
     * Test for inequality when only the new Rdn properties are different.
     */
    @Test
    public void testNotEqualDiffNewRdn() throws LdapException
    {
        ModifyDnRequestImpl req0 = getRequest();
        req0.setNewRdn( new RDN( "cn=admin0" ) );

        ModifyDnRequestImpl req1 = getRequest();
        req1.setNewRdn( new RDN( "cn=admin1" ) );

        assertFalse( req0.equals( req1 ) );
        assertFalse( req1.equals( req0 ) );
    }


    /**
     * Tests for equality even when another BindRequest implementation is used.
     */
    @Test
    public void testEqualsDiffImpl()
    {
        InternalModifyDnRequest req0 = new InternalModifyDnRequest()
        {
            public DN getName()
            {
                try
                {
                    return new DN( "dc=admins,dc=apache,dc=org" );
                }
                catch ( LdapException ine )
                {
                    // do nothing
                    return null;
                }
            }


            public void setName( DN name )
            {
            }


            public RDN getNewRdn()
            {
                try
                {
                    return new RDN( "dc=administrators" );
                }
                catch ( LdapException ine )
                {
                    // do nothing
                    return null;
                }
            }


            public void setNewRdn( RDN newRdn )
            {
            }


            public boolean getDeleteOldRdn()
            {
                return true;
            }


            public void setDeleteOldRdn( boolean deleteOldRdn )
            {
            }


            public DN getNewSuperior()
            {
                try
                {
                    return new DN( "dc=groups,dc=apache,dc=org" );
                }
                catch ( LdapException ine )
                {
                    // do nothing
                    return null;
                }
            }


            public void setNewSuperior( DN newSuperior )
            {
            }


            public boolean isMove()
            {
                return false;
            }


            public MessageTypeEnum getResponseType()
            {
                return MessageTypeEnum.MODIFYDN_RESPONSE;
            }


            public boolean hasResponse()
            {
                return true;
            }


            public MessageTypeEnum getType()
            {
                return MessageTypeEnum.MODIFYDN_REQUEST;
            }


            public Map<String, Control> getControls()
            {
                return EMPTY_CONTROL_MAP;
            }


            public void add( Control a_control ) throws MessageException
            {
            }


            public void remove( Control a_control ) throws MessageException
            {
            }


            public int getMessageId()
            {
                return 45;
            }


            public Object get( Object a_key )
            {
                return null;
            }


            public Object put( Object a_key, Object a_value )
            {
                return null;
            }


            public void abandon()
            {
            }


            public boolean isAbandoned()
            {
                return false;
            }


            public void addAbandonListener( AbandonListener listener )
            {
            }


            public InternalResultResponse getResultResponse()
            {
                return null;
            }


            public void addAll( Control[] controls ) throws MessageException
            {
            }


            public boolean hasControl( String oid )
            {
                return false;
            }
        };

        ModifyDnRequestImpl req1 = getRequest();
        assertTrue( req1.equals( req0 ) );
    }
}
