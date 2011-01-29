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
package org.apache.directory.shared.ldap.codec;


import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.directory.shared.ldap.model.message.Control;
import org.apache.mina.filter.codec.ProtocolCodecFactory;


public class TestLdapCodecService implements ILdapCodecService 
{
    Map<String,IControlFactory<?,?>> factories = new HashMap<String, IControlFactory<?,?>>();
    
    public void registerControl( IControlFactory<?,?> factory )
    {
        factories.put( factory.getOid(), factory );
    }

    
    public void registerExtendedOp( IExtendedOpFactory<?, ?> factory )
    {
    }


    @SuppressWarnings("unchecked")
    public <E> E newCodecControl( Class<? extends ICodecControl<? extends Control>> clazz )
    {
        try
        {
            Field f = clazz.getField( "OID" );
            String oid = ( String ) f.get( null );
            IControlFactory<?,?> factory = factories.get( oid );
            return ( E ) factory.newCodecControl();
        }
        catch ( IllegalAccessException e )
        {
            e.printStackTrace();
        }
        catch ( SecurityException e )
        {
            e.printStackTrace();
        }
        catch ( NoSuchFieldException e )
        {
            e.printStackTrace();
        }
        
        return null;
    }

    
    @SuppressWarnings("unchecked")
    public <E> E newControl( Class<? extends Control> clazz )
    {
        try
        {
            Field f = clazz.getField( "OID" );
            String oid = ( String ) f.get( null );
            IControlFactory<?,?> factory = factories.get( oid );
            return ( E ) factory.newControl();
        }
        catch ( IllegalAccessException e )
        {
            e.printStackTrace();
        }
        catch ( SecurityException e )
        {
            e.printStackTrace();
        }
        catch ( NoSuchFieldException e )
        {
            e.printStackTrace();
        }
        
        return null;
    }

    
    public Iterator<String> controlOids()
    {
        return null;
    }

    public Iterator<String> extendedRequestOids()
    {
        return null;
    }

    public Iterator<String> extendedResponseOids()
    {
        return null;
    }


    public ProtocolCodecFactory newProtocolCodecFactory( boolean client )
    {
        return null;
    }


    public <E> E newControl( String oid )
    {
        return null;
    }


    public ICodecControl<? extends Control> decorate( Control control )
    {
        return null;
    }
}