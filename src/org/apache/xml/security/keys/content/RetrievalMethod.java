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
package org.apache.xml.security.keys.content;



import javax.xml.transform.TransformerException;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.*;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.exceptions.*;
import org.apache.xml.security.transforms.*;
import org.apache.xml.security.utils.*;
import org.apache.xml.security.utils.resolver.*;


/**
 *
 * @author $Author$
 */
public class RetrievalMethod extends SignatureElementProxy
        implements KeyInfoContent {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(RetrievalMethod.class.getName());
   //J-
   public static final String TYPE_DSA     = Constants.SignatureSpecNS + "DSAKeyValue";
   public static final String TYPE_RSA     = Constants.SignatureSpecNS + "RSAKeyValue";
   public static final String TYPE_PGP     = Constants.SignatureSpecNS + "PGPData";
   public static final String TYPE_SPKI    = Constants.SignatureSpecNS + "SPKIData";
   public static final String TYPE_MGMT    = Constants.SignatureSpecNS + "MgmtData";
   public static final String TYPE_X509    = Constants.SignatureSpecNS + "X509Data";
   public static final String TYPE_RAWX509 = Constants.SignatureSpecNS + "rawX509Certificate";
   //J+

   /**
    * Constructor RetrievalMethod
    *
    * @param element
    * @param BaseURI
    * @throws XMLSecurityException
    */
   public RetrievalMethod(Element element, String BaseURI)
           throws XMLSecurityException {
      super(element, BaseURI);
   }

   /**
    * Constructor RetrievalMethod
    *
    * @param doc
    * @param URI
    * @param transforms
    * @param Type
    */
   public RetrievalMethod(Document doc, String URI, Transforms transforms,
                          String Type) {

      super(doc);

      this._constructionElement.setAttribute(Constants._ATT_URI, URI);

      if (Type != null) {
         this._constructionElement.setAttribute(Constants._ATT_TYPE, Type);
      }

      if (transforms != null) {
         this._constructionElement.appendChild(transforms.getElement());
         XMLUtils.addReturnToElement(this._constructionElement);
      }
   }

   /**
    * Method getURIAttr
    *
    * @return
    */
   public Attr getURIAttr() {
      return this._constructionElement.getAttributeNode(Constants._ATT_URI);
   }

   /**
    * Method getURI
    *
    * @return
    */
   public String getURI() {
      return this.getURIAttr().getNodeValue();
   }

   /**
    * Method getType
    *
    * @return
    */
   public String getType() {
      return this._constructionElement.getAttribute(Constants._ATT_TYPE);
   }

   /**
    * Method getTransforms
    *
    * @return
    * @throws XMLSecurityException
    */
   public Transforms getTransforms() throws XMLSecurityException {

      try {
         Element nscontext = XMLUtils.createDSctx(this._doc, "ds",
                                                  Constants.SignatureSpecNS);
         Element transformsElem =
            (Element) XPathAPI.selectSingleNode(this._constructionElement,
                                                "./ds:"
                                                + Constants
                                                   ._TAG_TRANSFORMS, nscontext);

         if (transformsElem != null) {
            return new Transforms(transformsElem, this._baseURI);
         }

         return null;
      } catch (TransformerException ex) {
         throw new XMLSecurityException("empty", ex);
      } catch (XMLSignatureException ex) {
         throw new XMLSecurityException("empty", ex);
      }
   }

   public String getBaseLocalName() {
      return Constants._TAG_RETRIEVALMETHOD;
   }
}
