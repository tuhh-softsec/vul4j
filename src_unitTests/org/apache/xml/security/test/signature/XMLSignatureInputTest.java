
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
package org.apache.xml.security.test.signature;



import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xpath.CachedXPathAPI;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;


/**
 * Unit test for {@link org.apache.xml.security.signature.XMLSignatureInput}
 *
 * @author Christian Geuer-Pollmann
 * @see <A HREF="http://nagoya.apache.org/bugzilla/show_bug.cgi?id=4336">Bug 4336</A>
 */
public class XMLSignatureInputTest extends TestCase {

   /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(XMLSignatureInputTest.class.getName());

   /**
    * Method suite
    *
    *
    */
   public static Test suite() {
      return new TestSuite(XMLSignatureInputTest.class);
   }

   //J-
   static final String _octetStreamTextInput = "Kleiner Test";
   //J+

   /**
    * Constructor XMLSignatureInputTest
    *
    *
    * @param Name_
    *
    */
   public XMLSignatureInputTest(String Name_) {
      super(Name_);
   }

   /**
    * Method main
    *
    *
    * @param args
    *
    */
   public static void main(String[] args) {

      String[] testCaseName = { "-noloading",
                                XMLSignatureInputTest.class.getName() };

      junit.textui.TestRunner.main(testCaseName);
   }

   /**
    * Method printNodeSet
    *
    * @param nl
    */
   private void printNodeSet(NodeList nl) {

      for (int i = 0; i < nl.getLength(); i++) {
         log.debug("Type "
                   + XMLUtils.getNodeTypeString(nl.item(i).getNodeType()));
      }
   }

   /**
    * Method testSetOctetStreamGetOctetStream
    *
    * @throws CanonicalizationException
    * @throws IOException
    * @throws InvalidCanonicalizerException
    */
   public static void testSetOctetStreamGetOctetStream()
           throws IOException, CanonicalizationException,
                  InvalidCanonicalizerException {

      InputStream inputStream =
         new ByteArrayInputStream(_octetStreamTextInput.getBytes("UTF-8"));
      XMLSignatureInput input = new XMLSignatureInput(inputStream);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      InputStream res = input.getOctetStream();
      int off = 0;

      while (res.available() > 0) {
         byte array[] = new byte[1024];
         int len = res.read(array);

         baos.write(array, off, len);

         off += len;
      }

      byte resBytes[] = baos.toByteArray();
      String resString = new String(resBytes, "UTF-8");

      assertTrue(resString.equals(_octetStreamTextInput));
   }

   //J-
   static final String _nodeSetInput1 =
        "<?xml version=\"1.0\"?>\n"
      + "<!DOCTYPE doc [\n"
      + "<!ELEMENT doc (n+)>\n"
      + "<!ELEMENT n (#PCDATA)>\n"
      + "]>\n"
      + "<!-- full document with decl -->"
      + "<doc>"
      + "<n>1</n>"
      + "<n>2</n>"
      + "<n>3</n>"
      + "<n>4</n>"
      + "</doc>";
   // added one for xmlns:xml since Xalan 2.2.D11
   static final int _nodeSetInput1Nodes = 11; // was 10
   static final int _nodeSetInput1NodesWithComments = _nodeSetInput1Nodes + 1;
   //J+
   //J-
   static final String _nodeSetInput2 =
        "<?xml version=\"1.0\"?>\n"
      + "<!-- full document -->"
      + "<doc>"
      + "<n>1</n>"
      + "<n>2</n>"
      + "<n>3</n>"
      + "<n>4</n>"
      + "</doc>";
   // added one for xmlns:xml since Xalan 2.2.D11
   static final int _nodeSetInput2Nodes = 11; // was 10
   static final int _nodeSetInput2NodesWithComments = _nodeSetInput2Nodes + 1;
   //J+
   //J-
   static final String _nodeSetInput3 =
        "<!-- document -->"
      + "<doc>"
      + "<n>1</n>"
      + "<n>2</n>"
      + "<n>3</n>"
      + "<n>4</n>"
      + "</doc>";
   // added one for xmlns:xml since Xalan 2.2.D11
   static final int _nodeSetInput3Nodes = 11; // was 10
   static final int _nodeSetInput3NodesWithComments = _nodeSetInput3Nodes + 1;
   //J+

   /**
    * Method getNodeSet1
    *
    *
    * @throws ParserConfigurationException
    * @throws TransformerException
    */
   private static Set getNodeSet1()
           throws ParserConfigurationException, TransformerException {

      // This should build
      DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();

      dfactory.setValidating(false);
      dfactory.setNamespaceAware(true);

      DocumentBuilder db = dfactory.newDocumentBuilder();
      Document doc = db.newDocument();
      Comment c1 = doc.createComment("Small Comment Test");

      doc.appendChild(c1);

      Element root = doc.createElementNS(null, "RootElement");
      Element e1 = doc.createElementNS(null, "Element1");
      Element e2 = doc.createElementNS(null, "Element2");
      Element e3 = doc.createElementNS(null, "Element3");
      Text e3t = doc.createTextNode("Text in Element3");

      e3.appendChild(e3t);
      root.appendChild(e1);
      root.appendChild(e2);
      root.appendChild(e3);
      doc.appendChild(root);

      String s1 =
         "<!--Small Comment Test--><RootElement><Element1/><Element2/><Element3>Text in Element3</Element3></RootElement>";

      //XMLUtils.circumventBug2650(doc);

      CachedXPathAPI cXPathAPI = new CachedXPathAPI();
      NodeList nl = cXPathAPI.selectNodeList(doc,
                                             "(//. | //@* | //namespace::*)");

      return XMLUtils.convertNodelistToSet(nl);
   }

