/*
 * Copyright  1999-2004 The Apache Software Foundation.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.xml.security.utils.resolver.implementations;



import java.util.Set;

import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.utils.IdResolver;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.utils.resolver.ResourceResolverException;
import org.apache.xml.security.utils.resolver.ResourceResolverSpi;
import org.apache.xml.utils.URI;
import org.apache.xpath.CachedXPathAPI;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * Handles barename XPointer Reference URIs.
 * <BR />
 * To retain comments while selecting an element by an identifier ID,
 * use the following full XPointer: URI='#xpointer(id('ID'))'.
 * <BR />
 * To retain comments while selecting the entire document,
 * use the following full XPointer: URI='#xpointer(/)'.
 * This XPointer contains a simple XPath expression that includes
 * the root node, which the second to last step above replaces with all
 * nodes of the parse tree (all descendants, plus all attributes,
 * plus all namespaces nodes).
 *
 * @author $Author$
 */
public class ResolverXPointer extends ResourceResolverSpi {

   /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(
                            ResolverXPointer.class.getName());

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

      NodeList resultNodes = null;
      Document doc = uri.getOwnerDocument();

      // this must be done so that Xalan can catch ALL namespaces
      XMLUtils.circumventBug2650(doc);

      CachedXPathAPI cXPathAPI = new CachedXPathAPI();

      try {
         if (isXPointerSlash(uri, BaseURI)) {
            resultNodes =
               cXPathAPI.selectNodeList(doc,
                                        Canonicalizer.XPATH_C14N_WITH_COMMENTS);
         } else if (isXPointerId(uri, BaseURI)) {
            String id = getXPointerId(uri, BaseURI);
            Element selectedElem = IdResolver.getElementById(doc, id);

            // log.debug("Use #xpointer(id('" + id + "')) on element " + selectedElem);

            if (selectedElem == null) {
               Object exArgs[] = { id };

               throw new ResourceResolverException(
                  "signature.Verification.MissingID", exArgs, uri, BaseURI);
            }

            resultNodes =
               cXPathAPI
                  .selectNodeList(selectedElem, Canonicalizer
                     .XPATH_C14N_WITH_COMMENTS_SINGLE_NODE);
         }
      } catch (javax.xml.transform.TransformerException ex) {
         throw new ResourceResolverException("generic.EmptyMessage", ex, uri,
                                             BaseURI);
      }

      Set resultSet = XMLUtils.convertNodelistToSet(resultNodes);
      XMLSignatureInput result = new XMLSignatureInput(resultSet, cXPathAPI);

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

      if (isXPointerSlash(uri, BaseURI) || isXPointerId(uri, BaseURI)) {
         return true;
      }

      return false;
   }

   /**
    * Method isXPointerSlash
    *
    * @param uri
    * @param BaseURI
    *
    */
   private static boolean isXPointerSlash(Attr uri, String BaseURI) {

      if (uri.getNodeValue().equals("#xpointer(/)")) {
         return true;
      }

      return false;
   }

   /**
    * Method isXPointerId
    *
    * @param uri
    * @param BaseURI
    *
    */
   private static boolean isXPointerId(Attr uri, String BaseURI) {

      String uriNodeValue = uri.getNodeValue();

      if (uriNodeValue.startsWith("#xpointer(id(")
              && uriNodeValue.endsWith("))")) {
         String idPlusDelim = uriNodeValue.substring("#xpointer(id(".length(),
                                                     uriNodeValue.length()
                                                     - "))".length());

         // log.debug("idPlusDelim=" + idPlusDelim);

         if (((idPlusDelim.charAt(0) == '"') && (idPlusDelim
                 .charAt(idPlusDelim.length() - 1) == '"')) || ((idPlusDelim
                 .charAt(0) == '\'') && (idPlusDelim
                 .charAt(idPlusDelim.length() - 1) == '\''))) {
            log.debug("Id="
                      + idPlusDelim.substring(1, idPlusDelim.length() - 1));

            return true;
         }
      }

      return false;
   }

   /**
    * Method getXPointerId
    *
    * @param uri
    * @param BaseURI
    *
    */
   private static String getXPointerId(Attr uri, String BaseURI) {

      String uriNodeValue = uri.getNodeValue();

      if (uriNodeValue.startsWith("#xpointer(id(")
              && uriNodeValue.endsWith("))")) {
         String idPlusDelim = uriNodeValue.substring("#xpointer(id(".length(),
                                                     uriNodeValue.length()
                                                     - "))".length());

         if (((idPlusDelim.charAt(0) == '"') && (idPlusDelim
                 .charAt(idPlusDelim.length() - 1) == '"')) || ((idPlusDelim
                 .charAt(0) == '\'') && (idPlusDelim
                 .charAt(idPlusDelim.length() - 1) == '\''))) {
            return idPlusDelim.substring(1, idPlusDelim.length() - 1);
         }
      }

      return null;
   }
}
