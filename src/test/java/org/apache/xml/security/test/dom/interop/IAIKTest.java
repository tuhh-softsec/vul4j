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
package org.apache.xml.security.test.dom.interop;

import org.apache.xml.security.test.dom.utils.resolver.OfflineResolver;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.utils.resolver.ResourceResolverSpi;
import org.apache.xml.security.utils.resolver.implementations.ResolverAnonymous;

/**
 * This test is to ensure interoperability with the examples provided by the IAIK
 * XML Signature implementation. Thanks to Gregor Karlinger who provided these
 * test vectors. They are located in the directory <CODE>data/at/iaik/ixsil/</CODE>.
 *
 * @author $Author$
 * @see <A HREF="http://jcewww.iaik.at/products/ixsil/index.php">The IAIK IXSIL Website</A>
 */
public class IAIKTest extends InteropTestBase {

    /** {@link org.apache.commons.logging} logging facility */
    static org.slf4j.Logger log = 
        org.slf4j.LoggerFactory.getLogger(IAIKTest.class.getName());

    /** Field gregorsDir */
    static String gregorsDir = "src/test/resources/at/iaik/ixsil/";
    
    static {
        String basedir = System.getProperty("basedir");
        if (basedir != null && !"".equals(basedir)) {
            gregorsDir = basedir + "/" + gregorsDir;
        }
        org.apache.xml.security.Init.init();
    }

    /**
     * Constructor IAIKTest
     */
    public IAIKTest() {
        super();
    }

    /**
     * Method test_signatureAlgorithms_signatures_hMACShortSignature
     *
     * @throws Exception
     */
    @org.junit.Test
    public void test_signatureAlgorithms_signatures_hMACShortSignature()
        throws Exception {

        String filename =
            gregorsDir + "signatureAlgorithms/signatures/hMACShortSignature.xml";
        ResourceResolverSpi resolver = new OfflineResolver();
        boolean followManifests = false;
        byte[] hmacKey = "secret".getBytes("ASCII");

        try {
            this.verifyHMAC(filename, resolver, followManifests, hmacKey);
            fail("HMACOutputLength Exception not caught");
        } catch (RuntimeException ex) {
            log.error("Verification crashed for " + filename);
            throw ex;
        } catch (XMLSignatureException ex) {
            if (ex.getMsgID().equals("algorithms.HMACOutputLengthMin")) {
                // succeed
            } else {
                fail(ex.getMessage());
            }
        }
    }

    /**
     * Method test_signatureAlgorithms_signatures_hMACSignature
     *
     * @throws Exception
     */
    @org.junit.Test
    public void test_signatureAlgorithms_signatures_hMACSignature()
        throws Exception {

        String filename = gregorsDir + "signatureAlgorithms/signatures/hMACSignature.xml";
        ResourceResolverSpi resolver = new OfflineResolver();
        boolean followManifests = false;
        byte[] hmacKey = "secret".getBytes("ASCII");
        boolean verify = false;

        try {
            verify = this.verifyHMAC(filename, resolver, followManifests, hmacKey);
        } catch (RuntimeException ex) {
            log.error("Verification crashed for " + filename);

            throw ex;
        }

        if (!verify) {
            log.error("Verification failed for " + filename);
        }

        assertTrue(filename, verify);
    }

    /**
     * Method test_coreFeatures_signatures_manifestSignature
     *
     * @throws Exception
     */
    @org.junit.Test
    public void test_coreFeatures_signatures_manifestSignature_core()
        throws Exception {

        String filename = gregorsDir + "coreFeatures/signatures/manifestSignature.xml";
        ResourceResolverSpi resolver = null;
        boolean followManifests = false;
        boolean verify = false;

        try {
            verify = this.verify(filename, resolver, followManifests);
        } catch (RuntimeException ex) {
            log.error("Core validation crashed for " + filename);

            throw ex;
        }

        if (!verify) {
            log.error("Core validation failed for " + filename);
        }

        assertTrue("Core validation failed for " + filename, verify);
    }

    /**
     * Method test_coreFeatures_signatures_manifestSignature_manifest
     *
     * @throws Exception
     */
    @org.junit.Test
    public void test_coreFeatures_signatures_manifestSignature_manifest()
        throws Exception {

        String filename = gregorsDir + "coreFeatures/signatures/manifestSignature.xml";
        ResourceResolverSpi resolver = null;
        boolean followManifests = true;
        boolean verify = false;

        try {
            verify = this.verify(filename, resolver, followManifests);
        } catch (RuntimeException ex) {
            log.error("Verification crashed for " + filename);

            throw ex;
        }

        if (!verify) {
            log.error("Following the ds:Manifest failed for " + filename);
        }

        assertTrue("Following the ds:Manifest failed for " + filename, verify);
    }

    /**
     * Method test_coreFeatures_signatures_signatureTypesSignature
     *
     * @throws Exception
     */
    @org.junit.Test
    public void test_coreFeatures_signatures_signatureTypesSignature()
        throws Exception {

        String filename = gregorsDir + "coreFeatures/signatures/signatureTypesSignature.xml";
        ResourceResolverSpi resolver = new OfflineResolver();
        boolean followManifests = false;
        boolean verify = false;

        try {
            verify = this.verify(filename, resolver, followManifests);
        } catch (RuntimeException ex) {
            log.error("Verification crashed for " + filename);

            throw ex;
        }

        if (!verify) {
            log.error("Verification failed for " + filename);
        }

        assertTrue("Verification failed for " + filename, verify);
    }

