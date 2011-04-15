/*
 * Copyright 1999-2009 The Apache Software Foundation.
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;
import org.apache.xml.security.c14n.implementations.Canonicalizer20010315OmitComments;
import org.apache.xml.security.c14n.implementations.Canonicalizer11_OmitComments;
import org.apache.xml.security.exceptions.XMLSecurityRuntimeException;
import org.apache.xml.security.utils.JavaUtils;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Class XMLSignatureInput
 *
 * @author Christian Geuer-Pollmann
 * $todo$ check whether an XMLSignatureInput can be _both_, octet stream _and_ node set?
 */
public class XMLSignatureInput implements Cloneable {
    private static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(XMLSignatureInput.class);

    /*
     * The XMLSignature Input can be either:
     *   A byteArray like with/or without InputStream.
     *   Or a nodeSet like defined either:
     *       * as a collection of nodes
     *       * or as subnode excluding or not commets and excluding or 
     *         not other nodes.
     */

    /**
     * Some InputStreams do not support the {@link java.io.InputStream#reset}
     * method, so we read it in completely and work on our Proxy.
     */
    private InputStream inputOctetStreamProxy = null;
    /**
     * The original NodeSet for this XMLSignatureInput
     */
    private Set<Node> inputNodeSet = null;
    /**
     * The original Element
     */
    private Node subNode = null;
    /**
     * Exclude Node *for enveloped transformations*
     */
    private Node excludeNode = null;
    /**
     * 
     */
    private boolean excludeComments = false;
   
    private boolean isNodeSet = false;
    /**
     * A cached bytes
     */
    private byte[] bytes = null;

    /**
     * Some Transforms may require explicit MIME type, charset (IANA registered
     * "character set"), or other such information concerning the data they are
     * receiving from an earlier Transform or the source data, although no 
     * Transform algorithm specified in this document needs such explicit 
     * information. Such data characteristics are provided as parameters to the 
     * Transform algorithm and should be described in the specification for the 
     * algorithm.
     */   
    private String MIMEType = null;

    /**
     * Field sourceURI 
     */
    private String sourceURI = null;

    /**
     * Node Filter list.
     */
    private List<NodeFilter> nodeFilters = new ArrayList<NodeFilter>();
   
    private boolean needsToBeExpanded = false;
    private OutputStream outputStream = null;

    /**
     * Construct a XMLSignatureInput from an octet array.
     * <p>
     * This is a comfort method, which internally converts the byte[] array into 
     * an InputStream
     * <p>NOTE: no defensive copy</p>
     * @param inputOctets an octet array which including XML document or node
     */
    public XMLSignatureInput(byte[] inputOctets) {
        // NO  defensive copy
        this.bytes = inputOctets;
    }

    /**
     * Constructs a <code>XMLSignatureInput</code> from an octet stream. The
     * stream is directly read.
     *
     * @param inputOctetStream
     */
    public XMLSignatureInput(InputStream inputOctetStream)  {
        this.inputOctetStreamProxy = inputOctetStream;
    }

    /**
     * Construct a XMLSignatureInput from a subtree rooted by rootNode. This
     * method included the node and <I>all</I> his descendants in the output.
     *
     * @param rootNode
     */
    public XMLSignatureInput(Node rootNode) {
        this.subNode = rootNode;
    }

    /**
     * Constructor XMLSignatureInput
     *
     * @param inputNodeSet
     */
    public XMLSignatureInput(Set<Node> inputNodeSet) {
        this.inputNodeSet = inputNodeSet;
    }

    /**
     * Check if the structure needs to be expanded.
     * @return true if so.
     */
    public boolean isNeedsToBeExpanded() {
        return needsToBeExpanded;
    }
   
    /**
     * Set if the structure needs to be expanded.
     * @param needsToBeExpanded true if so.
     */
    public void setNeedsToBeExpanded(boolean needsToBeExpanded) {
        this.needsToBeExpanded = needsToBeExpanded;
    }

    /**
     * Returns the node set from input which was specified as the parameter of 
     * {@link XMLSignatureInput} constructor
     *
     * @return the node set
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws CanonicalizationException
     */
    public Set<Node> getNodeSet() throws CanonicalizationException, ParserConfigurationException, 
        IOException, SAXException {
        return getNodeSet(false);
    }
    
    /**
     * Get the Input NodeSet.
     * @return the Input NodeSet.
     */
    public Set<Node> getInputNodeSet() {
        return inputNodeSet;
    }

