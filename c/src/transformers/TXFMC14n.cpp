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
 * TXFMC14n := Class that performs C14n canonicalisation
 *
 */

#include <xsec/transformers/TXFMC14n.hpp>
#include <xsec/framework/XSECException.hpp>

TXFMC14n::TXFMC14n(DOMDocument *doc) : TXFMBase(doc) {

	mp_c14n = NULL;

}
TXFMC14n::~TXFMC14n() {

	if (mp_c14n != NULL) {
		delete mp_c14n;
	}

}

// Methods to set the inputs

void TXFMC14n::setInput(TXFMBase *newInput) {

	if (newInput->getOutputType() != TXFMBase::DOM_NODES) {

		throw XSECException(XSECException::TransformInputOutputFail, "C14n canonicalisation transform requires DOM_NODES input");

	}

	input = newInput;

	// Set up for comments  - by default we ALWAYS strip comments

	keepComments = false;

	TXFMBase::nodeType type = input->getNodeType();

	switch (type) {

	case TXFMBase::DOM_NODE_DOCUMENT :

		mp_c14n = new XSECC14n20010315(input->getDocument());
		// Expand name spaces
		input->expandNameSpaces();

		break;

	case TXFMBase::DOM_NODE_DOCUMENT_NODE :
	case TXFMBase::DOM_NODE_DOCUMENT_FRAGMENT :

		mp_c14n = new XSECC14n20010315(input->getDocument(), input->getFragmentNode());
		input->expandNameSpaces();
		break;

	case TXFMBase::DOM_NODE_XPATH_NODESET :

		mp_c14n = new XSECC14n20010315(input->getDocument());
		mp_c14n->setXPathMap(input->getXPathNodeList());
		break;

	default :

		throw XSECException(XSECException::XPathError);

	}

	mp_c14n->setCommentsProcessing(keepComments);			// By default we strip comments

}
	
void TXFMC14n::activateComments(void) {

	if (input != NULL)
		keepComments = input->getCommentsStatus();
	else
		keepComments = true;

	if (mp_c14n != NULL)
		mp_c14n->setCommentsProcessing(keepComments);

}

void TXFMC14n::setExclusive() {

	if (mp_c14n != NULL)
		mp_c14n->setExclusive();

}

void TXFMC14n::setExclusive(safeBuffer & NSList) {

	if (mp_c14n != NULL)
		mp_c14n->setExclusive((char *) NSList.rawBuffer());

}

// Methods to get tranform output type and input requirement

TXFMBase::ioType TXFMC14n::getInputType(void) {

	return TXFMBase::DOM_NODES;

}

TXFMBase::ioType TXFMC14n::getOutputType(void) {

	return TXFMBase::BYTE_STREAM;

}

TXFMBase::nodeType TXFMC14n::getNodeType(void) {

	return TXFMBase::DOM_NODE_NONE;

}

// Methods to get output data

unsigned int TXFMC14n::readBytes(XMLByte * const toFill, unsigned int maxToFill) {

	if (mp_c14n == NULL)

		return 0;

	return mp_c14n->outputBuffer(toFill, maxToFill);

}

DOMDocument * TXFMC14n::getDocument() {

	return NULL;

}

DOMNode * TXFMC14n::getFragmentNode() {

	return NULL;

}

const XMLCh * TXFMC14n::getFragmentId() {

	return NULL;	// Empty string

}


