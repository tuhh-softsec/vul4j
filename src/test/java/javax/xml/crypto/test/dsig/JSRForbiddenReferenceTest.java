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
package javax.xml.crypto.test.dsig;

import java.io.File;
import java.security.Security;

import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.test.KeySelectors;
import javax.xml.crypto.test.dsig.SignatureValidator;

/**
 * This is a test for a forbidden Reference algorithm when secure validation is enabled.
 */
public class JSRForbiddenReferenceTest extends org.junit.Assert {

    private SignatureValidator validator;
    private File dir;

    static {
        Security.insertProviderAt(new org.apache.jcp.xml.dsig.internal.dom.XMLDSigRI(), 1);
    }

    public JSRForbiddenReferenceTest() {
        String fs = System.getProperty("file.separator");
        String base = System.getProperty("basedir") == null ? "./": System.getProperty("basedir");
        
        dir = new File(base + fs + "src/test/resources" + fs 
            + "javax" + fs + "xml" + fs + "crypto", "dsig");
        validator = new SignatureValidator(dir);
    }


    @org.junit.Test
    public void testLocalFilesystem() throws Exception {
        String file = "signature-external-c14n-xmlatrs.xml";

        DOMValidateContext vc = 
            validator.getValidateContext(
                file, new KeySelectors.SecretKeySelector("secret".getBytes("ASCII"))
            );
        
        vc.setProperty("org.apache.jcp.xml.dsig.secureValidation", Boolean.FALSE);
        boolean coreValidity = validator.validate(vc);
        assertTrue("Signature failed core validation", coreValidity);
        
        vc.setProperty("org.apache.jcp.xml.dsig.secureValidation", Boolean.TRUE);
        
        try {
            validator.validate(vc);
            fail("Failure expected when secure validation is enabled");
        } catch (XMLSignatureException ex) {
            assertTrue(ex.getMessage().contains("URIReferenceException"));
        }
    }

}
