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
 * TXFMXSL := Class that performs XPath transforms
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */

#include <xsec/transformers/TXFMXSL.hpp>
#include <xsec/dsig/DSIGConstants.hpp>
#include <xsec/framework/XSECError.hpp>

#ifndef XSEC_NO_XSLT

// Xerces
#include <xercesc/dom/DOM.hpp>
#include <xercesc/dom/DOMImplementation.hpp>
#include <xercesc/dom/DOMImplementationLS.hpp>
#include <xercesc/parsers/XercesDOMParser.hpp>
#include <xercesc/framework/MemBufInputSource.hpp>

XSEC_USING_XERCES(XercesDOMParser);
XSEC_USING_XERCES(MemBufInputSource);

#include <iostream>
#include <strstream>
#include <fstream>

XALAN_USING_XALAN(XSLTResultTarget)

// Function used to output data to a safeBuffer

typedef struct TransformXSLOutputHolderStruct {

	safeBuffer	buffer;
	int			offset;

} TransformXSLOutputHolder;

CallbackSizeType TransformXSLOutputFn(const char * s, CallbackSizeType sz, void * data) {

	TransformXSLOutputHolder * output = (TransformXSLOutputHolder *) data;

	output->buffer.sbMemcpyIn(output->offset, s, sz);
	output->offset += sz;
	output->buffer[output->offset] = '\0';

	return sz;

}

// -----------------------------------------------------------------------
//  For expanding name spaces when necessary
// -----------------------------------------------------------------------

bool TXFMXSL::nameSpacesExpanded(void) {

	// NOTE : Do not check inputs as this has its own document

	return (mp_nse != NULL);

}

void TXFMXSL::expandNameSpaces(void) {

	if (mp_nse != NULL)
		return;		// Already done
	
	if (docOut != NULL) {

		XSECnew(mp_nse, XSECNameSpaceExpander(docOut));

		mp_nse->expandNameSpaces();

	}

}

// -----------------------------------------------------------------------
//  Transform functions
// -----------------------------------------------------------------------


TXFMXSL::TXFMXSL(DOMDocument *doc) : 
	TXFMBase(doc),
xpl(xds) {

	// Zeroise all the pointers

	xd = NULL;

}

TXFMXSL::~TXFMXSL() {

	if (docOut != NULL) {
		if (mp_nse != NULL) {
			delete mp_nse;	// Don't bother collapsing
			mp_nse = NULL;
		}
		docOut->release();	
	}
}

// Methods to set the inputs

void TXFMXSL::setInput(TXFMBase *newInput) {

	input = newInput;

	if (newInput->getOutputType() != TXFMBase::BYTE_STREAM) {

		throw XSECException(XSECException::TransformInputOutputFail, "XSL requires DOM_NODES input type");

	}

	// Should have a method to check if the input is a straight URL - if it is, just read the
	// URL name and create an XSLTInputSource with this as the input ID.

	int size = 0;
	int count = 0;
	unsigned char buf[512];

	while ((count = input->readBytes((XMLByte *) buf, 512)) != 0) {

		sbInDoc.sbMemcpyIn(size, buf, count);
		size += count;

	}

	sbInDoc[size] = '\0';

}

void TXFMXSL::evaluateStyleSheet(const safeBuffer &sbStyleSheet) {

	// Set up iostreams for input
	std::istrstream	theXMLStream((char *) sbInDoc.rawBuffer(), strlen((char *) sbInDoc.rawBuffer()));
	std::istrstream	theXSLStream((char *) sbStyleSheet.rawBuffer(), strlen((char *) sbStyleSheet.rawBuffer()));
	//std::istringstream	theXMLStream((char *) sbInDoc.rawBuffer(), strlen((char *) sbInDoc.rawBuffer()));
	//std::istringstream	theXSLStream((char *) sbStyleSheet.rawBuffer(), strlen((char *) sbStyleSheet.rawBuffer()));

	// Now resolve

	XalanTransformer xt;
	TransformXSLOutputHolder txoh;
	txoh.buffer.sbStrcpyIn("");
	txoh.offset = 0;
	
	int res = xt.transform(&theXMLStream, &theXSLStream, (void *) & txoh, TransformXSLOutputFn);

	// Should check res

	// Now use xerces to "re parse" this back into a DOM_Nodes document
	XercesDOMParser * parser = new XercesDOMParser;
	parser->setDoNamespaces(true);
	parser->setCreateEntityReferenceNodes(true);
	parser->setDoSchema(true);

	// Create an input source

	MemBufInputSource* memIS = new MemBufInputSource ((const XMLByte*) txoh.buffer.rawBuffer(), txoh.offset, "XSECMem");

	int errorCount = 0;

	parser->parse(*memIS);
    errorCount = parser->getErrorCount();
    if (errorCount > 0)
		throw XSECException(XSECException::XSLError, "Errors occured when XSL result was parsed back to DOM_Nodes");

    docOut = parser->adoptDocument();

	// Clean up

	delete memIS;
	delete parser;

}

// Methods to get tranform output type and input requirement

TXFMBase::ioType TXFMXSL::getInputType(void) {

	return TXFMBase::DOM_NODES;

}
TXFMBase::ioType TXFMXSL::getOutputType(void) {

	return TXFMBase::DOM_NODES;

}

TXFMBase::nodeType TXFMXSL::getNodeType(void) {

	return TXFMBase::DOM_NODE_DOCUMENT;

}

// Methods to get output data

unsigned int TXFMXSL::readBytes(XMLByte * const toFill, unsigned int maxToFill) {

	return 0;

}

DOMDocument * TXFMXSL::getDocument() {

	return docOut;

}

DOMNode * TXFMXSL::getFragmentNode() {

	return NULL;

}

const XMLCh * TXFMXSL::getFragmentId() {

	return NULL;	// Empty string

}

#endif /* NO_XSLT */
