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
 * XKMSMessageAbstractTypeImpl := Implementation class for base XKMS messages
 *
 * $Id$
 *
 */

#ifndef XKMSMESSAGEABSTRACTTYPEIMPL_INCLUDE
#define XKMSMESSAGEABSTRACTTYPEIMPL_INCLUDE

// XSEC Includes

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/xkms/XKMSMessageAbstractType.hpp>
#include <xsec/framework/XSECProvider.hpp>

class XSECEnv;
class DSIGSignature;

XSEC_DECLARE_XERCES_CLASS(DOMElement);
XSEC_DECLARE_XERCES_CLASS(DOMAttr);

class XKMSMessageAbstractTypeImpl : virtual public XKMSMessageAbstractType {

public: 

	/* Constructors and Destructors */

	XKMSMessageAbstractTypeImpl(
		const XSECEnv * env, 
		XERCES_CPP_NAMESPACE_QUALIFIER DOMElement * node
	);

	virtual ~XKMSMessageAbstractTypeImpl() ;

	// load
	void load(void);

	/* Message Manipulation Methods */

	virtual XKMSMessageAbstractType::messageType getMessageType(void) = 0;

	/* Getter Interface Methods */

	virtual bool isSigned(void) const;
	virtual DSIGSignature * getSignature(void) const;
	virtual XERCES_CPP_NAMESPACE_QUALIFIER DOMElement * getElement(void) const;
	virtual const XMLCh * getId(void) const;
	virtual const XMLCh * getService(void) const;
	virtual const XMLCh * getNonce(void) const;

	/* Setter interface methods */

	virtual void setId(const XMLCh * id);
	virtual void setService(const XMLCh * service);
	virtual void setNonce(const XMLCh * uri);

protected:

	const XSECEnv		* mp_env;		// NOTE: Owned by the base message class

	XERCES_CPP_NAMESPACE_QUALIFIER DOMElement
						* mp_messageAbstractTypeElement;

private:

	XERCES_CPP_NAMESPACE_QUALIFIER  DOMAttr
						* mp_idAttr;
	XERCES_CPP_NAMESPACE_QUALIFIER  DOMAttr
						* mp_serviceAttr;
	XERCES_CPP_NAMESPACE_QUALIFIER  DOMAttr
						* mp_nonceAttr;
	XERCES_CPP_NAMESPACE_QUALIFIER  DOMElement
						* mp_signatureElement;

	XSECProvider		m_prov;
	DSIGSignature		* mp_signature;

	// Unimplemented
	XKMSMessageAbstractTypeImpl(void);
	XKMSMessageAbstractTypeImpl(const XKMSMessageAbstractTypeImpl &);
	XKMSMessageAbstractTypeImpl & operator = (const XKMSMessageAbstractTypeImpl &);

};

#endif /* XKMSMESSAGEABSTRACTTYPEIMPL_INCLUDE */
