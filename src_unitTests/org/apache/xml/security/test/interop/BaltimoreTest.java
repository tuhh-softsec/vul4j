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
import org.apache.xml.security.c14n.helper.XPathContainer;
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
 * This test is to ensure interoperability with the examples provided by Merlin Huges
 * from Baltimore using KeyTools XML. These test vectors are located in the directory
 * <CODE>data/ie/baltimore/merlin-examples/</CODE>.
 *
 * @author $Author$
 * @see <A HREF="http://www.baltimore.com/keytools/xml/index.html">The KeyTools XML Website</A>
 */
public class BaltimoreTest extends InteropTest {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(BaltimoreTest.class.getName());

   /** Field merlinsDir15           */
   static final String merlinsDir15 =
      "data/ie/baltimore/merlin-examples/merlin-xmldsig-fifteen/";

   /**
    * Method suite
    *
    * @return
    */
   public static Test suite() {
      TestSuite suite = new TestSuite(BaltimoreTest.class);

      return suite;
   }
   /**
    * Constructor BaltimoreTest
    *
    * @param Name_
    */
   public BaltimoreTest(String Name_) {
      super(Name_);
   }


   /**
    * Method main
    *
    * @param args
    */
   public static void main(String[] args) {

      String[] testCaseName = { "-noloading", BaltimoreTest.class.getName() };

      junit.textui.TestRunner.main(testCaseName);
   }

   /**
    * Method test_fifteen_enveloping_hmac_sha1
    *
    * @throws Exception
    */
   public void test_fifteen_enveloping_hmac_sha1() throws Exception {

      String filename = merlinsDir15 + "signature-enveloping-hmac-sha1.xml";
      boolean verify = this.verifyHMAC(filename, new OfflineResolver(), false,
                                       "secret".getBytes("ASCII"));

      if (!verify) {
         cat.error("Verification failed for " + filename);
      }

      assertTrue(filename, verify);
   }

   /**
    * Method test_fifteen_enveloping_hmac_sha1_40
    *
    * @throws Exception
    */
   public void test_fifteen_enveloping_hmac_sha1_40() throws Exception {

      String filename = merlinsDir15 + "signature-enveloping-hmac-sha1-40.xml";
      ResourceResolverSpi resolver = new OfflineResolver();
      boolean followManifests = false;
      byte[] hmacKey = "secret".getBytes("ASCII");
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
    * Method test_fifteen_enveloped_dsa
    *
    * @throws Exception
    */
   public void test_fifteen_enveloped_dsa() throws Exception {

      String filename = merlinsDir15 + "signature-enveloped-dsa.xml";
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
    * Method test_fifteen_enveloping_b64_dsa
    *
    * @throws Exception
    */
   public void test_fifteen_enveloping_b64_dsa() throws Exception {

      String filename = merlinsDir15 + "signature-enveloping-b64-dsa.xml";
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
    * Method test_fifteen_enveloping_dsa
    *
    * @throws Exception
    */
   public void test_fifteen_enveloping_dsa() throws Exception {

      String filename = merlinsDir15 + "signature-enveloping-dsa.xml";
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
    * Method test_fifteen_enveloping_rsa
    *
    * @throws Exception
    */
   public void test_fifteen_enveloping_rsa() throws Exception {

      String filename = merlinsDir15 + "signature-enveloping-rsa.xml";
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
    * Method test_fifteen_external_b64_dsa
    *
    * @throws Exception
    */
   public void test_fifteen_external_b64_dsa() throws Exception {

      String filename = merlinsDir15 + "signature-external-b64-dsa.xml";
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
    * Method test_fifteen_external_dsa
    *
    * @throws Exception
    */
   public void test_fifteen_external_dsa() throws Exception {

      String filename = merlinsDir15 + "signature-external-dsa.xml";
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
    * Method test_sixteen_external_dsa
    *
    * @throws Exception
    */
   public void test_sixteen_external_dsa() throws Exception {

      String filename =
         "data/ie/baltimore/merlin-examples/merlin-xmldsig-sixteen/signature.xml";
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

   static {
      org.apache.xml.security.Init.init();
   }
}
