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
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.c14n.CanonicalizerSpi;
import org.apache.xml.security.c14n.helper.C14nHelper;
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
   //J-
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
   Writer _writer = null;

   /**
    * This Set contains the names (Strings like "xmlns" or "xmlns:foo") of
    * the inclusive namespaces.
    */
   Set _inclusiveNSSet = null;

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
    *
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
         this._writer = new OutputStreamWriter(baos, Canonicalizer.ENCODING);         
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
   void canonicalizeSubTree(Node currentNode, NameSpaceSymbTable ns)
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
            this.outputCommentToWriter((Comment) currentNode);
         }
         break;

      case Node.PROCESSING_INSTRUCTION_NODE :
         this.outputPItoWriter((ProcessingInstruction) currentNode);
         break;

      case Node.TEXT_NODE :
      case Node.CDATA_SECTION_NODE :
         this.outputTextToWriter(currentNode.getNodeValue());
         break;

      case Node.ELEMENT_NODE :
      	if (currentNode==this._excludeNode) {
      		return;
      	}
         Element currentElement = (Element) currentNode;
         //Add a level to the nssymbtable. So latter can be pop-back.
      	 ns.outputNodePush();
         this._writer.write("<");
         this._writer.write(currentElement.getTagName());
         
         Object[] attrs = this.handleAttributesSubtree(currentElement,ns);
         
         // we output all Attrs which are available
         for (int i = 0; i < attrs.length; i++) {
         	Attr attr = (Attr) attrs[i];
            this.outputAttrToWriter(attr.getNodeName(),
                                    attr.getNodeValue());
         }

         this._writer.write(">");

         // traversal
         for (Node currentChild = currentNode.getFirstChild();
                 currentChild != null;
                 currentChild = currentChild.getNextSibling()) {
            canonicalizeSubTree(currentChild,ns);
         }

         this._writer.write("</");
         this._writer.write(currentElement.getTagName());
         this._writer.write(">");
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
    * @see NODE_BEFORE_DOCUMENT_ELEMENT
    * @see NODE_NOT_BEFORE_OR_AFTER_DOCUMENT_ELEMENT
    * @see NODE_AFTER_DOCUMENT_ELEMENT
    */
   static int getPositionRelativeToDocumentElement(Node currentNode) {

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
    *
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
         nullNode=
			_doc.createAttributeNS(Constants.NamespaceSpecNS,"xmlns");
		nullNode.setValue("");
      }

      try {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();

         this._writer = new OutputStreamWriter(baos, Canonicalizer.ENCODING);
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
    *
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
			_doc.createAttributeNS(Constants.NamespaceSpecNS,"xmlns");
		nullNode.setValue("");
      }

      try {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();

         this._writer = new OutputStreamWriter(baos, Canonicalizer.ENCODING);         
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
   void canonicalizeXPathNodeSet(Node currentNode, NameSpaceSymbTable ns )
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
            this.outputCommentToWriter((Comment) currentNode);           
         }
         break;

      case Node.PROCESSING_INSTRUCTION_NODE :
         if (this._xpathNodeSet.contains(currentNode)) {            
            this.outputPItoWriter((ProcessingInstruction) currentNode);            
         }
         break;

      case Node.TEXT_NODE :
      case Node.CDATA_SECTION_NODE :
         if (this._xpathNodeSet.contains(currentNode)) {
            this.outputTextToWriter(currentNode.getNodeValue());

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
               this.outputTextToWriter(nextSibling.getNodeValue());
            }
         }
         break;

      case Node.ELEMENT_NODE :
         Element currentElement = (Element) currentNode;
      	
         if (currentNodeIsVisible) {
            //This is an outputNode.
         	ns.outputNodePush();
            this._writer.write("<");
            this._writer.write(currentElement.getTagName());
         } else {
           //Not an outputNode.
         	ns.push(); 	
         }

         // we output all Attrs which are available
         Object[] attrs = handleAttributes(currentElement,ns);

         attrs = C14nHelper.sortAttributes(attrs);

         for (int i = 0; i < attrs.length; i++) {
            Attr attr = (Attr) attrs[i];
            this.outputAttrToWriter(attr.getNodeName(), attr.getNodeValue());
         }

         if (currentNodeIsVisible) {
            this._writer.write(">");
         }

         // traversal
         for (Node currentChild = currentNode.getFirstChild();
                 currentChild != null;
                 currentChild = currentChild.getNextSibling()) {
            canonicalizeXPathNodeSet(currentChild,ns);
         }

         if (currentNodeIsVisible) {
            this._writer.write("</");
            this._writer.write(currentElement.getTagName());
            this._writer.write(">");
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
   	 * @throws CanonicalizationException
   	 */
   	void getParentNameSpaces(Element el,NameSpaceSymbTable ns) throws CanonicalizationException {
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

            if (C14nHelper.namespaceIsRelative(N)) {
               Object exArgs[] = { ele.getTagName(), N.getName(), N.getNodeValue() };
               throw new CanonicalizationException(
                  "c14n.Canonicalizer.RelativeNamespace", exArgs);
            }
            
            if ("xml".equals(N.getLocalName())
                    && Constants.XML_LANG_SPACE_SpecNS.equals(N.getNodeValue())) {
               continue;
            }            
            ns.addMapping(N.getName(),N.getValue(),N);             
   		 }   			
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
    * @throws IOException
    */
   void outputAttrToWriter(String name, String value) throws IOException {

      this._writer.write(" ");
      this._writer.write(name);
      this._writer.write("=\"");

      int length = value.length();
      //char c[]=value.toCharArray();
      for (int i = 0; i < length; i++) {
         char c = value.charAt(i);

         switch (c) {

         case '&' :
            this._writer.write("&amp;");
            break;

         case '<' :
            this._writer.write("&lt;");
            break;

         case '"' :
            this._writer.write("&quot;");
            break;

         case 0x09 :    // '\t'
            this._writer.write("&#x9;");
            break;

         case 0x0A :    // '\n'
            this._writer.write("&#xA;");
            break;

         case 0x0D :    // '\r'
            this._writer.write("&#xD;");
            break;

         default :
            this._writer.write(c);
            break;
         }
      }

      this._writer.write("\"");
   }

   /**
    * Outputs a PI to the internal Writer.
    *
    * @param currentPI
    * @throws IOException
    */
   void outputPItoWriter(ProcessingInstruction currentPI) throws IOException {
   	  int position = getPositionRelativeToDocumentElement(currentPI);

      if (position == NODE_AFTER_DOCUMENT_ELEMENT) {
        this._writer.write('\n');
      }
      this._writer.write("<?");

      String target = currentPI.getTarget();
      char []c= target.toCharArray();
      //int length = target.length();

      for (int i = 0; i < c.length; i++) {
         //char c = target.charAt(i);

         switch (c[i]) {

         case 0x0D :
            this._writer.write("&#xD;");
            break;

         default :
            this._writer.write(c[i]);
            break;
         }
      }

      String data = currentPI.getData();
      c=data.toCharArray();
      //length = data.length();

      if ((data != null) && (c.length > 0)) {
         this._writer.write(' ');

         for (int i = 0; i < c.length; i++) {            

            switch (c[i]) {

            case 0x0D :
               this._writer.write("&#xD;");
               break;

            default :
               this._writer.write(c[i]);
               break;
            }
         }
      }

      this._writer.write("?>");
      if (position == NODE_BEFORE_DOCUMENT_ELEMENT) {
        this._writer.write('\n');
     }
   }

   /**
    * Method outputCommentToWriter
    *
    * @param currentComment
    * @throws IOException
    */
   void outputCommentToWriter(Comment currentComment) throws IOException {
   	   int position = getPositionRelativeToDocumentElement(currentComment);

   	   if (position == NODE_AFTER_DOCUMENT_ELEMENT) {
   		this._writer.write('\n');
   	  }
      this._writer.write("<!--");

      String data = currentComment.getData();
      //int length = data.length();
      char c[]=data.toCharArray();

      for (int i = 0; i < c.length; i++) {
         //char c = data.charAt(i);

         switch (c[i]) {

         case 0x0D :
            this._writer.write("&#xD;");
            break;

         default :
            this._writer.write(c[i]);
            break;
         }       
      }

      this._writer.write("-->");
      if (position == NODE_BEFORE_DOCUMENT_ELEMENT) {
		this._writer.write('\n');
	 }
   }

   /**
    * Outputs a Text of CDATA section to the internal Writer.
    *
    * @param text
    * @throws IOException
    */
   void outputTextToWriter(String text) throws IOException {

      //int length = text.length();
   	char c[]=text.toCharArray();

      for (int i = 0; i < c.length; i++) {
         //char c = text.charAt(i);

         switch (c[i]) {

         case '&' :
            this._writer.write("&amp;");
            break;

         case '<' :
            this._writer.write("&lt;");
            break;

         case '>' :
            this._writer.write("&gt;");
            break;

         case 0xD :
            this._writer.write("&#xD;");
            break;

         default :
            this._writer.write(c[i]);
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
   abstract Object[] handleAttributes(Element E, NameSpaceSymbTable ns )
   throws CanonicalizationException;

   /**
    * Obtain the attributes to output for this node in a Subtree c14n.
    *
    * @param E
	* @param ns
	* @return the attributes nodes to output.
    * @throws CanonicalizationException
    */
   abstract Object[] handleAttributesSubtree(Element E, NameSpaceSymbTable ns)
   throws CanonicalizationException;

/**
 * A stack based Symble Table.
 *<br>For speed reasons all the symbols are introduced in the same map,
 * and at the same time in a list so it can be removed when the frame is pop back.
 **/
static protected final class NameSpaceSymbTable {
	/**
	 * The internal structure of NameSpaceSymbTable.
	 **/
	final class NameSpaceSymbEntry {
		public NameSpaceSymbEntry(String name,Attr n,boolean rendered) {
			this.uri=name;			
			this.rendered=rendered;
			this.n=n;
		}
		public Object clone() {
			NameSpaceSymbEntry ne=new NameSpaceSymbEntry(uri,n,rendered);
			ne.level=level;
			return ne;
		}
		/** The level where the definition was rendered(Only for inclusive) */
		int level=0;
		/**The URI that the prefix defines */
		String uri;
		/**The last output in the URI for this prefix (This for speed reason).*/
		String lastrendered=null;
		/**This prefix-URI has been already render or not.*/
		boolean rendered=false;
		/**The attribute to include.*/
		Attr n;
	};
	/**The map betwen prefix-> entry table. */
	Map symb = new HashMap();
	/**The level of nameSpaces (for Inclusive visibility).*/
	int nameSpaces=0;
	/**The stacks for removing the definitions when doing pop.*/
	List level = new ArrayList();
	/**The number of definitions in this level (for pop purpose).*/
	int current = 0;
	
    /**
     * Default constractor
     **/		
    public NameSpaceSymbTable() {
    	//Insert the default binding for xmlns.
    	NameSpaceSymbEntry ne=new NameSpaceSymbEntry("",null,true);
		ne.lastrendered="";
    	symb.put("xmlns",ne);    
    }
    
    /**
	 * Get all the unrendered nodes in the name space.
	 * For Inclusive rendering
	 **/       
	public  List getUnrenderedNodes() {		
	   List result=new ArrayList();
	   Iterator it=symb.entrySet().iterator();
	   while (it.hasNext()) {	   	   
	   		NameSpaceSymbEntry n=(NameSpaceSymbEntry)((Map.Entry)it.next()).getValue();
	   		//put them rendered?
	   		if ((!n.rendered) && (n.n!=null)) {
	   			result.add(n.n);
	   			n.rendered=true;
	   		}
	   }
	   return result;
	}
	
	/**
     * Push a frame for visible namespace. 
     * For Inclusive rendering.
     **/
	public void outputNodePush() {
		nameSpaces++;
		push();
	}
	
	/**
     * Pop a frame for visible namespace.
     **/
	public void outputNodePop() {
		nameSpaces--;
		pop();
	}
	
	/**
     * Push a frame for a node.
     * Inclusive or Exclusive.
     **/
	public void push() {		
		//Put the number of namespace definitions in the stack.
		level.add(new Integer(current));
		current = 0;
	}
	
	/**
     * Pop a frame.
     * Inclusive or Exclusive.
     **/
	public void pop() {		
		int size = level.size() - 1;
		int i=0;
		if (current != 0) {
			//We have some definitions to undo.						
			for (i = 0; i < current; i++) {
				//For every definition in the current frame.
				Object key = level.remove(size - i);
				if (key instanceof Object[]) {
					//It has a previous mapping puting back to the map. removing the current one.
					Object[] obs = (Object[]) key;					
					symb.put(obs[0], obs[1]);
				} else {
					//Just remove it from the map of definitions
					symb.remove(key);
				}
			}
		} 
		//Get back the current number of definitions.
		current = ((Integer) level.remove(size-i)).intValue();
	}
	

	
	
	/**
	 * Gets the attribute node that defines the binding for the prefix.      
     * @param prefix the prefix to obtain the attribute.
     * @param outputNode the container node is an output node.
     * @return null if there is no need to render the prefix. Otherwise the node of
     * definition.
     **/
	public Attr getMapping(String prefix) {					
		NameSpaceSymbEntry entry=(NameSpaceSymbEntry) symb.get(prefix);
		if (entry==null) {
			//There is no definition for the prefix(a bug?).
			return null;
		}
		if (entry.rendered) {		
			//No need to render an entry already rendered.
			return null;		
		}
		// Store in the stack for latter pop it.
		Object []obs={prefix,entry.clone()};
		level.add(obs);
		// Mark this entry as render.
		entry.rendered=true;
		entry.level=nameSpaces;
		entry.lastrendered=entry.uri;		
		current++;
		// Return the node for outputing.
		return entry.n;
	}
	
	/**
     * Gets a definition without mark it as render. 
     * For render in exclusive c14n the namespaces in the include prefixes.
     **/
	public Attr getMappingWithoutRendered(String prefix) {					
		NameSpaceSymbEntry entry=(NameSpaceSymbEntry) symb.get(prefix);
		if (entry==null) {		   
			return null;
		}
		if (entry.rendered) {		
			return null;		
		}
		return entry.n;
	}
	
	/**
     * Adds the mapping for a prefix.
     **/
	public void addMapping(String prefix, String uri,Attr n) {						
		NameSpaceSymbEntry ob = (NameSpaceSymbEntry)symb.get(prefix);		
		if ((ob!=null) && uri.equals(ob.uri)) {
			//If we have it previously defined. Don't keep working.
			return;
		}			
		//Creates and entry in the table for this new definition.
		NameSpaceSymbEntry ne=new NameSpaceSymbEntry(uri,n,false);		
		symb.put(prefix, ne);
		if (ob != null) {
			//We have a previous definition store it for the pop.
			Object obs[] = {prefix, ob};
			level.add(obs);
			//Check if a previous definition(not the inmidiatly one) has been rendered.			
			ne.lastrendered=ob.lastrendered;			
			if ((ob.lastrendered!=null)&& (ob.lastrendered.equals(uri))) {
				//Yes it is. Mark as rendered.
				ne.rendered=true;
			}			
		} else {
			//Just add the prefix for pop.
			level.add(prefix);
		}
		//A more definition in this frame.
		current++;			
	}

	/**
     * Adds a definition and mark it as render.
     * For inclusive c14n.
     **/
	public Node addMappingAndRender(String prefix, String uri,Attr n) {						
		NameSpaceSymbEntry ob = (NameSpaceSymbEntry)symb.get(prefix);
		
		if ((ob!=null) && uri.equals(ob.uri)) {
			if (!ob.rendered) {	
				Object obs[] = {prefix, ob.clone()};
				level.add(obs);
				current++;
				ob.rendered=true;
				return ob.n;
			}			
			return null;
		}	
		
		NameSpaceSymbEntry ne=new NameSpaceSymbEntry(uri,n,true);
		
		symb.put(prefix, ne);
		if (ob != null) {
			Object obs[] = {prefix, ob};
			ne.lastrendered=ob.lastrendered;
			
			if ((ob.lastrendered!=null)&& (ob.lastrendered.equals(uri))) {
				ne.rendered=true;
			}
			level.add(obs);
		} else {
			level.add(prefix);
		}
		current++;		
		return ne.n;
	}
	/** 
     * Adds & gets(if needed) the attribute node that defines the binding for the prefix. 
     * Take on account if the rules of rendering in the inclusive c14n.
     * For inclusive c14n.
     * @param prefix the prefix to obtain the attribute.
     * @param outputNode the container element is an output element.
     * @return null if there is no need to render the prefix. Otherwise the node of
     * definition.     
     **/
	public Node addMappingAndRenderXNodeSet(String prefix, String uri,Attr n,boolean outputNode) {						
		NameSpaceSymbEntry ob = (NameSpaceSymbEntry)symb.get(prefix);
		int visibleNameSpaces=nameSpaces;		
		if ((ob!=null) && uri.equals(ob.uri)) {
			if (!ob.rendered) {	
				Object obs[] = {prefix, ob.clone()};
				level.add(obs);
				current++;
				ob.rendered=true;
				ob.level=visibleNameSpaces;
				return ob.n;
			}
			Object []obs={prefix,ob.clone()};				
			current++;
			level.add(obs);							
			if (outputNode && (((visibleNameSpaces-ob.level)<2) || "xmlns".equals(prefix)) ) {
				ob.level=visibleNameSpaces;
				return null; //Already rendered, just return nulll
			}
			ob.level=visibleNameSpaces;
			return ob.n;
		}	
		
		NameSpaceSymbEntry ne=new NameSpaceSymbEntry(uri,n,true);
		ne.level=nameSpaces;
		ne.rendered=true;
		symb.put(prefix, ne);
		if (ob != null) {
			Object obs[] = {prefix, ob};
			ne.lastrendered=ob.lastrendered;
			
			if ((ob.lastrendered!=null)&& (ob.lastrendered.equals(uri))) {
				ne.rendered=true;
			}
			level.add(obs);
		} else {
			level.add(prefix);
		}
		current++;		
		return ne.n;
	}
}
//The null xmlns definiton.
protected Attr nullNode;
/**
 * @return Returns the _includeComments.
 */
public boolean is_includeComments() {
	return _includeComments;
}
/**
 * @param comments The _includeComments to set.
 */
public void set_includeComments(boolean comments) {
	_includeComments = comments;
}
}
