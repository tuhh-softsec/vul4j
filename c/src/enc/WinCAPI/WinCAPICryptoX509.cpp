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
 * WinCAPICryptoX509:= Windows CAPI based class for handling X509 (V3) certificates
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/framework/XSECError.hpp>
#include <xsec/enc/WinCAPI/WinCAPICryptoProvider.hpp>
#include <xsec/enc/WinCAPI/WinCAPICryptoX509.hpp>
#include <xsec/enc/WinCAPI/WinCAPICryptoKeyDSA.hpp>
//#include <xsec/enc/WinCAPI/WinCAPICryptoKeyRSA.hpp>
#include <xsec/enc/XSECCryptoException.hpp>
#include <xsec/enc/XSCrypt/XSCryptCryptoBase64.hpp>

#include <xercesc/util/Janitor.hpp>

XSEC_USING_XERCES(ArrayJanitor);

WinCAPICryptoX509::WinCAPICryptoX509(WinCAPICryptoProvider * owner) :
m_DERX509(""), mp_certContext(NULL), mp_ownerProvider(owner) { 


}

WinCAPICryptoX509::~WinCAPICryptoX509() {

	if (mp_certContext != NULL)
		CertFreeCertificateContext(mp_certContext);

}

// load functions

void WinCAPICryptoX509::loadX509Base64Bin(const char * buf, unsigned int len) {

	unsigned char * rawCert;
	XSECnew(rawCert, unsigned char [len]);
	ArrayJanitor<unsigned char> j_rawCert(rawCert);

	// Base64 Decode
	XSCryptCryptoBase64 b64;

	b64.decodeInit();
	unsigned int rawCertLen = b64.decode((unsigned char *) buf, len, rawCert, len);
	rawCertLen += b64.decodeFinish(&rawCert[rawCertLen], len - rawCertLen);

	// Now load certificate into Win32 CSP

	mp_certContext = CertCreateCertificateContext(
		X509_ASN_ENCODING,
		rawCert,
		rawCertLen);

	if (mp_certContext == 0) {
		throw XSECCryptoException(XSECCryptoException::X509Error,
			"WinCAPIX509:loadX509Base64Bin - Error decoding certificate");
	}


	m_DERX509.sbMemcpyIn(buf, len);
	m_DERX509[len] = '\0';

}

// Info functions

XSECCryptoKey::KeyType WinCAPICryptoX509::getPublicKeyType() {

	if (mp_certContext == NULL) {
		throw XSECCryptoException(XSECCryptoException::X509Error,
			"WinCAPI:X509 - getSigningKeyType called before X509 loaded");
	}

	if (lstrcmp(mp_certContext->pCertInfo->SubjectPublicKeyInfo.Algorithm.pszObjId, CRYPTO_OID_DSA) == 0)
		return XSECCryptoKey::KEY_DSA_PUBLIC;

	if (lstrcmp(mp_certContext->pCertInfo->SubjectPublicKeyInfo.Algorithm.pszObjId, "RSA") == 0)
		return XSECCryptoKey::KEY_RSA_PUBLIC;

	return XSECCryptoKey::KEY_NONE;

}
		

// Get functions
XSECCryptoKey * WinCAPICryptoX509::clonePublicKey() {


	if (mp_certContext == NULL) {
		throw XSECCryptoException(XSECCryptoException::X509Error,
			"WinCAPI:X509 - getSigningKeyType called before X509 loaded");
	}

	// Import the key into the provider to get a pointer to the key

	HCRYPTKEY key;
	BOOL fResult;
                 
	if (getPublicKeyType() == XSECCryptoKey::KEY_DSA_PUBLIC) {

		fResult= CryptImportPublicKeyInfo(
			   mp_ownerProvider->getProvider(),
			   X509_ASN_ENCODING,
			   &(mp_certContext->pCertInfo->SubjectPublicKeyInfo),
			   &key);
                 

		if (fResult == FALSE) {
			throw XSECCryptoException(XSECCryptoException::X509Error,
				"WinCAPI:X509 - Error loading public key info from certificate");
		}

		// Now that we have a handle for the DSA key, create a DSA Key object to
		// wrap it in

		WinCAPICryptoKeyDSA * ret;
		XSECnew(ret, WinCAPICryptoKeyDSA(mp_ownerProvider, key));

		return ret;

	}

	return NULL;		// Unknown key type, but not necessarily an error

}


