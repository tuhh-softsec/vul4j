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
 * XSCryptCryptoBase64 := Internal implementation of a base64 encoder/decoder
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */

#ifndef XSCRYPTCRYPTOBASE64_INCLUDE
#define XSCRYPTCRYPTOBASE64_INCLUDE

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/enc/XSECCryptoBase64.hpp>
#include <xsec/utils/XSECSafeBuffer.hpp>

 /**
 * @defgroup xscryptcrypto Internal Crypto API Interface
 * @ingroup crypto
 * The XSCrypt Interface provides cryptographic functions that are missing
 * from other libraries.
 *
 * Currently this only provides Base64 functionality which is not
 * available in the Windows Crypto API.
 *
 */

class DSIG_EXPORT XSCryptCryptoBase64 : public XSECCryptoBase64 {


public :

	// Constructors/Destructors
	
	XSCryptCryptoBase64() : m_state(B64_UNINITIALISED) {};
	virtual ~XSCryptCryptoBase64() {};

	/** @name Decoding Functions */
	//@{

	/**
	 * \brief Initialise the base64 object.
	 *
	 * Initialises the OpenSSL decode context and gets ready for data
	 * to be decoded.
	 *
	 */

	virtual void		 decodeInit(void);					// Setup

	/**
	 * \brief Decode some passed in data.
	 *
	 * Decode the passed in data and place the data in the outData buffer
	 *
	 * @param inData Pointer to the buffer holding encoded data.
	 * @param inLength Length of the encoded data in the buffer
	 * @param outData Buffer to place decoded data into
	 * @param outLength Maximum amount of data that can be placed in
	 *        the buffer.
	 * @returns The number of bytes placed in the outData buffer.
	 */
	
	virtual unsigned int decode(const unsigned char * inData, 
						 	    unsigned int inLength,
								unsigned char * outData,
								unsigned int outLength);

	/**
	 * \brief Finish off a decode.
	 *
	 * Clean out any extra data from previous decode operations and place
	 * into the outData buffer.
	 *
	 * @param outData Buffer to place any remaining decoded data
	 * @param outLength Max amount of data to be placed in the buffer.
	 * @returns Amount of data placed in the outData buffer
	 */

	virtual unsigned int decodeFinish(unsigned char * outData,
							 	      unsigned int outLength);

	//@}

	/** @name Encoding Functions */
	//@{

	/**
	 * \brief Initialise the base64 object for encoding
	 *
	 * Get the context variable ready for a base64 decode
	 *
	 */

	virtual void		 encodeInit(void);

	/**
	 * \brief Encode some passed in data.
	 *
	 * @param inData Pointer to the buffer holding data to be encoded.
	 * @param inLength Length of the data in the buffer
	 * @param outData Buffer to place encoded data into
	 * @param outLength Maximum amount of data that can be placed in
	 *        the buffer.
	 * @returns The number of bytes placed in the outData buffer.
	 */

	virtual unsigned int encode(const unsigned char * inData, 
						 	    unsigned int inLength,
								unsigned char * outData,
								unsigned int outLength);

	/**
	 * \brief Finish off an encode.
	 *
	 * Take any data from previous encode operations and create the
	 * tail of the base64 encoding.
	 *
	 * @param outData Buffer to place any remaining encoded data
	 * @param outLength Max amount of data to be placed in the buffer.
	 * @returns Amount of data placed in the outData buffer
	 */


	virtual unsigned int encodeFinish(unsigned char * outData,
							 	      unsigned int outLength);

	//@}

private :

	enum b64state {

		B64_UNINITIALISED,
		B64_ENCODE,
		B64_DECODE,
	};

	safeBuffer				m_inputBuffer;		// Carry over buffer
	safeBuffer				m_outputBuffer;		// Carry over output

	unsigned int			m_remainingInput;	// Number of bytes in carry input buffer
	unsigned int			m_remainingOutput;	// Number of bytes in carry output buffer

	bool					m_allDone;			// End found (=)

	b64state				m_state;			// What are we currently doing?

	unsigned int			m_charCount;		// How many characters in current line?

	// Private functions
	void canonicaliseInput(const unsigned char *inData, unsigned int inLength);

};

#endif /* XSCRYPTCRYPTOBASE64_INCLUDE */
