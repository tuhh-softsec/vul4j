
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
package org.apache.xml.security.signature;



import java.util.Collection;
import org.apache.xml.security.signature.Reference;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignatureException;


/**
 * Thrown by {@link org.apache.xml.security.signature.SignedInfo#verify} when
 * testing the signature fails because of uninitialized
 * {@link org.apache.xml.security.signature.Reference}s.
 *
 * @author Christian Geuer-Pollmann
 * @see ReferenceNotInitializedException
 */
public class MissingResourceFailureException extends XMLSignatureException {

   /** Field uninitializedReference */
   Reference uninitializedReference = null;

   /**
    * MissingKeyResourceFailureException constructor.
    * @param msgID
    * @param reference
    * @see #getReference
    */
   public MissingResourceFailureException(String msgID, Reference reference) {

      super(msgID);

      this.uninitializedReference = reference;
   }

   /**
    * Constructor MissingResourceFailureException
    *
    * @param msgID
    * @param exArgs
    * @param reference
    * @see #getReference
    */
   public MissingResourceFailureException(String msgID, Object exArgs[],
                                          Reference reference) {

      super(msgID, exArgs);

      this.uninitializedReference = reference;
   }

   /**
    * Constructor MissingResourceFailureException
    *
    * @param msgID
    * @param originalException
    * @param reference
    * @see #getReference
    */
   public MissingResourceFailureException(String msgID,
                                          Exception originalException,
                                          Reference reference) {

      super(msgID, originalException);

      this.uninitializedReference = reference;
   }

   /**
    * Constructor MissingResourceFailureException
    *
    * @param msgID
    * @param exArgs
    * @param originalException
    * @param reference
    * @see #getReference
    */
   public MissingResourceFailureException(String msgID, Object exArgs[],
                                          Exception originalException,
                                          Reference reference) {

      super(msgID, exArgs, originalException);

      this.uninitializedReference = reference;
   }

   /**
    * used to set the uninitialized {@link org.apache.xml.security.signature.Reference}
    *
    * @param reference the Reference object
    * @see #getReference
    */
   public void setReference(Reference reference) {
      this.uninitializedReference = reference;
   }

   /**
    * used to get the uninitialized {@link org.apache.xml.security.signature.Reference}
    *
    * This allows to supply the correct {@link org.apache.xml.security.signature.XMLSignatureInput}
    * to the {@link org.apache.xml.security.signature.Reference} to try again verification.
    *
    * @return the Reference object
    * @see #setReference
    */
   public Reference getReference() {
      return this.uninitializedReference;
   }
}
