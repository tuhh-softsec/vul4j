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
 * TXFMBase64 := Class that performs a Base64 transform
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */

// XSEC

#include <xsec/transformers/TXFMBase64.hpp>
#include <xsec/utils/XSECPlatformUtils.hpp>
#include <xsec/framework/XSECException.hpp>

XERCES_CPP_NAMESPACE_USE

TXFMBase64::TXFMBase64(DOMDocument *doc, bool decode) : TXFMBase(doc) {

	m_complete = false;					// Nothing yet to output
	m_remaining = 0;
	m_doDecode = decode;

	mp_b64 = XSECPlatformUtils::g_cryptoProvider->base64();
	
	if (!mp_b64) {

		throw XSECException(XSECException::CryptoProviderError, 
				"Error requesting Base64 object from Crypto Provider");

	}
	if (decode)
		mp_b64->decodeInit();
	else
		mp_b64->encodeInit();

};

TXFMBase64::~TXFMBase64() {

	if (mp_b64 != NULL)
		delete mp_b64;

};

	// Methods to set the inputs

void TXFMBase64::setInput(TXFMBase *newInput) {

	input = newInput;

	// Set up for comments
	keepComments = input->getCommentsStatus();

}

	// Methods to get tranform output type and input requirement

TXFMBase::ioType TXFMBase64::getInputType(void) {

	return TXFMBase::BYTE_STREAM;

}
TXFMBase::ioType TXFMBase64::getOutputType(void) {

	return TXFMBase::BYTE_STREAM;

}


TXFMBase::nodeType TXFMBase64::getNodeType(void) {

	return TXFMBase::DOM_NODE_NONE;

}

	// Methods to get output data

unsigned int TXFMBase64::readBytes(XMLByte * const toFill, unsigned int maxToFill) {
	
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
			leftToFill -= fill;
			ret += fill;
		}

		// Now do some crypting

		if (m_complete == false && m_remaining == 0) {

			unsigned int sz = input->readBytes(m_inputBuffer, 1024);
		
			if (m_doDecode) {
				
				if (sz == 0) {
					m_complete = true;
					m_remaining = mp_b64->decodeFinish(m_outputBuffer, 2048);
				}
				else
					m_remaining = mp_b64->decode(m_inputBuffer, sz, m_outputBuffer, 2048);
			}
			else {

				if (sz == 0) {
					m_complete = true;
					m_remaining = mp_b64->encodeFinish(m_outputBuffer, 3072);
				}
				else
					m_remaining = mp_b64->encode(m_inputBuffer, sz, m_outputBuffer, 2048);
			}
		}

	}

	return ret;
#if 0
	unsigned int ret, fill;

	if (m_complete)
		return 0;

	fill = (maxToFill > 2000 ? 2000 : maxToFill);
	ret = 0;
	while (ret == 0 && !m_complete) {
	
		unsigned int sz = input->readBytes(m_base64Buffer, fill / 2);

		if (m_doDecode) {
			if (sz == 0)
				ret = mp_b64->decodeFinish((unsigned char *) toFill, maxToFill);
			else
				ret = mp_b64->decode(m_base64Buffer, sz, (unsigned char *) toFill, maxToFill);

			if (ret == 0)
				m_complete = true;
		}
		else {
			if (sz == 0) {
				ret = mp_b64->encodeFinish((unsigned char *) toFill, maxToFill);
				m_complete = true;
			}
			else 
				ret = mp_b64->encode(m_base64Buffer, sz, (unsigned char *) toFill, maxToFill);

		}
	}

	return ret;
#endif
}

DOMDocument *TXFMBase64::getDocument() {

	return NULL;

}

DOMNode * TXFMBase64::getFragmentNode() {

	return NULL;		// Return a null node

}

const XMLCh * TXFMBase64::getFragmentId() {

	return NULL;	// Empty string

}
