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
 * Revision 1.1  2003/02/12 11:21:03  blautenb
 * UNIX generic URI resolver
 *
 *
 */

#ifndef UNIXXSECBINHTTPURIINPUTSTREAM_HEADER
#define UNIXXSECBINHTTPURIINPUTSTREAM_HEADER

#include <xsec/framework/XSECDefs.hpp>

#include <xercesc/util/XMLUri.hpp>
#include <xercesc/util/XMLExceptMsgs.hpp>
#include <xercesc/util/BinInputStream.hpp>

XSEC_USING_XERCES(XMLUri);
XSEC_USING_XERCES(BinInputStream);


//
// This class implements the BinInputStream interface specified by the XML
// parser.
//

class DSIG_EXPORT XSECBinHTTPURIInputStream : public BinInputStream
{
public :
    XSECBinHTTPURIInputStream(const XMLUri&  urlSource);
    ~XSECBinHTTPURIInputStream();

    unsigned int curPos() const;
    unsigned int readBytes
    (
                XMLByte* const  toFill
        , const unsigned int    maxToRead
    );


private :
    // -----------------------------------------------------------------------
    //  Private data members
    //
    //  fSocket
    //      The socket representing the connection to the remote file.
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

	int getSocketHandle(const XMLUri&  urlSource);

    int                 fSocket;
    unsigned int        fBytesProcessed;
    char                fBuffer[4000];
    char *              fBufferEnd;
    char *              fBufferPos;

};


inline unsigned int XSECBinHTTPURIInputStream::curPos() const
{
    return fBytesProcessed;
}


#endif // UNIXXSECBINHTTPURIINPUTSTREAM_HEADER
