/*
 * Copyright 2002-2005 The Apache Software Foundation.
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
 * OpenSSLCryptoProvider := Base class to define an OpenSSL module
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */

#include <xsec/framework/XSECDefs.hpp>
#if defined (HAVE_OPENSSL)

#include <xsec/framework/XSECError.hpp>

#include <xsec/enc/OpenSSL/OpenSSLCryptoProvider.hpp>
#include <xsec/enc/OpenSSL/OpenSSLCryptoHash.hpp>
#include <xsec/enc/OpenSSL/OpenSSLCryptoHashHMAC.hpp>
#include <xsec/enc/OpenSSL/OpenSSLCryptoBase64.hpp>
#include <xsec/enc/OpenSSL/OpenSSLCryptoX509.hpp>
#include <xsec/enc/OpenSSL/OpenSSLCryptoKeyDSA.hpp>
#include <xsec/enc/OpenSSL/OpenSSLCryptoKeyHMAC.hpp>
#include <xsec/enc/OpenSSL/OpenSSLCryptoKeyRSA.hpp>
#include <xsec/enc/OpenSSL/OpenSSLCryptoSymmetricKey.hpp>

#include <xsec/enc/XSCrypt/XSCryptCryptoBase64.hpp>

#include <xsec/enc/XSECCryptoException.hpp>

#include <openssl/rand.h>
#include <openssl/err.h>

OpenSSLCryptoProvider::OpenSSLCryptoProvider() {

	OpenSSL_add_all_algorithms();		// Initialise Openssl
	ERR_load_crypto_strings();

	//SSLeay_add_all_algorithms();

}


OpenSSLCryptoProvider::~OpenSSLCryptoProvider() {

	EVP_cleanup();
	ERR_free_strings();
	/* As suggested by Jesse Pelton */
	CRYPTO_cleanup_all_ex_data();
	RAND_cleanup();
	X509_TRUST_cleanup();
	ERR_remove_state(0);
}


const XMLCh * OpenSSLCryptoProvider::getProviderName() {

	return DSIGConstants::s_unicodeStrPROVOpenSSL;

}
	// Hashing classes

XSECCryptoHash	* OpenSSLCryptoProvider::hashSHA1() {

	OpenSSLCryptoHash * ret;

	XSECnew(ret, OpenSSLCryptoHash(XSECCryptoHash::HASH_SHA1));

	return ret;

}

XSECCryptoHash * OpenSSLCryptoProvider::hashHMACSHA1() {

	OpenSSLCryptoHashHMAC * ret;

	XSECnew(ret, OpenSSLCryptoHashHMAC(XSECCryptoHash::HASH_SHA1));

	return ret;

}

XSECCryptoHash	* OpenSSLCryptoProvider::hashSHA(int length) {


	OpenSSLCryptoHash * ret;

	switch (length) {

	case 160: XSECnew(ret, OpenSSLCryptoHash(XSECCryptoHash::HASH_SHA1));
		break;
	case 224: XSECnew(ret, OpenSSLCryptoHash(XSECCryptoHash::HASH_SHA224));
		break;
	case 256: XSECnew(ret, OpenSSLCryptoHash(XSECCryptoHash::HASH_SHA256));
		break;
	case 384: XSECnew(ret, OpenSSLCryptoHash(XSECCryptoHash::HASH_SHA384));
		break;
	case 512: XSECnew(ret, OpenSSLCryptoHash(XSECCryptoHash::HASH_SHA512));
		break;
	default:
		ret = NULL;
	}

	return ret;

}

XSECCryptoHash * OpenSSLCryptoProvider::hashHMACSHA(int length) {

	OpenSSLCryptoHashHMAC * ret;

	switch (length) {

	case 160: XSECnew(ret, OpenSSLCryptoHashHMAC(XSECCryptoHash::HASH_SHA1));
		break;
	case 224: XSECnew(ret, OpenSSLCryptoHashHMAC(XSECCryptoHash::HASH_SHA224));
		break;
	case 256: XSECnew(ret, OpenSSLCryptoHashHMAC(XSECCryptoHash::HASH_SHA256));
		break;
	case 384: XSECnew(ret, OpenSSLCryptoHashHMAC(XSECCryptoHash::HASH_SHA384));
		break;
	case 512: XSECnew(ret, OpenSSLCryptoHashHMAC(XSECCryptoHash::HASH_SHA512));
		break;
	default:
		ret = NULL;
	}

	return ret;

}

