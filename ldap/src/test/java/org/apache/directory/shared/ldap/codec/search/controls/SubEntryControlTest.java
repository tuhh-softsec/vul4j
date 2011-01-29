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
package org.apache.directory.shared.ldap.codec.search.controls;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.ByteBuffer;

import org.apache.directory.junit.tools.Concurrent;
import org.apache.directory.junit.tools.ConcurrentJunitRunner;
import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.EncoderException;
import org.apache.directory.shared.ldap.model.message.controls.Subentries;
import org.apache.directory.shared.ldap.codec.search.controls.subentries.SubentriesDecorator;
import org.apache.directory.shared.ldap.codec.search.controls.subentries.SubentriesContainer;
import org.apache.directory.shared.util.Strings;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test the SubEntryControlTest codec
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrent()
public class SubEntryControlTest
{
    /**
     * Test the decoding of a SubEntryControl with a true visibility
     */
    @Test
    public void testDecodeSubEntryVisibilityTrue()
    {
        ByteBuffer bb = ByteBuffer.allocate( 0x03 );
        bb.put( new byte[]
            { 
              0x01, 0x01, ( byte ) 0xFF // Visibility ::= BOOLEAN
            } );
        bb.flip();

        SubentriesContainer container = new SubentriesContainer();
        SubentriesDecorator decorator = container.getSubentriesControl();
        
        try
        {
            decorator.decode( bb.array() );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }

        assertTrue( ( ( Subentries ) decorator.getDecorated() ).isVisible() );
        // test encoding
        try
        {
            ByteBuffer buffer = ByteBuffer.allocate( 0x20 );
            buffer.put( new byte[]
                { 
                0x30, 0x1E,                            // Control
                  0x04, 0x17,                          // OID (Subentries)
                    '1', '.', '3', '.', '6', '.', '1', '.', 
                    '4', '.', '1', '.', '4', '2', '0', '3', 
                    '.', '1', '.', '1', '0', '.', '1',
                  0x04, 0x03,
                    0x01, 0x01, (byte)0xFF // Visibility ::= BOOLEAN
                } );

            buffer.flip();

            bb = decorator.encode( ByteBuffer.allocate( decorator.computeLength() ) );
            String expected = Strings.dumpBytes(buffer.array());
            String decoded = Strings.dumpBytes(bb.array());
            assertEquals( expected, decoded );
        }
        catch( EncoderException e )
        {
            fail( e.getMessage() );
        }
    }


    /**
     * Test the decoding of a SubEntryControl with a false visibility
     */
    @Test
    public void testDecodeSubEntryVisibilityFalse()
    {
        ByteBuffer bb = ByteBuffer.allocate( 0x03 );
        bb.put( new byte[]
            { 
              0x01, 0x01, 0x00 // Visibility ::= BOOLEAN
            } );
        bb.flip();

        SubentriesContainer container = new SubentriesContainer();
        SubentriesDecorator decorator = container.getSubentriesControl();
        
        try
        {
            decorator.decode( bb.array() );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }

        assertFalse( ( ( Subentries ) decorator.getDecorated() ).isVisible() );
        
        // test encoding
        try
        {
            ByteBuffer buffer = ByteBuffer.allocate( 0x20 );
            buffer.put( new byte[]
                { 
                0x30, 0x1E,                            // Control
                  0x04, 0x17,                          // OID (Subentries)
                    '1', '.', '3', '.', '6', '.', '1', '.', 
                    '4', '.', '1', '.', '4', '2', '0', '3', 
                    '.', '1', '.', '1', '0', '.', '1',
                  0x04, 0x03,
                    0x01, 0x01, 0x00 // Visibility ::= BOOLEAN
                } );

            buffer.flip();

            bb = decorator.encode( ByteBuffer.allocate( decorator.computeLength() ) );
            String expected = Strings.dumpBytes(buffer.array());
            String decoded = Strings.dumpBytes(bb.array());
            assertEquals( expected, decoded );
        }
        catch( EncoderException e )
        {
            fail( e.getMessage() );
        }
    }


    /**
     * Test the decoding of a SubEntryControl with an empty visibility
     */
    @Test
    public void testDecodeSubEntryEmptyVisibility()
    {
        ByteBuffer bb = ByteBuffer.allocate( 0x02 );

        bb.put( new byte[]
            { 
              0x01, 0x00 // Visibility ::= BOOLEAN
            } );

        bb.flip();

        // Allocate a LdapMessage Container
        SubentriesContainer container = new SubentriesContainer();
        SubentriesDecorator decorator = container.getSubentriesControl();
        
        try
        {
            decorator.decode( bb.array() );
            fail( "We should never reach this point !!!" );
        }
        catch ( DecoderException de )
        {
            assertTrue( true );
        }
    }


    /**
     * Test the decoding of a bad SubEntryControl
     */
    @Test
    public void testDecodeSubEntryBad()
    {
        ByteBuffer bb = ByteBuffer.allocate( 0x03 );

        bb.put( new byte[]
            { 
              0x02, 0x01, 0x01 // Visibility ::= BOOLEAN
            } );

        bb.flip();

        // Allocate a LdapMessage Container
        SubentriesContainer container = new SubentriesContainer();
        SubentriesDecorator decorator = container.getSubentriesControl();
        
        try
        {
            decorator.decode( bb.array() );
            fail( "We should never reach this point !!!" );
        }
        catch ( DecoderException de )
        {
            assertTrue( true );
        }
    }
}
