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
 * XKMSConstants := Definitions of varius XKMSconstants (mainly strings)
 *
 * $Id$
 *
 */

#include <xsec/xkms/XKMSConstants.hpp>

#include <xercesc/util/XMLUniDefs.hpp>
#include <xercesc/util/XMLString.hpp>

XERCES_CPP_NAMESPACE_USE
// --------------------------------------------------------------------------------
//           Constant Strings Storage
// --------------------------------------------------------------------------------

const XMLCh * XKMSConstants::s_unicodeStrURIXKMS;
const XMLCh * XKMSConstants::s_unicodeStrURISOAP11;
const XMLCh * XKMSConstants::s_unicodeStrURISOAP12;

// NOTE All tags are unicode (UTF-16) - but are not marked as such

const XMLCh XKMSConstants::s_tagEncryption[] = {

	chLatin_E,
	chLatin_n,
	chLatin_c,
	chLatin_r,
	chLatin_y,
	chLatin_p,
	chLatin_t,
	chLatin_i,
	chLatin_o,
	chLatin_n,
	chNull
};

const XMLCh XKMSConstants::s_tagExchange[] = {

	chLatin_E,
	chLatin_x,
	chLatin_c,
	chLatin_h,
	chLatin_a,
	chLatin_n,
	chLatin_g,
	chLatin_e,
	chNull
};

const XMLCh XKMSConstants::s_tagId[] = {

	chLatin_I,
	chLatin_d,
	chNull
};

const XMLCh XKMSConstants::s_tagKeyInfo[] = {

	chLatin_K,
	chLatin_e,
	chLatin_y,
	chLatin_I,
	chLatin_n,
	chLatin_f,
	chLatin_o,
	chNull
};

const XMLCh XKMSConstants::s_tagKeyUsage[] = {

	chLatin_K,
	chLatin_e,
	chLatin_y,
	chLatin_U,
	chLatin_s,
	chLatin_a,
	chLatin_g,
	chLatin_e,
	chNull
};

const XMLCh XKMSConstants::s_tagLocateRequest[] = {

	chLatin_L,
	chLatin_o,
	chLatin_c,
	chLatin_a,
	chLatin_t,
	chLatin_e,
	chLatin_R,
	chLatin_e,
	chLatin_q,
	chLatin_u,
	chLatin_e,
	chLatin_s,
	chLatin_t,
	chNull
};

const XMLCh XKMSConstants::s_tagNonce[] = {

	chLatin_N,
	chLatin_o,
	chLatin_n,
	chLatin_c,
	chLatin_e,
	chNull
};

const XMLCh XKMSConstants::s_tagQueryKeyBinding[] = {

	chLatin_Q,
	chLatin_u,
	chLatin_e,
	chLatin_r,
	chLatin_y,
	chLatin_K,
	chLatin_e,
	chLatin_y,
	chLatin_B,
	chLatin_i,
	chLatin_n,
	chLatin_d,
	chLatin_i,
	chLatin_n,
	chLatin_g,
	chNull
};

const XMLCh XKMSConstants::s_tagRespondWith[] = {

	chLatin_R,
	chLatin_e,
	chLatin_s,
	chLatin_p,
	chLatin_o,
	chLatin_n,
	chLatin_d,
	chLatin_W,
	chLatin_i,
	chLatin_t,
	chLatin_h,
	chNull
};

const XMLCh XKMSConstants::s_tagService[] = {

	chLatin_S,
	chLatin_e,
	chLatin_r,
	chLatin_v,
	chLatin_i,
	chLatin_c,
	chLatin_e,
	chNull
};

const XMLCh XKMSConstants::s_tagSignature[] = {

	chLatin_S,
	chLatin_i,
	chLatin_g,
	chLatin_n,
	chLatin_a,
	chLatin_t,
	chLatin_u,
	chLatin_r,
	chLatin_e,
	chNull
};

// --------------------------------------------------------------------------------
//           Constant Strings Creation and Deletion
// --------------------------------------------------------------------------------

void XKMSConstants::create() {

	// Set up the static strings

	s_unicodeStrURIXKMS = XMLString::transcode(URI_ID_XKMS);
	s_unicodeStrURISOAP11 = XMLString::transcode(URI_ID_SOAP11);
	s_unicodeStrURISOAP12 = XMLString::transcode(URI_ID_SOAP12);

}

void XKMSConstants::destroy() {

	XMLString::release((XMLCh **) &s_unicodeStrURIXKMS);
	XMLString::release((XMLCh **) &s_unicodeStrURISOAP11);
	XMLString::release((XMLCh **) &s_unicodeStrURISOAP12);

}
