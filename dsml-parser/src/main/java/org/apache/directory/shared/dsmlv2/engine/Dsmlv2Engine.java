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

package org.apache.directory.shared.dsmlv2.engine;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.apache.directory.shared.asn1.ber.Asn1Decoder;
import org.apache.directory.shared.asn1.ber.IAsn1Container;
import org.apache.directory.shared.asn1.ber.tlv.TLVStateEnum;
import org.apache.directory.shared.asn1.codec.DecoderException;
import org.apache.directory.shared.asn1.codec.EncoderException;
import org.apache.directory.shared.dsmlv2.Dsmlv2Parser;
import org.apache.directory.shared.dsmlv2.reponse.AddResponseDsml;
import org.apache.directory.shared.dsmlv2.reponse.AuthResponseDsml;
import org.apache.directory.shared.dsmlv2.reponse.BatchResponseDsml;
import org.apache.directory.shared.dsmlv2.reponse.CompareResponseDsml;
import org.apache.directory.shared.dsmlv2.reponse.DelResponseDsml;
import org.apache.directory.shared.dsmlv2.reponse.ErrorResponse;
import org.apache.directory.shared.dsmlv2.reponse.ExtendedResponseDsml;
import org.apache.directory.shared.dsmlv2.reponse.ModDNResponseDsml;
import org.apache.directory.shared.dsmlv2.reponse.ModifyResponseDsml;
import org.apache.directory.shared.dsmlv2.reponse.SearchResponseDsml;
import org.apache.directory.shared.dsmlv2.reponse.SearchResultDoneDsml;
import org.apache.directory.shared.dsmlv2.reponse.SearchResultEntryDsml;
import org.apache.directory.shared.dsmlv2.reponse.SearchResultReferenceDsml;
import org.apache.directory.shared.dsmlv2.reponse.ErrorResponse.ErrorResponseType;
import org.apache.directory.shared.dsmlv2.request.BatchRequest;
import org.apache.directory.shared.dsmlv2.request.BatchRequest.OnError;
import org.apache.directory.shared.dsmlv2.request.BatchRequest.Processing;
import org.apache.directory.shared.dsmlv2.request.BatchRequest.ResponseOrder;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.codec.LdapMessageCodec;
import org.apache.directory.shared.ldap.codec.LdapMessageContainer;
import org.apache.directory.shared.ldap.codec.LdapResponseCodec;
import org.apache.directory.shared.ldap.codec.MessageTypeEnum;
import org.apache.directory.shared.ldap.codec.add.AddResponseCodec;
import org.apache.directory.shared.ldap.codec.bind.BindRequestCodec;
import org.apache.directory.shared.ldap.codec.bind.BindResponseCodec;
import org.apache.directory.shared.ldap.codec.bind.LdapAuthentication;
import org.apache.directory.shared.ldap.codec.bind.SimpleAuthentication;
import org.apache.directory.shared.ldap.codec.compare.CompareResponseCodec;
import org.apache.directory.shared.ldap.codec.del.DelResponseCodec;
import org.apache.directory.shared.ldap.codec.extended.ExtendedResponseCodec;
import org.apache.directory.shared.ldap.codec.modify.ModifyResponseCodec;
import org.apache.directory.shared.ldap.codec.modifyDn.ModifyDNResponseCodec;
import org.apache.directory.shared.ldap.codec.search.SearchResultDoneCodec;
import org.apache.directory.shared.ldap.codec.search.SearchResultEntryCodec;
import org.apache.directory.shared.ldap.codec.search.SearchResultReferenceCodec;
import org.apache.directory.shared.ldap.exception.LdapInvalidDnException;
import org.apache.directory.shared.ldap.message.ResultCodeEnum;
import org.apache.directory.shared.ldap.message.control.Control;
import org.apache.directory.shared.ldap.name.DN;
import org.apache.directory.shared.ldap.util.StringTools;
import org.xmlpull.v1.XmlPullParserException;


