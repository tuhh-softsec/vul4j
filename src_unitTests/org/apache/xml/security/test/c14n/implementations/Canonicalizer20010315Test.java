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
package org.apache.xml.security.test.c14n.implementations;



import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.test.resource.TestVectorResolver;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.IgnoreAllErrorHandler;
import org.apache.xml.security.utils.JavaUtils;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xpath.CachedXPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;


/**
 * Unit test for {@link org.apache.xml.security.c14n.implementations.Canonicalizer20010315WithXPath}
 *
 * @author Christian Geuer-Pollmann
 */
public class Canonicalizer20010315Test extends TestCase {

   /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(Canonicalizer20010315Test.class.getName());

   /**
    * Method suite
    *
    *
    */
   public static Test suite() {
      return new TestSuite(Canonicalizer20010315Test.class);
   }

   /**
    *  Constructor Canonicalizer20010315WithXPathTest
    *
    *  @param Name_
    */
   public Canonicalizer20010315Test(String Name_) {
      super(Name_);
   }

   /**
    * Method main
    *
    * @param args
    */
   public static void main(String[] args) {

      String[] testCaseName = { "-noloading",
                                Canonicalizer20010315Test.class.getName() };

      junit.textui.TestRunner.main(testCaseName);
   }

   /** Field prefix */
   private static String prefix;

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
    */
   public static void test31withCommentsSubtree()
           throws IOException, FileNotFoundException, SAXException,
                  ParserConfigurationException, CanonicalizationException,
                  InvalidCanonicalizerException, TransformerException {

      String descri =
         "3.1: PIs, Comments, and Outside of Document Element. (commented)";


      String fileIn = prefix + "in/31_input.xml";
      String fileRef = prefix + "in/31_c14n-comments.xml";
      String fileOut = prefix + "out/xpath_31_output-comments.xml";
      String c14nURI = Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS;
      boolean validating = true;
      String xpath = null;

      assertTrue(descri,
                 c14nAndCompare(fileIn, fileRef, fileOut, c14nURI, validating,
                                xpath));
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
    */
   public static void test31withCommentsSubset()
           throws IOException, FileNotFoundException, SAXException,
                  ParserConfigurationException, CanonicalizationException,
                  InvalidCanonicalizerException, TransformerException {

      String descri =
         "3.1: PIs, Comments, and Outside of Document Element. (commented)";


      String fileIn = prefix + "in/31_input.xml";
      String fileRef = prefix + "in/31_c14n-comments.xml";
      String fileOut = prefix + "out/xpath_31_output-comments.xml";
      String c14nURI = Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS;
      boolean validating = true;
      String xpath = Canonicalizer.XPATH_C14N_WITH_COMMENTS_SINGLE_NODE;

      assertTrue(descri,
                 c14nAndCompare(fileIn, fileRef, fileOut, c14nURI, validating,
                                xpath));
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
    * @see <A HREF="http://www.w3.org/TR/2001/PR-xml-c14n-20010119#Example-OutsideDoc">the example from the spec</A>
    * @throws TransformerException
    */
   public static void test31subtree()
           throws IOException, FileNotFoundException, SAXException,
                  ParserConfigurationException, CanonicalizationException,
                  InvalidCanonicalizerException, TransformerException {

      String descri =
         "3.1: PIs, Comments, and Outside of Document Element. (uncommented)";
      String fileIn = prefix + "in/31_input.xml";
      String fileRef = prefix + "in/31_c14n.xml";
      String fileOut = prefix + "out/xpath_31_output.xml";
      String c14nURI = Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS;
      boolean validating = true;
      String xpath = null;

      assertTrue(descri,
                 c14nAndCompare(fileIn, fileRef, fileOut, c14nURI, validating,
                                xpath));
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
    * @see <A HREF="http://www.w3.org/TR/2001/PR-xml-c14n-20010119#Example-OutsideDoc">the example from the spec</A>
    * @throws TransformerException
    */
   public static void test31subset()
           throws IOException, FileNotFoundException, SAXException,
                  ParserConfigurationException, CanonicalizationException,
                  InvalidCanonicalizerException, TransformerException {

      String descri =
         "3.1: PIs, Comments, and Outside of Document Element. (uncommented)";
      String fileIn = prefix + "in/31_input.xml";
      String fileRef = prefix + "in/31_c14n.xml";
      String fileOut = prefix + "out/xpath_31_output.xml";
      String c14nURI = Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS;
      boolean validating = true;
      String xpath = Canonicalizer.XPATH_C14N_WITH_COMMENTS_SINGLE_NODE;

      assertTrue(descri,
                 c14nAndCompare(fileIn, fileRef, fileOut, c14nURI, validating,
                                xpath));
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
    */
   public static void test32subtree()
           throws IOException, FileNotFoundException, SAXException,
                  ParserConfigurationException, CanonicalizationException,
                  InvalidCanonicalizerException, TransformerException {

      String descri = "3.2 Whitespace in Document Content. (uncommented)";
      String fileIn = prefix + "in/32_input.xml";
      String fileRef = prefix + "in/32_c14n.xml";
      String fileOut = prefix + "out/xpath_32_output.xml";
      String c14nURI = Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS;
      boolean validating = true;
      String xpath = null;

      assertTrue(descri,
                 c14nAndCompare(fileIn, fileRef, fileOut, c14nURI, validating,
                                xpath));
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
    */
   public static void test32subset()
           throws IOException, FileNotFoundException, SAXException,
                  ParserConfigurationException, CanonicalizationException,
                  InvalidCanonicalizerException, TransformerException {

      String descri = "3.2 Whitespace in Document Content. (uncommented)";
      String fileIn = prefix + "in/32_input.xml";
      String fileRef = prefix + "in/32_c14n.xml";
      String fileOut = prefix + "out/xpath_32_output.xml";
      String c14nURI = Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS;
      boolean validating = true;
      String xpath = Canonicalizer.XPATH_C14N_WITH_COMMENTS_SINGLE_NODE;

      assertTrue(descri,
                 c14nAndCompare(fileIn, fileRef, fileOut, c14nURI, validating,
                                xpath));
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
    */
   public static void test33subtree()
           throws IOException, FileNotFoundException, SAXException,
                  ParserConfigurationException, CanonicalizationException,
                  InvalidCanonicalizerException, TransformerException {

      String descri = "3.3 Start and End Tags. (uncommented)";
      String fileIn = prefix + "in/33_input.xml";
      String fileRef = prefix + "in/33_c14n.xml";
      String fileOut = prefix + "out/xpath_33_output.xml";
      String c14nURI = Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS;
      boolean validating = true;
      String xpath = null;    // Canonicalizer.XPATH_C14N_OMIT_COMMENTS_SINGLE_NODE;

      assertTrue(descri,
                 c14nAndCompare(fileIn, fileRef, fileOut, c14nURI, validating,
                                xpath));
   }

   public static void test33subset()
           throws IOException, FileNotFoundException, SAXException,
                  ParserConfigurationException, CanonicalizationException,
                  InvalidCanonicalizerException, TransformerException {

      String descri = "3.3 Start and End Tags. (uncommented)";
      String fileIn = prefix + "in/33_input.xml";
      String fileRef = prefix + "in/33_c14n.xml";
      String fileOut = prefix + "out/xpath_33_output.xml";
      String c14nURI = Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS;
      boolean validating = true;
      String xpath = Canonicalizer.XPATH_C14N_WITH_COMMENTS_SINGLE_NODE;

      assertTrue(descri,
                 c14nAndCompare(fileIn, fileRef, fileOut, c14nURI, validating,
                                xpath));
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
    */
   public static void test34()
           throws IOException, FileNotFoundException, SAXException,
                  ParserConfigurationException, CanonicalizationException,
                  InvalidCanonicalizerException, TransformerException
   {
   	
   	  //TODO Check what we have to do to get this f*cking test working!!!

      String descri =
         "3.4 Character Modifications and Character References. (uncommented)";
      String fileIn = prefix + "in/34_input.xml";
      String fileRef = prefix + "in/34_c14n.xml";
      String fileOut = prefix + "out/xpath_34_output.xml";
      String c14nURI = Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS;
      boolean validating = false;
      String xpath = null;

      assertTrue(descri,
                 c14nAndCompare(fileIn, fileRef, fileOut, c14nURI, validating,
                                xpath));
   }

   /**
    * 3.4 Character Modifications and Character References (patched to run on validating Parsers)
    * <P>
    * <A HREF="http://www.w3.org/TR/2001/PR-xml-c14n-20010119"> The spec</A> states that:
    * <P>
    * Note: The last element, normId, is well-formed but violates a validity
    * constraint for attributes of type ID. For testing canonical XML
    * implementations based on validating processors, remove the line
    * containing this element from the input and canonical form. In general,
    * XML consumers should be discouraged from using this feature of XML.
    *
    * @throws CanonicalizationException
    * @throws FileNotFoundException
    * @throws IOException
    * @throws InvalidCanonicalizerException
    * @throws ParserConfigurationException
    * @throws SAXException
    * @see <A HREF="http://www.w3.org/TR/2001/PR-xml-c14n-20010119#Example-Chars">the example from the spec</A>
    * @throws TransformerException
    */
   public static void test34subtree()
           throws IOException, FileNotFoundException, SAXException,
                  ParserConfigurationException, CanonicalizationException,
                  InvalidCanonicalizerException, TransformerException {

      String descri =
         "3.4 Character Modifications and Character References. (uncommented, patched to run on validating Parsers)";
      String fileIn = prefix + "in/34_input_validatingParser.xml";
      String fileRef = prefix + "in/34_c14n_validatingParser.xml";
      String fileOut = prefix + "out/xpath_34_output_validatingParser.xml";
      String c14nURI = Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS;
      boolean validating = true;
      String xpath = null;

      assertTrue(descri,
                 c14nAndCompare(fileIn, fileRef, fileOut, c14nURI, validating,
                                xpath));
   }
   /**
    * 3.4 Character Modifications and Character References (patched to run on validating Parsers)
    * <P>
    * <A HREF="http://www.w3.org/TR/2001/PR-xml-c14n-20010119"> The spec</A> states that:
    * <P>
    * Note: The last element, normId, is well-formed but violates a validity
    * constraint for attributes of type ID. For testing canonical XML
    * implementations based on validating processors, remove the line
    * containing this element from the input and canonical form. In general,
    * XML consumers should be discouraged from using this feature of XML.
    *
    * @throws CanonicalizationException
    * @throws FileNotFoundException
    * @throws IOException
    * @throws InvalidCanonicalizerException
    * @throws ParserConfigurationException
    * @throws SAXException
    * @see <A HREF="http://www.w3.org/TR/2001/PR-xml-c14n-20010119#Example-Chars">the example from the spec</A>
    * @throws TransformerException
    */
   public static void test34subset()
           throws IOException, FileNotFoundException, SAXException,
                  ParserConfigurationException, CanonicalizationException,
                  InvalidCanonicalizerException, TransformerException {

      String descri =
         "3.4 Character Modifications and Character References. (uncommented, patched to run on validating Parsers)";
      String fileIn = prefix + "in/34_input_validatingParser.xml";
      String fileRef = prefix + "in/34_c14n_validatingParser.xml";
      String fileOut = prefix + "out/xpath_34_output_validatingParser.xml";
      String c14nURI = Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS;
      boolean validating = true;
      String xpath = Canonicalizer.XPATH_C14N_WITH_COMMENTS_SINGLE_NODE;

      assertTrue(descri,
                 c14nAndCompare(fileIn, fileRef, fileOut, c14nURI, validating,
                                xpath));
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
    */
   public static void test35subtree()
           throws IOException, FileNotFoundException, SAXException,
                  ParserConfigurationException, CanonicalizationException,
                  InvalidCanonicalizerException, TransformerException {

      String descri = "3.5 Entity References. (uncommented)";
      String fileIn = prefix + "in/35_input.xml";
      String fileRef = prefix + "in/35_c14n.xml";
      String fileOut = prefix + "out/xpath_35_output.xml";
      String c14nURI = Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS;
      boolean validating = true;
      String xpath = null;

      assertTrue(descri,
                 c14nAndCompare(fileIn, fileRef, fileOut, c14nURI, validating,
                                xpath));
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
    */
   public static void test35subset()
           throws IOException, FileNotFoundException, SAXException,
                  ParserConfigurationException, CanonicalizationException,
                  InvalidCanonicalizerException, TransformerException {

      String descri = "3.5 Entity References. (uncommented)";
      String fileIn = prefix + "in/35_input.xml";
      String fileRef = prefix + "in/35_c14n.xml";
      String fileOut = prefix + "out/xpath_35_output.xml";
      String c14nURI = Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS;
      boolean validating = true;
      String xpath = Canonicalizer.XPATH_C14N_WITH_COMMENTS_SINGLE_NODE;

      assertTrue(descri,
                 c14nAndCompare(fileIn, fileRef, fileOut, c14nURI, validating,
                                xpath));
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
    */
   public static void test36subtree()
           throws IOException, FileNotFoundException, SAXException,
                  ParserConfigurationException, CanonicalizationException,
                  InvalidCanonicalizerException, TransformerException {

      String descri = "3.6 UTF-8 Encoding. (uncommented)";
      String fileIn = prefix + "in/36_input.xml";
      String fileRef = prefix + "in/36_c14n.xml";
      String fileOut = prefix + "out/xpath_36_output.xml";
      String c14nURI = Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS;
      boolean validating = true;
      String xpath = null;

      assertTrue(descri,
                 c14nAndCompare(fileIn, fileRef, fileOut, c14nURI, validating,
                                xpath));
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
    */
   public static void test36subset()
           throws IOException, FileNotFoundException, SAXException,
                  ParserConfigurationException, CanonicalizationException,
                  InvalidCanonicalizerException, TransformerException {

      String descri = "3.6 UTF-8 Encoding. (uncommented)";
      String fileIn = prefix + "in/36_input.xml";
      String fileRef = prefix + "in/36_c14n.xml";
      String fileOut = prefix + "out/xpath_36_output.xml";
      String c14nURI = Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS;
      boolean validating = true;
      String xpath = Canonicalizer.XPATH_C14N_WITH_COMMENTS_SINGLE_NODE;

      assertTrue(descri,
                 c14nAndCompare(fileIn, fileRef, fileOut, c14nURI, validating,
                                xpath));
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
    */
   public static void test37()
           throws IOException, FileNotFoundException, SAXException,
                  ParserConfigurationException, CanonicalizationException,
                  InvalidCanonicalizerException, TransformerException {

      String descri = "3.7 Document Subsets. (uncommented)";
      String fileIn = prefix + "in/37_input.xml";
      String fileRef = prefix + "in/37_c14n.xml";
      String fileOut = prefix + "out/xpath_37_output.xml";
      String c14nURI = Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS;
      boolean validating = true;
      Element xpath = null;
      DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();

      dfactory.setNamespaceAware(true);

      DocumentBuilder db = dfactory.newDocumentBuilder();
      Document doc = db.newDocument();

      xpath = XMLUtils.createElementInSignatureSpace(doc, Constants._TAG_XPATH);

      xpath.setAttributeNS(Constants.NamespaceSpecNS, "xmlns:ietf", "http://www.ietf.org");

      //J-
         String xpathFromSpec =
              "(//. | //@* | //namespace::*)"
            + "[ "
            + "self::ietf:e1 or "
            + "(parent::ietf:e1 and not(self::text() or self::e2)) or "
            + "count(id(\"E3\")|ancestor-or-self::node()) = count(ancestor-or-self::node()) "
            + "]";

         //J+
      xpath.appendChild(doc.createTextNode(xpathFromSpec));
      assertTrue(descri,
                 c14nAndCompare(fileIn, fileRef, fileOut, c14nURI, validating,
                                xpath));
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
    */
   public static void test37byNodeList()
           throws IOException, FileNotFoundException, SAXException,
                  ParserConfigurationException, CanonicalizationException,
                  InvalidCanonicalizerException, TransformerException {

      //String descri = "3.7 Document Subsets. (uncommented), c14n by NodeList";
      String fileIn = prefix + "in/37_input.xml";
      String fileRef = prefix + "in/37_c14n.xml";
      //String c14nURI = Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS;
      //boolean validating = true;
      DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();

      dfactory.setNamespaceAware(true);

      DocumentBuilder db = dfactory.newDocumentBuilder();
      org.xml.sax.EntityResolver resolver = new TestVectorResolver();

      db.setEntityResolver(resolver);

      Document doc = db.parse(resolver.resolveEntity(null, fileIn));
      //J-
      Element nscontext = XMLUtils.createDSctx(doc, "ietf", "http://www.ietf.org");

      String xpath = "(//. | //@* | //namespace::*)"
         + "[ "
         + "self::ietf:e1 or "
         + "(parent::ietf:e1 and not(self::text() or self::e2)) or "
         + "count(id(\"E3\")|ancestor-or-self::node()) = count(ancestor-or-self::node()) "
         + "]";
      //J+
      CachedXPathAPI xpathAPI = new CachedXPathAPI();

      XMLUtils.circumventBug2650(doc);

      NodeList nodes = xpathAPI.selectNodeList(doc, xpath, nscontext);
      Canonicalizer c14n =
         Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS);
      byte c14nBytes[] = c14n.canonicalizeXPathNodeSet(nodes);
      InputStream refStream = resolver.resolveEntity(null,
                                 fileRef).getByteStream();
      byte refBytes[] = JavaUtils.getBytesFromStream(refStream);
      boolean equal = JavaUtils.binaryCompare(refBytes, c14nBytes);

      assertTrue(equal);
   }

   /**
    * Method testDefaultNamespaceOverridden
    *
    * @throws CanonicalizationException
    * @throws FileNotFoundException
    * @throws IOException
    * @throws InvalidCanonicalizerException
    * @throws ParserConfigurationException
    * @throws SAXException
    * @throws TransformerException
    * public static void _testDefaultNamespaceOverridden()
    *       throws IOException, FileNotFoundException, SAXException,
    *              ParserConfigurationException, CanonicalizationException,
    *              InvalidCanonicalizerException, TransformerException {
    *
    *  String descri = "Default namespace overridden";
    *  String fileIn =
    *     "data/org/apache/xml/security/temp/key/retrieval-from-same-doc.xml";
    *  String fileRef =
    *     "data/org/apache/xml/security/temp/key/retrieval-from-same-doc-key.xml";
    *  String fileOut =
    *     "data/org/apache/xml/security/temp/key/retrieval-from-same-doc-key-error.xml";
    *  String c14nURI = Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS;
    *  boolean validating = false;
    *  DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
    *
    *  dfactory.setNamespaceAware(true);
    *
    *  DocumentBuilder db = dfactory.newDocumentBuilder();
    *  // org.xml.sax.EntityResolver resolver = new TestVectorResolver();
    *  // db.setEntityResolver(resolver);
    *
    *  // Document doc = db.parse(resolver.resolveEntity(null, fileIn));
    *  Document doc = db.parse(fileIn);
    *  Element nscontext = doc.createElement("container");
    *
    *  nscontext.setAttribute("xmlns:ds", "http://www.w3.org/2000/09/xmldsig#");
    *  nscontext.setAttribute("xmlns:my",
    *                         "http://www.xmlsecurity.org/temp/mytempns");
    *
    *  String xpath =
    *     "//my:KeyMaterials[1]/descendant::node()[not(self::text())]";
    *  CachedXPathAPI xpathAPI = new CachedXPathAPI();
    *  NodeList nodes = xpathAPI.selectNodeList(doc, xpath, nscontext);
    *  Canonicalizer c14n =
    *     Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS);
    *  byte c14nBytes[] = c14n.canonicalizeXPathNodeSet(nodes);
    *  // InputStream refStream = resolver.resolveEntity(null, fileRef).getByteStream();
    *  // byte refBytes[] = JavaUtils.getBytesFromStream(refStream);
    *  byte refBytes[] = JavaUtils.getBytesFromFile(fileRef);
    *  boolean equal = JavaUtils.binaryCompare(refBytes, c14nBytes);
    *
    *  if (!equal) {
    *     JavaUtils.writeBytesToFilename(fileOut, c14nBytes);
    *     log.debug("Wrote malicious output from Test \"" + descri
    *               + "\" to file " + fileOut);
    *  }
    *
    *  assertTrue(equal);
    * }
    */

   /**
    * Note: This specification supports the recent XML plenary decision to
    * deprecate relative namespace URIs as follows: implementations of XML
    * canonicalization MUST report an operation failure on documents containing
    * relative namespace URIs. XML canonicalization MUST NOT be implemented
    * with an XML parser that converts relative URIs to absolute URIs.
    *
    * Implementations MUST report an operation failure on documents containing
    * relative namespace URIs.
    *
    * @throws CanonicalizationException
    * @throws FileNotFoundException
    * @throws IOException
    * @throws InvalidCanonicalizerException
    * @throws ParserConfigurationException
    * @throws SAXException
    * @throws TransformerException
    */
   public static void testRelativeNSbehaviour()
           throws IOException, FileNotFoundException, SAXException,
                  ParserConfigurationException, CanonicalizationException,
                  InvalidCanonicalizerException, TransformerException {

      //J-
      String inputStr = ""
         + "<absolute:correct      xmlns:absolute='http://www.absolute.org/#likeVodka'>"
         + "<relative:incorrect    xmlns:relative='../cheating#away'>"
         + "</relative:incorrect>"
         + "</absolute:correct>"
         + "\n"
         + "";
      //J+
      DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();

      dfactory.setNamespaceAware(true);

      DocumentBuilder db = dfactory.newDocumentBuilder();
      Document doc = db.parse(new ByteArrayInputStream(inputStr.getBytes()));
      boolean weCatchedTheRelativeNS = false;

      try {
         Canonicalizer c14n =
            Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS);
         c14n.canonicalizeSubtree(doc);

      } catch (CanonicalizationException cex) {

         // if we reach this point - good.
         log.debug("We catched the C14nEx, that's good: " + cex.getMessage());

         weCatchedTheRelativeNS = true;
      }

      assertTrue("We did not catch the relative namespace",
                 weCatchedTheRelativeNS);
   }

   /**
    * The XPath data model represents data using UCS characters.
    * Implementations MUST use XML processors that support UTF-8 and UTF-16
    * and translate to the UCS character domain. For UTF-16, the leading byte
    * order mark is treated as an artifact of encoding and stripped from the
    * UCS character data (subsequent zero width non-breaking spaces appearing
    * within the UTF-16 data are not removed) [UTF-16, Section 3.2]. Support
    * for ISO-8859-1 encoding is RECOMMENDED, and all other character encodings
    * are OPTIONAL.
    *
    * $todo$ implement the test
    * @throws CanonicalizationException
    * @throws FileNotFoundException
    * @throws IOException
    * @throws InvalidCanonicalizerException
    * @throws ParserConfigurationException
    * @throws SAXException
    * @throws TransformerException
    */
   public static void testTranslationFromUTF16toUTF8()
           throws IOException, FileNotFoundException, SAXException,
                  ParserConfigurationException, CanonicalizationException,
                  InvalidCanonicalizerException, TransformerException {

      String val =
         "<UTF16>The german &amp;auml (which is Unicode &amp;#xE4;):  &quot;&#xE4;&quot;</UTF16>";
      byte utf16[] = convertToUTF16(val.getBytes());
      Canonicalizer c14n =
         Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS);
      byte c14nBytes[] = c14n.canonicalize(utf16);
      org.xml.sax.EntityResolver resolver = new TestVectorResolver();
      InputStream refStream = resolver.resolveEntity(
         null,
            prefix + "/in/testTranslationFromUTF16toUTF8.xml")
               .getByteStream();
      byte refBytes[] = JavaUtils.getBytesFromStream(refStream);
      boolean equal = JavaUtils.binaryCompare(refBytes, c14nBytes);

      assertTrue("Parser does not translate to UCS character domain", equal);
   }

   /**
    * Method testXMLAttributes1
    *
    * @throws CanonicalizationException
    * @throws FileNotFoundException
    * @throws IOException
    * @throws InvalidCanonicalizerException
    * @throws ParserConfigurationException
    * @throws SAXException
    * @throws TransformerException
    */
   public static void testXMLAttributes1()
           throws IOException, FileNotFoundException, SAXException,
                  ParserConfigurationException, CanonicalizationException,
                  InvalidCanonicalizerException, TransformerException {
      //J-
      String input = ""
         + "<included    xml:lang='de'>"
         + "<notIncluded xml:lang='de'>"
         + "<notIncluded xml:lang='uk'>"
         + "<included                 >"
         + "</included>"
         + "</notIncluded>"
         + "</notIncluded>"
         + "</included>";

      String definedOutput = ""
         + "<included xml:lang=\"de\">"
         + "<included xml:lang=\"uk\">"
         + "</included>"
         + "</included>";
      //J+
      assertTrue(doTestXMLAttributes(input, definedOutput));
   }

   /**
    * Method testXMLAttributes2
    *
    * @throws CanonicalizationException
    * @throws FileNotFoundException
    * @throws IOException
    * @throws InvalidCanonicalizerException
    * @throws ParserConfigurationException
    * @throws SAXException
    * @throws TransformerException
    */
   public static void testXMLAttributes2()
           throws IOException, FileNotFoundException, SAXException,
                  ParserConfigurationException, CanonicalizationException,
                  InvalidCanonicalizerException, TransformerException {
      //J-
      String input = ""
         + "<included    xml:lang='uk'>"
         + "<notIncluded xml:lang='de'>"
         + "<notIncluded xml:lang='uk'>"
         + "<included                 >"
         + "</included>"
         + "</notIncluded>"
         + "</notIncluded>"
         + "</included>";

      String definedOutput = ""
         + "<included xml:lang=\"uk\">"
         + "<included xml:lang=\"uk\">"
         + "</included>"
         + "</included>";
      //J+
      assertTrue(doTestXMLAttributes(input, definedOutput));
   }

   /**
    * Method testXMLAttributes3
    *
    * @throws CanonicalizationException
    * @throws FileNotFoundException
    * @throws IOException
    * @throws InvalidCanonicalizerException
    * @throws ParserConfigurationException
    * @throws SAXException
    * @throws TransformerException
    */
   public static void testXMLAttributes3()
           throws IOException, FileNotFoundException, SAXException,
                  ParserConfigurationException, CanonicalizationException,
                  InvalidCanonicalizerException, TransformerException {
      //J-
      String input = ""
         + "<included    xml:lang='de'>"
         + "<notIncluded xml:lang='de'>"
         + "<notIncluded xml:lang='uk'>"
         + "<included    xml:lang='de'>"
         + "</included>"
         + "</notIncluded>"
         + "</notIncluded>"
         + "</included>";

      String definedOutput = ""
         + "<included xml:lang=\"de\">"
         + "<included xml:lang=\"de\">"
         + "</included>"
         + "</included>";
      //J+
      assertTrue(doTestXMLAttributes(input, definedOutput));
   }

   /**
    * Method testXMLAttributes4
    *
    * @throws CanonicalizationException
    * @throws FileNotFoundException
    * @throws IOException
    * @throws InvalidCanonicalizerException
    * @throws ParserConfigurationException
    * @throws SAXException
    * @throws TransformerException
    */
   public static void _testXMLAttributes4()
           throws IOException, FileNotFoundException, SAXException,
                  ParserConfigurationException, CanonicalizationException,
                  InvalidCanonicalizerException, TransformerException {
      //J-
      String input = ""
         + "<included    xml:lang='de'>"
         + "<included    xml:lang='de'>"
         + "<notIncluded xml:lang='uk'>"
         + "<included                 >"
         + "</included>"
         + "</notIncluded>"
         + "</included>"
         + "</included>";

      String definedOutput = ""
         + "<included xml:lang=\"de\">"
         + "<included>"
         + "<included xml:lang=\"uk\">"
         + "</included>"
         + "</included>"
         + "</included>";
      //J+
      assertTrue(doTestXMLAttributes(input, definedOutput));
   }

   /**
    * Method testXMLAttributes5
    *
    * @throws CanonicalizationException
    * @throws FileNotFoundException
    * @throws IOException
    * @throws InvalidCanonicalizerException
    * @throws ParserConfigurationException
    * @throws SAXException
    * @throws TransformerException
    */
   public static void _testXMLAttributes5()
           throws IOException, FileNotFoundException, SAXException,
                  ParserConfigurationException, CanonicalizationException,
                  InvalidCanonicalizerException, TransformerException {
      //J-
      String input = ""
         + "<included                         xml:lang='de'>"
         + "<included                         xml:lang='de'>"
         + "<notIncluded xml:space='preserve' xml:lang='uk'>"
         + "<included                 >"
         + "</included>"
         + "</notIncluded>"
         + "</included>"
         + "</included>";

      String definedOutput = ""
         + "<included xml:lang=\"de\">"
         + "<included>"
         + "<included xml:lang=\"uk\" xml:space=\"preserve\">"
         + "</included>"
         + "</included>"
         + "</included>";
      //J+
      assertTrue(doTestXMLAttributes(input, definedOutput));
   }

   /**
    * Method testXMLAttributes6
    *
    * @throws CanonicalizationException
    * @throws FileNotFoundException
    * @throws IOException
    * @throws InvalidCanonicalizerException
    * @throws ParserConfigurationException
    * @throws SAXException
    * @throws TransformerException
    */
   public static void _testXMLAttributes6()
           throws IOException, FileNotFoundException, SAXException,
                  ParserConfigurationException, CanonicalizationException,
                  InvalidCanonicalizerException, TransformerException {
      //J-
      String input = ""
         + "<included   xml:space='preserve'  xml:lang='de'>"
         + "<included                         xml:lang='de'>"
         + "<notIncluded                      xml:lang='uk'>"
         + "<included>"
         + "</included>"
         + "</notIncluded>"
         + "</included>"
         + "</included>";

      String definedOutput = ""
         + "<included xml:lang=\"de\" xml:space=\"preserve\">"
         + "<included>"
         + "<included xml:lang=\"uk\" xml:space=\"preserve\">"
         + "</included>"
         + "</included>"
         + "</included>";
      //J+
      assertTrue(doTestXMLAttributes(input, definedOutput));
   }

   /**
    * Method doTestXMLAttributes
    *
    * @param input
    * @param definedOutput
    * @param writeResultsToFile
    *
    * @throws CanonicalizationException
    * @throws FileNotFoundException
    * @throws IOException
    * @throws InvalidCanonicalizerException
    * @throws ParserConfigurationException
    * @throws SAXException
    * @throws TransformerException
    */
   private static boolean doTestXMLAttributes(
           String input, String definedOutput)
              throws IOException, FileNotFoundException, SAXException,
                     ParserConfigurationException, CanonicalizationException,
                     InvalidCanonicalizerException, TransformerException {

      DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();

      dfactory.setNamespaceAware(true);
      dfactory.setValidating(true);

      DocumentBuilder db = dfactory.newDocumentBuilder();

      db.setErrorHandler(new org.apache.xml.security.utils
         .IgnoreAllErrorHandler());

      Document doc = db.parse(new ByteArrayInputStream(input.getBytes()));
      Canonicalizer c14nizer =
         Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS);
      CachedXPathAPI xpathAPI = new CachedXPathAPI();

      //XMLUtils.circumventBug2650(doc);

      NodeList nodes =
         xpathAPI.selectNodeList(doc, "(//*[local-name()='included'] | //@*[parent::node()[local-name()='included']])");
      byte result[] = c14nizer.canonicalizeXPathNodeSet(nodes);
      byte defined[] = definedOutput.getBytes();

      return JavaUtils.binaryCompare(defined, result);
   }

   /**
    * Method c14nAndCompare
    *
    * @param fileIn
    * @param fileRef
    * @param fileOut
    * @param c14nURI
    * @param validating
    * @param xpath
    *
    * @throws CanonicalizationException
    * @throws FileNotFoundException
    * @throws IOException
    * @throws InvalidCanonicalizerException
    * @throws ParserConfigurationException
    * @throws SAXException
    * @throws TransformerException
    */
   private static boolean c14nAndCompare(
           String fileIn, String fileRef, String fileOut, String c14nURI, boolean validating, Object xpath)
              throws IOException, FileNotFoundException, SAXException,
                     ParserConfigurationException, CanonicalizationException,
                     InvalidCanonicalizerException, TransformerException {

      DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();

      dfactory.setNamespaceAware(true);
      dfactory.setValidating(validating);

      DocumentBuilder documentBuilder = dfactory.newDocumentBuilder();

      // throw away all warnings and errors
      documentBuilder.setErrorHandler(new IgnoreAllErrorHandler());

      // org.xml.sax.EntityResolver resolver = new TestVectorResolver();
      // documentBuilder.setEntityResolver(resolver);
      // Document doc = documentBuilder.parse(resolver.resolveEntity(null, fileIn));
            
      Document doc = documentBuilder.parse(fileIn);

      XMLUtils.circumventBug2650(doc);

      Canonicalizer c14n = Canonicalizer.getInstance(c14nURI);
      byte c14nBytes[] = null;

      if (xpath == null) {
         c14nBytes = c14n.canonicalizeSubtree(doc);
      } else {
         CachedXPathAPI xpathAPI = new CachedXPathAPI();
         NodeList nl = null;

         if (xpath instanceof String) {
            nl = xpathAPI.selectNodeList(doc, (String) xpath);
         } else {
            Element xpathElement = (Element) xpath;
            String xpathStr = ((Text) xpathElement.getFirstChild()).getData();

            nl = xpathAPI.selectNodeList(doc, xpathStr, xpathElement);
         }

         c14nBytes = c14n.canonicalizeXPathNodeSet(nl);
      }

      // org.xml.sax.InputSource refIs = resolver.resolveEntity(null, fileRef);
      // byte refBytes[] = JavaUtils.getBytesFromStream(refIs.getByteStream());
      byte refBytes[] = JavaUtils.getBytesFromFile(fileRef);

      // if everything is OK, result is true; we do a binary compare, byte by byte
      boolean result = JavaUtils.binaryCompare(refBytes, c14nBytes);

      if (result == false) {
         File f = new File(fileOut);
         if (!f.exists()) {
         	File parent = new File(f.getParent());
         	parent.mkdirs();
         	f.createNewFile();
         }
         FileOutputStream fos = new FileOutputStream(f);

         fos.write(c14nBytes);
         log.debug("Wrote errornous result to file " + f.toURL().toString());
      }

      return result;
   }

   /**
    * This method takes the input bytes as XML Document and converts it to an
    * UTF-16 encoded XML document which is serialized to byte[] and returned.
    *
    * @param input
    *
    * @throws IOException
    * @throws ParserConfigurationException
    * @throws SAXException
    * @throws TransformerConfigurationException
    * @throws TransformerException
    */
   public static byte[] convertToUTF16(byte input[])
           throws ParserConfigurationException, IOException, SAXException,
                  TransformerConfigurationException, TransformerException {

      //String ENCODING_ISO8859_1 = "ISO-8859-1";
      //String ENCODING_UTF8 = "UTF-8";
      String ENCODING_UTF16 = "UTF-16";
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document doc = db.parse(new ByteArrayInputStream(input));
      TransformerFactory tFactory = TransformerFactory.newInstance();
      Transformer transformer = tFactory.newTransformer();

      transformer.setOutputProperty(OutputKeys.ENCODING, ENCODING_UTF16);
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");

      DOMSource source = new DOMSource(doc);
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      StreamResult result = new StreamResult(os);

      transformer.transform(source, result);

      return os.toByteArray();
   }

   static {
      /*
      try {
         // String wd = new File(".").toURL().toExternalForm();
         // prefix = wd.substring(0, wd.length()-2) + "data/org/apache/xml/security/c14n/";
      } catch (java.net.MalformedURLException urlEx) {
         throw new RuntimeException(urlEx.getMessage());
      }
      */
   	  prefix = "data/org/apache/xml/security/c14n/";
   	  String basedir = System.getProperty("basedir");
   	  if(basedir != null && !"".equals(basedir)) {
   		prefix = basedir + "/" + prefix;
      }
      
      org.apache.xml.security.Init.init();
   }
}
