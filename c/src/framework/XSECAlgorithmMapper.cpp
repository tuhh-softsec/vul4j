/*
 * Copyright 2003-2005 The Apache Software Foundation.
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
 * limitations under the License.
 */

/*
 * XSEC
 *
 * XSECAlgorithmMapper := Provides a table of AlgorithmHandlers
 *						  Mapped by Type URI
 *
 * $Id$
 *
 */

// XSEC Includes

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/framework/XSECAlgorithmMapper.hpp>
#include <xsec/framework/XSECError.hpp>
#include <xsec/utils/XSECDOMUtils.hpp>

#include "XSECAlgorithmMapper.hpp"
#include "XSECAlgorithmHandler.hpp"

// Xerces

#include <xercesc/util/XMLString.hpp>
#include <xercesc/util/XMLUniDefs.hpp>

XERCES_CPP_NAMESPACE_USE

// Default strings

const XMLCh XSECAlgorithmMapper::s_defaultEncryptionMapping [] = {
	chLatin_D,
	chLatin_e,
	chLatin_f,
	chLatin_a,
	chLatin_u,
	chLatin_l,
	chLatin_t,
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



XSECAlgorithmMapper::XSECAlgorithmMapper(void) {

}


XSECAlgorithmMapper::~XSECAlgorithmMapper() {

	MapperEntryVectorType::iterator it = m_mapping.begin();

	while (it != m_mapping.end()) {
		
		XSEC_RELEASE_XMLCH(((*it)->mp_uri));
		delete (*it)->mp_handler;
		delete (*it);

		it++;

	}

	m_mapping.clear();

}

XSECAlgorithmMapper::MapperEntry * XSECAlgorithmMapper::findEntry(const XMLCh * URI) const {

	MapperEntryVectorType::iterator it = m_mapping.begin();

	while (it != m_mapping.end()) {
		
		if (strEquals((*it)->mp_uri, URI)) {
			return (*it);
		}

		it++;

	}

	return NULL;

}


XSECAlgorithmHandler * XSECAlgorithmMapper::mapURIToHandler(const XMLCh * URI) const {


	MapperEntry * entry = findEntry(URI);

	if (entry == NULL) {
		throw XSECException(XSECException::AlgorithmMapperError,
			"XSECAlgorithmMapper::mapURIToHandler - URI not found");
	}

	return entry->mp_handler;
}

void XSECAlgorithmMapper::registerHandler(const XMLCh * URI, const XSECAlgorithmHandler & handler) {

	MapperEntry * entry = findEntry(URI);

	if (entry != NULL) {

		delete entry->mp_handler;

	}
	else {
		XSECnew(entry, MapperEntry);

		entry->mp_uri = XMLString::replicate(URI);
		m_mapping.push_back(entry);

	}
	entry->mp_handler = handler.clone();

}

