/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.xml.security.samples.signature;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import org.apache.xml.security.keys.content.RetrievalMethod;
import org.apache.xml.security.keys.content.X509Data;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.apache.xml.security.keys.content.x509.XMLX509IssuerSerial;
import org.apache.xml.security.keys.content.x509.XMLX509SubjectName;
import org.apache.xml.security.samples.utils.resolver.OfflineResolver;
import org.apache.xml.security.signature.Manifest;
import org.apache.xml.security.signature.ObjectContainer;
import org.apache.xml.security.signature.Reference;
import org.apache.xml.security.signature.SignatureProperties;
import org.apache.xml.security.signature.SignatureProperty;
import org.apache.xml.security.signature.SignedInfo;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.transforms.params.XPathContainer;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.ElementProxy;
import org.apache.xml.security.utils.JavaUtils;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author $Author$
 */
public class CreateMerlinsExampleSixteen {

    /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(CreateMerlinsExampleSixteen.class.getName());

    static {
        org.apache.xml.security.Init.init();
    }
    
    /**
     * Method main
     *
     * @param unused
     * @throws Exception
     */
    public static void main(String unused[]) throws Exception {
        ElementProxy.setDefaultPrefix(Constants.SignatureSpecNS, "ds");
        String keystoreType = "JKS";
        String keystoreFile = "samples/data/keystore.jks";
        String keystorePass = "xmlsecurity";
        String privateKeyAlias = "test";
        String privateKeyPass = "xmlsecurity";
        String certificateAlias = "test";
        File signatureFile = new File("build/merlinsSixteenRecreatedNoRetrievalMethod.xml");

        KeyStore ks = KeyStore.getInstance(keystoreType);
        FileInputStream fis = new FileInputStream(keystoreFile);

        ks.load(fis, keystorePass.toCharArray());

        PrivateKey privateKey = 
            (PrivateKey) ks.getKey(privateKeyAlias, privateKeyPass.toCharArray());

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
        Element envelope = doc.createElementNS("http://www.usps.gov/", "Envelope");

        envelope.setAttributeNS(Constants.NamespaceSpecNS, "xmlns", "http://www.usps.gov/");
        envelope.setAttributeNS(Constants.NamespaceSpecNS, "xmlns:foo", "http://www.usps.gov/foo");
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

        Element YoursSincerely = doc.createElementNS("http://www.usps.gov/", "YoursSincerely");
        YoursSincerely.appendChild(doc.createTextNode("\n"));

        envelope.appendChild(YoursSincerely);

        Element PostScript = doc.createElementNS("http://www.usps.gov/", "PostScript");

        PostScript.appendChild(doc.createTextNode("bar"));
        envelope.appendChild(PostScript);

        Element Notaries = doc.createElementNS(null, "Notaries");

        Notaries.setAttributeNS(Constants.NamespaceSpecNS, "xmlns", "");
        Notaries.setAttributeNS(null, "Id", "notaries");
        Notaries.setIdAttributeNS(null, "Id", true);

        {
            Element Notary = doc.createElementNS(null, "Notary");

            Notary.setAttributeNS(null, "name", "Great, A. T.");
            Notaries.appendChild(Notary);
        }

        {
            Element Notary = doc.createElementNS(null, "Notary");

            Notary.setAttributeNS(null, "name", "Hun, A. T.");
            Notaries.appendChild(Notary);
        }

        envelope.appendChild(Notaries);
        envelope.appendChild(doc.createComment(" Commentary "));

        //////////////////////////////////////////////////
        String BaseURI = signatureFile.toURI().toURL().toString();
        XMLSignature sig =
            new XMLSignature(doc, BaseURI, XMLSignature.ALGO_ID_SIGNATURE_DSA);

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

            Element nonc = doc.createElementNS(null, "NonCommentandus");

            nonc.setAttributeNS(Constants.NamespaceSpecNS, "xmlns", "");
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

            signedAdress.setAttributeNS(Constants.NamespaceSpecNS, "xmlns", "urn:demo");

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
        sig.getSignedInfo().addResourceResolver(
            new org.apache.xml.security.samples.utils.resolver.OfflineResolver());
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
            retrievalTransforms.addTransform(Transforms.TRANSFORM_XPATH, xpathC.getElement());
            sig.getKeyInfo().add(
                new RetrievalMethod(
                    doc, "#object-4", retrievalTransforms, "http://www.w3.org/2000/09/xmldsig#X509Data"));

            System.out.println("Start signing");
            sig.sign(privateKey);
            System.out.println("Finished signing");
        }

        FileOutputStream f = new FileOutputStream(signatureFile);

        XMLUtils.outputDOMc14nWithComments(doc, f);
        f.close();
        System.out.println("Wrote signature to " + BaseURI);

        SignedInfo s = sig.getSignedInfo();
        for (int i = 0; i < s.getLength(); i++) {
            Reference r = s.item(i);
            String fn = "build/merlin16_"+i+".html";
            System.out.println("Wrote Reference " + i + " to file " + fn);
            JavaUtils.writeBytesToFilename(fn, r.getHTMLRepresentation().getBytes());
        }

    }

    /**
     * Method createObject4
     *
     * @param sig
     *
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

        return manifest.getElement();
    }

}
