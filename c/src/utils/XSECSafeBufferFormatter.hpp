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
 * XSECSafeBufferFormatter := Class for formatting DOMStrings into SafeBuffers
 *
 * Author(s): Berin Lautenbach
 *
 * $ID$
 *
 * $LOG$
 *
 */

#ifndef XSECSAFEBUFFERFORMATTER_INCLUDE
#define XSECSAFEBUFFERFORMATTER_INCLUDE

// XSEC includes

#include <xsec/utils/XSECSafeBuffer.hpp>

// Xerces includes

#include <xercesc/framework/XMLFormatter.hpp>

XSEC_USING_XERCES(XMLFormatter);
XSEC_USING_XERCES(XMLFormatTarget);

class sbFormatTarget : public XMLFormatTarget
{
public:
    
	safeBuffer * buffer;		// Buffer to write to

	sbFormatTarget()  {};
    ~sbFormatTarget() {};

	void setBuffer (safeBuffer * toSet) {buffer = toSet;};


    // -----------------------------------------------------------------------
    //  Implementations of the format target interface
	//  Based on Xerces/Xalan example code
    // -----------------------------------------------------------------------

    void writeChars(const   XMLByte* const  toWrite,
                    const unsigned int    count,
                    XMLFormatter * const formatter)
    {
         buffer->sbMemcpyIn((char *) toWrite, (int) count);
		 buffer->setBufferType(safeBuffer::BUFFER_CHAR);
		(*buffer)[count] = '\0';
    };

private:

    sbFormatTarget(const sbFormatTarget& other);
    void operator=(const sbFormatTarget& rhs);

	
};

// For easy copying to a buffer

//XMLFormatter& operator<< (XMLFormatter& strm, const DOMString& s);

class XSECSafeBufferFormatter {

	XMLFormatter		* formatter;		// To actually perform the formatting
	safeBuffer			formatBuffer;		// Storage of translated strings
	sbFormatTarget		* sbf;				// Format target used by XMLFormatter

public:

	// Constructor

	XSECSafeBufferFormatter(
		const XMLCh * const						outEncoding,
		const XMLFormatter::EscapeFlags			escapeFlags=XMLFormatter::NoEscapes,
		const XMLFormatter::UnRepFlags			unrepFlags=XMLFormatter::UnRep_Fail);

	XSECSafeBufferFormatter(
		const char * const						outEncoding,
		const XMLFormatter::EscapeFlags			escapeFlags=XMLFormatter::NoEscapes,
		const XMLFormatter::UnRepFlags			unrepFlags=XMLFormatter::UnRep_Fail);

	// Destructor

	~XSECSafeBufferFormatter();

	// Reimplementation of XMLFormatter functions

	void  formatBuf (
		const XMLCh *const toFormat,
		const unsigned int count,
		const XMLFormatter::EscapeFlags escapeFlags=XMLFormatter::DefaultEscape,
		const XMLFormatter::UnRepFlags unrepFlags=XMLFormatter::DefaultUnRep);		// Format a buffer

	XSECSafeBufferFormatter&  operator<< (
		const XMLCh *const toFormat);					// Format a buffer
	
	XSECSafeBufferFormatter&  operator<< (
		const XMLCh toFormat);							// Format a character

	const XMLCh*  getEncodingName ()const;				// Get current encoding

	void  setEscapeFlags (const XMLFormatter::EscapeFlags newFlags);
	void  setUnRepFlags (const XMLFormatter::UnRepFlags newFlags);

	XSECSafeBufferFormatter&  operator<< (const XMLFormatter::EscapeFlags newFlags); 
	XSECSafeBufferFormatter&  operator<< (const XMLFormatter::UnRepFlags newFlags); 
	//XSECSafeBufferFormatter& operator<<  (const DOMString &s);

	// Friends for working with safestrings

	friend safeBuffer& operator<< (safeBuffer &to, const XSECSafeBufferFormatter & from);

private:

	// Unimplemented

	XSECSafeBufferFormatter() {};

};

#endif /* XSECSAFEBUFFERFORMATTER_INCLUDE */
