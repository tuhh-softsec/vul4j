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
 * XENCEncryptionMethod := Interface definition for EncryptionMethod element
 *
 * $Id$
 *
 */

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/framework/XSECError.hpp>
#include <xsec/utils/XSECDOMUtils.hpp>
#include <xsec/framework/XSECEnv.hpp>

#include "XENCEncryptionMethodImpl.hpp"

#include <xercesc/util/XMLUniDefs.hpp>
#include <xercesc/dom/DOM.hpp>

XERCES_CPP_NAMESPACE_USE

// --------------------------------------------------------------------------------
//			UNICODE Strings
// --------------------------------------------------------------------------------

static XMLCh s_EncryptionMethod[] = {

	chLatin_E,
	chLatin_n,
	chLatin_c,
	chLatin_r,
	chLatin_y,
	chLatin_p,
	chLatin_t,
	chLatin_i,
	chLatin_o,
	chLatin_n,
	chLatin_M,
	chLatin_e,
	chLatin_t,
	chLatin_h,
	chLatin_o,
	chLatin_d,
	chNull,
};

static XMLCh s_DigestMethod[] = {

	chLatin_D,
	chLatin_i,
	chLatin_g,
	chLatin_e,
	chLatin_s,
	chLatin_t,
	chLatin_M,
	chLatin_e,
	chLatin_t,
	chLatin_h,
	chLatin_o,
	chLatin_d,
	chNull
};

static XMLCh s_OAEPparams [] = {

	chLatin_O,
	chLatin_A,
	chLatin_E,
	chLatin_P,
	chLatin_p,
	chLatin_a,
	chLatin_r,
	chLatin_a,
	chLatin_m,
	chLatin_s,
	chNull
};

// --------------------------------------------------------------------------------
//			Constructors and Destructors
// --------------------------------------------------------------------------------

XENCEncryptionMethodImpl::XENCEncryptionMethodImpl(const XSECEnv * env) :
mp_env(env),
mp_encryptionMethodNode(NULL),
mp_algorithm(NULL),
mp_digestAlgorithmAttributeNode(NULL),
mp_oaepParamsTextNode(NULL) {

}

XENCEncryptionMethodImpl::XENCEncryptionMethodImpl(
		const XSECEnv * env, 
		DOMNode * node) :
mp_env(env),
mp_encryptionMethodNode(node),
mp_algorithm(NULL),
mp_digestAlgorithmAttributeNode(NULL),
mp_oaepParamsTextNode(NULL) {

}

XENCEncryptionMethodImpl::~XENCEncryptionMethodImpl() {

	if (mp_algorithm != NULL)
		delete[] mp_algorithm;

}


// --------------------------------------------------------------------------------
//			Load from DOM
// --------------------------------------------------------------------------------


void XENCEncryptionMethodImpl::load() {

	if (mp_encryptionMethodNode == NULL) {

		// Attempt to load an empty encryptedType element
		throw XSECException(XSECException::EncryptionMethodError,
			"XENCEncryptionMethod::load - called on empty DOM");

	}

	if (!strEquals(getXENCLocalName(mp_encryptionMethodNode), s_EncryptionMethod)) {

		// Attempt to load an empty encryptedData element
		throw XSECException(XSECException::EncryptionMethodError,
			"XENCEncryptionMethod::load - called on non EncryptionMethod node");

	}

	// Clean up
	if (mp_algorithm != NULL)
		delete[] mp_algorithm;

	// Find the type
	DOMNamedNodeMap * tmpAtts = mp_encryptionMethodNode->getAttributes();

	if (tmpAtts != NULL) {

		DOMNode * att = tmpAtts->getNamedItem(DSIGConstants::s_unicodeStrAlgorithm);

		if (att != NULL) {

			mp_algorithm = XMLString::replicate(att->getNodeValue());

		}

	}

	// Check for known children
	DOMNode * c = findFirstChildOfType(mp_encryptionMethodNode, DOMNode::ELEMENT_NODE);

	while (c != NULL) {

		if (strEquals(getDSIGLocalName(c), s_DigestMethod)) {

			mp_digestAlgorithmAttributeNode = NULL;
			tmpAtts = c->getAttributes();

			if (tmpAtts != NULL)
				mp_digestAlgorithmAttributeNode = tmpAtts->getNamedItem(DSIGConstants::s_unicodeStrAlgorithm);

			if (mp_digestAlgorithmAttributeNode == NULL) {
				throw XSECException(XSECException::EncryptionMethodError,
					"XENCEncryptionMethod::load - Cannot find Algorithm Attribute in DigestMethod element");
			}
		}

		else if (strEquals(getXENCLocalName(c), s_OAEPparams)) {

			mp_oaepParamsTextNode = NULL;
			mp_oaepParamsTextNode = findFirstChildOfType(c, DOMNode::TEXT_NODE);

			if (mp_oaepParamsTextNode == NULL) {
				throw XSECException(XSECException::EncryptionMethodError,
					"XENCEncryptionMethod::load - Cannot find text value of OAEPparams node");
			}

		}

		do {
			c = c->getNextSibling();
		} while (c != NULL && c->getNodeType() != DOMNode::ELEMENT_NODE);
	}
}

// --------------------------------------------------------------------------------
//			Create from scratch
// --------------------------------------------------------------------------------

