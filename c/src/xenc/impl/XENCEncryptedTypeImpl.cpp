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
 * XENCEncryptedTypeImpl := Implementation of the EncryptedType interface
 * element
 *
 * $Id$
 *
 */

#include <xsec/framework/XSECDefs.hpp>

#include "XENCCipherImpl.hpp"
#include "XENCCipherDataImpl.hpp"
#include "XENCEncryptedTypeImpl.hpp"
#include "XENCEncryptionMethodImpl.hpp"

#include <xsec/xenc/XENCEncryptedKey.hpp>

#include <xsec/framework/XSECError.hpp>
#include <xsec/utils/XSECDOMUtils.hpp>
#include <xsec/transformers/TXFMBase64.hpp>
#include <xsec/transformers/TXFMChain.hpp>
#include <xsec/transformers/TXFMSB.hpp>
#include <xsec/framework/XSECEnv.hpp>

#include <xercesc/util/XMLUniDefs.hpp>
#include <xercesc/util/Janitor.hpp>

XERCES_CPP_NAMESPACE_USE

// --------------------------------------------------------------------------------
//			UNICODE Strings
// --------------------------------------------------------------------------------

static XMLCh s_EncryptionMethod[] = {

	chLatin_E,
	chLatin_n,
	chLatin_c,
	chLatin_r,
	chLatin_y,
	chLatin_p,
	chLatin_t,
	chLatin_i,
	chLatin_o,
	chLatin_n,
	chLatin_M,
	chLatin_e,
	chLatin_t,
	chLatin_h,
	chLatin_o,
	chLatin_d,
	chNull,
};

static XMLCh s_KeyInfo[] = {

	chLatin_K,
	chLatin_e,
	chLatin_y,
	chLatin_I,
	chLatin_n,
	chLatin_f,
	chLatin_o,
	chNull,
};

static XMLCh s_CipherData[] = {

	chLatin_C,
	chLatin_i,
	chLatin_p,
	chLatin_h,
	chLatin_e,
	chLatin_r,
	chLatin_D,
	chLatin_a,
	chLatin_t,
	chLatin_a,
	chNull,
};

static XMLCh s_Type[] = {
	
	chLatin_T,
	chLatin_y,
	chLatin_p,
	chLatin_e,
	chNull
};

// --------------------------------------------------------------------------------
//			Constructors and Destructors
// --------------------------------------------------------------------------------

XENCEncryptedTypeImpl::XENCEncryptedTypeImpl(const XSECEnv * env) :
mp_env(env),
mp_encryptedTypeNode(NULL),
mp_keyInfoNode(NULL),
mp_cipherDataNode(NULL),
mp_cipherData(NULL),
mp_encryptionMethod(NULL),
m_keyInfoList(env),
mp_typeAttributeNode(NULL) {

}


XENCEncryptedTypeImpl::XENCEncryptedTypeImpl(const XSECEnv * env, DOMNode * node) :
mp_env(env),
mp_encryptedTypeNode(node),
mp_keyInfoNode(NULL),
mp_cipherDataNode(NULL),
mp_cipherData(NULL),
mp_encryptionMethod(NULL),
m_keyInfoList(env),
mp_typeAttributeNode(NULL) {

}

XENCEncryptedTypeImpl::~XENCEncryptedTypeImpl() {

	if (mp_cipherData != NULL)
		delete mp_cipherData;

	if (mp_encryptionMethod != NULL)
		delete mp_encryptionMethod;

}

// --------------------------------------------------------------------------------
//			Load DOM Structures
// --------------------------------------------------------------------------------

