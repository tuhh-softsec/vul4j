
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
package org.apache.xml.security.signature;



import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.c14n.helper.AttrCompare;
import org.apache.xml.security.c14n.implementations.Canonicalizer20010315OmitComments;
import org.apache.xml.security.utils.JavaUtils;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.utils.CachedXPathAPIHolder;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.xml.sax.SAXException;


/**
 * Class XMLSignatureInput
 *
 * @author Christian Geuer-Pollmann
 * $todo$ check whether an XMLSignatureInput can be _both_, octet stream _and_ node set?
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
   /**
    * The original Element
    */
   Node _subNode=null;
   /**
    * Exclude Node *for enveloped transformations*
    */
   Node excludeNode=null;
   /**
    * 
    */
   boolean excludeComments=false;
   /**
    * A cached bytes
    */
   byte []bytes=null;
   /** Field _cxpathAPI */
   CachedXPathAPIHolder _cxpathAPI=null;

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
      //this._cxpathAPI = new CachedXPathAPI();
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
    * This is a comfort method, which internally converts the String into a byte[] array using the {@link java.lang.String#getBytes()} method.
    *
    * @param inputStr the input String which including XML document or node
    */
   public XMLSignatureInput(String inputStr) {
      this(inputStr.getBytes());
   }

   /**
    * Construct a XMLSignatureInput from a String with a given encoding.
    * <p>
    * This is a comfort method, which internally converts the String into a byte[] array using the {@link java.lang.String#getBytes()} method.
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
    */
   public XMLSignatureInput(Node rootNode, CachedXPathAPIHolder usedXPathAPI)
           {

      this._cxpathAPI = usedXPathAPI;

      // get the Document and make all namespace nodes visible in DOM space
      //After the c14n FIX is merge this is neaded
      //Document doc = XMLUtils.getOwnerDocument(rootNode);
      //XMLUtils.circumventBug2650(doc);

     // NodeList result = this._cxpathAPI.selectNodeList(rootNode,
       //                    Canonicalizer.XPATH_C14N_WITH_COMMENTS_SINGLE_NODE);
      this._subNode = rootNode;
      //this._inputNodeSet = XMLUtils.getSetWithComments(rootNode,new HashSet());
   }

   /**
    * Construct a XMLSignatureInput from a subtree rooted by rootNode. This method included the node
    * and <I>all</I> his descendants in the output.
    *
    * @param rootNode    
    */
   public XMLSignatureInput(Node rootNode)  {
      this(rootNode, null);
   }

   /**
    * Constructor XMLSignatureInput
    *
    * @param inputNodeSet
    * @param usedXPathAPI
    */
   public XMLSignatureInput(Set inputNodeSet, CachedXPathAPIHolder usedXPathAPI) {
    this._inputNodeSet = inputNodeSet;
    this._cxpathAPI = usedXPathAPI;
    //    After the c14n FIX is merge this is neaded
 }

   /**
    * Constructor XMLSignatureInput
    *
    * @param inputNodeSet
    */
   public XMLSignatureInput(Set inputNodeSet) {
      this(inputNodeSet, null);
   }

   /**
    * Constructor XMLSignatureInput
    *
    * @param inputNodeSet
    * @deprecated Use {@link Set}s instead of {@link NodeList}s.
    */
   public XMLSignatureInput(NodeList inputNodeSet) {   	  
      this(XMLUtils.convertNodelistToSet(inputNodeSet), null);
      //After the c14n FIX is merge this is neaded
      
      //XMLUtils.circumventBug2650(XMLUtils.getOwnerDocument(inputNodeSet.item(0)));
   }

   /**
    * Constructor XMLSignatureInput
    *
    * @param inputNodeSet
    * @param usedXPathAPI
    * @deprecated Use {@link Set}s instead of {@link NodeList}s.
    */
   public XMLSignatureInput(NodeList inputNodeSet,
                            CachedXPathAPIHolder usedXPathAPI) {
      this(XMLUtils.convertNodelistToSet(inputNodeSet), usedXPathAPI);
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
   	  if (this.isElement() && this._inputNodeSet==null) {       
            XMLUtils.circumventBug2650(XMLUtils.getOwnerDocument(this._subNode));
            if (this.excludeComments) {
            	this._inputNodeSet = XMLUtils.getSetWithoutComments(_subNode,new HashSet());
            } else {
   	  			this._inputNodeSet = XMLUtils.getSetWithComments(_subNode,new HashSet());
            }
   	  	    if (excludeNode!=null) {
   	  		  this._inputNodeSet=XMLUtils.excludeNodeFromSet(excludeNode,this._inputNodeSet);
   	  	  }
   	  	return this._inputNodeSet;
   	  }
      if (this.isNodeSet()) {
         if (!this._inputNodeSet.isEmpty()) {
      	    XMLUtils.circumventBug2650(XMLUtils.getOwnerDocument(this._inputNodeSet));
         }
         return this._inputNodeSet;
      } else if (this.isOctetStream()) {
         DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();

         dfactory.setValidating(false);
         dfactory.setNamespaceAware(true);

         DocumentBuilder db = dfactory.newDocumentBuilder();

         // select all nodes, also the comments.
        
         try {
            db.setErrorHandler(new org.apache.xml.security.utils
               .IgnoreAllErrorHandler());

            Document doc = db.parse(this.getOctetStream());
            
            XMLUtils.circumventBug2650(doc);
            return XMLUtils.getSetWithComments(doc.getDocumentElement(), new HashSet()); /*convertNodelistToSet(nodeList);*/         
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
               NodeList nodeList = this._cxpathAPI.getCachedXPathAPI().selectNodeList(
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
      } else if (this.isElement()) {
         if (bytes==null) {         	
        Canonicalizer20010315OmitComments c14nizer =
            new Canonicalizer20010315OmitComments();                  
         if (this.excludeNode!=null ) {
         	bytes=c14nizer.engineCanonicalizeSubTree(this._subNode,this.excludeNode);
         } else {
         	bytes=c14nizer.engineCanonicalizeSubTree(this._subNode);
         }
         }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
 		baos.write(bytes);
        return new ByteArrayInputStream(baos.toByteArray());

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
            if (bytes==null) {
            bytes = c14nizer.engineCanonicalizeXPathNodeSet(nodes);
            }
            baos.write(bytes);
            /** $todo$ Clarify behavior. If isNodeSet() and we getOctetStream, do we have to this._inputOctetStream=xxx ? */

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
      if ( bytes!=null) {
      	return bytes;
      }
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
      return ( (this._inputOctetStreamProxy == null)
              && (this._inputNodeSet != null) );
   }
   /**
    * Determines if the object has been set up with an Element
    *
    * @return true is the object has been set up with a Node set
    */
   public boolean isElement() {
   		return ((this._inputOctetStreamProxy==null)&& (this._subNode!=null)
   				&& (this._inputNodeSet==null)
   				);
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
   public CachedXPathAPIHolder getCachedXPathAPI() {      
   	  if (this._cxpathAPI==null) {
         this._cxpathAPI=new CachedXPathAPIHolder(); 
      }
      return this._cxpathAPI;
   }
   
   /**
    * Method toString
    * @inheritDoc
    *
    */
   public String toString() {

      if (this.isNodeSet()) {
         return "XMLSignatureInput/NodeSet/" + this._inputNodeSet.size()
                   + " nodes/" + this.getSourceURI();         
      } 
      if (this.isElement()) {
        return "XMLSignatureInput/Element/" + this._subNode
        + " exclude "+ this.excludeNode + " comments:" + 
        this.excludeComments
        +"/" + this.getSourceURI();
      }
         try {
            return "XMLSignatureInput/OctetStream/" + this.getBytes().length
                   + " octets/" + this.getSourceURI();
         } catch (Exception ex) {
            return "XMLSignatureInput/OctetStream//" + this.getSourceURI();
         }
      
   }

   /**
    * Method getHTMLRepresentation
    *
    * @throws XMLSignatureException
    * @return The HTML representation for this XMLSignature
    */
   public String getHTMLRepresentation() throws XMLSignatureException {

      XMLSignatureInputDebugger db = new XMLSignatureInputDebugger(this);

      return db.getHTMLRepresentation();
   }

   /**
    * Method getHTMLRepresentation
    *
    * @param inclusiveNamespaces
    * @throws XMLSignatureException
    * @return The HTML representation for this XMLSignature
    */
   public String getHTMLRepresentation(Set inclusiveNamespaces)
           throws XMLSignatureException {

      XMLSignatureInputDebugger db = new XMLSignatureInputDebugger(this,
                                        inclusiveNamespaces);

      return db.getHTMLRepresentation();
   }

   /**
    * Class XMLSignatureInputDebugger
    *
    * @author $Author$
    * @version $Revision$
    */
   public class XMLSignatureInputDebugger {

      /** Field _xmlSignatureInput */
      private Set _xpathNodeSet;
      private Set _inclusiveNamespaces;

      /** Field _doc */
      private Document _doc = null;

      /** Field _writer */
      private Writer _writer = null;
      //J-
      // public static final String HTMLPrefix = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"><html><head><style type=\"text/css\"><!-- .INCLUDED { color: #000000; background-color: #FFFFFF; font-weight: bold; } .EXCLUDED { color: #666666; background-color: #999999; } .INCLUDEDINCLUSIVENAMESPACE {	color: #0000FF; background-color: #FFFFFF; font-weight: bold; font-style: italic; } .EXCLUDEDINCLUSIVENAMESPACE { color: #0000FF; background-color: #999999; font-style: italic; } --> </style> </head><body bgcolor=\"#999999\"><pre>";
      /**The HTML Prefix**/ 
      static final String HTMLPrefix = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n"
                 + "<html>\n"
                 + "<head>\n"
                 + "<title>Caninical XML node set</title>\n"
                 + "<style type=\"text/css\">\n"
                 + "<!-- \n"
                 + ".INCLUDED { \n"
                 + "   color: #000000; \n"
                 + "   background-color: \n"
                 + "   #FFFFFF; \n"
                 + "   font-weight: bold; } \n"
                 + ".EXCLUDED { \n"
                 + "   color: #666666; \n"
                 + "   background-color: \n"
                 + "   #999999; } \n"
                 + ".INCLUDEDINCLUSIVENAMESPACE { \n"
                 + "   color: #0000FF; \n"
                 + "   background-color: #FFFFFF; \n"
                 + "   font-weight: bold; \n"
                 + "   font-style: italic; } \n"
                 + ".EXCLUDEDINCLUSIVENAMESPACE { \n"
                 + "   color: #0000FF; \n"
                 + "   background-color: #999999; \n"
                 + "   font-style: italic; } \n"
                 + "--> \n"
                 + "</style> \n"
                 + "</head>\n"
                 + "<body bgcolor=\"#999999\">\n"
                 + "<h1>Explanation of the output</h1>\n"
                 + "<p>The following text contains the nodeset of the given Reference before it is canonicalized. There exist four different styles to indicate how a given node is treated.</p>\n"
                 + "<ul>\n"
                 + "<li class=\"INCLUDED\">A node which is in the node set is labeled using the INCLUDED style.</li>\n"
                 + "<li class=\"EXCLUDED\">A node which is <em>NOT</em> in the node set is labeled EXCLUDED style.</li>\n"
                 + "<li class=\"INCLUDEDINCLUSIVENAMESPACE\">A namespace which is in the node set AND in the InclusiveNamespaces PrefixList is labeled using the INCLUDEDINCLUSIVENAMESPACE style.</li>\n"
                 + "<li class=\"EXCLUDEDINCLUSIVENAMESPACE\">A namespace which is in NOT the node set AND in the InclusiveNamespaces PrefixList is labeled using the INCLUDEDINCLUSIVENAMESPACE style.</li>\n"
                 + "</ul>\n"
                 + "<h1>Output</h1>\n"
                 + "<pre>\n" ;

      /** HTML Suffix **/
      static final String HTMLSuffix = "</pre></body></html>";

      static final String HTMLExcludePrefix = "<span class=\"EXCLUDED\">";
      static final String HTMLExcludeSuffix = "</span>";

      static final String HTMLIncludePrefix = "<span class=\"INCLUDED\">";
      static final String HTMLIncludeSuffix = "</span>";

      static final String HTMLIncludedInclusiveNamespacePrefix = "<span class=\"INCLUDEDINCLUSIVENAMESPACE\">";
      static final String HTMLIncludedInclusiveNamespaceSuffix = "</span>";

      static final String HTMLExcludedInclusiveNamespacePrefix = "<span class=\"EXCLUDEDINCLUSIVENAMESPACE\">";
      static final String HTMLExcludedInclusiveNamespaceSuffix = "</span>";

      private static final int NODE_BEFORE_DOCUMENT_ELEMENT = -1;
      private static final int NODE_NOT_BEFORE_OR_AFTER_DOCUMENT_ELEMENT = 0;
      private static final int NODE_AFTER_DOCUMENT_ELEMENT = 1;
      //J+
      private XMLSignatureInputDebugger() {
         // do nothing
      }

      /**
       * Constructor XMLSignatureInputDebugger
       *
       * @param xmlSignatureInput
       */
      public XMLSignatureInputDebugger(XMLSignatureInput xmlSignatureInput)
      {

         if (!xmlSignatureInput.isNodeSet()) {
            this._xpathNodeSet = null;
         } else {
            this._xpathNodeSet = xmlSignatureInput._inputNodeSet;
         }
      }

      /**
       * Constructor XMLSignatureInputDebugger
       *
       * @param xmlSignatureInput
       * @param inclusiveNamespace
       */
      public XMLSignatureInputDebugger(
              XMLSignatureInput xmlSignatureInput, Set inclusiveNamespace)
      {

         this(xmlSignatureInput);

         this._inclusiveNamespaces = inclusiveNamespace;
      }

      /**
       * Method getHTMLRepresentation
       * @return The HTML Representation.
       * @throws XMLSignatureException
       */
      public String getHTMLRepresentation() throws XMLSignatureException {

         if ((this._xpathNodeSet == null) || (this._xpathNodeSet.size() == 0)) {
            return HTMLPrefix + "<blink>no node set, sorry</blink>"
                   + HTMLSuffix;
         }

         {

            // get only a single node as anchor to fetch the owner document
            Node n = (Node) this._xpathNodeSet.iterator().next();

            this._doc = XMLUtils.getOwnerDocument(n);
         }

         try {
            this._writer = new StringWriter();

            this.canonicalizeXPathNodeSet(this._doc);
            this._writer.close();

            return this._writer.toString();
         } catch (IOException ex) {
            throw new XMLSignatureException("empty", ex);
         } finally {
            this._xpathNodeSet = null;
            this._doc = null;
            this._writer = null;
         }
      }

      /**
       * Method canonicalizeXPathNodeSet
       *
       * @param currentNode
       * @throws XMLSignatureException
       * @throws IOException
       */
      private void canonicalizeXPathNodeSet(Node currentNode)
              throws XMLSignatureException, IOException {

         int currentNodeType = currentNode.getNodeType();
         switch (currentNodeType) {

         case Node.DOCUMENT_TYPE_NODE :
         default :
            break;

         case Node.ENTITY_NODE :
         case Node.NOTATION_NODE :
         case Node.DOCUMENT_FRAGMENT_NODE :
         case Node.ATTRIBUTE_NODE :
            throw new XMLSignatureException("empty");
         case Node.DOCUMENT_NODE :
            this._writer.write(HTMLPrefix);

            for (Node currentChild = currentNode.getFirstChild();
                    currentChild != null;
                    currentChild = currentChild.getNextSibling()) {
               this.canonicalizeXPathNodeSet(currentChild);
            }

            this._writer.write(HTMLSuffix);
            break;

         case Node.COMMENT_NODE :
            if (this._xpathNodeSet.contains(currentNode)) {
               this._writer.write(HTMLIncludePrefix);
            } else {
               this._writer.write(HTMLExcludePrefix);
            }

            int position = getPositionRelativeToDocumentElement(currentNode);

            if (position == NODE_AFTER_DOCUMENT_ELEMENT) {
               this._writer.write("\n");
            }

            this.outputCommentToWriter((Comment) currentNode);

            if (position == NODE_BEFORE_DOCUMENT_ELEMENT) {
               this._writer.write("\n");
            }

            if (this._xpathNodeSet.contains(currentNode)) {
               this._writer.write(HTMLIncludeSuffix);
            } else {
               this._writer.write(HTMLExcludeSuffix);
            }
            break;

         case Node.PROCESSING_INSTRUCTION_NODE :
            if (this._xpathNodeSet.contains(currentNode)) {
               this._writer.write(HTMLIncludePrefix);
            } else {
               this._writer.write(HTMLExcludePrefix);
            }

            position = getPositionRelativeToDocumentElement(currentNode);

            if (position == NODE_AFTER_DOCUMENT_ELEMENT) {
               this._writer.write("\n");
            }

            this.outputPItoWriter((ProcessingInstruction) currentNode);

            if (position == NODE_BEFORE_DOCUMENT_ELEMENT) {
               this._writer.write("\n");
            }

            if (this._xpathNodeSet.contains(currentNode)) {
               this._writer.write(HTMLIncludeSuffix);
            } else {
               this._writer.write(HTMLExcludeSuffix);
            }
            break;

         case Node.TEXT_NODE :
         case Node.CDATA_SECTION_NODE :
            if (this._xpathNodeSet.contains(currentNode)) {
               this._writer.write(HTMLIncludePrefix);
            } else {
               this._writer.write(HTMLExcludePrefix);
            }

            outputTextToWriter(currentNode.getNodeValue());

            for (Node nextSibling =
                    currentNode
                       .getNextSibling(); (nextSibling != null) && ((nextSibling
                          .getNodeType() == Node.TEXT_NODE) || (nextSibling
                             .getNodeType() == Node
                                .CDATA_SECTION_NODE)); nextSibling =
                                   nextSibling.getNextSibling()) {

               /* The XPath data model allows to select only the first of a
                * sequence of mixed text and CDATA nodes. But we must output
                * them all, so we must search:
                *
                * @see http://nagoya.apache.org/bugzilla/show_bug.cgi?id=6329
                */
               this.outputTextToWriter(nextSibling.getNodeValue());
            }

            if (this._xpathNodeSet.contains(currentNode)) {
               this._writer.write(HTMLIncludeSuffix);
            } else {
               this._writer.write(HTMLExcludeSuffix);
            }
            break;

         case Node.ELEMENT_NODE :
            Element currentElement = (Element) currentNode;

            if (this._xpathNodeSet.contains(currentNode)) {
               this._writer.write(HTMLIncludePrefix);
            } else {
               this._writer.write(HTMLExcludePrefix);
            }

            this._writer.write("&lt;");
            this._writer.write(currentElement.getTagName());

            if (this._xpathNodeSet.contains(currentNode)) {
               this._writer.write(HTMLIncludeSuffix);
            } else {
               this._writer.write(HTMLExcludeSuffix);
            }

            // we output all Attrs which are available
            NamedNodeMap attrs = currentElement.getAttributes();
            int attrsLength = attrs.getLength();
            Object attrs2[] = new Object[attrsLength];

            for (int i = 0; i < attrsLength; i++) {
               attrs2[i] = attrs.item(i);
            }
            
            Arrays.sort(attrs2,new AttrCompare());
            Object attrs3[] = attrs2;

            for (int i = 0; i < attrsLength; i++) {
               Attr a = (Attr) attrs3[i];
               boolean included = this._xpathNodeSet.contains(a);
               boolean inclusive =
                  this._inclusiveNamespaces.contains(a.getName());

               if (included) {
                  if (inclusive) {

                     // included and inclusive
                     this._writer.write(HTMLIncludedInclusiveNamespacePrefix);
                  } else {

                     // included and not inclusive
                     this._writer.write(HTMLIncludePrefix);
                  }
               } else {
                  if (inclusive) {

                     // excluded and inclusive
                     this._writer.write(HTMLExcludedInclusiveNamespacePrefix);
                  } else {

                     // excluded and not inclusive
                     this._writer.write(HTMLExcludePrefix);
                  }
               }

               this.outputAttrToWriter(a.getNodeName(), a.getNodeValue());

               if (included) {
                  if (inclusive) {

                     // included and inclusive
                     this._writer.write(HTMLIncludedInclusiveNamespaceSuffix);
                  } else {

                     // included and not inclusive
                     this._writer.write(HTMLIncludeSuffix);
                  }
               } else {
                  if (inclusive) {

                     // excluded and inclusive
                     this._writer.write(HTMLExcludedInclusiveNamespaceSuffix);
                  } else {

                     // excluded and not inclusive
                     this._writer.write(HTMLExcludeSuffix);
                  }
               }
            }

            if (this._xpathNodeSet.contains(currentNode)) {
               this._writer.write(HTMLIncludePrefix);
            } else {
               this._writer.write(HTMLExcludePrefix);
            }

            this._writer.write("&gt;");

            if (this._xpathNodeSet.contains(currentNode)) {
               this._writer.write(HTMLIncludeSuffix);
            } else {
               this._writer.write(HTMLExcludeSuffix);
            }

            // traversal
            for (Node currentChild = currentNode.getFirstChild();
                    currentChild != null;
                    currentChild = currentChild.getNextSibling()) {
               this.canonicalizeXPathNodeSet(currentChild);
            }

            if (this._xpathNodeSet.contains(currentNode)) {
               this._writer.write(HTMLIncludePrefix);
            } else {
               this._writer.write(HTMLExcludePrefix);
            }

            this._writer.write("&lt;/");
            this._writer.write(currentElement.getTagName());
            this._writer.write("&gt;");

            if (this._xpathNodeSet.contains(currentNode)) {
               this._writer.write(HTMLIncludeSuffix);
            } else {
               this._writer.write(HTMLExcludeSuffix);
            }
            break;
         }
      }



      /**
       * Checks whether a Comment or ProcessingInstruction is before or after the
       * document element. This is needed for prepending or appending "\n"s.
       *
       * @param currentNode comment or pi to check
       * @return NODE_BEFORE_DOCUMENT_ELEMENT, NODE_NOT_BEFORE_OR_AFTER_DOCUMENT_ELEMENT or NODE_AFTER_DOCUMENT_ELEMENT
       * @see #NODE_BEFORE_DOCUMENT_ELEMENT
       * @see #NODE_NOT_BEFORE_OR_AFTER_DOCUMENT_ELEMENT
       * @see #NODE_AFTER_DOCUMENT_ELEMENT
       */
      private int getPositionRelativeToDocumentElement(Node currentNode) {

         if (currentNode == null) {
            return NODE_NOT_BEFORE_OR_AFTER_DOCUMENT_ELEMENT;
         }

         Document doc = currentNode.getOwnerDocument();

         if (currentNode.getParentNode() != doc) {
            return NODE_NOT_BEFORE_OR_AFTER_DOCUMENT_ELEMENT;
         }

         Element documentElement = doc.getDocumentElement();

         if (documentElement == null) {
            return NODE_NOT_BEFORE_OR_AFTER_DOCUMENT_ELEMENT;
         }

         if (documentElement == currentNode) {
            return NODE_NOT_BEFORE_OR_AFTER_DOCUMENT_ELEMENT;
         }

         for (Node x = currentNode; x != null; x = x.getNextSibling()) {
            if (x == documentElement) {
               return NODE_BEFORE_DOCUMENT_ELEMENT;
            }
         }

         return NODE_AFTER_DOCUMENT_ELEMENT;
      }

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
       * @param name
       * @param value
       * @throws IOException
       */
      private void outputAttrToWriter(String name, String value)
              throws IOException {

         this._writer.write(" ");
         this._writer.write(name);
         this._writer.write("=\"");

         int length = value.length();

         for (int i = 0; i < length; i++) {
            char c = value.charAt(i);

            switch (c) {

            case '&' :
               this._writer.write("&amp;amp;");
               break;

            case '<' :
               this._writer.write("&amp;lt;");
               break;

            case '"' :
               this._writer.write("&amp;quot;");
               break;

            case 0x09 :    // '\t'
               this._writer.write("&amp;#x9;");
               break;

            case 0x0A :    // '\n'
               this._writer.write("&amp;#xA;");
               break;

            case 0x0D :    // '\r'
               this._writer.write("&amp;#xD;");
               break;

            default :
               this._writer.write(c);
               break;
            }
         }

         this._writer.write("\"");
      }

      /**
       * Normalizes a {@link org.w3c.dom.Comment} value
       *
       * @param currentPI
       * @throws IOException
       */
      private void outputPItoWriter(ProcessingInstruction currentPI)
              throws IOException {

         if (currentPI == null) {
            return;
         }

         this._writer.write("&lt;?");

         String target = currentPI.getTarget();
         int length = target.length();

         for (int i = 0; i < length; i++) {
            char c = target.charAt(i);

            switch (c) {

            case 0x0D :
               this._writer.write("&amp;#xD;");
               break;

            case ' ' :
               this._writer.write("&middot;");
               break;

            case '\n' :
               this._writer.write("&para;\n");
               break;

            default :
               this._writer.write(c);
               break;
            }
         }

         String data = currentPI.getData();

         length = data.length();

         if ((data != null) && (length > 0)) {
            this._writer.write(" ");

            for (int i = 0; i < length; i++) {
               char c = data.charAt(i);

               switch (c) {

               case 0x0D :
                  this._writer.write("&amp;#xD;");
                  break;

               default :
                  this._writer.write(c);
                  break;
               }
            }
         }

         this._writer.write("?&gt;");
      }

      /**
       * Method outputCommentToWriter
       *
       * @param currentComment
       * @throws IOException
       */
      private void outputCommentToWriter(Comment currentComment)
              throws IOException {

         if (currentComment == null) {
            return;
         }

         this._writer.write("&lt;!--");

         String data = currentComment.getData();
         int length = data.length();

         for (int i = 0; i < length; i++) {
            char c = data.charAt(i);

            switch (c) {

            case 0x0D :
               this._writer.write("&amp;#xD;");
               break;

            case ' ' :
               this._writer.write("&middot;");
               break;

            case '\n' :
               this._writer.write("&para;\n");
               break;

            default :
               this._writer.write(c);
               break;
            }
         }

         this._writer.write("--&gt;");
      }

      /**
       * Method outputTextToWriter
       *
       * @param text
       * @throws IOException
       */
      private void outputTextToWriter(String text) throws IOException {

         if (text == null) {
            return;
         }

         int length = text.length();

         for (int i = 0; i < length; i++) {
            char c = text.charAt(i);

            switch (c) {

            case '&' :
               this._writer.write("&amp;amp;");
               break;

            case '<' :
               this._writer.write("&amp;lt;");
               break;

            case '>' :
               this._writer.write("&amp;gt;");
               break;

            case 0xD :
               this._writer.write("&amp;#xD;");
               break;

            case ' ' :
               this._writer.write("&middot;");
               break;

            case '\n' :
               this._writer.write("&para;\n");
               break;

            default :
               this._writer.write(c);
               break;
            }
         }
      }
   }
   /**
    * Gets the exclude node of this XMLSignatureInput
    * @return Returns the excludeNode.
    */
    public Node getExcludeNode() {
	   return excludeNode;
    }
    
    /**
     * Sets the exclude node of this XMLSignatureInput
     * @param excludeNode The excludeNode to set.
     */
     public void setExcludeNode(Node excludeNode) {
	    this.excludeNode = excludeNode;
     }

     /**
      * Gets the node of this XMLSignatureInput
      * @return The excludeNode set.
      */
     public Node getSubNode() {
  	    return _subNode;
     }
     /**
      * @return Returns the excludeComments.
      */
     public boolean isExcludeComments() {
     	return excludeComments;
     }
     /**
      * @param excludeComments The excludeComments to set.
      */
     public void setExcludeComments(boolean excludeComments) {
     	this.excludeComments = excludeComments;
     }
}
