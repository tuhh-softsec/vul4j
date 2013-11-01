/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.xml.security.test.dom.c14n.implementations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.test.dom.DSNamespaceContext;
import org.apache.xml.security.utils.IgnoreAllErrorHandler;
import org.apache.xml.security.utils.JavaUtils;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Unit test for 
 * {@link org.apache.xml.security.c14n.implementations.Canonicalizer11}
 */
public class Canonicalizer11Test extends org.junit.Assert {

    static org.slf4j.Logger log =
        org.slf4j.LoggerFactory.getLogger(Canonicalizer11Test.class);

    static {
        org.apache.xml.security.Init.init();
    }
    
    /** Field prefix */
    private String prefix;
    
    public Canonicalizer11Test() {
        prefix = "src/test/resources/org/apache/xml/security/c14n/";
        String basedir = System.getProperty("basedir");
        if (basedir != null && !"".equals(basedir)) {
            prefix = basedir + "/" + prefix;
        }
    }

    /**
     * 3.1 PIs, Comments, and Outside of Document Element
     *
     * @throws CanonicalizationException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws InvalidCanonicalizerException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws TransformerException
     * @throws XPathExpressionException 
     */
    @org.junit.Test
    public void test31withCommentsSubtree()
        throws IOException, FileNotFoundException, SAXException,
        ParserConfigurationException, CanonicalizationException,
        InvalidCanonicalizerException, TransformerException, XPathExpressionException {
        String descri =
            "3.1: PIs, Comments, and Outside of Document Element. (commented)";

        String fileIn = prefix + "in/31_input.xml";
        String fileRef = prefix + "in/31_c14n-comments.xml";
        String fileOut = prefix + "out/xpath_31_output-comments.xml";
        String c14nURI = Canonicalizer.ALGO_ID_C14N11_WITH_COMMENTS;
        boolean validating = true;
        String xpath = null;

        assertTrue(descri,
                   c14nAndCompare(fileIn, fileRef, fileOut, c14nURI, validating, xpath));
    }
    
    /**
     * 3.2 Whitespace in Document Content
     *
     * @throws CanonicalizationException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws InvalidCanonicalizerException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @see <A HREF="http://www.w3.org/TR/2001/PR-xml-c14n-20010119#Example-WhitespaceInContent">the example from the spec</A>
     * @throws TransformerException
     * @throws XPathExpressionException 
     */
    @org.junit.Test
    public void test32subtree()
        throws IOException, FileNotFoundException, SAXException,
        ParserConfigurationException, CanonicalizationException,
        InvalidCanonicalizerException, TransformerException, XPathExpressionException {
        String descri = "3.2 Whitespace in Document Content. (uncommented)";
        String fileIn = prefix + "in/32_input.xml";
        String fileRef = prefix + "in/32_c14n.xml";
        String fileOut = prefix + "out/xpath_32_output.xml";
        String c14nURI = Canonicalizer.ALGO_ID_C14N11_OMIT_COMMENTS;
        boolean validating = true;
        String xpath = null;

        assertTrue(descri,
                   c14nAndCompare(fileIn, fileRef, fileOut, c14nURI, validating, xpath));
    }

    /**
     * 3.3 Start and End Tags
     *
     * @throws CanonicalizationException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws InvalidCanonicalizerException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @see <A HREF="http://www.w3.org/TR/2001/PR-xml-c14n-20010119#Example-SETags">the example from the spec</A>
     * @throws TransformerException
     * @throws XPathExpressionException 
     */
    @org.junit.Test
    public void test33subtree()
        throws IOException, FileNotFoundException, SAXException,
        ParserConfigurationException, CanonicalizationException,
        InvalidCanonicalizerException, TransformerException, XPathExpressionException {
        String descri = "3.3 Start and End Tags. (uncommented)";
        String fileIn = prefix + "in/33_input.xml";
        String fileRef = prefix + "in/33_c14n.xml";
        String fileOut = prefix + "out/xpath_33_output.xml";
        String c14nURI = Canonicalizer.ALGO_ID_C14N11_OMIT_COMMENTS;
        boolean validating = true;
        String xpath = null;    // Canonicalizer.XPATH_C14N_OMIT_COMMENTS_SINGLE_NODE;

        assertTrue(descri,
                   c14nAndCompare(fileIn, fileRef, fileOut, c14nURI, validating, xpath));
    }

