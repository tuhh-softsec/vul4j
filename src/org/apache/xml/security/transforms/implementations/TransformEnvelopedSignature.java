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
package org.apache.xml.security.transforms.implementations;



import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.TransformSpi;
import org.apache.xml.security.transforms.TransformationException;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;


/**
 * Implements the <CODE>http://www.w3.org/2000/09/xmldsig#enveloped-signature</CODE>
 * transform.
 *
 * @author Christian Geuer-Pollmann
 */
public class TransformEnvelopedSignature extends TransformSpi {

   /** Field implementedTransformURI */
   public static final String implementedTransformURI =
      Transforms.TRANSFORM_ENVELOPED_SIGNATURE;

   //J-
   public boolean wantsOctetStream ()   { return true; }
   public boolean wantsNodeSet ()       { return true; }
   public boolean returnsOctetStream () { return true; }
   public boolean returnsNodeSet ()     { return false; }
   //J+

   /**
    * Method engineGetURI
    *
    *
    */
   protected String engineGetURI() {
      return implementedTransformURI;
   }

   /**
    * This transform performs the Enveloped-Signature-Transform by
    *
    * @param input
    *
    * @throws TransformationException
    */
   protected XMLSignatureInput enginePerformTransform(XMLSignatureInput input)
           throws TransformationException {

      try {

         /**
          * If the actual input is an octet stream, then the application MUST
          * convert the octet stream to an XPath node-set suitable for use by
          * Canonical XML with Comments. (A subsequent application of the
          * REQUIRED Canonical XML algorithm would strip away these comments.)
          *
          * ...
          *
          * The evaluation of this expression includes all of the document's nodes
          * (including comments) in the node-set representing the octet stream.
          */

         /*
         if (input.isOctetStream()) {
            input.setNodesetXPath(Canonicalizer.XPATH_C14N_WITH_COMMENTS);
         }
         */
         Set inputSet = input.getNodeSet();

         if (inputSet.isEmpty()) {
            Object exArgs[] = { "input node set contains no nodes" };

            throw new TransformationException("generic.EmptyMessage", exArgs);
         }

         Element transformElement = this._transformObject.getElement();
         Node signatureElement = transformElement;
         boolean found = false;

         searchSignatureElemLoop: while (true) {
            if ((signatureElement == null)
                    || (signatureElement.getNodeType() == Node.DOCUMENT_NODE)) {
               break searchSignatureElemLoop;
            }

            if (((Element) signatureElement).getNamespaceURI()
                    .equals(Constants
                    .SignatureSpecNS) && ((Element) signatureElement)
                       .getLocalName().equals(Constants._TAG_SIGNATURE)) {
               found = true;

               break searchSignatureElemLoop;
            }

            signatureElement = signatureElement.getParentNode();
         }

         if (!found) {
            throw new TransformationException(
               "envelopedSignatureTransformNotInSignatureElement");
         }

         Document transformDoc = transformElement.getOwnerDocument();
         Document inputDoc = XMLUtils.getOwnerDocument((Node) inputSet.iterator().next());

         if (transformDoc != inputDoc) {
            throw new TransformationException("xpath.funcHere.documentsDiffer");
         }

         Set resultSet = new HashSet();
         Iterator iterator = inputSet.iterator();

         while (iterator.hasNext()) {
            Node inputNode = (Node) iterator.next();

            if (!TransformEnvelopedSignature
                    .isDescendantOrSelf(signatureElement, inputNode)) {
               resultSet.add(inputNode);
            }
         }

         XMLSignatureInput result = new XMLSignatureInput(resultSet,
                                       input.getCachedXPathAPI());

         return result;
      } catch (IOException ex) {
         throw new TransformationException("empty", ex);
      } catch (SAXException ex) {
         throw new TransformationException("empty", ex);
      } catch (ParserConfigurationException ex) {
         throw new TransformationException("empty", ex);
      } catch (CanonicalizationException ex) {
         throw new TransformationException("empty", ex);
      } catch (InvalidCanonicalizerException ex) {
         throw new TransformationException("empty", ex);
      }
   }

   /**
    * Returns true if the descendantOrSelf is on the descendant-or-self axis
    * of the context node.
    *
    * @param ctx
    * @param descendantOrSelf
    *
    */
   static boolean isDescendantOrSelf(Node ctx, Node descendantOrSelf) {

      if (ctx == descendantOrSelf) {
         return true;
      }

      Node parent = descendantOrSelf;

      while (true) {
         if (parent == null) {
            return false;
         }

         if (parent == ctx) {
            return true;
         }

         if (parent.getNodeType() == Node.ATTRIBUTE_NODE) {
            parent = ((Attr) parent).getOwnerElement();
         } else {
            parent = parent.getParentNode();
         }
      }
   }
}
