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
 * XSECDOMUtils:= Utilities to manipulate DOM within XML-SECURITY
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */

#ifndef XSECDOMUTILS_HEADER
#define XSECDOMUTILS_HEADER

// XSEC

#include <xsec/utils/XSECPlatformUtils.hpp>
#include <xsec/utils/XSECSafeBuffer.hpp>
#include <xsec/dsig/DSIGConstants.hpp>

// Xerces

#include <xercesc/dom/DOM.hpp>
#include <xercesc/util/XMLString.hpp>

XSEC_USING_XERCES(DOMNode);
XSEC_USING_XERCES(XMLString);

#define COMPARE_STRING_LENGTH	256

// --------------------------------------------------------------------------------
//           "Quick" Transcode Class (low performance)
// --------------------------------------------------------------------------------


class DSIG_EXPORT XMLT {

public:

	XMLT(const char * str);
	~XMLT(void);

	XMLCh * getUnicodeStr(void);

private:

	XMLCh * mp_unicodeStr;
	XMLT();

};

#define MAKE_UNICODE_STRING(str) XMLT(str).getUnicodeStr()

// --------------------------------------------------------------------------------
//           Utilities to manipulate namespaces
// --------------------------------------------------------------------------------

const XMLCh DSIG_EXPORT * getDSIGLocalName(const DOMNode *node);
const XMLCh DSIG_EXPORT * getECLocalName(const DOMNode *node);
const XMLCh DSIG_EXPORT * getXPFLocalName(const DOMNode *node);

// --------------------------------------------------------------------------------
//           Find a nominated DSIG node in a document
// --------------------------------------------------------------------------------

DOMNode DSIG_EXPORT * findDSIGNode(DOMNode *n, const char * nodeName);

// --------------------------------------------------------------------------------
//           Find particular type of node child
// --------------------------------------------------------------------------------

DOMNode DSIG_EXPORT * findFirstChildOfType(DOMNode *n, DOMNode::NodeType t);

// --------------------------------------------------------------------------------
//           Make a QName
// --------------------------------------------------------------------------------

safeBuffer DSIG_EXPORT &makeQName(safeBuffer & qname, safeBuffer &prefix, const char * localName);
safeBuffer DSIG_EXPORT &makeQName(safeBuffer & qname, const XMLCh *prefix, const char * localName);

// --------------------------------------------------------------------------------
//           Gather text from children
// --------------------------------------------------------------------------------

void DSIG_EXPORT gatherChildrenText(DOMNode * parent, safeBuffer &output);

// --------------------------------------------------------------------------------
//           String decode/encode
// --------------------------------------------------------------------------------

/*
 * Distinguished names have a particular encoding that needs to be performed prior
 * to enclusion in the DOM
 */

XMLCh * encodeDName(const XMLCh * toEncode);
XMLCh * decodeDName(const XMLCh * toDecode);

// --------------------------------------------------------------------------------
//           String Functions 
// --------------------------------------------------------------------------------

inline
bool strEquals (const XMLCh * str1, const XMLCh *str2) {

	return (XMLString::compareString(str1, str2) == 0);

}

inline
bool strEquals (const char * str1, const char *str2) {

	return (XMLString::compareString(str1, str2) == 0);

}

inline 
bool strEquals (const char * str1, const XMLCh * str2) {

	bool ret;
	XMLCh * str1XMLCh = XMLString::transcode(str1);

	if (str1XMLCh != NULL) {

		ret = (XMLString::compareString(str1XMLCh, str2) == 0);
		delete str1XMLCh;

	}
	else
		ret = false;

	return ret;

}

inline 
bool strEquals (const XMLCh * str1, const char * str2) {

	bool ret;
	XMLCh * str2XMLCh = XMLString::transcode(str2);

	if (str2XMLCh != NULL) {

		ret = (XMLString::compareString(str1, str2XMLCh) == 0);
		delete [] str2XMLCh;

	}
	else
		ret = false;

	return ret;

}

#endif /* XSECDOMUTILS_HEADER */

