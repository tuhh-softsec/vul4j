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
 * XKMSRegisterRequestImpl := Implementation for RegisterRequest Messages
 *
 * $Id$
 *
 */

// XSEC Includes

#include <xsec/framework/XSECDefs.hpp>

#include <xsec/dsig/DSIGReference.hpp>
#include <xsec/framework/XSECEnv.hpp>
#include <xsec/framework/XSECError.hpp>
#include <xsec/utils/XSECDOMUtils.hpp>
#include <xsec/xkms/XKMSConstants.hpp>

#include "XKMSRegisterRequestImpl.hpp"
#include "XKMSAuthenticationImpl.hpp"
#include "XKMSPrototypeKeyBindingImpl.hpp"

#include <xercesc/dom/DOM.hpp>
#include <xercesc/util/XMLUniDefs.hpp>

XERCES_CPP_NAMESPACE_USE

// --------------------------------------------------------------------------------
//           Construct/Destruct
// --------------------------------------------------------------------------------

XKMSRegisterRequestImpl::XKMSRegisterRequestImpl(const XSECEnv * env) :
XKMSRequestAbstractTypeImpl(env),
mp_authentication(NULL),
mp_prototypeKeyBinding(NULL),
mp_proofOfPossessionSignature(NULL) {
}

XKMSRegisterRequestImpl::XKMSRegisterRequestImpl(const XSECEnv * env, DOMElement * node) :
XKMSRequestAbstractTypeImpl(env, node),
mp_authentication(NULL),
mp_prototypeKeyBinding(NULL),
mp_proofOfPossessionSignature(NULL) {
}

XKMSRegisterRequestImpl::~XKMSRegisterRequestImpl() {

	if (mp_authentication != NULL)
		delete mp_authentication;
	if (mp_prototypeKeyBinding != NULL)
		delete mp_prototypeKeyBinding;
	// Provider will take care of the proofOfPossession signature

}

// --------------------------------------------------------------------------------
//           Load
// --------------------------------------------------------------------------------

