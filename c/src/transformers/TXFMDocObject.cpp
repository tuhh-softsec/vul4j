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
 * TXFMDocObject := Class that takes an input Document object ID to start a txfm pipe
 *
 * $Id$
 *
 */

#include <xsec/transformers/TXFMDocObject.hpp>
#include <xsec/framework/XSECException.hpp>
#include <xsec/utils/XSECDOMUtils.hpp>

XERCES_CPP_NAMESPACE_USE

// Construct/Destruct

TXFMDocObject::TXFMDocObject(DOMDocument *doc) : TXFMBase(doc) {

	input = NULL;
	fragmentId = NULL;
	type = TXFMBase::DOM_NODE_NONE;	// No nodes currently held

}

TXFMDocObject::~TXFMDocObject() {

	if (fragmentId != NULL)
		delete[] fragmentId;

}

// Methods to set the inputs

void TXFMDocObject::setInput(TXFMBase *newInput) {

	// Probably should error, but we store it so we can later clear out pipes.

	input = newInput;

}

DOMNode * findDSIGId(DOMNode *current, const XMLCh * newFragmentId) {

	XSEC_USING_XERCES(DOMNamedNodeMap);

	DOMNode *tmp, *ret;
	DOMNamedNodeMap *atts;

	if (current->getNodeType() == DOMNode::ELEMENT_NODE) {

		atts = current->getAttributes();
		if (atts != NULL) {
			tmp = atts->getNamedItem(MAKE_UNICODE_STRING("Id"));
			if (tmp != 0 && strEquals(tmp->getNodeValue(), newFragmentId)) {

				// Found it!

				return current;

			}

		}

	}

	// Check children

	tmp = current->getFirstChild();
	while (tmp != NULL) {

		if ((ret = findDSIGId(tmp, newFragmentId)) != 0)
			return ret;

		tmp = tmp->getNextSibling();

	}

	return NULL;

}

void TXFMDocObject::setInput(DOMDocument *doc, const XMLCh * newFragmentId) {

	// We have a document fragment marked by an objectID string.

	// Now try to find the node that the objectId belongs to

	fragmentObject = doc->getElementById(newFragmentId);

	if (fragmentObject == 0) {

		// It might be that no DSIG DTD was attached and that the ID is in a
		// DSIG element

		fragmentObject = findDSIGId(doc, newFragmentId);

		if (fragmentObject == 0)

			throw XSECException(XSECException::IDNotFoundInDOMDoc);

	}

	document = doc;
	fragmentId = XMLString::replicate(newFragmentId);
	type = TXFMBase::DOM_NODE_DOCUMENT_FRAGMENT;

}

void TXFMDocObject::setInput(DOMDocument * doc, DOMNode * newFragmentObject) {

	// Have a document fragment directly notified by a DOM_Node

	document = doc;
	fragmentObject = newFragmentObject;
	type = TXFMBase::DOM_NODE_DOCUMENT_NODE;

}

void TXFMDocObject::setInput(DOMDocument *doc) {

	document = doc;
	type = TXFMBase::DOM_NODE_DOCUMENT;

}

	// Methods to get tranform output type and input requirement

TXFMBase::ioType TXFMDocObject::getInputType(void) {

	return TXFMBase::NONE;

}

TXFMBase::ioType TXFMDocObject::getOutputType(void) {

	return TXFMBase::DOM_NODES;

}

TXFMBase::nodeType TXFMDocObject::getNodeType(void) {

	return type;

}

	// Methods to get output data

unsigned int TXFMDocObject::readBytes(XMLByte * const toFill, unsigned int maxToFill) {

	return 0;		// Cannot read a bytestream from a DOM_NODES transform

}

DOMDocument * TXFMDocObject::getDocument() {

	return document;

}

DOMNode * TXFMDocObject::getFragmentNode() {

	return fragmentObject;

}

const XMLCh * TXFMDocObject::getFragmentId() {

	return fragmentId;	

}