    /**
     * 3.4 Character Modifications and Character References
     *
     * @throws CanonicalizationException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws InvalidCanonicalizerException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @see #test34validatingParser
     * @see <A HREF="http://www.w3.org/TR/2001/PR-xml-c14n-20010119#Example-Chars">the example from the spec</A>
     * @throws TransformerException
     * @throws XPathExpressionException 
     */
    @org.junit.Test
    public void test34()
        throws IOException, FileNotFoundException, SAXException,
        ParserConfigurationException, CanonicalizationException,
        InvalidCanonicalizerException, TransformerException, XPathExpressionException {
        String descri =
            "3.4 Character Modifications and Character References. (uncommented)";
        String fileIn = prefix + "in/34_input.xml";
        String fileRef = prefix + "in/34_c14n.xml";
        String fileOut = prefix + "out/xpath_34_output.xml";
        String c14nURI = Canonicalizer.ALGO_ID_C14N11_OMIT_COMMENTS;
        boolean validating = false;
        String xpath = null;

        assertTrue(descri,
                   c14nAndCompare(fileIn, fileRef, fileOut, c14nURI, validating, xpath));
    }

    /**
     * 3.5 Entity References
     *
     * @throws CanonicalizationException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws InvalidCanonicalizerException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @see <A HREF="http://www.w3.org/TR/2001/PR-xml-c14n-20010119#Example-Entities">the example from the spec</A>
     * @throws TransformerException
     * @throws XPathExpressionException 
     */
    @org.junit.Test
    public void test35subtree()
        throws IOException, FileNotFoundException, SAXException,
        ParserConfigurationException, CanonicalizationException,
        InvalidCanonicalizerException, TransformerException, XPathExpressionException {
        String descri = "3.5 Entity References. (uncommented)";
        String fileIn = prefix + "in/35_input.xml";
        String fileRef = prefix + "in/35_c14n.xml";
        String fileOut = prefix + "out/xpath_35_output.xml";
        String c14nURI = Canonicalizer.ALGO_ID_C14N11_OMIT_COMMENTS;
        boolean validating = true;
        String xpath = null;

        assertTrue(descri,
                   c14nAndCompare(fileIn, fileRef, fileOut, c14nURI, validating, xpath));
    }
    
    /**
     * 3.6 UTF-8 Encoding
     *
     * @throws CanonicalizationException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws InvalidCanonicalizerException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @see <A HREF="http://www.w3.org/TR/2001/PR-xml-c14n-20010119#Example-UTF8">the example from the spec</A>
     * @throws TransformerException
     * @throws XPathExpressionException 
     */
    @org.junit.Test
    public void test36subtree()
        throws IOException, FileNotFoundException, SAXException,
        ParserConfigurationException, CanonicalizationException,
        InvalidCanonicalizerException, TransformerException, XPathExpressionException {
        String descri = "3.6 UTF-8 Encoding. (uncommented)";
        String fileIn = prefix + "in/36_input.xml";
        String fileRef = prefix + "in/36_c14n.xml";
        String fileOut = prefix + "out/xpath_36_output.xml";
        String c14nURI = Canonicalizer.ALGO_ID_C14N11_OMIT_COMMENTS;
        boolean validating = true;
        String xpath = null;

        assertTrue(descri,
                   c14nAndCompare(fileIn, fileRef, fileOut, c14nURI, validating, xpath));
    }
    
    /**
     * 3.7 Document Subsets
     *
     * @throws CanonicalizationException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws InvalidCanonicalizerException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @see <A HREF="http://www.w3.org/TR/2001/PR-xml-c14n-20010119#Example-DocSubsets">the example from the spec</A>
     * @throws TransformerException
     * @throws XPathExpressionException 
     */
    @org.junit.Test
    public void test37()
        throws IOException, FileNotFoundException, SAXException,
        ParserConfigurationException, CanonicalizationException,
        InvalidCanonicalizerException, TransformerException, XPathExpressionException {
        String descri = "3.7 Document Subsets. (uncommented)";
        String fileIn = prefix + "in/37_input.xml";
        String fileRef = prefix + "in/37_c14n.xml";
        String fileOut = prefix + "out/xpath_37_output.xml";
        String c14nURI = Canonicalizer.ALGO_ID_C14N11_OMIT_COMMENTS;
        boolean validating = true;

        Map<String, String> namespace = new HashMap<String, String>();
        namespace.put("ietf", "http://www.ietf.org");
        String xpath =
            "(//. | //@* | //namespace::*)"
            + "[ "
            + "self::ietf:e1 or "
            + "(parent::ietf:e1 and not(self::text() or self::e2)) or "
            + "count(id(\"E3\")|ancestor-or-self::node()) = count(ancestor-or-self::node()) "
            + "]";

        assertTrue(descri,
                   c14nAndCompare(fileIn, fileRef, fileOut, c14nURI, validating, xpath, namespace));
    }
    
