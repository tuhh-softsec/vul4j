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
 * XENCEncryptedDataImpl := Implementation for holder object for EncryptedData 
 *
 * $Id$
 *
 */

#include <xsec/framework/XSECDefs.hpp>

#include "XENCCipherImpl.hpp"
#include "XENCEncryptedDataImpl.hpp"
#include "XENCCipherDataImpl.hpp"

#include <xsec/framework/XSECError.hpp>
#include <xsec/utils/XSECDOMUtils.hpp>

#include <xercesc/util/XMLUniDefs.hpp>

// --------------------------------------------------------------------------------
//			UNICODE Strings
// --------------------------------------------------------------------------------

static XMLCh s_EncryptedData[] = {

	XERCES_CPP_NAMESPACE :: chLatin_E,
	XERCES_CPP_NAMESPACE :: chLatin_n,
	XERCES_CPP_NAMESPACE :: chLatin_c,
	XERCES_CPP_NAMESPACE :: chLatin_r,
	XERCES_CPP_NAMESPACE :: chLatin_y,
	XERCES_CPP_NAMESPACE :: chLatin_p,
	XERCES_CPP_NAMESPACE :: chLatin_t,
	XERCES_CPP_NAMESPACE :: chLatin_e,
	XERCES_CPP_NAMESPACE :: chLatin_d,
	XERCES_CPP_NAMESPACE :: chLatin_D,
	XERCES_CPP_NAMESPACE :: chLatin_a,
	XERCES_CPP_NAMESPACE :: chLatin_t,
	XERCES_CPP_NAMESPACE :: chLatin_a,
	XERCES_CPP_NAMESPACE :: chNull,
};

// --------------------------------------------------------------------------------
//			Construct/Destruct
// --------------------------------------------------------------------------------


XENCEncryptedDataImpl::XENCEncryptedDataImpl(XENCCipherImpl * cipher) :
XENCEncryptedTypeImpl(cipher) {
	
}

XENCEncryptedDataImpl::XENCEncryptedDataImpl(XENCCipherImpl * cipher, DOMNode * node) :
XENCEncryptedTypeImpl(cipher, node) {

}

XENCEncryptedDataImpl::~XENCEncryptedDataImpl() {

}

// --------------------------------------------------------------------------------
//			Load
// --------------------------------------------------------------------------------

void XENCEncryptedDataImpl::load(void) {

	if (mp_encryptedTypeNode == NULL) {

		// Attempt to load an empty encryptedData element
		throw XSECException(XSECException::EncryptedTypeError,
			"XENCEncryptedData::load - called on empty DOM");

	}

	if (!strEquals(getXENCLocalName(mp_encryptedTypeNode), s_EncryptedData)) {

		// Attempt to load an empty encryptedData element
		throw XSECException(XSECException::EncryptedTypeError,
			"XENCEncryptedData::load - called on non EncryptedData node");

	}

	// Now call the virtual function we overloaded to get here.
	XENCEncryptedTypeImpl::load();

}

// --------------------------------------------------------------------------------
//			Interface Methods
// --------------------------------------------------------------------------------

XENCCipherData * XENCEncryptedDataImpl::getCipherData(void) {

	return mp_cipherData;

}

