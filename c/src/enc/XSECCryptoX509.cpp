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
 * XSECCryptoX509:= A base class for handling X509 (V3) certificates
 *
 * $Id$
 *
 */

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/enc/XSECCryptoX509.hpp>
#include <xsec/framework/XSECError.hpp>
#include <xsec/enc/XSECCryptoException.hpp>

void XSECCryptoX509::loadX509PEM(const char * buf, unsigned int len) {

	const char * b;
	char * b1 = NULL;
	if (len == 0)
		b = buf;
	else {
		XSECnew(b1, char[len+1]);
		memcpy(b1, buf, len);
		b1[len] = '\0';
		b = b1;
	}

	char *p = strstr(buf, "-----BEGIN CERTIFICATE-----");

	if (p == NULL) {

		if (b1 != NULL)
			delete[] b1;

		throw XSECCryptoException(XSECCryptoException::X509Error,
		"X509::loadX509PEM - Cannot find start of PEM certificate");

	}

	p += strlen("-----BEGIN CERTIFICATE-----");

	while (*p == '\n' || *p == '\r' || *p == '-')
		p++;

	safeBuffer output;
	int i = 0;
	while (*p != '\0' && *p != '-') {
		output[i++] = *p;
		++p;
	}

	if (strstr(p, "-----END CERTIFICATE-----") != p) {

		if (b1 != NULL)
			delete[] b1;

		throw XSECCryptoException(XSECCryptoException::X509Error,
		"X509::loadX509PEM - Cannot find end of PEM certificate");

	}
	
	if (b1 != NULL)
		delete[] b1;

	output[i] = '\0';

	this->loadX509Base64Bin(output.rawCharBuffer(), i);

}

