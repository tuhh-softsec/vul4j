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
 * XSECBinHTTPURIInputStream := Re-implementation of Xerces BinHTTPInputStream
 *                              Allows us to modify and create an input
 *                              stream that follows re-directs which is
 *                              necessary to fully support XML-DSIG interop
 *                              tests
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 * $Log$
 * Revision 1.6  2003/09/11 11:29:12  blautenb
 * Fix Xerces namespace usage in *NIX build
 *
 * Revision 1.5  2003/07/05 10:30:37  blautenb
 * Copyright update
 *
 * Revision 1.4  2003/05/19 12:31:00  blautenb
 * Cleaned up constants so can compile under INTEL compiler
 *
 * Revision 1.3  2003/03/23 09:49:49  blautenb
 * Silly mistype in ==
 *
 * Revision 1.2  2003/03/15 22:41:46  blautenb
 * Add 301 (permanently moved) support
 *
 * Revision 1.1  2003/02/12 11:21:03  blautenb
 * UNIX generic URI resolver
 *
 *
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <errno.h>

#include "XSECBinHTTPURIInputStream.hpp"
#include <xsec/framework/XSECError.hpp>

#include <xercesc/util/XMLNetAccessor.hpp>
#include <xercesc/util/XMLString.hpp>
#include <xercesc/util/XMLExceptMsgs.hpp>
#include <xercesc/util/Janitor.hpp>
#include <xercesc/util/XMLUniDefs.hpp>

XERCES_CPP_NAMESPACE_USE

