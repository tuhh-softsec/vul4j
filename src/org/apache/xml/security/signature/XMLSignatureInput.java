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
package org.apache.xml.security.signature;



import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import javax.xml.parsers.*;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.c14n.*;
import org.apache.xml.security.c14n.implementations.*;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.utils.JavaUtils;
import org.apache.xml.security.utils.HelperNodeList;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import org.xml.sax.SAXException;
import org.apache.xpath.CachedXPathAPI;
import org.apache.xpath.XPathContext;


/**
 * Class XMLSignatureInput
 *
 * @author Christian Geuer-Pollmann
 * @todo check whether an XMLSignatureInput can be _both_, octet stream _and_ node set?
 */
public class XMLSignatureInput {

   /**
    * Some InputStreams do not support the {@link java.io.InputStream#reset}
    * method, so we read it in completely and work on our Proxy.
    */
   InputStream _inputOctetStreamProxy = null;

   /**
    * The original NodeSet for this XMLSignatureInput
    */
   Set _inputNodeSet = null;

   /** Field _cxpathAPI */
   CachedXPathAPI _cxpathAPI;

   /**
    * Construct a XMLSignatureInput from an octet array.
    * <p>
    * This is a comfort method, which internally converts the byte[] array into an InputStream
    *
    * @param inputOctets an octet array which including XML document or node
    */
   public XMLSignatureInput(byte[] inputOctets) {

      // defensive copy
      byte[] copy = new byte[inputOctets.length];

      System.arraycopy(inputOctets, 0, copy, 0, inputOctets.length);

      this._inputOctetStreamProxy = new ByteArrayInputStream(copy);
      this._cxpathAPI = new CachedXPathAPI();
   }

   /**
    * Constructs a <code>XMLSignatureInput</code> from an octet stream. The
    * stream is directly read.
    *
    * @param inputOctetStream
    * @throws IOException
    */
   public XMLSignatureInput(InputStream inputOctetStream) throws IOException {

      this(JavaUtils.getBytesFromStream(inputOctetStream));

      inputOctetStream = null;    // free object reference
   }

   /**
    * Construct a XMLSignatureInput from a String.
    * <p>
    * This is a comfort method, which internally converts the String into a byte[] array using the {@link java.lang.String#getBytes} method.
    *
    * @param inputStr the input String which including XML document or node
    */
   public XMLSignatureInput(String inputStr) {
      this(inputStr.getBytes());
   }

   /**
    * Construct a XMLSignatureInput from a String with a given encoding.
    * <p>
    * This is a comfort method, which internally converts the String into a byte[] array using the {@link java.lang.String#getBytes} method.
    *
    * @param inputStr the input String with encoding <code>encoding</code>
    * @param encoding the encoding of <code>inputStr</code>
    * @throws UnsupportedEncodingException
    */
   public XMLSignatureInput(String inputStr, String encoding)
           throws UnsupportedEncodingException {
      this(inputStr.getBytes(encoding));
   }

   /**
    * Construct a XMLSignatureInput from a subtree rooted by rootNode. This
    * method included the node and <I>all</I> his descendants in the output.
    *
    * @param rootNode
    * @param usedXPathAPI
    * @throws TransformerException
    */
   public XMLSignatureInput(Node rootNode, CachedXPathAPI usedXPathAPI)
           throws TransformerException {

      this._cxpathAPI = usedXPathAPI;

      // get the Document and make all namespace nodes visible in DOM space
      Document doc = XMLUtils.getOwnerDocument(rootNode);
      XMLUtils.circumventBug2650(doc);

      NodeList result = this._cxpathAPI.selectNodeList(rootNode,
                           Canonicalizer.XPATH_C14N_WITH_COMMENTS_SINGLE_NODE);

      this._inputNodeSet = XMLUtils.convertNodelistToSet(result);
   }

   /**
    * Construct a XMLSignatureInput from a subtree rooted by rootNode. This method included the node
    * and <I>all</I> his descendants in the output.
    *
    * @param rootNode
    * @throws TransformerException
    */
   public XMLSignatureInput(Node rootNode) throws TransformerException {
      this(rootNode, new CachedXPathAPI());
   }

