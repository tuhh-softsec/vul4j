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
package org.apache.directory.ldap.client.api;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.SSLContext;
import javax.security.auth.Subject;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import javax.security.sasl.Sasl;
import javax.security.sasl.SaslClient;

import org.apache.directory.ldap.client.api.callback.SaslCallbackHandler;
import org.apache.directory.ldap.client.api.exception.InvalidConnectionException;
import org.apache.directory.ldap.client.api.future.AddFuture;
import org.apache.directory.ldap.client.api.future.BindFuture;
import org.apache.directory.ldap.client.api.future.CompareFuture;
import org.apache.directory.ldap.client.api.future.DeleteFuture;
import org.apache.directory.ldap.client.api.future.ExtendedFuture;
import org.apache.directory.ldap.client.api.future.ModifyDnFuture;
import org.apache.directory.ldap.client.api.future.ModifyFuture;
import org.apache.directory.ldap.client.api.future.ResponseFuture;
import org.apache.directory.ldap.client.api.future.SearchFuture;
import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.util.OID;
import org.apache.directory.shared.ldap.codec.api.LdapCodecService;
import org.apache.directory.shared.ldap.codec.api.LdapCodecServiceFactory;
import org.apache.directory.shared.ldap.codec.api.MessageEncoderException;
import org.apache.directory.shared.ldap.model.constants.SaslQoP;
import org.apache.directory.shared.ldap.model.constants.SaslSecurityStrength;
import org.apache.directory.shared.ldap.model.constants.SchemaConstants;
import org.apache.directory.shared.ldap.model.cursor.Cursor;
import org.apache.directory.shared.ldap.model.cursor.SearchCursor;
import org.apache.directory.shared.ldap.model.entry.DefaultEntry;
import org.apache.directory.shared.ldap.model.entry.Entry;
import org.apache.directory.shared.ldap.model.entry.EntryAttribute;
import org.apache.directory.shared.ldap.model.entry.Modification;
import org.apache.directory.shared.ldap.model.entry.ModificationOperation;
import org.apache.directory.shared.ldap.model.entry.Value;
import org.apache.directory.shared.ldap.model.exception.LdapException;
import org.apache.directory.shared.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.shared.ldap.model.exception.LdapNoPermissionException;
import org.apache.directory.shared.ldap.model.exception.LdapOperationException;
import org.apache.directory.shared.ldap.model.filter.SearchScope;
import org.apache.directory.shared.ldap.model.message.AbandonRequest;
import org.apache.directory.shared.ldap.model.message.AbandonRequestImpl;
import org.apache.directory.shared.ldap.model.message.AddRequest;
import org.apache.directory.shared.ldap.model.message.AddRequestImpl;
import org.apache.directory.shared.ldap.model.message.AddResponse;
import org.apache.directory.shared.ldap.model.message.AliasDerefMode;
import org.apache.directory.shared.ldap.model.message.BindRequest;
import org.apache.directory.shared.ldap.model.message.BindRequestImpl;
import org.apache.directory.shared.ldap.model.message.BindResponse;
import org.apache.directory.shared.ldap.model.message.CompareRequest;
import org.apache.directory.shared.ldap.model.message.CompareRequestImpl;
import org.apache.directory.shared.ldap.model.message.CompareResponse;
import org.apache.directory.shared.ldap.model.message.Control;
import org.apache.directory.shared.ldap.model.message.DeleteRequest;
import org.apache.directory.shared.ldap.model.message.DeleteRequestImpl;
import org.apache.directory.shared.ldap.model.message.DeleteResponse;
import org.apache.directory.shared.ldap.model.message.ExtendedRequest;
import org.apache.directory.shared.ldap.model.message.ExtendedRequestImpl;
import org.apache.directory.shared.ldap.model.message.ExtendedResponse;
import org.apache.directory.shared.ldap.model.message.IntermediateResponse;
import org.apache.directory.shared.ldap.model.message.IntermediateResponseImpl;
import org.apache.directory.shared.ldap.model.message.LdapResult;
import org.apache.directory.shared.ldap.model.message.Message;
import org.apache.directory.shared.ldap.model.message.ModifyDnRequest;
import org.apache.directory.shared.ldap.model.message.ModifyDnRequestImpl;
import org.apache.directory.shared.ldap.model.message.ModifyDnResponse;
import org.apache.directory.shared.ldap.model.message.ModifyRequest;
import org.apache.directory.shared.ldap.model.message.ModifyRequestImpl;
import org.apache.directory.shared.ldap.model.message.ModifyResponse;
import org.apache.directory.shared.ldap.model.message.Response;
import org.apache.directory.shared.ldap.model.message.ResultCodeEnum;
import org.apache.directory.shared.ldap.model.message.SearchRequest;
import org.apache.directory.shared.ldap.model.message.SearchRequestImpl;
import org.apache.directory.shared.ldap.model.message.SearchResultDone;
import org.apache.directory.shared.ldap.model.message.SearchResultEntry;
import org.apache.directory.shared.ldap.model.message.SearchResultReference;
import org.apache.directory.shared.ldap.model.message.UnbindRequest;
import org.apache.directory.shared.ldap.model.message.UnbindRequestImpl;
import org.apache.directory.shared.ldap.model.message.controls.OpaqueControl;
import org.apache.directory.shared.ldap.model.message.extended.AddNoDResponse;
import org.apache.directory.shared.ldap.model.message.extended.BindNoDResponse;
import org.apache.directory.shared.ldap.model.message.extended.CompareNoDResponse;
import org.apache.directory.shared.ldap.model.message.extended.DeleteNoDResponse;
import org.apache.directory.shared.ldap.model.message.extended.ExtendedNoDResponse;
import org.apache.directory.shared.ldap.model.message.extended.ModifyDnNoDResponse;
import org.apache.directory.shared.ldap.model.message.extended.ModifyNoDResponse;
import org.apache.directory.shared.ldap.model.message.extended.NoticeOfDisconnect;
import org.apache.directory.shared.ldap.model.message.extended.SearchNoDResponse;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.apache.directory.shared.ldap.model.name.Rdn;
import org.apache.directory.shared.ldap.model.schema.AttributeType;
import org.apache.directory.shared.ldap.model.schema.ObjectClass;
import org.apache.directory.shared.ldap.model.schema.SchemaManager;
import org.apache.directory.shared.ldap.model.schema.parsers.OpenLdapSchemaParser;
import org.apache.directory.shared.ldap.model.schema.registries.AttributeTypeRegistry;
import org.apache.directory.shared.ldap.model.schema.registries.ObjectClassRegistry;
import org.apache.directory.shared.ldap.model.schema.registries.Schema;
import org.apache.directory.shared.ldap.model.schema.registries.SchemaLoader;
import org.apache.directory.shared.ldap.schemaloader.JarLdifSchemaLoader;
import org.apache.directory.shared.ldap.schemamanager.impl.DefaultSchemaManager;
import org.apache.directory.shared.util.StringConstants;
import org.apache.directory.shared.util.Strings;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.ProtocolEncoderException;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class is the base for every operations sent or received to and
 * from a LDAP server.
 *
 * A connection instance is necessary to send requests to the server. The connection
 * is valid until either the client closes it, the server closes it or the
 * client does an unbind.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdapNetworkConnection extends IoHandlerAdapter implements LdapAsyncConnection
{
    /** logger for reporting errors that might not be handled properly upstream */
    private static final Logger LOG = LoggerFactory.getLogger( LdapNetworkConnection.class );

    /** The timeout used for response we are waiting for */
    private long timeout = LdapConnectionConfig.DEFAULT_TIMEOUT;

    /** configuration object for the connection */
    private LdapConnectionConfig config;

    /** The connector open with the remote server */
    private IoConnector connector;

    /** A flag set to true when we used a local connector */
    private boolean localConnector;

    /**
     * The created session, created when we open a connection with
     * the Ldap server.
     */
    private IoSession ldapSession;

    /** A Message ID which is incremented for each operation */
    private AtomicInteger messageId;

    /** a map to hold the ResponseFutures for all operations */
    private Map<Integer, ResponseFuture<? extends Response>> futureMap = new ConcurrentHashMap<Integer, ResponseFuture<? extends Response>>();

    /** list of controls supported by the server */
    private List<String> supportedControls;

    /** The ROOT DSE entry */
    private Entry rootDSE;

    /** A flag indicating that the BindRequest has been issued and successfully authenticated the user */
    private AtomicBoolean authenticated = new AtomicBoolean( false );

    /** A flag indicating that the connection is connected or not */
    private AtomicBoolean connected = new AtomicBoolean( false );

    /** a list of listeners interested in getting notified when the
     *  connection's session gets closed cause of network issues
     */
    private List<ConnectionClosedEventListener> conCloseListeners;

    /** the schema manager */
    private SchemaManager schemaManager;

    /** the ldap codec service */
    LdapCodecService codec = LdapCodecServiceFactory.getSingleton();

    /** The Ldap codec protocol filter */
    private IoFilter ldapProtocolFilter = new ProtocolCodecFilter( codec.newProtocolCodecFactory( true ) );

    /** the SslFilter key */
    private static final String SSL_FILTER_KEY = "sslFilter";

    /** the StartTLS extended operation's OID */
    private static final String START_TLS_REQ_OID = "1.3.6.1.4.1.1466.20037";

    // ~~~~~~~~~~~~~~~~~ common error messages ~~~~~~~~~~~~~~~~~~~~~~~~~~

    static final String TIME_OUT_ERROR = "TimeOut occured";

    static final String NO_RESPONSE_ERROR = "The response queue has been emptied, no response was found.";


    //--------------------------- Helper methods ---------------------------//
    /**
     * {@inheritDoc}
     */
    public boolean isConnected()
    {
        return ( ldapSession != null ) && connected.get();
    }


    /**
     * {@inheritDoc}
     */
    public boolean isAuthenticated()
    {
        return isConnected() && authenticated.get();
    }


    /**
     * Check that a session is valid, ie we can send requests to the
     * server
     *
     * @throws Exception If the session is not valid
     */
    private void checkSession() throws InvalidConnectionException
    {
        if ( ldapSession == null )
        {
            throw new InvalidConnectionException( "Cannot connect on the server, the connection is null" );
        }

        if ( !connected.get() )
        {
            throw new InvalidConnectionException( "Cannot connect on the server, the connection is invalid" );
        }
    }


    private void addToFutureMap( int messageId, ResponseFuture<? extends Response> future )
    {
        LOG.debug( "Adding <" + messageId + ", " + future.getClass().getName() + ">" );
        futureMap.put( messageId, future );
    }


    private ResponseFuture<? extends Response> getFromFutureMap( int messageId )
    {
        ResponseFuture<? extends Response> future = futureMap.remove( messageId );

        if ( future != null )
        {
            LOG.debug( "Removing <" + messageId + ", " + future.getClass().getName() + ">" );
        }

        return future;
    }


    private ResponseFuture<? extends Response> peekFromFutureMap( int messageId )
    {
        ResponseFuture<? extends Response> future = futureMap.get( messageId );

        // future can be null if there was a abandon operation on that messageId
        if ( future != null )
        {
            LOG.debug( "Getting <" + messageId + ", " + future.getClass().getName() + ">" );
        }

        return future;
    }


    /**
     * Get the smallest timeout from the client timeout and the connection
     * timeout.
     */
    private long getTimeout( long clientTimeout )
    {
        if ( clientTimeout <= 0 )
        {
            return ( timeout <= 0 ) ? Long.MAX_VALUE : timeout;
        }
        else if ( timeout <= 0 )
        {
            return clientTimeout;
        }
        else
        {
            return timeout < clientTimeout ? timeout : clientTimeout;
        }
    }


    //------------------------- The constructors --------------------------//
    /**
     * Create a new instance of a LdapConnection on localhost,
     * port 389.
     */
    public LdapNetworkConnection()
    {
        config = new LdapConnectionConfig();
        config.setUseSsl( false );
        config.setLdapPort( config.getDefaultLdapPort() );
        config.setLdapHost( config.getDefaultLdapHost() );
        messageId = new AtomicInteger( 0 );
    }


    /**
     *
     * Creates a new instance of LdapConnection with the given connection configuration.
     *
     * @param config the configuration of the LdapConnection
     */
    public LdapNetworkConnection( LdapConnectionConfig config )
    {
        this.config = config;
        messageId = new AtomicInteger( 0 );

    }


    /**
     * Create a new instance of a LdapConnection on localhost,
     * port 389 if the SSL flag is off, or 636 otherwise.
     *
     * @param useSsl A flag to tell if it's a SSL connection or not.
     */
    public LdapNetworkConnection( boolean useSsl )
    {
        config = new LdapConnectionConfig();
        config.setUseSsl( useSsl );
        config.setLdapPort( useSsl ? config.getDefaultLdapsPort() : config.getDefaultLdapPort() );
        config.setLdapHost( config.getDefaultLdapHost() );
        messageId = new AtomicInteger( 0 );
    }


    /**
     * Create a new instance of a LdapConnection on a given
     * server, using the default port (389).
     *
     * @param server The server we want to be connected to. If null or empty,
     * we will default to LocalHost.
     */
    public LdapNetworkConnection( String server )
    {
        config = new LdapConnectionConfig();
        config.setUseSsl( false );
        config.setLdapPort( config.getDefaultLdapPort() );

        // Default to localhost if null
        if ( Strings.isEmpty( server ) )
        {
            config.setLdapHost( "localhost" );
        }
        else
        {
            config.setLdapHost( server );
        }

        messageId = new AtomicInteger( 0 );
    }


    /**
     * Create a new instance of a LdapConnection on a given
     * server, using the default port (389) if the SSL flag
     * is off, or 636 otherwise.
     *
     * @param server The server we want to be connected to. If null or empty,
     * we will default to LocalHost.
     * @param useSsl A flag to tell if it's a SSL connection or not.
     */
    public LdapNetworkConnection( String server, boolean useSsl )
    {
        config = new LdapConnectionConfig();
        config.setUseSsl( useSsl );
        config.setLdapPort( useSsl ? config.getDefaultLdapsPort() : config.getDefaultLdapPort() );

        // Default to localhost if null
        if ( Strings.isEmpty( server ) )
        {
            config.setLdapHost( "localhost" );
        }
        else
        {
            config.setLdapHost( server );
        }

        messageId = new AtomicInteger( 0 );
    }


    /**
     * Create a new instance of a LdapConnection on a
     * given server and a given port. We don't use ssl.
     *
     * @param server The server we want to be connected to
     * @param port The port the server is listening to
     */
    public LdapNetworkConnection( String server, int port )
    {
        this( server, port, false );
    }


    /**
     * Create a new instance of a LdapConnection on a given
     * server, and a give port. We set the SSL flag accordingly
     * to the last parameter.
     *
     * @param server The server we want to be connected to. If null or empty,
     * we will default to LocalHost.
     * @param port The port the server is listening to
     * @param useSsl A flag to tell if it's a SSL connection or not.
     */
    public LdapNetworkConnection( String server, int port, boolean useSsl )
    {
        config = new LdapConnectionConfig();
        config.setUseSsl( useSsl );
        config.setLdapPort( port );

        // Default to localhost if null
        if ( Strings.isEmpty( server ) )
        {
            config.setLdapHost( "localhost" );
        }
        else
        {
            config.setLdapHost( server );
        }

        messageId = new AtomicInteger();
    }


    //-------------------------- The methods ---------------------------//
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("PMD.EmptyCatchBlock")
    public boolean connect() throws LdapException, IOException
    {
        if ( ( ldapSession != null ) && connected.get() )
        {
            // No need to connect if we already have a connected session
            return true;
        }

        // Create the connector if needed
        if ( connector == null )
        {
            connector = new NioSocketConnector();
            localConnector = true;

            // Add the codec to the chain
            connector.getFilterChain().addLast( "ldapCodec", ldapProtocolFilter );

            // If we use SSL, we have to add the SslFilter to the chain
            if ( config.isUseSsl() )
            {
                addSslFilter();
            }

            // Add an executor so that this connection can be used
            // for handling more than one request (mainly because
            // we may have to handle some abandon request)
            /*connector.getFilterChain().addLast( "executor",
                new ExecutorFilter( new OrderedThreadPoolExecutor( 10 ), IoEventType.MESSAGE_RECEIVED ) );*/

            // Inject the protocolHandler
            connector.setHandler( this );
        }

        // Build the connection address
        SocketAddress address = new InetSocketAddress( config.getLdapHost(), config.getLdapPort() );

        // And create the connection future
        ConnectFuture connectionFuture = connector.connect( address );

        // Wait until it's established
        connectionFuture.awaitUninterruptibly();

        if ( !connectionFuture.isConnected() )
        {
            // disposing connector if not connected
            try
            {
                close();
            }
            catch ( IOException ioe )
            {
                // Nothing to do
            }

            return false;
        }

        // Get the close future for this session
        CloseFuture closeFuture = connectionFuture.getSession().getCloseFuture();

        // Add a listener to close the session in the session.
        closeFuture.addListener( new IoFutureListener<IoFuture>()
        {
            public void operationComplete( IoFuture future )
            {
                // Process all the waiting operations and cancel them
                LOG.debug( "received a NoD, closing everything" );

                for ( int messageId : futureMap.keySet() )
                {
                    ResponseFuture<?> responseFuture = futureMap.get( messageId );
                    LOG.debug( "closing {}", responseFuture );

                    responseFuture.cancel();

                    try
                    {
                        if ( responseFuture instanceof AddFuture )
                        {
                            ( ( AddFuture ) responseFuture ).set( AddNoDResponse.PROTOCOLERROR );
                        }
                        else if ( responseFuture instanceof BindFuture )
                        {
                            ( ( BindFuture ) responseFuture ).set( BindNoDResponse.PROTOCOLERROR );
                        }
                        else if ( responseFuture instanceof CompareFuture )
                        {
                            ( ( CompareFuture ) responseFuture ).set( CompareNoDResponse.PROTOCOLERROR );
                        }
                        else if ( responseFuture instanceof DeleteFuture )
                        {
                            ( ( DeleteFuture ) responseFuture ).set( DeleteNoDResponse.PROTOCOLERROR );
                        }
                        else if ( responseFuture instanceof ExtendedFuture )
                        {
                            ( ( ExtendedFuture ) responseFuture ).set( ExtendedNoDResponse.PROTOCOLERROR );
                        }
                        else if ( responseFuture instanceof ModifyFuture )
                        {
                            ( ( ModifyFuture ) responseFuture ).set( ModifyNoDResponse.PROTOCOLERROR );
                        }
                        else if ( responseFuture instanceof ModifyDnFuture )
                        {
                            ( ( ModifyDnFuture ) responseFuture ).set( ModifyDnNoDResponse.PROTOCOLERROR );
                        }
                        else if ( responseFuture instanceof SearchFuture )
                        {
                            ( ( SearchFuture ) responseFuture ).set( SearchNoDResponse.PROTOCOLERROR );
                        }
                    }
                    catch ( Exception e )
                    {
                        LOG.error( "Error while processing the NoD for {}", responseFuture );
                    }

                    futureMap.remove( messageId );
                }

                futureMap.clear();
            }
        } );

        // Get back the session
        ldapSession = connectionFuture.getSession();
        connected.set( true );

        // Store the container into the session
        ldapSession.setAttribute( "LDAP-Container", codec.newMessageContainer() );

        // Initialize the MessageId
        messageId.set( 0 );

        // And return
        return true;
    }


    /**
     * {@inheritDoc}
     */
    public boolean close() throws IOException
    {
        // Close the session
        if ( ( ldapSession != null ) && connected.get() )
        {
            ldapSession.close( true );
            connected.set( false );
        }

        // And close the connector if it has been created locally
        if ( localConnector && ( connector != null ) )
        {
            // Release the connector
            connector.dispose();
            connector = null;
        }

        // Reset the messageId
        messageId.set( 0 );

        return true;
    }


    //------------------------ The LDAP operations ------------------------//
    // Add operations                                                      //
    //---------------------------------------------------------------------//
    /**
     * {@inheritDoc}
     */
    public AddResponse add( Entry entry ) throws LdapException
    {
        if ( entry == null )
        {
            String msg = "Cannot add an empty entry";
            LOG.debug( msg );
            throw new IllegalArgumentException( msg );
        }

        AddRequest addRequest = new AddRequestImpl();
        addRequest.setEntry( entry );

        return add( addRequest );
    }


    /**
     * {@inheritDoc}
     */
    public AddFuture addAsync( Entry entry ) throws LdapException
    {
        if ( entry == null )
        {
            String msg = "Cannot add null entry";
            LOG.debug( msg );
            throw new IllegalArgumentException( msg );
        }

        AddRequest addRequest = new AddRequestImpl();
        addRequest.setEntry( entry );

        return addAsync( addRequest );
    }


    /**
     * {@inheritDoc}
     */
    public AddResponse add( AddRequest addRequest ) throws LdapException
    {
        if ( addRequest == null )
        {
            String msg = "Cannot process a null addRequest";
            LOG.debug( msg );
            throw new IllegalArgumentException( msg );
        }

        if ( addRequest.getEntry() == null )
        {
            String msg = "Cannot add a null entry";
            LOG.debug( msg );
            throw new IllegalArgumentException( msg );
        }

        AddFuture addFuture = addAsync( addRequest );

        // Get the result from the future
        try
        {
            // Read the response, waiting for it if not available immediately
            // Get the response, blocking
            AddResponse addResponse = addFuture.get( timeout, TimeUnit.MILLISECONDS );

            if ( addResponse == null )
            {
                // We didn't received anything : this is an error
                LOG.error( "Add failed : timeout occured" );
                throw new LdapException( TIME_OUT_ERROR );
            }

            if ( addResponse.getLdapResult().getResultCode() == ResultCodeEnum.SUCCESS )
            {
                // Everything is fine, return the response
                LOG.debug( "Add successful : {}", addResponse );
            }
            else
            {
                // We have had an error
                LOG.debug( "Add failed : {}", addResponse );
            }

            return addResponse;
        }
        catch ( TimeoutException te )
        {
            // Send an abandon request
            if ( !addFuture.isCancelled() )
            {
                abandon( addRequest.getMessageId() );
            }

            // We didn't received anything : this is an error
            LOG.error( "Add failed : timeout occured" );
            throw new LdapException( TIME_OUT_ERROR );
        }
        catch ( Exception ie )
        {
            // Catch all other exceptions
            LOG.error( NO_RESPONSE_ERROR, ie );
            LdapException ldapException = new LdapException( NO_RESPONSE_ERROR );
            ldapException.initCause( ie );

            // Send an abandon request
            if ( !addFuture.isCancelled() )
            {
                abandon( addRequest.getMessageId() );
            }

            throw ldapException;
        }
    }


    /**
     * {@inheritDoc}
     */
    public AddFuture addAsync( AddRequest addRequest ) throws LdapException
    {
        if ( addRequest == null )
        {
            String msg = "Cannot process a null addRequest";
            LOG.debug( msg );
            throw new IllegalArgumentException( msg );
        }

        if ( addRequest.getEntry() == null )
        {
            String msg = "Cannot add a null entry";
            LOG.debug( msg );
            throw new IllegalArgumentException( msg );
        }

        checkSession();

        int newId = messageId.incrementAndGet();

        addRequest.setMessageId( newId );
        AddFuture addFuture = new AddFuture( this, newId );
        addToFutureMap( newId, addFuture );

        // Send the request to the server
        WriteFuture writeFuture = ldapSession.write( addRequest );

        // Wait for the message to be sent to the server
        if ( !writeFuture.awaitUninterruptibly( timeout ) )
        {
            // We didn't received anything : this is an error
            LOG.error( "Add failed : timeout occured" );

            throw new LdapException( TIME_OUT_ERROR );
        }

        // Ok, done return the future
        return addFuture;
    }


    //------------------------ The LDAP operations ------------------------//

    /**
     * {@inheritDoc}
     */
    public void abandon( int messageId )
    {
        if ( messageId < 0 )
        {
            String msg = "Cannot abandon a negative message ID";
            LOG.debug( msg );
            throw new IllegalArgumentException( msg );
        }

        AbandonRequest abandonRequest = new AbandonRequestImpl();
        abandonRequest.setAbandoned( messageId );

        abandonInternal( abandonRequest );
    }


    /**
     * {@inheritDoc}
     */
    public void abandon( AbandonRequest abandonRequest )
    {
        if ( abandonRequest == null )
        {
            String msg = "Cannot process a null abandonRequest";
            LOG.debug( msg );
            throw new IllegalArgumentException( msg );
        }

        abandonInternal( abandonRequest );
    }


    /**
     * Internal AbandonRequest handling
     */
    private void abandonInternal( AbandonRequest abandonRequest )
    {
        LOG.debug( "-----------------------------------------------------------------" );
        LOG.debug( "Sending request \n{}", abandonRequest );

        int newId = messageId.incrementAndGet();
        abandonRequest.setMessageId( newId );

        // Send the request to the server
        ldapSession.write( abandonRequest );

        // remove the associated listener if any
        int abandonId = abandonRequest.getAbandoned();

        ResponseFuture<? extends Response> rf = getFromFutureMap( abandonId );

        // if the listener is not null, this is a async operation and no need to
        // send cancel signal on future, sending so will leave a dangling poision object in the corresponding queue
        // this is a sync operation send cancel signal to the corresponding ResponseFuture
        if ( rf != null )
        {
            LOG.debug( "sending cancel signal to future" );
            rf.cancel( true );
        }
        else
        {
            // this shouldn't happen
            LOG
                .error(
                    "There is no future associated with operation message ID {}, perhaps the operation would have been completed",
                    abandonId );
        }
    }


    /**
     * {@inheritDoc}
     */
    public BindResponse bind() throws LdapException, IOException
    {
        LOG.debug( "Anonymous Bind request" );

        // Create the BindRequest
        BindRequest bindRequest = createBindRequest( StringConstants.EMPTY, StringConstants.EMPTY_BYTES );

        return bind( bindRequest );
    }


    /**
     * {@inheritDoc}
     */
    public BindFuture bindAsync() throws LdapException, IOException
    {
        LOG.debug( "Anonymous Bind request" );

        // Create the BindRequest
        BindRequest bindRequest = createBindRequest( StringConstants.EMPTY, StringConstants.EMPTY_BYTES );

        return bindAsync( bindRequest );
    }


    /**
     * {@inheritDoc}
     */
    public BindResponse bind( String name, String credentials ) throws LdapException, IOException
    {
        LOG.debug( "Bind request : {}", name );

        // Create the BindRequest
        BindRequest bindRequest = createBindRequest( name, Strings.getBytesUtf8( credentials ) );

        return bind( bindRequest );
    }


    /**
     * {@inheritDoc}
     */
    public BindFuture bindAsync( String name, String credentials ) throws LdapException, IOException
    {
        LOG.debug( "Bind request : {}", name );

        // Create the BindRequest
        BindRequest bindRequest = createBindRequest( name, Strings.getBytesUtf8( credentials ) );

        return bindAsync( bindRequest );
    }


    /**
     * {@inheritDoc}
     */
    public BindResponse bind( Dn name, String credentials ) throws LdapException, IOException
    {
        LOG.debug( "Bind request : {}", name );

        // Create the BindRequest
        BindRequest bindRequest = createBindRequest( name, Strings.getBytesUtf8( credentials ), null );

        return bind( bindRequest );
    }


    /**
     * {@inheritDoc}
     */
    public BindFuture bindAsync( Dn name, String credentials ) throws LdapException, IOException
    {
        LOG.debug( "Bind request : {}", name );

        // Create the BindRequest
        BindRequest bindRequest = createBindRequest( name, Strings.getBytesUtf8( credentials ) );

        return bindAsync( bindRequest );
    }


    /**
     * {@inheritDoc}
     */
    public BindResponse bind( BindRequest bindRequest ) throws LdapException, IOException
    {
        if ( bindRequest == null )
        {
            String msg = "Cannot process a null bindRequest";
            LOG.debug( msg );
            throw new IllegalArgumentException( msg );
        }

        BindFuture bindFuture = bindAsync( bindRequest );

        // Get the result from the future
        try
        {
            // Read the response, waiting for it if not available immediately
            // Get the response, blocking
            BindResponse bindResponse = bindFuture.get( timeout, TimeUnit.MILLISECONDS );

            if ( bindResponse == null )
            {
                // We didn't received anything : this is an error
                LOG.error( "Bind failed : timeout occured" );
                throw new LdapException( TIME_OUT_ERROR );
            }

            if ( bindResponse.getLdapResult().getResultCode() == ResultCodeEnum.SUCCESS )
            {
                authenticated.set( true );

                // Everything is fine, return the response
                LOG.debug( "Bind successful : {}", bindResponse );
            }
            else
            {
                // We have had an error
                LOG.debug( "Bind failed : {}", bindResponse );
            }

            return bindResponse;
        }
        catch ( TimeoutException te )
        {
            // We didn't received anything : this is an error
            LOG.error( "Bind failed : timeout occured" );
            throw new LdapException( TIME_OUT_ERROR );
        }
        catch ( Exception ie )
        {
            // Catch all other exceptions
            LOG.error( NO_RESPONSE_ERROR, ie );
            LdapException ldapException = new LdapException( NO_RESPONSE_ERROR );
            ldapException.initCause( ie );
            throw ldapException;
        }
    }


    /**
     * Create a Simple BindRequest ready to be sent.
     */
    private BindRequest createBindRequest( String name, byte[] credentials ) throws LdapException
    {
        return createBindRequest( name, credentials, null, ( Control[] ) null );
    }


    /**
     * Create a Simple BindRequest ready to be sent.
     */
    private BindRequest createBindRequest( Dn name, byte[] credentials ) throws LdapException
    {
        return createBindRequest( name, credentials, null, ( Control[] ) null );
    }


    /**
     * Create a complete BindRequest ready to be sent.
     */
    private BindRequest createBindRequest( String name, byte[] credentials, String saslMechanism, Control... controls )
        throws LdapException
    {
        // Set the name
        try
        {
            Dn dn = new Dn( name );

            return createBindRequest( dn, credentials, saslMechanism, controls );
        }
        catch ( LdapInvalidDnException ine )
        {
            String msg = "The given dn '" + name + "' is not valid";
            LOG.error( msg );
            LdapException ldapException = new LdapException( msg );
            ldapException.initCause( ine );

            throw ldapException;
        }
    }


    /**
     * Create a complete BindRequest ready to be sent.
     */
    private BindRequest createBindRequest( Dn name, byte[] credentials, String saslMechanism, Control... controls )
        throws LdapException
    {
        // Set the new messageId
        BindRequest bindRequest = new BindRequestImpl();

        // Set the version
        bindRequest.setVersion3( true );

        // Set the name
        bindRequest.setName( name );

        // Set the credentials
        if ( Strings.isEmpty( saslMechanism ) )
        {
            // Simple bind
            bindRequest.setSimple( true );
            bindRequest.setCredentials( credentials );
        }
        else
        {
            // SASL bind
            bindRequest.setSimple( false );
            bindRequest.setCredentials( credentials );
            bindRequest.setSaslMechanism( saslMechanism );
        }

        // Add the controls
        if ( ( controls != null ) && ( controls.length != 0 ) )
        {
            bindRequest.addAllControls( controls );
        }

        return bindRequest;
    }


    /**
     * {@inheritDoc}
     */
    public BindFuture bindAsync( BindRequest bindRequest ) throws LdapException, IOException
    {
        if ( bindRequest == null )
        {
            String msg = "Cannot process a null bindRequest";
            LOG.debug( msg );
            throw new IllegalArgumentException( msg );
        }

        // First switch to anonymous state
        authenticated.set( false );

        // try to connect, if we aren't already connected.
        connect();

        // If the session has not been establish, or is closed, we get out immediately
        checkSession();

        // Update the messageId
        int newId = messageId.incrementAndGet();
        bindRequest.setMessageId( newId );

        LOG.debug( "-----------------------------------------------------------------" );
        LOG.debug( "Sending request \n{}", bindRequest );

        // Create a future for this Bind operation
        BindFuture bindFuture = new BindFuture( this, newId );

        addToFutureMap( newId, bindFuture );

        writeBindRequest( bindRequest );

        // Ok, done return the future
        return bindFuture;
    }


    /**
     * Bind to the server using a CramMd5Request object.
     *
     * @param request The CramMd5Request POJO containing all the needed parameters
     * @return A LdapResponse containing the result
     * @throws LdapException if some error occurred
     * @throws IOException if an I/O exception occurred
     */
    public BindResponse bind( CramMd5Request request ) throws LdapException, IOException
    {
        if ( request == null )
        {
            String msg = "Cannot process a null request";
            LOG.debug( msg );
            throw new IllegalArgumentException( msg );
        }

        BindFuture bindFuture = bindAsync( request );

        // Get the result from the future
        try
        {
            // Read the response, waiting for it if not available immediately
            // Get the response, blocking
            BindResponse bindResponse = bindFuture.get( timeout, TimeUnit.MILLISECONDS );

            if ( bindResponse == null )
            {
                // We didn't received anything : this is an error
                LOG.error( "Bind failed : timeout occured" );
                throw new LdapException( TIME_OUT_ERROR );
            }

            if ( bindResponse.getLdapResult().getResultCode() == ResultCodeEnum.SUCCESS )
            {
                authenticated.set( true );

                // Everything is fine, return the response
                LOG.debug( "Bind successful : {}", bindResponse );
            }
            else
            {
                // We have had an error
                LOG.debug( "Bind failed : {}", bindResponse );
            }

            return bindResponse;
        }
        catch ( TimeoutException te )
        {
            // We didn't received anything : this is an error
            LOG.error( "Bind failed : timeout occured" );
            throw new LdapException( TIME_OUT_ERROR );
        }
        catch ( Exception ie )
        {
            // Catch all other exceptions
            LOG.error( NO_RESPONSE_ERROR, ie );
            LdapException ldapException = new LdapException( NO_RESPONSE_ERROR );
            ldapException.initCause( ie );

            throw ldapException;
        }
    }


    /**
     * Do an asynchronous bind, based on a CramMd5Request.
     *
     * @param request The CramMd5Request POJO containing all the needed parameters
     * @return The bind operation's future
     * @throws LdapException if some error occurred
     * @throws IOException if an I/O exception occurred
     */
    public BindFuture bindAsync( CramMd5Request request )
        throws LdapException, IOException
    {
        return bindSasl( request );
    }


    /**
     * Bind to the server using a DigestMd5Request object.
     *
     * @param request The DigestMd5Request POJO containing all the needed parameters
     * @return A LdapResponse containing the result
     * @throws LdapException if some error occurred
     * @throws IOException if an I/O exception occurred
     */
    public BindResponse bind( DigestMd5Request request ) throws LdapException, IOException
    {
        if ( request == null )
        {
            String msg = "Cannot process a null request";
            LOG.debug( msg );
            throw new IllegalArgumentException( msg );
        }

        BindFuture bindFuture = bindAsync( request );

        // Get the result from the future
        try
        {
            // Read the response, waiting for it if not available immediately
            // Get the response, blocking
            BindResponse bindResponse = bindFuture.get( timeout, TimeUnit.MILLISECONDS );

            if ( bindResponse == null )
            {
                // We didn't received anything : this is an error
                LOG.error( "Bind failed : timeout occured" );
                throw new LdapException( TIME_OUT_ERROR );
            }

            if ( bindResponse.getLdapResult().getResultCode() == ResultCodeEnum.SUCCESS )
            {
                authenticated.set( true );

                // Everything is fine, return the response
                LOG.debug( "Bind successful : {}", bindResponse );
            }
            else
            {
                // We have had an error
                LOG.debug( "Bind failed : {}", bindResponse );
            }

            return bindResponse;
        }
        catch ( TimeoutException te )
        {
            // We didn't received anything : this is an error
            LOG.error( "Bind failed : timeout occured" );
            throw new LdapException( TIME_OUT_ERROR );
        }
        catch ( Exception ie )
        {
            // Catch all other exceptions
            LOG.error( NO_RESPONSE_ERROR, ie );
            LdapException ldapException = new LdapException( NO_RESPONSE_ERROR );
            ldapException.initCause( ie );

            throw ldapException;
        }
    }


    /**
     * Do an asynchronous bind, based on a DigestMd5Request.
     *
     * @param request The DigestMd5Request POJO containing all the needed parameters
     * @return The bind operation's future
     * @throws LdapException if some error occurred
     * @throws IOException if an I/O exception occurred
     */
    public BindFuture bindAsync( DigestMd5Request request )
        throws LdapException, IOException
    {
        return bindSasl( request );
    }


    /**
     * Bind to the server using a GssApiRequest object.
     *
     * @param request The GssApiRequest POJO containing all the needed parameters
     * @return A LdapResponse containing the result
     * @throws LdapException if some error occurred
     * @throws IOException if an I/O exception occurred
     */
    public BindResponse bind( GssApiRequest request ) throws LdapException, IOException
    {
        if ( request == null )
        {
            String msg = "Cannot process a null request";
            LOG.debug( msg );
            throw new IllegalArgumentException( msg );
        }

        BindFuture bindFuture = bindAsync( request );

        // Get the result from the future
        try
        {
            // Read the response, waiting for it if not available immediately
            // Get the response, blocking
            BindResponse bindResponse = bindFuture.get( timeout, TimeUnit.MILLISECONDS );

            if ( bindResponse == null )
            {
                // We didn't received anything : this is an error
                LOG.error( "Bind failed : timeout occured" );
                throw new LdapException( TIME_OUT_ERROR );
            }

            if ( bindResponse.getLdapResult().getResultCode() == ResultCodeEnum.SUCCESS )
            {
                authenticated.set( true );

                // Everything is fine, return the response
                LOG.debug( "Bind successful : {}", bindResponse );
            }
            else
            {
                // We have had an error
                LOG.debug( "Bind failed : {}", bindResponse );
            }

            return bindResponse;
        }
        catch ( TimeoutException te )
        {
            // We didn't received anything : this is an error
            LOG.error( "Bind failed : timeout occured" );
            throw new LdapException( TIME_OUT_ERROR );
        }
        catch ( Exception ie )
        {
            // Catch all other exceptions
            LOG.error( NO_RESPONSE_ERROR, ie );
            LdapException ldapException = new LdapException( NO_RESPONSE_ERROR );
            ldapException.initCause( ie );

            throw ldapException;
        }
    }


    /**
     * Do an asynchronous bind, based on a GssApiRequest.
     *
     * @param request The GssApiRequest POJO containing all the needed parameters
     * @return The bind operation's future
     * @throws LdapException if some error occurred
     * @throws IOException if an I/O exception occurred
     */
    public BindFuture bindAsync( GssApiRequest request )
        throws LdapException, IOException
    {
        System.clearProperty( "java.security.krb5.conf" );
        String krbConfPath = createKrbConfFile( request.getRealmName(), request.getKdcHost(), request.getKdcPort() );
        System.setProperty( "java.security.krb5.conf", krbConfPath );

        Configuration.setConfiguration( new Krb5LoginConfiguration() );
        System.setProperty( "javax.security.auth.useSubjectCredsOnly", "true" );

        try
        {
            LoginContext loginContext = new LoginContext( "ldapnetworkconnection",
                        new SaslCallbackHandler( request ) );
            loginContext.login();

            final GssApiRequest requetFinal = request;

            return ( BindFuture ) Subject.doAs( loginContext.getSubject(),
                        new PrivilegedExceptionAction<Object>()
                    {
                        public Object run() throws Exception
                        {
                            return bindSasl( requetFinal );
                        }
                    } );
        }
        catch ( Exception e )
        {
            throw new LdapException( e );
        }
    }


    /**
     * {@inheritDoc}
     */
    public SearchCursor search( Dn baseDn, String filter, SearchScope scope, String... attributes )
        throws LdapException
    {
        if ( baseDn == null )
        {
            LOG.debug( "received a null dn for a search" );
            throw new IllegalArgumentException( "The base Dn cannot be null" );
        }

        // Create a new SearchRequest object
        SearchRequest searchRequest = new SearchRequestImpl();

        searchRequest.setBase( baseDn );
        searchRequest.setFilter( filter );
        searchRequest.setScope( scope );
        searchRequest.addAttributes( attributes );
        searchRequest.setDerefAliases( AliasDerefMode.DEREF_ALWAYS );

        // Process the request in blocking mode
        return search( searchRequest );
    }


    /**
     * {@inheritDoc}
     */
    public SearchCursor search( String baseDn, String filter, SearchScope scope, String... attributes )
        throws LdapException
    {
        return search( new Dn( baseDn ), filter, scope, attributes );
    }


    /**
     * {@inheritDoc}
     */
    public SearchFuture searchAsync( Dn baseDn, String filter, SearchScope scope, String... attributes )
        throws LdapException
    {
        // Create a new SearchRequest object
        SearchRequest searchRequest = new SearchRequestImpl();

        searchRequest.setBase( baseDn );
        searchRequest.setFilter( filter );
        searchRequest.setScope( scope );
        searchRequest.addAttributes( attributes );
        searchRequest.setDerefAliases( AliasDerefMode.DEREF_ALWAYS );

        // Process the request in blocking mode
        return searchAsync( searchRequest );
    }


    /**
     * {@inheritDoc}
     */
    public SearchFuture searchAsync( String baseDn, String filter, SearchScope scope, String... attributes )
        throws LdapException
    {
        return searchAsync( new Dn( baseDn ), filter, scope, attributes );
    }


    /**
     * {@inheritDoc}
     */
    public SearchFuture searchAsync( SearchRequest searchRequest ) throws LdapException
    {
        if ( searchRequest == null )
        {
            String msg = "Cannot process a null searchRequest";
            LOG.debug( msg );
            throw new IllegalArgumentException( msg );
        }

        // If the session has not been establish, or is closed, we get out immediately
        checkSession();

        int newId = messageId.incrementAndGet();
        searchRequest.setMessageId( newId );

        LOG.debug( "-----------------------------------------------------------------" );
        LOG.debug( "Sending request \n{}", searchRequest );

        SearchFuture searchFuture = new SearchFuture( this, searchRequest.getMessageId() );
        addToFutureMap( searchRequest.getMessageId(), searchFuture );

        // Send the request to the server
        WriteFuture writeFuture = ldapSession.write( searchRequest );

        // Wait for the message to be sent to the server
        if ( !writeFuture.awaitUninterruptibly( timeout ) )
        {
            // We didn't received anything : this is an error
            LOG.error( "Search failed : timeout occured" );

            throw new LdapException( TIME_OUT_ERROR );
        }

        // Chekc that the future hasn't be canceled
        if ( searchFuture.isCancelled() )
        {
            // Thow an exception here
            throw new LdapException( searchFuture.getCause() );
        }

        // Ok, done return the future
        return searchFuture;
    }


    /**
     * {@inheritDoc}
     */
    public SearchCursor search( SearchRequest searchRequest ) throws LdapException
    {
        if ( searchRequest == null )
        {
            String msg = "Cannot process a null searchRequest";
            LOG.debug( msg );
            throw new IllegalArgumentException( msg );
        }

        SearchFuture searchFuture = searchAsync( searchRequest );

        long timeout = getTimeout( searchRequest.getTimeLimit() );

        return new SearchCursorImpl( searchFuture, timeout, TimeUnit.MILLISECONDS );
    }


    //------------------------ The LDAP operations ------------------------//
    // Unbind operations                                                   //
    //---------------------------------------------------------------------//
    /**
     * {@inheritDoc}
     */
    public void unBind() throws LdapException
    {
        // If the session has not been establish, or is closed, we get out immediately
        checkSession();

        // Creates the messageID and stores it into the
        // initial message and the transmitted message.
        int newId = messageId.incrementAndGet();

        // Create the UnbindRequest
        UnbindRequest unbindRequest = new UnbindRequestImpl( newId );

        LOG.debug( "-----------------------------------------------------------------" );
        LOG.debug( "Sending Unbind request \n{}", unbindRequest );

        // Send the request to the server
        // Use this for logging instead: WriteFuture unbindFuture = ldapSession.write( unbindRequest );
        ldapSession.write( unbindRequest );

        //LOG.debug( "waiting for unbindFuture" );
        //unbindFuture.awaitUninterruptibly();
        //LOG.debug( "unbindFuture done" );

        authenticated.set( false );

        // clear the mappings
        clearMaps();

        //  We now have to close the session
        if ( ( ldapSession != null ) && connected.get() )
        {
            CloseFuture closeFuture = ldapSession.close( true );

            LOG.debug( "waiting for closeFuture" );
            closeFuture.awaitUninterruptibly();
            LOG.debug( "closeFuture done" );
            connected.set( false );
        }

        // Last, not least, reset the MessageId value
        messageId.set( 0 );

        // And get out
        LOG.debug( "Unbind successful" );
    }


    /**
     * Set the connector to use.
     *
     * @param connector The connector to use
     */
    public void setConnector( IoConnector connector )
    {
        this.connector = connector;
    }


    /**
     * {@inheritDoc}
     */
    public void setTimeOut( long timeout )
    {
        if ( timeout <= 0 )
        {
            this.timeout = Long.MAX_VALUE;
        }
        else
        {
            this.timeout = timeout;
        }
    }


    /**
     * Handle the exception we got.
     *
     * @param session The session we got the exception on
     * @param cause The exception cause
     * @throws Exception The t
     */
    @Override
    public void exceptionCaught( IoSession session, Throwable cause ) throws Exception
    {
        LOG.warn( cause.getMessage(), cause );

        if ( cause instanceof ProtocolEncoderException )
        {
            Throwable realCause = ( ( ProtocolEncoderException ) cause ).getCause();

            if ( realCause instanceof MessageEncoderException )
            {
                int messageId = ( ( MessageEncoderException ) realCause ).getMessageId();

                ResponseFuture<?> response = futureMap.get( messageId );
                response.cancel( true );
                response.setCause( realCause );
            }
        }
    }


    /**
     * Check if the message is a NoticeOfDisconnect message
     */
    private boolean isNoticeOfDisconnect( Message message )
    {
        if ( message instanceof ExtendedResponse )
        {
            ExtendedResponse response = ( ExtendedResponse ) message;

            if ( response.getResponseName().equals( NoticeOfDisconnect.EXTENSION_OID ) )
            {
                return true;
            }
        }

        return false;
    }


    /**
     * Handle the incoming LDAP messages. This is where we feed the cursor for search
     * requests, or call the listener.
     *
     * @param session The session that received a message
     * @param message The received message
     * @throws Exception If there is some error while processing the message
     */
    @Override
    public void messageReceived( IoSession session, Object message ) throws Exception
    {
        // Feed the response and store it into the session
        Message response = ( Message ) message;
        LOG.debug( "-------> {} Message received <-------", response );
        int messageId = response.getMessageId();

        // this check is necessary to prevent adding an abandoned operation's
        // result(s) to corresponding queue
        ResponseFuture<? extends Response> responseFuture = peekFromFutureMap( messageId );

        boolean isNoD = isNoticeOfDisconnect( response );

        if ( ( responseFuture == null ) && !isNoD )
        {
            LOG.info( "There is no future associated with the messageId {}, ignoring the message", messageId );
            return;
        }

        if ( isNoD )
        {
            // close the session
            session.close( true );

            return;
        }

        switch ( response.getType() )
        {
            case ADD_RESPONSE:
                // Transform the response
                AddResponse addResponse = ( AddResponse ) response;

                AddFuture addFuture = ( AddFuture ) responseFuture;

                // remove the listener from the listener map
                if ( LOG.isDebugEnabled() )
                {
                    if ( addResponse.getLdapResult().getResultCode() == ResultCodeEnum.SUCCESS )
                    {
                        // Everything is fine, return the response
                        LOG.debug( "Add successful : {}", addResponse );
                    }
                    else
                    {
                        // We have had an error
                        LOG.debug( "Add failed : {}", addResponse );
                    }
                }

                // Store the response into the future
                addFuture.set( addResponse );

                // Remove the future from the map
                removeFromFutureMaps( messageId );

                break;

            case BIND_RESPONSE:
                // Transform the response
                BindResponse bindResponse = ( BindResponse ) response;

                BindFuture bindFuture = ( BindFuture ) responseFuture;

                // remove the listener from the listener map
                if ( bindResponse.getLdapResult().getResultCode() == ResultCodeEnum.SUCCESS )
                {
                    authenticated.set( true );

                    // Everything is fine, return the response
                    LOG.debug( "Bind successful : {}", bindResponse );
                }
                else
                {
                    // We have had an error
                    LOG.debug( "Bind failed : {}", bindResponse );
                }

                // Store the response into the future
                bindFuture.set( bindResponse );

                // Remove the future from the map
                removeFromFutureMaps( messageId );

                break;

            case COMPARE_RESPONSE:
                // Transform the response
                CompareResponse compareResponse = ( CompareResponse ) response;

                CompareFuture compareFuture = ( CompareFuture ) responseFuture;

                // remove the listener from the listener map
                if ( LOG.isDebugEnabled() )
                {
                    if ( compareResponse.getLdapResult().getResultCode() == ResultCodeEnum.SUCCESS )
                    {
                        // Everything is fine, return the response
                        LOG.debug( "Compare successful : {}", compareResponse );
                    }
                    else
                    {
                        // We have had an error
                        LOG.debug( "Compare failed : {}", compareResponse );
                    }
                }

                // Store the response into the future
                compareFuture.set( compareResponse );

                // Remove the future from the map
                removeFromFutureMaps( messageId );

                break;

            case DEL_RESPONSE:
                // Transform the response
                DeleteResponse deleteResponse = ( DeleteResponse ) response;

                DeleteFuture deleteFuture = ( DeleteFuture ) responseFuture;

                if ( LOG.isDebugEnabled() )
                {
                    if ( deleteResponse.getLdapResult().getResultCode() == ResultCodeEnum.SUCCESS )
                    {
                        // Everything is fine, return the response
                        LOG.debug( "Delete successful : {}", deleteResponse );
                    }
                    else
                    {
                        // We have had an error
                        LOG.debug( "Delete failed : {}", deleteResponse );
                    }
                }

                // Store the response into the future
                deleteFuture.set( deleteResponse );

                // Remove the future from the map
                removeFromFutureMaps( messageId );

                break;

            case EXTENDED_RESPONSE:
                // Transform the response
                ExtendedResponse extendedResponse = ( ExtendedResponse ) response;

                ExtendedFuture extendedFuture = ( ExtendedFuture ) responseFuture;

                // remove the listener from the listener map
                if ( LOG.isDebugEnabled() )
                {
                    if ( extendedResponse.getLdapResult().getResultCode() == ResultCodeEnum.SUCCESS )
                    {
                        // Everything is fine, return the response
                        LOG.debug( "Extended successful : {}", extendedResponse );
                    }
                    else
                    {
                        // We have had an error
                        LOG.debug( "Extended failed : {}", extendedResponse );
                    }
                }

                // Store the response into the future
                extendedFuture.set( extendedResponse );

                // Remove the future from the map
                removeFromFutureMaps( messageId );

                break;

            case INTERMEDIATE_RESPONSE:
                IntermediateResponse intermediateResponse = null;

                if ( responseFuture instanceof SearchFuture )
                {
                    intermediateResponse = new IntermediateResponseImpl( messageId );
                    addControls( intermediateResponse, response );
                    ( ( SearchFuture ) responseFuture ).set( intermediateResponse );
                }
                else if ( responseFuture instanceof ExtendedFuture )
                {
                    intermediateResponse = new IntermediateResponseImpl( messageId );
                    addControls( intermediateResponse, response );
                    ( ( ExtendedFuture ) responseFuture ).set( intermediateResponse );
                }
                else
                {
                    // currently we only support IR for search and extended operations
                    throw new UnsupportedOperationException( "Unknown ResponseFuture type "
                        + responseFuture.getClass().getName() );
                }

                intermediateResponse.setResponseName( ( ( IntermediateResponse ) response ).getResponseName() );
                intermediateResponse.setResponseValue( ( ( IntermediateResponse ) response ).getResponseValue() );

                break;

            case MODIFY_RESPONSE:
                // Transform the response
                ModifyResponse modifyResponse = ( ModifyResponse ) response;

                ModifyFuture modifyFuture = ( ModifyFuture ) responseFuture;

                if ( LOG.isDebugEnabled() )
                {
                    if ( modifyResponse.getLdapResult().getResultCode() == ResultCodeEnum.SUCCESS )
                    {
                        // Everything is fine, return the response
                        LOG.debug( "ModifyFuture successful : {}", modifyResponse );
                    }
                    else
                    {
                        // We have had an error
                        LOG.debug( "ModifyFuture failed : {}", modifyResponse );
                    }
                }

                // Store the response into the future
                modifyFuture.set( modifyResponse );

                // Remove the future from the map
                removeFromFutureMaps( messageId );

                break;

            case MODIFYDN_RESPONSE:
                // Transform the response
                ModifyDnResponse modifyDnResponse = ( ModifyDnResponse ) response;

                ModifyDnFuture modifyDnFuture = ( ModifyDnFuture ) responseFuture;

                if ( LOG.isDebugEnabled() )
                {
                    if ( modifyDnResponse.getLdapResult().getResultCode() == ResultCodeEnum.SUCCESS )
                    {
                        // Everything is fine, return the response
                        LOG.debug( "ModifyDN successful : {}", modifyDnResponse );
                    }
                    else
                    {
                        // We have had an error
                        LOG.debug( "ModifyDN failed : {}", modifyDnResponse );
                    }
                }

                // Store the response into the future
                modifyDnFuture.set( modifyDnResponse );

                // Remove the future from the map
                removeFromFutureMaps( messageId );

                break;

            case SEARCH_RESULT_DONE:
                // Store the response into the responseQueue
                SearchResultDone searchResultDone = ( SearchResultDone ) response;

                SearchFuture searchFuture = ( SearchFuture ) responseFuture;

                if ( LOG.isDebugEnabled() )
                {
                    if ( searchResultDone.getLdapResult().getResultCode() == ResultCodeEnum.SUCCESS )
                    {
                        // Everything is fine, return the response
                        LOG.debug( "Search successful : {}", searchResultDone );
                    }
                    else
                    {
                        // We have had an error
                        LOG.debug( "Search failed : {}", searchResultDone );
                    }
                }

                // Store the response into the future
                searchFuture.set( searchResultDone );

                // Remove the future from the map
                removeFromFutureMaps( messageId );

                break;

            case SEARCH_RESULT_ENTRY:
                // Store the response into the responseQueue
                SearchResultEntry searchResultEntry = ( SearchResultEntry ) response;

                if ( schemaManager != null )
                {
                    searchResultEntry.setEntry( new DefaultEntry( schemaManager, searchResultEntry.getEntry() ) );
                }

                searchFuture = ( SearchFuture ) responseFuture;

                if ( LOG.isDebugEnabled() )
                {
                    LOG.debug( "Search entry found : {}", searchResultEntry );
                }

                // Store the response into the future
                searchFuture.set( searchResultEntry );

                break;

            case SEARCH_RESULT_REFERENCE:
                // Store the response into the responseQueue
                SearchResultReference searchResultReference = ( SearchResultReference ) response;

                searchFuture = ( SearchFuture ) responseFuture;

                if ( LOG.isDebugEnabled() )
                {
                    LOG.debug( "Search reference found : {}", searchResultReference );
                }

                // Store the response into the future
                searchFuture.set( searchResultReference );

                break;

            default:
                throw new IllegalStateException( "Unexpected response type " + response.getType() );
        }
    }


    /**
     * {@inheritDoc}
     */
    public ModifyResponse modify( Entry entry, ModificationOperation modOp ) throws LdapException
    {
        if ( entry == null )
        {
            LOG.debug( "received a null entry for modification" );
            throw new IllegalArgumentException( "Entry to be modified cannot be null" );
        }

        ModifyRequest modReq = new ModifyRequestImpl();
        modReq.setName( entry.getDn() );

        Iterator<EntryAttribute> itr = entry.iterator();
        while ( itr.hasNext() )
        {
            modReq.addModification( itr.next(), modOp );
        }

        return modify( modReq );
    }


    /**
     * {@inheritDoc}
     */
    public ModifyResponse modify( Dn dn, Modification... modifications ) throws LdapException
    {
        if ( dn == null )
        {
            LOG.debug( "received a null dn for modification" );
            throw new IllegalArgumentException( "The Dn to be modified cannot be null" );
        }

        if ( ( modifications == null ) || ( modifications.length == 0 ) )
        {
            String msg = "Cannot process a ModifyRequest without any modification";
            LOG.debug( msg );
            throw new IllegalArgumentException( msg );
        }

        ModifyRequest modReq = new ModifyRequestImpl();
        modReq.setName( dn );

        for ( Modification modification : modifications )
        {
            modReq.addModification( modification );
        }

        return modify( modReq );
    }


    /**
     * {@inheritDoc}
     */
    public ModifyResponse modify( String dn, Modification... modifications ) throws LdapException
    {
        return modify( new Dn( dn ), modifications );
    }


    /**
     * {@inheritDoc}
     */
    public ModifyResponse modify( ModifyRequest modRequest ) throws LdapException
    {
        if ( modRequest == null )
        {
            String msg = "Cannot process a null modifyRequest";
            LOG.debug( msg );
            throw new IllegalArgumentException( msg );
        }

        ModifyFuture modifyFuture = modifyAsync( modRequest );

        // Get the result from the future
        try
        {
            // Read the response, waiting for it if not available immediately
            // Get the response, blocking
            ModifyResponse modifyResponse = modifyFuture.get( timeout, TimeUnit.MILLISECONDS );

            if ( modifyResponse == null )
            {
                // We didn't received anything : this is an error
                LOG.error( "Modify failed : timeout occured" );
                throw new LdapException( TIME_OUT_ERROR );
            }

            if ( modifyResponse.getLdapResult().getResultCode() == ResultCodeEnum.SUCCESS )
            {
                // Everything is fine, return the response
                LOG.debug( "Modify successful : {}", modifyResponse );
            }
            else
            {
                if ( modifyResponse instanceof ModifyNoDResponse )
                {
                    // A NoticeOfDisconnect : deserves a special treatment
                    throw new LdapException( modifyResponse.getLdapResult().getErrorMessage() );
                }

                // We have had an error
                LOG.debug( "Modify failed : {}", modifyResponse );
            }

            return modifyResponse;
        }
        catch ( TimeoutException te )
        {
            // Send an abandon request
            if ( !modifyFuture.isCancelled() )
            {
                abandon( modRequest.getMessageId() );
            }

            // We didn't received anything : this is an error
            LOG.error( "Modify failed : timeout occured" );
            throw new LdapException( TIME_OUT_ERROR );
        }
        catch ( Exception ie )
        {
            // Catch all other exceptions
            LOG.error( NO_RESPONSE_ERROR, ie );
            LdapException ldapException = new LdapException( ie.getMessage() );
            ldapException.initCause( ie );

            // Send an abandon request
            if ( !modifyFuture.isCancelled() )
            {
                abandon( modRequest.getMessageId() );
            }

            throw ldapException;
        }
    }


    /**
     * {@inheritDoc}
     */
    public ModifyFuture modifyAsync( ModifyRequest modRequest ) throws LdapException
    {
        if ( modRequest == null )
        {
            String msg = "Cannot process a null modifyRequest";
            LOG.debug( msg );
            throw new IllegalArgumentException( msg );
        }

        checkSession();

        int newId = messageId.incrementAndGet();
        modRequest.setMessageId( newId );

        ModifyFuture modifyFuture = new ModifyFuture( this, newId );
        addToFutureMap( newId, modifyFuture );

        // Send the request to the server
        WriteFuture writeFuture = ldapSession.write( modRequest );

        // Wait for the message to be sent to the server
        if ( !writeFuture.awaitUninterruptibly( timeout ) )
        {
            // We didn't received anything : this is an error
            LOG.error( "Modify failed : timeout occured" );

            throw new LdapException( TIME_OUT_ERROR );
        }

        // Ok, done return the future
        return modifyFuture;
    }


    /**
     * {@inheritDoc}
     */
    public ModifyDnResponse rename( String entryDn, String newRdn ) throws LdapException
    {
        return rename( entryDn, newRdn, true );
    }


    /**
     * {@inheritDoc}
     */
    public ModifyDnResponse rename( Dn entryDn, Rdn newRdn ) throws LdapException
    {
        return rename( entryDn, newRdn, true );
    }


    /**
     * {@inheritDoc}
     */
    public ModifyDnResponse rename( String entryDn, String newRdn, boolean deleteOldRdn ) throws LdapException
    {
        if ( entryDn == null )
        {
            String msg = "Cannot process a rename of a null Dn";
            LOG.debug( msg );
            throw new IllegalArgumentException( msg );
        }

        if ( newRdn == null )
        {
            String msg = "Cannot process a rename with a null Rdn";
            LOG.debug( msg );
            throw new IllegalArgumentException( msg );
        }

        try
        {
            return rename( new Dn( entryDn ), new Rdn( newRdn ), deleteOldRdn );
        }
        catch ( LdapInvalidDnException e )
        {
            LOG.error( e.getMessage(), e );
            throw new LdapException( e.getMessage(), e );
        }
    }


    /**
     * {@inheritDoc}
     */
    public ModifyDnResponse rename( Dn entryDn, Rdn newRdn, boolean deleteOldRdn ) throws LdapException
    {
        if ( entryDn == null )
        {
            String msg = "Cannot process a rename of a null Dn";
            LOG.debug( msg );
            throw new IllegalArgumentException( msg );
        }

        if ( newRdn == null )
        {
            String msg = "Cannot process a rename with a null Rdn";
            LOG.debug( msg );
            throw new IllegalArgumentException( msg );
        }

        ModifyDnRequest modDnRequest = new ModifyDnRequestImpl();
        modDnRequest.setName( entryDn );
        modDnRequest.setNewRdn( newRdn );
        modDnRequest.setDeleteOldRdn( deleteOldRdn );

        return modifyDn( modDnRequest );
    }


    /**
     * {@inheritDoc}
     */
    public ModifyDnResponse move( String entryDn, String newSuperiorDn ) throws LdapException
    {
        if ( entryDn == null )
        {
            String msg = "Cannot process a move of a null Dn";
            LOG.debug( msg );
            throw new IllegalArgumentException( msg );
        }

        if ( newSuperiorDn == null )
        {
            String msg = "Cannot process a move to a null Dn";
            LOG.debug( msg );
            throw new IllegalArgumentException( msg );
        }

        try
        {
            return move( new Dn( entryDn ), new Dn( newSuperiorDn ) );
        }
        catch ( LdapInvalidDnException e )
        {
            LOG.error( e.getMessage(), e );
            throw new LdapException( e.getMessage(), e );
        }
    }


    /**
     * {@inheritDoc}
     */
    public ModifyDnResponse move( Dn entryDn, Dn newSuperiorDn ) throws LdapException
    {
        if ( entryDn == null )
        {
            String msg = "Cannot process a move of a null Dn";
            LOG.debug( msg );
            throw new IllegalArgumentException( msg );
        }

        if ( newSuperiorDn == null )
        {
            String msg = "Cannot process a move to a null Dn";
            LOG.debug( msg );
            throw new IllegalArgumentException( msg );
        }

        ModifyDnRequest modDnRequest = new ModifyDnRequestImpl();
        modDnRequest.setName( entryDn );
        modDnRequest.setNewSuperior( newSuperiorDn );

        //TODO not setting the below value is resulting in error
        modDnRequest.setNewRdn( entryDn.getRdn() );

        return modifyDn( modDnRequest );
    }


    /**
     * {@inheritDoc}
     */
    public ModifyDnResponse moveAndRename( Dn entryDn, Dn newDn ) throws LdapException
    {
        return moveAndRename( entryDn, newDn, true );
    }


    /**
     * {@inheritDoc}
     */
    public ModifyDnResponse moveAndRename( String entryDn, String newDn ) throws LdapException
    {
        return moveAndRename( new Dn( entryDn ), new Dn( newDn ), true );
    }


    /**
     * {@inheritDoc}
     */
    public ModifyDnResponse moveAndRename( Dn entryDn, Dn newDn, boolean deleteOldRdn ) throws LdapException
    {
        // Check the parameters first
        if ( entryDn == null )
        {
            throw new IllegalArgumentException( "The entry Dn must not be null" );
        }

        if ( entryDn.isRootDSE() )
        {
            throw new IllegalArgumentException( "The RootDSE cannot be moved" );
        }

        if ( newDn == null )
        {
            throw new IllegalArgumentException( "The new Dn must not be null" );
        }

        if ( newDn.isRootDSE() )
        {
            throw new IllegalArgumentException( "The RootDSE cannot be the target" );
        }

        // Create the request
        ModifyDnRequest modDnRequest = new ModifyDnRequestImpl();
        modDnRequest.setName( entryDn );
        modDnRequest.setNewRdn( newDn.getRdn() );
        modDnRequest.setNewSuperior( newDn.getParent() );
        modDnRequest.setDeleteOldRdn( deleteOldRdn );

        return modifyDn( modDnRequest );
    }


    /**
     * {@inheritDoc}
     */
    public ModifyDnResponse moveAndRename( String entryDn, String newDn, boolean deleteOldRdn ) throws LdapException
    {
        return moveAndRename( new Dn( entryDn ), new Dn( newDn ), true );
    }


    /**
     * {@inheritDoc}
     */
    public ModifyDnResponse modifyDn( ModifyDnRequest modDnRequest ) throws LdapException
    {
        if ( modDnRequest == null )
        {
            String msg = "Cannot process a null modDnRequest";
            LOG.debug( msg );
            throw new IllegalArgumentException( msg );
        }

        ModifyDnFuture modifyDnFuture = modifyDnAsync( modDnRequest );

        // Get the result from the future
        try
        {
            // Read the response, waiting for it if not available immediately
            // Get the response, blocking
            ModifyDnResponse modifyDnResponse = modifyDnFuture.get( timeout, TimeUnit.MILLISECONDS );

            if ( modifyDnResponse == null )
            {
                // We didn't received anything : this is an error
                LOG.error( "ModifyDN failed : timeout occured" );
                throw new LdapException( TIME_OUT_ERROR );
            }

            if ( modifyDnResponse.getLdapResult().getResultCode() == ResultCodeEnum.SUCCESS )
            {
                // Everything is fine, return the response
                LOG.debug( "ModifyDN successful : {}", modifyDnResponse );
            }
            else
            {
                // We have had an error
                LOG.debug( "Modify failed : {}", modifyDnResponse );
            }

            return modifyDnResponse;
        }
        catch ( TimeoutException te )
        {
            // Send an abandon request
            if ( !modifyDnFuture.isCancelled() )
            {
                abandon( modDnRequest.getMessageId() );
            }

            // We didn't received anything : this is an error
            LOG.error( "Modify failed : timeout occured" );
            throw new LdapException( TIME_OUT_ERROR );
        }
        catch ( Exception ie )
        {
            // Catch all other exceptions
            LOG.error( NO_RESPONSE_ERROR, ie );
            LdapException ldapException = new LdapException( NO_RESPONSE_ERROR );
            ldapException.initCause( ie );

            // Send an abandon request
            if ( !modifyDnFuture.isCancelled() )
            {
                abandon( modDnRequest.getMessageId() );
            }

            throw ldapException;
        }
    }


    /**
     * {@inheritDoc}
     */
    public ModifyDnFuture modifyDnAsync( ModifyDnRequest modDnRequest ) throws LdapException
    {
        if ( modDnRequest == null )
        {
            String msg = "Cannot process a null modDnRequest";
            LOG.debug( msg );
            throw new IllegalArgumentException( msg );
        }

        checkSession();

        int newId = messageId.incrementAndGet();
        modDnRequest.setMessageId( newId );

        ModifyDnFuture modifyDnFuture = new ModifyDnFuture( this, newId );
        addToFutureMap( newId, modifyDnFuture );

        // Send the request to the server
        WriteFuture writeFuture = ldapSession.write( modDnRequest );

        // Wait for the message to be sent to the server
        if ( !writeFuture.awaitUninterruptibly( timeout ) )
        {
            // We didn't received anything : this is an error
            LOG.error( "Modify failed : timeout occured" );

            throw new LdapException( TIME_OUT_ERROR );
        }

        // Ok, done return the future
        return modifyDnFuture;
    }


    /**
     * {@inheritDoc}
     */
    public DeleteResponse delete( String dn ) throws LdapException
    {
        return delete( new Dn( dn ) );
    }


    /**
     * {@inheritDoc}
     */
    public DeleteResponse delete( Dn dn ) throws LdapException
    {
        DeleteRequest deleteRequest = new DeleteRequestImpl();
        deleteRequest.setName( dn );

        return delete( deleteRequest );
    }


    /**
     * deletes the entry with the given Dn, and all its children
     *
     * @param dn the target entry's Dn
     * @return operation's response
     * @throws LdapException If the Dn is not valid or if the deletion failed
     */
    public DeleteResponse deleteTree( Dn dn ) throws LdapException
    {
        String treeDeleteOid = "1.2.840.113556.1.4.805";

        if ( isControlSupported( treeDeleteOid ) )
        {
            DeleteRequest deleteRequest = new DeleteRequestImpl();
            deleteRequest.setName( dn );
            deleteRequest.addControl( new OpaqueControl( treeDeleteOid ) );
            return delete( deleteRequest );
        }
        else
        {
            String msg = "The subtreeDelete control (1.2.840.113556.1.4.805) is not supported by the server\n"
                + " The deletion has been aborted";
            LOG.error( msg );
            throw new LdapException( msg );
        }
    }


    /**
     * deletes the entry with the given Dn, and all its children
     *
     * @param dn the target entry's Dn as a String
     * @return operation's response
     * @throws LdapException If the Dn is not valid or if the deletion failed
     */
    public DeleteResponse deleteTree( String dn ) throws LdapException
    {
        try
        {
            String treeDeleteOid = "1.2.840.113556.1.4.805";
            Dn newDn = new Dn( dn );

            if ( isControlSupported( treeDeleteOid ) )
            {
                DeleteRequest deleteRequest = new DeleteRequestImpl();
                deleteRequest.setName( newDn );
                deleteRequest.addControl( new OpaqueControl( treeDeleteOid ) );
                return delete( deleteRequest );
            }
            else
            {
                String msg = "The subtreeDelete control (1.2.840.113556.1.4.805) is not supported by the server\n"
                    + " The deletion has been aborted";
                LOG.error( msg );
                throw new LdapException( msg );
            }
        }
        catch ( LdapInvalidDnException e )
        {
            LOG.error( e.getMessage(), e );
            throw new LdapException( e.getMessage(), e );
        }
    }


    /**
     * {@inheritDoc}
     */
    public DeleteResponse delete( DeleteRequest deleteRequest ) throws LdapException
    {
        if ( deleteRequest == null )
        {
            String msg = "Cannot process a null deleteRequest";
            LOG.debug( msg );
            throw new IllegalArgumentException( msg );
        }

        DeleteFuture deleteFuture = deleteAsync( deleteRequest );

        // Get the result from the future
        try
        {
            // Read the response, waiting for it if not available immediately
            // Get the response, blocking
            DeleteResponse delResponse = deleteFuture.get( timeout, TimeUnit.MILLISECONDS );

            if ( delResponse == null )
            {
                // We didn't received anything : this is an error
                LOG.error( "Delete failed : timeout occured" );
                throw new LdapException( TIME_OUT_ERROR );
            }

            if ( delResponse.getLdapResult().getResultCode() == ResultCodeEnum.SUCCESS )
            {
                // Everything is fine, return the response
                LOG.debug( "Delete successful : {}", delResponse );
            }
            else
            {
                // We have had an error
                LOG.debug( "Delete failed : {}", delResponse );
            }

            return delResponse;
        }
        catch ( TimeoutException te )
        {
            // Send an abandon request
            if ( !deleteFuture.isCancelled() )
            {
                abandon( deleteRequest.getMessageId() );
            }

            // We didn't received anything : this is an error
            LOG.error( "Del failed : timeout occured" );
            throw new LdapException( TIME_OUT_ERROR );
        }
        catch ( Exception ie )
        {
            // Catch all other exceptions
            LOG.error( NO_RESPONSE_ERROR, ie );
            LdapException ldapException = new LdapException( NO_RESPONSE_ERROR );
            ldapException.initCause( ie );

            // Send an abandon request
            if ( !deleteFuture.isCancelled() )
            {
                abandon( deleteRequest.getMessageId() );
            }

            throw ldapException;
        }
    }


    /**
     * {@inheritDoc}
     */
    public DeleteFuture deleteAsync( DeleteRequest deleteRequest ) throws LdapException
    {
        if ( deleteRequest == null )
        {
            String msg = "Cannot process a null deleteRequest";
            LOG.debug( msg );
            throw new IllegalArgumentException( msg );
        }

        checkSession();

        int newId = messageId.incrementAndGet();

        deleteRequest.setMessageId( newId );

        DeleteFuture deleteFuture = new DeleteFuture( this, newId );
        addToFutureMap( newId, deleteFuture );

        // Send the request to the server
        WriteFuture writeFuture = ldapSession.write( deleteRequest );

        // Wait for the message to be sent to the server
        if ( !writeFuture.awaitUninterruptibly( timeout ) )
        {
            // We didn't received anything : this is an error
            LOG.error( "Delete failed : timeout occured" );

            throw new LdapException( TIME_OUT_ERROR );
        }

        // Ok, done return the future
        return deleteFuture;
    }


    /**
     * {@inheritDoc}
     */
    public CompareResponse compare( String dn, String attributeName, String value ) throws LdapException
    {
        return compare( new Dn( dn ), attributeName, value );
    }


    /**
     * {@inheritDoc}
     */
    public CompareResponse compare( String dn, String attributeName, byte[] value ) throws LdapException
    {
        return compare( new Dn( dn ), attributeName, value );
    }


    /**
     * {@inheritDoc}
     */
    public CompareResponse compare( String dn, String attributeName, Value<?> value ) throws LdapException
    {
        return compare( new Dn( dn ), attributeName, value );
    }


    /**
     * {@inheritDoc}
     */
    public CompareResponse compare( Dn dn, String attributeName, String value ) throws LdapException
    {
        CompareRequest compareRequest = new CompareRequestImpl();
        compareRequest.setName( dn );
        compareRequest.setAttributeId( attributeName );
        compareRequest.setAssertionValue( value );

        return compare( compareRequest );
    }


    /**
     * {@inheritDoc}
     */
    public CompareResponse compare( Dn dn, String attributeName, byte[] value ) throws LdapException
    {
        CompareRequest compareRequest = new CompareRequestImpl();
        compareRequest.setName( dn );
        compareRequest.setAttributeId( attributeName );
        compareRequest.setAssertionValue( value );

        return compare( compareRequest );
    }


    /**
     * {@inheritDoc}
     */
    public CompareResponse compare( Dn dn, String attributeName, Value<?> value ) throws LdapException
    {
        CompareRequest compareRequest = new CompareRequestImpl();
        compareRequest.setName( dn );
        compareRequest.setAttributeId( attributeName );

        if ( value.isBinary() )
        {
            compareRequest.setAssertionValue( value.getBytes() );
        }
        else
        {
            compareRequest.setAssertionValue( value.getString() );
        }

        return compare( compareRequest );
    }


    /**
     * {@inheritDoc}
     */
    public CompareResponse compare( CompareRequest compareRequest ) throws LdapException
    {
        if ( compareRequest == null )
        {
            String msg = "Cannot process a null compareRequest";
            LOG.debug( msg );
            throw new IllegalArgumentException( msg );
        }

        CompareFuture compareFuture = compareAsync( compareRequest );

        // Get the result from the future
        try
        {
            // Read the response, waiting for it if not available immediately
            // Get the response, blocking
            CompareResponse compareResponse = compareFuture.get( timeout, TimeUnit.MILLISECONDS );

            if ( compareResponse == null )
            {
                // We didn't received anything : this is an error
                LOG.error( "Compare failed : timeout occured" );
                throw new LdapException( TIME_OUT_ERROR );
            }

            if ( compareResponse.getLdapResult().getResultCode() == ResultCodeEnum.SUCCESS )
            {
                // Everything is fine, return the response
                LOG.debug( "Compare successful : {}", compareResponse );
            }
            else
            {
                // We have had an error
                LOG.debug( "Compare failed : {}", compareResponse );
            }

            return compareResponse;
        }
        catch ( TimeoutException te )
        {
            // Send an abandon request
            if ( !compareFuture.isCancelled() )
            {
                abandon( compareRequest.getMessageId() );
            }

            // We didn't received anything : this is an error
            LOG.error( "Compare failed : timeout occured" );
            throw new LdapException( TIME_OUT_ERROR );
        }
        catch ( Exception ie )
        {
            // Catch all other exceptions
            LOG.error( NO_RESPONSE_ERROR, ie );
            LdapException ldapException = new LdapException( NO_RESPONSE_ERROR );
            ldapException.initCause( ie );

            // Send an abandon request
            if ( !compareFuture.isCancelled() )
            {
                abandon( compareRequest.getMessageId() );
            }

            throw ldapException;
        }
    }


    /**
     * {@inheritDoc}
     */
    public CompareFuture compareAsync( CompareRequest compareRequest ) throws LdapException
    {
        if ( compareRequest == null )
        {
            String msg = "Cannot process a null compareRequest";
            LOG.debug( msg );
            throw new IllegalArgumentException( msg );
        }

        checkSession();

        int newId = messageId.incrementAndGet();

        compareRequest.setMessageId( newId );

        CompareFuture compareFuture = new CompareFuture( this, newId );
        addToFutureMap( newId, compareFuture );

        // Send the request to the server
        WriteFuture writeFuture = ldapSession.write( compareRequest );

        // Wait for the message to be sent to the server
        if ( !writeFuture.awaitUninterruptibly( timeout ) )
        {
            // We didn't received anything : this is an error
            LOG.error( "Compare failed : timeout occured" );

            throw new LdapException( TIME_OUT_ERROR );
        }

        // Ok, done return the future
        return compareFuture;
    }


    /**
     * {@inheritDoc}
     */
    public ExtendedResponse extended( String oid ) throws LdapException
    {
        return extended( oid, null );
    }


    /**
     * {@inheritDoc}
     */
    public ExtendedResponse extended( String oid, byte[] value ) throws LdapException
    {
        try
        {
            return extended( new OID( oid ), value );
        }
        catch ( DecoderException e )
        {
            String msg = "Failed to decode the OID " + oid;
            LOG.error( msg );
            throw new LdapException( msg, e );
        }
    }


    /**
     * {@inheritDoc}
     */
    public ExtendedResponse extended( OID oid ) throws LdapException
    {
        return extended( oid, null );
    }


    /**
     * {@inheritDoc}
     */
    public ExtendedResponse extended( OID oid, byte[] value ) throws LdapException
    {
        ExtendedRequest extendedRequest = new ExtendedRequestImpl();
        extendedRequest.setRequestName( oid.toString() );
        extendedRequest.setRequestValue( value );

        return extended( extendedRequest );
    }


    /**
     * {@inheritDoc}
     */
    public ExtendedResponse extended( ExtendedRequest extendedRequest ) throws LdapException
    {
        if ( extendedRequest == null )
        {
            String msg = "Cannot process a null extendedRequest";
            LOG.debug( msg );
            throw new IllegalArgumentException( msg );
        }

        ExtendedFuture extendedFuture = extendedAsync( extendedRequest );

        // Get the result from the future
        try
        {
            // Read the response, waiting for it if not available immediately
            // Get the response, blocking
            ExtendedResponse extendedResponse = ( ExtendedResponse ) extendedFuture
                .get( timeout, TimeUnit.MILLISECONDS );

            if ( extendedResponse == null )
            {
                // We didn't received anything : this is an error
                LOG.error( "Extended failed : timeout occured" );
                throw new LdapException( TIME_OUT_ERROR );
            }

            if ( extendedResponse.getLdapResult().getResultCode() == ResultCodeEnum.SUCCESS )
            {
                // Everything is fine, return the response
                LOG.debug( "Extended successful : {}", extendedResponse );
            }
            else
            {
                // We have had an error
                LOG.debug( "Extended failed : {}", extendedResponse );
            }

            return extendedResponse;
        }
        catch ( TimeoutException te )
        {
            // Send an abandon request
            if ( !extendedFuture.isCancelled() )
            {
                abandon( extendedRequest.getMessageId() );
            }

            // We didn't received anything : this is an error
            LOG.error( "Extended failed : timeout occured" );
            throw new LdapException( TIME_OUT_ERROR );
        }
        catch ( Exception ie )
        {
            // Catch all other exceptions
            LOG.error( NO_RESPONSE_ERROR, ie );
            LdapException ldapException = new LdapException( NO_RESPONSE_ERROR );
            ldapException.initCause( ie );

            // Send an abandon request
            if ( !extendedFuture.isCancelled() )
            {
                abandon( extendedRequest.getMessageId() );
            }

            throw ldapException;
        }
    }


    /**
     * {@inheritDoc}
     */
    public ExtendedFuture extendedAsync( ExtendedRequest extendedRequest ) throws LdapException
    {
        if ( extendedRequest == null )
        {
            String msg = "Cannot process a null extendedRequest";
            LOG.debug( msg );
            throw new IllegalArgumentException( msg );
        }

        checkSession();

        int newId = messageId.incrementAndGet();

        extendedRequest.setMessageId( newId );
        ExtendedFuture extendedFuture = new ExtendedFuture( this, newId );
        addToFutureMap( newId, extendedFuture );

        // Send the request to the server
        WriteFuture writeFuture = ldapSession.write( extendedRequest );

        // Wait for the message to be sent to the server
        if ( !writeFuture.awaitUninterruptibly( timeout ) )
        {
            // We didn't received anything : this is an error
            LOG.error( "Extended failed : timeout occured" );

            throw new LdapException( TIME_OUT_ERROR );
        }

        // Ok, done return the future
        return extendedFuture;
    }


    /**
     * {@inheritDoc}
     */
    public boolean exists( String dn ) throws LdapException
    {
        return exists( new Dn( dn ) );
    }


    /**
     * {@inheritDoc}
     */
    public boolean exists( Dn dn ) throws LdapException
    {
        try
        {
            Entry entry = lookup( dn, SchemaConstants.NO_ATTRIBUTE_ARRAY );

            return entry != null;
        }
        catch ( LdapNoPermissionException lnpe )
        {
            // Special case to deal with insufficient permissions
            return false;
        }
        catch ( LdapException le )
        {
            throw le;
        }
    }


    /**
     * {@inheritDoc}
     */
    public Entry lookup( Dn dn ) throws LdapException
    {
        return lookup( dn, SchemaConstants.ALL_USER_ATTRIBUTES_ARRAY );
    }


    /**
     * {@inheritDoc}
     */
    public Entry lookup( String dn ) throws LdapException
    {
        return lookup( dn, SchemaConstants.ALL_USER_ATTRIBUTES_ARRAY );
    }


    /**
     * {@inheritDoc}
     */
    public Entry lookup( Dn dn, String... attributes ) throws LdapException
    {
        return lookup( dn, null, attributes );
    }


    /**
     * {@inheritDoc}
     */
    public Entry lookup( Dn dn, Control[] controls, String... attributes ) throws LdapException
    {
        Entry entry = null;

        try
        {
            SearchRequest searchRequest = new SearchRequestImpl();

            searchRequest.setBase( dn );
            searchRequest.setFilter( "(objectClass=*)" );
            searchRequest.setScope( SearchScope.OBJECT );
            searchRequest.addAttributes( attributes );
            searchRequest.setDerefAliases( AliasDerefMode.DEREF_ALWAYS );

            if ( ( controls != null ) && ( controls.length > 0 ) )
            {
                searchRequest.addAllControls( controls );
            }

            Cursor<Response> cursor = search( searchRequest );

            // Read the response
            if ( cursor.next() )
            {
                // cursor will always hold SearchResultEntry objects cause there is no ManageDsaITControl passed with search request
                entry = ( ( SearchResultEntry ) cursor.get() ).getEntry();
            }

            // Pass through the SaerchResultDone, or stop
            // if we have other responses
            cursor.next();

            // And close the cursor
            cursor.close();
        }
        catch ( Exception e )
        {
            throw new LdapException( e );
        }

        return entry;
    }


    /**
     * {@inheritDoc}
     */
    public Entry lookup( String dn, String... attributes ) throws LdapException
    {
        return lookup( new Dn( dn ), null, attributes );
    }


    /**
     * {@inheritDoc}
     */
    public Entry lookup( String dn, Control[] controls, String... attributes ) throws LdapException
    {
        return lookup( new Dn( dn ), controls, attributes );
    }


    /**
     * {@inheritDoc}
     */
    public boolean isControlSupported( String controlOID ) throws LdapException
    {
        return getSupportedControls().contains( controlOID );
    }


    /**
     * {@inheritDoc}
     */
    public List<String> getSupportedControls() throws LdapException
    {
        if ( supportedControls != null )
        {
            return supportedControls;
        }

        if ( rootDSE == null )
        {
            fetchRootDSE();
        }

        supportedControls = new ArrayList<String>();

        EntryAttribute attr = rootDSE.get( SchemaConstants.SUPPORTED_CONTROL_AT );

        for ( Value<?> value : attr )
        {
            supportedControls.add( value.getString() );
        }

        return supportedControls;
    }


    /**
     * {@inheritDoc}
     */
    public void loadSchema() throws LdapException
    {
        try
        {
            JarLdifSchemaLoader jarSchemaLoader = new JarLdifSchemaLoader();

            // we enable all the schemas so that need not check with server for enabled schemas
            Collection<Schema> schemas = jarSchemaLoader.getAllSchemas();
            for ( Schema s : schemas )
            {
                s.enable();
            }

            loadSchema( jarSchemaLoader );
        }
        catch ( LdapException e )
        {
            throw e;
        }
        catch ( Exception e )
        {
            LOG.error( "failed to load the schema using JarLdifSchemaLoader", e );
            throw new LdapException( e );
        }
    }


    /**
     * loads schema using the specified schema loader
     *
     * @param loader the {@link SchemaLoader} to be used to load schema
     * @throws LdapException
     */
    public void loadSchema( SchemaLoader loader ) throws LdapException
    {
        try
        {
            SchemaManager tmp = new DefaultSchemaManager( loader );

            tmp.loadAllEnabled();

            if ( !tmp.getErrors().isEmpty() )
            {
                String msg = "there are errors while loading the schema";
                LOG.error( msg + " {}", schemaManager.getErrors() );
                throw new LdapException( msg );
            }

            schemaManager = tmp;
        }
        catch ( LdapException le )
        {
            throw le;
        }
        catch ( Exception e )
        {
            LOG.error( "failed to load the schema", e );
            throw new LdapException( e );
        }
    }


    /**
     * parses the given schema file present in OpenLDAP schema format
     * and adds all the SchemaObjects present in it to the SchemaManager
     *
     * @param schemaFile the schema file in OpenLDAP schema format
     * @throws LdapException in case of any errors while parsing
     */
    public void addSchema( File schemaFile ) throws LdapException
    {
        try
        {
            if ( schemaManager == null )
            {
                loadSchema();
            }

            OpenLdapSchemaParser olsp = new OpenLdapSchemaParser();
            olsp.setQuirksMode( true );
            olsp.parse( schemaFile );

            List<AttributeType> atList = olsp.getAttributeTypes();
            AttributeTypeRegistry atRegistry = schemaManager.getRegistries().getAttributeTypeRegistry();
            for ( AttributeType atType : atList )
            {
                atRegistry.addMappingFor( atType );
            }

            List<ObjectClass> ocList = olsp.getObjectClassTypes();
            ObjectClassRegistry ocRegistry = schemaManager.getRegistries().getObjectClassRegistry();
            for ( ObjectClass oc : ocList )
            {
                ocRegistry.register( oc );
            }

            LOG.info( "successfully loaded the schema from file {}", schemaFile.getAbsolutePath() );
        }
        catch ( Exception e )
        {
            LOG.error( "failed to load the schema from file {}", schemaFile.getAbsolutePath() );
            throw new LdapException( e );
        }
    }


    /**
     * @see #addSchema(File)
     */
    public void addSchema( String schemaFileName ) throws LdapException
    {
        addSchema( new File( schemaFileName ) );
    }


    /**
     * {@inheritDoc}
     */
    public LdapCodecService getCodecService()
    {
        return codec;
    }


    /**
     * {@inheritDoc}
     */
    public SchemaManager getSchemaManager()
    {
        return schemaManager;
    }


    /**
     * fetches the rootDSE from the server
     * @throws LdapException
     */
    private void fetchRootDSE() throws LdapException
    {
        Cursor<Response> cursor = null;
        try
        {
            cursor = search( "", "(objectClass=*)", SearchScope.OBJECT, "*", "+" );
            cursor.next();
            SearchResultEntry searchRes = ( SearchResultEntry ) cursor.get();

            rootDSE = searchRes.getEntry();
        }
        catch ( Exception e )
        {
            String msg = "Failed to fetch the RootDSE";
            LOG.error( msg );
            throw new LdapException( msg, e );
        }
        finally
        {
            if ( cursor != null )
            {
                try
                {
                    cursor.close();
                }
                catch ( Exception e )
                {
                    LOG.error( "Failed to close open cursor", e );
                }
            }
        }
    }


    /**
     * gives the configuration information of the connection
     *
     * @return the configuration of the connection
     */
    public LdapConnectionConfig getConfig()
    {
        return config;
    }


    private void addControls( Message codec, Message message )
    {
        Map<String, Control> controls = codec.getControls();

        if ( controls != null )
        {
            for ( Control cc : controls.values() )
            {
                // FIXME why the cc is coming as null!?
                if ( cc == null )
                {
                    continue;
                }

                message.addControl( cc );
            }
        }
    }


    /**
     * removes the Objects associated with the given message ID
     * from future and response queue maps
     *
     * @param msgId id of the message
     */
    private void removeFromFutureMaps( int msgId )
    {
        getFromFutureMap( msgId );
    }


    /**
     * clears the async listener, responseQueue and future mapppings to the corresponding request IDs
     */
    private void clearMaps()
    {
        futureMap.clear();
    }


    /**
     * {@inheritDoc}
     */
    public boolean doesFutureExistFor( int messageId )
    {
        ResponseFuture<?> responseFuture = futureMap.get( messageId );
        return responseFuture != null;
    }


    /**
     * Adds the connection closed event listener.
     *
     * @param ccListener the connection closed listener
     */
    public void addConnectionClosedEventListener( ConnectionClosedEventListener ccListener )
    {
        if ( conCloseListeners == null )
        {
            conCloseListeners = new ArrayList<ConnectionClosedEventListener>();
        }

        conCloseListeners.add( ccListener );
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void sessionClosed( IoSession session ) throws Exception
    {
        // no need to handle if this session was closed by the user
        if ( !connected.get() )
        {
            return;
        }

        ldapSession.close( true );
        connected.set( false );
        // Reset the messageId
        messageId.set( 0 );

        // DO NOT call connector.dispose(), it is hanging when there is no network connection
        // set localConnector flag to false to avoid NPE when close() is called after this sessionClosed() method
        // gets called
        localConnector = false;
        connector = null;

        clearMaps();

        if ( conCloseListeners != null )
        {
            LOG.debug( "notifying the registered ConnectionClosedEventListeners.." );
            for ( ConnectionClosedEventListener listener : conCloseListeners )
            {
                listener.connectionClosed();
            }
        }
    }


    /**
     * Sends the StartTLS extended request to server and adds a security layer
     * upon receiving a response with successful result. Note that we will use
     * the default LDAP connection.
     *
     * @throws LdapException
     */
    public void startTls() throws LdapException
    {
        try
        {
            checkSession();

            ExtendedResponse resp = extended( START_TLS_REQ_OID );
            LdapResult result = resp.getLdapResult();

            if ( result.getResultCode() == ResultCodeEnum.SUCCESS )
            {
                addSslFilter();
            }
            else
            {
                throw new LdapOperationException( result.getResultCode(), result.getErrorMessage() );
            }
        }
        catch ( LdapException e )
        {
            throw e;
        }
        catch ( Exception e )
        {
            throw new LdapException( e );
        }
    }


    /**
     * adds {@link SslFilter} to the IOConnector or IOSession's filter chain
     */
    private void addSslFilter() throws LdapException
    {
        try
        {
            SSLContext sslContext = SSLContext.getInstance( config.getSslProtocol() );
            sslContext.init( config.getKeyManagers(), config.getTrustManagers(), config.getSecureRandom() );

            SslFilter sslFilter = new SslFilter( sslContext );
            sslFilter.setUseClientMode( true );

            // for LDAPS
            if ( ldapSession == null )
            {
                connector.getFilterChain().addFirst( SSL_FILTER_KEY, sslFilter );
            }
            else
            // for StartTLS
            {
                ldapSession.getFilterChain().addFirst( SSL_FILTER_KEY, sslFilter );
            }
        }
        catch ( Exception e )
        {
            String msg = "Failed to initialize the SSL context";
            LOG.error( msg, e );
            throw new LdapException( msg, e );
        }
    }


    /**
     * Process the SASL Bind. It's a dialog with the server, we will send a first BindRequest, receive
     * a response and the, if this response is a challenge, continue by sending a new BindRequest with
     * the requested informations.
     */
    private BindFuture bindSasl( SaslRequest saslRequest ) throws LdapException, IOException
    {
        // First switch to anonymous state
        authenticated.set( false );

        // try to connect, if we aren't already connected.
        connect();

        // If the session has not been establish, or is closed, we get out immediately
        checkSession();

        BindRequest bindRequest = createBindRequest( ( String ) null, null, saslRequest.getSaslMechanism(), saslRequest
            .getControls() );

        // Update the messageId
        int newId = messageId.incrementAndGet();
        bindRequest.setMessageId( newId );

        LOG.debug( "-----------------------------------------------------------------" );
        LOG.debug( "Sending request \n{}", bindRequest );

        // Create a future for this Bind operation
        BindFuture bindFuture = new BindFuture( this, newId );

        // Store it in the future Map
        addToFutureMap( newId, bindFuture );

        try
        {
            BindResponse bindResponse = null;
            byte[] response = null;
            ResultCodeEnum result = null;

            // Creating a map for SASL properties
            Map<String, Object> properties = new HashMap<String, Object>();

            // Quality of Protection SASL property
            if ( saslRequest.getQualityOfProtection() != null )
            {

                properties.put( Sasl.QOP, saslRequest.getQualityOfProtection().getValue() );
            }

            // Security Strength SASL property
            if ( saslRequest.getSecurityStrength() != null )
            {
                properties.put( Sasl.STRENGTH, saslRequest.getSecurityStrength().getValue() );
            }

            // Mutual Authentication SASL property
            if ( saslRequest.isMutualAuthentication() )
            {
                properties.put( Sasl.SERVER_AUTH, "true" );
            }

            // Creating a SASL Client
            SaslClient sc = Sasl.createSaslClient(
                new String[]
                    { bindRequest.getSaslMechanism() },
                saslRequest.getAuthorizationId(),
                "ldap",
                config.getLdapHost(),
                properties,
                new SaslCallbackHandler( saslRequest ) );

            // If the SaslClient wasn't created, that means we can't create the SASL client
            // for the requested mechanism. We then produce an Exception
            if ( sc == null )
            {
                String message = "Cannot find a SASL factory for the " + bindRequest.getSaslMechanism() + " mechanism";
                LOG.error( message );
                throw new LdapException( message );
            }

            // Corner case : the SASL mech might send an initial challenge, and we have to
            // deal with it immediately.
            if ( sc.hasInitialResponse() )
            {
                byte[] challengeResponse = sc.evaluateChallenge( new byte[0] );

                // Stores the challenge's response, and send it to the server
                bindRequest.setCredentials( challengeResponse );
                writeBindRequest( bindRequest );

                // Get the server's response, blocking
                bindResponse = bindFuture.get( timeout, TimeUnit.MILLISECONDS );

                if ( bindResponse == null )
                {
                    // We didn't received anything : this is an error
                    LOG.error( "bind failed : timeout occured" );
                    throw new LdapException( TIME_OUT_ERROR );
                }

                result = bindResponse.getLdapResult().getResultCode();
            }
            else
            {
                // Copy the bindRequest without setting the credentials
                BindRequest bindRequestCopy = new BindRequestImpl( newId );
                bindRequestCopy.setName( bindRequest.getName() );
                bindRequestCopy.setSaslMechanism( bindRequest.getSaslMechanism() );
                bindRequestCopy.setSimple( bindRequest.isSimple() );
                bindRequestCopy.setVersion3( bindRequest.getVersion3() );
                bindRequestCopy.addAllControls( bindRequest.getControls().values().toArray( new Control[0] ) );

                writeBindRequest( bindRequestCopy );

                bindResponse = bindFuture.get( timeout, TimeUnit.MILLISECONDS );

                if ( bindResponse == null )
                {
                    // We didn't received anything : this is an error
                    LOG.error( "bind failed : timeout occured" );
                    throw new LdapException( TIME_OUT_ERROR );
                }

                result = bindResponse.getLdapResult().getResultCode();
            }

            while ( !sc.isComplete()
                && ( ( result == ResultCodeEnum.SASL_BIND_IN_PROGRESS ) || ( result == ResultCodeEnum.SUCCESS ) ) )
            {
                response = sc.evaluateChallenge( bindResponse.getServerSaslCreds() );

                if ( result == ResultCodeEnum.SUCCESS )
                {
                    if ( response != null )
                    {
                        throw new LdapException( "protocol error" );
                    }
                }
                else
                {
                    newId = messageId.incrementAndGet();
                    bindRequest.setMessageId( newId );
                    bindRequest.setCredentials( response );

                    addToFutureMap( newId, bindFuture );

                    writeBindRequest( bindRequest );

                    bindResponse = bindFuture.get( timeout, TimeUnit.MILLISECONDS );

                    if ( bindResponse == null )
                    {
                        // We didn't received anything : this is an error
                        LOG.error( "bind failed : timeout occured" );
                        throw new LdapException( TIME_OUT_ERROR );
                    }

                    result = bindResponse.getLdapResult().getResultCode();
                }
            }

            bindFuture.set( bindResponse );

            return bindFuture;
        }
        catch ( LdapException e )
        {
            throw e;
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            throw new LdapException( e );
        }
    }


    /**
     * a reusable code block to be used in various bind methods
     */
    private void writeBindRequest( BindRequest bindRequest ) throws LdapException
    {
        // Send the request to the server
        WriteFuture writeFuture = ldapSession.write( bindRequest );

        // Wait for the message to be sent to the server
        if ( !writeFuture.awaitUninterruptibly( timeout ) )
        {
            // We didn't received anything : this is an error
            LOG.error( "Bind failed : timeout occured" );

            throw new LdapException( TIME_OUT_ERROR );
        }
    }


    /**
     * method to write the kerberos config in the standard MIT kerberos format
     *
     * This is required cause the JGSS api is not able to recognize the port value set
     * in the system property java.security.krb5.kdc this issue makes it impossible
     * to set a kdc running non standard ports (other than 88)
     *
     * e.g localhost:6088
     *
     * <pre>
     * [libdefaults]
     *     default_realm = EXAMPLE.COM
     *
     * [realms]
     *     EXAMPLE.COM = {
     *         kdc = localhost:6088
     *     }
     * </pre>
     *
     * @return the full path of the config file
     */
    private String createKrbConfFile( String realmName, String kdcHost, int kdcPort ) throws IOException
    {
        StringBuilder sb = new StringBuilder();

        sb.append( "[libdefaults]" )
            .append( "\n\t" );
        sb.append( "default_realm = " )
            .append( realmName )
            .append( "\n" );

        sb.append( "[realms]" )
            .append( "\n\t" );

        sb.append( realmName )
            .append( " = {" )
            .append( "\n\t\t" );
        sb.append( "kdc = " )
            .append( kdcHost )
            .append( ":" )
            .append( kdcPort )
            .append( "\n\t}\n" );

        File krb5Conf = File.createTempFile( "client-api-krb5", ".conf" );
        krb5Conf.deleteOnExit();
        FileWriter fw = new FileWriter( krb5Conf );
        fw.write( sb.toString() );
        fw.close();

        String krbConfPath = krb5Conf.getAbsolutePath();

        LOG.debug( "krb config file created at {}", krbConfPath );

        return krbConfPath;
    }
}
