/*
 * Copyright 2002-2004 The Apache Software Foundation.
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
 * NSSCryptoProvider := Provider to support NSS
 *
 * Author(s): Milan Tomic
 *
 * $Id$
 *
 */

#include <xsec/framework/XSECError.hpp>


#include <xsec/enc/NSS/NSSCryptoProvider.hpp>
#include <xsec/enc/NSS/NSSCryptoX509.hpp>
#include <xsec/enc/NSS/NSSCryptoKeyDSA.hpp>
#include <xsec/enc/NSS/NSSCryptoKeyRSA.hpp>
#include <xsec/enc/NSS/NSSCryptoHash.hpp>
#include <xsec/enc/NSS/NSSCryptoHashHMAC.hpp>
#include <xsec/enc/NSS/NSSCryptoSymmetricKey.hpp>
#include <xsec/enc/XSCrypt/XSCryptCryptoBase64.hpp>
#include <xsec/enc/XSECCryptoException.hpp>

#include "nss/pk11func.h"
#include "nss/nss.h"

#include <xercesc/util/Janitor.hpp>

XSEC_USING_XERCES(ArrayJanitor);

//static char s_xsecKeyStoreName[] = "ApacheXML-SecurityKeyStore";

NSSCryptoProvider::NSSCryptoProvider()
{

	NSS_NoDB_Init(".");

}

NSSCryptoProvider::~NSSCryptoProvider()
{

	

}

const XMLCh * NSSCryptoProvider::getProviderName() {

	return DSIGConstants::s_unicodeStrPROVNSS;

}


// Hashing classes

XSECCryptoHash	* NSSCryptoProvider::hashSHA1() {

	NSSCryptoHash * ret;

	XSECnew(ret, NSSCryptoHash(XSECCryptoHash::HASH_SHA1));

	return ret;

}

XSECCryptoHash * NSSCryptoProvider::hashHMACSHA1() {

	NSSCryptoHashHMAC * ret;

	XSECnew(ret, NSSCryptoHashHMAC(XSECCryptoHash::HASH_SHA1));

	return ret;

}

XSECCryptoHash	* NSSCryptoProvider::hashMD5() {

	NSSCryptoHash * ret;

	XSECnew(ret, NSSCryptoHash(XSECCryptoHash::HASH_MD5));

	return ret;

}

XSECCryptoHash * NSSCryptoProvider::hashHMACMD5() {

	NSSCryptoHashHMAC * ret;

	XSECnew(ret, NSSCryptoHashHMAC(XSECCryptoHash::HASH_MD5));

	return ret;

}

XSECCryptoKeyDSA * NSSCryptoProvider::keyDSA() {
	
	NSSCryptoKeyDSA * ret;

	//XSECnew(ret, NSSCryptoKeyDSA(m_provDSS));

	return ret;

}

XSECCryptoKeyRSA * NSSCryptoProvider::keyRSA() {
	
	NSSCryptoKeyRSA * ret;

	//XSECnew(ret, NSSCryptoKeyRSA(m_provRSA));

	return ret;

}


XSECCryptoX509 * NSSCryptoProvider::X509() {

	NSSCryptoX509 * ret;

	//XSECnew(ret, NSSCryptoX509(m_provRSA, m_provDSS));

	return ret;

}

XSECCryptoBase64 * NSSCryptoProvider::base64() {

	// The NSS does not provide a Base64 decoder/encoder.
	// Use the internal implementation.

	XSCryptCryptoBase64 * ret;

	XSECnew(ret, XSCryptCryptoBase64());

	return ret;

}

bool NSSCryptoProvider::algorithmSupported(XSECCryptoSymmetricKey::SymmetricKeyType alg) {

	switch (alg) {

	case (XSECCryptoSymmetricKey::KEY_AES_128) :
	case (XSECCryptoSymmetricKey::KEY_AES_192) :
	case (XSECCryptoSymmetricKey::KEY_AES_256) :

		return m_haveAES;

	case (XSECCryptoSymmetricKey::KEY_3DES_192) :

		return true;

	default:

		return false;

	}

	return false;

}

XSECCryptoSymmetricKey	* NSSCryptoProvider::keySymmetric(XSECCryptoSymmetricKey::SymmetricKeyType alg) {

	// Only temporary

	NSSCryptoSymmetricKey * ret;
	
	//XSECnew(ret, NSSCryptoSymmetricKey(m_provApacheKeyStore, alg));

	return ret;

	//return NULL;

}

unsigned int NSSCryptoProvider::getRandom(unsigned char * buffer, unsigned int numOctets) {

	/*if (!CryptGenRandom(m_provApacheKeyStore, numOctets, buffer)) {
		throw XSECException(XSECException::InternalError,
			"NSSCryptoProvider() - Error generating Random data");
	}*/

	return numOctets;
}

