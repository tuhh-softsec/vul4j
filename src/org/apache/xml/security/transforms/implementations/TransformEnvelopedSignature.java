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
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.TransformSpi;
import org.apache.xml.security.transforms.TransformationException;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
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
   /** @inheritDoc */
   public boolean wantsOctetStream ()   { return true; }
   /** @inheritDoc */
   public boolean wantsNodeSet ()       { return true; }
   /** @inheritDoc */
   public boolean returnsOctetStream () { return true; }
   /** @inheritDoc */
   public boolean returnsNodeSet ()     { return false; }
   //J+

   /**
    * Method engineGetURI
    *
    * @inheritDoc
    */
   protected String engineGetURI() {
      return implementedTransformURI;
   }

   /**
    * @inheritDoc
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
         
         Element transformElement = this._transformObject.getElement();
         Node signatureElement = transformElement;
         

         signatureElement = searchSignatureElement(signatureElement);
         if (input.isElement()) {
         	XMLSignatureInput result = new XMLSignatureInput(input.getSubNode(),input.getCachedXPathAPI());
         	result.setExcludeNode(signatureElement);
         	result.setExcludeComments(input.isExcludeComments());
         	return result;
         }
         //
         Set inputSet = input.getNodeSet();

         if (inputSet.isEmpty()) {
            Object exArgs[] = { "input node set contains no nodes" };

            throw new TransformationException("generic.EmptyMessage", exArgs);
         }
         
         Set resultSet=XMLUtils.excludeNodeFromSet(signatureElement, inputSet);

         XMLSignatureInput result = new XMLSignatureInput(resultSet,
                                       null/*input.getCachedXPathAPI()*/);

         return result;
      } catch (IOException ex) {
         throw new TransformationException("empty", ex);
      } catch (SAXException ex) {
         throw new TransformationException("empty", ex);
      } catch (ParserConfigurationException ex) {
         throw new TransformationException("empty", ex);
      } catch (CanonicalizationException ex) {
         throw new TransformationException("empty", ex);
      } 
   }

   /**
    * @param signatureElement    
    * @return
    * @throws TransformationException
    */
    private static Node searchSignatureElement(Node signatureElement) throws TransformationException {
	    boolean found=false;
        
	    while (true) {
	    	if ((signatureElement == null)
	            || (signatureElement.getNodeType() == Node.DOCUMENT_NODE)) {
	    		break;
	    	}
	    	Element el=(Element)signatureElement;
	    	if (el.getNamespaceURI().equals(Constants.SignatureSpecNS)
                    && 
	               el.getLocalName().equals(Constants._TAG_SIGNATURE)) {
	    		found = true;
	    		break;
	    	}

	    	signatureElement = signatureElement.getParentNode();
	    }

	    if (!found) {
	      throw new TransformationException(
	       "envelopedSignatureTransformNotInSignatureElement");
	    }
	    return signatureElement;
    }
}
