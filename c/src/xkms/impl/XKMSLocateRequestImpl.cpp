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
 * XKMSLocateRequestImpl := Implementation of LocateRequest Messages
 *
 * $Id$
 *
 */

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/framework/XSECError.hpp>
#include <xsec/framework/XSECEnv.hpp>
#include <xsec/utils/XSECDOMUtils.hpp>
#include <xsec/xkms/XKMSConstants.hpp>

#include "XKMSLocateRequestImpl.hpp"

#include <xercesc/dom/DOM.hpp>

XERCES_CPP_NAMESPACE_USE

// --------------------------------------------------------------------------------
//           Construct/Destruct
// --------------------------------------------------------------------------------

XKMSLocateRequestImpl::XKMSLocateRequestImpl(
		const XSECEnv * env) :
XKMSRequestAbstractTypeImpl(env),
mp_queryKeyBindingElement(NULL),
mp_queryKeyBinding(NULL) {

}

XKMSLocateRequestImpl::XKMSLocateRequestImpl(
		const XSECEnv * env, 
		XERCES_CPP_NAMESPACE_QUALIFIER DOMElement * node) :
XKMSRequestAbstractTypeImpl(env, node),
mp_queryKeyBinding(NULL) {

}

XKMSLocateRequestImpl::~XKMSLocateRequestImpl() {

	if (mp_queryKeyBinding != NULL)
		delete mp_queryKeyBinding;

}


// --------------------------------------------------------------------------------
//           Load from DOM
// --------------------------------------------------------------------------------

// Load elements
void XKMSLocateRequestImpl::load() {

	if (mp_messageAbstractTypeElement == NULL) {

		// Attempt to load an empty element
		throw XSECException(XSECException::XKMSError,
			"XKMSLocateRequest::load - called on empty DOM");

	}

	if (!strEquals(getXKMSLocalName(mp_messageAbstractTypeElement), 
									XKMSConstants::s_tagLocateRequest)) {
	
		throw XSECException(XSECException::XKMSError,
			"XKMSLocateRequest::load - called incorrect node");
	
	}

	// Load the base message
	XKMSRequestAbstractTypeImpl::load();

	// Now check for any QueryKeyBinding elements
	DOMElement * tmpElt = findFirstElementChild(mp_messageAbstractTypeElement);
	while (tmpElt != NULL && 
		!strEquals(getXKMSLocalName(tmpElt), XKMSConstants::s_tagQueryKeyBinding)) 
		tmpElt = findNextElementChild(tmpElt);

	if (tmpElt != NULL) {

		XSECnew(mp_queryKeyBinding, XKMSQueryKeyBindingImpl(mp_env, tmpElt));
		mp_queryKeyBinding->load();
		mp_queryKeyBindingElement = tmpElt;

	}

}

// --------------------------------------------------------------------------------
//           Create a blank one
// --------------------------------------------------------------------------------
DOMElement * XKMSLocateRequestImpl::createBlankLocateRequest(
		const XMLCh * service,
		const XMLCh * id) {

	return XKMSRequestAbstractTypeImpl::createBlankMessageAbstractType(
		XKMSConstants::s_tagLocateRequest, service, id);
//	return XKMSRequestAbstractTypeImpl::createBlankMessageAbstractType(
//		MAKE_UNICODE_STRING("ValidateRequest"), service, id);

}

// --------------------------------------------------------------------------------
//           Get interface methods
// --------------------------------------------------------------------------------

XKMSMessageAbstractType::messageType XKMSLocateRequestImpl::getMessageType(void) {

	return XKMSMessageAbstractType::LocateRequest;

}


XKMSQueryKeyBinding * XKMSLocateRequestImpl::getQueryKeyBinding(void) {

	return mp_queryKeyBinding;

}

// --------------------------------------------------------------------------------
//           Setter methods
// --------------------------------------------------------------------------------

XKMSQueryKeyBinding * XKMSLocateRequestImpl::addQueryKeyBinding(void) {

	if (mp_queryKeyBinding != NULL)
		return mp_queryKeyBinding;


	// OK - Nothing exists, so we need to create from scratch

	XSECnew(mp_queryKeyBinding, XKMSQueryKeyBindingImpl(mp_env));
	mp_queryKeyBindingElement = mp_queryKeyBinding->createBlankQueryKeyBinding();

	if (mp_messageAbstractTypeElement->getFirstChild() == NULL) {
		mp_env->doPrettyPrint(mp_messageAbstractTypeElement);
	}
	mp_messageAbstractTypeElement->appendChild(mp_queryKeyBindingElement);
	mp_env->doPrettyPrint(mp_messageAbstractTypeElement);

	return mp_queryKeyBinding;

}
