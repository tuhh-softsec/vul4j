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
 * XSECBinHTTPURIInputStream := Re-implementation of the Xerces
 *							    BinHTTPURLInputStream.  Allows us to make
 *								some small changes to support the requirements
 *								of XMLDSIG (notably re-directs)
 *
 * NOTE: Much code taken from Xerces, and the cross platform interfacing is
 * no-where near as nice.
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 * $Log$
 * Revision 1.3  2003/03/15 22:41:46  blautenb
 * Add 301 (permanently moved) support
 *
 * Revision 1.2  2003/02/13 10:19:43  blautenb
 * Updated Xerces exceptions to Xsec exception
 *
 * Revision 1.1  2003/02/12 09:45:29  blautenb
 * Win32 Re-implementation of Xerces URIResolver to support re-directs
 *
 *
 */

#include <xsec/utils/winutils/XSECBinHTTPURIInputStream.hpp>

#define _WINSOCKAPI_

#define INCL_WINSOCK_API_TYPEDEFS 1
#include <winsock2.h>
#include <windows.h>
#include <tchar.h>

#include <stdio.h>
#include <string.h>
#include <stdlib.h>


#include <xercesc/util/PlatformUtils.hpp>
#include <xercesc/util/XMLNetAccessor.hpp>
#include <xercesc/util/XMLString.hpp>
#include <xercesc/util/XMLExceptMsgs.hpp>
#include <xercesc/util/Janitor.hpp>
#include <xercesc/util/XMLUniDefs.hpp>

XSEC_USING_XERCES(NetAccessorException);
XSEC_USING_XERCES(XMLExcepts);
XSEC_USING_XERCES(XMLPlatformUtils);
XSEC_USING_XERCES(XMLMutexLock);
XSEC_USING_XERCES(XMLString);
XSEC_USING_XERCES(ArrayJanitor);

#include <xsec/utils/winutils/XSECBinHTTPURIInputStream.hpp>
#include <xsec/framework/XSECError.hpp>

HMODULE gWinsockLib = NULL;
LPFN_GETHOSTBYNAME gWSgethostbyname = NULL;
LPFN_INET_ADDR gWSinet_addr = NULL;
LPFN_GETHOSTBYADDR gWSgethostbyaddr = NULL;
LPFN_HTONS gWShtons = NULL;
LPFN_SOCKET gWSsocket = NULL;
LPFN_CONNECT gWSconnect = NULL;
LPFN_SEND gWSsend = NULL;
LPFN_RECV gWSrecv = NULL;
LPFN_SHUTDOWN gWSshutdown = NULL;
LPFN_CLOSESOCKET gWSclosesocket = NULL;
LPFN_WSACLEANUP gWSACleanup = NULL;

bool XSECBinHTTPURIInputStream::fInitialized = false;
XMLMutex* XSECBinHTTPURIInputStream::fInitMutex = 0;

