
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
package org.apache.xml.security.samples.transforms;



import java.io.*;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.c14n.helper.XPathContainer;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.*;
import org.apache.xml.security.utils.*;


/**
 * This class demonstrates the use of a Transform for XSLT. The
 * <CODE>xsl:stylesheet</CODE> is directly embedded in the <CODE>ds:Transform</CODE>,
 * so the {@link Transform} object is created by using the Element.
 *
 * @author Christian Geuer-Pollmann
 * @version %I%, %G%
 */
public class SampleTransformXSLT {

   /**
    * Method main
    *
    * @param args
    * @throws Exception
    */
   public static void main(String args[]) throws Exception {
      org.apache.xml.security.Init.init();

      //J-
      String transformStr =
        "<?xml version=\"1.0\"?>\n"
      + "<ds:Transforms xmlns:ds='http://www.w3.org/2000/09/xmldsig#'>\n"
      + "<ds:Transform Algorithm='http://www.w3.org/TR/1999/REC-xslt-19991116'>\n"
      + "<xsl:stylesheet  version=\"1.0\"\n"
      + "                 xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>\n"
      + "<xsl:output method=\"xml\" indent=\"yes\"/>\n"

      + "<xsl:template match=\"Class\">\n"
      + "<BirdInfo>\n"
      + "	<xsl:apply-templates select=\"Order\"/>\n"
      + "</BirdInfo>\n"
      + "</xsl:template>\n"

      + "<xsl:template match=\"Order\">\n"
      + "Order is:  <xsl:value-of select=\"@Name\"/>\n"
      + "	<xsl:apply-templates select=\"Family\"/><xsl:text>\n"
      + "</xsl:text>\n"
      + "</xsl:template>\n"

      + "<xsl:template match=\"Family\">\n"
      + "	Family is:  <xsl:value-of select=\"@Name\"/>\n"
      + "	<xsl:apply-templates select=\"Species | SubFamily | text()\"/>\n"
      + "</xsl:template>\n"
      + "<xsl:template match=\"SubFamily\">\n"
      + "		SubFamily is <xsl:value-of select=\"@Name\"/>\n"
      + "    <xsl:apply-templates select=\"Species | text()\"/>\n"
      + "</xsl:template>\n"

      + "<xsl:template match=\"Species\">\n"
      + "	<xsl:choose>\n"
      + "	  <xsl:when test=\"name(..)='SubFamily'\">\n"
      + "		<xsl:text>	</xsl:text><xsl:value-of select=\".\"/><xsl:text> </xsl:text><xsl:value-of select=\"@Scientific_Name\"/>\n"
      + "	  </xsl:when>\n"
      + "	  <xsl:otherwise>\n"
      + "		<xsl:value-of select=\".\"/><xsl:text> </xsl:text><xsl:value-of select=\"@Scientific_Name\"/>\n"
      + "	  </xsl:otherwise>\n"
      + "	</xsl:choose>\n"
      + "</xsl:template>\n"

      + "</xsl:stylesheet>\n"
      + "</ds:Transform>\n"
      + "</ds:Transforms>\n"
      ;

      String inputStr =
        "<?xml version=\"1.0\"?>\n"
      + "<Class>\n"
      + "<Order Name=\"TINAMIFORMES\">\n"
      + "        <Family Name=\"TINAMIDAE\">\n"
      + "            <Species Scientific_Name=\"Tinamus major\">  Great Tinamou.</Species>\n"
      + "            <Species Scientific_Name=\"Nothocercus\">Highland Tinamou.</Species>\n"
      + "            <Species Scientific_Name=\"Crypturellus soui\">Little Tinamou.</Species>\n"
      + "            <Species Scientific_Name=\"Crypturellus cinnamomeus\">Thicket Tinamou.</Species>\n"
      + "            <Species Scientific_Name=\"Crypturellus boucardi\">Slaty-breasted Tinamou.</Species>\n"
      + "            <Species Scientific_Name=\"Crypturellus kerriae\">Choco Tinamou.</Species>\n"
      + "        </Family>\n"
      + "    </Order>\n"
      + "    <Order Name=\"GAVIIFORMES\">\n"
      + "        <Family Name=\"GAVIIDAE\">\n"
      + "            <Species Scientific_Name=\"Gavia stellata\">Red-throated Loon.</Species>\n"
      + "            <Species Scientific_Name=\"Gavia arctica\">Arctic Loon.</Species>\n"
      + "            <Species Scientific_Name=\"Gavia pacifica\">Pacific Loon.</Species>\n"
      + "            <Species Scientific_Name=\"Gavia immer\">Common Loon.</Species>\n"
      + "            <Species Scientific_Name=\"Gavia adamsii\">Yellow-billed Loon.</Species>\n"
      + "        </Family>\n"
      + "    </Order>\n"
      + "    <Order Name=\"PODICIPEDIFORMES\">\n"
      + "        <Family Name=\"PODICIPEDIDAE\">\n"
      + "            <Species Scientific_Name=\"Tachybaptus dominicus\">Least Grebe.</Species>\n"
      + "            <Species Scientific_Name=\"Podilymbus podiceps\">Pied-billed Grebe.</Species>\n"
      + "            <Species Scientific_Name=\"\">Atitlan Grebe.</Species>\n"
      + "            <Species Scientific_Name=\"\">Horned Grebe.</Species>\n"
      + "            <Species Scientific_Name=\"\">Red-necked Grebe.</Species>\n"
      + "            <Species Scientific_Name=\"\">Eared Grebe.</Species>\n"
      + "            <Species Scientific_Name=\"\">Western Grebe.</Species>\n"
      + "            <Species Scientific_Name=\"\">Clark's Grebe.</Species>\n"
      + "        </Family>\n"
      + "    </Order>\n"
      + "</Class>\n"
      ;
      //J+
      javax.xml.parsers.DocumentBuilderFactory dbf =
         javax.xml.parsers.DocumentBuilderFactory.newInstance();

      dbf.setNamespaceAware(true);

      javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
      org.w3c.dom.Document doc =
         db.parse(new java.io.ByteArrayInputStream(transformStr.getBytes()));

      Transforms t = new Transforms(doc.getDocumentElement(), "memory://");
      XMLSignatureInput result =
         t.performTransforms(new XMLSignatureInput(inputStr.getBytes()));

      System.out.println(new String(result.getBytes()));
   }
}
