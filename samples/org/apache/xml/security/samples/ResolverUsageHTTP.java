
/*
 * Copyright  1999-2004 The Apache Software Foundation.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.xml.security.samples;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.xml.security.samples.SampleUtils;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.utils.resolver.ResourceResolver;
import org.w3c.dom.Element;

/**
 *
 * @author $Author$
 */
public class ResolverUsageHTTP {

    /**
     * Method main
     *
     * @param unused
     * @throws Exception
     */
    public static void main(String unused[]) throws Exception {

        ResourceResolver.registerAtStart(
            "org.apache.xml.security.samples.utils.resolver.OfflineResolver");

        String currentSystemId = "file:/Y:/signature.xml";
        // String refURI =          "#N3";
        String refURI =          "#xpointer(id(&quot;id2&quot;))";
        String inputStr =
            "<?xml version='1.0'?>" + "\n"
            + "<!DOCTYPE doc [" + "\n"
            + "<!ATTLIST e9 Id ID #IMPLIED>" + "\n"
            + "]>" + "\n"
            + "<doc>" + "\n"
            + "   <!-- A comment -->" + "\n"
            + "   <Signature xmlns='http://www.w3.org/2000/09/xmldsig#'>" + "\n"
            + "    <SignedInfo>" + "\n"
            + "      <CanonicalizationMethod Algorithm='http://www.w3.org/TR/2001/REC-xml-c14n-20010315' />" + "\n"
            + "      <SignatureMethod Algorithm='http://www.w3.org/2000/09/xmldsig#rsa-sha1' />" + "\n"
            + "      <Reference URI='http://www.w3.org/TR/xml-stylesheet'>" + "\n"
            + "         <DigestMethod Algorithm='http://www.w3.org/2000/09/xmldsig#sha1' />" + "\n"
            + "         <DigestValue>60NvZvtdTB+7UnlLp/H24p7h4bs=</DigestValue>" + "\n"
            + "      </Reference>" + "\n"
            + "      <Reference URI='#xpointer(id(&quot;id2&quot;))'>" + "\n"
            + "         <DigestMethod Algorithm='http://www.w3.org/2000/09/xmldsig#sha1' />" + "\n"
            + "         <DigestValue>RJeREVHXdM5ysghhvpIYGJJaNQI=</DigestValue>" + "\n"
            + "      </Reference>" + "\n"
            + "      <Reference URI='http://www.nue.et-inf.uni-siegen.de/index.html'>" + "\n"
            + "         <DigestMethod Algorithm='http://www.w3.org/2000/09/xmldsig#sha1' />" + "\n"
            + "         <DigestValue>Hpg+6h1k1jYY5yr3TRzDZzw23CQ=</DigestValue>" + "\n"
            // + "         <DigestValue>RJeREVHXdM5ysghhvpIYGJJaNQI=</DigestValue>" + "\n"
            + "      </Reference>" + "\n"
            // + "      <Reference URI='file:/Y:/jbproject/xmlsecurity/data/org.apache.xml.security/temp/id2.xml#xpointer(id(&quot;id2&quot;))' xmlns='http://www.w3.org/2000/09/xmldsig#'>" + "\n"
            + "      <Reference URI='http://www.nue.et-inf.uni-siegen.de/~geuer-pollmann/id2.xml'>" + "\n"
            + "         <Transforms>" + "\n"
            + "            <Transform Algorithm='http://www.w3.org/TR/1999/REC-xpath-19991116'>" + "\n"
            + "               <XPath>self::text()</XPath>" + "\n"
            + "            </Transform>" + "\n"
            + "         </Transforms>" + "\n"
            + "         <DigestMethod Algorithm='http://www.w3.org/2000/09/xmldsig#sha1' />" + "\n"
            + "         <DigestValue>RK9DKU4NnECPpNAb+QxMwTmSL+w=</DigestValue>" + "\n"
            + "      </Reference>" + "\n"
            + "    </SignedInfo>" + "\n"
            + "    <SignatureValue>" + "\n"
            + "       KTe1H5Hjp8hwahNFoUqHDuPJNNqhS1U3BBBH5/gByItNIwV18nMiLq4KunzFnOqD" + "\n"
            + "       xzTuO0/T+wsoYC1xOEuCDxyIujNCaJfLh+rCi5THulnc8KSHHEoPQ+7fA1VjmO31" + "\n"
            + "       2iw1iENOi7m//wzKlIHuxZCJ5nvolT21PV6nSE4DHlA=" + "\n"
            + "    </SignatureValue>" + "\n"
            + "   </Signature>" + "\n"
            + "   <e9 Id='N3'><!-- A comment -->Das N3 Element</e9>" + "\n"
            + "   <e9 Id='id2'><!-- A comment --> Das id2 Element</e9>" + "\n"
            + "</doc> " + "\n"
            + "";
        javax.xml.parsers.DocumentBuilderFactory dbf =
            javax.xml.parsers.DocumentBuilderFactory.newInstance();

        dbf.setNamespaceAware(true);

        javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
        org.w3c.dom.Document doc =
            db.parse(new java.io.ByteArrayInputStream(inputStr.getBytes()));

        XPathFactory xpf = XPathFactory.newInstance();
        XPath xpath = xpf.newXPath();
        xpath.setNamespaceContext(new DSNamespaceContext());

        String expression = "//ds:Signature[1]";
        Element sigElement = 
            (Element) xpath.evaluate(expression, doc, XPathConstants.NODE);
        
        XMLSignature signature = 
            new XMLSignature(sigElement, currentSystemId);

        // how can I reg my own keystore?
        boolean verify = signature.checkSignatureValue(signature.getKeyInfo().getPublicKey());

        System.out.println("Signature " + (verify
            ? "Verification successful"
                : "Verification failed"));
        System.out.println("Canonicalized SignedInfo:");
        System.out.println(new String(signature.getSignedInfo().getCanonicalizedOctetStream()));
    }
}