void XSECBinHTTPURIInputStream::Initialize() {
    //
    // Initialize the WinSock library here.
    //
    WORD        wVersionRequested;
    WSADATA     wsaData;

	LPFN_WSASTARTUP startup = NULL;
	if(gWinsockLib == NULL) {
		gWinsockLib = LoadLibrary(_T("WSOCK32"));
		if(gWinsockLib == NULL) {
			ThrowXML(NetAccessorException, XMLExcepts::NetAcc_InitFailed);
		}
		else {
			startup = (LPFN_WSASTARTUP) GetProcAddress(gWinsockLib,_T("WSAStartup"));
			gWSACleanup = (LPFN_WSACLEANUP) GetProcAddress(gWinsockLib,_T("WSACleanup"));
			gWSgethostbyname = (LPFN_GETHOSTBYNAME) GetProcAddress(gWinsockLib,_T("gethostbyname"));
			gWSinet_addr = (LPFN_INET_ADDR) GetProcAddress(gWinsockLib,_T("inet_addr"));
			gWSgethostbyaddr = (LPFN_GETHOSTBYADDR) GetProcAddress(gWinsockLib,_T("gethostbyaddr"));
			gWShtons = (LPFN_HTONS) GetProcAddress(gWinsockLib,_T("htons"));
			gWSsocket = (LPFN_SOCKET) GetProcAddress(gWinsockLib,_T("socket"));
			gWSconnect = (LPFN_CONNECT) GetProcAddress(gWinsockLib,_T("connect"));
			gWSsend = (LPFN_SEND) GetProcAddress(gWinsockLib,_T("send"));
			gWSrecv = (LPFN_RECV) GetProcAddress(gWinsockLib,_T("recv"));
			gWSshutdown = (LPFN_SHUTDOWN) GetProcAddress(gWinsockLib,_T("shutdown"));
			gWSclosesocket = (LPFN_CLOSESOCKET) GetProcAddress(gWinsockLib,_T("closesocket"));

			if(startup == NULL ||
				gWSACleanup == NULL ||
				gWSgethostbyname == NULL ||
				gWSinet_addr == NULL ||
				gWSgethostbyaddr == NULL ||
				gWShtons == NULL ||
				gWSsocket == NULL ||
				gWSconnect == NULL ||
				gWSsend == NULL ||
				gWSrecv == NULL ||
				gWSshutdown == NULL ||
				gWSclosesocket == NULL)
			{
				gWSACleanup = NULL;
				Cleanup();
				ThrowXML(NetAccessorException, XMLExcepts::NetAcc_InitFailed);
			}
		}
	}
    wVersionRequested = MAKEWORD( 2, 2 );
    int err = (*startup)(wVersionRequested, &wsaData);
    if (err != 0)
    {
        // Call WSAGetLastError() to get the last error.
        ThrowXML(NetAccessorException, XMLExcepts::NetAcc_InitFailed);
    }
    fInitialized = true;
}

void XSECBinHTTPURIInputStream::Cleanup() {
	if(fInitialized)
	{
		if(gWSACleanup) (*gWSACleanup)();
		gWSACleanup = NULL;
		FreeLibrary(gWinsockLib);
		gWinsockLib = NULL;
		gWSgethostbyname = NULL;
		gWSinet_addr = NULL;
		gWSgethostbyaddr = NULL;
		gWShtons = NULL;
		gWSsocket = NULL;
		gWSconnect = NULL;
		gWSsend = NULL;
		gWSrecv = NULL;
		gWSshutdown = NULL;
		gWSclosesocket = NULL;

      fInitialized = false;
      delete fInitMutex;
      fInitMutex = 0;
	}
}


hostent* XSECBinHTTPURIInputStream::gethostbyname(const char* name)
{
	return (*gWSgethostbyname)(name);
}

unsigned long XSECBinHTTPURIInputStream::inet_addr(const char* cp)
{
	return (*gWSinet_addr)(cp);
}

hostent* XSECBinHTTPURIInputStream::gethostbyaddr(const char* addr,int len,int type)
{
	return (*gWSgethostbyaddr)(addr,len,type);
}

unsigned short XSECBinHTTPURIInputStream::htons(unsigned short hostshort)
{
	return (*gWShtons)(hostshort);
}

unsigned short XSECBinHTTPURIInputStream::socket(int af,int type,int protocol)
{
	return (*gWSsocket)(af,type,protocol);
}

int XSECBinHTTPURIInputStream::connect(unsigned short s,const sockaddr* name,int namelen)
{
	return (*gWSconnect)(s,name,namelen);
}

int XSECBinHTTPURIInputStream::send(unsigned short s,const char* buf,int len,int flags)
{
	return (*gWSsend)(s,buf,len,flags);
}

int XSECBinHTTPURIInputStream::recv(unsigned short s,char* buf,int len,int flags)
{
	return (*gWSrecv)(s,buf,len,flags);
}

int XSECBinHTTPURIInputStream::shutdown(unsigned int s,int how)
{
	return (*gWSshutdown)(s,how);
}

int XSECBinHTTPURIInputStream::closesocket(unsigned int socket)
{
	return (*gWSclosesocket)(socket);
}

