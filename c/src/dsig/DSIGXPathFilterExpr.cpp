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
 * DSIGXPathFilterExpr := Class that holds an XPath Filter expression
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/dsig/DSIGXPathFilterExpr.hpp>
#include <xsec/dsig/DSIGSignature.hpp>
#include <xsec/framework/XSECException.hpp>
#include <xsec/framework/XSECEnv.hpp>


#include <xercesc/dom/DOMNode.hpp>
#include <xercesc/dom/DOMNamedNodeMap.hpp>
#include <xercesc/util/XMLUniDefs.hpp>


XERCES_CPP_NAMESPACE_USE

XMLCh filterStr[] = {

	chLatin_F,
	chLatin_i,
	chLatin_l,
	chLatin_t,
	chLatin_e,
	chLatin_r,
	chNull

};


// --------------------------------------------------------------------------------
//           Constructors and Destructors
// --------------------------------------------------------------------------------

DSIGXPathFilterExpr::DSIGXPathFilterExpr(const XSECEnv * env, DOMNode * node) :
mp_env(env),
mp_xpathFilterNode(node),
mp_NSMap(NULL) {

}

DSIGXPathFilterExpr::DSIGXPathFilterExpr(const XSECEnv * env) :
mp_env(env),
mp_xpathFilterNode(NULL),
mp_NSMap(NULL) {

}


DSIGXPathFilterExpr::~DSIGXPathFilterExpr() {

	// Nothing to do at the moment

}

// --------------------------------------------------------------------------------
//           Load existing DOM structure
// --------------------------------------------------------------------------------
	
void DSIGXPathFilterExpr::load(void) {

	// Find the XPath expression

	if (mp_xpathFilterNode == NULL ||
		!strEquals(getXPFLocalName(mp_xpathFilterNode), "XPath")) {

		throw XSECException(XSECException::ExpectedDSIGChildNotFound,
			"Expected <XPath> as first node in DSIGXPathFilterExpr::load");

	}

		
	// Check for attributes - in particular any namespaces

	mp_NSMap = mp_xpathFilterNode->getAttributes();

	// Find the filter type
	DOMNode * a;
	if (mp_NSMap == NULL ||
		((a = mp_NSMap->getNamedItem(filterStr)) == NULL)) {

		throw XSECException(XSECException::ExpectedDSIGChildNotFound,
			"Expected Filter attribute of <XPath> node in in DSIGXPathFilterExpr::load");

	}

	const XMLCh * f = a->getNodeValue();
	if (strEquals(f, "intersect")) {
		m_filterType = FILTER_INTERSECT;
	}
	else if (strEquals(f, "union")) {
		m_filterType = FILTER_UNION;
	}
	else if (strEquals(f, "subtract")) {
		m_filterType = FILTER_SUBTRACT;
	}
	else {

		throw XSECException(XSECException::ExpectedDSIGChildNotFound,
				"DSIGXPathFilterExpr::load Expected on of intersect, union or subtract as filter type");
	}

	// Find the text node
	mp_exprTextNode = findFirstChildOfType(mp_xpathFilterNode, DOMNode::TEXT_NODE);

	if (mp_exprTextNode == NULL) {
		throw XSECException(XSECException::ExpectedDSIGChildNotFound,
			"Expected Text Node in beneath <XPath> in DSIGXPathFilterExpr::load");
	}

	// Gather the text - hold it in UTF16 format
	gatherChildrenText(mp_xpathFilterNode, m_expr);

	m_loaded = true;

}

// --------------------------------------------------------------------------------
//           Create a new filter
// --------------------------------------------------------------------------------

