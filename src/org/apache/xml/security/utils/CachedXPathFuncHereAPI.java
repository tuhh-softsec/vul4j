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
package org.apache.xml.security.utils;



import javax.xml.transform.TransformerException;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.dtm.ref.DTMManagerDefault;
import org.apache.xml.dtm.ref.DTMNodeIterator;
import org.apache.xml.dtm.ref.DTMNodeList;
import org.apache.xml.security.transforms.implementations.FuncHereContext;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.PrefixResolverDefault;
import org.apache.xpath.CachedXPathAPI;
import org.apache.xpath.compiler.XPathParser;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.w3c.dom.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;


/**
 *
 * @author $Author$
 */
public class CachedXPathFuncHereAPI {

   /**
    * XPathContext, and thus DTMManager and DTMs, persists through multiple
    *   calls to this object.
    */
   FuncHereContext _funcHereContext = null;

   /** Field _dtmManager           */
   DTMManager _dtmManager = null;

   /**
    * Method getFuncHereContext
    *
    * @return
    */
   public FuncHereContext getFuncHereContext() {
      return this._funcHereContext;
   }

   /**
    * Constructor CachedXPathFuncHereAPI
    *
    * @param existingXPathContext
    */
   public CachedXPathFuncHereAPI(XPathContext existingXPathContext) {
      this._dtmManager = existingXPathContext.getDTMManager();
   }

   /**
    * Constructor CachedXPathFuncHereAPI
    *
    * @param previouslyUsed
    */
   public CachedXPathFuncHereAPI(CachedXPathAPI previouslyUsed) {
      this._dtmManager = previouslyUsed.getXPathContext().getDTMManager();
   }

   /**
    * Use an XPath string to select a single node. XPath namespace
    * prefixes are resolved from the context node, which may not
    * be what you want (see the next method).
    *
    * @param contextNode The node to start searching from.
    * @param xpathnode A Node containing a valid XPath string.
    * @return The first node found that matches the XPath, or null.
    *
    * @throws TransformerException
    */
   public Node selectSingleNode(Node contextNode, Node xpathnode)
           throws TransformerException {
      return selectSingleNode(contextNode, xpathnode, contextNode);
   }

   /**
    * Use an XPath string to select a single node.
    * XPath namespace prefixes are resolved from the namespaceNode.
    *
    * @param contextNode The node to start searching from.
    * @param xpathnode
    * @param namespaceNode The node from which prefixes in the XPath will be resolved to namespaces.
    * @return The first node found that matches the XPath, or null.
    *
    * @throws TransformerException
    */
   public Node selectSingleNode(
           Node contextNode, Node xpathnode, Node namespaceNode)
              throws TransformerException {

      // Have the XObject return its result as a NodeSetDTM.
      NodeIterator nl = selectNodeIterator(contextNode, xpathnode,
                                           namespaceNode);

      // Return the first node, or null
      return nl.nextNode();
   }

   /**
    *   Use an XPath string to select a nodelist.
    *   XPath namespace prefixes are resolved from the contextNode.
    *
    *   @param contextNode The node to start searching from.
    *   @param xpathnode
    *   @return A NodeIterator, should never be null.
    *
    *  @throws TransformerException
    */
   public NodeIterator selectNodeIterator(Node contextNode, Node xpathnode)
           throws TransformerException {
      return selectNodeIterator(contextNode, xpathnode, contextNode);
   }

   /**
    *  Use an XPath string to select a nodelist.
    *  XPath namespace prefixes are resolved from the namespaceNode.
    *
    *  @param contextNode The node to start searching from.
    * @param xpathnode
    *  @param namespaceNode The node from which prefixes in the XPath will be resolved to namespaces.
    *  @return A NodeIterator, should never be null.
    *
    * @throws TransformerException
    */
   public NodeIterator selectNodeIterator(
           Node contextNode, Node xpathnode, Node namespaceNode)
              throws TransformerException {

      // Execute the XPath, and have it return the result
      XObject list = eval(contextNode, xpathnode, namespaceNode);

      // Have the XObject return its result as a NodeSetDTM.
      return list.nodeset();
   }

   /**
    *  Use an XPath string to select a nodelist.
    *  XPath namespace prefixes are resolved from the contextNode.
    *
    *  @param contextNode The node to start searching from.
    * @param xpathnode
    *  @return A NodeIterator, should never be null.
    *
    * @throws TransformerException
    */
   public NodeList selectNodeList(Node contextNode, Node xpathnode)
           throws TransformerException {
      return selectNodeList(contextNode, xpathnode, contextNode);
   }

   /**
    *  Use an XPath string to select a nodelist.
    *  XPath namespace prefixes are resolved from the namespaceNode.
    *
    *  @param contextNode The node to start searching from.
    * @param xpathnode
    *  @param namespaceNode The node from which prefixes in the XPath will be resolved to namespaces.
    *  @return A NodeIterator, should never be null.
    *
    * @throws TransformerException
    */
   public NodeList selectNodeList(
           Node contextNode, Node xpathnode, Node namespaceNode)
              throws TransformerException {

      // Execute the XPath, and have it return the result
      XObject list = eval(contextNode, xpathnode, namespaceNode);

      // Return a NodeList.
      return list.nodelist();
   }

