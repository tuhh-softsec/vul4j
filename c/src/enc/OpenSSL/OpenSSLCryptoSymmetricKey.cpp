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
 * XSECCryptoSymmetricKey := Bulk encryption algorithms should all be
 *							implemented via this interface
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/enc/OpenSSL/OpenSSLCryptoSymmetricKey.hpp>
#include <xsec/framework/XSECError.hpp>
#include <xsec/enc/XSECCryptoException.hpp>

// --------------------------------------------------------------------------------
//           Constructors and Destructors
// --------------------------------------------------------------------------------

OpenSSLCryptoSymmetricKey::OpenSSLCryptoSymmetricKey(XSECCryptoSymmetricKey::SymmetricKeyType type) :
m_keyType(type),
m_keyBuf(""),
m_keyLen(0),
m_initialised(false) {

	EVP_CIPHER_CTX_init(&m_ctx);
	m_keyBuf.isSensitive();

}

OpenSSLCryptoSymmetricKey::~OpenSSLCryptoSymmetricKey() {

	// Clean up the context

	EVP_CIPHER_CTX_cleanup(&m_ctx);
}

// --------------------------------------------------------------------------------
//           Basic Key interface methods
// --------------------------------------------------------------------------------

XSECCryptoSymmetricKey::SymmetricKeyType OpenSSLCryptoSymmetricKey::getSymmetricKeyType() {

	return m_keyType;

}

const XMLCh * OpenSSLCryptoSymmetricKey::getProviderName() {

	return DSIGConstants::s_unicodeStrPROVOpenSSL;

}

XSECCryptoKey * OpenSSLCryptoSymmetricKey::clone() {

	OpenSSLCryptoSymmetricKey * ret;

	XSECnew(ret, OpenSSLCryptoSymmetricKey(m_keyType));
	ret->m_keyLen = m_keyLen;
	ret->m_keyBuf = m_keyBuf;

	return ret;

}

// --------------------------------------------------------------------------------
//           Store the key value
// --------------------------------------------------------------------------------

void OpenSSLCryptoSymmetricKey::setKey(const unsigned char * key, unsigned int keyLen) {

	m_keyBuf.sbMemcpyIn(key, keyLen);
	m_keyLen = keyLen;

}

// --------------------------------------------------------------------------------
//           Decrypt
// --------------------------------------------------------------------------------

int OpenSSLCryptoSymmetricKey::decryptCtxInit(const unsigned char * iv) {

	// Returns amount of IV data used (in bytes)
	// Sets m_initialised iff the key is OK and the IV is OK.

	if (m_initialised)
		return 0;

	if (m_keyLen == 0) {

		throw XSECCryptoException(XSECCryptoException::SymmetricError,
			"OpenSSL:SymmetricKey - Cannot initialise without key"); 

	}

	// Set up the context according to the required cipher type

	switch (m_keyType) {

	case (XSECCryptoSymmetricKey::KEY_3DES_CBC_192) :

		// A 3DES key

		if (iv == NULL) {

			return 0;	// Cannot initialise without an IV

		}

		EVP_DecryptInit_ex(&m_ctx, EVP_des_ede3_cbc(), NULL, m_keyBuf.rawBuffer(), iv);
		// Turn off padding
		EVP_CIPHER_CTX_set_padding(&m_ctx, 0);

		// That means we have to handle padding, so we always hold back
		// 8 bytes of data.
		m_blockSize = 8;
		m_bytesInLastBlock = 0;

		return 8;	// 3DEC_CBC uses a 64 bit IV

		break;

	default :

		// Cannot do this without an IV
		throw XSECCryptoException(XSECCryptoException::SymmetricError,
			"OpenSSL:SymmetricKey - Unknown key type"); 

	}

	return 0;
}


bool OpenSSLCryptoSymmetricKey::decryptInit(const unsigned char * iv) {

	decryptCtxInit(iv);

	return true;

}

unsigned int OpenSSLCryptoSymmetricKey::decrypt(const unsigned char * inBuf, 
								 unsigned char * plainBuf, 
								 unsigned int inLength,
								 unsigned int maxOutLength) {



	// NOTE: This won't actually stop OpenSSL blowing the buffer, so the onus is
	// on the caller.

	unsigned int offset = 0;
	if (!m_initialised) {
		offset = decryptCtxInit(inBuf);
		if (offset > inLength) {
			throw XSECCryptoException(XSECCryptoException::SymmetricError,
			"OpenSSL:SymmetricKey - Not enough data passed in to get IV");
		}
	}

	int outl = maxOutLength;

	if (EVP_DecryptUpdate(&m_ctx, &plainBuf[m_bytesInLastBlock], &outl, &inBuf[offset], inLength - m_bytesInLastBlock - offset) == 0) {

		throw XSECCryptoException(XSECCryptoException::SymmetricError,
			"OpenSSL:SymmetricKey - Error during OpenSSL decrypt"); 

	}

	// Store the last block
	if (m_blockSize > 0 && outl >= m_blockSize) {

		// Output will always be *at least* the blocksize

		// Copy the previous last block into the start of the output buffer
		memcpy(plainBuf, m_lastBlock, m_bytesInLastBlock);

		// Copy the tail into the buffer
		memcpy(m_lastBlock, &plainBuf[outl + m_bytesInLastBlock - m_blockSize], m_blockSize);

		outl = outl + m_bytesInLastBlock - m_blockSize;
		m_bytesInLastBlock = m_blockSize;

	}

	return outl;

}

