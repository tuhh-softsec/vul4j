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
package org.apache.xml.security.encryption;



import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.keys.content.KeyName;
import org.apache.xml.security.exceptions.*;
import org.apache.xml.security.utils.*;
import org.w3c.dom.*;


/**
 * This class maps to the <CODE>xenc:CarriedKeyName</CODE> element.
 *
 * @author $Author$
 */
public class CarriedKeyName extends EncryptionElementProxy {

   /**
    * Constructor CarriedKeyName
    *
    * @param doc
    * @param carriedKeyName
    */
   public CarriedKeyName(Document doc, String carriedKeyName) {

      super(doc, EncryptionConstants._TAG_CARRIEDKEYNAME);

      this.setCarriedKeyName(carriedKeyName);
   }

   /**
    * Constructor CarriedKeyName
    *
    * @param element
    * @param BaseURI
    * @throws XMLSecurityException
    */
   public CarriedKeyName(Element element, String BaseURI)
           throws XMLSecurityException {
      super(element, BaseURI, EncryptionConstants._TAG_CARRIEDKEYNAME);
   }

   /**
    * Method getCarriedKeyName
    *
    * @return
    */
   public String getCarriedKeyName() {
      return ((Text) this._constructionElement.getFirstChild()).getData();
   }

   /**
    * Method setCarriedKeyName
    *
    * @param carriedKeyName
    */
   public void setCarriedKeyName(String carriedKeyName) {

      while (this._constructionElement.hasChildNodes()) {
         this._constructionElement
            .removeChild(this._constructionElement.getFirstChild());
      }

      this._constructionElement
         .appendChild(this._doc.createTextNode(carriedKeyName));
   }

   /**
    * Return true if the CarriedKeyName carries a key which os references by
    * the KeyInfo.
    *
    * @param ki
    * @return true if the CarriedKeyName carries a key which os references by the KeyInfo
    * @throws XMLSecurityException
    */
   public boolean matchesAgainstKeyInfo(KeyInfo ki)
           throws XMLSecurityException {

      String thisName = this.getCarriedKeyName();

      for (int i = 0; i < ki.lengthKeyName(); i++) {
         KeyName kn = ki.itemKeyName(i);

         if (thisName.equals(kn.getKeyName())) {
            return true;
         }
      }

      return false;
   }
}
