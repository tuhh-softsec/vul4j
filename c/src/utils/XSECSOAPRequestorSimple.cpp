/*
 * Copyright 2004 The Apache Software Foundation.
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
 * XSECSOAPRequestorSimple := (Very) Basic implementation of a SOAP
 *                         HTTP wrapper for testing the client code.
 *
 *
 * $Id$
 *
 */

#include "XSECSOAPRequestorSimple.hpp"

#include <xsec/framework/XSECError.hpp>
#include <xsec/utils/XSECSafeBuffer.hpp>
#include <xsec/utils/XSECDOMUtils.hpp>
#include <xsec/xkms/XKMSConstants.hpp>

#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#include <xercesc/dom/DOM.hpp>
#include <xercesc/parsers/XercesDOMParser.hpp>
#include <xercesc/framework/XMLFormatter.hpp>
#include <xercesc/framework/MemBufFormatTarget.hpp>
#include <xercesc/util/PlatformUtils.hpp>
#include <xercesc/util/XMLNetAccessor.hpp>
#include <xercesc/util/XMLString.hpp>
#include <xercesc/util/XMLExceptMsgs.hpp>
#include <xercesc/util/Janitor.hpp>
#include <xercesc/util/XMLUniDefs.hpp>
#include <xercesc/framework/MemBufInputSource.hpp>

XERCES_CPP_NAMESPACE_USE

// --------------------------------------------------------------------------------
//           Strings for constructing SOAP envelopes
// --------------------------------------------------------------------------------

static XMLCh s_prefix[] = {

	chLatin_e,
	chLatin_n,
	chLatin_v,
	chNull
};

static XMLCh s_Envelope[] = {

	chLatin_E,
	chLatin_n,
	chLatin_v,
	chLatin_e,
	chLatin_l,
	chLatin_o,
	chLatin_p,
	chLatin_e,
	chNull
};

static XMLCh s_Body[] = {

	chLatin_B,
	chLatin_o,
	chLatin_d,
	chLatin_y,
	chNull
};

// --------------------------------------------------------------------------------
//           Constructors and Destructors
// --------------------------------------------------------------------------------


/* NOTE: This is initialised via the platform specific code */

XSECSOAPRequestorSimple::~XSECSOAPRequestorSimple() {
}


// --------------------------------------------------------------------------------
//           Wrap and serialise the request message
// --------------------------------------------------------------------------------

char * XSECSOAPRequestorSimple::wrapAndSerialise(DOMDocument * request) {

	// Create a new document to wrap the request in

	XMLCh tempStr[100];
	XMLString::transcode("Core", tempStr, 99);    
	DOMImplementation *impl = DOMImplementationRegistry::getDOMImplementation(tempStr);

	safeBuffer str;

	makeQName(str, s_prefix, s_Envelope);

	DOMDocument *doc = impl->createDocument(
		XKMSConstants::s_unicodeStrURISOAP11,
				str.rawXMLChBuffer(),
				NULL);// DOMDocumentType());  // document type object (DTD).

	DOMElement *rootElem = doc->getDocumentElement();

	makeQName(str, s_prefix, s_Body);
	DOMElement *body = doc->createElementNS(
			XKMSConstants::s_unicodeStrURISOAP11,
			str.rawXMLChBuffer());

	rootElem->appendChild(body);

	// Now replicate the request into the document
	DOMElement * reqElement = (DOMElement *) doc->importNode(request->getDocumentElement(), true);
	body->appendChild(reqElement);

	// OK - Now we have the SOAP request as a document, we serialise to a string buffer
	// and return

	DOMWriter         *theSerializer = ((DOMImplementationLS*)impl)->createDOMWriter();

	theSerializer->setEncoding(MAKE_UNICODE_STRING("UTF-8"));
	if (theSerializer->canSetFeature(XMLUni::fgDOMWRTFormatPrettyPrint, false))
		theSerializer->setFeature(XMLUni::fgDOMWRTFormatPrettyPrint, false);

	MemBufFormatTarget *formatTarget = new MemBufFormatTarget;
	theSerializer->writeNode(formatTarget, *doc);

	// Now replicate the buffer
	char * ret = XMLString::replicate((const char *) formatTarget->getRawBuffer());

	delete theSerializer;
	delete formatTarget;

	doc->release();

	return ret;
}

// --------------------------------------------------------------------------------
//           UnWrap and de-serialise the response message
// --------------------------------------------------------------------------------

DOMDocument * XSECSOAPRequestorSimple::parseAndUnwrap(const char * buf, unsigned int len) {

	XercesDOMParser * parser = new XercesDOMParser;
	Janitor<XercesDOMParser> j_parser(parser);

	parser->setDoNamespaces(true);
	parser->setCreateEntityReferenceNodes(true);
	parser->setDoSchema(true);

	// Create an input source

	MemBufInputSource* memIS = new MemBufInputSource ((const XMLByte*) buf, len, "XSECMem");
	Janitor<MemBufInputSource> j_memIS(memIS);

	int errorCount = 0;

	parser->parse(*memIS);
    errorCount = parser->getErrorCount();
    if (errorCount > 0)
		throw XSECException(XSECException::HTTPURIInputStreamError,
							"Error parsing SOAP response message");

    DOMDocument * responseDoc = parser->getDocument();

	// Now create a new document for the Response message
	XMLCh tempStr[100];
	XMLString::transcode("Core", tempStr, 99);    
	DOMImplementation *impl = DOMImplementationRegistry::getDOMImplementation(tempStr);

	DOMDocument * retDoc = impl->createDocument();

	// Find the base of the response


	DOMNode * e = responseDoc->getDocumentElement();

	e = e->getFirstChild();

	while (e != NULL && (e->getNodeType() != DOMNode::ELEMENT_NODE || !strEquals(e->getLocalName(), "Body")))
		e = e->getNextSibling();

	if (e == NULL)
		throw XSECException(XSECException::HTTPURIInputStreamError,
							"Could not find SOAP body");

	e = findFirstChildOfType(e, DOMNode::ELEMENT_NODE);

	if (e == NULL)
		throw XSECException(XSECException::HTTPURIInputStreamError,
							"Could not find message within SOAP body");

	retDoc->appendChild(retDoc->importNode(e, true));

	return retDoc;

}
