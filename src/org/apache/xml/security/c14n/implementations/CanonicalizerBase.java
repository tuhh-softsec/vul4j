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
package org.apache.xml.security.c14n.implementations;



import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.TreeSet;

import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.CanonicalizerSpi;
import org.apache.xml.security.c14n.helper.AttrCompare;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;


/**
 * Abstract base class for canonicalization algorithms.
 *
 * @author Christian Geuer-Pollmann <geuerp@apache.org>
 * @version $Revision$
 */
public abstract class CanonicalizerBase extends CanonicalizerSpi {
   //Constants to be outputed, In char array form, so
   //less garbage is generate when outputed.
   private static final byte[] _END_PI = {'?','>'};
   private static final byte[] _BEGIN_PI = {'<','?'};
   private static final byte[] _END_COMM = {'-','-','>'};
   private static final byte[] _BEGIN_COMM = {'<','!','-','-'};
   private static final byte[] __XA_ = {'&','#','x','A',';'};
   private static final byte[] __X9_ = {'&','#','x','9',';'};
   private static final byte[] _QUOT_ = {'&','q','u','o','t',';'};
   private static final byte[] __XD_ = {'&','#','x','D',';'};
   private static final byte[] _GT_ = {'&','g','t',';'};
   private static final byte[] _LT_ = {'&','l','t',';'};
   private static final byte[] _END_TAG = {'<','/'};
   private static final byte[] _AMP_ = {'&','a','m','p',';'};
   
   boolean _includeComments = false;
   Set _xpathNodeSet = null;
   Document _doc = null;
   Element _documentElement = null;
   Node _rootNodeOfC14n = null;
   /**
    * The node to be skiped/excluded from the DOM tree 
    * in subtree canonicalizations.
    */
   Node _excludeNode =null;
   OutputStream _writer = null;

   /**
    * This Set contains the names (Strings like "xmlns" or "xmlns:foo") of
    * the inclusive namespaces.
    */
   TreeSet _inclusiveNSSet = null;

   final static AttrCompare COMPARE=new AttrCompare();
   final static String XML="xml";
   final static String XMLNS="xmlns";
   final static byte[] equalsStr= {'=','\"'};
   //J+

   /**
    * Constructor CanonicalizerBase
    *
    * @param includeComments
    */
   public CanonicalizerBase(boolean includeComments) {
      this._includeComments = includeComments;
   }

