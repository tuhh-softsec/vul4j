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
 * XSECEnv := Configuration class - used by the other classes to retrieve
 *            information on the environment they are working under
 *
 * $Id$
 *
 */

// XSEC Includes
#include <xsec/framework/XSECEnv.hpp>
#include <xsec/framework/XSECError.hpp>
#include <xsec/framework/XSECURIResolver.hpp>
#include <xsec/dsig/DSIGConstants.hpp>

#include <xercesc/util/XMLUniDefs.hpp>

XERCES_CPP_NAMESPACE_USE

// --------------------------------------------------------------------------------
//           Default prefix strings
// --------------------------------------------------------------------------------

const XMLCh s_defaultECPrefix[] = {

	chLatin_e,
	chLatin_c,
	chNull

};

const XMLCh s_defaultXPFPrefix[] = {

	chLatin_x,
	chLatin_p,
	chLatin_f,
	chNull

};

const XMLCh s_defaultXENCPrefix[] = {

	chLatin_x,
	chLatin_e,
	chLatin_n,
	chLatin_c,
	chNull

};

// --------------------------------------------------------------------------------
//           Env
// --------------------------------------------------------------------------------

// Constructors and Destructors

XSECEnv::XSECEnv(DOMDocument *doc) {

	mp_doc = doc;

	mp_prefixNS = XMLString::replicate(DSIGConstants::s_unicodeStrEmpty);
	mp_ecPrefixNS = XMLString::replicate(s_defaultECPrefix);
	mp_xpfPrefixNS = XMLString::replicate(s_defaultXPFPrefix);
	mp_xencPrefixNS = XMLString::replicate(s_defaultXENCPrefix);

	mp_URIResolver = NULL;

	// Set up our formatter
	XSECnew(mp_formatter, XSECSafeBufferFormatter("UTF-8",XMLFormatter::NoEscapes, 
												XMLFormatter::UnRep_CharRef));


}

XSECEnv::~XSECEnv() {

	if (mp_formatter != NULL) {
		delete mp_formatter;
	}

	if (mp_prefixNS != NULL) {
		delete[] mp_prefixNS;
	}

	if (mp_ecPrefixNS != NULL) {
		delete[] mp_ecPrefixNS;
	}
	
	if (mp_xpfPrefixNS != NULL) {
		delete[] mp_xpfPrefixNS;
	}

	if (mp_xencPrefixNS != NULL) {
		delete[] mp_xencPrefixNS;
	}

	if (mp_URIResolver != NULL) {
		delete mp_URIResolver;
	}

}

// --------------------------------------------------------------------------------
//           Set and Get Resolvers
// --------------------------------------------------------------------------------


void XSECEnv::setURIResolver(XSECURIResolver * resolver) {

	if (mp_URIResolver != 0)
		delete mp_URIResolver;

	mp_URIResolver = resolver->clone();

}

XSECURIResolver * XSECEnv::getURIResolver(void) const {

	return mp_URIResolver;

}

// --------------------------------------------------------------------------------
//           Set and Get Prefixes
// --------------------------------------------------------------------------------

void XSECEnv::setDSIGNSPrefix(const XMLCh * prefix) {

	if (mp_prefixNS != NULL)
		delete[] mp_prefixNS;

	mp_prefixNS = XMLString::replicate(prefix);

}

void XSECEnv::setECNSPrefix(const XMLCh * prefix) {

	if (mp_ecPrefixNS != NULL)
		delete[] mp_ecPrefixNS;

	mp_ecPrefixNS = XMLString::replicate(prefix);

}

void XSECEnv::setXPFNSPrefix(const XMLCh * prefix) {

	if (mp_xpfPrefixNS != NULL)
		delete[] mp_xpfPrefixNS;

	mp_xpfPrefixNS = XMLString::replicate(prefix);

}

void XSECEnv::setXENCNSPrefix(const XMLCh * prefix) {

	if (mp_xencPrefixNS != NULL)
		delete[] mp_xencPrefixNS;

	mp_xencPrefixNS = XMLString::replicate(prefix);

}
