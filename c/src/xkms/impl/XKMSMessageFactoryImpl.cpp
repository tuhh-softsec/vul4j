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

#include "XKMSCompoundRequestImpl.hpp"
#include "XKMSCompoundResultImpl.hpp"
#include "XKMSMessageFactoryImpl.hpp"
#include "XKMSLocateRequestImpl.hpp"
#include "XKMSLocateResultImpl.hpp"
#include "XKMSStatusRequestImpl.hpp"
#include "XKMSStatusResultImpl.hpp"
#include "XKMSResultImpl.hpp"
#include "XKMSValidateRequestImpl.hpp"
#include "XKMSValidateResultImpl.hpp"
#include "XKMSPendingRequestImpl.hpp"
#include "XKMSRegisterRequestImpl.hpp"
#include "XKMSRegisterResultImpl.hpp"

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
//			Internal methods
// --------------------------------------------------------------------------------

void XKMSMessageFactoryImpl::copyRequestToResult(XKMSRequestAbstractType * req, 
												 XKMSResultType * res) {


	/* Set the requestId */
	res->setRequestId(req->getId());

	/* Copy any Opaque Data */

	int i;
	int sz = req->getOpaqueClientDataSize();
	for (i = 0; i < sz; ++i) {
		res->appendOpaqueClientDataItem(req->getOpaqueClientDataItemStr(i));
	}

	/* Set RequestSignatureValue data if necessary */
	sz = req->getResponseMechanismSize();
	for (i = 0; i < sz; ++i) {
		if (strEquals(req->getResponseMechanismItemStr(i), XKMSConstants::s_tagRequestSignatureValue)) {
			DSIGSignature *s = req->getSignature();
			if (s != NULL && res->getResultMinor() != XKMSResultType::NoAuthentication) {

				res->setRequestSignatureValue(s->getSignatureValue());

			}

			break;
		}
	}

}

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


	XSECEnv * env;
	XSECnew(env, XSECEnv(*mp_env));

	env->setParentDocument(elt->getOwnerDocument());

	if (elt == NULL) {

		throw XSECException(XSECException::XKMSError,
			"XKMSMessageFactory::newMessageFromDOM - called on empty DOM");

	}

	// See if this is a known element
	const XMLCh * name = getXKMSLocalName(elt);

	if (strEquals(name, XKMSConstants::s_tagCompoundRequest)) {

		// This is a <CompoundRequest> message
		XKMSCompoundRequestImpl * ret;
		XSECnew(ret, XKMSCompoundRequestImpl(env, elt));

		ret->load();

		return ret;

	}

	if (strEquals(name, XKMSConstants::s_tagCompoundResult)) {

		// This is a <CompoundResult> message
		XKMSCompoundResultImpl * ret;
		XSECnew(ret, XKMSCompoundResultImpl(env, elt));

		ret->load();

		return ret;

	}

	if (strEquals(name, XKMSConstants::s_tagLocateRequest)) {

		// This is a <LocateRequest> message
		XKMSLocateRequestImpl * ret;
		XSECnew(ret, XKMSLocateRequestImpl(env, elt));

		ret->load();

		return ret;

	}

	if (strEquals(name, XKMSConstants::s_tagValidateRequest)) {

		// This is a <ValidateRequest> message
		XKMSValidateRequestImpl * ret;
		XSECnew(ret, XKMSValidateRequestImpl(env, elt));

		ret->load();

		return ret;

	}

	else if (strEquals(name, XKMSConstants::s_tagLocateResult)) {

		// This is a <LocateRequest> message
		XKMSLocateResultImpl * ret;
		XSECnew(ret, XKMSLocateResultImpl(env, elt));
		Janitor<XKMSLocateResultImpl> j_ret(ret);

		ret->load();
		
		j_ret.release();
		return ret;

	}

	else if (strEquals(name, XKMSConstants::s_tagValidateResult)) {

		// This is a <LocateRequest> message
		XKMSValidateResultImpl * ret;
		XSECnew(ret, XKMSValidateResultImpl(env, elt));
		Janitor<XKMSValidateResultImpl> j_ret(ret);

		ret->load();
		
		j_ret.release();
		return ret;

	}

	else if (strEquals(name, XKMSConstants::s_tagResult)) {

		// This is a <LocateRequest> message
		XKMSResultImpl * ret;
		XSECnew(ret, XKMSResultImpl(env, elt));
		Janitor<XKMSResultImpl> j_ret(ret);

		ret->load();
		
		j_ret.release();
		return ret;

	}

	else if (strEquals(name, XKMSConstants::s_tagPendingRequest)) {

		// This is a <PendingRequest> message
		XKMSPendingRequestImpl * ret;
		XSECnew(ret, XKMSPendingRequestImpl(env, elt));
		Janitor<XKMSPendingRequestImpl> j_ret(ret);

		ret->load();
		
		j_ret.release();
		return ret;

	}

	else if (strEquals(name, XKMSConstants::s_tagStatusRequest)) {

		// This is a <StatusRequest> message
		XKMSStatusRequestImpl * ret;
		XSECnew(ret, XKMSStatusRequestImpl(env, elt));
		Janitor<XKMSStatusRequestImpl> j_ret(ret);

		ret->load();
		
		j_ret.release();
		return ret;

	}

	else if (strEquals(name, XKMSConstants::s_tagStatusResult)) {

		// This is a <StatusRequest> message
		XKMSStatusResultImpl * ret;
		XSECnew(ret, XKMSStatusResultImpl(env, elt));
		Janitor<XKMSStatusResultImpl> j_ret(ret);

		ret->load();
		
		j_ret.release();
		return ret;

	}

	else if (strEquals(name, XKMSConstants::s_tagRegisterRequest)) {

		// This is a <PendingRequest> message
		XKMSRegisterRequestImpl * ret;
		XSECnew(ret, XKMSRegisterRequestImpl(env, elt));
		Janitor<XKMSRegisterRequestImpl> j_ret(ret);

		ret->load();
		
		j_ret.release();
		return ret;

	}

	else if (strEquals(name, XKMSConstants::s_tagRegisterResult)) {

		// This is a <RegisterResult> message
		XKMSRegisterResultImpl * ret;
		XSECnew(ret, XKMSRegisterResultImpl(env, elt));
		Janitor<XKMSRegisterResultImpl> j_ret(ret);

		ret->load();
		
		j_ret.release();
		return ret;

	}

	delete env;
	return NULL;

}

