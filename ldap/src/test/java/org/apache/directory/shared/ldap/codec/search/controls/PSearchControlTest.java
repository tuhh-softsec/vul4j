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
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;

import org.apache.directory.junit.tools.Concurrent;
import org.apache.directory.junit.tools.ConcurrentJunitRunner;
import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.ldap.codec.DefaultLdapCodecService;
import org.apache.directory.shared.ldap.codec.api.ILdapCodecService;
import org.apache.directory.shared.ldap.codec.search.controls.persistentSearch.PersistentSearchDecorator;
import org.apache.directory.shared.ldap.model.message.controls.ChangeType;
import org.apache.directory.shared.ldap.model.message.controls.PersistentSearch;
import org.apache.directory.shared.util.Strings;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test the PSearchControlTest codec
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrent()
public class PSearchControlTest
{
    private ILdapCodecService codec = new DefaultLdapCodecService();

    /**
     * Test encoding of a PSearchControl.
     * @throws Exception on error
     */
    @Test
    public void testEncodePSearchControl() throws Exception
    {
        ByteBuffer bb = ByteBuffer.allocate( 0x0B );
        bb.put( new byte[]
            { 
                0x30, 0x09,           // PersistentSearch ::= SEQUENCE {
                  0x02, 0x01, 0x01,   // changeTypes INTEGER,
                  0x01, 0x01, 0x00,   // changesOnly BOOLEAN,
                  0x01, 0x01, 0x00    // returnECs BOOLEAN
            } );

        String expected = Strings.dumpBytes(bb.array());
        bb.flip();

        PersistentSearchDecorator decorator = new PersistentSearchDecorator( codec );
        PersistentSearch ctrl = ( PersistentSearch ) decorator.getDecorated();
        ctrl.setChangesOnly( false );
        ctrl.setReturnECs( false );
        ctrl.setChangeTypes( 1 );
        bb = decorator.encode(ByteBuffer.allocate( decorator.computeLength() ) );
        String decoded = Strings.dumpBytes(bb.array());
        assertEquals( expected, decoded );
    }

    /**
     * Test the decoding of a PSearchControl with combined changes types
     */
    @Test
    public void testDecodeModifyDNRequestSuccessChangeTypesAddModDN() throws Exception
    {
        ByteBuffer bb = ByteBuffer.allocate( 0x0b );
        bb.put( new byte[]
            { 
            0x30, 0x09,         // PersistentSearch ::= SEQUENCE {
              0x02, 0x01, 0x09, // changeTypes INTEGER,
              0x01, 0x01, 0x00, // changesOnly BOOLEAN,
              0x01, 0x01, 0x00  // returnECs BOOLEAN
            } );
        bb.flip();

        PersistentSearchDecorator decorator = new PersistentSearchDecorator( codec );

        PersistentSearch ctrl = ( PersistentSearch )decorator.decode( bb.array() );

        int changeTypes = ctrl.getChangeTypes();
        assertTrue( ChangeType.ADD.presentIn( changeTypes ) );
        assertTrue( ChangeType.MODDN.presentIn( changeTypes ) );
        assertEquals( false, ctrl.isChangesOnly() );
        assertEquals( false, ctrl.isReturnECs() );
    }

    
    /**
     * Test the decoding of a PSearchControl with a changes types which
     * value is 0
     */
    @Test( expected=DecoderException.class )
    public void testDecodeModifyDNRequestSuccessChangeTypes0() throws Exception
    {
        ByteBuffer bb = ByteBuffer.allocate( 0x0b );
        bb.put( new byte[]
            { 
            0x30, 0x09,         // PersistentSearch ::= SEQUENCE {
              0x02, 0x01, 0x00, // changeTypes INTEGER,
              0x01, 0x01, 0x00, // changesOnly BOOLEAN,
              0x01, 0x01, 0x00  // returnECs BOOLEAN
            } );
        bb.flip();

        PersistentSearchDecorator decorator = new PersistentSearchDecorator( codec );

        decorator.decode( bb.array() );
    }

    /**
     * Test the decoding of a PSearchControl with a changes types which
     * value is above 15
     */
    @Test( expected=DecoderException.class )
    public void testDecodeModifyDNRequestSuccessChangeTypes22() throws Exception
    {
        ByteBuffer bb = ByteBuffer.allocate( 0x0b );
        bb.put( new byte[]
            { 
            0x30, 0x09,         // PersistentSearch ::= SEQUENCE {
              0x02, 0x01, 0x22, // changeTypes INTEGER,
              0x01, 0x01, 0x00, // changesOnly BOOLEAN,
              0x01, 0x01, 0x00  // returnECs BOOLEAN
            } );
        bb.flip();

        PersistentSearchDecorator decorator = new PersistentSearchDecorator( codec );

        decorator.decode( bb.array() );
    }

    
    /**
     * Test the decoding of a PSearchControl with a null sequence
     */
    @Test( expected=DecoderException.class )
    public void testDecodeModifyDNRequestSuccessNullSequence() throws Exception
    {
        ByteBuffer bb = ByteBuffer.allocate( 0x02 );
        bb.put( new byte[]
            { 
            0x30, 0x00,         // PersistentSearch ::= SEQUENCE {
            } );
        bb.flip();

        PersistentSearchDecorator decorator = new PersistentSearchDecorator( codec );

        decorator.decode( bb.array() );
    }

    
    /**
     * Test the decoding of a PSearchControl without changeTypes
     */
    @Test( expected=DecoderException.class )
    public void testDecodeModifyDNRequestSuccessWithoutChangeTypes() throws Exception
    {
        ByteBuffer bb = ByteBuffer.allocate( 0x08 );
        bb.put( new byte[]
            { 
            0x30, 0x06,         // PersistentSearch ::= SEQUENCE {
              0x01, 0x01, 0x00, // changesOnly BOOLEAN,
              0x01, 0x01, 0x00  // returnECs BOOLEAN
            } );
        bb.flip();

        PersistentSearchDecorator decorator = new PersistentSearchDecorator( codec );

        decorator.decode( bb.array() );
    }

    
    /**
     * Test the decoding of a PSearchControl without changeOnly
     */
    @Test( expected=DecoderException.class )
    public void testDecodeModifyDNRequestSuccessWithoutChangesOnly() throws Exception
    {
        ByteBuffer bb = ByteBuffer.allocate( 0x08 );
        bb.put( new byte[]
            { 
            0x30, 0x06,         // PersistentSearch ::= SEQUENCE {
              0x02, 0x01, 0x01, // changeTypes INTEGER,
              0x01, 0x01, 0x00  // returnECs BOOLEAN
            } );
        bb.flip();

        PersistentSearchDecorator decorator = new PersistentSearchDecorator( codec );

        decorator.decode( bb.array() );
    }

    
    /**
     * Test the decoding of a PSearchControl without returnECs
     */
    @Test( expected=DecoderException.class )
    public void testDecodeModifyDNRequestSuccessWithoutReturnECs() throws Exception
    {
        ByteBuffer bb = ByteBuffer.allocate( 0x08 );
        bb.put( new byte[]
            { 
            0x30, 0x06,         // PersistentSearch ::= SEQUENCE {
              0x02, 0x01, 0x01, // changeTypes INTEGER,
              0x01, 0x01, 0x00, // changesOnly BOOLEAN,
            } );
        bb.flip();

        PersistentSearchDecorator decorator = new PersistentSearchDecorator( codec );

        decorator.decode( bb.array() );
    }
}
