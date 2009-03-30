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
package org.apache.directory.shared.ldap.client.api;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

import org.apache.directory.shared.asn1.ber.IAsn1Container;
import org.apache.directory.shared.ldap.client.api.protocol.LdapProtocolCodecFactory;
import org.apache.directory.shared.ldap.codec.LdapMessage;
import org.apache.directory.shared.ldap.codec.LdapMessageContainer;
import org.apache.directory.shared.ldap.codec.LdapResponse;
import org.apache.directory.shared.ldap.codec.bind.BindRequest;
import org.apache.directory.shared.ldap.codec.bind.SimpleAuthentication;
import org.apache.directory.shared.ldap.codec.unbind.UnBindRequest;
import org.apache.directory.shared.ldap.cursor.Cursor;
import org.apache.directory.shared.ldap.entry.Entry;
import org.apache.directory.shared.ldap.message.ResultCodeEnum;
import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.shared.ldap.util.StringTools;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 *  Describe the methods to be implemented by the LdapConnection class.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class LdapConnection  extends IoHandlerAdapter
{
    /** logger for reporting errors that might not be handled properly upstream */
    private static final Logger LOG = LoggerFactory.getLogger( LdapConnectionImpl.class );

    /** Define the default ports for LDAP and LDAPS */
    private static final int DEFAULT_LDAP_PORT = 389; 
    private static final int DEFAULT_LDAPS_PORT = 686;
    
    /** The default host : localhost */
    private static final String DEFAULT_LDAP_HOST = "127.0.0.1";
    
    /** The LDAP version */
    private static int LDAP_V3 = 3;
    
    private static final String LDAP_RESPONSE = "LdapReponse";
    
    /** A flag indicating if we are using SSL or not */
    private boolean useSsl = false;
    
    /** The default timeout for operation : 30 seconds */
    private static final long DEFAULT_TIMEOUT = 30000L;
    
    /** The timeout used for response we are waiting for */ 
    private long timeOut = DEFAULT_TIMEOUT;
    
    /** The selected LDAP port */
    private int ldapPort;
    
    /** the remote LDAP host */
    private String ldapHost;
    
    /** The connector open with the remote server */
    private IoConnector connector;
    
    /** A flag set to true when we used a local connector */ 
    private boolean localConnector;
    
    /** The Ldap codec */
    private IoFilter ldapProtocolFilter = new ProtocolCodecFilter(
            new LdapProtocolCodecFactory() );

    /**  
     * The created session, created when we open a connection with
     * the Ldap server.
     */
    private IoSession ldapSession;
    
    /** A Message ID which is incremented for each operation */
    private int messageId;
    
    /** A queue used to store the incoming responses */
    private BlockingQueue<LdapMessage> responseQueue;
    
    /** An operation mutex to guarantee the operation order */
    private Semaphore operationMutex;
    
    /** The search listener used to get results back */
    private SearchListener listener;

    //--------------------------- Helper methods ---------------------------//
    /**
     * Check if the connection is valid : created and connected
     *
     * @return <code>true</code> if the session is valid.
     */
    private boolean isSessionValid()
    {
        return ( ldapSession != null ) && ldapSession.isConnected();
    }

    
    /**
     * Check that a session is valid, ie we can send requests to the
     * server
     *
     * @throws Exception If the session is not valid
     */
    private void checkSession() throws Exception
    {
        if ( !isSessionValid() )
        {
            throw new Exception( "Cannot connect on the server, the connection is invalid" );
        }
    }
    
    /**
     * Return the response stored into the current session.
     *
     * @return The last request response
     */
    public LdapMessage getResponse()
    {
        return (LdapMessage)ldapSession.getAttribute( LDAP_RESPONSE );
    }
    

    //------------------------- The constructors --------------------------//
    /**
     * Create a new instance of a LdapConnection on localhost,
     * port 389.
     */
    public LdapConnection()
    {
        useSsl = false;
        ldapPort = DEFAULT_LDAP_PORT;
        ldapHost = DEFAULT_LDAP_HOST;
        messageId = 1;
        operationMutex = new Semaphore(1);
    }
    
    
    /**
     * Create a new instance of a LdapConnection on localhost,
     * port 389 if the SSL flag is off, or 686 otherwise.
     * 
     * @param useSsl A flag to tell if it's a SSL connection or not.
     */
    public LdapConnection( boolean useSsl )
    {
        this.useSsl = useSsl;
        ldapPort = ( useSsl ? DEFAULT_LDAPS_PORT : DEFAULT_LDAP_PORT );
        ldapHost = DEFAULT_LDAP_HOST;
        messageId = 1;
        operationMutex = new Semaphore(1);
    }
    
    
    /**
     * Create a new instance of a LdapConnection on a given
     * server, using the default port (389).
     *
     * @param server The server we want to be connected to
     */
    public LdapConnection( String server )
    {
        useSsl = false;
        ldapPort = DEFAULT_LDAP_PORT;
        ldapHost = server;
        messageId = 1;
        operationMutex = new Semaphore(1);
    }
    
    
    /**
     * Create a new instance of a LdapConnection on a given
     * server, using the default port (389) if the SSL flag 
     * is off, or 686 otherwise.
     *
     * @param server The server we want to be connected to
     * @param useSsl A flag to tell if it's a SSL connection or not.
     */
    public LdapConnection( String server, boolean useSsl )
    {
        this.useSsl = useSsl;
        ldapPort = ( useSsl ? DEFAULT_LDAPS_PORT : DEFAULT_LDAP_PORT );
        ldapHost = DEFAULT_LDAP_HOST;
        messageId = 1;
        operationMutex = new Semaphore(1);
    }
    
    
    /**
     * Create a new instance of a LdapConnection on a 
     * given server and a given port. We don't use ssl.
     *
     * @param server The server we want to be connected to
     * @param port The port the server is listening to
     */
    public LdapConnection( String server, int port )
    {
        useSsl = false;
        ldapPort = port;
        ldapHost = server;
        messageId = 1;
        operationMutex = new Semaphore(1);
    }
    
    
    /**
     * Create a new instance of a LdapConnection on a given
     * server, and a give port. We set the SSL flag accordingly
     * to the last parameter.
     *
     * @param server The server we want to be connected to
     * @param port The port the server is listening to
     * @param useSsl A flag to tell if it's a SSL connection or not.
     */
    public LdapConnection( String server, int port, boolean useSsl )
    {
        this.useSsl = useSsl;
        ldapPort = port;
        ldapHost = server;
        messageId = 1;
        operationMutex = new Semaphore(1);
    }

    
    //-------------------------- The methods ---------------------------//
    /**
     * Connect to the remote LDAP server.
     *
     * @return <code>true</code> if the connection is established, false otherwise
     * @throws IOException if some I/O error occurs
     */
    public boolean connect() throws IOException
    {
        if ( ( ldapSession != null ) && ldapSession.isConnected() ) 
        {
            throw new IllegalStateException( "Already connected. Disconnect first." );
        }

        // Create the connector if needed
        if ( connector == null ) 
        {
            connector = new NioSocketConnector();
            localConnector = true;
            
            // Add the codec to the chain
            connector.getFilterChain().addLast( "ldapCodec", ldapProtocolFilter );
    
            // If we use SSL, we have to add the SslFilter to the chain
            if ( useSsl ) 
            {
                SSLContext sslContext = null; // BogusSslContextFactory.getInstance( false );
                SslFilter sslFilter = new SslFilter( sslContext );
                sslFilter.setUseClientMode(true);
                connector.getFilterChain().addLast( "sslFilter", sslFilter );
            }
    
            // Inject the protocolHandler
            connector.setHandler( this );
        }
        
        // Build the connection address
        SocketAddress address = new InetSocketAddress( ldapHost , ldapPort );
        
        // And create the connection future
        ConnectFuture connectionFuture = connector.connect( address );
        
        // Wait until it's established
        connectionFuture.awaitUninterruptibly();
        
        if ( !connectionFuture.isConnected() ) 
        {
            return false;
        }
        
        // Get back the session
        ldapSession = connectionFuture.getSession();
        
        // And inject the current Ldap container into the session
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();
        
        // Store the container into the session 
        ldapSession.setAttribute( "LDAP-Container", ldapMessageContainer );
        
        // Create the responses queue
        responseQueue = new LinkedBlockingQueue<LdapMessage>();
        
        // And return
        return true;
    }
    
    
    /**
     * Disconnect from the remote LDAP server
     *
     * @return <code>true</code> if the connection is closed, false otherwise
     * @throws IOException if some I/O error occurs
     */
    public boolean close() throws IOException 
    {
        // Close the session
        if ( ( ldapSession != null ) && ldapSession.isConnected() )
        {
            ldapSession.close( true );
        }
        
        // And close the connector if it has been created locally
        if ( localConnector ) 
        {
            // Release the connector
            connector.dispose();
        }
        
        return true;
    }
    
    
    //------------------------ The LDAP operations ------------------------//
    // Bind operations                                                     //
    //---------------------------------------------------------------------//
    /**
     * Anonymous Bind on a server. 
     *
     * @return The BindResponse LdapResponse 
     */
    public LdapResponse bind() throws Exception
    {
        LOG.debug( " Unauthenticated Authentication bind" );
        
        LdapResponse response = bind( (String)null, (byte[])null );
        
        if (response.getLdapResult().getResultCode() == ResultCodeEnum.SUCCESS )
        {
            LOG.debug( " Unauthenticated Authentication bind successfull" );
        }
        else
        {
            LOG.debug( " Unauthenticated Authentication bind failure {}", response );
        }
        
        return response;
    }
    
    
    /**
     * An Unauthenticated Authentication Bind on a server. (cf RFC 4513,
     * par 5.1.2)
     *
     * @param name The name we use to authenticate the user. It must be a 
     * valid DN
     * @return The BindResponse LdapResponse 
     */
    public LdapResponse bind( String name ) throws Exception
    {
        LOG.debug( "Anonymous bind" );
        
        LdapResponse response = bind( name, (byte[])null );
        
        if (response.getLdapResult().getResultCode() == ResultCodeEnum.SUCCESS )
        {
            LOG.debug( "Anonymous bind successfull" );
        }
        else
        {
            LOG.debug( "Anonymous bind failure {}", response );
        }
        
        return response;
    }

    
    /**
     * Simple Bind on a server.
     *
     * @param name The name we use to authenticate the user. It must be a 
     * valid DN
     * @param credentials The password. It can't be null 
     * @return The BindResponse LdapResponse 
     */
    public LdapResponse bind( String name, String credentials ) throws Exception
    {
        return bind( name, StringTools.getBytesUtf8( credentials ) );
    }


    /**
     * Simple Bind on a server.
     *
     * @param name The name we use to authenticate the user. It must be a 
     * valid DN
     * @param credentials The password.
     * @return The BindResponse LdapResponse 
     */
    public LdapResponse bind( String name, byte[] credentials ) throws Exception
    {
        // If the session has not been establish, or is closed, we get out immediately
        checkSession();

        // Guarantee that for this session, we don't have more than one operation
        // running at the same time
        operationMutex.acquire();
        
        // Create the BindRequest
        LdapDN dn = new LdapDN( name );
        
        BindRequest bindRequest = new BindRequest();
        bindRequest.setName( dn );
        bindRequest.setVersion( LDAP_V3 );
        
        // Create the Simple authentication
        SimpleAuthentication simpleAuth = new SimpleAuthentication();
        simpleAuth.setSimple( credentials );

        bindRequest.setAuthentication( simpleAuth );
        
        // Encode the request
        LdapMessage message = new LdapMessage();
        message.setMessageId( messageId++ );
        message.setProtocolOP( bindRequest );
        
        LOG.debug( "-----------------------------------------------------------------" );
        LOG.debug( "Sending request \n{}", message );

        // Send the request to the server
        ldapSession.write( message );

        // Read the response, waiting for it if not available immediately
        LdapMessage response = responseQueue.poll( timeOut, TimeUnit.MILLISECONDS );
    
        // Check that we didn't get out because of a timeout
        if ( response == null )
        {
            // We didn't received anything : this is an error
            LOG.error( "Bind failed : timeout occured" );
            operationMutex.release();
            throw new Exception( "TimeOut occured" );
        }
        
        operationMutex.release();
        
        // Everything is fine, return the response
        LdapResponse resp = response.getBindResponse();
        
        LOG.debug( "Bind successful : {}", resp );
        
        return resp;
    }

    
    //------------------------ The LDAP operations ------------------------//
    // Search operations                                                   //
    //---------------------------------------------------------------------//
    /**
     * Do a search, on the base object, using the given filter. The
     * SearchRequest parameters default to :
     * Scope : ONE
     * DerefAlias : ALWAYS
     * SizeLimit : none
     * TimeLimit : none
     * TypesOnly : false
     * Attributes : all the user's attributes.
     * This method is blocking.
     * 
     * @param baseObject The base for the search. It must be a valid
     * DN, and can't be emtpy
     * @param filter The filter to use for this search. It can't be empty
     * @return A cursor on the result. 
     */
    public Cursor<Entry> search( String baseObject, String filter ) throws Exception
    {
        return null;
    }
    
    
    /**
     * Do a search, on the base object, using the given filter. The
     * SearchRequest parameters default to :
     * Scope : ONE
     * DerefAlias : ALWAYS
     * SizeLimit : none
     * TimeLimit : none
     * TypesOnly : false
     * Attributes : all the user's attributes.
     * This method is blocking.
     * 
     * @param listener a SearchListener used to be informed when a result 
     * has been found, or when the search is done
     * @param baseObject The base for the search. It must be a valid
     * DN, and can't be emtpy
     * @param filter The filter to use for this search. It can't be empty
     * @return A cursor on the result. 
     */
    public void search( SearchListener listener, String baseObject, String filter ) throws Exception
    {
        
    }
    
    //------------------------ The LDAP operations ------------------------//
    // Unbind operations                                                   //
    //---------------------------------------------------------------------//
    /**
     * UnBind from a server
     */
    public void unBind() throws Exception
    {
        // If the session has not been establish, or is closed, we get out immediately
        checkSession();
        
        // Guarantee that for this session, we don't have more than one operation
        // running at the same time
        operationMutex.acquire();
        
        // Create the UnBindRequest
        UnBindRequest unBindRequest = new UnBindRequest();
        
        // Encode the request
        LdapMessage message = new LdapMessage();
        message.setMessageId( messageId );
        message.setProtocolOP( unBindRequest );
        
        LOG.debug( "-----------------------------------------------------------------" );
        LOG.debug( "Sending Unbind request \n{}", message );
        
        // Send the request to the server
        ldapSession.write( message );

        // We don't have to wait for a response. Reset the messageId counter to 0
        messageId = 0;
        
        // We also have to reset the response queue
        responseQueue.clear();
        
        operationMutex.release();
        LOG.debug( "Unbind successful" );
    }
    
    
    /**
     * 
     * Adds a SearchListener which can handle the results of a search request.
     *
     * @param listener an instance of SearchListener implementation.
     */
    public void addListener( SearchListener listener )
    {
        this.listener = listener;
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
     * Set the timeOut for the responses. We wont wait longer than this 
     * value.
     *
     * @param timeOut The timeout, in milliseconds
     */
    public void setTimeOut( long timeOut )
    {
        this.timeOut = timeOut;
    }
    
    
}