// --------------------------------------------------------------------------------
//           Construction from scratch
// --------------------------------------------------------------------------------

XKMSCompoundRequest * XKMSMessageFactoryImpl::createCompoundRequest(
		const XMLCh * service,
		DOMDocument * doc,
		const XMLCh * id) {

	XKMSCompoundRequestImpl * cri;

	XSECEnv * tenv;
	XSECnew(tenv, XSECEnv(*mp_env));
	tenv->setParentDocument(doc);

	XSECnew(cri, XKMSCompoundRequestImpl(tenv));
	cri->createBlankCompoundRequest(service, id);

	return cri;

}

XKMSCompoundRequest * XKMSMessageFactoryImpl::createCompoundRequest(
		const XMLCh * service,
		DOMDocument **doc,
		const XMLCh * id) {

	// Create a document to put the element in

	XMLCh tempStr[100];
	XMLString::transcode("Core", tempStr, 99);    
	DOMImplementation *impl = DOMImplementationRegistry::getDOMImplementation(tempStr);

	*doc = impl->createDocument();

	// Embed the new structure in the document
	XKMSCompoundRequest * cri = createCompoundRequest(service, *doc, id);
	(*doc)->appendChild(cri->getElement());

	return cri;
}
	
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

XKMSPendingRequest * XKMSMessageFactoryImpl::createPendingRequest(
		const XMLCh * service,
		XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument * doc,
		const XMLCh * id) {
	
	XKMSPendingRequestImpl * pri;

	XSECEnv * tenv;
	XSECnew(tenv, XSECEnv(*mp_env));
	tenv->setParentDocument(doc);

	XSECnew(pri, XKMSPendingRequestImpl(tenv));
	pri->createBlankPendingRequest(service, id);

	return pri;

}


