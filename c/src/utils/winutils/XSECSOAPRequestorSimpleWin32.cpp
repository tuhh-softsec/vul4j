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

#include "XSECSOAPRequestorSimpleWin32.hpp"

#include <xsec/utils/winutils/XSECBinHTTPURIInputStream.hpp>
#include <xsec/framework/XSECError.hpp>
#include <xsec/utils/XSECSafeBuffer.hpp>
#include <xsec/utils/XSECDOMUtils.hpp>
#include <xsec/xkms/XKMSConstants.hpp>

#define _WINSOCKAPI_

#define INCL_WINSOCK_API_TYPEDEFS 1
#include <winsock2.h>
#include <windows.h>
#include <tchar.h>

#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#include <xercesc/dom/DOM.hpp>
#include <xercesc/parsers/XercesDOMParser.hpp>
#include <xercesc/framework/XMLFormatter.hpp>
#include <xercesc/framework/MemBufFormatTarget.hpp>
#include <xercesc/util/PlatformUtils.hpp>
#include <xercesc/util/XMLNetAccessor.hpp>
#include <xercesc/util/XMLString.hpp>
#include <xercesc/util/XMLExceptMsgs.hpp>
#include <xercesc/util/Janitor.hpp>
#include <xercesc/util/XMLUniDefs.hpp>

XERCES_CPP_NAMESPACE_USE

// --------------------------------------------------------------------------------
//           Strings for constructing SOAP envelopes
// --------------------------------------------------------------------------------

static XMLCh s_prefix[] = {

	chLatin_e,
	chLatin_n,
	chLatin_v,
	chNull
};

static XMLCh s_Envelope[] = {

	chLatin_E,
	chLatin_n,
	chLatin_v,
	chLatin_e,
	chLatin_l,
	chLatin_o,
	chLatin_p,
	chLatin_e,
	chNull
};

static XMLCh s_Header[] = {

	chLatin_H,
	chLatin_e,
	chLatin_a,
	chLatin_d,
	chLatin_e,
	chLatin_r,
	chNull
};

static XMLCh s_Body[] = {

	chLatin_B,
	chLatin_o,
	chLatin_d,
	chLatin_y,
	chNull
};

// --------------------------------------------------------------------------------
//           Constructors and Destructors
// --------------------------------------------------------------------------------


XSECSOAPRequestorSimpleWin32::XSECSOAPRequestorSimpleWin32(const XMLCh * uri) : m_uri(uri) {

	XSECBinHTTPURIInputStream::ExternalInitialize();

}

XSECSOAPRequestorSimpleWin32::~XSECSOAPRequestorSimpleWin32() {
}


// --------------------------------------------------------------------------------
//           Wrap and serialise the request message
// --------------------------------------------------------------------------------

char * XSECSOAPRequestorSimpleWin32::wrapAndSerialise(DOMDocument * request) {

	// Create a new document to wrap the request in

	XMLCh tempStr[100];
	XMLString::transcode("Core", tempStr, 99);    
	DOMImplementation *impl = DOMImplementationRegistry::getDOMImplementation(tempStr);

	safeBuffer str;

	makeQName(str, s_prefix, s_Envelope);

	DOMDocument *doc = impl->createDocument(
		XKMSConstants::s_unicodeStrURISOAP12,
				str.rawXMLChBuffer(),
				NULL);// DOMDocumentType());  // document type object (DTD).

	DOMElement *rootElem = doc->getDocumentElement();

	makeQName(str, s_prefix, s_Header);
	DOMElement *header = doc->createElementNS(
			XKMSConstants::s_unicodeStrURISOAP12,
			str.rawXMLChBuffer());

	rootElem->appendChild(header);

	makeQName(str, s_prefix, s_Body);
	DOMElement *body = doc->createElementNS(
			XKMSConstants::s_unicodeStrURISOAP12,
			str.rawXMLChBuffer());

	header->appendChild(body);

	// Now replicate the request into the document
	DOMElement * reqElement = (DOMElement *) doc->importNode(request->getDocumentElement(), true);
	body->appendChild(reqElement);

	// OK - Now we have the SOAP request as a document, we serialise to a string buffer
	// and return

	DOMWriter         *theSerializer = ((DOMImplementationLS*)impl)->createDOMWriter();

	theSerializer->setEncoding(MAKE_UNICODE_STRING("UTF-8"));
	if (theSerializer->canSetFeature(XMLUni::fgDOMWRTFormatPrettyPrint, false))
		theSerializer->setFeature(XMLUni::fgDOMWRTFormatPrettyPrint, false);

	MemBufFormatTarget *formatTarget = new MemBufFormatTarget;
	theSerializer->writeNode(formatTarget, *doc);

	// Now replicate the buffer
	char * ret = XMLString::replicate((const char *) formatTarget->getRawBuffer());

	delete theSerializer;
	delete formatTarget;

	doc->release();

	return ret;
}



