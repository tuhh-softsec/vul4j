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
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
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

#include <xsec/dsig/DSIGConstants.hpp>

// --------------------------------------------------------------------------------
//           Constant Strings Storage
// --------------------------------------------------------------------------------

const XMLCh * DSIGConstants::s_unicodeStrEmpty;		// ""
const XMLCh * DSIGConstants::s_unicodeStrNL;			// "\n"
const XMLCh * DSIGConstants::s_unicodeStrXmlns;		// "xmlns"

const XMLCh * DSIGConstants::s_unicodeStrAlgorithm;

const XMLCh * DSIGConstants::s_unicodeStrURIDSIG;
const XMLCh * DSIGConstants::s_unicodeStrURIEC;
const XMLCh * DSIGConstants::s_unicodeStrURIXPF;
const XMLCh * DSIGConstants::s_unicodeStrURIXENC;

const XMLCh * DSIGConstants::s_unicodeStrURIRawX509;
const XMLCh * DSIGConstants::s_unicodeStrURISHA1;
const XMLCh * DSIGConstants::s_unicodeStrURIMD5;		// Not recommended
const XMLCh * DSIGConstants::s_unicodeStrURIBASE64;
const XMLCh * DSIGConstants::s_unicodeStrURIXPATH;
const XMLCh * DSIGConstants::s_unicodeStrURIXSLT;
const XMLCh * DSIGConstants::s_unicodeStrURIENVELOPE;
const XMLCh * DSIGConstants::s_unicodeStrURIC14N_NOC;
const XMLCh * DSIGConstants::s_unicodeStrURIC14N_COM;
const XMLCh * DSIGConstants::s_unicodeStrURIEXC_C14N_NOC;
const XMLCh * DSIGConstants::s_unicodeStrURIEXC_C14N_COM;
const XMLCh * DSIGConstants::s_unicodeStrURIDSA_SHA1;
const XMLCh * DSIGConstants::s_unicodeStrURIRSA_SHA1;
const XMLCh * DSIGConstants::s_unicodeStrURIHMAC_SHA1;
const XMLCh * DSIGConstants::s_unicodeStrURIXMLNS;
const XMLCh * DSIGConstants::s_unicodeStrURIMANIFEST;
const XMLCh * DSIGConstants::s_unicodeStrURI3DES_CBC;
const XMLCh * DSIGConstants::s_unicodeStrURIAES128_CBC;
const XMLCh * DSIGConstants::s_unicodeStrURIAES192_CBC;
const XMLCh * DSIGConstants::s_unicodeStrURIAES256_CBC;
const XMLCh * DSIGConstants::s_unicodeStrURIKW_AES128;
const XMLCh * DSIGConstants::s_unicodeStrURIKW_AES192;
const XMLCh * DSIGConstants::s_unicodeStrURIKW_AES256;
const XMLCh * DSIGConstants::s_unicodeStrURIRSA_1_5;

const XMLCh * DSIGConstants::s_unicodeStrURIXENC_ELEMENT;
const XMLCh * DSIGConstants::s_unicodeStrURIXENC_CONTENT;

const XMLCh * DSIGConstants::s_unicodeStrPROVOpenSSL;
const XMLCh * DSIGConstants::s_unicodeStrPROVWinCAPI;

// --------------------------------------------------------------------------------
//           Constant Strings Creation and Deletion
// --------------------------------------------------------------------------------

