/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "<WebSig>" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 2001, Institute for
 * Data Communications Systems, <http://www.nue.et-inf.uni-siegen.de/>.
 * The development of this software was partly funded by the European 
 * Commission in the <WebSig> project in the ISIS Programme. 
 * For more information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

/*
 * XSEC
 *
 * XENCEncryptedKeyImpl := Implementation for holder object for EncryptedKeys
 *
 * $Id$
 *
 */

#include <xsec/framework/XSECDefs.hpp>

#include "XENCCipherImpl.hpp"
#include "XENCEncryptedKeyImpl.hpp"
#include "XENCCipherDataImpl.hpp"

#include <xsec/framework/XSECError.hpp>
#include <xsec/framework/XSECEnv.hpp>
#include <xsec/utils/XSECDOMUtils.hpp>

#include <xercesc/util/XMLUniDefs.hpp>

XERCES_CPP_NAMESPACE_USE

// --------------------------------------------------------------------------------
//			UNICODE Strings
// --------------------------------------------------------------------------------

static XMLCh s_EncryptedKey[] = {

	chLatin_E,
	chLatin_n,
	chLatin_c,
	chLatin_r,
	chLatin_y,
	chLatin_p,
	chLatin_t,
	chLatin_e,
	chLatin_d,
	chLatin_K,
	chLatin_e,
	chLatin_y,
	chNull
};

static XMLCh s_CarriedKeyName[] = {

	chLatin_C,
	chLatin_a,
	chLatin_r,
	chLatin_r,
	chLatin_i,
	chLatin_e,
	chLatin_d,
	chLatin_K,
	chLatin_e,
	chLatin_y,
	chLatin_N,
	chLatin_a,
	chLatin_m,
	chLatin_e,
	chNull
};

static XMLCh s_Recipient[] = {

	chLatin_R,
	chLatin_e,
	chLatin_c,
	chLatin_i,
	chLatin_p,
	chLatin_i,
	chLatin_e,
	chLatin_n,
	chLatin_t,
	chNull
};

// --------------------------------------------------------------------------------
//			Construct/Destruct
// --------------------------------------------------------------------------------


XENCEncryptedKeyImpl::XENCEncryptedKeyImpl(const XSECEnv * env) :
XENCEncryptedTypeImpl(env),
XENCEncryptedKey(env),
mp_carriedKeyNameTextNode(NULL),
mp_recipientAttr(NULL) {
	
}

XENCEncryptedKeyImpl::XENCEncryptedKeyImpl(const XSECEnv * env, DOMElement * node) :
XENCEncryptedTypeImpl(env, node),
XENCEncryptedKey(env),
mp_carriedKeyNameTextNode(NULL),
mp_recipientAttr(NULL) {

}

XENCEncryptedKeyImpl::~XENCEncryptedKeyImpl() {

}

// --------------------------------------------------------------------------------
//			Load
// --------------------------------------------------------------------------------

void XENCEncryptedKeyImpl::load(void) {

	if (mp_encryptedTypeElement == NULL) {

		// Attempt to load an empty encryptedData element
		throw XSECException(XSECException::EncryptedTypeError,
			"XENCEncryptedKey::load - called on empty DOM");

	}

	if (!strEquals(getXENCLocalName(mp_encryptedTypeElement), s_EncryptedKey)) {

		// Attempt to load an empty encryptedKey element
		throw XSECException(XSECException::EncryptedTypeError,
			"XENCEncryptedKey::load - called on non EncryptedData node");

	}

	// Now call the virtual function we overloaded to get here.
	XENCEncryptedTypeImpl::load();

	// Set up the keyInfo node
	mp_keyInfoDOMNode = mp_encryptedTypeElement;

	// Find the Recipient Attribute
	mp_recipientAttr = mp_encryptedTypeElement->getAttributeNodeNS(NULL, s_Recipient);

	// Now load specific EncryptedKey elements
	DOMNode * c = findFirstChildOfType(mp_encryptedTypeElement, DOMNode::ELEMENT_NODE);

	while (c != NULL) {

		if (strEquals(getXENCLocalName(c), s_CarriedKeyName)) {

			// Have a CarriedKeyName node
			mp_carriedKeyNameTextNode = findFirstChildOfType(c, DOMNode::TEXT_NODE);

		}

		c = findNextElementChild(c);

	}

}
// --------------------------------------------------------------------------------
//			Create from scratch
// --------------------------------------------------------------------------------

DOMElement * XENCEncryptedKeyImpl::createBlankEncryptedKey(
									XENCCipherData::XENCCipherDataType type, 
									const XMLCh * algorithm,
									const XMLCh * value) {

	DOMElement * ret = createBlankEncryptedType(s_EncryptedKey, type, algorithm, value);

	// Set up the KeyInfo information
	mp_keyInfoDOMNode = mp_encryptedTypeElement;

	return ret;

}

// --------------------------------------------------------------------------------
//			Interface Methods
// --------------------------------------------------------------------------------


const XMLCh * XENCEncryptedKeyImpl::getCarriedKeyName(void) const {

	if (mp_carriedKeyNameTextNode != NULL)
		return mp_carriedKeyNameTextNode->getNodeValue();

	return NULL;

}

const XMLCh * XENCEncryptedKeyImpl::getRecipient(void) const {

	if (mp_recipientAttr != NULL)
		return mp_recipientAttr->getNodeValue();

	return NULL;

}

void XENCEncryptedKeyImpl::setCarriedKeyName(const XMLCh * name) {

	if (mp_carriedKeyNameTextNode == NULL) {

		// Get some setup values
		safeBuffer str;
		DOMDocument *doc = XENCEncryptedTypeImpl::mp_env->getParentDocument();
		const XMLCh * prefix = XENCEncryptedTypeImpl::mp_env->getXENCNSPrefix();

		makeQName(str, prefix, s_CarriedKeyName);

		DOMElement *e = doc->createElementNS(DSIGConstants::s_unicodeStrURIXENC, str.rawXMLChBuffer());

		mp_encryptedTypeElement->appendChild(e);
		XENCEncryptedTypeImpl::mp_env->doPrettyPrint(mp_encryptedTypeElement);

		mp_carriedKeyNameTextNode = doc->createTextNode(name);
		e->appendChild(mp_carriedKeyNameTextNode);

	} 
	
	else {

		mp_carriedKeyNameTextNode->setNodeValue(name);

	}
}

void XENCEncryptedKeyImpl::setRecipient(const XMLCh * recipient) {

	if (mp_recipientAttr == NULL) {

		if (mp_encryptedTypeElement->getNodeType() != DOMNode::ELEMENT_NODE) {
			throw XSECException(XSECException::EncryptedTypeError,
				"XENCEncryptedKeyImpl::setRecipient - encryptedTypeNode is not an Element");
		}

		mp_encryptedTypeElement->setAttributeNS(NULL, 
											 s_Recipient,
											 recipient);
		// Now retrieve for later use
		mp_recipientAttr = mp_encryptedTypeElement->getAttributeNodeNS(NULL, s_Recipient);

		if (mp_recipientAttr == NULL) {

			throw XSECException(XSECException::EncryptionMethodError,
				"XENCEncryptionKey::setRecipient - Error creating Recipient Attribute");
		}
	} 
	
	else {

		mp_recipientAttr->setNodeValue(recipient);

	}

}