    /**
     * Returns the node set from input which was specified as the parameter of 
     * {@link XMLSignatureInput} constructor
     * @param circumvent
     *
     * @return the node set
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws CanonicalizationException
     */
    public Set<Node> getNodeSet(boolean circumvent) throws ParserConfigurationException,
        IOException, SAXException, CanonicalizationException {
        if (inputNodeSet != null) {
            return inputNodeSet;
        }
        if (inputOctetStreamProxy == null && subNode != null) {
            if (circumvent) {
                XMLUtils.circumventBug2650(XMLUtils.getOwnerDocument(subNode));
            }
            inputNodeSet = new HashSet<Node>();
            XMLUtils.getSet(subNode, inputNodeSet, excludeNode, excludeComments);
            return inputNodeSet;
        } else if (this.isOctetStream()) {
            convertToNodes();
            Set<Node> result = new HashSet<Node>();
            XMLUtils.getSet(subNode, result, null, false); 
            return result;
        }

        throw new RuntimeException("getNodeSet() called but no input data present");
    }

    /**
     * Returns the Octet stream(byte Stream) from input which was specified as 
     * the parameter of {@link XMLSignatureInput} constructor
     *
     * @return the Octet stream(byte Stream) from input which was specified as 
     * the parameter of {@link XMLSignatureInput} constructor
     * @throws IOException
     */
    public InputStream getOctetStream() throws IOException  {
        if (inputOctetStreamProxy instanceof FileInputStream) {
            return inputOctetStreamProxy;
        } else {
            return getResetableInputStream();                 
        }
    }

    /**
     * @return real octet stream
     */
    public InputStream getOctetStreamReal () {
        return this.inputOctetStreamProxy;
    }

    /**
     * Returns the byte array from input which was specified as the parameter of 
     * {@link XMLSignatureInput} constructor
     *
     * @return the byte[] from input which was specified as the parameter of 
     * {@link XMLSignatureInput} constructor
     *
     * @throws CanonicalizationException
     * @throws IOException
     */
    public byte[] getBytes() throws IOException, CanonicalizationException {
        if (bytes != null) {
            return bytes;      
        }
        InputStream is = getResetableInputStream();
        if (is != null) {
            // resetable can read again bytes. 
            if (bytes == null) {
                is.reset();       
                bytes = JavaUtils.getBytesFromStream(is);
            } 	  	
            return bytes;   	  	      
        }
        Canonicalizer20010315OmitComments c14nizer = new Canonicalizer20010315OmitComments();                  
        bytes = c14nizer.engineCanonicalize(this);         
        return bytes;
    }

    /**
     * Determines if the object has been set up with a Node set
     *
     * @return true if the object has been set up with a Node set
     */
    public boolean isNodeSet() {
        return ((this.inputOctetStreamProxy == null
            && this.inputNodeSet != null) || isNodeSet);
    }

    /**
     * Determines if the object has been set up with an Element
     *
     * @return true if the object has been set up with a Node set
     */
    public boolean isElement() {
        return (this.inputOctetStreamProxy == null && this.subNode != null
            && this.inputNodeSet == null && !isNodeSet);
    }
   
    /**
     * Determines if the object has been set up with an octet stream
     *
     * @return true if the object has been set up with an octet stream
     */
    public boolean isOctetStream() {
        return ((this.inputOctetStreamProxy != null || bytes != null)
          && (this.inputNodeSet == null && subNode == null));
    }

    /**
     * Determines if {@link #setOutputStream} has been called with a 
     * non-null OutputStream.
     *
     * @return true if {@link #setOutputStream} has been called with a 
     * non-null OutputStream
     */
    public boolean isOutputStreamSet() {
        return outputStream != null;
    }
   
    /**
     * Determines if the object has been set up with a ByteArray
     *
     * @return true is the object has been set up with an octet stream
     */
    public boolean isByteArray() {
        return (bytes != null && (this.inputNodeSet == null && subNode == null));
    }

    /**
     * Is the object correctly set up?
     *
     * @return true if the object has been set up correctly
     */
    public boolean isInitialized() {
        return this.isOctetStream() || this.isNodeSet();
    }

    /**
     * Returns MIMEType
     *
     * @return MIMEType
     */
    public String getMIMEType() {
        return this.MIMEType;
    }

    /**
     * Sets MIMEType
     *
     * @param MIMEType
     */
    public void setMIMEType(String mimeType) {
        this.MIMEType = mimeType;
    }

    /**
     * Return SourceURI
     *
     * @return SourceURI
     */
    public String getSourceURI() {
        return this.sourceURI;
    }

    /**
     * Sets SourceURI
     *
     * @param SourceURI
     */
    public void setSourceURI(String sourceURI) {
        this.sourceURI = sourceURI;
    }
   
