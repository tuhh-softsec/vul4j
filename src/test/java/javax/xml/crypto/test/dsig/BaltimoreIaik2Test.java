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
 * This is a testcase to validate all "ec-merlin-iaikTests-two" 
 * testcases from Baltimore
 *
 * @author Sean Mullan
 */
public class BaltimoreIaik2Test extends org.junit.Assert {

    private SignatureValidator validator;
    private File dir;

    static {
        Security.insertProviderAt
            (new org.apache.jcp.xml.dsig.internal.dom.XMLDSigRI(), 1);
    }

    public BaltimoreIaik2Test() {
        String fs = System.getProperty("file.separator");
        String base = System.getProperty("basedir") == null ? "./": System.getProperty("basedir");
        
        dir = new File(base + fs + "src/test/resources" + fs +
            "ie" + fs + "baltimore" + fs + "merlin-examples",
            "ec-merlin-iaikTests-two");
        validator = new SignatureValidator(dir);
    }

    @org.junit.Test
    public void testSignature() throws Exception {
        String file = "signature.xml";
        boolean coreValidity = validator.validate
            (file, new KeySelectors.KeyValueKeySelector());
        assertTrue("Signature failed core validation", coreValidity);
    }    

}
