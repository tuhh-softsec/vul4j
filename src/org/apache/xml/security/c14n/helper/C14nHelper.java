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
package org.apache.xml.security.c14n.helper;



import org.w3c.dom.*;
import java.util.Comparator;
import java.util.Arrays;
import org.apache.xml.utils.URI;
import java.net.MalformedURLException;
import org.apache.xml.security.c14n.CanonicalizationException;


/**
 * Temporary swapped static functions from the normalizer Section
 *
 * @author Christian Geuer-Pollmann
 */
public class C14nHelper {

   /**
    * Constructor C14nHelper
    *
    */
   private C14nHelper() {

      // don't allow instantiation
   }

   /**
    * Method sortAttributes
    *
    * @param namednodemap
    * @return
   public static final Attr[] sortAttributes(NamedNodeMap namednodemap) {

      if (namednodemap == null) {
         return new Attr[0];
      }

      Attr aattr[] = new Attr[namednodemap.getLength()];

      for (int j = 0; j < namednodemap.getLength(); j++) {
         aattr[j] = (Attr) namednodemap.item(j);
      }

      java.util.Arrays.sort(aattr, new AttrCompare());

      // java.util.Sort.quicksort(aattr, new AttrCompare());
      return aattr;
   }
    */

   /**
    * Method sortAttributes
    *
    * @param namednodemap
    * @return
   public static final Attr[] sortAttributes(Attr[] namednodemap) {

      if (namednodemap == null) {
         return new Attr[0];
      }

      java.util.Arrays.sort(namednodemap, new AttrCompare());

      // java.util.Sort.quicksort(namednodemap, new AttrCompare());
      return namednodemap;
   }
    */

   /**
    * Method sortAttributes
    *
    * @param namednodemap
    * @return
   public static final Object[] sortAttributes(Object[] namednodemap) {

      for (

      java.util.Arrays.sort(namednodemap, new AttrCompare());

      // java.util.Sort.quicksort(namednodemap, new AttrCompare());
      return namednodemap;
   }
    */

   /**
    * Normalizes an {@link Attr}ibute value
    *
    * The string value of the node is modified by replacing
    * <UL>
    * <LI>all ampersands (&) with <CODE>&amp;amp;</CODE></LI>
    * <LI>all open angle brackets (<) with <CODE>&amp;lt;</CODE></LI>
    * <LI>all quotation mark characters with <CODE>&amp;quot;</CODE></LI>
    * <LI>and the whitespace characters <CODE>#x9</CODE>, #xA, and #xD, with character
    * references. The character references are written in uppercase
    * hexadecimal with no leading zeroes (for example, <CODE>#xD</CODE> is represented
    * by the character reference <CODE>&amp;#xD;</CODE>)</LI>
    * </UL>
    *
    * @param s
    * @return the normalized {@link org.w3c.dom.Attr}ibute value ((({@link String})
    */
   public static final String normalizeAttr(String s) {

      StringBuffer stringbuffer = new StringBuffer();
      int i = (s == null)
              ? 0
              : s.length();

      for (int j = 0; j < i; j++) {
         char c = s.charAt(j);

         switch (c) {

         case '&' :
            stringbuffer.append("&amp;");
            break;

         case '<' :
            stringbuffer.append("&lt;");
            break;

         case '"' :
            stringbuffer.append("&quot;");
            break;

         case 0x09 :    // '\t'
            stringbuffer.append("&#x9;");
            break;

         case 0x0A :    // '\n'
            stringbuffer.append("&#xA;");
            break;

         case 0x0D :    // '\r'
            stringbuffer.append("&#xD;");
            break;

         default :
            stringbuffer.append(c);
            break;
         }
      }

      return stringbuffer.toString();
   }

   /**
    * Normalizes a {@link org.w3c.dom.Comment} value
    *
    * @param s
    * @return the normalized {@link org.w3c.dom.Comment} value ((({@link String})
    */
   public static final String normalizeComment(String s) {
      return normalizeProcessingInstruction(s);
   }

   /**
    * Normalizes a {@link org.w3c.dom.ProcessingInstruction} value
    *
    * @param s
    * @return the normalized {@link org.w3c.dom.ProcessingInstruction} value ((({@link String})
    */
   public static final String normalizeProcessingInstruction(String s) {

      StringBuffer stringbuffer = new StringBuffer();
      int i = (s == null)
              ? 0
              : s.length();

      for (int j = 0; j < i; j++) {
         char c = s.charAt(j);

         switch (c) {

         case 0x0D :
            stringbuffer.append("&#xD;");
            break;

         default :
            stringbuffer.append(c);
            break;
         }
      }

      return stringbuffer.toString();
   }