void XKMSRegisterRequestImpl::load(void) {

	if (mp_messageAbstractTypeElement == NULL) {

		// Attempt to load an empty element
		throw XSECException(XSECException::XKMSError,
			"XKMSRegisterRequest::load - called on empty DOM");

	}

	if (!strEquals(getXKMSLocalName(mp_messageAbstractTypeElement), 
									XKMSConstants::s_tagRegisterRequest)) {
	
		throw XSECException(XSECException::XKMSError,
			"XKMSRegisterRequest::load - called on incorrect node");
	
	}

	// Load the base message
	XKMSRequestAbstractTypeImpl::load();

	// Now check for any PrototypeKeyBinding elements
	DOMElement * tmpElt = findFirstElementChild(mp_messageAbstractTypeElement);
	while (tmpElt != NULL && !strEquals(getXKMSLocalName(tmpElt), XKMSConstants::s_tagPrototypeKeyBinding)) {
		tmpElt = findNextElementChild(tmpElt);
	}

	if (tmpElt != NULL) {

		XSECnew(mp_prototypeKeyBinding, XKMSPrototypeKeyBindingImpl(mp_env, tmpElt));
		mp_prototypeKeyBinding->load();

		tmpElt = findNextElementChild(tmpElt);

	}
	else {

		throw XSECException(XSECException::ExpectedXKMSChildNotFound,
			"XKMSRegisterRequest::load - Expected PrototypeKeyBinding node");
	
	}

	// Authentication Element

	if (tmpElt != NULL && strEquals(getXKMSLocalName(tmpElt), XKMSConstants::s_tagAuthentication)) {

		XSECnew(mp_authentication, XKMSAuthenticationImpl(mp_env, tmpElt));
		mp_authentication->load(mp_prototypeKeyBinding->getId());

		tmpElt = findNextElementChild(tmpElt);

	}
	else {

		throw XSECException(XSECException::ExpectedXKMSChildNotFound,
			"XKMSRegisterRequest::load - Expected Authentication node");
	
	}

	if (tmpElt != NULL && strEquals(getXKMSLocalName(tmpElt), XKMSConstants::s_tagProofOfPossession)) {

		// Find the signature

		DOMElement * sigElt = (DOMElement *) findFirstElementChild(tmpElt);

		if (sigElt == NULL || !strEquals(getDSIGLocalName(sigElt), 
			XKMSConstants::s_tagSignature)) {
			throw XSECException(XSECException::ExpectedXKMSChildNotFound,
				"XKMSRegisterRequest::load - Expected Signature child of ProofOfPossession");

		}

		// The provider will take care of cleaning this up later.

		mp_proofOfPossessionSignature = m_prov.newSignatureFromDOM(mp_env->getParentDocument(), 
													  sigElt);
		mp_proofOfPossessionSignature->load();

		// Check the signature is across the correct input
			
		DSIGReferenceList * rl = 
			mp_proofOfPossessionSignature->getReferenceList();

		if (rl->getSize() != 1) {
			throw XSECException(XSECException::XKMSError,
				"XKMSRegisterRequestImpl::load - ProofOfPossession Signature with incorrect number of references found (should be 1)");
		}

		safeBuffer sb;
		sb.sbXMLChIn(DSIGConstants::s_unicodeStrEmpty);
		sb.sbXMLChAppendCh(chPound);
		sb.sbXMLChCat(mp_prototypeKeyBinding->getId());

		if (!strEquals(rl->item(0)->getURI(), sb.rawXMLChBuffer())) {
			throw XSECException(XSECException::XKMSError,
				"XKMSRegisterRequestImpl::load - ProofOfPossession Signature refers to incorrect Id (should be for PrototypeKeyBinding)");
		}

		// We don't actually check the signature as we have no key material to do so!
	}
}
	
	
// --------------------------------------------------------------------------------
//           Create
// --------------------------------------------------------------------------------

DOMElement * XKMSRegisterRequestImpl::
	createBlankRegisterRequest(const XMLCh * service, const XMLCh * id) {

	return XKMSRequestAbstractTypeImpl::createBlankMessageAbstractType(
		XKMSConstants::s_tagRegisterRequest, service, id);

}

// --------------------------------------------------------------------------------
//           MessageType 
// --------------------------------------------------------------------------------

XKMSMessageAbstractType::messageType XKMSRegisterRequestImpl::getMessageType(void) {

	return XKMSMessageAbstractType::RegisterRequest;

}

// --------------------------------------------------------------------------------
//           Get Methods
// --------------------------------------------------------------------------------

XKMSPrototypeKeyBinding * XKMSRegisterRequestImpl::getPrototypeKeyBinding(void) const {

	return mp_prototypeKeyBinding;

}

XKMSAuthentication * XKMSRegisterRequestImpl::getAuthentication (void) const {

	return mp_authentication;

}

DSIGSignature * XKMSRegisterRequestImpl::getProofOfPossessionSignature(void) const {

	return mp_proofOfPossessionSignature;

}

// --------------------------------------------------------------------------------
//           Set Methods
// --------------------------------------------------------------------------------