   /**
    * Method testSetNodeSetGetOctetStream1
    *
    * @throws CanonicalizationException
    * @throws IOException
    * @throws InvalidCanonicalizerException
    * @throws ParserConfigurationException
    * @throws SAXException
    * @throws TransformerException
    * @throws UnsupportedEncodingException
    */
   public static void testSetNodeSetGetOctetStream1()
           throws IOException, UnsupportedEncodingException,
                  ParserConfigurationException, SAXException,
                  CanonicalizationException, InvalidCanonicalizerException,
                  TransformerException {

      XMLSignatureInput input = new XMLSignatureInput(getNodeSet1(), null);
      String definedWithoutComments =
         "<RootElement><Element1></Element1><Element2></Element2><Element3>Text in Element3</Element3></RootElement>";

      {

         // input.setCanonicalizerURI(Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS);
         String resultWithoutComments = new String(input.getBytes(), "UTF-8");

         /* FileOutputStream fos = new FileOutputStream ("xResult.xml");
            fos.write(resultWithoutComments.getBytes()); */
         assertTrue("testSetNodeSetGetOctetStream(false)",
                    resultWithoutComments.equals(definedWithoutComments));
      }
   }

   /**
    * Method testIsInitialized
    *
    * @throws IOException
    */
   public static void testIsInitializedWithOctetStream() throws IOException {

      InputStream inputStream =
         new ByteArrayInputStream(_octetStreamTextInput.getBytes());
      XMLSignatureInput input = new XMLSignatureInput(inputStream);

      assertTrue("Input is initialized", input.isInitialized());
   }

   /**
    * Method testOctetStreamIsOctetStream
    *
    * @throws IOException
    */
   public static void testOctetStreamIsOctetStream() throws IOException {

      InputStream inputStream =
         new ByteArrayInputStream(_octetStreamTextInput.getBytes());
      XMLSignatureInput input = new XMLSignatureInput(inputStream);

      assertTrue("Input is octet stream", input.isOctetStream());
   }

   /**
    * Method testOctetStreamIsNotNodeSet
    *
    * @throws IOException
    */
   public static void testOctetStreamIsNotNodeSet() throws IOException {

      InputStream inputStream =
         new ByteArrayInputStream(_octetStreamTextInput.getBytes());
      XMLSignatureInput input = new XMLSignatureInput(inputStream);

      assertTrue("Input is not node set", !input.isNodeSet());
   }

   /**
    * Method testIsInitializedWithNodeSet
    *
    * @throws CanonicalizationException
    * @throws IOException
    * @throws ParserConfigurationException
    * @throws SAXException
    * @throws TransformerException
    * @throws UnsupportedEncodingException
    */
   public static void testIsInitializedWithNodeSet()
           throws IOException, UnsupportedEncodingException,
                  ParserConfigurationException, SAXException,
                  CanonicalizationException, TransformerException {

      XMLSignatureInput input = new XMLSignatureInput(getNodeSet1(), null);

      assertTrue("Input is initialized", input.isInitialized());
   }

   /**
    * Method testNodeSetIsNotOctetStream
    *
    * @throws CanonicalizationException
    * @throws IOException
    * @throws ParserConfigurationException
    * @throws SAXException
    * @throws TransformerException
    * @throws UnsupportedEncodingException
    */
   public static void testNodeSetIsNotOctetStream()
           throws IOException, UnsupportedEncodingException,
                  ParserConfigurationException, SAXException,
                  CanonicalizationException, TransformerException {

      XMLSignatureInput input = new XMLSignatureInput(getNodeSet1(), null);

      assertTrue("Input is not octet stream", !input.isOctetStream());
   }

   /**
    * Method testNodeSetIsNodeSet
    *
    * @throws CanonicalizationException
    * @throws IOException
    * @throws ParserConfigurationException
    * @throws SAXException
    * @throws TransformerException
    * @throws UnsupportedEncodingException
    */
   public static void testNodeSetIsNodeSet()
           throws IOException, UnsupportedEncodingException,
                  ParserConfigurationException, SAXException,
                  CanonicalizationException, TransformerException {

      XMLSignatureInput input = new XMLSignatureInput(getNodeSet1(), null);

      assertTrue("Input is node set", input.isNodeSet());
   }

   static {
      org.apache.xml.security.Init.init();
   }
}
