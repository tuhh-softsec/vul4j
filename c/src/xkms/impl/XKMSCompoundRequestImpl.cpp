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
 * XKMSCompoundRequestImpl := Implementation of CompoundRequest Messages
 *
 * $Id$
 *
 */

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/framework/XSECError.hpp>
#include <xsec/framework/XSECEnv.hpp>
#include <xsec/utils/XSECDOMUtils.hpp>
#include <xsec/xkms/XKMSConstants.hpp>

#include "XKMSCompoundRequestImpl.hpp"
#include "XKMSLocateRequestImpl.hpp"
#include "XKMSValidateRequestImpl.hpp"
#include "XKMSRegisterRequestImpl.hpp"

#include <xercesc/dom/DOM.hpp>

XERCES_CPP_NAMESPACE_USE

// --------------------------------------------------------------------------------
//           Construct/Destruct
// --------------------------------------------------------------------------------

XKMSCompoundRequestImpl::XKMSCompoundRequestImpl(
		const XSECEnv * env) :
XKMSRequestAbstractTypeImpl(env) {

}

XKMSCompoundRequestImpl::XKMSCompoundRequestImpl(
		const XSECEnv * env, 
		XERCES_CPP_NAMESPACE_QUALIFIER DOMElement * node) :
XKMSRequestAbstractTypeImpl(env, node) {

}

XKMSCompoundRequestImpl::~XKMSCompoundRequestImpl() {

	RequestListVectorType::iterator i;

	for (i = m_requestList.begin() ; i < m_requestList.end(); i++) {

		delete (*i);

	}


}


// --------------------------------------------------------------------------------
//           Load from DOM
// --------------------------------------------------------------------------------

// Load elements
void XKMSCompoundRequestImpl::load() {

	if (mp_messageAbstractTypeElement == NULL) {

		// Attempt to load an empty element
		throw XSECException(XSECException::XKMSError,
			"XKMSCompoundRequest::load - called on empty DOM");

	}

	if (!strEquals(getXKMSLocalName(mp_messageAbstractTypeElement), 
									XKMSConstants::s_tagCompoundRequest)) {
	
		throw XSECException(XSECException::XKMSError,
			"XKMSCompoundRequest::load - called incorrect node");
	
	}

	// Load the base message
	XKMSRequestAbstractTypeImpl::load();

	// Now find all Request elements
	DOMElement * e = findFirstElementChild(mp_messageAbstractTypeElement);

	while (e != NULL) {

		if (strEquals(getXKMSLocalName(e), XKMSConstants::s_tagLocateRequest) ||
			strEquals(getXKMSLocalName(e), XKMSConstants::s_tagValidateRequest)) {

			// Have a legitimate request to load
			XKMSMessageAbstractTypeImpl * m = 
				(XKMSMessageAbstractTypeImpl *) m_factory.newMessageFromDOM(e);
			m_requestList.push_back((XKMSRequestAbstractTypeImpl *) m);

		}

		e = findNextElementChild(e);

	}
	

}

// --------------------------------------------------------------------------------
//           Create a blank one
// --------------------------------------------------------------------------------
DOMElement * XKMSCompoundRequestImpl::createBlankCompoundRequest(
		const XMLCh * service,
		const XMLCh * id) {

	return XKMSRequestAbstractTypeImpl::createBlankMessageAbstractType(
		XKMSConstants::s_tagCompoundRequest, service, id);

}

// --------------------------------------------------------------------------------
//           Get interface methods
// --------------------------------------------------------------------------------

XKMSMessageAbstractType::messageType XKMSCompoundRequestImpl::getMessageType(void) {

	return XKMSMessageAbstractTypeImpl::CompoundRequest;

}

int XKMSCompoundRequestImpl::getRequestListSize(void) {

	return m_requestList.size();

}

XKMSRequestAbstractType * XKMSCompoundRequestImpl::getRequestListItem(int item) {

	if (item < 0 || item >= m_requestList.size()) {

		throw XSECException(XSECException::XKMSError,
			"XKMSCompoundRequest::getRequestListItem - item out of range");
	}

	return m_requestList[item];


}

// --------------------------------------------------------------------------------
//           Setter methods
// --------------------------------------------------------------------------------

XKMSLocateRequest * XKMSCompoundRequestImpl::createLocateRequest(
		const XMLCh * service,
		const XMLCh * id) {

	XKMSLocateRequest * r = m_factory.createLocateRequest(service, mp_env->getParentDocument(), id);
	m_requestList.push_back((XKMSRequestAbstractTypeImpl*) r);

	mp_messageAbstractTypeElement->appendChild(r->getElement());
	mp_env->doPrettyPrint(mp_messageAbstractTypeElement);

	return r;

}

XKMSValidateRequest * XKMSCompoundRequestImpl::createValidateRequest(
		const XMLCh * service,
		const XMLCh * id) {

	XKMSValidateRequest * r = m_factory.createValidateRequest(service, mp_env->getParentDocument(), id);
	m_requestList.push_back((XKMSRequestAbstractTypeImpl*) r);

	mp_messageAbstractTypeElement->appendChild(r->getElement());
	mp_env->doPrettyPrint(mp_messageAbstractTypeElement);

	return r;
}

XKMSRegisterRequest * XKMSCompoundRequestImpl::createRegisterRequest(
		const XMLCh * service,
		const XMLCh * id) {

	XKMSRegisterRequest * r = m_factory.createRegisterRequest(service, mp_env->getParentDocument(), id);
	m_requestList.push_back((XKMSRequestAbstractTypeImpl*) r);

	mp_messageAbstractTypeElement->appendChild(r->getElement());
	mp_env->doPrettyPrint(mp_messageAbstractTypeElement);

	return r;
}
