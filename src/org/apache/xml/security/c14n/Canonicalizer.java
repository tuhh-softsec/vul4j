
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
import java.util.HashMap;
import java.util.Iterator;
import org.w3c.dom.*;
import org.apache.xml.security.exceptions.*;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.JavaUtils;


/**
 *
 *
 * @author Christian Geuer-Pollmann
 */
public class Canonicalizer {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(Canonicalizer.class.getName());

   /** The output encoding of canonicalized data */
   public static final String ENCODING = "UTF8";

   /** Field XPATH_NO_COMMENTS */
   private static final String XPATH_NO_COMMENTS = "[not(self::comment())]";

   /** The XPath for Canonicalization - Recommended Canonical XML with Comments */
   public static final String XPATH_C14N_WITH_COMMENTS =
      "(//. | //@* | //namespace::*)";

   /** The XPath for Canonicalization - Required Canonical XML (omits comments) */
   public static final String XPATH_C14N_OMIT_COMMENTS =
      XPATH_C14N_WITH_COMMENTS + XPATH_NO_COMMENTS;

   /** The XPath for Canonicalization of a single Node with Comments */
   public static final String XPATH_C14N_WITH_COMMENTS_SINGLE_NODE =
      "(.//. | .//@* | .//namespace::*)";

   /** The XPath for Canonicalization of a single Node (omits comments) */
   public static final String XPATH_C14N_OMIT_COMMENTS_SINGLE_NODE =
      XPATH_C14N_WITH_COMMENTS_SINGLE_NODE + XPATH_NO_COMMENTS;

   /** Field ALGO_ID_C14N_OMIT_COMMENTS */
   public static final String ALGO_ID_C14N_OMIT_COMMENTS =
      "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";

   /** Field ALGO_ID_C14N_WITH_COMMENTS */
   public static final String ALGO_ID_C14N_WITH_COMMENTS =
      ALGO_ID_C14N_OMIT_COMMENTS + "#WithComments";

   /** Canonicalization - Required Exclusive Canonicalization (omits comments) */
   public static final String ALGO_ID_C14N_EXCL = Constants.SignatureSpecNS
                                                     + "excludeC14N";

   /** Canonicalization - Recommended Exclusive Canonicalization with Comments */
   public static final String ALGO_ID_C14N_EXCL_WITHCOMMENTS =
      Constants.SignatureSpecNS + "excludeC14NwithComments";

   /** Field canonicalizerSpi */
   protected CanonicalizerSpi canonicalizerSpi = null;

   /** Field _alreadyInitialized */
   static boolean _alreadyInitialized = false;

   /** Field _canonicalizerHash */
   static HashMap _canonicalizerHash = null;

   /**
    * Method init
    *
    */
   public static void init() {

      if (!_alreadyInitialized) {
         _canonicalizerHash = new HashMap(10);
         _alreadyInitialized = true;
      }
   }

   /**
    * Constructor Canonicalizer
    *
    * @param algorithmURI
    * @throws InvalidCanonicalizerException
    */
   private Canonicalizer(String algorithmURI)
           throws InvalidCanonicalizerException {

      try {
         String implementingClass = getImplementingClass(algorithmURI);

         this.canonicalizerSpi =
            (CanonicalizerSpi) Class.forName(implementingClass).newInstance();

         cat.debug("Canonicalizer  spi = "
                   + this.canonicalizerSpi.engineGetURI() + "");

         // this.canonicalizerSpi.engineSetURI(algorithmURI);
      } catch (Exception e) {
         if (_canonicalizerHash == null) {
            cat.fatal("The _canonicalizerHash is null");
         } else {
            Iterator i = _canonicalizerHash.keySet().iterator();

            while (i.hasNext()) {
               String URI = (String) i.next();

               cat.debug("The URI " + URI + "maps to "
                         + getImplementingClass(URI));
            }
         }

         Object exArgs[] = { algorithmURI };

         throw new InvalidCanonicalizerException(
            "signature.Canonicalizer.UnknownCanonicalizer", exArgs);
      }
   }

   /**
    * Method getInstance
    *
    * @param algorithmURI
    * @return
    * @throws InvalidCanonicalizerException
    */
   public static final Canonicalizer getInstance(String algorithmURI)
           throws InvalidCanonicalizerException {

      org.apache.xml.security.Init.init();

      Canonicalizer c14nizer = new Canonicalizer(algorithmURI);

      return c14nizer;
   }

   /**
    * Method getInstance
    *
    * @param algorithmURI
    * @param xpath
    * @return
    * @throws InvalidCanonicalizerException
    */
   public static final Canonicalizer getInstance(
           String algorithmURI, Object xpath)
              throws InvalidCanonicalizerException {

      org.apache.xml.security.Init.init();

      Canonicalizer c14nizer = new Canonicalizer(algorithmURI);

      c14nizer.setXPath(xpath);

      return c14nizer;
   }

   /**
    * Method register
    *
    * @param algorithmURI
    * @param implementingClass
    * @throws AlgorithmAlreadyRegisteredException
    */
   public static void register(String algorithmURI, String implementingClass)
           throws AlgorithmAlreadyRegisteredException {

      org.apache.xml.security.Init.init();
      cat.debug("Try to register Canonicalizer " + algorithmURI + " to class "
                + implementingClass);

      // check whether URI is already registered
      String registeredClass = getImplementingClass(algorithmURI);

      if ((registeredClass != null) && (registeredClass.length() != 0)) {
         Object exArgs[] = { algorithmURI, registeredClass };

         throw new AlgorithmAlreadyRegisteredException(
            "algorithm.alreadyRegistered", exArgs);
      }

      cat.debug("Map of C14N " + algorithmURI + " to class "
                + implementingClass);
      _canonicalizerHash.put(algorithmURI, implementingClass);
   }

