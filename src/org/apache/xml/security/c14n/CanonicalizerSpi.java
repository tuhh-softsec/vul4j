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
package org.apache.xml.security.c14n;



import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.HashMap;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.apache.xml.security.exceptions.*;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.utils.I18n;


/**
 * Base class which all Caninicalization algorithms extend.
 *
 * @todo cange JavaDoc
 * @author Christian Geuer-Pollmann
 */
public abstract class CanonicalizerSpi {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(CanonicalizerSpi.class.getName());

   /** Field BEFORE_DOCUMENT_ELEM */
   protected static final short BEFORE_DOCUMENT_ELEM = 0;

   /** Field INSIDE_DOCUMENT_ELEM */
   protected static final short INSIDE_DOCUMENT_ELEM = 1;

   /** Field AFTER_DOCUMENT_ELEM */
   protected static final short AFTER_DOCUMENT_ELEM = 2;

   /** Field implementedTransformURI */
   protected String implementedTransformURI = null;

   /** Field validating */
   private boolean _validating = false;

   /** Field includeComments */
   private boolean _includeComments = false;

   /** Field _engineXpathObject */
   private Object _engineXpathObject = null;

   /** Field _engineXpathString */
   private String _engineXpathString = null;

   /**
    * Method engineSetURI
    *
    * @param algorithmURI
    */
   public void engineSetURI(String algorithmURI) {
      this.implementedTransformURI = algorithmURI;
   }

   /**
    * Method engineGetURI
    *
    * @return
    */
   public String engineGetURI() {
      return this.implementedTransformURI;
   }

   /**
    * Method engineSetValidating
    *
    * @param validating
    */
   public void engineSetValidating(boolean validating) {
      this._validating = validating;
   }

   /**
    * Method engineGetValidating
    *
    * @return
    */
   public boolean engineGetValidating() {
      return this._validating;
   }

   /**
    * Method engineSetIncludeComments
    *
    * @param includeComments
    */
   public void engineSetIncludeComments(boolean includeComments) {
      this._includeComments = includeComments;
   }

   /**
    * Method engineGetIncludeComments
    *
    * @return
    */
   public boolean engineGetIncludeComments() {
      return this._includeComments;
   }

   // public abstract void engineOutput(Document document, OutputStream out) throws IOException, CanonicalizationException;

   /**
    * Sets the XPath which is beeing used by this Canonicalizer. This method
    * can get the XPath as pure String, as an ds:XPath element or as
    * {@link NodeList} which contains a ds:XPath element.
    *
    * @param _context
    * @throws DOMException
    * @throws IllegalArgumentException
    */
   public void engineSetXPath(Object _context)
           throws IllegalArgumentException, DOMException {

      cat.debug("engineSetXPath(" + _context.getClass().getName() + ") called");

      if (_context == null) {
         return;
      }

      this._engineXpathObject = _context;

      /** @todo check whether length of XPathString can be 0 */
      if (_context instanceof String) {
         cat.debug("Treat input as String");

         this._engineXpathString = (String) _context;
      } else if (_context instanceof Element) {
         cat.debug("Treat input as Element");

         Element contextElement = (Element) _context;

         if (!contextElement.getNamespaceURI().equals(Constants.SignatureSpecNS)
                 ||!contextElement.getLocalName().equals(Constants._TAG_XPATH)
                 || (contextElement.getChildNodes().getLength() != 1)) {
            throw new IllegalArgumentException(
               "Must be a single ds:XPath element with Text child");
         }

         Text xpathText = (Text) ((Element) _context).getFirstChild();

         this._engineXpathString = xpathText.getData();

         if ((this._engineXpathString == null)
                 || (this._engineXpathString.length() == 0)) {
            Object exArgs[] = { "", Constants._TAG_TRANSFORM };

            throw new DOMException(DOMException.WRONG_DOCUMENT_ERR,
                                   I18n.translate("xml.WrongContent", exArgs));
         }
      } else if (_context instanceof NodeList) {
         cat.debug("Treat input as NodeList");

         NodeList nl = (NodeList) _context;
         String XPathString = null;

         for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);

            //J-
            if ((n.getNodeType() == Node.ELEMENT_NODE) &&
                ((Element) n).getNamespaceURI().equals(Constants.SignatureSpecNS) &&
                ((Element) n).getLocalName().equals(Constants._TAG_XPATH)) {
               this._engineXpathObject = n;
               this._engineXpathString = (String) ((Text) n.getFirstChild()).getData();
            }
            //J+
         }

