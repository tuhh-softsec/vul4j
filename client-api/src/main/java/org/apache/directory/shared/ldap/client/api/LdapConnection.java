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
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import javax.naming.InvalidNameException;
import javax.naming.ldap.Control;
import javax.net.ssl.SSLContext;

import org.apache.directory.shared.asn1.ber.IAsn1Container;
import org.apache.directory.shared.ldap.client.api.exception.InvalidConnectionException;
import org.apache.directory.shared.ldap.client.api.exception.LdapException;
import org.apache.directory.shared.ldap.client.api.listeners.BindListener;
import org.apache.directory.shared.ldap.client.api.listeners.SearchListener;
import org.apache.directory.shared.ldap.client.api.messages.BindRequest;
import org.apache.directory.shared.ldap.client.api.messages.BindRequestImpl;
import org.apache.directory.shared.ldap.client.api.protocol.LdapProtocolCodecFactory;
import org.apache.directory.shared.ldap.codec.LdapConstants;
import org.apache.directory.shared.ldap.codec.LdapMessage;
import org.apache.directory.shared.ldap.codec.LdapMessageContainer;
import org.apache.directory.shared.ldap.codec.LdapResponse;
import org.apache.directory.shared.ldap.codec.bind.LdapAuthentication;
import org.apache.directory.shared.ldap.codec.bind.SaslCredentials;
import org.apache.directory.shared.ldap.codec.bind.SimpleAuthentication;
import org.apache.directory.shared.ldap.codec.search.Filter;
import org.apache.directory.shared.ldap.codec.search.SearchRequest;
import org.apache.directory.shared.ldap.codec.search.SearchResultDone;
import org.apache.directory.shared.ldap.codec.search.SearchResultEntry;
import org.apache.directory.shared.ldap.codec.search.SearchResultReference;
import org.apache.directory.shared.ldap.codec.unbind.UnBindRequest;
import org.apache.directory.shared.ldap.constants.SchemaConstants;
import org.apache.directory.shared.ldap.cursor.Cursor;
import org.apache.directory.shared.ldap.cursor.ListCursor;
import org.apache.directory.shared.ldap.entry.Entry;
import org.apache.directory.shared.ldap.filter.ExprNode;
import org.apache.directory.shared.ldap.filter.FilterParser;
import org.apache.directory.shared.ldap.filter.SearchScope;
import org.apache.directory.shared.ldap.message.AddResponse;
import org.apache.directory.shared.ldap.message.BindResponse;
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
    
    /** A queue used to store the incoming add responses */
    private BlockingQueue<LdapMessage> addResponseQueue;
    
    /** A queue used to store the incoming bind responses */
    private BlockingQueue<LdapMessage> bindResponseQueue;
    
    /** A queue used to store the incoming compare responses */
    private BlockingQueue<LdapMessage> compareResponseQueue;
    
    /** A queue used to store the incoming delete responses */
    private BlockingQueue<LdapMessage> deleteResponseQueue;
    
    /** A queue used to store the incoming extended responses */
    private BlockingQueue<LdapMessage> extendedResponseQueue;
    
    /** A queue used to store the incoming modify responses */
    private BlockingQueue<LdapMessage> modifyResponseQueue;
    
    /** A queue used to store the incoming modifyDN responses */
    private BlockingQueue<LdapMessage> modifyDNResponseQueue;
    
    /** A queue used to store the incoming search responses */
    private BlockingQueue<LdapMessage> searchResponseQueue;
    
    /** A queue used to store the incoming intermediate responses */
    private BlockingQueue<LdapMessage> intermediateResponseQueue;
    
    
    /** An operation mutex to guarantee the operation order */
    private Semaphore operationMutex;
    
    /** The listeners used to get results back */
    private SearchListener searchListener;
    private BindListener bindListener;

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
    private void checkSession() throws InvalidConnectionException
    {
        if ( !isSessionValid() )
        {
            throw new InvalidConnectionException( "Cannot connect on the server, the connection is invalid" );
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
    
    
    /**
     * Handle the lock mechanism on session
     */
    private void lock() throws LdapException
    {
        try
        {
            operationMutex.acquire();
        }
        catch ( InterruptedException ie )
        {
            String message = "Cannot acquire the session lock";
            LOG.error(  message );
            LdapException ldapException = 
                new LdapException( message );
            ldapException.initCause( ie );
            
            throw ldapException;
        }
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
        
        // Create the responses queues
        addResponseQueue = new LinkedBlockingQueue<LdapMessage>();
        bindResponseQueue = new LinkedBlockingQueue<LdapMessage>();
        compareResponseQueue = new LinkedBlockingQueue<LdapMessage>();
        deleteResponseQueue = new LinkedBlockingQueue<LdapMessage>();
        extendedResponseQueue = new LinkedBlockingQueue<LdapMessage>();
        modifyResponseQueue = new LinkedBlockingQueue<LdapMessage>();
        modifyDNResponseQueue = new LinkedBlockingQueue<LdapMessage>();
        searchResponseQueue = new LinkedBlockingQueue<LdapMessage>();
        intermediateResponseQueue = new LinkedBlockingQueue<LdapMessage>();
        
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
        
        // clean the queues
        addResponseQueue.clear();
        bindResponseQueue.clear();
        compareResponseQueue.clear();
        deleteResponseQueue.clear();
        extendedResponseQueue.clear();
        modifyResponseQueue.clear();
        modifyDNResponseQueue.clear();
        searchResponseQueue.clear();
        intermediateResponseQueue.clear();
        
        // And close the connector if it has been created locally
        if ( localConnector ) 
        {
            // Release the connector
            connector.dispose();
        }
        
        return true;
    }
    
    //------------------------ The LDAP operations ------------------------//
    // Add operations                                                      //
    //---------------------------------------------------------------------//
    /**
     * Add an entry to the server. This is a blocking add : the user has 
     * to wait for the response until the AddResponse is returned.
     * 
     * @param entry The entry to add
     * @result AddResponse The resulting response 
     *
    public AddResponse add( Entry entry )
    {
        if ( entry == null ) 
        {
            LOG.debug( "Cannot add empty entry" );
            return null;
        }
        
        
    }
    
    
    public void add( AddRequest addRequest ) throws InvalidConnectionException
    {
        // If the session has not been establish, or is closed, we get out immediately
        checkSession();

        // Guarantee that for this session, we don't have more than one operation
        // running at the same time
        lock();
        
        // Create the AddRequest
        LdapDN dn = new LdapDN( name );
        
        InternalAddRequest addRequest = new InternalBindRequest();
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
        LdapMessage response = bindResponseQueue.poll( timeOut, TimeUnit.MILLISECONDS );
    
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
    */
    
    //------------------------ The LDAP operations ------------------------//
    // Bind operations                                                     //
    //---------------------------------------------------------------------//
    /**
     * Anonymous Bind on a server. 
     *
     * @return The BindResponse LdapResponse 
     */
    public LdapResponse bind()  throws LdapException
    {
        return bind( (String)null, (byte[])null );
    }
    
    
    /**
     * An Unauthenticated Authentication Bind on a server. (cf RFC 4513,
     * par 5.1.2)
     *
     * @param name The name we use to authenticate the user. It must be a 
     * valid DN
     * @return The BindResponse LdapResponse 
     *
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
    public LdapResponse bind( String name, String credentials ) throws LdapException
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
    public LdapResponse bind( String name, byte[] credentials )  throws LdapException
    {
        LOG.debug( "Bind request : {}", name );

        // Create the BindRequest
        BindRequest bindRequest = new BindRequestImpl();
        bindRequest.setName( name );
        bindRequest.setCredentials( credentials );
        
        LdapResponse response = bind( bindRequest );

        if ( response.getLdapResult().getResultCode() == ResultCodeEnum.SUCCESS )
        {
            LOG.debug( " Bind successfull" );
        }
        else
        {
            LOG.debug( " Bind failure {}", response );
        }

        return response;
    }
    
    
    /**
     * Bind to the server using a BindRequest object.
     *
     * @param bindRequest The BindRequest POJO containing all the needed
     * parameters 
     * @return A LdapResponse containing the result
     */
    public LdapResponse bind( BindRequest bindRequest ) throws LdapException
    {
        return bind( bindRequest, null );
    }
        

    /**
     * Do the bind blocking or non-blocking, depending on the listener value.
     *
     * @param bindRequest The BindRequest to send
     * @param listener The listener (Can be null) 
     */
    public LdapResponse bind( BindRequest bindRequest, BindListener bindListener ) throws LdapException 
    {
        // If the session has not been establish, or is closed, we get out immediately
        checkSession();

        // Guarantee that for this session, we don't have more than one operation
        // running at the same time
        lock();
        
        // Encode the request
        LdapMessage message = new LdapMessage();
        message.setMessageId( messageId++ );
        
        // Create a new codec BindRequest object
        org.apache.directory.shared.ldap.codec.bind.BindRequest request = 
            new org.apache.directory.shared.ldap.codec.bind.BindRequest();
        
        // Set the name
        try
        {
            LdapDN dn = new LdapDN( bindRequest.getName() );
            request.setName( dn );
        }
        catch ( InvalidNameException ine )
        {
            LOG.error( "The given dn '{}' is not valid", bindRequest.getName() );
            LdapException ldapException = new LdapException();
            ldapException.initCause( ine );
            throw ldapException;
        }
        
        // Set the credentials
        LdapAuthentication authentication = null;
        
        if ( bindRequest.isSimple() )
        {
            // Simple bind
            authentication = new SimpleAuthentication();
            ((SimpleAuthentication)authentication).setSimple( bindRequest.getCredentials() );
        }
        else
        {
            // SASL bind
            authentication = new SaslCredentials();
            ((SaslCredentials)authentication).setCredentials( bindRequest.getCredentials() );
            ((SaslCredentials)authentication).setMechanism( bindRequest.getSaslMechanism() );
        }
        
        // The authentication
        request.setAuthentication( authentication );
        
        // Stores the BindRequest into the message
        message.setProtocolOP( request );
        
        // Add the controls
        Map<String, Control> controls = bindRequest.getControls();
        
        if ( controls != null )
        {
            for ( Control control:controls.values() )
            {
                org.apache.directory.shared.ldap.codec.Control ctrl = 
                    new org.apache.directory.shared.ldap.codec.Control();
                
                ctrl.setControlType( control.getID() );
                ctrl.setControlValue( control.getEncodedValue() );
                
                message.addControl( ctrl );
            }
        }

        // Set the message ID now
        message.setMessageId( messageId++ );
        
        LOG.debug( "-----------------------------------------------------------------" );
        LOG.debug( "Sending request \n{}", message );

        // Send the request to the server
        ldapSession.write( message );

        if ( bindListener == null )
        {
            // Read the response, waiting for it if not available immediately
            try
            {
                LdapMessage response = bindResponseQueue.poll( bindRequest.getTimeout(), TimeUnit.MILLISECONDS );
            
                // Check that we didn't get out because of a timeout
                if ( response == null )
                {
                    // TODO Send an abandon request here
                    //abandon( message.getBindRequest().getMessageId() );
                    
                    // We didn't received anything : this is an error
                    LOG.error( "Bind failed : timeout occured" );
                    operationMutex.release();
                    throw new LdapException( "TimeOut occured" );
                }
                
                operationMutex.release();
                
                // Everything is fine, return the response
                LdapResponse resp = response.getBindResponse();
                
                LOG.debug( "Bind successful : {}", resp );
                
                return resp;
            }
            catch ( InterruptedException ie )
            {
                LOG.error( "The response queue has been emptied, no response will be find." );
                LdapException ldapException = new LdapException();
                ldapException.initCause( ie );
                //abandon( message.getBindRequest().getMessageId() );
                throw ldapException;
            }
        }
        else
        {
            // The listener will be called on a MessageReceived event
            return null;
        }
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
     * @param filterString The filter to use for this search. It can't be empty
     * @return A cursor on the result. 
     *
    public Cursor<Entry> search( String baseObject, String filterString ) throws Exception
    {
        // If the session has not been establish, or is closed, we get out immediately
        checkSession();
        
        LdapDN baseDN = null;
        Filter filter = null;
        
        // Check that the baseObject is not null or empty, 
        // and is a valid DN
        if ( StringTools.isEmpty( baseObject ) )
        {
            throw new Exception( "Cannot search on RootDSE when the scope is not BASE" );
        }
        
        try
        {
            baseDN = new LdapDN( baseObject );
        }
        catch ( InvalidNameException ine )
        {
            throw new Exception( "The baseObject is not a valid DN" );
        }
        
        // Check that the filter is valid
        try
        {
            ExprNode filterNode = FilterParser.parse( filterString );
            
            filter = TwixTransformer.transformFilter( filterNode );
        }
        catch ( ParseException pe )
        {
            throw new Exception( "The filter is invalid" );
        }
        
        // Create the searchRequest
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setBaseObject( baseDN );
        searchRequest.setFilter( filter );
        
        // Fill the default values
        searchRequest.setSizeLimit( 0 );
        searchRequest.setTimeLimit( 0 );
        searchRequest.setDerefAliases( LdapConstants.DEREF_ALWAYS );
        searchRequest.setScope( SearchScope.ONELEVEL );
        searchRequest.setTypesOnly( false );
        searchRequest.addAttribute( SchemaConstants.ALL_USER_ATTRIBUTES );

        // Send the search request
        search( searchRequest );
        
        // We now have to create the cursor around the response, and return it.
        Cursor<LdapResponse> searchCursor = new ListCursor<LdapResponse>();
        
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
    
    
    /**
     * {@inheritDoc}
     */
    private void search( SearchRequest searchRequest ) throws Exception
    {
        // First check the session
        checkSession();
        
        // Guarantee that for this session, we don't have more than one operation
        // running at the same time
        lock();
        
        // Encode the request
        LdapMessage message = new LdapMessage();
        message.setMessageId( messageId++ );
        message.setProtocolOP( searchRequest );
        message.addControl( searchRequest.getCurrentControl() );
        
        LOG.debug( "-----------------------------------------------------------------" );
        LOG.debug( "Sending request \n{}", message );
    
        // Loop and get all the responses
        // Send the request to the server
        ldapSession.write( message );

        operationMutex.release();
//        int i = 0;
//        
//        List<SearchResultEntry> searchResults = new ArrayList<SearchResultEntry>();
        
        // Now wait for the responses
        // Loop until we got all the responses
        
/*        do
        {
            // If we get out before the timeout, check that the response 
            // is there, and get it
            LdapMessage response = responseQueue.poll( timeOut, TimeUnit.MILLISECONDS );
            
            if ( response == null )
            {
                // No response, get out
                operationMutex.release();
                
                // We didn't received anything : this is an error
                throw new Exception( "TimeOut occured" );
            }
            
            i++;

            // Print the response
//            System.out.println( "Result[" + i + "]" + response );
            
            if( response.getMessageType() == LdapConstants.INTERMEDIATE_RESPONSE )
            {
                consumer.handleSyncInfo( response.getIntermediateResponse().getResponseValue() );
            }
            
            if ( response.getMessageType() == LdapConstants.SEARCH_RESULT_DONE )
            {
                SearchResultDone resDone = response.getSearchResultDone();
                resDone.addControl( response.getCurrentControl() );
                
                operationMutex.release();
                consumer.handleSearchDone( resDone );
                
                return;
            }
       
            if( response.getMessageType() == LdapConstants.SEARCH_RESULT_ENTRY )
            {
                SearchResultEntry sre = response.getSearchResultEntry();
                sre.addControl( response.getCurrentControl() );
                consumer.handleSearchResult( sre );
            }
            
            if( response.getMessageType() == LdapConstants.SEARCH_RESULT_REFERENCE )
            {
                SearchResultReference searchRef = response.getSearchResultReference();
                searchRef.addControl( response.getCurrentControl() );
                
                consumer.handleSearchReference( searchRef );
            }
        }
        while ( true );
*/    }
    
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
        lock();
        
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
        
        // We also have to reset the response queues
        bindResponseQueue.clear();
        
        operationMutex.release();
        LOG.debug( "Unbind successful" );
    }
    
    
    /**
     * 
     * Adds a SearchListener which can handle the results of a search request.
     *
     * @param listener an instance of SearchListener implementation.
     */
    public void addListener( SearchListener searchListener )
    {
        this.searchListener = searchListener;
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
    
    
    /**
     * Handle the incoming LDAP messages. This is where we feed the cursor for search 
     * requests, or call the listener. 
     */
    public void messageReceived( IoSession session, Object message) throws Exception 
    {
        // Feed the response and store it into the session
        LdapMessage response = (LdapMessage)message;

        LOG.debug( "-------> {} Message received <-------", response.getMessageTypeName() );
        
        switch ( response.getMessageType() )
        {
            case LdapConstants.ADD_RESPONSE :
                // Store the response into the responseQueue
                addResponseQueue.add( response ); 
                break;
                
            case LdapConstants.BIND_RESPONSE: 
                if ( bindListener != null )
                {
                    bindListener.bindCompleted( this, response.getBindResponse() );
                }
                else
                {
                    // Store the response into the responseQueue
                    bindResponseQueue.add( response.getBindResponse() );
                }
                
                break;
                
            case LdapConstants.COMPARE_RESPONSE :
                // Store the response into the responseQueue
                compareResponseQueue.add( response ); 
                break;
                
            case LdapConstants.DEL_RESPONSE :
                // Store the response into the responseQueue
                deleteResponseQueue.add( response ); 
                break;
                
            case LdapConstants.EXTENDED_RESPONSE :
                // Store the response into the responseQueue
                extendedResponseQueue.add( response ); 
                break;
                
            case LdapConstants.INTERMEDIATE_RESPONSE:
                //consumer.handleSyncInfo( response.getIntermediateResponse().getResponseValue() );
                break;
     
            case LdapConstants.MODIFY_RESPONSE :
                // Store the response into the responseQueue
                modifyResponseQueue.add( response ); 
                break;
                
            case LdapConstants.MODIFYDN_RESPONSE :
                // Store the response into the responseQueue
                modifyDNResponseQueue.add( response ); 
                break;
                
            case LdapConstants.SEARCH_RESULT_DONE:
                // Store the response into the responseQueue
                SearchResultDone resDone = response.getSearchResultDone();
                resDone.addControl( response.getCurrentControl() );
                
                if ( searchListener != null )
                {
                    searchListener.searchDone( this, response.getLdapResponse() );
                }
                else
                {
                    searchResponseQueue.add( resDone );
                }
                
                break;
            
            case LdapConstants.SEARCH_RESULT_ENTRY:
                // Store the response into the responseQueue
                SearchResultEntry searchResultEntry = response.getSearchResultEntry();
                searchResultEntry.addControl( response.getCurrentControl() );
                
                if ( searchListener != null )
                {
                    searchListener.entryFound( this, searchResultEntry );
                }
                else
                {
                    searchResponseQueue.add( searchResultEntry );
                }
                
                break;
                       
            case LdapConstants.SEARCH_RESULT_REFERENCE:
                // Store the response into the responseQueue
                SearchResultReference searchRef = response.getSearchResultReference();
                searchRef.addControl( response.getCurrentControl() );

                if ( searchListener != null )
                {
                    searchListener.referralFound( this, searchRef );
                }
                else
                {
                    searchResponseQueue.add( searchRef );
                }

                break;
                       
             default: LOG.error( "~~~~~~~~~~~~~~~~~~~~~ Unknown message type {} ~~~~~~~~~~~~~~~~~~~~~", response.getMessageTypeName() );
        }
    }
}
