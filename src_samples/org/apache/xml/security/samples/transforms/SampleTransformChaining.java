
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
package org.apache.xml.security.samples.transforms;



import java.io.*;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.c14n.helper.XPathContainer;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.*;
import org.apache.xml.security.utils.*;


/**
 * This class demonstrates the use of a Transform for XSLT. The
 * <CODE>xsl:stylesheet</CODE> is directly embedded in the <CODE>ds:Transform</CODE>,
 * so the {@link Transform} object is created by using the Element.
 *
 * @author Christian Geuer-Pollmann
 * @version %I%, %G%
 */
public class SampleTransformChaining {

   /**
    * Method main
    *
    * @param args
    * @throws Exception
    */
   public static void main(String args[]) throws Exception {
      //J-
      String inputStr =
        "<?xml version=\"1.0\"?>" + "\n"
      + "<Class>" + "\n"
      + "   <BASE64>" + "\n"
      + "       PGNvbnRhaW5lcj4KICAgPGU+SGVsbG8sIDwhLS0gY29tbWVudCAtLT4gd29ybGQhPC9lPgogICA8" + "\n"
      + "       T3JkZXIgTmFtZT0nVElOQU1JRk9STUVTJyB4bWxucz0naHR0cDovL3NmZGZnLyc+CiAgICAgIDxG" + "\n"
      + "       YW1pbHkgTmFtZT0nVElOQU1JREFFJz4KICAgICAgICAgPFNwZWNpZXMgU2NpZW50aWZpY19OYW1l" + "\n"
      + "       PSdDcnlwdHVyZWxsdXMgYm91Y2FyZGknPlNsYXR5LWJyZWFzdGVkIFRpbmFtb3UuPC9TcGVjaWVz" + "\n"
      + "       PgogICAgICA8L0ZhbWlseT4KICAgPC9PcmRlcj4KICAgPE9yZGVyIE5hbWU9J1BPRElDSVBFRElG" + "\n"
      + "       T1JNRVMnLz4KPC9jb250YWluZXI+Cg==" + "\n"
      + "   </BASE64>" + "\n"
      + "<Signature Id='SignatureToBeOmitted' xmlns='http://www.w3.org/2000/09/xmldsig#'>" + "\n"
      + "     <SignedInfo>" + "\n"
      + "       <Reference URI=''>" + "\n"
      + "         <Transforms>" + "\n"
      + "           <Transform Algorithm='http://www.w3.org/TR/1999/REC-xpath-19991116'>\n"
      + "             <!-- Exclude all signatures -->\n"
      + "               <ds:XPath xmlns:ds='http://www.w3.org/2000/09/xmldsig#'>\n"
      + "                 ancestor::BASE64"
      + "               </ds:XPath>\n"
      + "           </Transform>\n"
      + "           <Transform Algorithm='http://www.w3.org/2000/09/xmldsig#base64' />\n"
      + "           <Transform Algorithm='http://www.w3.org/TR/1999/REC-xpath-19991116'>\n"
      + "               <ds:XPath xmlns:ds='http://www.w3.org/2000/09/xmldsig#'>\n"
      + "                 not(self::container)     "
      + "               </ds:XPath>\n"
      + "           </Transform>\n"
      + "           <Transform Algorithm='http://www.w3.org/TR/2001/REC-xml-c14n-20010315' />\n"
      + "         </Transforms>" + "\n"
      + "       </Reference>" + "\n"
      + "     </SignedInfo>" + "\n"
      + "   </Signature>"
      + "</Class>" + "\n"
      ;
      //J+
      javax.xml.parsers.DocumentBuilderFactory dbf =
         javax.xml.parsers.DocumentBuilderFactory.newInstance();

      dbf.setNamespaceAware(true);

      javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
      org.w3c.dom.Document doc =
         db.parse(new java.io.ByteArrayInputStream(inputStr.getBytes()));

      // catch the ds:Transforms
      Element nscontext = XMLUtils.createDSctx(doc, "ds", Constants.SignatureSpecNS);

      Element transformsElement = (Element) XPathAPI.selectSingleNode(doc,
                                     "//ds:Transforms", nscontext);
      Transforms transforms = new Transforms(transformsElement, "memory://");
      XMLSignatureInput input = new XMLSignatureInput(doc);

      // execute Transforms
      XMLSignatureInput result = transforms.performTransforms(input);

      // output result
      System.out.println(new String(result.getBytes()));
   }
}
