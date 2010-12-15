
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
package org.apache.xml.security.samples.canonicalization;



import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xml.security.c14n.Canonicalizer;
import org.w3c.dom.Document;


/**
 *
 *
 * @author Christian Geuer-Pollmann
 */
public class CanonDirect {
   //J-
   static String input = ""
      + "<!DOCTYPE doc [<!ATTLIST e9 attr CDATA \"default\">]>\n"
      + "<!-- Comment 2 --><doc>\n"
      + "   <e1   />\n"
      + "   <e2   ></e2>\n"
      + "   <e3    name = \"elem3\"   id=\"elem3\"    />\n"
      + "   <e4    name=\"elem4\"   id=\"elem4\"    ></e4>\n"
      + "   <e5 a:attr=\"out\" b:attr=\"sorted\" attr2=\"all\" attr=\"I'm\"\n"
      + "       xmlns:b=\"http://www.ietf.org\"\n"
      + "       xmlns:a=\"http://www.w3.org\"\n"
      + "       xmlns=\"http://example.org\"/>\n"
      + "   <e6 xmlns=\"\" xmlns:a=\"http://www.w3.org\">\n"
      + "       <e7 xmlns=\"http://www.ietf.org\">\n"
      + "           <e8 xmlns=\"\" xmlns:a=\"http://www.w3.org\">\n"
      + "               <e9 xmlns=\"\" xmlns:a=\"http://www.ietf.org\"/>\n"
      + "               <text>&#169;</text>\n"
      + "           </e8>\n"
      + "       </e7>\n"
      + "   </e6>\n"
      + "</doc><!-- Comment 3 -->\n"
      ;
   //J+

   /**
    * Method main
    *
    * @param args
    * @throws Exception
    */
   public static void main(String args[]) throws Exception {
      org.apache.xml.security.Init.init();


      DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();

      dfactory.setNamespaceAware(true);
      dfactory.setValidating(true);

      DocumentBuilder documentBuilder = dfactory.newDocumentBuilder();

      // this is to throw away all validation warnings
      documentBuilder
         .setErrorHandler(new org.apache.xml.security.utils
            .IgnoreAllErrorHandler());

      byte inputBytes[] = input.getBytes();
      Document doc =
         documentBuilder.parse(new ByteArrayInputStream(inputBytes));

      // after playing around, we have our document now
      Canonicalizer c14n = Canonicalizer.getInstance(
         "http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments");
      byte outputBytes[] = c14n.canonicalizeSubtree(doc);

      System.out.println(new String(outputBytes));
   }
}