XKMSPrototypeKeyBinding * XKMSRegisterRequestImpl::addPrototypeKeyBinding(void) {

	if (mp_prototypeKeyBinding != NULL)
		return mp_prototypeKeyBinding;


	// OK - Nothing exists, so we need to create from scratch

	XSECnew(mp_prototypeKeyBinding, XKMSPrototypeKeyBindingImpl(mp_env));
	DOMElement * elt = mp_prototypeKeyBinding->createBlankPrototypeKeyBinding();

	// Insert

	DOMElement * be = findFirstElementChild(mp_messageAbstractTypeElement);

	while (be != NULL && 
		!strEquals(getXKMSLocalName(be), XKMSConstants::s_tagAuthentication) &&
		!strEquals(getXKMSLocalName(be), XKMSConstants::s_tagProofOfPossession)) {
		be = findNextElementChild(be);
	}

	if (be == NULL) {
		mp_env->doPrettyPrint(mp_messageAbstractTypeElement);
		mp_messageAbstractTypeElement->appendChild(elt);
		mp_env->doPrettyPrint(mp_messageAbstractTypeElement);
		return mp_prototypeKeyBinding;
	}

	mp_messageAbstractTypeElement->insertBefore(elt, be);
	if (mp_env->getPrettyPrintFlag() == true) {
		mp_messageAbstractTypeElement->insertBefore(
			mp_env->getParentDocument()->createTextNode(DSIGConstants::s_unicodeStrNL),
			be);
	}

	return mp_prototypeKeyBinding;

}

XKMSAuthentication * XKMSRegisterRequestImpl::addAuthentication(void) {

	if (mp_authentication != NULL)
		return mp_authentication;

	if (mp_prototypeKeyBinding == NULL) {
		throw XSECException(XSECException::XKMSError,
			"XKMSRegisterRequestImpl::addAuthentication - called prior to key infos being added");
	}

	XSECnew(mp_authentication, XKMSAuthenticationImpl(mp_env));
	DOMElement * e = 
		mp_authentication->createBlankAuthentication(mp_prototypeKeyBinding->getId());

	DOMElement * be = findFirstElementChild(mp_messageAbstractTypeElement);

	while (be != NULL && !strEquals(getXKMSLocalName(be), XKMSConstants::s_tagProofOfPossession))
		be = findNextElementChild(be);

	if (be == NULL) {
		mp_env->doPrettyPrint(mp_messageAbstractTypeElement);
		mp_messageAbstractTypeElement->appendChild(e);
		mp_env->doPrettyPrint(mp_messageAbstractTypeElement);
		return mp_authentication;
	}

	mp_messageAbstractTypeElement->insertBefore(e, be);
	if (mp_env->getPrettyPrintFlag() == true) {
		mp_messageAbstractTypeElement->insertBefore(
			mp_env->getParentDocument()->createTextNode(DSIGConstants::s_unicodeStrNL),
			be);
	}

	return mp_authentication;

}

DSIGSignature * XKMSRegisterRequestImpl::addProofOfPossessionSignature(
		canonicalizationMethod cm,
		signatureMethod	sm,
		hashMethod hm) {

	DSIGSignature * ret = m_prov.newSignature();
	DOMElement * elt = ret->createBlankSignature(mp_env->getParentDocument(), cm, sm, hm);

	/* Create the enveloping reference */
	safeBuffer sb;
	sb.sbXMLChIn(DSIGConstants::s_unicodeStrEmpty);
	sb.sbXMLChAppendCh(chPound);
	sb.sbXMLChCat(mp_prototypeKeyBinding->getId());

	DSIGReference *ref = ret->createReference(sb.rawXMLChBuffer());
	ref->appendCanonicalizationTransform(CANON_C14NE_COM);

	/* Embed the signature in the document inside a KeyBindingAuthentication element */
	safeBuffer str;
	DOMDocument *doc = mp_env->getParentDocument();
	const XMLCh * prefix = mp_env->getXKMSNSPrefix();

	makeQName(str, prefix, XKMSConstants::s_tagProofOfPossession);

	DOMElement * t = doc->createElementNS(XKMSConstants::s_unicodeStrURIXKMS, 
												str.rawXMLChBuffer());

	mp_env->doPrettyPrint(t);
	t->appendChild(elt);
	mp_env->doPrettyPrint(t);

	// Now append into the RegisterRequest
	mp_messageAbstractTypeElement->appendChild(t);
	mp_env->doPrettyPrint(mp_messageAbstractTypeElement);

	return ret;
}