int XSECBinHTTPURIInputStream::getSocketHandle(const XMLUri&  urlSource) {

    //
    // Pull all of the parts of the URL out of th urlSource object, and transcode them
    //   and transcode them back to ASCII.
    //
    const XMLCh*        hostName = urlSource.getHost();
    char*               hostNameAsCharStar = XMLString::transcode(hostName);
    ArrayJanitor<char>  janBuf1(hostNameAsCharStar);

    const XMLCh*        path = urlSource.getPath();
    char*               pathAsCharStar = XMLString::transcode(path);
    ArrayJanitor<char>  janBuf2(pathAsCharStar);

    const XMLCh*        fragment = urlSource.getFragment();
    char*               fragmentAsCharStar = 0;
    if (fragment)
        fragmentAsCharStar = XMLString::transcode(fragment);
    ArrayJanitor<char>  janBuf3(fragmentAsCharStar);

    const XMLCh*        query = urlSource.getQueryString();
    char*               queryAsCharStar = 0;
    if (query)
        queryAsCharStar = XMLString::transcode(query);
    ArrayJanitor<char>  janBuf4(queryAsCharStar);		

    unsigned short      portNumber = (unsigned short) urlSource.getPort();
	if (portNumber == USHRT_MAX)
		portNumber = 80;

    //
    // Set up a socket.
    //
    struct hostent*     hostEntPtr = 0;
    struct sockaddr_in  sa;


    if ((hostEntPtr = gethostbyname(hostNameAsCharStar)) == NULL)
    {
        unsigned long  numAddress = inet_addr(hostNameAsCharStar);
        if (numAddress == 0)
        {
            ThrowXML(NetAccessorException,
                     XMLExcepts::NetAcc_TargetResolution);
        }
        if ((hostEntPtr =
                gethostbyaddr((char *) &numAddress,
                              sizeof(unsigned long), AF_INET)) == NULL)
        {
            ThrowXML(NetAccessorException,
                     XMLExcepts::NetAcc_TargetResolution);
        }
    }

    memcpy((void *) &sa.sin_addr,
           (const void *) hostEntPtr->h_addr, hostEntPtr->h_length);
    sa.sin_family = hostEntPtr->h_addrtype;
    sa.sin_port = htons(portNumber);

    int s = socket(hostEntPtr->h_addrtype, SOCK_STREAM, 0);
    if (s < 0)
    {
        throw XSECException(XSECException::HTTPURIInputStreamError,
							"Error creating socket");

    }

    if (connect(s, (struct sockaddr *) &sa, sizeof(sa)) < 0)
    {
        throw XSECException(XSECException::HTTPURIInputStreamError,
							"Error connecting to end server");
    }

    // The port is open and ready to go.
    // Build up the http GET command to send to the server.
    // To do:  We should really support http 1.1.  This implementation
    //         is weak.
    strcpy(fBuffer, "GET ");
    strcat(fBuffer, pathAsCharStar);

    if (queryAsCharStar != 0)
    {		
        size_t n = strlen(fBuffer);
        fBuffer[n] = XERCES_CPP_NAMESPACE_QUALIFIER chQuestion;
        fBuffer[n+1] = XERCES_CPP_NAMESPACE_QUALIFIER chNull;
        strcat(fBuffer, queryAsCharStar);
    }

    if (fragmentAsCharStar != 0)
    {
        strcat(fBuffer, fragmentAsCharStar);
    }
    strcat(fBuffer, " HTTP/1.0\r\n");


    strcat(fBuffer, "Host: ");
    strcat(fBuffer, hostNameAsCharStar);
    if (portNumber != 80)
    {
        int i = strlen(fBuffer);
		sprintf(fBuffer+i, ":%d", portNumber);
        // _itoa(portNumber, fBuffer+i, 10);
    }
    strcat(fBuffer, "\r\n\r\n");

    // Send the http request
    int lent = strlen(fBuffer);
    int  aLent = 0;
    if ((aLent = write(s, (void *) fBuffer, lent)) != lent)
    {
        throw XSECException(XSECException::HTTPURIInputStreamError,
							"Error writing to socket");
    }

    //
    // get the response, check the http header for errors from the server.
    //
    aLent = read(s, (void *)fBuffer, sizeof(fBuffer)-1);
    if (aLent <= 0)
    {
        throw XSECException(XSECException::HTTPURIInputStreamError,
							"Error reported reading socket");
    }

    fBufferEnd = fBuffer+aLent;
    *fBufferEnd = 0;

    // Find the break between the returned http header and any data.
    //  (Delimited by a blank line)
    // Hang on to any data for use by the first read from this BinHTTPURLInputStream.
    //
    fBufferPos = strstr(fBuffer, "\r\n\r\n");
    if (fBufferPos != 0)
    {
        fBufferPos += 4;
        *(fBufferPos-2) = 0;
    }
    else
    {
        fBufferPos = strstr(fBuffer, "\n\n");
        if (fBufferPos != 0)
        {
            fBufferPos += 2;
            *(fBufferPos-1) = 0;
        }
        else
            fBufferPos = fBufferEnd;
    }

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

	if (httpResponse == 302 || httpResponse == 301) {
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
		XMLCh * redirectBufTrans = XMLString::transcode(redirectBuf);
		ArrayJanitor<XMLCh> j_redirectBufTrans(redirectBufTrans);

		return getSocketHandle(XMLUri(redirectBufTrans));
	}

    else if (httpResponse != 200)
    {
        // Most likely a 404 Not Found error.
        //   Should recognize and handle the forwarding responses.
        //
		throw XSECException(XSECException::HTTPURIInputStreamError,
						"Unknown HTTP Response");
    }

	return s;
}


XSECBinHTTPURIInputStream::XSECBinHTTPURIInputStream(const XMLUri& urlSource)
      : fSocket(0)
      , fBytesProcessed(0)
{

    fSocket = getSocketHandle(urlSource);

}



XSECBinHTTPURIInputStream::~XSECBinHTTPURIInputStream()
{
    shutdown(fSocket, 2);
    close(fSocket);
}


unsigned int XSECBinHTTPURIInputStream::readBytes(XMLByte* const    toFill
                                      , const unsigned int    maxToRead)
{
    unsigned int len = fBufferEnd - fBufferPos;
    if (len > 0)
    {
        // If there's any data left over in the buffer into which we first
        //   read from the server (to get the http header), return that.
        if (len > maxToRead)
            len = maxToRead;
        memcpy(toFill, fBufferPos, len);
        fBufferPos += len;
    }
    else
    {
        // There was no data in the local buffer.
        // Read some from the socket, straight into our caller's buffer.
        //
        len = read(fSocket, (void *) toFill, maxToRead);
        if (len == UINT_MAX)
        {
			throw XSECException(XSECException::HTTPURIInputStreamError,
						"Error reading from Socket");
        }
    }

    fBytesProcessed += len;
    return len;
}

