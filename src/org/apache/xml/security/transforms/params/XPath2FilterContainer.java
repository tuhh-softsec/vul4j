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
import org.apache.xml.security.exceptions.*;
import org.apache.xml.security.transforms.TransformParam;
import org.apache.xml.security.transforms.TransformationException;
import org.apache.xml.security.transforms.Transforms;


/**
 * Implements the parameters for the <I>XML Signature XPath Filter v2.0</I>
 *
 * @author $Author$
 * @see <A HREF="http://www.w3.org/TR/xmldsig-filter2/">XPath Filter v2.0 (TR)</A>
 * @see <A HREF=http://www.w3.org/Signature/Drafts/xmldsig-xfilter2/">XPath Filter v2.0 (editors copy)</A>
 */
public class XPath2FilterContainer extends ElementProxy
        implements TransformParam {

   /** Field _ATT_FILTER */
   private static final String _ATT_FILTER = "Filter";

   /** Field _ATT_FILTER_VALUE_INTERSECT */
   private static final String _ATT_FILTER_VALUE_INTERSECT = "intersect";

   /** Field _ATT_FILTER_VALUE_SUBTRACT */
   private static final String _ATT_FILTER_VALUE_SUBTRACT = "subtract";

   /** Field _ATT_FILTER_VALUE_UNION */
   private static final String _ATT_FILTER_VALUE_UNION = "union";

   /** Field _TAG_XPATH2 */
   public static final String _TAG_XPATH2 = "XPath";

   /** Field XPathFiler2NS */
   public static final String XPathFilter2NS =
      "http://www.w3.org/2002/04/xmldsig-filter2";

   /**
    * Constructor XPath2FilterContainer
    *
    */
   private XPath2FilterContainer() {

      // no instantiation
   }

   /**
    * Constructor XPath2FilterContainer
    *
    * @param doc
    * @param xpath2filter
    * @param filterType
    */
   private XPath2FilterContainer(Document doc, String xpath2filter,
                                 String filterType) {

      super(doc);

      this._constructionElement.setAttribute(XPath2FilterContainer._ATT_FILTER,
                                             filterType);

      if ((xpath2filter.length() > 2)
              && (!Character.isWhitespace(xpath2filter.charAt(0)))) {
         this._constructionElement.appendChild(doc.createTextNode("\n"
                 + xpath2filter + "\n"));
      } else {
         this._constructionElement
            .appendChild(doc.createTextNode(xpath2filter));
      }
   }

   /**
    * Constructor XPath2FilterContainer
    *
    * @param element
    * @param BaseURI
    * @throws XMLSecurityException
    */
   private XPath2FilterContainer(Element element, String BaseURI)
           throws XMLSecurityException {

      super(element, BaseURI);

      String filterStr =
         this._constructionElement
            .getAttribute(XPath2FilterContainer._ATT_FILTER);

      if (!filterStr
              .equals(XPath2FilterContainer
              ._ATT_FILTER_VALUE_INTERSECT) &&!filterStr
                 .equals(XPath2FilterContainer
                 ._ATT_FILTER_VALUE_SUBTRACT) &&!filterStr
                    .equals(XPath2FilterContainer._ATT_FILTER_VALUE_UNION)) {
         Object exArgs[] = { XPath2FilterContainer._ATT_FILTER, filterStr,
                             XPath2FilterContainer._ATT_FILTER_VALUE_INTERSECT
                             + ", "
                             + XPath2FilterContainer._ATT_FILTER_VALUE_SUBTRACT
                             + " or "
                             + XPath2FilterContainer._ATT_FILTER_VALUE_UNION };

         throw new XMLSecurityException("attributeValueIllegal", exArgs);
      }
   }

   /**
    * Creates a new XPath2FilterContainer with the filter type "intersect".
    *
    * @param doc
    * @param xpath2filter
    * @return
    */
   public static XPath2FilterContainer newInstanceIntersect(Document doc,
           String xpath2filter) {

      return new XPath2FilterContainer(doc, xpath2filter,
                                       XPath2FilterContainer
                                          ._ATT_FILTER_VALUE_INTERSECT);
   }

   /**
    * Creates a new XPath2FilterContainer with the filter type "subtract".
    *
    * @param doc
    * @param xpath2filter
    * @return
    */
   public static XPath2FilterContainer newInstanceSubtract(Document doc,
           String xpath2filter) {

      return new XPath2FilterContainer(doc, xpath2filter,
                                       XPath2FilterContainer
                                          ._ATT_FILTER_VALUE_SUBTRACT);
   }

   /**
    * Creates a new XPath2FilterContainer with the filter type "union".
    *
    * @param doc
    * @param xpath2filter
    * @return
    */
   public static XPath2FilterContainer newInstanceUnion(Document doc,
           String xpath2filter) {

      return new XPath2FilterContainer(doc, xpath2filter,
                                       XPath2FilterContainer
                                          ._ATT_FILTER_VALUE_UNION);
   }

   /**
    * Creates a XPath2FilterContainer from an existing Element; needed for verification.
    *
    * @param element
    * @param BaseURI
    * @return
    * @throws XMLSecurityException
    */
   public static XPath2FilterContainer newInstance(
           Element element, String BaseURI) throws XMLSecurityException {
      return new XPath2FilterContainer(element, BaseURI);
   }

   /**
    * Returns <code>true</code> if the <code>Filter</code> attribute has value "intersect".
    *
    * @return <code>true</code> if the <code>Filter</code> attribute has value "intersect".
    */
   public boolean isIntersect() {

      return this._constructionElement
         .getAttribute(XPath2FilterContainer._ATT_FILTER)
         .equals(XPath2FilterContainer._ATT_FILTER_VALUE_INTERSECT);
   }

   /**
    * Returns <code>true</code> if the <code>Filter</code> attribute has value "subtract".
    *
    * @return <code>true</code> if the <code>Filter</code> attribute has value "subtract".
    */
   public boolean isSubtract() {

      return this._constructionElement
         .getAttribute(XPath2FilterContainer._ATT_FILTER)
         .equals(XPath2FilterContainer._ATT_FILTER_VALUE_SUBTRACT);
   }

   /**
    * Returns <code>true</code> if the <code>Filter</code> attribute has value "union".
    *
    * @return <code>true</code> if the <code>Filter</code> attribute has value "union".
    */
   public boolean isUnion() {

      return this._constructionElement
         .getAttribute(XPath2FilterContainer._ATT_FILTER)
         .equals(XPath2FilterContainer._ATT_FILTER_VALUE_UNION);
   }

   /**
    * Returns the XPath 2 Filter String
    *
    * @return the XPath 2 Filter String
    */
   public String getXPathFilterStr() {
      return this.getTextFromTextChild();
   }

   /**
    * Returns the first Text node which contains information from the XPath 2
    * Filter String. We must use this stupid hook to enable the here() function
    * to work.
    *
    * @todo I dunno whether this crashes: <XPath> here()<!-- comment -->/ds:Signature[1]</XPath>
    * @return the first Text node which contains information from the XPath 2 Filter String
    */
   public Node getXPathFilterTextNode() {
      NodeList children = this._constructionElement.getChildNodes();
      int length = children.getLength();

      for (int i = 0; i < length; i++) {
         if (children.item(i).getNodeType() == Node.TEXT_NODE) {
            return children.item(i);
         }
      }

      return null;
   }

   /**
    * Method getBaseLocalName
    *
    * @return
    */
   public final String getBaseLocalName() {
      return XPath2FilterContainer._TAG_XPATH2;
   }

   /**
    * Method getBaseNamespace
    *
    * @return
    */
   public final String getBaseNamespace() {
      return XPath2FilterContainer.XPathFilter2NS;
   }
}
