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

import org.apache.directory.junit.tools.Concurrent;
import org.apache.directory.junit.tools.ConcurrentJunitRunner;
import org.apache.directory.shared.asn1.EncoderException;
import org.apache.directory.shared.ldap.codec.DefaultLdapCodecService;
import org.apache.directory.shared.ldap.codec.ILdapCodecService;
import org.apache.directory.shared.ldap.codec.controls.replication.syncDoneValue.ISyncDoneValue;
import org.apache.directory.shared.ldap.codec.controls.replication.syncDoneValue.SyncDoneValueDecorator;
import org.apache.directory.shared.util.Strings;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * 
 * TestCase for SyncDoneValueControlCodec .
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrent()
public class SyncDoneValueControlTest
{
    private ILdapCodecService codec = new DefaultLdapCodecService();

    
    @Test
    public void testSyncDoneValueControl() throws Exception
    {
        ByteBuffer bb = ByteBuffer.allocate( 11 );

        bb.put( new byte[]
            { 
              0x30, 0x09, 
                0x04, 0x04, 'x', 'k', 'c', 'd', // the cookie 
                0x01, 0x01, ( byte ) 0xFF       // refreshDeletes flag TRUE
            } );

        bb.flip();

        SyncDoneValueDecorator decorator =  new SyncDoneValueDecorator( codec );

        ISyncDoneValue control = (ISyncDoneValue)decorator.decode( bb.array() );
        
        assertEquals( "xkcd", Strings.utf8ToString(control.getCookie()) );
        assertTrue( control.isRefreshDeletes() );
        
        // test encoding
        try
        {
            ByteBuffer buffer = ((SyncDoneValueDecorator)control).encode( ByteBuffer.allocate( ((SyncDoneValueDecorator)control).computeLength() ) );
            String expected = Strings.dumpBytes( bb.array() );
            String decoded = Strings.dumpBytes( buffer.array() );
            assertEquals( expected, decoded );
        }
        catch( EncoderException e )
        {
            fail( e.getMessage() );
        }
    }


    @Test
    public void testSyncDoneValueControlWithoutCookie() throws Exception
    {
        ByteBuffer bb = ByteBuffer.allocate( 5 );

        bb.put( new byte[]
            { 
              0x30, 0x03, 
              // null cookie
                0x01, 0x01, (byte)0xFF // refreshDeletes flag TRUE
            } );

        bb.flip();

        SyncDoneValueDecorator decorator =  new SyncDoneValueDecorator( codec );

        ISyncDoneValue control = (ISyncDoneValue)decorator.decode( bb.array() );

        assertNull( control.getCookie() );
        assertTrue( control.isRefreshDeletes() );

        // test encoding
        try
        {
            ByteBuffer buffer = ((SyncDoneValueDecorator)control).encode( ByteBuffer.allocate( ((SyncDoneValueDecorator)control).computeLength() ) );
            String expected = Strings.dumpBytes( bb.array() );
            String decoded = Strings.dumpBytes( buffer.array() );
            assertEquals( expected, decoded );
        }
        catch( EncoderException e )
        {
            fail( e.getMessage() );
        }
    }

    
    @Test
    public void testSyncDoneValueWithSequenceOnly() throws Exception
    {
        ByteBuffer bb = ByteBuffer.allocate( 2 );

        bb.put( new byte[]
            { 
              0x30, 0x00 
            } );

        bb.flip();

        SyncDoneValueDecorator decorator =  new SyncDoneValueDecorator( codec );

        ISyncDoneValue control = (ISyncDoneValue)decorator.decode( bb.array() );

        assertNull( control.getCookie() );
        assertFalse( control.isRefreshDeletes() );
    }

    
    @Test
    public void testSyncDoneValueControlWithEmptyCookie() throws Exception
    {
        ByteBuffer bb = ByteBuffer.allocate( 7 );

        bb.put( new byte[]
            { 
              0x30, 0x05, 
                0x04, 0x00,      // empty cookie
                0x01, 0x01, 0x00 // refreshDeletes flag FALSE
            } );

        bb.flip();

        SyncDoneValueDecorator decorator =  new SyncDoneValueDecorator( codec );

        ISyncDoneValue control = (ISyncDoneValue)decorator.decode( bb.array() );

        assertEquals( "", Strings.utf8ToString(control.getCookie()) );
        assertFalse( control.isRefreshDeletes() );

        // test encoding
        try
        {
            ByteBuffer buffer = ((SyncDoneValueDecorator)control).encode( ByteBuffer.allocate( ((SyncDoneValueDecorator)control).computeLength() ) );
            String decoded = Strings.dumpBytes( buffer.array() );
            assertEquals( "0x30 0x00 ", decoded );
        }
        catch( Exception e )
        {
            fail( e.getMessage() );
        }
    }
}
