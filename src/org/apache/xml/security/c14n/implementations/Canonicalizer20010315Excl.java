
/*
 * Copyright 1999-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *  
 */
package org.apache.xml.security.c14n.implementations;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.c14n.helper.C14nHelper;
import org.apache.xml.security.transforms.params.InclusiveNamespaces;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
/**
 * Implements &quot; <A
 * HREF="http://www.w3.org/TR/2002/REC-xml-exc-c14n-20020718/">Exclusive XML
 * Canonicalization, Version 1.0 </A>&quot; <BR />
 * Credits: During restructuring of the Canonicalizer framework, Ren??
 * Kollmorgen from Software AG submitted an implementation of ExclC14n which
 * fitted into the old architecture and which based heavily on my old (and slow)
 * implementation of "Canonical XML". A big "thank you" to Ren?? for this.
 * <BR />
 * <i>THIS </i> implementation is a complete rewrite of the algorithm.
 * 
 * @author Christian Geuer-Pollmann <geuerp@apache.org>
 * @version $Revision$
 * @see <A
 *          HREF="http://www.w3.org/TR/2002/REC-xml-exc-c14n-20020718/">"Exclusive
 *          XML Canonicalization, Version 1.0" </A>
 */
public abstract class Canonicalizer20010315Excl extends CanonicalizerBase {
	/**
	 * Constructor Canonicalizer20010315Excl
	 * 
	 * @param includeComments
	 */
	public Canonicalizer20010315Excl(boolean includeComments) {
		super(includeComments);
	}

