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



import org.w3c.dom.*;
import org.apache.xerces.dom.DocumentImpl;
import javax.xml.transform.TransformerException;
import org.apache.xpath.XPathAPI;
import org.apache.xml.security.utils.*;
import org.apache.xml.security.Init;


/**
 * Purpose of this class is to enable the XML Parser to keep track of ID
 * attributes. This is done by 'registering' attributes of type ID at the
 * IdResolver. This is necessary if we create a document from scratch and we
 * sign some resources with a URI using a fragent identifier...
 * <BR />
 * The problem is that if you do not validate a document, you cannot use the
 * <CODE>getElementByID</CODE> functionality. So this modules uses some implicit
 * knowledge on selected Schemas and DTDs to pick the right Element for a given
 * ID: We know that all <CODE>@Id</CODE> attributes in an Element from the XML
 * Signature namespace are of type <CODE>ID</CODE>.
 *
 * @author $Author$
 * @see org.apache.xml.security.utils.resolver.implementations.ResolverFragment
 * @see <A HREF="http://www.xml.com/lpt/a/2001/11/07/id.html">"Identity Crisis" on xml.com</A>
 */
public class IdResolver {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(IdResolver.class.getName());

   /**
    * Constructor IdResolver
    *
    */
   private IdResolver() {

      // we don't allow instantiation
   }

   /**
    * Method registerElementById
    *
    * @param element
    * @param idValue
    */
   public static void registerElementById(Element element, String idValue) {

      Document doc = element.getOwnerDocument();

      ((org.apache.xerces.dom.DocumentImpl) doc).putIdentifier(idValue,
              element);
   }

   /**
    * Method registerElementById
    *
    * @param element
    * @param id
    */
   public static void registerElementById(Element element, Attr id) {
      IdResolver.registerElementById(element, id.getNodeValue());
   }

   /**
    * Method getElementById
    *
    * @param doc
    * @param id
    * @return
    */
   public static Element getElementById(Document doc, String id) {

      Element result = null;

      result = IdResolver.getElementByIdType(doc, id);

      if (result != null) {
         cat.debug(
            "I could find an Element using the simple getElementById method: "
            + result.getTagName());

         return result;
      }

      result = IdResolver.getElementByIdInDSNamespace(doc, id);

      if (result != null) {
         cat.debug(
            "I could find an Element using the advanced ds:Namespace searcher method: "
            + result.getTagName());

         // register the ID to speed up further queries on that ID
         IdResolver.registerElementById(result, id);

         return result;
      }

      result = IdResolver.getElementByIdInXENCNamespace(doc, id);

      if (result != null) {
         cat.debug(
            "I could find an Element using the advanced xenc:Namespace searcher method: "
            + result.getTagName());

         // register the ID to speed up further queries on that ID
         IdResolver.registerElementById(result, id);

         return result;
      }

      result = IdResolver.getElementByIdInSOAPSignatureNamespace(doc, id);

      if (result != null) {
         cat.debug(
            "I could find an Element using the advanced SOAP-SEC:id searcher method: "
            + result.getTagName());

         // register the ID to speed up further queries on that ID
         IdResolver.registerElementById(result, id);

         return result;
      }

      result = IdResolver.getElementByIdInXKMSNamespace(doc, id);

      if (result != null) {
         cat.debug("I could find an Element using the XKMS searcher method: "
                   + result.getTagName());

         // register the ID to speed up further queries on that ID
         IdResolver.registerElementById(result, id);

         return result;
      }

      result = IdResolver.getElementByIdUnsafeMatchByIdName(doc, id);

      if (result != null) {
         cat.warn(
            "I could find an Element using the totally stupid and insecure Id/ID/id searcher method: "
            + result.getTagName());

         // Don't register the ID, we're not sure
         return result;
      }

      return null;
   }

   /**
    * Method getElementByIdType
    *
    * @param doc
    * @param id
    * @return
    */
   private static Element getElementByIdType(Document doc, String id) {

      cat.debug("getElementByIdType() Search for ID " + id);

      return doc.getElementById(id);
   }

