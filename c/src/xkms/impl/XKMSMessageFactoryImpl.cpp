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

#include "XKMSMessageFactoryImpl.hpp"
#include "XKMSLocateRequestImpl.hpp"

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
