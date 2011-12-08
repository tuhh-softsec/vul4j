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
package org.apache.xml.security.samples.transforms;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.transforms.params.XPath2FilterContainer;
import org.apache.xml.security.transforms.params.XPathFilterCHGPContainer;
import org.apache.xml.security.utils.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Sample for the <I>XML Signature XPath Filter v2.0</I>
 *
 * @author $Author$
 * @see <A HREF="http://www.w3.org/TR/xmldsig-filter2/">XPath Filter v2.0 (TR)</A>
 * @see <A HREF=http://www.w3.org/Signature/Drafts/xmldsig-xfilter2/">XPath Filter v2.0 (editors copy)</A>
 */
public class SampleTransformXPathFilterCHGP {

    /**
     * Method main
     *
     * @param args
     * @throws Exception
     */
    public static void main(String args[]) throws Exception {

        org.apache.xml.security.Init.init();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        dbf.setNamespaceAware(true);

        DocumentBuilder db = dbf.newDocumentBuilder();

        // String inputDoc = "<A><U><B><S><S><S></S><S><S></S><S></S></S><S></S></S><S><S></S></S></S><C><U><U><U><U></U><U><U></U><U></U></U><U></U></U><U><U></U></U></U></U></C></B><D><U></U></D><U><E><S><S><S></S><S><S></S><S></S></S><S></S></S><S><S></S></S></S></E><U><F><G><H/></G></F></U></U></U></A>";
        // String inputDoc = "<A><U><B><S></S><C><U></U></C></B><D><U></U></D><U><E><S></S></E><U><F><G><H/></G></F></U></U></U></A>";
        String inputDoc =
            "<A xmlns:foo=\"http://foo.bar/\">\n<U>\n<U>\n<U>\n<U>\n<U>\n<B foo:attr=\"attr\">\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<C>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n</U>\n</U>\n</U>\n</C>\n</B>\n<D>\n<U/>\n</D>\n<U>\n<E>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n</E>\n<U>\n<F>\n<G>\n<H/>\n<G>\n<H/>\n</G>\n</G>\n</F>\n</U>\n</U>\n</U>\n</U>\n</U>\n</U>\n</U>\n</A>\n";
        Document doc = db.parse(new ByteArrayInputStream(inputDoc.getBytes()));

        SampleTransformXPathFilterCHGP.outApache(doc);

        doc = db.parse(new ByteArrayInputStream(inputDoc.getBytes()));

        SampleTransformXPathFilterCHGP.outXFilter2(doc);
    }

    /**
     * Method outApache
     *
     * @param doc
     * @throws Exception
     */
    static void outApache(Document doc) throws Exception {

        XMLSignature sig = new XMLSignature(doc, null, XMLSignature.ALGO_ID_MAC_HMAC_SHA1);

        doc.getDocumentElement().appendChild(sig.getElement());

        Transforms transforms = new Transforms(doc);
        String includeButSearchStr = "//B | //E | //F | //H";
        String excludeButSearchStr = "//G";

        // String excludeStr = "//C | //D | //ds:Signature";
        String excludeStr =
            "//C | //D | here()/ancestor::ds:Signature[1] | //@x:attr";
        boolean includeSlashPolicy = XPathFilterCHGPContainer.ExcludeSlash;
        XPathFilterCHGPContainer xpathContainer =
            XPathFilterCHGPContainer.getInstance(doc, includeSlashPolicy,
                                                 includeButSearchStr,
                                                 excludeButSearchStr, excludeStr);

        xpathContainer.setXPathNamespaceContext("ds", Constants.SignatureSpecNS);
        xpathContainer.setXPathNamespaceContext("x", "http://foo.bar/");
        transforms.addTransform(XPathFilterCHGPContainer.TRANSFORM_XPATHFILTERCHGP, xpathContainer.getElement());
        sig.addDocument("", transforms);

        String secretKey = "secret";

        sig.getKeyInfo().addKeyName("The UTF-8 octets of \"" + secretKey
                                    + "\" are used for signing (" + secretKey.length() + " octets)");
        sig.sign(sig.createSecretKey(secretKey.getBytes()));

        Canonicalizer c14n =
            Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS);
        byte[] full = c14n.canonicalizeSubtree(doc);

        System.out.println(
            "-------------------------------------------------------------");
        System.out.println("The signed octets (output of the transforms) are ");
        System.out.println(
            "-------------------------------------------------------------");
        System.out.println(new String(sig.getSignedInfo().item(0).getTransformsOutput().getBytes()));
        System.out.println(
            "-------------------------------------------------------------");
        System.out.println("The document is ");
        System.out.println(
            "-------------------------------------------------------------");
        System.out.println(new String(full));
        System.out.println(
            "-------------------------------------------------------------");

