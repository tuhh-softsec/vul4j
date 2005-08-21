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
 * NSSCryptoKeyHMAC := HMAC Keys
 *
 * Author(s): Milan Tomic
 *
 * $Id$
 *
 */

#ifndef NSSCRYPTOKEYHMAC_INCLUDE
#define NSSCRYPTOKEYHMAC_INCLUDE

#include <xsec/enc/XSECCryptoKeyHMAC.hpp>

#include "nss/pk11func.h"
#include "nss/nss.h"

/**
 * \ingroup nss
 * @{
 */

/**
 * \brief NSS implementation for HMAC keys.
 *
 * Used to provide HMAC keys to NSS CryptoHashHMAC
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

class DSIG_EXPORT NSSCryptoKeyHMAC : public XSECCryptoKeyHMAC {

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

	NSSCryptoKeyHMAC();
	
	virtual ~NSSCryptoKeyHMAC() {};

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

	virtual const XMLCh * getProviderName() {return DSIGConstants::s_unicodeStrPROVNSS;}

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

private:

	safeBuffer			m_keyBuf;
	unsigned int		m_keyLen;

};

#endif /* NSSCRYPTOKEYHMAC_INCLUDE */
