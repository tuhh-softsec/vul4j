/*
    Licensed to the Apache Software Foundation (ASF) under one
    or more contributor license agreements.  See the NOTICE file
    distributed with this work for additional information
    regarding copyright ownership.  The ASF licenses this file
    to you under the Apache License, Version 2.0 (the
    "License"); you may not use this file except in compliance
    with the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.
 */
package org.apache.wiki;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.sourceforge.stripes.mock.MockHttpServletRequest;
import net.sourceforge.stripes.mock.MockHttpServletResponse;
import net.sourceforge.stripes.mock.MockServletConfig;
import net.sourceforge.stripes.mock.MockServletContext;


public class WikiServletTest {
    
    @Test
    public void testDoGet() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest( "/JSPWiki", "/wiki/Wiki.jsp" );
        MockHttpServletResponse res = new MockHttpServletResponse();
        WikiServlet wikiServlet = new WikiServlet();
        MockServletConfig config = new MockServletConfig();
        config.setServletContext( new MockServletContext( "/JSPWiki" ) );
        
        wikiServlet.init( config );
        wikiServlet.doGet( req, res );
        wikiServlet.destroy();
        
        Assertions.assertEquals( "/Wiki.jsp?page=Main&", req.getForwardUrl() );
    }
    
    @Test
    public void testNastyDoPost() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest( "/JSPWiki", "/wiki/Edit.jsp" );
        MockHttpServletResponse res = new MockHttpServletResponse();
        WikiServlet wikiServlet = new WikiServlet();
        MockServletConfig config = new MockServletConfig();
        config.setServletContext( new MockServletContext( "/JSPWiki" ) );
        
        wikiServlet.init( config );
        wikiServlet.doPost( req, res );
        wikiServlet.destroy();
        
        Assertions.assertEquals( "/Wiki.jsp?page=Main&", req.getForwardUrl() );
    }

}
