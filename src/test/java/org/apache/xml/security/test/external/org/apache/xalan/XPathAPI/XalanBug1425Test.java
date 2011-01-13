
/*
 * Copyright  1999-2009 The Apache Software Foundation.
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
package org.apache.xml.security.test.external.org.apache.xalan.XPathAPI;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;


/**
 * Testcase for testing the 
 * <A HREF="http://nagoya.apache.org/bugzilla/show_bug.cgi?id=1425">Xalan Bug 1425</A>.
 *
 * This fails with Xalan v2.1.0 and works with Xalan v2.2D6.
 */
public class XalanBug1425Test extends org.junit.Assert {

    /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(XalanBug1425Test.class.getName());

    static {
        org.apache.xml.security.Init.init();
    }

    /**
     * Method testBad01
     *
     * @throws Exception
     */
    @org.junit.Test
    public void testBad01() throws Exception {

        String xml = "<doc><a /> </doc><!-- -->";
        String desc = " # mixed content and following comment";
        String xpath = "(//.)";

        assertTrue("Bad " + xml + desc + "  " , containsDocumentElement(xml, xpath));
    }

    /**
     * Method testBad02
     *
     * @throws Exception
     */
    @org.junit.Test
    public void testBad02() throws Exception {

        String xml = "<doc><a /> </doc><?pi?>";
        String desc = " # mixed content and following PI";
        String xpath = "(//.)";

        assertTrue("Bad " + xml + desc + "  " , containsDocumentElement(xml, xpath));
    }

    /**
     * Method testBad03
     *
     * @throws Exception
     */
    @org.junit.Test
    public void testBad03() throws Exception {

        String xml = "<doc><a /><b /></doc><!-- -->";
        String desc = " # mixed content and following comment";
        String xpath = "(//.)";

        assertTrue("Bad " + xml + desc + "  " , containsDocumentElement(xml, xpath));
    }

    /**
     * Method testGood01
     *
     * @throws Exception
     */
    @org.junit.Test
    public void testGood01() throws Exception {

        String xml = "<doc><a /></doc><!-- -->";
        String desc = " # 'clean' content and following comment";
        String xpath = "(//.)";

        assertTrue("Good " + xml + desc, containsDocumentElement(xml, xpath));
    }

    /**
     * Method testGood02
     *
     * @throws Exception
     */
    @org.junit.Test
    public void testGood02() throws Exception {

        String xml = "<doc><a /> </doc>";
        String desc = " # mixed content and nothing follows";
        String xpath = "(//.)";

        assertTrue("Good " + xml + desc, containsDocumentElement(xml, xpath));
    }

    /**
     * Method testGood03
     *
     * @throws Exception
     */
    @org.junit.Test
    public void testGood03() throws Exception {

        String xml = "<!-- --><doc><a /> </doc>";
        String desc = " # mixed content and preceding comment";
        String xpath = "(//.)";

        assertTrue("Good " + xml + desc, containsDocumentElement(xml, xpath));
    }

    /**
     * Method testGood04
     *
     * @throws Exception
     */
    @org.junit.Test
    public void testGood04() throws Exception {

        String xml = "<?pi?><doc><a /> </doc>";
        String desc = " # mixed content and preceding PI";
        String xpath = "(//.)";

        assertTrue("Good " + xml + desc, containsDocumentElement(xml, xpath));
    }

    /**
     * Method testGood05
     *
     * @throws Exception
     */
    @org.junit.Test
    public void testGood05() throws Exception {

        String xml = "<doc><a /><b /></doc>";
        String desc = " # mixed ElemContent";
        String xpath = "(//.)";

        assertTrue("Good " + xml + desc, containsDocumentElement(xml, xpath));
    }
    
    /**
     * Process input args and execute the XPath.
     *
     * @param xmlString
     * @param xpath
     *
     * @throws Exception
     */
    private static boolean containsDocumentElement(String xmlString, String xpath) 
        throws Exception {
        DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dfactory.newDocumentBuilder();
        Document doc = db.parse(new ByteArrayInputStream(xmlString.getBytes()));

        // Set up an identity transformer to use as serializer.
        Transformer serializer =
            TransformerFactory.newInstance().newTransformer();

        serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

        // Use the simple XPath API to select a nodeIterator.
        // System.out.println("Querying DOM using " + xpath);
        NodeIterator nl = XPathAPI.selectNodeIterator(doc, xpath);

        // Serialize the found nodes to System.out.
        // System.out.println("<output>");
        Node n;

        while ((n = nl.nextNode()) != null) {
            // System.out.println("<node" + ++i + " nodeType=\"" + nodeTypeString[n.getNodeType()] + "\">");
            // serializer.transform(new DOMSource(n), new StreamResult(System.out));
            // System.out.println("</node" + i + ">");
            // System.out.println();
            if (n == doc.getDocumentElement()) {
                return true;
            }
        }

        // System.out.println("</output>");
        return false;
    }
    
}
