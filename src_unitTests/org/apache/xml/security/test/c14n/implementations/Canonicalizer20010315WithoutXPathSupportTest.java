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
package org.apache.xml.security.test.c14n.implementations;



import java.io.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.xml.sax.SAXException;
import org.w3c.dom.*;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.utils.IgnoreAllErrorHandler;
import org.apache.xml.security.utils.JavaUtils;
import org.apache.xml.security.test.resource.TestVectorResolver;


/**
 * Unit test for {@link org.apache.xml.security.c14n.implementations.Canonicalizer20010315}
 *
 * @author Christian Geuer-Pollmann
 */
public class Canonicalizer20010315WithoutXPathSupportTest extends TestCase {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category
         .getInstance(Canonicalizer20010315WithoutXPathSupportTest.class
            .getName());

   /**
    * Method suite
    *
    * @return
    */
   public static Test suite() {
      return new TestSuite(Canonicalizer20010315WithoutXPathSupportTest.class);
   }

   /**
    * Constructor Canonicalizer20010315Test
    *
    * @param Name_
    */
   public Canonicalizer20010315WithoutXPathSupportTest(String Name_) {
      super(Name_);
   }

   /**
    * Method main
    *
    * @param args
    */
   public static void main(String[] args) {

      String[] testCaseName = { "-noloading",
                                Canonicalizer20010315WithoutXPathSupportTest.class
                                   .getName() };

      junit.textui.TestRunner.main(testCaseName);
   }

   /** Field prefix */
   static String prefix = "data/org/apache/xml/security/c14n/";

