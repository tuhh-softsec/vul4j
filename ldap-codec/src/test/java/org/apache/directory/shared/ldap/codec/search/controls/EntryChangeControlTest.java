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
import static org.junit.Assert.assertNull;

import java.nio.ByteBuffer;

import org.apache.directory.junit.tools.Concurrent;
import org.apache.directory.junit.tools.ConcurrentJunitRunner;
import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.ldap.codec.api.DefaultLdapCodecService;
import org.apache.directory.shared.ldap.codec.api.LdapCodecService;
import org.apache.directory.shared.ldap.codec.controls.search.entryChange.EntryChangeDecorator;
import org.apache.directory.shared.ldap.model.message.controls.ChangeType;
import org.apache.directory.shared.ldap.model.message.controls.EntryChange;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.apache.directory.shared.util.Strings;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test the EntryChangeControlTest codec
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrent()
public class EntryChangeControlTest
{
    private LdapCodecService codec = new DefaultLdapCodecService();

    /**
     * Test the decoding of a EntryChangeControl
     */
    @Test
    public void testDecodeEntryChangeControlSuccess() throws Exception
    {
        ByteBuffer bb = ByteBuffer.allocate( 0x0D );
        bb.put( new byte[]
            { 
            0x30, 0x0B,                     // EntryChangeNotification ::= SEQUENCE {
              0x0A, 0x01, 0x08,             //     changeType ENUMERATED {
                                            //         modDN (8)
                                            //     }
              0x04, 0x03, 'a', '=', 'b',    //     previousDN LDAPDN OPTIONAL, -- modifyDN ops. only
              0x02, 0x01, 0x10              //     changeNumber INTEGER OPTIONAL } -- if supported
            } );
        bb.flip();

        EntryChangeDecorator decorator = new EntryChangeDecorator( codec );
        
        EntryChange entryChange = (EntryChange)decorator.decode( bb.array() );

        assertEquals( ChangeType.MODDN, entryChange.getChangeType() );
        assertEquals( "a=b", entryChange.getPreviousDn().toString() );
        assertEquals( 16, entryChange.getChangeNumber() );
    }


    /**
     * Test the decoding of a EntryChangeControl
     */
    @Test
    public void testDecodeEntryChangeControlSuccessLongChangeNumber() throws Exception
    {
        ByteBuffer bb = ByteBuffer.allocate( 0x13 );
        bb.put( new byte[]
            { 
            0x30, 0x11,                     // EntryChangeNotification ::= SEQUENCE {
              0x0A, 0x01, 0x08,             //     changeType ENUMERATED {
                                            //         modDN (8)
                                            //     }
              0x04, 0x03, 'a', '=', 'b',    //     previousDN LDAPDN OPTIONAL, -- modifyDN ops. only
              0x02, 0x07,                   //     changeNumber INTEGER OPTIONAL } -- if supported
                0x12, 0x34, 0x56, 0x78, (byte)0x9A, (byte)0xBC, (byte)0xDE
            } );
        bb.flip();

        EntryChangeDecorator decorator = new EntryChangeDecorator( codec );
        
        EntryChange entryChange = (EntryChange)decorator.decode( bb.array() );

        assertEquals( ChangeType.MODDN, entryChange.getChangeType() );
        assertEquals( "a=b", entryChange.getPreviousDn().toString() );
        assertEquals( 5124095576030430L, entryChange.getChangeNumber() );
    }


    /**
     * Test the decoding of a EntryChangeControl with a add and a change number
     */
    @Test
    public void testDecodeEntryChangeControlWithADDAndChangeNumber() throws Exception
    {
        ByteBuffer bb = ByteBuffer.allocate( 0x08 );
        bb.put( new byte[]
            { 
            0x30, 0x06,             // EntryChangeNotification ::= SEQUENCE {
              0x0A, 0x01, 0x01,     //     changeType ENUMERATED {
                                    //         Add (1)
                                    //     }
              0x02, 0x01, 0x10      //     changeNumber INTEGER OPTIONAL -- if supported
                                    // }
            } );
        bb.flip();

        EntryChangeDecorator decorator = new EntryChangeDecorator( codec );
        
        EntryChange entryChange = (EntryChange)decorator.decode( bb.array() );

        assertEquals( ChangeType.ADD, entryChange.getChangeType() );
        assertNull( entryChange.getPreviousDn() );
        assertEquals( 16, entryChange.getChangeNumber() );
    }


    /**
     * Test the decoding of a EntryChangeControl with a add so we should not
     * have a PreviousDN
     */
    @Test( expected=DecoderException.class )
    public void testDecodeEntryChangeControlWithADDAndPreviousDNBad() throws Exception
    {
        ByteBuffer bb = ByteBuffer.allocate( 0x0D );
        bb.put( new byte[]
            { 
            0x30, 0x0B,                     // EntryChangeNotification ::= SEQUENCE {
              0x0A, 0x01, 0x01,             //     changeType ENUMERATED {
                                            //         ADD (1)
                                            //     }
              0x04, 0x03, 'a', '=', 'b',    //     previousDN LDAPDN OPTIONAL, --
                                            //     modifyDN ops. only
              0x02, 0x01, 0x10              //     changeNumber INTEGER OPTIONAL -- if supported
                                            // }
            } );
        bb.flip();

        EntryChangeDecorator decorator = new EntryChangeDecorator( codec );
        
        decorator.decode( bb.array() );
    }