         if ((this._engineXpathString == null)
                 || (this._engineXpathString.length() == 0)) {
            Object exArgs[] = { "a single ds:XPath element with Text",
                                Constants._TAG_TRANSFORM };

            throw new DOMException(DOMException.WRONG_DOCUMENT_ERR,
                                   I18n.translate("xml.WrongContent", exArgs));
         }
      } else {
         throw new IllegalArgumentException("No XPath has been supplied");
      }

      cat.debug("The XPath " + this._engineXpathString + " has been set");
   }

   /**
    * Returns the XPath which is beeing used by this Canonicalizer;
    *
    * @return the XPath which is beeing used by this Canonicalizer;
    */
   public Object engineGetXPath() {
      return this._engineXpathObject;
   }

   /**
    * Method engineGetXPathString
    *
    * @return
    */
   public String engineGetXPathString() {
      return this._engineXpathString;
   }

   /**
    * Method engineC14nFiles
    *
    * @param inFile
    * @param outFile
    */
   public void engineC14nFiles(String inFile, String outFile) {

      String systemId = inFile;
      String outputFileName = outFile;

      try {
         InputSource in = new InputSource(systemId);
         DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();

         // needs to validate for ID attribute nomalization
         dfactory.setValidating(this.engineGetValidating());
         dfactory.setNamespaceAware(true);

         DocumentBuilder db = dfactory.newDocumentBuilder();

         /*
          * for some of the test vectors from the specification,
          * there has to be a validating parser for ID attributes, default
          * attribute values, NMTOKENS, etc.
          * Unfortunaltely, the test vectors do use different DTDs or
          * even no DTD. So Xerces 1.3.1 fires many warnings about using
          * ErrorHandlers.
          *
          * Text from the spec:
          *
          * The input octet stream MUST contain a well-formed XML document,
          * but the input need not be validated. However, the attribute
          * value normalization and entity reference resolution MUST be
          * performed in accordance with the behaviors of a validating
          * XML processor. As well, nodes for default attributes (declared
          * in the ATTLIST with an AttValue but not specified) are created
          * in each element. Thus, the declarations in the document type
          * declaration are used to help create the canonical form, even
          * though the document type declaration is not retained in the
          * canonical form.
          *
          */

         // ErrorHandler eh = new C14NErrorHandler();
         // db.setErrorHandler(eh);
         Document document = db.parse(in);
         byte result[] = this.engineCanonicalizeSubTree(document);
         FileOutputStream fos = new FileOutputStream(outputFileName);

         fos.write(result);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   /**
    * Compatibility method for implementations that use
    * {@link de.uni_siegen.xmlsecurity.util.C14N} as Canonicalizer
    *
    * @param document
    * @param out
    * @throws CanonicalizationException
    * @throws IOException
    */
   public void engineOutput(Document document, OutputStream out)
           throws IOException, CanonicalizationException {
      out.write(this.engineCanonicalizeSubTree(document));
   }

   /**
    * Method canonicalize
    *
    *
    * @param inputBytes
    *
    * @return
    *
    * @throws CanonicalizationException
    * @throws java.io.IOException
    * @throws javax.xml.parsers.ParserConfigurationException
    * @throws org.xml.sax.SAXException
    *
    */
   public byte[] engineCanonicalize(byte[] inputBytes)
           throws javax.xml.parsers.ParserConfigurationException,
                  java.io.IOException, org.xml.sax.SAXException,
                  CanonicalizationException {

      java.io.ByteArrayInputStream bais = new ByteArrayInputStream(inputBytes);
      InputSource in = new InputSource(bais);
      DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();

      // needs to validate for ID attribute nomalization
      dfactory.setValidating(this.engineGetValidating());
      dfactory.setNamespaceAware(true);

      DocumentBuilder db = dfactory.newDocumentBuilder();

      /*
       * for some of the test vectors from the specification,
       * there has to be a validatin parser for ID attributes, default
       * attribute values, NMTOKENS, etc.
       * Unfortunaltely, the test vectors do use different DTDs or
       * even no DTD. So Xerces 1.3.1 fires many warnings about using
       * ErrorHandlers.
       *
       * Text from the spec:
       *
       * The input octet stream MUST contain a well-formed XML document,
       * but the input need not be validated. However, the attribute
       * value normalization and entity reference resolution MUST be
       * performed in accordance with the behaviors of a validating
       * XML processor. As well, nodes for default attributes (declared
       * in the ATTLIST with an AttValue but not specified) are created
       * in each element. Thus, the declarations in the document type
       * declaration are used to help create the canonical form, even
       * though the document type declaration is not retained in the
       * canonical form.
       *
       */

      // ErrorHandler eh = new C14NErrorHandler();
      // db.setErrorHandler(eh);
      Document document = db.parse(in);
      byte result[] = this.engineCanonicalizeSubTree(document);

      return result;
   }

   /**
    * Method engineCanonicalizeSubTree
    *
    * @param node
    * @return
    * @throws CanonicalizationException
    */
   public abstract byte[] engineCanonicalizeSubTree(Node rootNode)
      throws CanonicalizationException;

   /**
    * Method engineCanonicalizeXPathNodeSet
    *
    * @param selectedNodes
    * @return
    * @throws CanonicalizationException
    */
   public abstract byte[] engineCanonicalizeXPathNodeSet(NodeList xpathNodeSet)
      throws CanonicalizationException;

   /**
    * Method engineVisible
    *
    * @param node
    * @return
    */
   public abstract boolean engineVisible(Node node);

   /**
    * Method engineMakeVisible
    *
    * @param node
    */
   public abstract void engineMakeVisible(Node node);

   /**
    * Method engineMakeInVisible
    *
    * @param node
    */
   public abstract void engineMakeInVisible(Node node);

   /**
    * Method engineSetXPathNodeSet
    *
    * @param nodeList
    */
   public abstract void engineSetXPathNodeSet(NodeList nodeList);

   /**
    * Method engineSetRemoveNSAttrs
    *
    * @param remove
    */
   public abstract void engineSetRemoveNSAttrs(boolean remove);

   /**
    * Method engineGetRemoveNSAttrs
    *
    * @return
    */
   public abstract boolean engineGetRemoveNSAttrs();

   static {
      org.apache.xml.security.Init.init();
   }
}
