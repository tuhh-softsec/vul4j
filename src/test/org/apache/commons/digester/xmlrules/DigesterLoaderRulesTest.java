/*
 * Copyright 2001-2004 The Apache Software Foundation.
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
package org.apache.commons.digester.xmlrules;

import java.io.StringReader;

import junit.framework.TestCase;

import org.xml.sax.InputSource;

import org.apache.commons.digester.Digester;

/**
 * Tests for digester loader rule implementations,
 *
 * @author Robert Burrell Donkin
 */

public class DigesterLoaderRulesTest extends TestCase {

    public DigesterLoaderRulesTest(java.lang.String testName) {
        super(testName);
    }
    
    /** Basic test for object param rule */
    public void testObjectParamRule() throws Exception {
        String xmlRules = 
            "<?xml version='1.0'?>" +
            "<digester-rules>" + 
            "  <pattern value='root'>" +
            "     <pattern value='foo'>" +
            "        <call-method-rule " +
            "                methodname='duo' " +
            "                paramcount='2' " +
            "                paramtypes='java.lang.String,java.lang.String'/>" +
            "        <pattern value='bar'>" +
            "           <object-param-rule paramnumber='0' type='java.lang.String' />" +
            "        </pattern>" +
            "        <pattern value='rab'>" +
            "           <object-param-rule paramnumber='1' type='java.lang.String' value='tester.test' />" +
            "        </pattern>" +
            "     </pattern>" +
            "   </pattern>" +
            "</digester-rules>";
        
        String xml = 
            "<?xml version='1.0'?>" + 
            "<root>" +
            "  <foo>" +
            "    <bar/>" +
            "    <rab/>" +
            "  </foo>" +
            "</root>";
            
        CallParamTestObject testObject = new CallParamTestObject();
        Digester digester = DigesterLoader.createDigester(new InputSource(new StringReader(xmlRules)));
        digester.push(testObject);    
        digester.parse(new InputSource(new StringReader(xml)));
        assertEquals("First param", "", testObject.getLeft());
        assertEquals("Param with default set", "tester.test", testObject.getRight());
        
    }
}
