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
 * XKMSPendingRequestImpl := Implementation of PendingRequest Messages
 *
 * $Id$
 *
 */

#ifndef XKMSPENDINGREQUESTIMPL_INCLUDE
#define XKMSPENDINGREQUESTIMPL_INCLUDE

// XSEC Includes

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/xkms/XKMSPendingRequest.hpp>

#include "XKMSRequestAbstractTypeImpl.hpp"

class XKMSPendingRequestImpl : public XKMSRequestAbstractTypeImpl, public XKMSPendingRequest {

public:

	XKMSPendingRequestImpl(
		const XSECEnv * env
	);

	XKMSPendingRequestImpl(
		const XSECEnv * env, 
		XERCES_CPP_NAMESPACE_QUALIFIER DOMElement * node
	);

	virtual ~XKMSPendingRequestImpl();

	// Load elements
	void load();

	// Creation
	XERCES_CPP_NAMESPACE_QUALIFIER DOMElement * 
		createBlankPendingRequest(
		const XMLCh * service,
		const XMLCh * id = NULL);


	/* Getter Interface Methods */
	virtual const XMLCh * getResponseId(void) const;

	/* Setter interface methods */
	virtual void setResponseId(const XMLCh * responseId);

	/* Implemented from MessageAbstractType */
	virtual messageType getMessageType(void);

	/* Forced inheritance from XKMSMessageAbstractTypeImpl */
	XKMS_MESSAGEABSTRACTYPE_IMPL_METHODS

	/* Forced inheritance from RequestAbstractType */
	XKMS_REQUESTABSTRACTYPE_IMPL_METHODS

private:

	XERCES_CPP_NAMESPACE_QUALIFIER  DOMAttr
						* mp_responseIdAttr;

	// Unimplemented
	XKMSPendingRequestImpl(const XKMSPendingRequestImpl &);
	XKMSPendingRequestImpl & operator = (const XKMSPendingRequestImpl &);

};

#endif /* XKMSPENDINGREQUESTIMPL_INCLUDE */
