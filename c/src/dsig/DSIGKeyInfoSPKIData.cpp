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
 * DSIGKeyInfoSPKIData := SPKI Information
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */

#include <xsec/dsig/DSIGKeyInfoSPKIData.hpp>
#include <xsec/framework/XSECError.hpp>
#include <xsec/dsig/DSIGSignature.hpp>

#include <xercesc/dom/DOM.hpp>

XERCES_CPP_NAMESPACE_USE

// --------------------------------------------------------------------------------
//           Constructors and Destructors
// --------------------------------------------------------------------------------

DSIGKeyInfoSPKIData::DSIGKeyInfoSPKIData(DSIGSignature * sig, DOMNode *nameNode) : 
DSIGKeyInfo(sig) {

	mp_keyInfoDOMNode = nameNode;

}

DSIGKeyInfoSPKIData::DSIGKeyInfoSPKIData(DSIGSignature * sig) :
DSIGKeyInfo(sig) {

	mp_keyInfoDOMNode = NULL;

}

DSIGKeyInfoSPKIData::~DSIGKeyInfoSPKIData() {

	sexpVectorType::iterator i;

	for (i = m_sexpList.begin(); i < m_sexpList.end(); ++i) {

		delete *i;

	}
}

// --------------------------------------------------------------------------------
//           Load and get
// --------------------------------------------------------------------------------

void DSIGKeyInfoSPKIData::load(void) {

	// Assuming we have a valid DOM_Node to start with, load the signing key so that it can
	// be used later on

	if (mp_keyInfoDOMNode == NULL) {

		// Attempt to load an empty signature element
		throw XSECException(XSECException::ExpectedDSIGChildNotFound,
			"DSIGKeyInfoSPKIData::load called without node being set");

	}

	if (!strEquals(getDSIGLocalName(mp_keyInfoDOMNode), "SPKIData")) {

		throw XSECException(XSECException::ExpectedDSIGChildNotFound,
			"Expected an <SPKIData> node");

	}

	// Now find the SPKISexp nodes
	DOMNode * tmpElt = findFirstChildOfType(mp_keyInfoDOMNode, DOMNode::ELEMENT_NODE);

	while (tmpElt != NULL && strEquals(getDSIGLocalName(tmpElt), "SPKISexp")) {

		DOMNode * txt = findFirstChildOfType(tmpElt, DOMNode::TEXT_NODE);

		if (txt == NULL) {

			throw XSECException(XSECException::ExpectedDSIGChildNotFound,
				"Expected text node child of <SPKISexp>");

		}

		SexpNode * s;
		XSECnew(s, SexpNode);

		m_sexpList.push_back(s);

		s->mp_expr = txt->getNodeValue();
		s->mp_exprTextNode = txt;

		// Find next SPKISexp
		
		do {

			tmpElt = tmpElt->getNextSibling();

		} while (tmpElt != NULL && tmpElt->getNodeType() != DOMNode::ELEMENT_NODE);

	}

	/*
	 * Note that it is not necessarily an error if non SPKISexp nodes are found
	 * after the SPKISexp nodes.
	 */

}

unsigned int DSIGKeyInfoSPKIData::getSexpSize(void) {

	return m_sexpList.size();

}

const XMLCh * DSIGKeyInfoSPKIData::getSexp(unsigned int index) {

	if (index < 0 || index >= m_sexpList.size()) {

		throw XSECException(XSECException::KeyInfoError,
			"DSIGKeyInfoSPKIData::getSexp - index out of range");

	}

	return m_sexpList[index]->mp_expr;

}

// --------------------------------------------------------------------------------
//           Create and set
// --------------------------------------------------------------------------------

DOMElement * DSIGKeyInfoSPKIData::createBlankSPKIData(const XMLCh * Sexp) {

	// Create the DOM Structure

	safeBuffer str;
	DOMDocument *doc = mp_parentSignature->getParentDocument();
	const XMLCh * prefix = mp_parentSignature->getDSIGNSPrefix();

	makeQName(str, prefix, "SPKIData");

	DOMElement * ret = doc->createElementNS(DSIGConstants::s_unicodeStrURIDSIG, str.rawXMLChBuffer());
	mp_keyInfoDOMNode = ret;
	ret->appendChild(doc->createTextNode(DSIGConstants::s_unicodeStrNL));

	appendSexp(Sexp);

	return ret;

}

void DSIGKeyInfoSPKIData::appendSexp(const XMLCh * Sexp) {

	// First create the new element in the list
	SexpNode *s;
	XSECnew(s, SexpNode);

	m_sexpList.push_back(s);

	safeBuffer str;
	DOMDocument *doc = mp_parentSignature->getParentDocument();
	const XMLCh * prefix = mp_parentSignature->getDSIGNSPrefix();

	makeQName(str, prefix, "SPKISexp");

	DOMNode *tmpElt = doc->createElementNS(DSIGConstants::s_unicodeStrURIDSIG, str.rawXMLChBuffer());
	
	s->mp_exprTextNode = doc->createTextNode(Sexp);
	s->mp_expr = s->mp_exprTextNode->getNodeValue();

	tmpElt->appendChild(s->mp_exprTextNode);

	mp_keyInfoDOMNode->appendChild(tmpElt);
	mp_keyInfoDOMNode->appendChild(doc->createTextNode(DSIGConstants::s_unicodeStrNL));

}

