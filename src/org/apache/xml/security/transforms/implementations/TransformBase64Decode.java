
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
package org.apache.xml.security.transforms.implementations;



import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.exceptions.Base64DecodingException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.TransformSpi;
import org.apache.xml.security.transforms.TransformationException;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;
import org.xml.sax.SAXException;


/**
 * Implements the <CODE>http://www.w3.org/2000/09/xmldsig#base64</CODE> decoding
 * transform.
 *
 * <p>The normative specification for base64 decoding transforms is
 * <A HREF="http://www.w3.org/TR/2001/CR-xmldsig-core-20010419/#ref-MIME">[MIME]</A>.
 * The base64 Transform element has no content. The input
 * is decoded by the algorithms. This transform is useful if an
 * application needs to sign the raw data associated with the encoded
 * content of an element. </p>
 *
 * <p>This transform requires an octet stream for input.
 * If an XPath node-set (or sufficiently functional alternative) is
 * given as input, then it is converted to an octet stream by
 * performing operations logically equivalent to 1) applying an XPath
 * transform with expression self::text(), then 2) taking the string-value
 * of the node-set. Thus, if an XML element is identified by a barename
 * XPointer in the Reference URI, and its content consists solely of base64
 * encoded character data, then this transform automatically strips away the
 * start and end tags of the identified element and any of its descendant
 * elements as well as any descendant comments and processing instructions.
 * The output of this transform is an octet stream.</p>
 *
 * @author Christian Geuer-Pollmann
 * @see org.apache.xml.security.utils.Base64
 */
public class TransformBase64Decode extends TransformSpi {

   /** Field implementedTransformURI */
   public static final String implementedTransformURI =
      Transforms.TRANSFORM_BASE64_DECODE;

   /**
    * Method engineGetURI
    *
    *
    */
   protected String engineGetURI() {
      return TransformBase64Decode.implementedTransformURI;
   }

   //J-
   public boolean wantsOctetStream ()   { return true; }
   public boolean wantsNodeSet ()       { return true; }
   public boolean returnsOctetStream () { return true; }
   public boolean returnsNodeSet ()     { return false; }
   //J+

   /**
    * Method enginePerformTransform
    *
    * @param input
    *
    * @throws CanonicalizationException
    * @throws IOException
    * @throws InvalidCanonicalizerException
    * @throws TransformationException
    */
   protected XMLSignatureInput enginePerformTransform(XMLSignatureInput input)
           throws IOException, CanonicalizationException,
                  TransformationException, InvalidCanonicalizerException {

	 try {
      if (input.isOctetStream() || isTextNodeSet(input.getNodeSet())) {
         try {
            byte[] base64Bytes = input.getBytes();
            byte[] decodedBytes = Base64.decode(base64Bytes);

            return new XMLSignatureInput(
               new ByteArrayInputStream(decodedBytes));
         } catch (Base64DecodingException ex) {
            throw new TransformationException("empty", ex);
         }
      } else {
		  try {
            Document doc =
               DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
                  input.getOctetStream());
            DocumentTraversal dt = ((DocumentTraversal) doc);
            Node rootNode = (Node) doc;

            // we accept all nodes
            NodeFilter nodefilter = new AlwaysAcceptNodeFilter();
            TreeWalker treewalker = dt.createTreeWalker(rootNode,
                                                        NodeFilter.SHOW_ALL,
                                                        nodefilter, true);
            StringBuffer sb = new StringBuffer();

            process(treewalker, sb);

            byte[] decodedBytes = Base64.decode(sb.toString());
			
            return new XMLSignatureInput(
               new ByteArrayInputStream(decodedBytes));
		  } catch (ParserConfigurationException e) {
			  throw new TransformationException("c14n.Canonicalizer.Exception",
												e);
		  } catch (SAXException e) {
			  throw new TransformationException("SAX exception", e);
		  } catch (Base64DecodingException ex) {
			  throw new TransformationException("empty", ex);
		  }
      }
	 } catch (ParserConfigurationException e) {
		 throw new TransformationException("c14n.Canonicalizer.Exception",
										   e);
	 } catch (SAXException e) {
		 throw new TransformationException("SAX exception", e);
	 }
   }

   /**
    * Method process
    *
    * @param treewalker
    * @param sb
    */
   private void process(TreeWalker treewalker, StringBuffer sb) {

      Node currentNode = treewalker.getCurrentNode();

      if (currentNode.getNodeType() == Node.TEXT_NODE) {
         sb.append(((Text) currentNode).getData());
      }

      for (Node node1 = treewalker.firstChild(); node1 != null;
              node1 = treewalker.nextSibling()) {
         process(treewalker, sb);
      }

      treewalker.setCurrentNode(currentNode);
   }

   /**
	* Method to take a set of nodes and check whether any are "non-text"
	*/

   private boolean isTextNodeSet(Set s) {

	   boolean isText = true;

	   Iterator it = s.iterator();
	   while (it.hasNext() && isText) {
				
		   Node n = (Node) it.next();
		   if (n.getNodeType() != Node.TEXT_NODE)
			   isText = false;
	   }

	   return isText;
   }

   /**
    * This {@link NodeFilter} always returns <code>true</code>
    *
    * @author Christian Geuer-Pollmann
    */
   public class AlwaysAcceptNodeFilter implements NodeFilter {

      /**
       * Method acceptNode
       *
       * @param n
       *
       */
      public short acceptNode(Node n) {
         return NodeFilter.FILTER_ACCEPT;
      }
   }
}
