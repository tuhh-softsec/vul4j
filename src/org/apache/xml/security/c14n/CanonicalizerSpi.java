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
import java.util.Set;
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
    * Method engineCanonicalizeXPathNodeSet
    *
    * @param xpathNodeSet
    * @return
    * @throws CanonicalizationException
    */
   public byte[] engineCanonicalizeXPathNodeSet(NodeList xpathNodeSet)
           throws CanonicalizationException {

      return this
         .engineCanonicalizeXPathNodeSet(XMLUtils
            .convertNodelistToSet(xpathNodeSet));
   }

   public byte[] engineCanonicalizeXPathNodeSet(NodeList xpathNodeSet, String inclusiveNamespaces)
           throws CanonicalizationException {

      return this
         .engineCanonicalizeXPathNodeSet(XMLUtils
            .convertNodelistToSet(xpathNodeSet), inclusiveNamespaces);
   }

   //J-
   public abstract String engineGetURI();

   public abstract boolean engineGetIncludeComments();

   public abstract byte[] engineCanonicalizeXPathNodeSet(Set xpathNodeSet)
      throws CanonicalizationException;

   public abstract byte[] engineCanonicalizeXPathNodeSet(Set xpathNodeSet, String inclusiveNamespaces)
      throws CanonicalizationException;

   public abstract byte[] engineCanonicalizeSubTree(Node rootNode)
      throws CanonicalizationException;

   public abstract byte[] engineCanonicalizeSubTree(Node rootNode, String inclusiveNamespaces)
      throws CanonicalizationException;
   //J+
}
