/*
 * Copyright 2004-2005 The Apache Software Foundation.
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
 * limitations under the License.
 */

/*
 * XSEC
 *
 * XKMSRegisterResultImpl := Implementation of RegisterResult Messages
 *
 * $Id$
 *
 */

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/framework/XSECError.hpp>
#include <xsec/framework/XSECEnv.hpp>
#include <xsec/utils/XSECDOMUtils.hpp>
#include <xsec/xkms/XKMSConstants.hpp>

#include "XKMSRegisterResultImpl.hpp"
#include "XKMSKeyBindingImpl.hpp"

#include <xercesc/dom/DOM.hpp>

XERCES_CPP_NAMESPACE_USE

// --------------------------------------------------------------------------------
//           Construct/Destruct
// --------------------------------------------------------------------------------

XKMSRegisterResultImpl::XKMSRegisterResultImpl(
		const XSECEnv * env) :
XKMSResultTypeImpl(env) {

}

XKMSRegisterResultImpl::XKMSRegisterResultImpl(
		const XSECEnv * env, 
		XERCES_CPP_NAMESPACE_QUALIFIER DOMElement * node) :
XKMSResultTypeImpl(env, node) {

}

XKMSRegisterResultImpl::~XKMSRegisterResultImpl() {

	XKMSRegisterResultImpl::KeyBindingVectorType::iterator i;

	for (i = m_keyBindingList.begin() ; i < m_keyBindingList.end(); i++) {

		delete (*i);

	}

}


// --------------------------------------------------------------------------------
//           Load from DOM
// --------------------------------------------------------------------------------

// Load elements
void XKMSRegisterResultImpl::load() {

	if (mp_messageAbstractTypeElement == NULL) {

		// Attempt to load an empty element
		throw XSECException(XSECException::XKMSError,
			"XKMSRegisterResult::load - called on empty DOM");

	}

	if (!strEquals(getXKMSLocalName(mp_messageAbstractTypeElement), 
									XKMSConstants::s_tagRegisterResult)) {
	
		throw XSECException(XSECException::XKMSError,
			"XKMSRegisterResult::load - called incorrect node");
	
	}

	// Get any UnverifiedKeyBinding elements
	DOMNodeList * nl = mp_messageAbstractTypeElement->getElementsByTagNameNS(
		XKMSConstants::s_unicodeStrURIXKMS,
		XKMSConstants::s_tagKeyBinding);

	if (nl != NULL) {

		XKMSKeyBindingImpl * kb;
		for (int i = 0; i < nl->getLength() ; ++ i) {

			XSECnew(kb, XKMSKeyBindingImpl(mp_env, (DOMElement *) nl->item(i)));
			m_keyBindingList.push_back(kb);
			kb->load();

		}

	}


	// Load the base message
	XKMSResultTypeImpl::load();

}

// --------------------------------------------------------------------------------
//           Create a blank one
// --------------------------------------------------------------------------------
DOMElement * XKMSRegisterResultImpl::createBlankRegisterResult(
		const XMLCh * service,
		const XMLCh * id,
		ResultMajor rmaj,
		ResultMinor rmin) {

	return XKMSResultTypeImpl::createBlankResultType(
		XKMSConstants::s_tagRegisterResult, service, id, rmaj, rmin);

}

// --------------------------------------------------------------------------------
//           Get interface methods
// --------------------------------------------------------------------------------

XKMSMessageAbstractType::messageType XKMSRegisterResultImpl::getMessageType(void) {

	return XKMSMessageAbstractType::RegisterResult;

}

// --------------------------------------------------------------------------------
//           UnverifiedKeyBinding handling
// --------------------------------------------------------------------------------


int XKMSRegisterResultImpl::getKeyBindingSize(void) const {

	return m_keyBindingList.size();

}

XKMSKeyBinding * XKMSRegisterResultImpl::getKeyBindingItem(int item) const {

	if (item < 0 || item >= m_keyBindingList.size()) {
		throw XSECException(XSECException::XKMSError,
			"XKMSRegisterResult::getKeyBindingItem - item out of range");
	}

	return m_keyBindingList[item];

}

XKMSKeyBinding * XKMSRegisterResultImpl::appendKeyBindingItem(XKMSStatus::StatusValue status) {

	XKMSKeyBindingImpl * u;

	XSECnew(u, XKMSKeyBindingImpl(mp_env));

	m_keyBindingList.push_back(u);

	DOMElement * e = u->createBlankKeyBinding(status);

	// Append the element

	mp_messageAbstractTypeElement->appendChild(e);
	mp_env->doPrettyPrint(mp_messageAbstractTypeElement);

	return u;

}
