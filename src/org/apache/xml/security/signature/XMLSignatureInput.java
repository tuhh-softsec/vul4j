
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
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import javax.xml.parsers.*;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.utils.XMLUtils;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import org.xml.sax.SAXException;
// import org.apache.xpath.XPathAPI;
import org.apache.xpath.CachedXPathAPI;
import org.apache.xml.dtm.DTMManager;


/**
 * Class XMLSignatureInput
 *
 * @author Christian Geuer-Pollmann
 * @todo check whether an XMLSignatureInput can be _both_, octet stream _and_ node set?
 */
public class XMLSignatureInput {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(XMLSignatureInput.class.getName());

   /** Field useFlatNodes */
   static final boolean useFlatNodes = false;

   /**
    * The original InputStream for this XMLSignatureInput
    */
   InputStream _inputOctetStream = null;

   /**
    * Some InputStreams do not support the {@link java.io.InputStream#reset}
    * method, so we read it in completely and work on our Proxy.
    */
   ByteArrayInputStream _inputOctetStreamProxy = null;

   /**
    * The original NodeSet for this XMLSignatureInput
    */
   NodeList _inputNodeSet = null;

   DTMManager _myDTMManager = null;

   /**
    *  If we serialize a NodeSet, will Comment nodes be included?
    *  <p>
    *  If we look in section 4.3.3.2 The Reference Processing Model, there is stated:
    *  <ul>
    *  <li>If the data object is a node-set and the next transform requires
    *      octets, the signature application MUST attempt to convert the
    *      node-set to an octet stream using the REQUIRED canonicalization algorithm [XML-C14N].</li>
    *  </ul>
    * <p>
    * From my understanding, Canonical XML (omits comments) is the only c14n algorithm which is REQUIRED.
    */
   String _canonicalizerURI = Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS;

   /** Field _xpathString */
   String _xpathString = Canonicalizer.XPATH_C14N_OMIT_COMMENTS;

   /**
    * Constructs a <code>XMLSignatureInput</code> from {@link InputStream an octet stream} which made from XML document , node
    *
    * @param inputOctetStream {@link InputStream} which including XML document or node
    */
   public XMLSignatureInput(InputStream inputOctetStream) {
      this._inputOctetStream = inputOctetStream;
      this._myDTMManager = new CachedXPathAPI().getXPathContext().getDTMManager();
   }

   /**
    * Construct a XMLSignatureInput from an octet array.
    * <p>
    * This is a comfort method, which internally converts the byte[] array into an InputStream
    *
    * @param inputOctets an octet array which including XML document or node
    */
   public XMLSignatureInput(byte[] inputOctets) {
      this._inputOctetStream = new ByteArrayInputStream(inputOctets);
      this._myDTMManager = new CachedXPathAPI().getXPathContext().getDTMManager();
   }

   /**
    * Construct a XMLSignatureInput from a String.
    * <p>
    * This is a comfort method, which internally converts the String into a byte[] array using the {@link java.lang.String#getBytes} method.
    *
    * @param inputStr the input String which including XML document or node
    */
   public XMLSignatureInput(String inputStr) {
      this._inputOctetStream = new ByteArrayInputStream(inputStr.getBytes());
      this._myDTMManager = new CachedXPathAPI().getXPathContext().getDTMManager();
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
      this._inputOctetStream =
         new ByteArrayInputStream(inputStr.getBytes(encoding));
      this._myDTMManager = new CachedXPathAPI().getXPathContext().getDTMManager();
   }

   /**
    * Construct a XMLSignatureInput from a node set. Only the nodes from the
    * <CODE>inputNodeSet</CODE> occur in the output.
    *
    * @param inputNodeSet is the node set
    */
   private XMLSignatureInput(NodeList inputNodeSet) {
      this._inputNodeSet = inputNodeSet;
      this._myDTMManager = new CachedXPathAPI().getXPathContext().getDTMManager();
   }

