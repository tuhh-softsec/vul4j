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



import javax.xml.transform.TransformerException;
import org.apache.xml.dtm.DTM;
import org.apache.xpath.XPathContext;
import org.apache.xpath.NodeSetDTM;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.functions.Function;
import org.apache.xpath.res.XPATHErrorResources;
import org.w3c.dom.*;
import org.w3c.dom.traversal.NodeIterator;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.utils.I18n;


/**
 * The 'here()' function returns a node-set containing the attribute or
 * processing instruction node or the parent element of the text node
 * that directly bears the XPath expression.  This expression results
 * in an error if the containing XPath expression does not appear in the
 * same XML document against which the XPath expression is being evaluated.
 *
 * Mainpart is stolen from FuncId.java
 *
 * This does crash under Xalan2.2.D7 and works under Xalan2.2.D9
 *
 * To get this baby to work, a special trick has to be used. The function needs
 * access to the Node where the XPath expression has been defined. This is done
 * by constructing a {@link FuncHereContext) which has this Node as 'owner'.
 *
 * @see http://www.w3.org/Signature/Drafts/xmldsig-core/Overview.html#function-here
 */
public class FuncHere extends Function {

   /**
    * The here function returns a node-set containing the attribute or
    * processing instruction node or the parent element of the text node
    * that directly bears the XPath expression.  This expression results
    * in an error if the containing XPath expression does not appear in the
    * same XML document against which the XPath expression is being evaluated.
    *
    * @param xctxt
    * @return
    * @throws javax.xml.transform.TransformerException
    */
   public XObject execute(XPathContext xctxt)
           throws javax.xml.transform.TransformerException {

      Node xpathOwnerNode = (Node) xctxt.getOwnerObject();

      if (xpathOwnerNode == null) {
         return null;
      }

      int xpathOwnerNodeDTM = xctxt.getDTMHandleFromNode(xpathOwnerNode);

      int currentNode = xctxt.getCurrentNode();
      DTM dtm = xctxt.getDTM(currentNode);
      int docContext = dtm.getDocument();

      if (DTM.NULL == docContext) {
         error(xctxt, XPATHErrorResources.ER_CONTEXT_HAS_NO_OWNERDOC, null);
      }

      {

         // check whether currentNode and the node containing the XPath expression
         // are in the same document
         Document currentDoc =
            XMLUtils.getOwnerDocument(dtm.getNode(currentNode));
         Document xpathOwnerDoc = XMLUtils.getOwnerDocument(xpathOwnerNode);

         if (currentDoc != xpathOwnerDoc) {
            throw new TransformerException(I18n
               .translate("xpath.funcHere.documentsDiffer"));
         }
      }

      XNodeSet nodes = new XNodeSet(xctxt.getDTMManager());
      NodeSetDTM nodeSet = nodes.mutableNodeset();

      {
         int hereNode = DTM.NULL;

         switch (dtm.getNodeType(xpathOwnerNodeDTM)) {

         case Node.ATTRIBUTE_NODE : {
            // returns a node-set containing the attribute
            hereNode = xpathOwnerNodeDTM;

            nodeSet.addNode(hereNode);

            break;
         }
         case Node.PROCESSING_INSTRUCTION_NODE : {
            // returns a node-set containing the processing instruction node
            hereNode = xpathOwnerNodeDTM;

            nodeSet.addNode(hereNode);

            break;
         }
         case Node.TEXT_NODE : {
            // returns a node-set containing the parent element of the
            // text node that directly bears the XPath expression
            hereNode = dtm.getParent(xpathOwnerNodeDTM);

            nodeSet.addNode(hereNode);

            break;
         }
         default :
            break;
         }
      }

      /** @todo Do I have to do this detach() call? */
      nodeSet.detach();

      return nodes;
   }

   /**
    * No arguments to process, so this does nothing.
    * @param vars
    * @param globalsSize
    */
   public void fixupVariables(java.util.Vector vars, int globalsSize) {

      // do nothing
   }
}