   /**
    * 3.1 PIs, Comments, and Outside of Document Element
    *
    * @throws CanonicalizationException
    * @throws FileNotFoundException
    * @throws IOException
    * @throws InvalidCanonicalizerException
    * @throws ParserConfigurationException
    * @throws SAXException
    */
   public static void test31withComments()
           throws IOException, FileNotFoundException, SAXException,
                  ParserConfigurationException, CanonicalizationException,
                  InvalidCanonicalizerException {

      String descri =
         "3.1: PIs, Comments, and Outside of Document Element. (commented)";
      String fileIn = prefix + "in/31_input.xml";
      String fileRef = prefix + "in/31_c14n-comments.xml";
      String fileOut = prefix + "out/31_output-comments_noXPath.xml";
      String c14nURI =
         "http://www.xmlsecurity.org/canonicalizerWithoutXPath#withComments";
      boolean validating = true;

      assertTrue(descri,
                 c14nAndCompare(fileIn, fileRef, fileOut, c14nURI, validating));
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
    */
   public static void test31()
           throws IOException, FileNotFoundException, SAXException,
                  ParserConfigurationException, CanonicalizationException,
                  InvalidCanonicalizerException {

      String descri =
         "3.1: PIs, Comments, and Outside of Document Element. (uncommented)";
      String fileIn = prefix + "in/31_input.xml";
      String fileRef = prefix + "in/31_c14n.xml";
      String fileOut = prefix + "out/31_output_noXPath.xml";
      String c14nURI = "http://www.xmlsecurity.org/canonicalizerWithoutXPath";
      boolean validating = true;

      assertTrue(descri,
                 c14nAndCompare(fileIn, fileRef, fileOut, c14nURI, validating));
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
    */
   public static void test32()
           throws IOException, FileNotFoundException, SAXException,
                  ParserConfigurationException, CanonicalizationException,
                  InvalidCanonicalizerException {

      String descri = "3.2 Whitespace in Document Content. (uncommented)";
      String fileIn = prefix + "in/32_input.xml";
      String fileRef = prefix + "in/32_c14n.xml";
      String fileOut = prefix + "out/32_output_noXPath.xml";
      String c14nURI = "http://www.xmlsecurity.org/canonicalizerWithoutXPath";
      boolean validating = true;

      assertTrue(descri,
                 c14nAndCompare(fileIn, fileRef, fileOut, c14nURI, validating));
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
    */
   public static void test33()
           throws IOException, FileNotFoundException, SAXException,
                  ParserConfigurationException, CanonicalizationException,
                  InvalidCanonicalizerException {

      String descri = "3.3 Start and End Tags. (uncommented)";
      String fileIn = prefix + "in/33_input.xml";
      String fileRef = prefix + "in/33_c14n.xml";
      String fileOut = prefix + "out/33_output_noXPath.xml";
      String c14nURI = "http://www.xmlsecurity.org/canonicalizerWithoutXPath";
      boolean validating = true;

      assertTrue(descri,
                 c14nAndCompare(fileIn, fileRef, fileOut, c14nURI, validating));
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
    * @todo Check what we have to do to get this f*cking test working!!!
    */
   public static void _test34()
           throws IOException, FileNotFoundException, SAXException,
                  ParserConfigurationException, CanonicalizationException,
                  InvalidCanonicalizerException {

      String descri =
         "3.4 Character Modifications and Character References. (uncommented)";
      String fileIn = prefix + "in/34_input.xml";
      String fileRef = prefix + "in/34_c14n.xml";
      String fileOut = prefix + "out/34_output_noXPath.xml";
      String c14nURI = "http://www.xmlsecurity.org/canonicalizerWithoutXPath";
      boolean validating = false;

      assertTrue(descri,
                 c14nAndCompare(fileIn, fileRef, fileOut, c14nURI, validating));
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
    */
   public static void test34validatingParser()
           throws IOException, FileNotFoundException, SAXException,
                  ParserConfigurationException, CanonicalizationException,
                  InvalidCanonicalizerException {

      String descri =
         "3.4 Character Modifications and Character References. (uncommented, patched to run on validating Parsers)";
      String fileIn = prefix + "in/34_input_validatingParser.xml";
      String fileRef = prefix + "in/34_c14n_validatingParser.xml";
      String fileOut = prefix + "out/34_output_validatingParser_noXPath.xml";
      String c14nURI = "http://www.xmlsecurity.org/canonicalizerWithoutXPath";
      boolean validating = true;

      assertTrue(descri,
                 c14nAndCompare(fileIn, fileRef, fileOut, c14nURI, validating));
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
    */
   public static void test35()
           throws IOException, FileNotFoundException, SAXException,
                  ParserConfigurationException, CanonicalizationException,
                  InvalidCanonicalizerException {

      String descri = "3.5 Entity References. (uncommented)";
      String fileIn = prefix + "in/35_input.xml";
      String fileRef = prefix + "in/35_c14n.xml";
      String fileOut = prefix + "out/35_output_noXPath.xml";
      String c14nURI = "http://www.xmlsecurity.org/canonicalizerWithoutXPath";
      boolean validating = true;

      assertTrue(descri,
                 c14nAndCompare(fileIn, fileRef, fileOut, c14nURI, validating));
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
    */
   public static void test36()
           throws IOException, FileNotFoundException, SAXException,
                  ParserConfigurationException, CanonicalizationException,
                  InvalidCanonicalizerException {

      String descri = "3.6 UTF-8 Encoding. (uncommented)";
      String fileIn = prefix + "in/36_input.xml";
      String fileRef = prefix + "in/36_c14n.xml";
      String fileOut = prefix + "out/36_output_noXPath.xml";
      String c14nURI = "http://www.xmlsecurity.org/canonicalizerWithoutXPath";
      boolean validating = true;

      assertTrue(descri,
                 c14nAndCompare(fileIn, fileRef, fileOut, c14nURI, validating));
   }

   /**
    * Method c14nAndCompare
    *
    * @param fileIn
    * @param fileRef
    * @param fileOut
    * @param c14nURI
    * @param validating
    * @return
    * @throws CanonicalizationException
    * @throws FileNotFoundException
    * @throws IOException
    * @throws InvalidCanonicalizerException
    * @throws ParserConfigurationException
    * @throws SAXException
    */
   private static boolean c14nAndCompare(
           String fileIn, String fileRef, String fileOut, String c14nURI, boolean validating)
              throws IOException, FileNotFoundException, SAXException,
                     ParserConfigurationException, CanonicalizationException,
                     InvalidCanonicalizerException {

      DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();

      dfactory.setNamespaceAware(true);
      dfactory.setValidating(validating);

      DocumentBuilder documentBuilder = dfactory.newDocumentBuilder();

      // throw away all warnings and errors
      documentBuilder.setErrorHandler(new IgnoreAllErrorHandler());

      // org.xml.sax.EntityResolver resolver = new TestVectorResolver();
      // documentBuilder.setEntityResolver(resolver);

      /*      Document doc =
               documentBuilder
                  .parse( resolver.resolveEntity(null, fileIn  ));*/
      Document doc = documentBuilder.parse(fileIn);
      Canonicalizer c14n = Canonicalizer.getInstance(c14nURI);
      byte c14nBytes[] = c14n.canonicalize(doc);

      // org.xml.sax.InputSource refIs = resolver.resolveEntity(null, fileRef);
      // byte refBytes[] = JavaUtils.getBytesFromStream(refIs.getByteStream());
      byte refBytes[] = JavaUtils.getBytesFromFile(fileRef);

      // if everything is OK, result is true; we do a binary compare, byte by byte
      boolean result = JavaUtils.binaryCompare(refBytes, c14nBytes);

      if (result == false) {
         FileOutputStream fos = new FileOutputStream(fileOut);

         fos.write(c14nBytes);
      }

      return result;
   }

   static {
      org.apache.xml.security.Init.init();
   }
}
