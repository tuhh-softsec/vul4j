
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
package org.apache.xml.security.samples.transforms;



import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Element;


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