/**
 * This is the DSMLv2Engine. It can be use to execute operations on a LDAP Server and get the results of these operations.
 * The format used for request and responses is the DSMLv2 format.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class Dsmlv2Engine
{
    /** Socket used to connect to the server */
    private SocketChannel channel;
    private SocketAddress serverAddress;

    // server configuration
    private int port;
    private String host;
    private String user;
    private String password;

    private Asn1Decoder ldapDecoder = new Asn1Decoder();

    private IAsn1Container ldapMessageContainer = new LdapMessageContainer();

    private Dsmlv2Parser parser;

    private boolean continueOnError;
    private boolean exit = false;

    private int bbLimit;

    private int bbposition;
    private BatchRequest batchRequest;
    private BatchResponseDsml batchResponse;


    /**
     * Creates a new instance of Dsmlv2Engine.
     * 
     * @param host 
     *      the server host
     * @param port 
     *      the server port
     * @param user 
     *      the server admin DN
     * @param password 
     *      the server admin's password
     */
    public Dsmlv2Engine( String host, int port, String user, String password )
    {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
    }


    /**
     * Processes the file given and return the result of the operations
     * 
     * @param dsmlInput 
     *      the DSMLv2 formatted request input
     * @return
     *      the XML response in DSMLv2 Format
     * @throws XmlPullParserException
     *      if an error occurs in the parser
     */
    public String processDSML( String dsmlInput ) throws XmlPullParserException
    {
        parser = new Dsmlv2Parser();
        parser.setInput( dsmlInput );
        return processDSML();
    }


    /**
     * Processes the file given and return the result of the operations
     * 
     * @param fileName 
     *      the path to the file
     * @return 
     *      the XML response in DSMLv2 Format
     * @throws XmlPullParserException
     *      if an error occurs in the parser
     * @throws FileNotFoundException
     *      if the file does not exist
     */
    public String processDSMLFile( String fileName ) throws XmlPullParserException, FileNotFoundException
    {
        parser = new Dsmlv2Parser();
        parser.setInputFile( fileName );
        return processDSML();
    }


    /**
     * Processes the file given and return the result of the operations
     * 
     * @param inputStream 
     *      contains a raw byte input stream of possibly unknown encoding (when inputEncoding is null).
     * @param inputEncoding 
     *      if not null it MUST be used as encoding for inputStream
     * @return 
     *      the XML response in DSMLv2 Format
     * @throws XmlPullParserException
     *      if an error occurs in the parser
     */
    public String processDSML( InputStream inputStream, String inputEncoding ) throws XmlPullParserException
    {
        parser = new Dsmlv2Parser();
        parser.setInput( inputStream, inputEncoding );
        return processDSML();
    }


    /**
     * Processes the Request document
     * 
     * @return 
     *      the XML response in DSMLv2 Format
     */
    private String processDSML()
    {
        batchResponse = new BatchResponseDsml();

        // Binding to LDAP Server
        try
        {
            bind( 1 );
        }
        catch ( Exception e )
        {
            // Unable to connect to server
            // We create a new ErrorResponse and return the XML response.
            ErrorResponse errorResponse = new ErrorResponse( 0, ErrorResponseType.COULD_NOT_CONNECT, e
                .getLocalizedMessage() );
            batchResponse.addResponse( errorResponse );
            return batchResponse.toDsml();
        }

        // Processing BatchRequest:
        //    - Parsing and Getting BatchRequest
        //    - Getting and registering options from BatchRequest
        try
        {
            processBatchRequest();
        }
        catch ( XmlPullParserException e )
        {
            // We create a new ErrorResponse and return the XML response.
            ErrorResponse errorResponse = new ErrorResponse( 0, ErrorResponseType.MALFORMED_REQUEST, I18n.err(
                I18n.ERR_03001, e.getLocalizedMessage(), e.getLineNumber(), e.getColumnNumber() ) );
            batchResponse.addResponse( errorResponse );
            return batchResponse.toDsml();
        }

        // Processing each request:
        //    - Getting a new request
        //    - Checking if the request is well formed
        //    - Sending the request to the server
        //    - Getting and converting reponse(s) as XML
        //    - Looping until last request
        LdapMessageCodec request = null;
        try
        {
            request = parser.getNextRequest();
        }
        catch ( XmlPullParserException e )
        {
            // We create a new ErrorResponse and return the XML response.
            ErrorResponse errorResponse = new ErrorResponse( 0, ErrorResponseType.MALFORMED_REQUEST, I18n.err(
                I18n.ERR_03001, e.getLocalizedMessage(), e.getLineNumber(), e.getColumnNumber() ) );
            batchResponse.addResponse( errorResponse );
            return batchResponse.toDsml();
        }

        while ( request != null ) // (Request == null when there's no more request to process)
        {
            // Checking the request has a requestID attribute if Processing = Parallel and ResponseOrder = Unordered
            if ( ( batchRequest.getProcessing().equals( Processing.PARALLEL ) )
                && ( batchRequest.getResponseOrder().equals( ResponseOrder.UNORDERED ) )
                && ( request.getMessageId() == 0 ) )
            {
                // Then we have to send an errorResponse
                ErrorResponse errorResponse = new ErrorResponse( 0, ErrorResponseType.MALFORMED_REQUEST, I18n
                    .err( I18n.ERR_03002 ) );
                batchResponse.addResponse( errorResponse );
                return batchResponse.toDsml();
            }

            try
            {
                processRequest( request );
            }
            catch ( Exception e )
            {
                // We create a new ErrorResponse and return the XML response.
                ErrorResponse errorResponse = new ErrorResponse( 0, ErrorResponseType.GATEWAY_INTERNAL_ERROR, I18n.err(
                    I18n.ERR_03003, e.getMessage() ) );
                batchResponse.addResponse( errorResponse );
                return batchResponse.toDsml();
            }

            // Checking if we need to exit processing (if an error has occurred if onError == Exit)
            if ( exit )
            {
                break;
            }

            // Getting next request
            try
            {
                request = parser.getNextRequest();
            }
            catch ( XmlPullParserException e )
            {
                // We create a new ErrorResponse and return the XML response.
                ErrorResponse errorResponse = new ErrorResponse( 0, ErrorResponseType.MALFORMED_REQUEST, I18n.err(
                    I18n.ERR_03001, e.getLocalizedMessage(), e.getLineNumber(), e.getColumnNumber() ) );
                batchResponse.addResponse( errorResponse );
                return batchResponse.toDsml();
            }
        }

        return batchResponse.toDsml();
    }


    /**
     * Processes a single request
     * 
     * @param request 
     *      the request to process
     * @throws EncoderException 
     * @throws IOException 
     * @throws DecoderException 
     */
    private void processRequest( LdapMessageCodec request ) throws EncoderException, IOException, DecoderException
    {
        ByteBuffer bb = request.encode();

        bb.flip();

        sendMessage( bb );

        bb.clear();
        bb.position( bb.capacity() );

        // Get the response
        LdapMessageCodec response = null;

        response = readResponse( bb );

        switch ( response.getMessageType() )
        {
            case ADD_RESPONSE:
                AddResponseCodec addResponse = ( AddResponseCodec ) response;
                copyMessageIdAndControls( response, addResponse );

                AddResponseDsml addResponseDsml = new AddResponseDsml( addResponse );
                batchResponse.addResponse( addResponseDsml );
                break;

            case BIND_RESPONSE:
                BindResponseCodec bindResponse = ( BindResponseCodec ) response;
                copyMessageIdAndControls( response, bindResponse );

                AuthResponseDsml authResponseDsml = new AuthResponseDsml( bindResponse );
                batchResponse.addResponse( authResponseDsml );
                break;

            case COMPARE_RESPONSE:
                CompareResponseCodec compareResponse = ( CompareResponseCodec ) response;
                copyMessageIdAndControls( response, compareResponse );

                CompareResponseDsml compareResponseDsml = new CompareResponseDsml( compareResponse );
                batchResponse.addResponse( compareResponseDsml );
                break;

            case DEL_RESPONSE:
                DelResponseCodec delResponse = ( DelResponseCodec ) response;
                copyMessageIdAndControls( response, delResponse );

                DelResponseDsml delResponseDsml = new DelResponseDsml( delResponse );
                batchResponse.addResponse( delResponseDsml );
                break;

            case MODIFY_RESPONSE:
                ModifyResponseCodec modifyResponse = ( ModifyResponseCodec ) response;
                copyMessageIdAndControls( response, modifyResponse );

                ModifyResponseDsml modifyResponseDsml = new ModifyResponseDsml( modifyResponse );
                batchResponse.addResponse( modifyResponseDsml );
                break;

            case MODIFYDN_RESPONSE:
                ModifyDNResponseCodec modifyDNResponse = ( ModifyDNResponseCodec ) response;
                copyMessageIdAndControls( response, modifyDNResponse );

                ModDNResponseDsml modDNResponseDsml = new ModDNResponseDsml( modifyDNResponse );
                batchResponse.addResponse( modDNResponseDsml );
                break;

            case EXTENDED_RESPONSE:
                ExtendedResponseCodec extendedResponse = ( ExtendedResponseCodec ) response;
                copyMessageIdAndControls( response, extendedResponse );

                ExtendedResponseDsml extendedResponseDsml = new ExtendedResponseDsml( extendedResponse );
                batchResponse.addResponse( extendedResponseDsml );
                break;

            case SEARCH_RESULT_ENTRY:
            case SEARCH_RESULT_REFERENCE:
            case SEARCH_RESULT_DONE:
                // A SearchResponse can contains multiple responses of 3 types:
                //     - 0 to n SearchResultEntry
                //     - O to n SearchResultReference
                //     - 1 (only) SearchResultDone
                // So we have to include those individual responses in a "General" SearchResponse
                SearchResponseDsml searchResponseDsml = null;

                // RequestID
                int requestID = response.getMessageId();

                while ( MessageTypeEnum.SEARCH_RESULT_DONE != response.getMessageType() )
                {
                    if ( MessageTypeEnum.SEARCH_RESULT_ENTRY == response.getMessageType() )
                    {
                        SearchResultEntryCodec sre = ( SearchResultEntryCodec ) response;
                        copyMessageIdAndControls( response, sre );

                        SearchResultEntryDsml searchResultEntryDsml = new SearchResultEntryDsml( sre );
                        searchResponseDsml = new SearchResponseDsml( ( LdapMessageCodec ) sre );

                        if ( requestID != 0 )
                        {
                            searchResponseDsml.setMessageId( requestID );
                        }

                        searchResponseDsml.addResponse( searchResultEntryDsml );
                    }
                    else if ( MessageTypeEnum.SEARCH_RESULT_REFERENCE == response.getMessageType() )
                    {
                        SearchResultReferenceCodec srr = ( SearchResultReferenceCodec ) response;
                        copyMessageIdAndControls( response, srr );

                        SearchResultReferenceDsml searchResultReferenceDsml = new SearchResultReferenceDsml( srr );
                        searchResponseDsml.addResponse( searchResultReferenceDsml );
                    }

                    response = readResponse( bb );
                }

                SearchResultDoneCodec srd = ( SearchResultDoneCodec ) response;
                copyMessageIdAndControls( response, srd );

                SearchResultDoneDsml searchResultDoneDsml = new SearchResultDoneDsml( srd );
                searchResponseDsml.addResponse( searchResultDoneDsml );
                break;
        }

        LdapResponseCodec realResponse = ( LdapResponseCodec ) response;
        ResultCodeEnum resultCode = realResponse.getLdapResult().getResultCode();

        if ( ( !continueOnError ) && ( resultCode != ResultCodeEnum.SUCCESS )
            && ( resultCode != ResultCodeEnum.COMPARE_TRUE ) && ( resultCode != ResultCodeEnum.COMPARE_FALSE )
            && ( resultCode != ResultCodeEnum.REFERRAL ) )
        {
            // Turning on Exit flag
            exit = true;
        }
    }


    private void copyMessageIdAndControls( LdapMessageCodec from, LdapMessageCodec to )
    {
        to.setMessageId( from.getMessageId() );

        for ( Control control : from.getControls() )
        {
            to.addControl( control );
        }
    }


    /**
     * Processes the BatchRequest
     * <ul>
     *     <li>Parsing and Getting BatchRequest</li>
     *     <li>Getting and registering options from BatchRequest</li>
     * </ul>
     *     
     * @throws XmlPullParserException
     *      if an error occurs in the parser
     */
    private void processBatchRequest() throws XmlPullParserException
    {
        // Parsing BatchRequest
        parser.parseBatchRequest();

        // Getting BatchRequest
        batchRequest = parser.getBatchRequest();

        if ( OnError.RESUME.equals( batchRequest.getOnError() ) )
        {
            continueOnError = true;
        }
        else if ( OnError.EXIT.equals( batchRequest.getOnError() ) )
        {
            continueOnError = false;
        }

        if ( batchRequest.getRequestID() != 0 )
        {
            batchResponse.setRequestID( batchRequest.getRequestID() );
        }
    }


    /**
     * Connect to the LDAP server through a socket and establish the Input and
     * Output Streams. All the required information for the connection should be
     * in the options from the command line, or the default values.
     * 
     * @throws UnknownHostException
     *      if the hostname or the Address of server could not be found
     * @throws IOException
     *      if there was a error opening or establishing the socket
     */
    private void connect() throws UnknownHostException, IOException
    {
        serverAddress = new InetSocketAddress( host, port );
        channel = SocketChannel.open( serverAddress );
        channel.configureBlocking( true );
    }


    /**
     * Sends a message
     * 
     * @param bb
     *      the message as a byte buffer
     * @throws IOException
     *      if the message could not be sent
     */
    private void sendMessage( ByteBuffer bb ) throws IOException
    {
        channel.write( bb );
        bb.clear();
    }


    /**
     * Reads the response to a request
     * 
     * @param bb
     *      the response as a byte buffer
     * @return the response
     *      the response as a LDAP message
     * @throws IOException
     * @throws DecoderException
     */
    private LdapMessageCodec readResponse( ByteBuffer bb ) throws IOException, DecoderException
    {

        LdapMessageCodec messageResp = null;

        if ( bb.hasRemaining() )
        {
            bb.position( bbposition );
            bb.limit( bbLimit );
            ldapDecoder.decode( bb, ldapMessageContainer );
            bbposition = bb.position();
            bbLimit = bb.limit();
        }
        bb.flip();
        while ( ldapMessageContainer.getState() != TLVStateEnum.PDU_DECODED )
        {

            int nbRead = channel.read( bb );

            if ( nbRead == -1 )
            {
                System.err.println( "fsdfsdfsdfsd" );
            }

            bb.flip();
            ldapDecoder.decode( bb, ldapMessageContainer );
            bbposition = bb.position();
            bbLimit = bb.limit();
            bb.flip();
        }

        messageResp = ( ( LdapMessageContainer ) ldapMessageContainer ).getLdapMessage();

        if ( messageResp instanceof BindResponseCodec )
        {
            BindResponseCodec resp = ( ( LdapMessageContainer ) ldapMessageContainer ).getBindResponse();

            if ( resp.getLdapResult().getResultCode() != ResultCodeEnum.SUCCESS )
            {
                System.err.println( "Error : " + resp.getLdapResult().getErrorMessage() );
            }
        }
        else if ( messageResp instanceof ExtendedResponseCodec )
        {
            ExtendedResponseCodec resp = ( ( LdapMessageContainer ) ldapMessageContainer ).getExtendedResponse();

            if ( resp.getLdapResult().getResultCode() != ResultCodeEnum.SUCCESS )
            {
                System.err.println( "Error : " + resp.getLdapResult().getErrorMessage() );
            }
        }

        ( ( LdapMessageContainer ) ldapMessageContainer ).clean();

        return messageResp;
    }


    /**
     * Binds to the ldap server
     * 
     * @param messageId 
     *      the message Id
     * @throws EncoderException
     * @throws DecoderException
     * @throws IOException
     * @throws LdapInvalidDnException
     */
    private void bind( int messageId ) throws EncoderException, DecoderException, IOException, LdapInvalidDnException
    {
        BindRequestCodec bindRequest = new BindRequestCodec();
        LdapAuthentication authentication = new SimpleAuthentication();
        ( ( SimpleAuthentication ) authentication ).setSimple( StringTools.getBytesUtf8( password ) );

        bindRequest.setAuthentication( authentication );
        bindRequest.setName( new DN( user ) );
        bindRequest.setVersion( 3 );

        bindRequest.setMessageId( messageId );

        // Encode and send the bind request
        ByteBuffer bb = bindRequest.encode();
        bb.flip();

        connect();
        sendMessage( bb );

        bb.clear();

        bb.position( bb.limit() );

        readResponse( bb );
    }
}
