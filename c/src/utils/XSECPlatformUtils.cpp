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

