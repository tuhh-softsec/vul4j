/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights 
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

/*
 * XSEC
 *
 * XSECNameSpaceExander := Class for expanding out a document's name space axis
 *							and then shrinking again
 *
 * Author(s): Berin Lautenbach
 *
 * $ID$
 *
 * $LOG$
 *
 */

// XSEC Includes
#include <xsec/utils/XSECNameSpaceExpander.hpp>
#include <xsec/dsig/DSIGConstants.hpp>
#include <xsec/framework/XSECError.hpp>
#include <xsec/utils/XSECDOMUtils.hpp>

XSEC_USING_XERCES(DOMNamedNodeMap);

XSECNameSpaceExpander::XSECNameSpaceExpander(DOMDocument *d) {

	mp_doc = d;
	mp_fragment = d->getDocumentElement();
	XSECnew(mp_formatter, XSECSafeBufferFormatter("UTF-8",XMLFormatter::NoEscapes, 
												XMLFormatter::UnRep_CharRef));

	m_expanded = false;
	
}

XSECNameSpaceExpander::XSECNameSpaceExpander(DOMElement *f) {

	mp_doc = NULL;
	mp_fragment = f;
	XSECnew(mp_formatter, XSECSafeBufferFormatter("UTF-8",XMLFormatter::NoEscapes, 
												XMLFormatter::UnRep_CharRef));

	m_expanded = false;
	
}

XSECNameSpaceExpander::~XSECNameSpaceExpander() {

	if (mp_formatter != NULL)
		delete mp_formatter;

}

void XSECNameSpaceExpander::recurse(DOMElement *n) {

	// Recursively go down the tree adding namespaces

	DOMNode *p = n->getParentNode();
	if (p->getNodeType() != DOMNode::ELEMENT_NODE)
		return;

	DOMNamedNodeMap *pmap = p->getAttributes();
	int psize = pmap->getLength();

	DOMNamedNodeMap *nmap = n->getAttributes();

	safeBuffer pname, pURI, nURI;
	DOMNode *finder;

	XSECNameSpaceEntry * tmpEnt;

	for (int i = 0; i < psize; i++) {

		// Run through each parent node to find namespaces
		pname << (*mp_formatter << pmap->item(i)->getNodeName());

		// See if this is an xmlns node
		
		if (pname.sbStrncmp("xmlns", 5) == 0) {

			// It is - see if it already exists
			finder = nmap->getNamedItem(pname.sbStrToXMLCh());
			if (finder == 0) {

				// Need to add
				n->setAttributeNS(DSIGConstants::s_unicodeStrURIXMLNS, 
					pmap->item(i)->getNodeName(),
					pmap->item(i)->getNodeValue());

				// Add it to the list so it can be removed later
				XSECnew(tmpEnt, XSECNameSpaceEntry);
				tmpEnt->m_name.sbStrcpyIn(pname);
				tmpEnt->mp_node = n;
				tmpEnt->mp_att = nmap->getNamedItem(pname.sbStrToXMLCh());
				m_lst.push_back(tmpEnt);

			}

		}

	}

	// Do the children

	DOMNode *c;

	c = n->getFirstChild();

	while (c != NULL) {
		if (c->getNodeType() == DOMNode::ELEMENT_NODE)
			recurse((DOMElement *) c);
		c = c->getNextSibling();
	}

}

int attNodeCount(DOMElement * d) {

	int ret;

	ret = d->getAttributes()->getLength();

	DOMNode *c;

	c = d->getFirstChild();

	while (c != NULL) {

		if (c->getNodeType() == DOMNode::ELEMENT_NODE)
			ret += attNodeCount((DOMElement *) c);

		c = c->getNextSibling();

	}

	return ret;

}

void XSECNameSpaceExpander::expandNameSpaces(void) {

	if (m_expanded)
		return;				// Don't do this twice!

	DOMElement	*docElt;		// The document element - do not expand it's namespaces
	
	docElt = mp_fragment; //mp_doc->getDocumentElement();
	int count = attNodeCount(docElt);

	DOMNode *c;

	c = docElt->getFirstChild();

	while (c != NULL) {
		if (c->getNodeType() == DOMNode::ELEMENT_NODE)
			recurse((DOMElement *) c);
		c = c->getNextSibling();
	}

	m_expanded = true;

	count = attNodeCount(docElt);

}


void XSECNameSpaceExpander::deleteAddedNamespaces(void) {

	NameSpaceEntryListVectorType::size_type size = m_lst.size();
	XSECNameSpaceEntry *e;

	DOMElement *docElt = mp_fragment; //mp_doc->getDocumentElement();
	int 	count = attNodeCount(docElt);

	NameSpaceEntryListVectorType::size_type i;

	for (i = 0; i < size; ++i) {

		// Delete the element attribute, and then this node
		e = m_lst[i];
		if (e->m_name[5] == ':')
			e->mp_node->removeAttributeNS(DSIGConstants::s_unicodeStrURIXMLNS,
										MAKE_UNICODE_STRING((char *) &((e->m_name.rawBuffer())[6])));
		else
			e->mp_node->removeAttributeNS(DSIGConstants::s_unicodeStrURIXMLNS,
										MAKE_UNICODE_STRING((char *) e->m_name.rawBuffer()));

		// Delete the entry
		delete e;

	}

	// Now done - empty everything
	m_lst.clear();
	m_expanded = false;
	count = attNodeCount(docElt);

}

bool XSECNameSpaceExpander::nodeWasAdded(DOMNode *n) {

	NameSpaceEntryListVectorType::size_type size = m_lst.size();
	XSECNameSpaceEntry *e;

	NameSpaceEntryListVectorType::size_type i;
	for (i = 0; i < size; ++i) {

		// Delete the element attribute, and then this node
		e = m_lst[i];
		
		if (e->mp_att == n)
			return true;

	}

	return false;

}
