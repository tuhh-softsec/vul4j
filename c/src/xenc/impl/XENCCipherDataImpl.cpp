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
 * XENCCipherDataImpl := Implementation of CipherData elements 
 *
 * $Id$
 *
 */

#include <xsec/framework/XSECDefs.hpp>

#include "XENCCipherDataImpl.hpp"
#include "XENCCipherValueImpl.hpp"

#include <xsec/framework/XSECError.hpp>
#include <xsec/utils/XSECDOMUtils.hpp>

#include <xercesc/util/XMLUniDefs.hpp>

// --------------------------------------------------------------------------------
//			String Constants
// --------------------------------------------------------------------------------

static XMLCh s_CipherData[] = {

	XERCES_CPP_NAMESPACE :: chLatin_C,
	XERCES_CPP_NAMESPACE :: chLatin_i,
	XERCES_CPP_NAMESPACE :: chLatin_p,
	XERCES_CPP_NAMESPACE :: chLatin_h,
	XERCES_CPP_NAMESPACE :: chLatin_e,
	XERCES_CPP_NAMESPACE :: chLatin_r,
	XERCES_CPP_NAMESPACE :: chLatin_D,
	XERCES_CPP_NAMESPACE :: chLatin_a,
	XERCES_CPP_NAMESPACE :: chLatin_t,
	XERCES_CPP_NAMESPACE :: chLatin_a,
	XERCES_CPP_NAMESPACE :: chNull,
};

static XMLCh s_CipherValue[] = {

	XERCES_CPP_NAMESPACE :: chLatin_C,
	XERCES_CPP_NAMESPACE :: chLatin_i,
	XERCES_CPP_NAMESPACE :: chLatin_p,
	XERCES_CPP_NAMESPACE :: chLatin_h,
	XERCES_CPP_NAMESPACE :: chLatin_e,
	XERCES_CPP_NAMESPACE :: chLatin_r,
	XERCES_CPP_NAMESPACE :: chLatin_V,
	XERCES_CPP_NAMESPACE :: chLatin_a,
	XERCES_CPP_NAMESPACE :: chLatin_l,
	XERCES_CPP_NAMESPACE :: chLatin_u,
	XERCES_CPP_NAMESPACE :: chLatin_e,
	XERCES_CPP_NAMESPACE :: chNull,
};

static XMLCh s_CipherReference[] = {

	XERCES_CPP_NAMESPACE :: chLatin_C,
	XERCES_CPP_NAMESPACE :: chLatin_i,
	XERCES_CPP_NAMESPACE :: chLatin_p,
	XERCES_CPP_NAMESPACE :: chLatin_h,
	XERCES_CPP_NAMESPACE :: chLatin_e,
	XERCES_CPP_NAMESPACE :: chLatin_r,
	XERCES_CPP_NAMESPACE :: chLatin_R,
	XERCES_CPP_NAMESPACE :: chLatin_e,
	XERCES_CPP_NAMESPACE :: chLatin_f,
	XERCES_CPP_NAMESPACE :: chLatin_e,
	XERCES_CPP_NAMESPACE :: chLatin_r,
	XERCES_CPP_NAMESPACE :: chLatin_e,
	XERCES_CPP_NAMESPACE :: chLatin_n,
	XERCES_CPP_NAMESPACE :: chLatin_c,
	XERCES_CPP_NAMESPACE :: chLatin_e,
	XERCES_CPP_NAMESPACE :: chNull,
};

// --------------------------------------------------------------------------------
//			Constructors and Destructors
// --------------------------------------------------------------------------------

XENCCipherDataImpl::XENCCipherDataImpl(XENCCipherImpl * cipher) :
mp_cipher(cipher),
mp_cipherDataNode(NULL),
mp_cipherValue(NULL) {

}


XENCCipherDataImpl::XENCCipherDataImpl(XENCCipherImpl * cipher, DOMNode * node) :
mp_cipher(cipher),
mp_cipherDataNode(node),
mp_cipherValue(NULL) {

}

XENCCipherDataImpl::~XENCCipherDataImpl() {

	if (mp_cipherValue != NULL)
		delete mp_cipherValue;
}

// --------------------------------------------------------------------------------
//			Load DOM
// --------------------------------------------------------------------------------

void XENCCipherDataImpl::load() {

	if (mp_cipherDataNode == NULL) {

		// Attempt to load an empty encryptedType element
		throw XSECException(XSECException::CipherDataError,
			"XENCCipherData::load - called on empty DOM");

	}

	if (!strEquals(getXENCLocalName(mp_cipherDataNode), s_CipherData)) {
	
		throw XSECException(XSECException::CipherDataError,
			"XENCCipherData::load - called incorrect node");
	
	}

	// Find out whether this is a CipherValue or CipherReference and load
	// appropriately

	DOMNode *tmpElt = findFirstChildOfType(mp_cipherDataNode, DOMNode::ELEMENT_NODE);

	if (tmpElt != NULL && strEquals(getXENCLocalName(tmpElt), s_CipherValue)) {

		m_cipherDataType = VALUE_TYPE;
		XSECnew(mp_cipherValue, XENCCipherValueImpl(mp_cipher, tmpElt));
		mp_cipherValue->load();

	}

	else if (tmpElt != NULL && strEquals(getXENCLocalName(tmpElt), s_CipherReference)) {

		m_cipherDataType = NO_TYPE;

	}

	else {

		throw XSECException(XSECException::ExpectedXENCChildNotFound,
			"XENCCipherData::load - expected <CipherValue> or <CipherReference>");

	}

}

// --------------------------------------------------------------------------------
//			Create a blank structure
// --------------------------------------------------------------------------------

DOMElement * XENCCipherDataImpl::createBlankCipherData(
						XENCCipherData::XENCCipherDataType type, 
						const XMLCh * value) {

	// Reset
	if (mp_cipherValue != NULL) {
		delete mp_cipherValue;
		mp_cipherValue = NULL;
	}

	m_cipherDataType = NO_TYPE;

	// Get some setup values
	safeBuffer str;
	DOMDocument *doc = mp_cipher->getDocument();
	const XMLCh * prefix = mp_cipher->getXENCNSPrefix();

	makeQName(str, prefix, s_CipherData);

	DOMElement *ret = doc->createElementNS(DSIGConstants::s_unicodeStrURIXENC, str.rawXMLChBuffer());
	mp_cipherDataNode = ret;

	// Set the type
	if (type == VALUE_TYPE) {
		
		// Should set the type attribute

		// Create the Cipher Value
		XSECnew(mp_cipherValue, XENCCipherValueImpl(mp_cipher));
		DOMNode * cipherValueNode = mp_cipherValue->createBlankCipherValue(value);

		ret->appendChild(cipherValueNode);

	}

	return ret;

}

// --------------------------------------------------------------------------------
//			Constructors and Destructors
// --------------------------------------------------------------------------------

	// Interface methods
XENCCipherDataImpl::XENCCipherDataType XENCCipherDataImpl::getCipherDataType(void) {

	return m_cipherDataType;

}

XENCCipherValue * XENCCipherDataImpl::getCipherValue(void) {

	return mp_cipherValue;

}


