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
	 * \brief Return a Symmetric Key implementation object.
	 *
	 * Call used by the library to obtain a bulk encryption
	 * object.
	 *
	 * @returns Pointer to the new SymmetricKey object
	 * @see XSECCryptoSymmetricKey
	 */

	virtual XSECCryptoSymmetricKey	* keySymmetric(XSECCryptoSymmetricKey::SymmetricKeyType alg);

	//@}

	/*\@}*/

};

#endif /* HAVE_OPENSSL */
#endif /* OPENSSLCRYPTOPROVIDER_INCLUDE */
