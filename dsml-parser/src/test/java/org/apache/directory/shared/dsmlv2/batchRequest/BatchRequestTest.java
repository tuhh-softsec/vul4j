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

package org.apache.directory.shared.dsmlv2.batchRequest;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.apache.directory.junit.tools.Concurrent;
import org.apache.directory.junit.tools.ConcurrentJunitRunner;
import org.apache.directory.shared.dsmlv2.AbstractTest;
import org.apache.directory.shared.dsmlv2.DsmlDecorator;
import org.apache.directory.shared.dsmlv2.Dsmlv2Parser;
import org.apache.directory.shared.dsmlv2.request.BatchRequestDsml;
import org.apache.directory.shared.ldap.model.message.AbandonRequest;
import org.apache.directory.shared.ldap.model.message.AddRequest;
import org.apache.directory.shared.ldap.model.message.BindRequest;
import org.apache.directory.shared.ldap.model.message.CompareRequest;
import org.apache.directory.shared.ldap.model.message.DeleteRequest;
import org.apache.directory.shared.ldap.model.message.ExtendedRequest;
import org.apache.directory.shared.ldap.model.message.ModifyDnRequest;
import org.apache.directory.shared.ldap.model.message.ModifyRequest;
import org.apache.directory.shared.ldap.model.message.Request;
import org.apache.directory.shared.ldap.model.message.SearchRequest;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests for the Compare Response parsing
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrent()
public class BatchRequestTest extends AbstractTest
{
    /**
     * Test parsing of a Request with the (optional) requestID attribute
     */
    @Test
    public void testResponseWithRequestId()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser( getCodec() );

            parser.setInput( BatchRequestTest.class.getResource( "request_with_requestID_attribute.xml" ).openStream(),
                "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchRequestDsml batchRequest = parser.getBatchRequest();

        assertEquals( 1234567890, batchRequest.getRequestID() );
    }


    /**
     * Test parsing of a request with the (optional) requestID attribute equals to 0
     */
    @Test
    public void testRequestWithRequestIdEquals0()
    {
        testParsingFail( BatchRequestTest.class, "request_with_requestID_equals_0.xml" );
    }


    /**
     * Test parsing of a Request with the (optional) requestID attribute
     */
    @Test
    public void testResponseWith0Request()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser( getCodec() );

            parser.setInput( BatchRequestTest.class.getResource( "request_with_requestID_attribute.xml" ).openStream(),
                "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchRequestDsml batchRequest = parser.getBatchRequest();

        assertEquals( 0, batchRequest.getRequests().size() );
    }


    /**
     * Test parsing of a Request with 1 AuthRequest
     */
    @Test
    public void testResponseWith1AuthRequest()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser( getCodec() );

