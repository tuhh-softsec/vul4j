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
 * XKMSQueryKeyBindingImpl := Implementation for QueryKeyBinding
 *
 * $Id$
 *
 */

#ifndef XKMSQUERYKEYBINDINGIMPL_INCLUDE
#define XKMSQUERYKEYBINDINGIMPL_INCLUDE

// XSEC Includes

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/xkms/XKMSQueryKeyBinding.hpp>

#include "XKMSKeyBindingAbstractTypeImpl.hpp"

class XKMSQueryKeyBindingImpl : public XKMSQueryKeyBinding, public XKMSKeyBindingAbstractTypeImpl {

public:

	XKMSQueryKeyBindingImpl(
		const XSECEnv * env
	);

	XKMSQueryKeyBindingImpl(
		const XSECEnv * env, 
		XERCES_CPP_NAMESPACE_QUALIFIER DOMElement * node
	);

	virtual ~XKMSQueryKeyBindingImpl() ;

	// Load
	void load(void);

	// Create
	XERCES_CPP_NAMESPACE_QUALIFIER DOMElement *
		createBlankQueryKeyBinding(void);

	// Import methods from XKMSKeyBindingAbstractType
	XKMS_KEYBINDINGABSTRACTYPE_IMPL_METHODS

private:

	// Unimplemented
	XKMSQueryKeyBindingImpl(void);
	XKMSQueryKeyBindingImpl(const XKMSQueryKeyBindingImpl &);
	XKMSQueryKeyBindingImpl & operator = (const XKMSQueryKeyBindingImpl &);

};

#endif /* XKMSQUERYKEYBINDING_INCLUDE */