   /**
    *  Evaluate XPath string to an XObject.  Using this method,
    *  XPath namespace prefixes will be resolved from the namespaceNode.
    *  @param contextNode The node to start searching from.
    * @param xpathnode
    *  @return An XObject, which can be used to obtain a string, number, nodelist, etc, should never be null.
    *  @see org.apache.xpath.objects.XObject
    *  @see org.apache.xpath.objects.XNull
    *  @see org.apache.xpath.objects.XBoolean
    *  @see org.apache.xpath.objects.XNumber
    *  @see org.apache.xpath.objects.XString
    *  @see org.apache.xpath.objects.XRTreeFrag
    *
    * @throws TransformerException
    */
   public XObject eval(Node contextNode, Node xpathnode)
           throws TransformerException {
      return eval(contextNode, xpathnode, contextNode);
   }

   /**
    *  Evaluate XPath string to an XObject.
    *  XPath namespace prefixes are resolved from the namespaceNode.
    *  The implementation of this is a little slow, since it creates
    *  a number of objects each time it is called.  This could be optimized
    *  to keep the same objects around, but then thread-safety issues would arise.
    *
    *  @param contextNode The node to start searching from.
    * @param xpathnode
    *  @param namespaceNode The node from which prefixes in the XPath will be resolved to namespaces.
    *  @return An XObject, which can be used to obtain a string, number, nodelist, etc, should never be null.
    *  @see org.apache.xpath.objects.XObject
    *  @see org.apache.xpath.objects.XNull
    *  @see org.apache.xpath.objects.XBoolean
    *  @see org.apache.xpath.objects.XNumber
    *  @see org.apache.xpath.objects.XString
    *  @see org.apache.xpath.objects.XRTreeFrag
    *
    * @throws TransformerException
    */
   public XObject eval(Node contextNode, Node xpathnode, Node namespaceNode)
           throws TransformerException {

      // Since we don't have a XML Parser involved here, install some default support
      // for things like namespaces, etc.
      // (Changed from: XPathContext xpathSupport = new XPathContext();
      //    because XPathContext is weak in a number of areas... perhaps
      //    XPathContext should be done away with.)
      if (this._funcHereContext == null) {
         this._funcHereContext = new FuncHereContext(xpathnode,
                                                     this._dtmManager);
      }

      // Create an object to resolve namespace prefixes.
      // XPath namespaces are resolved from the input context node's document element
      // if it is a root node, or else the current context node (for lack of a better
      // resolution space, given the simplicity of this sample code).
      PrefixResolverDefault prefixResolver =
         new PrefixResolverDefault((namespaceNode.getNodeType()
                                    == Node.DOCUMENT_NODE)
                                   ? ((Document) namespaceNode)
                                      .getDocumentElement()
                                   : namespaceNode);

      // Create the XPath object.
      String str = CachedXPathFuncHereAPI.getStrFromNode(xpathnode);
      XPath xpath = new XPath(str, null, prefixResolver, XPath.SELECT, null);

      // Execute the XPath, and have it return the result
      // return xpath.execute(xpathSupport, contextNode, prefixResolver);
      int ctxtNode = this._funcHereContext.getDTMHandleFromNode(contextNode);

      return xpath.execute(this._funcHereContext, ctxtNode, prefixResolver);
   }

   /**
    *   Evaluate XPath string to an XObject.
    *   XPath namespace prefixes are resolved from the namespaceNode.
    *   The implementation of this is a little slow, since it creates
    *   a number of objects each time it is called.  This could be optimized
    *   to keep the same objects around, but then thread-safety issues would arise.
    *
    *   @param contextNode The node to start searching from.
    * @param xpathnode
    *   @param prefixResolver Will be called if the parser encounters namespace
    *                         prefixes, to resolve the prefixes to URLs.
    *   @return An XObject, which can be used to obtain a string, number, nodelist, etc, should never be null.
    *   @see org.apache.xpath.objects.XObject
    *   @see org.apache.xpath.objects.XNull
    *   @see org.apache.xpath.objects.XBoolean
    *   @see org.apache.xpath.objects.XNumber
    *   @see org.apache.xpath.objects.XString
    *   @see org.apache.xpath.objects.XRTreeFrag
    *
    * @throws TransformerException
    */
   public XObject eval(
           Node contextNode, Node xpathnode, PrefixResolver prefixResolver)
              throws TransformerException {

      // Since we don't have a XML Parser involved here, install some default support
      // for things like namespaces, etc.
      // (Changed from: XPathContext xpathSupport = new XPathContext();
      //    because XPathContext is weak in a number of areas... perhaps
      //    XPathContext should be done away with.)
      // Create the XPath object.
      String str = CachedXPathFuncHereAPI.getStrFromNode(xpathnode);
      XPath xpath = new XPath(str, null, prefixResolver, XPath.SELECT, null);

      // Execute the XPath, and have it return the result
      if (this._funcHereContext == null) {
         this._funcHereContext = new FuncHereContext(xpathnode,
                                                     this._dtmManager);
      }

      int ctxtNode = this._funcHereContext.getDTMHandleFromNode(contextNode);

      return xpath.execute(this._funcHereContext, ctxtNode, prefixResolver);
   }

   /**
    * Method getStrFromNode
    *
    * @param xpathnode
    * @return
    */
   private static String getStrFromNode(Node xpathnode) {

      if (xpathnode.getNodeType() == Node.TEXT_NODE) {
         return ((Text) xpathnode).getData();
      } else if (xpathnode.getNodeType() == Node.ATTRIBUTE_NODE) {
         return ((Attr) xpathnode).getNodeValue();
      } else if (xpathnode.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE) {
         return ((ProcessingInstruction) xpathnode).getNodeValue();
      }

      return null;
   }
}
