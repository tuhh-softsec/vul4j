
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


/**
 * This class demonstrates the use of a Transform for XSLT. The
 * <CODE>xsl:stylesheet</CODE> is directly embedded in the <CODE>ds:Transform</CODE>,
 * so the {@link Transform} object is created by using the Element.
 *
 * @author Christian Geuer-Pollmann
 * @version %I%, %G%
 */
public class SampleTransformXPath {

   /**
    * Method main
    *
    * @param args
    * @throws Exception
    */
   public static void main(String args[]) throws Exception {
      //J-
      String transformStr =
        "<?xml version='1.0'?>\n"
      + "<Transforms xmlns='http://www.w3.org/2000/09/xmldsig#'>\n"
      + "<Transform Algorithm='http://www.w3.org/TR/1999/REC-xpath-19991116'>\n"
      // + "   <ds:XPath xmlns:match='http://sfdfg/'>(self::match:Order | self::text()[string(parent::e)=\"Hello,  world!\"])</ds:XPath>\n"
      // + "   <ds:XPath>//@*</ds:XPath>\n"
      + "   <!-- Exclude all signatures -->\n"
      // + "   <ds:XPath xmlns:ds='http://www.w3.org/2000/09/xmldsig#'>not(ancestor-or-self::ds:Signature)</ds:XPath>\n"
      + "   <ds:XPath xmlns:ds='http://www.w3.org/2000/09/xmldsig#'>self::text()[ancestor-or-self::node()=/Class/e[1]]</ds:XPath>\n"
      + "</Transform>\n"
      + "</Transforms>\n"
      ;

      String inputStr =
        "<?xml version=\"1.0\"?>" + "\n"
      + "<Class>" + "\n"
      + "   <e>Hello, <!-- comment --> world!</e>" + "\n"
      + "   <Order Name='TINAMIFORMES' xmlns='http://sfdfg/'>" + "\n"
      + "      <Family Name='TINAMIDAE'>" + "\n"
      + "         <Species Scientific_Name='Crypturellus boucardi'>Slaty-breasted Tinamou.</Species>" + "\n"
      + "      </Family>" + "\n"
      + "   </Order>" + "\n"
      + "   <Order Name='PODICIPEDIFORMES'/>" + "\n"
      + "<Signature Id='SignatureToBeOmitted' xmlns='http://www.w3.org/2000/09/xmldsig#'>" + "\n"
      + "     <SignedInfo>" + "\n"
      + "       <Reference>" + "\n"
      + "         <Transforms>" + "\n"
      + "           <Transform Algorithm='http://www.w3.org/2000/09/xmldsig#enveloped-signature' />" + "\n"
//       + "           <Transform Algorithm='http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments' />" + "\n"
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
         db.parse(new java.io.ByteArrayInputStream(transformStr.getBytes()));

      String BaseURI = null;
      Transforms transforms = new Transforms(doc.getDocumentElement(), BaseURI);

      XMLSignatureInput input = new XMLSignatureInput(inputStr.getBytes());

      // input.setCanonicalizerURI(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS);

      XMLSignatureInput result = transforms.performTransforms(input);

      System.out.println(new String(result.getBytes()));
   }
}