        Element sE =
            (Element) doc.getElementsByTagNameNS(Constants.SignatureSpecNS,
                                                 Constants._TAG_SIGNATURE).item(0);
        XMLSignature sigVer = new XMLSignature(sE, null);
        boolean verify =
            sigVer.checkSignatureValue(sigVer.createSecretKey("secret".getBytes()));

        System.out.println("verify: " + verify);
    }

    /**
     * Method outXFilter2
     *
     * @param doc
     * @throws Exception
     */
    static void outXFilter2(Document doc) throws Exception {

        XMLSignature sig = new XMLSignature(doc, null, XMLSignature.ALGO_ID_MAC_HMAC_SHA1);

        doc.getDocumentElement().appendChild(sig.getElement());

        Transforms transforms = new Transforms(doc);

        transforms.addTransform(Transforms.TRANSFORM_XPATH2FILTER,
                                XPath2FilterContainer.newInstanceIntersect(doc, "//E").getElement());
        transforms.addTransform(Transforms.TRANSFORM_XPATH2FILTER,
                                XPath2FilterContainer.newInstanceUnion(doc, "//B").getElement());
        transforms.addTransform(Transforms.TRANSFORM_XPATH2FILTER,
                                XPath2FilterContainer.newInstanceSubtract(doc,  "//C").getElement());
        transforms.addTransform(Transforms.TRANSFORM_XPATH2FILTER,
                                XPath2FilterContainer.newInstanceUnion(doc, "//F").getElement());
        transforms.addTransform(Transforms.TRANSFORM_XPATH2FILTER,
                                XPath2FilterContainer.newInstanceSubtract(doc, "//G").getElement());
        transforms.addTransform(Transforms.TRANSFORM_XPATH2FILTER,
                                XPath2FilterContainer.newInstanceUnion(doc, "//H").getElement());
        transforms.addTransform(Transforms.TRANSFORM_XPATH2FILTER,
                                XPath2FilterContainer.newInstanceSubtract(doc, 
                                                                          "//@x:attr").getElement());
        transforms.setXPathNamespaceContext("xmlns:x", "http://foo.bar/");
        transforms.setXPathNamespaceContext(
            Transforms.getDefaultPrefix(
                Transforms.TRANSFORM_XPATH2FILTER),
                Transforms.TRANSFORM_XPATH2FILTER
            );
        sig.addDocument("", transforms);

        String secretKey = "secret";

        sig.getKeyInfo().addKeyName("The UTF-8 octets of \"" + secretKey
                                    + "\" are used for signing (" + secretKey.length() + " octets)");
        sig.sign(sig.createSecretKey(secretKey.getBytes()));

        Canonicalizer c14n =
            Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS);
        byte[] full = c14n.canonicalizeSubtree(doc);

        System.out.println(
            "-------------------------------------------------------------");
        System.out.println("The signed octets (output of the transforms) are ");
        System.out.println(
            "-------------------------------------------------------------");
        System.out.println(new String(sig.getSignedInfo().item(0).getTransformsOutput().getBytes()));
        System.out.println(
            "-------------------------------------------------------------");
        System.out.println("The document is ");
        System.out.println(
            "-------------------------------------------------------------");
        System.out.println(new String(full));
        System.out.println(
            "-------------------------------------------------------------");

        Element sE =
            (Element) doc.getElementsByTagNameNS(Constants.SignatureSpecNS,
                                                 Constants._TAG_SIGNATURE).item(0);
        XMLSignature sigVer = new XMLSignature(sE, null);
        boolean verify =
            sigVer.checkSignatureValue(sigVer.createSecretKey("secret".getBytes()));

        System.out.println("verify: " + verify);
    }

    /**
     * Method check
     *
     * @param filename
     * @throws Exception
     */
    public static void check(String filename) throws Exception {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        dbf.setNamespaceAware(true);

        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new FileInputStream(filename));
        NodeList sigs = doc.getElementsByTagNameNS(Constants.SignatureSpecNS, Constants._TAG_SIGNATURE);
        XMLSignature sig =
            new XMLSignature((Element) sigs.item(0), new File(filename).toURI().toURL().toString());
        boolean check =
            sig.checkSignatureValue(sig.createSecretKey("secret".getBytes()));

        System.out.println(
            "-------------------------------------------------------------");
        System.out.println("Verification of " + filename + ": " + check);
        System.out.println(
            "-------------------------------------------------------------");
        System.out.println(new String(sig.getSignedInfo().item(0).getTransformsOutput().getBytes()));
        System.out.println(
            "-------------------------------------------------------------");
    }
}
