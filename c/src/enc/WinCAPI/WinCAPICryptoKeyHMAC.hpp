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
 * WinCAPICryptoKeyHMAC := HMAC Keys
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */

#ifndef WINCAPICRYPTOKEYHMAC_INCLUDE
#define WINCAPICRYPTOKEYHMAC_INCLUDE

#include <xsec/enc/XSECCryptoKeyHMAC.hpp>

#if !defined(_WIN32_WINNT)
#	define _WIN32_WINNT 0x0400
#endif

#include <wincrypt.h>

/**
 * \ingroup wincapicrypto
 * @{
 */

/**
 * \brief Windows Crypto API implementation for HMAC keys.
 *
 * Used to provide HMAC keys to WinCAPI CryptoHashHMAC
 *
 * Provides two types of key.
 *
 * A <b>Windows Key</b> (via setWinKey) is a direct key that can be used
 * by the Windows HMAC implementation.
 *
 * A <b>byte</b> key (via setKey) is a string of bytes that will be used 
 * as a key.  This requires an internal implementation of an HMAC using the
 * Windows Digest functions, as the Windows API does not allow direct
 * loading of these keys.
 */

class DSIG_EXPORT WinCAPICryptoKeyHMAC : public XSECCryptoKeyHMAC {

public :

	/** @name Constructors and Destructors */
	//@{
	/**
	 * \brief Constructor
	 *
	 * @param prov The handle to the provider context that was used to
	 * create any Windows keys (later set via setKey).  If this is not
	 * to be used for a windows key (i.e. will be used for a "normal"
	 * buffer of bytes as a key, then this value can be set to 0
	 */

	WinCAPICryptoKeyHMAC(HCRYPTPROV prov);
	
	virtual ~WinCAPICryptoKeyHMAC() {};

	//@}

	/** @name Key Interface methods */
	//@{

	/**
	 * \brief Return the type of this key.
	 *
	 * For DSA keys, this allows people to determine whether this is a 
	 * public key, private key or a key pair
	 */

	virtual XSECCryptoKey::KeyType getKeyType() {return KEY_HMAC;}
	
	/**
	 * \brief Replicate key
	 */

	virtual XSECCryptoKey * clone();

	/**
	 * \brief Return the WinCAPI string identifier
	 */

	virtual const XMLCh * getProviderName() {return DSIGConstants::s_unicodeStrPROVWinCAPI;}

	//@}

	/** @name Optional Interface methods */
	//@{

	/**
	 * \brief Set the key
	 *
	 * Set the key from the buffer
	 *
	 * @param inBuf Buffer containing the direct bitwise representation of the key
	 * @param inLength Number of bytes of key in the buffer
	 *
	 * @note isSensitive() should have been called on the inbound buffer
	 * to ensure the contents is overwritten when the safeBuffer is deleted
	 */

	virtual void setKey(unsigned char * inBuf, unsigned int inLength);

	/**
	 * \brief Get the key value
	 * 
	 * Copy the key into the safeBuffer and return the number of bytes
	 * copied.
	 *
	 * @param outBuf Buffer to copy key into
	 * @returns number of bytes copied in
	 */

	virtual unsigned int getKey(safeBuffer &outBuf);

	//@}

	/** @name Windows specific keys */
	//@{

	/**
	 * \brief Set a Windows key
	 *
	 * Set a Windows Crypto key that has been either derived via the
	 * various Crypt functions or has been loaded from an encrypted BLOB
	 *
	 * @param k Windows CAPI key to load
	 * Note that the library now owns this key (and will destroy it).
	 */

	void setWinKey(HCRYPTKEY k);

	/**
	 * \brief Get a windows key
	 *
	 * Used by WinCAPICryptoHashHMAC to retrieve the key in order to
	 * load it into the HMAC function.
	 *
	 * @returns The key to use or 0 if this object does not hold one
	 */

	HCRYPTKEY getWinKey(void);

	/**
	 * \brief Get a windows key provider
	 *
	 * Used by WinCAPICryptoHashHMAC to retrieve the provider handle associated
	 * with an HMAC key in order to load it into the HMAC function.
	 *
	 * @returns The key to use or 0 if this object does not hold one
	 */

	HCRYPTPROV getWinKeyProv(void);

	//@}

private:

	safeBuffer			m_keyBuf;
	unsigned int		m_keyLen;

	HCRYPTKEY			m_k;
	HCRYPTPROV			m_p;
};

#endif /* WINCAPICRYPTOKEYHMAC_INCLUDE */
