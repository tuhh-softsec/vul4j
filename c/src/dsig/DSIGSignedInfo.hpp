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
 * DSIGSignature := Class for checking and setting up signature nodes in a DSIG signature
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */

#ifndef DSIGSIGNEDINFO_INCLUDE
#define DSIGSIGNEDINFO_INCLUDE

// XSEC Includes
#include <xsec/framework/XSECDefs.hpp>
#include <xsec/utils/XSECSafeBufferFormatter.hpp>
#include <xsec/dsig/DSIGConstants.hpp>
#include <xsec/dsig/DSIGReferenceList.hpp>

// Xerces Includes

XSEC_DECLARE_XERCES_CLASS(DOMDocument);
XSEC_DECLARE_XERCES_CLASS(DOMNode);
XSEC_DECLARE_XERCES_CLASS(DOMElement);

// General includes

#include <vector>

class DSIGSignature;

/**
 * @ingroup pubsig
 * @{
 */

/**
 * @brief Constructs and holds a SignedInfo.
 *
 * The <SignedInfo> node is the container for all the information
 * that is signed.  It contains the ReferenceList and information
 * on the signature and canonicalisation method for the signature.
 *
 * Generally this class should not be manipulated directly.
 *
 */

class DSIGSignedInfo {

public:

	/** @name Constructors and Destructors */
	//@{

	/**
	 * \brief Constructor for existing nodes
	 *
	 * Called by the library to construct a SignedInfo in cases
	 * where the DOM Nodes exist and need to be loaded
	 *
	 * @param doc The document containing the structure to be loaded
	 * @param pFormatter A safeBuffer formatter that will translate to UTF-8
	 * @param signedInfoNode The node at the top of the SignedInfo tree fragment
	 * @param parentSignature The signature that owns me
	 */

	DSIGSignedInfo(DOMDocument *doc, 
		XSECSafeBufferFormatter * pFormatter, 
		DOMNode *signedInfoNode,
		DSIGSignature * parentSignature);


	/**
	 * \brief Constructor for building from scratch
	 *
	 * Will set up the class in preparation for building the 
	 * DOM structure 
	 *
	 * @param doc The document to use to construct
	 * @param pFormatter Formatter to use to translate to UTF-8
	 * @param parentSignature The owning Signature
	 */

	DSIGSignedInfo(DOMDocument *doc,
				XSECSafeBufferFormatter * pFormatter, 
				DSIGSignature * parentSignature);

	/**
	 * \brief Destructur
	 * 
	 * Delete - but does not destroy the DOM Nodes
	 *
	 */

	~DSIGSignedInfo();

	//@}

	/** @name Create and Set */
	//@{

	/**
	 * \brief Load from DOM
	 *
	 * Load the SignedInfo from the DOM Document
	 *
	 * Does not do any verification of signatures or references - 
	 * simply loads the values
	 */

	void load(void);

	/**
	 * \brief Verify the SignedInfo
	 *
	 * Validates each reference contained in the SignedInfo.  Does not
	 * validate the signature itself - this is done by DSIGSignature
	 *
	 * @param errStr The safeBuffer that error messages should be written to.
	 */

	bool verify(safeBuffer &errStr);

	/**
	 * \brief Hash the reference list
	 *
	 * Goes through each reference in the SignedInfo (including referenced
	 * manifests), performs the digest operation and adds the digest
	 * to the reference element
	 */

	void hash(void);				// Setup hashes for each Reference element

	/**
	 * \brief Create an empty SignedInfo
	 *
	 * Creates the DOM structure for a SignedInfo
	 *
	 * Builds the DOM structures and sets the control
	 * structures of the SignedInfo
	 *
	 * @param cm The canonicalisation method to set the SignedInfo as
	 * @param sm Signature Method to use
	 * @param hm Hash method to use (for the SignedInfo, not the references)
	 */

	DOMElement *createBlankSignedInfo(canonicalizationMethod cm,
			signatureMethod	sm,
			hashMethod hm);

	/**
	 * \brief Create a reference to add to the SignedInfo
	 *
	 * Called by DSIGSignature to create and enter a new reference element
	 *
	 * @param URI What the reference references
	 * @param hm Digest method to use for the reference
	 * @type Reference type
	 */

	DSIGReference * createReference(const XMLCh * URI,
		hashMethod hm, char * type);

	//@}

	/** @name Getter functions */
	//@{

	/**
	 * \brief Get the node pointing to the top of the DOM fragment
	 *
	 * @returns the SignedInfo node
	 */

	DOMNode *getDOMNode(void);

	/**
	 * \brief Get the canonicalisation method 
	 * 
	 * @returns Canonicalisation method
	 */

	canonicalizationMethod getCanonicalizationMethod(void);

	/**
	 * \brief Get the hash method
	 *
	 * @returns the Hash (digest) Method
	 */

	hashMethod getHashMethod(void);

	/**
	 * \brief Get the signature method
	 *
	 * @returns the Signature method
	 */

	signatureMethod getSignatureMethod(void);

	/**
	 * \brief Get HMAC length
	 * 
	 * HMAC signatures can be truncated to a nominated length.
	 * This returns the length used.
	 */

	int getHMACOutputLength(void);

	/**
	 * \brief Return the list of references
	 *
	 * Returns a pointer to the object holding the references
	 * contained in the SignedInfo
	 */

	DSIGReferenceList *getReferenceList (void) {return mp_referenceList;}

	//@}


private:

	XSECSafeBufferFormatter		* mp_formatter;
	bool						m_loaded;				// Have we already loaded?
	DOMDocument					* mp_doc;
	DOMNode						* mp_signedInfoNode;
	canonicalizationMethod		m_canonicalizationMethod;
	signatureMethod				m_signatureMethod;
	hashMethod					m_hashMethod;
	DSIGReferenceList			* mp_referenceList;
	int							m_HMACOutputLength;
	DSIGSignature				* mp_parentSignature;

	// Not implemented constructors

	DSIGSignedInfo();
	// DSIGSignedInfo & operator= (const DSIGSignedInfo &);

};

/** @} */

#endif /* DSIGSIGNEDINFO_INCLUDE */
