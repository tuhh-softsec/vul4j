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

import javax.xml.crypto.test.KeySelectors;

/**
 * This is a testcase to validate all "c14n" testcases
 * under data/vectors/interop directory
 *
 * @author Sean Mullan
 */
public class InteropC14nTest extends org.junit.Assert {

    private SignatureValidator validator = null;
    private String base;

    static {
        Security.insertProviderAt
            (new org.apache.jcp.xml.dsig.internal.dom.XMLDSigRI(), 1);
    }

    public InteropC14nTest() {
        String fs = System.getProperty("file.separator");
        base = System.getProperty("basedir") == null ? "./": System.getProperty("basedir");	
        base = base + fs + "src/test/resources" + fs + "interop";
    }
    
    @org.junit.Test
    public void test_y1_exc_signature() throws Exception {
        validator = new SignatureValidator(new File(base, "c14n/Y1"));
        String file = "exc-signature.xml";

        boolean coreValidity = validator.validate
            (file, new KeySelectors.KeyValueKeySelector());
        assertTrue("Signature failed core validation", coreValidity);

    }
    
    /* COMMENTED OUT since this test requires MD5 support
    public void test_y2_signature_joseph_exc() throws Exception {
        validator = new SignatureValidator(new File(base, "c14n/Y2"));
        String file = "signature-joseph-exc.xml";

        boolean coreValidity = validator.validate
            (file, new KeySelectors.KeyValueKeySelector());
        assertTrue("Signature failed core validation", coreValidity);
    }
    */
    
    @org.junit.Test
    public void test_y3_signature() throws Exception {
        validator = new SignatureValidator(new File(base, "c14n/Y3"));
        String file = "signature.xml";

        boolean coreValidity = validator.validate
            (file, new KeySelectors.KeyValueKeySelector());
        assertTrue("Signature failed core validation#1", coreValidity);

        coreValidity = validator.validate
            (file, new KeySelectors.RawX509KeySelector());
        assertTrue("Signature failed core validation#2", coreValidity);
    }
    
    @org.junit.Test
    public void test_y4_signature() throws Exception {
        validator = new SignatureValidator(new File(base, "c14n/Y4"));
        String file = "signature.xml";

        boolean coreValidity = validator.validate
            (file, new KeySelectors.KeyValueKeySelector());
        assertTrue("Signature failed core validation#1", coreValidity);

        coreValidity = validator.validate
            (file, new KeySelectors.RawX509KeySelector());
        assertTrue("Signature failed core validation#2", coreValidity);
    }
    
    @org.junit.Test
    @org.junit.Ignore
    public void test_y5_signature() throws Exception {
        validator = new SignatureValidator(new File(base, "c14n/Y5"));
        String file = "signature.xml";

        boolean coreValidity = validator.validate
            (file, new KeySelectors.KeyValueKeySelector());
        assertTrue("Signature failed core validation#1", coreValidity);

        coreValidity = validator.validate
            (file, new KeySelectors.RawX509KeySelector());
        assertTrue("Signature failed core validation#2", coreValidity);
    }
    
    @org.junit.Test
    @org.junit.Ignore
    public void test_y5_signatureCommented() throws Exception {
        validator = new SignatureValidator(new File(base, "c14n/Y5"));
        String file = "signatureCommented.xml";

        boolean coreValidity = validator.validate
            (file, new KeySelectors.KeyValueKeySelector());
        assertTrue("Signature failed core validation#1", coreValidity);

        coreValidity = validator.validate
            (file, new KeySelectors.RawX509KeySelector());
        assertTrue("Signature failed core validation#2", coreValidity);
    }
    
}
