/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.xml.security.test.stax;

import org.junit.Assert;
import org.junit.Test;

import org.apache.xml.security.stax.config.Init;
import org.apache.xml.security.stax.ext.XMLSecurityException;

import java.net.URL;

/**
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class UncategorizedTest extends org.junit.Assert {

    @Test
    public void testConfigurationLoadFromUrl() throws Exception {
        URL url = 
            this.getClass().getClassLoader().getResource(
                "org/apache/xml/security/c14n/in/31_input.xml");
        try {
            Init.init(url.toURI());
            Assert.fail();
        } catch (XMLSecurityException e) {
            Assert.assertEquals(e.getMessage(), "General security error; nested exception is: \n" +
                    "\torg.xml.sax.SAXParseException: cvc-elt.1: Cannot find the declaration of element 'doc'.");
        }
    }
}
