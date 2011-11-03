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

import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.ExcC14NParameterSpec;

/**
 * Unit test for javax.xml.crypto.dsig.CanonicalizationMethod
 *
 * @version $Id$
 * @author Valerie Peng
 */
public class CanonicalizationMethodTest extends org.junit.Assert {

    XMLSignatureFactory factory;

    static {
        Security.insertProviderAt
            (new org.apache.jcp.xml.dsig.internal.dom.XMLDSigRI(), 1);
    }

    private static final String C14N_ALGOS[] = {
        CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS,
        CanonicalizationMethod.INCLUSIVE,
        CanonicalizationMethod.EXCLUSIVE_WITH_COMMENTS,
        CanonicalizationMethod.EXCLUSIVE
    };

    public CanonicalizationMethodTest() throws Exception { 
        factory = XMLSignatureFactory.getInstance
            ("DOM", new org.apache.jcp.xml.dsig.internal.dom.XMLDSigRI());
    }

    @org.junit.Test
    public void testIsFeatureSupported() throws Exception {
        CanonicalizationMethod cm; 
        for (int i = 0; i < C14N_ALGOS.length; i++) {
            String algo = C14N_ALGOS[i];
            ExcC14NParameterSpec params = null;
            if (i >= 2) {
                params = new ExcC14NParameterSpec();
            }
            cm = factory.newCanonicalizationMethod(algo, params);
            try {
                cm.isFeatureSupported(null); 
                fail("Should raise a NPE for null feature"); 
            } catch (NullPointerException npe) {}
            
            assertTrue(!cm.isFeatureSupported("not supported"));
        }
    }

    @org.junit.Test
    public void testConstructor() throws Exception {
        // test newAlgorithmMethod(String algorithm, 
        //                         AlgorithmParameterSpec params)
        // for generating CanonicalizationMethod objects
        CanonicalizationMethod cm; 
        for (int i = 0; i < C14N_ALGOS.length; i++) {
            String algo = C14N_ALGOS[i];
            cm = factory.newCanonicalizationMethod(algo, 
                (C14NMethodParameterSpec) null);
            assertNotNull(cm);
            assertEquals(cm.getAlgorithm(), algo);
            assertNull(cm.getParameterSpec());
            
            try {
                cm = factory.newCanonicalizationMethod
                    (algo, new TestUtils.MyOwnC14nParameterSpec());
                fail("Should raise an IAPE for invalid c14n parameters"); 
            } catch (InvalidAlgorithmParameterException iape) {
            } catch (Exception ex) {
                fail("Should raise a IAPE instead of " + ex); 
            }
            if (algo.equals(CanonicalizationMethod.EXCLUSIVE_WITH_COMMENTS) ||
                algo.equals(CanonicalizationMethod.EXCLUSIVE)) {
                cm = factory.newCanonicalizationMethod
                    (CanonicalizationMethod.EXCLUSIVE,
                     new ExcC14NParameterSpec());
                AlgorithmParameterSpec aps = cm.getParameterSpec();
                assertNotNull(aps);
                assertTrue(aps instanceof ExcC14NParameterSpec);	
            }
        }

        try {
            cm = factory.newCanonicalizationMethod(null, 
                (C14NMethodParameterSpec) null); 
            fail("Should raise a NPE for null algo"); 
        } catch (NullPointerException npe) {
        } catch (Exception ex) {
            fail("Should raise a NPE instead of " + ex); 
        }

        try {
            cm = factory.newCanonicalizationMethod("non-existent", 
                (C14NMethodParameterSpec) null); 
            fail("Should raise an NSAE for non-existent algos"); 
        } catch (NoSuchAlgorithmException nsae) {
        } catch (Exception ex) {
            fail("Should raise an NSAE instead of " + ex); 
        }
    }
    
}
