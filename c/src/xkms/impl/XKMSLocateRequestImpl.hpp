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
 * XKMSLocateRequestImpl := Implementation of LocateRequest Messages
 *
 * $Id$
 *
 */

#ifndef XKMSLOCATEREQUESTIMPL_INCLUDE
#define XKMSLOCATEREQUESTIMPL_INCLUDE

// XSEC Includes

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/xkms/XKMSLocateRequest.hpp>

#include "XKMSRequestAbstractTypeImpl.hpp"
#include "XKMSQueryKeyBindingImpl.hpp"

class XKMSQueryKeyRequestImpl;

class XKMSLocateRequestImpl : public XKMSRequestAbstractTypeImpl, public XKMSLocateRequest {

public:

	XKMSLocateRequestImpl(
		const XSECEnv * env, 
		XERCES_CPP_NAMESPACE_QUALIFIER DOMElement * node
	);

	virtual ~XKMSLocateRequestImpl();

	// Load elements
	void load();

	/* Getter Interface Methods */

	virtual XERCES_CPP_NAMESPACE_QUALIFIER DOMElement * getElement(void) const;
	virtual XKMSQueryKeyBinding * getQueryKeyBinding(void);

	/* Implemented from MessageAbstractType */
	virtual messageType getMessageType(void);

	/* Forced inheritance from XKMSMessageAbstractTypeImpl */
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

	/* Forced inheritance from RequestAbstractType */
	virtual const XMLCh * getOriginalRequestId(void) const
		{return XKMSRequestAbstractTypeImpl::getOriginalRequestId();}
	virtual void setOriginalRequestId(const XMLCh * id)
		{XKMSRequestAbstractTypeImpl::setOriginalRequestId(id);}
	virtual int getRespondWithSize(void)
		{return XKMSRequestAbstractTypeImpl::getRespondWithSize();}	
	virtual XKMSRespondWith * getRespondWithItem(int item)
		{return XKMSRequestAbstractTypeImpl::getRespondWithItem(item);}	
	virtual const XMLCh * getRespondWithItemStr(int item)
		{return XKMSRequestAbstractTypeImpl::getRespondWithItemStr(item);}	
	virtual void appendRespondWithItem(XKMSRespondWith * item)
		{XKMSRequestAbstractTypeImpl::appendRespondWithItem(item);}
	virtual void appendRespondWithItem(const XMLCh * item)
		{XKMSRequestAbstractTypeImpl::appendRespondWithItem(item);}

private:

	XKMSQueryKeyBindingImpl * mp_queryKeyBinding;

	// Unimplemented
	XKMSLocateRequestImpl(const XKMSLocateRequestImpl &);
	XKMSLocateRequestImpl & operator = (const XKMSLocateRequestImpl &);

};

#endif /* XKMSLOCATEREQUESTIMPL_INCLUDE */
