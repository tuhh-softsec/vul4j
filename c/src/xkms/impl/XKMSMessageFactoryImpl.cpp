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

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/framework/XSECEnv.hpp>
#include <xsec/framework/XSECError.hpp>
#include <xsec/utils/XSECDOMUtils.hpp>
#include <xsec/xkms/XKMSConstants.hpp>

#include <xercesc/dom/DOM.hpp>
#include <xercesc/util/Janitor.hpp>

#include "XKMSMessageFactoryImpl.hpp"
#include "XKMSLocateRequestImpl.hpp"
#include "XKMSLocateResultImpl.hpp"
#include "XKMSResultImpl.hpp"
#include "XKMSValidateRequestImpl.hpp"
#include "XKMSValidateResultImpl.hpp"

XERCES_CPP_NAMESPACE_USE

// --------------------------------------------------------------------------------
//           Construct/Destruct
// --------------------------------------------------------------------------------

XKMSMessageFactoryImpl::XKMSMessageFactoryImpl(void) {

	// Factory isn't tied to a particular document

	XSECnew(mp_env, XSECEnv(NULL));
	mp_env->setDSIGNSPrefix(MAKE_UNICODE_STRING("ds"));

};

XKMSMessageFactoryImpl::~XKMSMessageFactoryImpl(void) {

	delete mp_env;

};

// --------------------------------------------------------------------------------
//			Set/get the namespace prefix to be used when creating nodes
// --------------------------------------------------------------------------------

void XKMSMessageFactoryImpl::setDSIGNSPrefix(const XMLCh * prefix) {

	mp_env->setDSIGNSPrefix(prefix);

}
void XKMSMessageFactoryImpl::setECNSPrefix(const XMLCh * prefix) {

	mp_env->setECNSPrefix(prefix);

}
void XKMSMessageFactoryImpl::setXPFNSPrefix(const XMLCh * prefix) {

	mp_env->setXPFNSPrefix(prefix);

}
void XKMSMessageFactoryImpl::setXENCNSPrefix(const XMLCh * prefix) {

	mp_env->setXENCNSPrefix(prefix);

}
void XKMSMessageFactoryImpl::setXKMSNSPrefix(const XMLCh * prefix) {

	mp_env->setXKMSNSPrefix(prefix);

}

// --------------------------------------------------------------------------------
//           DOM Based construction
// --------------------------------------------------------------------------------

XKMSMessageAbstractType * XKMSMessageFactoryImpl::newMessageFromDOM(
						XERCES_CPP_NAMESPACE_QUALIFIER DOMElement * elt) {


	if (elt == NULL) {

		throw XSECException(XSECException::XKMSError,
			"XKMSMessageFactory::newMessageFromDOM - called on empty DOM");

	}

	// See if this is a known element
	const XMLCh * name = getXKMSLocalName(elt);

	if (strEquals(name, XKMSConstants::s_tagLocateRequest)) {

		// This is a <LocateRequest> message
		XKMSLocateRequestImpl * ret;
		XSECnew(ret, XKMSLocateRequestImpl(new XSECEnv(*mp_env), elt));

		ret->load();

		return ret;

	}

	if (strEquals(name, XKMSConstants::s_tagValidateRequest)) {

		// This is a <ValidateRequest> message
		XKMSValidateRequestImpl * ret;
		XSECnew(ret, XKMSValidateRequestImpl(new XSECEnv(*mp_env), elt));

		ret->load();

		return ret;

	}

	else if (strEquals(name, XKMSConstants::s_tagLocateResult)) {

		// This is a <LocateRequest> message
		XKMSLocateResultImpl * ret;
		XSECnew(ret, XKMSLocateResultImpl(new XSECEnv(*mp_env), elt));
		Janitor<XKMSLocateResultImpl> j_ret(ret);

		ret->load();
		
		j_ret.release();
		return ret;

	}

	else if (strEquals(name, XKMSConstants::s_tagValidateResult)) {

		// This is a <LocateRequest> message
		XKMSValidateResultImpl * ret;
		XSECnew(ret, XKMSValidateResultImpl(new XSECEnv(*mp_env), elt));
		Janitor<XKMSValidateResultImpl> j_ret(ret);

		ret->load();
		
		j_ret.release();
		return ret;

	}

	else if (strEquals(name, XKMSConstants::s_tagResult)) {

		// This is a <LocateRequest> message
		XKMSResultImpl * ret;
		XSECnew(ret, XKMSResultImpl(new XSECEnv(*mp_env), elt));
		Janitor<XKMSResultImpl> j_ret(ret);

		ret->load();
		
		j_ret.release();
		return ret;

	}


	return NULL;

}

