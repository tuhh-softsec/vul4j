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
package org.apache.xml.security.utils.resolver.implementations;



import java.io.*;
import java.net.URL;
import org.w3c.dom.*;
import org.apache.xml.utils.URI;
import org.apache.xml.security.utils.resolver.ResourceResolverException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.utils.resolver.ResourceResolverSpi;


/**
 * A simple ResourceResolver for requests into the local filesystem.
 *
 * @author $Author$
 */
public class ResolverLocalFilesystem extends ResourceResolverSpi {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category
         .getInstance(ResolverLocalFilesystem.class.getName());

   /**
    * Method resolve
    *
    * @param uri
    * @param BaseURI
    * @return
    * @throws ResourceResolverException
    * @todo calculate the correct URI from the attribute and the BaseURI
    */
   public XMLSignatureInput engineResolve(Attr uri, String BaseURI)
           throws ResourceResolverException {

      try {
         URI uriNew = new URI(new URI(BaseURI), uri.getNodeValue());

         // if the URI contains a fragment, ignore it
         URI uriNewNoFrag = new URI(uriNew);

         uriNewNoFrag.setFragment(null);

         String fileName =
            ResolverLocalFilesystem
               .translateUriToFilename(uriNewNoFrag.toString());
         FileInputStream inputStream = new FileInputStream(fileName);
         XMLSignatureInput result = new XMLSignatureInput(inputStream);

         result.setSourceURI(uriNew.toString());

         return result;
      } catch (Exception e) {
         throw new ResourceResolverException("generic.EmptyMessage", e, uri,
                                             BaseURI);
      }
   }

   /**
    * Method translateUriToFilename
    *
    * @param uri
    * @return
    */
   private static String translateUriToFilename(String uri) {

      String subStr = uri.substring("file:/".length());

      subStr.replace('/', java.io.File.separatorChar);

      if (subStr.charAt(1) == ':') {
      	 // we're running M$ Windows, so this works fine
         return subStr;
      } else {
      	 // we're running some UNIX, so we have to prepend a slash
         return "/" + subStr;
      }
   }

   /**
    * Method engineCanResolve
    *
    * @param uri
    * @param BaseURI
    * @return
    */
   public boolean engineCanResolve(Attr uri, String BaseURI) {

      if (uri == null) {
         return false;
      }

      String uriNodeValue = uri.getNodeValue();

      if (uriNodeValue.equals("") || uriNodeValue.startsWith("#")) {
         return false;
      }

      try {
         URI uriNew = new URI(new URI(BaseURI), uri.getNodeValue());

         cat.debug("I was asked whether I can resolve " + uriNew.toString());

         if (uriNew.getScheme().equals("file")) {
            cat.debug("I state that I can resolve " + uriNew.toString());

            return true;
         }
      } catch (Exception e) {}

      cat.debug("But I can't");

      return false;
   }
}