    /**
     * 3.8 Document Subsets and XML Attributes
     *
     * @throws CanonicalizationException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws InvalidCanonicalizerException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws TransformerException
     * @throws XPathExpressionException 
     */
    @org.junit.Test
    public void test38()
        throws IOException, FileNotFoundException, SAXException,
        ParserConfigurationException, CanonicalizationException,
        InvalidCanonicalizerException, TransformerException, XPathExpressionException {
        String descri = "3.8 Document Subsets and XML Attributes (uncommented)";
        String fileIn = prefix + "in/38_input.xml";
        String fileRef = prefix + "in/38_c14n.xml";
        String fileOut = prefix + "out/xpath_38_output.xml";
        String c14nURI = Canonicalizer.ALGO_ID_C14N11_OMIT_COMMENTS;
        boolean validating = true;

        Map<String, String> namespace = new HashMap<String, String>();
        namespace.put("ietf", "http://www.ietf.org");
        String xpath =
            "(//. | //@* | //namespace::*)"
            + "[ "
            + "self::ietf:e1 or "
            + "(parent::ietf:e1 and not(self::text() or self::e2)) or "
            + "count(id(\"E3\")|ancestor-or-self::node()) = count(ancestor-or-self::node()) "
            + "]";

        assertTrue(descri,
                   c14nAndCompare(fileIn, fileRef, fileOut, c14nURI, validating, xpath, namespace));
    }

    private boolean c14nAndCompare(
                                   String fileIn, 
                                   String fileRef, 
                                   String fileOut, 
                                   String c14nURI, 
                                   boolean validating,
                                   String xpath
                               ) throws IOException, FileNotFoundException, SAXException,
                                   ParserConfigurationException, CanonicalizationException,
                                   InvalidCanonicalizerException, TransformerException, XPathExpressionException {
        Map<String, String> namespace = new HashMap<String, String>();
        return c14nAndCompare(fileIn, fileRef, fileOut, 
            c14nURI, validating, xpath, namespace);
    }
    
    private boolean c14nAndCompare(
        String fileIn, 
        String fileRef, 
        String fileOut, 
        String c14nURI, 
        boolean validating,
        String xpath,
        Map<String, String> namespaces
    ) throws IOException, FileNotFoundException, SAXException,
        ParserConfigurationException, CanonicalizationException,
        InvalidCanonicalizerException, TransformerException, XPathExpressionException {

        DocumentBuilder documentBuilder = XMLUtils.createDocumentBuilder(validating, false);

        // throw away all warnings and errors
        documentBuilder.setErrorHandler(new IgnoreAllErrorHandler());

        // org.xml.sax.EntityResolver resolver = new TestVectorResolver();
        // documentBuilder.setEntityResolver(resolver);
        // Document doc = documentBuilder.parse(resolver.resolveEntity(null, fileIn));

        Document doc = documentBuilder.parse(fileIn);


        Canonicalizer c14n = Canonicalizer.getInstance(c14nURI);
        byte c14nBytes[] = null;

        if (xpath == null) {
            c14nBytes = c14n.canonicalizeSubtree(doc);
        } else {
            NodeList nl = null;
            
            XPathFactory xpf = XPathFactory.newInstance();
            XPath xPath = xpf.newXPath();
            DSNamespaceContext namespaceContext =
                new DSNamespaceContext(namespaces);
            xPath.setNamespaceContext(namespaceContext);

            nl = (NodeList)xPath.evaluate(xpath, doc, XPathConstants.NODESET);

            c14nBytes = c14n.canonicalizeXPathNodeSet(nl);
        }

        // org.xml.sax.InputSource refIs = resolver.resolveEntity(null, fileRef);
        // byte refBytes[] = JavaUtils.getBytesFromStream(refIs.getByteStream());
        byte refBytes[] = JavaUtils.getBytesFromFile(fileRef);

        // if everything is OK, result is true; we do a binary compare, byte by byte
        boolean result = java.security.MessageDigest.isEqual(refBytes, c14nBytes);

        if (!result) {    	  
            File f = new File(fileOut);
            if (!f.exists()) {
                File parent = new File(f.getParent());
                parent.mkdirs();
                f.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(f);

            fos.write(c14nBytes);
            log.debug("Wrote erroneous result to file " + f.toURI().toURL().toString());
            assertEquals(new String(refBytes), new String(c14nBytes));
            fos.close();
        }

        return result;
    }

}
