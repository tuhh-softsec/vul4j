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

#ifndef XSECALGMAPPER_INCLUDE
#define XSECALGMAPPER_INCLUDE

// XSEC Includes

#include <xsec/framework/XSECDefs.hpp>

#include <vector>

class XSECAlgorithmHandler;

/**
 * @ingroup internal
 *\@{*/



/**
 * @brief Holder class for mapping Algorithms to Handlers
 *
 */

class XSECAlgorithmMapper {

public:
	
	/** @name Constructors and Destructors */
	//@{
	
	XSECAlgorithmMapper(void);
	~XSECAlgorithmMapper();

	//@}

	/** @name Default mapping strings */
	//@{

	static const XMLCh s_defaultEncryptionMapping[];

	/** @name Map Methods */
	//@{

	/**
	 * \brief Map a URI to a handler
	 */

	XSECAlgorithmHandler * mapURIToHandler(const XMLCh * URI) const;

	//@}

	/** @name Registration Methods */
	//@{

	/**
	 * \brief Register a new handler
	 */

	void registerHandler(const XMLCh * URI, const XSECAlgorithmHandler & handler);

	//@}

private:

	struct MapperEntry {

		XMLCh * mp_uri;
		XSECAlgorithmHandler * mp_handler;

	};

	MapperEntry * findEntry(const XMLCh * URI) const;

#if defined(XSEC_NO_NAMESPACES)
	typedef vector<MapperEntry *>			MapperEntryVectorType;
#else
	typedef std::vector<MapperEntry *>		MapperEntryVectorType;
#endif

	mutable MapperEntryVectorType		m_mapping;

};

/*\@}*/

#endif /* XSECALGMAPPER_INCLUDE */