DOMElement * DSIGXPathFilterExpr::setFilter(xpathFilterType filterType,
						const XMLCh * filterExpr) {

	if (m_loaded == true) {

		throw XSECException(XSECException::XPathFilterError,
			"DSIGXPathFilterExpr::setFilter - called when already loaded");

	}

	safeBuffer str;
	const XMLCh * prefix;
	DOMDocument *doc = mp_env->getParentDocument();
	DOMElement * xe;

	// Create the XPath element
	prefix = mp_env->getXPFNSPrefix();
	makeQName(str, prefix, "XPath");
	xe = doc->createElementNS(DSIGConstants::s_unicodeStrURIXPF, str.rawXMLChBuffer());
	mp_xpathFilterNode = xe;

	// Put in correct namespace
	prefix = mp_env->getXPFNSPrefix();

	// Set the namespace attribute
	if (prefix[0] == '\0') {
		str.sbTranscodeIn("xmlns");
	}
	else {
		str.sbTranscodeIn("xmlns:");
		str.sbXMLChCat(prefix);
	}

	xe->setAttributeNS(DSIGConstants::s_unicodeStrURIXMLNS, 
							str.rawXMLChBuffer(), 
							DSIGConstants::s_unicodeStrURIXPF);

	// Set the filter type
	m_filterType = filterType;

	switch (filterType) {

	case FILTER_INTERSECT :

		xe->setAttribute(MAKE_UNICODE_STRING("Filter"), MAKE_UNICODE_STRING("intersect"));
		break;

	case FILTER_SUBTRACT :

		xe->setAttribute(MAKE_UNICODE_STRING("Filter"), MAKE_UNICODE_STRING("subtract"));
		break;

	case FILTER_UNION :

		xe->setAttribute(MAKE_UNICODE_STRING("Filter"), MAKE_UNICODE_STRING("union"));
		break;

	default :

		mp_xpathFilterNode->release();
		throw XSECException(XSECException::XPathFilterError,
			"DSIGXPathFilterExpr::appendFilter - Unexpected Filter Type");

	}

	// Now add the actual filter

	mp_exprTextNode = doc->createTextNode(filterExpr);
	mp_xpathFilterNode->appendChild(mp_exprTextNode);

	mp_NSMap = mp_xpathFilterNode->getAttributes();

	m_expr.sbXMLChIn(filterExpr);
	m_loaded = true;
	return xe;

}


// --------------------------------------------------------------------------------
//           Find the type
// --------------------------------------------------------------------------------

xpathFilterType DSIGXPathFilterExpr::getFilterType(void) {

	if (m_loaded == false) {
		throw XSECException(XSECException::LoadEmptyXPathFilter,
			"DSIGXPathFilterExpr::Element node loaded");
	}

	return m_filterType;

}

// --------------------------------------------------------------------------------
//           Set and clear namespaces
// --------------------------------------------------------------------------------

void DSIGXPathFilterExpr::setNamespace(const XMLCh * prefix, const XMLCh * value) {

	if (mp_xpathFilterNode == NULL) {

		throw XSECException(XSECException::XPathFilterError,
			"DSIGXPathFilterExpr::setNamespace - load not called");

	}
	
	safeBuffer str;

	str.sbTranscodeIn("xmlns:");
	str.sbXMLChCat(prefix);

	DOMElement *x;

	x = static_cast <DOMElement *> (mp_xpathFilterNode);

	x->setAttributeNS(DSIGConstants::s_unicodeStrURIXMLNS,
		str.rawXMLChBuffer(),
		value);

	mp_NSMap = mp_xpathFilterNode->getAttributes();


}

void DSIGXPathFilterExpr::deleteNamespace(const XMLCh * prefix) {

	if (mp_xpathFilterNode == NULL) {

		throw XSECException(XSECException::XPathFilterError,
			"DSIGXPathFilterExpr::deleteNamespace - load not called");

	}

	DOMElement *x;

	x = static_cast <DOMElement *> (mp_xpathFilterNode);

	x->removeAttributeNS(DSIGConstants::s_unicodeStrURIXMLNS,
		prefix);

}



	/**
	 * \brief Add a new namespace to the list to be used
	 *
	 * Add a new namespace to the XPath Element.
	 *
	 * @param prefix NCName of the Namespace to set
	 * @param value The string with the URI to set
	 */

//	void setNamespace(const char * prefix, const char * value);

	/**
	 * \brief Get the list of namespaces.
	 *
	 * Returns the DOMNamedNodeMap of the attributes of the XPath transform
	 * node.  This <em>should</em> only contain namespaces.
	 *
	 * @returns A pointer to the NamedNodeMap
	 */

//	DOMNamedNodeMap * getNamespaces(void) {
//		return mp_NSMap;
//	}

	/**
	 * \brief Delete a namespace to the list to be used
	 *
	 * Delete a namespace from the XPath Element.
	 *
	 * @param prefix NCName of the Namespace to delete
	 * @throws XSECException if the NCName does not exist
	 *
	 */

//	void deleteNamespace(const char * prefix);

	//@}
	