   /**
    * Constructor XMLSignatureInput
    *
    * @param inputNodeSet
    * @param usedXPathAPI
    */
   public XMLSignatureInput(Set inputNodeSet, CachedXPathAPI usedXPathAPI) {
      this._inputNodeSet = inputNodeSet;
      this._cxpathAPI = usedXPathAPI;
   }

   /**
    * Constructor XMLSignatureInput
    *
    * @param inputNodeSet
    */
   public XMLSignatureInput(Set inputNodeSet) {
      this(inputNodeSet, new CachedXPathAPI());
   }

   /**
    * Returns the node set from input which was specified as the parameter of {@link XMLSignatureInput} constructor
    *
    * @return the node set
    * @throws CanonicalizationException
    * @throws IOException
    * @throws InvalidCanonicalizerException
    * @throws ParserConfigurationException
    * @throws SAXException
    */
   public Set getNodeSet()
           throws ParserConfigurationException, IOException, SAXException,
                  CanonicalizationException, InvalidCanonicalizerException {

      if (this.isNodeSet()) {
         return this._inputNodeSet;
      } else if (this.isOctetStream()) {
         DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();

         dfactory.setValidating(false);
         dfactory.setNamespaceAware(true);

         DocumentBuilder db = dfactory.newDocumentBuilder();

         try {
            db.setErrorHandler(new org.apache.xml.security.utils
               .IgnoreAllErrorHandler());

            Document doc = db.parse(this.getOctetStream());

            XMLUtils.circumventBug2650(doc);

            // select all nodes, also the comments.
            NodeList nodeList =
               this._cxpathAPI
                  .selectNodeList(doc,
                                  Canonicalizer
                                     .XPATH_C14N_WITH_COMMENTS_SINGLE_NODE);

            return XMLUtils.convertNodelistToSet(nodeList);
         } catch (TransformerException ex) {
            throw new CanonicalizationException("generic.EmptyMessage", ex);
         } catch (SAXException ex) {

            // if a not-wellformed nodeset exists, put a container around it...
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            baos.write("<container>".getBytes());
            baos.write(this.getBytes());
            baos.write("</container>".getBytes());

            byte result[] = baos.toByteArray();
            Document document = db.parse(new ByteArrayInputStream(result));
            XMLUtils.circumventBug2650(document);

            try {
               NodeList nodeList = this._cxpathAPI.selectNodeList(
                  document,
                  "(//. | //@* | //namespace::*)[not(self::node()=/) and not(self::node=/container)]");

               return XMLUtils.convertNodelistToSet(nodeList);
            } catch (TransformerException ex2) {
               throw new CanonicalizationException("generic.EmptyMessage", ex2);
            }
         }
      }

      throw new RuntimeException(
         "getNodeSet() called but no input data present");
   }

   /**
    * Returns the Octect stream(byte Stream) from input which was specified as the parameter of {@link XMLSignatureInput} constructor
    *
    * @return the Octect stream(byte Stream) from input which was specified as the parameter of {@link XMLSignatureInput} constructor
    * @throws CanonicalizationException
    * @throws IOException
    * @throws InvalidCanonicalizerException
    */
   public InputStream getOctetStream()
           throws IOException, CanonicalizationException,
                  InvalidCanonicalizerException {

      if (this.isOctetStream()) {
         this._inputOctetStreamProxy.reset();

         return this._inputOctetStreamProxy;
      } else if (this.isNodeSet()) {

         /* If we have a node set but an octet stream is needed, we MUST c14nize
          * without any comments.
          *
          * We don't use the factory because direct instantiation should be a
          * little bit faster...
          */
         Canonicalizer20010315OmitComments c14nizer =
            new Canonicalizer20010315OmitComments();
         ByteArrayOutputStream baos = new ByteArrayOutputStream();

         if (this._inputNodeSet.size() == 0) {

            // empty nodeset
            return new ByteArrayInputStream(baos.toByteArray());
         }

         try {
            Set nodes = this.getNodeSet();
            byte bytes[] = c14nizer.engineCanonicalizeXPathNodeSet(nodes);

            baos.write(bytes);

            /** @todo Clarify behavior. If isNodeSet() and we getOctetStream, do we have to this._inputOctetStream=xxx ? */

            /*
            this._inputOctetStream = new ByteArrayInputStream(baos.toByteArray());
            this._inputNodeSet = null;
            return this._inputOctetStream;
            */
            return new ByteArrayInputStream(baos.toByteArray());
         } catch (SAXException ex) {
            throw new CanonicalizationException("empty", ex);
         } catch (ParserConfigurationException ex) {
            throw new CanonicalizationException("empty", ex);
         }
      }

      throw new RuntimeException(
         "getOctetStream() called but no input data present");
   }

