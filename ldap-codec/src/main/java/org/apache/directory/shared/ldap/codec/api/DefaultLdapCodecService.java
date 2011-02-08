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


import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
import org.apache.directory.shared.ldap.codec.osgi.CodecHostActivator;
import org.apache.directory.shared.ldap.codec.protocol.mina.LdapProtocolCodecFactory;
import org.apache.directory.shared.ldap.model.message.Control;
import org.apache.directory.shared.ldap.model.message.Message;
import org.apache.directory.shared.ldap.model.message.controls.OpaqueControl;
import org.apache.directory.shared.util.exception.NotImplementedException;
import org.apache.felix.framework.Felix;
import org.apache.felix.framework.util.FelixConstants;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The default {@link LdapCodecService} implementation.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class DefaultLdapCodecService implements LdapCodecService
{
    /** A logger */
    private static final Logger LOG = LoggerFactory.getLogger( DefaultLdapCodecService.class );
    
    /**
     * This should be constructed at class initialization time by reading the
     * Export-Package attribute of the this jar's manifest file.
     */
    private static final String[] SYSTEM_PACKAGES =
    {
        "org.apache.directory.shared.ldap.codec.api; version=1.0.0"
    };
 
    /** System property checked if the pluginProperty is null */
    public static final String PLUGIN_DIRECTORY_PROPERTY = DefaultLdapCodecService.class.getName() + 
        ".pluginDirectory";
    
    /** The map of registered {@link ControlFactory}'s */
    private Map<String,ControlFactory<?,?>> controlFactories = new HashMap<String, ControlFactory<?,?>>();

    /** The map of registered {@link ExtendedOpFactory}'s by request OID */
    private Map<String,ExtendedOpFactory<?,?>> extReqFactories = new HashMap<String, ExtendedOpFactory<?,?>>();
    
    /** The map of registered {@link ExtendedOpFactory}'s by response OID */
    private Map<String,ExtendedOpFactory<?,?>> extResFactories = new HashMap<String, ExtendedOpFactory<?,?>>();
    
    /** The codec's {@link BundleActivator} */
    private CodecHostActivator activator;
    
    /** The embedded {@link Felix} instance */
    private Felix felix;
    
    /** The plugin (bundle) containing directory to load codec extensions from */
    private final File pluginDirectory;
    
    
    /**
     * Creates a new instance of DefaultLdapCodecService.
     */
    public DefaultLdapCodecService()
    {
        this( getPluginDirectorySystemValue() );
    }
    
    
    private static File getPluginDirectorySystemValue()
    {
        String value = System.getProperty( DefaultLdapCodecService.PLUGIN_DIRECTORY_PROPERTY );
        
        if ( value == null )
        {
            return null;
        }
        
        return new File( value );
    }
    
    
    /**
     * Creates a new instance of DefaultLdapCodecService.
     */
    public DefaultLdapCodecService( File pluginDirectory )
    {
        this.pluginDirectory = pluginDirectory;
        
        if ( pluginDirectory == null )
        {
            // do nothing
        }
        else if ( ! pluginDirectory.isDirectory() )
        {
            String msg = "The provided plugin directory is not a directory:" + pluginDirectory.getAbsolutePath();
            LOG.error( msg );
            throw new IllegalArgumentException( msg );
        }
        else if ( ! pluginDirectory.canRead() )
        {
            String msg = "The provided plugin directory is not readable:" + pluginDirectory.getAbsolutePath();
            LOG.error( msg );
            throw new IllegalArgumentException( msg );
        }
        
        loadStockControls();
        setupFelix();
    }
    
    
    /**
     * Assembles the <code>org.osgi.framework.system.packages.extra</code> list
     * of system packages exported by the embedding host to interact with bundles
     * running inside {@link Felix}.
     * 
     * @return A comma delimited list of exported host packages.
     */
    private String getSystemPackages()
    {
        StringBuilder sb = new StringBuilder();
        int ii = 0;
        
        do
        {
            // add comma if we're not at start and have more left
            if ( ii > 0 && ii < SYSTEM_PACKAGES.length - 1 )
            {
                sb.append( ',' );
            }
            
            sb.append( SYSTEM_PACKAGES[ii] );
            ii++;
        }
        while( ii < SYSTEM_PACKAGES.length );
        
        return sb.toString();
    }
    
    
    /**
     * Sets up a {@link Felix} instance.
     */
    private void setupFelix()
    {
        // initialize activator and setup system bundle activators
        activator = new CodecHostActivator( this );
        List<BundleActivator> activators = new ArrayList<BundleActivator>();
        activators.add( activator );
        
        // setup configuration for felix 
        Map<String, Object> config = new HashMap<String, Object>();
        config.put( FelixConstants.SYSTEMBUNDLE_ACTIVATORS_PROP, activators );
        config.put( FelixConstants.FRAMEWORK_SYSTEMPACKAGES_EXTRA, getSystemPackages() );
        
        // @TODO - this must be overridden by tests to be false in surefire configs
        // @TODO - remove this and make it a property on this class which is true 
        //         by default unless overridden by surefire's configuration.
        config.put( "felix.cache.locking", false );
        
        // instantiate and start up felix
        felix = new Felix( config );
        try
        {
            felix.start();
        }
        catch ( BundleException e )
        {
            String message = "Failed to start embedded felix instance: " + e.getMessage();
            LOG.error( message, e );
            throw new RuntimeException( message, e );
        }
    }
    
    
    /**
     * Shuts down the codec and its embedded {@link Felix} instance.
     */
    public void shutdown()
    {
        try
        {
            felix.stop();
            felix.waitForStop( 0 );
        }
        catch ( Exception e )
        {
            String message = "Failed to stop embedded felix instance: " + e.getMessage();
            LOG.error( message, e );
            throw new RuntimeException( message, e );
        }
    }
    
    
    /**
     * Loads the Controls implement out of the box in the codec.
     */
    private void loadStockControls()
    {
        ControlFactory<?, ?> factory = new CascadeFactory( this );
        controlFactories.put( factory.getOid(), factory );
        LOG.info( "Registered Stock Control: {}", factory.getOid() );
        
        factory = new EntryChangeFactory( this );
        controlFactories.put( factory.getOid(), factory );
        
        factory = new ManageDsaITFactory( this );
        controlFactories.put( factory.getOid(), factory );
        
        factory = new PagedResultsFactory( this );
        controlFactories.put( factory.getOid(), factory );
        
        factory = new PersistentSearchFactory( this );
        controlFactories.put( factory.getOid(), factory );

        factory = new SubentriesFactory( this );
        controlFactories.put( factory.getOid(), factory );
}
    

    //-------------------------------------------------------------------------
    // LdapCodecService implementation methods
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
    public void unregisterControl( String oid )
    {
        controlFactories.remove( oid );
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


    /**
     * Gets the plugin directory containing codec extension bundles to load. 
     * If null, the service checks to see if system properties were used to 
     * specify the plugin directory. 
     *
     * @see {@link #PLUGIN_DIRECTORY_PROPERTY}
     * @return The directory containing plugins.
     */
    public File getPluginDirectory()
    {
        return pluginDirectory;
    }
}
