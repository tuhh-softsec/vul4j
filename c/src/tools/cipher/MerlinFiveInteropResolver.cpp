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
#include <xsec/dsig/DSIGKeyInfoX509.hpp>
#include <xsec/utils/XSECDOMUtils.hpp>
#include <xercesc/util/Janitor.hpp>
#include <xercesc/util/XMLUniDefs.hpp>

XERCES_CPP_NAMESPACE_USE

#include <iostream>

#if !defined (HAVE_OPENSSL) && !defined (HAVE_WINCAPI)
#	error Require OpenSSL or Windows Crypto API for the Merlin Resolver
#endif

#if defined (HAVE_OPENSSL)
#	include <openssl/x509.h>
#	include <openssl/pem.h>
#	include <xsec/enc/OpenSSL/OpenSSLCryptoX509.hpp>
#	include <xsec/enc/OpenSSL/OpenSSLCryptoKeyRSA.hpp>
#   include <xsec/enc/OpenSSL/OpenSSLCryptoSymmetricKey.hpp>
#endif

#if defined (HAVE_WINCAPI)
#   include <xsec/enc/WinCAPI/WinCAPICryptoSymmetricKey.hpp>
#endif


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

static XMLCh s_jebName[] = {
	chLatin_j,
	chLatin_e,
	chLatin_b,
	chNull
};

static XMLCh s_jedName[] = {
	chLatin_j,
	chLatin_e,
	chLatin_d,
	chNull
};

static char s_bobKey[] = "abcdefghijklmnopqrstuvwx";
static char s_jobKey[] = "abcdefghijklmnop";
static char s_jebKey[] = "abcdefghijklmnopqrstuvwx";
static char s_jedKey[] = "abcdefghijklmnopqrstuvwxyz012345";


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
// --------------------------------------------------------------------------------
//			Utility functions
// --------------------------------------------------------------------------------
#if defined(_WIN32)

void reverseSlash(safeBuffer &path) {

	for (int i = 0; i < strlen(path.rawCharBuffer()); ++i) {

		if (path[i] == '/')
			path[i] = '\\';

	}

}

#endif
	
XSECCryptoSymmetricKey * MerlinFiveInteropResolver::makeSymmetricKey(XSECCryptoSymmetricKey::SymmetricKeyType type) {

#if defined (HAVE_OPENSSL)

	OpenSSLCryptoSymmetricKey * k;
	k = new OpenSSLCryptoSymmetricKey(type);

	return k;

#else

	WinCAPICryptoSymmetricKey * k;
	k = new WinCAPICryptoSymmetricKey(0, type);

	return k;

#endif

}

BIO * createFileBIO(const XMLCh * baseURI, const char * name) {

	// Open file URI relative to the encrypted file

	BIO * bioFile;
	if ((bioFile = BIO_new(BIO_s_file())) == NULL) {
		
		return NULL;

	}

	safeBuffer fname;
	fname.sbTranscodeIn(baseURI);
	fname.sbStrcatIn("/");
	fname.sbStrcatIn(name);

#if defined(_WIN32)
	reverseSlash(fname);
#endif

	if (BIO_read_filename(bioFile, fname.rawCharBuffer()) <= 0) {
		
		return NULL;

	}

	return bioFile;
}

// --------------------------------------------------------------------------------
//           Resolver
// --------------------------------------------------------------------------------