   /**
    * Construct a XMLSignatureInput from a Node. This method included the node
    * and <I>all</I> his descendants in the output.
    *
    * @param node
    * @throws TransformerException
    */
   public XMLSignatureInput(Node node) throws TransformerException {

      cat.debug("Start " + _xpathString + " on Node " + node.getNodeName());

      CachedXPathAPI myXPathAPI = new CachedXPathAPI();
      this._myDTMManager = myXPathAPI.getXPathContext().getDTMManager();
      this._inputNodeSet = myXPathAPI.selectNodeList(node,
              Canonicalizer.XPATH_C14N_WITH_COMMENTS_SINGLE_NODE);
   }

   /**
    * Construct a XMLSignatureInput from a node set. Only the nodes from the
    * <CODE>inputNodeSet</CODE> occur in the output.
    *
    * @param inputNodeSet is the node set
    */
   public XMLSignatureInput(NodeList inputNodeSet, DTMManager dtmManager) {
      this._inputNodeSet = inputNodeSet;
      this._myDTMManager = dtmManager;
   }

   /**
    * Construct a XMLSignatureInput from a Node. This method included the node
    * and <I>all</I> his descendants in the output.
    *
    * @param node
    * @throws TransformerException
    */
   public XMLSignatureInput(Node node, DTMManager dtmManager) throws TransformerException {

      cat.debug("Start " + _xpathString + " on Node " + node.getNodeName());
      this._myDTMManager = dtmManager;

      CachedXPathAPI myXPathAPI = new CachedXPathAPI();
      myXPathAPI.getXPathContext().setDTMManager(this._myDTMManager);

      this._inputNodeSet = myXPathAPI.selectNodeList(node,
              Canonicalizer.XPATH_C14N_WITH_COMMENTS_SINGLE_NODE);
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
   public NodeList getNodeSet()
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
            Document document = db.parse(this.getOctetStream());

            if (XMLSignatureInput.useFlatNodes) {
               return document.getChildNodes();
            } else {
               CachedXPathAPI myXPathAPI = new CachedXPathAPI();
               myXPathAPI.getXPathContext().setDTMManager(this._myDTMManager);
               return myXPathAPI.selectNodeList(document, _xpathString);
            }
         } catch (TransformerException ex) {
            throw new CanonicalizationException("generic.EmptyMessage", ex);
         } catch (SAXException ex) {

            // if a not-wellformed nodeset exists, put a container around it...
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            String container = "container";

            baos.write((new String("<" + container + ">")).getBytes());
            baos.write(this.getBytes());
            baos.write((new String("</" + container + ">")).getBytes());

            if (XMLSignatureInput.useFlatNodes) {
               byte result[] = baos.toByteArray();
               Document document = db.parse(new ByteArrayInputStream(result));

               return document.getFirstChild().getChildNodes();
            } else {
               byte result[] = baos.toByteArray();
               Document document = db.parse(new ByteArrayInputStream(result));

               try {
                  String noDocument = "not(self::node()=/)";

                  // String noDocumentElement = "not(local-name()='" + container + "')"
                  String noDocumentElement = "not(self::node=/node())";
                  String xpathStr =
                     "(//. | //@* | //namespace::*)[not(self::comment()) and "
                     + noDocument + " and " + noDocumentElement + "]";
                  CachedXPathAPI myXPathAPI = new CachedXPathAPI();
                  myXPathAPI.getXPathContext().setDTMManager(this._myDTMManager);

                  NodeList nodes = myXPathAPI.selectNodeList(document, xpathStr);

                  return nodes;
               } catch (TransformerException ex2) {
                  throw new CanonicalizationException("generic.EmptyMessage",
                                                      ex2);
               }
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
         if (this._inputOctetStream.markSupported()) {

            // no need to read in the complete stream, because we can reset()
            this._inputOctetStream.reset();

            return this._inputOctetStream;
         } else {
            if (this._inputOctetStreamProxy == null) {

               // read in complete InputStream into internal byte[] array.
               byte[] _inputOctets =
                  new byte[this._inputOctetStream.available()];

               this._inputOctetStream.read(_inputOctets);

               this._inputOctetStreamProxy =
                  new ByteArrayInputStream(_inputOctets);
            } else {
               this._inputOctetStreamProxy.reset();
            }

            return this._inputOctetStreamProxy;
         }
      } else if (this.isNodeSet()) {

         /* serialize Element(s) and output them
          */
         Canonicalizer c14nizer =
            Canonicalizer.getInstance(this._canonicalizerURI);
         ByteArrayOutputStream baos = new ByteArrayOutputStream();

         if (this._inputNodeSet.getLength() == 0) {

            // empty nodeset
            return new ByteArrayInputStream(baos.toByteArray());
         }

         cat.debug("set XPathNodeSet with " + this._inputNodeSet.getLength()
                   + " nodes");
         c14nizer.setXPathNodeSet(this._inputNodeSet);
         cat.debug("The nodeset _inputNodeSet has "
                   + this._inputNodeSet.getLength() + " Nodes");

         /* We want to output the NodeList and do this by retrieving the Document Node
          * and outputting the Document
          */
         cat.debug("node(0) is " +  this._inputNodeSet.item(0));

         Document doc = XMLUtils.getOwnerDocument(this._inputNodeSet.item(0));
         byte bytes[] = c14nizer.canonicalize(doc);

         baos.write(bytes);

         /** @todo Clarify behavior. If isNodeSet() and we getOctetStream, do we have to this._inputOctetStream=xxx ? */

         /*
         this._inputOctetStream = new ByteArrayInputStream(baos.toByteArray());
         this._inputNodeSet = null;
         return this._inputOctetStream;
         */
         return new ByteArrayInputStream(baos.toByteArray());
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
      return ((this._inputOctetStream == null) && (this._inputNodeSet != null));
   }

   /**
    * Determines if the object has been set up with an octet stream
    *
    * @return true is the object has been set up with an octet stream
    */
   public boolean isOctetStream() {
      return ((this._inputOctetStream != null) && (this._inputNodeSet == null));
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
    * Defines whether Comment nodes should be included if we serialize a NodeSet
    *
    * @param canonicalizerURI
    */
   public void setCanonicalizerURI(String canonicalizerURI) {
      this._canonicalizerURI = canonicalizerURI;
   }

   /**
    * If we serialize a NodeSet, will Comment nodes be included?
    *
    * @return true if Comment nodes are included in a serialized node set
    */
   public String getCanonicalizerURI() {
      return this._canonicalizerURI;
   }

   /**
    * Sets <code>XPath expression</code> want to get node set
    *
    * @param selectedNodesetXPath <code>XPath</code> want to get node set
    */
   public void setNodesetXPath(String selectedNodesetXPath) {
      this._xpathString = selectedNodesetXPath;
   }

   /**
    * Returns <code>XPath expression</code> want to get node set
    *
    * @return <code>XPath expression</code> want to get node set
    */
   public String getNodesetXPath() {
      return this._xpathString;
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

   /** Field _SourceURI           */
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
    * Method main
    *
    * @param args
    * @throws Exception
    */
   public static void main(String args[]) throws Exception {

      String inputStr =
         "<?xml version=\"1.0\"?>\n" + "<!-- full document --><_doc>"
         + "<n xmlns:ietf='http://www.ietf.org/'><ietf:comment>1</ietf:comment></n><n>2</n><n>3</n><n>4</n>"
         + "</_doc>";

      inputStr =
         "<n xmlns:ietf='http://www.ietf.org/'><ietf:comment xmlns:ietf='http://www.ietf.org/'>1</ietf:comment></n><n>2</n><n>3</n><n>4</n>";

      XMLSignatureInput input = new XMLSignatureInput(inputStr.getBytes());
      NodeList nl = input.getNodeSet();

      if (nl.getLength() == 0) {
         System.out.println("No Nodes found");
      } else {
         for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);

            System.out.println(XMLUtils.getNodeTypeString(n) + " "
                               + n.getNodeName());
         }
      }

      Canonicalizer c =
         Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS);

      c.setXPathNodeSet(nl);
      System.out.println(new String(c.canonicalize(nl)));
   }

   public DTMManager getDTMManager() {
      return this._myDTMManager;
   }

   static {
      org.apache.xml.security.Init.init();
   }
}