// --------------------------------------------------------------------------------
//           Interface
// --------------------------------------------------------------------------------


DOMDocument * XSECSOAPRequestorSimpleWin32::doRequest(DOMDocument * request) {


	char * content = wrapAndSerialise(request);

	// First we need to serialise

    char                fBuffer[4000];
    char *              fBufferEnd;
    char *              fBufferPos;

    //
    // Pull all of the parts of the URL out of th m_uri object, and transcode them
    //   and transcode them back to ASCII.
    //
    const XMLCh*        hostName = m_uri.getHost();
    char*               hostNameAsCharStar = XMLString::transcode(hostName);
    ArrayJanitor<char>  janBuf1(hostNameAsCharStar);

    const XMLCh*        path = m_uri.getPath();
    char*               pathAsCharStar = XMLString::transcode(path);
    ArrayJanitor<char>  janBuf2(pathAsCharStar);

    const XMLCh*        fragment = m_uri.getFragment();
    char*               fragmentAsCharStar = 0;
    if (fragment)
        fragmentAsCharStar = XMLString::transcode(fragment);
    ArrayJanitor<char>  janBuf3(fragmentAsCharStar);

    const XMLCh*        query = m_uri.getQueryString();
    char*               queryAsCharStar = 0;
    if (query)
        queryAsCharStar = XMLString::transcode(query);
    ArrayJanitor<char>  janBuf4(queryAsCharStar);		

    unsigned short      portNumber = (unsigned short) m_uri.getPort();

	// If no number is set, go with port 80
	if (portNumber == USHRT_MAX)
		portNumber = 80;

    //
    // Set up a socket.
    //
    struct hostent*     hostEntPtr = 0;
    struct sockaddr_in  sa;


    if ((hostEntPtr = XSECBinHTTPURIInputStream::gethostbyname(hostNameAsCharStar)) == NULL)
    {
        unsigned long  numAddress = XSECBinHTTPURIInputStream::inet_addr(hostNameAsCharStar);
        if (numAddress == INADDR_NONE)
        {
            // Call WSAGetLastError() to get the error number.
	        throw XSECException(XSECException::HTTPURIInputStreamError,
							"Error reported resolving IP address");
        }
        if ((hostEntPtr =
                XSECBinHTTPURIInputStream::gethostbyaddr((const char *) &numAddress,
                              sizeof(unsigned long), AF_INET)) == NULL)
        {
            // Call WSAGetLastError() to get the error number.
	        throw XSECException(XSECException::HTTPURIInputStreamError,
							"Error reported resolving IP address");
        }
    }

    memcpy((void *) &sa.sin_addr,
           (const void *) hostEntPtr->h_addr, hostEntPtr->h_length);
    sa.sin_family = hostEntPtr->h_addrtype;
    sa.sin_port = XSECBinHTTPURIInputStream::htons(portNumber);

    SOCKET s = XSECBinHTTPURIInputStream::socket(hostEntPtr->h_addrtype, SOCK_STREAM, 0);
    if (s == INVALID_SOCKET)
    {
        // Call WSAGetLastError() to get the error number.
        throw XSECException(XSECException::HTTPURIInputStreamError,
							"Error reported creating socket");
    }

    if (XSECBinHTTPURIInputStream::connect((unsigned short) s, 
		(struct sockaddr *) &sa, sizeof(sa)) == SOCKET_ERROR)
    {
        // Call WSAGetLastError() to get the error number.
        throw XSECException(XSECException::HTTPURIInputStreamError,
							"Error reported connecting to socket");
    }


    // Set a flag so we know that the headers have not been read yet.
    bool fHeaderRead = false;

    // The port is open and ready to go.
    // Build up the http POST command to send to the server.
    // To do:  We should really support http 1.1.  This implementation
    //         is weak.

    memset(fBuffer, 0, sizeof(fBuffer));

    strcpy(fBuffer, "POST ");
    strcat(fBuffer, pathAsCharStar);

    if (queryAsCharStar != 0)
    {
        // Tack on a ? before the fragment
        strcat(fBuffer,"?");
        strcat(fBuffer, queryAsCharStar);
    }

    if (fragmentAsCharStar != 0)
    {
        strcat(fBuffer, fragmentAsCharStar);
    }
    strcat(fBuffer, " HTTP/1.0\r\n");

	strcat(fBuffer, "Content-Type: text/xml\r\n");


    strcat(fBuffer, "Host: ");
    strcat(fBuffer, hostNameAsCharStar);
    if (portNumber != 80)
    {
        strcat(fBuffer, ":");
        int i = (int) strlen(fBuffer);
        _itoa(portNumber, fBuffer+i, 10);
    }
	strcat(fBuffer, "\r\n");

	strcat(fBuffer, "Content-Length: ");
    int i = (int) strlen(fBuffer);
    _itoa(strlen(content)+2, fBuffer+i, 10);
	strcat(fBuffer, "\r\n");

	strcat(fBuffer, "Connection: Close\r\n");
	strcat(fBuffer, "Cache-Control: no-cache\r\n");
    strcat(fBuffer, "\r\n\r\n");

	// Now the content
	strcat(fBuffer, content);

    // Send the http request
    int lent = (int) strlen(fBuffer);
    int  aLent = 0;
    if ((aLent = XSECBinHTTPURIInputStream::send((unsigned short) s, 
		fBuffer, lent, 0)) != lent)
    {
        // Call WSAGetLastError() to get the error number.
        throw XSECException(XSECException::HTTPURIInputStreamError,
							"Error reported writing to socket");
    }


    //
    // get the response, check the http header for errors from the server.
    //
    memset(fBuffer, 0, sizeof(fBuffer));
    aLent = XSECBinHTTPURIInputStream::recv((unsigned short) s, fBuffer, sizeof(fBuffer)-1, 0);
    if (aLent == SOCKET_ERROR || aLent == 0)
    {
        // Call WSAGetLastError() to get the error number.
        throw XSECException(XSECException::HTTPURIInputStreamError,
							"Error reported reading socket");
    }

    fBufferEnd = fBuffer+aLent;
    *fBufferEnd = 0;

    do {
        // Find the break between the returned http header and any data.
        //  (Delimited by a blank line)
        // Hang on to any data for use by the first read from this XSECBinHTTPURIInputStream.
        //
        fBufferPos = strstr(fBuffer, "\r\n\r\n");
        if (fBufferPos != 0)
        {
            fBufferPos += 4;
            *(fBufferPos-2) = 0;
            fHeaderRead = true;
        }
        else
        {
            fBufferPos = strstr(fBuffer, "\n\n");
            if (fBufferPos != 0)
            {
                fBufferPos += 2;
                *(fBufferPos-1) = 0;
                fHeaderRead = true;
            }
            else
            {
                //
                // Header is not yet read, do another recv() to get more data...
                aLent = XSECBinHTTPURIInputStream::recv((unsigned short) s, fBufferEnd, (sizeof(fBuffer) - 1) - (fBufferEnd - fBuffer), 0);
                if (aLent == SOCKET_ERROR || aLent == 0)
                {
                    // Call WSAGetLastError() to get the error number.
			        throw XSECException(XSECException::HTTPURIInputStreamError,
							"Error reported reading socket");
                }
                fBufferEnd = fBufferEnd + aLent;
                *fBufferEnd = 0;
            }
        }
    } while(fHeaderRead == false);

    // Make sure the header includes an HTTP 200 OK response.
    //
    char *p = strstr(fBuffer, "HTTP");
    if (p == 0)
    {
        throw XSECException(XSECException::HTTPURIInputStreamError,
							"Error reported reading socket");
    }

    p = strchr(p, ' ');
    if (p == 0)
    {
        throw XSECException(XSECException::HTTPURIInputStreamError,
							"Error reported reading socket");
    }

    int httpResponse = atoi(p);

    // Check for redirect or permanently moved
    if (httpResponse == 302 || httpResponse == 301)
    {
        //Once grows, should use a switch
        char redirectBuf[256];
        int q;

        // Find the "Location:" string
        p = strstr(p, "Location:");
        if (p == 0)
        {
	        throw XSECException(XSECException::HTTPURIInputStreamError,
							"Error reported reading socket");
        }
        p = strchr(p, ' ');
		if (p == 0)
		{
	        throw XSECException(XSECException::HTTPURIInputStreamError,
							"Error reported reading socket");
		}
		
		// Now read
		p++;
		for (q=0; q < 255 && p[q] != '\r' && p[q] !='\n'; ++q)
			redirectBuf[q] = p[q];
		
		redirectBuf[q] = '\0';
		
		// Try to find this location
		m_uri;
		m_uri = XMLUri(XMLString::transcode(redirectBuf));

		return doRequest(request);

	}
    else if (httpResponse != 200)
    {
        // Most likely a 404 Not Found error.
        //   Should recognize and handle the forwarding responses.
        //
        throw XSECException(XSECException::HTTPURIInputStreamError,
							"Unknown HTTP response received");
    }

    return NULL;
}



