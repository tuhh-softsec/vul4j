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
 * XKMSRespondWithImpl := Implementation of XKMSRespondWith
 *
 * $Id$
 *
 */

// XSEC Includes

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/framework/XSECEnv.hpp>
#include <xsec/framework/XSECError.hpp>
#include <xsec/utils/XSECDOMUtils.hpp>

#include "XKMSRespondWithImpl.hpp"

#include <xercesc/dom/DOM.hpp>

XERCES_CPP_NAMESPACE_USE


// --------------------------------------------------------------------------------
//           Construct/Destruct
// --------------------------------------------------------------------------------


XKMSRespondWithImpl::XKMSRespondWithImpl(const XSECEnv * env) :
mp_env(env),
mp_respondWithTextNode(NULL)
{}

XKMSRespondWithImpl::XKMSRespondWithImpl(		
		const XSECEnv * env, 
		XERCES_CPP_NAMESPACE_QUALIFIER DOMElement * node) :
mp_env(env),
mp_respondWithElement(node),
mp_respondWithTextNode(NULL) {

}

XKMSRespondWithImpl::~XKMSRespondWithImpl() {}

// --------------------------------------------------------------------------------
//           Load
// --------------------------------------------------------------------------------

void XKMSRespondWithImpl::load(void) {

	if (mp_respondWithElement == NULL) {

		// Attempt to load an empty element
		throw XSECException(XSECException::XKMSError,
			"XKMSRespondWith::load - called on empty DOM");

	}

	mp_respondWithTextNode = findFirstChildOfType(mp_respondWithElement, DOMNode::TEXT_NODE);

	if (mp_respondWithTextNode == NULL) {

		throw XSECException(XSECException::ExpectedXKMSChildNotFound,
			"XKMSRespondWith::load - Expected TEXT node beneath <RespondWith> element");

	}

}

// --------------------------------------------------------------------------------
//           Get interface
// --------------------------------------------------------------------------------

const XMLCh * XKMSRespondWithImpl::getRespondWithString(void) const {

	if (mp_respondWithTextNode != NULL)
		return mp_respondWithTextNode->getNodeValue();

	throw XSECException(XSECException::XKMSError,
			"XKMSRespondWith::getRespondWithString - Attempt to get prior to initialisation");
	
}

// --------------------------------------------------------------------------------
//           Set interface
// --------------------------------------------------------------------------------

void XKMSRespondWithImpl::setRespondWithString(const XMLCh * str) {

	if (mp_respondWithTextNode != NULL)
		mp_respondWithTextNode->setNodeValue(str);
	else {

		throw XSECException(XSECException::XKMSError,
			"XKMSRespondWith::setRespondWithString - Attempt to set prior to initialisation");
	}
}

