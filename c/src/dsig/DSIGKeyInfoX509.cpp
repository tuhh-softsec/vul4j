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
 * DSIGKeyX509 := A "Super" key that defines a certificate with a sub-key that defines
 *                the signing key
 *
 * Author(s): Berin Lautenbach
 *
 * $ID$
 *
 * $LOG$
 *
 */

#include <xsec/dsig/DSIGKeyInfoX509.hpp>
#include <xsec/framework/XSECError.hpp>
#include <xsec/utils/XSECPlatformUtils.hpp>
#include <xsec/utils/XSECDOMUtils.hpp>
#include <xsec/dsig/DSIGSignature.hpp>

// --------------------------------------------------------------------------------
//           Constructors and Destructors
// --------------------------------------------------------------------------------


DSIGKeyInfoX509::DSIGKeyInfoX509(DSIGSignature * sig, DOMNode *X509Data) :
DSIGKeyInfo(sig),
mp_X509IssuerName(NULL),
mp_X509SerialNumber(NULL),
mp_X509SubjectName(NULL),
mp_X509SubjectNameTextNode(0),
mp_X509IssuerNameTextNode(0),
mp_X509SerialNumberTextNode(0) {

	mp_keyInfoDOMNode = X509Data;
	m_X509List.clear();

}

DSIGKeyInfoX509::DSIGKeyInfoX509(DSIGSignature * sig) :
DSIGKeyInfo(sig),
mp_X509IssuerName(NULL),
mp_X509SerialNumber(NULL),
mp_X509SubjectName(NULL),
mp_X509SubjectNameTextNode(0),
mp_X509IssuerNameTextNode(0),
mp_X509SerialNumberTextNode(0) {

	mp_keyInfoDOMNode = 0;
	m_X509List.clear();

}


DSIGKeyInfoX509::~DSIGKeyInfoX509() {

	X509ListType::iterator i;

	for (i = m_X509List.begin(); i != m_X509List.end(); ++i) {

		delete (*i);

	}

	m_X509List.clear();
	
};

// --------------------------------------------------------------------------------
//           Load and Get
// --------------------------------------------------------------------------------


// Methods unique to DSIGKeyInfoX509

