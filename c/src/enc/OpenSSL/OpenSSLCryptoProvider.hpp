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
 * OpenSSLCryptoProvider := Base class to define an OpenSSL module
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */

#ifndef OPENSSLCRYPTOPROVIDER_INCLUDE
#define OPENSSLCRYPTOPROVIDER_INCLUDE

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/enc/XSECCryptoProvider.hpp>

#if defined (HAVE_OPENSSL)

/**
 * @defgroup opensslcrypto OpenSSL Interface
 * @ingroup crypto
 * The OpenSSL/OpenSSL* classes provide an implementation of the 
 * XSECCrypto interface layer for OpenSSL.  The layer is very thin -
 * it only provides the functionality necessary to provide cryptographic
 * services to the library.
 *
 * Calling applications need to do the work to initialise OpenSSL, load
 * keys from disk etc.
 *
 */
 /*\@{*/

class DSIG_EXPORT OpenSSLCryptoProvider : public XSECCryptoProvider {


public :

	/** @name Constructors and Destructors */
	//@{
	
	OpenSSLCryptoProvider();
	virtual ~OpenSSLCryptoProvider();

	//@}

	/** @name Hashing (Digest) Functions */
	//@{

	/**
	 * \brief Return a SHA1 implementation.
	 *
	 * Call used by the library to obtain a SHA1 object from the 
	 * provider.
	 *
	 * @returns A pointer to an OpenSSL Hash object that implements SHA1
	 * @see XSECCryptoHash
	 */

	virtual XSECCryptoHash			* hashSHA1();

	/**
	 * \brief Return a HMAC SHA1 implementation.
	 *
	 * Call used by the library to obtain a HMAC SHA1 object from the 
	 * provider.  The caller will need to set the key in the hash
	 * object with an XSECCryptoKeyHMAC using OpenSSLCryptoHash::setKey()
	 *
	 * @returns A pointer to a Hash object that implements HMAC-SHA1
	 * @see OpenSSLCryptoHash
	 */

	virtual XSECCryptoHash			* hashHMACSHA1();

	/**
	 * \brief Return a MD5 implementation.
	 *
	 * Call used by the library to obtain a MD5 object from the 
	 * OpenSSL provider.
	 *
	 * @returns A pointer to a Hash object that implements MD5
	 * @see OpenSSLCryptoHash
	 */
	 
	virtual XSECCryptoHash			* hashMD5();

	/**
	 * \brief Return a HMAC MD5 implementation.
	 *
	 * Call used by the library to obtain a HMAC MD5 object from the 
	 * provider.  The caller will need to set the key in the hash
	 * object with an XSECCryptoKeyHMAC using XSECCryptoHash::setKey()
	 *
	 * @note The use of MD5 is explicitly marked as <b>not recommended</b> 
	 * in the XML Digital Signature standard due to recent advances in
	 * cryptography indicating there <em>may</em> be weaknesses in the 
	 * algorithm.
	 *
	 * @returns A pointer to a Hash object that implements HMAC-MD5
	 * @see OpenSSLCryptoHash
	 */

	virtual XSECCryptoHash			* hashHMACMD5();

	//@}

	/** @name Encoding functions */
	//@{

	/**
	 * \brief Return a Base64 encoder/decoder implementation.
	 *
	 * Call used by the library to obtain an OpenSSL Base64 
	 * encoder/decoder.
	 *
	 * @returns Pointer to the new Base64 encoder.
	 * @see OpenSSLCryptoBase64
	 */

	virtual XSECCryptoBase64		* base64();

	//@}

	/** @name Keys and Certificates */
	//@{

	/**
	 * \brief Return a DSA key implementation object.
	 * 
	 * Call used by the library to obtain a DSA key object.
	 *
	 * @returns Pointer to the new DSA key
	 * @see OpenSSLCryptoKeyDSA
	 */

	virtual XSECCryptoKeyDSA		* keyDSA();

	/**
	 * \brief Return an RSA key implementation object.
	 * 
	 * Call used by the library to obtain an OpenSSL RSA key object.
	 *
	 * @returns Pointer to the new RSA key
	 * @see OpenSSLCryptoKeyRSA
	 */

	virtual XSECCryptoKeyRSA		* keyRSA();

	/**
	 * \brief Return an X509 implementation object.
	 * 
	 * Call used by the library to obtain an object that can work
	 * with X509 certificates.
	 *
	 * @returns Pointer to the new X509 object
	 * @see OpenSSLCryptoX509
	 */

	virtual XSECCryptoX509			* X509();

	/**
	 * \brief Determine whether a given algorithm is supported
	 *
	 * A call that can be used to determine whether a given 
	 * symmetric algorithm is supported
	 */

	virtual bool algorithmSupported(XSECCryptoSymmetricKey::SymmetricKeyType alg);

	/**
	 * \brief Return a Symmetric Key implementation object.
	 *
	 * Call used by the library to obtain a bulk encryption
	 * object.
	 *
	 * @returns Pointer to the new SymmetricKey object
	 * @see XSECCryptoSymmetricKey
	 */

	virtual XSECCryptoSymmetricKey	* keySymmetric(XSECCryptoSymmetricKey::SymmetricKeyType alg);

	/**
	 * \brief Obtain some random octets
	 *
	 * For generation of IVs and the like, the library needs to be able
	 * to obtain "random" octets.  The library uses this call to the 
	 * crypto provider to obtain what it needs.
	 *
	 * @param buffer The buffer to place the random data in
	 * @param numOctets Number of bytes required
	 * @returns Number of bytes obtained.
	 */

	virtual unsigned int getRandom(unsigned char * buffer, unsigned int numOctets);


	//@}

	/** @name Information Functions */
	//@{

	/**
	 * \brief Returns a string that identifies the Crypto Provider
	 */

	virtual const XMLCh * getProviderName();

	//@}

	/*\@}*/

};

#endif /* HAVE_OPENSSL */
#endif /* OPENSSLCRYPTOPROVIDER_INCLUDE */
