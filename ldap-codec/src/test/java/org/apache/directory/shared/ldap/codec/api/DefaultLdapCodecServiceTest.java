/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.apache.directory.shared.ldap.codec.api;


import static org.junit.Assert.*;

import org.junit.Test;


/**
 * Tests for DefaultLdapCodecService.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DefaultLdapCodecServiceTest
{
    /**
     * Test method for {@link org.apache.directory.shared.ldap.codec.api.DefaultLdapCodecService#DefaultLdapCodecService()}.
     */
    @Test
    public void testDefaultLdapCodecService()
    {
        DefaultLdapCodecService codec = new DefaultLdapCodecService();
        assertNotNull( codec );
        codec.shutdown();
    }


//    /**
//     * Test method for {@link org.apache.directory.shared.ldap.codec.api.DefaultLdapCodecService#shutdown()}.
//     */
//    @Test
//    public void testShutdown()
//    {
//        fail( "Not yet implemented" );
//    }
//
//
//    /**
//     * Test method for {@link org.apache.directory.shared.ldap.codec.api.DefaultLdapCodecService#registerControl(org.apache.directory.shared.ldap.codec.api.ControlFactory)}.
//     */
//    @Test
//    public void testRegisterControl()
//    {
//        fail( "Not yet implemented" );
//    }
//
//
//    /**
//     * Test method for {@link org.apache.directory.shared.ldap.codec.api.DefaultLdapCodecService#registeredControls()}.
//     */
//    @Test
//    public void testRegisteredControls()
//    {
//        fail( "Not yet implemented" );
//    }
//
//
//    /**
//     * Test method for {@link org.apache.directory.shared.ldap.codec.api.DefaultLdapCodecService#registeredExtendedRequests()}.
//     */
//    @Test
//    public void testRegisteredExtendedRequests()
//    {
//        fail( "Not yet implemented" );
//    }
//
//
//    /**
//     * Test method for {@link org.apache.directory.shared.ldap.codec.api.DefaultLdapCodecService#registeredExtendedResponses()}.
//     */
//    @Test
//    public void testRegisteredExtendedResponses()
//    {
//        fail( "Not yet implemented" );
//    }
//
//
//    /**
//     * Test method for {@link org.apache.directory.shared.ldap.codec.api.DefaultLdapCodecService#registerExtendedOp(org.apache.directory.shared.ldap.codec.api.ExtendedOpFactory)}.
//     */
//    @Test
//    public void testRegisterExtendedOp()
//    {
//        fail( "Not yet implemented" );
//    }
//
//
//    /**
//     * Test method for {@link org.apache.directory.shared.ldap.codec.api.DefaultLdapCodecService#newProtocolCodecFactory(boolean)}.
//     */
//    @Test
//    public void testNewProtocolCodecFactory()
//    {
//        fail( "Not yet implemented" );
//    }
//
//
//    /**
//     * Test method for {@link org.apache.directory.shared.ldap.codec.api.DefaultLdapCodecService#newControl(java.lang.String)}.
//     */
//    @Test
//    public void testNewControlString()
//    {
//        fail( "Not yet implemented" );
//    }
//
//
//    /**
//     * Test method for {@link org.apache.directory.shared.ldap.codec.api.DefaultLdapCodecService#newControl(org.apache.directory.shared.ldap.model.message.Control)}.
//     */
//    @Test
//    public void testNewControlControl()
//    {
//        fail( "Not yet implemented" );
//    }
//
//
//    /**
//     * Test method for {@link org.apache.directory.shared.ldap.codec.api.DefaultLdapCodecService#toJndiControl(org.apache.directory.shared.ldap.model.message.Control)}.
//     */
//    @Test
//    public void testToJndiControl()
//    {
//        fail( "Not yet implemented" );
//    }
//
//
//    /**
//     * Test method for {@link org.apache.directory.shared.ldap.codec.api.DefaultLdapCodecService#fromJndiControl(javax.naming.ldap.Control)}.
//     */
//    @Test
//    public void testFromJndiControl()
//    {
//        fail( "Not yet implemented" );
//    }
//
//
//    /**
//     * Test method for {@link org.apache.directory.shared.ldap.codec.api.DefaultLdapCodecService#newMessageContainer()}.
//     */
//    @Test
//    public void testNewMessageContainer()
//    {
//        fail( "Not yet implemented" );
//    }
}
