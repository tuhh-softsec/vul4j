package org.apache.xml.security.test.interop;


import java.io.File;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

import junit.framework.TestCase;

import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.utils.resolver.ResourceResolverSpi;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Element;


/**
 *
 * @author $Author$
 */
public class InteropTest extends TestCase {

   /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(BaltimoreTest.class.getName());

   /** Field xercesVerStr */
   static String xercesVerStr = XMLUtils.getXercesVersion();

   /** Field xalanVerStr */
   static String xalanVerStr = XMLUtils.getXalanVersion();

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
    *
    * @throws Exception
    */
   public boolean verifyHMAC(
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
    *
    * @throws Exception
    */
  public boolean verify(String filename, ResourceResolverSpi resolver, boolean followManifests)
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