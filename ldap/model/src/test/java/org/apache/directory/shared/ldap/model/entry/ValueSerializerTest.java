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
import org.apache.directory.shared.ldap.model.schema.AttributeType;
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
    public void testBinaryValueWithDataSerialization() throws IOException, ClassNotFoundException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        bv1.writeExternal( out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        BinaryValue bvDeser = new BinaryValue( (AttributeType)null );
        bvDeser.readExternal( in );

        assertEquals( bv1, bvDeser );
    }
    
    
    @Test
    public void testBinaryValueWithEmptyDataSerialization() throws IOException, ClassNotFoundException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        bv2.writeExternal( out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        BinaryValue bvDeser = new BinaryValue( (AttributeType)null );
        bvDeser.readExternal( in );

        assertEquals( bv2, bvDeser );
    }
    
    
    @Test
    public void testBinaryValueNoDataSerialization() throws IOException, ClassNotFoundException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        bv3.writeExternal( out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        BinaryValue bvDeser = new BinaryValue( (AttributeType)null );
        bvDeser.readExternal( in );

        assertEquals( bv3, bvDeser );
    }
    
    
    @Test
    public void testStringValueWithDataSerialization() throws IOException, ClassNotFoundException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        sv1.writeExternal( out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        StringValue svDeser = new StringValue( (AttributeType)null );
        svDeser.readExternal( in );

        assertEquals( sv1, svDeser );
    }
    
    
    @Test
    public void testStringValueWithEmptyDataSerialization() throws IOException, ClassNotFoundException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        sv2.writeExternal( out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        StringValue svDeser = new StringValue( (AttributeType)null );
        svDeser.readExternal( in );

        assertEquals( sv2, svDeser );
    }
    
    
    @Test
    public void testStringValueNoDataSerialization() throws IOException, ClassNotFoundException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        sv3.writeExternal( out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        StringValue svDeser = new StringValue( (AttributeType)null );
        svDeser.readExternal( in );

        assertEquals( sv3, svDeser );
    }
    
    
    @Test
    public void testBinaryValueWithDataNormalizedSerialization() throws IOException, LdapException, ClassNotFoundException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );
        bv1n.normalize();

        bv1n.writeExternal( out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        BinaryValue bvDeser = new BinaryValue( (AttributeType)null );
        bvDeser.readExternal( in );

        assertEquals( bv1n, bvDeser );
    }
    
    
    @Test
    public void testBinaryValueWithEmptyDataNormalizedSerialization() throws IOException, LdapException, ClassNotFoundException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );
        bv2n.normalize();

        bv2n.writeExternal( out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        BinaryValue bvDeser = new BinaryValue( (AttributeType)null );
        bvDeser.readExternal( in );

        assertEquals( bv2n, bvDeser );
    }
    
    
    @Test
    public void testBinaryValueNoDataNormalizedSerialization() throws IOException, LdapException, ClassNotFoundException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );
        bv3n.normalize();

        bv3n.writeExternal( out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        BinaryValue bvDeser = new BinaryValue( (AttributeType)null );
        bvDeser.readExternal( in );

        assertEquals( bv3n, bvDeser );
    }
    
    
    @Test
    public void testStringValueWithDataNormalizedSerialization() throws IOException, LdapException, ClassNotFoundException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );
        sv1n.normalize();

        sv1n.writeExternal( out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        StringValue svDeser = new StringValue( (AttributeType)null );
        svDeser.readExternal( in );

        assertEquals( sv1n, svDeser );
    }
    
    
    @Test
    public void testStringValueWithEmptyDataNormalizedSerialization() throws IOException, LdapException, ClassNotFoundException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );
        sv2n.normalize();

        sv2n.writeExternal( out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        StringValue svDeser = new StringValue( (AttributeType)null );
        svDeser.readExternal( in );

        assertEquals( sv2n, svDeser );
    }
    
    
    @Test
    public void testStringValueNoDataNormalizedSerialization() throws IOException, LdapException, ClassNotFoundException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );
        sv3n.normalize();

        sv3n.writeExternal( out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        StringValue svDeser = new StringValue( (AttributeType)null );
        svDeser.readExternal( in );

        assertEquals( sv3n, svDeser );
    }
}
