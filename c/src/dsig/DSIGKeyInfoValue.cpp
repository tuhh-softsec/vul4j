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
 * DSIGKeyInfoValue := A value setting
 *
 * Author(s): Berin Lautenbach
 *
 * $ID$
 *
 * $LOG$
 *
 */

#include <xsec/dsig/DSIGKeyInfoValue.hpp>
#include <xsec/utils/XSECPlatformUtils.hpp>
#include <xsec/utils/XSECDOMUtils.hpp>
#include <xsec/framework/XSECException.hpp>
#include <xsec/dsig/DSIGSignature.hpp>

DSIGKeyInfoValue::DSIGKeyInfoValue(DSIGSignature * sig, DOMNode *valueNode) :
DSIGKeyInfo(sig),
mp_PTextNode(0),
mp_QTextNode(0),
mp_GTextNode(0),
mp_YTextNode(0),
mp_modulusTextNode(0),
mp_exponentTextNode(0),
mp_valueNode(valueNode),
m_keyInfoType(KEYINFO_NOTSET) {

}

DSIGKeyInfoValue::DSIGKeyInfoValue(DSIGSignature * sig) :
DSIGKeyInfo(sig),
mp_PTextNode(0),
mp_QTextNode(0),
mp_GTextNode(0),
mp_YTextNode(0),
mp_modulusTextNode(0),
mp_exponentTextNode(0),
mp_valueNode(0),
m_keyInfoType(KEYINFO_NOTSET) {

}

DSIGKeyInfoValue::~DSIGKeyInfoValue() {


}

// --------------------------------------------------------------------------------
//           Load from XML
// --------------------------------------------------------------------------------

void DSIGKeyInfoValue::load(void) {

	if (mp_valueNode == NULL || !strEquals(getDSIGLocalName(mp_valueNode), "KeyValue")) {

		throw XSECException(XSECException::ExpectedDSIGChildNotFound,
			"Empty or incorrect node passed to DSIGKeyInfoValue");

	}

	DOMNode *child, *p, *val;

	child = mp_valueNode->getFirstChild();
	while (child != NULL && child->getNodeType() != DOMNode::ELEMENT_NODE)
		child = child->getNextSibling();
	//child = findFirstChildOfType(mp_valueNode, DOMNode::ELEMENT_NODE);

	if (child == 0) {
		throw XSECException(XSECException::ExpectedDSIGChildNotFound,
			"Empty Expected value node beneath <KeyValue>");
	}

	if (strEquals(getDSIGLocalName(child), "DSAKeyValue")) {

		m_keyInfoType = KEYINFO_VALUE_DSA;

		p = findFirstChildOfType(child, DOMNode::ELEMENT_NODE);

		while (p != NULL) {

			if (strEquals(getDSIGLocalName(p), "P")) {
				val = findFirstChildOfType(p, DOMNode::TEXT_NODE);
				if (val != NULL) {
					mp_PTextNode = val;
				}
			}
			if (strEquals(getDSIGLocalName(p), "Q")) {
				val = findFirstChildOfType(p, DOMNode::TEXT_NODE);
				if (val != NULL) {
					mp_QTextNode = val;
				}
			}
			if (strEquals(getDSIGLocalName(p), "G")) {
				val = findFirstChildOfType(p, DOMNode::TEXT_NODE);
				if (val != NULL) {
					mp_GTextNode = val;
				}
			}
			if (strEquals(getDSIGLocalName(p), "Y")) {
				val = findFirstChildOfType(p, DOMNode::TEXT_NODE);
				if (val != NULL) {
					mp_YTextNode = val;
				}
			}

			p = p->getNextSibling();

		}
	}

	else if (strEquals(getDSIGLocalName(child), "RSAKeyValue")) {

		m_keyInfoType = KEYINFO_VALUE_RSA;

		p = findFirstChildOfType(child, DOMNode::ELEMENT_NODE);

		if (p == 0 || !strEquals(getDSIGLocalName(p), "Modulus")) {

			throw XSECException(XSECException::ExpectedDSIGChildNotFound,
				"Expected <Modulus> node beneath <RSAKeyValue>");

		}

		val = findFirstChildOfType(p, DOMNode::TEXT_NODE);

		if (val == 0) {

			throw XSECException(XSECException::ExpectedDSIGChildNotFound,
				"Expected a text node beneath <Modulus>");

		}
		
		mp_modulusTextNode = val;

		// Find the Exponent

		p = p->getNextSibling();

		while (p != 0 && p->getNodeType() != DOMNode::ELEMENT_NODE)
			p = p->getNextSibling();

		if (p == 0 || !strEquals(getDSIGLocalName(p), "Exponent")) {

			throw XSECException(XSECException::ExpectedDSIGChildNotFound,
				"Expected <Exponent> node beneath <RSAKeyValue>");

		}

		val = findFirstChildOfType(p, DOMNode::TEXT_NODE);

		if (val == 0) {

			throw XSECException(XSECException::ExpectedDSIGChildNotFound,
				"Expected a text node beneath <Exponent>");

		}
		
		mp_exponentTextNode = val;

	}

	else {

		throw XSECException(XSECException::UnknownKeyValue);

	}

}

