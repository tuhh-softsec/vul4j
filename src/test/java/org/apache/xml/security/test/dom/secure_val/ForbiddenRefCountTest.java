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
package org.apache.xml.security.test.dom.secure_val;

import java.io.File;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.Manifest;
import org.apache.xml.security.test.dom.interop.InteropTestBase;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Element;


/**
 * This is a test for a forbidden number of references when secure validation is enabled.
 */
public class ForbiddenRefCountTest extends InteropTestBase {

    static org.slf4j.Logger log =
        org.slf4j.LoggerFactory.getLogger(ForbiddenRefCountTest.class);

    static {
        org.apache.xml.security.Init.init();
    }
    
    public ForbiddenRefCountTest() {
        super();
    }

    @org.junit.Test
    public void testReferenceCount() throws Exception {
        boolean success = 
            readAndVerifyManifest("src/test/resources/interop/c14n/Y4", "signature-manifest.xml", false);

        assertTrue(success);
        
        try {
            readAndVerifyManifest("src/test/resources/interop/c14n/Y4", "signature-manifest.xml", true);
            fail("Failure expected when secure validation is enabled");
        } catch (XMLSecurityException ex) {
            assertTrue(ex.getMessage().contains("references are contained in the Manifest"));
        }
    }

    private boolean readAndVerifyManifest(
        String directory, String file, boolean secValidation
    ) throws Exception {
        String basedir = System.getProperty("basedir");
        if (basedir != null && !"".equals(basedir)) {
            directory = basedir + "/" + directory;
        }

        File f = new File(directory + "/" + file);

        javax.xml.parsers.DocumentBuilder db = XMLUtils.createDocumentBuilder(false);
        org.w3c.dom.Document doc = db.parse(f);

        Element manifestElement =
            (Element) doc.getElementsByTagNameNS(Constants.SignatureSpecNS,
                                                 Constants._TAG_SIGNEDINFO).item(0);
        Manifest manifest = new Manifest(manifestElement, f.toURI().toURL().toString(), secValidation);
        return manifest.verifyReferences();
    }
    
}
