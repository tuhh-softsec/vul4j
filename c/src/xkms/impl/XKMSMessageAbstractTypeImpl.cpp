/*
 * Copyright 2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * imitations under the License.
 */

/*
 * XSEC
 *
 * XKMSMessageAbstractTypeImpl := Implementation class for base XKMS messages
 *
 * $Id$
 *
 */

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/framework/XSECEnv.hpp>
#include <xsec/framework/XSECError.hpp>
#include <xsec/utils/XSECDOMUtils.hpp>
#include <xsec/dsig/DSIGSignature.hpp>
#include <xsec/dsig/DSIGReference.hpp>
#include <xsec/xkms/XKMSConstants.hpp>

#include <xercesc/dom/DOM.hpp>
#include <xercesc/util/XMLUniDefs.hpp>
#include <xercesc/util/Janitor.hpp>

#include "XKMSMessageAbstractTypeImpl.hpp"

XERCES_CPP_NAMESPACE_USE

// --------------------------------------------------------------------------------
//           Construct/Destruct
// --------------------------------------------------------------------------------

XKMSMessageAbstractTypeImpl::XKMSMessageAbstractTypeImpl(
		const XSECEnv * env) :

mp_env(env),
mp_messageAbstractTypeElement(NULL),
mp_idAttr(NULL),
mp_serviceAttr(NULL),
mp_nonceAttr(NULL),
mp_signatureElement(NULL),
mp_signature(NULL) {

}

XKMSMessageAbstractTypeImpl::XKMSMessageAbstractTypeImpl(
		const XSECEnv * env, 
		XERCES_CPP_NAMESPACE_QUALIFIER DOMElement * node) :

mp_env(env),
mp_messageAbstractTypeElement(node),
mp_idAttr(NULL),
mp_serviceAttr(NULL),
mp_nonceAttr(NULL),
mp_signatureElement(NULL),
mp_signature(NULL) {

}

XKMSMessageAbstractTypeImpl::~XKMSMessageAbstractTypeImpl() {

	// We own the environment, so we delete it.

	delete mp_env;

}

// --------------------------------------------------------------------------------
//           Load
// --------------------------------------------------------------------------------

void XKMSMessageAbstractTypeImpl::load(void) {

	if (mp_messageAbstractTypeElement == NULL) {

		// Attempt to load an empty element
		throw XSECException(XSECException::MessageAbstractTypeError,
			"XKMSMessageAbstractType::load - called on empty DOM");

	}

	// Id
	mp_idAttr = 
		mp_messageAbstractTypeElement->getAttributeNodeNS(NULL, XKMSConstants::s_tagId);
	// Service
	mp_serviceAttr = 
		mp_messageAbstractTypeElement->getAttributeNodeNS(NULL, XKMSConstants::s_tagService);
	// Nonce
	mp_nonceAttr = 
		mp_messageAbstractTypeElement->getAttributeNodeNS(NULL, XKMSConstants::s_tagNonce);

	// Id and Service MUST be set for a message to be OK

	if (mp_idAttr == NULL) {
		throw XSECException(XSECException::ExpectedXKMSChildNotFound,
			"XKMSMessageAbstractType::load - Id attribute not found");
	}

	if (mp_serviceAttr == NULL) {
		throw XSECException(XSECException::ExpectedXKMSChildNotFound,
			"XKMSMessageAbstractType::load - Service attribute not found");
	}

	// Check for <Signature> node
	DOMElement *mp_signatureElement = (DOMElement *) findFirstChildOfType(mp_messageAbstractTypeElement, DOMNode::ELEMENT_NODE);

	while (mp_signatureElement != NULL && 
		!strEquals(getDSIGLocalName(mp_signatureElement), XKMSConstants::s_tagSignature)) {

		mp_signatureElement = findNextElementChild(mp_signatureElement);

	}

	// The provider will take care of cleaning this up later.

	if (mp_signatureElement != NULL) {

		mp_signature = m_prov.newSignatureFromDOM(mp_signatureElement->getOwnerDocument(), 
												  mp_signatureElement);
		mp_signature->load();

	}

}

// --------------------------------------------------------------------------------
//           Create blank
// --------------------------------------------------------------------------------

