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
 * XSECAlgorithmHandlerDefault := Interface class to define handling of
 *								  default encryption algorithms
 *
 * $Id$
 *
 */

// XSEC Includes

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/transformers/TXFMChain.hpp>
#include <xsec/transformers/TXFMCipher.hpp>
#include <xsec/transformers/TXFMBase64.hpp>
#include <xsec/xenc/XENCEncryptionMethod.hpp>
#include <xsec/enc/XSECCryptoKey.hpp>
#include <xsec/enc/XSECCryptoSymmetricKey.hpp>
#include <xsec/framework/XSECError.hpp>
#include <xsec/utils/XSECDOMUtils.hpp>

#include "XENCAlgorithmHandlerDefault.hpp"

#include <xercesc/dom/DOM.hpp>

XERCES_CPP_NAMESPACE_USE

// --------------------------------------------------------------------------------
//			Internal functions
// --------------------------------------------------------------------------------

void XENCAlgorithmHandlerDefault::mapURIToKey(const XMLCh * uri, XSECCryptoKey * key) {

	if (strEquals(uri, DSIGConstants::s_unicodeStrURI3DES_CBC)) {

		// 3 Key 3DES in CBC mode.
		if (key->getKeyType() != XSECCryptoKey::KEY_SYMMETRIC || 
			dynamic_cast<XSECCryptoSymmetricKey *>(key)->getSymmetricKeyType() !=
			XSECCryptoSymmetricKey::KEY_3DES_CBC_192) {

			throw XSECException(XSECException::CipherError, 
				"XENCAlgorithmHandlerDefault - 3DES Algorithm, but not a 3DES key");
		
		}
	}

}
	


// --------------------------------------------------------------------------------
//			SafeBuffer decryption
// --------------------------------------------------------------------------------

bool XENCAlgorithmHandlerDefault::decryptToSafeBuffer(
		TXFMChain * cipherText,
		XENCEncryptionMethod * encryptionMethod,
		XSECCryptoKey * key,
		DOMDocument * doc,
		safeBuffer & result
		) {


	// The default case is to just do a standard, padded block decrypt.
	// So the only thing we have to do is ensure key type matches URI.

	mapURIToKey(encryptionMethod->getAlgorithm(), key);

	// Add the decryption TXFM

	TXFMCipher * tcipher;
	XSECnew(tcipher, TXFMCipher(doc, key, false));

	cipherText->appendTxfm(tcipher);

	// Do the decrypt to the safeBuffer

	result.sbStrcpyIn("");
	result << cipherText->getLastTxfm();

	return true;

}

// --------------------------------------------------------------------------------
//			SafeBuffer encryption
// --------------------------------------------------------------------------------

bool XENCAlgorithmHandlerDefault::encryptToSafeBuffer(
		TXFMChain * plainText,
		XENCEncryptionMethod * encryptionMethod,
		XSECCryptoKey * key,
		XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument * doc,
		safeBuffer & result
		) {

	// Check the URI and key match

	mapURIToKey(encryptionMethod->getAlgorithm(), key);

	// Do the encryption
	TXFMCipher *tcipher;
	XSECnew(tcipher, TXFMCipher(doc, key, true));
	plainText->appendTxfm(tcipher);

	// Transform to Base64
	TXFMBase64 * tb64;
	XSECnew(tb64, TXFMBase64(doc, false));
	plainText->appendTxfm(tb64);

	// Read into the safeBuffer
	result = "";

	result << plainText->getLastTxfm();

	return true;

}

// --------------------------------------------------------------------------------
//			Clone
// --------------------------------------------------------------------------------

XSECAlgorithmHandler * XENCAlgorithmHandlerDefault::clone(void) const {

	XENCAlgorithmHandlerDefault * ret;
	XSECnew(ret, XENCAlgorithmHandlerDefault);

	return ret;

}



