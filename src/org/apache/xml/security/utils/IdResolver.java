/*
 * Copyright  1999-2004 The Apache Software Foundation.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.xml.security.utils;



import javax.xml.transform.TransformerException;

import org.apache.xpath.XPathAPI;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


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
 * @see <A HREF="http://www.xml.com/lpt/a/2001/11/07/id.html">"Identity Crisis" on xml.com</A>
 */
public class IdResolver {

   /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log =
        org.apache.commons.logging.LogFactory.getLog(IdResolver.class.getName());

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
    *
    */
   public static Element getElementById(Document doc, String id) {

      Element result = null;

      result = IdResolver.getElementByIdType(doc, id);

      if (result != null) {
         log.debug(
            "I could find an Element using the simple getElementById method: "
            + result.getTagName());

         return result;
      }

      result = IdResolver.getElementByIdInDSNamespace(doc, id);

      if (result != null) {
         log.debug(
            "Found an Element using an insecure Id/ID/id search method: "
            + result.getTagName());

         // register the ID to speed up further queries on that ID
         IdResolver.registerElementById(result, id);

         return result;
      }

      result = IdResolver.getElementByIdInXENCNamespace(doc, id);

      if (result != null) {
         log.debug(
            "I could find an Element using the advanced xenc:Namespace searcher method: "
            + result.getTagName());

         // register the ID to speed up further queries on that ID
         IdResolver.registerElementById(result, id);

         return result;
      }

      result = IdResolver.getElementByIdInSOAPSignatureNamespace(doc, id);

      if (result != null) {
         log.debug(
            "I could find an Element using the advanced SOAP-SEC:id searcher method: "
            + result.getTagName());

         // register the ID to speed up further queries on that ID
         IdResolver.registerElementById(result, id);

         return result;
      }

      result = IdResolver.getElementByIdInXKMSNamespace(doc, id);

      if (result != null) {
         log.debug("I could find an Element using the XKMS searcher method: "
                   + result.getTagName());

         // register the ID to speed up further queries on that ID
         IdResolver.registerElementById(result, id);

         return result;
      }

      result = IdResolver.getElementByIdUnsafeMatchByIdName(doc, id);

      if (result != null) {
         log.warn(
            "Found an Element using an insecure Id/ID/id search method: "
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
    *
    */
   private static Element getElementByIdType(Document doc, String id) {

      log.debug("getElementByIdType() Search for ID " + id);

      return doc.getElementById(id);
   }

   /**
    * Method getElementByIdInDSNamespace
    *
    * @param doc
    * @param id
    *
    */
   private static Element getElementByIdInDSNamespace(Document doc, String id) {

      log.debug("getElementByIdInDSNamespace() Search for ID " + id);

      try {
         Element nscontext = XMLUtils.createDSctx(doc, "ds",
                                                  Constants.SignatureSpecNS);
         Element element = (Element) XPathAPI.selectSingleNode(doc,
                              "//ds:*[@Id='" + id + "']", nscontext);

         return element;

         /*
         NodeList dsElements = XPathAPI.selectNodeList(doc, "//ds:*",
                                  nscontext);

         log.debug("Found ds:Elements: " + dsElements.getLength());

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
         log.fatal("empty", ex);
      }

      return null;
   }

   /**
    * Method getElementByIdInXENCNamespace
    *
    * @param doc
    * @param id
    *
    */
   private static Element getElementByIdInXENCNamespace(Document doc,
           String id) {

      log.debug("getElementByIdInXENCNamespace() Search for ID " + id);

      try {
         Element nscontext =
            XMLUtils.createDSctx(doc, "xenc",
                                 org.apache.xml.security.utils
                                    .EncryptionConstants.EncryptionSpecNS);
         Element element = (Element) XPathAPI.selectSingleNode(doc,
                              "//xenc:*[@Id='" + id + "']", nscontext);

         return element;
      } catch (TransformerException ex) {
         log.fatal("empty", ex);
      }

      return null;
   }

   /**
    * Method getElementByIdInSOAPSignatureNamespace
    *
    * @param doc
    * @param id
    *
    */
   private static Element getElementByIdInSOAPSignatureNamespace(Document doc,
           String id) {

      log.debug("getElementByIdInSOAPSignatureNamespace() Search for ID " + id);

      try {
         Element nscontext = XMLUtils.createDSctx(
            doc, "SOAP-SEC",
            "http://schemas.xmlsoap.org/soap/security/2000-12");
         Element element = (Element) XPathAPI.selectSingleNode(doc,
                              "//*[@SOAP-SEC:id='" + id + "']", nscontext);

         return element;
      } catch (TransformerException ex) {
         log.fatal("empty", ex);
      }

      return null;
   }

   /**
    * Method getElementByIdInXKMSNamespace
    *
    * @param doc
    * @param id
    *
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
      log.debug("getElementByIdInXKMSNamespace() Search for ID " + id);

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
         log.fatal("empty", ex);
      }

      return null;
   }

   /**
    * Method getElementByIdUnsafeMatchByIdName
    *
    * @param doc
    * @param id
    *
    */
   private static Element getElementByIdUnsafeMatchByIdName(Document doc,
           String id) {

      log.debug("getElementByIdUnsafeMatchByIdName() Search for ID " + id);

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
         log.fatal("empty", ex);
      }

      return null;
   }
}