	/**
	 * Method engineCanonicalizeSubTree
	 * 
	 * @param rootNode
	 * 
	 * @throws CanonicalizationException
	 */
	public byte[] engineCanonicalizeSubTree(Node rootNode)
			throws CanonicalizationException {
		return this.engineCanonicalizeSubTree(rootNode, "",null);
	}
	/**
	 * Method engineCanonicalizeSubTree
	 * 
	 * @param rootNode
	 * @param inclusiveNamespaces
	 * 
	 * @throws CanonicalizationException
	 */
	public byte[] engineCanonicalizeSubTree(Node rootNode,
			String inclusiveNamespaces) throws CanonicalizationException {
		return this.engineCanonicalizeSubTree(rootNode, inclusiveNamespaces,null);
	}
	/**
	 * Method engineCanonicalizeSubTree
	 * 
	 * @param rootNode
	 * @param inclusiveNamespaces
	 * @param excl A element to exclude from the c14n process.
	 * 
	 * @throws CanonicalizationException
	 */
	public byte[] engineCanonicalizeSubTree(Node rootNode,
			String inclusiveNamespaces,Node excl) throws CanonicalizationException {
		this._excludeNode=excl;
		this._rootNodeOfC14n = rootNode;
		this._doc = XMLUtils.getOwnerDocument(this._rootNodeOfC14n);
		this._documentElement = this._doc.getDocumentElement();
		nullNode=
			_doc.createAttributeNS(Constants.NamespaceSpecNS,"xmlns");
		nullNode.setValue("");
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			this._writer = new OutputStreamWriter(baos, Canonicalizer.ENCODING);
			this._inclusiveNSSet = InclusiveNamespaces
					.prefixStr2Set(inclusiveNamespaces);			
			NameSpaceSymbTable ns= new NameSpaceSymbTable();
			if (rootNode instanceof Element) {
				this.getParentNameSpaces((Element)rootNode,ns);
			}
			canonicalizeSubTree(rootNode,ns);
			//this._writer.flush();
			this._writer.close();
			return baos.toByteArray();
		} catch (UnsupportedEncodingException ex) {
			throw new CanonicalizationException("empty", ex);
		} catch (IOException ex) {
			throw new CanonicalizationException("empty", ex);
		} finally {
			this._xpathNodeSet = null;
			this._inclusiveNSSet = null;
			this._rootNodeOfC14n = null;
			this._doc = null;
			this._documentElement = null;
			this._writer = null;
		}
	}
 
	/**
	 * Method handleAttributesSubtree
	 * 
	 * @param E
	 * @throws CanonicalizationException
	 */
	Object[] handleAttributesSubtree(Element E,NameSpaceSymbTable ns)
			throws CanonicalizationException {
		// System.out.println("During the traversal, I encountered " +
		// XMLUtils.getXPath(E));
		// result will contain the attrs which have to be outputted
		List result = new ArrayList();
		NamedNodeMap attrs = E.getAttributes();
		int attrsLength = attrs.getLength();
		//The prefix visibly utilized(in the attribute or in the name) in the element
		Set visiblyUtilized = new HashSet();
				
		//Is the xmlns defined in this node?
		boolean xmlnsDef=false;
		for (int i = 0; i < attrsLength; i++) {
			Attr N = (Attr) attrs.item(i);
			String NName=N.getName();
			String NNodeValue=N.getNodeValue();
						
			if (!Constants.NamespaceSpecNS.equals(N.getNamespaceURI())) {
				//Not a namespace definition.
				//The Element is output element, add his prefix(if used) to visibyUtilized
				String prefix = N.getPrefix();
				if ( (prefix != null) && (!prefix.equals("xml") && !prefix.equals("xmlns")) ) {
						visiblyUtilized.add("xmlns:" + prefix);
				}					
				//Add to the result.
				 result.add(N);				
				continue;
			}
			
			if (C14nHelper.namespaceIsRelative(NNodeValue)) {
				Object exArgs[] = {E.getTagName(), NName,
						N.getNodeValue()};
				throw new CanonicalizationException(
						"c14n.Canonicalizer.RelativeNamespace", exArgs);
			}
			
			if ("xmlns".equals(NName)) {				
				xmlnsDef=true;
			}
			ns.addMapping(NName, NNodeValue,N);
		}		
							
		if (E.getNamespaceURI() != null) {
			String prefix = E.getPrefix();
			if ((prefix == null) || (prefix.length() == 0)) {
				visiblyUtilized.add("xmlns");
			} else {
				visiblyUtilized.add("xmlns:" + prefix);
			}
		} else {
			visiblyUtilized.add("xmlns");
		}
									
		//This can be optimezed by I don't have time
		visiblyUtilized.addAll(this._inclusiveNSSet);
		Iterator it=visiblyUtilized.iterator();
		while (it.hasNext()) {
			String s=(String)it.next();									
			Attr key=ns.getMapping(s);
			if (key==null) {
				continue;
			}
			result.add(key);
		}
		
		return C14nHelper.sortAttributes(result.toArray()); 		
	}

	/**
	 * Method engineCanonicalizeXPathNodeSet
	 * 
	 * @param xpathNodeSet
	 * @param inclusiveNamespaces
	 * @throws CanonicalizationException
	 */
	public byte[] engineCanonicalizeXPathNodeSet(Set xpathNodeSet,
			String inclusiveNamespaces) throws CanonicalizationException {
		
		try {
			this._inclusiveNSSet = InclusiveNamespaces
					.prefixStr2Set(inclusiveNamespaces);
			return super.engineCanonicalizeXPathNodeSet(xpathNodeSet);
		} finally {
			this._inclusiveNSSet = null;
		}
	}

    public byte[] engineCanonicalizeXPathNodeSet(Set xpathNodeSet
            ) throws CanonicalizationException {
        return engineCanonicalizeXPathNodeSet(xpathNodeSet,"");
    }
          	
	/**
	 * @param E
	 * @throws CanonicalizationException
	 */
	final Object[] handleAttributes(Element E, NameSpaceSymbTable ns)
			throws CanonicalizationException {
		// System.out.println("During the traversal, I encountered " +
		// XMLUtils.getXPath(E));
		// result will contain the attrs which have to be outputted
		List result = new ArrayList();
		NamedNodeMap attrs = E.getAttributes();
		int attrsLength = attrs.getLength();
		//The prefix visibly utilized(in the attribute or in the name) in the element
		Set visiblyUtilized =null;
		//It's the output selected.
		boolean isOutputElement = this._xpathNodeSet.contains(E);			
		if (isOutputElement) {
			visiblyUtilized =  new HashSet();
		}
		
		//Is the xmlns defined in this node?
		boolean xmlnsDef=false;
		for (int i = 0; i < attrsLength; i++) {
			Attr N = (Attr) attrs.item(i);
			String NName=N.getName();
			String NNodeValue=N.getNodeValue();
			if ( !this._xpathNodeSet.contains(N) )  {
				//The node is not in the nodeset(if there is a nodeset)
				continue;
			}			
						
			if (!Constants.NamespaceSpecNS.equals(N.getNamespaceURI())) {
				//Not a namespace definition.
				if (isOutputElement) {
					//The Element is output element, add his prefix(if used) to visibyUtilized
					String prefix = N.getPrefix();
					if ((prefix != null) && (!prefix.equals("xml") && !prefix.equals("xmlns")) ){ 
							visiblyUtilized.add("xmlns:" + prefix);
					}					
					//Add to the result.
				    result.add(N);
				}
				continue;
			}
			
			if (C14nHelper.namespaceIsRelative(NNodeValue)) {
				Object exArgs[] = {E.getTagName(), NName,
						N.getNodeValue()};
				throw new CanonicalizationException(
						"c14n.Canonicalizer.RelativeNamespace", exArgs);
			}
			
			if ("xmlns".equals(NName)) {				
				xmlnsDef=true;
			}
			ns.addMapping(NName, NNodeValue,N);
		}
		if (!xmlnsDef ) {
			ns.addMapping("xmlns","",nullNode);
		}
		
		if (isOutputElement) {			
			if (E.getNamespaceURI() != null) {
				String prefix = E.getPrefix();
				if ((prefix == null) || (prefix.length() == 0)) {
					visiblyUtilized.add("xmlns");
				} else {
					visiblyUtilized.add("xmlns:" + prefix);
				}
			} else {
				visiblyUtilized.add("xmlns");
			}									
			//This can be optimezed by I don't have time
			visiblyUtilized.addAll(this._inclusiveNSSet);
			Iterator it=visiblyUtilized.iterator();
			while (it.hasNext()) {
				String s=(String)it.next();										
				Attr key=ns.getMapping(s);
				if (key==null) {
					continue;
				}
				result.add(key);
			}
		} else /*if (_circunvented)*/ {			
			Iterator it=this._inclusiveNSSet.iterator();
			while (it.hasNext()) {
				String s=(String)it.next();				
				Attr key=ns.getMappingWithoutRendered(s);
				if (key==null) {
					continue;
				}
				result.add(key);								
			}
		}

		return C14nHelper.sortAttributes(result.toArray()); 
	}
}