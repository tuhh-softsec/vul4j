
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
package org.apache.xml.security.test.c14n.implementations;



import java.io.File;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.xml.security.signature.Reference;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.test.interop.InteropTest;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.utils.JavaUtils;
import org.w3c.dom.Element;


/**
 * Interop test for exclusive canonical XML.
 *
 * @author Christian Geuer-Pollmann
 */
public class ExclusiveC14NInterop extends InteropTest {

   /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(ExclusiveC14NInterop.class.getName());

    static {
        org.apache.xml.security.Init.init();
    }
   /**
    * Method suite
    *
    *
    */
   public static Test suite() {
      return new TestSuite(ExclusiveC14NInterop.class);
   }

   /**
    *  Constructor ExclusiveC14NInterop
    *
    *  @param Name_
    */
   public ExclusiveC14NInterop(String Name_) {
      super(Name_);
   }

   /**
    * Method main
    *
    * @param args
    */
   public static void main(String[] args) {

      String[] testCaseName = { "-noloading",
                                ExclusiveC14NInterop.class.getName() };

      org.apache.xml.security.Init.init();
      junit.textui.TestRunner.main(testCaseName);
   }

   /**
    * Method test_Y1
    *
    * @throws Exception
    */
   public void test_Y1() throws Exception {

      String success = t("data/interop/c14n/Y1", "exc-signature.xml");

      assertTrue(success, success == null);
   }

   /**
    * Method test_Y2
    *
    * @throws Exception
    */
   public void test_Y2() throws Exception {

      String success = t("data/interop/c14n/Y2", "signature-joseph-exc.xml");

      assertTrue(success, success == null);
   }

   /**
    * Method test_Y3
    *
    * @throws Exception
    */
   public void test_Y3() throws Exception {

      String success = t("data/interop/c14n/Y3", "signature.xml");

      assertTrue(success, success == null);
   }

   /**
    * Method test_Y4
    *
    * @throws Exception
    */
   public void test_Y4() throws Exception {

      String success = t("data/interop/c14n/Y4", "signature.xml");

      assertTrue(success, success == null);
   }

   public void test_xfilter2() throws Exception {

      String success = t("data/interop/xfilter2/merlin-xpath-filter2-three", "sign-spec.xml");

      assertTrue(success, success == null);
   }

   /**
    * Method t
    *
    * @param directory
    * @param file
    *
    * @throws Exception
    */
   public String t(String directory, String file) throws Exception
   {
   	  String basedir = System.getProperty("basedir");
   	  if(basedir != null && !"".equals(basedir)) {
   	  	directory = basedir + "/" + directory;
   	  }

      File f = new File(directory + "/" + file);
      javax.xml.parsers.DocumentBuilderFactory dbf =
         javax.xml.parsers.DocumentBuilderFactory.newInstance();

      dbf.setNamespaceAware(true);

      javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
      org.w3c.dom.Document doc = db.parse(f);
      long start = System.currentTimeMillis();

      //XMLUtils.circumventBug2650(doc);

      long end = System.currentTimeMillis();

      log.debug("fixSubtree took " + (int) (end - start));

      Element sigElement =
         (Element) doc.getElementsByTagNameNS(Constants.SignatureSpecNS,
                                              Constants._TAG_SIGNATURE).item(0);
      XMLSignature signature = new XMLSignature(sigElement,
                                                f.toURL().toString());
      boolean verify =
         signature.checkSignatureValue(signature.getKeyInfo().getPublicKey());

      log.debug("   signature.checkSignatureValue finished: " + verify);

      int failures = 0;

      // if (!verify) {
      if (true) {
         StringBuffer sb = new StringBuffer();

         for (int i = 0; i < signature.getSignedInfo().getLength(); i++) {
            boolean refVerify =
               signature.getSignedInfo().getVerificationResult(i);
            JavaUtils.writeBytesToFilename(directory + "/c14n-" + i + ".apache.html", signature.getSignedInfo().item(i).getHTMLRepresentation().getBytes());

            if (refVerify) {
               log.debug("Reference " + i + " was OK");
            } else {
               failures++;

               sb.append(i + " ");

               JavaUtils.writeBytesToFilename(directory + "/c14n-" + i + ".apache.txt", signature.getSignedInfo().item(i).getContentsAfterTransformation().getBytes());
               JavaUtils.writeBytesToFilename(directory + "/c14n-" + i + ".apache.html", signature.getSignedInfo().item(i).getHTMLRepresentation().getBytes());

               Reference reference = signature.getSignedInfo().item(i);
               int length = reference.getTransforms().getLength();
               String algo = reference.getTransforms().item(length
                  - 1).getURI();

               log.debug("Reference " + i + " failed: " + algo);
            }
         }

         String r = sb.toString().trim();

         if (r.length() == 0) {
            return null;
         } else {
            return r;
         }
      } else {
         return null;
      }
   }
}
