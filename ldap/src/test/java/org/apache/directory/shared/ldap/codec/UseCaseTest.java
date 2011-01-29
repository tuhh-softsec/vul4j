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
package org.apache.directory.shared.ldap.codec;


import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 * Tests various use cases of a codec API.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class UseCaseTest
{
    @Test
    public void testCodecControlUsage()
    {
        TestLdapCodecService codec = new TestLdapCodecService();
        
        codec.registerControl( new TestControlFactory() );
        
        ITestControl control = codec.newControl( TestControl.class );
        control.setFoo( 24 );
        
        ITestCodecControl codecControl = codec.newCodecControl( TestCodecControl.class );
        codecControl.setFoo( 12 );
        
        assertEquals( 24, control.getFoo() );
        assertEquals( 12, codecControl.getFoo() );
        assertEquals( 12, codecControl.getDecorated().getFoo() );
    }
}
