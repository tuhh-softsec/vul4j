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
package org.apache.xml.security.transforms.implementations;



import java.util.*;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;
import org.w3c.dom.*;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.c14n.*;
import org.apache.xml.security.transforms.params.XPath2FilterContainer;
import org.apache.xml.security.transforms.*;
import org.apache.xml.security.exceptions.*;
import org.apache.xml.security.utils.*;
import org.apache.xpath.CachedXPathAPI;
import org.apache.xpath.objects.XObject;
import org.apache.xml.utils.PrefixResolverDefault;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.dtm.DTMManager;


/**
 * Implements the <I>XML Signature XPath Filter v2.0</I>
 *
 * @author $Author$
 * @see <A HREF="http://www.w3.org/TR/xmldsig-filter2/">XPath Filter v2.0 (TR)</A>
 * @see <A HREF=http://www.w3.org/Signature/Drafts/xmldsig-xfilter2/">XPath Filter v2.0 (editors copy)</A>
 */
public class TransformXPath2Filter extends TransformSpi {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category
         .getInstance(TransformXPath2Filter.class.getName());

   /** Field implementedTransformURI */
   public static final String implementedTransformURI =
      Transforms.TRANSFORM_XPATH2FILTER;

   //J-
   public boolean wantsOctetStream ()   { return false; }
   public boolean wantsNodeSet ()       { return true; }
   public boolean returnsOctetStream () { return false; }
   public boolean returnsNodeSet ()     { return true; }
   //J+

   /**
    * Method engineGetURI
    *
    * @return
    */
   protected String engineGetURI() {
      return this.implementedTransformURI;
   }

   /**
    * Method enginePerformTransform
    *
    * @param input
    * @return
    * @throws TransformationException
    */
   protected XMLSignatureInput enginePerformTransform(XMLSignatureInput input)
           throws TransformationException {

      try {
         if (input.isOctetStream()) {
            input.setNodesetXPath(Canonicalizer.XPATH_C14N_WITH_COMMENTS);
         }

         NodeList inputNodes = input.getNodeSet();
         CachedXPathFuncHereAPI xPathFuncHereAPI =
            new CachedXPathFuncHereAPI(input.getCachedXPathAPI());
         CachedXPathAPI myXPathAPI =
            new CachedXPathAPI(input.getCachedXPathAPI());

         if (inputNodes.getLength() == 0) {
            Object exArgs[] = { "input node set contains no nodes" };

            throw new TransformationException("empty", exArgs);
         }

         Element transformElement = this._transformObject.getElement();
         Document doc = transformElement.getOwnerDocument();
         Element nscontext =
            XMLUtils.createDSctx(doc, "dsig-xpath",
                                 XPath2FilterContainer.XPathFilter2NS);
         Element xpathElement =
            (Element) myXPathAPI.selectSingleNode(transformElement,
                                                  "./dsig-xpath:"
                                                  + XPath2FilterContainer
                                                     ._TAG_XPATH2, nscontext);

         if (xpathElement == null) {
            Object exArgs[] = { "dsig-xpath:XPath", "Transform" };

            throw new TransformationException("xml.WrongContent", exArgs);
         }

         Document inputDoc = XMLUtils.getOwnerDocument(inputNodes.item(0));
         XPath2FilterContainer xpathContainer =
            XPath2FilterContainer.newInstance(xpathElement,
                                              input.getSourceURI());
         NodeList subtreeRoots = xPathFuncHereAPI.selectNodeList(doc,
                                    xpathContainer.getXPathFilterTextNode(),
                                    xpathContainer.getElement());

         /*
          * foreach subtree
          */
         Set selectedNodes = new HashSet();

         for (int i = 0; i < subtreeRoots.getLength(); i++) {
            Node currentRootNode = subtreeRoots.item(i);
            NodeList nodesInSubtree =
               myXPathAPI
                  .selectNodeList(currentRootNode, Canonicalizer
                     .XPATH_C14N_WITH_COMMENTS_SINGLE_NODE);
            int jMax = nodesInSubtree.getLength();

            for (int j = 0; j < jMax; j++) {
               selectedNodes.add(nodesInSubtree.item(j));
            }
         }

         HelperNodeList resultNodes = new HelperNodeList();

         if (xpathContainer.isIntersect()) {
            cat.debug("Intersect: "
                      + xpathContainer.getXPathFilterStr().trim());

            for (int i = 0; i < inputNodes.getLength(); i++) {
               Node currentInputNode = inputNodes.item(i);

               // if the input node is selected, include it in the output
               if (selectedNodes.contains(currentInputNode)) {
                  resultNodes.appendChild(currentInputNode);
               } else {
                  ;
               }
            }
         } else if (xpathContainer.isSubtract()) {
            cat.debug("Subtract:  "
                      + xpathContainer.getXPathFilterStr().trim());

            for (int i = 0; i < inputNodes.getLength(); i++) {
               Node currentInputNode = inputNodes.item(i);

               // if the input node is selected, do not include it
               // otherwise, include it
               if (selectedNodes.contains(currentInputNode)) {
                  ;
               } else {
                  resultNodes.appendChild(currentInputNode);
               }
            }
         } else if (xpathContainer.isUnion()) {
            cat.debug("Union:     "
                      + xpathContainer.getXPathFilterStr().trim());

            for (int i = 0; i < inputNodes.getLength(); i++) {
               Node currentInputNode = inputNodes.item(i);

               // add all input nodes to the selected nodes
               selectedNodes.add(currentInputNode);
            }

            Iterator iterator = selectedNodes.iterator();

            while (iterator.hasNext()) {
               resultNodes.appendChild((Node) iterator.next());
            }
         } else {
            throw new TransformationException("empty");
         }

         XMLSignatureInput result = new XMLSignatureInput(resultNodes,
                                       input.getCachedXPathAPI());

         result.setSourceURI(input.getSourceURI());

         return result;
      } catch (TransformerException ex) {
         throw new TransformationException("empty", ex);
      } catch (DOMException ex) {
         throw new TransformationException("empty", ex);
      } catch (IOException ex) {
         throw new TransformationException("empty", ex);
      } catch (CanonicalizationException ex) {
         throw new TransformationException("empty", ex);
      } catch (InvalidCanonicalizerException ex) {
         throw new TransformationException("empty", ex);
      } catch (ParserConfigurationException ex) {
         throw new TransformationException("empty", ex);
      } catch (XMLSecurityException ex) {
         throw new TransformationException("empty", ex);
      } catch (SAXException ex) {
         throw new TransformationException("empty", ex);
      }
   }
}
