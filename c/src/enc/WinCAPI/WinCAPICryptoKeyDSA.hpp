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
 * WinCAPICryptoKeyDSA := DSA Keys
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */

#ifndef WINCAPICRYPTOKEYDSA_INCLUDE
#define WINCAPICRYPTOKEYDSA_INCLUDE

#include <xsec/enc/XSECCryptoKeyDSA.hpp>

#if !defined(_WIN32_WINNT)
#	define _WIN32_WINNT 0x0400
#endif

#include <wincrypt.h>

class WinCAPICryptoProvider;

/**
 * \ingroup wincapicrypto
 * @{
 */

/**
 * \brief WinCAPI implementation class for DSA keys.
 *
 * The library uses classes derived from this to process DSA keys.
 */


class DSIG_EXPORT WinCAPICryptoKeyDSA : public XSECCryptoKeyDSA {

public :

	/** @name Constructors and Destructors */
	//@{
	
	/**
	 * \brief Ctor for DSA keys
	 *
	 * This constructor should be used for public keys that are going to be
	 * loaded later via P, Q etc.
	 *
	 * @param prov A handle to the provider (and key store) that will be
	 * used to hold the key being built  (Obviously a provider type that supports
	 * DSS, such as DSS_PROV)
	 */

	WinCAPICryptoKeyDSA(HCRYPTPROV prov);
	
	virtual ~WinCAPICryptoKeyDSA();

	/**
	 * \brief WinCAPI Specific constructor for public keys
	 *
	 * Create a DSA key for use in XSEC from an existing public HCRYPTKEY
	 *
	 * @param prov A handle to the CSP to be used for operations under this key.
	 * @param k The key to use
	 * @note k is owned by the library.  When the wrapper 
	 * WinCAPICryptoKeyDSA is deleted, k will be destroyed using
	 * CryptDestroyKey().  Note also that prov will not be released.
	 */

	WinCAPICryptoKeyDSA(HCRYPTPROV prov, HCRYPTKEY k);

	/**
	 * \brief WinCAPI Specific constructor for private keys
	 *
	 * Create a DSA key for use in XSEC from a passed in handle to a provider
	 * and associated key store
	 *
	 * @param prov A handle to the CSP to be used and read for the key.
	 * @param keySpec The Key to use (AT_SIGNATURE or AT_KEYEXCHANGE)
	 * @param isPrivate Should be true.  In future, may be able to define public
	 * keys this way as well.
	 * @note The prov will not be released on close.
	 */

	WinCAPICryptoKeyDSA(HCRYPTPROV prov, DWORD keySpec, bool isPrivate);

	//@}

	/** @name Required Key Interface methods */
	//@{

	/**
	 * \brief Return the type of this key.
	 *
	 * Allows the caller (and library) to determine whether this is a 
	 * public key, private key or a key pair
	 */

	virtual XSECCryptoKey::KeyType getKeyType();

	/**
	 *\brief Return the OpenSSL string identifier
	 */

	virtual const XMLCh * getProviderName() {return DSIGConstants::s_unicodeStrPROVWinCAPI;}
	
	/**
	 * \brief Replicate key
	 */

	virtual XSECCryptoKey * clone();

	//@}

	/** @name Required DSA methods */
	//@{

	/**
	 * \brief Create a signature
	 *
	 * Sign the buffer using the internal private key.  Will throw a DSAError
	 * if the key is not set, or is not a private key.
	 *
	 * @param hashBuf Buffer containing the pre-calculated (binary) digest
	 * @param hashLen Number of bytes of hash in the hashBuf
	 * @param base64SignatureBuf Buffer to place the base64 encoded result
	 * in.
	 * @param base64SignatureBufLen Implementations need to ensure they do
	 * not write more bytes than this into the buffer
	 */

	virtual unsigned int signBase64Signature(unsigned char * hashBuf,
						unsigned int hashLen,
						char * base64SignatureBuf,
						unsigned int base64SignatureBufLen);