// --------------------------------------------------------------------------------
//           Get RSA Values
// --------------------------------------------------------------------------------

const XMLCh* DSIGKeyInfoValue::getRSAModulus(void) {

	if (m_keyInfoType != KEYINFO_VALUE_RSA) {

		throw XSECException(XSECException::KeyInfoError,
			"Attempt to Get an RSA Modulus from a non-RSAValue KeyValue node");

	}

	if (mp_modulusTextNode != NULL)
		return mp_modulusTextNode->getNodeValue();

	return NULL;

}

const XMLCh * DSIGKeyInfoValue::getRSAExponent(void) {

	if (m_keyInfoType != KEYINFO_VALUE_RSA) {

		throw XSECException(XSECException::KeyInfoError,
			"Attempt to Get an RSA Exponent from a non-RSAValue KeyValue node");

	}

	if (mp_exponentTextNode != NULL)
		return mp_exponentTextNode->getNodeValue();

	return NULL;

}

// --------------------------------------------------------------------------------
//           Create and manipulate DSA Values
// --------------------------------------------------------------------------------

DOMElement * DSIGKeyInfoValue::createBlankDSAKeyValue(const XMLCh * P,
	const XMLCh * Q,
	const XMLCh * G,
	const XMLCh * Y) {

	// Set our type
	
	m_keyInfoType = KEYINFO_VALUE_DSA;

	// Create the DOM Structure

	safeBuffer str;
	DOMDocument *doc = mp_parentSignature->getParentDocument();
	const XMLCh * prefix = mp_parentSignature->getDSIGNSPrefix();

	makeQName(str, prefix, "KeyValue");

	DOMElement *ret = doc->createElementNS(DSIGConstants::s_unicodeStrURIDSIG, str.rawXMLChBuffer());
	mp_valueNode = ret;

	makeQName(str, prefix, "DSAKeyValue");
	DOMElement * dsa = doc->createElementNS(DSIGConstants::s_unicodeStrURIDSIG, str.rawXMLChBuffer());
	ret->appendChild(doc->createTextNode(DSIGConstants::s_unicodeStrNL));
	ret->appendChild(dsa);
	dsa->appendChild(doc->createTextNode(DSIGConstants::s_unicodeStrNL));
	ret->appendChild(doc->createTextNode(DSIGConstants::s_unicodeStrNL));

	// Now create the value children

	makeQName(str, prefix, "P");
	DOMElement * v = doc->createElementNS(DSIGConstants::s_unicodeStrURIDSIG, str.rawXMLChBuffer());
	mp_PTextNode = doc->createTextNode(P);
	dsa->appendChild(v);
	dsa->appendChild(doc->createTextNode(DSIGConstants::s_unicodeStrNL));
	v->appendChild(mp_PTextNode);

	makeQName(str, prefix, "Q");
	v = doc->createElementNS(DSIGConstants::s_unicodeStrURIDSIG, str.rawXMLChBuffer());
	mp_PTextNode = doc->createTextNode(Q);
	dsa->appendChild(v);
	dsa->appendChild(doc->createTextNode(DSIGConstants::s_unicodeStrNL));
	v->appendChild(mp_PTextNode);

	makeQName(str, prefix, "G");
	v = doc->createElementNS(DSIGConstants::s_unicodeStrURIDSIG, str.rawXMLChBuffer());
	mp_PTextNode = doc->createTextNode(G);
	dsa->appendChild(v);
	dsa->appendChild(doc->createTextNode(DSIGConstants::s_unicodeStrNL));
	v->appendChild(mp_PTextNode);

	makeQName(str, prefix, "Y");
	v = doc->createElementNS(DSIGConstants::s_unicodeStrURIDSIG, str.rawXMLChBuffer());
	mp_PTextNode = doc->createTextNode(Y);
	dsa->appendChild(v);
	dsa->appendChild(doc->createTextNode(DSIGConstants::s_unicodeStrNL));
	v->appendChild(mp_PTextNode);

	return ret;

}

