
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
package org.apache.xml.security.samples.transforms;



import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.Transforms;


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
