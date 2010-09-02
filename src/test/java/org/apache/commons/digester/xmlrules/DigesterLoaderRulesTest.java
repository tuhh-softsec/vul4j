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
package org.apache.commons.digester.xmlrules;

import static org.junit.Assert.*;

import java.io.StringReader;

import org.apache.commons.digester.Digester;
import org.junit.Test;
import org.xml.sax.InputSource;

/**
 * Tests for digester loader rule implementations,
 *
 * @author Robert Burrell Donkin
 */

public class DigesterLoaderRulesTest {

    @Test
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
    
    /** Test for call method rule with target offset */
    @Test
    public void testCallMethodRuleTargetOffset() throws Exception {
        String xmlRules = 
            "<?xml version='1.0'?>" +
            "<digester-rules>" + 
            "  <pattern value='root'>" +
            "     <pattern value='call-top-object'>" +
            "        <call-method-rule " +
            "                methodname='setMiddle' " +
            "                targetoffset='0' " +
            "                paramcount='1' " +
            "                paramtypes='java.lang.String' />" +
            "        <call-param-rule paramnumber='0' />" +
            "     </pattern>" +
            "     <pattern value='call-parent-object'>" +
            "        <call-method-rule " +
            "                methodname='setLeft' " +
            "                targetoffset='1' " +
            "                paramcount='1' " +
            "                paramtypes='java.lang.String' />" +
            "        <call-param-rule paramnumber='0' />" +
            "     </pattern>" +
            "     <pattern value='call-root-object'>" +
            "        <call-method-rule " +
            "                methodname='setRight' " +
            "                targetoffset='-1' " +
            "                paramcount='1' " +
            "                paramtypes='java.lang.String' />" +
            "        <call-param-rule paramnumber='0' />" +
            "     </pattern>" +
            "   </pattern>" +
            "</digester-rules>";
        
        String xml = 
            "<?xml version='1.0'?>" + 
            "<root>" +
            "  <call-top-object>DataForTheTopObject</call-top-object>" +
            "  <call-parent-object>DataForTheParentObject</call-parent-object>" +
            "  <call-root-object>DataForTheRootObject</call-root-object>" +
            "</root>";
            
        CallParamTestObject testObjectA = new CallParamTestObject();
        CallParamTestObject testObjectB = new CallParamTestObject();
        CallParamTestObject testObjectC = new CallParamTestObject();
        Digester digester = DigesterLoader.createDigester(new InputSource(new StringReader(xmlRules)));
        digester.push( testObjectA );
        digester.push( testObjectB );
        digester.push( testObjectC );
        digester.parse(new InputSource(new StringReader(xml)));
        
        assertEquals("Top object invoked", "DataForTheTopObject", testObjectC.getMiddle());
        assertEquals("Parent object invoked", "DataForTheParentObject", testObjectB.getLeft());
        assertEquals("Root object invoked", "DataForTheRootObject", testObjectA.getRight());

    }
}
