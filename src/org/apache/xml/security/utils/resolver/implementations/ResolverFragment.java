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



import java.net.*;
import java.io.*;
import java.util.*;
import org.w3c.dom.*;
import org.apache.xml.utils.URI;
import org.apache.xpath.CachedXPathAPI;
import org.apache.xml.security.c14n.*;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.utils.*;
import org.apache.xml.security.utils.resolver.*;


/**
 * This resolver is used for resolving same-document URIs like URI="" of URI="#id".
 *
 * @author $Author$
 * @see <A HREF="http://www.w3.org/TR/xmldsig-core/#sec-ReferenceProcessingModel">The Reference processing model in the XML Signature spec</A>
 * @see <A HREF="http://www.w3.org/TR/xmldsig-core/#sec-Same-Document">Same-Document URI-References in the XML Signature spec</A>
 * @see <A HREF="http://www.ietf.org/rfc/rfc2396.txt">Section 4.2 of RFC 2396</A>
 */
public class ResolverFragment extends ResourceResolverSpi {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(ResolverFragment.class.getName());

   /**
    * Method engineResolve
    *
    * Wird das gleiche Dokument referenziert?
    * Wird ein anderes Dokument referenziert?
    *
    * @param uri
    * @param BaseURI
    *
    * @throws ResourceResolverException
    */
   public XMLSignatureInput engineResolve(Attr uri, String BaseURI)
           throws ResourceResolverException {

      String uriNodeValue = uri.getNodeValue();
      NodeList resultNodes = null;
      Document doc = uri.getOwnerDocument();

      // this must be done so that Xalan can catch ALL namespaces
      XMLUtils.circumventBug2650(doc);

      CachedXPathAPI cXPathAPI = new CachedXPathAPI();

      if (uriNodeValue.equals("")) {

         /*
          * Identifies the node-set (minus any comment nodes) of the XML
          * resource containing the signature
          */

         // cat.debug("ResolverFragment with empty URI (means complete document)");
         try {
            resultNodes =
               cXPathAPI.selectNodeList(doc,
                                        Canonicalizer.XPATH_C14N_OMIT_COMMENTS);
         } catch (javax.xml.transform.TransformerException ex) {
            throw new ResourceResolverException("generic.EmptyMessage", ex,
                                                uri, BaseURI);
         }
      } else {

         /*
          * URI="#chapter1"
          * Identifies a node-set containing the element with ID attribute
          * value 'chapter1' of the XML resource containing the signature.
          * XML Signature (and its applications) modify this node-set to
          * include the element plus all descendents including namespaces and
          * attributes -- but not comments.
          */
         String id = uriNodeValue.substring(1);

         // Element selectedElem = doc.getElementById(id);
         Element selectedElem = IdResolver.getElementById(doc, id);

         // cat.debug("Try to catch an Element with ID " + id + " and Element was " + selectedElem);
         if (selectedElem == null) {
            resultNodes = new HelperNodeList();
         } else {
            try {
               resultNodes =
                  cXPathAPI
                     .selectNodeList(selectedElem, Canonicalizer
                        .XPATH_C14N_OMIT_COMMENTS_SINGLE_NODE);
            } catch (javax.xml.transform.TransformerException ex) {
               throw new ResourceResolverException("generic.EmptyMessage", ex,
                                                   uri, BaseURI);
            }
         }
      }

      Set resultSet = XMLUtils.convertNodelistToSet(resultNodes);
      XMLSignatureInput result = new XMLSignatureInput(resultSet, cXPathAPI);

      // cat.debug("We return a nodeset with " + resultNodes.getLength() + " nodes");
      result.setMIMEType("text/xml");

      try {
         URI uriNew = new URI(new URI(BaseURI), uri.getNodeValue());

         result.setSourceURI(uriNew.toString());
      } catch (URI.MalformedURIException ex) {
         result.setSourceURI(BaseURI);
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
         return false;
      }

      String uriNodeValue = uri.getNodeValue();

      if (uriNodeValue.equals("")
              || (uriNodeValue.startsWith("#")
                  &&!uriNodeValue.startsWith("#xpointer("))) {
         return true;
      }

      return false;
   }

   /**
    * Method isSameDocumentReference
    *
    * @param uri
    * @param BaseURI
    *
    * @throws URI.MalformedURIException
    */
   private static boolean isSameDocumentReference(Attr uri, String BaseURI)
           throws URI.MalformedURIException {

      String uriNodeValue = uri.getNodeValue();

      if (uriNodeValue.equals("") || uriNodeValue.startsWith("#")) {
         return true;
      } else {
         return false;
      }
   }

   /**
    * Method selectIdentifiedPortions
    *
    * @param uri
    * @param BaseURI
    * @param input
    *
    * @throws ResourceResolverException
    * private static XMLSignatureInput selectIdentifiedPortions(
    *       Attr uri, String BaseURI, XMLSignatureInput input)
    *          throws ResourceResolverException {
    *
    *  try {
    *     cat.debug("Got " + input.getBytes().length
    *               + " bytes from source and  URI oif " + input.getSourceURI());
    *  } catch (Exception ex) {
    *     throw new ResourceResolverException("generic.EmptyMessage", ex, uri,
    *                                         BaseURI);
    *  }
    *
    *  try {
    *     URI fragmentURI = new URI(new URI(BaseURI), uri.getNodeValue());
    *     String fragment = fragmentURI.getFragment();
    *     javax.xml.parsers.DocumentBuilderFactory dbf =
    *        javax.xml.parsers.DocumentBuilderFactory.newInstance();
    *
    *     dbf.setNamespaceAware(true);
    *     dbf.setValidating(true);
    *
    *     javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
    *     Document doc =
    *        db.parse(new java.io.ByteArrayInputStream(input.getBytes()));
    *
    *     cat.debug("Owner Document " + doc.getDocumentElement().getTagName());
    *     cat.debug("try to select fragment " + fragment);
    *
    *     // Element selectedElem = doc.getElementById(fragment);
    *     Element selectedElem = IdResolver.getElementById(doc, fragment);
    *
    *     if (selectedElem != null) {
    *        cat.debug("Selection hat geklappt!!!: "
    *                  + selectedElem.getTagName());
    *     }
    *
    *     return input;
    *  } catch (Exception ex) {
    *     throw new ResourceResolverException("generic.EmptyMessage", ex, uri,
    *                                         BaseURI);
    *  }
    * }
    */
}
