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

import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.implementations.Canonicalizer20010315ExclWithComments;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.TransformSpi;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.transforms.params.InclusiveNamespaces;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;


/**
 * Implements the <CODE>http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments</CODE>
 * transform.
 *
 * @author Christian Geuer-Pollmann
 */
public class TransformC14NExclusiveWithComments extends TransformSpi {

   /** Field implementedTransformURI */
   public static final String implementedTransformURI =
      Transforms.TRANSFORM_C14N_EXCL_WITH_COMMENTS;

   //J-
   /** @inheritDoc */
   public boolean wantsOctetStream ()   { return false; }
   /** @inheritDoc */
   public boolean wantsNodeSet ()       { return true; }
   /** @inheritDoc */
   public boolean returnsOctetStream () { return true; }
   /** @inheritDoc */
   public boolean returnsNodeSet ()     { return false; }
   //J+

   /**
    * Method engineGetURI
    *@inheritDoc 
    *
    */
   protected String engineGetURI() {
      return implementedTransformURI;
   }

   /**
    * @inheritDoc 
    */
   protected XMLSignatureInput enginePerformTransform(XMLSignatureInput input)
           throws CanonicalizationException {
   	    return enginePerformTransform(input,null);
   }
    protected XMLSignatureInput enginePerformTransform(XMLSignatureInput input,OutputStream os)
    throws CanonicalizationException {
     try {
        String inclusiveNamespaces = null;

        if (this._transformObject
                .length(InclusiveNamespaces
                   .ExclusiveCanonicalizationNamespace, InclusiveNamespaces
                   ._TAG_EC_INCLUSIVENAMESPACES) == 1) {
           Element inclusiveElement =
               XMLUtils.selectNode(
              this._transformObject.getElement().getFirstChild(),
                 InclusiveNamespaces.ExclusiveCanonicalizationNamespace,
                 InclusiveNamespaces._TAG_EC_INCLUSIVENAMESPACES,0);

           inclusiveNamespaces = new InclusiveNamespaces(inclusiveElement,
                   this._transformObject.getBaseURI()).getInclusiveNamespaces();
        }

        Canonicalizer20010315ExclWithComments c14n =
            new Canonicalizer20010315ExclWithComments();
         c14n.set_includeComments(!input.isExcludeComments());
        if (os!=null) {
           c14n.setWriter( os);
        }
        byte []result;
        if (input.isOctetStream()) {
           result=c14n.engineCanonicalize(input.getBytes());
        } else if (input.isElement()) {
                org.w3c.dom.Node excl=input.getExcludeNode();
                result =c14n
                           .engineCanonicalizeSubTree(input
                              .getSubNode(), inclusiveNamespaces
                              ,excl);
          }    else {
              result = c14n
                 .engineCanonicalizeXPathNodeSet(input
                    .getNodeSet(), inclusiveNamespaces
                    );      
          }
        XMLSignatureInput output=new XMLSignatureInput(result);
        if (os!=null) {
           output.setOutputStream(os);
        }
        return output;
     } catch (IOException ex) {
        throw new CanonicalizationException("empty", ex);
     } catch (ParserConfigurationException ex) {
        throw new CanonicalizationException("empty", ex);
     } catch (XMLSecurityException ex) {
        throw new CanonicalizationException("empty", ex);
     } catch (SAXException ex) {
        throw new CanonicalizationException("empty", ex);
     }
   }
}