   /**
    * Method engineCanonicalizeSubTree
    * @inheritDoc
    * @param rootNode
    * @throws CanonicalizationException
    */
   public byte[] engineCanonicalizeSubTree(Node rootNode)
           throws CanonicalizationException {
   		return engineCanonicalizeSubTree(rootNode,(Node)null);
   }
   /**
    * Canonicalizes a Subtree node.
    * @param rootNode the root of the subtree to canicalize
    * @param excludeNode a node to be excluded from the canicalize operation
    * @return The canonicalize stream.
    * @throws CanonicalizationException
    */
    public byte[] engineCanonicalizeSubTree(Node rootNode,Node excludeNode)
    throws CanonicalizationException {
    	this._excludeNode = excludeNode;
      this._rootNodeOfC14n = rootNode;
      this._doc = XMLUtils.getOwnerDocument(this._rootNodeOfC14n);
      this._documentElement = this._doc.getDocumentElement();
     
      try {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         this._writer =// new BufferedWriter(new OutputStreamWriter(baos, Canonicalizer.ENCODING));
         	baos;
         NameSpaceSymbTable ns=new NameSpaceSymbTable();
         if (this._rootNodeOfC14n instanceof Element) {
         	//Fills the nssymbtable with the definitions of the parent of the root subnode
         	getParentNameSpaces((Element)this._rootNodeOfC14n,ns);
         }
         this.canonicalizeSubTree(this._rootNodeOfC14n,ns);
         this._writer.close();

         return baos.toByteArray();
      } catch (UnsupportedEncodingException ex) {
         throw new CanonicalizationException("empty", ex);
      } catch (IOException ex) {
         throw new CanonicalizationException("empty", ex);
      } finally {

         // mark contents for garbage collector
         this._rootNodeOfC14n = null;
         this._excludeNode=null;
         this._doc = null;
         this._documentElement = null;
         this._writer = null;
      }
   }

   
   /**
    * Method canonicalizeSubTree, this function is a recursive one.
    *    
    * @param currentNode
    * @param ns 
    * @throws CanonicalizationException
    * @throws IOException
    */
   final void canonicalizeSubTree(Node currentNode, NameSpaceSymbTable ns)
           throws CanonicalizationException, IOException {

      int currentNodeType = currentNode.getNodeType();

      switch (currentNodeType) {

      case Node.DOCUMENT_TYPE_NODE :
      default :
         break;

      case Node.ENTITY_NODE :
      case Node.NOTATION_NODE :
      case Node.DOCUMENT_FRAGMENT_NODE :
      case Node.ATTRIBUTE_NODE :
         // illegal node type during traversal
         throw new CanonicalizationException("empty");
      case Node.DOCUMENT_NODE :
         for (Node currentChild = currentNode.getFirstChild();
                 currentChild != null;
                 currentChild = currentChild.getNextSibling()) {
            canonicalizeSubTree(currentChild,ns);
         }
         break;

      case Node.COMMENT_NODE :
         if (this._includeComments) {
            outputCommentToWriter((Comment) currentNode, this._writer);
         }
         break;

      case Node.PROCESSING_INSTRUCTION_NODE :
         outputPItoWriter((ProcessingInstruction) currentNode, this._writer);
         break;

      case Node.TEXT_NODE :
      case Node.CDATA_SECTION_NODE :
         outputTextToWriter(currentNode.getNodeValue(), this._writer);
         break;

      case Node.ELEMENT_NODE :        
      	if (currentNode==this._excludeNode) {
      		return;
      	}
        OutputStream writer=this._writer;
         Element currentElement = (Element) currentNode;
         //Add a level to the nssymbtable. So latter can be pop-back.
      	 ns.outputNodePush();
         writer.write('<');
         writeStringToUtf8(currentElement.getTagName(),writer);
         //this._writer.write(currentElement.getTagName().getBytes("UTF8"));
         
         Iterator attrs = this.handleAttributesSubtree(currentElement,ns);
         
         // we output all Attrs which are available
         //for (int i = 0; i < attrs.length; i++) {
         while (attrs.hasNext()) {
         	Attr attr = (Attr) attrs.next();
            outputAttrToWriter(attr.getNodeName(),
                                    attr.getNodeValue(), writer);
         }

         writer.write('>');

         // traversal
         for (Node currentChild = currentNode.getFirstChild();
                 currentChild != null;
                 currentChild = currentChild.getNextSibling()) {
            canonicalizeSubTree(currentChild,ns);
         }

         writer.write(_END_TAG);
         writeStringToUtf8(currentElement.getTagName(),writer);
         //this._writer.write(currentElement.getTagName().getBytes("UTF8"));
         writer.write('>');
         //We fineshed with this level, pop to the previous definitions.
         ns.outputNodePop();
         break;
      }
   }

   //J-
   static final int NODE_BEFORE_DOCUMENT_ELEMENT = -1;
   static final int NODE_NOT_BEFORE_OR_AFTER_DOCUMENT_ELEMENT = 0;
   static final int NODE_AFTER_DOCUMENT_ELEMENT = 1;
   //J+

   /**
    * Checks whether a Comment or ProcessingInstruction is before or after the
    * document element. This is needed for prepending or appending "\n"s.
    *
    * @param currentNode comment or pi to check
    * @return NODE_BEFORE_DOCUMENT_ELEMENT, NODE_NOT_BEFORE_OR_AFTER_DOCUMENT_ELEMENT or NODE_AFTER_DOCUMENT_ELEMENT
    * @see #NODE_BEFORE_DOCUMENT_ELEMENT
    * @see #NODE_NOT_BEFORE_OR_AFTER_DOCUMENT_ELEMENT
    * @see #NODE_AFTER_DOCUMENT_ELEMENT
    */
   final static int getPositionRelativeToDocumentElement(Node currentNode) {

      if (currentNode == null) {
         return CanonicalizerBase.NODE_NOT_BEFORE_OR_AFTER_DOCUMENT_ELEMENT;
      }

      Document doc = currentNode.getOwnerDocument();

      if (currentNode.getParentNode() != doc) {
         return CanonicalizerBase.NODE_NOT_BEFORE_OR_AFTER_DOCUMENT_ELEMENT;
      }

      Element documentElement = doc.getDocumentElement();

      if (documentElement == null) {
         return CanonicalizerBase.NODE_NOT_BEFORE_OR_AFTER_DOCUMENT_ELEMENT;
      }

      if (documentElement == currentNode) {
         return CanonicalizerBase.NODE_NOT_BEFORE_OR_AFTER_DOCUMENT_ELEMENT;
      }

      for (Node x = currentNode; x != null; x = x.getNextSibling()) {
         if (x == documentElement) {
            return CanonicalizerBase.NODE_BEFORE_DOCUMENT_ELEMENT;
         }
      }

      return CanonicalizerBase.NODE_AFTER_DOCUMENT_ELEMENT;
   }

