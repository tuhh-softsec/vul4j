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
 * XSECException:= How we throw exceptions in XSEC
 *
 * Author(s): Berin Lautenbach
 *
 * $ID$
 *
 * $LOG$
 *
 */

#include <xsec/framework/XSECError.hpp>
#include <xsec/utils/XSECDOMUtils.hpp>

XSECException::XSECException(XSECExceptionType eNum, const XMLCh * inMsg) {

	if (eNum > UnknownError)
		type = UnknownError;
	else
		type = eNum;

	if (inMsg != NULL) {
		msg = XMLString::replicate(inMsg);
	}
	else {
		msg = XMLString::transcode(XSECExceptionStrings[type]);
	}

}

XSECException::XSECException(XSECExceptionType eNum, const char * inMsg) {

	if (eNum > UnknownError)
		type = UnknownError;
	else
		type = eNum;

	if (inMsg != NULL) {
		msg = XMLString::transcode(inMsg);
	}
	else {
		msg = XMLString::transcode(XSECExceptionStrings[type]);
	}

}

XSECException::XSECException(const XSECException &toCopy) {

	// Copy Constructor

	type = toCopy.type;
	if (toCopy.msg == NULL)
		msg = NULL;
	else {
		msg = XMLString::replicate(toCopy.msg);
	}
}

XSECException::~XSECException() {

	if (msg != NULL)
		XSEC_RELEASE_XMLCH(msg);

}

const XMLCh * XSECException::getMsg(void) {

	return msg;

}

XSECException::XSECExceptionType XSECException::getType(void) {

	return type;

}
