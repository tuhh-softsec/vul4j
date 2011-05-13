/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.xml.security.c14n.implementations;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.helper.C14nHelper;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.params.InclusiveNamespaces;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

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
 * @see <a href="http://www.w3.org/TR/2002/REC-xml-exc-c14n-20020718/ Exclusive#">
 *          XML Canonicalization, Version 1.0</a>
 */
public abstract class Canonicalizer20010315Excl extends CanonicalizerBase {

    private static final String XML_LANG_URI = Constants.XML_LANG_SPACE_SpecNS;
    private static final String XMLNS_URI = Constants.NamespaceSpecNS;

    /**
      * This Set contains the names (Strings like "xmlns" or "xmlns:foo") of
      * the inclusive namespaces.
      */
    private SortedSet<String> inclusiveNSSet = new TreeSet<String>();

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
     * @inheritDoc
     * @param rootNode
     * 
     * @throws CanonicalizationException
     */
    public byte[] engineCanonicalizeSubTree(Node rootNode)
        throws CanonicalizationException {
        return engineCanonicalizeSubTree(rootNode, "", null);
    }

    /**
     * Method engineCanonicalizeSubTree
     *  @inheritDoc
     * @param rootNode
     * @param inclusiveNamespaces
     * 
     * @throws CanonicalizationException
     */
    public byte[] engineCanonicalizeSubTree(
        Node rootNode, String inclusiveNamespaces
    ) throws CanonicalizationException {
        return engineCanonicalizeSubTree(rootNode, inclusiveNamespaces, null);
    }

    /**
     * Method engineCanonicalizeSubTree  
     * @param rootNode
     * @param inclusiveNamespaces   
     * @param excl A element to exclude from the c14n process. 
     * @return the rootNode c14n.
     * @throws CanonicalizationException
     */
    public byte[] engineCanonicalizeSubTree(
        Node rootNode, String inclusiveNamespaces, Node excl
    ) throws CanonicalizationException{
        inclusiveNSSet = InclusiveNamespaces.prefixStr2Set(inclusiveNamespaces);
        return super.engineCanonicalizeSubTree(rootNode, excl);
    }

    /**
     * 
     * @param rootNode
     * @param inclusiveNamespaces
     * @return the rootNode c14n.
     * @throws CanonicalizationException
     */
    public byte[] engineCanonicalize(
        XMLSignatureInput rootNode, String inclusiveNamespaces
    ) throws CanonicalizationException {
        inclusiveNSSet = InclusiveNamespaces.prefixStr2Set(inclusiveNamespaces);
        return super.engineCanonicalize(rootNode);
    }
 
    /**
     * Method handleAttributesSubtree
     * @inheritDoc
     * @param E
     * @throws CanonicalizationException
     */
    protected Iterator<Attr> handleAttributesSubtree(Element E, NameSpaceSymbTable ns)
        throws CanonicalizationException {
        // result will contain the attrs which have to be output
        SortedSet<Attr> result = new TreeSet<Attr>(COMPARE);
        NamedNodeMap attrs = null;

        int attrsLength = 0;
        if (E.hasAttributes()) {
            attrs = E.getAttributes();
            attrsLength = attrs.getLength();
        }
        // The prefix visibly utilized(in the attribute or in the name) in 
        // the element
        SortedSet<String> visiblyUtilized = new TreeSet<String>(inclusiveNSSet);

        for (int i = 0; i < attrsLength; i++) {
            Attr N = (Attr) attrs.item(i);

            if (!XMLNS_URI.equals(N.getNamespaceURI())) {
                // Not a namespace definition.
                // The Element is output element, add the prefix (if used) to 
                // visibyUtilized
                String prefix = N.getPrefix();
                if (prefix != null && (!prefix.equals(XML) && !prefix.equals(XMLNS))) {
                    visiblyUtilized.add(prefix);
                }					
                // Add to the result.
                result.add(N);				
                continue;
            }
            String NName = N.getLocalName();
            String NNodeValue = N.getNodeValue();
            if (XML.equals(NName) && XML_LANG_URI.equals(NNodeValue)) {
                // The default mapping for xml must not be output.
                continue;
            }

            if (ns.addMapping(NName, NNodeValue, N) 
                && C14nHelper.namespaceIsRelative(NNodeValue)) {
                // New definition check if it is relative.
                Object exArgs[] = {E.getTagName(), NName, N.getNodeValue()};
                throw new CanonicalizationException(
                    "c14n.Canonicalizer.RelativeNamespace", exArgs);
            }
        }		
        String prefix;
        if (E.getNamespaceURI() != null) {
            prefix = E.getPrefix();
            if (prefix == null || prefix.length() == 0) {
                prefix = XMLNS;
            }
        } else {
            prefix = XMLNS;
        }
        visiblyUtilized.add(prefix);

        for (String s : visiblyUtilized) {
            Attr key = ns.getMapping(s);
            if (key != null) {
                result.add(key);
            }
        }

        return result.iterator(); 		
    }

