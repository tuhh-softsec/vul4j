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
 * NSSCryptoKeyHMAC := NSS HMAC keys
 *
 * Author(s): Milan Tomic
 *
 * $Id$
 *
 */

#include <xsec/enc/NSS/NSSCryptoKeyHMAC.hpp>
#include <xsec/framework/XSECError.hpp>
#include <xsec/enc/XSECCryptoException.hpp>

NSSCryptoKeyHMAC::NSSCryptoKeyHMAC() :m_keyBuf("") {

	m_keyBuf.isSensitive();
	m_keyLen = 0;

};

void NSSCryptoKeyHMAC::setKey(unsigned char * inBuf, unsigned int inLength) {

	m_keyBuf.sbMemcpyIn(inBuf, inLength);
	m_keyLen = inLength;

}

unsigned int NSSCryptoKeyHMAC::getKey(safeBuffer &outBuf) {

	outBuf = m_keyBuf;
	return m_keyLen;

}

XSECCryptoKey * NSSCryptoKeyHMAC::clone() {

	NSSCryptoKeyHMAC * ret;

	XSECnew(ret, NSSCryptoKeyHMAC());

	ret->m_keyBuf = m_keyBuf;
	ret->m_keyLen = m_keyLen;

	return ret;

}

