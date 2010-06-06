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
package org.apache.directory.shared.ldap.codec.controls.replication;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.ByteBuffer;

import org.apache.directory.shared.asn1.ber.Asn1Decoder;
import org.apache.directory.shared.asn1.codec.EncoderException;
import org.apache.directory.shared.ldap.codec.controls.replication.syncDoneValue.SyncDoneValueControl;
import org.apache.directory.shared.ldap.codec.controls.replication.syncDoneValue.SyncDoneValueControlContainer;
import org.apache.directory.shared.ldap.codec.controls.replication.syncDoneValue.SyncDoneValueControlDecoder;
import org.apache.directory.shared.ldap.util.StringTools;
import org.junit.Test;


/**
 * 
 * TestCase for SyncDoneValueControlCodec .
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SyncDoneValueControlTest
{

    @Test
    public void testSyncDoneValueControl()
    {
        Asn1Decoder decoder = new SyncDoneValueControlDecoder();
        ByteBuffer bb = ByteBuffer.allocate( 11 );

        bb.put( new byte[]
            { 
              0x30, 0x09, 
                0x04, 0x04, 'x', 'k', 'c', 'd', // the cookie 
                0x01, 0x01, ( byte ) 0xFF       // refreshDeletes flag TRUE
            } );

        bb.flip();

        SyncDoneValueControlContainer container = new SyncDoneValueControlContainer();
        container.setSyncDoneValueControl( new SyncDoneValueControl() );

        try
        {
            decoder.decode( bb, container );
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        SyncDoneValueControl control = container.getSyncDoneValueControl();
        assertEquals( "xkcd", StringTools.utf8ToString( control.getCookie() ) );
        assertTrue( control.isRefreshDeletes() );
        
        // test encoding
        try
        {
            ByteBuffer buffer = ByteBuffer.allocate( 0x29 );
            buffer.put( new byte[]
                { 
                0x30, 0x27,                            // Control
                  0x04, 0x18,                          // OID (SyncRequestValue)
                    '1', '.', '3', '.', '6', '.', '1', '.', 
                    '4', '.', '1', '.', '4', '2', '0', '3', 
                    '.', '1', '.', '9', '.', '1', '.', '3',
                  0x04, 0x0B,
                    0x30, 0x09, 
                      0x04, 0x04, 'x', 'k', 'c', 'd',  // the cookie 
                      0x01, 0x01, ( byte ) 0xFF        // refreshDeletes flag TRUE
                } );

            buffer.flip();

            bb = control.encode( ByteBuffer.allocate( control.computeLength() ) );
            String expected = StringTools.dumpBytes( buffer.array() );
            String decoded = StringTools.dumpBytes( bb.array() );
            assertEquals( expected, decoded );
        }
        catch( EncoderException e )
        {
            fail( e.getMessage() );
        }
    }


    @Test
    public void testSyncDoneValueControlWithoutCookie()
    {
        Asn1Decoder decoder = new SyncDoneValueControlDecoder();
        ByteBuffer bb = ByteBuffer.allocate( 5 );

        bb.put( new byte[]
            { 
              0x30, 0x03, 
              // null cookie
                0x01, 0x01, 0x10 // refreshDeletes flag TRUE
            } );

        bb.flip();

        SyncDoneValueControlContainer container = new SyncDoneValueControlContainer();
        container.setSyncDoneValueControl( new SyncDoneValueControl() );

        try
        {
            decoder.decode( bb, container );
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        SyncDoneValueControl control = container.getSyncDoneValueControl();
        assertNull( control.getCookie() );
        assertTrue( control.isRefreshDeletes() );

        // test encoding
        try
        {
            ByteBuffer buffer = ByteBuffer.allocate( 0x23 );
            buffer.put( new byte[]
                { 
                0x30, 0x21,                            // Control
                  0x04, 0x18,                          // OID (SyncRequestValue)
                    '1', '.', '3', '.', '6', '.', '1', '.', 
                    '4', '.', '1', '.', '4', '2', '0', '3', 
                    '.', '1', '.', '9', '.', '1', '.', '3',
                  0x04, 0x05,
                    0x30, 0x03, 
                      0x01, 0x01, ( byte ) 0xFF        // refreshDeletes flag TRUE
                } );

            buffer.flip();

            bb = control.encode( ByteBuffer.allocate( control.computeLength() ) );
            String expected = StringTools.dumpBytes( buffer.array() );
            String decoded = StringTools.dumpBytes( bb.array() );
            assertEquals( expected, decoded );
        }
        catch( EncoderException e )
        {
            fail( e.getMessage() );
        }
    }

    
    @Test
    public void testSyncDoneValueWithSequenceOnly()
    {
        Asn1Decoder decoder = new SyncDoneValueControlDecoder();
        ByteBuffer bb = ByteBuffer.allocate( 2 );

        bb.put( new byte[]
            { 
              0x30, 0x00 
            } );

        bb.flip();

        SyncDoneValueControlContainer container = new SyncDoneValueControlContainer();
        container.setSyncDoneValueControl( new SyncDoneValueControl() );

        try
        {
            decoder.decode( bb, container );
        }
        catch ( Exception e )
        {
            fail( "shouldn't reach this" );
        }

        SyncDoneValueControl control = container.getSyncDoneValueControl();
        assertNull( control.getCookie() );
        assertFalse( control.isRefreshDeletes() );
    }

    
    @Test
    public void testSyncDoneValueControlWithEmptyCookie()
    {
        Asn1Decoder decoder = new SyncDoneValueControlDecoder();
        ByteBuffer bb = ByteBuffer.allocate( 7 );

        bb.put( new byte[]
            { 
              0x30, 0x05, 
                0x04, 0x00,      // empty cookie
                0x01, 0x01, 0x00 // refreshDeletes flag FALSE
            } );

        bb.flip();

        SyncDoneValueControlContainer container = new SyncDoneValueControlContainer();
        container.setSyncDoneValueControl( new SyncDoneValueControl() );

        try
        {
            decoder.decode( bb, container );
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        SyncDoneValueControl control = container.getSyncDoneValueControl();
        assertEquals( "", StringTools.utf8ToString( control.getCookie() ) );
        assertFalse( control.isRefreshDeletes() );

        // test encoding
        try
        {
            ByteBuffer buffer = ByteBuffer.allocate( 0x20 );
            buffer.put( new byte[]
                { 
                0x30, 0x1E,                            // Control
                  0x04, 0x18,                          // OID (SyncRequestValue)
                    '1', '.', '3', '.', '6', '.', '1', '.', 
                    '4', '.', '1', '.', '4', '2', '0', '3', 
                    '.', '1', '.', '9', '.', '1', '.', '3',
                  0x04, 0x02,
                    0x30, 0x00
                } );

            buffer.flip();

            bb = control.encode( ByteBuffer.allocate( control.computeLength() ) );
            String expected = StringTools.dumpBytes( buffer.array() );
            String decoded = StringTools.dumpBytes( bb.array() );
            assertEquals( expected, decoded );
        }
        catch( Exception e )
        {
            fail( e.getMessage() );
        }
    }

}
