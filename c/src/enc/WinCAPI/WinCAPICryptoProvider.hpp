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


// Required for windows functions


/**
 * @defgroup wincapicrypto Windows Crypto API Interface
 * @ingroup crypto
 * The WinCAPI crypto provides an experimental inerface to
 * the Windows Cryptographic API
 */
 /*\@{*/

class DSIG_EXPORT WinCAPICryptoProvider : public XSECCryptoProvider {


public :

	// Constructors/Destructors

	/**
	 * \brief Create a Windows CAPI interface layer
	 *
	 * Windows CSPs work under a provider model.  The user should specify
	 * which CSP to use and which key container to use.
	 *
	 * @param pszContainer Key container (NULL for default)
	 * @param pszProvider Cryptographic provider (NULL for default)
	 */
	
	WinCAPICryptoProvider(HCRYPTPROV provDSS);

	virtual ~WinCAPICryptoProvider();

	// Hashing classes
	virtual XSECCryptoHash			* hashSHA1();
	virtual XSECCryptoHash			* hashHMACSHA1();

	// Encode/Decode
	virtual XSECCryptoBase64		* base64();

	// Keys
	virtual XSECCryptoKeyDSA		* keyDSA();
	virtual XSECCryptoKeyRSA		* keyRSA();

	// X509
	virtual XSECCryptoX509			* X509();


	// WinCAPI Unique
	HCRYPTPROV getProvider(void) {return m_provDSS;}

	/**
	 * \brief Translate B64 I2OS integer to a WinCAPI int.
	 *
	 * Decodes a Base64 integer and reverses the order to allow loading into
	 * a Windows CAPI function.  (CAPI uses Little Endian storage of integers).
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
	 * @param b64 Base 64 buffer
	 * @param b64Len Length of base64 buffer
	 * @param retLen Parameter to hold length of return integer
	 */

	static unsigned char * WinBN2b64(BYTE * n, DWORD nLen, unsigned int &retLen);

	/*\@}*/



private:

	// Default constructor not used
	WinCAPICryptoProvider();

	HCRYPTPROV		m_provDSS;

};

#endif /* WINCAPICRYPTOPROVIDER_INCLUDE */
