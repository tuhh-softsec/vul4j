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
 * Revision 1.4  2004/02/08 10:25:40  blautenb
 * Convert to Apache 2.0 license
 *
 * Revision 1.3  2003/09/11 11:11:05  blautenb
 * Cleaned up usage of Xerces namespace - no longer inject into global namespace in headers
 *
 * Revision 1.2  2003/07/05 10:30:38  blautenb
 * Copyright update
 *
 * Revision 1.1  2003/02/12 09:45:29  blautenb
 * Win32 Re-implementation of Xerces URIResolver to support re-directs
 *
 *
 */


#ifndef XSECBINHTTPURIINPUTSTREAM_HEADER
#define XSECBINHTTPURIINPUTSTREAM_HEADER

#include <xsec/framework/XSECDefs.hpp>

#include <xercesc/util/XMLUri.hpp>
#include <xercesc/util/XMLExceptMsgs.hpp>
#include <xercesc/util/BinInputStream.hpp>
#include <xercesc/util/Mutexes.hpp>

//
// This class implements the BinInputStream interface specified by the XML
// parser.
//

struct hostent;
struct sockaddr;


class DSIG_EXPORT XSECBinHTTPURIInputStream : public XERCES_CPP_NAMESPACE_QUALIFIER BinInputStream
{
public :

    XSECBinHTTPURIInputStream(const XERCES_CPP_NAMESPACE_QUALIFIER XMLUri&  urlSource);
    ~XSECBinHTTPURIInputStream();

    unsigned int curPos() const;
    unsigned int readBytes(XMLByte* const  toFill, const unsigned int    maxToRead);

	static void Cleanup();


private :
    // -----------------------------------------------------------------------
    //  Private data members
    //
    //  fSocketHandle
    //      The socket representing the connection to the remote file.
    //      We deliberately did not define the type to be SOCKET, so as to
    //      avoid bringing in any Windows header into this file.
    //  fBytesProcessed
    //      Its a rolling count of the number of bytes processed off this
    //      input stream.
    //  fBuffer
    //      Holds the http header, plus the first part of the actual
    //      data.  Filled at the time the stream is opened, data goes
    //      out to user in response to readBytes().
    //  fBufferPos, fBufferEnd
    //      Pointers into fBuffer, showing start and end+1 of content
    //      that readBytes must return.
    // -----------------------------------------------------------------------

    unsigned int        fSocketHandle;
    unsigned int        fBytesProcessed;
    char                fBuffer[4000];
    char *              fBufferEnd;
    char *              fBufferPos;
    static bool         fInitialized;
    static XERCES_CPP_NAMESPACE_QUALIFIER XMLMutex*    fInitMutex;

	static void Initialize();
	unsigned int getSocketHandle(const XERCES_CPP_NAMESPACE_QUALIFIER XMLUri&  urlSource);

	inline static hostent* gethostbyname(const char* name);
	inline static unsigned long inet_addr(const char* cp);
	inline static hostent* gethostbyaddr(const char* addr,int len,int type);
	inline static unsigned short htons(unsigned short hostshort);
	inline static unsigned short socket(int af,int type,int protocol);
	inline static int connect(unsigned short s,const sockaddr* name,int namelen);
	inline static int send(unsigned short s,const char* buf,int len,int flags);
	inline static int recv(unsigned short s,char* buf,int len,int flags);
	inline static int shutdown(unsigned int s,int how);
	inline static int closesocket(unsigned int socket);
};


inline unsigned int XSECBinHTTPURIInputStream::curPos() const
{
    return fBytesProcessed;
}


#endif // XSECBINHTTPURIINPUTSTREAM_HEADER
