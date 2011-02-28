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

package org.apache.directory.shared.dsmlv2.batchResponse;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.directory.shared.dsmlv2.AbstractResponseTest;
import org.apache.directory.shared.dsmlv2.DsmlDecorator;
import org.apache.directory.shared.dsmlv2.Dsmlv2ResponseParser;
import org.apache.directory.shared.dsmlv2.reponse.BatchResponseDsml;
import org.apache.directory.shared.dsmlv2.reponse.ErrorResponse;
import org.apache.directory.shared.dsmlv2.reponse.SearchResponse;
import org.apache.directory.shared.ldap.model.message.AddResponse;
import org.apache.directory.shared.ldap.model.message.BindResponse;
import org.apache.directory.shared.ldap.model.message.CompareResponse;
import org.apache.directory.shared.ldap.model.message.DeleteResponse;
import org.apache.directory.shared.ldap.model.message.ExtendedResponse;
import org.apache.directory.shared.ldap.model.message.ModifyDnResponse;
import org.apache.directory.shared.ldap.model.message.ModifyResponse;
import org.apache.directory.shared.ldap.model.message.Response;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.mycila.junit.concurrent.Concurrency;
import com.mycila.junit.concurrent.ConcurrentJunitRunner;


/**
 * Tests for the Compare Response parsing
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrency()
public class BatchResponseTest extends AbstractResponseTest
{
    /**
     * Test parsing of a Response with the (optional) requestID attribute
     */
    @Test
    public void testResponseWithRequestId()
    {
        Dsmlv2ResponseParser parser = null;
        try
        {
            parser = new Dsmlv2ResponseParser( getCodec() );

            parser.setInput( BatchResponseTest.class.getResource( "response_with_requestID_attribute.xml" )
                .openStream(), "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchResponseDsml batchResponse = parser.getBatchResponse();

        assertEquals( 1234567890, batchResponse.getRequestID() );
    }


    /**
     * Test parsing of a Response with the (optional) requestID attribute equals 0
     */
    @Test
    public void testResponseWithRequestIdEquals0()
    {
        testParsingFail( BatchResponseTest.class, "response_with_requestID_equals_0.xml" );
    }


    /**
     * Test parsing of a Response with 0 Response
     */
    @Test
    public void testResponseWith0Reponse()
    {
        Dsmlv2ResponseParser parser = null;
        try
        {
            parser = new Dsmlv2ResponseParser( getCodec() );

            parser.setInput( BatchResponseTest.class.getResource( "response_with_0_response.xml" ).openStream(),
                "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchResponseDsml batchResponse = parser.getBatchResponse();

        assertEquals( 0, batchResponse.getResponses().size() );
    }


    /**
     * Test parsing of a Response with the 1 AddResponse
     */
    @Test
    public void testResponseWith1AddResponse()
    {
        Dsmlv2ResponseParser parser = null;
        try
        {
            parser = new Dsmlv2ResponseParser( getCodec() );

            parser.setInput( BatchResponseTest.class.getResource( "response_with_1_AddResponse.xml" ).openStream(),
                "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchResponseDsml batchResponse = parser.getBatchResponse();

        assertEquals( 1, batchResponse.getResponses().size() );

        DsmlDecorator<? extends Response> response = batchResponse.getCurrentResponse();

        if ( response instanceof AddResponse)
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a Response with the 1 AuthResponse
     */
    @Test
    public void testResponseWith1AuthResponse()
    {
        Dsmlv2ResponseParser parser = null;
        try
        {
            parser = new Dsmlv2ResponseParser( getCodec() );

            parser.setInput( BatchResponseTest.class.getResource( "response_with_1_AuthResponse.xml" ).openStream(),
                "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchResponseDsml batchResponse = parser.getBatchResponse();

        assertEquals( 1, batchResponse.getResponses().size() );

        DsmlDecorator<? extends Response> response = batchResponse.getCurrentResponse();

        if ( response instanceof BindResponse )
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a Response with the 1 CompareResponse
     */
    @Test
    public void testResponseWith1CompareResponse()
    {
        Dsmlv2ResponseParser parser = null;
        try
        {
            parser = new Dsmlv2ResponseParser( getCodec() );

            parser.setInput( BatchResponseTest.class.getResource( "response_with_1_CompareResponse.xml" ).openStream(),
                "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchResponseDsml batchResponse = parser.getBatchResponse();

        assertEquals( 1, batchResponse.getResponses().size() );

        DsmlDecorator<? extends Response> response = batchResponse.getCurrentResponse();

        if ( response instanceof CompareResponse )
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a Response with the 1 DelResponse
     */
    @Test
    public void testResponseWith1DelResponse()
    {
        Dsmlv2ResponseParser parser = null;
        try
        {
            parser = new Dsmlv2ResponseParser( getCodec() );

            parser.setInput( BatchResponseTest.class.getResource( "response_with_1_DelResponse.xml" ).openStream(),
                "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchResponseDsml batchResponse = parser.getBatchResponse();

        assertEquals( 1, batchResponse.getResponses().size() );

        DsmlDecorator<? extends Response> response = batchResponse.getCurrentResponse();

        if ( response instanceof DeleteResponse )
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a Response with the 1 ErrorResponse
     */
    @Test
    public void testResponseWith1ErrorResponse()
    {
        Dsmlv2ResponseParser parser = null;
        try
        {
            parser = new Dsmlv2ResponseParser( getCodec() );

            parser.setInput( BatchResponseTest.class.getResource( "response_with_1_ErrorResponse.xml" ).openStream(),
                "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchResponseDsml batchResponse = parser.getBatchResponse();

        assertEquals( 1, batchResponse.getResponses().size() );

        DsmlDecorator<? extends Response> response = batchResponse.getCurrentResponse();

        if ( response instanceof ErrorResponse )
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a Response with the 1 ExtendedResponse
     */
    @Test
    public void testResponseWith1ExtendedResponse()
    {
        Dsmlv2ResponseParser parser = null;
        try
        {
            parser = new Dsmlv2ResponseParser( getCodec() );

            parser.setInput(
                BatchResponseTest.class.getResource( "response_with_1_ExtendedResponse.xml" ).openStream(), "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchResponseDsml batchResponse = parser.getBatchResponse();

        assertEquals( 1, batchResponse.getResponses().size() );

        DsmlDecorator<? extends Response> response = batchResponse.getCurrentResponse();

        if ( response instanceof ExtendedResponse )
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a Response with the 1 ModDNResponse
     */
    @Test
    public void testResponseWith1ModDNResponse()
    {
        Dsmlv2ResponseParser parser = null;
        try
        {
            parser = new Dsmlv2ResponseParser( getCodec() );

            parser.setInput( BatchResponseTest.class.getResource( "response_with_1_ModDNResponse.xml" ).openStream(),
                "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchResponseDsml batchResponse = parser.getBatchResponse();

        assertEquals( 1, batchResponse.getResponses().size() );

        DsmlDecorator<? extends Response> response = batchResponse.getCurrentResponse();

        if ( response instanceof ModifyDnResponse )
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a Response with the 1 ModifyResponse
     */
    @Test
    public void testResponseWith1ModifyResponse()
    {
        Dsmlv2ResponseParser parser = null;
        try
        {
            parser = new Dsmlv2ResponseParser( getCodec() );

            parser.setInput( BatchResponseTest.class.getResource( "response_with_1_ModifyResponse.xml" ).openStream(),
                "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchResponseDsml batchResponse = parser.getBatchResponse();

        assertEquals( 1, batchResponse.getResponses().size() );

        DsmlDecorator<? extends Response> response = batchResponse.getCurrentResponse();

        if ( response instanceof ModifyResponse )
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a Response with the 1 SearchResponse
     */
    @Test
    public void testResponseWith1SearchResponse()
    {
        Dsmlv2ResponseParser parser = null;
        try
        {
            parser = new Dsmlv2ResponseParser( getCodec() );

            parser.setInput( BatchResponseTest.class.getResource( "response_with_1_SearchResponse.xml" ).openStream(),
                "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchResponseDsml batchResponse = parser.getBatchResponse();

        assertEquals( 1, batchResponse.getResponses().size() );

        DsmlDecorator<? extends Response> response = batchResponse.getCurrentResponse();

        if ( response.getDecorated() instanceof SearchResponse )
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a Response with the 2 AddResponse
     */
    @Test
    public void testResponseWith2AddResponse()
    {
        Dsmlv2ResponseParser parser = null;
        try
        {
            parser = new Dsmlv2ResponseParser( getCodec() );

            parser.setInput( BatchResponseTest.class.getResource( "response_with_2_AddResponse.xml" ).openStream(),
                "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchResponseDsml batchResponse = parser.getBatchResponse();

        assertEquals( 2, batchResponse.getResponses().size() );

        DsmlDecorator<? extends Response> response = batchResponse.getCurrentResponse();

        if ( response instanceof AddResponse )
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a Response with the 2 AuthResponse
     */
    @Test
    public void testResponseWith2AuthResponse()
    {
        Dsmlv2ResponseParser parser = null;
        try
        {
            parser = new Dsmlv2ResponseParser( getCodec() );

            parser.setInput( BatchResponseTest.class.getResource( "response_with_2_AuthResponse.xml" ).openStream(),
                "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchResponseDsml batchResponse = parser.getBatchResponse();

        assertEquals( 2, batchResponse.getResponses().size() );

        DsmlDecorator<? extends Response> response = batchResponse.getCurrentResponse();

        if ( response instanceof BindResponse )
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a Response with the 2 CompareResponse
     */
    @Test
    public void testResponseWith2CompareResponse()
    {
        Dsmlv2ResponseParser parser = null;
        try
        {
            parser = new Dsmlv2ResponseParser( getCodec() );

            parser.setInput( BatchResponseTest.class.getResource( "response_with_2_CompareResponse.xml" ).openStream(),
                "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchResponseDsml batchResponse = parser.getBatchResponse();

        assertEquals( 2, batchResponse.getResponses().size() );

        DsmlDecorator<? extends Response> response = batchResponse.getCurrentResponse();

        if ( response instanceof CompareResponse )
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a Response with the 2 DelResponse
     */
    @Test
    public void testResponseWith2DelResponse()
    {
        Dsmlv2ResponseParser parser = null;
        try
        {
            parser = new Dsmlv2ResponseParser( getCodec() );

            parser.setInput( BatchResponseTest.class.getResource( "response_with_2_DelResponse.xml" ).openStream(),
                "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchResponseDsml batchResponse = parser.getBatchResponse();

        assertEquals( 2, batchResponse.getResponses().size() );

        DsmlDecorator<? extends Response> response = batchResponse.getCurrentResponse();

        if ( response instanceof DeleteResponse )
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a Response with the 2 ErrorResponse
     */
    @Test
    public void testResponseWith2ErrorResponse()
    {
        Dsmlv2ResponseParser parser = null;
        try
        {
            parser = new Dsmlv2ResponseParser( getCodec() );

            parser.setInput( BatchResponseTest.class.getResource( "response_with_2_ErrorResponse.xml" ).openStream(),
                "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchResponseDsml batchResponse = parser.getBatchResponse();

        assertEquals( 2, batchResponse.getResponses().size() );

        DsmlDecorator<? extends Response> response = batchResponse.getCurrentResponse();

        if ( response instanceof ErrorResponse )
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a Response with the 2 ExtendedResponse
     */
    @Test
    public void testResponseWith2ExtendedResponse()
    {
        Dsmlv2ResponseParser parser = null;
        try
        {
            parser = new Dsmlv2ResponseParser( getCodec() );

            parser.setInput(
                BatchResponseTest.class.getResource( "response_with_2_ExtendedResponse.xml" ).openStream(), "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchResponseDsml batchResponse = parser.getBatchResponse();

        assertEquals( 2, batchResponse.getResponses().size() );

        DsmlDecorator<? extends Response> response = batchResponse.getCurrentResponse();

        if ( response instanceof ExtendedResponse)
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a Response with the 2 ModDNResponse
     */
    @Test
    public void testResponseWith2ModDNResponse()
    {
        Dsmlv2ResponseParser parser = null;
        try
        {
            parser = new Dsmlv2ResponseParser( getCodec() );

            parser.setInput( BatchResponseTest.class.getResource( "response_with_2_ModDNResponse.xml" ).openStream(),
                "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchResponseDsml batchResponse = parser.getBatchResponse();

        assertEquals( 2, batchResponse.getResponses().size() );

        DsmlDecorator<? extends Response> response = batchResponse.getCurrentResponse();

        if ( response instanceof ModifyDnResponse )
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a Response with the 2 ModifyResponse
     */
    @Test
    public void testResponseWith2ModifyResponse()
    {
        Dsmlv2ResponseParser parser = null;
        try
        {
            parser = new Dsmlv2ResponseParser( getCodec() );

            parser.setInput( BatchResponseTest.class.getResource( "response_with_2_ModifyResponse.xml" ).openStream(),
                "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchResponseDsml batchResponse = parser.getBatchResponse();

        assertEquals( 2, batchResponse.getResponses().size() );

        DsmlDecorator<? extends Response> response = batchResponse.getCurrentResponse();

        if ( response instanceof ModifyResponse )
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a Response with the 2 SearchResponse
     */
    @Test
    public void testResponseWith2SearchResponse()
    {
        Dsmlv2ResponseParser parser = null;
        try
        {
            parser = new Dsmlv2ResponseParser( getCodec() );

            parser.setInput( BatchResponseTest.class.getResource( "response_with_2_SearchResponse.xml" ).openStream(),
                "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchResponseDsml batchResponse = parser.getBatchResponse();

        assertEquals( 2, batchResponse.getResponses().size() );

        DsmlDecorator<? extends Response> response = batchResponse.getCurrentResponse();

        if ( response.getDecorated() instanceof SearchResponse )
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }
}
