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



import java.util.HashSet;
import java.util.Set;

import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.utils.IdResolver;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.utils.resolver.ResourceResolverException;
import org.apache.xml.security.utils.resolver.ResourceResolverSpi;
import org.apache.xml.utils.URI;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;


/**
 * This resolver is used for resolving same-document URIs like URI="" of URI="#id".
 *
 * @author $Author$
 * @see <A HREF="http://www.w3.org/TR/xmldsig-core/#sec-ReferenceProcessingModel">The Reference processing model in the XML Signature spec</A>
 * @see <A HREF="http://www.w3.org/TR/xmldsig-core/#sec-Same-Document">Same-Document URI-References in the XML Signature spec</A>
 * @see <A HREF="http://www.ietf.org/rfc/rfc2396.txt">Section 4.2 of RFC 2396</A>
 */
public class ResolverFragment extends ResourceResolverSpi {

   /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(
                            ResolverFragment.class.getName());

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
      Document doc = uri.getOwnerDocument();

      // this must be done so that Xalan can catch ALL namespaces
      XMLUtils.circumventBug2650(doc);

      Element selectedElem = null;
      if (uriNodeValue.equals("")) {

         /*
          * Identifies the node-set (minus any comment nodes) of the XML
          * resource containing the signature
          */

         log.debug("ResolverFragment with empty URI (means complete document)");
	 selectedElem = doc.getDocumentElement();
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
         selectedElem = IdResolver.getElementById(doc, id);

         log.debug("Try to catch an Element with ID " + id + " and Element was " + selectedElem);
      }

      Set resultSet = dereferenceSameDocumentURI(selectedElem);
      XMLSignatureInput result = new XMLSignatureInput(resultSet);

      log.debug("We return a nodeset with " + resultSet.size() + " nodes");
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
         log.debug("Quick fail for null uri");
         return false;
      }

      String uriNodeValue = uri.getNodeValue();

      if (uriNodeValue.equals("")
              || (uriNodeValue.startsWith("#")
                  &&!uriNodeValue.startsWith("#xpointer("))) {
         log.debug("State I can resolve reference: \"" + uriNodeValue + "\"");
         return true;
      }
      log.debug("Do not seem to be able to resolve reference: \"" + uriNodeValue + "\"");
      return false;
   }

   /**
     * Dereferences a same-document URI fragment.
     *
     * @param node the node (document or element) referenced by the
     *   URI fragment. If null, returns an empty set.
     * @return a set of nodes (minus any comment nodes)
     */
    private Set dereferenceSameDocumentURI(Node node) {
	Set nodeSet = new HashSet();
	if (node != null) {
	    nodeSetMinusCommentNodes(node, nodeSet, null);
	}
	return nodeSet;
    }

    /**
     * Recursively traverses the subtree, and returns an XPath-equivalent
     * node-set of all nodes traversed, excluding any comment nodes.
     *
     * @param node the node to traverse
     * @param nodeSet the set of nodes traversed so far
     * @param the previous sibling node
     */
    private void nodeSetMinusCommentNodes(Node node, Set nodeSet,
	Node prevSibling) {
	switch (node.getNodeType()) {
            case Node.ELEMENT_NODE :
		NamedNodeMap attrs = node.getAttributes();
		if (attrs != null) {
                    for (int i = 0; i<attrs.getLength(); i++) {
                        nodeSet.add(attrs.item(i));
                    }
		}
                nodeSet.add(node);
        	Node pSibling = null;
		for (Node child = node.getFirstChild(); child != null;
                    child = child.getNextSibling()) {
                    nodeSetMinusCommentNodes(child, nodeSet, pSibling);
                    pSibling = child;
		}
                break;
            case Node.TEXT_NODE :
            case Node.CDATA_SECTION_NODE:
		// emulate XPath which only returns the first node in
		// contiguous text/cdata nodes
		if (prevSibling != null &&
                    (prevSibling.getNodeType() == Node.TEXT_NODE ||
                     prevSibling.getNodeType() == Node.CDATA_SECTION_NODE)) {
                    return;
		}
            case Node.PROCESSING_INSTRUCTION_NODE :
		nodeSet.add(node);
	}
    }
}
