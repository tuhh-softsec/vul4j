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
 * XKMSLocateResultImpl := Implementation of LocateResult Messages
 *
 * $Id$
 *
 */

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/framework/XSECError.hpp>
#include <xsec/framework/XSECEnv.hpp>
#include <xsec/utils/XSECDOMUtils.hpp>
#include <xsec/xkms/XKMSConstants.hpp>

#include "XKMSLocateResultImpl.hpp"
#include "XKMSUnverifiedKeyBindingImpl.hpp"

#include <xercesc/dom/DOM.hpp>

XERCES_CPP_NAMESPACE_USE

// --------------------------------------------------------------------------------
//           Construct/Destruct
// --------------------------------------------------------------------------------

XKMSLocateResultImpl::XKMSLocateResultImpl(
		const XSECEnv * env) :
XKMSResultTypeImpl(env) {

}

XKMSLocateResultImpl::XKMSLocateResultImpl(
		const XSECEnv * env, 
		XERCES_CPP_NAMESPACE_QUALIFIER DOMElement * node) :
XKMSResultTypeImpl(env, node) {

}

XKMSLocateResultImpl::~XKMSLocateResultImpl() {

	XKMSLocateResultImpl::UnverifiedKeyBindingVectorType::iterator i;

	for (i = m_unverifiedKeyBindingList.begin() ; i < m_unverifiedKeyBindingList.end(); i++) {

		delete (*i);

	}

}


// --------------------------------------------------------------------------------
//           Load from DOM
// --------------------------------------------------------------------------------

// Load elements
void XKMSLocateResultImpl::load() {

	if (mp_messageAbstractTypeElement == NULL) {

		// Attempt to load an empty element
		throw XSECException(XSECException::XKMSError,
			"XKMSLocateResult::load - called on empty DOM");

	}

	if (!strEquals(getXKMSLocalName(mp_messageAbstractTypeElement), 
									XKMSConstants::s_tagLocateResult)) {
	
		throw XSECException(XSECException::XKMSError,
			"XKMSLocateResult::load - called incorrect node");
	
	}

	// Get any UnverifiedKeyBinding elements
	DOMNodeList * nl = mp_messageAbstractTypeElement->getElementsByTagNameNS(
		XKMSConstants::s_unicodeStrURIXKMS,
		XKMSConstants::s_tagUnverifiedKeyBinding);

	if (nl != NULL) {

		XKMSUnverifiedKeyBindingImpl * ukb;
		for (unsigned int i = 0; i < nl->getLength() ; ++ i) {

			XSECnew(ukb, XKMSUnverifiedKeyBindingImpl(mp_env, (DOMElement *) nl->item(i)));
			m_unverifiedKeyBindingList.push_back(ukb);
			ukb->load();

		}

	}


	// Load the base message
	XKMSResultTypeImpl::load();

}

// --------------------------------------------------------------------------------
//           Create a blank one
// --------------------------------------------------------------------------------
DOMElement * XKMSLocateResultImpl::createBlankLocateResult(
		const XMLCh * service,
		const XMLCh * id,
		ResultMajor rmaj,
		ResultMinor rmin) {

	return XKMSResultTypeImpl::createBlankResultType(
		XKMSConstants::s_tagLocateResult, service, id, rmaj, rmin);

}

// --------------------------------------------------------------------------------
//           Get interface methods
// --------------------------------------------------------------------------------

XKMSMessageAbstractType::messageType XKMSLocateResultImpl::getMessageType(void) {

	return XKMSMessageAbstractTypeImpl::LocateResult;

}

// --------------------------------------------------------------------------------
//           UnverifiedKeyBinding handling
// --------------------------------------------------------------------------------


int XKMSLocateResultImpl::getUnverifiedKeyBindingSize(void) const {

	return (int) m_unverifiedKeyBindingList.size();

}

XKMSUnverifiedKeyBinding * XKMSLocateResultImpl::getUnverifiedKeyBindingItem(int item) const {

	if (item < 0 || item >= (int) m_unverifiedKeyBindingList.size()) {
		throw XSECException(XSECException::XKMSError,
			"XKMSLocateResult::getUnverifiedKeyBindingItem - item out of range");
	}

	return m_unverifiedKeyBindingList[item];

}

XKMSUnverifiedKeyBinding * XKMSLocateResultImpl::appendUnverifiedKeyBindingItem(void) {

	XKMSUnverifiedKeyBindingImpl * u;

	XSECnew(u, XKMSUnverifiedKeyBindingImpl(mp_env));

	m_unverifiedKeyBindingList.push_back(u);

	DOMElement * e = u->createBlankUnverifiedKeyBinding();

	// Append the element

	mp_messageAbstractTypeElement->appendChild(e);
	mp_env->doPrettyPrint(mp_messageAbstractTypeElement);

	return u;

}