    /**
     * Test the decoding of a EntryChangeControl with a add and nothing else
     */
    @Test
    public void testDecodeEntryChangeControlWithADD() throws Exception
    {
        ByteBuffer bb = ByteBuffer.allocate( 0x05 );
        bb.put( new byte[]
            { 
            0x30, 0x03,                 // EntryChangeNotification ::= SEQUENCE {
              0x0A, 0x01, 0x01,         //     changeType ENUMERATED {
                                        //         ADD (1)
                                        //     }
                                        // }
            } );
        bb.flip();

        EntryChangeDecorator decorator = new EntryChangeDecorator( codec );
        
        EntryChange entryChange = (EntryChange)decorator.decode( bb.array() );

        assertEquals( ChangeType.ADD, entryChange.getChangeType() );
        assertNull( entryChange.getPreviousDn() );
        assertEquals( EntryChangeDecorator.UNDEFINED_CHANGE_NUMBER, entryChange.getChangeNumber() );
    }


    /**
     * Test the decoding of a EntryChangeControl with a wrong changeType and
     * nothing else
     */
    @Test( expected=DecoderException.class )
    public void testDecodeEntryChangeControlWithWrongChangeType() throws Exception
    {
        ByteBuffer bb = ByteBuffer.allocate( 0x05 );
        bb.put( new byte[]
            { 
            0x30, 0x03,                 // EntryChangeNotification ::= SEQUENCE {
              0x0A, 0x01, 0x03,         //     changeType ENUMERATED {
                                        //         BAD Change Type
                                        //     }
                                        // }
            } );
        bb.flip();

        EntryChangeDecorator decorator = new EntryChangeDecorator( codec );
        
        decorator.decode( bb.array() );
    }


    /**
     * Test the decoding of a EntryChangeControl with a wrong changeNumber
     */
    @Test( expected=DecoderException.class )
    public void testDecodeEntryChangeControlWithWrongChangeNumber() throws Exception
    {
        ByteBuffer bb = ByteBuffer.allocate( 0x1C );
        bb.put( new byte[]
            { 
            0x30, 0x1A,                     // EntryChangeNotification ::= SEQUENCE {
              0x0A, 0x01, 0x08,             //     changeType ENUMERATED {
                                            //         modDN (8)
                                            //     }
              0x04, 0x03, 'a', '=', 'b',    //     previousDN LDAPDN OPTIONAL, -- modifyDN ops. only
              0x02, 0x10,                   //     changeNumber INTEGER OPTIONAL -- if supported
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
            } );
        bb.flip();

        EntryChangeDecorator decorator = new EntryChangeDecorator( codec );
        
        decorator.decode( bb.array() );
    }


    /**
     * Test encoding of a EntryChangeControl.
     */
    @Test
    public void testEncodeEntryChangeControl() throws Exception
    {
        ByteBuffer bb = ByteBuffer.allocate( 0x0D );
        bb.put( new byte[]
            { 
                0x30, 0x0B,                        // EntryChangeNotification ::= SEQUENCE {
                  0x0A, 0x01, 0x08,                //     changeType ENUMERATED {
                                                   //         modDN (8)
                                                   //     }
                  0x04, 0x03, 'a', '=', 'b',       //     previousDN LDAPDN OPTIONAL, -- modifyDN ops. only
                  0x02, 0x01, 0x10,                //     changeNumber INTEGER OPTIONAL -- if supported
            } );

        String expected = Strings.dumpBytes(bb.array());
        bb.flip();

        EntryChangeDecorator decorator = new EntryChangeDecorator( codec );

        EntryChange entryChange = (EntryChange) decorator.getDecorated();
        entryChange.setChangeType( ChangeType.MODDN );
        entryChange.setChangeNumber( 16 );
        entryChange.setPreviousDn( new Dn( "a=b" ) );
        bb = decorator.encode( ByteBuffer.allocate( decorator.computeLength() ) );
        String decoded = Strings.dumpBytes( bb.array() );
        assertEquals( expected, decoded );
    }


    /**
     * Test encoding of a EntryChangeControl with a long changeNumber.
     */
    @Test
    public void testEncodeEntryChangeControlLong() throws Exception
    {
        ByteBuffer bb = ByteBuffer.allocate( 0x13 );
        bb.put( new byte[]
            { 
                0x30, 0x11,                        // EntryChangeNotification ::= SEQUENCE {
                  0x0A, 0x01, 0x08,                //     changeType ENUMERATED {
                                                   //         modDN (8)
                                                   //     }
                  0x04, 0x03, 'a', '=', 'b',       //     previousDN LDAPDN OPTIONAL, -- modifyDN ops. only
                  0x02, 0x07,                      //     changeNumber INTEGER OPTIONAL -- if supported
                    0x12, 0x34, 0x56, 0x78, (byte)0x9a, (byte)0xbc, (byte)0xde
            } );

        String expected = Strings.dumpBytes(bb.array());
        bb.flip();

        EntryChangeDecorator decorator = new EntryChangeDecorator( codec );
        
        EntryChange entryChange = (EntryChange) decorator.getDecorated();

        entryChange.setChangeType( ChangeType.MODDN );
        entryChange.setChangeNumber( 5124095576030430L );
        entryChange.setPreviousDn( new Dn( "a=b" ) );
        bb = decorator.encode( ByteBuffer.allocate( decorator.computeLength() ) );
        String decoded = Strings.dumpBytes(bb.array());
        assertEquals( expected, decoded );
    }
}