    /**
     * Method engineCanonicalizeXPathNodeSet
     * @inheritDoc
     * @param xpathNodeSet
     * @param inclusiveNamespaces
     * @throws CanonicalizationException
     */
    public byte[] engineCanonicalizeXPathNodeSet(
        Set<Node> xpathNodeSet, String inclusiveNamespaces
    ) throws CanonicalizationException {
        inclusiveNSSet = InclusiveNamespaces.prefixStr2Set(inclusiveNamespaces);
        return super.engineCanonicalizeXPathNodeSet(xpathNodeSet);
    }
        
    /**
     * @inheritDoc
     * @param E
     * @throws CanonicalizationException
     */
    protected final Iterator<Attr> handleAttributes(Element E, NameSpaceSymbTable ns)
        throws CanonicalizationException {
        // result will contain the attrs which have to be output
        SortedSet<Attr> result = new TreeSet<Attr>(COMPARE);
        NamedNodeMap attrs = null;
        int attrsLength = 0;
        if (E.hasAttributes()) {
            attrs = E.getAttributes();           
            attrsLength = attrs.getLength();
        }
        // The prefix visibly utilized (in the attribute or in the name) in 
        // the element
        Set<String> visiblyUtilized = null;
        // It's the output selected.
        boolean isOutputElement = isVisibleDO(E, ns.getLevel()) == 1;
        if (isOutputElement) {
            visiblyUtilized = new TreeSet<String>(inclusiveNSSet);
        }

        for (int i = 0; i < attrsLength; i++) {
            Attr N = (Attr) attrs.item(i);

            if (!XMLNS_URI.equals(N.getNamespaceURI())) {
                if (!isVisible(N)) {
                    // The node is not in the nodeset(if there is a nodeset)
                    continue;
                }
                // Not a namespace definition.
                if (isOutputElement) {
                    // The Element is output element, add the prefix (if used) 
                    // to visibyUtilized
                    String prefix = N.getPrefix();
                    if (prefix != null && (!prefix.equals(XML) && !prefix.equals(XMLNS))) {
                        visiblyUtilized.add(prefix);
                    }					
                    // Add to the result.
                    result.add(N);
                }
                continue;
            }
            String NName = N.getLocalName();
            if (isOutputElement && !isVisible(N) && !XMLNS.equals(NName)) {
                ns.removeMappingIfNotRender(NName);
                continue;
            }
            String NNodeValue = N.getNodeValue();

            if (!isOutputElement && isVisible(N) 
                && inclusiveNSSet.contains(NName) 
                && !ns.removeMappingIfRender(NName)) {
                Node n = ns.addMappingAndRender(NName, NNodeValue, N);
                if (n != null) {
                    result.add((Attr)n);
                    if (C14nHelper.namespaceIsRelative(N)) {
                        Object exArgs[] = 
                            { E.getTagName(), NName, N.getNodeValue() };
                        throw new CanonicalizationException(
                            "c14n.Canonicalizer.RelativeNamespace", exArgs);
                    }
                }
            }

            if (ns.addMapping(NName, NNodeValue, N)
                && C14nHelper.namespaceIsRelative(NNodeValue)) {
                // New definition check if it is relative
                Object exArgs[] = 
                    { E.getTagName(), NName, N.getNodeValue() };
                throw new CanonicalizationException(
                    "c14n.Canonicalizer.RelativeNamespace", exArgs);
            }
        }

        if (isOutputElement) {	               
            // The element is visible, handle the xmlns definition    
            Attr xmlns = E.getAttributeNodeNS(XMLNS_URI, XMLNS);
            if (xmlns != null && !isVisible(xmlns)) {
                // There is a definition but the xmlns is not selected by the 
                // xpath. then xmlns=""
                ns.addMapping(XMLNS, "", nullNode);
            }

            if (E.getNamespaceURI() != null) {
                String prefix = E.getPrefix();
                if (prefix == null || prefix.length() == 0) {
                    visiblyUtilized.add(XMLNS);
                } else {
                    visiblyUtilized.add(prefix);
                }
            } else {
                visiblyUtilized.add(XMLNS);
            }									
            for (String s : visiblyUtilized) {
                Attr key = ns.getMapping(s);
                if (key != null) {
                    result.add(key);
                }
            }
        } 

        return result.iterator(); 
    }

    protected void circumventBugIfNeeded(XMLSignatureInput input) 
        throws CanonicalizationException, ParserConfigurationException, 
               IOException, SAXException {
        if (!input.isNeedsToBeExpanded() || inclusiveNSSet.isEmpty()) {
            return;
        }
        Document doc = null;
        if (input.getSubNode() != null) {
            doc = XMLUtils.getOwnerDocument(input.getSubNode());
        } else {
            doc = XMLUtils.getOwnerDocument(input.getNodeSet());
        }
        XMLUtils.circumventBug2650(doc);
    }
}
