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
 * XSECSafeBufferFormatter := Class for formatting DOMStrings into SafeBuffers
 *
 * $Id$
 *
 */

#include <xsec/utils/XSECSafeBufferFormatter.hpp>
#include <xercesc/util/XMLString.hpp>
#include <xsec/framework/XSECError.hpp>

XERCES_CPP_NAMESPACE_USE

// Constructor

XSECSafeBufferFormatter::XSECSafeBufferFormatter(
						const XMLCh * const			outEncoding,
						const XMLFormatter::EscapeFlags	escapeFlags,
						const XMLFormatter::UnRepFlags unrepFlags) {

	
	sbf = new sbFormatTarget();
	sbf->setBuffer(&formatBuffer);

#if defined(XSEC_XERCES_FORMATTER_REQUIRES_VERSION)
	formatter = new XMLFormatter(outEncoding, 
									0,
									sbf, 
									escapeFlags, 
									unrepFlags);
#else
	XSECnew(formatter, XMLFormatter(outEncoding, 
									sbf, 
									escapeFlags, 
									unrepFlags));
#endif
}





XSECSafeBufferFormatter::XSECSafeBufferFormatter(
						const char * const			outEncoding,
						const XMLFormatter::EscapeFlags	escapeFlags,
						const XMLFormatter::UnRepFlags unrepFlags) {

	sbf = new sbFormatTarget();
	sbf->setBuffer(&formatBuffer);

#if defined(XSEC_XERCES_FORMATTER_REQUIRES_VERSION)
	formatter = new XMLFormatter(outEncoding, 
									0,
									sbf, 
									escapeFlags, 
									unrepFlags);
#else
	XSECnew(formatter, XMLFormatter(outEncoding, 
									sbf, 
									escapeFlags, 
									unrepFlags));
#endif

}

// Destructor

XSECSafeBufferFormatter::~XSECSafeBufferFormatter() {

	if (formatter != NULL)
		delete formatter;

	if (sbf != NULL)
		delete sbf;

}

// Reimplementation of XMLFormatter functions

void  XSECSafeBufferFormatter::formatBuf (
				 const XMLCh *const toFormat,
				 const unsigned int count,
				 const XMLFormatter::EscapeFlags escapeFlags,
				 const XMLFormatter::UnRepFlags unrepFlags) {


	formatter->formatBuf(toFormat, count, escapeFlags, unrepFlags);

}

XSECSafeBufferFormatter&  XSECSafeBufferFormatter::operator<< (const XMLCh *const toFormat) {

	sbf->reset();
	*formatter << toFormat;

	return *this;

}

XSECSafeBufferFormatter&  
     XSECSafeBufferFormatter::operator<< (const XMLCh toFormat) {

	sbf->reset();
	*formatter << toFormat;
	return *this;

}

const XMLCh*  XSECSafeBufferFormatter::getEncodingName ()const {

	return formatter->getEncodingName();

}

void  XSECSafeBufferFormatter::setEscapeFlags (const XMLFormatter::EscapeFlags newFlags) {

	formatter->setEscapeFlags(newFlags);

}
void  XSECSafeBufferFormatter::setUnRepFlags (const XMLFormatter::UnRepFlags newFlags) {

	formatter->setUnRepFlags(newFlags);

}

XSECSafeBufferFormatter&  XSECSafeBufferFormatter::operator<< (const XMLFormatter::EscapeFlags newFlags) {

	*formatter << newFlags;
	return *this;

}

XSECSafeBufferFormatter&  XSECSafeBufferFormatter::operator<< (const XMLFormatter::UnRepFlags newFlags) {

	*formatter << newFlags;
	return *this;

}

// Friends for working with safestrings

safeBuffer& operator<< (safeBuffer &to, const XSECSafeBufferFormatter & from) {

	// Simply copy out the format buffer, but zeroise the original first

	to = from.formatBuffer;
	return to;

}

