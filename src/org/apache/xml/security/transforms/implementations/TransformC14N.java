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
import java.io.OutputStream;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.implementations.Canonicalizer20010315OmitComments;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.TransformSpi;
import org.apache.xml.security.transforms.Transforms;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;


/**
 * Implements the <CODE>http://www.w3.org/TR/2001/REC-xml-c14n-20010315</CODE>
 * transform.
 *
 * @author Christian Geuer-Pollmann
 */
public class TransformC14N extends TransformSpi {

   /** Field implementedTransformURI */
   public static final String implementedTransformURI =
      Transforms.TRANSFORM_C14N_OMIT_COMMENTS;

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
    * @inheritDoc 
    */
   protected String engineGetURI() {
      return TransformC14N.implementedTransformURI;
   }

   /**
    *  @inheritDoc 
    */
   protected XMLSignatureInput enginePerformTransform(XMLSignatureInput input)
           throws IOException, CanonicalizationException {
   	    return enginePerformTransform(input,null);
   }
    protected XMLSignatureInput enginePerformTransform(XMLSignatureInput input,OutputStream os)
    throws IOException, CanonicalizationException {
      try {
         Canonicalizer20010315OmitComments c14n = new Canonicalizer20010315OmitComments();
         if (os!=null) {
         	c14n.setWriter(os);
         }
         byte[] result = null;
         if (input.isOctetStream()) {
            result = c14n.engineCanonicalize(input.getBytes());
         } else {
         	if (input.isElement()) {
         		Node excl=input.getExcludeNode();
         		result=c14n.engineCanonicalizeSubTree(input.getSubNode(),excl);         		
         	} else {
                Set set=input.getNodeSet(true);                
         		result = c14n.engineCanonicalizeXPathNodeSet(set);
            }
         }
         XMLSignatureInput output=new XMLSignatureInput(result);
         if (os!=null) {
            output.setOutputStream(os);
         }
         return output;
      } catch (ParserConfigurationException ex) {
         Object[] exArgs = { ex.getMessage() };
         CanonicalizationException cex = new CanonicalizationException(
            "c14n.Canonicalizer.ParserConfigurationException", exArgs);

         throw cex;
      } catch (SAXException ex) {
         Object[] exArgs = { ex.toString() };
         CanonicalizationException cex =
            new CanonicalizationException("c14n.Canonicalizer.SAXException",
                                          exArgs);

         throw cex;
      }
   }
}
