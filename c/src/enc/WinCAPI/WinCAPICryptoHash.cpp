/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
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
 * WinCAPICryptoHash := Windows CAPI Implementation of Message digests
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */

#include <xsec/enc/WinCAPI/WinCAPICryptoHash.hpp>
#include <xsec/enc/XSECCryptoException.hpp>
#include <xsec/enc/WinCAPI/WinCAPICryptoProvider.hpp>

#include <memory.h>

// Constructors/Destructors

WinCAPICryptoHash::WinCAPICryptoHash(WinCAPICryptoProvider * owner, HashType alg) {

	mp_ownerProvider = owner;
	m_hashType = alg;
	m_h = 0;

	reset();
}


WinCAPICryptoHash::~WinCAPICryptoHash() {

	if (m_h != 0)
		CryptDestroyHash(m_h);

}



// Hashing Activities
void WinCAPICryptoHash::reset(void) {

	if (m_h != 0)
		CryptDestroyHash(m_h);

	m_h = 0;
	
	BOOL fResult;
	ALG_ID alg_id;

	switch (m_hashType) {

	case (XSECCryptoHash::HASH_SHA1) :
	
		alg_id = CALG_SHA;
		break;

	case (XSECCryptoHash::HASH_MD5) :
	
		alg_id = CALG_MD5;
		break;

	default :

		alg_id = 0;

	}

	if(alg_id == 0) {

		throw XSECCryptoException(XSECCryptoException::MDError,
			"WinCAPI:Hash - Unknown algorithm"); 
	}

	fResult = CryptCreateHash(
		mp_ownerProvider->getProviderDSS(),
		alg_id,
		0,
		0,
		&m_h);

	if (fResult == 0 || m_h == 0) {
		throw XSECCryptoException(XSECCryptoException::MDError,
			"WinCAPI:Hash - Error creating hash object"); 
	}

}

void WinCAPICryptoHash::hash(unsigned char * data, 
								 unsigned int length) {

	BOOL fResult = CryptHashData(
		m_h,
		data,
		length,
		0);

	if (fResult == 0) {
		throw XSECCryptoException(XSECCryptoException::MDError,
			"WinCAPI:Hash - Error Hashing Data"); 
	}

}

unsigned int WinCAPICryptoHash::finish(unsigned char * hash,
									   unsigned int maxLength) {

	DWORD retLen;
	BOOL fResult;

	retLen = WINCAPI_MAX_HASH_SIZE;

	fResult = CryptGetHashParam(
		m_h,
		HP_HASHVAL,
		m_mdValue,
		&retLen,
		0);

	if (fResult == 0) {
		throw XSECCryptoException(XSECCryptoException::MDError,
			"WinCAPI:Hash - Error getting hash value"); 
	}

	m_mdLen = retLen;
	retLen = (maxLength > m_mdLen ? m_mdLen : maxLength);
	memcpy(hash, m_mdValue, retLen);

	return (unsigned int) retLen;

}

// Get information

XSECCryptoHash::HashType WinCAPICryptoHash::getHashType(void) {

	return m_hashType;			// This could be any kind of hash

}