DOMElement * XENCEncryptionMethodImpl::createBlankEncryptedMethod(const XMLCh * algorithm) {

	// Reset
	if (mp_algorithm != NULL) {
		delete[] mp_algorithm;
		mp_algorithm = NULL;
	}

	// Get some setup values
	safeBuffer str;
	DOMDocument *doc = mp_env->getParentDocument();
	const XMLCh * prefix = mp_env->getXENCNSPrefix();

	makeQName(str, prefix, s_EncryptionMethod);

	DOMElement *ret = doc->createElementNS(DSIGConstants::s_unicodeStrURIXENC, str.rawXMLChBuffer());
	mp_encryptionMethodNode = ret;

	// Set the algorithm attribute

	if (algorithm != NULL) {
		ret->setAttributeNS(DSIGConstants::s_unicodeStrURIXENC,
							DSIGConstants::s_unicodeStrAlgorithm,
							algorithm);
		mp_algorithm = XMLString::replicate(algorithm);

	}

	return ret;

}
// --------------------------------------------------------------------------------
//			Getter functions
// --------------------------------------------------------------------------------

const XMLCh * XENCEncryptionMethodImpl::getDigestMethod(void) {

	if (mp_digestAlgorithmAttributeNode != NULL)
		return mp_digestAlgorithmAttributeNode->getNodeValue();

	return NULL;

}

const XMLCh * XENCEncryptionMethodImpl::getOAEPparams(void) {

	if (mp_oaepParamsTextNode != NULL) {
		return mp_oaepParamsTextNode->getNodeValue();
	}

	return NULL;

}

// --------------------------------------------------------------------------------
//			Setter functions
// --------------------------------------------------------------------------------

void XENCEncryptionMethodImpl::setDigestMethod(const XMLCh * method) {

	if (mp_digestAlgorithmAttributeNode == NULL) {

		// Need to create
		if (mp_oaepParamsTextNode == NULL) {
			mp_env->doPrettyPrint(mp_encryptionMethodNode);
		}

		// Get some setup values
		safeBuffer str;
		DOMDocument *doc = mp_env->getParentDocument();
		const XMLCh * prefix = mp_env->getDSIGNSPrefix();

		makeQName(str, prefix, s_DigestMethod);

		DOMElement *e = doc->createElementNS(DSIGConstants::s_unicodeStrURIDSIG, str.rawXMLChBuffer());
		if (mp_oaepParamsTextNode != NULL) {
			mp_encryptionMethodNode->insertBefore(e, mp_oaepParamsTextNode->getParentNode());
			if (mp_env->getPrettyPrintFlag())
				mp_encryptionMethodNode->insertBefore(doc->createTextNode(DSIGConstants::s_unicodeStrNL), mp_oaepParamsTextNode->getParentNode());
		}
		else {
			mp_encryptionMethodNode->appendChild(e);
			mp_env->doPrettyPrint(mp_encryptionMethodNode);
		}

		e->setAttributeNS(DSIGConstants::s_unicodeStrURIDSIG,
							DSIGConstants::s_unicodeStrAlgorithm,
							method);

		// Set namespace
		if (prefix[0] == XERCES_CPP_NAMESPACE::chNull) {
			str.sbTranscodeIn("xmlns");
		}
		else {
			str.sbTranscodeIn("xmlns:");
			str.sbXMLChCat(prefix);
		}

		e->setAttributeNS(DSIGConstants::s_unicodeStrURIXMLNS, 
								str.rawXMLChBuffer(), 
								DSIGConstants::s_unicodeStrURIXENC);

		// Now retrieve for later use
		DOMNamedNodeMap * tmpAtts = e->getAttributes();

		if (tmpAtts != NULL) {

			mp_digestAlgorithmAttributeNode = tmpAtts->getNamedItem(DSIGConstants::s_unicodeStrAlgorithm);

		}

		if (mp_digestAlgorithmAttributeNode == NULL) {

			throw XSECException(XSECException::EncryptionMethodError,
				"XENCEncryptionMethod::setDigestMethod - Error creating Algorithm Attribute");
		}
	} 
	
	else {

		mp_digestAlgorithmAttributeNode->setNodeValue(method);

	}

}

void XENCEncryptionMethodImpl::setOAEPparams(const XMLCh * params) {

	if (mp_oaepParamsTextNode == NULL) {

		// Need to create
		if (mp_digestAlgorithmAttributeNode == NULL) {
			mp_env->doPrettyPrint(mp_encryptionMethodNode);
		}

		// Get some setup values
		safeBuffer str;
		DOMDocument *doc = mp_env->getParentDocument();
		const XMLCh * prefix = mp_env->getXENCNSPrefix();

		makeQName(str, prefix, s_OAEPparams);

		DOMElement *e = doc->createElementNS(DSIGConstants::s_unicodeStrURIXENC, str.rawXMLChBuffer());
		mp_encryptionMethodNode->appendChild(e);
		mp_env->doPrettyPrint(mp_encryptionMethodNode);

		mp_oaepParamsTextNode = doc->createTextNode(params);
		e->appendChild(mp_oaepParamsTextNode);

	} 
	
	else {

		mp_oaepParamsTextNode->setNodeValue(params);

	}

}

