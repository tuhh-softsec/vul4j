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
 * DSIGKeyInfoList := Class for Loading and storing a list of KeyInfo elements
 *					 
 * $Id$
 *
 */

#ifndef DSIGKEYINFOLIST_INCLUDE
#define DSIGKEYINFOLIST_INCLUDE


// XSEC Includes
#include <xsec/framework/XSECDefs.hpp>
#include <xsec/dsig/DSIGKeyInfo.hpp>
#include <xsec/utils/XSECSafeBufferFormatter.hpp>
#include <xsec/enc/XSECCryptoKey.hpp>

// Xerces
#include <xercesc/dom/DOM.hpp>

// General includes
#include <vector>

class DSIGSignature;

/**
 * @ingroup pubsig
 * @{
 */

/**
 * \brief Container class for KeyInfo elements.
 *
 * The library stores KeyInfo lists using this class.
 */

class DSIG_EXPORT DSIGKeyInfoList {

public:

#if defined(XSEC_NO_NAMESPACES)
	typedef vector<DSIGKeyInfo *>			KeyInfoListVectorType;
#else
	typedef std::vector<DSIGKeyInfo *>		KeyInfoListVectorType;
#endif

#if defined(XSEC_SIZE_T_IN_NAMESPACE_STD)
	typedef std::size_t		size_type;
#else
	typedef size_t			size_type;
#endif

	/** @name Constructors and Destructors */
	//@{

	/**
	 * \brief Main constructor
	 *
	 * Main constructor called by DSIGSignature
	 *
	 * @note Should only ever be created by a Signature or Cipher class.
	 *
	 * @param env The environment the KeyInfo is operating within
	 */

	DSIGKeyInfoList(const XSECEnv * env);

	/**
	 * \brief Destructor
	 */

	~DSIGKeyInfoList();

	//@}

	/** @name Public (API) functions */
	//@{

	/**
	 * \brief Get size of list
	 *
	 * @returns the number of elements in the list
	 */

	size_t getSize();

	/*
	 * \brief Get an item
	 *
	 * Returns the item at index point in the list
	 *
	 * @note This is an internal function and should not be called directly
	 *
	 * @param index Pointer into the list
	 * @returns The indicated element or 0 if it does not exist.
	 */

	DSIGKeyInfo * item(size_type index);

	//@}

	/** @name Manipulate the List */
	//@{

	/**
	 * \brief Add an already created KeyInfo 
	 *
	 * Adds a KeyInfo element that has already been built
	 * into the list.
	 *
	 * @param ref The KeyInfo to add
	 */

	void addKeyInfo(DSIGKeyInfo * ref);

	/**
	 * \brief Read from DOM and create.
	 *
	 * Uses a DOMNode pointing to the start of the KeyInfo element
	 * to build a new KeyInfo and then add it to the list
	 *
	 * @note This is an internal function and should not be called directly
	 *
	 * @param ki Head of DOM structure with the KeyInfo
	 * @returns true if successfully loaded
	 */

	bool addXMLKeyInfo(XERCES_CPP_NAMESPACE_QUALIFIER DOMNode *ki);

	/**
	 * \brief Read full list from DOM
	 *
	 * Will take the starting node of a KeyInfo list and read into the
	 * list structure.  This is a bit different from other "load"
	 * functions, in that it takes a node as a parameter.
	 *
	 * @note This is an internal functions and should not be called directly
	 *
	 * @param node The <KeyInfo> element node to read from
	 */

	bool loadListFromXML(XERCES_CPP_NAMESPACE_QUALIFIER DOMNode * node);

	/*
	 * \brief Remove a KeyInfo from the list
	 *
	 * Removes the KeyInfo element at the indicated index point in the list
	 *
	 * @param index Pointer in the list to remove element from
	 * @returns The removed KeyInfo element
	 */

	DSIGKeyInfo * removeKeyInfo(size_type index);

	/**
	 * \brief Set the overarching environment
	 *
	 * Sets the environment this list is operating within
	 *
	 * @param env Operating environment
	 */

	void setEnvironment(const XSECEnv * env) {mp_env = env;}

	/**
	 * \brief Clear out the list
	 *
	 * Removes all elements from the list
	 *
	 * @note Deletes the items themselves as well as clearing the list.
	 */

	void empty();
	
	/**
	 * \brief Is the list empty?
	 *
	 * @returns true Iff there are no elements in the list
	 */

	bool isEmpty();

	//@}

private:

	DSIGKeyInfoList();

	KeyInfoListVectorType					m_keyInfoList;
	const XSECEnv							* mp_env;
	// KeyInfoListVectorType::iterator			m_iterator;
};


#endif /* DSIGKEYINFOLIST_INCLUDE */