    /**
     * Method test_coreFeatures_signatures_anonymousReferenceSignature
     *
     * @throws Exception
     */
    @org.junit.Test
    public void test_coreFeatures_signatures_anonymousReferenceSignature()
        throws Exception {

        String filename =
            gregorsDir + "coreFeatures/signatures/anonymousReferenceSignature.xml";
        String anonymousRef =
            gregorsDir + "coreFeatures/samples/anonymousReferenceContent.xml";
        ResourceResolverSpi resolver = new ResolverAnonymous(anonymousRef);
        boolean followManifests = false;
        boolean verify = false;

        try {
            verify = this.verify(filename, resolver, followManifests);
        } catch (RuntimeException ex) {
            log.error("Verification crashed for " + filename);

            throw ex;
        }

        if (!verify) {
            log.error("Verification failed for " + filename);
        }

        assertTrue(filename, verify);
    }

    /**
     * Method test_signatureAlgorithms_signatures_dSASignature
     *
     * @throws Exception
     */
    @org.junit.Test
    public void test_signatureAlgorithms_signatures_dSASignature()
        throws Exception {

        String filename = gregorsDir + "signatureAlgorithms/signatures/dSASignature.xml";
        ResourceResolverSpi resolver = null;
        boolean followManifests = false;
        boolean verify = false;

        try {
            verify = this.verify(filename, resolver, followManifests);
        } catch (RuntimeException ex) {
            log.error("Verification crashed for " + filename);

            throw ex;
        }

        if (!verify) {
            log.error("Verification failed for " + filename);
        }

        assertTrue(filename, verify);
    }

    /**
     * Method test_signatureAlgorithms_signatures_rSASignature
     *
     * @throws Exception
     */
    @org.junit.Test
    public void test_signatureAlgorithms_signatures_rSASignature()
        throws Exception {

        String filename = gregorsDir + "signatureAlgorithms/signatures/rSASignature.xml";
        ResourceResolverSpi resolver = null;
        boolean followManifests = false;
        boolean verify = false;

        try {
            verify = this.verify(filename, resolver, followManifests);
        } catch (RuntimeException ex) {
            log.error("Verification crashed for " + filename);

            throw ex;
        }

        if (!verify) {
            log.error("Verification failed for " + filename);
        }

        assertTrue(filename, verify);
    }

    /**
     * Method test_transforms_signatures_base64DecodeSignature
     *
     * @throws Exception
     */
    @org.junit.Test
    public void test_transforms_signatures_base64DecodeSignature()
        throws Exception {

        String filename = gregorsDir + "transforms/signatures/base64DecodeSignature.xml";
        ResourceResolverSpi resolver = null;
        boolean followManifests = false;
        boolean verify = false;

        try {
            verify = this.verify(filename, resolver, followManifests);
        } catch (RuntimeException ex) {
            log.error("Verification crashed for " + filename);

            throw ex;
        }

        if (!verify) {
            log.error("Verification failed for " + filename);
        }

        assertTrue(filename, verify);
    }

    /**
     * Method test_transforms_signatures_c14nSignature
     *
     * @throws Exception
     */
    @org.junit.Test
    public void test_transforms_signatures_c14nSignature() throws Exception {

        String filename = gregorsDir + "transforms/signatures/c14nSignature.xml";
        ResourceResolverSpi resolver = null;
        boolean followManifests = false;
        boolean verify = false;

        try {
            verify = this.verify(filename, resolver, followManifests);
        } catch (RuntimeException ex) {
            log.error("Verification crashed for " + filename);

            throw ex;
        }

        if (!verify) {
            log.error("Verification failed for " + filename);
        }

        assertTrue(filename, verify);
    }

    /**
     * Method test_transforms_signatures_envelopedSignatureSignature
     *
     * @throws Exception
     */
    @org.junit.Test
    public void test_transforms_signatures_envelopedSignatureSignature()
        throws Exception {

        String filename =
            gregorsDir + "transforms/signatures/envelopedSignatureSignature.xml";
        ResourceResolverSpi resolver = null;
        boolean followManifests = false;
        boolean verify = false;

        try {
            verify = this.verify(filename, resolver, followManifests);
        } catch (RuntimeException ex) {
            log.error("Verification crashed for " + filename);

            throw ex;
        }

        if (!verify) {
            log.error("Verification failed for " + filename);
        }

        assertTrue(filename, verify);
    }

    /**
     * Method test_transforms_signatures_xPathSignature
     *
     * @throws Exception
     */
    @org.junit.Test
    public void test_transforms_signatures_xPathSignature() throws Exception {

        String filename = gregorsDir + "transforms/signatures/xPathSignature.xml";
        ResourceResolverSpi resolver = null;
        boolean followManifests = false;
        boolean verify = false;

        try {
            verify = this.verify(filename, resolver, followManifests);
        } catch (RuntimeException ex) {
            log.error("Verification crashed for " + filename);

            throw ex;
        }

        if (!verify) {
            log.error("Verification failed for " + filename);
        }

        assertTrue(filename, verify);
    }

}