   /**
    * Method engineCanonicalizeXPathNodeSet
    * @inheritDoc
    * @param xpathNodeSet
    * @param inclusiveNamespaces
    * @throws CanonicalizationException
    */
   public byte[] engineCanonicalizeXPathNodeSet(Set xpathNodeSet, String inclusiveNamespaces)
           throws CanonicalizationException {       
      this._xpathNodeSet = xpathNodeSet;

      if (this._xpathNodeSet.size() == 0) {
         return new byte[0];
      }

      {

         // get only a single node as anchor to fetch the owner document
         Node n = (Node) this._xpathNodeSet.iterator().next();

         this._doc = XMLUtils.getOwnerDocument(n);
         this._documentElement = this._doc.getDocumentElement();
         if (nullNode==null) {
         	nullNode=
         		_doc.createAttributeNS(Constants.NamespaceSpecNS,XMLNS);
         	nullNode.setValue("");
         }
      }

      try {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();

         this._writer = //new BufferedWriter(new OutputStreamWriter(baos, Canonicalizer.ENCODING));
            baos;
         this.canonicalizeXPathNodeSet(this._doc, new  NameSpaceSymbTable());
         this._writer.close();

         return baos.toByteArray();
      } catch (UnsupportedEncodingException ex) {
         throw new CanonicalizationException("empty", ex);
      } catch (IOException ex) {
         throw new CanonicalizationException("empty", ex);
      } finally {
         this._xpathNodeSet = null;
         this._rootNodeOfC14n = null;
         this._doc = null;
         this._documentElement = null;
         this._writer = null;
      }
   }
     
   /**
    * Method engineCanonicalizeXPathNodeSet
    * @inheritDoc
    * @param xpathNodeSet
    * @throws CanonicalizationException
    */
   public byte[] engineCanonicalizeXPathNodeSet(Set xpathNodeSet)
           throws CanonicalizationException {          	
      this._xpathNodeSet = xpathNodeSet;

      if (this._xpathNodeSet.size() == 0) {
         return new byte[0];
      }

      if (this._doc == null) {
         this._doc = XMLUtils.getOwnerDocument(this._xpathNodeSet);
         this._documentElement = this._doc.getDocumentElement();
         this._rootNodeOfC14n = this._doc;
         nullNode=
			_doc.createAttributeNS(Constants.NamespaceSpecNS,XMLNS);
		nullNode.setValue("");
      }

      try {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();

         this._writer = // new BufferedWriter(new OutputStreamWriter(baos, Canonicalizer.ENCODING));
            baos;
         this.canonicalizeXPathNodeSet(this._rootNodeOfC14n,new  NameSpaceSymbTable());
         this._writer.close();

         return baos.toByteArray();
      } catch (UnsupportedEncodingException ex) {
         throw new CanonicalizationException("empty", ex);
      } catch (IOException ex) {
         throw new CanonicalizationException("empty", ex);
      } finally {
         this._xpathNodeSet = null;
         this._rootNodeOfC14n = null;
         this._doc = null;
         this._documentElement = null;
         this._writer = null;
      }
   }

