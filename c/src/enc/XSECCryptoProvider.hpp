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
 * XSECCryptoProvider := Base virtual class to define a crpyto module
 *
 * Author(s): Berin Lautenbach
 *
 * $ID$
 *
 * $LOG$
 *
 */

#ifndef XSECCRYPTOPROVIDER_INCLUDE
#define XSECCRYPTOPROVIDER_INCLUDE

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/enc/XSECCryptoHash.hpp>
#include <xsec/enc/XSECCryptoBase64.hpp>
#include <xsec/enc/XSECCryptoX509.hpp>
#include <xsec/enc/XSECCryptoKeyDSA.hpp>
#include <xsec/enc/XSECCryptoKeyRSA.hpp>

/**
 * @defgroup crypto Cryptographic Abstraction Layer
 * <p>The interface layer between the cryptographic modules and the XML 
 * Security library.  It has been created to allow users to easily 
 * integrate other cryptographic libraries into the XML-Security 
 * library.</p>
 *
 * <p>The XML-Security-C library itself makes calls to this interface 
 * layer to perform all cryptographic procedures.  In order to 
 * instantiate the correct object (i.e. the object
 * that belongs to the correct crypto library), XSEC makes calls to 
 * the virtual class XSECCryptoProvider, which returns pointers to 
 * particular virtual class objects.</p>
 *
 * <p>The particular instantiation of XSECCryptoProvider that is to 
 * be used is set via the XSECPlatformUtils#Initialise() function 
 * call.  If no provider is passed in, the Initialise function 
 * generates an OpenSSLCryptoProvider class for use.</p>
 *
 * <p>The provider is kept in a global variable, and is used by 
 * all signature objects created by a particular application.  At 
 * this time there is no way to have different signature
 * objects use different CryptoProviders</p>
 *
 * @note This abstraction layer is currently under construction and 
 * is very subject to change.  In particular, the layer currently 
 * has calls to do things like extract keys from X509 certificates.  
 * This is not strictly necessary for the library to work correctly 
 * and may be removed - or made optional.
 * @todo Add an ability to handle "optional" functions.  The library 
 * should make a call to the
 * provider to see whether an optional function (e.g. extract key from 
 * X509) has been
 * provided.
 *
 * @note The virtual classes (XSECCrypto*) only describe the functions
 * necessary for the library to work.  Other functions need to also
 * be implemented to allow applications to setup and manipulate the
 * objects appropriately.  These functions have not been defined as the
 * manner in which they work is likely to be impacted by the particular
 * cryptographic library being used.
 *
 *\@{*/

// Some constants

/**
 *\brief Maximum length (in bytes) of any expected Digest results.
 *
 * This constant defines the maximum length (in bytes) of HASH returns.
 *
 * @todo This should really come from each of the providers and should
 * be implemented as a function call to *CryptoProvider
 */

#define CRYPTO_MAX_HASH_SIZE		128

/**
 *\brief The base class that all *CryptoProviders need to implement.
 *
 * The instatiations of this class are used by the core library to
 * create cryptographic objects necessary for the library to do its work
 * without actually knowing any details at all about the provider library
 *
 * @note Subject to change
 * @see OpenSSLCryptoProvider
 */

class DSIG_EXPORT XSECCryptoProvider {


public :

	/**
	 * Enumeration of types of keys that must be handled by
	 * the CryptoProvider
	 */

	enum CryptoKeyType {

		KEY_NONE			= 0,        /**< Key is empty - type unknown */
		KEY_DSA_PUB			= 1,		/**< DSA key - Public part only */
		KEY_DSA_PRIV		= 2,		/**< DSA key - Private part only */
		KEY_DSA_PAIR		= 3,		/**< DSA key - Full Key Pair */

	};

	/**
	 * Enumeration of cryptographic algorithms that the provider must
	 * supply
	 */

	enum CryptoAlgorithmType {

		ALG_NONE			= 0,       /**< Used for catching errors */
		ALG_DSA				= 1,       /**< Digital Signature Algorithm */

	};


	/** @name Constructors and Destructors */
	//@{
	
	XSECCryptoProvider() {};
	virtual ~XSECCryptoProvider() {};
	//@}


	/** @name Hashing (Digest) Functions */
	//@{

	/**
	 * \brief Return a SHA1 implementation.
	 *
	 * Call used by the library to obtain a SHA1 object from the 
	 * provider.
	 *
	 * @returns A pointer to a Hash object that implements SHA1
	 * @see XSECCryptoHash
	 */
	 
	virtual XSECCryptoHash			* hashSHA1() = 0;

	/**
	 * \brief Return a HMAC SHA1 implementation.
	 *
	 * Call used by the library to obtain a HMAC SHA1 object from the 
	 * provider.  The caller will need to set the key in the hash
	 * object with an XSECCryptoKeyHMAC using XSECCryptoHash::setKey()
	 *
	 * @returns A pointer to a Hash object that implements HMAC-SHA1
	 * @see XSECCryptoHash
	 */

	virtual XSECCryptoHash			* hashHMACSHA1() = 0;

	/**
	 * \brief Return a MD5 implementation.
	 *
	 * Call used by the library to obtain a MD5 object from the 
	 * provider.
	 *
	 * @returns A pointer to a Hash object that implements MD5
	 * @see XSECCryptoHash
	 */
	 
	virtual XSECCryptoHash			* hashMD5() = 0;

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
	 * @see XSECCryptoHash
	 */

	virtual XSECCryptoHash			* hashHMACMD5() = 0;

	//@}

	/** @name Encoding functions */
	//@{

	/**
	 * \brief Return a Base64 encoder/decoder implementation.
	 *
	 * Call used by the library to obtain a Base64 encoder/decoder.
	 *
	 * @returns Pointer to the new Base64 encoder.
	 * @see XSECCryptoBase64
	 */

	virtual XSECCryptoBase64		* base64() = 0;

	//@}

	/** @name Keys and Certificates */
	//@{

	/**
	 * \brief Return a DSA key implementation object.
	 * 
	 * Call used by the library to obtain a DSA key object.
	 *
	 * @returns Pointer to the new DSA key
	 * @see XSECCryptoKeyDSA
	 */

	virtual XSECCryptoKeyDSA		* keyDSA() = 0;

	/**
	 * \brief Return an RSA key implementation object.
	 * 
	 * Call used by the library to obtain an RSA key object.
	 *
	 * @returns Pointer to the new RSA key
	 * @see XSECCryptoKeyRSA
	 */

	virtual XSECCryptoKeyRSA		* keyRSA() = 0;


	/**
	 * \brief Return an X509 implementation object.
	 * 
	 * Call used by the library to obtain an object that can work
	 * with X509 certificates.
	 *
	 * @returns Pointer to the new X509 object
	 * @see XSECCryptoX509
	 */
	virtual XSECCryptoX509			* X509() = 0;

	//@}

	/*\@}*/
};


#endif /* XSECCRYPTOPROVIDER_INCLUDE */
