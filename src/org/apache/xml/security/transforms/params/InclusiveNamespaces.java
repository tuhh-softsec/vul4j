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



import java.io.*;
import java.util.*;
import org.w3c.dom.*;
import org.apache.xml.security.utils.*;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.transforms.TransformParam;
import org.apache.xml.security.transforms.TransformationException;
import org.apache.xalan.extensions.ExpressionContext;
import org.apache.xpath.NodeSet;
import org.apache.xpath.XPathAPI;


/**
 * This Object serves as Content for the ds:Transforms for exclusive
 * Canonicalization.
 * <BR />
 * It implements the {@link Element} interface
 * and can be used directly in a DOM tree.
 *
 * @author Christian Geuer-Pollmann
 */
public class InclusiveNamespaces extends ElementProxy
        implements TransformParam {

   /** Field _TAG_EC_INCLUSIVENAMESPACES */
   public static final String _TAG_EC_INCLUSIVENAMESPACES =
      "InclusiveNamespaces";

   /** Field _ATT_EC_PREFIXLIST */
   public static final String _ATT_EC_PREFIXLIST = "PrefixList";

   /** Field ExclusiveCanonicalizationNamespace */
   public static final String ExclusiveCanonicalizationNamespace =
      "http://www.w3.org/2001/10/xml-exc-c14n#";

   /**
    * Constructor XPathContainer
    *
    * @param doc
    * @param prefixList
    */
   public InclusiveNamespaces(Document doc, String prefixList) {
      this(doc, InclusiveNamespaces.prefixStr2Set(prefixList));
   }

   /**
    * Constructor InclusiveNamespaces
    *
    * @param doc
    * @param prefixes
    */
   public InclusiveNamespaces(Document doc, Set prefixes) {

      super(doc);

      StringBuffer sb = new StringBuffer();
      List prefixList = new Vector(prefixes);

      Collections.sort(prefixList);

      Iterator it = prefixList.iterator();

      while (it.hasNext()) {
         String prefix = (String) it.next();

         if (prefix.equals("xmlns")) {
            sb.append("#default ");
         } else {

            sb.append(prefix.substring("xmlns:".length()) + " ");
         }
      }

      this._constructionElement
         .setAttributeNS(null, InclusiveNamespaces._ATT_EC_PREFIXLIST,
                       sb.toString().trim());
   }

   /**
    * Method getInclusiveNamespaces
    *
    * @return
    */
   public String getInclusiveNamespaces() {
      return this._constructionElement
         .getAttributeNS(null, InclusiveNamespaces._ATT_EC_PREFIXLIST);
   }

   /**
    * Constructor InclusiveNamespaces
    *
    * @param element
    * @param BaseURI
    * @throws XMLSecurityException
    */
   public InclusiveNamespaces(Element element, String BaseURI)
           throws XMLSecurityException {
      super(element, BaseURI);
   }

   /**
    * Decodes the <code>inclusiveNamespaces</code> String and returns all
    * selected namespace prefixes as a Set. The <code>#default</code>
    * namespace token is represented as an empty namespace prefix
    * (<code>"xmlns"</code>).
    * <BR/>
    * The String <code>inclusiveNamespaces=" xenc    ds #default"</code>
    * is returned as a Set containing the following Strings:
    * <UL>
    * <LI><code>xmlns</code></LI>
    * <LI><code>xmlns:xenc</code></LI>
    * <LI><code>xmlns:ds</code></LI>
    * </UL>
    *
    * @param inclusiveNamespaces
    * @return
    */
   public static Set prefixStr2Set(String inclusiveNamespaces) {

      Set prefixes = new HashSet();

      if ((inclusiveNamespaces == null)
              || (inclusiveNamespaces.length() == 0)) {
         return prefixes;
      }

      StringTokenizer st = new StringTokenizer(inclusiveNamespaces, " \t\r\n");

      while (st.hasMoreTokens()) {
         String prefix = st.nextToken();

         if (prefix.equals("#default")) {
            prefixes.add("xmlns" + "");
         } else {
            prefixes.add("xmlns:" + prefix);
         }
      }

      return prefixes;
   }

   /**
    * Method getBaseNamespace
    *
    * @return
    */
   public String getBaseNamespace() {
      return InclusiveNamespaces.ExclusiveCanonicalizationNamespace;
   }

   /**
    * Method getBaseLocalName
    *
    * @return
    */
   public String getBaseLocalName() {
      return InclusiveNamespaces._TAG_EC_INCLUSIVENAMESPACES;
   }
}
