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
#include <xsec/framework/XSECEnv.hpp>

#include <xercesc/util/XMLUniDefs.hpp>

XERCES_CPP_NAMESPACE_USE

// --------------------------------------------------------------------------------
//			String Constants
// --------------------------------------------------------------------------------

static XMLCh s_CipherData[] = {

	chLatin_C,
	chLatin_i,
	chLatin_p,
	chLatin_h,
	chLatin_e,
	chLatin_r,
	chLatin_D,
	chLatin_a,
	chLatin_t,
	chLatin_a,
	chNull,
};

static XMLCh s_CipherValue[] = {

	chLatin_C,
	chLatin_i,
	chLatin_p,
	chLatin_h,
	chLatin_e,
	chLatin_r,
	chLatin_V,
	chLatin_a,
	chLatin_l,
	chLatin_u,
	chLatin_e,
	chNull,
};

static XMLCh s_CipherReference[] = {

	chLatin_C,
	chLatin_i,
	chLatin_p,
	chLatin_h,
	chLatin_e,
	chLatin_r,
	chLatin_R,
	chLatin_e,
	chLatin_f,
	chLatin_e,
	chLatin_r,
	chLatin_e,
	chLatin_n,
	chLatin_c,
	chLatin_e,
	chNull,
};

// --------------------------------------------------------------------------------
//			Constructors and Destructors
// --------------------------------------------------------------------------------

XENCCipherDataImpl::XENCCipherDataImpl(const XSECEnv * env) :
mp_env(env),
mp_cipherDataNode(NULL),
mp_cipherValue(NULL) {

}


XENCCipherDataImpl::XENCCipherDataImpl(const XSECEnv * env, DOMNode * node) :
mp_env(env),
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
		XSECnew(mp_cipherValue, XENCCipherValueImpl(mp_env, tmpElt));
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
	DOMDocument *doc = mp_env->getParentDocument();
	const XMLCh * prefix = mp_env->getXENCNSPrefix();

	makeQName(str, prefix, s_CipherData);

	DOMElement *ret = doc->createElementNS(DSIGConstants::s_unicodeStrURIXENC, str.rawXMLChBuffer());
	mp_cipherDataNode = ret;

	// Set the type
	if (type == VALUE_TYPE) {
		
		// Should set the type attribute

		// Create the Cipher Value
		XSECnew(mp_cipherValue, XENCCipherValueImpl(mp_env));
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


