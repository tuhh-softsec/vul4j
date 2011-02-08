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

import java.util.Iterator;

import org.apache.directory.shared.ldap.extras.controls.PasswordPolicy;
import org.apache.directory.shared.ldap.model.message.Control;
import org.junit.Test;


/**
 * Tests for DefaultLdapCodecService.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DefaultLdapCodecServiceTest
{   
    /**
     * Test method for {@link DefaultLdapCodecService#DefaultLdapCodecService()}.
     */
    @Test
    public void testLoadingExtras()
    {
        System.out.println( "Property name = " + DefaultLdapCodecService.PLUGIN_DIRECTORY_PROPERTY );
        System.out.println( "Property value = " + System.getProperty( DefaultLdapCodecService.PLUGIN_DIRECTORY_PROPERTY ) );
        
        DefaultLdapCodecService codec = new DefaultLdapCodecService();
        Iterator<String> oids = codec.registeredControls();
        while ( oids.hasNext() )
        {
            System.out.println( "Registered OID = " + oids.next() );
        }
        
        assertTrue( codec.isControlRegistered( PasswordPolicy.OID ) );

        CodecControl<? extends Control> control = codec.newControl( PasswordPolicy.OID );
        assertNotNull( control );
        System.out.println( control );
        assertNotNull( codec );
        codec.shutdown();
    }
}
