/*
 * Copyright 2002-2010 The Apache Software Foundation.
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
 * limitations under the License.
 */

/*
 * XSEC
 *
 * XSECCryptoException:= How we throw exceptions in XSEC
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */

#include <xsec/enc/XSECCryptoException.hpp>

#include <stdlib.h>
#include <string.h>

extern const char * XSECCryptoExceptionStrings[] = {

	"No Error",
	"General error occurred somewhere in cryptographic routines",
	"Error Creating SHA1 MD",
	"Error in Base64",
	"Memory allocation error",
	"X509 Error",
	"DSA Error",
	"RSA Error",
	"Symmetric Error",
    "EC Error",
	"Unsupported Algorithm"

};

XSECCryptoException::XSECCryptoException(XSECCryptoExceptionType eNum, const char * inMsg) {

	if (eNum > UnknownError)
		type = UnknownError;
	else
		type = eNum;

	if (inMsg != NULL) {
		msg = new char[strlen(inMsg) + 1];
		strcpy(msg, inMsg);
	}
	else {
		msg = new char[strlen(XSECCryptoExceptionStrings[type]) + 1];
		strcpy(msg, XSECCryptoExceptionStrings[type]);
	}

}

XSECCryptoException::XSECCryptoException(XSECCryptoExceptionType eNum, safeBuffer &inMsg) {

	if (eNum > UnknownError)
		type = UnknownError;
	else
		type = eNum;

	
	msg = new char[strlen((char *) inMsg.rawBuffer()) + 1];
	strcpy(msg, (char *) inMsg.rawBuffer());

}

XSECCryptoException::XSECCryptoException(const XSECCryptoException &toCopy) {

	// Copy Constructor

	type = toCopy.type;
	if (toCopy.msg == NULL)
		msg = NULL;
	else {

		msg = new char[strlen(toCopy.msg) + 1];
		strcpy(msg, toCopy.msg);
	}
}

XSECCryptoException::~XSECCryptoException() {

	if (msg != NULL)
		delete[] msg;

}

const char * XSECCryptoException::getMsg(void) {

	return msg;

}

XSECCryptoException::XSECCryptoExceptionType XSECCryptoException::getType(void) {

	return type;

}
