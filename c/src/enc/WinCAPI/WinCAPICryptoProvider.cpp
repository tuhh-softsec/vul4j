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
 * WinCAPICryptoProvider := Provider to support Windows Crypto API
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */

#include <xsec/framework/XSECError.hpp>


#include <xsec/enc/WinCAPI/WinCAPICryptoProvider.hpp>
#include <xsec/enc/WinCAPI/WinCAPICryptoX509.hpp>
#include <xsec/enc/WinCAPI/WinCAPICryptoKeyDSA.hpp>
#include <xsec/enc/WinCAPI/WinCAPICryptoKeyRSA.hpp>
#include <xsec/enc/WinCAPI/WinCAPICryptoHash.hpp>
#include <xsec/enc/WinCAPI/WinCAPICryptoHashHMAC.hpp>
#include <xsec/enc/XSCrypt/XSCryptCryptoBase64.hpp>
#include <xsec/enc/XSECCryptoException.hpp>

#include <xercesc/util/Janitor.hpp>

XSEC_USING_XERCES(ArrayJanitor);

WinCAPICryptoProvider::WinCAPICryptoProvider(
						HCRYPTPROV provDSS,
						HCRYPTPROV provRSA) {

	// Copy parameters for later use

	m_provDSS = provDSS; 
	m_provRSA = provRSA;

}

WinCAPICryptoProvider::WinCAPICryptoProvider() {

	// Obtain default PROV_DSS and PROV_RSA_FULL, with default user key containers
	if (!CryptAcquireContext(&m_provDSS,
		NULL,
		NULL,
		PROV_DSS,
		CRYPT_VERIFYCONTEXT)) 
	{
		throw XSECException(XSECException::InternalError,
			"WinCAPICryptoProvider() - Error obtaining default PROV_DSS");
	}

	if (!CryptAcquireContext(&m_provRSA,
		NULL,
		NULL,
		PROV_RSA_FULL,
		CRYPT_VERIFYCONTEXT)) 
	{
		throw XSECException(XSECException::InternalError,
			"WinCAPICryptoProvider() - Error obtaining default PROV_RSA_FULL");
	}
}


WinCAPICryptoProvider::~WinCAPICryptoProvider() {

}

// Hashing classes

XSECCryptoHash	* WinCAPICryptoProvider::hashSHA1() {

	WinCAPICryptoHash * ret;

	XSECnew(ret, WinCAPICryptoHash(this, XSECCryptoHash::HASH_SHA1));

	return ret;

}

XSECCryptoHash * WinCAPICryptoProvider::hashHMACSHA1() {

	WinCAPICryptoHashHMAC * ret;

	XSECnew(ret, WinCAPICryptoHashHMAC(this, XSECCryptoHash::HASH_SHA1));

	return ret;

}

XSECCryptoHash	* WinCAPICryptoProvider::hashMD5() {

	WinCAPICryptoHash * ret;

	XSECnew(ret, WinCAPICryptoHash(this, XSECCryptoHash::HASH_MD5));

	return ret;

}

XSECCryptoHash * WinCAPICryptoProvider::hashHMACMD5() {

	WinCAPICryptoHashHMAC * ret;

	XSECnew(ret, WinCAPICryptoHashHMAC(this, XSECCryptoHash::HASH_MD5));

	return ret;

}

XSECCryptoKeyDSA * WinCAPICryptoProvider::keyDSA() {
	
	WinCAPICryptoKeyDSA * ret;

	XSECnew(ret, WinCAPICryptoKeyDSA(this));

	return ret;

}

XSECCryptoKeyRSA * WinCAPICryptoProvider::keyRSA() {
	
	WinCAPICryptoKeyRSA * ret;

	XSECnew(ret, WinCAPICryptoKeyRSA(this));

	return ret;

}


XSECCryptoX509 * WinCAPICryptoProvider::X509() {

	WinCAPICryptoX509 * ret;

	XSECnew(ret, WinCAPICryptoX509(this));

	return ret;

}

XSECCryptoBase64 * WinCAPICryptoProvider::base64() {

	// The Windows CAPI does not provide a Base64 decoder/encoder.
	// Use the internal implementation.

	XSCryptCryptoBase64 * ret;

	XSECnew(ret, XSCryptCryptoBase64());

	return ret;

}

// --------------------------------------------------------------------------------
//     Translate a Base64 number to a Windows (little endian) integer
// --------------------------------------------------------------------------------

BYTE * WinCAPICryptoProvider::b642WinBN(const char * b64, unsigned int b64Len, unsigned int &retLen) {

	BYTE * os;
	XSECnew(os, BYTE[b64Len]);
	ArrayJanitor<BYTE> j_os(os);

	// Decode
	XSCryptCryptoBase64 b;

	b.decodeInit();
	retLen = b.decode((unsigned char *) b64, b64Len, os, b64Len);
	retLen += b.decodeFinish(&os[retLen], b64Len - retLen);

	BYTE * ret;
	XSECnew(ret, BYTE[retLen]);

	BYTE * j = os;
	BYTE * k = ret + retLen - 1;

	for (unsigned int i = 0; i < retLen ; ++i)
		*k-- = *j++;

	return ret;

}

// --------------------------------------------------------------------------------
//     Translate a Windows integer to a Base64 I2OSP number 
// --------------------------------------------------------------------------------

unsigned char * WinCAPICryptoProvider::WinBN2b64(BYTE * n, DWORD nLen, unsigned int &retLen) {


	// First reverse
	BYTE * rev;;
	XSECnew(rev, BYTE[nLen]);
	ArrayJanitor<BYTE> j_rev(rev);

	BYTE * j = n;
	BYTE * k = rev + nLen - 1;

	for (unsigned int i = 0; i < nLen ; ++i)
		*k-- = *j++;

	
	unsigned char * b64;
	// Naieve length calculation
	unsigned int bufLen = nLen * 2 + 4;

	XSECnew(b64, unsigned char[bufLen]);
	ArrayJanitor<unsigned char> j_b64(b64);

	XSCryptCryptoBase64 b;

	b.encodeInit();
	retLen = b.encode(rev, (unsigned int) nLen, b64, bufLen);
	retLen += b.encodeFinish(&b64[retLen], bufLen - retLen);

	j_b64.release();
	return b64;

}

