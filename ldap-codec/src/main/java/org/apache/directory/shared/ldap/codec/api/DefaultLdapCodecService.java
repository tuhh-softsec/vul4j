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
package org.apache.directory.shared.ldap.codec.api;


import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.EncoderException;
import org.apache.directory.shared.asn1.ber.Asn1Container;
import org.apache.directory.shared.ldap.codec.BasicControlDecorator;
import org.apache.directory.shared.ldap.codec.LdapMessageContainer;
import org.apache.directory.shared.ldap.codec.controls.cascade.CascadeFactory;
import org.apache.directory.shared.ldap.codec.controls.manageDsaIT.ManageDsaITFactory;
import org.apache.directory.shared.ldap.codec.controls.search.entryChange.EntryChangeFactory;
import org.apache.directory.shared.ldap.codec.controls.search.pagedSearch.PagedResultsFactory;
import org.apache.directory.shared.ldap.codec.controls.search.persistentSearch.PersistentSearchFactory;
import org.apache.directory.shared.ldap.codec.controls.search.subentries.SubentriesFactory;
import org.apache.directory.shared.ldap.codec.decorators.MessageDecorator;
import org.apache.directory.shared.ldap.codec.protocol.mina.LdapProtocolCodecFactory;
import org.apache.directory.shared.ldap.extras.controls.PasswordPolicyImpl;
import org.apache.directory.shared.ldap.extras.controls.SyncDoneValue;
import org.apache.directory.shared.ldap.extras.controls.SyncInfoValue;
import org.apache.directory.shared.ldap.extras.controls.SyncModifyDn;
import org.apache.directory.shared.ldap.extras.controls.SyncRequestValue;
import org.apache.directory.shared.ldap.extras.controls.SyncStateValue;
import org.apache.directory.shared.ldap.extras.controls.ppolicy_impl.PasswordPolicyFactory;
import org.apache.directory.shared.ldap.extras.controls.syncrepl_impl.SyncDoneValueFactory;
import org.apache.directory.shared.ldap.extras.controls.syncrepl_impl.SyncInfoValueFactory;
import org.apache.directory.shared.ldap.extras.controls.syncrepl_impl.SyncModifyDnFactory;
import org.apache.directory.shared.ldap.extras.controls.syncrepl_impl.SyncRequestValueFactory;
import org.apache.directory.shared.ldap.extras.controls.syncrepl_impl.SyncStateValueFactory;
import org.apache.directory.shared.ldap.model.message.Control;
import org.apache.directory.shared.ldap.model.message.Message;
import org.apache.directory.shared.ldap.model.message.controls.Cascade;
import org.apache.directory.shared.ldap.model.message.controls.EntryChange;
import org.apache.directory.shared.ldap.model.message.controls.ManageDsaIT;
import org.apache.directory.shared.ldap.model.message.controls.OpaqueControl;
import org.apache.directory.shared.ldap.model.message.controls.PagedResults;
import org.apache.directory.shared.ldap.model.message.controls.PersistentSearch;
import org.apache.directory.shared.ldap.model.message.controls.Subentries;
import org.apache.directory.shared.util.exception.NotImplementedException;
import org.apache.mina.filter.codec.ProtocolCodecFactory;


