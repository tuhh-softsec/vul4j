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
 * XSECCryptoBase64 := Base virtual class to define a base64 encoder/decoder
 *
 */

#ifndef XSECCRYPTOBASE64_INCLUDE
#define XSECCRYPTOBASE64_INCLUDE

#include <xsec/framework/XSECDefs.hpp>

/**
 * @ingroup crypto
 */
 /*\@{*/

/**
 * \brief Base64 encode/decode handler.
 *
 * <p>The XSEC library will use this class for translating bytes to/from
 * base64 encoding.</p>
 *
 * <p>There are many places where XML DSIG uses Base64 encoding for 
 * embedding data in the <Signature> structure.  In some cases this object
 * is used.  In other cases, the library passes base64 code directly to
 * the cryptographic handler.</p>
 *
 * @note The library may re-use Base64 objects.  However it will always
 * call the ??Init function prior to re-use.  In addtion, the object does
 * not need to be able to handle concurrent encode/decode operations.
 */

class DSIG_EXPORT XSECCryptoBase64 {


public :

	// Constructors/Destructors
	
	XSECCryptoBase64() {};
	virtual ~XSECCryptoBase64() {};

	/** @name Decoding Functions */
	//@{

	/**
	 * \brief Initialise the base64 object.
	 *
	 * The XSEC library will <em>always</em> call this function prior
	 * to decoding any data.  This function will also be called when
	 * one decode (or encode) has been completed and the library wishes
	 * to re-use the object for another decode operation.
	 *
	 */

	virtual void		 decodeInit(void) = 0;

	/**
	 * \brief Decode some passed in data.
	 *
	 * <p>The XSEC library will pass a block of data into the decoder
	 * and request that as much as possible be decoded into the outData
	 * buffer.</p>
	 *
	 * <p>Due to the nature of base64, there may be data that cannot be
	 * fully decoded (not enough encoding through yet).  The implementation
	 * is expected to keep this in memory until another call to #decode()
	 * or a call to #decodeFinish().</p>
	 *
	 * @param inData Pointer to the buffer holding encoded data.
	 * @param inLength Length of the encoded data in the buffer
	 * @param outData Buffer to place decoded data into
	 * @param outLength Maximum amount of data that can be placed in
	 *        the buffer.
	 * @returns The number of bytes placed in the outData buffer.
	 */

	virtual unsigned int decode(unsigned char * inData, 
						 	    unsigned int inLength,
								unsigned char * outData,
								unsigned int outLength) = 0;
	/**
	 * \brief Finish off a decode.
	 *
	 * <p>The library will call this when there is no more base64 data for
	 * the current decode.</p>
	 *
	 * @param outData Buffer to place any remaining decoded data
	 * @param outLength Max amount of data to be placed in the buffer.
	 * @returns Amount of data placed in the outData buffer
	 */

	virtual unsigned int decodeFinish(unsigned char * outData,
							 	      unsigned int outLength) = 0;

	//@}

	/** @name Encoding Functions */
	//@{

	/**
	 * \brief Initialise the base64 object for encoding
	 *
	 * The XSEC library will <em>always</em> call this function prior
	 * to encoding any data.  This function will also be called when
	 * one encode (or decode) has been completed and the library wishes
	 * to re-use the object for another encode operation.
	 *
	 */
	
	virtual void		 encodeInit(void) = 0;

	/**
	 * \brief Encode some passed in data.
	 *
	 * <p>The XSEC library will pass a block of data into the Encoder
	 * and request that as much as possible be encoded into the outData
	 * buffer.</p>
	 *
	 * <p>Due to the nature of the implementation, there may be data 
	 * that cannot be
	 * fully encoded (not enough data through yet).  The implementation
	 * is expected to keep this in memory until another call to #encode()
	 * or a call to #encodeFinish().</p>
	 *
	 * @param inData Pointer to the buffer holding data to be encoded.
	 * @param inLength Length of the data in the buffer
	 * @param outData Buffer to place encoded data into
	 * @param outLength Maximum amount of data that can be placed in
	 *        the buffer.
	 * @returns The number of bytes placed in the outData buffer.
	 */


	virtual unsigned int encode(unsigned char * inData, 
						 	    unsigned int inLength,
								unsigned char * outData,
								unsigned int outLength) = 0;
	/**
	 * \brief Finish off an encode.
	 *
	 * <p>The library will call this when there is no more data for
	 * the current encode operation.</p>
	 *
	 * @param outData Buffer to place any remaining encoded data
	 * @param outLength Max amount of data to be placed in the buffer.
	 * @returns Amount of data placed in the outData buffer
	 */


	virtual unsigned int encodeFinish(unsigned char * outData,
							 	      unsigned int outLength) = 0;

	//@}

};
/*\@}*/
#endif /* XSECCRYPTOBASE64_INCLUDE */
