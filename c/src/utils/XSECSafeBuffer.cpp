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
 * $ID$
 *
 * $LOG$
 *
 */

// XSEC includes

#include <xsec/utils/XSECSafeBuffer.hpp>
#include <xsec/framework/XSECError.hpp>

XSEC_USING_XERCES(XMLString);

// Standard includes

#include <stdlib.h>
#include <string.h>

size_t safeBuffer::size_XMLCh;

void safeBuffer::checkAndExpand(unsigned int size) {

	// For a given size, check it will fit (with one byte spare) 
	// and expand if necessary

	if (size + 1 < bufferSize)
		return;

	// Make the new size twice the size of the new string requirement
	int newBufferSize = size * 2;

	unsigned char * newBuffer = new unsigned char[newBufferSize];
	memcpy(newBuffer, buffer, bufferSize);

	// clean up
	bufferSize = newBufferSize;
	delete[] buffer;
	buffer = newBuffer;

}

void safeBuffer::checkBufferType(bufferType bt) const {

	if (bt != m_bufferType) {

		throw XSECException(XSECException::SafeBufferError,
			"Attempt to perform an operation on a buffer of incorrect type");
	}

}


void safeBuffer::setBufferType(bufferType bt) {

	m_bufferType = bt;

}

void safeBuffer::resize(unsigned int sz) {

	checkAndExpand(sz);

}

safeBuffer::safeBuffer(int initialSize) {

	// Initialise the buffer with a set size string

	bufferSize = initialSize;
	buffer = new unsigned char[initialSize];
	mp_XMLCh = NULL;

}

safeBuffer::safeBuffer() {

	bufferSize = DEFAULT_SAFE_BUFFER_SIZE;
	buffer = new unsigned char[bufferSize];
	mp_XMLCh = NULL;
	m_bufferType = BUFFER_UNKNOWN;

}

safeBuffer::safeBuffer(char * inStr, unsigned int initialSize) {

	// Initialise with a string

	bufferSize = (strlen(inStr) > initialSize ? (strlen(inStr) * 2) : initialSize);
	buffer = new unsigned char[bufferSize];
	strcpy((char *) buffer, inStr);
	mp_XMLCh = NULL;
	m_bufferType = BUFFER_CHAR;

}

safeBuffer::safeBuffer(const safeBuffer & other) {

	// Copy constructor

	bufferSize = other.bufferSize;
	buffer = new unsigned char [bufferSize];

	memcpy(buffer, other.buffer, bufferSize);

	if (other.mp_XMLCh != NULL) {

		mp_XMLCh = XMLString::replicate(other.mp_XMLCh);

	}
	else {

		mp_XMLCh = NULL;

	}

	m_bufferType = other.m_bufferType;

}
	
safeBuffer::~safeBuffer() {

	if (buffer != NULL)
		delete[] buffer;

	if (mp_XMLCh != NULL)
		delete[] mp_XMLCh;

}

void safeBuffer::init (void) {

	size_XMLCh = sizeof(XMLCh);

}

// "IN" functions - these read in information to the buffer

void safeBuffer::sbStrcpyIn(const char * inStr) {

	// Copy a string into the safe buffer
	checkAndExpand(strlen(inStr));
	strcpy((char *) buffer, inStr);
	m_bufferType = BUFFER_CHAR;

}

void safeBuffer::sbStrcpyIn(const safeBuffer & inStr) {

	inStr.checkBufferType(BUFFER_CHAR);
	checkAndExpand(strlen((char *) inStr.buffer));
	strcpy((char *) buffer, (char *) inStr.buffer);
	m_bufferType = BUFFER_CHAR;

}


void safeBuffer::sbStrncpyIn(char * inStr, int n) {

	int len = strlen(inStr);
	checkAndExpand((n < len) ? n : len);
	strncpy((char *) buffer, inStr, n);
	m_bufferType = BUFFER_CHAR;

}

void safeBuffer::sbStrncpyIn(const safeBuffer & inStr, int n) {

	inStr.checkBufferType(BUFFER_CHAR);
	checkAndExpand(n);
	strncpy((char *) buffer, (char *) inStr.buffer, n);
	buffer[n] = '\0';
	m_bufferType = BUFFER_CHAR;


}