XSECCryptoHash	* OpenSSLCryptoProvider::hashMD5() {

	OpenSSLCryptoHash * ret;

	XSECnew(ret, OpenSSLCryptoHash(XSECCryptoHash::HASH_MD5));

	return ret;

}

XSECCryptoHash * OpenSSLCryptoProvider::hashHMACMD5() {

	OpenSSLCryptoHashHMAC * ret;

	XSECnew(ret, OpenSSLCryptoHashHMAC(XSECCryptoHash::HASH_MD5));

	return ret;

}

XSECCryptoKeyHMAC * OpenSSLCryptoProvider::keyHMAC(void) {

	OpenSSLCryptoKeyHMAC * ret;
	XSECnew(ret, OpenSSLCryptoKeyHMAC);

	return ret;

}

XSECCryptoKeyDSA * OpenSSLCryptoProvider::keyDSA() {
	
	OpenSSLCryptoKeyDSA * ret;

	XSECnew(ret, OpenSSLCryptoKeyDSA());

	return ret;

}

XSECCryptoKeyRSA * OpenSSLCryptoProvider::keyRSA() {
	
	OpenSSLCryptoKeyRSA * ret;

	XSECnew(ret, OpenSSLCryptoKeyRSA());

	return ret;

}


XSECCryptoX509 * OpenSSLCryptoProvider::X509() {

	OpenSSLCryptoX509 * ret;

	XSECnew(ret, OpenSSLCryptoX509());

	return ret;

}

XSECCryptoBase64 * OpenSSLCryptoProvider::base64() {

#if 0
	OpenSSLCryptoBase64 * ret;

	XSECnew(ret, OpenSSLCryptoBase64());
#else
	XSCryptCryptoBase64 *ret;
	XSECnew(ret, XSCryptCryptoBase64);

#endif
	return ret;

}

bool OpenSSLCryptoProvider::algorithmSupported(XSECCryptoSymmetricKey::SymmetricKeyType alg) {

	switch (alg) {

	case (XSECCryptoSymmetricKey::KEY_AES_128) :
	case (XSECCryptoSymmetricKey::KEY_AES_192) :
	case (XSECCryptoSymmetricKey::KEY_AES_256) :

#if !defined (XSEC_OPENSSL_HAVE_AES)
		return false;
#endif
	case (XSECCryptoSymmetricKey::KEY_3DES_192) :

		return true;

	default:

		return false;

	}

	return false;

}

bool OpenSSLCryptoProvider::algorithmSupported(XSECCryptoHash::HashType alg)  {

	switch (alg) {

	case (XSECCryptoHash::HASH_SHA1) :
	case (XSECCryptoHash::HASH_MD5) :

		return true;

	case (XSECCryptoHash::HASH_SHA224) :
	case (XSECCryptoHash::HASH_SHA256) :
	case (XSECCryptoHash::HASH_SHA384) :
	case (XSECCryptoHash::HASH_SHA512) :

		return 
#if defined (SHA512_DIGEST_LENGTH) && !defined (OPENSSL_NO_SHA512)
			true;
#else
			false;
#endif

	default:
		return false;
	}

	return false;

}


XSECCryptoSymmetricKey	* OpenSSLCryptoProvider::keySymmetric(XSECCryptoSymmetricKey::SymmetricKeyType alg) {

	OpenSSLCryptoSymmetricKey * ret;

	XSECnew(ret, OpenSSLCryptoSymmetricKey(alg));

	return ret;

}

unsigned int OpenSSLCryptoProvider::getRandom(unsigned char * buffer, unsigned int numOctets) {

	if (RAND_status() != 1) {

		throw XSECCryptoException(XSECCryptoException::GeneralError,
			"OpenSSLCryptoProvider::getRandom - OpenSSL random not properly initialised"); 
	}

	int res = RAND_bytes(buffer, numOctets);

	if (res == 0) {

		throw XSECCryptoException(XSECCryptoException::GeneralError,
			"OpenSSLCryptoProvider::getRandom - Error obtaining random octets"); 
	
	}

	return numOctets;

}


#endif /* HAVE_OPENSSL */
