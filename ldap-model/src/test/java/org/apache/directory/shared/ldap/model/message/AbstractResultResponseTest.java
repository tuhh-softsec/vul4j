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
package org.apache.directory.shared.ldap.model.message;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.mycila.junit.concurrent.Concurrency;
import com.mycila.junit.concurrent.ConcurrentJunitRunner;
import org.apache.directory.shared.ldap.model.exception.LdapException;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * TestCase for the methods of the AbstractResultResponse class.
 * 
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrency()
public class AbstractResultResponseTest
{
    /**
     * Tests to see the same object returns true.
     */
    @Test
    public void testEqualsSameObj()
    {
        AbstractResultResponse msg;
        msg = new AbstractResultResponse( 5, MessageTypeEnum.BIND_REQUEST )
        {
            private static final long serialVersionUID = 1L;
        };
        assertTrue( msg.equals( msg ) );
    }


    /**
     * Tests to see the same exact copy returns true.
     */
    @Test
    public void testEqualsExactCopy() throws LdapException
    {
        AbstractResultResponse msg0 = new AbstractResultResponse( 5, MessageTypeEnum.BIND_REQUEST )
        {
            private static final long serialVersionUID = 1L;
        };
        AbstractResultResponse msg1 = new AbstractResultResponse( 5, MessageTypeEnum.BIND_REQUEST )
        {
            private static final long serialVersionUID = 1L;
        };
        LdapResult r0 = msg0.getLdapResult();
        LdapResult r1 = msg1.getLdapResult();

        r0.setErrorMessage( "blah blah blah" );
        r1.setErrorMessage( "blah blah blah" );

        r0.setMatchedDn( new Dn( "dc=example,dc=com" ) );
        r1.setMatchedDn( new Dn( "dc=example,dc=com" ) );

        r0.setResultCode( ResultCodeEnum.TIME_LIMIT_EXCEEDED );
        r1.setResultCode( ResultCodeEnum.TIME_LIMIT_EXCEEDED );

        Referral refs0 = new ReferralImpl();
        refs0.addLdapUrl( "ldap://someserver.com" );
        refs0.addLdapUrl( "ldap://anotherserver.org" );

        Referral refs1 = new ReferralImpl();
        refs1.addLdapUrl( "ldap://someserver.com" );
        refs1.addLdapUrl( "ldap://anotherserver.org" );

        assertTrue( msg0.equals( msg1 ) );
        assertTrue( msg1.equals( msg0 ) );
    }


    /**
     * Tests to see the same exact copy returns true.
     */
    @Test
    public void testNotEqualsDiffResult() throws LdapException
    {
        AbstractResultResponse msg0 = new AbstractResultResponse( 5, MessageTypeEnum.BIND_REQUEST )
        {
            private static final long serialVersionUID = 1L;
        };
        AbstractResultResponse msg1 = new AbstractResultResponse( 5, MessageTypeEnum.BIND_REQUEST )
        {
            private static final long serialVersionUID = 1L;
        };
        LdapResult r0 = msg0.getLdapResult();
        LdapResult r1 = msg1.getLdapResult();

        r0.setErrorMessage( "blah blah blah" );
        r1.setErrorMessage( "blah blah blah" );

        r0.setMatchedDn( new Dn( "dc=example,dc=com" ) );
        r1.setMatchedDn( new Dn( "dc=apache,dc=org" ) );

        r0.setResultCode( ResultCodeEnum.TIME_LIMIT_EXCEEDED );
        r1.setResultCode( ResultCodeEnum.TIME_LIMIT_EXCEEDED );

        Referral refs0 = new ReferralImpl();
        refs0.addLdapUrl( "ldap://someserver.com" );
        refs0.addLdapUrl( "ldap://anotherserver.org" );

        Referral refs1 = new ReferralImpl();
        refs1.addLdapUrl( "ldap://someserver.com" );
        refs1.addLdapUrl( "ldap://anotherserver.org" );

        assertFalse( msg0.equals( msg1 ) );
        assertFalse( msg1.equals( msg0 ) );
    }


    /**
     * Tests to make sure changes in the id result in inequality.
     */
    @Test
    public void testNotEqualsDiffId()
    {
        AbstractResultResponse msg0;
        AbstractResultResponse msg1;
        msg0 = new AbstractResultResponse( 5, MessageTypeEnum.BIND_REQUEST )
        {
            private static final long serialVersionUID = 1L;
        };
        msg1 = new AbstractResultResponse( 6, MessageTypeEnum.BIND_REQUEST )
        {
            private static final long serialVersionUID = 1L;
        };
        assertFalse( msg0.equals( msg1 ) );
        assertFalse( msg1.equals( msg0 ) );
    }


    /**
     * Tests to make sure changes in the type result in inequality.
     */
    @Test
    public void testNotEqualsDiffType()
    {
        AbstractResultResponse msg0;
        AbstractResultResponse msg1;
        msg0 = new AbstractResultResponse( 5, MessageTypeEnum.BIND_REQUEST )
        {
            private static final long serialVersionUID = 1L;
        };
        msg1 = new AbstractResultResponse( 5, MessageTypeEnum.UNBIND_REQUEST )
        {
            private static final long serialVersionUID = 1L;
        };
        assertFalse( msg0.equals( msg1 ) );
        assertFalse( msg1.equals( msg0 ) );
    }


    /**
     * Tests to make sure changes in the controls result in inequality.
     */
    @Test
    public void testNotEqualsDiffControls()
    {
        AbstractResultResponse msg0;
        AbstractResultResponse msg1;

        msg0 = new AbstractResultResponse( 5, MessageTypeEnum.BIND_REQUEST )
        {
            private static final long serialVersionUID = 1L;
        };

        msg0.addControl( new Control()
        {
            private static final long serialVersionUID = 1L;


            public boolean isCritical()
            {
                return false;
            }


            public void setCritical( boolean isCritical )
            {
            }


            public String getOid()
            {
                return "0.0";
            }
        } );

        msg1 = new AbstractResultResponse( 5, MessageTypeEnum.BIND_REQUEST )
        {
            private static final long serialVersionUID = 1L;
        };
        assertFalse( msg0.equals( msg1 ) );
        assertFalse( msg1.equals( msg0 ) );
    }
}