/**
 * The default {@link LdapCodecService} implementation.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class DefaultLdapCodecService implements LdapCodecService
{
    Map<String,ControlFactory<?,?>> controlFactories = new HashMap<String, ControlFactory<?,?>>();
    Map<String,ExtendedOpFactory<?,?>> extReqFactories = new HashMap<String, ExtendedOpFactory<?,?>>();
    Map<String,ExtendedOpFactory<?,?>> extResFactories = new HashMap<String, ExtendedOpFactory<?,?>>();
    
    
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
        
        // @TODO - these will eventually be removed to enable plugin driven
        // registration instead
        
        SyncDoneValueFactory syncDoneValueFactory = new SyncDoneValueFactory( this );
        controlFactories.put( SyncDoneValue.OID, syncDoneValueFactory );
        
        SyncInfoValueFactory syncInfoValueFactory = new SyncInfoValueFactory( this );
        controlFactories.put( SyncInfoValue.OID, syncInfoValueFactory );
        
        SyncModifyDnFactory syncModifyDnFactory = new SyncModifyDnFactory( this );
        controlFactories.put( SyncModifyDn.OID, syncModifyDnFactory );
        
        SyncRequestValueFactory syncRequestValueFactory = new SyncRequestValueFactory( this );
        controlFactories.put( SyncRequestValue.OID, syncRequestValueFactory );

        SyncStateValueFactory syncStateValueFactory = new SyncStateValueFactory( this );
        controlFactories.put( SyncStateValue.OID, syncStateValueFactory );
        
        PasswordPolicyFactory passwordPolicyFactory = new PasswordPolicyFactory( this );
        controlFactories.put( PasswordPolicyImpl.OID, passwordPolicyFactory );
}
    

    //-------------------------------------------------------------------------
    // ILdapCodecService implementation methods
    //-------------------------------------------------------------------------
    
    
    /**
     * {@inheritDoc}
     */
    public void registerControl( ControlFactory<?,?> factory )
    {
        controlFactories.put( factory.getOid(), factory );
    }
    

    /**
     * {@inheritDoc}
     */
    public Iterator<String> registeredControls()
    {
        return Collections.unmodifiableSet( controlFactories.keySet() ).iterator();
    }
    

    /**
     * {@inheritDoc}
     */
    public Iterator<String> registeredExtendedRequests()
    {
        return Collections.unmodifiableSet( extReqFactories.keySet() ).iterator();
    }

    
    /**
     * {@inheritDoc}
     */
    public Iterator<String> registeredExtendedResponses()
    {
        return Collections.unmodifiableSet( extResFactories.keySet() ).iterator();
    }

    
    /**
     * {@inheritDoc}
     */
    public void registerExtendedOp( ExtendedOpFactory<?, ?> factory )
    {
        extReqFactories.put( factory.getRequestOid(), factory );
    }

    
    /**
     * {@inheritDoc}
     * 
     * @TODO - finish this up and add factory registration capabilities,
     * of course there is one default mechanism for now.
     */
    public ProtocolCodecFactory newProtocolCodecFactory( boolean client )
    {
        if ( client )
        {
            return new LdapProtocolCodecFactory();
        }
        else
        {
            throw new NotImplementedException( 
                "Filters may be different here, and we're probably going to " +
                "want to have a protocol codec factory registration mechanism" +
                "since this way we can swap in and out MINA/Grizzly" );
        }
    }

    
    /**
     * {@inheritDoc}
     */
    public CodecControl<? extends Control> newControl( String oid )
    {
        ControlFactory<?,?> factory = controlFactories.get( oid );
        
        if ( factory == null )
        {
            return new BasicControlDecorator<Control>( this, new OpaqueControl( oid ) );
        }
        
        return factory.newCodecControl();
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public CodecControl<? extends Control> newControl( Control control )
    {
        if ( control == null )
        {
            throw new NullPointerException( "Control argument was null." );
        }
        
        // protect agains being multiply decorated
        if ( control instanceof CodecControl )
        {
            return (org.apache.directory.shared.ldap.codec.api.CodecControl<?> )control;
        }
        
        @SuppressWarnings("rawtypes")
        ControlFactory factory = controlFactories.get( control.getOid() );
        
        if ( factory == null )
        {
            return new BasicControlDecorator<Control>( this, control ); 
        }
        
        return factory.newCodecControl( control );
    }


    /**
     * {@inheritDoc}
     */
    public javax.naming.ldap.Control toJndiControl( Control control ) throws EncoderException
    {
        CodecControl<? extends Control> decorator = newControl( control );
        ByteBuffer bb = ByteBuffer.allocate( decorator.computeLength() );
        decorator.encode( bb );
        bb.flip();
        javax.naming.ldap.BasicControl jndiControl = 
            new javax.naming.ldap.BasicControl( control.getOid(), control.isCritical(), bb.array() );
        return jndiControl;
    }


    /**
     * {@inheritDoc}
     */
    public Control fromJndiControl( javax.naming.ldap.Control control ) throws DecoderException
    {
        @SuppressWarnings("rawtypes")
        ControlFactory factory = controlFactories.get( control.getID() );
        
        if ( factory == null )
        {
            OpaqueControl ourControl = new OpaqueControl( control.getID() );
            ourControl.setCritical( control.isCritical() );
            BasicControlDecorator<Control> decorator = 
                new BasicControlDecorator<Control>( this, ourControl );
            decorator.setValue( control.getEncodedValue() );
            return decorator;
        }
        
        @SuppressWarnings("unchecked")
        CodecControl<? extends Control> ourControl = factory.newCodecControl();
        ourControl.setCritical( control.isCritical() );
        ourControl.setValue( control.getEncodedValue() );
        ourControl.decode( control.getEncodedValue() );
        return ourControl;
    }


    /**
     * {@inheritDoc}
     */
    public Asn1Container newMessageContainer()
    {
        return new LdapMessageContainer<MessageDecorator<? extends Message>>( this );
    }
}
