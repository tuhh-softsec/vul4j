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


import org.apache.directory.shared.ldap.codec.LdapEncoder;
import org.apache.directory.shared.ldap.codec.standalone.StandaloneLdapCodecService;
import org.junit.AfterClass;
import org.junit.BeforeClass;


/**
 * Initialize the Codec service
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class AbstractCodecServiceTest
{
    /** The codec service */
    protected static StandaloneLdapCodecService codec;

    /** The encoder instance */
    protected static LdapEncoder encoder;

    
    /**
     * Initialize the codec service
     */
    @BeforeClass
    public static void setupLdapCodecService()
    {
        codec = new StandaloneLdapCodecService();
        encoder = new LdapEncoder( codec );
    }


    /**
     * Shutdown the codec service
     */
    @AfterClass
    public static void tearDownLdapCodecService()
    {
        codec.shutdown();
        codec = null;
        encoder = null;
    }
}