void DSIGKeyInfoX509::load(void) {

	// Assuming we have a valid DOM_Node to start with, load the signing key so that it can
	// be used later on

	if (mp_keyInfoDOMNode == NULL) {

		// Attempt to load an empty signature element
		throw XSECException(XSECException::LoadEmptyX509);

	}

	XSECSafeBufferFormatter *formatter = mp_parentSignature->getSBFormatter();

	if (!strEquals(getDSIGLocalName(mp_keyInfoDOMNode), "X509Data")) {

		throw XSECException(XSECException::LoadNonX509);

	}

	// Now check for an X509 Data Element we understand

	DOMNode *tmpElt = mp_keyInfoDOMNode->getFirstChild();
	DOMNode *child;	// Worker

	while (tmpElt != 0) {

		if (tmpElt->getNodeType() == DOMNode::ELEMENT_NODE) {

			// See if it's a known element type
			if (strEquals(getDSIGLocalName(tmpElt), "X509Certificate")) {

				X509Holder * h;

				DOMNode *certElt = findFirstChildOfType(tmpElt, DOMNode::TEXT_NODE);

				if (certElt != 0) {
	
					XSECnew(h, X509Holder);

					h->mp_encodedX509 = certElt->getNodeValue();

					// Add to the list
					
					m_X509List.push_back(h);

				}
			}

			else if (strEquals(getDSIGLocalName(tmpElt), "X509SubjectName")) {

				child = findFirstChildOfType(tmpElt, DOMNode::TEXT_NODE);

				if (child == NULL) {

					throw XSECException(XSECException::ExpectedDSIGChildNotFound,
						"Expected TEXT_NODE child of <X509SubjectName>");

				}

				mp_X509SubjectName = child->getNodeValue();

			}

			else if (strEquals(getDSIGLocalName(tmpElt), "X509IssuerSerial")) {

				child = tmpElt->getFirstChild();
				while (child != 0 && child->getNodeType() != DOMNode::ELEMENT_NODE &&
					!strEquals(getDSIGLocalName(child), "X509IssuerName"))
					child = child->getNextSibling();

				if (child == NULL) {

					throw XSECException(XSECException::ExpectedDSIGChildNotFound,
						"Expected <X509IssuerName> child of <X509IssuerSerial>");

				}

				child = child->getFirstChild();
				while (child != 0 && child->getNodeType() != DOMNode::TEXT_NODE)
					child = child->getNextSibling();

				if (child == NULL) {

					throw XSECException(XSECException::ExpectedDSIGChildNotFound,
						"Expected TEXT_NODE child of <X509IssuerSerial>");

				}

				mp_X509IssuerName = child->getNodeValue();

				// Now find the serial number
				child = tmpElt->getFirstChild();
				while (child != 0 && child->getNodeType() != DOMNode::ELEMENT_NODE &&
					!strEquals(getDSIGLocalName(child), "X509SerialNumber"))
					child = child->getNextSibling();

				if (child == NULL) {

					throw XSECException(XSECException::ExpectedDSIGChildNotFound,
						"Expected <X509SerialNumber> child of <X509IssuerSerial>");

				}

				child = child->getFirstChild();
				while (child != 0 && child->getNodeType() != DOMNode::TEXT_NODE)
					child = child->getNextSibling();

				if (child == NULL) {

					throw XSECException(XSECException::ExpectedDSIGChildNotFound,
						"Expected TEXT_NODE child of <X509IssuerSerial>");

				}

				mp_X509SerialNumber = child->getNodeValue();

			}
		}

		// Go to next data element to load if we understand

		tmpElt = tmpElt->getNextSibling();

	}

}

const XMLCh * DSIGKeyInfoX509::getKeyName(void) {

	return mp_X509SubjectName;

	// Should return DN

}

const XMLCh * DSIGKeyInfoX509::getX509SubjectName(void) {


	return mp_X509SubjectName;

}

const XMLCh * DSIGKeyInfoX509::getX509IssuerName(void) {

	return mp_X509IssuerName;

}

const XMLCh * DSIGKeyInfoX509::getX509IssuerSerialNumber(void) {

	return mp_X509SerialNumber;

}

int DSIGKeyInfoX509::getCertificateListSize(void) {

	return m_X509List.size();

}



const XMLCh * DSIGKeyInfoX509::getCertificateItem(int item) {

	if (item >=0 && item < m_X509List.size())
		return m_X509List[item]->mp_encodedX509;

	return 0;

}

// --------------------------------------------------------------------------------
//           Create and Set
// --------------------------------------------------------------------------------

DOMElement * DSIGKeyInfoX509::createBlankX509Data(void) {

	// Create the DOM Structure

	safeBuffer str;
	DOMDocument *doc = mp_parentSignature->getParentDocument();
	const XMLCh * prefix = mp_parentSignature->getDSIGNSPrefix();

	makeQName(str, prefix, "X509Data");

	DOMElement *ret = doc->createElementNS(DSIGConstants::s_unicodeStrURIDSIG, str.rawXMLChBuffer());
	mp_keyInfoDOMNode = ret;
	ret->appendChild(doc->createTextNode(DSIGConstants::s_unicodeStrNL));

	return ret;

}