DOMElement * XKMSMessageAbstractTypeImpl::createBlankMessageAbstractType(
		const XMLCh * tag,
		const XMLCh * service,
		const XMLCh * id) {

	// Get some setup values
	safeBuffer str;
	DOMDocument *doc = mp_env->getParentDocument();
	const XMLCh * prefix = mp_env->getXKMSNSPrefix();

	makeQName(str, prefix, tag);

	mp_messageAbstractTypeElement = doc->createElementNS(XKMSConstants::s_unicodeStrURIXKMS, 
												str.rawXMLChBuffer());

	// Set namespace
	if (prefix[0] == chNull) {
		str.sbTranscodeIn("xmlns");
	}
	else {
		str.sbTranscodeIn("xmlns:");
		str.sbXMLChCat(prefix);
	}

	mp_messageAbstractTypeElement->setAttributeNS(DSIGConstants::s_unicodeStrURIXMLNS, 
							str.rawXMLChBuffer(), 
							XKMSConstants::s_unicodeStrURIXKMS);

	mp_env->doPrettyPrint(mp_messageAbstractTypeElement);

	// Setup the service URI
	mp_messageAbstractTypeElement->setAttributeNS(NULL, 
							XKMSConstants::s_tagService,
							service);
	mp_serviceAttr = 
		mp_messageAbstractTypeElement->getAttributeNodeNS(NULL, XKMSConstants::s_tagService);


	// Setup the id
	XMLCh * myId;

	if (id != NULL)
		myId = XMLString::replicate(id);
	else
		myId = generateId();

	ArrayJanitor<XMLCh> j_myId(myId);

	mp_messageAbstractTypeElement->setAttributeNS(NULL, XKMSConstants::s_tagId, myId);
	mp_messageAbstractTypeElement->setIdAttributeNS(NULL, XKMSConstants::s_tagId);
	mp_idAttr = 
		mp_messageAbstractTypeElement->getAttributeNodeNS(NULL, XKMSConstants::s_tagId);

	// Nonce is blank at start
	mp_nonceAttr = NULL;

	return mp_messageAbstractTypeElement;

}


// --------------------------------------------------------------------------------
//           Getter interfaces
// --------------------------------------------------------------------------------

bool XKMSMessageAbstractTypeImpl::isSigned(void) const {

	return mp_signature != NULL;

}

DSIGSignature * XKMSMessageAbstractTypeImpl::getSignature(void) const {

	return mp_signature;

}


DOMElement * XKMSMessageAbstractTypeImpl::getElement(void) const {

	return mp_messageAbstractTypeElement;

}

const XMLCh * XKMSMessageAbstractTypeImpl::getId(void) const {

	return (mp_idAttr != NULL ? mp_idAttr->getNodeValue() : NULL);

}

const XMLCh * XKMSMessageAbstractTypeImpl::getService(void) const {

	return (mp_serviceAttr != NULL ? mp_serviceAttr->getNodeValue() : NULL);

}

const XMLCh * XKMSMessageAbstractTypeImpl::getNonce(void) const {

	return (mp_nonceAttr != NULL ? mp_nonceAttr->getNodeValue() : NULL);

}

// --------------------------------------------------------------------------------
//           Setter Interfaces
// --------------------------------------------------------------------------------

void XKMSMessageAbstractTypeImpl::setId(const XMLCh * id) {}
void XKMSMessageAbstractTypeImpl::setService(const XMLCh * service) {}
void XKMSMessageAbstractTypeImpl::setNonce(const XMLCh * uri) {}

DSIGSignature * XKMSMessageAbstractTypeImpl::addSignature(
		canonicalizationMethod cm,
		signatureMethod	sm,
		hashMethod hm) {

	DSIGSignature * ret = m_prov.newSignature();
	DOMElement * elt = ret->createBlankSignature(mp_env->getParentDocument(), cm, sm, hm);

	/* Create the enveloping reference */
	safeBuffer sb;
	sb.sbXMLChIn(DSIGConstants::s_unicodeStrEmpty);
	sb.sbXMLChAppendCh(chPound);
	sb.sbXMLChCat(getId());

	DSIGReference *ref = ret->createReference(sb.rawXMLChBuffer());
	ref->appendEnvelopedSignatureTransform();
	ref->appendCanonicalizationTransform(CANON_C14NE_COM);

	/* Embed the signature in the document */
	DOMNode * c = mp_messageAbstractTypeElement->getFirstChild();
	if (c != NULL) {
		if (mp_env->getPrettyPrintFlag() == true) {
			mp_messageAbstractTypeElement->insertBefore(
				mp_env->getParentDocument()->createTextNode(DSIGConstants::s_unicodeStrNL),
				c);
		}
		mp_messageAbstractTypeElement->insertBefore(elt, c);
	}
	else
		mp_messageAbstractTypeElement->appendChild(elt);

	return ret;
}

