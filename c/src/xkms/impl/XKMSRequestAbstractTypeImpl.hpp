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
 * XKMSRequestAbstractTypeImpl := Implementation class for XKMS Request messages
 *
 * $Id$
 *
 */

#ifndef XKMSREQUESTABSTRACTTYPEIMPL_INCLUDE
#define XKMSREQUESTABSTRACTTYPEIMPL_INCLUDE

// XSEC Includes

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/xkms/XKMSRequestAbstractType.hpp>

#include "XKMSMessageAbstractTypeImpl.hpp"

#include <vector>

class XKMSRespondWithImpl;

class XKMSRequestAbstractTypeImpl : virtual public XKMSRequestAbstractType, public XKMSMessageAbstractTypeImpl {

public:

	/* Constructors and Destructors */
	XKMSRequestAbstractTypeImpl(
		const XSECEnv * env
	);
	XKMSRequestAbstractTypeImpl(
		const XSECEnv * env, 
		XERCES_CPP_NAMESPACE_QUALIFIER DOMElement * node
	);

	virtual ~XKMSRequestAbstractTypeImpl();

	virtual void load(void);

	// Create from scratch - tag is the element name to create
	XERCES_CPP_NAMESPACE_QUALIFIER DOMElement * 
		createBlankRequestAbstractType(
		const XMLCh * tag,
		const XMLCh * service,
		const XMLCh * id = NULL);

	/* Getter Interface Methods */

	virtual const XMLCh * getOriginalRequestId(void) const;
	virtual void setOriginalRequestId(const XMLCh * id);

	/* RespondWith handling */

	virtual int getRespondWithSize(void);
	virtual XKMSRespondWith * getRespondWithItem(int item);
	virtual const XMLCh * getRespondWithItemStr(int item);
	virtual void appendRespondWithItem(XKMSRespondWith * item);
	virtual void appendRespondWithItem(const XMLCh * item);

	/* Inherited from XKMSMessageType */
	virtual messageType getMessageType(void) = 0;

	/* Forced inheritance from XKMSMessageAbstractTypeImpl */
	virtual XERCES_CPP_NAMESPACE_QUALIFIER DOMElement * getElement(void) const
		{return XKMSMessageAbstractTypeImpl::getElement();}

	virtual bool isSigned(void) const
		{return XKMSMessageAbstractTypeImpl::isSigned();}
	virtual DSIGSignature * getSignature(void) const
		{return XKMSMessageAbstractTypeImpl::getSignature();}
	virtual const XMLCh * getId(void) const
		{return XKMSMessageAbstractTypeImpl::getId();}
	virtual const XMLCh * getService(void) const
		{return XKMSMessageAbstractTypeImpl::getService();}
	virtual const XMLCh * getNonce(void) const
		{return XKMSMessageAbstractTypeImpl::getNonce();}
	virtual void setId(const XMLCh * id)
		{XKMSMessageAbstractTypeImpl::setId(id);}
	virtual void setService(const XMLCh * service)
		{XKMSMessageAbstractTypeImpl::setService(service);}
	virtual void setNonce(const XMLCh * uri)
		{XKMSMessageAbstractTypeImpl::setNonce(uri);}


private:

#if defined(XSEC_NO_NAMESPACES)
	typedef vector<XKMSRespondWithImpl *>		RespondWithVectorType;
#else
	typedef std::vector<XKMSRespondWithImpl *>	RespondWithVectorType;
#endif

	RespondWithVectorType		m_respondWithList;		// List of m_respondWith elements

	// Unimplemented
	XKMSRequestAbstractTypeImpl(void);
	XKMSRequestAbstractTypeImpl(const XKMSRequestAbstractTypeImpl &);
	XKMSRequestAbstractTypeImpl & operator = (const XKMSRequestAbstractTypeImpl &);

};

#endif /* XKMSREQUESTABSTRACTTYPEIMPL_INCLUDE */
