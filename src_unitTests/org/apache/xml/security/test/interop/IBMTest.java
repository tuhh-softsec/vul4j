/*
 * Copyright  1999-2004 The Apache Software Foundation.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.xml.security.test.interop;



import java.io.File;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.xml.security.test.utils.resolver.OfflineResolver;
import org.apache.xml.security.utils.JavaUtils;
import org.apache.xml.security.utils.resolver.ResourceResolverSpi;


/**
 * This test is to ensure interoperability with the examples provided by the IBM
 * XML Security Suite. They have to be located in the directory
 * <CODE>data/com/ibm/xss4j-20030127/</CODE>.
 * <BR />
 * For license issues, the vectors are not included in the distibution. See
 * <A HREF="../../../../../../../interop.html">the interop page</A> for more on this.
 *
 * @author $Author$
 * @see <A HREF="http://www.alphaworks.ibm.com/tech/xmlsecuritysuite">The IBM alphaWorks Website</A>
 */
public class IBMTest extends InteropTest {

   /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(IBMTest.class.getName());

   /** Field kentsDir           */
   static final String kentsDir = "data/com/ibm/xss4j-20030127/";

   /**
    * Method suite
    *
    *
    */
   public static Test suite() {

      TestSuite suite = new TestSuite(IBMTest.class);

      return suite;
   }

   /**
    * Constructor IBMTest
    *
    * @param Name_
    */
   public IBMTest(String Name_) {
      super(Name_);
   }

   /**
    * Method main
    *
    * @param args
    */
   public static void main(String[] args) {

      String[] testCaseName = { "-noloading", IBMTest.class.getName() };

      junit.textui.TestRunner.main(testCaseName);
   }

   /**
    * Method test_enveloping_hmac
    *
    * @throws Exception
    */
   public void test_enveloping_hmac() throws Exception {

      String filename = kentsDir + "enveloping-hmac.sig";
      ResourceResolverSpi resolver = new OfflineResolver();
      boolean followManifests = false;
      byte[] hmacKey = JavaUtils.getBytesFromFile(kentsDir + "enveloping-hmac.key");
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
    * Method test_detached_dsa
    *
    * @throws Exception
    */
   public void test_detached_dsa() throws Exception {
      String filename = kentsDir + "detached-dsa.sig";
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

      assertTrue(filename, verify);
   }

   /**
    * Method test_detached_rsa
    *
    * @throws Exception
    */
   public void test_detached_rsa() throws Exception {
      String filename = kentsDir + "detached-rsa.sig";
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

      assertTrue(filename, verify);
   }

   /**
    * Method test_enveloped_dsa
    *
    * @throws Exception
    */
   public void test_enveloped_dsa() throws Exception {
      String filename = kentsDir + "enveloped-dsa.sig";
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
    * Method test_enveloped_rsa
    *
    * @throws Exception
    */
   public void test_enveloped_rsa() throws Exception {
      String filename = kentsDir + "enveloped-rsa.sig";
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
    * Method test_enveloping_dsa
    *
    * @throws Exception
    */
   public void test_enveloping_dsa() throws Exception {
      String filename = kentsDir + "enveloping-dsa.sig";
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
    * Method test_enveloping_rsa
    *
    * @throws Exception
    */
   public void test_enveloping_rsa() throws Exception {
      String filename = kentsDir + "enveloping-rsa.sig";
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
    * Method test_enveloping_dsa_soaped_broken
    *
    * @throws Exception
    */
   public void test_enveloping_dsa_soaped_broken() throws Exception {
      String filename = kentsDir + "enveloping-dsa-soaped-broken.sig";
      if(!new File(filename).exists() ) {
        System.err.println("Couldn't find: " + filename + " and couldn't do the test");
        return;
      }
      ResourceResolverSpi resolver = null;
      boolean followManifests = false;
      boolean verify = true;

      try {
         verify = this.verify(filename, resolver, followManifests);
      } catch (RuntimeException ex) {
         log.error("Verification crashed for " + filename);
         throw ex;
      }

      if (verify) {
         log.error("Verification failed for " + filename + ", had to be broken but was successful");
      }

      assertTrue(filename, !verify);
   }

   /**
    * Method test_enveloping_exclusive
    *
    * @throws Exception
    * $todo$ implement exclusive-c14n
    */
   public void _not_active_test_enveloping_exclusive() throws Exception {
     // exclusive c14n not supported yet
   }

   /**
    * Method test_enveloping_exclusive_soaped
    *
    * @throws Exception
    * $todo$ implement exclusive-c14n
    */
   public void _not_active_test_enveloping_exclusive_soaped() throws Exception {
     // exclusive c14n not supported yet
   }

   static {
      org.apache.xml.security.Init.init();
   }
}
