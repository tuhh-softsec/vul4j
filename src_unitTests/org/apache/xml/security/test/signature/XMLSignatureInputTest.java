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
package org.apache.xml.security.test.signature;



import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.xpath.CachedXPathAPI;
import javax.xml.transform.TransformerException;
import org.w3c.dom.*;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.utils.Constants;


/**
 * Unit test for {@link org.apache.xml.security.signature.XMLSignatureInput}
 *
 * @author Christian Geuer-Pollmann
 * @see <A HREF="http://nagoya.apache.org/bugzilla/show_bug.cgi?id=4336">Bug 4336</A>
 */
public class XMLSignatureInputTest extends TestCase {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category
         .getInstance(XMLSignatureInputTest.class.getName());

   /**
    * Method suite
    *
    * @return
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
         cat.debug("Type "
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

   /**
    * Method testSetOctetStreamGetNodeSet
    *
    * @throws CanonicalizationException
    * @throws IOException
    * @throws InvalidCanonicalizerException
    * @throws ParserConfigurationException
    * @throws SAXException
    * @throws UnsupportedEncodingException
    */
   public static void testSetOctetStreamGetNodeSet1()
           throws IOException, UnsupportedEncodingException,
                  ParserConfigurationException, SAXException,
                  CanonicalizationException, InvalidCanonicalizerException {

      InputStream inputStream =
         new ByteArrayInputStream(_nodeSetInput1.getBytes("UTF-8"));
      XMLSignatureInput input = new XMLSignatureInput(inputStream);
      Set nl = input.getNodeSet();

      assertEquals("_nodeSetInput1 Number of nodes",
                   _nodeSetInput1NodesWithComments, nl.size());
   }

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

   /**
    * Method testSetOctetStreamGetNodeSet2
    *
    * @throws CanonicalizationException
    * @throws IOException
    * @throws InvalidCanonicalizerException
    * @throws ParserConfigurationException
    * @throws SAXException
    * @throws UnsupportedEncodingException
    */
   public static void testSetOctetStreamGetNodeSet2()
           throws IOException, UnsupportedEncodingException,
                  ParserConfigurationException, SAXException,
                  CanonicalizationException, InvalidCanonicalizerException {

      InputStream inputStream =
         new ByteArrayInputStream(_nodeSetInput2.getBytes("UTF-8"));
      XMLSignatureInput input = new XMLSignatureInput(inputStream);
      Set nl = input.getNodeSet();

      assertEquals("_nodeSetInput2 Number of nodes",
                   _nodeSetInput2NodesWithComments, nl.size());
   }

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
    * Method testSetOctetStreamGetNodeSet3
    *
    * @throws CanonicalizationException
    * @throws IOException
    * @throws InvalidCanonicalizerException
    * @throws ParserConfigurationException
    * @throws SAXException
    * @throws UnsupportedEncodingException
    */
   public static void testSetOctetStreamGetNodeSet3()
           throws IOException, UnsupportedEncodingException,
                  ParserConfigurationException, SAXException,
                  CanonicalizationException, InvalidCanonicalizerException {

      InputStream inputStream =
         new ByteArrayInputStream(_nodeSetInput3.getBytes("UTF-8"));
      XMLSignatureInput input = new XMLSignatureInput(inputStream);
      Set nl = input.getNodeSet();

      assertEquals("_nodeSetInput3 Number of nodes",
                   _nodeSetInput3NodesWithComments, nl.size());
   }

   //J-
   static final String _nodeSetInput4 =
        "<!-- node set -->"
      + "<n>1</n>"
      + " "
      + "<n>2</n>"
      + "<n>3</n>"
      + "<n>4</n>";
   // added one for xmlns:xml since Xalan 2.2.D11
   static final int _nodeSetInput4Nodes = 10; // was 9
   static final int _nodeSetInput4NodesWithComments = _nodeSetInput4Nodes + 1;
   //J+

   /**
    * Method testSetOctetStreamGetNodeSet4
    *
    * @throws CanonicalizationException
    * @throws IOException
    * @throws InvalidCanonicalizerException
    * @throws ParserConfigurationException
    * @throws SAXException
    * @throws UnsupportedEncodingException
    */
   public static void testSetOctetStreamGetNodeSet4()
           throws IOException, UnsupportedEncodingException,
                  ParserConfigurationException, SAXException,
                  CanonicalizationException, InvalidCanonicalizerException {

      InputStream inputStream =
         new ByteArrayInputStream(_nodeSetInput4.getBytes("UTF-8"));
      XMLSignatureInput input = new XMLSignatureInput(inputStream);
      Set nl = input.getNodeSet();

      assertEquals("_nodeSetInput4 Number of nodes",
                   _nodeSetInput4NodesWithComments, nl.size());
   }

   /**
    * Method getNodeSet1
    *
    * @return
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

      Element root = doc.createElement("RootElement");
      Element e1 = doc.createElement("Element1");
      Element e2 = doc.createElement("Element2");
      Element e3 = doc.createElement("Element3");
      Text e3t = doc.createTextNode("Text in Element3");

      e3.appendChild(e3t);
      root.appendChild(e1);
      root.appendChild(e2);
      root.appendChild(e3);
      doc.appendChild(root);

      String s1 =
         "<!--Small Comment Test--><RootElement><Element1/><Element2/><Element3>Text in Element3</Element3></RootElement>";
      CachedXPathAPI cXPathAPI = new CachedXPathAPI();
      NodeList nl = cXPathAPI.selectNodeList(doc,
                                             "(//. | //@* | //namespace::*)");

      return XMLUtils.convertNodelistToSet(nl);
   }

   /**
    * Method testSetNodeSetGetNodeSet1
    *
    * @throws CanonicalizationException
    * @throws IOException
    * @throws InvalidCanonicalizerException
    * @throws ParserConfigurationException
    * @throws SAXException
    * @throws TransformerException
    * @throws UnsupportedEncodingException
    */
   public static void testSetNodeSetGetNodeSet1()
           throws IOException, UnsupportedEncodingException,
                  ParserConfigurationException, SAXException,
                  CanonicalizationException, InvalidCanonicalizerException,
                  TransformerException {

      XMLSignatureInput input = new XMLSignatureInput(getNodeSet1(), null);
      Set nl = input.getNodeSet();

      assertEquals("getNodeSet1 Number of nodes", 8, nl.size());    // 8 was 7
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
