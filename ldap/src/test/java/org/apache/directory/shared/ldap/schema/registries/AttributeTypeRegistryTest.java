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
package org.apache.directory.shared.ldap.schema.registries;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.directory.shared.ldap.schema.AttributeType;
import org.junit.Test;

/**
 * Test the AttributeTypeRegistry class
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class AttributeTypeRegistryTest
{
    @Test
    public void testClone() throws Exception
    {
        OidRegistry oidRegistry = new OidRegistry();
        AttributeTypeRegistry atRegistry = new AttributeTypeRegistry( oidRegistry );
        AttributeType at0 = new AttributeType( "1.1" );
        at0.addName( "t", "test", "Test", "T" );
        
        atRegistry.register( at0 );
        
        AttributeType at1 = new AttributeType( "1.2" );
        at1.addName( "u", "unit", "Unit", "U" );

        atRegistry.register( at1 );
        
        // Clone the ATRegistry
        AttributeTypeRegistry clone = atRegistry.clone();
        
        assertEquals( at0, clone.lookup( "1.1" ) );
        assertEquals( at1, clone.lookup( "1.2" ) );
        
        atRegistry.unregister( "1.1" );
        assertFalse( atRegistry.contains( "1.1" ) );
        assertTrue( clone.contains( "1.1" ) );
        
        AttributeType at = atRegistry.lookup( "1.2" );
        at.setOid( "2.2" );
        
        at = atRegistry.lookup( "1.2" );
        assertEquals( "2.2", at.getOid() );

        at = clone.lookup( "1.2" );
        assertEquals( "1.2", at.getOid() );
    }
}
