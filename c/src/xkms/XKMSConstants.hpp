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
 * XKMSConstants := Definitions of varius XKMS constants (mainly strings)
 *
 * $Id$
 *
 */

#ifndef XKMSCONSTANTS_HEADER
#define XKMSCONSTANTS_HEADER

// Xerces
#include <xsec/framework/XSECDefs.hpp>

// Name Spaces

#define URI_ID_XKMS		"http://www.w3.org/2002/03/xkms#"
#define URI_ID_SOAP11   "http://schemas.xmlsoap.org/soap/envelope/"
#define URI_ID_SOAP12   "http://www.w3.org/2002/06/soap-envelope/"


// --------------------------------------------------------------------------------
//           Constant Strings Class
// --------------------------------------------------------------------------------

class DSIG_EXPORT XKMSConstants {

public:

	// URI_IDs
	static const XMLCh * s_unicodeStrURIXKMS;
	static const XMLCh * s_unicodeStrURISOAP11;
	static const XMLCh * s_unicodeStrURISOAP12;

	// Tags - note all are UTF-16, but not marked as such
	static const XMLCh s_tagApplication[];
	static const XMLCh s_tagEncryption[];
	static const XMLCh s_tagExchange[];
	static const XMLCh s_tagId[];
	static const XMLCh s_tagIdentifier[];
	static const XMLCh s_tagKeyBinding[];
	static const XMLCh s_tagKeyInfo[];
	static const XMLCh s_tagKeyUsage[];
	static const XMLCh s_tagKeyValue[];
	static const XMLCh s_tagLocateRequest[];
	static const XMLCh s_tagLocateResult[];
	static const XMLCh s_tagNonce[];
	static const XMLCh s_tagQueryKeyBinding[];
	static const XMLCh s_tagRequestId[];
	static const XMLCh s_tagRespondWith[];
	static const XMLCh s_tagResult[];
	static const XMLCh s_tagResultMajor[];
	static const XMLCh s_tagResultMinor[];
	static const XMLCh s_tagService[];
	static const XMLCh s_tagSignature[];
	static const XMLCh s_tagUnverifiedKeyBinding[];
	static const XMLCh s_tagUseKeyWith[];
	static const XMLCh s_tagValidateRequest[];
	static const XMLCh s_tagValidateResult[];

	// ResultMajor codes

	static const XMLCh s_tagResultMajorCodes[][16];
	static const XMLCh s_tagResultMinorCodes[][20];

	XKMSConstants();

	static void create();
	static void destroy();

};

#endif /* XKMSCONSTANTS_HEADER */