   /**
    * Returns the byte array from input which was specified as the parameter of {@link XMLSignatureInput} constructor
    *
    * @return the byte[] from input which was specified as the parameter of {@link XMLSignatureInput} constructor
    *
    * @throws CanonicalizationException
    * @throws IOException
    * @throws InvalidCanonicalizerException
    */
   public byte[] getBytes()
           throws IOException, CanonicalizationException,
                  InvalidCanonicalizerException {

      InputStream is = this.getOctetStream();
      int available = is.available();
      byte[] data = new byte[available];

      is.read(data);

      if (available != data.length) {
         throw new IOException("Not enough bytes read");
      }

      return data;
   }

   /**
    * Determines if the object has been set up with a Node set
    *
    * @return true is the object has been set up with a Node set
    */
   public boolean isNodeSet() {
      return ((this._inputOctetStreamProxy == null)
              && (this._inputNodeSet != null));
   }

   /**
    * Determines if the object has been set up with an octet stream
    *
    * @return true is the object has been set up with an octet stream
    */
   public boolean isOctetStream() {
      return ((this._inputOctetStreamProxy != null)
              && (this._inputNodeSet == null));
   }

   /**
    * Is the object correctly set up?
    *
    * @return true if the object has been set up correctly
    */
   public boolean isInitialized() {
      return (this.isOctetStream() || this.isNodeSet());
   }

   /**
    * Some Transforms may require explicit MIME type, charset (IANA registered
    * "character set"), or other such information concerning the data they
    * are receiving from an earlier Transform or the source data, although no
    * Transform algorithm specified in this document needs such explicit
    * information. Such data characteristics are provided as parameters to the
    * Transform algorithm and should be described in the specification for the
    * algorithm.
    */
   private String _MIMEType = null;

   /**
    * Returns MIMEType
    *
    * @return MIMEType
    */
   public String getMIMEType() {
      return this._MIMEType;
   }

   /**
    * Sets MIMEType
    *
    * @param MIMEType
    */
   public void setMIMEType(String MIMEType) {
      this._MIMEType = MIMEType;
   }

   /** Field _SourceURI */
   private String _SourceURI = null;

   /**
    * Return SourceURI
    *
    * @return SourceURI
    */
   public String getSourceURI() {
      return this._SourceURI;
   }

   /**
    * Sets SourceURI
    *
    * @param SourceURI
    */
   public void setSourceURI(String SourceURI) {
      this._SourceURI = SourceURI;
   }

   /**
    * This method gives access to an {@link org.apache.xpath.CachedXPathAPI}
    * object which was used for creating the internal node set and which MUST be
    * used for subsequent operations on this node set.
    *
    * @return an existing {@link org.apache.xpath.CachedXPathAPI}
    */
   public CachedXPathAPI getCachedXPathAPI() {
      return this._cxpathAPI;
   }

   /**
    * Method toString
    *
    * @return
    */
   public String toString() {

      if (this.isNodeSet()) {
         try {
            return "XMLSignatureInput/NodeSet/" + this._inputNodeSet.size()
                   + " nodes/" + this.getSourceURI();
         } catch (Exception ex) {
            return "XMLSignatureInput/NodeSet//" + this.getSourceURI();
         }
      } else {
         try {
            return "XMLSignatureInput/OctetStream/" + this.getBytes().length
                   + " octets/" + this.getSourceURI();
         } catch (Exception ex) {
            return "XMLSignatureInput/OctetStream//" + this.getSourceURI();
         }
      }
   }
}