void DSIGKeyInfoValue::setDSAP(const XMLCh * P) {

	if (m_keyInfoType != KEYINFO_VALUE_DSA) {

		throw XSECException(XSECException::KeyInfoError,
			"Attempt to set a DSA value in a non-DSAValue KeyValue node");

	}

	mp_PTextNode->setNodeValue(P);

}

void DSIGKeyInfoValue::setDSAQ(const XMLCh * Q) {

	if (m_keyInfoType != KEYINFO_VALUE_DSA) {

		throw XSECException(XSECException::KeyInfoError,
			"Attempt to set a DSA value in a non-DSAValue KeyValue node");

	}

	mp_QTextNode->setNodeValue(Q);

}

void DSIGKeyInfoValue::setDSAG(const XMLCh * G) {

	if (m_keyInfoType != KEYINFO_VALUE_DSA) {

		throw XSECException(XSECException::KeyInfoError,
			"Attempt to set a DSA value in a non-DSAValue KeyValue node");

	}

	mp_GTextNode->setNodeValue(G);

}

void DSIGKeyInfoValue::setDSAY(const XMLCh * Y) {

	if (m_keyInfoType != KEYINFO_VALUE_DSA) {

		throw XSECException(XSECException::KeyInfoError,
			"Attempt to set a DSA value in a non-DSAValue KeyValue node");

	}

	mp_YTextNode->setNodeValue(Y);

}

// --------------------------------------------------------------------------------
//           Create and manipulate RSA Values
// --------------------------------------------------------------------------------

DOMElement * DSIGKeyInfoValue::createBlankRSAKeyValue(const XMLCh * modulus,
													  const XMLCh * exponent) {

	// Set our type
	
	m_keyInfoType = KEYINFO_VALUE_RSA;

	// Create the DOM Structure

	safeBuffer str;
	DOMDocument *doc = mp_parentSignature->getParentDocument();
	const XMLCh * prefix = mp_parentSignature->getDSIGNSPrefix();

	makeQName(str, prefix, "KeyValue");

	DOMElement *ret = doc->createElementNS(DSIGConstants::s_unicodeStrURIDSIG, str.rawXMLChBuffer());
	mp_valueNode = ret;

	makeQName(str, prefix, "RSAKeyValue");
	DOMElement * rsa = doc->createElementNS(DSIGConstants::s_unicodeStrURIDSIG, str.rawXMLChBuffer());
	ret->appendChild(doc->createTextNode(DSIGConstants::s_unicodeStrNL));
	ret->appendChild(rsa);
	rsa->appendChild(doc->createTextNode(DSIGConstants::s_unicodeStrNL));
	ret->appendChild(doc->createTextNode(DSIGConstants::s_unicodeStrNL));

	// Now create the value children

	makeQName(str, prefix, "Modulus");
	DOMElement * v = doc->createElementNS(DSIGConstants::s_unicodeStrURIDSIG, str.rawXMLChBuffer());
	mp_modulusTextNode = doc->createTextNode(modulus);
	rsa->appendChild(v);
	rsa->appendChild(doc->createTextNode(DSIGConstants::s_unicodeStrNL));
	v->appendChild(mp_modulusTextNode);

	makeQName(str, prefix, "Exponent");
	v = doc->createElementNS(DSIGConstants::s_unicodeStrURIDSIG, str.rawXMLChBuffer());
	mp_exponentTextNode = doc->createTextNode(exponent);
	rsa->appendChild(v);
	rsa->appendChild(doc->createTextNode(DSIGConstants::s_unicodeStrNL));
	v->appendChild(mp_exponentTextNode);

	return ret;

}

void DSIGKeyInfoValue::setRSAModulus(const XMLCh * modulus) {

	if (m_keyInfoType != KEYINFO_VALUE_RSA) {

		throw XSECException(XSECException::KeyInfoError,
			"Attempt to set an RSA Modulus from a non-RSAValue KeyValue node");

	}

	mp_modulusTextNode->setNodeValue(modulus);

}

void DSIGKeyInfoValue::setRSAExponent(const XMLCh * exponent) {

	if (m_keyInfoType != KEYINFO_VALUE_RSA) {

		throw XSECException(XSECException::KeyInfoError,
			"Attempt to set an RSA Exponent from a non-RSAValue KeyValue node");

	}

	mp_exponentTextNode->setNodeValue(exponent);

}

// --------------------------------------------------------------------------------
//           Other interface functions
// --------------------------------------------------------------------------------

DSIGKeyInfo::keyInfoType DSIGKeyInfoValue::getKeyInfoType(void) {

	return m_keyInfoType;

}

const XMLCh * DSIGKeyInfoValue::getKeyName(void) {

	return DSIGConstants::s_unicodeStrEmpty;

}

