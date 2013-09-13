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

import org.apache.xml.security.signature.Manifest;
import org.apache.xml.security.signature.MissingResourceFailureException;
import org.apache.xml.security.test.dom.interop.InteropTestBase;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Element;


/**
 * This is a test for a forbidden Reference algorithm when secure validation is enabled.
 */
public class ForbiddenReferenceTest extends InteropTestBase {

    static org.slf4j.Logger log =
        org.slf4j.LoggerFactory.getLogger(ForbiddenReferenceTest.class);

    static {
        org.apache.xml.security.Init.init();
    }
    
    public ForbiddenReferenceTest() {
        super();
    }

    @org.junit.Test
    public void testLocalFilesystem() throws Exception {
        boolean success = 
            readAndVerifyManifest("src/test/resources/interop/c14n/Y3", "signature.xml", false);

        assertTrue(success);
        
        try {
            readAndVerifyManifest("src/test/resources/interop/c14n/Y3", "signature.xml", true);
            fail("Failure expected when secure validation is enabled");
        } catch (MissingResourceFailureException ex) {
            assertTrue(ex.getMessage().contains("The Reference for URI"));
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
