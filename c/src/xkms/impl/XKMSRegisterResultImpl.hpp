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
 * XKMSRegisterResultImpl := Implementation of RegisterResult Messages
 *
 * $Id$
 *
 */

#ifndef XKMSREGISTERRESULTIMPL_INCLUDE
#define XKMSREGISTERRESULTIMPL_INCLUDE

// XSEC Includes

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/xkms/XKMSRegisterResult.hpp>

#include "XKMSResultTypeImpl.hpp"

#include <vector>

class XKMSKeyBindingImpl;

class XKMSRegisterResultImpl : public XKMSResultTypeImpl, public XKMSRegisterResult {

public:

	XKMSRegisterResultImpl(
		const XSECEnv * env
	);

	XKMSRegisterResultImpl(
		const XSECEnv * env, 
		XERCES_CPP_NAMESPACE_QUALIFIER DOMElement * node
	);

	virtual ~XKMSRegisterResultImpl();

	// Load elements
	void load();

	// Creation
	XERCES_CPP_NAMESPACE_QUALIFIER DOMElement * 
		createBlankRegisterResult(
		const XMLCh * service,
		const XMLCh * id,
		ResultMajor rmaj,
		ResultMinor rmin);

	// Interface methods
	virtual int getKeyBindingSize(void) const;
	virtual XKMSKeyBinding * getKeyBindingItem(int item) const;
	virtual XKMSKeyBinding * appendKeyBindingItem(XKMSStatus::StatusValue status);


	/* Implemented from MessageAbstractType */
	virtual messageType getMessageType(void);

	/* Forced inheritance from XKMSMessageAbstractTypeImpl */
	XKMS_MESSAGEABSTRACTYPE_IMPL_METHODS

	/* Forced inheritance from XKMSResultTypeImpl */
	XKMS_RESULTTYPE_IMPL_METHODS

private:

#if defined(XSEC_NO_NAMESPACES)
	typedef vector<XKMSKeyBindingImpl *>		KeyBindingVectorType;
#else
	typedef std::vector<XKMSKeyBindingImpl *>	KeyBindingVectorType;
#endif

	KeyBindingVectorType	m_keyBindingList;

	// Unimplemented
	XKMSRegisterResultImpl(void);
	XKMSRegisterResultImpl(const XKMSRegisterResultImpl &);
	XKMSRegisterResultImpl & operator = (const XKMSRegisterResultImpl &);

};

#endif /* XKMSREGISTERRESULTIMPL_INCLUDE */
