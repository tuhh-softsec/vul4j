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
package org.apache.xml.security.algorithms.encryption.params;



import org.w3c.dom.*;
import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
import org.apache.xml.security.utils.*;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.exceptions.XMLSecurityException;


/**
 * Parameters for RSAES-OAEP-ENCRYPT from RFC 2437
 *
 * @author $Author$
 */
public class OAEPParams extends EncryptionMethodParams {

   /** Field _digestMethodURI */
   String _digestMethodURI;

   /** Field _OAEPParamBytes[] */
   byte _OAEPParamBytes[];

   /** Field DEFAULT_DIGEST */
   public static final String DEFAULT_DIGEST = Constants.ALGO_ID_DIGEST_SHA1;

   /**
    * Method getAlgorithmURI
    *
    * @return
    */
   public String getAlgorithmURI() {
      return EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP;
   }

   /**
    * Constructor OAEPParams
    *
    * @param digestMethodURI
    * @param OAEPParamBytes encoding parameters, an octet string that may be empty
    */
   public OAEPParams(String digestMethodURI, byte[] OAEPParamBytes) {

      if (digestMethodURI == null) {
         this._digestMethodURI = OAEPParams.DEFAULT_DIGEST;
      } else {
         this._digestMethodURI = digestMethodURI;
      }

      if ((OAEPParamBytes == null) || (OAEPParamBytes.length == 0)) {
         this._OAEPParamBytes = null;
      } else {
         this._OAEPParamBytes = OAEPParamBytes;
      }
   }

   /**
    * Constructor OAEPParams
    *
    * @param encryptionMethodElem
    * @throws XMLSecurityException
    */
   public OAEPParams(Element encryptionMethodElem) throws XMLSecurityException {

      Element digestElem = XMLUtils.getDirectChild(encryptionMethodElem,
                              Constants._TAG_DIGESTMETHOD,
                              Constants.SignatureSpecNS);
      Element paramElem =
         XMLUtils.getDirectChild(encryptionMethodElem,
                                 EncryptionConstants._TAG_OAEPPARAMS,
                                 EncryptionConstants.EncryptionSpecNS);

      if (digestElem == null) {
         throw new XMLSecurityException("encryption.MissingDigestMethod");
      }

      if (paramElem == null) {
         throw new XMLSecurityException("encryption.MissingOAEPParams");
      }

      this._digestMethodURI = digestElem.getAttributeNS(null, Constants._ATT_ALGORITHM);
      this._OAEPParamBytes = Base64.decode(paramElem);
   }

   /**
    * Method getDigestMethodURI
    *
    * @return
    */
   public String getDigestMethodURI() {
      return this._digestMethodURI;
   }

   /**
    * Method getOAEPParamBytes
    *
    * @return
    */
   public byte[] getOAEPParamBytes() {
      return this._OAEPParamBytes;
   }

   /**
    * Method createChildNodes
    *
    * @param doc
    * @return
    * @throws XMLSecurityException
    */
   public DocumentFragment createChildNodes(Document doc)
           throws XMLSecurityException {

      DocumentFragment nl = doc.createDocumentFragment();

      XMLUtils.addReturnToNode(nl);

      MessageDigestAlgorithm mda = MessageDigestAlgorithm.getInstance(doc,
                                      this.getDigestMethodURI());

      nl.appendChild(mda.getElement());
      XMLUtils.addReturnToNode(nl);

      if ((this.getOAEPParamBytes() != null)
              && (this.getOAEPParamBytes().length > 0)) {
         Element oaepElem = XMLUtils.createElementInEncryptionSpace(doc,
                               EncryptionConstants._TAG_OAEPPARAMS);

         if (this.getOAEPParamBytes() != null) {
            Text oaepText =
               doc.createTextNode(Base64.encode(this.getOAEPParamBytes()));

            oaepElem.appendChild(oaepText);
         }

         nl.appendChild(oaepElem);
         XMLUtils.addReturnToNode(nl);
      }

      return nl;
   }
}