            parser.setInput( BatchRequestTest.class.getResource( "request_with_1_AuthRequest.xml" ).openStream(),
                "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchRequestDsml batchRequest = parser.getBatchRequest();

        assertEquals( 1, batchRequest.getRequests().size() );

        if ( batchRequest.getCurrentRequest() instanceof BindRequest)
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a Request with 1 AddRequest
     */
    @Test
    public void testResponseWith1AddRequest()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser( getCodec() );

            parser.setInput( BatchRequestTest.class.getResource( "request_with_1_AddRequest.xml" ).openStream(),
                "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchRequestDsml batchRequest = parser.getBatchRequest();

        assertEquals( 1, batchRequest.getRequests().size() );

        if ( batchRequest.getCurrentRequest() instanceof AddRequest )
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a Request with 1 CompareRequest
     */
    @Test
    public void testResponseWith1CompareRequest()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser( getCodec() );

            parser.setInput( BatchRequestTest.class.getResource( "request_with_1_CompareRequest.xml" ).openStream(),
                "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchRequestDsml batchRequest = parser.getBatchRequest();

        assertEquals( 1, batchRequest.getRequests().size() );

        if ( batchRequest.getCurrentRequest() instanceof CompareRequest )
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a Request with 1 AbandonRequest
     */
    @Test
    public void testResponseWith1AbandonRequest()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser( getCodec() );

            parser.setInput( BatchRequestTest.class.getResource( "request_with_1_AbandonRequest.xml" ).openStream(),
                "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchRequestDsml batchRequest = parser.getBatchRequest();

        assertEquals( 1, batchRequest.getRequests().size() );

        if ( batchRequest.getCurrentRequest() instanceof AbandonRequest )
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a Request with 1 DelRequest
     */
    @Test
    public void testResponseWith1DelRequest()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser( getCodec() );

            parser.setInput( BatchRequestTest.class.getResource( "request_with_1_DelRequest.xml" ).openStream(),
                "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchRequestDsml batchRequest = parser.getBatchRequest();

        assertEquals( 1, batchRequest.getRequests().size() );

        if ( batchRequest.getCurrentRequest() instanceof DeleteRequest )
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a Request with 1 ExtendedRequest
     */
    @Test
    public void testResponseWith1ExtendedRequest()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser( getCodec() );

            parser.setInput( BatchRequestTest.class.getResource( "request_with_1_ExtendedRequest.xml" ).openStream(),
                "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchRequestDsml batchRequest = parser.getBatchRequest();

        assertEquals( 1, batchRequest.getRequests().size() );

        if ( batchRequest.getCurrentRequest() instanceof ExtendedRequest)
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a Request with 1 ModDNRequest
     */
    @Test
    public void testResponseWith1ModDNRequest()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser( getCodec() );

            parser.setInput( BatchRequestTest.class.getResource( "request_with_1_ModDNRequest.xml" ).openStream(),
                "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchRequestDsml batchRequest = parser.getBatchRequest();

        assertEquals( 1, batchRequest.getRequests().size() );

        if ( batchRequest.getCurrentRequest() instanceof ModifyDnRequest )
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a Request with 1 ModifyRequest
     */
    @Test
    public void testResponseWith1ModifyRequest()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser( getCodec() );

            parser.setInput( BatchRequestTest.class.getResource( "request_with_1_ModifyRequest.xml" ).openStream(),
                "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchRequestDsml batchRequest = parser.getBatchRequest();

        assertEquals( 1, batchRequest.getRequests().size() );

        if ( batchRequest.getCurrentRequest() instanceof ModifyRequest)
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a Request with 1 SearchRequest
     */
    @Test
    public void testResponseWith1SearchRequest()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser( getCodec() );

            parser.setInput( BatchRequestTest.class.getResource( "request_with_1_SearchRequest.xml" ).openStream(),
                "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchRequestDsml batchRequest = parser.getBatchRequest();

        assertEquals( 1, batchRequest.getRequests().size() );

        if ( batchRequest.getCurrentRequest() instanceof SearchRequest )
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a Request with 2 AddRequest
     */
    @Test
    public void testResponseWith2AddRequest()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser( getCodec() );

            parser.setInput( BatchRequestTest.class.getResource( "request_with_2_AddRequest.xml" ).openStream(),
                "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchRequestDsml batchRequest = parser.getBatchRequest();

        assertEquals( 2, batchRequest.getRequests().size() );

        if ( batchRequest.getCurrentRequest() instanceof AddRequest )
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a Request with 2 CompareRequest
     */
    @Test
    public void testResponseWith2CompareRequest()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser( getCodec() );

            parser.setInput( BatchRequestTest.class.getResource( "request_with_2_CompareRequest.xml" ).openStream(),
                "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchRequestDsml batchRequest = parser.getBatchRequest();

        assertEquals( 2, batchRequest.getRequests().size() );

        if ( batchRequest.getCurrentRequest() instanceof CompareRequest )
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a Request with 2 AbandonRequest
     */
    @Test
    public void testResponseWith2AbandonRequest()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser( getCodec() );

            parser.setInput( BatchRequestTest.class.getResource( "request_with_2_AbandonRequest.xml" ).openStream(),
                "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchRequestDsml batchRequest = parser.getBatchRequest();

        assertEquals( 2, batchRequest.getRequests().size() );

        if ( batchRequest.getCurrentRequest() instanceof AbandonRequest )
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a Request with 2 DelRequest
     */
    @Test
    public void testResponseWith2DelRequest()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser( getCodec() );

            parser.setInput( BatchRequestTest.class.getResource( "request_with_2_DelRequest.xml" ).openStream(),
                "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchRequestDsml batchRequest = parser.getBatchRequest();

        assertEquals( 2, batchRequest.getRequests().size() );

        if ( batchRequest.getCurrentRequest() instanceof DeleteRequest )
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a Request with 2 ExtendedRequest
     */
    @Test
    public void testResponseWith2ExtendedRequest()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser( getCodec() );

            parser.setInput( BatchRequestTest.class.getResource( "request_with_2_ExtendedRequest.xml" ).openStream(),
                "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchRequestDsml batchRequest = parser.getBatchRequest();

        assertEquals( 2, batchRequest.getRequests().size() );

        if ( batchRequest.getCurrentRequest() instanceof ExtendedRequest )
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a Request with 2 ModDNRequest
     */
    @Test
    public void testResponseWith2ModDNRequest()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser( getCodec() );

            parser.setInput( BatchRequestTest.class.getResource( "request_with_2_ModDNRequest.xml" ).openStream(),
                "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchRequestDsml batchRequest = parser.getBatchRequest();

        assertEquals( 2, batchRequest.getRequests().size() );

        if ( batchRequest.getCurrentRequest() instanceof ModifyDnRequest )
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a Request with 2 ModifyRequest
     */
    @Test
    public void testResponseWith2ModifyRequest()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser( getCodec() );

            parser.setInput( BatchRequestTest.class.getResource( "request_with_2_ModifyRequest.xml" ).openStream(),
                "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchRequestDsml batchRequest = parser.getBatchRequest();

        assertEquals( 2, batchRequest.getRequests().size() );

        if ( batchRequest.getCurrentRequest() instanceof ModifyRequest )
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a Request with 2 SearchRequest
     */
    @Test
    public void testResponseWith2SearchRequest()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser( getCodec() );

            parser.setInput( BatchRequestTest.class.getResource( "request_with_2_SearchRequest.xml" ).openStream(),
                "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchRequestDsml batchRequest = parser.getBatchRequest();

        assertEquals( 2, batchRequest.getRequests().size() );

        if ( batchRequest.getCurrentRequest() instanceof SearchRequest )
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a Request with 1 AuthRequest and 1 AddRequest
     */
    @Test
    public void testResponseWith1AuthRequestAnd1AddRequest()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser( getCodec() );

            parser.setInput( BatchRequestTest.class.getResource( "request_with_1_AuthRequest_1_AddRequest.xml" )
                .openStream(), "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        BatchRequestDsml batchRequest = parser.getBatchRequest();

        List<DsmlDecorator<? extends Request>> requests = 
            batchRequest.getRequests();

        assertEquals( 2, requests.size() );

        if ( requests.get( 0 ) instanceof BindRequest )
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }

        if ( requests.get( 1 ) instanceof AddRequest )
        {
            assertTrue( true );
        }
        else
        {
            fail();
        }
    }


    /**
     * Test parsing of a request with 1 wrong placed AuthRequest
     */
    @Test
    public void testRequestWithWrongPlacedAuthRequest()
    {
        testParsingFail( BatchRequestTest.class, "request_with_wrong_placed_AuthRequest.xml" );
    }
}
