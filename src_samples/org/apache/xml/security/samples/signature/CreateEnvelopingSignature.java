package org.apache.xml.security.samples.signature;


import java.io.*;
import java.lang.reflect.*;
import java.security.*;
import java.security.cert.*;
import java.util.*;
import javax.xml.transform.TransformerException;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.*;
import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
import org.apache.xml.security.c14n.helper.XPathContainer;
import org.apache.xml.security.c14n.*;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.*;
import org.apache.xml.security.keys.*;
import org.apache.xml.security.keys.content.*;
import org.apache.xml.security.keys.content.x509.*;
import org.apache.xml.security.keys.keyresolver.*;
import org.apache.xml.security.keys.storage.*;
import org.apache.xml.security.keys.storage.implementations.*;
import org.apache.xml.security.utils.*;
import org.apache.xml.security.transforms.*;
import org.apache.xml.security.Init;
import org.apache.xml.security.samples.utils.resolver.OfflineResolver;
import  org.apache.xml.serialize.*;


/**
 *
 *
 * @author $Author$
 */
public class CreateEnvelopingSignature {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(CreateSignature.class.getName());

   /**
    * Method main
    *
    * @param unused
    * @throws Exception
    */
   public static void main(String unused[]) throws Exception {
      //J-
      String keystoreType = "JKS";
      String keystoreFile = "data/org/apache/xml/security/samples/input/keystore.jks";
      String keystorePass = "xmlsecurity";
      String privateKeyAlias = "test";
      String privateKeyPass = "xmlsecurity";
      String certificateAlias = "test";
      File signatureFile = new File("signature.xml");
      //J+
      KeyStore ks = KeyStore.getInstance(keystoreType);
      FileInputStream fis = new FileInputStream(keystoreFile);

      ks.load(fis, keystorePass.toCharArray());

      PrivateKey privateKey = (PrivateKey) ks.getKey(privateKeyAlias,
                                             privateKeyPass.toCharArray());
      javax.xml.parsers.DocumentBuilderFactory dbf =
         javax.xml.parsers.DocumentBuilderFactory.newInstance();

      dbf.setNamespaceAware(true);

      javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
      org.w3c.dom.Document doc = db.newDocument();

      String BaseURI = signatureFile.toURL().toString();
      XMLSignature sig = new XMLSignature(doc, BaseURI,
                                          XMLSignature.ALGO_ID_SIGNATURE_DSA);

      doc.appendChild(sig.getElement());
      {
         ObjectContainer obj = new ObjectContainer(doc);
         Element anElement = doc.createElement("InsideObject");
         anElement.appendChild(doc.createTextNode("A text in a box"));
         obj.appendChild(anElement);
         String Id = "TheFirstObject";
         obj.setId(Id);
         sig.appendObject(obj);

         Transforms transforms = new Transforms(doc);
         transforms.addTransform(Transforms.TRANSFORM_C14N_WITH_COMMENTS);

         sig.addDocument("#" + Id, transforms, Constants.ALGO_ID_DIGEST_SHA1);
      }

      {
         X509Certificate cert =
            (X509Certificate) ks.getCertificate(certificateAlias);

         sig.addKeyInfo(cert);
         sig.addKeyInfo(cert.getPublicKey());
         System.out.println("Start signing");
         sig.sign(privateKey);
         System.out.println("Finished signing");
      }

      FileOutputStream f = new FileOutputStream(signatureFile);

      XMLUtils.outputDOMc14nWithComments(doc, f);

      f.close();
      System.out.println("Wrote signature to " + BaseURI);
   }

   static {
      org.apache.xml.security.Init.init();
   }
}