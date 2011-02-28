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
package org.apache.directory.shared.ldap.codec.osgi;


import org.apache.directory.shared.ldap.codec.api.LdapCodecServiceFactory;
import org.apache.directory.shared.ldap.codec.api.LdapEncoder;
import org.apache.directory.shared.ldap.codec.osgi.DefaultLdapCodecService;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.junit.AfterClass;
import org.junit.BeforeClass;


/**
 * Initialize the Codec service. This can later be removed.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class AbstractCodecServiceTest
{
    protected static DefaultLdapCodecService codec;

    /** The encoder instance */
    protected static LdapEncoder encoder;


    /**
     * Initialize the codec service
     */
    @BeforeClass
    public static void setupLdapCodecService()
    {
        codec = new DefaultLdapCodecService();

        codec.registerProtocolCodecFactory( new ProtocolCodecFactory()
        {
            public ProtocolEncoder getEncoder( IoSession session ) throws Exception
            {
                return null;
            }
            
            public ProtocolDecoder getDecoder( IoSession session ) throws Exception
            {
                return null;
            }
        });
        
        
        if ( LdapCodecServiceFactory.isInitialized() == false )
        {
            LdapCodecServiceFactory.initialize( codec );
        }
        encoder = new LdapEncoder( codec );
    }


    /**
     * Shutdown the codec service
     */
    @AfterClass
    public static void tearDownLdapCodecService()
    {
        codec = null;
        encoder = null;
    }
}