void XENCEncryptedTypeImpl::load() {

	if (mp_encryptedTypeNode == NULL) {

		// Attempt to load an empty encryptedType element
		throw XSECException(XSECException::EncryptedTypeError,
			"XENCEncryptedType::load - called on empty DOM");

	}

	// See if any attributes of interest are set
	DOMNamedNodeMap *atts = mp_encryptedTypeNode->getAttributes();

	// Type
	mp_typeAttributeNode = atts->getNamedItemNS(DSIGConstants::s_unicodeStrURIXENC,
												s_Type);

	// Don't know what the node name should be (held by super class), 
	// so go straight to the children
	
	DOMNode *tmpElt = findFirstChildOfType(mp_encryptedTypeNode, DOMNode::ELEMENT_NODE);

	if (tmpElt != NULL && strEquals(getXENCLocalName(tmpElt), s_EncryptionMethod)) {

		XSECnew(mp_encryptionMethod, XENCEncryptionMethodImpl(mp_env, tmpElt));
		mp_encryptionMethod->load();

		tmpElt = findNextChildOfType(tmpElt, DOMNode::ELEMENT_NODE);

	}

	if (tmpElt != NULL && strEquals(getDSIGLocalName(tmpElt), s_KeyInfo)) {

		// Load
		mp_keyInfoNode = tmpElt;
		m_keyInfoList.loadListFromXML(tmpElt);

		// Find the next node

		tmpElt = findNextChildOfType(tmpElt, DOMNode::ELEMENT_NODE);

	}

	if (tmpElt != NULL && strEquals(getXENCLocalName(tmpElt), s_CipherData)) {

		mp_cipherDataNode = tmpElt;

		XSECnew(mp_cipherData, XENCCipherDataImpl(mp_env, tmpElt));
		mp_cipherData->load();
		tmpElt = findNextChildOfType(tmpElt, DOMNode::ELEMENT_NODE);

	}

	else {

		throw XSECException(XSECException::ExpectedXENCChildNotFound,
			"Expected <CipherData> child of <EncryptedType>");

	}

	// Should check for EncryptionProperties

}

// --------------------------------------------------------------------------------
//			Create a blank structure
// --------------------------------------------------------------------------------

DOMElement * XENCEncryptedTypeImpl::createBlankEncryptedType(
						XMLCh * localName,
						XENCCipherData::XENCCipherDataType type, 
						const XMLCh * algorithm,
						const XMLCh * value) {

	// Reset
	mp_cipherData = NULL;
	mp_encryptionMethod = NULL;

	// Get some setup values
	safeBuffer str;
	DOMDocument *doc = mp_env->getParentDocument();
	const XMLCh * prefix = mp_env->getXENCNSPrefix();

	makeQName(str, prefix, localName);

	DOMElement *ret = doc->createElementNS(DSIGConstants::s_unicodeStrURIXENC, str.rawXMLChBuffer());
	mp_encryptedTypeNode = ret;

	// Set namespace
	if (prefix[0] == XERCES_CPP_NAMESPACE::chNull) {
		str.sbTranscodeIn("xmlns");
	}
	else {
		str.sbTranscodeIn("xmlns:");
		str.sbXMLChCat(prefix);
	}

	ret->setAttributeNS(DSIGConstants::s_unicodeStrURIXMLNS, 
							str.rawXMLChBuffer(), 
							DSIGConstants::s_unicodeStrURIXENC);

	mp_env->doPrettyPrint(ret);

	// Create the EncryptionMethod
	if (algorithm != NULL) {

		XSECnew(mp_encryptionMethod, XENCEncryptionMethodImpl(mp_env));
		DOMNode * encryptionMethodNode = 
			mp_encryptionMethod->createBlankEncryptedType(algorithm);

		ret->appendChild(encryptionMethodNode);

		mp_env->doPrettyPrint(ret);

	}

	// Create the cipher Data
	XSECnew(mp_cipherData, XENCCipherDataImpl(mp_env));
	mp_cipherDataNode = mp_cipherData->createBlankCipherData(type, value);

	// Add to EncryptedType
	ret->appendChild(mp_cipherDataNode);

	mp_env->doPrettyPrint(ret);

	return ret;

}

// --------------------------------------------------------------------------------
//			Create a txfm chain for this transform list
// --------------------------------------------------------------------------------

TXFMChain * XENCEncryptedTypeImpl::createCipherTXFMChain(void) {

	if (mp_cipherData->getCipherDataType() == XENCCipherData::VALUE_TYPE) {

		TXFMChain * chain;

		// Given we already have this in memory, we transcode to
		// local code page and then transform

		char * b64 = XMLString::transcode(mp_cipherData->getCipherValue()->getCipherString());
		ArrayJanitor<char> j_b64(b64);

		TXFMSB *sb;
		XSECnew(sb, TXFMSB(mp_env->getParentDocument()));

		sb->setInput(safeBuffer(b64));

		// Create a chain
		XSECnew(chain, TXFMChain(sb));

		// Create a base64 decoder
		TXFMBase64 * tb64;
		XSECnew(tb64, TXFMBase64(mp_env->getParentDocument()));

		chain->appendTxfm(tb64);

		return chain;

	}

	else {

		throw XSECException(XSECException::EncryptedTypeError,
			"XENCEncryptedType::createDecryptionTXFMChain - cannot process non CipherValue elements");

	}


}

