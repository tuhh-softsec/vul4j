/*
 * Copyright 2003-2004 The Apache Software Foundation.
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

	m_prettyPrintFlag = true;

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

// --------------------------------------------------------------------------------
//           Set and Get Resolvers
// --------------------------------------------------------------------------------

void XSECEnv::doPrettyPrint(XERCES_CPP_NAMESPACE_QUALIFIER DOMNode * node) const {

	// Very simple
	if (m_prettyPrintFlag)
		node->appendChild(mp_doc->createTextNode(DSIGConstants::s_unicodeStrNL));

}

