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
 * TXFMCipher := Class that performs encryption and decryption transforms
 *
 * $Id$
 *
 */

// XSEC

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/transformers/TXFMCipher.hpp>
#include <xsec/utils/XSECPlatformUtils.hpp>
#include <xsec/framework/XSECException.hpp>

XERCES_CPP_NAMESPACE_USE

TXFMCipher::TXFMCipher(DOMDocument *doc, 
					   XSECCryptoKey * key, 
					   bool encrypt) : 
TXFMBase(doc),
m_doEncrypt(encrypt),
m_remaining(0) {


	mp_cipher = key->clone();
	
	if (!mp_cipher) {

		throw XSECException(XSECException::CryptoProviderError, 
				"Error cloning key");

	}

	m_complete = false;

	try {
		if (mp_cipher->getKeyType() == XSECCryptoKey::KEY_SYMMETRIC && m_doEncrypt)
			(dynamic_cast<XSECCryptoSymmetricKey *>(mp_cipher))->encryptInit();
		else
			(dynamic_cast<XSECCryptoSymmetricKey *>(mp_cipher))->decryptInit();
	}
	catch (...) {
		delete mp_cipher;
		mp_cipher = NULL;
		throw;
	}

};

TXFMCipher::~TXFMCipher() {

	if (mp_cipher != NULL)
		delete mp_cipher;

};

	// Methods to set the inputs

void TXFMCipher::setInput(TXFMBase *newInput) {

	input = newInput;

	// Set up for comments
	keepComments = input->getCommentsStatus();

}

	// Methods to get tranform output type and input requirement

TXFMBase::ioType TXFMCipher::getInputType(void) {

	return TXFMBase::BYTE_STREAM;

}
TXFMBase::ioType TXFMCipher::getOutputType(void) {

	return TXFMBase::BYTE_STREAM;

}


TXFMBase::nodeType TXFMCipher::getNodeType(void) {

	return TXFMBase::DOM_NODE_NONE;

}

// Methods to get output data

unsigned int TXFMCipher::readBytes(XMLByte * const toFill, unsigned int maxToFill) {
	
	unsigned int ret, fill, leftToFill;

	ret = 0;					// How much have we copied?
	leftToFill = maxToFill;		// Still have to copy in entire thing

	while (ret != maxToFill && (m_complete == false || m_remaining > 0)) {
	
		if (m_remaining != 0) {

			// Copy anything remaining in the buffer to the output

			fill = (leftToFill > m_remaining ? m_remaining : leftToFill);
			memcpy(&toFill[ret], m_outputBuffer, fill);

			if (fill < m_remaining)
				memmove(m_outputBuffer, m_outputBuffer + fill, (m_remaining - fill));

			m_remaining -= fill;
			ret += fill;
		}

		// Now do some crypting

		if (m_complete == false) {

			unsigned int sz = input->readBytes(m_inputBuffer, 2048);
		
			if (mp_cipher->getKeyType() == XSECCryptoKey::KEY_SYMMETRIC) {
				XSECCryptoSymmetricKey * symCipher = 
					dynamic_cast<XSECCryptoSymmetricKey*>(mp_cipher);
				if (m_doEncrypt) {
					
					if (sz == 0) {
						m_complete = true;
						m_remaining = symCipher->encryptFinish(m_outputBuffer, 3072);
					}
					else
						m_remaining = symCipher->encrypt(m_inputBuffer, m_outputBuffer, sz, 3072);
				}
				else {

					if (sz == 0) {
						m_complete = true;
						m_remaining = symCipher->decryptFinish(m_outputBuffer, 3072);
					}
					else
						m_remaining = symCipher->decrypt(m_inputBuffer, m_outputBuffer, sz, 3072);
				}
			}
		}

	}

	return ret;

}

DOMDocument *TXFMCipher::getDocument() {

	return NULL;

}

DOMNode * TXFMCipher::getFragmentNode() {

	return NULL;		// Return a null node

}

const XMLCh * TXFMCipher::getFragmentId() {

	return NULL;	// Empty string

}
