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
 * XKMSKeyBindingAbstractTypeImpl := Implementation of base for KeyBinding elements
 *
 * $Id$
 *
 */

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/framework/XSECError.hpp>
#include <xsec/xkms/XKMSConstants.hpp>
#include <xsec/utils/XSECDOMUtils.hpp>
#include <xsec/dsig/DSIGKeyInfoList.hpp>

#include <xercesc/dom/DOM.hpp>

#include "XKMSKeyBindingAbstractTypeImpl.hpp"

XERCES_CPP_NAMESPACE_USE

// --------------------------------------------------------------------------------
//           Constructor/Destructor
// --------------------------------------------------------------------------------

XKMSKeyBindingAbstractTypeImpl::XKMSKeyBindingAbstractTypeImpl(
		const XSECEnv * env, 
		XERCES_CPP_NAMESPACE_QUALIFIER DOMElement * node) :
mp_keyBindingAbstractTypeElement(node),
mp_idAttr(NULL),
mp_keyUsageEncryptionElement(NULL),
mp_keyUsageSignatureElement(NULL),
mp_keyUsageExchangeElement(NULL),
mp_env(env),
mp_keyInfoList(NULL) {

}

XKMSKeyBindingAbstractTypeImpl::~XKMSKeyBindingAbstractTypeImpl() {

	if (mp_keyInfoList != NULL)
		delete mp_keyInfoList;

}

// --------------------------------------------------------------------------------
//           Load from DOM
// --------------------------------------------------------------------------------

void XKMSKeyBindingAbstractTypeImpl::load(void) {

	if (mp_keyBindingAbstractTypeElement == NULL) {
		throw XSECException(XSECException::ExpectedXKMSChildNotFound,
			"XKMSKeyBindingAbstractTypeImpl::load - called on empty DOM");
	}

	// Id
	mp_idAttr = 
		mp_keyBindingAbstractTypeElement->getAttributeNodeNS(NULL, XKMSConstants::s_tagId);

	DOMElement * tmpElt = findFirstElementChild(mp_keyBindingAbstractTypeElement);

	if (tmpElt != NULL && strEquals(getDSIGLocalName(tmpElt), XKMSConstants::s_tagKeyInfo)) {

		if (mp_keyInfoList != NULL)
			delete mp_keyInfoList;

		XSECnew(mp_keyInfoList, DSIGKeyInfoList(mp_env));

		mp_keyInfoList->loadListFromXML(tmpElt);
		tmpElt = findNextElementChild(tmpElt);

	}

	while (tmpElt != NULL && strEquals(getXKMSLocalName(tmpElt), XKMSConstants::s_tagKeyUsage)) {

		DOMNode * txt = findFirstChildOfType(tmpElt, DOMNode::TEXT_NODE);

		if (txt == NULL) {
			throw XSECException(XSECException::ExpectedXKMSChildNotFound,
				"XKMSKeyBindingAbstractTypeImpl::load - Require text node beneath <KeyUsage>");
		}

		const XMLCh * usageStr = txt->getNodeValue();

		if (strEquals(usageStr, XKMSConstants::s_tagEncryption)) {
			mp_keyUsageEncryptionElement = tmpElt;
		} else 	if (strEquals(usageStr, XKMSConstants::s_tagExchange)) {
			mp_keyUsageExchangeElement = tmpElt;
		} else if (strEquals(usageStr, XKMSConstants::s_tagSignature)) {
			mp_keyUsageSignatureElement = tmpElt;
		} else {
			throw XSECException(XSECException::ExpectedXKMSChildNotFound,
				"XKMSKeyBindingAbstractTypeImpl::load - require Encryption, Exchange or Signature text node beneath <KeyUsage>");
		}

		tmpElt = findNextElementChild(tmpElt);
	}

}


// --------------------------------------------------------------------------------
//           Getter methods
// --------------------------------------------------------------------------------

DOMElement * XKMSKeyBindingAbstractTypeImpl::getElement(void) const {

	return mp_keyBindingAbstractTypeElement;

}

const XMLCh * XKMSKeyBindingAbstractTypeImpl::getId(void) const {

	return (mp_idAttr != NULL ? mp_idAttr->getNodeValue() : NULL);

}

DSIGKeyInfoList * XKMSKeyBindingAbstractTypeImpl::getKeyInfoList(void) const {

	return mp_keyInfoList;

}

bool XKMSKeyBindingAbstractTypeImpl::getEncryptionKeyUsage(void) const {

	return (mp_keyUsageEncryptionElement != NULL || 
		(mp_keyUsageExchangeElement == NULL && mp_keyUsageSignatureElement == NULL));

}

bool XKMSKeyBindingAbstractTypeImpl::getSignatureKeyUsage(void) const {

	return (mp_keyUsageSignatureElement != NULL || 
		(mp_keyUsageExchangeElement == NULL && mp_keyUsageEncryptionElement == NULL));

}

bool XKMSKeyBindingAbstractTypeImpl::getExchangeKeyUsage(void) const {

	return (mp_keyUsageExchangeElement != NULL || 
		(mp_keyUsageEncryptionElement == NULL && mp_keyUsageSignatureElement == NULL));

}


// --------------------------------------------------------------------------------
//           Setter methods
// --------------------------------------------------------------------------------

void XKMSKeyBindingAbstractTypeImpl::setId(const XMLCh * id) {
}
void XKMSKeyBindingAbstractTypeImpl::setEncryptionKeyUsage(void) {
}
void XKMSKeyBindingAbstractTypeImpl::setSignatureKeyUsage(void) {
}
void XKMSKeyBindingAbstractTypeImpl::setExchangeKeyUsage(void) {
}

