
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
package org.apache.xml.security.samples.canonicalization;



import java.io.ByteArrayInputStream;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.xpath.XPathAPI;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.c14n.helper.XPathContainer;
import org.apache.xml.security.utils.*;

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