XSECCryptoKey * MerlinFiveInteropResolver::resolveKey(DSIGKeyInfoList * lst) {

	int lstSize = lst->getSize();

	for (int i = 0; i < lstSize; ++i) {

		DSIGKeyInfo * ki = lst->item(i);

		if (ki->getKeyInfoType() == DSIGKeyInfo::KEYINFO_NAME) {

			DSIGKeyInfoName * kn = dynamic_cast<DSIGKeyInfoName *>(ki);

			const XMLCh * name = kn->getKeyName();

			// Check if this is a key we know

			if (strEquals(s_bobName, name)) {
				XSECCryptoSymmetricKey * k = 
					XSECPlatformUtils::g_cryptoProvider->keySymmetric(XSECCryptoSymmetricKey::KEY_3DES_192);
				try {
					k->setKey((unsigned char *) s_bobKey, strlen(s_bobKey));
				} catch (...) {
					delete k;
					throw;
				}
				return k;
			}
			if (strEquals(s_jobName, name)) {
				XSECCryptoSymmetricKey * k = 
					XSECPlatformUtils::g_cryptoProvider->keySymmetric(XSECCryptoSymmetricKey::KEY_AES_128);
				try {
					k->setKey((unsigned char *) s_jobKey, strlen(s_jobKey));
				} catch(...) {
					delete k;
					throw;
				}
				return k;
			}
			if (strEquals(s_jebName, name)) {
				XSECCryptoSymmetricKey * k = 
					XSECPlatformUtils::g_cryptoProvider->keySymmetric(XSECCryptoSymmetricKey::KEY_AES_192);
				try {
					k->setKey((unsigned char *) s_jebKey, strlen(s_jebKey));
				} catch(...) {
					delete k;
					throw;
				}
				return k;
			}
			if (strEquals(s_jedName, name)) {
				XSECCryptoSymmetricKey * k = 
					XSECPlatformUtils::g_cryptoProvider->keySymmetric(XSECCryptoSymmetricKey::KEY_AES_256);
				try {
					k->setKey((unsigned char *) s_jedKey, strlen(s_jedKey));
				} catch(...) {
					delete k;
					throw;
				}
				return k;
			}

		}

		else if (ki->getKeyInfoType() == DSIGKeyInfo::KEYINFO_X509) {

			DSIGKeyInfoX509 * kix = dynamic_cast<DSIGKeyInfoX509 *> (ki);

			XSECCryptoX509 * XCX509 = kix->getCertificateCryptoItem(0);

			if (XCX509 != 0) {
#if defined (HAVE_OPENSSL)


				if (strEquals(XCX509->getProviderName(),DSIGConstants::s_unicodeStrPROVOpenSSL)) {

					OpenSSLCryptoX509 * OSSLX509 = dynamic_cast<OpenSSLCryptoX509 *>(XCX509);
					X509 * x509 = OSSLX509->getOpenSSLX509();

					// Check the serial number
					BIGNUM * bnserial = ASN1_INTEGER_to_BN(x509->cert_info->serialNumber, NULL);
					char * xserial = BN_bn2dec(bnserial);
					BN_free(bnserial);

					BIO * rsaFile = createFileBIO(mp_baseURI, "rsa.p8");
					if (rsaFile == NULL)
						return NULL;

					PKCS8_PRIV_KEY_INFO * p8inf;
					p8inf = d2i_PKCS8_PRIV_KEY_INFO_bio(rsaFile, NULL);

					EVP_PKEY * pk = EVP_PKCS82PKEY(p8inf);
					OpenSSLCryptoKeyRSA * k = new OpenSSLCryptoKeyRSA(pk);
					PKCS8_PRIV_KEY_INFO_free(p8inf);
					BIO_free_all(rsaFile);
					return k;
						//d2i_PKCS8PrivateKey_bio(rsaFile, NULL, NULL, NULL);

/*					if (strcmp(xserial, cserial) == 0) {
					
						OPENSSL_free(xserial);
						delete[] cserial;
						return true;

					}*/

				}
#if defined (HAVE_WINCAPI)
				else {
#endif /* HAVE_WINCAPI */
#endif /* HAVE_OPENSSL */

#if defined (HAVE_WINCAPI)
					std::cerr << "WARNING - Unable to load PKCS8 private key file into Windows CAPI" << std::endl;
#if defined (HAVE_OPENSSL)
				}
#endif /* HAVE_WINCAPI */
#endif /* HAVE_OPENSSL */
			}
		}
	}

	return NULL;

}

XSECKeyInfoResolver * MerlinFiveInteropResolver::clone(void) const {

	return new MerlinFiveInteropResolver(mp_baseURI);

}



