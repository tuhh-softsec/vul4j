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
import org.apache.directory.shared.ldap.exception.LdapException;
import org.apache.directory.shared.ldap.message.control.Control;
import org.apache.directory.shared.ldap.message.internal.InternalLdapResult;
import org.apache.directory.shared.ldap.message.internal.InternalSearchResponseDone;
import org.apache.directory.shared.ldap.name.DN;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * TestCases for the SearchResponseImpl class methods.
 * 
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory Project</a>
 *         $Rev: 946251 $
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrent()
public class SearchResponseDoneImplTest
{
    private static final Map<String, Control> EMPTY_CONTROL_MAP = new HashMap<String, Control>();


    /**
     * Creates and populates a SearchResponseDoneImpl stub for testing purposes.
     * 
     * @return a populated SearchResponseDoneImpl stub
     */
    private SearchResponseDoneImpl createStub()
    {
        // Construct the Search response to test with results and referrals
        SearchResponseDoneImpl response = new SearchResponseDoneImpl( 45 );
        InternalLdapResult result = response.getLdapResult();

        try
        {
            result.setMatchedDn( new DN( "dc=example,dc=com" ) );
        }
        catch ( LdapException ine )
        {
            // do nothing
        }

        result.setResultCode( ResultCodeEnum.SUCCESS );
        ReferralImpl refs = new ReferralImpl();
        refs.addLdapUrl( "ldap://someserver.com" );
        refs.addLdapUrl( "ldap://apache.org" );
        refs.addLdapUrl( "ldap://another.net" );
        result.setReferral( refs );
        return response;
    }


    /**
     * Tests for equality using the same object.
     */
    @Test
    public void testEqualsSameObj()
    {
        SearchResponseDoneImpl resp = createStub();
        assertTrue( resp.equals( resp ) );
    }


    /**
     * Tests for equality using an exact copy.
     */
    @Test
    public void testEqualsExactCopy()
    {
        SearchResponseDoneImpl resp0 = createStub();
        SearchResponseDoneImpl resp1 = createStub();
        assertTrue( resp0.equals( resp1 ) );
        assertTrue( resp1.equals( resp0 ) );
    }


    /**
     * Tests for equality using different stub implementations.
     */
    @Test
    public void testEqualsDiffImpl()
    {
        SearchResponseDoneImpl resp0 = createStub();
        InternalSearchResponseDone resp1 = new InternalSearchResponseDone()
        {
            public InternalLdapResult getLdapResult()
            {
                LdapResultImpl result = new LdapResultImpl();

                try
                {
                    result.setMatchedDn( new DN( "dc=example,dc=com" ) );
                }
                catch ( Exception e )
                {
                    // Do nothing
                }
                result.setResultCode( ResultCodeEnum.SUCCESS );
                ReferralImpl refs = new ReferralImpl();
                refs.addLdapUrl( "ldap://someserver.com" );
                refs.addLdapUrl( "ldap://apache.org" );
                refs.addLdapUrl( "ldap://another.net" );
                result.setReferral( refs );

                return result;
            }


            public MessageTypeEnum getType()
            {
                return MessageTypeEnum.SEARCH_RESULT_DONE;
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


            public void addAll( Control[] controls ) throws MessageException
            {
            }


            public boolean hasControl( String oid )
            {
                return false;
            }


            public Control getCurrentControl()
            {
                return null;
            }
        };

        assertTrue( resp0.equals( resp1 ) );
        assertFalse( resp1.equals( resp0 ) );
    }


    /**
     * Tests for equal hashCode using the same object.
     */
    @Test
    public void testHashCodeSameObj()
    {
        SearchResponseDoneImpl resp = createStub();
        assertTrue( resp.hashCode() == resp.hashCode() );
    }


    /**
     * Tests for equal hashCode using an exact copy.
     */
    @Test
    public void testHashCodeExactCopy()
    {
        SearchResponseDoneImpl resp0 = createStub();
        SearchResponseDoneImpl resp1 = createStub();
        assertTrue( resp0.hashCode() == resp1.hashCode() );
    }


    /**
     * Tests inequality when messageIds are different.
     */
    @Test
    public void testNotEqualsDiffIds()
    {
        SearchResponseDoneImpl resp0 = new SearchResponseDoneImpl( 3 );
        SearchResponseDoneImpl resp1 = new SearchResponseDoneImpl( 4 );

        assertFalse( resp0.equals( resp1 ) );
        assertFalse( resp1.equals( resp0 ) );
    }
}