   /**
    * Method getURI
    *
    * @return
    */
   public final String getURI() {
      return this.canonicalizerSpi.engineGetURI();
   }

   /**
    * Method setValidating
    *
    * @param validating
    */
   public void setValidating(boolean validating) {
      this.canonicalizerSpi.engineSetValidating(validating);
   }

   /**
    * Method getValidating
    *
    * @return
    */
   public boolean getValidating() {
      return this.canonicalizerSpi.engineGetValidating();
   }

   /**
    * Method setNamespaceAware
    *
    * @param namespaceAware
    */
   public void setNamespaceAware(boolean namespaceAware) {
      this.canonicalizerSpi.engineSetNamespaceAware(namespaceAware);
   }

   /**
    * Method getNamespaceAware
    *
    * @return
    */
   public boolean getNamespaceAware() {
      return this.canonicalizerSpi.engineGetNamespaceAware();
   }

   /**
    * Method setIncludeComments
    *
    * @param includeComments
    */
   public void setIncludeComments(boolean includeComments) {
      this.canonicalizerSpi.engineSetIncludeComments(includeComments);
   }

   /**
    * Method getIncludeComments
    *
    * @return
    */
   public boolean getIncludeComments() {
      return this.canonicalizerSpi.engineGetIncludeComments();
   }

   /**
    * Method canonicalize
    *
    * @param node
    * @return
    * @throws CanonicalizationException
    */
   public byte[] canonicalize(Node node) throws CanonicalizationException {
      if (node == null) {
         cat.error("I was asked to canonicalize a null node");
      }
      return this.canonicalizerSpi.engineCanonicalize(node);
   }

   /**
    * Method canonicalize
    *
    * @param inputBytes
    * @return
    * @throws CanonicalizationException
    * @throws java.io.IOException
    * @throws javax.xml.parsers.ParserConfigurationException
    * @throws org.xml.sax.SAXException
    */
   public byte[] canonicalize(byte[] inputBytes)
           throws javax.xml.parsers.ParserConfigurationException,
                  java.io.IOException, org.xml.sax.SAXException,
                  CanonicalizationException {
      return this.canonicalizerSpi.engineCanonicalize(inputBytes);
   }

   /**
    * Method canonicalize
    *
    * @param selectedNodes
    * @return
    * @throws CanonicalizationException
    */
   public byte[] canonicalize(NodeList selectedNodes)
           throws CanonicalizationException {
      return this.canonicalizerSpi.engineCanonicalize(selectedNodes);
   }

   /**
    * Method canonicalizeDocument
    *
    * @param doc
    * @return
    * @throws CanonicalizationException
    */
   public byte[] canonicalizeDocument(Document doc)
           throws CanonicalizationException {

      if (this.getIncludeComments()) {
         this.setXPath(Canonicalizer.XPATH_C14N_WITH_COMMENTS);
      } else {
         this.setXPath(Canonicalizer.XPATH_C14N_OMIT_COMMENTS);
      }

      return this.canonicalize(doc);
   }

   /**
    * Method canonicalizeSingleNode
    *
    * @param rootNode
    * @return
    * @throws CanonicalizationException
    */
   public byte[] canonicalizeSingleNode(Node rootNode)
           throws CanonicalizationException {

      if (this.getIncludeComments()) {
         this.setXPath(Canonicalizer.XPATH_C14N_WITH_COMMENTS_SINGLE_NODE);
      } else {
         this.setXPath(Canonicalizer.XPATH_C14N_OMIT_COMMENTS_SINGLE_NODE);
      }

      return this.canonicalize(rootNode);
   }

   /**
    * Method canonicalize
    *
    * @param input
    * @return
    */
   public XMLSignatureInput canonicalize(XMLSignatureInput input) {
      return null;
   }

   /**
    * Method c14nFiles
    *
    * @param inFile
    * @param outFile
    */
   public void c14nFiles(String inFile, String outFile) {
      this.canonicalizerSpi.engineC14nFiles(inFile, outFile);
   }

   /**
    * Method output
    *
    * @param document
    * @param out
    * @throws CanonicalizationException
    * @throws IOException
    */
   public void output(Document document, OutputStream out)
           throws IOException, CanonicalizationException {
      this.canonicalizerSpi.engineOutput(document, out);
   }

   /**
    * Returns the name of the implementing {@link CanonicalizerSpi} class
    *
    * @return the name of the implementing {@link CanonicalizerSpi} class
    */
   public String getImplementingCanonicalizerClass() {
      return this.canonicalizerSpi.getClass().getName();
   }

   /**
    * Proxy method for {@link CanonicalizerSpi#engineSetXPath}.
    *
    * @param xpath
    */
   public void setXPath(Object xpath) {
      this.canonicalizerSpi.engineSetXPath(xpath);
   }

   /**
    * Method getXPath
    *
    * @return
    */
   public Object getXPath() {
      return this.canonicalizerSpi.engineGetXPath();
   }

   /**
    * Method getXPathString
    *
    * @return
    */
   public String getXPathString() {
      return this.canonicalizerSpi.engineGetXPathString();
   }

   /**
    * Method setXPathNodeSet
    *
    * @param nodeList
    */
   public void setXPathNodeSet(NodeList nodeList) {
      this.canonicalizerSpi.engineSetXPathNodeSet(nodeList);
   }

   /**
    * Method getImplementingClass
    *
    * @param URI
    * @return
    */
   private static String getImplementingClass(String URI) {

      Iterator i = _canonicalizerHash.keySet().iterator();

      while (i.hasNext()) {
         String key = (String) i.next();

         if (key.equals(URI)) {
            return (String) _canonicalizerHash.get(key);
         }
      }

      return null;
   }

   static {
      org.apache.xml.security.Init.init();
   }
}
