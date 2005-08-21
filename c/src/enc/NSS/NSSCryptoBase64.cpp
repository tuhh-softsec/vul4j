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
 * NSSCryptoBase64 := Base class to define a base64 encoder/decoder
 *
 * Author(s): Milan Tomic
 *
 * $ID$
 *
 * $LOG$
 *
 */

#include <xsec/framework/XSECDefs.hpp>
//#if defined (HAVE_NSS)

#include <xsec/enc/NSS/NSSCryptoBase64.hpp>
#include <xsec/enc/XSECCryptoException.hpp>
#include <xsec/enc/XSCrypt/XSCryptCryptoBase64.hpp>
#include <xsec/framework/XSECError.hpp>

#include <xercesc/util/Janitor.hpp>

XERCES_CPP_NAMESPACE_USE


// --------------------------------------------------------------------------------
//           Decoding
// --------------------------------------------------------------------------------

void NSSCryptoBase64::decodeInit(void) {

	//

}

unsigned int NSSCryptoBase64::decode(const unsigned char * inData, 
						 	    unsigned int inLength,
								unsigned char * outData,
								unsigned int outLength) {

	if (outLength < inLength) {

		throw XSECCryptoException(XSECCryptoException::MemoryError,
			"NSS:Base64 - Output buffer not big enough for Base64 decode");

	}

  unsigned int outLen = inLength;

  unsigned char * ret = ATOB_AsciiToData((const char *)inData, &outLen);

  if (!ret || !outLen) {

		throw XSECCryptoException(XSECCryptoException::Base64Error,
			"NSS:Base64 - Error during Base64 Decode");
	}

  if (outLen > outLength) {

		throw XSECCryptoException(XSECCryptoException::MemoryError,
			"NSS:Base64 - Output buffer not big enough for Base64 decode and overflowed");

	}

  memcpy(outData, inData, outLen);
		
	return outLen;

}

unsigned int NSSCryptoBase64::decodeFinish(unsigned char * outData,
							 	      unsigned int outLength) {

	throw XSECCryptoException(XSECCryptoException::MemoryError,
			"NSS:Base64 - finish not supported with NSS, call decode only once");

	return 0;

}

// --------------------------------------------------------------------------------
//           Encoding
// --------------------------------------------------------------------------------

void NSSCryptoBase64::encodeInit(void) {

	//m_ectx = NSSBase64Encoder_Create (PRInt32 (*output_fn) (void *, const char *, PRInt32), void *output_arg);

}


unsigned int NSSCryptoBase64::encode(const unsigned char * inData, 
						 	    unsigned int inLength,
								unsigned char * outData,
								unsigned int outLength) {

	if (outLength + 24 < inLength) {

		throw XSECCryptoException(XSECCryptoException::MemoryError,
			"NSS:Base64 - Output buffer not big enough for Base64 encode");

	}

  char * ret = BTOA_DataToAscii(inData, inLength);

  unsigned int outLen = strlen(ret) + 1;

	if (outLen > outLength) {

		throw XSECCryptoException(XSECCryptoException::MemoryError,
			"NSS:Base64 - Output buffer not big enough for Base64 encode and overflowed");

	}

  memcpy(ret, outData, outLen);

	return outLen;

}

unsigned int NSSCryptoBase64::encodeFinish(unsigned char * outData,
							 	      unsigned int outLength) {

	throw XSECCryptoException(XSECCryptoException::MemoryError,
  	"NSS:Base64 - finish not supported with NSS, call decode only once");

	return 0;

}

//#endif /* HAVE_NSS */
