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
package org.apache.xml.security.keys.provider;



import java.util.Date;
import java.util.TimeZone;
import java.util.Calendar;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.security.cert.*;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.*;
import org.w3c.dom.*;


/**
 *
 * @author $Author$
 */
public abstract class KeyBaseType extends ElementProxy {

   /** Field DATE_STR */

   // static final String DATE_STR = "yyyy-MM-DD'T'hh:mm:ss.SSS'Z'";
   static final String DATE_STR = "yyyy-MM-dd'T'HH:mm:ss'Z'";

   /**
    * Constructor KeyBaseType
    *
    * @param doc
    * @param alias
    */
   public KeyBaseType(Document doc, String alias) {

      super(doc);

      Alias a = new Alias(doc, alias);

      XMLUtils.addReturnToElement(this);
      this._constructionElement.appendChild(a.getElement());
      XMLUtils.addReturnToElement(this);
      this.setDate(new Date(System.currentTimeMillis()));
   }

   /**
    * Constructor KeyBaseType
    *
    * @param element
    * @param BaseURI
    * @throws XMLSecurityException
    */
   public KeyBaseType(Element element, String BaseURI)
           throws XMLSecurityException {
      super(element, BaseURI);
   }

   /**
    * Method getBaseNamespace
    *
    * @return
    */
   public String getBaseNamespace() {
      return ApacheKeyStoreConstants.ApacheKeyStore_NAMESPACE;
   }

   /**
    * Method setDate
    *
    * @param date
    */
   public void setDate(Date date) {

      if (this._state == MODE_CREATE) {
         DateFormat df = new SimpleDateFormat(DATE_STR);

         df.setTimeZone(TimeZone.getTimeZone("GMT"));

         String dateString = df.format(date);

         this._constructionElement
            .setAttributeNS(null, ApacheKeyStoreConstants._ATT_DATE, dateString);
      }
   }

   /**
    * Method getDate
    *
    * @return
    * @throws XMLSecurityException
    */
   public Date getDate() throws XMLSecurityException {

      try {
         String dateString =
            this._constructionElement
               .getAttributeNS(null, ApacheKeyStoreConstants._ATT_DATE);
         DateFormat df = new SimpleDateFormat(DATE_STR);

         df.setTimeZone(TimeZone.getTimeZone("GMT"));

         return df.parse(dateString.trim());
      } catch (ParseException ex) {
         throw new XMLSecurityException("empty", ex);
      }
   }

   /**
    * Method getAlias
    *
    * @return
    * @throws XMLSecurityException
    */
   public String getAlias() throws XMLSecurityException {

      Alias alias =
         new Alias(this
            .getChildElementLocalName(0, ApacheKeyStoreConstants
            .ApacheKeyStore_NAMESPACE, ApacheKeyStoreConstants._TAG_ALIAS), this
               ._baseURI);

      return alias.getAlias();
   }
}
