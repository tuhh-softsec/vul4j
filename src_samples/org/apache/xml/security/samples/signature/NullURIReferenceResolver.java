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
package org.apache.xml.security.samples.signature;



import java.io.*;
import org.w3c.dom.*;
import org.apache.xml.utils.URI;
import org.apache.xml.security.utils.resolver.ResourceResolverException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.utils.resolver.ResourceResolverSpi;


/**
 * This is a sample ResourceResolver who demonstrated how References without
 * URI attribuet could be handled.
 *
 * @author $Author$
 */
public class NullURIReferenceResolver extends ResourceResolverSpi {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category
         .getInstance(NullURIReferenceResolver.class.getName());

   /** Field _data[] */
   byte _data[] = null;

   /** Field _data2[][] */
   byte _data2[][] = null;

   /** Field _count */
   int _count = -1;

   /**
    * Constructor NullURIReferenceResolver
    *
    * @param data
    */
   public NullURIReferenceResolver(byte[] data) {
      _data = data;
      _count = -1;
   }

   /**
    * Constructor NullURIReferenceResolver
    *
    * @param data
    */
   public NullURIReferenceResolver(byte[][] data) {
      _data2 = data;
      _count = 0;
   }

   /**
    * Method engineResolve
    *
    * @param uri
    * @param BaseURI
    *
    * @throws ResourceResolverException
    */
   public XMLSignatureInput engineResolve(Attr uri, String BaseURI)
           throws ResourceResolverException {

      XMLSignatureInput result = null;

      if ((this._data != null) && (this._count == -1)) {

         // we always return the same stuff;
         result = new XMLSignatureInput(this._data);

         result.setSourceURI("memory://null");
         result.setMIMEType("text/txt");
      } else if ((this._data == null) && (this._count != -1)) {
         if (this._count < this._data2.length) {
            result = new XMLSignatureInput(this._data2[this._count]);

            result.setSourceURI("memory://" + this._count);

            this._count++;

            result.setMIMEType("text/txt");
         } else {
            String errMsg = "You did not supply enough data!!! There are only "
                            + (this._data2.length) + " byte[] arrays";
            Object exArgs[] = { errMsg };

            throw new ResourceResolverException("empty", exArgs, uri, BaseURI);
         }
      } else {
         Object exArgs[] = { "You did not supply data !!!" };

         throw new ResourceResolverException("empty", exArgs, uri, BaseURI);
      }

      return result;
   }

   /**
    * Method engineCanResolve
    *
    * @param uri
    * @param BaseURI
    *
    */
   public boolean engineCanResolve(Attr uri, String BaseURI) {

      if (uri == null) {
         if ((this._data != null) && (this._count == -1)) {
            return true;
         } else if ((this._data == null) && (this._count != -1)) {
            return true;
         }
      }

      return false;
   }

   /**
    * Method engineGetPropertyKeys
    *
    *
    */
   public String[] engineGetPropertyKeys() {
      return null;
   }
}
