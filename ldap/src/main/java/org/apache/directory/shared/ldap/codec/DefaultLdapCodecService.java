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

import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.EncoderException;
import org.apache.directory.shared.ldap.codec.controls.CascadeFactory;
import org.apache.directory.shared.ldap.codec.controls.ManageDsaITFactory;
import org.apache.directory.shared.ldap.codec.search.controls.entryChange.EntryChangeFactory;
import org.apache.directory.shared.ldap.codec.search.controls.subentries.SubentriesFactory;
import org.apache.directory.shared.ldap.model.message.Control;
import org.apache.directory.shared.ldap.model.message.controls.BasicControl;
import org.apache.directory.shared.ldap.model.message.controls.Cascade;
import org.apache.directory.shared.ldap.model.message.controls.EntryChange;
import org.apache.directory.shared.ldap.model.message.controls.ManageDsaIT;
import org.apache.directory.shared.ldap.model.message.controls.Subentries;
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
    
    
    public DefaultLdapCodecService()
    {
        loadStockControls();
    }
    
    
    /**
     * Loads the Controls implement out of the box in the codec.
     */
    private void loadStockControls()
    {
        SubentriesFactory subentriesFactory = new SubentriesFactory( this );
        controlFactories.put( Subentries.OID, subentriesFactory );

        CascadeFactory cascadeFactory = new CascadeFactory( this );
        controlFactories.put( Cascade.OID, cascadeFactory );
        
        ManageDsaITFactory manageDsaITFactory = new ManageDsaITFactory( this );
        controlFactories.put( ManageDsaIT.OID, manageDsaITFactory );
        
        EntryChangeFactory entryChangeFactory = new EntryChangeFactory( this );
        controlFactories.put( EntryChange.OID, entryChangeFactory );
    }
    

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
    @SuppressWarnings("unchecked")
    public <E> E newCodecControl( Class<? extends ICodecControl<? extends Control>> clazz )
    {
        try
        {
            Field f = clazz.getField( "OID" );
            String oid = ( String ) f.get( null );
            IControlFactory<?,?> factory = controlFactories.get( oid );
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

    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <E> E newControl( Class<? extends Control> clazz )
    {
        try
        {
            Field f = clazz.getField( "OID" );
            String oid = ( String ) f.get( null );
            IControlFactory<?,?> factory = controlFactories.get( oid );
            
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

    
    /**
     * {@inheritDoc}
     */
    public ProtocolCodecFactory newProtocolCodecFactory( boolean client )
    {
        return null;
    }

    
    @SuppressWarnings("unchecked")
    public <E> E newControl( String oid )
    {
        try
        {
            IControlFactory<?,?> factory = controlFactories.get( oid );
            
            if ( factory == null )
            {
                return ( E ) new BasicControl( oid );
            }
            
            return ( E ) factory.newControl();
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
            IControlFactory factory = controlFactories.get( control.getOid() );
            
            if ( factory == null )
            {
                org.apache.directory.shared.ldap.model.message.controls.BasicControl basic = 
                    new org.apache.directory.shared.ldap.model.message.controls.BasicControl( control.getOid() );
                basic.setCritical( control.isCritical() );
                return new BasicControlDecorator( this, basic ); 
            }
            
            return factory.decorate( control );
        }
        catch ( SecurityException e )
        {
            e.printStackTrace();
        }
        
        return null;
    }


    public javax.naming.ldap.Control toJndiControl( Control control ) throws EncoderException
    {
        return null;
    }


    public Control fromJndiControl( javax.naming.ldap.Control control ) throws DecoderException
    {
        return null;
    }
}
