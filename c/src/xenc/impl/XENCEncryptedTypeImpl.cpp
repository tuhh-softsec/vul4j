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

#include <xsec/framework/XSECError.hpp>
#include <xsec/utils/XSECDOMUtils.hpp>
#include <xsec/transformers/TXFMBase64.hpp>
#include <xsec/transformers/TXFMCipher.hpp>
#include <xsec/transformers/TXFMChain.hpp>
#include <xsec/transformers/TXFMSB.hpp>

#include <xercesc/util/XMLUniDefs.hpp>
#include <xercesc/util/Janitor.hpp>

XSEC_USING_XERCES(Janitor);
XSEC_USING_XERCES(ArrayJanitor);


// --------------------------------------------------------------------------------
//			UNICODE Strings
// --------------------------------------------------------------------------------

static XMLCh s_EncryptionMethod[] = {

	XERCES_CPP_NAMESPACE :: chLatin_E,
	XERCES_CPP_NAMESPACE :: chLatin_n,
	XERCES_CPP_NAMESPACE :: chLatin_c,
	XERCES_CPP_NAMESPACE :: chLatin_r,
	XERCES_CPP_NAMESPACE :: chLatin_y,
	XERCES_CPP_NAMESPACE :: chLatin_p,
	XERCES_CPP_NAMESPACE :: chLatin_t,
	XERCES_CPP_NAMESPACE :: chLatin_i,
	XERCES_CPP_NAMESPACE :: chLatin_o,
	XERCES_CPP_NAMESPACE :: chLatin_n,
	XERCES_CPP_NAMESPACE :: chLatin_M,
	XERCES_CPP_NAMESPACE :: chLatin_e,
	XERCES_CPP_NAMESPACE :: chLatin_t,
	XERCES_CPP_NAMESPACE :: chLatin_h,
	XERCES_CPP_NAMESPACE :: chLatin_o,
	XERCES_CPP_NAMESPACE :: chLatin_d,
	XERCES_CPP_NAMESPACE :: chNull,
};

static XMLCh s_KeyInfo[] = {

	XERCES_CPP_NAMESPACE :: chLatin_K,
	XERCES_CPP_NAMESPACE :: chLatin_e,
	XERCES_CPP_NAMESPACE :: chLatin_y,
	XERCES_CPP_NAMESPACE :: chLatin_I,
	XERCES_CPP_NAMESPACE :: chLatin_n,
	XERCES_CPP_NAMESPACE :: chLatin_f,
	XERCES_CPP_NAMESPACE :: chLatin_o,
	XERCES_CPP_NAMESPACE :: chNull,
};

static XMLCh s_CipherData[] = {

	XERCES_CPP_NAMESPACE :: chLatin_C,
	XERCES_CPP_NAMESPACE :: chLatin_i,
	XERCES_CPP_NAMESPACE :: chLatin_p,
	XERCES_CPP_NAMESPACE :: chLatin_h,
	XERCES_CPP_NAMESPACE :: chLatin_e,
	XERCES_CPP_NAMESPACE :: chLatin_r,
	XERCES_CPP_NAMESPACE :: chLatin_D,
	XERCES_CPP_NAMESPACE :: chLatin_a,
	XERCES_CPP_NAMESPACE :: chLatin_t,
	XERCES_CPP_NAMESPACE :: chLatin_a,
	XERCES_CPP_NAMESPACE :: chNull,
};

// --------------------------------------------------------------------------------
//			Constructors and Destructors
// --------------------------------------------------------------------------------

XENCEncryptedTypeImpl::XENCEncryptedTypeImpl(XENCCipherImpl * cipher) :
mp_cipher(cipher),
mp_encryptedTypeNode(NULL),
mp_cipherData(NULL),
mp_key(NULL) {

}


XENCEncryptedTypeImpl::XENCEncryptedTypeImpl(XENCCipherImpl * cipher, DOMNode * node) :
mp_cipher(cipher),
mp_encryptedTypeNode(node),
mp_cipherData(NULL),
mp_key(NULL) {

}

XENCEncryptedTypeImpl::~XENCEncryptedTypeImpl() {

	if (mp_cipherData != NULL)
		delete mp_cipherData;

	if (mp_key != NULL)
		delete mp_key;

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

	// Don't know what the node name should be (held by super class), 
	// so go straight to the children
	
	DOMNode *tmpElt = findFirstChildOfType(mp_encryptedTypeNode, DOMNode::ELEMENT_NODE);

	if (tmpElt != NULL && strEquals(getXENCLocalName(tmpElt), s_EncryptionMethod)) {

		// For now, ignore

		tmpElt = findNextChildOfType(tmpElt, DOMNode::ELEMENT_NODE);

	}

	if (tmpElt != NULL && strEquals(getDSIGLocalName(tmpElt), s_KeyInfo)) {

		// For now, ignore

		tmpElt = findNextChildOfType(tmpElt, DOMNode::ELEMENT_NODE);

	}

	if (tmpElt != NULL && strEquals(getXENCLocalName(tmpElt), s_CipherData)) {

		XSECnew(mp_cipherData, XENCCipherDataImpl(mp_cipher, tmpElt));
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
//			Create a txfm chain for this transform list
// --------------------------------------------------------------------------------

void XENCEncryptedTypeImpl::setKey(XSECCryptoKey * key) {

	if (key == NULL)
		return;

	if (mp_key != NULL)
		delete mp_key;

	mp_key = key->clone();

}

// --------------------------------------------------------------------------------
//			Create a txfm chain for this transform list
// --------------------------------------------------------------------------------

TXFMChain * XENCEncryptedTypeImpl::createDecryptionTXFMChain(void) {

	TXFMChain * chain;

	if (mp_cipherData->getCipherDataType() == XENCCipherData::CipherValue) {

		// Given we already have this in memory, we transcode to
		// local code page and then transform

		char * b64 = XMLString::transcode(mp_cipherData->getCipherValue()->getCipherString());
		ArrayJanitor<char> j_b64(b64);

		TXFMSB *sb;
		XSECnew(sb, TXFMSB(mp_cipher->getDocument()));

		sb->setInput(safeBuffer(b64));

		// Create a chain
		XSECnew(chain, TXFMChain(sb));

		// Create a base64 decoder
		TXFMBase64 * tb64;
		XSECnew(tb64, TXFMBase64(mp_cipher->getDocument()));

		chain->appendTxfm(tb64);

	}

	else {

		throw XSECException(XSECException::EncryptedTypeError,
			"XENCEncryptedType::createDecryptionTXFMChain - cannot process non CipherValue elements");

	}

	Janitor<TXFMChain> j_chain(chain);

	// Now add the decryption TXFM
	TXFMCipher * tcipher;
	XSECnew(tcipher, TXFMCipher(mp_cipher->getDocument(), mp_key, false));

	chain->appendTxfm(tcipher);

	j_chain.release();

	return chain;

}