void safeBuffer::sbStrcatIn(char * inStr) {

	checkBufferType(BUFFER_CHAR);
	checkAndExpand(strlen((char *) buffer) + strlen(inStr));
	strcat((char *) buffer, inStr);

}

void safeBuffer::sbStrcatIn(const safeBuffer & inStr) {

	checkBufferType(BUFFER_CHAR);
	checkAndExpand(strlen((char *) buffer) + strlen((char *) inStr.buffer) + 2);
	strcat((char *) buffer, (char *) inStr.buffer);

}

void safeBuffer::sbStrncatIn(char * inStr, int n) {


	checkBufferType(BUFFER_CHAR);
	int len = strlen(inStr);
	checkAndExpand(((n < len) ? n : len) + strlen((char *) buffer) + 2);
	strncat((char *) buffer, inStr, n);

}

void safeBuffer::sbMemcpyIn(void * inBuf, int n) {

	checkAndExpand(n);
	memcpy(buffer, inBuf, n);
	m_bufferType = BUFFER_UNKNOWN;

}

void safeBuffer::sbMemcpyIn(int offset, const void * inBuf, int n) {

	checkAndExpand(n + offset);
	memcpy(&buffer[offset], inBuf, n);
	m_bufferType = BUFFER_UNKNOWN;
}

void safeBuffer::sbStrinsIn(const char * inStr, unsigned int offset) {

	checkBufferType(BUFFER_CHAR);
	
	unsigned int bl = strlen((char *) buffer);
	unsigned int il = strlen((char *) inStr);

	if (offset > bl) {
		throw XSECException(XSECException::SafeBufferError,
			"Attempt to insert string after termination point");
	}

	checkAndExpand(bl + il);

	memmove(&buffer[offset + il], &buffer[offset], bl - offset + 1);
	memcpy(&buffer[offset], inStr, il);

}



void safeBuffer::sbMemcpyOut(void *outBuf, int n) const {

	// WARNING - JUST ASSUMES OUTPUT BUFFER LONG ENOUGH
	// ALSO MAKES NO ASSUMPTION OF THE BUFFER TYPE

	memcpy(outBuf, buffer, n);

}

// Comparisons

int safeBuffer::sbStrncmp(char *inStr, int n) {

	checkBufferType(BUFFER_CHAR);
	return (strncmp((char *) buffer, inStr, n));

}

int safeBuffer::sbStrcmp(char *inStr) const {

	checkBufferType(BUFFER_CHAR);
	return (strcmp((char *) buffer, inStr));

}

int safeBuffer::sbStrcmp(const safeBuffer & inStr) const {

	checkBufferType(BUFFER_CHAR);
	return (strcmp((char *) buffer, (char *) inStr.buffer));

}

int safeBuffer::sbOffsetStrcmp(char * inStr, unsigned int offset) {

	checkBufferType(BUFFER_CHAR);
	unsigned int bl = strlen((char *) buffer);

	if (offset > bl)
		return -1;

	return (strcmp((char *) &buffer[offset], inStr));

}

int safeBuffer::sbOffsetStrncmp(char * inStr, unsigned int offset, int n) {

	checkBufferType(BUFFER_CHAR);
	unsigned int bl = strlen((char *) buffer);
	if (offset > bl)
		return -1;

	return (strncmp((char *) &buffer[offset], inStr, n));

}

int safeBuffer::sbStrstr(char * inStr) {

	char * p;
	int d;

	checkBufferType(BUFFER_CHAR);
	p = strstr((char *) buffer, inStr);

	if (p == NULL)
		return -1;

	d = (unsigned int) p - (unsigned int) buffer;

	if (d < 0 || (unsigned int) d > bufferSize)
		return -1;

	return d;

}	

int safeBuffer::sbOffsetStrstr(const char * inStr, unsigned int offset) {

	char * p;
	int d;

	checkBufferType(BUFFER_CHAR);
	unsigned int bl = strlen((char *) buffer);

	if (offset > bl)
		return -1;

	p = strstr((char *) &buffer[offset], inStr);

	if (p == NULL)
		return -1;

	d = (unsigned int) p - (unsigned int) buffer;

	if (d < 0 || (unsigned int) d > bufferSize)
		return -1;

	return d;

}

// Operators