// --------------------------------------------------------------------------------
//			Get Methods
// --------------------------------------------------------------------------------

XENCCipherData * XENCEncryptedTypeImpl::getCipherData(void) {

	return mp_cipherData;

}

XENCEncryptionMethod * XENCEncryptedTypeImpl::getEncryptionMethod(void) {
	
	return mp_encryptionMethod;

}

// --------------------------------------------------------------------------------
//			KeyInfo elements
// --------------------------------------------------------------------------------

void XENCEncryptedTypeImpl::clearKeyInfo(void) {

	if (mp_keyInfoNode == NULL)
		return;

	if (mp_encryptedTypeNode->removeChild(mp_keyInfoNode) != mp_keyInfoNode) {

		throw XSECException(XSECException::ExpectedDSIGChildNotFound,
			"Attempted to remove KeyInfo node but it is no longer a child of <EncryptedType>");

	}

	mp_keyInfoNode->release();		// No longer required

	mp_keyInfoNode = NULL;

	// Clear out the list
	m_keyInfoList.empty();

}

void XENCEncryptedTypeImpl::createKeyInfoElement(void) {

	if (mp_keyInfoNode != NULL)
		return;

	safeBuffer str;

	const XMLCh * prefixNS = mp_env->getDSIGNSPrefix();
	makeQName(str, prefixNS, "KeyInfo");

	mp_keyInfoNode = m_keyInfoList.createKeyInfo();

	// Place the node before the CipherData node
	if (mp_cipherDataNode == NULL) {

		throw XSECException(XSECException::EncryptedTypeError,
			"XENCEncryptedTypeImpl::createKeyInfoElement - unable to find CipherData node");

	}

	mp_encryptedTypeNode->insertBefore(mp_keyInfoNode, mp_cipherDataNode);

	if (mp_env->getPrettyPrintFlag() == true)
		mp_encryptedTypeNode->insertBefore(mp_env->getParentDocument()->createTextNode(DSIGConstants::s_unicodeStrNL), mp_cipherDataNode);
	
	// Need to add the DS namespace

	if (prefixNS[0] == '\0') {
		str.sbTranscodeIn("xmlns");
	}
	else {
		str.sbTranscodeIn("xmlns:");
		str.sbXMLChCat(prefixNS);
	}

	static_cast<DOMElement *>(mp_keyInfoNode)->setAttributeNS(DSIGConstants::s_unicodeStrURIXMLNS, 
							str.rawXMLChBuffer(), 
							DSIGConstants::s_unicodeStrURIDSIG);

}


DSIGKeyInfoName * XENCEncryptedTypeImpl::appendKeyName(const XMLCh * name, bool isDName) {

	createKeyInfoElement();
	return m_keyInfoList.appendKeyName(name, isDName);

}

void XENCEncryptedTypeImpl::appendEncryptedKey(XENCEncryptedKey * encryptedKey) {

	createKeyInfoElement();
	m_keyInfoList.addAndInsertKeyInfo(encryptedKey);

}

// --------------------------------------------------------------------------------
//			Type URI handling
// --------------------------------------------------------------------------------

const XMLCh * XENCEncryptedTypeImpl::getTypeURI(void) const {

	if (mp_typeAttributeNode != NULL)
		return mp_typeAttributeNode->getNodeValue();

	return NULL;

}

void XENCEncryptedTypeImpl::setTypeURI(const XMLCh * uri) {

	if (mp_typeAttributeNode != NULL) {
		mp_typeAttributeNode->setNodeValue(uri);
	}
	else {

		// Need to create the node
		DOMElement * typeElt = static_cast<DOMElement *>(mp_encryptedTypeNode);

		typeElt->setAttributeNS(DSIGConstants::s_unicodeStrURIXENC, s_Type, uri);

		DOMNamedNodeMap *atts = mp_encryptedTypeNode->getAttributes();
		mp_typeAttributeNode = atts->getNamedItemNS(DSIGConstants::s_unicodeStrURIXENC,
												s_Type);
		if (mp_typeAttributeNode = NULL) {

			throw XSECException(XSECException::InternalError,
				"XENCEncryptedTypeImpl::setTypeURI - Cannot find the attribute I just added");

		}

	}
}
