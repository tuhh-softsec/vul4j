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

XERCES_CPP_NAMESPACE_USE

// --------------------------------------------------------------------------------
//			UNICODE Strings
// --------------------------------------------------------------------------------

static XMLCh s_EncryptedData[] = {

	chLatin_E,
	chLatin_n,
	chLatin_c,
	chLatin_r,
	chLatin_y,
	chLatin_p,
	chLatin_t,
	chLatin_e,
	chLatin_d,
	chLatin_D,
	chLatin_a,
	chLatin_t,
	chLatin_a,
	chNull,
};

// --------------------------------------------------------------------------------
//			Construct/Destruct
// --------------------------------------------------------------------------------


XENCEncryptedDataImpl::XENCEncryptedDataImpl(const XSECEnv * env) :
XENCEncryptedTypeImpl(env) {
	
}

XENCEncryptedDataImpl::XENCEncryptedDataImpl(const XSECEnv * env, DOMElement * node) :
XENCEncryptedTypeImpl(env, node) {

}

XENCEncryptedDataImpl::~XENCEncryptedDataImpl() {

}

// --------------------------------------------------------------------------------
//			Load
// --------------------------------------------------------------------------------

void XENCEncryptedDataImpl::load(void) {

	if (mp_encryptedTypeElement == NULL) {

		// Attempt to load an empty encryptedData element
		throw XSECException(XSECException::EncryptedTypeError,
			"XENCEncryptedData::load - called on empty DOM");

	}

	if (!strEquals(getXENCLocalName(mp_encryptedTypeElement), s_EncryptedData)) {

		// Attempt to load an empty encryptedData element
		throw XSECException(XSECException::EncryptedTypeError,
			"XENCEncryptedData::load - called on non EncryptedData node");

	}

	// Now call the virtual function we overloaded to get here.
	XENCEncryptedTypeImpl::load();

}
// --------------------------------------------------------------------------------
//			Create from scratch
// --------------------------------------------------------------------------------

DOMElement * XENCEncryptedDataImpl::createBlankEncryptedData(
									XENCCipherData::XENCCipherDataType type, 
									const XMLCh * algorithm,
									const XMLCh * value) {

	return createBlankEncryptedType(s_EncryptedData, type, algorithm, value);

}

// --------------------------------------------------------------------------------
//			Interface Methods
// --------------------------------------------------------------------------------


