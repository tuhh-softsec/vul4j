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
#include <xsec/utils/XSECDOMUtils.hpp>

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

const XMLCh s_defaultXKMSPrefix[] = {

	chLatin_x,
	chLatin_k,
	chLatin_m,
	chLatin_s,
	chNull

};

// --------------------------------------------------------------------------------
//           Default Id names
// --------------------------------------------------------------------------------

const XMLCh s_Id[] = {

	chLatin_I,
	chLatin_d,
	chNull

};

const XMLCh s_id[] = {

	chLatin_i,
	chLatin_d,
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
	mp_xkmsPrefixNS = XMLString::replicate(s_defaultXKMSPrefix);

	m_prettyPrintFlag = true;

	mp_URIResolver = NULL;

	// Set up our formatter
	XSECnew(mp_formatter, XSECSafeBufferFormatter("UTF-8",XMLFormatter::NoEscapes, 
												XMLFormatter::UnRep_CharRef));

	// Set up IDs
	m_idByAttributeNameFlag = true;		// At the moment this is on by default
	// Register "Id" and "id" as valid Attribute names
	registerIdAttributeName(s_Id);
	registerIdAttributeName(s_id);

}

XSECEnv::XSECEnv(const XSECEnv & theOther) {

	mp_doc = theOther.mp_doc;

	mp_prefixNS = XMLString::replicate(theOther.mp_prefixNS);
	mp_ecPrefixNS = XMLString::replicate(theOther.mp_ecPrefixNS);
	mp_xpfPrefixNS = XMLString::replicate(theOther.mp_xpfPrefixNS);
	mp_xencPrefixNS = XMLString::replicate(theOther.mp_xencPrefixNS);
	mp_xkmsPrefixNS = XMLString::replicate(theOther.mp_xkmsPrefixNS);

	m_prettyPrintFlag = theOther.m_prettyPrintFlag;

	if (theOther.mp_URIResolver != NULL)
		mp_URIResolver = theOther.mp_URIResolver->clone();
	else
		mp_URIResolver = NULL;

	// Set up our formatter
	XSECnew(mp_formatter, XSECSafeBufferFormatter("UTF-8",XMLFormatter::NoEscapes, 
												XMLFormatter::UnRep_CharRef));

	// Set up IDs
	m_idByAttributeNameFlag = theOther.m_idByAttributeNameFlag;

	for (int i = 0; i < theOther.getIdAttributeNameListSize() ; ++i) {
		registerIdAttributeName(theOther.getIdAttributeNameListItem(i));
	}

}

XSECEnv::~XSECEnv() {

	if (mp_formatter != NULL) {
		delete mp_formatter;
	}

	if (mp_prefixNS != NULL) {
		XSEC_RELEASE_XMLCH(mp_prefixNS);
	}

	if (mp_ecPrefixNS != NULL) {
		XSEC_RELEASE_XMLCH(mp_ecPrefixNS);
	}
	
	if (mp_xpfPrefixNS != NULL) {
		XSEC_RELEASE_XMLCH(mp_xpfPrefixNS);
	}

	if (mp_xencPrefixNS != NULL) {
		XSEC_RELEASE_XMLCH(mp_xencPrefixNS);
	}

	if (mp_xkmsPrefixNS != NULL) {
		XSEC_RELEASE_XMLCH(mp_xkmsPrefixNS);
	}

	if (mp_URIResolver != NULL) {
		delete mp_URIResolver;
	}

	// Clean up Id attribute names
	IdNameVectorType::iterator it;

	for (it = m_idAttributeNameList.begin(); it != m_idAttributeNameList.end(); it++) {
		XSEC_RELEASE_XMLCH((*it));
	}

	m_idAttributeNameList.empty();


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
		XSEC_RELEASE_XMLCH(mp_prefixNS);

	mp_prefixNS = XMLString::replicate(prefix);

}

void XSECEnv::setECNSPrefix(const XMLCh * prefix) {

	if (mp_ecPrefixNS != NULL)
		XSEC_RELEASE_XMLCH(mp_ecPrefixNS);

	mp_ecPrefixNS = XMLString::replicate(prefix);

}

void XSECEnv::setXPFNSPrefix(const XMLCh * prefix) {

	if (mp_xpfPrefixNS != NULL)
		XSEC_RELEASE_XMLCH(mp_xpfPrefixNS);

	mp_xpfPrefixNS = XMLString::replicate(prefix);

}

void XSECEnv::setXENCNSPrefix(const XMLCh * prefix) {

	if (mp_xencPrefixNS != NULL)
		XSEC_RELEASE_XMLCH(mp_xencPrefixNS);

	mp_xencPrefixNS = XMLString::replicate(prefix);

}

void XSECEnv::setXKMSNSPrefix(const XMLCh * prefix) {

	if (mp_xkmsPrefixNS != NULL)
		XSEC_RELEASE_XMLCH(mp_xkmsPrefixNS);

	mp_xkmsPrefixNS = XMLString::replicate(prefix);

}

// --------------------------------------------------------------------------------
//           Id Attribute Names Handling
// --------------------------------------------------------------------------------

void XSECEnv::setIdByAttributeName(bool flag) {

	m_idByAttributeNameFlag = flag;

}

bool XSECEnv::getIdByAttributeName(void) const {

	return m_idByAttributeNameFlag;

}

bool XSECEnv::isRegisteredIdAttributeName(const XMLCh * name) const {

	int sz = (int) m_idAttributeNameList.size();

	for (int i = 0; i < sz; ++i) {
		if (strEquals(m_idAttributeNameList[i], name))
			return true;
	}

	return false;

}

void XSECEnv::registerIdAttributeName(const XMLCh * name) {

	if (isRegisteredIdAttributeName(name))
		return;

	m_idAttributeNameList.push_back(XMLString::replicate(name));

}

bool XSECEnv::deregisterIdAttributeName(const XMLCh * name) {

	IdNameVectorType::iterator it;

	for (it = m_idAttributeNameList.begin(); it != m_idAttributeNameList.end(); it++) {
		if (strEquals(*it, name)) {

			// Remove this item
			XSEC_RELEASE_XMLCH((*it));
			m_idAttributeNameList.erase(it);
			return true;
		}
	}

	return false;
}

int XSECEnv::getIdAttributeNameListSize() const {

	return (int) m_idAttributeNameList.size();

}

const XMLCh * XSECEnv::getIdAttributeNameListItem(int index) const {

	if (index >= 0 && index < (int) m_idAttributeNameList.size())
		return m_idAttributeNameList[index];

	return NULL;

}

// --------------------------------------------------------------------------------
//           Set and Get Resolvers
// --------------------------------------------------------------------------------

void XSECEnv::doPrettyPrint(XERCES_CPP_NAMESPACE_QUALIFIER DOMNode * node) const {

	// Very simple
	if (m_prettyPrintFlag)
		node->appendChild(mp_doc->createTextNode(DSIGConstants::s_unicodeStrNL));

}

