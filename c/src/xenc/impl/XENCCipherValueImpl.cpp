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
 * XENCCipherValueImpl := Implementation for CipherValue elements
 *
 * $Id$
 *
 */

// XSEC Includes

#include <xsec/framework/XSECDefs.hpp>

#include "XENCCipherValueImpl.hpp"

#include <xsec/framework/XSECError.hpp>
#include <xsec/utils/XSECDOMUtils.hpp>

#include <xercesc/util/XMLUniDefs.hpp>

// --------------------------------------------------------------------------------
//			String Constants
// --------------------------------------------------------------------------------

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

// --------------------------------------------------------------------------------
//			Constructors/Destructors
// --------------------------------------------------------------------------------

XENCCipherValueImpl::XENCCipherValueImpl(XENCCipherImpl * cipher) :
mp_cipher(cipher),
mp_cipherValueNode(NULL),
mp_cipherString(NULL) {

}

XENCCipherValueImpl::XENCCipherValueImpl(XENCCipherImpl * cipher, DOMNode * node) :
mp_cipher(cipher),
mp_cipherValueNode(node),
mp_cipherString(NULL) {

}


XENCCipherValueImpl::~XENCCipherValueImpl() {

	if (mp_cipherString != NULL)
		delete[] mp_cipherString;

}

// --------------------------------------------------------------------------------
//			Load
// --------------------------------------------------------------------------------

void XENCCipherValueImpl::load(void) {

	if (mp_cipherValueNode == NULL) {

		// Attempt to load an empty encryptedType element
		throw XSECException(XSECException::CipherValueError,
			"XENCCipherData::load - called on empty DOM");

	}

	if (!strEquals(getXENCLocalName(mp_cipherValueNode), s_CipherValue)) {
	
		throw XSECException(XSECException::CipherValueError,
			"XENCCipherData::load - called incorrect node");
	
	}

	// JUst gather the text children and continue
	safeBuffer txt;

	gatherChildrenText(mp_cipherValueNode, txt);

	// Get a copy
	mp_cipherString = XMLString::replicate(txt.rawXMLChBuffer());

}

// --------------------------------------------------------------------------------
//			Create a blank structure
// --------------------------------------------------------------------------------

DOMElement * XENCCipherValueImpl::createBlankCipherValue(
						const XMLCh * value) {

	// Rest
	if (mp_cipherString != NULL) {
		delete[] mp_cipherString;
		mp_cipherString = NULL;
	}

	// Get some setup values
	safeBuffer str;
	DOMDocument *doc = mp_cipher->getDocument();
	const XMLCh * prefix = mp_cipher->getXENCNSPrefix();

	makeQName(str, prefix, s_CipherValue);

	DOMElement *ret = doc->createElementNS(DSIGConstants::s_unicodeStrURIXENC, str.rawXMLChBuffer());
	mp_cipherValueNode = ret;

	// Append the value
	ret->appendChild(doc->createTextNode(value));
	
	mp_cipherString = XMLString::replicate(value);;

	return ret;

}

// --------------------------------------------------------------------------------
//			Interface Methods
// --------------------------------------------------------------------------------

const XMLCh * XENCCipherValueImpl::getCipherString(void) {

	return mp_cipherString;

}

