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
 * XSCryptCryptoBase64 := Internal implementation of a base64 
 * encoder/decoder
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */

#include <xsec/enc/XSCrypt/XSCryptCryptoBase64.hpp>
#include <xsec/enc/XSECCryptoException.hpp>

// --------------------------------------------------------------------------------
//           Lookup tables and macros
// --------------------------------------------------------------------------------

char Base64LookupTable[] = {
	'A','B','C','D','E','F','G','H','I','J','K','L','M',
	'N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
	'a','b','c','d','e','f','g','h','i','j','k','l','m',
	'n','o','p','q','r','s','t','u','v','w','x','y','z',
	'0','1','2','3','4','5','6','7','8','9','+','/',
};

#define IS_UPPER(c) (c >= 'A' && c <= 'Z')
#define IS_LOWER(c) (c >= 'a' && c <= 'z')
#define IS_NUMBR(c) (c >= '0' && c <= '9')
#define IS_OTHER(c) (c == '+' || c == '/')
#define IS_B64CH(c) (IS_LOWER(c) || IS_UPPER(c) || IS_NUMBR(c) || IS_OTHER(c))
#define IS_B64OE(c) (IS_B64CH(c) || c == '=')

// --------------------------------------------------------------------------------
//           Decoding
// --------------------------------------------------------------------------------

unsigned char decodeCh(unsigned char c) {

	if (IS_UPPER(c))
		return c - 'A';

	if (IS_LOWER(c))
		return (c - 'a') + 26;

	if (IS_NUMBR(c))
		return (c - '0') + 52;

	if (c == '+')
		return 62;
	if (c == '/')
		return 63;

	if (c == '=')
		return 64;

	return 65;		// error;

}

void XSCryptCryptoBase64::canonicaliseInput(unsigned char *inData, 
											unsigned int inLength) {

	// Canonicalise the input buffer into m_inputBuffer

	unsigned char buf[400];			// Do 400 bytes at a time

	unsigned int i, j;
	j = 0;

	for (i = 0; i < inLength; ++i) {

		if (IS_B64OE(inData[i])) {

			// Have a base64 or '=' char
			buf[j++] = inData[i];

			if (j == 400) {
				m_inputBuffer.sbMemcpyIn(m_remainingInput, buf, 400);
				m_remainingInput += 400;
				j = 0;
			}
		}
	}

	if (j > 0) {
		m_inputBuffer.sbMemcpyIn(m_remainingInput, buf, j);
		m_remainingInput += j;
	}

}


void XSCryptCryptoBase64::decodeInit(void) {

	m_remainingInput = m_remainingOutput = 0;
	m_allDone = false;
	m_state = B64_DECODE;

}

