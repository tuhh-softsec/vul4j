
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
import java.net.*;
import org.apache.xml.utils.URI;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import org.apache.xml.security.c14n.*;
import org.apache.xml.security.signature.*;
import org.apache.xml.security.transforms.*;
import org.apache.xml.security.utils.*;
import org.apache.xml.security.utils.resolver.*;


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

      //J-
      // String currentSystemId = "http://www.nue.et-inf.uni-siegen.de/~geuer-pollmann/signature.xml";
      String currentSystemId = "file:/Y:/signature.xml";
      // String refURI =          "#N3";
      String refURI =          "#xpointer(id(&quot;id2&quot;))";
      // String refURI =          "file:/Y:/jbproject/xmlsecurity/data/org.apache.xml.security/temp/id.xml#N3";
      // String refURI =          "http://www.nue.et-inf.uni-siegen.de/index.html#xpointer(&apos;id3&apos;)";
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
      //J+
      javax.xml.parsers.DocumentBuilderFactory dbf =
         javax.xml.parsers.DocumentBuilderFactory.newInstance();

      dbf.setNamespaceAware(true);

      javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
      org.w3c.dom.Document doc =
         db.parse(new java.io.ByteArrayInputStream(inputStr.getBytes()));
      Element context = XMLUtils.createDSctx(doc, "ds", Constants.SignatureSpecNS);

      Element dsElem = (Element) XPathAPI.selectSingleNode(doc,
                          "//ds:Signature[1]", context);
      XMLSignature signature = new XMLSignature(dsElem, currentSystemId);

      // how can I reg my own keystore?
      boolean verify = signature.checkSignatureValue(signature.getKeyInfo().getPublicKey());

      System.out.println("Signature " + (verify
                                         ? "Verification successful"
                                         : "Verification failed"));
      System.out.println("Canonicalized SignedInfo:");
      System.out
         .println(new String(signature.getSignedInfo()
            .getCanonicalizedOctetStream()));

      /*
      Element signedInfoElem = (Element) XPathAPI.selectSingleNode(doc, "/doc/ds:Signature/ds:SignedInfo", context);
      SignedInfo si = new SignedInfo(signedInfoElem, currentSystemId);
      boolean useProxy = false;
      if (useProxy) {
         si.setResolverProperty("http.proxy.host", "www-cache.uni-siegen.de");
         si.setResolverProperty("http.proxy.port", "3128");
      }
      boolean verify = si.verify();
      System.out.println(verify ? "Verification successful" : "Verification failed");
      if (!verify) {
         for (int i=0; i<si.getLength(); i++) {
            if (si.getVerificationResult(i)) {
               System.out.println("OK:     " + si.item(i).getURI());
            } else {
               System.out.println("Failed: " + si.item(i).getURI());
               System.out.println("data follows: ");
               System.out.print(new String(si.item(i).getXMLSignatureInput().getBytes()));
            }
         }
      }
      */
   }
}
