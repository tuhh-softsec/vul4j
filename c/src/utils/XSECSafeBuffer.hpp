/*
 * Copyright 2002-2005 The Apache Software Foundation.
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
 * limitations under the License.
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

class TXFMBase;

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
	safeBuffer(const char * inStr, unsigned int initialSize = DEFAULT_SAFE_BUFFER_SIZE);
	safeBuffer(const safeBuffer & other);

	~safeBuffer();

	static void init(void);

	// "IN" functions - these read in information to the buffer

	void sbStrcpyIn(const char * inStr);
	void sbStrcpyIn(const safeBuffer & inStr);
	void sbStrncpyIn(const char * inStr, int n);
	void sbStrncpyIn(const safeBuffer & inStr, int n);
	void sbStrcatIn(const char * inStr);
	void sbStrcatIn(const safeBuffer & inStr);
	void sbStrncatIn(const char * inStr, int n);
	void sbStrinsIn(const char * inStr, unsigned int offset);

	void sbMemcpyIn(const void * inBuf, int n);
	void sbMemcpyIn(int offset, const void * inBuf, int n);

	void sbMemcpyOut(void * outBuf, int n) const;
	void sbMemshift(int toOffset, int fromOffset, int len);

	// Comparison functions

	int sbStrncmp(const char * inStr, int n) const;
	int sbOffsetStrcmp(const char * inStr, unsigned int offset) const;
	int sbOffsetStrncmp(const char * inStr, unsigned int offset, int n) const;
	int sbStrcmp(const char * inStr) const;
	int sbStrcmp(const safeBuffer & inStr) const;
	int sbStrstr(const char * inStr) const;
	int sbOffsetStrstr(const char * inStr, unsigned int offset) const;

	// XMLCh versions
	int sbStrstr(const XMLCh * inStr) const;
	void sbStrinsIn(const XMLCh * inStr, unsigned int offset);

	// XMLCh and char common functions
	void sbStrlwr(void);		// Lowercase the string

	// Operators

	unsigned char & operator[](int n);
	safeBuffer & operator= (const safeBuffer & cpy);
	safeBuffer & operator= (const XMLCh * inStr);
	safeBuffer & operator << (TXFMBase * t);

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
	void sbXMLChCat8(const char * str);			// Append a (transcoded) UTF-8 string to the buffer

	// Sensitive data functions
	void isSensitive(void);
	void cleanseBuffer(void);

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

	// For sensitive data
	bool			m_isSensitive;
};

/** @} */

#endif /* XSECSAFEBUFFER_INCLUDE */