   /**
    * Canoicalizes all the nodes included in the currentNode and contained in the 
	* _xpathNodeSet field.
    *
    * @param currentNode
	* @param ns
    * @throws CanonicalizationException
    * @throws IOException
    */
   final void canonicalizeXPathNodeSet(Node currentNode, NameSpaceSymbTable ns )
           throws CanonicalizationException, IOException {
 
      int currentNodeType = currentNode.getNodeType();
      boolean currentNodeIsVisible = this._xpathNodeSet.contains(currentNode);

      switch (currentNodeType) {

      case Node.DOCUMENT_TYPE_NODE :
      default :
         break;

      case Node.ENTITY_NODE :
      case Node.NOTATION_NODE :
      case Node.DOCUMENT_FRAGMENT_NODE :
      case Node.ATTRIBUTE_NODE :
         throw new CanonicalizationException("empty");
      case Node.DOCUMENT_NODE :
         for (Node currentChild = currentNode.getFirstChild();
                 currentChild != null;
                 currentChild = currentChild.getNextSibling()) {
            canonicalizeXPathNodeSet(currentChild,ns);
         }
         break;

      case Node.COMMENT_NODE :
         if (this._includeComments
                 && this._xpathNodeSet.contains(currentNode)) {           
            outputCommentToWriter((Comment) currentNode, this._writer);           
         }
         break;

      case Node.PROCESSING_INSTRUCTION_NODE :
         if (this._xpathNodeSet.contains(currentNode)) {            
            outputPItoWriter((ProcessingInstruction) currentNode, this._writer);            
         }
         break;

      case Node.TEXT_NODE :
      case Node.CDATA_SECTION_NODE :
         if (this._xpathNodeSet.contains(currentNode)) {
            outputTextToWriter(currentNode.getNodeValue(), this._writer);

            for (Node nextSibling = currentNode.getNextSibling();
                    (nextSibling != null)
                    && ((nextSibling.getNodeType() == Node.TEXT_NODE)
                        || (nextSibling.getNodeType()
                            == Node.CDATA_SECTION_NODE));
                    nextSibling = nextSibling.getNextSibling()) {

               /* The XPath data model allows to select only the first of a
                * sequence of mixed text and CDATA nodes. But we must output
                * them all, so we must search:
                *
                * @see http://nagoya.apache.org/bugzilla/show_bug.cgi?id=6329
                */
               outputTextToWriter(nextSibling.getNodeValue(), this._writer);
            }
         }
         break;

      case Node.ELEMENT_NODE :
         Element currentElement = (Element) currentNode;
      	 OutputStream writer=this._writer;
         if (currentNodeIsVisible) {
            //This is an outputNode.
         	ns.outputNodePush();
            writer.write('<');
            writeStringToUtf8(currentElement.getTagName(),writer);
            //this._writer.write(currentElement.getTagName().getBytes("UTF8"));
         } else {
           //Not an outputNode.
         	ns.push(); 	
         }

         // we output all Attrs which are available
         Iterator attrs = handleAttributes(currentElement,ns);

         //for (int i = 0; i < attrs.length; i++) {
         while (attrs.hasNext()) {
            Attr attr = (Attr) attrs.next();
            outputAttrToWriter(attr.getNodeName(), attr.getNodeValue(), this._writer);
         }

         if (currentNodeIsVisible) {
            writer.write('>');
         }

         // traversal
         for (Node currentChild = currentNode.getFirstChild();
                 currentChild != null;
                 currentChild = currentChild.getNextSibling()) {
            canonicalizeXPathNodeSet(currentChild,ns);
         }

         if (currentNodeIsVisible) {
            writer.write(_END_TAG);
            writeStringToUtf8(currentElement.getTagName(),writer);
            //this._writer.write(currentElement.getTagName().getBytes("UTF8"));
            writer.write('>');
            ns.outputNodePop();
         } else {
         	ns.pop();
         }
         break;
      }
   }

