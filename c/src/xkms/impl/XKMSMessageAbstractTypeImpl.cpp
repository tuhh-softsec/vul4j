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
#include <xsec/xkms/XKMSConstants.hpp>

#include <xercesc/dom/DOM.hpp>

#include "XKMSMessageAbstractTypeImpl.hpp"

XERCES_CPP_NAMESPACE_USE

// --------------------------------------------------------------------------------
//           Construct/Destruct
// --------------------------------------------------------------------------------

XKMSMessageAbstractTypeImpl::XKMSMessageAbstractTypeImpl(
		const XSECEnv * env, 
		XERCES_CPP_NAMESPACE_QUALIFIER DOMElement * node) :

mp_env(env),
mp_messageAbstractTypeElement(node) {

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

