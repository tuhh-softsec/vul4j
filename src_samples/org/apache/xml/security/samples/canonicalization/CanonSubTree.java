
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
package org.apache.xml.security.samples.canonicalization;



import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author Christian Geuer-Pollmann
 */
public class CanonSubTree {
   //J-
   static String input = ""
      + "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
      + "<Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\">\n"
      + "  <SignedInfo><!-- comment inside -->\n"
      + "    <CanonicalizationMethod Algorithm=\"http://www.w3.org/TR/2001/REC-xml-c14n-20010315\" />\n"
      + "    <SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\" />\n"
      + "    <Reference URI=\"http://www.w3.org/TR/xml-stylesheet\">\n"
      + "      <DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\" />\n"
      + "      <DigestValue>60NvZvtdTB+7UnlLp/H24p7h4bs=</DigestValue>\n"
      + "    </Reference>\n"
      + "  </SignedInfo>\n"
      + "  <SignatureValue>\n"
      + "    fKMmy9GYF2s8rLFrZdVugTOFuWx19ccX7jh5HqFd4vMOY7LWAj52ykjSdvtW3fNY\n"
      + "    PPYGC4MFL19oPSId5GEsMtFMpGXB3XaCtoKjMCHQsN3+kom8YnGf7Ge1JNRcGty5\n"
      + "    0UsoP6Asj47+QR7QECT64uoziha4WRDVyXjDrg24W+U=\n"
      + "  </SignatureValue>\n"
      + "  <KeyInfo>\n"
      + "    <KeyName>Lugh</KeyName>\n"
      + "  </KeyInfo>\n"
      + "</Signature>\n"
      ;
   //J+

   /**
    * Method main
    *
    * @param args
    * @throws Exception
    */
   public static void main(String args[]) throws Exception {
      org.apache.xml.security.Init.init();

      DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();

      dfactory.setNamespaceAware(true);
      dfactory.setValidating(true);

      DocumentBuilder documentBuilder = dfactory.newDocumentBuilder();

      // this is to throw away all validation warnings
      documentBuilder
         .setErrorHandler(new org.apache.xml.security.utils
            .IgnoreAllErrorHandler());

      byte inputBytes[] = input.getBytes();
      Document doc =
         documentBuilder.parse(new ByteArrayInputStream(inputBytes));
      Canonicalizer c14n =
         Canonicalizer
            .getInstance("http://www.w3.org/TR/2001/REC-xml-c14n-20010315");
      Element nscontext = XMLUtils.createDSctx(doc, "ds", Constants.SignatureSpecNS);

      Node signedInfo = XPathAPI.selectSingleNode(doc, "//ds:SignedInfo",
                                                  nscontext);
      byte outputBytes[] = c14n.canonicalizeSubtree(signedInfo);

      if (outputBytes != null) {
         System.out.println(new String(outputBytes));
      }
   }
}
