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
 * DSIGTransformXPath := Class that holds XPath transforms
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */


#include <xsec/dsig/DSIGTransformXPath.hpp>
#include <xsec/transformers/TXFMXPath.hpp>
#include <xsec/framework/XSECException.hpp>
#include <xsec/transformers/TXFMC14n.hpp>
#include <xsec/transformers/TXFMChain.hpp>
#include <xsec/framework/XSECError.hpp>
#include <xsec/dsig/DSIGSignature.hpp>

#include <xercesc/util/Janitor.hpp>

XSEC_USING_XERCES(Janitor);

// --------------------------------------------------------------------------------
//           Constructors and Destructors
// --------------------------------------------------------------------------------

DSIGTransformXPath::DSIGTransformXPath(DSIGSignature *sig, DOMNode * node) :
DSIGTransform(sig, node) {

	mp_exprTextNode = NULL;
	mp_xpathNode = NULL;
	mp_NSMap = NULL;
	m_expr = "";

}
	

DSIGTransformXPath::DSIGTransformXPath(DSIGSignature *sig) :
DSIGTransform(sig) {

	mp_exprTextNode = NULL;
	mp_xpathNode = NULL;
	mp_NSMap = NULL;
	m_expr = "";
}
		  
DSIGTransformXPath::~DSIGTransformXPath() {};

// --------------------------------------------------------------------------------
//           Interface Methods
// --------------------------------------------------------------------------------
	
transformType DSIGTransformXPath::getTransformType() {

	return TRANSFORM_XPATH;

}

void DSIGTransformXPath::appendTransformer(TXFMChain * input) {

#ifdef XSEC_NO_XPATH

	throw XSECException(XSECException::UnsupportedFunction,
		"XPath transforms are not supported in this compilation of the XSEC library");

#else

	TXFMXPath *x;
	// XPath transform
	XSECnew(x, TXFMXPath(mp_txfmNode->getOwnerDocument()));
	input->appendTxfm(x);

	// These can throw, but the TXFMXPath is now owned by the chain, so will
	// be cleaned up down the calling stack.

	x->setNameSpace(mp_NSMap);
	x->evaluateExpr(mp_txfmNode, m_expr);
	
#endif /* NO_XPATH */

}

DOMElement * DSIGTransformXPath::createBlankTransform(DOMDocument * parentDoc) {

	safeBuffer str;
	const XMLCh * prefix;
	DOMElement *ret;
	DOMDocument *doc = mp_parentSignature->getParentDocument();

	prefix = mp_parentSignature->getDSIGNSPrefix();
	
	// Create the transform node
	makeQName(str, prefix, "Transform");
	ret = doc->createElementNS(DSIGConstants::s_unicodeStrURIDSIG, str.rawXMLChBuffer());
	ret->setAttribute(DSIGConstants::s_unicodeStrAlgorithm, DSIGConstants::s_unicodeStrURIXPATH);
	
	// Create the XPath element
	
	makeQName(str, prefix, "XPath");
	mp_xpathNode = doc->createElementNS(DSIGConstants::s_unicodeStrURIDSIG, str.rawXMLChBuffer());
	mp_exprTextNode = doc->createTextNode(MAKE_UNICODE_STRING(""));
	ret->appendChild(mp_xpathNode);
	mp_xpathNode->appendChild(mp_exprTextNode);

	mp_txfmNode = ret;

	return ret;

}

void DSIGTransformXPath::load(void) {

	// Find the XPath expression
	
	mp_xpathNode = mp_txfmNode->getFirstChild();

	while (mp_xpathNode != 0 && 
		mp_xpathNode->getNodeType() != DOMNode::ELEMENT_NODE && !strEquals(mp_xpathNode->getNodeName(), "XPath"))
		mp_xpathNode = mp_xpathNode->getNextSibling();
	
	if (mp_xpathNode == 0) {

		throw XSECException(XSECException::ExpectedDSIGChildNotFound,
			"Expected <XPath> Node in DSIGTransformXPath::load");
	}
	
	else {
		
		// Check for attributes - in particular any namespaces

		mp_NSMap = mp_xpathNode->getAttributes();
		
		// Find the text node
		mp_exprTextNode = findFirstChildOfType(mp_xpathNode, DOMNode::TEXT_NODE);

		if (mp_exprTextNode == NULL) {
			throw XSECException(XSECException::ExpectedDSIGChildNotFound,
				"Expected Text Node in beneath <XPath> in DSIGTransformXPath::load");
		}

		// Gather the text
		safeBuffer exprSB;

		gatherChildrenText(mp_xpathNode, exprSB);

		m_expr << (*(mp_parentSignature->getSBFormatter()) << exprSB.rawXMLChBuffer());

		//m_expr << (*(mp_parentSignature->getSBFormatter()) << mp_exprTextNode->getNodeValue());
				
	}

}


// --------------------------------------------------------------------------------
//           XPath Transform Specific Methods
// --------------------------------------------------------------------------------

void DSIGTransformXPath::setExpression(const char * expr) {

	mp_exprTextNode->setNodeValue(MAKE_UNICODE_STRING(expr));

	m_expr.sbStrcpyIn((char *) expr);

}



const char * DSIGTransformXPath::getExpression(void) {

	return m_expr.rawCharBuffer();

}


void DSIGTransformXPath::setNamespace(const char * prefix, const char * value) {

	safeBuffer str;

	str.sbStrcpyIn("xmlns:");
	str.sbStrcatIn((char *) prefix);

	DOMElement *x;

	x = static_cast <DOMElement *> (mp_xpathNode);

//	if (x == NULL) {
//
//		throw XSECException(XSECException::TransformError,
//			"Found a non ELEMENT node as the XPath node in DSIGTransformXPath");
//	}

	x->setAttributeNS(DSIGConstants::s_unicodeStrURIXMLNS,
		str.sbStrToXMLCh(),
		MAKE_UNICODE_STRING(value));

	mp_NSMap = mp_xpathNode->getAttributes();


}

void DSIGTransformXPath::deleteNamespace(const char * prefix) {

	DOMElement *x;

	x = static_cast <DOMElement *> (mp_xpathNode);

//	if (x == NULL) {
//
//		throw XSECException(XSECException::TransformError,
//			"Found a non ELEMENT node as the XPath node in DSIGTransformXPath");
//	}

	x->removeAttributeNS(DSIGConstants::s_unicodeStrURIXMLNS,
		MAKE_UNICODE_STRING(prefix));

}

