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
 * AnonymousResolver := Class to resolve the IAIK anonymous sample reference
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */

#include "AnonymousResolver.hpp"

// XSEC

#include <xsec/framework/XSECException.hpp>
#include <xsec/utils/XSECDOMUtils.hpp>

#include <xercesc/util/XMLString.hpp>
#include <xercesc/util/Janitor.hpp>
#include <xercesc/framework/URLInputSource.hpp>

XSEC_USING_XERCES(XMLString);
XSEC_USING_XERCES(Janitor);


// ----------------------------------------------------------------------------
//           AnonymousResolver
// ----------------------------------------------------------------------------

AnonymousResolver::~AnonymousResolver() {

	if (mp_baseURI != NULL)
		delete[] mp_baseURI;

}


XSECURIResolver * AnonymousResolver::clone(void) {

   	AnonymousResolver * ret;

	ret = new AnonymousResolver();

	if (this->mp_baseURI != 0)
		ret->mp_baseURI = XMLString::replicate(this->mp_baseURI);
	else
		ret->mp_baseURI = 0;

	return ret;

}

void AnonymousResolver::setBaseURI(const XMLCh * uri) {

	if (mp_baseURI != NULL)
		delete[] mp_baseURI;

	mp_baseURI = XMLString::replicate(uri);

}

BinInputStream * AnonymousResolver::resolveURI(const XMLCh * uri) {

	XSEC_USING_XERCES(URLInputSource);
	XSEC_USING_XERCES(XMLURL);
	XSEC_USING_XERCES(BinInputStream);

	URLInputSource			* URLS;		// Use Xerces URL Input source
	BinInputStream			* is;		// To handle the actual input

	if (uri != NULL) {
		throw XSECException(XSECException::ErrorOpeningURI,
			"AnonymousResolver - only anonymous references supported");
	}

	if (mp_baseURI == 0) {
		URLS = new URLInputSource(XMLURL(MAKE_UNICODE_STRING(anonURI)));
	}
	else {
		URLS = new URLInputSource(XMLURL(XMLURL(mp_baseURI), MAKE_UNICODE_STRING(anonURI)));
	}

	// makeStream can (and is quite likely to) throw an exception
	Janitor<URLInputSource> j_URLS(URLS);

	is = URLS->makeStream();

	if (is == NULL) {

		throw XSECException(XSECException::ErrorOpeningURI,
			"An error occurred in AnonymousResolver when opening an URLInputStream");

	}

	return is;
}

