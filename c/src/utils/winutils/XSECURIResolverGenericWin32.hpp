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
 * XSECURIResolverGenericWin32 := A URI Resolver that will work "out of
 *                                the box" with Windows.  Re-implements
 *								  much Xerces code, but allows us to
 *								  handle HTTP redirects as is required by
 *								  the DSIG Standard
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 * $Log$
 * Revision 1.3  2003/07/05 10:30:38  blautenb
 * Copyright update
 *
 * Revision 1.2  2003/02/13 10:19:43  blautenb
 * Updated Xerces exceptions to Xsec exception
 *
 * Revision 1.1  2003/02/12 09:45:29  blautenb
 * Win32 Re-implementation of Xerces URIResolver to support re-directs
 *
 *
 */

#ifndef XSECURIRESOLVERGENERICWIN32_INCLUDE
#define XSECURIRESOLVERGENERICWIN32_INCLUDE

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/framework/XSECURIResolver.hpp>

#include <xercesc/util/XMLString.hpp>

#include <map>

XSEC_USING_XERCES(XMLString);


/**
 * @ingroup pubsig
 */
/*\@{*/

/**
 * @brief Generic Windows URI Resolver.
 *
 * The XML Digital Signature standard makes heavy use of URIs to
 * identify information to be referenced and signed.
 *
 * This class implements the XSECURIResolver for Windows32, re-using
 * much of the Xerces code.
 *
 * @todo Re-implement using the Windows Internet API
 */

class DSIG_EXPORT XSECURIResolverGenericWin32 : public XSECURIResolver {

public:

	/** @name Constructors and Destructors */
	//@{

	XSECURIResolverGenericWin32();
	virtual ~XSECURIResolverGenericWin32();

	//@}

	/** @name Interface Methods */
	//@{

	/**
	 * \brief Create a BYTE_STREAM from a URI.
	 *
	 * The resolver is required to take the input URI and
	 * dereference it to an actual stream of octets.
	 *
	 * The octets are provided back to the library using
	 * the Xerces BinInputStream class.
	 *
	 * @note The returned stream is "owned" by the caller, which
	 * will delete it when processing is complete.
	 * @param uri The string containing the URI to be de-referenced.
	 * @returns The octet stream corresponding to the URI.
	 */

	virtual BinInputStream * resolveURI(const XMLCh * uri);

	/**
	 * \brief Clone the resolver to be installed in a new object.
	 *
	 * When URIResolvers are passed into signatures and other
	 * objects, they are cloned and control of the original object
	 * is left with the caller.
	 *
	 */

	virtual XSECURIResolver * clone(void);

	//@}

	/** @name Class specific functions */
	//@{

	/**
	 * \brief Set the base URI for relative URIs.
	 *
	 */

	void setBaseURI(const XMLCh * uri);

	//@}

private:

	XMLCh			* mp_baseURI;


};


#endif /* XSECURIRESOLVERGENERICWIN32_INCLUDE */