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

