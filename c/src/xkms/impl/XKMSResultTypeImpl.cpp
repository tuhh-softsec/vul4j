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
 * XKMSResultTypeImpl := Implementation of base schema of XKMS Request messages
 *
 * $Id$
 *
 */

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/framework/XSECException.hpp>
#include <xsec/framework/XSECEnv.hpp>
#include <xsec/xkms/XKMSConstants.hpp>

#include "XKMSResultTypeImpl.hpp"

#include <xercesc/dom/DOM.hpp>
#include <xercesc/util/XMLUniDefs.hpp>

XERCES_CPP_NAMESPACE_USE

// --------------------------------------------------------------------------------
//           Constructor Destructor
// --------------------------------------------------------------------------------

XKMSResultTypeImpl::XKMSResultTypeImpl(
	const XSECEnv * env) :
XKMSMessageAbstractTypeImpl(env),
mp_resultMajorAttr(NULL),
mp_resultMinorAttr(NULL)
{

}

XKMSResultTypeImpl::XKMSResultTypeImpl(
	const XSECEnv * env, 
	DOMElement * node) :
XKMSMessageAbstractTypeImpl(env, node)
{

}

XKMSResultTypeImpl::~XKMSResultTypeImpl();
	
// --------------------------------------------------------------------------------
//           Load
// --------------------------------------------------------------------------------

void XKMSResultTypeImpl::load(void) {

	if (mp_messageAbstractTypeElement == NULL) {

		// Attempt to load an empty element
		throw XSECException(XSECException::ResultTypeError,
			"XKMSResultType::load - called on empty DOM");

	}

	XKMSMessageAbstractTypeImpl::load();

	/* Now load the result attributes */

	mp_resultMajorAttr = 
		mp_messageAbstractTypeElement->getAttributeNodeNS(NULL, XKMSConstants::s_tagResultMajor);
	mp_resultMinorAttr = 
		mp_messageAbstractTypeElement->getAttributeNodeNS(NULL, XKMSConstants::s_tagResultMinor);
	mp_requestIdAttr =
		mp_messageAbstractTypeElement->getAttributeNodeNS(NULL, XKMSConstants::s_tagRequestId);

	/* Decode responses */
	if (mp_resultMajorAttr == NULL) {
		// Attempt to load an empty element
		throw XSECException(XSECException::ResultTypeError,
			"XKMSResultType::load - No Major Response code found");
	}

	const XMLCh * res = mp_resultMajorAttr->getNodeValue();

	// This is actually an QName, but we cheat and find the ':' character by hand
	// without actually checking the qualifier.
	// TODO - CHECK the qualifier.

	int res2 = XMLString::indexOf(res, chColon);
	if (res2 != -1) {
		if (XMLString::compareNString(res, mp_messageAbstractTypeElement->getPrefix(), res2)) {
			throw XSECException(XSECException::ResultTypeError,
				"XKMSResultType::load - ResultType not in XKMS Name Space");
		}

		res = &res[res2+1];
	}
	else {
		if (mp_messageAbstractTypeElement->getPrefix() != NULL) {
			throw XSECException(XSECException::ResultTypeError,
				"XKMSResultType::load - ResultType not in XKMS Name Space");
		}
	}

	for (m_resultMajor = XKMSResultType::Pending; 
		m_resultMajor > XKMSResultType::NoneMajor; 
		m_resultMajor = (XKMSResultType::ResultMajor) (m_resultMajor-1)) {

		if (strEquals(XKMSConstants::s_tagResultMajorCodes[m_resultMajor], res))
			break;

	}

	if (mp_resultMinorAttr != NULL) {

		res = mp_resultMinorAttr->getNodeValue();
		int res2 = XMLString::indexOf(res, chColon);
		if (res2 != -1) {
			if (XMLString::compareNString(res, mp_messageAbstractTypeElement->getPrefix(), res2)) {
				throw XSECException(XSECException::ResultTypeError,
					"XKMSResultType::load - ResultType not in XKMS Name Space");
			}
			res = &res[res2+1];
		}
		else {
			if (mp_messageAbstractTypeElement->getPrefix() != NULL) {
				throw XSECException(XSECException::ResultTypeError,
					"XKMSResultType::load - ResultType not in XKMS Name Space");
			}
		}
		for (m_resultMinor = XKMSResultType::NotSynchronous; 
			m_resultMinor > XKMSResultType::NoneMinor; 
			m_resultMinor = (XKMSResultType::ResultMinor) (m_resultMinor-1)) {

			if (strEquals(XKMSConstants::s_tagResultMinorCodes[m_resultMinor], res))
				break;

		}
	}

	else
		m_resultMinor = XKMSResultType::NoneMinor;

}

// --------------------------------------------------------------------------------
//           Create from scratch
// --------------------------------------------------------------------------------

DOMElement * XKMSResultTypeImpl::createBlankResultType(
		const XMLCh * tag,
		const XMLCh * service,
		const XMLCh * id,
		ResultMajor rmaj,
		ResultMinor rmin) {

	DOMElement * ret = 
		XKMSMessageAbstractTypeImpl::createBlankMessageAbstractType(tag, service, id);

	safeBuffer s;

	s.sbXMLChIn(DSIGConstants::s_unicodeStrEmpty);

	if (mp_env->getXKMSNSPrefix() != NULL) {
		s.sbXMLChCat(mp_env->getXKMSNSPrefix());
		s.sbXMLChAppendCh(chColon);
	}

	s.sbXMLChCat(XKMSConstants::s_tagResultMajorCodes[rmaj]);

	ret->setAttributeNS(NULL, 
		XKMSConstants::s_tagResultMajor,
		s.rawXMLChBuffer());

	if (rmin != XKMSResultType::NoneMinor) {

		s.sbXMLChIn(DSIGConstants::s_unicodeStrEmpty);

		if (mp_env->getXKMSNSPrefix() != NULL) {
			s.sbXMLChCat(mp_env->getXKMSNSPrefix());
			s.sbXMLChAppendCh(chColon);
		}

		s.sbXMLChCat(XKMSConstants::s_tagResultMinorCodes[rmin]);
	
		ret->setAttributeNS(NULL, 
			XKMSConstants::s_tagResultMinor,
			s.rawXMLChBuffer());
	}

	m_resultMajor = rmaj;
	m_resultMinor = rmin;

	mp_resultMajorAttr = 
		mp_messageAbstractTypeElement->getAttributeNodeNS(NULL, XKMSConstants::s_tagResultMajor);
	mp_resultMinorAttr = 
		mp_messageAbstractTypeElement->getAttributeNodeNS(NULL, XKMSConstants::s_tagResultMinor);

	return ret;
}

// --------------------------------------------------------------------------------
//           Getter interface
// --------------------------------------------------------------------------------

XKMSResultType::ResultMajor XKMSResultTypeImpl::getResultMajor(void) const {

	return m_resultMajor;

}

XKMSResultType::ResultMinor XKMSResultTypeImpl::getResultMinor(void) const {

	return m_resultMinor;

}

const XMLCh * XKMSResultTypeImpl::getRequestId(void) const {

	if (mp_requestIdAttr != NULL)
		return mp_requestIdAttr->getNodeValue();
	
	return NULL;

}


// --------------------------------------------------------------------------------
//           Setter interface
// --------------------------------------------------------------------------------

void XKMSResultTypeImpl::setResultMajor(ResultMajor) {}
void XKMSResultTypeImpl::setResultMinor(ResultMinor) {}
void XKMSResultTypeImpl::setRequestId(const XMLCh * id) {}