XKMSPendingRequest * XKMSMessageFactoryImpl::createPendingRequest(
		const XMLCh * service,
		XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument **doc,
		const XMLCh * id) {


	// Create a document to put the element in

	XMLCh tempStr[100];
	XMLString::transcode("Core", tempStr, 99);    
	DOMImplementation *impl = DOMImplementationRegistry::getDOMImplementation(tempStr);

	*doc = impl->createDocument();

	// Embed the new structure in the document
	XKMSPendingRequest * pri = createPendingRequest(service, *doc, id);
	(*doc)->appendChild(pri->getElement());

	return pri;
}

XKMSStatusRequest * XKMSMessageFactoryImpl::createStatusRequest(
		const XMLCh * service,
		XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument * doc,
		const XMLCh * id) {
	
	XKMSStatusRequestImpl* sri;

	XSECEnv * tenv;
	XSECnew(tenv, XSECEnv(*mp_env));
	tenv->setParentDocument(doc);

	XSECnew(sri, XKMSStatusRequestImpl(tenv));
	sri->createBlankStatusRequest(service, id);

	return sri;

}

XKMSStatusRequest * XKMSMessageFactoryImpl::createStatusRequest(
		const XMLCh * service,
		XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument **doc,
		const XMLCh * id) {


	// Create a document to put the element in

	XMLCh tempStr[100];
	XMLString::transcode("Core", tempStr, 99);    
	DOMImplementation *impl = DOMImplementationRegistry::getDOMImplementation(tempStr);

	*doc = impl->createDocument();

	// Embed the new structure in the document
	XKMSStatusRequest * sri = createStatusRequest(service, *doc, id);
	(*doc)->appendChild(sri->getElement());

	return sri;
}

XKMSRegisterRequest * XKMSMessageFactoryImpl::createRegisterRequest(
		const XMLCh * service,
		XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument * doc,
		const XMLCh * id) {
	
	XKMSRegisterRequestImpl * rri;

	XSECEnv * tenv;
	XSECnew(tenv, XSECEnv(*mp_env));
	tenv->setParentDocument(doc);

	XSECnew(rri, XKMSRegisterRequestImpl(tenv));
	rri->createBlankRegisterRequest(service, id);

	return rri;

}


XKMSRegisterRequest * XKMSMessageFactoryImpl::createRegisterRequest(
		const XMLCh * service,
		XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument **doc,
		const XMLCh * id) {


	// Create a document to put the element in

	XMLCh tempStr[100];
	XMLString::transcode("Core", tempStr, 99);    
	DOMImplementation *impl = DOMImplementationRegistry::getDOMImplementation(tempStr);

	*doc = impl->createDocument();

	// Embed the new structure in the document
	XKMSRegisterRequest * rri = createRegisterRequest(service, *doc, id);
	(*doc)->appendChild(rri->getElement());

	return rri;
}

// --------------------------------------------------------------------------------
//           Create a result based on a request
// --------------------------------------------------------------------------------

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

	copyRequestToResult(request, (XKMSResultTypeImpl*) lri);

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

XKMSStatusResult * XKMSMessageFactoryImpl::createStatusResult(
		XKMSStatusRequest * request,
		XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument *doc,
		XKMSResultType::ResultMajor rmaj,
		XKMSResultType::ResultMinor rmin,
		const XMLCh * id) {

	XKMSStatusResultImpl * sri;

	XSECEnv * tenv;
	XSECnew(tenv, XSECEnv(*mp_env));
	tenv->setParentDocument(doc);

	XSECnew(sri, XKMSStatusResultImpl(tenv));
	sri->createBlankStatusResult(request->getService(), id, rmaj, rmin);

	copyRequestToResult(request, (XKMSResultTypeImpl*) sri);

	return sri;

}