	/**
	 * \brief Verify a signature
	 *
	 * The library will call this function to validate a signature
	 *
	 * @param hashBuf Buffer containing the pre-calculated (binary) digest
	 * @param hashLen Length of the data in the digest buffer
	 * @param base64Signature Buffer containing the Base64 encoded signature
	 * @param sigLen Length of the data in the signature buffer
	 * @returns true if the signature was valid, false otherwise
	 */
		
	virtual bool verifyBase64Signature(unsigned char * hashBuf, 
						 unsigned int hashLen,
						 char * base64Signature,
						 unsigned int sigLen);

	//@}

	/** @name Optional Interface methods
	 * 
	 * Have been fully implemented in the OpenSSL interface to allow interop
	 * testing to occur.
	 *
	 */
	//@{

	/**
	 * \brief Load P
	 *
	 * @param b64 Base64 encoded parameter - read from XML document
	 * @param len Length of the encoded string
	 */

	virtual void loadPBase64BigNums(const char * b64, unsigned int len);

	/**
	 * \brief Load Q
	 *
	 * @param b64 Base64 encoded parameter - read from XML document
	 * @param len Length of the encoded string
	 */
	
	virtual void loadQBase64BigNums(const char * b64, unsigned int len);

	/**
	 * \brief Load G
	 *
	 * @param b64 Base64 encoded parameter - read from XML document
	 * @param len Length of the encoded string
	 */

	virtual void loadGBase64BigNums(const char * b64, unsigned int len);
	
	/**
	 * \brief Load Y
	 *
	 * @param b64 Base64 encoded parameter - read from XML document
	 * @param len Length of the encoded string
	 */

	virtual void loadYBase64BigNums(const char * b64, unsigned int len);
	
	/**
	 * \brief Load J
	 *
	 * @param b64 Base64 encoded parameter - read from XML document
	 * @param len Length of the encoded string
	 */

	virtual void loadJBase64BigNums(const char * b64, unsigned int len);

	//@}

	/** @name WinCAPI Specific functions */
	//@{

	/**
	 * \brief Retrieve P
	 *
	 * @param b64 Buffer to place ds:CryptoBinary encoded P parameter in
	 * @param len Max amount of data to be placed in buffer
	 * @returns The number of bytes placed in the buffer
	 */

	unsigned int getPBase64BigNums(char * b64, unsigned int len);

	/**
	 * \brief Retrieve Q
	 *
	 * @param b64 Buffer to place ds:CryptoBinary encoded Q parameter in
	 * @param len Max amount of data to be placed in buffer
	 * @returns The number of bytes placed in the buffer
	 */
	
	unsigned int getQBase64BigNums(char * b64, unsigned int len);

	/**
	 * \brief Retrieve G
	 *
	 * @param b64 Buffer to place ds:CryptoBinary encoded G parameter in
	 * @param len Max amount of data to be placed in buffer
	 * @returns The number of bytes placed in the buffer
	 */
	
	unsigned int getGBase64BigNums(char * b64, unsigned int len);

	/**
	 * \brief Retrieve Y
	 *
	 * @param b64 Buffer to place ds:CryptoBinary encoded Y parameter in
	 * @param len Max amount of data to be placed in buffer
	 * @returns The number of bytes placed in the buffer
	 */
	
	unsigned int getYBase64BigNums(char * b64, unsigned int len);

	//@}

private:

	HCRYPTPROV					m_p;
	HCRYPTKEY					m_key;		// For a public key
	DWORD						m_keySpec;	// For a private key

	BYTE						* mp_P;
	BYTE						* mp_Q;
	BYTE						* mp_G;
	BYTE						* mp_Y;

	unsigned int				m_PLen;
	unsigned int				m_QLen;
	unsigned int				m_GLen;
	unsigned int				m_YLen;

	// Instruct to import from parameters

	void importKey(void);
	void loadParamsFromKey(void);

	// No default constructor
	WinCAPICryptoKeyDSA();
};

#endif /* WINCAPICRYPTOKEYDSA_INCLUDE */
