/*
 * Copyright 2002-2005 The Apache Software Foundation.
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
 * limitations under the License.
 */

/*
 * XSEC
 *
 * XSECPlatformUtils:= To support the platform we run in
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */

// XSEC

#include <xsec/utils/XSECPlatformUtils.hpp>
#include <xsec/framework/XSECError.hpp>
#include <xsec/dsig/DSIGConstants.hpp>
#include <xsec/xkms/XKMSConstants.hpp>
#include <xsec/framework/XSECAlgorithmMapper.hpp>

#include "../xenc/impl/XENCCipherImpl.hpp"

#if defined(_WIN32)
#include <xsec/utils/winutils/XSECBinHTTPURIInputStream.hpp>
#endif

#if defined (HAVE_OPENSSL)
#	include <xsec/enc/OpenSSL/OpenSSLCryptoProvider.hpp>
#endif

#if defined (HAVE_WINCAPI)
#	include <xsec/enc/WinCAPI/WinCAPICryptoProvider.hpp>
#endif

// Static data used by all of XSEC
int XSECPlatformUtils::initCount = 0;
XSECCryptoProvider * XSECPlatformUtils::g_cryptoProvider = NULL;

// Have a const copy for external usage
const XSECAlgorithmMapper * XSECPlatformUtils::g_algorithmMapper = NULL;

XSECAlgorithmMapper * internalMapper = NULL;

// Determine default crypto provider

#if defined (HAVE_OPENSSL)
#	define XSEC_DEFAULT_PROVIDER	OpenSSLCryptoProvider()
#else
#	if defined (HAVE_WINCAPI)
#		define XSEC_DEFAULT_PROVIDER	WinCAPICryptoProvider()
#	endif
#endif

void XSECPlatformUtils::Initialise(XSECCryptoProvider * p) {

	if (++initCount > 1)
		return;

	if (p != NULL)
		g_cryptoProvider = p;
	else
#if defined XSEC_DEFAULT_PROVIDER
		XSECnew(g_cryptoProvider, XSEC_DEFAULT_PROVIDER);
#else
	throw XSECException(XSECException::CryptoProviderError,
		"XSECPlatformUtils::Initialise() called with NULL provider, but no default defined");
#endif

	// Set up necessary constants
	DSIGConstants::create();
	XKMSConstants::create();

	// Initialise the safeBuffer system
	safeBuffer::init();

	// Initialise Algorithm Mapper
	XSECnew(internalMapper, XSECAlgorithmMapper);
	g_algorithmMapper = internalMapper;

	// Initialise the XENCCipherImpl class
	XENCCipherImpl::Initialise();

};

void XSECPlatformUtils::SetCryptoProvider(XSECCryptoProvider * p) {

	if (g_cryptoProvider != NULL)
		delete g_cryptoProvider;

	g_cryptoProvider = p;

}


void XSECPlatformUtils::Terminate(void) {

	if (--initCount > 0)
		return;

	// Clean out the algorithm mapper
	delete internalMapper;

	if (g_cryptoProvider != NULL)
		delete g_cryptoProvider;

	DSIGConstants::destroy();
	XKMSConstants::destroy();

	// Destroy anything platform specific
#if defined(_WIN32)
	XSECBinHTTPURIInputStream::Cleanup();
#endif

}

void XSECPlatformUtils::registerAlgorithmHandler(
		const XMLCh * uri, 
		const XSECAlgorithmHandler & handler) {

	internalMapper->registerHandler(uri, handler);

}