void DSIGConstants::create() {

	// Set up the static strings

	s_unicodeStrEmpty = XMLString::transcode("");
	s_unicodeStrNL = XMLString::transcode("\n");
	s_unicodeStrXmlns = XMLString::transcode("xmlns");

	s_unicodeStrAlgorithm = XMLString::transcode("Algorithm");

	s_unicodeStrURIRawX509 = XMLString::transcode(URI_ID_RAWX509);
	s_unicodeStrURIDSIG = XMLString::transcode(URI_ID_DSIG);
	s_unicodeStrURIEC = XMLString::transcode(URI_ID_EC);
	s_unicodeStrURIXPF = XMLString::transcode(URI_ID_XPF);
	s_unicodeStrURIXENC = XMLString::transcode(URI_ID_XENC);
	s_unicodeStrURISHA1 = XMLString::transcode(URI_ID_SHA1);
	s_unicodeStrURIMD5 = XMLString::transcode(URI_ID_MD5);
	s_unicodeStrURIBASE64 = XMLString::transcode(URI_ID_BASE64);
	s_unicodeStrURIXPATH = XMLString::transcode(URI_ID_XPATH);
	s_unicodeStrURIXSLT = XMLString::transcode(URI_ID_XSLT);
	s_unicodeStrURIENVELOPE = XMLString::transcode(URI_ID_ENVELOPE);
	s_unicodeStrURIC14N_NOC = XMLString::transcode(URI_ID_C14N_NOC);
	s_unicodeStrURIC14N_COM = XMLString::transcode(URI_ID_C14N_COM);
	s_unicodeStrURIEXC_C14N_NOC = XMLString::transcode(URI_ID_EXC_C14N_NOC);
	s_unicodeStrURIEXC_C14N_COM = XMLString::transcode(URI_ID_EXC_C14N_COM);
	s_unicodeStrURIDSA_SHA1 = XMLString::transcode(URI_ID_DSA_SHA1);
	s_unicodeStrURIRSA_SHA1 = XMLString::transcode(URI_ID_RSA_SHA1);
	s_unicodeStrURIHMAC_SHA1 = XMLString::transcode(URI_ID_HMAC_SHA1);
	s_unicodeStrURIXMLNS = XMLString::transcode(URI_ID_XMLNS);
	s_unicodeStrURIMANIFEST = XMLString::transcode(URI_ID_MANIFEST);
	
	s_unicodeStrURI3DES_CBC	= XMLString::transcode(URI_ID_3DES_CBC);
	s_unicodeStrURIAES128_CBC	= XMLString::transcode(URI_ID_AES128_CBC);
	s_unicodeStrURIAES192_CBC	= XMLString::transcode(URI_ID_AES192_CBC);
	s_unicodeStrURIAES256_CBC	= XMLString::transcode(URI_ID_AES256_CBC);
	s_unicodeStrURIKW_AES128 = XMLString::transcode(URI_ID_KW_AES128);
	s_unicodeStrURIKW_AES192 = XMLString::transcode(URI_ID_KW_AES192);
	s_unicodeStrURIKW_AES256 = XMLString::transcode(URI_ID_KW_AES256);
	s_unicodeStrURIRSA_1_5 = XMLString::transcode(URI_ID_RSA_1_5);

	s_unicodeStrURIXENC_ELEMENT = XMLString::transcode(URI_ID_XENC_ELEMENT);
	s_unicodeStrURIXENC_CONTENT = XMLString::transcode(URI_ID_XENC_CONTENT);

	s_unicodeStrPROVOpenSSL = XMLString::transcode(PROV_OPENSSL);
	s_unicodeStrPROVWinCAPI = XMLString::transcode(PROV_WINCAPI);


}

void DSIGConstants::destroy() {

	// Delete the static strings
	delete[] (XMLCh *) s_unicodeStrEmpty;
	delete[] (XMLCh *) s_unicodeStrNL;
	delete[] (XMLCh *) s_unicodeStrXmlns;

	delete[] (XMLCh *) s_unicodeStrAlgorithm;

	delete[] (XMLCh *) s_unicodeStrURIRawX509;
	delete[] (XMLCh *) s_unicodeStrURIDSIG;
	delete[] (XMLCh *) s_unicodeStrURIEC;
	delete[] (XMLCh *) s_unicodeStrURIXPF;
	delete[] (XMLCh *) s_unicodeStrURIXENC;
	delete[] (XMLCh *) s_unicodeStrURISHA1;
	delete[] (XMLCh *) s_unicodeStrURIMD5;
	delete[] (XMLCh *) s_unicodeStrURIBASE64;
	delete[] (XMLCh *) s_unicodeStrURIXPATH;
	delete[] (XMLCh *) s_unicodeStrURIXSLT;
	delete[] (XMLCh *) s_unicodeStrURIENVELOPE;
	delete[] (XMLCh *) s_unicodeStrURIC14N_NOC;
	delete[] (XMLCh *) s_unicodeStrURIC14N_COM;
	delete[] (XMLCh *) s_unicodeStrURIEXC_C14N_NOC;
	delete[] (XMLCh *) s_unicodeStrURIEXC_C14N_COM;
	delete[] (XMLCh *) s_unicodeStrURIDSA_SHA1;
	delete[] (XMLCh *) s_unicodeStrURIRSA_SHA1;
	delete[] (XMLCh *) s_unicodeStrURIHMAC_SHA1;
	delete[] (XMLCh *) s_unicodeStrURIXMLNS;
	delete[] (XMLCh *) s_unicodeStrURIMANIFEST;

	delete[] (XMLCh *) s_unicodeStrURI3DES_CBC;
	delete[] (XMLCh *) s_unicodeStrURIAES128_CBC;
	delete[] (XMLCh *) s_unicodeStrURIAES192_CBC;
	delete[] (XMLCh *) s_unicodeStrURIAES256_CBC;
	delete[] (XMLCh *) s_unicodeStrURIKW_AES128;
	delete[] (XMLCh *) s_unicodeStrURIKW_AES192;
	delete[] (XMLCh *) s_unicodeStrURIKW_AES256;
	delete[] (XMLCh *) s_unicodeStrURIRSA_1_5;

	delete[] (XMLCh *) s_unicodeStrURIXENC_ELEMENT;
	delete[] (XMLCh *) s_unicodeStrURIXENC_CONTENT;

	delete[] (XMLCh *) s_unicodeStrPROVOpenSSL;
	delete[] (XMLCh *) s_unicodeStrPROVWinCAPI;

}
