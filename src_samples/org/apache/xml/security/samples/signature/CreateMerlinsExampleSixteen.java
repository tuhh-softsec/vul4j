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
import org.apache.xml.serialize.*;


/**
 *
 * @author $Author$
 */
public class CreateMerlinsExampleSixteen {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category
         .getInstance(CreateMerlinsExampleSixteen.class.getName());

   /**
    * Method main
    *
    * @param unused
    * @throws Exception
    */
   public static void main(String unused[]) throws Exception {
      Constants.setSignatureSpecNSprefix("");
      //J-
      String keystoreType = "JKS";
      String keystoreFile = "data/org/apache/xml/security/samples/input/keystore.jks";
      String keystorePass = "xmlsecurity";
      String privateKeyAlias = "test";
      String privateKeyPass = "xmlsecurity";
      String certificateAlias = "test";
      File signatureFile = new File("merlinsSixteenRecreatedNoRetrievalMethod.xml");
      //J+
      KeyStore ks = KeyStore.getInstance(keystoreType);
      FileInputStream fis = new FileInputStream(keystoreFile);

      ks.load(fis, keystorePass.toCharArray());

      PrivateKey privateKey = (PrivateKey) ks.getKey(privateKeyAlias,
                                 privateKeyPass.toCharArray());

      if (privateKey == null) {
         throw new RuntimeException("Private key is null");
      }

      X509Certificate cert =
         (X509Certificate) ks.getCertificate(certificateAlias);
      javax.xml.parsers.DocumentBuilderFactory dbf =
         javax.xml.parsers.DocumentBuilderFactory.newInstance();

      dbf.setNamespaceAware(true);

      javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
      org.w3c.dom.Document doc = db.newDocument();

      //////////////////////////////////////////////////
      Element envelope = doc.createElementNS("http://www.usps.gov/",
                                             "Envelope");

      envelope.setAttribute("xmlns", "http://www.usps.gov/");
      envelope.setAttribute("xmlns:foo", "http://www.usps.gov/foo");
      envelope.appendChild(doc.createTextNode("\n"));
      doc.appendChild(doc.createComment(" Preamble "));
      doc.appendChild(envelope);
      doc.appendChild(doc.createComment(" Postamble "));

      Element dearSir = doc.createElementNS("http://www.usps.gov/", "DearSir");

      dearSir.appendChild(doc.createTextNode("foo"));
      envelope.appendChild(dearSir);
      envelope.appendChild(doc.createTextNode("\n"));

      Element body = doc.createElementNS("http://www.usps.gov/", "Body");

      body.appendChild(doc.createTextNode("bar"));
      envelope.appendChild(body);
      envelope.appendChild(doc.createTextNode("\n"));

      Element YoursSincerely = doc.createElementNS("http://www.usps.gov/",
                                  "YoursSincerely");
      YoursSincerely.appendChild(doc.createTextNode("\n"));

      envelope.appendChild(YoursSincerely);

      Element PostScript = doc.createElementNS("http://www.usps.gov/",
                                               "PostScript");

      PostScript.appendChild(doc.createTextNode("bar"));
      envelope.appendChild(PostScript);

      Element Notaries = doc.createElement("Notaries");

      Notaries.setAttribute("xmlns", "");
      Notaries.setAttribute("Id", "notaries");
      IdResolver.registerElementById(Notaries, "Id");

      {
         Element Notary = doc.createElement("Notary");

         Notary.setAttribute("name", "Great, A. T.");
         Notaries.appendChild(Notary);
      }

      {
         Element Notary = doc.createElement("Notary");

         Notary.setAttribute("name", "Hun, A. T.");
         Notaries.appendChild(Notary);
      }

      envelope.appendChild(Notaries);
      envelope.appendChild(doc.createComment(" Commentary "));

      //////////////////////////////////////////////////
      String BaseURI = signatureFile.toURL().toString();
      XMLSignature sig = new XMLSignature(doc, BaseURI,
                                          XMLSignature.ALGO_ID_SIGNATURE_DSA);

      YoursSincerely.appendChild(sig.getElement());
      sig.setId("signature");

      /*
       * Add the Objects
       */

      // object-1
      {
         ObjectContainer object1 = new ObjectContainer(doc);

         object1.setId("object-1");
         object1.setMimeType("text/plain");
         object1.appendChild(doc.createTextNode("I am the text."));
         sig.appendObject(object1);
      }

      // object-2
      {
         ObjectContainer object2 = new ObjectContainer(doc);

         object2.setId("object-2");
         object2.setMimeType("text/plain");
         object2.setEncoding("http://www.w3.org/2000/09/xmldsig#base64");
         object2.appendChild(doc.createTextNode("SSBhbSB0aGUgdGV4dC4="));
         sig.appendObject(object2);
      }

      // object-3
      {
         ObjectContainer object = new ObjectContainer(doc);

         object.setId("object-3");

         Element nonc = doc.createElement("NonCommentandus");

         nonc.setAttribute("xmlns", "");
         nonc.appendChild(doc.createComment(" Commentandum "));
         object.appendChild(doc.createTextNode("\n        "));
         object.appendChild(nonc);
         object.appendChild(doc.createTextNode("\n      "));
         sig.appendObject(object);
      }

      // object number 4
      {
         ObjectContainer object = new ObjectContainer(doc);

         object.appendChild(createObject4(sig));
         sig.appendObject(object);
      }

      // object number 4
      {
         ObjectContainer object = new ObjectContainer(doc);
         SignatureProperties sps = new SignatureProperties(doc);

         sps.setId("signature-properties-1");

         SignatureProperty sp = new SignatureProperty(doc, "#signature");
         Element signedAdress = doc.createElementNS("urn:demo",
                                                    "SignedAddress");

         signedAdress.setAttribute("xmlns", "urn:demo");

         Element IP = doc.createElementNS("urn:demo", "IP");

         IP.appendChild(doc.createTextNode("192.168.21.138"));
         signedAdress.appendChild(IP);
         sp.appendChild(signedAdress);
         sps.addSignatureProperty(sp);
         object.appendChild(sps.getElement());
         sig.appendObject(object);
      }

      {
         ObjectContainer object = new ObjectContainer(doc);

         object.setId("object-4");

         X509Data x509data = new X509Data(doc);

         x509data.add(new XMLX509SubjectName(doc, cert));
         x509data.add(new XMLX509IssuerSerial(doc, cert));
         x509data.add(new XMLX509Certificate(doc, cert));
         object.appendChild(x509data.getElement());
         sig.appendObject(object);
      }

      /*
       * Add References
       */
      sig.getSignedInfo()
         .addResourceResolver(new org.apache.xml.security.samples.utils.resolver
            .OfflineResolver());
      sig.addDocument("http://www.w3.org/TR/xml-stylesheet");

      {
         Transforms transforms = new Transforms(doc);

         transforms.addTransform(Transforms.TRANSFORM_BASE64_DECODE);
         sig.addDocument("http://xmldsig.pothole.com/xml-stylesheet.txt",
                         transforms, Constants.ALGO_ID_DIGEST_SHA1);
      }

      {
         Transforms transforms = new Transforms(doc);
         XPathContainer xpathC = new XPathContainer(doc);

         xpathC.setXPath("self::text()");
         transforms.addTransform(Transforms.TRANSFORM_XPATH,
                                 xpathC.getElementPlusReturns());
         sig.addDocument("#object-1", transforms,
                         Constants.ALGO_ID_DIGEST_SHA1, null,
                         "http://www.w3.org/2000/09/xmldsig#Object");
      }
      /*
      {
         Transforms transforms = new Transforms(doc);
         XPathContainer xpathC = new XPathContainer(doc);

         //J-
         xpathC.setXPathNamespaceContext("ds", Constants.SignatureSpecNS);
         xpathC.setXPath("\n"
          + " ancestor-or-self::ds:SignedInfo                    " + "\n"
          + "  and                                               " + "\n"
          + " count(ancestor-or-self::ds:Reference |             " + "\n"
          + "      here()/ancestor::ds:Reference[1]) >           " + "\n"
          + " count(ancestor-or-self::ds:Reference)              " + "\n"
          + "  or                                                " + "\n"
          + " count(ancestor-or-self::node() |                   " + "\n"
          + "      id('notaries')) =                             " + "\n"
          + " count(ancestor-or-self::node())                    " + "\n");
         //J+
         transforms.addTransform(Transforms.TRANSFORM_XPATH,
                                 xpathC.getElementPlusReturns());
         sig.addDocument("", transforms, Constants.ALGO_ID_DIGEST_SHA1, null,
                         "http://www.w3.org/2000/09/xmldsig#Object");
      }
      */

      {
         Transforms transforms = new Transforms(doc);

         transforms.addTransform(Transforms.TRANSFORM_BASE64_DECODE);
         sig.addDocument("#object-2", transforms,
                         Constants.ALGO_ID_DIGEST_SHA1, null,
                         "http://www.w3.org/2000/09/xmldsig#Object");
      }

      sig.addDocument("#manifest-1", null, Constants.ALGO_ID_DIGEST_SHA1, null,
                      "http://www.w3.org/2000/09/xmldsig#Manifest");
      sig.addDocument("#signature-properties-1", null,
                      Constants.ALGO_ID_DIGEST_SHA1, null,
                      "http://www.w3.org/2000/09/xmldsig#SignatureProperties");

      {
         Transforms transforms = new Transforms(doc);

         transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
         sig.addDocument("", transforms, Constants.ALGO_ID_DIGEST_SHA1);
      }

      {
         Transforms transforms = new Transforms(doc);

         transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
         transforms.addTransform(Transforms.TRANSFORM_C14N_WITH_COMMENTS);
         sig.addDocument("", transforms, Constants.ALGO_ID_DIGEST_SHA1);
      }

      {
         Transforms transforms = new Transforms(doc);

         transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
         sig.addDocument("#xpointer(/)", transforms,
                         Constants.ALGO_ID_DIGEST_SHA1);
      }

      {
         Transforms transforms = new Transforms(doc);

         transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
         transforms.addTransform(Transforms.TRANSFORM_C14N_WITH_COMMENTS);
         sig.addDocument("#xpointer(/)", transforms,
                         Constants.ALGO_ID_DIGEST_SHA1);
      }

      {
         sig.addDocument("#object-3", null, Constants.ALGO_ID_DIGEST_SHA1,
                         null, "http://www.w3.org/2000/09/xmldsig#Object");
      }

      {
         Transforms transforms = new Transforms(doc);

         transforms.addTransform(Transforms.TRANSFORM_C14N_WITH_COMMENTS);
         sig.addDocument("#object-3", transforms,
                         Constants.ALGO_ID_DIGEST_SHA1, null,
                         "http://www.w3.org/2000/09/xmldsig#Object");
      }

      {
         sig.addDocument("#xpointer(id('object-3'))", null,
                         Constants.ALGO_ID_DIGEST_SHA1, null,
                         "http://www.w3.org/2000/09/xmldsig#Object");
      }

      {
         Transforms transforms = new Transforms(doc);

         transforms.addTransform(Transforms.TRANSFORM_C14N_WITH_COMMENTS);
         sig.addDocument("#xpointer(id('object-3'))", transforms,
                         Constants.ALGO_ID_DIGEST_SHA1, null,
                         "http://www.w3.org/2000/09/xmldsig#Object");
      }

      {
         sig.addDocument("#manifest-reference-1", null,
                         Constants.ALGO_ID_DIGEST_SHA1, "reference-1",
                         "http://www.w3.org/2000/09/xmldsig#Reference");
      }

      {
         sig.addDocument("#reference-1", null,
                         Constants.ALGO_ID_DIGEST_SHA1, "reference-2",
                         "http://www.w3.org/2000/09/xmldsig#Reference");
      }

      {
         sig.addDocument("#reference-2", null,
                         Constants.ALGO_ID_DIGEST_SHA1, null,
                         "http://www.w3.org/2000/09/xmldsig#Reference");
      }

      /*
       * Add KeyInfo and sign()
       */
      {
         Transforms retrievalTransforms = new Transforms(doc);
         XPathContainer xpathC = new XPathContainer(doc);

         xpathC.setXPathNamespaceContext("ds", Constants.SignatureSpecNS);
         xpathC.setXPath("ancestor-or-self::ds:X509Data");
         retrievalTransforms.addTransform(Transforms.TRANSFORM_XPATH,
                                          xpathC.getElement());
         sig.getKeyInfo().add(
            new RetrievalMethod(
               doc, "#object-4", retrievalTransforms,
               "http://www.w3.org/2000/09/xmldsig#X509Data"));

         /*
         X509Data x509data = new X509Data(doc);

         x509data.add(new XMLX509SubjectName(doc, cert));
         x509data.add(new XMLX509IssuerSerial(doc, cert));
         x509data.add(new XMLX509Certificate(doc, cert));
         sig.getKeyInfo().add(x509data);
         */

         System.out.println("Start signing");
         sig.sign(privateKey);
         System.out.println("Finished signing");
      }

      FileOutputStream f = new FileOutputStream(signatureFile);

      XMLUtils.outputDOMc14nWithComments(doc, f);
      f.close();
      System.out.println("Wrote signature to " + BaseURI);

      SignedInfo s = sig.getSignedInfo();
      for (int i=0; i<s.getSignedContentLength(); i++) {
         if (s.item(i).getType().equals(Reference.MANIFEST_URI)) {
            System.out.println("################ Signed Manifest " + i + " ################");
         } else {
            System.out.println("################ Signed Resource " + i + " ################");
         }
         System.out.println(new String(s.getSignedContentItem(i)));
         System.out.println();
      }
   }

