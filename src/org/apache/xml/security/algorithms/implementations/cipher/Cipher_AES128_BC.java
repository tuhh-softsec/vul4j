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
package org.apache.xml.security.algorithms.implementations.cipher;



import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.algorithms.*;
import org.apache.xml.security.utils.*;


/**
 *
 * @author $Author$
 */
public class Cipher_AES128_BC extends CipherAlgorithmSpi {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(Cipher_AES128_BC.class.getName());

   /** Field _URI */
   public static final String _URI =
      EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128;

   /** Field _ProviderId */
   private static final String _ProviderId = "BC";

   /** Field _keyBits */
   private static final int _keyBits = 128;

   /**
    * Method engineGetURI
    *
    * @return
    */
   protected String engineGetURI() {
      return this._URI;
   }

   /**
    * Constructor Cipher_AES128_BC
    *
    * @throws XMLSecurityException
    */
   public Cipher_AES128_BC() throws XMLSecurityException {

      JCEMapper.ProviderIdClass algorithmID =
         JCEMapper.translateURItoJCEID(this._URI, this._ProviderId);

      try {
         cat.debug("Create a " + this.getClass().getName()
                   + " using the Provider \"" + algorithmID.getProviderId()
                   + "\" and the Id \"" + algorithmID.getAlgorithmID() + "\"");

         this._cipherAlgorithm =
            Cipher.getInstance(algorithmID.getAlgorithmID(),
                               algorithmID.getProviderId());

         cat.debug(this._cipherAlgorithm);
      } catch (java.security.NoSuchAlgorithmException ex) {
         Object[] exArgs = { algorithmID.getAlgorithmID(),
                             ex.getLocalizedMessage() };

         throw new XMLSecurityException("algorithms.NoSuchAlgorithm", exArgs);
      } catch (java.security.NoSuchProviderException ex) {
         Object[] exArgs = { algorithmID.getProviderId(),
                             ex.getLocalizedMessage() };

         throw new XMLSecurityException("algorithms.NoSuchProvider", exArgs);
      } catch (NoSuchPaddingException ex) {
         throw new XMLSecurityException("empty", ex);
      }
   }

   /**
    * Proxy method for {@link javax.crypto.Cipher#update(byte[])}
    * which is executed on the internal {@link javax.crypto.Cipher} object.
    *
    * @param input
    * @throws XMLSecurityException
    */
   protected void engineUpdate(byte[] input) throws XMLSecurityException {

      try {
         this._cipherAlgorithm.update(input);
      } catch (IllegalStateException ex) {
         throw new XMLSecurityException("empty", ex);
      }
   }

   /**
    * Proxy method for {@link javax.crypto.Cipher#update(byte[],int,int)}
    * which is executed on the internal {@link javax.crypto.Cipher} object.
    *
    * @param buf
    * @param offset
    * @param len
    * @throws XMLSecurityException
    */
   protected void engineUpdate(byte buf[], int offset, int len)
           throws XMLSecurityException {

      try {
         this._cipherAlgorithm.update(buf, offset, len);
      } catch (IllegalStateException ex) {
         throw new XMLSecurityException("empty", ex);
      }
   }

   static {
      org.apache.xml.security.Init.init();
   }
}