unsigned int OpenSSLCryptoSymmetricKey::decryptFinish(unsigned char * plainBuf,
													  unsigned int maxOutLength) {

	int outl = maxOutLength;

	if (EVP_DecryptFinal_ex(&m_ctx, plainBuf, &outl) == 0) {

		throw XSECCryptoException(XSECCryptoException::SymmetricError,
			"OpenSSL:SymmetricKey - Error during OpenSSL decrypt finalisation"); 

	}

	if (outl > 0) {
	
		// Should never see any bytes output, as we are not padding

		throw XSECCryptoException(XSECCryptoException::SymmetricError,
			"OpenSSL:SymmetricKey - Unexpectedly recieved bytes from EVP_DecryptFinal_ex"); 

	}

	// Calculate any padding issues
	if (m_bytesInLastBlock == m_blockSize) {

		outl = m_blockSize - m_lastBlock[m_blockSize - 1];

		if (outl > m_blockSize || outl < 0) {
			
			throw XSECCryptoException(XSECCryptoException::SymmetricError,
				"OpenSSL:SymmetricKey::decryptFinish - Out of range padding value in final block"); 
	
		}

		memcpy(plainBuf, m_lastBlock, outl);

	}

	return outl;

}

// --------------------------------------------------------------------------------
//           Encrypt
// --------------------------------------------------------------------------------

bool OpenSSLCryptoSymmetricKey::encryptInit(const unsigned char * iv) {

	if (m_initialised == true)
		return true;
	
	if (m_keyLen == 0) {

		throw XSECCryptoException(XSECCryptoException::SymmetricError,
			"OpenSSL:SymmetricKey - Cannot initialise without key"); 

	}

	m_initialised = true;

	// Set up the context according to the required cipher type

	const unsigned char * usedIV;
	const unsigned char tstIV[] = "abcdefghijklmnopqrstuvwxyz";

	// Tell the library that the IV still has to be sent
	m_ivSent = false;

	switch (m_keyType) {

	case (XSECCryptoSymmetricKey::KEY_3DES_CBC_192) :

		// A 3DES key

		if (iv == NULL) {
			
			usedIV = tstIV;
			//return 0;	// Cannot initialise without an IV

		}
		else
			usedIV = iv;

		EVP_EncryptInit_ex(&m_ctx, EVP_des_ede3_cbc(), NULL, m_keyBuf.rawBuffer(), usedIV);
		// Turn off padding
		// EVP_CIPHER_CTX_set_padding(&m_ctx, 0);

		// That means we have to handle padding, so we always hold back
		// 8 bytes of data.
		m_blockSize = 8;
		m_ivSize = 8;
		memcpy(m_lastBlock, usedIV, m_ivSize);
		m_bytesInLastBlock = 0;

		break;

	default :

		// Cannot do this without an IV
		throw XSECCryptoException(XSECCryptoException::SymmetricError,
			"OpenSSL:SymmetricKey - Unknown key type"); 

	}

	return true;

}

unsigned int OpenSSLCryptoSymmetricKey::encrypt(const unsigned char * inBuf, 
								 unsigned char * cipherBuf, 
								 unsigned int inLength,
								 unsigned int maxOutLength) {

	if (m_initialised == false) {

		encryptInit();

	}

	// NOTE: This won't actually stop OpenSSL blowing the buffer, so the onus is
	// on the caller.

	unsigned int offset = 0;
	if (m_ivSent == false && m_ivSize > 0) {

		memcpy(cipherBuf, m_lastBlock, m_ivSize);
		m_ivSent = true;

		offset = m_ivSize;

	}

	int outl = maxOutLength - offset;

	if (inLength + offset > maxOutLength) {

		throw XSECCryptoException(XSECCryptoException::SymmetricError,
			"OpenSSL:SymmetricKey - Not enough space in output buffer for encrypt"); 

	}

	if (EVP_EncryptUpdate(&m_ctx, &cipherBuf[offset], &outl, inBuf, inLength) == 0) {

		throw XSECCryptoException(XSECCryptoException::SymmetricError,
			"OpenSSL:SymmetricKey - Error during OpenSSL encrypt"); 

	}

	return outl + offset;

}

unsigned int OpenSSLCryptoSymmetricKey::encryptFinish(unsigned char * cipherBuf,
													  unsigned int maxOutLength) {

	int outl = maxOutLength;

	if (EVP_EncryptFinal_ex(&m_ctx, cipherBuf, &outl) == 0) {

		throw XSECCryptoException(XSECCryptoException::SymmetricError,
			"OpenSSL:SymmetricKey - Error during OpenSSL decrypt finalisation"); 

	}

	return outl;

}