   	/**
	 * Adds to ns the definitons from the parent elements of el
   	 * @param el
   	 * @param ns
   	 */
   	final static void getParentNameSpaces(Element el,NameSpaceSymbTable ns)  {
   		List parents=new ArrayList();
   		Node n1=el.getParentNode();
   		if (!(n1 instanceof Element)) {
   			return;
   		}
   		//Obtain all the parents of the elemnt
   		Element parent=(Element) el.getParentNode();
   		while (parent!=null) {
   			parents.add(parent);
   			Node n=parent.getParentNode();
   			if (!(n instanceof Element )) {
   				break;
   			}
   			parent=(Element)n;
   		}
   		//Visit them in reverse order.
   		ListIterator it=parents.listIterator(parents.size());
   		while (it.hasPrevious()) {
   		Element ele=(Element)it.previous();
		NamedNodeMap attrs = ele.getAttributes();
   		int attrsLength = attrs.getLength();
   		 for (int i = 0; i < attrsLength; i++) {
            Attr N = (Attr) attrs.item(i);
            if (!Constants.NamespaceSpecNS.equals(N.getNamespaceURI())) {
               //Not a namespace definition, ignore.
               continue;
            }

            String NName=N.getLocalName();
            String NValue=N.getNodeValue();
            if (XML.equals(NName)
                    && Constants.XML_LANG_SPACE_SpecNS.equals(NValue)) {
               continue;
            }            
            ns.addMapping(NName,NValue,N);             
   		 }   			
   		}
        Attr nsprefix;
        if (((nsprefix=ns.getMappingWithoutRendered("xmlns"))!=null) 
                &&
                "".equals(nsprefix.getValue())) {
                
        	ns.addMappingAndRender("xmlns","",nullNode);
        }
   	}
   /**
    * Outputs an Attribute to the internal Writer.
    *
    * The string value of the node is modified by replacing
    * <UL>
    * <LI>all ampersands (&) with <CODE>&amp;amp;</CODE></LI>
    * <LI>all open angle brackets (<) with <CODE>&amp;lt;</CODE></LI>
    * <LI>all quotation mark characters with <CODE>&amp;quot;</CODE></LI>
    * <LI>and the whitespace characters <CODE>#x9</CODE>, #xA, and #xD, with character
    * references. The character references are written in uppercase
    * hexadecimal with no leading zeroes (for example, <CODE>#xD</CODE> is represented
    * by the character reference <CODE>&amp;#xD;</CODE>)</LI>
    * </UL>
    *
    * @param name
    * @param value
    * @param writer 
    * @throws IOException
    */
   static final void outputAttrToWriter(String name, String value, OutputStream writer) throws IOException {
      writer.write(' ');
      writeStringToUtf8(name,writer);
      writer.write(equalsStr);

      int length = value.length();
      for (int i=0;i < length; i++) {        
         char c = value.charAt(i);

         switch (c) {

         case '&' :
            writer.write(_AMP_);
            break;

         case '<' :
            writer.write(_LT_);
            break;

         case '"' :
            writer.write(_QUOT_);
            break;

         case 0x09 :    // '\t'
            writer.write(__X9_);
            break;

         case 0x0A :    // '\n'
            writer.write(__XA_);
            break;

         case 0x0D :    // '\r'
            writer.write(__XD_);
            break;

         default :
            writeCharToUtf8(c,writer);
            //this._writer.write(c);
            break;
         }
      }

      writer.write('\"');
   }

   final static void writeCharToUtf8(char c,OutputStream out) throws IOException{
   	
   	if (/*(c >= 0x0001) &&*/ (c <= 0x007F)) {
   		out.write(c);
   	} else if (c > 0x07FF) {
   		out.write(0xE0 | ((c >> 12) & 0x0F));
   		out.write(0x80 | ((c >>  6) & 0x3F));
   		out.write(0x80 | ((c >>  0) & 0x3F));
   		
   	} else {
   		out.write(0xC0 | ((c >>  6) & 0x1F));
   		out.write(0x80 | ((c >>  0) & 0x3F));
   		
   	}
   	
   }
   
