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
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.EncoderException;
import org.apache.directory.shared.ldap.codec.controls.cascade.CascadeFactory;
import org.apache.directory.shared.ldap.codec.controls.manageDsaIT.ManageDsaITFactory;
import org.apache.directory.shared.ldap.codec.controls.ppolicy.PasswordPolicy;
import org.apache.directory.shared.ldap.codec.controls.ppolicy.PasswordPolicyFactory;
import org.apache.directory.shared.ldap.codec.controls.replication.syncDoneValue.ISyncDoneValue;
import org.apache.directory.shared.ldap.codec.controls.replication.syncDoneValue.SyncDoneValueFactory;
import org.apache.directory.shared.ldap.codec.controls.replication.syncInfoValue.ISyncInfoValue;
import org.apache.directory.shared.ldap.codec.controls.replication.syncInfoValue.SyncInfoValueFactory;
import org.apache.directory.shared.ldap.codec.controls.replication.syncRequestValue.ISyncRequestValue;
import org.apache.directory.shared.ldap.codec.controls.replication.syncRequestValue.SyncRequestValueFactory;
import org.apache.directory.shared.ldap.codec.controls.replication.syncStateValue.ISyncStateValue;
import org.apache.directory.shared.ldap.codec.controls.replication.syncStateValue.SyncStateValueFactory;
import org.apache.directory.shared.ldap.codec.controls.replication.syncmodifydn.ISyncModifyDn;
import org.apache.directory.shared.ldap.codec.controls.replication.syncmodifydn.SyncModifyDnFactory;
import org.apache.directory.shared.ldap.codec.controls.search.entryChange.EntryChangeFactory;
import org.apache.directory.shared.ldap.codec.controls.search.pagedSearch.PagedResultsFactory;
import org.apache.directory.shared.ldap.codec.controls.search.persistentSearch.PersistentSearchFactory;
import org.apache.directory.shared.ldap.codec.controls.search.subentries.SubentriesFactory;
import org.apache.directory.shared.ldap.model.message.Control;
import org.apache.directory.shared.ldap.model.message.controls.AbstractControl;
import org.apache.directory.shared.ldap.model.message.controls.Cascade;
import org.apache.directory.shared.ldap.model.message.controls.EntryChange;
import org.apache.directory.shared.ldap.model.message.controls.ManageDsaIT;
import org.apache.directory.shared.ldap.model.message.controls.OpaqueControlImpl;
import org.apache.directory.shared.ldap.model.message.controls.PagedResults;
import org.apache.directory.shared.ldap.model.message.controls.PersistentSearch;
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
        CascadeFactory cascadeFactory = new CascadeFactory( this );
        controlFactories.put( Cascade.OID, cascadeFactory );
        
        EntryChangeFactory entryChangeFactory = new EntryChangeFactory( this );
        controlFactories.put( EntryChange.OID, entryChangeFactory );
        
        ManageDsaITFactory manageDsaITFactory = new ManageDsaITFactory( this );
        controlFactories.put( ManageDsaIT.OID, manageDsaITFactory );
        
        PagedResultsFactory pagedResultsFactory = new PagedResultsFactory( this );
        controlFactories.put( PagedResults.OID, pagedResultsFactory );
        
        PersistentSearchFactory persistentSearchFactory = new PersistentSearchFactory( this );
        controlFactories.put( PersistentSearch.OID, persistentSearchFactory );

        SubentriesFactory subentriesFactory = new SubentriesFactory( this );
        controlFactories.put( Subentries.OID, subentriesFactory );
        
        SyncDoneValueFactory syncDoneValueFactory = new SyncDoneValueFactory( this );
        controlFactories.put( ISyncDoneValue.OID, syncDoneValueFactory );
        
        SyncInfoValueFactory syncInfoValueFactory = new SyncInfoValueFactory( this );
        controlFactories.put( ISyncInfoValue.OID, syncInfoValueFactory );
        
        SyncModifyDnFactory syncModifyDnFactory = new SyncModifyDnFactory( this );
        controlFactories.put( ISyncModifyDn.OID, syncModifyDnFactory );
        
        SyncRequestValueFactory syncRequestValueFactory = new SyncRequestValueFactory( this );
        controlFactories.put( ISyncRequestValue.OID, syncRequestValueFactory );

        SyncStateValueFactory syncStateValueFactory = new SyncStateValueFactory( this );
        controlFactories.put( ISyncStateValue.OID, syncStateValueFactory );
        
        PasswordPolicyFactory passwordPolicyFactory = new PasswordPolicyFactory( this );
        controlFactories.put( PasswordPolicy.OID, passwordPolicyFactory );
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
                return ( E ) new OpaqueControlImpl( oid );
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
                return new BasicControlDecorator( this, (AbstractControl)control ); 
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
        ICodecControl<? extends Control> decorator = decorate( control );
        ByteBuffer bb = ByteBuffer.allocate( decorator.computeLength() );
        decorator.encode( bb );
        bb.flip();
        javax.naming.ldap.BasicControl jndiControl = 
            new javax.naming.ldap.BasicControl( control.getOid(), control.isCritical(), bb.array() );
        return jndiControl;
    }


    public Control fromJndiControl( javax.naming.ldap.Control control ) throws DecoderException
    {
        IControlFactory factory = controlFactories.get( control.getID() );
        
        if ( factory == null )
        {
            AbstractControl ourControl = new OpaqueControlImpl( control.getID() );
            ourControl.setCritical( control.isCritical() );
            BasicControlDecorator decorator = new BasicControlDecorator( this, ourControl );
            decorator.setValue( control.getEncodedValue() );
            return decorator;
        }
        
        return factory.fromJndiControl( control );
    }
}
