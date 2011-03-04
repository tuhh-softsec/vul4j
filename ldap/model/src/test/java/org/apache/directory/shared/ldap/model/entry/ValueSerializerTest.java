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
package org.apache.directory.shared.ldap.model.entry;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.directory.shared.ldap.model.exception.LdapException;
import org.apache.directory.shared.util.StringConstants;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.mycila.junit.concurrent.Concurrency;
import com.mycila.junit.concurrent.ConcurrentJunitRunner;

/**
 * Test the Value Serialization
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrency()
public class ValueSerializerTest
{
    private static byte[] data = new byte[] {0x01, 0x02, 0x03, 0x04};
    BinaryValue bv1 = new BinaryValue( data );
    BinaryValue bv2 = new BinaryValue( StringConstants.EMPTY_BYTES );
    BinaryValue bv3 = new BinaryValue();
    BinaryValue bv1n = new BinaryValue( data );
    BinaryValue bv2n = new BinaryValue( StringConstants.EMPTY_BYTES );
    BinaryValue bv3n = new BinaryValue();
    StringValue sv1 = new StringValue( "test" );
    StringValue sv2 = new StringValue( "" );
    StringValue sv3 = new StringValue();
    StringValue sv1n = new StringValue( "test" );
    StringValue sv2n = new StringValue( "" );
    StringValue sv3n = new StringValue();
    
    
    @Test
    public void testBinaryValueWithDataSerialization() throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        BinaryValue.serialize( bv1, out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        BinaryValue bvDeser = (BinaryValue)BinaryValue.deserialize( null, in );

        assertEquals( bv1, bvDeser );
    }
    
    
    @Test
    public void testBinaryValueWithEmptyDataSerialization() throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        BinaryValue.serialize( bv2, out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        BinaryValue bvDeser = (BinaryValue)BinaryValue.deserialize( null, in );

        assertEquals( bv2, bvDeser );
    }
    
    
    @Test
    public void testBinaryValueNoDataSerialization() throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        BinaryValue.serialize( bv3, out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        BinaryValue bvDeser = (BinaryValue)BinaryValue.deserialize( null, in );

        assertEquals( bv3, bvDeser );
    }
    
    
    @Test
    public void testStringValueWithDataSerialization() throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        StringValue.serialize( sv1, out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        StringValue svDeser = (StringValue)StringValue.deserialize( null, in );

        assertEquals( sv1, svDeser );
    }
    
    
    @Test
    public void testStringValueWithEmptyDataSerialization() throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        StringValue.serialize( sv2, out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        StringValue svDeser = (StringValue)StringValue.deserialize( null, in );

        assertEquals( sv2, svDeser );
    }
    
    
    @Test
    public void testStringValueNoDataSerialization() throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        StringValue.serialize( sv3, out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        StringValue svDeser = (StringValue)StringValue.deserialize( null, in );

        assertEquals( sv3, svDeser );
    }
    
    
    @Test
    public void testBinaryValueWithDataNormalizedSerialization() throws IOException, LdapException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );
        bv1n.normalize();

        BinaryValue.serialize( bv1n, out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        BinaryValue bvDeser = (BinaryValue)BinaryValue.deserialize( null, in );

        assertEquals( bv1n, bvDeser );
    }
    
    
    @Test
    public void testBinaryValueWithEmptyDataNormalizedSerialization() throws IOException, LdapException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );
        bv2n.normalize();

        BinaryValue.serialize( bv2n, out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        BinaryValue bvDeser = (BinaryValue)BinaryValue.deserialize( null, in );

        assertEquals( bv2n, bvDeser );
    }
    
    
    @Test
    public void testBinaryValueNoDataNormalizedSerialization() throws IOException, LdapException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );
        bv3n.normalize();

        BinaryValue.serialize( bv3n, out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        BinaryValue bvDeser = (BinaryValue)BinaryValue.deserialize( null, in );

        assertEquals( bv3n, bvDeser );
    }
    
    
    @Test
    public void testStringValueWithDataNormalizedSerialization() throws IOException, LdapException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );
        sv1n.normalize();

        StringValue.serialize( sv1n, out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        StringValue svDeser = (StringValue)StringValue.deserialize( null, in );

        assertEquals( sv1n, svDeser );
    }
    
    
    @Test
    public void testStringValueWithEmptyDataNormalizedSerialization() throws IOException, LdapException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );
        sv2n.normalize();

        StringValue.serialize( sv2n, out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        StringValue svDeser = (StringValue)StringValue.deserialize( null, in );

        assertEquals( sv2n, svDeser );
    }
    
    
    @Test
    public void testStringValueNoDataNormalizedSerialization() throws IOException, LdapException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );
        sv3n.normalize();

        StringValue.serialize( sv3n, out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        StringValue svDeser = (StringValue)StringValue.deserialize( null, in );

        assertEquals( sv3n, svDeser );
    }
}
