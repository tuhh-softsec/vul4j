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
public class XPathFilterCHGPContainer extends ElementProxy
        implements TransformParam {

   /** Field _ATT_FILTER_VALUE_INTERSECT */
   private static final String _TAG_INCLUDE_BUT_SEARCH = "IncludeButSearch";

   /** Field _ATT_FILTER_VALUE_SUBTRACT */
   private static final String _TAG_EXCLUDE_BUT_SEARCH = "ExcludeButSearch";

   /** Field _ATT_FILTER_VALUE_UNION */
   private static final String _TAG_EXCLUDE = "Exclude";

   /** Field _TAG_XPATHCHGP */
   public static final String _TAG_XPATHCHGP = "XPathAlternative";

   /** Field _ATT_INCLUDESLASH */
   public static final String _ATT_INCLUDESLASH = "IncludeSlashPolicy";

   /** Field IncludeSlash           */
   public static final boolean IncludeSlash = true;

   /** Field ExcludeSlash           */
   public static final boolean ExcludeSlash = false;

   /**
    * Constructor XPathFilterCHGPContainer
    *
    */
   private XPathFilterCHGPContainer() {

      // no instantiation
   }

   /**
    * Constructor XPathFilterCHGPContainer
    *
    * @param doc
    * @param includeSlashPolicy
    * @param includeButSearch
    * @param excludeButSearch
    * @param exclude
    */
   private XPathFilterCHGPContainer(Document doc, boolean includeSlashPolicy,
                                    String includeButSearch,
                                    String excludeButSearch, String exclude) {

      super(doc);

      if (includeSlashPolicy) {
         this._constructionElement
            .setAttribute(XPathFilterCHGPContainer._ATT_INCLUDESLASH, "true");
      } else {
         this._constructionElement
            .setAttribute(XPathFilterCHGPContainer._ATT_INCLUDESLASH, "false");
      }

      if ((includeButSearch != null)
              && (includeButSearch.trim().length() > 0)) {
         Element includeButSearchElem =
            this.createElementForFamily(doc, this.getBaseNamespace(),
                                        XPathFilterCHGPContainer
                                           ._TAG_INCLUDE_BUT_SEARCH);

         includeButSearchElem
            .appendChild(this._doc
               .createTextNode(indentXPathText(includeButSearch)));
         this._constructionElement.appendChild(doc.createTextNode("\n"));
         this._constructionElement.appendChild(includeButSearchElem);
      }

      if ((excludeButSearch != null)
              && (excludeButSearch.trim().length() > 0)) {
         Element excludeButSearchElem =
            this.createElementForFamily(doc, this.getBaseNamespace(),
                                        XPathFilterCHGPContainer
                                           ._TAG_EXCLUDE_BUT_SEARCH);

         excludeButSearchElem
            .appendChild(this._doc
               .createTextNode(indentXPathText(excludeButSearch)));
         this._constructionElement.appendChild(doc.createTextNode("\n"));
         this._constructionElement.appendChild(excludeButSearchElem);
      }

      if ((exclude != null) && (exclude.trim().length() > 0)) {
         Element excludeElem = this.createElementForFamily(doc,
                                  this.getBaseNamespace(),
                                  XPathFilterCHGPContainer._TAG_EXCLUDE);

         excludeElem
            .appendChild(this._doc.createTextNode(indentXPathText(exclude)));
         this._constructionElement.appendChild(doc.createTextNode("\n"));
         this._constructionElement.appendChild(excludeElem);
      }

      this._constructionElement.appendChild(doc.createTextNode("\n"));
   }

   /**
    * Method indentXPathText
    *
    * @param xp
    * @return
    */
   static String indentXPathText(String xp) {

      if ((xp.length() > 2) && (!Character.isWhitespace(xp.charAt(0)))) {
         return "\n" + xp + "\n";
      } else {
         return xp;
      }
   }

   /**
    * Constructor XPathFilterCHGPContainer
    *
    * @param element
    * @param BaseURI
    * @throws XMLSecurityException
    */
   private XPathFilterCHGPContainer(Element element, String BaseURI)
           throws XMLSecurityException {
      super(element, BaseURI);
   }

   /**
    * Creates a new XPathFilterCHGPContainer; needed for generation.
    *
    * @param doc
    * @param includeSlashPolicy
    * @param includeButSearch
    * @param excludeButSearch
    * @param exclude
    * @return
    */
   public static XPathFilterCHGPContainer getInstance(Document doc,
           boolean includeSlashPolicy, String includeButSearch,
           String excludeButSearch, String exclude) {

      return new XPathFilterCHGPContainer(doc, includeSlashPolicy,
                                          includeButSearch, excludeButSearch,
                                          exclude);
   }

   /**
    * Creates a XPathFilterCHGPContainer from an existing Element; needed for verification.
    *
    * @param element
    * @param BaseURI
    * @return
    * @throws XMLSecurityException
    */
   public static XPathFilterCHGPContainer getInstance(
           Element element, String BaseURI) throws XMLSecurityException {
      return new XPathFilterCHGPContainer(element, BaseURI);
   }

   /**
    * Method getXStr
    *
    * @param type
    * @return
    */
   private String getXStr(String type) {

      if (this.length(this.getBaseNamespace(), type) != 1) {
         return "";
      }

      Element xElem = this.getChildElementLocalName(0, this.getBaseNamespace(),
                         type);

      return XMLUtils.getFullTextChildrenFromElement(xElem);
   }

   /**
    * Method getIncludeButSearch
    *
    * @return
    */
   public String getIncludeButSearch() {
      return this.getXStr(XPathFilterCHGPContainer._TAG_INCLUDE_BUT_SEARCH);
   }

   /**
    * Method getExcludeButSearch
    *
    * @return
    */
   public String getExcludeButSearch() {
      return this.getXStr(XPathFilterCHGPContainer._TAG_EXCLUDE_BUT_SEARCH);
   }

   /**
    * Method getExclude
    *
    * @return
    */
   public String getExclude() {
      return this.getXStr(XPathFilterCHGPContainer._TAG_EXCLUDE);
   }

   /**
    * Method getIncludeSlashPolicy
    *
    * @return
    */
   public boolean getIncludeSlashPolicy() {

      return this._constructionElement
         .getAttribute(XPathFilterCHGPContainer._ATT_INCLUDESLASH)
         .equals("true");
   }

   /**
    * Returns the first Text node which contains information from the XPath
    * Filter String. We must use this stupid hook to enable the here() function
    * to work.
    *
    * @todo I dunno whether this crashes: <XPath> he<!-- comment -->re()/ds:Signature[1]</XPath>
    * @param type
    * @return the first Text node which contains information from the XPath 2 Filter String
    */
   private Node getHereContextNode(String type) {

      if (this.length(this.getBaseNamespace(), type) != 1) {
         return null;
      }

      Element xElem = this.getChildElementLocalName(0, this.getBaseNamespace(),
                         type);
      NodeList children = xElem.getChildNodes();
      int length = children.getLength();

      for (int i = 0; i < length; i++) {
         if (children.item(i).getNodeType() == Node.TEXT_NODE) {
            return children.item(i);
         }
      }

      return null;
   }

   /**
    * Method getHereContextNodeIncludeButSearch
    *
    * @return
    */
   public Node getHereContextNodeIncludeButSearch() {
      return this
         .getHereContextNode(XPathFilterCHGPContainer._TAG_INCLUDE_BUT_SEARCH);
   }

   /**
    * Method getHereContextNodeExcludeButSearch
    *
    * @return
    */
   public Node getHereContextNodeExcludeButSearch() {
      return this
         .getHereContextNode(XPathFilterCHGPContainer._TAG_EXCLUDE_BUT_SEARCH);
   }

   /**
    * Method getHereContextNodeExclude
    *
    * @return
    */
   public Node getHereContextNodeExclude() {
      return this.getHereContextNode(XPathFilterCHGPContainer._TAG_EXCLUDE);
   }

   /**
    * Method getBaseLocalName
    *
    * @return
    */
   public final String getBaseLocalName() {
      return XPathFilterCHGPContainer._TAG_XPATHCHGP;
   }

   /**
    * Method getBaseNamespace
    *
    * @return
    */
   public final String getBaseNamespace() {
      return Transforms.TRANSFORM_XPATHFILTERCHGP;
   }
}
