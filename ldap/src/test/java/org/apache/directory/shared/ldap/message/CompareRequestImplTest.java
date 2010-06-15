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

import org.apache.directory.junit.tools.Concurrent;
import org.apache.directory.junit.tools.ConcurrentJunitRunner;
import org.apache.directory.shared.ldap.codec.MessageTypeEnum;
import org.apache.directory.shared.ldap.entry.Value;
import org.apache.directory.shared.ldap.exception.LdapException;
import org.apache.directory.shared.ldap.message.control.Control;
import org.apache.directory.shared.ldap.message.internal.InternalCompareRequest;
import org.apache.directory.shared.ldap.message.internal.InternalResultResponse;
import org.apache.directory.shared.ldap.name.DN;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * TestCase for the CompareRequestImpl class.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrent()
public class CompareRequestImplTest
{
    private static final Map<String, Control> EMPTY_CONTROL_MAP = new HashMap<String, Control>();

    /**
     * Tests the same object reference for equality.
     */
    @Test
    public void testEqualsSameObj()
    {
        CompareRequestImpl req = new CompareRequestImpl( 5 );
        assertTrue( req.equals( req ) );
    }


    /**
     * Tests for equality using exact copies.
     */
    @Test
    public void testEqualsExactCopy() throws LdapException
    {
        CompareRequestImpl req0 = new CompareRequestImpl( 5 );
        req0.setName( new DN( "cn=admin,dc=example,dc=com" ) );
        req0.setAttributeId( "objectClass" );
        req0.setAssertionValue( "top" );

        CompareRequestImpl req1 = new CompareRequestImpl( 5 );
        req1.setName( new DN( "cn=admin,dc=example,dc=com" ) );
        req1.setAttributeId( "objectClass" );
        req1.setAssertionValue( "top" );

        assertTrue( req0.equals( req1 ) );
        assertTrue( req1.equals( req0 ) );
    }


    /**
     * Tests the same object reference for equal hashCode.
     */
    @Test
    public void testHashCodeSameObj()
    {
        CompareRequestImpl req = new CompareRequestImpl( 5 );
        assertTrue( req.hashCode() == req.hashCode() );
    }


    /**
     * Tests for equal hashCode using exact copies.
     */
    @Test
    public void testHashCodeExactCopy() throws LdapException
    {
        CompareRequestImpl req0 = new CompareRequestImpl( 5 );
        req0.setName( new DN( "cn=admin,dc=example,dc=com" ) );
        req0.setAttributeId( "objectClass" );
        req0.setAssertionValue( "top" );

        CompareRequestImpl req1 = new CompareRequestImpl( 5 );
        req1.setName( new DN( "cn=admin,dc=example,dc=com" ) );
        req1.setAttributeId( "objectClass" );
        req1.setAssertionValue( "top" );

        assertTrue( req0.hashCode() == req1.hashCode() );
    }


    /**
     * Test for inequality when only the IDs are different.
     */
    @Test
    public void testNotEqualDiffId() throws LdapException
    {
        CompareRequestImpl req0 = new CompareRequestImpl( 7 );
        req0.setName( new DN( "cn=admin,dc=example,dc=com" ) );

        CompareRequestImpl req1 = new CompareRequestImpl( 5 );
        req1.setName( new DN( "cn=admin,dc=example,dc=com" ) );

        assertFalse( req0.equals( req1 ) );
        assertFalse( req1.equals( req0 ) );
    }


    /**
     * Test for inequality when only the attributeIds are different.
     */
    @Test
    public void testNotEqualDiffAttributeIds() throws LdapException
    {
        CompareRequestImpl req0 = new CompareRequestImpl( 5 );
        req0.setName( new DN( "cn=admin,dc=apache,dc=org" ) );
        req0.setAttributeId( "dc" );
        req0.setAssertionValue( "apache.org" );

        CompareRequestImpl req1 = new CompareRequestImpl( 5 );
        req1.setName( new DN( "cn=admin,dc=apache,dc=org" ) );
        req1.setAttributeId( "nisDomain" );
        req1.setAssertionValue( "apache.org" );

        assertFalse( req0.equals( req1 ) );
        assertFalse( req1.equals( req0 ) );
    }


    /**
     * Test for inequality when only the Assertion values are different.
     */
    @Test
    public void testNotEqualDiffValue() throws LdapException
    {
        CompareRequestImpl req0 = new CompareRequestImpl( 5 );
        req0.setName( new DN( "cn=admin,dc=apache,dc=org" ) );
        req0.setAttributeId( "dc" );
        req0.setAssertionValue( "apache.org" );

        CompareRequestImpl req1 = new CompareRequestImpl( 5 );
        req1.setName( new DN( "cn=admin,dc=apache,dc=org" ) );
        req1.setAttributeId( "dc" );
        req1.setAssertionValue( "nagoya.apache.org" );

        assertFalse( req0.equals( req1 ) );
        assertFalse( req1.equals( req0 ) );
    }


    /**
     * Tests for equality even when another CompareRequest implementation is
     * used.
     */
    @Test
    public void testEqualsDiffImpl()
    {
        InternalCompareRequest req0 = new InternalCompareRequest()
        {
            public Value<?> getAssertionValue()
            {
                return null;
            }


            public void setAssertionValue( String value )
            {

            }


            public void setAssertionValue( byte[] value )
            {

            }


            public String getAttributeId()
            {
                return null;
            }


            public void setAttributeId( String attrId )
            {

            }


            public DN getName()
            {
                return null;
            }


            public void setName( DN name )
            {
            }


            public MessageTypeEnum getResponseType()
            {
                return MessageTypeEnum.COMPARE_RESPONSE;
            }


            public boolean hasResponse()
            {
                return true;
            }


            public MessageTypeEnum getType()
            {
                return MessageTypeEnum.COMPARE_REQUEST;
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
                return 5;
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

        CompareRequestImpl req1 = new CompareRequestImpl( 5 );
        assertTrue( req1.equals( req0 ) );
        assertFalse( req0.equals( req1 ) );
    }
}
