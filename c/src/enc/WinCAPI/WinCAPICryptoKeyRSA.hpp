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
 * WinCAPICryptoKeyRSA := Windows implementation of RSA Keys
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */

#ifndef WINCAPICRYPTOKEYRSA_INCLUDE
#define WINCAPICRYPTOKEYRSA_INCLUDE

#include <xsec/enc/XSECCryptoKeyRSA.hpp>

#if !defined(_WIN32_WINNT)
#	define _WIN32_WINNT 0x0400
#endif

#include <wincrypt.h>

class WinCAPICryptoProvider;

class DSIG_EXPORT WinCAPICryptoKeyRSA : public XSECCryptoKeyRSA {

public :

	// Constructors/Destructors
	
	WinCAPICryptoKeyRSA(WinCAPICryptoProvider * owner);

	/**
	 * \brief Dedicated WinCAPI constructor
	 *
	 * Create a RSA key for use in XSEC from an existing HCRYPTKEY
	 *
	 * @param owner The owner provider object (needed to find CSP)
	 * @param k The key to use
	 * @param havePrivate The CSP holds the private key as well as public
	 * @note k is owned by the library.  When the wrapper 
	 * WinCAPICryptoKeyRSA is deleted, k will be destroyed using
	 * CryptDestroyKey()
	 */

	WinCAPICryptoKeyRSA(WinCAPICryptoProvider * owner, HCRYPTKEY k, bool havePrivate = false);

	virtual ~WinCAPICryptoKeyRSA();

	// Generic key functions

	virtual XSECCryptoKey::KeyType getKeyType();
	virtual const XMLCh * getProviderName() {return DSIGConstants::s_unicodeStrPROVWinCAPI;}
	virtual XSECCryptoKey * clone();

	// RSA Specific Functions

	virtual void loadPublicModulusBase64BigNums(const char * b64, unsigned int len);
	virtual void loadPublicExponentBase64BigNums(const char * b64, unsigned int len);

	// Signature functions

	virtual bool verifySHA1PKCS1Base64Signature(const unsigned char * hashBuf, 
								 unsigned int hashLen,
								 const char * base64Signature,
								 unsigned int sigLen);

	virtual unsigned int signSHA1PKCS1Base64Signature(unsigned char * hashBuf,
		unsigned int hashLen,
		char * base64SignatureBuf,
		unsigned int base64SignatureBufLen);

	// "Extra" WinCAPI functions

	WinCAPICryptoKeyRSA(HCRYPTKEY k);

	// Some useful functions for extracting parameters from a Windows key

	unsigned int getExponentBase64BigNums(char * b64, unsigned int len);
	unsigned int getModulusBase64BigNums(char * b64, unsigned int len);

private:

	HCRYPTKEY					m_key;	
	WinCAPICryptoProvider		* mp_ownerProvider;
	bool						m_havePrivate;		// Do we have the private key?

	BYTE						* mp_modulus;
	BYTE						* mp_exponent;

	unsigned int				m_modulusLen;
	unsigned int				m_exponentLen;

	// Instruct to import from parameters

	void importKey(void);
	void loadParamsFromKey(void);

};

#endif /* WINCAPICRYPTOKEYRSA_INCLUDE */
