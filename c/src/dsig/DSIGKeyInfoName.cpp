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
 * DSIGKeyName := Simply a string that names a key for an application to read
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */

#include <xsec/dsig/DSIGKeyInfoName.hpp>
#include <xsec/framework/XSECError.hpp>
#include <xsec/utils/XSECPlatformUtils.hpp>
#include <xsec/utils/XSECDOMUtils.hpp>
#include <xsec/dsig/DSIGSignature.hpp>

#include <xercesc/util/Janitor.hpp>

XSEC_USING_XERCES(ArrayJanitor);

// --------------------------------------------------------------------------------
//           Constructors and Destructors
// --------------------------------------------------------------------------------


DSIGKeyInfoName::DSIGKeyInfoName(DSIGSignature *sig, DOMNode *nameNode) : 
DSIGKeyInfo(sig),
mp_name(NULL),
mp_decodedDName(NULL),
mp_keyNameTextNode(0) {

	mp_keyInfoDOMNode = nameNode;

}


DSIGKeyInfoName::DSIGKeyInfoName(DSIGSignature *sig) : 
DSIGKeyInfo(sig),
mp_name(NULL),
mp_decodedDName(NULL),
mp_keyNameTextNode(0) {

	mp_keyInfoDOMNode = 0;

}


DSIGKeyInfoName::~DSIGKeyInfoName() {

	if (mp_decodedDName != NULL)
		delete[] mp_decodedDName;

};

// --------------------------------------------------------------------------------
//           Load and Get functions
// --------------------------------------------------------------------------------


void DSIGKeyInfoName::load(void) {

	// Assuming we have a valid DOM_Node to start with, load the signing key so that it can
	// be used later on

	if (mp_keyInfoDOMNode == NULL) {

		// Attempt to load an empty signature element
		throw XSECException(XSECException::LoadEmptyInfoName);

	}

	if (!strEquals(getDSIGLocalName(mp_keyInfoDOMNode), "KeyName")) {

		throw XSECException(XSECException::LoadNonInfoName);

	}

	// Now find the text node containing the name

	DOMNode *tmpElt = mp_keyInfoDOMNode->getFirstChild();

	while (tmpElt != 0 && tmpElt->getNodeType() != DOMNode::TEXT_NODE)
		tmpElt = tmpElt->getNextSibling();

	if (tmpElt != 0) {

		mp_keyNameTextNode = tmpElt;
		mp_name = tmpElt->getNodeValue();

	}

	else {

		throw XSECException(XSECException::ExpectedDSIGChildNotFound,
			MAKE_UNICODE_STRING("Expected TEXT node as child to <KeyName> element"));

	}

}

const XMLCh * DSIGKeyInfoName::getDecodedKeyName(void) {

	if (mp_decodedDName == NULL) {

		mp_decodedDName = decodeDName(mp_name);

	}

	return mp_decodedDName;

}

// --------------------------------------------------------------------------------
//           Create and Set functions
// --------------------------------------------------------------------------------

DOMElement * DSIGKeyInfoName::createBlankKeyName(const XMLCh * name, bool isDName) {

	// Create the DOM Structure

	safeBuffer str;
	DOMDocument *doc = mp_parentSignature->getParentDocument();
	const XMLCh * prefix = mp_parentSignature->getDSIGNSPrefix();

	makeQName(str, prefix, "KeyName");

	DOMElement *ret = doc->createElementNS(DSIGConstants::s_unicodeStrURIDSIG, str.rawXMLChBuffer());
	mp_keyInfoDOMNode = ret;

	// Check whether to encode prior to adding
	if (isDName == true) {

		// Treat as a distinguished name

		mp_decodedDName = XMLString::replicate(name);
		XMLCh * encodedName = encodeDName(name);
		ArrayJanitor<XMLCh> j_encodedName(encodedName);

		mp_keyNameTextNode = doc->createTextNode(encodedName);

	}

	else
		mp_keyNameTextNode = doc->createTextNode(name);

	ret->appendChild(mp_keyNameTextNode);

	mp_name = mp_keyNameTextNode->getNodeValue();

	return ret;

}

void DSIGKeyInfoName::setKeyName(const XMLCh * name, bool isDName) {

	if (mp_keyNameTextNode == 0) {

		// Attempt to load an empty element
		throw XSECException(XSECException::LoadEmptySignature,
			MAKE_UNICODE_STRING("KeyInfoName::set() called prior to load() or createBlank()"));

	}

	if (mp_decodedDName != NULL) {

		delete[] mp_decodedDName;
		mp_decodedDName = NULL;

	}

	if (isDName == true) {

		// This name should be treated as a Distinguished Name - so do the
		// required encoding

		mp_decodedDName = XMLString::replicate(name);
		XMLCh * encodedName = encodeDName(name);
		ArrayJanitor<XMLCh> j_encodedName(encodedName);

		mp_keyNameTextNode->setNodeValue(encodedName);

	}

	else {

		mp_keyNameTextNode->setNodeValue(name);

	}

	mp_name = mp_keyNameTextNode->getNodeValue();

}