   /**
    * Method getElementByIdInDSNamespace
    *
    * @param doc
    * @param id
    * @return
    */
   private static Element getElementByIdInDSNamespace(Document doc, String id) {

      cat.debug("getElementByIdInDSNamespace() Search for ID " + id);

      try {
         Element nscontext = XMLUtils.createDSctx(doc, "ds",
                                                  Constants.SignatureSpecNS);
         Element element = (Element) XPathAPI.selectSingleNode(doc,
                              "//ds:*[@Id='" + id + "']", nscontext);

         return element;

         /*
         NodeList dsElements = XPathAPI.selectNodeList(doc, "//ds:*",
                                  nscontext);

         cat.debug("Found ds:Elements: " + dsElements.getLength());

         for (int i = 0; i < dsElements.getLength(); i++) {
            Element currentElem = (Element) dsElements.item(i);
            Attr IdAttr = currentElem.getAttributeNode(Constants._ATT_ID);

            if (IdAttr != null) {
               if (IdAttr.getNodeValue().equals(id)) {
                  return currentElem;
               }
            }
         }
         */
      } catch (TransformerException ex) {
         cat.fatal("empty", ex);
      }

      return null;
   }

   /**
    * Method getElementByIdInXENCNamespace
    *
    * @param doc
    * @param id
    * @return
    */
   private static Element getElementByIdInXENCNamespace(Document doc,
           String id) {

      cat.debug("getElementByIdInXENCNamespace() Search for ID " + id);

      try {
         Element nscontext =
            XMLUtils.createDSctx(doc, "xenc",
                                 org.apache.xml.security.utils
                                    .EncryptionConstants.EncryptionSpecNS);
         Element element = (Element) XPathAPI.selectSingleNode(doc,
                              "//xenc:*[@Id='" + id + "']", nscontext);

         return element;
      } catch (TransformerException ex) {
         cat.fatal("empty", ex);
      }

      return null;
   }

   /**
    * Method getElementByIdInSOAPSignatureNamespace
    *
    * @param doc
    * @param id
    * @return
    */
   private static Element getElementByIdInSOAPSignatureNamespace(Document doc,
           String id) {

      cat.debug("getElementByIdInSOAPSignatureNamespace() Search for ID " + id);

      try {
         Element nscontext = XMLUtils.createDSctx(
            doc, "SOAP-SEC",
            "http://schemas.xmlsoap.org/soap/security/2000-12");
         Element element = (Element) XPathAPI.selectSingleNode(doc,
                              "//*[@SOAP-SEC:id='" + id + "']", nscontext);

         return element;
      } catch (TransformerException ex) {
         cat.fatal("empty", ex);
      }

      return null;
   }

   /**
    * Method getElementByIdInXKMSNamespace
    *
    * @param doc
    * @param id
    * @return
    * @see http://www.w3c.org/2001/XKMS/Drafts/XKMS-20020410
    */
   private static Element getElementByIdInXKMSNamespace(Document doc,
           String id) {

      /*
      xmlns:xkms="http://www.w3.org/2002/03/xkms#"

      <attribute name="ID"                type="ID" use="optional"/>
      <attribute name="OriginalRequestID" type="ID" use="optional"/>
      <attribute name="RequestID"         type="ID" use="optional"/>
      <attribute name="ResponseID"        type="ID" use="required"/>
      */
      cat.debug("getElementByIdInXKMSNamespace() Search for ID " + id);

      try {
         Element nscontext =
            XMLUtils.createDSctx(doc, "xkms",
                                 "http://www.w3.org/2002/03/xkms#");
         String[] attrs = { "ID", "OriginalRequestID", "RequestID",
                            "ResponseID" };

         for (int i = 0; i < attrs.length; i++) {
            String attr = attrs[i];
            Element element = (Element) XPathAPI.selectSingleNode(doc,
                                 "//xkms:*[@" + attr + "='" + id + "']",
                                 nscontext);

            if (element != null) {
               return element;
            }
         }

         return null;
      } catch (TransformerException ex) {
         cat.fatal("empty", ex);
      }

      return null;
   }

   /**
    * Method getElementByIdUnsafeMatchByIdName
    *
    * @param doc
    * @param id
    * @return
    */
   private static Element getElementByIdUnsafeMatchByIdName(Document doc,
           String id) {

      cat.debug("getElementByIdUnsafeMatchByIdName() Search for ID " + id);

      try {
         Element element_Id = (Element) XPathAPI.selectSingleNode(doc,
                                 "//*[@Id='" + id + "']");

         if (element_Id != null) {
            return element_Id;
         }

         Element element_ID = (Element) XPathAPI.selectSingleNode(doc,
                                 "//*[@ID='" + id + "']");

         if (element_ID != null) {
            return element_ID;
         }

         Element element_id = (Element) XPathAPI.selectSingleNode(doc,
                                 "//*[@id='" + id + "']");

         if (element_id != null) {
            return element_id;
         }
      } catch (TransformerException ex) {
         cat.fatal("empty", ex);
      }

      return null;
   }
}