XKMSStatusResult * XKMSMessageFactoryImpl::createStatusResult(
		XKMSStatusRequest * request,
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
	XKMSStatusResult * sr = createStatusResult(request, *doc, rmaj, rmin, id);
	(*doc)->appendChild(sr->getElement());

	return sr;
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

	copyRequestToResult(request, (XKMSResultTypeImpl*) ri);

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

	copyRequestToResult(request, (XKMSResultTypeImpl*) vri);

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

XKMSCompoundResult * XKMSMessageFactoryImpl::createCompoundResult(
		XKMSCompoundRequest * request,
		XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument *doc,
		XKMSResultType::ResultMajor rmaj,
		XKMSResultType::ResultMinor rmin,
		const XMLCh * id) {

	XKMSCompoundResultImpl * cri;

	XSECEnv * tenv;
	XSECnew(tenv, XSECEnv(*mp_env));
	tenv->setParentDocument(doc);

	XSECnew(cri, XKMSCompoundResultImpl(tenv));
	cri->createBlankCompoundResult(request->getService(), id, rmaj, rmin);

	copyRequestToResult(request, (XKMSResultTypeImpl*) cri);

	return cri;

}

XKMSCompoundResult * XKMSMessageFactoryImpl::createCompoundResult(
		XKMSCompoundRequest * request,
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
	XKMSCompoundResult * cr = createCompoundResult(request, *doc, rmaj, rmin, id);
	(*doc)->appendChild(cr->getElement());

	return cr;
}

XKMSRegisterResult * XKMSMessageFactoryImpl::createRegisterResult(
		XKMSRegisterRequest * request,
		XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument *doc,
		XKMSResultType::ResultMajor rmaj,
		XKMSResultType::ResultMinor rmin,
		const XMLCh * id) {

	XKMSRegisterResultImpl * rri;

	XSECEnv * tenv;
	XSECnew(tenv, XSECEnv(*mp_env));
	tenv->setParentDocument(doc);

	XSECnew(rri, XKMSRegisterResultImpl(tenv));
	rri->createBlankRegisterResult(request->getService(), id, rmaj, rmin);

	copyRequestToResult(request, (XKMSResultTypeImpl*) rri);

	return rri;

}

XKMSRegisterResult * XKMSMessageFactoryImpl::createRegisterResult(
		XKMSRegisterRequest * request,
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
	XKMSRegisterResult * rr = createRegisterResult(request, *doc, rmaj, rmin, id);
	(*doc)->appendChild(rr->getElement());

	return rr;
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
	if (msg->getMessageType() == XKMSMessageAbstractType::CompoundRequest) {
		XKMSCompoundRequest * cr = dynamic_cast<XKMSCompoundRequest*>(msg);
		return cr;
	}
	if (msg->getMessageType() == XKMSMessageAbstractType::PendingRequest) {
		XKMSPendingRequest * pr = dynamic_cast<XKMSPendingRequest*>(msg);
		return pr;
	}
	if (msg->getMessageType() == XKMSMessageAbstractType::RegisterRequest) {
		XKMSRegisterRequest * rr = dynamic_cast<XKMSRegisterRequest*>(msg);
		return rr;
	}
	if (msg->getMessageType() == XKMSMessageAbstractType::StatusRequest) {
		XKMSStatusRequest * sr = dynamic_cast<XKMSStatusRequest*>(msg);
		return sr;
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
	if (msg->getMessageType() == XKMSMessageAbstractType::CompoundResult) {
		XKMSCompoundResult * cr = dynamic_cast<XKMSCompoundResult*>(msg);
		return cr;
	}
	if (msg->getMessageType() == XKMSMessageAbstractType::RegisterResult) {
		XKMSRegisterResult * rr = dynamic_cast<XKMSRegisterResult*>(msg);
		return rr;
	}
	if (msg->getMessageType() == XKMSMessageAbstractType::StatusResult) {
		XKMSStatusResult * sr = dynamic_cast<XKMSStatusResult*>(msg);
		return sr;
	}
	if (msg->getMessageType() == XKMSMessageAbstractType::Result) {
		XKMSResult * r = dynamic_cast<XKMSResult*>(msg);
		return r;
	}
	return NULL;
}
