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
 * DSIGConstants := Definitions of varius DSIG constants (mainly strings)
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */

#ifndef DSIGCONSTANTS_HEADER
#define DSIGCONSTANTS_HEADER

#include <xsec/utils/XSECSafeBuffer.hpp>

// Xerces
#include <xercesc/util/XMLString.hpp>

XSEC_USING_XERCES(XMLString);

// Name Spaces

#define URI_ID_DSIG		"http://www.w3.org/2000/09/xmldsig#"
#define URI_ID_EC		"http://www.w3.org/2001/10/xml-exc-c14n#"
// Also used as algorithm ID for XPATH_FILTER
#define URI_ID_XPF		"http://www.w3.org/2002/06/xmldsig-filter2"

// Hashing Algorithms

#define URI_ID_SHA1		"http://www.w3.org/2000/09/xmldsig#sha1"
#define URI_ID_MD5		"http://www.w3.org/2001/04/xmldsig-more#md5"

// Transforms

#define URI_ID_BASE64			"http://www.w3.org/2000/09/xmldsig#base64"
#define URI_ID_XPATH			"http://www.w3.org/TR/1999/REC-xpath-19991116"
#define URI_ID_XSLT				"http://www.w3.org/TR/1999/REC-xslt-19991116"
#define URI_ID_ENVELOPE			"http://www.w3.org/2000/09/xmldsig#enveloped-signature"
#define URI_ID_C14N_NOC			"http://www.w3.org/TR/2001/REC-xml-c14n-20010315"
#define URI_ID_C14N_COM			"http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments"
#define URI_ID_EXC_C14N_NOC		"http://www.w3.org/2001/10/xml-exc-c14n#"
#define URI_ID_EXC_C14N_COM		"http://www.w3.org/2001/10/xml-exc-c14n#WithComments"
#define XPATH_EXPR_ENVELOPE		"count(ancestor-or-self::dsig:Signature | \
								 here()/ancestor::dsig:Signature[1]) > \
								 count(ancestor-or-self::dsig:Signature)"

// Signature Algorithms

#define URI_ID_SIG_BASE		"http://www.w3.org/2000/09/xmldsig#"
#define URI_ID_SIG_DSA		"dsa"
#define URI_ID_SIG_HMAC		"hmac"
#define URI_ID_SIG_SHA1		"sha1"
#define URI_ID_SIG_RSA		"rsa"

#define URI_ID_DSA_SHA1		"http://www.w3.org/2000/09/xmldsig#dsa-sha1"
#define URI_ID_HMAC_SHA1	"http://www.w3.org/2000/09/xmldsig#hmac-sha1"
#define URI_ID_RSA_SHA1		"http://www.w3.org/2000/09/xmldsig#rsa-sha1"

// General

#define URI_ID_XMLNS	"http://www.w3.org/2000/xmlns/"
#define URI_ID_MANIFEST "http://www.w3.org/2000/09/xmldsig#Manifest"
#define URI_ID_RAWX509  "http://www.w3.org/2000/09/xmldsig#rawX509Certificate"

// Internal Crypto Providers

#define PROV_OPENSSL	"OpenSSL Provider"
#define PROV_WINCAPI	"WinCAPI Provider"

// Enumerated Types


enum canonicalizationMethod {

	CANON_NONE					= 0,			// No method defined
	CANON_C14N_NOC				= 1,			// C14n without comments
	CANON_C14N_COM				= 2, 			// C14n with comments
	CANON_C14NE_NOC				= 3,			// C14n Exclusive (without comments)
	CANON_C14NE_COM				= 4				// C14n Exlusive (with Comments
};

enum signatureMethod {

	SIGNATURE_NONE				= 0,			// No method defined
	SIGNATURE_DSA				= 1, 			// DSA
	SIGNATURE_HMAC				= 2,			// Hash MAC
	SIGNATURE_RSA				= 3				// RSA
};


enum hashMethod {

	HASH_NONE					= 0,			// No method defined
	HASH_SHA1					= 1, 			// SHA1
	HASH_MD5					= 2
};

enum transformType {

	TRANSFORM_BASE64,
	TRANSFORM_C14N,
	TRANSFORM_EXC_C14N,
	TRANSFORM_ENVELOPED_SIGNATURE,
	TRANSFORM_XPATH,
	TRANSFORM_XSLT,
	TRANSFORM_XPATH_FILTER

};

enum xpathFilterType {

	FILTER_UNION			= 0,	/** Results should be added to previous nodeset */
	FILTER_INTERSECT		= 1,	/** Results should be included if in prev nodeset */
	FILTER_SUBTRACT			= 2		/** Results should be subtracted from prev nodeset */

};


// --------------------------------------------------------------------------------
//           Some utility functions
// --------------------------------------------------------------------------------

