
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
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.implementations.Canonicalizer20010315OmitComments;
import org.apache.xml.security.utils.JavaUtils;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * Class XMLSignatureInput
 *
 * @author Christian Geuer-Pollmann
 * $todo$ check whether an XMLSignatureInput can be _both_, octet stream _and_ node set?
 */
public class XMLSignatureInput {
	 static org.apache.commons.logging.Log log = 
	        org.apache.commons.logging.LogFactory.getLog(XMLSignatureInput.class.getName());

	/*
     * The XMLSignature Input can be either:
     *   A byteArray like with/or without InputStream.
     *   Or a  nodeSet like defined either:
     *                     * as a collection of nodes
     *                     * or as subnode excluding or not commets and excluding or 
     *                            not other nodes.
	 */
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

   /**
    * Some Transforms may require explicit MIME type, charset (IANA registered "character set"), or other such information concerning the data they are receiving from an earlier Transform or the source data, although no Transform algorithm specified in this document needs such explicit information. Such data characteristics are provided as parameters to the Transform algorithm and should be described in the specification for the algorithm.
    */   
   private String _MIMEType = null;

   /**
    * Field _SourceURI 
    */
   private String _SourceURI = null;

   
   OutputStream outputStream=null;

   /**
    * Construct a XMLSignatureInput from an octet array.
    * <p>
    * This is a comfort method, which internally converts the byte[] array into an InputStream
    * <p>NOTE: no defensive copy</p>
    * @param inputOctets an octet array which including XML document or node
    */
   public XMLSignatureInput(byte[] inputOctets) {

      // NO  defensive copy
   	  
      //this._inputOctetStreamProxy = new ByteArrayInputStream(inputOctets);
      this.bytes=inputOctets;
   }


      /**
    * Constructs a <code>XMLSignatureInput</code> from an octet stream. The
    * stream is directly read.
    *
    * @param inputOctetStream
    */
   public XMLSignatureInput(InputStream inputOctetStream)  {
   	  this._inputOctetStreamProxy=inputOctetStream;
   	  
      //this(JavaUtils.getBytesFromStream(inputOctetStream));

   }

   /**
    * Construct a XMLSignatureInput from a String.
    * <p>
    * This is a comfort method, which internally converts the String into a byte[] array using the {@link java.lang.String#getBytes()} method.
    * @deprecated
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
    * @deprecated
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
    */
   public XMLSignatureInput(Node rootNode)
   {
      this._subNode = rootNode;
   }

   
   /**
    * Constructor XMLSignatureInput
    *
    * @param inputNodeSet
    */
   public XMLSignatureInput(Set inputNodeSet) {
   	    this._inputNodeSet = inputNodeSet;
    }


   /**
    * Constructor XMLSignatureInput
    *
    * @param inputNodeSet
    * @deprecated Use {@link Set}s instead of {@link NodeList}s.
    */
   public XMLSignatureInput(NodeList inputNodeSet) {   	  
      this(XMLUtils.convertNodelistToSet(inputNodeSet));
   }

   
   /**
    * Returns the node set from input which was specified as the parameter of {@link XMLSignatureInput} constructor
    *
    * @return the node set
    * @throws SAXException
    * @throws IOException
    * @throws ParserConfigurationException
    * @throws CanonicalizationException
    * @throws CanonicalizationException
    * @throws IOException
    * @throws ParserConfigurationException
    * @throws SAXException
    */
   public Set getNodeSet() throws CanonicalizationException, ParserConfigurationException, IOException, SAXException {
   	      return getNodeSet(false);
   }
   /**
    * Returns the node set from input which was specified as the parameter of {@link XMLSignatureInput} constructor
    * @param circunvent
    *
    * @return the node set
    * @throws SAXException
    * @throws IOException
    * @throws ParserConfigurationException
    * @throws CanonicalizationException
    * @throws CanonicalizationException
    * @throws IOException
    * @throws ParserConfigurationException
    * @throws SAXException
    */
   public Set getNodeSet(boolean circunvent)
           throws ParserConfigurationException, IOException, SAXException,
                  CanonicalizationException {
      if (this._inputNodeSet!=null) {
      	  return this._inputNodeSet;
      }
   	  if (this.isElement()) {
            
   	  	    if (circunvent) {           
   	  	    	XMLUtils.circumventBug2650(XMLUtils.getOwnerDocument(_subNode));
            }
            this._inputNodeSet = new HashSet();
            XMLUtils.getSet(_subNode,this._inputNodeSet, excludeNode, this.excludeComments);
            
   	  	    return this._inputNodeSet;
   	  }
       else if (this.isOctetStream()) {
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
            HashSet result=new HashSet();
            XMLUtils.getSet(doc.getDocumentElement(), result,null,false); 
            //this._inputNodeSet=result;
            return result;         
         } catch (SAXException ex) {

            // if a not-wellformed nodeset exists, put a container around it...
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            baos.write("<container>".getBytes());
            baos.write(this.getBytes());
            baos.write("</container>".getBytes());

            byte result[] = baos.toByteArray();
            Document document = db.parse(new ByteArrayInputStream(result));

            //XMLUtils.circumventBug2650(document);
            
				Set set = new HashSet();
				XMLUtils.getSet(
						document.getDocumentElement().getFirstChild().getFirstChild(),
						set,null,true);
              // NodeList nodeList = CachedXPathAPIHolder.getCachedXPathAPI().selectNodeList(
              //    document,
              //    "(//. | //@* | //namespace::*)[not(self::node()=/) and not(self::node=/container)]");

               return set;
         }
      }

