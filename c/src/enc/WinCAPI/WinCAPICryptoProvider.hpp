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
 * WinCAPICryptoProvider := Base class to handle Windows Crypto API
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */

#ifndef WINCAPICRYPTOPROVIDER_INCLUDE
#define WINCAPICRYPTOPROVIDER_INCLUDE

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/enc/XSECCryptoProvider.hpp>

#define _WIN32_WINNT 0x0400
#include <wincrypt.h>


#define WINCAPI_BLOBHEADERLEN	0x08
#define WINCAPI_DSSPUBKEYLEN	0x08
#define WINCAPI_DSSSEEDLEN		0x18
#define WINCAPI_RSAPUBKEYLEN	0x0C

/**
 * @defgroup wincapicrypto Windows Crypto API Interface
 * @ingroup crypto
 * The WinCAPI crypto provides an experimental inerface to
 * the Windows Cryptographic API.
 *
 * All initialisation of the Windows providers needs to be done
 * by the calling application.  The interface will call the provided
 * DSS (PROV_DSS) provider and RSA (PROV_RSA_FULL) provider to perform
 * cryptographic functions.
 *
 * The tools use the default providers, but the calling application
 * can use any providers that implement PROV_DSS and PROV_FULL_RSA.
 *
 * Note that, unlike the OpenSSL classes, the various implementation 
 * classes all require their owner provider class to be passed into
 * the constructor.  This allows them to access the RSA and DSS CAPI
 * providers being used for the implementation.
 *
 * @todo Need to allow the various classes to over-ride the PROV
 * objects to allow specific private key instances rather than one
 * instance across the library instance.
 */
 /*\@{*/

class DSIG_EXPORT WinCAPICryptoProvider : public XSECCryptoProvider {


public :

	/** @name Constructors and Destructors */
	//@{
	/**
	 * \brief Create a Windows CAPI interface layer
	 *
	 * Windows CSPs work under a provider model.  The user should specify
	 * which CSP to use and which key container to use.
	 *
	 * @param provDSS DSS provider - must be of type PROV_DSS
	 * @param provRSA RSA provider - must be of type PROV_RSA_FULL
	 */
	
	WinCAPICryptoProvider(HCRYPTPROV provDSS, HCRYPTPROV provRSA);

	/**
	 * \brief Create a Windows CAPI interface layer.
	 *
	 * The default constructor will use the default providers and containers
	 * 
	 * @note This call will fail if the user has not generated keys in the
	 * default DSS and RSA provider containers
	 */

	WinCAPICryptoProvider();

	virtual ~WinCAPICryptoProvider();

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
	 * @see WinCAPICryptoHash
	 */

	virtual XSECCryptoHash			* hashSHA1();

	/**
	 * \brief Return a HMAC SHA1 implementation.
	 *
	 * Call used by the library to obtain a HMAC SHA1 object from the 
	 * provider.  The caller will need to set the key in the hash
	 * object with an XSECCryptoKeyHMAC using WinCAPICryptoHash::setKey()
	 *
	 * @returns A pointer to a Hash object that implements HMAC-SHA1
	 * @see WinCAPICryptoHash
	 */

	virtual XSECCryptoHash			* hashHMACSHA1();

	/**
	 * \brief Return a MD5 implementation.
	 *
	 * Call used by the library to obtain a MD5 object from the 
	 * OpenSSL provider.
	 *
	 * @returns A pointer to a Hash object that implements MD5
	 * @see WinCAPICryptoHash
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
	 * @see WinCAPICryptoHash
	 */

	virtual XSECCryptoHash			* hashHMACMD5();

	//@}

	/** @name Encoding functions */
	//@{

	/**
	 * \brief Return a Base64 encoder/decoder implementation.
	 *
	 * Call used by the library to obtain a Base64 
	 * encoder/decoder.
	 *
	 * @note Windows providers do not implement Base64, so the internal
	 * implementation (XSCrypt) is used instead.
	 * 
	 *
	 * @returns Pointer to the new Base64 encoder.
	 * @see XSCryptCryptoBase64
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
	 * @see WinCAPICryptoKeyDSA
	 */

	virtual XSECCryptoKeyDSA		* keyDSA();

	/**
	 * \brief Return an RSA key implementation object.
	 * 
	 * Call used by the library to obtain an OpenSSL RSA key object.
	 *
	 * @returns Pointer to the new RSA key
	 * @see WinCAPICryptoKeyRSA
	 */

	virtual XSECCryptoKeyRSA		* keyRSA();

	/**
	 * \brief Return an X509 implementation object.
	 * 
	 * Call used by the library to obtain an object that can work
	 * with X509 certificates.
	 *
	 * @returns Pointer to the new X509 object
	 * @see WinCAPICryptoX509
	 */

	virtual XSECCryptoX509			* X509();

	//@}

	/** @name Windows CAPI Specific methods */
	//@{

	/**
	 * \brief Returns the Crypto Provider being used for DSS
	 */

	HCRYPTPROV getProviderDSS(void) {return m_provDSS;}

	/**
	 * \brief Returns the Provider being used for RSA functions
	 */

	HCRYPTPROV getProviderRSA(void) {return m_provRSA;}

	/**
	 * \brief Translate B64 I2OS integer to a WinCAPI int.
	 *
	 * Decodes a Base64 (ds:CryptoBinary) integer and reverses the order to 
	 * allow loading into a Windows CAPI function.  (CAPI uses Little Endian 
	 * storage of integers).
	 *
	 * @param b64 Base 64 string
	 * @param b64Len Length of base64 string
	 * @param retLen Parameter to hold length of return integer
	 */

	static BYTE * b642WinBN(const char * b64, unsigned int b64Len, unsigned int &retLen);

	/**
	 * \brief Translate a WinCAPI int to a B64 I2OS integer .
	 *
	 * Encodes a Windows integer in I2OSP base64 encoded format.
	 *
	 * @param n Buffer holding the Windows Integer
	 * @param nLen Length of data in buffer
	 * @param retLen Parameter to hold length of return integer
	 * @returns A pointer to a buffer holding the encoded data 
	 * (transfers ownership)
	 */

	static unsigned char * WinBN2b64(BYTE * n, DWORD nLen, unsigned int &retLen);

	//@}

private:

	HCRYPTPROV		m_provDSS;
	HCRYPTPROV		m_provRSA;

};

/*\@}*/


#endif /* WINCAPICRYPTOPROVIDER_INCLUDE */

