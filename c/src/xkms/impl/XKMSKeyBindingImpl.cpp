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
 * XKMSKeyBindingImpl := Implementation for KeyBinding
 *
 * $Id$
 *
 */

// XSEC Includes

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/framework/XSECError.hpp>
#include <xsec/framework/XSECEnv.hpp>
#include <xsec/xkms/XKMSConstants.hpp>

#include <xercesc/dom/DOM.hpp>

#include "XKMSKeyBindingImpl.hpp"
#include "XKMSStatusImpl.hpp"

XERCES_CPP_NAMESPACE_USE

// --------------------------------------------------------------------------------
//           Construct/Destruct
// --------------------------------------------------------------------------------

XKMSKeyBindingImpl::XKMSKeyBindingImpl(
		const XSECEnv * env 
		) :
XKMSKeyBindingAbstractTypeImpl(env) {

}

XKMSKeyBindingImpl::XKMSKeyBindingImpl(
		const XSECEnv * env, 
		XERCES_CPP_NAMESPACE_QUALIFIER DOMElement * node
		) :
XKMSKeyBindingAbstractTypeImpl(env, node) {

}

XKMSKeyBindingImpl::~XKMSKeyBindingImpl() {}

// --------------------------------------------------------------------------------
//           Load from DOM
// --------------------------------------------------------------------------------

void XKMSKeyBindingImpl::load(void) {

	if (mp_keyBindingAbstractTypeElement == NULL) {
		throw XSECException(XSECException::ExpectedXKMSChildNotFound,
			"XKMSKeyBindingImpl::load - called on empty DOM");
	}

	XKMSKeyBindingAbstractTypeImpl::load();

	/* Find the status element */
	DOMNodeList * nl = mp_keyBindingAbstractTypeElement->getElementsByTagNameNS(
		XKMSConstants::s_unicodeStrURIXKMS,
		XKMSConstants::s_tagStatus);

	if (nl == NULL || nl->getLength() != 1) {
		throw XSECException(XSECException::ExpectedXKMSChildNotFound,
			"XKMSKeyBinding::load - Status value not found");
	}

	XSECnew(mp_status, XKMSStatusImpl(mp_env, (DOMElement*) nl->item(0)));
	mp_status->load();

}

// --------------------------------------------------------------------------------
//           Create
// --------------------------------------------------------------------------------

DOMElement * XKMSKeyBindingImpl::createBlankKeyBinding(XKMSStatus::StatusValue status) {

	DOMElement * ret = XKMSKeyBindingAbstractTypeImpl::
				createBlankKeyBindingAbstractType(XKMSConstants::s_tagKeyBinding);

	mp_env->doPrettyPrint(ret);

	// Create the status element
	XSECnew(mp_status, XKMSStatusImpl(mp_env));
	ret->appendChild(mp_status->createBlankStatus(status));
	mp_env->doPrettyPrint(ret);

	return ret;

}

// --------------------------------------------------------------------------------
//           Status handling
// --------------------------------------------------------------------------------

XKMSStatus * XKMSKeyBindingImpl::getStatus(void) const {

	return mp_status;

}

