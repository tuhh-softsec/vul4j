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
 * XKMSValidateRequestImpl := Implementation of ValidateRequest Messages
 *
 * $Id$
 *
 */

#ifndef XKMSVALIDATEREQUESTIMPL_INCLUDE
#define XKMSVALIDATEREQUESTIMPL_INCLUDE

// XSEC Includes

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/xkms/XKMSValidateRequest.hpp>

#include "XKMSRequestAbstractTypeImpl.hpp"
#include "XKMSQueryKeyBindingImpl.hpp"

class XKMSQueryKeyRequestImpl;

class XKMSValidateRequestImpl : public XKMSRequestAbstractTypeImpl, public XKMSValidateRequest {

public:

	XKMSValidateRequestImpl(
		const XSECEnv * env
	);

	XKMSValidateRequestImpl(
		const XSECEnv * env, 
		XERCES_CPP_NAMESPACE_QUALIFIER DOMElement * node
	);

	virtual ~XKMSValidateRequestImpl();

	// Load elements
	void load();

	// Creation
	XERCES_CPP_NAMESPACE_QUALIFIER DOMElement * 
		createBlankValidateRequest(
		const XMLCh * service,
		const XMLCh * id = NULL);


	/* Getter Interface Methods */

	virtual XKMSQueryKeyBinding * getQueryKeyBinding(void);

	/* Setter interface methods */
	virtual XKMSQueryKeyBinding * addQueryKeyBinding(void);

	/* Implemented from MessageAbstractType */
	virtual messageType getMessageType(void);

	/* Forced inheritance from XKMSMessageAbstractTypeImpl */
	XKMS_MESSAGEABSTRACTYPE_IMPL_METHODS

	/* Forced inheritance from RequestAbstractType */
	XKMS_REQUESTABSTRACTYPE_IMPL_METHODS

private:

	XERCES_CPP_NAMESPACE_QUALIFIER DOMElement
				* mp_queryKeyBindingElement;
	XKMSQueryKeyBindingImpl * mp_queryKeyBinding;

	// Unimplemented
	XKMSValidateRequestImpl(const XKMSValidateRequestImpl &);
	XKMSValidateRequestImpl & operator = (const XKMSValidateRequestImpl &);

};

#endif /* XKMSVALIDATEREQUESTIMPL_INCLUDE */