unsigned int XSECBinHTTPURIInputStream::getSocketHandle(const XMLUri&  urlSource) {

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

	// If no number is set, go with port 80
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
        if (numAddress == INADDR_NONE)
        {
            // Call WSAGetLastError() to get the error number.
	        throw XSECException(XSECException::HTTPURIInputStreamError,
							"Error reported resolving IP address");
        }
        if ((hostEntPtr =
                gethostbyaddr((const char *) &numAddress,
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
    sa.sin_port = htons(portNumber);

    SOCKET s = socket(hostEntPtr->h_addrtype, SOCK_STREAM, 0);
    if (s == INVALID_SOCKET)
    {
        // Call WSAGetLastError() to get the error number.
        throw XSECException(XSECException::HTTPURIInputStreamError,
							"Error reported creating socket");
    }

    if (connect(s, (struct sockaddr *) &sa, sizeof(sa)) == SOCKET_ERROR)
    {
        // Call WSAGetLastError() to get the error number.
        throw XSECException(XSECException::HTTPURIInputStreamError,
							"Error reported connecting to socket");
    }


    // Set a flag so we know that the headers have not been read yet.
    bool fHeaderRead = false;

    // The port is open and ready to go.
    // Build up the http GET command to send to the server.
    // To do:  We should really support http 1.1.  This implementation
    //         is weak.

    memset(fBuffer, 0, sizeof(fBuffer));

    strcpy(fBuffer, "GET ");
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


    strcat(fBuffer, "Host: ");
    strcat(fBuffer, hostNameAsCharStar);
    if (portNumber != 80)
    {
        strcat(fBuffer, ":");
        int i = strlen(fBuffer);
        _itoa(portNumber, fBuffer+i, 10);
    }
    strcat(fBuffer, "\r\n\r\n");

    // Send the http request
    int lent = strlen(fBuffer);
    int  aLent = 0;
    if ((aLent = send(s, fBuffer, lent, 0)) != lent)
    {
        // Call WSAGetLastError() to get the error number.
        throw XSECException(XSECException::HTTPURIInputStreamError,
							"Error reported writing to socket");
    }


    //
    // get the response, check the http header for errors from the server.
    //
    memset(fBuffer, 0, sizeof(fBuffer));
    aLent = recv(s, fBuffer, sizeof(fBuffer)-1, 0);
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
                aLent = recv(s, fBufferEnd, (sizeof(fBuffer) - 1) - (fBufferEnd - fBuffer), 0);
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
		XMLCh * redirectBufTrans = XMLString::transcode(redirectBuf);
		ArrayJanitor<XMLCh> j_redirectBuf(redirectBufTrans);

		return getSocketHandle(XMLUri(redirectBufTrans));
	}
    else if (httpResponse != 200)
    {
        // Most likely a 404 Not Found error.
        //   Should recognize and handle the forwarding responses.
        //
        throw XSECException(XSECException::HTTPURIInputStreamError,
							"Unknown HTTP response received");
    }

    return (unsigned int) s;
}


XSECBinHTTPURIInputStream::XSECBinHTTPURIInputStream(const XMLUri& urlSource)
      : fSocketHandle(0)
      , fBytesProcessed(0)
{
    if(!fInitialized)
    {
        if (!fInitMutex)
        {
            XMLMutex* tmpMutex = new XMLMutex;
            if (XMLPlatformUtils::compareAndSwap((void**)&fInitMutex, tmpMutex, 0))
            {
                // Someone beat us to it, so let's clean up ours
                delete tmpMutex;
            }
         }
         XMLMutexLock lock(fInitMutex);
         if (!fInitialized)
         {
             Initialize();
         }
    }

	fSocketHandle = getSocketHandle(urlSource);

}



XSECBinHTTPURIInputStream::~XSECBinHTTPURIInputStream()
{
    shutdown(fSocketHandle, SD_BOTH);
    closesocket(fSocketHandle);
}


//
//  readBytes
//
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
        len = recv((SOCKET) fSocketHandle, (char *) toFill, maxToRead, 0);
        if (len == SOCKET_ERROR)
        {
            // Call WSAGetLastError() to get the error number.
	        throw XSECException(XSECException::HTTPURIInputStreamError,
							"Error reported reading socket");
        }
    }

    fBytesProcessed += len;
    return len;
}