    /**
     * Method toString
     * @inheritDoc
     */
    public String toString() {
        if (this.isNodeSet()) {
            return "XMLSignatureInput/NodeSet/" + this.inputNodeSet.size()
                   + " nodes/" + this.getSourceURI();         
        } 
        if (this.isElement()) {
            return "XMLSignatureInput/Element/" + this.subNode
                + " exclude "+ this.excludeNode + " comments:" 
                + this.excludeComments +"/" + this.getSourceURI();
        }
        try {
            return "XMLSignatureInput/OctetStream/" + this.getBytes().length
                   + " octets/" + this.getSourceURI();
        } catch (IOException iex) {
            return "XMLSignatureInput/OctetStream//" + this.getSourceURI();
        } catch (CanonicalizationException cex) {
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
    public String getHTMLRepresentation(Set<String> inclusiveNamespaces)
       throws XMLSignatureException {
        XMLSignatureInputDebugger db = 
            new XMLSignatureInputDebugger(this, inclusiveNamespaces);
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
        return subNode;
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
    public void updateOutputStream(OutputStream diOs)
        throws CanonicalizationException, IOException {        
        updateOutputStream(diOs, false);
    }
    
    public void updateOutputStream(OutputStream diOs, boolean c14n11) 
        throws CanonicalizationException, IOException {        
        if (diOs == outputStream) {
            return;
        }
        if (bytes != null) {
            diOs.write(bytes);
        } else if (inputOctetStreamProxy == null) {                    
            CanonicalizerBase c14nizer = null;
            if (c14n11) {
                c14nizer = new Canonicalizer11_OmitComments();       
            } else {
                c14nizer = new Canonicalizer20010315OmitComments();       
            }
            c14nizer.setWriter(diOs);
            c14nizer.engineCanonicalize(this); 
        } else {
            if (inputOctetStreamProxy instanceof FileInputStream) {
                byte[] buffer = new byte[4 * 1024];
                int bytesread = 0;
                while ((bytesread = inputOctetStreamProxy.read(buffer)) != -1) {
                    diOs.write(buffer, 0, bytesread);
                }
            } else {
                InputStream is = getResetableInputStream();
                if (bytes != null) {
                    // already read write it, can be rea.
                    diOs.write(bytes, 0, bytes.length);
                } else {
                    is.reset();            
                    byte[] bytesT = new byte[1024];
                    int num = 0;
                    while ((num = is.read(bytesT)) > 0) {
                        diOs.write(bytesT, 0, num);
                    }
                }
            }
        }
    }

    /**
     * @param os
     */
    public void setOutputStream(OutputStream os) {
        outputStream = os;
    }

    protected InputStream getResetableInputStream() throws IOException {    	
        if (inputOctetStreamProxy instanceof ByteArrayInputStream) {
            if (!inputOctetStreamProxy.markSupported()) {
                throw new RuntimeException(
                    "Accepted as Markable but not truly been" + inputOctetStreamProxy
                );
            }
            return inputOctetStreamProxy;
        }
        if (bytes != null) {
            inputOctetStreamProxy = new ByteArrayInputStream(bytes);
            return inputOctetStreamProxy;
        }
        if (inputOctetStreamProxy == null) {
            return null;
        }
        if (inputOctetStreamProxy.markSupported()) {
            if (log.isDebugEnabled()) {
                log.debug("Mark Suported but not used as reset");
            }
        }
        bytes = JavaUtils.getBytesFromStream(inputOctetStreamProxy);
        inputOctetStreamProxy.close();
        inputOctetStreamProxy = new ByteArrayInputStream(bytes);
        return inputOctetStreamProxy;
    }
        
    /**
     * @param filter
     */
    public void addNodeFilter(NodeFilter filter) {	
        if (isOctetStream()) {
            try {
                convertToNodes();
            } catch (Exception e) {
                throw new XMLSecurityRuntimeException(
                    "signature.XMLSignatureInput.nodesetReference", e
                );
            }
        }
        nodeFilters.add(filter);
    }

    /**
     * @return the node filters
     */
    public List<NodeFilter> getNodeFilters() {
        return nodeFilters;
    }

    /**
     * @param b
     */
    public void setNodeSet(boolean b) {
        isNodeSet = b;
    }
        
    void convertToNodes() throws CanonicalizationException, 
        ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
        dfactory.setValidating(false);        
        dfactory.setNamespaceAware(true);
        DocumentBuilder db = dfactory.newDocumentBuilder();
        // select all nodes, also the comments.        
        try {
            db.setErrorHandler(new org.apache.xml.security.utils.IgnoreAllErrorHandler());

            Document doc = db.parse(this.getOctetStream());
            this.subNode = doc;
        } catch (SAXException ex) {
            // if a not-wellformed nodeset exists, put a container around it...
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            baos.write("<container>".getBytes("UTF-8"));
            baos.write(this.getBytes());
            baos.write("</container>".getBytes("UTF-8"));

            byte result[] = baos.toByteArray();
            Document document = db.parse(new ByteArrayInputStream(result));
            this.subNode = document.getDocumentElement().getFirstChild().getFirstChild();				
        }
        this.inputOctetStreamProxy = null;
        this.bytes = null;
    }
}
