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


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.directory.shared.ldap.codec.standalone.StandaloneLdapCodecService;
import org.apache.directory.shared.ldap.extras.controls.PasswordPolicy;
import org.apache.directory.shared.ldap.extras.extended.StoredProcedureRequest;
import org.apache.directory.shared.ldap.extras.extended.StoredProcedureRequestImpl;
import org.apache.directory.shared.ldap.model.message.Control;
import org.junit.Test;


/**
 * Tests for StandaloneLdapCodecService.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class StandaloneLdapCodecServiceTest
{   
    /**
     * Test method for {@link org.apache.directory.shared.ldap.codec.standalone.StandaloneLdapCodecService#StandaloneLdapCodecService()}.
     */
    @Test
    public void testLoadingExtras()
    {
        StandaloneLdapCodecService codec = new StandaloneLdapCodecService();
        
        assertTrue( codec.isControlRegistered( PasswordPolicy.OID ) );

        CodecControl<? extends Control> control = codec.newControl( PasswordPolicy.OID );
        assertNotNull( control );
        assertNotNull( codec );
        codec.shutdown();
    }


    /**
     * Test an extended operation.
     */
    @Test
    public void testLoadingExtendedOperation()
    {
        StandaloneLdapCodecService codec = new StandaloneLdapCodecService();
        StoredProcedureRequest req = new StoredProcedureRequestImpl();
        req.setLanguage( "Java" );
        req.setProcedure( "bogusProc" );
        
        assertNotNull( req );
        assertNotNull( codec );
        
        StoredProcedureRequest decorator = ( StoredProcedureRequest ) codec.decorate( req );
        assertNotNull( decorator );
        codec.shutdown();
    }
}
