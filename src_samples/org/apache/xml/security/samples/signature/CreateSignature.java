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
 *
 * @author $Author$
 */
public class CreateSignature {

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
      Constants.setSignatureSpecNSprefix("");

      //J-
      //All the parameters for the keystore
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

      //load the keystore
      ks.load(fis, keystorePass.toCharArray());

      //get the private key for signing.
      PrivateKey privateKey = (PrivateKey) ks.getKey(privateKeyAlias,
                                             privateKeyPass.toCharArray());
      javax.xml.parsers.DocumentBuilderFactory dbf =
         javax.xml.parsers.DocumentBuilderFactory.newInstance();

      //XML Signature needs to be namespace aware
      dbf.setNamespaceAware(true);

      javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
      org.w3c.dom.Document doc = db.newDocument();

      //Build a sample document. It will look something like:
      //<!-- Comment before -->
      //<apache:RootElement xmlns:apache="http://www.apache.org/ns/#app1">Some simple text
      //</apache:RootElement>
      //<!-- Comment after -->
      doc.appendChild(doc.createComment(" Comment before "));

      Element root = doc.createElementNS("http://www.apache.org/ns/#app1",
                                         "apache:RootElement");

      root.setAttributeNS(null, "attr1", "test1");
      root.setAttributeNS(null, "attr2", "test2");
      root.setAttributeNS(Constants.NamespaceSpecNS, "xmlns:foo", "http://example.org/#foo");
      root.setAttributeNS("http://example.org/#foo", "foo:attr1", "foo's test");



      root.setAttributeNS(Constants.NamespaceSpecNS, "xmlns:apache", "http://www.apache.org/ns/#app1");
      doc.appendChild(root);
      root.appendChild(doc.createTextNode("Some simple text\n"));

      //The BaseURI is the URI that's used to prepend to relative URIs
      String BaseURI = signatureFile.toURL().toString();
      //Create an XML Signature object from the document, BaseURI and
      //signature algorithm (in this case DSA)
      XMLSignature sig = new XMLSignature(doc, BaseURI,
                                          XMLSignature.ALGO_ID_SIGNATURE_DSA);

      //Append the signature element to the root element before signing because
      //this is going to be an enveloped signature.
      //This means the signature is going to be enveloped by the document.
      //Two other possible forms are enveloping where the document is inside the
      //signature and detached where they are seperate.
      //Note that they can be mixed in 1 signature with seperate references as
      //shown below.
      root.appendChild(sig.getElement());
      doc.appendChild(doc.createComment(" Comment after "));
      sig.getSignedInfo()
         .addResourceResolver(new org.apache.xml.security.samples.utils.resolver
            .OfflineResolver());

      {
         //create the transforms object for the Document/Reference
         Transforms transforms = new Transforms(doc);

         //First we have to strip away the signature element (it's not part of the
         //signature calculations). The enveloped transform can be used for this.
         transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
         //Part of the signature element needs to be canonicalized. It is a kind
         //of normalizing algorithm for XML. For more information please take a
         //look at the W3C XML Digital Signature webpage.
         transforms.addTransform(Transforms.TRANSFORM_C14N_WITH_COMMENTS);
         //Add the above Document/Reference
         sig.addDocument("", transforms, Constants.ALGO_ID_DIGEST_SHA1);
      }

      {
         //Add in 2 external URIs. This is a detached Reference.
         //
         // When sign() is called, two network connections are made. -- well,
         // not really, as we use the OfflineResolver which acts as a proxy for
         // these two resouces ;-))
         //
         sig.addDocument("http://www.w3.org/TR/xml-stylesheet");
         sig.addDocument("http://www.nue.et-inf.uni-siegen.de/index.html");
      }

      {
         //Add in the KeyInfo for the certificate that we used the private key of
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
