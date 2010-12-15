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
package org.apache.xml.security.samples.signature.contract;



import java.io.File;
import java.io.FileOutputStream;

import org.apache.xml.security.keys.content.KeyName;
import org.apache.xml.security.signature.SignedInfo;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.transforms.params.XPathContainer;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Element;



/**
 * In the past the protokol to sign data (like a contract) from more than one people
 * looks like this:
 * 1. A signes the hash of the data => SignatureA
 * 2. B signes SignatureA => SignatureB
 * 3. C signes SignatureB => SignatureC
 *
 * To verify e.g. signature C the following steps were necessary:
 * 1. Verify signature C thereby decrypt SignatureC (SignatureB)
 * 2. Verify signature B thereby decrypt SignatureB (SignatureA)
 * 3. Verify signature A thereby decrypt SignatureA (hash of the data)
 * 4. Compare the calculated hash of the sent contract with the decrypted SignatureA result
 *
 * XML-Signatures are more flexible in this way.
 * It is possible to sign data in steps from different signers and
 * verify a signature independent from the others signatures.
 * Furthermore all the signed data and the signatures can be hold in one file.
 *
 * @author Rene Kollmorgen <Rene.Kollmorgen@softwareag.com>
 */
public class ThreeSignerContractSign {

   /**
    * Method main
    *
    * @param unused
    * @throws Exception
    */
   public static void main(String unused[]) throws Exception {
      //J-
      File signatureFile = new File("threeSignerContract.xml");
      String BaseURI = signatureFile.toURL().toString();
      //J+
      javax.xml.parsers.DocumentBuilderFactory dbf =
         javax.xml.parsers.DocumentBuilderFactory.newInstance();

      dbf.setNamespaceAware(true);

      javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
      org.w3c.dom.Document doc = db.newDocument();
      Element contract = doc.createElementNS(null, "contract");

      // create contract ////////////////////////////////////////////
      doc.appendChild(contract);

      // beautifying //////
      Element condition1 = doc.createElementNS(null, "condition1");

      condition1.setAttributeNS(null, "Id", "cond1");
      condition1.appendChild(
         doc.createTextNode(
            "condition1 not covered in first signature, only binding for the second and third signer"));

      Element condition2 = doc.createElementNS(null, "condition2");

      condition2.appendChild(doc.createTextNode("condition2"));

      Element condition3 = doc.createElementNS(null, "condition3");

      condition3.appendChild(doc.createTextNode("condition3"));
      contract.appendChild(doc.createTextNode("\n"));
      contract.appendChild(condition1);
      contract.appendChild(doc.createTextNode("\n"));
      contract.appendChild(condition2);
      contract.appendChild(doc.createTextNode("\n"));
      contract.appendChild(condition3);
      contract.appendChild(doc.createTextNode("\n"));

      //J-
      String id1 = "firstSigner";
      String id2 = "secondSigner";
      String id3 = "thirdSigner";

      // sign the whole contract and no signature and exclude condition1
      String xp1Old = "not(ancestor-or-self::ds:Signature)"
            + " and not(ancestor-or-self::node()[@Id='cond1'])";

      // sign the contract with condition2 and codition3 and no signature
      String xp1 = "not(ancestor-or-self::ds:Signature)" + "\n"
                + " and (" + "\n"
                + "    (ancestor-or-self::node() = /contract/condition2) " + "\n"
                + " or (ancestor-or-self::node() = /contract/condition3) " + "\n"
                + " or (self::node() = /contract) " + "\n"
                + " or ((parent::node() = /contract) and (self::text()))" + "\n"
                + ")";

      // sign the whole contract and no signature but the first
      String xp2 = "not(ancestor-or-self::ds:Signature)" + "\n"
                 + " or ancestor-or-self::ds:Signature[@Id='" + id1 + "']";

      // sign the whole contract and no signature but the first and the second
      String xp3 = "not(ancestor-or-self::ds:Signature)" + "\n"
                 + " or ancestor-or-self::ds:Signature[@Id='" + id1 + "']" + "\n"
                 + " or ancestor-or-self::ds:Signature[@Id='" + id2 + "']";
      //J+
      //////////////////////////////////////////////////////////////////
      // first signer //////////////////////////////////////////////////
      //////////////////////////////////////////////////////////////////
      {
         XMLSignature firstSigner =
            new XMLSignature(doc, BaseURI, XMLSignature.ALGO_ID_MAC_HMAC_SHA1);

         firstSigner.setId(id1);
         contract.appendChild(firstSigner.getElement());

         String rootnamespace = contract.getNamespaceURI();
         boolean rootprefixed = (rootnamespace != null)
                                && (rootnamespace.length() > 0);
         String rootlocalname = contract.getNodeName();
         Transforms transforms = new Transforms(doc);
         XPathContainer xpath = new XPathContainer(doc);

         xpath.setXPathNamespaceContext("ds", Constants.SignatureSpecNS);
         xpath.setXPath("\n" + xp1 + "\n");
         transforms.addTransform(Transforms.TRANSFORM_XPATH,
                                 xpath.getElementPlusReturns());
         firstSigner.addDocument("", transforms, Constants.ALGO_ID_DIGEST_SHA1);

         {

            // not really secure ///////////////////
            firstSigner.getKeyInfo().add(new KeyName(doc, "First signer key"));

            ////////////////////////////////////////////////
            System.out.println("First signer: Start signing");
            firstSigner
               .sign(firstSigner
                  .createSecretKey("First signer key".getBytes()));
            System.out.println("First signer: Finished signing");
         }

         SignedInfo s = firstSigner.getSignedInfo();

         for (int i = 0; i < s.getSignedContentLength(); i++) {
            System.out.println("################ Signed Resource " + i
                               + " ################");
            System.out.println(new String(s.getSignedContentItem(i)));
            System.out.println();
         }
      }

      //////////////////////////////////////////////////////////////////
      // second signer /////////////////////////////////////////////////
      //////////////////////////////////////////////////////////////////
      {
         XMLSignature secondSigner = new XMLSignature(doc, BaseURI,
                                        XMLSignature.ALGO_ID_MAC_HMAC_SHA1);

         secondSigner.setId(id2);
         contract.appendChild(secondSigner.getElement());

         Transforms transforms2 = new Transforms(doc);
         XPathContainer xpath2 = new XPathContainer(doc);

         xpath2.setXPathNamespaceContext("ds", Constants.SignatureSpecNS);
         xpath2.setXPath("\n" + xp2 + "\n");
         transforms2.addTransform(Transforms.TRANSFORM_XPATH,
                                  xpath2.getElementPlusReturns());
         secondSigner.addDocument("", transforms2,
                                  Constants.ALGO_ID_DIGEST_SHA1);

         {
            secondSigner.getKeyInfo().add(new KeyName(doc,
                                                      "Second signer key"));
            System.out.println("Second signer: Start signing");
            secondSigner
               .sign(secondSigner
                  .createSecretKey("Second signer key".getBytes()));
            System.out.println("Second signer: Finished signing");
         }

         SignedInfo s2 = secondSigner.getSignedInfo();

         for (int i = 0; i < s2.getSignedContentLength(); i++) {
            System.out.println("################ Signed Resource " + i
                               + " ################");
            System.out.println(new String(s2.getSignedContentItem(i)));
            System.out.println();
         }
      }

      //////////////////////////////////////////////////////////////////
      // third signer //////////////////////////////////////////////////
      //////////////////////////////////////////////////////////////////
      {
         XMLSignature thirdSigner =
            new XMLSignature(doc, BaseURI, XMLSignature.ALGO_ID_MAC_HMAC_SHA1);

         thirdSigner.setId(id3);
         contract.appendChild(thirdSigner.getElement());

         Transforms transforms3 = new Transforms(doc);
         XPathContainer xpath3 = new XPathContainer(doc);

         xpath3.setXPathNamespaceContext("ds", Constants.SignatureSpecNS);
         xpath3.setXPath("\n" + xp3 + "\n");
         transforms3.addTransform(Transforms.TRANSFORM_XPATH,
                                  xpath3.getElementPlusReturns());
         thirdSigner.addDocument("", transforms3,
                                 Constants.ALGO_ID_DIGEST_SHA1);

         {
            thirdSigner.getKeyInfo().add(new KeyName(doc, "Third signer key"));
            System.out.println("Third signer: Start signing");
            thirdSigner
               .sign(thirdSigner
                  .createSecretKey("Third signer key".getBytes()));
            System.out.println("Third signer: Finished signing");
         }

         SignedInfo s3 = thirdSigner.getSignedInfo();

         for (int i = 0; i < s3.getSignedContentLength(); i++) {
            System.out.println("################ Signed Resource " + i
                               + " ################");
            System.out.println(new String(s3.getSignedContentItem(i)));
            System.out.println();
         }
      }

      //////////////////////////////////////////////////////////////////
      // forth signer //////////////////////////////////////////////////
      //////////////////////////////////////////////////////////////////
      {
         XMLSignature forthSigner =
            new XMLSignature(doc, BaseURI, XMLSignature.ALGO_ID_MAC_HMAC_SHA1);

         forthSigner.setId("sig4");
         contract.appendChild(forthSigner.getElement());

         {

            // first of all, add the basic document without signatures
            Transforms transforms4 = new Transforms(doc);
            XPathContainer xpath4 = new XPathContainer(doc);

            xpath4.setXPathNamespaceContext("ds", Constants.SignatureSpecNS);
            xpath4.setXPath("\n" + "not(ancestor-or-self::ds:Signature)"
                            + "\n");
            transforms4.addTransform(Transforms.TRANSFORM_XPATH,
                                     xpath4.getElementPlusReturns());
            forthSigner.addDocument("", transforms4,
                                    Constants.ALGO_ID_DIGEST_SHA1);
         }

         {

            // then add the different signatures

            /*
            Transforms transforms4 = new Transforms(doc);
            XPathContainer xpath4 = new XPathContainer(doc);

            xpath4.setXPathNamespaceContext("ds", Constants.SignatureSpecNS);
            xpath4.setXPath("\n" + "ancestor-or-self::ds:Signature[@Id='" + id1 + "']" + "\n");
            transforms4.addTransform(Transforms.TRANSFORM_XPATH, xpath4.getElementPlusReturns());
            forthSigner.addDocument("#xpointer(id('firstSigner'))", transforms4, Constants.ALGO_ID_DIGEST_SHA1, null, "ds:Signature");
            */
            forthSigner.addDocument("#xpointer(id('firstSigner'))", null,
                                    Constants.ALGO_ID_DIGEST_SHA1, null,
                                    "ds:Signature");
         }

         {
            forthSigner.getKeyInfo().add(new KeyName(doc, "Forth signer key"));
            System.out.println("Forth signer: Start signing");
            forthSigner
               .sign(forthSigner
                  .createSecretKey("Forth signer key".getBytes()));
            System.out.println("Forth signer: Finished signing");
         }

         SignedInfo s4 = forthSigner.getSignedInfo();

         for (int i = 0; i < s4.getSignedContentLength(); i++) {
            System.out.println("################ Signed Resource " + i
                               + " ################");
            System.out.println(new String(s4.getSignedContentItem(i)));
            System.out.println();
         }
      }

      //////////////////////////////////////////////////////////////////
      // write away files
      //////////////////////////////////////////////////////////////////
      {
         FileOutputStream f = new FileOutputStream(signatureFile);

         XMLUtils.outputDOMc14nWithComments(doc, f);
         f.close();
         System.out.println("Wrote signature to " + BaseURI);
      }
   }

   static {
      org.apache.xml.security.Init.init();

      // org.apache.xml.security.utils.Constants.setSignatureSpecNSprefix("");
   }
}