// --------------------------------------------------------------------------------
//           Construction from scratch
// --------------------------------------------------------------------------------

XKMSLocateRequest * XKMSMessageFactoryImpl::createLocateRequest(
		const XMLCh * service,
		XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument * doc,
		const XMLCh * id) {
	
	XKMSLocateRequestImpl * lri;

	XSECEnv * tenv;
	XSECnew(tenv, XSECEnv(*mp_env));
	tenv->setParentDocument(doc);

	XSECnew(lri, XKMSLocateRequestImpl(tenv));
	lri->createBlankLocateRequest(service, id);

	return lri;

}


XKMSLocateRequest * XKMSMessageFactoryImpl::createLocateRequest(
		const XMLCh * service,
		XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument **doc,
		const XMLCh * id) {


	// Create a document to put the element in

	XMLCh tempStr[100];
	XMLString::transcode("Core", tempStr, 99);    
	DOMImplementation *impl = DOMImplementationRegistry::getDOMImplementation(tempStr);

	*doc = impl->createDocument();

	// Embed the new structure in the document
	XKMSLocateRequest * lri = createLocateRequest(service, *doc, id);
	(*doc)->appendChild(lri->getElement());

	return lri;
}

XKMSValidateRequest * XKMSMessageFactoryImpl::createValidateRequest(
		const XMLCh * service,
		XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument * doc,
		const XMLCh * id) {
	
	XKMSValidateRequestImpl * vri;

	XSECEnv * tenv;
	XSECnew(tenv, XSECEnv(*mp_env));
	tenv->setParentDocument(doc);

	XSECnew(vri, XKMSValidateRequestImpl(tenv));
	vri->createBlankValidateRequest(service, id);

	return vri;

}


XKMSValidateRequest * XKMSMessageFactoryImpl::createValidateRequest(
		const XMLCh * service,
		XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument **doc,
		const XMLCh * id) {


	// Create a document to put the element in

	XMLCh tempStr[100];
	XMLString::transcode("Core", tempStr, 99);    
	DOMImplementation *impl = DOMImplementationRegistry::getDOMImplementation(tempStr);

	*doc = impl->createDocument();

	// Embed the new structure in the document
	XKMSValidateRequest * vri = createValidateRequest(service, *doc, id);
	(*doc)->appendChild(vri->getElement());

	return vri;
}

XKMSLocateResult * XKMSMessageFactoryImpl::createLocateResult(
		XKMSLocateRequest * request,
		XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument *doc,
		XKMSResultType::ResultMajor rmaj,
		XKMSResultType::ResultMinor rmin,
		const XMLCh * id) {

	XKMSLocateResultImpl * lri;

	XSECEnv * tenv;
	XSECnew(tenv, XSECEnv(*mp_env));
	tenv->setParentDocument(doc);

	XSECnew(lri, XKMSLocateResultImpl(tenv));
	lri->createBlankLocateResult(request->getService(), id, rmaj, rmin);

	return lri;

}

XKMSLocateResult * XKMSMessageFactoryImpl::createLocateResult(
		XKMSLocateRequest * request,
		XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument **doc,
		XKMSResultType::ResultMajor rmaj,
		XKMSResultType::ResultMinor rmin,
		const XMLCh * id) {

	// Create a document to put the element in

	XMLCh tempStr[100];
	XMLString::transcode("Core", tempStr, 99);    
	DOMImplementation *impl = DOMImplementationRegistry::getDOMImplementation(tempStr);

	*doc = impl->createDocument();

	// Embed the new structure in the document
	XKMSLocateResult * lr = createLocateResult(request, *doc, rmaj, rmin, id);
	(*doc)->appendChild(lr->getElement());

	return lr;
}

