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
 * XSECSafeBuffer := a class for storing expanding amounts of information.
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *					 
 */


#ifndef XSECSAFEBUFFER_INCLUDE
#define XSECSAFEBUFFER_INCLUDE

#include <xsec/framework/XSECDefs.hpp>
#include <xercesc/util/XMLString.hpp>

/** 
 * \addtogroup internal
 * @{
 */


#define DEFAULT_SAFE_BUFFER_SIZE		1024		// Default size for a safe Buffer

 /**
 *\brief Manage buffers of arbitrary size
 *
 * The safeBuffer class is used internally in the library
 * to manage buffers of bytes or UTF-16 characters.
 *
 * It's a fairly innefficient class, as buffers are continually
 * being wrapped, coppied and enlarged, but given the nature of the
 * library, a single class that manipulates buffers of variable
 * size was felt to be preferable,
 *
 * The safeBuffer is not exposed through interface classes that 
 * might be used by external functions.  In these cases, a
 * pointer to a XMLCh * buffer is used by preference.
 */

class CANON_EXPORT safeBuffer {

public:

	// For checking we are operating on the buffer correctly
	enum bufferType {

		BUFFER_UNKNOWN		= 0,
		BUFFER_CHAR			= 1,
		BUFFER_UNICODE		= 2
	};


	safeBuffer();
	safeBuffer(int initialSize);
	safeBuffer(char * inStr, unsigned int initialSize = DEFAULT_SAFE_BUFFER_SIZE);
	safeBuffer(const safeBuffer & other);

	~safeBuffer();

	static void init(void);

	// "IN" functions - these read in information to the buffer

	void sbStrcpyIn(const char * inStr);
	void sbStrcpyIn(const safeBuffer & inStr);
	void sbStrncpyIn(char * inStr, int n);
	void sbStrncpyIn(const safeBuffer & inStr, int n);
	void sbStrcatIn(char * inStr);
	void sbStrcatIn(const safeBuffer & inStr);
	void sbStrncatIn(char * inStr, int n);
	void sbStrinsIn(const char * inStr, unsigned int offset);

	void sbMemcpyIn(const void * inBuf, int n);
	void sbMemcpyIn(int offset, const void * inBuf, int n);

	void sbMemcpyOut(void * outBuf, int n) const;
	void sbMemshift(int toOffset, int fromOffset, int len);

	// Comparison functions

	int sbStrncmp(char * inStr, int n);
	int sbOffsetStrcmp(char * inStr, unsigned int offset);
	int sbOffsetStrncmp(char * inStr, unsigned int offset, int n);
	int sbStrcmp(char * inStr) const;
	int sbStrcmp(const safeBuffer & inStr) const;
	int sbStrstr(char * inStr);
	int sbOffsetStrstr(const char * inStr, unsigned int offset);


	// Operators

	unsigned char & operator[](int n);
	safeBuffer & operator= (const safeBuffer & cpy);

	// Get functions

	int sbStrlen(void) const;
	unsigned int sbRawBufferSize(void) const;

	// raw buffer manipulation

	const unsigned char * rawBuffer() const;
	const char * rawCharBuffer() const;
	const XMLCh * rawXMLChBuffer() const;
	void resize(unsigned int sz);			// NOTE : Only grows
	void setBufferType(bufferType bt);		// Use with care

	// Unicode (UTF-16 manipulation)
	const XMLCh * sbStrToXMLCh(void);			// Note does not affect internal buffer
	void sbTranscodeIn(const XMLCh * inStr);	// Create a local string from UTF-16
	void sbTranscodeIn(const char * inStr);		// Create a UTF-16 string from local
	void sbXMLChIn(const XMLCh * in);			// Buffer holds XMLCh *
	void sbXMLChAppendCh(const XMLCh c);		// Append a Unicode character to the buffer
	void sbXMLChCat(const XMLCh *str);			// Append a UTF-16 string to the buffer
	void sbXMLChCat(const char * str);			// Append a (transcoded) local string to the buffer


private:

	// Internal function that is used to get a string size and 
	// then re-allocate if necessary

	void checkAndExpand(unsigned int size);
	void checkBufferType(bufferType bt) const;

	unsigned char * buffer;
	unsigned int	bufferSize;
	XMLCh			* mp_XMLCh;
	bufferType		m_bufferType;

	// For XMLCh manipulation
	static size_t	size_XMLCh;
};

/** @} */

#endif /* XSECSAFEBUFFER_INCLUDE */

