
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
package org.apache.xml.security.test.utils.resolver;



import java.util.*;
import java.net.*;
import java.io.*;
import org.w3c.dom.*;
import org.apache.xml.utils.URI;
import org.apache.xml.security.utils.resolver.ResourceResolverException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.utils.resolver.ResourceResolverSpi;
import org.apache.xml.security.utils.Base64;


/**
 * This class helps us home users to resolve http URIs without a network
 * connection.
 * <BR />
 * The OfflineResolver is only needed for Unit testing. This is not needed for
 * a production environment. It's a very simple cache/proxy to HTTP space
 * so that I can do unit testing with http:// URIs even if I'm not connected
 * to the internet.
 *
 * @author $Author$
 */
public class OfflineResolver extends ResourceResolverSpi {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(OfflineResolver.class.getName());

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

      try {
         String URI = uri.getNodeValue();

         if (this._uriMap.containsKey(URI)) {
            String newURI = (String) this._uriMap.get(URI);

            cat.debug("Mapped " + URI + " to " + newURI);

            InputStream is = new FileInputStream(newURI);

            cat.debug("Available bytes = " + is.available());

            XMLSignatureInput result = new XMLSignatureInput(is);

            // XMLSignatureInput result = new XMLSignatureInput(inputStream);
            result.setSourceURI(URI);
            result.setMIMEType((String) this._mimeMap.get(URI));

            return result;
         } else {
            Object exArgs[] = {
               "The URI " + URI + " is not configured for offline work" };

            throw new ResourceResolverException("generic.EmptyMessage", exArgs,
                                                uri, BaseURI);
         }
      } catch (IOException ex) {
         throw new ResourceResolverException("generic.EmptyMessage", ex, uri,
                                             BaseURI);
      }
   }

   /**
    * We resolve http URIs <I>without</I> fragment...
    *
    * @param uri
    * @param BaseURI
    *
    */
   public boolean engineCanResolve(Attr uri, String BaseURI) {

      String uriNodeValue = uri.getNodeValue();

      if (uriNodeValue.equals("") || uriNodeValue.startsWith("#")) {
         return false;
      }

      try {
         URI uriNew = new URI(new URI(BaseURI), uri.getNodeValue());

         if (uriNew.getScheme().equals("http")) {
            cat.debug("I state that I can resolve " + uriNew.toString());

            return true;
         }

         cat.debug("I state that I can't resolve " + uriNew.toString());
      } catch (URI.MalformedURIException ex) {}

      return false;
   }

   /** Field _uriMap */
   static Map _uriMap = null;

   /** Field _mimeMap */
   static Map _mimeMap = null;

   /**
    * Method register
    *
    * @param URI
    * @param filename
    * @param MIME
    */
   private static void register(String URI, String filename, String MIME) {
      OfflineResolver._uriMap.put(URI, filename);
      OfflineResolver._mimeMap.put(URI, MIME);
   }

   static {
      org.apache.xml.security.Init.init();

      OfflineResolver._uriMap = new HashMap();
      OfflineResolver._mimeMap = new HashMap();

      OfflineResolver.register("http://www.w3.org/TR/xml-stylesheet",
                               "data/org/w3c/www/TR/xml-stylesheet.html",
                               "text/html");
      OfflineResolver.register("http://www.w3.org/TR/2000/REC-xml-20001006",
                               "data/org/w3c/www/TR/2000/REC-xml-20001006",
                               "text/xml");
      OfflineResolver.register("http://www.nue.et-inf.uni-siegen.de/index.html",
                               "data/org/apache/xml/security/temp/nuehomepage",
                               "text/html");
      OfflineResolver.register(
         "http://www.nue.et-inf.uni-siegen.de/~geuer-pollmann/id2.xml",
         "data/org/apache/xml/security/temp/id2.xml", "text/xml");
      OfflineResolver.register(
         "http://xmldsig.pothole.com/xml-stylesheet.txt",
         "data/com/pothole/xmldsig/xml-stylesheet.txt", "text/xml");
   }
}