unsigned int XSCryptCryptoBase64::decode(unsigned char * inData, 
						 	    unsigned int inLength,
								unsigned char * outData,
								unsigned int outLength) {


	// Ensure we are in an appropriate state
	if (m_state != B64_DECODE) {

		throw XSECCryptoException(XSECCryptoException::Base64Error,
			"XSCrypt:Base64 - Attempt to decode when not in decode state");
	
	}

	// Copy the data into our input buffer
	canonicaliseInput(inData, inLength);

	unsigned int i = 0;
	unsigned char t;

	while (m_allDone != true && m_remainingInput - i >= 4) {

		// BYTE 1

		t = decodeCh(m_inputBuffer[i++]);

		if (t > 63) {

			throw XSECCryptoException(XSECCryptoException::Base64Error,
			"XSCrypt:Base64 - Invalid character at start of base 64 block");

		}

		m_outputBuffer[m_remainingOutput] = (t << 2);

		// BYTE 2

		t = decodeCh(m_inputBuffer[i++]);

		if (t > 63) {

			throw XSECCryptoException(XSECCryptoException::Base64Error,
			"XSCrypt:Base64 - Invalid character at start of base 64 block");

		}

		// Take top two bits and place at end of current byte
		m_outputBuffer[m_remainingOutput] = m_outputBuffer[m_remainingOutput] |
			(t >> 4);

		m_remainingOutput++;		// Have a new complete byte

		// Take remaining 4 bits and add to outputBuffer
		m_outputBuffer[m_remainingOutput] = ( t << 4);

		// BYTE 3

		t = decodeCh(m_inputBuffer[i++]);

		if (t > 64) {

			throw XSECCryptoException(XSECCryptoException::Base64Error,
			"XSCrypt:Base64 - Invalid character at start of base 64 block");

		}

		if (t == 64) {

			// '=' character found

			m_allDone = true;
			break;

		}

		// Take 4 bits and append to current buffer

		m_outputBuffer[m_remainingOutput] = m_outputBuffer[m_remainingOutput] | (t >> 2);
		m_remainingOutput++;

		// Take last 2 bits and append to buffer

		m_outputBuffer[m_remainingOutput] = (t << 6);

		// BYTE 4

		t = decodeCh(m_inputBuffer[i++]);

		if (t > 64) {

			throw XSECCryptoException(XSECCryptoException::Base64Error,
			"XSCrypt:Base64 - Invalid character at start of base 64 block");

		}

		if (t == 64) {

			m_allDone = true;
			break;

		}

		// Place all six bits and end of current byte

		m_outputBuffer[m_remainingOutput] = m_outputBuffer[m_remainingOutput] | t;
		m_remainingOutput++;

	}

	// Now whatever we've decoded can be placed in the output buffer

	unsigned int cpyOut = (m_remainingOutput < outLength ? m_remainingOutput : outLength);

	m_outputBuffer.sbMemcpyOut(outData, cpyOut);

	// Move the buffers down
	if (cpyOut != m_remainingOutput) {
		m_remainingOutput = m_remainingOutput - cpyOut;
		m_outputBuffer.sbMemshift(0, cpyOut, m_remainingOutput);
	}
	else
		m_remainingOutput = 0;

	if (i != m_remainingInput) {
		m_remainingInput -= i;
		m_inputBuffer.sbMemshift(0, i, m_remainingInput);
	}
	else
		m_remainingInput = 0;

	// Return however much we have decoded
	return cpyOut;

}

unsigned int XSCryptCryptoBase64::decodeFinish(unsigned char * outData,
							 	      unsigned int outLength) {

	if (m_state != B64_DECODE) {

		throw XSECCryptoException(XSECCryptoException::Base64Error,
			"XSCrypt:Base64 - Attempt to complete a decode when not in decode state");
	
	}

	m_allDone = true;
	unsigned int cpyOut = (m_remainingOutput < outLength ? m_remainingOutput : outLength);

	m_outputBuffer.sbMemcpyOut(outData, cpyOut);

	// Move the buffers down
	if (cpyOut != m_remainingOutput) {
		m_remainingOutput = m_remainingOutput - cpyOut;
		m_outputBuffer.sbMemshift(0, cpyOut, m_remainingOutput);
	}
	else {
		m_remainingOutput = 0;
	}

	return cpyOut;
}

// --------------------------------------------------------------------------------
//           Encoding
// --------------------------------------------------------------------------------

void XSCryptCryptoBase64::encodeInit(void) {

	m_remainingInput = m_remainingOutput = 0;
	m_allDone = false;
	m_charCount = 0;
	m_state = B64_ENCODE;

}


