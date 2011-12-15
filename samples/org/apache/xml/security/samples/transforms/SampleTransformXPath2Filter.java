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
import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.transforms.params.XPath2FilterContainer;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.JavaUtils;
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
public class SampleTransformXPath2Filter {

    /**
     * Method main
     *
     * @param args
     * @throws Exception
     */
    public static void main(String args[]) throws Exception {

        org.apache.xml.security.Init.init();

        boolean verbose = true;

        create("build/withComments.xml", true, verbose);
        System.out.println();
        System.out.println();
        System.out.println();
        create("build/omitComments.xml", false, verbose);
        System.out.println();
        System.out.println();
        System.out.println();
        check("build/withComments.xml");
    }

    /**
     * Method create
     *
     * @param filename
     * @param withComments
     * @param verbose
     * @throws Exception
     */
    public static void create(
        String filename, boolean withComments, boolean verbose
    ) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        dbf.setNamespaceAware(true);

        DocumentBuilder db = dbf.newDocumentBuilder();
        String inputDoc =
            "<Document>\n" +
            "     <ToBeSigned>\n" +
            "       <!-- comment -->\n" +
            "       <Data />\n" +
            "       <NotToBeSigned>\n" +
            "         <ReallyToBeSigned>\n" +
            "           <!-- comment -->\n" +
            "           <Data />\n" +
            "         </ReallyToBeSigned>\n" +
            "       </NotToBeSigned>\n" +
            "     </ToBeSigned>\n" +
            "     <ToBeSigned>\n" +
            "       <Data />\n" +
            "       <NotToBeSigned>\n" +
            "         <Data />\n" +
            "       </NotToBeSigned>\n" +
            "     </ToBeSigned>\n" +
            "</Document>";
        Document doc = db.parse(new ByteArrayInputStream(inputDoc.getBytes()));
        XMLSignature sig = new XMLSignature(doc, null, XMLSignature.ALGO_ID_MAC_HMAC_SHA1);

        doc.getDocumentElement().appendChild(sig.getElement());
        doc.getDocumentElement().appendChild(doc.createTextNode("\n"));

        Transforms transforms = new Transforms(doc);

        String filters[][] = {{XPath2FilterContainer.INTERSECT, "//ToBeSigned"},
                              {XPath2FilterContainer.SUBTRACT,  "//NotToBeSigned"},
                              {XPath2FilterContainer.UNION,     "//ReallyToBeSigned"}};

        transforms.addTransform(
            Transforms.TRANSFORM_XPATH2FILTER, XPath2FilterContainer.newInstances(doc, filters)
        );
        if (withComments) {
            transforms.addTransform(Transforms.TRANSFORM_C14N_WITH_COMMENTS);
        }

        sig.addDocument("#xpointer(/)", transforms);

        String secretKey = "secret";

        sig.getKeyInfo().addKeyName("The UTF-8 octets of \"" + secretKey
                                    + "\" are used for signing (" + secretKey.length() + " octets)");
        sig.sign(sig.createSecretKey(secretKey.getBytes()));

        Canonicalizer c14n =
            Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS);
        byte[] full = c14n.canonicalizeSubtree(doc);
        FileOutputStream fos = new FileOutputStream(filename);

        try {
            fos.write(full);
        } finally {
            fos.close();
        }

        if (verbose) {
            System.out.println(
                "-------------------------------------------------------------");
            System.out.println("Input to the transforms is");
            System.out.println(
                "-------------------------------------------------------------");
            System.out.println(
                new String(sig.getSignedInfo().item(0).getContentsBeforeTransformation().getBytes())
            );
            System.out.println(
                "-------------------------------------------------------------");
            System.out.println("The signed octets (output of the transforms) are ");
            System.out.println(
                "-------------------------------------------------------------");
            System.out.println(
                new String(sig.getSignedInfo().item(0).getTransformsOutput().getBytes())
            );
            System.out.println(
                "-------------------------------------------------------------");
            System.out.println("The document is ");
            System.out.println(
                "-------------------------------------------------------------");
            System.out.println(new String(full));
            System.out.println(
                "-------------------------------------------------------------");
        }

        JavaUtils.writeBytesToFilename(
            "build/xfilter2.html", sig.getSignedInfo().item(0).getHTMLRepresentation().getBytes()
        );
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
            new XMLSignature((Element)sigs.item(0), new File(filename).toURI().toURL().toString());
        boolean check = sig.checkSignatureValue(sig.createSecretKey("secret".getBytes()));

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
