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



import java.io.ByteArrayInputStream;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import javax.xml.parsers.*;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
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

   //J-
   /** The output encoding of canonicalized data */
   public static final String ENCODING = "UTF8";

   private static final String XPATH_NO_COMMENTS = "[not(self::comment())]";
   public static final String XPATH_C14N_WITH_COMMENTS = "(//. | //@* | //namespace::*)";
   public static final String XPATH_C14N_OMIT_COMMENTS = XPATH_C14N_WITH_COMMENTS + XPATH_NO_COMMENTS;
   public static final String XPATH_C14N_WITH_COMMENTS_SINGLE_NODE = "(.//. | .//@* | .//namespace::*)";
   public static final String XPATH_C14N_OMIT_COMMENTS_SINGLE_NODE = XPATH_C14N_WITH_COMMENTS_SINGLE_NODE + XPATH_NO_COMMENTS;

   public static final String ALGO_ID_C14N_OMIT_COMMENTS = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
   public static final String ALGO_ID_C14N_WITH_COMMENTS = ALGO_ID_C14N_OMIT_COMMENTS + "#WithComments";
   public static final String ALGO_ID_C14N_EXCL_OMIT_COMMENTS = "http://www.w3.org/2001/10/xml-exc-c14n#";
   public static final String ALGO_ID_C14N_EXCL_WITH_COMMENTS = ALGO_ID_C14N_EXCL_OMIT_COMMENTS + "WithComments";

   static boolean _alreadyInitialized = false;
   static Map _canonicalizerHash = null;

   protected CanonicalizerSpi canonicalizerSpi = null;
   //J+

   /**
    * Method init
    *
    */
   public static void init() {

      if (!Canonicalizer._alreadyInitialized) {
         Canonicalizer._canonicalizerHash = new HashMap(10);
         Canonicalizer._alreadyInitialized = true;
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
      } catch (Exception e) {
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

      Canonicalizer c14nizer = new Canonicalizer(algorithmURI);

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

      // check whether URI is already registered
      String registeredClass = getImplementingClass(algorithmURI);

      if ((registeredClass != null) && (registeredClass.length() != 0)) {
         Object exArgs[] = { algorithmURI, registeredClass };

         throw new AlgorithmAlreadyRegisteredException(
            "algorithm.alreadyRegistered", exArgs);
      }

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
    * Method getIncludeComments
    *
    * @return
    */
   public boolean getIncludeComments() {
      return this.canonicalizerSpi.engineGetIncludeComments();
   }

   /**
    * This method tries to canonicalize the given bytes. It's possible to even
    * canonicalize non-wellformed sequences if they are well-formed after being
    * wrapped with a <CODE>&gt;a&lt;...&gt;/a&lt;</CODE>.
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

      ByteArrayInputStream bais = new ByteArrayInputStream(inputBytes);
      InputSource in = new InputSource(bais);
      DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();

      dfactory.setNamespaceAware(true);

      // needs to validate for ID attribute nomalization
      dfactory.setValidating(true);

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
      db.setErrorHandler(new org.apache.xml.security.utils
         .IgnoreAllErrorHandler());

      Document document = db.parse(in);
      byte result[] = this.canonicalizeSubtree(document);

      return result;
   }

   /**
    * Canonicalizes the subtree rooted by <CODE>node</CODE>.
    *
    * @param node
    * @return
    * @throws CanonicalizationException
    */
   public byte[] canonicalizeSubtree(Node node)
           throws CanonicalizationException {
      return this.canonicalizerSpi.engineCanonicalizeSubTree(node);
   }

   /**
    * Canonicalizes the subtree rooted by <CODE>node</CODE>.
    *
    * @param node
    * @param inclusiveNamespaces
    * @return
    * @throws CanonicalizationException
    */
   public byte[] canonicalizeSubtree(Node node, String inclusiveNamespaces)
           throws CanonicalizationException {
      return this.canonicalizerSpi.engineCanonicalizeSubTree(node,
              inclusiveNamespaces);
   }

   /**
    * Canonicalizes an XPath node set. The <CODE>xpathNodeSet</CODE> is treated
    * as a list of XPath nodes, not as a list of subtrees.
    *
    * @param xpathNodeSet
    * @return
    * @throws CanonicalizationException
    */
   public byte[] canonicalizeXPathNodeSet(NodeList xpathNodeSet)
           throws CanonicalizationException {
      return this.canonicalizerSpi.engineCanonicalizeXPathNodeSet(xpathNodeSet);
   }

   /**
    * Canonicalizes an XPath node set. The <CODE>xpathNodeSet</CODE> is treated
    * as a list of XPath nodes, not as a list of subtrees.
    *
    * @param xpathNodeSet
    * @param inclusiveNamespaces
    * @return
    * @throws CanonicalizationException
    */
   public byte[] canonicalizeXPathNodeSet(
           NodeList xpathNodeSet, String inclusiveNamespaces)
              throws CanonicalizationException {
      return this.canonicalizerSpi.engineCanonicalizeXPathNodeSet(xpathNodeSet,
              inclusiveNamespaces);
   }

   /**
    * Canonicalizes an XPath node set.
    *
    * @param xpathNodeSet
    * @return
    * @throws CanonicalizationException
    */
   public byte[] canonicalizeXPathNodeSet(Set xpathNodeSet)
           throws CanonicalizationException {
      return this.canonicalizerSpi.engineCanonicalizeXPathNodeSet(xpathNodeSet);
   }

   /**
    * Canonicalizes an XPath node set.
    *
    * @param xpathNodeSet
    * @param inclusiveNamespaces
    * @return
    * @throws CanonicalizationException
    */
   public byte[] canonicalizeXPathNodeSet(
           Set xpathNodeSet, String inclusiveNamespaces)
              throws CanonicalizationException {
      return this.canonicalizerSpi.engineCanonicalizeXPathNodeSet(xpathNodeSet,
              inclusiveNamespaces);
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
}
