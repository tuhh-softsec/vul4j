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
 * XKMSCompoundResultImpl := Implementation of CompoundResult Messages
 *
 * $Id$
 *
 */

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/framework/XSECError.hpp>
#include <xsec/framework/XSECEnv.hpp>
#include <xsec/utils/XSECDOMUtils.hpp>
#include <xsec/xkms/XKMSConstants.hpp>

#include "XKMSCompoundResultImpl.hpp"
#include "XKMSLocateResultImpl.hpp"
#include "XKMSValidateResultImpl.hpp"
#include "XKMSResultImpl.hpp"

#include <xercesc/dom/DOM.hpp>

XERCES_CPP_NAMESPACE_USE

// --------------------------------------------------------------------------------
//           Construct/Destruct
// --------------------------------------------------------------------------------

XKMSCompoundResultImpl::XKMSCompoundResultImpl(
		const XSECEnv * env) :
XKMSResultTypeImpl(env) {

}

XKMSCompoundResultImpl::XKMSCompoundResultImpl(
		const XSECEnv * env, 
		XERCES_CPP_NAMESPACE_QUALIFIER DOMElement * node) :
XKMSResultTypeImpl(env, node) {

}

XKMSCompoundResultImpl::~XKMSCompoundResultImpl() {

	ResultListVectorType::iterator i;

	for (i = m_resultList.begin() ; i < m_resultList.end(); i++) {

		delete (*i);

	}


}


// --------------------------------------------------------------------------------
//           Load from DOM
// --------------------------------------------------------------------------------

// Load elements
void XKMSCompoundResultImpl::load() {

	if (mp_messageAbstractTypeElement == NULL) {

		// Attempt to load an empty element
		throw XSECException(XSECException::XKMSError,
			"XKMSCompoundResult::load - called on empty DOM");

	}

	if (!strEquals(getXKMSLocalName(mp_messageAbstractTypeElement), 
									XKMSConstants::s_tagCompoundResult)) {
	
		throw XSECException(XSECException::XKMSError,
			"XKMSCompoundResult::load - called incorrect node");
	
	}

	// Load the base message
	XKMSResultTypeImpl::load();

	// Now find all Result elements
	DOMElement * e = findFirstElementChild(mp_messageAbstractTypeElement);

	while (e != NULL) {

		if (strEquals(getXKMSLocalName(e), XKMSConstants::s_tagLocateResult) ||
			strEquals(getXKMSLocalName(e), XKMSConstants::s_tagValidateResult)) {

			// Have a legitimate Result to load
			XKMSMessageAbstractType * m = m_factory.newMessageFromDOM(e);
			m_resultList.push_back(dynamic_cast<XKMSResultTypeImpl *>(m));

		}

		e = findNextElementChild(e);

	}
	

}

// --------------------------------------------------------------------------------
//           Create a blank one
// --------------------------------------------------------------------------------
DOMElement * XKMSCompoundResultImpl::createBlankCompoundResult(
		const XMLCh * service,
		const XMLCh * id,
		ResultMajor rmaj,
		ResultMinor rmin) {

	return XKMSResultTypeImpl::createBlankResultType(
		XKMSConstants::s_tagCompoundResult, service, id, rmaj, rmin);

}

// --------------------------------------------------------------------------------
//           Get interface methods
// --------------------------------------------------------------------------------

XKMSMessageAbstractType::messageType XKMSCompoundResultImpl::getMessageType(void) {

	return XKMSMessageAbstractType::CompoundResult;

}

int XKMSCompoundResultImpl::getResultListSize(void) {

	return m_resultList.size();

}

XKMSResultType * XKMSCompoundResultImpl::getResultListItem(int item) {

	if (item < 0 || item >= m_resultList.size()) {

		throw XSECException(XSECException::XKMSError,
			"XKMSCompoundResult::getResultListItem - item out of range");
	}

	return m_resultList[item];


}

// --------------------------------------------------------------------------------
//           Setter methods
// --------------------------------------------------------------------------------

XKMSLocateResult * XKMSCompoundResultImpl::createLocateResult(
		XKMSLocateRequest * request,
		ResultMajor rmaj,
		ResultMinor rmin,
		const XMLCh * id) {

	XKMSLocateResult * r = m_factory.createLocateResult(request, mp_env->getParentDocument(), rmaj, rmin, id);
	m_resultList.push_back(dynamic_cast<XKMSResultTypeImpl*>(r));

	mp_messageAbstractTypeElement->appendChild(r->getElement());
	mp_env->doPrettyPrint(mp_messageAbstractTypeElement);

	return r;

}

XKMSValidateResult * XKMSCompoundResultImpl::createValidateResult(
		XKMSValidateRequest * request,
		ResultMajor rmaj,
		ResultMinor rmin,
		const XMLCh * id) {

	XKMSValidateResult * r = m_factory.createValidateResult(request, mp_env->getParentDocument(), rmaj, rmin, id);
	m_resultList.push_back(dynamic_cast<XKMSResultTypeImpl*>(r));

	mp_messageAbstractTypeElement->appendChild(r->getElement());
	mp_env->doPrettyPrint(mp_messageAbstractTypeElement);

	return r;

}


XKMSResult * XKMSCompoundResultImpl::createResult(
		XKMSRequestAbstractType * request,
		ResultMajor rmaj,
		ResultMinor rmin,
		const XMLCh * id) {

	XKMSResult * r = m_factory.createResult(request, mp_env->getParentDocument(), rmaj, rmin, id);
	m_resultList.push_back(dynamic_cast<XKMSResultTypeImpl*>(r));

	mp_messageAbstractTypeElement->appendChild(r->getElement());
	mp_env->doPrettyPrint(mp_messageAbstractTypeElement);

	return r;

}

