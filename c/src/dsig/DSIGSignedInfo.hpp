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

#include <xercesc/dom/DOM.hpp>

// General includes

#include <vector>

class DSIGSignature;

class DSIGSignedInfo {

public:

	// Constructors and Destructors

	DSIGSignedInfo(DOMDocument *doc, 
		XSECSafeBufferFormatter * pFormatter, 
		DOMNode *signedInfoNode,
		DSIGSignature * parentSignature);

	// For a blank signature

	DSIGSignedInfo(DOMDocument *doc,
				XSECSafeBufferFormatter * pFormatter, 
				DSIGSignature * parentSignature);

	~DSIGSignedInfo();

	// Actions

	void load(void);				// Load the signed info from the DOM source
	bool verify(safeBuffer &errStr);
	void hash(void);				// Setup hashes for each Reference element

	// Get information

	DOMNode *getDOMNode(void);
	canonicalizationMethod getCanonicalizationMethod(void);
	hashMethod getHashMethod(void);
	signatureMethod getSignatureMethod(void);
	int getHMACOutputLength(void);
	DSIGReferenceList *getReferenceList (void) {return mp_referenceList;}

	// Creation
	DOMElement *createBlankSignedInfo(canonicalizationMethod cm,
			signatureMethod	sm,
			hashMethod hm);
	DSIGReference * createReference(const XMLCh * URI,
		hashMethod hm, char * type);

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


#endif /* DSIGSIGNEDINFO_INCLUDE */