   /**
    * Normalizes a {@link Text} value
    *
    * <p>Text Nodes - the string value, except all ampersands (&amp; are replaced by
    * &amp;amp;, all open angle brackets (<) are replaced by &amp;lt;, all closing
    * angle brackets (>) are replaced by &amp;gt;, and all #xD characters
    * are replaced by &amp;#xD;. (See <A
    * HREF="http://www.w3.org/TR/2001/REC-xml-c14n-20010315#ProcessingModel">
    * processing model section in the specification</A>)</p>
    *
    * @param s
    * @return the normalized {@link Text} value ((({@link String})
    */
   public static final String normalizeText(String s) {

      StringBuffer stringbuffer = new StringBuffer();
      int i = (s == null)
              ? 0
              : s.length();

      for (int j = 0; j < i; j++) {
         char c = s.charAt(j);

         switch (c) {

         case '&' :
            stringbuffer.append("&amp;");
            break;

         case '<' :
            stringbuffer.append("&lt;");
            break;

         case '>' :
            stringbuffer.append("&gt;");
            break;

         case 0xD :
            stringbuffer.append("&#xD;");
            break;

         default :
            stringbuffer.append(c);
            break;
         }
      }

      return stringbuffer.toString();
   }

   /**
    * Method namespaceIsRelative
    *
    * @param namespace
    * @return
    */
   public static boolean namespaceIsRelative(Attr namespace) {
      return !namespaceIsAbsolute(namespace);
   }

   /**
    * Method namespaceIsRelative
    *
    * @param namespaceValue
    * @return
    */
   public static boolean namespaceIsRelative(String namespaceValue) {
      return !namespaceIsAbsolute(namespaceValue);
   }

   /**
    * Method namespaceIsAbsolute
    *
    * @param namespace
    * @return
    */
   public static boolean namespaceIsAbsolute(Attr namespace) {
      return namespaceIsAbsolute(namespace.getValue());
   }

   /**
    * Method namespaceIsAbsolute
    *
    * @param namespaceValue
    * @return
    */
   public static boolean namespaceIsAbsolute(String namespaceValue) {

      // assume empty namespaces are absolute
      if (namespaceValue.length() == 0) {
         return true;
      }

      boolean foundColon = false;
      int length = namespaceValue.length();

      for (int i = 0; i < length; i++) {
         char c = namespaceValue.charAt(i);

         if (c == ':') {
            foundColon = true;
         } else if (!foundColon && (c == '/')) {
            return false;
         }
      }

      return foundColon;

      /*
      try {
         URI uri = new URI(namespaceValue);
         String Scheme = uri.getScheme();
         boolean protocolOK = false;

         if (Scheme != null) {
            protocolOK = uri.getScheme().length() > 0;

            if (Scheme.equals("urn")) {
               return true;
            }
         }

         boolean hostOK = false;
         String Host = uri.getHost();

         if (Host != null) {
            hostOK = uri.getHost().length() > 0;
         }

         return (protocolOK && hostOK);
      } catch (URI.MalformedURIException ex) {
         return false;
      }
      */
   }

   /**
    * This method throws an exception if the Attribute value contains
    * a relative URI.
    *
    * @param attr
    * @throws CanonicalizationException
    */
   public static void assertNotRelativeNS(Attr attr)
           throws CanonicalizationException {

      if (attr == null) {
         return;
      }

      String nodeAttrName = attr.getNodeName();
      boolean definesDefaultNS = nodeAttrName.equals("xmlns");
      boolean definesNonDefaultNS = nodeAttrName.startsWith("xmlns:");

      if (definesDefaultNS || definesNonDefaultNS) {
         if (namespaceIsRelative(attr)) {
            String parentName = attr.getOwnerElement().getTagName();
            String attrValue = attr.getValue();
            Object exArgs[] = { parentName, nodeAttrName, attrValue };

            throw new CanonicalizationException(
               "c14n.Canonicalizer.RelativeNamespace", exArgs);
         }
      }
   }

   /**
    * This method throws a CanonicalizationException if the supplied Document
    * is not able to be traversed using a TreeWalker.
    *
    * @param document
    * @throws CanonicalizationException
    */
   public static void checkTraversability(Document document)
           throws CanonicalizationException {

      if (!document.isSupported("Traversal", "2.0")) {
         Object exArgs[] = {
            document.getImplementation().getClass().getName() };

         throw new CanonicalizationException(
            "c14n.Canonicalizer.TraversalNotSupported", exArgs);
      }
   }

   /**
    * This method throws a CanonicalizationException if the supplied Element
    * contains any relative namespaces.
    *
    * @param ctxNode
    * @throws CanonicalizationException
    * @see C14nHelper#assertNotRelativeNS(Attr)
    */
   public static void checkForRelativeNamespace(Element ctxNode)
           throws CanonicalizationException {

      if (ctxNode != null) {
         NamedNodeMap attributes = ctxNode.getAttributes();

         for (int i = 0; i < attributes.getLength(); i++) {
            C14nHelper.assertNotRelativeNS((Attr) attributes.item(i));
         }
      } else {
         throw new CanonicalizationException(
            "Called checkForRelativeNamespace() on null");
      }
   }
}
