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
 */

#include <xsec/dsig/DSIGConstants.hpp>

// --------------------------------------------------------------------------------
//           Actual strings
// --------------------------------------------------------------------------------

static XMLCh * s_unicodeStrEmpty;
static XMLCh * s_unicodeStrNL;
static XMLCh * s_unicodeStrXmlns;

static XMLCh * s_unicodeStrAlgorithm;

static XMLCh * s_unicodeStrURIDSIG;
static XMLCh * s_unicodeStrURISHA1;
static XMLCh * s_unicodeStrURIBASE64;
static XMLCh * s_unicodeStrURIXPATH;
static XMLCh * s_unicodeStrURIXSLT;
static XMLCh * s_unicodeStrURIENVELOPE;
static XMLCh * s_unicodeStrURIC14N_NOC;
static XMLCh * s_unicodeStrURIC14N_COM;
static XMLCh * s_unicodeStrURIEXC_C14N_NOC;
static XMLCh * s_unicodeStrURIEXC_C14N_COM;
static XMLCh * s_unicodeStrURIDSA_SHA1;
static XMLCh * s_unicodeStrURIRSA_SHA1;
static XMLCh * s_unicodeStrURIHMAC_SHA1;
static XMLCh * s_unicodeStrURIXMLNS;
static XMLCh * s_unicodeStrURIMANIFEST;


// --------------------------------------------------------------------------------
//           Class instances
// --------------------------------------------------------------------------------

const XMLCh * const & DSIGConstants::s_unicodeStrEmpty = ::s_unicodeStrEmpty;
const XMLCh * const & DSIGConstants::s_unicodeStrNL = ::s_unicodeStrNL;
const XMLCh * const & DSIGConstants::s_unicodeStrXmlns = ::s_unicodeStrXmlns;

const XMLCh * const & DSIGConstants::s_unicodeStrAlgorithm = ::s_unicodeStrAlgorithm;

const XMLCh * const & DSIGConstants::s_unicodeStrURIDSIG = ::s_unicodeStrURIDSIG;
const XMLCh * const & DSIGConstants::s_unicodeStrURISHA1 = ::s_unicodeStrURISHA1;
const XMLCh * const & DSIGConstants::s_unicodeStrURIBASE64 = ::s_unicodeStrURIBASE64;
const XMLCh * const & DSIGConstants::s_unicodeStrURIXPATH = ::s_unicodeStrURIXPATH;
const XMLCh * const & DSIGConstants::s_unicodeStrURIXSLT = ::s_unicodeStrURIXSLT;
const XMLCh * const & DSIGConstants::s_unicodeStrURIENVELOPE = ::s_unicodeStrURIENVELOPE;
const XMLCh * const & DSIGConstants::s_unicodeStrURIC14N_NOC = ::s_unicodeStrURIC14N_NOC;
const XMLCh * const & DSIGConstants::s_unicodeStrURIC14N_COM = ::s_unicodeStrURIC14N_COM;
const XMLCh * const & DSIGConstants::s_unicodeStrURIEXC_C14N_NOC = ::s_unicodeStrURIEXC_C14N_NOC;
const XMLCh * const & DSIGConstants::s_unicodeStrURIEXC_C14N_COM = ::s_unicodeStrURIEXC_C14N_COM;
const XMLCh * const & DSIGConstants::s_unicodeStrURIDSA_SHA1 = ::s_unicodeStrURIDSA_SHA1;
const XMLCh * const & DSIGConstants::s_unicodeStrURIRSA_SHA1 = ::s_unicodeStrURIRSA_SHA1;
const XMLCh * const & DSIGConstants::s_unicodeStrURIHMAC_SHA1 = ::s_unicodeStrURIHMAC_SHA1;
const XMLCh * const & DSIGConstants::s_unicodeStrURIXMLNS = ::s_unicodeStrURIXMLNS;
const XMLCh * const & DSIGConstants::s_unicodeStrURIMANIFEST = ::s_unicodeStrURIMANIFEST;

// --------------------------------------------------------------------------------
//           Constant Strings Class
// --------------------------------------------------------------------------------


void DSIGConstants::create() {

	// Set up the static strings

	::s_unicodeStrEmpty = XMLString::transcode("");
	::s_unicodeStrNL = XMLString::transcode("\n");
	::s_unicodeStrXmlns = XMLString::transcode("xmlns");

	::s_unicodeStrAlgorithm = XMLString::transcode("Algorithm");

	::s_unicodeStrURIDSIG = XMLString::transcode(URI_ID_DSIG);
	::s_unicodeStrURISHA1 = XMLString::transcode(URI_ID_SHA1);
	::s_unicodeStrURIBASE64 = XMLString::transcode(URI_ID_BASE64);
	::s_unicodeStrURIXPATH = XMLString::transcode(URI_ID_XPATH);
	::s_unicodeStrURIXSLT = XMLString::transcode(URI_ID_XSLT);
	::s_unicodeStrURIENVELOPE = XMLString::transcode(URI_ID_ENVELOPE);
	::s_unicodeStrURIC14N_NOC = XMLString::transcode(URI_ID_C14N_NOC);
	::s_unicodeStrURIC14N_COM = XMLString::transcode(URI_ID_C14N_COM);
	::s_unicodeStrURIEXC_C14N_NOC = XMLString::transcode(URI_ID_EXC_C14N_NOC);
	::s_unicodeStrURIEXC_C14N_COM = XMLString::transcode(URI_ID_EXC_C14N_COM);
	::s_unicodeStrURIDSA_SHA1 = XMLString::transcode(URI_ID_DSA_SHA1);
	::s_unicodeStrURIRSA_SHA1 = XMLString::transcode(URI_ID_RSA_SHA1);
	::s_unicodeStrURIHMAC_SHA1 = XMLString::transcode(URI_ID_HMAC_SHA1);
	::s_unicodeStrURIXMLNS = XMLString::transcode(URI_ID_XMLNS);
	::s_unicodeStrURIMANIFEST = XMLString::transcode(URI_ID_MANIFEST);


}

void DSIGConstants::destroy() {

	// Delete the static strings
	delete[] ::s_unicodeStrEmpty;
	delete[] ::s_unicodeStrNL;
	delete[] ::s_unicodeStrXmlns;

	delete[] ::s_unicodeStrAlgorithm;

	delete[] ::s_unicodeStrURIDSIG;
	delete[] ::s_unicodeStrURISHA1;
	delete[] ::s_unicodeStrURIBASE64;
	delete[] ::s_unicodeStrURIXPATH;
	delete[] ::s_unicodeStrURIXSLT;
	delete[] ::s_unicodeStrURIENVELOPE;
	delete[] ::s_unicodeStrURIC14N_NOC;
	delete[] ::s_unicodeStrURIC14N_COM;
	delete[] ::s_unicodeStrURIEXC_C14N_NOC;
	delete[] ::s_unicodeStrURIEXC_C14N_COM;
	delete[] ::s_unicodeStrURIDSA_SHA1;
	delete[] ::s_unicodeStrURIRSA_SHA1;
	delete[] ::s_unicodeStrURIHMAC_SHA1;
	delete[] ::s_unicodeStrURIXMLNS;
	delete[] ::s_unicodeStrURIMANIFEST;

}
