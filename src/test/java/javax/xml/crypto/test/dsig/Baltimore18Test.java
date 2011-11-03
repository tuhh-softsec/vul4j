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
/*
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
 */
package javax.xml.crypto.test.dsig;

import java.io.File;
import java.security.Security;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.URIDereferencer;

import javax.xml.crypto.test.KeySelectors;

/**
 * This is a testcase to validate all "merlin-xmldsig-eighteen" 
 * testcases from Baltimore
 *
 * @author Sean Mullan
 */
public class Baltimore18Test extends org.junit.Assert {

    private SignatureValidator validator;
    private File dir;
    private KeySelector cks;
    private URIDereferencer ud;

    static {
        Security.insertProviderAt
            (new org.apache.jcp.xml.dsig.internal.dom.XMLDSigRI(), 1);
    }

    public Baltimore18Test() {
        String base = System.getProperty("basedir") == null ? "./": System.getProperty("basedir");
        
        String fs = System.getProperty("file.separator");
        dir = new File(base + fs + "src/test/resources" + fs + "ie" +
            fs + "baltimore" + fs + "merlin-examples",
            "merlin-xmldsig-eighteen");
        validator = new SignatureValidator(dir);
        cks = new KeySelectors.CollectionKeySelector(dir);
        ud = new LocalHttpCacheURIDereferencer();
    }
    
    @org.junit.Test
    public void testSignatureKeyname() throws Exception {
        String file = "signature-keyname.xml";
        
        boolean coreValidity = validator.validate(file, cks, ud);
        assertTrue("Signature failed core validation", coreValidity);
    }
    
    @org.junit.Test
    public void testSignatureRetrievalmethodRawx509crt() throws Exception {
        String file = "signature-retrievalmethod-rawx509crt.xml";

        boolean coreValidity = validator.validate(file, cks, ud);
        assertTrue("Signature failed core validation", coreValidity);
    }

    @org.junit.Test
    public void testSignatureX509CrtCrl() throws Exception {
        String file = "signature-x509-crt-crl.xml";

        boolean coreValidity = validator.validate(file, cks, ud);
        assertTrue("Signature failed core validation", coreValidity);
    }

    @org.junit.Test
    public void testSignatureX509Crt() throws Exception {
        String file = "signature-x509-crt.xml";

        boolean coreValidity = validator.validate(file, cks, ud);
        assertTrue("Signature failed core validation", coreValidity);
    }
    
    @org.junit.Test
    public void testSignatureX509Is() throws Exception {
        String file = "signature-x509-is.xml";

        boolean coreValidity = validator.validate(file, cks, ud);
        assertTrue("Signature failed core validation", coreValidity);
    }
    
    @org.junit.Test
    public void testSignatureX509Ski() throws Exception {
        String file = "signature-x509-ski.xml";

        boolean coreValidity = validator.validate(file, cks, ud);
        assertTrue("Signature failed core validation", coreValidity);
    }

    @org.junit.Test
    public void testSignatureX509Sn() throws Exception {
        String file = "signature-x509-sn.xml";

        boolean coreValidity = validator.validate(file, cks, ud);
        assertTrue("Signature failed core validation", coreValidity);
    }

}
