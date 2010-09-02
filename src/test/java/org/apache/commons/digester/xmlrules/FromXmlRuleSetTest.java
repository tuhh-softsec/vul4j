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
 * Tests loading Digester rules from an XML file.
 */

public class FromXmlRuleSetTest {

    /** 
     * Test the FromXmlRules.addRuleInstances(digester, path) method, ie
     * test loading rules at a base position other than the root.
     */
    @Test
    public void testBasePath() throws Exception {
        String xmlRules = 
            "<?xml version='1.0'?>" +
            "<digester-rules>" + 
            "   <pattern value='foo'>" +
            "      <call-method-rule " +
            "              methodname='setProperty' " +
            "              paramcount='0' />" +
            "   </pattern>" +
            "</digester-rules>";
        
        String xml = 
            "<?xml version='1.0'?>" + 
            "<root>" +
            "  <foo>success</foo>" +
            "</root>";
            
        // First try with no base path. The rule shouldn't match, because
        // foo is not the root element.
        {
        ObjectTestImpl testObject = new ObjectTestImpl();
        FromXmlRuleSet ruleset = 
            new FromXmlRuleSet(
                new InputSource(new StringReader(xmlRules)));
        Digester digester = new Digester();
        ruleset.addRuleInstances(digester);
        
        digester.push(testObject);    
        digester.parse(new InputSource(new StringReader(xml)));
        
        assertEquals("", testObject.getProperty());
        }
        
        // Now try with a base path. The rule should now match.
        {
        ObjectTestImpl testObject = new ObjectTestImpl();
        FromXmlRuleSet ruleset = 
            new FromXmlRuleSet(
                new InputSource(new StringReader(xmlRules)));
        Digester digester = new Digester();
        ruleset.addRuleInstances(digester, "root");

        digester.push(testObject);    
        digester.parse(new InputSource(new StringReader(xml)));
        
        assertEquals("success", testObject.getProperty());
        }
    }
}
