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
package org.apache.xml.security.transforms.params;



import org.w3c.dom.*;
import org.apache.xml.security.utils.*;
import org.apache.xml.security.transforms.TransformParam;
import org.apache.xml.security.transforms.TransformationException;
import org.apache.xalan.extensions.ExpressionContext;
import org.apache.xpath.NodeSet;
import org.apache.xpath.XPathAPI;


/**
 * This Object serves both as namespace prefix resolver and as container for
 * the <CODE>ds:XPath</CODE> Element. It implements the {@link Element} interface
 * and can be used directly in a DOM tree.
 *
 * @author Christian Geuer-Pollmann
 */
public class XPathContainer extends SignatureElementProxy implements TransformParam {

   /**
    * Constructor XPathContainer
    *
    * @param doc
    */
   public XPathContainer(Document doc) {
      super(doc);
   }

   /**
    * Sets the TEXT value of the <CODE>ds:XPath</CODE> Element.
    *
    * @param xpath
    */
   public void setXPath(String xpath) {

      if (this._constructionElement.getChildNodes() != null) {
         NodeList nl = this._constructionElement.getChildNodes();

         for (int i = 0; i < nl.getLength(); i++) {
            this._constructionElement.removeChild(nl.item(i));
         }
      }

      Text xpathText = this._doc.createTextNode(xpath);
      this._constructionElement.appendChild(xpathText);
   }

   /**
    * Returns the TEXT value of the <CODE>ds:XPath</CODE> Element.
    *
    * @return the TEXT value of the <CODE>ds:XPath</CODE> Element.
    */
   public String getXPath() {
      return this.getTextFromTextChild();
   }

   public String getBaseLocalName() {
      return Constants._TAG_XPATH;
   }
}
