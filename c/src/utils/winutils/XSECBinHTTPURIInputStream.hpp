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

XSEC_USING_XERCES(BinInputStream);
XSEC_USING_XERCES(XMLUri);
XSEC_USING_XERCES(XMLMutex);

//
// This class implements the BinInputStream interface specified by the XML
// parser.
//

struct hostent;
struct sockaddr;


class DSIG_EXPORT XSECBinHTTPURIInputStream : public BinInputStream
{
public :

    XSECBinHTTPURIInputStream(const XMLUri&  urlSource);
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
    static XMLMutex*    fInitMutex;

	static void Initialize();
	unsigned int getSocketHandle(const XMLUri&  urlSource);

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
