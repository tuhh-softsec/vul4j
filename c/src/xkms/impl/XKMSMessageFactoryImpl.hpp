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
 * XKMSMessageFactoryImpl := Implementation of the XKMSMessageFactory class
 *
 * $Id$
 *
 */

#ifndef XKMSMESSAGEFACTORYIMPL_INCLUDE
#define XKMSMESSAGEFACTORYIMPL_INCLUDE

// XSEC Includes

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/xkms/XKMSMessageFactory.hpp>

class XSECProvider;
class XSECEnv;

class XKMSMessageFactoryImpl : public XKMSMessageFactory {

	XKMSMessageFactoryImpl(void);

public:

	virtual ~XKMSMessageFactoryImpl();

	/* DOM based construction methods */

	virtual XKMSMessageAbstractType * newMessageFromDOM(
		XERCES_CPP_NAMESPACE_QUALIFIER DOMElement * elt);

	/* Construction from scratch */
	virtual XKMSLocateRequest * createLocateRequest(
		const XMLCh * service,
		XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument * doc,
		const XMLCh * id = NULL);		
	virtual XKMSLocateRequest * createLocateRequest(
		const XMLCh * service,
		XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument **doc,
		const XMLCh * id = NULL);


	/* Environment Manipulation */
	virtual void setDSIGNSPrefix(const XMLCh * prefix);
	virtual void setECNSPrefix(const XMLCh * prefix);
	virtual void setXPFNSPrefix(const XMLCh * prefix);
	virtual void setXENCNSPrefix(const XMLCh * prefix);
	virtual void setXKMSNSPrefix(const XMLCh * prefix);


	friend class XSECProvider;

private:

	// Environment
	XSECEnv					* mp_env;

	// Unimplemented
	XKMSMessageFactoryImpl(const XKMSMessageFactoryImpl &);
	XKMSMessageFactoryImpl & operator = (const XKMSMessageFactoryImpl &);

};

#endif /* XKMSMESSAGEFACTORYIMPL_INCLUDE */