   /**
    * Method createObject4
    *
    * @param sig
    * @return
    * @throws Exception
    */
   public static Element createObject4(XMLSignature sig) throws Exception {

      Document doc = sig.getElement().getOwnerDocument();
      String BaseURI = sig.getBaseURI();
      Manifest manifest = new Manifest(doc);
      manifest.addResourceResolver(new OfflineResolver());

      manifest.setId("manifest-1");
      manifest.addDocument(BaseURI, "http://www.w3.org/TR/xml-stylesheet",
                           null, Constants.ALGO_ID_DIGEST_SHA1,
                           "manifest-reference-1", null);
      manifest.addDocument(BaseURI, "#reference-1", null,
                           Constants.ALGO_ID_DIGEST_SHA1, null,
                           "http://www.w3.org/2000/09/xmldsig#Reference");

      //J-
      String xslt = ""
      + "<xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform'\n"
      + "                xmlns='http://www.w3.org/TR/xhtml1/strict' \n"
      + "                exclude-result-prefixes='foo' \n"
      + "                version='1.0'>\n"
      + "  <xsl:output encoding='UTF-8' \n"
      + "              indent='no' \n"
      + "              method='xml' />\n"
      + "  <xsl:template match='/'>\n"
      + "    <html>\n"
      + "      <head>\n"
      + "        <title>Notaries</title>\n"
      + "      </head>\n"
      + "      <body>\n"
      + "        <table>\n"
      + "          <xsl:for-each select='Notaries/Notary'>\n"
      + "            <tr>\n"
      + "              <th>\n"
      + "                <xsl:value-of select='@name' />\n"
      + "              </th>\n"
      + "            </tr>\n"
      + "          </xsl:for-each>\n"
      + "        </table>\n"
      + "      </body>\n"
      + "    </html>\n"
      + "  </xsl:template>\n"
      + "</xsl:stylesheet>\n"
      ;
      //J+
      javax.xml.parsers.DocumentBuilderFactory dbf =
         javax.xml.parsers.DocumentBuilderFactory.newInstance();

      dbf.setNamespaceAware(true);

      javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
      org.w3c.dom.Document docxslt =
         db.parse(new ByteArrayInputStream(xslt.getBytes()));
      Node xslElem = docxslt.getDocumentElement();
      Node xslElemImported = doc.importNode(xslElem, true);
      Transforms transforms = new Transforms(doc);

      transforms.addTransform(Transforms.TRANSFORM_XSLT,
                              (Element) xslElemImported);
      manifest.addDocument(BaseURI, "#notaries", transforms,
                           Constants.ALGO_ID_DIGEST_SHA1, null, null);

      manifest.generateDigestValues();
      return manifest.getElement();
   }

   static {
      org.apache.xml.security.Init.init();
   }
}
