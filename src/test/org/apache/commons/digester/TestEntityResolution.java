/*
 * Copyright 2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
package org.apache.commons.digester;



import java.io.File;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.helpers.DefaultHandler;

import junit.framework.TestCase;

/**
 * Tests for entity resolution.
 * @author <a href='http://jakarta.apache.org/'>Jakarta Commons Team</a>
 * @version $Revision: 1.1 $
 */
public class TestEntityResolution extends TestCase {
    
    public void testParserResolveRelative() throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        factory.setNamespaceAware(true);
        SAXParser parser = factory.newSAXParser();
        
        parser.parse(
                    new File("src/test/org/apache/commons/digester/document-with-relative-dtd.xml"), 
                    new DefaultHandler());
    }
    
    public void testDigesterResolveRelative() throws Exception {
        Digester digester = new Digester();
        digester.setValidating(true);
        digester.parse(
                    new File("src/test/org/apache/commons/digester/document-with-relative-dtd.xml"));
    }
}
