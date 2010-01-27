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

import java.util.ArrayList;
import java.io.StringReader;
import org.xml.sax.InputSource;

import junit.framework.TestCase;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;

/**
 * Test for the include class functionality
 */
public class IncludeTest extends TestCase {

    public static class TestDigesterRuleSource implements DigesterRulesSource {
        public void getRules(Digester digester) {
            digester.addRule("bar", 
                new Rule() {
                    @Override
                    public void body(String namespace, String name, String text) {
                        ((ArrayList<String>) this.digester.peek()).add(text);
                    }
                });
        }
    }

    public IncludeTest(String testName) {
        super(testName);
    }
    
    public void testBasicInclude() throws Exception {
        String rulesXml = "<?xml version='1.0'?>"
                + "<digester-rules>"
                + " <pattern value='root/foo'>"
                + "   <include class='org.apache.commons.digester.xmlrules.IncludeTest$TestDigesterRuleSource'/>"
                + " </pattern>"
                + "</digester-rules>";
                
        String xml = "<?xml version='1.0' ?><root><foo><bar>short</bar></foo></root>";
        
        ArrayList<String> list = new ArrayList<String>();
        Digester digester = DigesterLoader.createDigester(new InputSource(new StringReader(rulesXml)));
        digester.push(list);
        digester.parse(new StringReader(xml));        
                                                                        
        assertEquals("Number of entries", 1, list.size());
        assertEquals("Entry value", "short", list.get(0));
    }
}