unsigned int XSCryptCryptoBase64::encode(unsigned char * inData, 
						 	    unsigned int inLength,
								unsigned char * outData,
								unsigned int outLength) {

	if (m_state != B64_ENCODE) {

		throw XSECCryptoException(XSECCryptoException::Base64Error,
			"XSCrypt:Base64 - Attempt to encode when not in encoding state");
	
	}

	// Copy input data into end of input buffer
	m_inputBuffer.sbMemcpyIn(m_remainingInput, inData, inLength);
	m_remainingInput += inLength;

	unsigned int i = 0;
	unsigned char t;

	while (m_allDone == false && m_remainingInput - i >= 3) {

		// Have a complete block of three bytes to encode

		// First 6 bits;
		t = (m_inputBuffer[i] >> 2);
		m_outputBuffer[m_remainingOutput++] = Base64LookupTable[t];

		// 2 bits from byte one and 4 from byte 2
		t = ((m_inputBuffer[i++] << 4) & 0x30) | (m_inputBuffer[i] >> 4);
		m_outputBuffer[m_remainingOutput++] = Base64LookupTable[t];

		// 4 from byte 2 and 2 from byte 3
		t = ((m_inputBuffer[i++] << 2) & 0x3C) | (m_inputBuffer[i] >> 6);
		m_outputBuffer[m_remainingOutput++] = Base64LookupTable[t];

		// last 6 bits from byte 3
		t = m_inputBuffer[i++] & 0x3F;
		m_outputBuffer[m_remainingOutput++] = Base64LookupTable[t];

		m_charCount += 4;

		if (m_charCount >= 76) {

			m_outputBuffer[m_remainingOutput++] = '\n';
			m_charCount = 0;
		
		}

	}

	// Now copy data out to output buffer

	unsigned int cpyOut = (m_remainingOutput < outLength ? m_remainingOutput : outLength);

	m_outputBuffer.sbMemcpyOut(outData, cpyOut);

	// Move the buffers down
	if (cpyOut != m_remainingOutput) {
		m_remainingOutput = m_remainingOutput - cpyOut;
		m_outputBuffer.sbMemshift(0, cpyOut, m_remainingOutput);
	}
	else
		m_remainingOutput = 0;

	if (i != m_remainingInput) {
		m_remainingInput -= i;
		m_inputBuffer.sbMemshift(0, i, m_remainingInput);
	}

	else

		m_remainingInput = 0;

	// Return however much we have decoded
	return cpyOut;

}

unsigned int XSCryptCryptoBase64::encodeFinish(unsigned char * outData,
							 	      unsigned int outLength) {

	if (m_state != B64_ENCODE) {

		throw XSECCryptoException(XSECCryptoException::Base64Error,
			"XSCrypt:Base64 - Attempt to complete an encode when not in encoding state");
	
	}

	if (m_allDone == false && m_remainingInput > 0) {

		// Will always be < 3 characters remaining in inputBuffer
		// If necessary - terminate the Base64 string

		if (m_remainingInput >= 3) {

			throw XSECCryptoException(XSECCryptoException::Base64Error,
				"XSCrypt:Base64 - Too much remaining input in input buffer");

		}

		// First 6 bits;
		unsigned int t = (m_inputBuffer[0] >> 2);
		m_outputBuffer[m_remainingOutput++] = Base64LookupTable[t];

		// 2 bits from byte one and 4 from byte 2
		t = ((m_inputBuffer[0] << 4) & 0x30);
		
		if (m_remainingInput == 1) {
			m_outputBuffer[m_remainingOutput++] = Base64LookupTable[t];
			m_outputBuffer[m_remainingOutput++] = '=';
			m_outputBuffer[m_remainingOutput++] = '=';
		}

		else {

			t |= (m_inputBuffer[1] >> 4);
			m_outputBuffer[m_remainingOutput++] = Base64LookupTable[t];

			// 4 from byte 2
			t = ((m_inputBuffer[1] << 2) & 0x3C);
			m_outputBuffer[m_remainingOutput++] = Base64LookupTable[t];
			m_outputBuffer[m_remainingOutput++] = '=';
		}

	}

	m_allDone = true;

	// Copy out
	unsigned int cpyOut = (m_remainingOutput < outLength ? m_remainingOutput : outLength);

	m_outputBuffer.sbMemcpyOut(outData, cpyOut);

	// Move the buffers down
	if (cpyOut != m_remainingOutput) {
		m_remainingOutput = m_remainingOutput - cpyOut;
		m_outputBuffer.sbMemshift(0, cpyOut, m_remainingOutput);
	}
	else {
		m_remainingOutput = 0;
	}

	return cpyOut;
}
