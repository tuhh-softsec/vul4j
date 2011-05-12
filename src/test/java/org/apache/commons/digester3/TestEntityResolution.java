/* $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.digester3;

import java.io.File;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.digester3.Digester;
import org.junit.Test;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Tests for entity resolution.
 * 
 * @author <a href='http://commons.apache.org/'>Apache Commons Team</a>
 * @version $Revision$
 */
public class TestEntityResolution
{

    @Test
    public void testParserResolveRelative()
        throws Exception
    {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating( true );
        factory.setNamespaceAware( true );
        SAXParser parser = factory.newSAXParser();

        parser.parse( new File( "src/test/resources/org/apache/commons/digester3/document-with-relative-dtd.xml" ),
                      new DefaultHandler() );
    }

    @Test
    public void testDigesterResolveRelative()
        throws Exception
    {
        Digester digester = new Digester();
        digester.setValidating( true );
        digester.parse( new File( "src/test/resources/org/apache/commons/digester3/document-with-relative-dtd.xml" ) );
    }
}
