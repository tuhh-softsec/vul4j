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
 * XSECCryptoKeyRSA := RSA Keys
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */

#ifndef OPENSSLCRYPTOKEYRSA_INCLUDE
#define OPENSSLCRYPTOKEYRSA_INCLUDE

#include <xsec/enc/XSECCryptoKeyRSA.hpp>

#if defined (HAVE_OPENSSL)
#include <openssl/evp.h>

/**
 * \ingroup opensslcrypto
 * @{
 */

/**
 * \brief Implementation of the interface class for RSA keys.
 *
 * The library uses classes derived from this to process RSA keys.
 */

class DSIG_EXPORT OpenSSLCryptoKeyRSA : public XSECCryptoKeyRSA {

public :

	/** @name Constructors and Destructors */
	//@{
	
	OpenSSLCryptoKeyRSA();
	virtual ~OpenSSLCryptoKeyRSA();

	//@}

	/** @name Key Interface methods */
	//@{

	/**
	 * \brief Return the type of this key.
	 *
	 * For RSA keys, this allows people to determine whether this is a 
	 * public key, private key or a key pair
	 */

	virtual XSECCryptoKey::KeyType getKeyType();

	/**
	 * \brief Return the OpenSSL identifier string
	 */

	virtual const XMLCh * getProviderName() {return DSIGConstants::s_unicodeStrPROVOpenSSL;}

	/**
	 * \brief Replicate key
	 */

	virtual XSECCryptoKey * clone();

	//@}

	/** @name Mandatory RSA interface methods 
	 *
	 * These classes are required by the library.
	 */
	//@{

	/**
	 * \brief Set the OAEPparams string
	 *
	 * By default, the library expects crypto implementations to perform
	 * OAEP padding with no params.  This call allows the library (or user)
	 * to set a params value prior to an encrypt/decrypt operation.
	 *
	 * @param params buffer containing the params data.  Pass in NULL to clear any
	 * old paramters.
	 * @param paramsLen number of bytes in buffer to use.  Pass in 0 to clear any
	 * old parameters.
	 */

	virtual void setOAEPparams(unsigned char * params, unsigned int paramsLen);

	/**
	 * \brief Get OAEPparams Length
	 *
	 * @returns the number of bytes of the OAEPparams buffer (assuming it has been set)
	 */

	virtual unsigned int getOAEPparamsLen(void);

	/**
	 * \brief Get the OAEPparams
	 *
	 * @returns a pointer to the (crypto object owned) buffer holding the OAEPparams
	 * or NULL if no params are held
	 */

	virtual const unsigned char * getOAEPparams(void);

	/**
	 * \brief Verify a SHA1 PKCS1 encoded signature
	 *
	 * The library will call this function to validate an RSA signature
	 * The standard by default uses SHA1 in a PKCS1 encoding.
	 *
	 * @param hashBuf Buffer containing the pre-calculated (binary) digest
	 * @param hashLen Length of the data in the digest buffer
	 * @param base64Signature Buffer containing the Base64 encoded signature
	 * @param sigLen Length of the data in the signature buffer
	 * @returns true if the signature was valid, false otherwise
	 */

	virtual bool verifySHA1PKCS1Base64Signature(const unsigned char * hashBuf, 
								 unsigned int hashLen,
								 const char * base64Signature,
								 unsigned int sigLen);

	/**
	 * \brief Create a signature
	 *
	 * The library will call this function to create a signature from
	 * a pre-calculated digest.  The output signature will
	 * be Base64 encoded such that it can be placed directly into the
	 * XML document
	 *
	 * @param hashBuf Buffer containing the pre-calculated (binary) digest
	 * @param hashLen Number of bytes of hash in the hashBuf
	 * @param base64SignatureBuf Buffer to place the base64 encoded result
	 * in.
	 * @param base64SignatureBufLen Implementations need to ensure they do
	 * not write more bytes than this into the buffer
	 */

	virtual unsigned int signSHA1PKCS1Base64Signature(unsigned char * hashBuf,
		unsigned int hashLen,
		char * base64SignatureBuf,
		unsigned int base64SignatureBufLen);

	/**
	 * \brief Decrypt using private key
	 *
	 * The library will call this function to decrypt a piece of cipher
	 * text using the private component of this key.
	 *
	 * @param inBuf cipher text to decrypt
	 * @param plainBuf output buffer for decrypted bytes
	 * @param inLength bytes of cipher text to decrypt
	 * @param maxOutLength size of outputBuffer
	 * @param padding Type of padding (PKCS 1.5 or OAEP)
	 * @param hm Hash Method for OAEP encryption (OAEPParams should be
	 * set using setOAEPparams()
	 */

	virtual unsigned int privateDecrypt(const unsigned char * inBuf,
								 unsigned char * plainBuf, 
								 unsigned int inLength,
								 unsigned int maxOutLength,
								 PaddingType padding,
								 hashMethod hm);


	/**
	 * \brief Encrypt using a public key
	 *
	 * The library will call this function to encrypt a plain text buffer
	 * using the public component of this key.
	 *
	 * @param inBuf plain text to decrypt
	 * @param cipherBuf output buffer for decrypted bytes
	 * @param inLength bytes of plain text to encrypt
	 * @param maxOutLength size of outputBuffer
	 * @param padding Type of padding (PKCS 1.5 or OAEP)
	 * @param hm Hash Method for OAEP encryption (OAEPParams should be
	 * set using setOAEPparams()
	 */

	virtual unsigned int publicEncrypt(const unsigned char * inBuf,
								 unsigned char * cipherBuf, 
								 unsigned int inLength,
								 unsigned int maxOutLength,
								 PaddingType padding,
								 hashMethod hm);

	/**
	 * \brief Obtain the length of an RSA key
	 *
	 * @returns The length of the rsa key (in bytes)
	 */

	virtual unsigned int getLength(void);

	//@}

	/** @name Optional Interface methods
	 * 
	 * Have been implemented to allow interoperability testing
	 */

	//@{

	/**
	 * \brief Load the modulus
	 *
	 * Load the modulus from a Base64 encoded string
	 *
	 * param b64 A buffer containing the encoded string
	 * param len The length of the data in the buffer
	 */

	virtual void loadPublicModulusBase64BigNums(const char * b64, unsigned int len);

	/**
	 * \brief Load the exponent
	 *
	 * Load the exponent from a Base64 encoded string
	 *
	 * param b64 A buffer containing the encoded string
	 * param len The length of the data in the buffer
	 */

	virtual void loadPublicExponentBase64BigNums(const char * b64, unsigned int len);

	//@}

	/** @name OpenSSL specific methods */
	//@{

	/**
	 * \brief Constructor to create the object around an existing OpenSSL RSA
	 * key
	 *
	 * @param k The key to copy
	 * @note The object takes a copy of the original key, and will not delete k on
	 * completion.  This must be done by the caller.
	 */

	OpenSSLCryptoKeyRSA(EVP_PKEY *k);

	//@}

private:

	XSECCryptoKey::KeyType			m_keyType;
	RSA								* mp_rsaKey;
	unsigned char					* mp_oaepParams;
	unsigned int					m_oaepParamsLen;

};

#endif /* HAVE_OPENSSL */
#endif /* OPENSSLCRYPTOKEYRSA_INCLUDE */
