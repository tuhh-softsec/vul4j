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
package org.apache.xml.security.test.dom;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilder;

import org.apache.xml.security.signature.ObjectContainer;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.transforms.params.InclusiveNamespaces;
import org.apache.xml.security.transforms.params.XPathContainer;
import org.apache.xml.security.utils.JavaUtils;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;


/**
 * Class CreateExclC14nInteropValues
 */
public class CreateExclC14nInteropValues {

    /**
     * Method main
     *
     * @param unused
     * @throws Exception
     */
    public static void main(String unused[]) throws Exception {

        org.apache.xml.security.Init.init();

        DocumentBuilder db = XMLUtils.createDocumentBuilder(false);
        Document doc = db.newDocument();
        String directory = "data/org/apache/xml/security/c14n/outExcl/";
        File signatureFile = new File(directory + "apacheSignature.xml");
        XMLSignature xmlSignature = new XMLSignature(doc,
                                                     signatureFile.toURI().toURL().toString(),
                                                     XMLSignature.ALGO_ID_MAC_HMAC_SHA1);

        doc.appendChild(xmlSignature.getElement());
        {
            // ref 0
            Transforms tf = new Transforms(doc);
            {
                XPathContainer xc = new XPathContainer(doc);

                xc.setXPath(
                "self::Parent or (parent::Parent and not(self::Child)) or self::GrandChild or parent::GrandChild");
                tf.addTransform(Transforms.TRANSFORM_XPATH, xc.getElement());
            }
            xmlSignature.addDocument("iaikTests.example1.xml", tf);
        }

        {
            // ref 1
            Transforms tf = new Transforms(doc);
            {
                XPathContainer xc = new XPathContainer(doc);

                xc.setXPath(
                "self::Parent or (parent::Parent and not(self::Child)) or self::GrandChild or parent::GrandChild");
                tf.addTransform(Transforms.TRANSFORM_XPATH, xc.getElement());
            }
            tf.addTransform(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
            xmlSignature.addDocument("iaikTests.example1.xml", tf);
        }

        {
            // ref 2
            Transforms tf = new Transforms(doc);
            {
                XPathContainer xc = new XPathContainer(doc);

                xc.setXPathNamespaceContext("xmlns:default", "http://example.org");
                xc.setXPath(
                "self::Parent or (parent::Parent and not(self::default:Child)) or self::GrandChild or parent::GrandChild");
                tf.addTransform(Transforms.TRANSFORM_XPATH, xc.getElement());
            }
            xmlSignature.addDocument("iaikTests.example2.xml", tf);
        }

        {
            // ref 3
            Transforms tf = new Transforms(doc);
            {
                XPathContainer xc = new XPathContainer(doc);

                xc.setXPathNamespaceContext("xmlns:default", "http://example.org");
                xc.setXPath(
                "self::Parent or (parent::Parent and not(self::default:Child)) or self::GrandChild or parent::GrandChild");
                tf.addTransform(Transforms.TRANSFORM_XPATH, xc.getElement());
            }
            tf.addTransform(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
            xmlSignature.addDocument("iaikTests.example2.xml", tf);
        }

        {
            // ref 4
            Transforms tf = new Transforms(doc);
            {
                XPathContainer xc = new XPathContainer(doc);

                xc.setXPathNamespaceContext("xmlns:default",
                "http://example.org/default");
                xc.setXPathNamespaceContext("xmlns:ns1", "http://example.org/ns1");
                xc.setXPath(
                "self::default:Parent or (parent::default:Parent and not(self::default:Child)) or self::ns1:GrandChild or parent::ns1:GrandChild or self::default:GrandChild or parent::default:GrandChild");
                tf.addTransform(Transforms.TRANSFORM_XPATH, xc.getElement());
            }

            xmlSignature.addDocument("iaikTests.example3.xml", tf);
        }

        {
            // ref 5
            Transforms tf = new Transforms(doc);
            {
                XPathContainer xc = new XPathContainer(doc);

                xc.setXPathNamespaceContext("xmlns:default",
                "http://example.org/default");
                xc.setXPathNamespaceContext("xmlns:ns1", "http://example.org/ns1");
                xc.setXPath(
                "self::default:Parent or (parent::default:Parent and not(self::default:Child)) or self::ns1:GrandChild or parent::ns1:GrandChild or self::default:GrandChild or parent::default:GrandChild");
                tf.addTransform(Transforms.TRANSFORM_XPATH, xc.getElement());
            }
            tf.addTransform(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
            xmlSignature.addDocument("iaikTests.example3.xml", tf);
        }

        {
            // ref 6
            Transforms tf = new Transforms(doc);
            {
                XPathContainer xc = new XPathContainer(doc);

                xc.setXPathNamespaceContext("xmlns:ns1", "http://example.org/ns1");
                xc.setXPath(
                "self::Parent or (parent::Parent and not(self::Child)) or self::ns1:GrandChild or parent::ns1:GrandChild");
                tf.addTransform(Transforms.TRANSFORM_XPATH, xc.getElement());
            }
            xmlSignature.addDocument("iaikTests.example4.xml", tf);
        }

        {
            // ref 7
            Transforms tf = new Transforms(doc);
            {
                XPathContainer xc = new XPathContainer(doc);

                xc.setXPathNamespaceContext("xmlns:ns1", "http://example.org/ns1");
                xc.setXPath(
                "self::Parent or (parent::Parent and not(self::Child)) or self::ns1:GrandChild or parent::ns1:GrandChild");
                tf.addTransform(Transforms.TRANSFORM_XPATH, xc.getElement());
            }
            {
                InclusiveNamespaces incNS = new InclusiveNamespaces(doc, "ns2");

                tf.addTransform(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS,
                                incNS.getElement());
            }
            xmlSignature.addDocument("iaikTests.example4.xml", tf);
        }

        {
            // ref 8
            ObjectContainer obj = new ObjectContainer(doc);
            String id = "object1";

            obj.setId(id);

            String xmlStr = "" + "<included    xml:lang='de'>" + "\n"
            + "<notIncluded xml:lang='de'>" + "\n"
            + "<notIncluded xml:lang='uk'>" + "\n"
            + "<included                 >" + "\n" + "</included>"
            + "\n" + "</notIncluded>" + "\n" + "</notIncluded>"
            + "\n" + "</included>";
            Document importDoc =
                db.parse(new ByteArrayInputStream(xmlStr.getBytes()));

            obj.getElement().appendChild(doc.createTextNode("\n"));
            obj.getElement()
            .appendChild(doc.importNode(importDoc.getDocumentElement(), true));
            obj.getElement().appendChild(doc.createTextNode("\n"));
            xmlSignature.appendObject(obj);

            // ref apache_8
            Transforms tf = new Transforms(doc);
            {
                XPathContainer xc = new XPathContainer(doc);

                xc.setXPath("self::node()[local-name()='included']");
                tf.addTransform(Transforms.TRANSFORM_XPATH, xc.getElement());
            }
            xmlSignature.addDocument("#" + id, tf);
        }

        {
            // ref 9
            ObjectContainer obj = new ObjectContainer(doc);
            String id = "object2";

            obj.setId(id);

            String xmlStr = "" + "<included    xml:lang='uk'>" + "\n"
            + "<notIncluded xml:lang='de'>" + "\n"
            + "<notIncluded xml:lang='uk'>" + "\n"
            + "<included                 >" + "\n" + "</included>"
            + "\n" + "</notIncluded>" + "\n" + "</notIncluded>"
            + "\n" + "</included>";
            Document importDoc =
                db.parse(new ByteArrayInputStream(xmlStr.getBytes()));

            obj.getElement().appendChild(doc.createTextNode("\n"));
            obj.getElement()
            .appendChild(doc.importNode(importDoc.getDocumentElement(), true));
            obj.getElement().appendChild(doc.createTextNode("\n"));
            xmlSignature.appendObject(obj);

            // ref apache_8
            Transforms tf = new Transforms(doc);
            {
                XPathContainer xc = new XPathContainer(doc);

                xc.setXPath("self::node()[local-name()='included']");
                tf.addTransform(Transforms.TRANSFORM_XPATH, xc.getElement());
            }
            xmlSignature.addDocument("#" + id, tf);
        }

        {
            // ref 10
            ObjectContainer obj = new ObjectContainer(doc);
            String id = "object3";

            obj.setId(id);

            String xmlStr = "" + "<included    xml:lang='de'>" + "\n"
            + "<notIncluded xml:lang='de'>" + "\n"
            + "<notIncluded xml:lang='uk'>" + "\n"
            + "<included    xml:lang='de'>" + "\n" + "</included>"
            + "\n" + "</notIncluded>" + "\n" + "</notIncluded>"
            + "\n" + "</included>";
            Document importDoc =
                db.parse(new ByteArrayInputStream(xmlStr.getBytes()));

            obj.getElement().appendChild(doc.createTextNode("\n"));
            obj.getElement()
            .appendChild(doc.importNode(importDoc.getDocumentElement(), true));
            obj.getElement().appendChild(doc.createTextNode("\n"));
            xmlSignature.appendObject(obj);

            // ref apache_8
            Transforms tf = new Transforms(doc);
            {
                XPathContainer xc = new XPathContainer(doc);

                xc.setXPath("self::node()[local-name()='included']");
                tf.addTransform(Transforms.TRANSFORM_XPATH, xc.getElement());
            }

            xmlSignature.addDocument("#" + id, tf);
        }

        {
            // ref 11
            ObjectContainer obj = new ObjectContainer(doc);
            String id = "object4";

            obj.setId(id);

            String xmlStr = "" + "<included    xml:lang='de'>" + "\n"
            + "<included    xml:lang='de'>" + "\n"
            + "<notIncluded xml:lang='uk'>" + "\n"
            + "<included                 >" + "\n" + "</included>"
            + "\n" + "</notIncluded>" + "\n" + "</included>"
            + "\n" + "</included>";
            Document importDoc =
                db.parse(new ByteArrayInputStream(xmlStr.getBytes()));

            obj.getElement().appendChild(doc.createTextNode("\n"));
            obj.getElement()
            .appendChild(doc.importNode(importDoc.getDocumentElement(), true));
            obj.getElement().appendChild(doc.createTextNode("\n"));
            xmlSignature.appendObject(obj);

            Transforms tf = new Transforms(doc);
            {
                XPathContainer xc = new XPathContainer(doc);

                xc.setXPath("self::node()[local-name()='included']");
                tf.addTransform(Transforms.TRANSFORM_XPATH, xc.getElement());
            }

            xmlSignature.addDocument("#" + id, tf);
        }

        {
            // ref 12
            ObjectContainer obj = new ObjectContainer(doc);
            String id = "object5";

            obj.setId(id);

            String xmlStr = "" + "<included                         xml:lang='de'>"
            + "\n"
            + "<included                         xml:lang='de'>"
            + "\n"
            + "<notIncluded xml:space='preserve' xml:lang='uk'>"
            + "\n" + "<included                 >" + "\n"
            + "</included>" + "\n" + "</notIncluded>" + "\n"
            + "</included>" + "\n" + "</included>";
            Document importDoc =
                db.parse(new ByteArrayInputStream(xmlStr.getBytes()));

            obj.getElement().appendChild(doc.createTextNode("\n"));
            obj.getElement()
            .appendChild(doc.importNode(importDoc.getDocumentElement(), true));
            obj.getElement().appendChild(doc.createTextNode("\n"));
            xmlSignature.appendObject(obj);

            Transforms tf = new Transforms(doc);
            {
                XPathContainer xc = new XPathContainer(doc);

                xc.setXPath("self::node()[local-name()='included']");
                tf.addTransform(Transforms.TRANSFORM_XPATH, xc.getElement());
            }

            xmlSignature.addDocument("#" + id, tf);
        }

        {
            // ref 13
            ObjectContainer obj = new ObjectContainer(doc);
            String id = "object6";

            obj.setId(id);

            String xmlStr = "" + "<included   xml:space='preserve'  xml:lang='de'>"
            + "\n"
            + "<included                         xml:lang='de'>"
            + "\n"
            + "<notIncluded                      xml:lang='uk'>"
            + "\n" + "<included>" + "\n" + "</included>" + "\n"
            + "</notIncluded>" + "\n" + "</included>" + "\n"
            + "</included>";
            Document importDoc =
                db.parse(new ByteArrayInputStream(xmlStr.getBytes()));

            obj.getElement().appendChild(doc.createTextNode("\n"));
            obj.getElement()
            .appendChild(doc.importNode(importDoc.getDocumentElement(), true));
            obj.getElement().appendChild(doc.createTextNode("\n"));
            xmlSignature.appendObject(obj);

            Transforms tf = new Transforms(doc);
            {
                XPathContainer xc = new XPathContainer(doc);

                xc.setXPath("self::node()[local-name()='included']");
                tf.addTransform(Transforms.TRANSFORM_XPATH, xc.getElement());
            }

            xmlSignature.addDocument("#" + id, tf);
        }

        {
            // ref 13b
            String id = "object6";
            Transforms tf = new Transforms(doc);
            {
                XPathContainer xc = new XPathContainer(doc);

                xc.setXPath("self::node()[local-name()='included']");
                tf.addTransform(Transforms.TRANSFORM_XPATH, xc.getElement());
                tf.addTransform(Transforms.TRANSFORM_C14N_OMIT_COMMENTS);
            }
            xmlSignature.addDocument("#" + id, tf);
        }

        {
            // ref 13c
            String id = "object6";
            Transforms tf = new Transforms(doc);

            {
                XPathContainer xc = new XPathContainer(doc);

                xc.setXPath("self::node()[local-name()='included']");
                tf.addTransform(Transforms.TRANSFORM_XPATH, xc.getElement());
                tf.addTransform(Transforms.TRANSFORM_C14N_OMIT_COMMENTS);
                tf.addTransform(Transforms.TRANSFORM_C14N_OMIT_COMMENTS);
            }
            xmlSignature.addDocument("#" + id, tf);
            // xmlSignature.addDocument("#" + id, tf, org.apache.xml.security.algorithms.MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA1, "ref13c", null);
        }

        String secretKey = "secret";

        xmlSignature.getKeyInfo().addKeyName("The UTF-8 octets of \"" + secretKey
                                             + "\" are used for signing ("
                                             + secretKey.length() + " octets)");
        xmlSignature.sign(xmlSignature.createSecretKey(secretKey.getBytes()));

        FileOutputStream fos = new FileOutputStream(signatureFile);

        XMLUtils.outputDOM(doc, fos);
        fos.close();

        int length = xmlSignature.getSignedInfo().getLength();

        for (int i = 0; i < length; i++) {
            String fname = directory + "c14n-" + i + "-apache.xml";

            System.out.println(fname);
            JavaUtils.writeBytesToFilename(fname, xmlSignature.getSignedInfo().getReferencedContentAfterTransformsItem(i).getBytes());
        }

        XMLSignature s = new XMLSignature(doc.getDocumentElement(),
                                          signatureFile.toURI().toURL().toString());
        boolean verify =
            s.checkSignatureValue(s.createSecretKey("secret".getBytes()));

        System.out.println("verify=" + verify);

        System.out.println("");

        XMLUtils.outputDOMc14nWithComments(doc, System.out);
    }
}
