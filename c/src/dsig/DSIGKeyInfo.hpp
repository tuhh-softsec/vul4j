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
 * DSIGKeyInfo := Base (virtual) class that defines an XSEC KeyInfo node
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */

#ifndef DSIGKEYINFO_INCLUDE
#define DSIGKEYINFO_INCLUDE

// XSEC Includes

#include <xsec/utils/XSECDOMUtils.hpp>
#include <xsec/utils/XSECSafeBufferFormatter.hpp>
#include <xsec/enc/XSECCryptoKey.hpp>

#include <xercesc/dom/DOM.hpp>

class DSIGSignature;
class XSECEnv;

/**
 * @ingroup pubsig
 * @{
 */

/**
 * @brief Base class for <Key*> nodes in a KeyInfo list.
 *
 * Digital signatures can have a number of KeyInfo elements that are
 * used to communicate information about what key to use between the
 * signer and the validator.
 *
 * In the XML-Security-C libary, KeyInfo elements are only used for
 * holding information about keys.  They do not in themselves perform
 * any cryptographic function.
 *
 */


class DSIG_EXPORT DSIGKeyInfo {

public:

	/** 
	 * \brief List of potential KeyInfo types
	 *
	 * The keyIntoType enumerated type defines the KeyInfo types known by
	 * the XML-Security-C library.
	 *
	 */
	 

	enum keyInfoType {

		KEYINFO_NOTSET			= 1,			// Empty key type
		KEYINFO_X509			= 2,			// X509 Certificate (with embedded key)
		KEYINFO_VALUE_DSA		= 3,			// DSA Key
		KEYINFO_VALUE_RSA		= 4,
		KEYINFO_NAME			= 5,			// A name of a key (application dependant)
		KEYINFO_PGPDATA			= 6,			// A PGP key
		KEYINFO_SPKIDATA		= 7,
		KEYINFO_MGMTDATA		= 8,			// Management data
		KEYINFO_ENCRYPTEDKEY	= 9				// XML Encryption - Encrypted Key

	};

public:

	/** @name Constructors and Destructors */
	//@{

	/**
	 * \brief Construct from an owning signature
	 *
	 * All KeyInfo types take a constructor that provides the controlling environment.
	 *
	 * @param env The environment that the KeyInfo is operating within
	 */

	DSIGKeyInfo(const XSECEnv * env) {mp_keyInfoDOMNode = NULL; mp_env = env;}

	/**
	 * \brief The Destructor
	 */

	virtual ~DSIGKeyInfo() {};

	//@}

	/** @name Get functions */
	//@{

	/**
	 * \brief Return type
	 *
	 * Can be used to find what type of KeyInfo this is
	 */

	virtual keyInfoType getKeyInfoType(void) = 0;

	/**
	 * \brief Return the DOMNode that heads up this DOMNode
	 */

	virtual XERCES_CPP_NAMESPACE_QUALIFIER DOMNode *getKeyInfoDOMNode() 
		{return mp_keyInfoDOMNode;}

	/**
	 * \brief Return the name of this key
	 *
	 * For those KeyInfo types that have a keyname, this function should return
	 * it.  For certificates, this may be the DN.
	 *
	 * @returns A pointer to a buffer containing the name
	 */

	virtual const XMLCh * getKeyName(void) = 0;

	//@}

	/** @name Load and Set */
	//@{

	/**
	 * \brief Load the DOM structures.
	 *
	 * Used by the library to instruct the object to load information from
	 * the DOM nodes
	 */

	virtual void load() = 0;

	//@}

protected:

	XERCES_CPP_NAMESPACE_QUALIFIER DOMNode			* mp_keyInfoDOMNode;
	const XSECEnv									* mp_env;

private:
	DSIGKeyInfo();

};




#endif /* #define XSECKEYINFO_INCLUDE */
