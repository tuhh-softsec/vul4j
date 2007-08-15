/*
 * Copyright 2002-2005 The Apache Software Foundation.
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
 * limitations under the License.
 */

/*
 * XSEC
 *
 * DSIGKeyInfoValue := A value setting
 *
 * $Id$
 *
 */

#include <xsec/dsig/DSIGKeyInfoValue.hpp>
#include <xsec/utils/XSECPlatformUtils.hpp>
#include <xsec/utils/XSECDOMUtils.hpp>
#include <xsec/framework/XSECException.hpp>
#include <xsec/dsig/DSIGSignature.hpp>
#include <xsec/framework/XSECEnv.hpp>

XERCES_CPP_NAMESPACE_USE

DSIGKeyInfoValue::DSIGKeyInfoValue(const XSECEnv * env, DOMNode *valueNode) :
DSIGKeyInfo(env),
mp_PTextNode(0),
mp_QTextNode(0),
mp_GTextNode(0),
mp_YTextNode(0),
mp_modulusTextNode(0),
mp_exponentTextNode(0),
m_keyInfoType(KEYINFO_NOTSET) {

		mp_keyInfoDOMNode = valueNode;
}

DSIGKeyInfoValue::DSIGKeyInfoValue(const XSECEnv * env) :
DSIGKeyInfo(env),
mp_PTextNode(0),
mp_QTextNode(0),
mp_GTextNode(0),
mp_YTextNode(0),
mp_modulusTextNode(0),
mp_exponentTextNode(0),
m_keyInfoType(KEYINFO_NOTSET) {

	mp_keyInfoDOMNode = NULL;
}

DSIGKeyInfoValue::~DSIGKeyInfoValue() {


}

// --------------------------------------------------------------------------------
//           Load from XML
// --------------------------------------------------------------------------------

void DSIGKeyInfoValue::load(void) {

	if (mp_keyInfoDOMNode == NULL || !strEquals(getDSIGLocalName(mp_keyInfoDOMNode), "KeyValue")) {

		throw XSECException(XSECException::ExpectedDSIGChildNotFound,
			"Empty or incorrect node passed to DSIGKeyInfoValue");

	}

	DOMNode *child, *p, *val;

	child = mp_keyInfoDOMNode->getFirstChild();
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

const XMLCh* DSIGKeyInfoValue::getRSAModulus(void) const {

	if (m_keyInfoType != KEYINFO_VALUE_RSA) {

		throw XSECException(XSECException::KeyInfoError,
			"Attempt to Get an RSA Modulus from a non-RSAValue KeyValue node");

	}

	if (mp_modulusTextNode != NULL)
		return mp_modulusTextNode->getNodeValue();

	return NULL;

}

const XMLCh * DSIGKeyInfoValue::getRSAExponent(void) const {

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
	DOMDocument *doc = mp_env->getParentDocument();
	const XMLCh * prefix = mp_env->getDSIGNSPrefix();

	makeQName(str, prefix, "KeyValue");

	DOMElement *ret = doc->createElementNS(DSIGConstants::s_unicodeStrURIDSIG, str.rawXMLChBuffer());
	mp_keyInfoDOMNode = ret;

	makeQName(str, prefix, "DSAKeyValue");
	DOMElement * dsa = doc->createElementNS(DSIGConstants::s_unicodeStrURIDSIG, str.rawXMLChBuffer());
	mp_env->doPrettyPrint(ret);
	ret->appendChild(dsa);
	mp_env->doPrettyPrint(dsa);
	mp_env->doPrettyPrint(ret);

	// Now create the value children

	makeQName(str, prefix, "P");
	DOMElement * v = doc->createElementNS(DSIGConstants::s_unicodeStrURIDSIG, str.rawXMLChBuffer());
	mp_PTextNode = doc->createTextNode(P);
	dsa->appendChild(v);
	mp_env->doPrettyPrint(dsa);
	v->appendChild(mp_PTextNode);

	makeQName(str, prefix, "Q");
	v = doc->createElementNS(DSIGConstants::s_unicodeStrURIDSIG, str.rawXMLChBuffer());
	mp_PTextNode = doc->createTextNode(Q);
	dsa->appendChild(v);
	mp_env->doPrettyPrint(dsa);
	v->appendChild(mp_PTextNode);

	makeQName(str, prefix, "G");
	v = doc->createElementNS(DSIGConstants::s_unicodeStrURIDSIG, str.rawXMLChBuffer());
	mp_PTextNode = doc->createTextNode(G);
	dsa->appendChild(v);
	mp_env->doPrettyPrint(dsa);
	v->appendChild(mp_PTextNode);

	makeQName(str, prefix, "Y");
	v = doc->createElementNS(DSIGConstants::s_unicodeStrURIDSIG, str.rawXMLChBuffer());
	mp_PTextNode = doc->createTextNode(Y);
	dsa->appendChild(v);
	mp_env->doPrettyPrint(dsa);
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
	DOMDocument *doc = mp_env->getParentDocument();
	const XMLCh * prefix = mp_env->getDSIGNSPrefix();

	makeQName(str, prefix, "KeyValue");

	DOMElement *ret = doc->createElementNS(DSIGConstants::s_unicodeStrURIDSIG, str.rawXMLChBuffer());
	mp_keyInfoDOMNode = ret;

	makeQName(str, prefix, "RSAKeyValue");
	DOMElement * rsa = doc->createElementNS(DSIGConstants::s_unicodeStrURIDSIG, str.rawXMLChBuffer());
	mp_env->doPrettyPrint(ret);
	ret->appendChild(rsa);
	mp_env->doPrettyPrint(rsa);
	mp_env->doPrettyPrint(ret);

	// Now create the value children

	makeQName(str, prefix, "Modulus");
	DOMElement * v = doc->createElementNS(DSIGConstants::s_unicodeStrURIDSIG, str.rawXMLChBuffer());
	mp_modulusTextNode = doc->createTextNode(modulus);
	rsa->appendChild(v);
	mp_env->doPrettyPrint(rsa);
	v->appendChild(mp_modulusTextNode);

	makeQName(str, prefix, "Exponent");
	v = doc->createElementNS(DSIGConstants::s_unicodeStrURIDSIG, str.rawXMLChBuffer());
	mp_exponentTextNode = doc->createTextNode(exponent);
	rsa->appendChild(v);
	mp_env->doPrettyPrint(rsa);
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

DSIGKeyInfo::keyInfoType DSIGKeyInfoValue::getKeyInfoType(void) const {

	return m_keyInfoType;

}

const XMLCh * DSIGKeyInfoValue::getKeyName(void) const {

	return DSIGConstants::s_unicodeStrEmpty;

}

