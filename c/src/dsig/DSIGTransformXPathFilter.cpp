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
 * DSIGTransformXPathFilter := Class that performs XPath Filter 
 *                             transforms
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */

#include <xsec/dsig/DSIGTransformXPathFilter.hpp>
#include <xsec/dsig/DSIGXPathFilterExpr.hpp>
#include <xsec/dsig/DSIGSignature.hpp>
#include <xsec/framework/XSECError.hpp>
#include <xsec/transformers/TXFMXPathFilter.hpp>
#include <xsec/transformers/TXFMChain.hpp>

#include <xercesc/dom/DOMNode.hpp>

XERCES_CPP_NAMESPACE_USE

// --------------------------------------------------------------------------------
//           Construct/Destruct
// --------------------------------------------------------------------------------

DSIGTransformXPathFilter::DSIGTransformXPathFilter(DSIGSignature *sig, DOMNode * node) :
DSIGTransform(sig, node),
m_loaded(false) {


}


DSIGTransformXPathFilter::DSIGTransformXPathFilter(DSIGSignature *sig) :
DSIGTransform(sig),
m_loaded(false) {

}
	  
	  
DSIGTransformXPathFilter::~DSIGTransformXPathFilter() {

	exprVectorType::iterator i;

	for (i = m_exprs.begin(); i < m_exprs.end(); ++i) {

		delete (*i);

	}

}

transformType DSIGTransformXPathFilter::getTransformType() {

	return TRANSFORM_XPATH_FILTER;

}


void DSIGTransformXPathFilter::appendTransformer(TXFMChain * input) {

	if (m_loaded == false) {

		throw XSECException(XSECException::XPathFilterError,
			"DSIGTransformXPathFilter::appendTransform - load not yet called");

	}

#ifdef XSEC_NO_XPATH

	throw XSECException(XSECException::UnsupportedFunction,
		"XPath transforms are not supported in this compilation of the XSEC library");

#else

	TXFMXPathFilter *xpf;
	// XPath transform
	XSECnew(xpf, TXFMXPathFilter(mp_txfmNode->getOwnerDocument()));
	input->appendTxfm(xpf);

	// These can throw, but the TXFMXPathFilter is now owned by the chain, so will
	// be cleaned up down the calling stack.

	xpf->evaluateExprs(&m_exprs);
	
#endif /* NO_XPATH */

}

// --------------------------------------------------------------------------------
//           Create a blank transform
// --------------------------------------------------------------------------------


DOMElement * DSIGTransformXPathFilter::createBlankTransform(DOMDocument * parentDoc) {

	safeBuffer str;
	const XMLCh * prefix;
	DOMElement *ret;
	DOMDocument *doc = mp_parentSignature->getParentDocument();

	prefix = mp_parentSignature->getDSIGNSPrefix();
	
	// Create the transform node
	makeQName(str, prefix, "Transform");
	ret = doc->createElementNS(DSIGConstants::s_unicodeStrURIDSIG, str.rawXMLChBuffer());
	ret->setAttribute(DSIGConstants::s_unicodeStrAlgorithm, DSIGConstants::s_unicodeStrURIXPF);
	
	mp_txfmNode = ret;

	m_loaded = true;

	return ret;


}

DSIGXPathFilterExpr * DSIGTransformXPathFilter::appendFilter(xpathFilterType filterType,
											const XMLCh * filterExpr) {

	DSIGXPathFilterExpr * e;

	XSECnew(e, DSIGXPathFilterExpr(mp_parentSignature));

	DOMNode * elt = e->setFilter(filterType, filterExpr);
	m_exprs.push_back(e);

	mp_txfmNode->appendChild(elt);
	mp_txfmNode->appendChild(mp_parentSignature->getParentDocument()->createTextNode(DSIGConstants::s_unicodeStrNL));

	return e;

}

// --------------------------------------------------------------------------------
//           Load from XML
// --------------------------------------------------------------------------------

void DSIGTransformXPathFilter::load(void) {

	if (mp_txfmNode == NULL) {

		throw XSECException(XSECException::XPathFilterError,
			"DSIGTransformXPathFilter::load called on NULL node");
		
	}

	// Very simple - go through each child.  If it's an XPath child
	// Create the DSIGXPathFilterExpr object

	DOMNode * n = mp_txfmNode->getFirstChild();

	while (n != NULL) {

		if (n->getNodeType() == DOMNode::ELEMENT_NODE &&
			strEquals(getXPFLocalName(n), "XPath")) {

			DSIGXPathFilterExpr * xpf;
			XSECnew(xpf, DSIGXPathFilterExpr(mp_parentSignature, n));

			// Add it to the vector prior to load to ensure deleted if
			// anything throws an exception

			m_exprs.push_back(xpf);

			xpf->load();

		}

		n = n->getNextSibling();

	}

	m_loaded = true;

}

// --------------------------------------------------------------------------------
//           Retrieve expression information
// --------------------------------------------------------------------------------

unsigned int DSIGTransformXPathFilter::getExprNum(void) {

	return m_exprs.size();

}


DSIGXPathFilterExpr * DSIGTransformXPathFilter::expr(unsigned int n) {

	if (n > m_exprs.size())
		return NULL;

	return m_exprs[n];

}