unsigned char & safeBuffer::operator[](int n) {

	// If the character is outside our range (but +ve), then simply increase
	// the buffer size - NOTE: it is not our problem if the caller does
	// not realise they are outside the buffer, we are simply trying to ensure
	// the call is "safe"

	if (n < 0)
		return buffer[0];  // Should raise exception

	checkAndExpand(n);

	return buffer[n];

}

safeBuffer & safeBuffer::operator= (const safeBuffer & cpy) {

	if (bufferSize != cpy.bufferSize) {

		if (bufferSize != 0)
			delete [] buffer;

		buffer = new unsigned char [cpy.bufferSize];
		bufferSize = cpy.bufferSize;

	}

	memcpy(buffer, cpy.buffer, bufferSize);
	m_bufferType = cpy.m_bufferType;

	return *this;
}

// Unicode Functions

const XMLCh * safeBuffer::sbStrToXMLCh(void) {

	checkBufferType(BUFFER_CHAR);
	if (mp_XMLCh != NULL)
		delete mp_XMLCh;

	mp_XMLCh = XMLString::transcode((char *) buffer);

	return mp_XMLCh;

}

void safeBuffer::sbTranscodeIn(const XMLCh * inStr) {

	// Transcode the string to the local code page and store in the buffer
	char * t;
	
	t = XMLString::transcode(inStr);

	assert (t != 0);


	// Now copy into our local buffer - a bit inefficient but better in the long run
	// as a buffer that is the exact size is likely to be deleted anyway during a 
	// concat operation

	unsigned int len = strlen(t) + 1;
	checkAndExpand(len);
	strcpy((char *) buffer, t);
	m_bufferType = BUFFER_CHAR;

	delete[] t;

}

void safeBuffer::sbTranscodeIn(const char * inStr) {

	// Transcode the string to the local code page and store in the buffer
	XMLCh * t;
	
	t = XMLString::transcode(inStr);

	assert (t != 0);

	// Copy into local buffer

	unsigned int len = XMLString::stringLen(t) + 1;
	len *= size_XMLCh;
	checkAndExpand(len);

	XMLString::copyString((XMLCh *) buffer, t);
	m_bufferType = BUFFER_UNICODE;
	
	delete[] t;

}


void safeBuffer::sbXMLChIn(const XMLCh * in) {

	checkAndExpand((XMLString::stringLen(in) + 1) * size_XMLCh);

	XMLString::copyString((XMLCh *) buffer, in);
	m_bufferType = BUFFER_UNICODE;

}

void safeBuffer::sbXMLChAppendCh(const XMLCh c) {
	
	checkBufferType(BUFFER_UNICODE);
	unsigned int len = XMLString::stringLen((XMLCh *) buffer);

	checkAndExpand((len + 2) * size_XMLCh); 

	((XMLCh *) buffer)[len++] = c;
	((XMLCh *) buffer)[len] = 0;

}
	
void safeBuffer::sbXMLChCat(const XMLCh *str) {
	
	checkBufferType(BUFFER_UNICODE);
	unsigned int len = XMLString::stringLen((XMLCh *) buffer) * size_XMLCh;
	len += XMLString::stringLen(str);
	len += (2 * size_XMLCh);

	checkAndExpand(len);

	XMLString::catString((XMLCh *) buffer, str);

}

void safeBuffer::sbXMLChCat(const char * str) {

	checkBufferType(BUFFER_UNICODE);
	unsigned int len = XMLString::stringLen((XMLCh *) buffer) * size_XMLCh;

	XMLCh * t = XMLString::transcode(str);

	assert (t != NULL);

	len += XMLString::stringLen(t);
	len += (2 * size_XMLCh);

	checkAndExpand(len);

	XMLString::catString((XMLCh *) buffer, t);

	delete[] t;
}

// Get functions

int safeBuffer::sbStrlen(void) const {

	checkBufferType(BUFFER_CHAR);
	return (strlen ((char *) buffer));

}

unsigned int safeBuffer::sbRawBufferSize(void) const {

	return bufferSize;

}


// raw buffer manipulation

const unsigned char * safeBuffer::rawBuffer() const {

	return buffer;

}

const char * safeBuffer::rawCharBuffer() const {

	return (char *) buffer;

}

const XMLCh * safeBuffer::rawXMLChBuffer() const {

	return (XMLCh *) buffer;

}
