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
 * InteropResolver := Class to resolve key elements into certificates for
 *						merlin-18 interop test
 *
 * $Id$
 *
 */

// XSEC

#include "MerlinFiveInteropResolver.hpp"

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/enc/XSECKeyInfoResolver.hpp>
#include <xsec/dsig/DSIGKeyInfoName.hpp>
#include <xsec/utils/XSECDOMUtils.hpp>
#include <xsec/enc/OpenSSL/OpenSSLCryptoSymmetricKey.hpp>

#include <xercesc/util/Janitor.hpp>
#include <xercesc/util/XMLUniDefs.hpp>

XERCES_CPP_NAMESPACE_USE

#include <iostream>

#if defined (HAVE_OPENSSL) 

// --------------------------------------------------------------------------------
//           Strings and keys
// --------------------------------------------------------------------------------

static XMLCh s_bobName[] = {
	chLatin_b,
	chLatin_o,
	chLatin_b,
	chNull
};

static XMLCh s_jobName[] = {
	chLatin_j,
	chLatin_o,
	chLatin_b,
	chNull
};

static char s_bobKey[] = "abcdefghijklmnopqrstuvwx";
static char s_jobKey[] = "abcdefghijklmnop";


// --------------------------------------------------------------------------------
//           Construct/Destruct
// --------------------------------------------------------------------------------


MerlinFiveInteropResolver::MerlinFiveInteropResolver(const XMLCh * baseURI) {

	if (baseURI != NULL)
		mp_baseURI = XMLString::replicate(baseURI);
	else
		mp_baseURI = NULL;

#if !defined(_WIN32)
	m_fcount = 0;
#endif

}


MerlinFiveInteropResolver::~MerlinFiveInteropResolver() {

	if (mp_baseURI != NULL)
		delete[]mp_baseURI;

}


XSECCryptoKey * MerlinFiveInteropResolver::resolveKey(DSIGKeyInfoList * lst) {

	int lstSize = lst->getSize();

	for (int i = 0; i < lstSize; ++i) {

		DSIGKeyInfo * ki = lst->item(i);

		if (ki->getKeyInfoType() == DSIGKeyInfo::KEYINFO_NAME) {

			DSIGKeyInfoName * kn = dynamic_cast<DSIGKeyInfoName *>(ki);

			const XMLCh * name = kn->getKeyName();

			// Check if this is a key we know

			if (strEquals(s_bobName, name)) {
				OpenSSLCryptoSymmetricKey * k;
				k = new OpenSSLCryptoSymmetricKey(XSECCryptoSymmetricKey::KEY_3DES_CBC_192);
				k->setKey((unsigned char *) s_bobKey, strlen(s_bobKey));
				return k;
			}
			if (strEquals(s_jobName, name)) {
				OpenSSLCryptoSymmetricKey * k;
				k = new OpenSSLCryptoSymmetricKey(XSECCryptoSymmetricKey::KEY_AES_ECB_128);
				k->setKey((unsigned char *) s_jobKey, strlen(s_bobKey));
				return k;
			}

		}

	}

	return NULL;

}



XSECKeyInfoResolver * MerlinFiveInteropResolver::clone(void) const {

	return new MerlinFiveInteropResolver(mp_baseURI);

}



#endif /* HAVE_OPENSSL */
