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
package org.apache.xml.security.samples;



import java.io.*;
import java.security.*;
import java.security.cert.*;
import java.util.*;
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
public class AxisSigner {

   /** Field AXIS_SIGNATURE_FILENAME           */
   public static final String AXIS_SIGNATURE_FILENAME = "axisSignature.xml";

   /**
    * Method main
    *
    * @param unused
    * @throws Exception
    */
   public static void main(String unused[]) throws Exception {

      org.apache.xml.security.Init.init();

      //J-
      String keystoreType = "JKS";
      String keystoreFile = "data/org/apache/xml/security/samples/input/keystore.jks";
      String keystorePass = "xmlsecurity";
      String privateKeyAlias = "test";
      String privateKeyPass = "xmlsecurity";
      String certificateAlias = "test";
      File signatureFile = new File(AXIS_SIGNATURE_FILENAME);
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

      /*
       * Start SOAP infrastructure code. This is to be made compatible with Axis.
       *
       */
      String soapNS = "http://www.w3.org/2001/12/soap-envelope";
      String env = "env";
      String envPrefix = env + ":";
      Element envelopeElement = doc.createElementNS(soapNS,
                                   envPrefix + "Envelope");

      envelopeElement.setAttribute("xmlns:" + env, soapNS);
      doc.appendChild(envelopeElement);

      Element headerElem = doc.createElementNS(soapNS, envPrefix + "Header");
      Element bodyElem = doc.createElementNS(soapNS, envPrefix + "Body");

      envelopeElement.appendChild(doc.createTextNode("\n"));
      envelopeElement.appendChild(headerElem);
      envelopeElement.appendChild(doc.createTextNode("\n"));
      envelopeElement.appendChild(bodyElem);
      envelopeElement.appendChild(doc.createTextNode("\n"));
      bodyElem
         .appendChild(doc
            .createTextNode("This is signed together with it's Body ancestor"));

      String SOAPSECNS = "http://schemas.xmlsoap.org/soap/security/2000-12";
      String SOAPSECprefix = "SOAP-SEC";

      bodyElem.setAttributeNS(SOAPSECNS, SOAPSECprefix + ":" + "id", "Body");

      Element soapSignatureElem = doc.createElementNS(SOAPSECNS,
                                     SOAPSECprefix + ":" + "Signature");

      envelopeElement.setAttribute("xmlns:" + SOAPSECprefix, SOAPSECNS);
      envelopeElement.setAttribute(env + ":" + "actor", "some-uri");
      envelopeElement.setAttribute(env + ":" + "mustUnderstand", "1");
      envelopeElement.appendChild(doc.createTextNode("\n"));
      headerElem.appendChild(soapSignatureElem);

      /*
       *
       * End SOAP infrastructure code. This is to be made compatible with Axis.
       */
      String BaseURI = signatureFile.toURL().toString();
      XMLSignature sig = new XMLSignature(doc, BaseURI,
                                          XMLSignature.ALGO_ID_SIGNATURE_DSA);

      soapSignatureElem.appendChild(sig.getElement());

      {
         // sig.addDocument("#Body");
         Transforms transforms = new Transforms(doc);
         transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
         sig.addDocument("", transforms);
      }

      {
         X509Certificate cert =
            (X509Certificate) ks.getCertificate(certificateAlias);

         sig.addKeyInfo(cert);
         sig.addKeyInfo(cert.getPublicKey());
         sig.sign(privateKey);
      }

      FileOutputStream f = new FileOutputStream(signatureFile);

      XMLUtils.outputDOMc14nWithComments(doc, f);
      f.close();
      System.out.println("Wrote signature to " + BaseURI);

      for (int i = 0; i < sig.getSignedInfo().getSignedContentLength(); i++) {
         System.out.println("--- Signed Content follows ---");
         System.out
            .println(new String(sig.getSignedInfo().getSignedContentItem(i)));
      }
   }
}
