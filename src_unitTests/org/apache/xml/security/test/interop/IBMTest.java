/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "<WebSig>" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 2001, Institute for
 * Data Communications Systems, <http://www.nue.et-inf.uni-siegen.de/>.
 * The development of this software was partly funded by the European
 * Commission in the <WebSig> project in the ISIS Programme.
 * For more information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xml.security.test.interop;



import java.io.*;
import java.lang.reflect.*;
import java.security.cert.*;
import java.security.PublicKey;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.transform.TransformerException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.extensions.TestSetup;
import org.apache.xml.security.c14n.*;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.Init;
import org.apache.xml.security.keys.*;
import org.apache.xml.security.keys.content.*;
import org.apache.xml.security.keys.content.x509.*;
import org.apache.xml.security.keys.keyresolver.*;
import org.apache.xml.security.keys.storage.*;
import org.apache.xml.security.keys.storage.implementations.*;
import org.apache.xml.security.test.utils.resolver.OfflineResolver;
import org.apache.xml.security.signature.*;
import org.apache.xml.security.utils.*;
import org.apache.xml.security.utils.resolver.*;
import org.apache.xml.security.utils.resolver.implementations.*;
import org.apache.xpath.objects.*;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.*;
import org.xml.sax.SAXException;


/**
 * This test is to ensure interoperability with the examples provided by the IBM
 * XML Security Suite. They have to be located in the directory
 * <CODE>data/com/ibm/xss4j-20011029/</CODE>.
 * <BR />
 * For license issues, the vectors are not included in the distibution. See
 * <A HREF="../../../../../../../interop.html">the interop page</A> for more on this.
 *
 * @author $Author$
 * @see <A HREF="http://www.alphaworks.ibm.com/tech/xmlsecuritysuite">The IBM alphaWorks Website</A>
 */
public class IBMTest extends InteropTest {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(IBMTest.class.getName());

   /** Field kentsDir           */
   static final String kentsDir = "data/com/ibm/xss4j-20011029/";

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
         cat.error("Verification crashed for " + filename);
         throw ex;
      }

      if (!verify) {
         cat.error("Verification failed for " + filename);
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
         cat.error("Verification crashed for " + filename);
         throw ex;
      }

      if (!verify) {
         cat.error("Verification failed for " + filename);
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
         cat.error("Verification crashed for " + filename);
         throw ex;
      }

      if (!verify) {
         cat.error("Verification failed for " + filename);
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
         cat.error("Verification crashed for " + filename);
         throw ex;
      }

      if (!verify) {
         cat.error("Verification failed for " + filename);
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
         cat.error("Verification crashed for " + filename);
         throw ex;
      }

      if (!verify) {
         cat.error("Verification failed for " + filename);
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
         cat.error("Verification crashed for " + filename);
         throw ex;
      }

      if (!verify) {
         cat.error("Verification failed for " + filename);
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
         cat.error("Verification crashed for " + filename);
         throw ex;
      }

      if (!verify) {
         cat.error("Verification failed for " + filename);
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
         cat.error("Verification crashed for " + filename);
         throw ex;
      }

      if (verify) {
         cat.error("Verification failed for " + filename + ", had to be broken but was successful");
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
