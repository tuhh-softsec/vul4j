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
 * XSECURIResolverGenericUnix := A URI Resolver that will work "out of
 *                                the box" with UNIX.  Re-implements
 *								  much Xerces code, but allows us to
 *								  handle HTTP redirects as is required by
 *								  the DSIG Standard
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 * $Log$
 * Revision 1.1  2003/02/12 11:21:03  blautenb
 * UNIX generic URI resolver
 *
 *
 */

#include "XSECURIResolverGenericUnix.hpp"

#include <xercesc/util/XMLUniDefs.hpp>
#include <xercesc/util/XMLUri.hpp>
#include <xercesc/util/XMLUni.hpp>
#include <xercesc/util/Janitor.hpp>
#include <xercesc/util/XMLString.hpp>
#include <xercesc/util/BinFileInputStream.hpp>

XSEC_USING_XERCES(XMLString);

#include <xsec/framework/XSECError.hpp>
#include <xsec/utils/unixutils/XSECBinHTTPURIInputStream.hpp>

static const XMLCh gFileScheme[] = {

	XERCES_CPP_NAMESPACE_QUALIFIER chLatin_f,
	XERCES_CPP_NAMESPACE_QUALIFIER chLatin_i,
	XERCES_CPP_NAMESPACE_QUALIFIER chLatin_l,
	XERCES_CPP_NAMESPACE_QUALIFIER chLatin_e,
	XERCES_CPP_NAMESPACE_QUALIFIER chNull

};

static const XMLCh gHttpScheme[] = {

	XERCES_CPP_NAMESPACE_QUALIFIER chLatin_h,
	XERCES_CPP_NAMESPACE_QUALIFIER chLatin_t,
	XERCES_CPP_NAMESPACE_QUALIFIER chLatin_t,
	XERCES_CPP_NAMESPACE_QUALIFIER chLatin_p,
	XERCES_CPP_NAMESPACE_QUALIFIER chNull

};


XSECURIResolverGenericUnix::XSECURIResolverGenericUnix() :
mp_baseURI(NULL) {

};

XSECURIResolverGenericUnix::~XSECURIResolverGenericUnix() {

	if (mp_baseURI != NULL)
		delete[] mp_baseURI;

}

// -----------------------------------------------------------------------
//  Resolve a URI that is passed in
// -----------------------------------------------------------------------

BinInputStream * XSECURIResolverGenericUnix::resolveURI(const XMLCh * uri) {

	XSEC_USING_XERCES(BinInputStream);
	XSEC_USING_XERCES(XMLUri);
	XSEC_USING_XERCES(XMLUni);
	XSEC_USING_XERCES(Janitor);
	XSEC_USING_XERCES(BinFileInputStream);

	XMLUri					* xmluri;

	// Create the appropriate XMLUri objects

	if (mp_baseURI != NULL) {
		XMLUri	* turi;
		XSECnew(turi, XMLUri(mp_baseURI));
		Janitor<XMLUri> j_turi(turi);

		XSECnew(xmluri, XMLUri(turi, uri));
	}
	else {
		XSECnew(xmluri, XMLUri(uri));
	}
	
	Janitor<XMLUri> j_xmluri(xmluri);

	// Determine what kind of URI this is and how to handle it.
	
	if (!XMLString::compareIString(xmluri->getScheme(), gFileScheme)) {

		// This is a file.  We only really understand if this is localhost
		// XMLUri has already cleaned of escape characters (%xx)
        
		if (xmluri->getHost() == NULL || 
			!XMLString::compareIString(xmluri->getHost(), XMLUni::fgLocalHostString)) {

			// Localhost

            BinFileInputStream* retStrm = new BinFileInputStream(xmluri->getPath());
            if (!retStrm->getIsOpen())
            {
                delete retStrm;
                return 0;
            }
            return retStrm;

		}

		else {

			throw XSECException(XSECException::ErrorOpeningURI,
				"XSECURIResolverGenericUnix - unable to open non-localhost file");
		
		}

	}

	// Is the scheme a HTTP?
	if (!XMLString::compareIString(xmluri->getScheme(), gHttpScheme)) {

		// Pass straight to our local XSECBinHTTPUriInputStream
		XSECBinHTTPURIInputStream *ret;

		XSECnew(ret, XSECBinHTTPURIInputStream(*xmluri));

		return ret;
		
	}

	throw XSECException(XSECException::ErrorOpeningURI,
		"XSECURIResolverGenericUnix - unknown URI scheme");
	
}

// -----------------------------------------------------------------------
//  Clone me
// -----------------------------------------------------------------------


XSECURIResolver * XSECURIResolverGenericUnix::clone(void) {

	XSECURIResolverGenericUnix * ret;

	ret = new XSECURIResolverGenericUnix();

	if (this->mp_baseURI != NULL)
		ret->mp_baseURI = XMLString::replicate(this->mp_baseURI);
	else
		ret->mp_baseURI = NULL;

	return ret;

}

