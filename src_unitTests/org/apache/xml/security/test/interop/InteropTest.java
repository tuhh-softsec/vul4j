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
import org.apache.xml.security.samples.utils.resolver.OfflineResolver;
import org.apache.xml.security.signature.*;
import org.apache.xml.security.utils.*;
import org.apache.xml.security.utils.resolver.*;
import org.apache.xml.security.utils.resolver.implementations.*;
import org.apache.xpath.objects.*;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.*;
import org.xml.sax.SAXException;


/**
 *
 * @author $Author$
 */
public class InteropTest extends TestCase {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(BaltimoreTest.class.getName());

   /** Field xercesVerStr */
   static String xercesVerStr = "Apache "
                                + org.apache.xerces.framework.Version.fVersion;

   /** Field xalanVerStr */
   static String xalanVerStr =
      "Apache " + org.apache.xml.security.utils.XMLUtils.getXalanVersion();
   /** Field dbf */
   javax.xml.parsers.DocumentBuilderFactory dbf = null;

   public InteropTest(String Name_) {
      super(Name_);
   }

   /**
    * Method setUp
    *
    */
   protected void setUp() {

      this.dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance();

      this.dbf.setNamespaceAware(true);
   }


   /**
    * Method verifyHMAC
    *
    * @param filename
    * @param resolver
    * @param hmacKey
    * @return
    * @throws Exception
    */
   boolean verifyHMAC(
           String filename, ResourceResolverSpi resolver, boolean followManifests, byte[] hmacKey)
              throws Exception {

      File f = new File(filename);
      javax.xml.parsers.DocumentBuilder db = this.dbf.newDocumentBuilder();
      org.w3c.dom.Document doc = db.parse(new java.io.FileInputStream(f));
      Element nscontext = XMLUtils.createDSctx(doc, "ds",
                                               Constants.SignatureSpecNS);
      Element sigElement = (Element) XPathAPI.selectSingleNode(doc,
                              "//ds:Signature[1]", nscontext);
      XMLSignature signature = new XMLSignature(sigElement,
                                                f.toURL().toString());

      if (resolver != null) {
         signature.addResourceResolver(resolver);
      }
      signature.setFollowNestedManifests(followManifests);

      byte keybytes[] = hmacKey;
      javax.crypto.SecretKey sk = signature.createSecretKey(keybytes);

      return signature.checkSignatureValue(sk);
   }

   /**
    * Method verify
    *
    * @param filename
    * @param resolver
    * @return
    * @throws Exception
    */
  boolean verify(String filename, ResourceResolverSpi resolver, boolean followManifests)
           throws Exception {

      File f = new File(filename);
      javax.xml.parsers.DocumentBuilder db = this.dbf.newDocumentBuilder();
      org.w3c.dom.Document doc = db.parse(new java.io.FileInputStream(f));
      Element nscontext = XMLUtils.createDSctx(doc, "ds",
                                               Constants.SignatureSpecNS);
      Element sigElement = (Element) XPathAPI.selectSingleNode(doc,
                              "//ds:Signature[1]", nscontext);
      XMLSignature signature = new XMLSignature(sigElement,
                                                f.toURL().toString());

      if (resolver != null) {
         signature.addResourceResolver(resolver);
      }
      signature.setFollowNestedManifests(followManifests);


      KeyInfo ki = signature.getKeyInfo();

      if (ki != null) {
         X509Certificate cert = ki.getX509Certificate();

         if (cert != null) {
            return signature.checkSignatureValue(cert);
         } else {
            PublicKey pk = ki.getPublicKey();

            if (pk != null) {
               return signature.checkSignatureValue(pk);
            } else {
               throw new RuntimeException(
                  "Did not find a public key, so I can't check the signature");
            }
         }
      } else {
         throw new RuntimeException("Did not find a KeyInfo");
      }
   }
}