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
 * TXFMParser := A transformer used to transform a byte stream to DOM Nodes
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */

#include <xsec/transformers/TXFMParser.hpp>
#include <xsec/transformers/TXFMChain.hpp>
#include <xsec/utils/XSECPlatformUtils.hpp>
#include <xsec/framework/XSECError.hpp>
#include <xsec/utils/XSECTXFMInputSource.hpp>

#include <xercesc/parsers/XercesDOMParser.hpp>
#include <xercesc/framework/MemBufInputSource.hpp>
#include <xercesc/util/Janitor.hpp>

XERCES_CPP_NAMESPACE_USE

TXFMParser::TXFMParser(DOMDocument * doc) : 
TXFMBase(doc),
mp_parsedDoc(NULL) {


};

TXFMParser::~TXFMParser() {

	if (mp_parsedDoc != NULL) {

		if (mp_nse != NULL) {
			delete mp_nse;	// Don't bother collapsing
			mp_nse = NULL;
		}

		mp_parsedDoc->release();

	}

	mp_parsedDoc = NULL;


};

// -----------------------------------------------------------------------
//  For expanding name spaces when necessary
// -----------------------------------------------------------------------

bool TXFMParser::nameSpacesExpanded(void) {

	// NOTE : Do not check inputs as this has its own document

	return (mp_nse != NULL);

}

void TXFMParser::expandNameSpaces(void) {

	if (mp_nse != NULL)
		return;		// Already done
	
	if (mp_parsedDoc != NULL) {

		XSECnew(mp_nse, XSECNameSpaceExpander(mp_parsedDoc));

		mp_nse->expandNameSpaces();

	}

}

// -----------------------------------------------------------------------
//  Worker function
// -----------------------------------------------------------------------

void TXFMParser::setInput(TXFMBase *newInput) {

	// This transformer terminates all previous inputs and deletes
	// the chain.

	input = newInput;

	// Create a InputStream
	TXFMChain * chain;
	XSECnew(chain, TXFMChain(newInput, false));
	Janitor<TXFMChain> j_chain(chain);

	XSECTXFMInputSource is(chain, false);

	// Create a XercesParser and parse!
	XercesDOMParser parser;

	parser.setDoNamespaces(true);
	parser.setCreateEntityReferenceNodes(true);
	parser.setDoSchema(true);

	int errorCount = 0;

	parser.parse(is);
    errorCount = parser.getErrorCount();
    if (errorCount > 0)
		throw XSECException(XSECException::XSLError, "Errors occured parsing BYTE STREAM");

    mp_parsedDoc = parser.adoptDocument();

	// Clean up

	keepComments = newInput->getCommentsStatus();

}

	// Methods to get tranform output type and input requirement

TXFMBase::ioType TXFMParser::getInputType(void) {

	return TXFMBase::BYTE_STREAM;

}
TXFMBase::ioType TXFMParser::getOutputType(void) {

	return TXFMBase::DOM_NODES;

}


TXFMBase::nodeType TXFMParser::getNodeType(void) {

	return TXFMBase::DOM_NODE_DOCUMENT;

}

	// Methods to get output data

unsigned int TXFMParser::readBytes(XMLByte * const toFill, unsigned int maxToFill) {
	
	return 0;

}

DOMDocument *TXFMParser::getDocument() {

	return mp_parsedDoc;

}

DOMNode * TXFMParser::getFragmentNode() {

	return NULL;		// Return a null node

}

const XMLCh * TXFMParser::getFragmentId() {

	return NULL;	// Empty string

}