      throw new RuntimeException(
         "getNodeSet() called but no input data present");
   }

   /**
    * Returns the Octect stream(byte Stream) from input which was specified as the parameter of {@link XMLSignatureInput} constructor
    *
    * @return the Octect stream(byte Stream) from input which was specified as the parameter of {@link XMLSignatureInput} constructor
    * @throws IOException
    */
   public InputStream getOctetStream()
           throws IOException  {
         	  
      return getResetableInputStream();                 

   }
   /**
     * @return real octect stream
     */
    public InputStream getOctetStreamReal () {
       return this._inputOctetStreamProxy;
   }
   /**
    * Returns the byte array from input which was specified as the parameter of {@link XMLSignatureInput} constructor
    *
    * @return the byte[] from input which was specified as the parameter of {@link XMLSignatureInput} constructor
    *
    * @throws CanonicalizationException
    * @throws IOException
    */
   public byte[] getBytes()
           throws IOException, CanonicalizationException {
    if (bytes!=null) {
        return bytes;      
      }
   	  InputStream is = getResetableInputStream();
   	  if (is!=null) {
        //reseatable can read again bytes. 
   	  	if (bytes==null) {
            is.reset();       
            bytes=JavaUtils.getBytesFromStream(is);
   	  	} 	  	
   	  	return bytes;   	  	      
      } else if (this.isElement()) {                    
         Canonicalizer20010315OmitComments c14nizer =
         		new Canonicalizer20010315OmitComments();                  
        bytes=c14nizer.engineCanonicalizeSubTree(this._subNode,this.excludeNode);         
        return bytes;
      } else if (this.isNodeSet()) {      	
         /* If we have a node set but an octet stream is needed, we MUST c14nize
          * without any comments.
          *
          * We don't use the factory because direct instantiation should be a
          * little bit faster...
          */
         Canonicalizer20010315OmitComments c14nizer =
            new Canonicalizer20010315OmitComments();         

         if (this._inputNodeSet.size() == 0) {
            // empty nodeset
            return null;
         }              
         bytes = c14nizer.engineCanonicalizeXPathNodeSet(_inputNodeSet);
          return bytes;         
      }

      throw new RuntimeException(
         "getBytes() called but no input data present");
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
      return ( ((this._inputOctetStreamProxy != null) || bytes!=null)
              && ((this._inputNodeSet == null) && _subNode ==null));
   }
   
   /**
    * Determines if the object has been set up with a ByteArray
    *
    * @return true is the object has been set up with an octet stream
    */
   public boolean isByteArray() {
      return ( (bytes!=null)
              && ((this._inputNodeSet == null) && _subNode ==null));
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

      XMLSignatureInputDebugger db = new XMLSignatureInputDebugger( this,
                                        inclusiveNamespaces);

      return db.getHTMLRepresentation();
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

	/**
	 * @param diOs
	 * @throws IOException
	 * @throws CanonicalizationException
	 */
	public void updateOutputStream(OutputStream diOs) throws CanonicalizationException, IOException {        
        if (diOs==outputStream) {
        	return;
        }
        if (bytes!=null) {
            diOs.write(bytes);
            return;      
         }else if (this.isElement()) {                    
             Canonicalizer20010315OmitComments c14nizer =
                    new Canonicalizer20010315OmitComments();       
             c14nizer.setWriter(diOs);
            c14nizer.engineCanonicalizeSubTree(this._subNode,this.excludeNode); 
            return;
          } else if (this.isNodeSet()) {        
             /* If we have a node set but an octet stream is needed, we MUST c14nize
              * without any comments.
              *
              * We don't use the factory because direct instantiation should be a
              * little bit faster...
              */
             Canonicalizer20010315OmitComments c14nizer =
                new Canonicalizer20010315OmitComments();         
             c14nizer.setWriter(diOs);
             if (this._inputNodeSet.size() == 0) {
                // empty nodeset
                return;
             }                              
             c14nizer.engineCanonicalizeXPathNodeSet(this._inputNodeSet);                
             return;             
          } else {
            InputStream is = getResetableInputStream();
            if (bytes!=null) {
                //already read write it, can be rea.
            	diOs.write(bytes,0,bytes.length);
                return;
            }            
            is.reset();            
            int num;
            byte[] bytesT = new byte[1024];
            while ((num=is.read(bytesT))>0) {
            	diOs.write(bytesT,0,num);
            }
                
          }
		
	}


	/**
	 * @param os
	 */
	public void setOutputStream(OutputStream os) {
		outputStream=os;
		
	}
    protected InputStream getResetableInputStream() throws IOException{    	
    	if ((_inputOctetStreamProxy instanceof ByteArrayInputStream) ) {            
            if (!_inputOctetStreamProxy.markSupported()) {
                throw new RuntimeException("Accepted as Markable but not truly been"+_inputOctetStreamProxy);
            }
           return _inputOctetStreamProxy;
        }
        if (bytes!=null) {
            _inputOctetStreamProxy=new ByteArrayInputStream(bytes);
            return _inputOctetStreamProxy;
        }
        if (_inputOctetStreamProxy ==null)
            return null;
        if (_inputOctetStreamProxy.markSupported()) {
            log.info("Mark Suported but not used as reset");
        }
    	bytes=JavaUtils.getBytesFromStream(_inputOctetStreamProxy);
    	_inputOctetStreamProxy=new ByteArrayInputStream(bytes);
        return _inputOctetStreamProxy;
    }
        
}