XKMSResult * XKMSMessageFactoryImpl::createResult(
		XKMSRequestAbstractType * request,
		XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument *doc,
		XKMSResultType::ResultMajor rmaj,
		XKMSResultType::ResultMinor rmin,
		const XMLCh * id) {

	XKMSResultImpl * ri;

	XSECEnv * tenv;
	XSECnew(tenv, XSECEnv(*mp_env));
	tenv->setParentDocument(doc);

	XSECnew(ri, XKMSResultImpl(tenv));
	ri->createBlankResult(request->getService(), id, rmaj, rmin);

	return ri;

}

XKMSResult * XKMSMessageFactoryImpl::createResult(
		XKMSRequestAbstractType * request,
		XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument **doc,
		XKMSResultType::ResultMajor rmaj,
		XKMSResultType::ResultMinor rmin,
		const XMLCh * id) {

	// Create a document to put the element in

	XMLCh tempStr[100];
	XMLString::transcode("Core", tempStr, 99);    
	DOMImplementation *impl = DOMImplementationRegistry::getDOMImplementation(tempStr);

	*doc = impl->createDocument();

	// Embed the new structure in the document
	XKMSResult * r = createResult(request, *doc, rmaj, rmin, id);
	(*doc)->appendChild(r->getElement());

	return r;
}

XKMSValidateResult * XKMSMessageFactoryImpl::createValidateResult(
		XKMSValidateRequest * request,
		XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument *doc,
		XKMSResultType::ResultMajor rmaj,
		XKMSResultType::ResultMinor rmin,
		const XMLCh * id) {

	XKMSValidateResultImpl * vri;

	XSECEnv * tenv;
	XSECnew(tenv, XSECEnv(*mp_env));
	tenv->setParentDocument(doc);

	XSECnew(vri, XKMSValidateResultImpl(tenv));
	vri->createBlankValidateResult(request->getService(), id, rmaj, rmin);

	return vri;

}

XKMSValidateResult * XKMSMessageFactoryImpl::createValidateResult(
		XKMSValidateRequest * request,
		XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument **doc,
		XKMSResultType::ResultMajor rmaj,
		XKMSResultType::ResultMinor rmin,
		const XMLCh * id) {

	// Create a document to put the element in

	XMLCh tempStr[100];
	XMLString::transcode("Core", tempStr, 99);    
	DOMImplementation *impl = DOMImplementationRegistry::getDOMImplementation(tempStr);

	*doc = impl->createDocument();

	// Embed the new structure in the document
	XKMSValidateResult * vr = createValidateResult(request, *doc, rmaj, rmin, id);
	(*doc)->appendChild(vr->getElement());

	return vr;
}

// --------------------------------------------------------------------------------
//           Message Conversions
// --------------------------------------------------------------------------------


XKMSRequestAbstractType * XKMSMessageFactoryImpl::toRequestAbstractType(XKMSMessageAbstractType *msg) {

	if (msg->getMessageType() == XKMSMessageAbstractType::LocateRequest) {
		XKMSLocateRequest * lr = dynamic_cast<XKMSLocateRequest*>(msg);
		return lr;

	}
	if (msg->getMessageType() == XKMSMessageAbstractType::ValidateRequest) {
		XKMSValidateRequest * vr = dynamic_cast<XKMSValidateRequest*>(msg);
		return vr;

	}
	return NULL;
}

XKMSResultType * XKMSMessageFactoryImpl::toResultType(XKMSMessageAbstractType *msg) {

	if (msg->getMessageType() == XKMSMessageAbstractType::LocateResult) {
		XKMSLocateResult * lr = dynamic_cast<XKMSLocateResult*>(msg);
		return lr;

	}
	if (msg->getMessageType() == XKMSMessageAbstractType::ValidateResult) {
		XKMSValidateResult * vr = dynamic_cast<XKMSValidateResult*>(msg);
		return vr;

	}
	return NULL;
}
