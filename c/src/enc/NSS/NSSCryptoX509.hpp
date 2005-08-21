/*
 * Copyright 2002-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * imitations under the License.
 */

/*
 * XSEC
 *
 * NSSCryptoX509:= NSS based class for handling X509 (V3) certificates
 *
 * Author(s): Milan Tomic
 *
 * $Id$
 *
 */

#ifndef NSSCRYPTOX509_INCLUDE
#define NSSCRYPTOX509_INCLUDE

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/enc/XSECCryptoX509.hpp>

#define _WIN32_WINNT 0x0400
#include <wincrypt.h>

class NSSCryptoProvider;

/**
 * \brief WinCAPI implementation class for interface for X509 certificates.
 * @ingroup wincapicrypto
 *
 * The library uses classes derived from this to process X509 Certificates.
 *
 */

class DSIG_EXPORT NSSCryptoX509 : public XSECCryptoX509 {

public :

	/** @name Constructors and Destructors */
	//@{

	/**
	 * \brief Constructor for X509 objects
	 *
	 * The windows constructor requires RSA or DSS crypto providers, 
	 * depending on the key type within the cert.
	 *
	 * @param provRSA A handle to the PROV_RSA_FULL type provider that the
	 * interface should use when importing keys and manipulating certs
	 * @param provDSS A handle to the PROV_DSS type provider that the
	 * interface should use when importing keys and manipulating certs
	 */

	NSSCryptoX509(HCRYPTPROV provRSA, HCRYPTPROV provDSS);
	NSSCryptoX509(PCCERT_CONTEXT pCertContext);
	virtual ~NSSCryptoX509();

	//@}
	/** @name Key Interface methods */
	//@{

	/**
	 * \brief Return the type of the key stored in the certificate.
	 *
	 * Will extract the key from the certificate to return the appropriate
	 * type
	 *
	 */

	virtual XSECCryptoKey::KeyType getPublicKeyType();

	/**
	 * \brief Get a copy of the public key.
	 *
	 * Extracts the public key from the certificate and returns the appropriate
	 * OpenSSLCryrptoKey (DSA or RSA) object
	 *
	 */

	virtual XSECCryptoKey * clonePublicKey();

	/**
	 * \brief Returns a string that identifies the crypto owner of this library.
	 */

	virtual const XMLCh * getProviderName();

	//@}

	/** @name Load and Get the certificate */
	//@{

	/**
	 * \brief Load a certificate into the object.
	 *
	 * Take a base64 DER encoded certificate and load.
	 *
	 * @param buf A buffer containing the Base64 encoded certificate
	 * @param len The number of bytes of data in the certificate.
	 */

	virtual void loadX509Base64Bin(const char * buf, unsigned int len);

	/**
	 * \brief Get a Base64 DER encoded copy of the certificate
	 *
	 * @returns A safeBuffer containing the DER encoded certificate
	 */

	virtual safeBuffer &getDEREncodingSB(void) {return m_DERX509;}

	//@}

private:

	safeBuffer				m_DERX509;
	PCCERT_CONTEXT		mp_certContext;

	HCRYPTPROV				m_pRSA;
	HCRYPTPROV				m_pDSS;

};


#endif /* NSSCRYPTOX509_INCLUDE */

