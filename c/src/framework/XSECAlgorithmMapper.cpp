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
		
		delete[] (*it)->mp_uri;
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