inline
bool canonicalizationMethod2URI(safeBuffer &uri, canonicalizationMethod cm) {

	switch (cm) {

	case (CANON_C14N_NOC) :

		uri = URI_ID_C14N_NOC;
		break;

	case (CANON_C14N_COM) :

		uri = URI_ID_C14N_COM;
		break;

	case (CANON_C14NE_NOC) :

		uri = URI_ID_EXC_C14N_NOC;
		break;

	case (CANON_C14NE_COM) :

		uri = URI_ID_EXC_C14N_COM;
		break;

	default :
		return false;		// Unknown type

	}

	return true;

}

inline
bool signatureHashMethod2URI(safeBuffer &uri, signatureMethod sm, hashMethod hm) {

	uri = URI_ID_SIG_BASE;

	switch (sm) {

	case (SIGNATURE_DSA) :

		uri.sbStrcatIn(URI_ID_SIG_DSA);
		break;

	case (SIGNATURE_HMAC) :

		uri.sbStrcatIn(URI_ID_SIG_HMAC);
		break;

	case (SIGNATURE_RSA) :

		uri.sbStrcatIn(URI_ID_SIG_RSA);
		break;

	default :

		return false;

	}

	uri.sbStrcatIn("-");

	switch (hm) {

	case (HASH_SHA1) :

		uri.sbStrcatIn(URI_ID_SIG_SHA1);
		break;

	default:

		return false;

	}

	return true;

}

inline
bool hashMethod2URI(safeBuffer &uri, hashMethod hm) {

	switch (hm) {

	case (HASH_SHA1) :

		uri = URI_ID_SHA1;
		break;

	case (HASH_MD5) :

		uri = URI_ID_MD5;
		break;

	default:
		return false;

	}

	return true;

}

// --------------------------------------------------------------------------------
//           Constant Strings Class
// --------------------------------------------------------------------------------

class DSIG_EXPORT DSIGConstants {

public:

	// General strings

	static const XMLCh * const & s_unicodeStrEmpty;		// ""
	static const XMLCh * const & s_unicodeStrNL;			// "\n"
	static const XMLCh * const & s_unicodeStrXmlns;		// "xmlns"

	// DSIG Element Strings
	static const XMLCh * const & s_unicodeStrAlgorithm;

	// URI_IDs
	static const XMLCh * const & s_unicodeStrURIDSIG;
	static const XMLCh * const & s_unicodeStrURIEC;
	static const XMLCh * const & s_unicodeStrURIXPF;

	static const XMLCh * const & s_unicodeStrURIRawX509;
	static const XMLCh * const & s_unicodeStrURISHA1;
	static const XMLCh * const & s_unicodeStrURIMD5;		// Not recommended
	static const XMLCh * const & s_unicodeStrURIBASE64;
	static const XMLCh * const & s_unicodeStrURIXPATH;
	static const XMLCh * const & s_unicodeStrURIXSLT;
	static const XMLCh * const & s_unicodeStrURIENVELOPE;
	static const XMLCh * const & s_unicodeStrURIC14N_NOC;
	static const XMLCh * const & s_unicodeStrURIC14N_COM;
	static const XMLCh * const & s_unicodeStrURIEXC_C14N_NOC;
	static const XMLCh * const & s_unicodeStrURIEXC_C14N_COM;
	static const XMLCh * const & s_unicodeStrURIDSA_SHA1;
	static const XMLCh * const & s_unicodeStrURIRSA_SHA1;
	static const XMLCh * const & s_unicodeStrURIHMAC_SHA1;
	static const XMLCh * const & s_unicodeStrURIXMLNS;
	static const XMLCh * const & s_unicodeStrURIMANIFEST;

	// Internal Crypto Providers
	static const XMLCh * const & s_unicodeStrPROVOpenSSL;
	static const XMLCh * const & s_unicodeStrPROVWinCAPI;


	DSIGConstants();

	static void create();
	static void destroy();

};




inline
const XMLCh * canonicalizationMethod2UNICODEURI(canonicalizationMethod cm) {

	switch (cm) {

	case (CANON_C14N_NOC) :

		return DSIGConstants::s_unicodeStrURIC14N_NOC;
		break;

	case (CANON_C14N_COM) :

		return DSIGConstants::s_unicodeStrURIC14N_COM;
		break;

	case (CANON_C14NE_NOC) :

		return DSIGConstants::s_unicodeStrURIEXC_C14N_NOC;
		break;

	case (CANON_C14NE_COM) :

		return DSIGConstants::s_unicodeStrURIEXC_C14N_COM;
		break;

	default :
		break;

	}

	return DSIGConstants::s_unicodeStrEmpty;

}

#endif /* DSIGCONSTANTS_HEADER */

