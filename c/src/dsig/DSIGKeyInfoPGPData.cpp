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
 * DSIGKeyName := Simply a string that names a key for an application to read
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */

#include <xsec/dsig/DSIGKeyInfoPGPData.hpp>
#include <xsec/framework/XSECError.hpp>
#include <xsec/utils/XSECPlatformUtils.hpp>
#include <xsec/utils/XSECDOMUtils.hpp>
#include <xsec/dsig/DSIGSignature.hpp>

#include <xercesc/util/Janitor.hpp>

XSEC_USING_XERCES(ArrayJanitor);

// --------------------------------------------------------------------------------
//           Constructors and Destructors
// --------------------------------------------------------------------------------


DSIGKeyInfoPGPData::DSIGKeyInfoPGPData(DSIGSignature *sig, DOMNode *pgpDataNode) : 
DSIGKeyInfo(sig),
mp_keyID(NULL),
mp_keyPacket(NULL),
mp_keyIDTextNode(NULL),
mp_keyPacketTextNode(NULL) {

	mp_keyInfoDOMNode = pgpDataNode;

}


DSIGKeyInfoPGPData::DSIGKeyInfoPGPData(DSIGSignature *sig) : 
DSIGKeyInfo(sig),
mp_keyID(NULL),
mp_keyPacket(NULL),
mp_keyIDTextNode(NULL),
mp_keyPacketTextNode(NULL) {

	mp_keyInfoDOMNode = 0;

}


DSIGKeyInfoPGPData::~DSIGKeyInfoPGPData() {


};

// --------------------------------------------------------------------------------
//           Load and Get functions
// --------------------------------------------------------------------------------


void DSIGKeyInfoPGPData::load(void) {

	// Assuming we have a valid DOM_Node to start with, load the signing key so that it can
	// be used later on

	if (mp_keyInfoDOMNode == NULL) {

		// Attempt to load an empty signature element
		throw XSECException(XSECException::LoadEmptyInfoName);

	}

	if (!strEquals(getDSIGLocalName(mp_keyInfoDOMNode), "PGPData")) {

		throw XSECException(XSECException::ExpectedDSIGChildNotFound,
			"Expected a PGPData node");

	}

	// Now find the first element node containing either KeyID or KeyPacket

	DOMNode * tmpElt = findFirstChildOfType(mp_keyInfoDOMNode, DOMNode::ELEMENT_NODE);

	if (tmpElt == 0) {

		throw XSECException(XSECException::ExpectedDSIGChildNotFound,
			"Expected <PGPKeyID> or <PGPKeyPacket> children of PGPData node");

	}

	if (strEquals(getDSIGLocalName(tmpElt), "PGPKeyID")) {

		// Find the text node
		mp_keyIDTextNode = findFirstChildOfType(tmpElt, DOMNode::TEXT_NODE);

		if (mp_keyIDTextNode == NULL) {

			throw XSECException(XSECException::ExpectedDSIGChildNotFound,
				"Expected a text node beneath PGPKeyID");

		}

		mp_keyID = mp_keyIDTextNode->getNodeValue();

		do {

			tmpElt = tmpElt->getNextSibling();

		} while (tmpElt != NULL && tmpElt->getNodeType() != DOMNode::ELEMENT_NODE);

	}

	if (tmpElt != NULL && strEquals(getDSIGLocalName(tmpElt), "PGPKeyPacket")) {

		// Find the text node
		mp_keyPacketTextNode = findFirstChildOfType(tmpElt, DOMNode::TEXT_NODE);

		if (mp_keyPacketTextNode == NULL) {

			throw XSECException(XSECException::ExpectedDSIGChildNotFound,
				"Expected a text node beneath PGPKeyPacket");

		}

		mp_keyPacket = mp_keyPacketTextNode->getNodeValue();

	}

	if (mp_keyPacketTextNode == NULL && mp_keyIDTextNode == NULL) {

		throw XSECException(XSECException::ExpectedDSIGChildNotFound,
			"Expected <PGPKeyID> or <PGPKeyPacket> children of PGPData node");
	
	}
}

// --------------------------------------------------------------------------------
//           Create and Set functions
// --------------------------------------------------------------------------------

