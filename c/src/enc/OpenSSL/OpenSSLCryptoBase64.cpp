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
 * OpenSSLCryptoBase64 := Base virtual class to define a base64 encoder/decoder
 *
 * Author(s): Berin Lautenbach
 *
 * $ID$
 *
 * $LOG$
 *
 */

#include <xsec/framework/XSECDefs.hpp>
#if defined (HAVE_OPENSSL)

#include <xsec/enc/OpenSSL/OpenSSLCryptoBase64.hpp>
#include <xsec/enc/XSECCryptoException.hpp>

#include <openssl/err.h>

// --------------------------------------------------------------------------------
//           Decoding
// --------------------------------------------------------------------------------

void OpenSSLCryptoBase64::decodeInit(void) {

	EVP_DecodeInit(&m_dctx);

}

unsigned int OpenSSLCryptoBase64::decode(const unsigned char * inData, 
						 	    unsigned int inLength,
								unsigned char * outData,
								unsigned int outLength) {

	int rc;
	int outLen;

	if (outLength < inLength) {

		throw XSECCryptoException(XSECCryptoException::MemoryError,
			"OpenSSL:Base64 - Output buffer not big enough for Base64 decode");

	}

	rc = EVP_DecodeUpdate(&m_dctx, 
						  outData, 
						  &outLen, 
						  (unsigned char *) inData, 
						  inLength);

	if (rc < 0) {

		throw XSECCryptoException(XSECCryptoException::Base64Error,
			"OpenSSL:Base64 - Error during Base64 Decode");
	}

	if (outLen > (int) outLength) {

		throw XSECCryptoException(XSECCryptoException::MemoryError,
			"OpenSSL:Base64 - Output buffer not big enough for Base64 decode and overflowed");

	}
		
	return outLen;

}

unsigned int OpenSSLCryptoBase64::decodeFinish(unsigned char * outData,
							 	      unsigned int outLength) {

	int outLen;
	outLen = outLength;

	EVP_DecodeFinal(&m_dctx, outData, &outLen); 

	return outLen;

}

// --------------------------------------------------------------------------------
//           Encoding
// --------------------------------------------------------------------------------

void OpenSSLCryptoBase64::encodeInit(void) {

	EVP_EncodeInit(&m_ectx);

}


unsigned int OpenSSLCryptoBase64::encode(const unsigned char * inData, 
						 	    unsigned int inLength,
								unsigned char * outData,
								unsigned int outLength) {

	int outLen;

	if (outLength + 24 < inLength) {

		throw XSECCryptoException(XSECCryptoException::MemoryError,
			"OpenSSL:Base64 - Output buffer not big enough for Base64 encode");

	}

	EVP_EncodeUpdate(&m_ectx, 
					  outData, 
					  &outLen, 
					  (unsigned char *) inData, 
					  inLength);

	if (outLen > (int) outLength) {

		throw XSECCryptoException(XSECCryptoException::MemoryError,
			"OpenSSL:Base64 - Output buffer not big enough for Base64 encode and overflowed");

	}
		
	return outLen;

}

unsigned int OpenSSLCryptoBase64::encodeFinish(unsigned char * outData,
							 	      unsigned int outLength) {

	int outLen;
	outLen = outLength;

	EVP_EncodeFinal(&m_ectx, outData, &outLen); 

	return outLen;

}

// --------------------------------------------------------------------------------
//           Utility functions
// --------------------------------------------------------------------------------

BIGNUM * OpenSSLCryptoBase64::b642BN(char * b64in, unsigned int len) {

	if (len > 1024)
		return NULL;

	int bufLen;
	unsigned char buf[1024];

	EVP_ENCODE_CTX m_dctx;
	EVP_DecodeInit(&m_dctx);
	int rc = EVP_DecodeUpdate(&m_dctx, 
						  buf, 
						  &bufLen, 
						  (unsigned char *) b64in, 
						  len);

	if (rc < 0) {

		throw XSECCryptoException(XSECCryptoException::Base64Error,
			"OpenSSL:Base64 - Error during Base64 Decode of BIGNUMS");
	}

	int finalLen;
	EVP_DecodeFinal(&m_dctx, &buf[bufLen], &finalLen); 

	bufLen += finalLen;

	// Now translate to a bignum
	return BN_dup(BN_bin2bn(buf, bufLen, NULL));

}

#endif /* HAVE_OPENSSL */
