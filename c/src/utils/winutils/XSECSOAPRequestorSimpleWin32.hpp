/*
 * Copyright 2004 The Apache Software Foundation.
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
 * XSECSOAPRequestorSimpleWin32 := (Very) Basic implementation of a SOAP
 *                         HTTP wrapper for testing the client code.
 *
 *
 * $Id$
 *
 */

#ifndef XSECSOAPREQUESTORSIMPLEWIN32_INCLUDE
#define XSECSOAPREQUESTORSIMPLEWIN32_INCLUDE

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/utils/XSECSOAPRequestor.hpp>

#include <xercesc/util/XMLUri.hpp>

XSEC_DECLARE_XERCES_CLASS(DOMDocument);

/**
 * @ingroup xkms
 */
/*\@{*/

/**
 * @brief Basic HTTP implementation for SOAP Requests
 *
 * The XKMS client code needs to be able to call on a SOAP requestor
 * implementation that will handle wrapping the request in a SOAP msg
 * and transporting it to the SOAP server.  This class provides a very
 * naieve implementation that wraps the message and does a basic
 * HTTP POST to get the message to the end server.
 *
 */


class DSIG_EXPORT XSECSOAPRequestorSimpleWin32 : public XSECSOAPRequestor {

public :

	/** @name Constructors and Destructors */
	//@{

	/**
	 * \brief Constructor
	 *
	 * Create a SOAP requestor that can be used to access a specific
	 * server
	 *
	 * @param uri The URI of the server that will be accessed.
	 * @note The URI must be http://...
	 */

	XSECSOAPRequestorSimpleWin32(const XMLCh * uri);
	~XSECSOAPRequestorSimpleWin32();

	//@}

	/** @name Interface methods */

	/**
	 * \brief Do a SOAP request
	 *
	 * Performs a request based on the passed in DOM document and
	 * the indicated URI.  The function is returns a pointer
	 * to the parsed result message (with the SOAP envelope removed)
	 *
	 * @param request The DOM document containing the message to be 
	 * wrapped and sent.
	 * @returns The DOM document representing the result, with all
	 * SOAP headers removed
	 */

	virtual XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument *
		doRequest(XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument * request);

private:

	char * wrapAndSerialise(XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument * request);

	XERCES_CPP_NAMESPACE_QUALIFIER XMLUri			
						m_uri;

};


#endif /* XSECSOAPREQUESTORSIMPLEWIN32_INCLUDE */