DOMElement * DSIGKeyInfoPGPData::createBlankPGPData(const XMLCh * id, const XMLCh * packet) {

	// Create the DOM Structure

	safeBuffer str;
	DOMDocument *doc = mp_parentSignature->getParentDocument();
	const XMLCh * prefix = mp_parentSignature->getDSIGNSPrefix();

	makeQName(str, prefix, "PGPData");

	DOMElement *ret = doc->createElementNS(DSIGConstants::s_unicodeStrURIDSIG, str.rawXMLChBuffer());
	mp_keyInfoDOMNode = ret;
	ret->appendChild(doc->createTextNode(DSIGConstants::s_unicodeStrNL));

	if (id != NULL) {

		makeQName(str, prefix, "PGPKeyID");
		DOMElement * t = doc->createElementNS(DSIGConstants::s_unicodeStrURIDSIG, str.rawXMLChBuffer());
		ret->appendChild(t);
		ret->appendChild(doc->createTextNode(DSIGConstants::s_unicodeStrNL));
		mp_keyIDTextNode = doc->createTextNode(id);
		t->appendChild(mp_keyIDTextNode);
		mp_keyID = mp_keyIDTextNode->getNodeValue();

	}

	if (packet != NULL) {

		makeQName(str, prefix, "PGPKeyPacket");
		DOMElement * t = doc->createElementNS(DSIGConstants::s_unicodeStrURIDSIG, str.rawXMLChBuffer());
		ret->appendChild(t);
		ret->appendChild(doc->createTextNode(DSIGConstants::s_unicodeStrNL));
		mp_keyPacketTextNode = doc->createTextNode(packet);
		t->appendChild(mp_keyPacketTextNode);
		mp_keyPacket = mp_keyPacketTextNode->getNodeValue();

	}
	
	return ret;

}

void DSIGKeyInfoPGPData::setKeyID(const XMLCh * id) {

	if (mp_keyInfoDOMNode == NULL) {

		throw XSECException(XSECException::KeyInfoError,
			"DSIGKeyInfoPGPData::setKeyID() called prior to load or createBlank");
	}

	if (mp_keyIDTextNode == 0) {

		// Need to create
		safeBuffer str;
		DOMDocument *doc = mp_parentSignature->getParentDocument();
		const XMLCh * prefix = mp_parentSignature->getDSIGNSPrefix();

		makeQName(str, prefix, "PGPKeyID");
		DOMElement * t = doc->createElementNS(DSIGConstants::s_unicodeStrURIDSIG, str.rawXMLChBuffer());
		DOMNode * pkt = findFirstChildOfType(mp_keyInfoDOMNode, DOMNode::ELEMENT_NODE);
		if (pkt != NULL) {
			mp_keyInfoDOMNode->insertBefore(t, pkt);
			mp_keyInfoDOMNode->insertBefore(doc->createTextNode(DSIGConstants::s_unicodeStrNL), pkt);
		}
		else {
			mp_keyInfoDOMNode->appendChild(t);
			mp_keyInfoDOMNode->appendChild(doc->createTextNode(DSIGConstants::s_unicodeStrNL));
		}
		mp_keyIDTextNode = doc->createTextNode(id);
		t->appendChild(mp_keyIDTextNode);

	}

	else 
		mp_keyIDTextNode->setNodeValue(id);

	mp_keyID = mp_keyIDTextNode->getNodeValue();

}

void DSIGKeyInfoPGPData::setKeyPacket(const XMLCh * packet) {

	if (mp_keyInfoDOMNode == NULL) {

		throw XSECException(XSECException::KeyInfoError,
			"DSIGKeyInfoPGPData::setKeyID() called prior to load or createBlank");
	}

	if (mp_keyPacketTextNode == 0) {

		// Need to create

		safeBuffer str;
		DOMDocument *doc = mp_parentSignature->getParentDocument();
		const XMLCh * prefix = mp_parentSignature->getDSIGNSPrefix();

		makeQName(str, prefix, "PGPKeyPacket");
		DOMElement * t = doc->createElementNS(DSIGConstants::s_unicodeStrURIDSIG, str.rawXMLChBuffer());
		mp_keyInfoDOMNode->appendChild(t);
		mp_keyInfoDOMNode->appendChild(doc->createTextNode(DSIGConstants::s_unicodeStrNL));
		mp_keyPacketTextNode = doc->createTextNode(packet);
		t->appendChild(mp_keyPacketTextNode);

	}

	else 
		mp_keyPacketTextNode->setNodeValue(packet);

	mp_keyPacket = mp_keyPacketTextNode->getNodeValue();

}

