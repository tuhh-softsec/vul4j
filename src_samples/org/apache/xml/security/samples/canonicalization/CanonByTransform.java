
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
package org.apache.xml.security.samples.canonicalization;



import java.io.ByteArrayInputStream;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.*;
import org.apache.xml.security.utils.*;


/**
 *
 *
 * @author Christian Geuer-Pollmann
 *
 */
public class CanonByTransform {
   //J-
   static String input = ""
      + "<!DOCTYPE doc [<!ATTLIST e9 attr CDATA \"default\">]>\n"
      + "<!-- Comment 2 --><doc><!-- comment inside -->\n"
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
      XMLSignatureInput signatureInput = new XMLSignatureInput((Node) doc);
      Element referenceElem = doc.createElement("Reference");
      Transforms c14nTrans = new Transforms(referenceElem, "memory://");
      c14nTrans.addTransform("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments");
      XMLSignatureInput c14nResult = c14nTrans.performTransforms(signatureInput);
      byte outputBytes[] = c14nResult.getBytes();

      System.out.println(new String(outputBytes));
   }
}