   final static void writeStringToUtf8(String str,OutputStream out) throws IOException{
   	int length=str.length();
   	for (int i=0;i<length;i++) {
   		char c=str.charAt(i);
   		if (/*(c >= 0x0001) && */(c <= 0x007F)) {
   			out.write(c);
   		} else if (c > 0x07FF) {
   			out.write(0xE0 | ((c >> 12) & 0x0F));
   			out.write(0x80 | ((c >>  6) & 0x3F));
   			out.write(0x80 | ((c >>  0) & 0x3F));
   			
   		} else {
   			out.write(0xC0 | ((c >>  6) & 0x1F));
   			out.write(0x80 | ((c >>  0) & 0x3F));
   			
   		}
   	}
    
   }
   /**
    * Outputs a PI to the internal Writer.
    *
    * @param currentPI
    * @param writer TODO
    * @throws IOException
    */
   static final void outputPItoWriter(ProcessingInstruction currentPI, OutputStream writer) throws IOException {
   	  int position = getPositionRelativeToDocumentElement(currentPI);

      if (position == NODE_AFTER_DOCUMENT_ELEMENT) {
        writer.write('\n');
      }
      writer.write(_BEGIN_PI);

      String target = currentPI.getTarget();
      //char []c= target.toCharArray();
      int length = target.length();

      for (int i = 0; i < length; i++) {
         //char c = target.charAt(i);
      	 char c=target.charAt(i);
         switch (c) {

         case 0x0D :
            writer.write(__XD_);
            break;

         default :
            writeCharToUtf8(c,writer);
            //this._writer.write(c[i]);
            break;
         }
      }

      String data = currentPI.getData();
     
      length = data.length();

      if ((data != null) && (length > 0)) {
         writer.write(' ');

         for (int i = 0; i < length; i++) {            
         	char c=data.charAt(i);
            switch (c) {

            case 0x0D :
               writer.write(__XD_);
               break;

            default :
                writeCharToUtf8(c,writer);
               //this._writer.write(c[i]);
               break;
            }
         }
      }

      writer.write(_END_PI);
      if (position == NODE_BEFORE_DOCUMENT_ELEMENT) {
        writer.write('\n');
     }
   }

   /**
    * Method outputCommentToWriter
    *
    * @param currentComment
    * @param writer TODO
    * @throws IOException
    */
   static final void outputCommentToWriter(Comment currentComment, OutputStream writer) throws IOException {
   	   int position = getPositionRelativeToDocumentElement(currentComment);
   	   if (position == NODE_AFTER_DOCUMENT_ELEMENT) {
   		writer.write('\n');
   	  }
      writer.write(_BEGIN_COMM);

      String data = currentComment.getData();
      int length = data.length();
      //char c[]=data.toCharArray();

      for (int i = 0; i < length; i++) {
         char c = data.charAt(i);

         switch (c) {

         case 0x0D :
            writer.write(__XD_);
            break;

         default :
            writeCharToUtf8(c,writer);
            //this._writer.write(c[i]);
            break;
         }       
      }

      writer.write(_END_COMM);
      if (position == NODE_BEFORE_DOCUMENT_ELEMENT) {
		writer.write('\n');
	 }
   }

   /**
    * Outputs a Text of CDATA section to the internal Writer.
    *
    * @param text
    * @param writer TODO
    * @throws IOException
    */
   static final void outputTextToWriter(String text, OutputStream writer) throws IOException {
      int length = text.length();
   	  //char c[]=text.toCharArray();

      for (int i = 0; i < length; i++) {
         char c = text.charAt(i);

         switch (c) {

         case '&' :
            writer.write(_AMP_);
            break;

         case '<' :
            writer.write(_LT_);
            break;

         case '>' :
            writer.write(_GT_);
            break;

         case 0xD :
            writer.write(__XD_);
            break;

         default :
            writeCharToUtf8(c,writer);
            //this._writer.write(c[i]);
            break;
         }
      }
   }

   /**
    * Obtain the attributes to output for this node in XPathNodeSet c14n. 
    *
    * @param E
	* @param ns
	* @return the attributes nodes to output.
    * @throws CanonicalizationException
    */
   abstract Iterator handleAttributes(Element E, NameSpaceSymbTable ns )
   throws CanonicalizationException;

   /**
    * Obtain the attributes to output for this node in a Subtree c14n.
    *
    * @param E
	* @param ns
	* @return the attributes nodes to output.
    * @throws CanonicalizationException
    */
   abstract Iterator handleAttributesSubtree(Element E, NameSpaceSymbTable ns)
   throws CanonicalizationException;

   //The null xmlns definiton.
   protected static Attr nullNode;
   /**
    * @return Returns the _includeComments.
    */
   final public boolean is_includeComments() {
	 return _includeComments;
   }
   /**
    * @param comments The _includeComments to set.
    */
    final public void set_includeComments(boolean comments) {
	    _includeComments = comments;
    }
}