void DSIGKeyInfoX509::setX509SubjectName(const XMLCh * name) {

	if (mp_X509SubjectNameTextNode == 0) {

		// Does not yet exist in the DOM

		safeBuffer str;
		DOMDocument *doc = mp_parentSignature->getParentDocument();
		const XMLCh * prefix = mp_parentSignature->getDSIGNSPrefix();

		makeQName(str, prefix, "X509SubjectName");

		DOMElement * s = doc->createElementNS(DSIGConstants::s_unicodeStrURIDSIG, str.rawXMLChBuffer());
		mp_X509SubjectNameTextNode = doc->createTextNode(name);
		s->appendChild(mp_X509SubjectNameTextNode);

		// Add to the over-arching node
		mp_keyInfoDOMNode->appendChild(s);
		mp_keyInfoDOMNode->appendChild(doc->createTextNode(DSIGConstants::s_unicodeStrNL));

	}

	else {

		mp_X509SubjectNameTextNode->setNodeValue(name);

	}

	mp_X509SubjectName = mp_X509SubjectNameTextNode->getNodeValue();

}

void DSIGKeyInfoX509::setX509IssuerSerial(const XMLCh * name, const XMLCh * serial) {

	if (mp_X509IssuerNameTextNode == 0) {

		// Does not yet exist in the DOM

		safeBuffer str;
		DOMDocument *doc = mp_parentSignature->getParentDocument();
		const XMLCh * prefix = mp_parentSignature->getDSIGNSPrefix();

		makeQName(str, prefix, "X509IssuerSerial");

		DOMElement * s = doc->createElementNS(DSIGConstants::s_unicodeStrURIDSIG, str.rawXMLChBuffer());
		s->appendChild(doc->createTextNode(DSIGConstants::s_unicodeStrNL));

		// Create the text nodes with the contents

		mp_X509IssuerNameTextNode = doc->createTextNode(name);
		mp_X509SerialNumberTextNode = doc->createTextNode(serial);
	
		// Create the sub elements

		makeQName(str, prefix, "X509IssuerName");
		DOMElement * t = doc->createElementNS(DSIGConstants::s_unicodeStrURIDSIG, str.rawXMLChBuffer());
		t->appendChild(mp_X509IssuerNameTextNode);
		
		s->appendChild(t);
		s->appendChild(doc->createTextNode(DSIGConstants::s_unicodeStrNL));
		
		makeQName(str, prefix, "X509SerialNumber");
		t = doc->createElementNS(DSIGConstants::s_unicodeStrURIDSIG, str.rawXMLChBuffer());
		t->appendChild(mp_X509SerialNumberTextNode);
		
		s->appendChild(t);
		s->appendChild(doc->createTextNode(DSIGConstants::s_unicodeStrNL));

		// Add to the over-arching X509Data

		mp_keyInfoDOMNode->appendChild(s);
		mp_keyInfoDOMNode->appendChild(doc->createTextNode(DSIGConstants::s_unicodeStrNL));

	}

	else {

		mp_X509IssuerNameTextNode->setNodeValue(name);
		mp_X509SerialNumberTextNode->setNodeValue(serial);

	}

	mp_X509IssuerName = mp_X509IssuerNameTextNode->getNodeValue();
	mp_X509SerialNumber = mp_X509SerialNumberTextNode->getNodeValue();

}


void DSIGKeyInfoX509::appendX509Certificate(const XMLCh * base64Certificate) {

	safeBuffer str;
	DOMDocument *doc = mp_parentSignature->getParentDocument();
	const XMLCh * prefix = mp_parentSignature->getDSIGNSPrefix();

	makeQName(str, prefix, "X509Certificate");

	DOMElement * s = doc->createElementNS(DSIGConstants::s_unicodeStrURIDSIG, str.rawXMLChBuffer());
	DOMNode * b64Txt = doc->createTextNode(base64Certificate);
	s->appendChild(b64Txt);

	mp_keyInfoDOMNode->appendChild(s);
	mp_keyInfoDOMNode->appendChild(doc->createTextNode(DSIGConstants::s_unicodeStrNL));

	// Add to the list

	X509Holder * h;
	XSECnew(h, X509Holder);
	h->mp_encodedX509 = b64Txt->getNodeValue();
	
}