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
package org.apache.directory.shared.ldap.model.ldif;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.directory.shared.ldap.model.message.Control;
import org.apache.directory.shared.ldap.model.message.controls.OpaqueControl;
import org.apache.directory.shared.util.StringConstants;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.mycila.junit.concurrent.Concurrency;
import com.mycila.junit.concurrent.ConcurrentJunitRunner;

/**
 * Test the LdifControlSerializer class
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrency()
public class LdifControlSerializationTest
{
    private static Control controlCriticalWithData;
    private static Control controlCriticalNoData;
    private static Control controlCriticalEmptyData;
    private static Control controlNoCriticalWithData;
    private static Control controlNoCriticalNoData;
    private static Control controlNoCriticalEmptyData;
    private static byte[] data = new byte[] {0x01, 0x02, 0x03, 0x04};
    
    @BeforeClass
    public static void setup()
    {
        controlCriticalWithData = new OpaqueControl( "1.2.3.4.1" );
        controlCriticalWithData.setCritical( true );
        ((OpaqueControl)controlCriticalWithData).setEncodedValue( data );

        controlCriticalNoData = new OpaqueControl( "1.2.3.4.2" );
        controlCriticalNoData.setCritical( true );

        controlCriticalEmptyData = new OpaqueControl( "1.2.3.4.3" );
        controlCriticalEmptyData.setCritical( true );
        ((OpaqueControl)controlCriticalEmptyData).setEncodedValue( StringConstants.EMPTY_BYTES );

        controlNoCriticalWithData = new OpaqueControl( "1.2.3.4.4" );
        controlNoCriticalWithData.setCritical( false );
        ((OpaqueControl)controlNoCriticalWithData).setEncodedValue( data );

        controlNoCriticalNoData = new OpaqueControl( "1.2.3.4.5" );
        controlNoCriticalNoData.setCritical( false );

        controlNoCriticalEmptyData = new OpaqueControl( "1.2.3.4.6" );
        controlNoCriticalEmptyData.setCritical( false );
    }
    
    
    @Test
    public void testControlCriticalWithDataSerialization() throws IOException, ClassNotFoundException
    {
        LdifControl ldifControl1 = new LdifControl( controlCriticalWithData.getOid() );
        ldifControl1.setCritical( controlCriticalWithData.isCritical() );
        ldifControl1.setValue( ((OpaqueControl)controlCriticalWithData).getEncodedValue() );
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        ldifControl1.writeExternal( out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        LdifControl ldifControl2 = new LdifControl();
        ldifControl2.readExternal( in );

        assertEquals( ldifControl1, ldifControl2 );
    }
    
    
    @Test
    public void testControlCriticalNoDataSerialization() throws IOException, ClassNotFoundException
    {
        LdifControl ldifControl1 = new LdifControl( controlCriticalNoData.getOid() );
        ldifControl1.setCritical( controlCriticalNoData.isCritical() );
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        ldifControl1.writeExternal( out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        LdifControl ldifControl2 = new LdifControl();
        ldifControl2.readExternal( in );

        assertEquals( ldifControl1, ldifControl2 );
    }
    
    
    @Test
    public void testControlCriticalEmptyDataSerialization() throws IOException, ClassNotFoundException
    {
        LdifControl ldifControl1 = new LdifControl( controlCriticalEmptyData.getOid() );
        ldifControl1.setCritical( controlCriticalEmptyData.isCritical() );
        ldifControl1.setValue( ((OpaqueControl)controlCriticalEmptyData).getEncodedValue() );
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        ldifControl1.writeExternal( out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        LdifControl ldifControl2 = new LdifControl();
        ldifControl2.readExternal( in );

        assertEquals( ldifControl1, ldifControl2 );
    }
    
    
    @Test
    public void testControlNoCriticalWithDataSerialization() throws IOException, ClassNotFoundException
    {
        LdifControl ldifControl1 = new LdifControl( controlNoCriticalWithData.getOid() );
        ldifControl1.setCritical( controlNoCriticalWithData.isCritical() );
        ldifControl1.setValue( ((OpaqueControl)controlNoCriticalWithData).getEncodedValue() );
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        ldifControl1.writeExternal( out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        LdifControl ldifControl2 = new LdifControl();
        ldifControl2.readExternal( in );

        assertEquals( ldifControl1, ldifControl2 );
    }
    
    
    @Test
    public void testControlNoCriticalNoDataSerialization() throws IOException, ClassNotFoundException
    {
        LdifControl ldifControl1 = new LdifControl( controlNoCriticalNoData.getOid() );
        ldifControl1.setCritical( controlNoCriticalNoData.isCritical() );
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        ldifControl1.writeExternal( out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        LdifControl ldifControl2 = new LdifControl();
        ldifControl2.readExternal( in );

        assertEquals( ldifControl1, ldifControl2 );
    }
    
    
    @Test
    public void testControlNoCriticalEmptyDataSerialization() throws IOException, ClassNotFoundException
    {
        LdifControl ldifControl1 = new LdifControl( controlNoCriticalEmptyData.getOid() );
        ldifControl1.setCritical( controlNoCriticalEmptyData.isCritical() );
        ldifControl1.setValue( ((OpaqueControl)controlNoCriticalEmptyData).getEncodedValue() );
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( baos );

        ldifControl1.writeExternal( out );
        
        ObjectInputStream in = null;

        byte[] data = baos.toByteArray();
        in = new ObjectInputStream( new ByteArrayInputStream( data ) );

        LdifControl ldifControl2 = new LdifControl();
        ldifControl2.readExternal( in );

        assertEquals( ldifControl1, ldifControl2 );
    }
}
