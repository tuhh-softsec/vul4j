/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.apache.directory.shared.ldap.codec;


import java.lang.reflect.Field; 
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.directory.shared.ldap.model.message.Control;
import org.apache.mina.filter.codec.ProtocolCodecFactory;


/**
 * The default {@link ILdapCodecService} implementation.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class DefaultLdapCodecService implements ILdapCodecService
{
    Map<String,IControlFactory<?,?>> controlFactories = new HashMap<String, IControlFactory<?,?>>();
    Map<String,IExtendedOpFactory<?,?>> extReqFactories = new HashMap<String, IExtendedOpFactory<?,?>>();
    Map<String,IExtendedOpFactory<?,?>> extResFactories = new HashMap<String, IExtendedOpFactory<?,?>>();
    

    //-------------------------------------------------------------------------
    // ILdapCodecService implementation methods
    //-------------------------------------------------------------------------
    
    
    /**
     * {@inheritDoc}
     */
    public void registerControl( IControlFactory<?,?> factory )
    {
        controlFactories.put( factory.getOid(), factory );
    }
    

    /**
     * {@inheritDoc}
     */
    public Iterator<String> controlOids()
    {
        return controlFactories.keySet().iterator();
    }
    

    /**
     * {@inheritDoc}
     */
    public Iterator<String> extendedRequestOids()
    {
        return extReqFactories.keySet().iterator();
    }

    
    /**
     * {@inheritDoc}
     */
    public Iterator<String> extendedResponseOids()
    {
        return extResFactories.keySet().iterator();
    }

    
    /**
     * {@inheritDoc}
     */
    public void registerExtendedOp( IExtendedOpFactory<?, ?> factory )
    {
        extReqFactories.put( factory.getRequestOid(), factory );
    }


    /**
     * {@inheritDoc}
     */
    public <E> E newCodecControl( Class<? extends ICodecControl<? extends Control>> clazz )
    {
        try
        {
            Field f = clazz.getField( "OID" );
            String oid = ( String ) f.get( null );
            IControlFactory<?,?> factory = controlFactories.get( oid );
            return extracted2( factory );
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

    
    /**
     * {@inheritDoc}
     */
    public <E> E newControl( Class<? extends Control> clazz )
    {
        try
        {
            Field f = clazz.getField( "OID" );
            String oid = ( String ) f.get( null );
            IControlFactory<?,?> factory = controlFactories.get( oid );
            return extracted( factory );
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

    
    /**
     * {@inheritDoc}
     */
    public ProtocolCodecFactory newProtocolCodecFactory( boolean client )
    {
        return null;
    }

    
    @SuppressWarnings("unchecked")
    private <E> E extracted( IControlFactory<?,?> factory )
    {
        return ( E ) factory.newControl();
    }

    
    @SuppressWarnings("unchecked")
    private <E> E extracted2( IControlFactory<?,?> factory )
    {
        return ( E ) factory.newCodecControl();
    }


    public <E> E newControl( String oid )
    {
        try
        {
            IControlFactory<?,?> factory = controlFactories.get( oid );
            return extracted( factory );
        }
        catch ( SecurityException e )
        {
            e.printStackTrace();
        }
        
        return null;
    }


    public ICodecControl<? extends Control> decorate( Control control )
    {
        try
        {
            IControlFactory<?,?> factory = controlFactories.get( control.getOid() );
            return factory.decorate( control );
        }
        catch ( SecurityException e )
        {
            e.printStackTrace();
        }
        
        return null;
    }
}
