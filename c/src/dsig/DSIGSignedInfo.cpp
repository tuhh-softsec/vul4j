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
 * DSIGSignedInfo := Class for checking and setting up signed Info nodes in a DSIG signature
 *
 * $Id$
 *
 */

// XSEC Includes
#include <xsec/dsig/DSIGSignedInfo.hpp>
#include <xsec/dsig/DSIGReference.hpp>
#include <xsec/framework/XSECError.hpp>
#include <xsec/framework/XSECEnv.hpp>
#include <xsec/utils/XSECDOMUtils.hpp>
#include <xsec/dsig/DSIGSignature.hpp>

#include <xercesc/util/Janitor.hpp>

XERCES_CPP_NAMESPACE_USE

// Constructors and Destructors

DSIGSignedInfo::DSIGSignedInfo(DOMDocument *doc, 
		XSECSafeBufferFormatter * pFormatter, 
		DOMNode *signedInfoNode, const XSECEnv * env) {

	mp_doc = doc;
	m_HMACOutputLength = 0;
	mp_formatter = pFormatter;
	mp_signedInfoNode = signedInfoNode;
	m_signatureMethod = SIGNATURE_NONE;
	mp_env = env;
	mp_referenceList = NULL;
	m_loaded = false;

}

DSIGSignedInfo::DSIGSignedInfo(DOMDocument *doc, 
		XSECSafeBufferFormatter * pFormatter, 
		const XSECEnv * env) {

	mp_doc = doc;
	m_HMACOutputLength = 0;
	mp_formatter = pFormatter;
	mp_signedInfoNode = NULL;
	m_signatureMethod = SIGNATURE_NONE;
	mp_env = env;
	mp_referenceList = NULL;
	m_loaded = false;

}

DSIGSignedInfo::~DSIGSignedInfo() {

	mp_formatter = NULL;
	mp_env = NULL;
	if (mp_referenceList != NULL) {

		delete mp_referenceList;
		mp_referenceList = NULL;

	}

}

signatureMethod DSIGSignedInfo::getSignatureMethod(void) {

	return m_signatureMethod;

}

DOMNode * DSIGSignedInfo::getDOMNode() {

	return mp_signedInfoNode;

}

canonicalizationMethod DSIGSignedInfo::getCanonicalizationMethod(void) {

	return m_canonicalizationMethod;

}

hashMethod DSIGSignedInfo::getHashMethod() {

	return m_hashMethod;

}

int DSIGSignedInfo::getHMACOutputLength() {

	return m_HMACOutputLength;

}




// --------------------------------------------------------------------------------
//           Verify each reference element
// --------------------------------------------------------------------------------


bool DSIGSignedInfo::verify(safeBuffer &errStr) {

	return DSIGReference::verifyReferenceList(mp_referenceList, errStr);

}

// --------------------------------------------------------------------------------
//           Calculate and set hash values for each reference element
// --------------------------------------------------------------------------------

void DSIGSignedInfo::hash(void) {

	DSIGReference::hashReferenceList(mp_referenceList);

}

// --------------------------------------------------------------------------------
//           Create an empty reference in the signed info
// --------------------------------------------------------------------------------


DSIGReference * DSIGSignedInfo::createReference(const XMLCh * URI, 
								hashMethod hm, 
								char * type) {

	DSIGReference * ref;
	XSECnew(ref, DSIGReference(mp_env));
	Janitor<DSIGReference> j_ref(ref);

	DOMNode *refNode = ref->createBlankReference(URI, hm, type);

	// Add the node to the end of the childeren
	mp_signedInfoNode->appendChild(refNode);
	mp_env->doPrettyPrint(mp_signedInfoNode);

	// Add to the reference List
	j_ref.release();
	mp_referenceList->addReference(ref);

	return ref;
}

// --------------------------------------------------------------------------------
//           Create an empty SignedInfo
// --------------------------------------------------------------------------------

DOMElement *DSIGSignedInfo::createBlankSignedInfo(canonicalizationMethod cm,
			signatureMethod	sm,
			hashMethod hm) {

	safeBuffer str;
	const XMLCh * prefixNS = mp_env->getDSIGNSPrefix();

	makeQName(str, prefixNS, "SignedInfo");

	DOMElement *ret = mp_doc->createElementNS(DSIGConstants::s_unicodeStrURIDSIG, 
								str.rawXMLChBuffer());
	
	mp_signedInfoNode = ret;

	// Now create the algorithm parts
	m_canonicalizationMethod = cm;
	m_signatureMethod = sm;
	m_hashMethod = hm;

	// Canonicalisation

	DOMElement *canMeth = mp_doc->createElementNS(DSIGConstants::s_unicodeStrURIDSIG, 
			makeQName(str, prefixNS, "CanonicalizationMethod").rawXMLChBuffer());

	mp_env->doPrettyPrint(mp_signedInfoNode);
	mp_signedInfoNode->appendChild(canMeth);
	mp_env->doPrettyPrint(mp_signedInfoNode);

	if (!canonicalizationMethod2URI(str, cm)) {
	
		throw XSECException(XSECException::SignatureCreationError,
			"Attempt to use undefined Canonicalisation Algorithm in SignedInfo Creation");

	}

	canMeth->setAttribute(DSIGConstants::s_unicodeStrAlgorithm, str.sbStrToXMLCh());

	// Now the SignatureMethod

	DOMElement *sigMeth = mp_doc->createElementNS(DSIGConstants::s_unicodeStrURIDSIG, 
			makeQName(str, prefixNS, "SignatureMethod").rawXMLChBuffer());

	mp_signedInfoNode->appendChild(sigMeth);
	mp_env->doPrettyPrint(mp_signedInfoNode);

	if (!signatureHashMethod2URI(str, sm, hm)) {
	
		throw XSECException(XSECException::SignatureCreationError,
			"Attempt to use undefined Signature/Algorithm combination in SignedInfo Creation");

	}

	sigMeth->setAttribute(DSIGConstants::s_unicodeStrAlgorithm, str.sbStrToXMLCh());

	// Create an empty reference list

	XSECnew(mp_referenceList, DSIGReferenceList());

	return ret;

}

// --------------------------------------------------------------------------------
//           Load the SignedInfo
// --------------------------------------------------------------------------------

void DSIGSignedInfo::load(void) {


	if (mp_signedInfoNode == 0) {

		// Attempt to load an empty signature element
		throw XSECException(XSECException::LoadEmptySignedInfo);

	}

	if (!strEquals(getDSIGLocalName(mp_signedInfoNode), "SignedInfo")) {

		throw XSECException(XSECException::LoadNonSignedInfo);

	}

	DOMNode * tmpSI = mp_signedInfoNode->getFirstChild();

	// Check for CanonicalizationMethod

	while (tmpSI != 0 && (tmpSI->getNodeType() != DOMNode::ELEMENT_NODE))
		// Skip text and comments
		tmpSI = tmpSI->getNextSibling();

	if (tmpSI == 0 || !strEquals(getDSIGLocalName(tmpSI), "CanonicalizationMethod")) {

		throw XSECException(XSECException::ExpectedDSIGChildNotFound, 
				"Expected <CanonicalizationMethod> as first child of <SignedInfo>");

	}

	// Determine what the canonicalization method is
	DOMNamedNodeMap *tmpAtts = tmpSI->getAttributes();

	DOMNode *algorithm = tmpAtts->getNamedItem(DSIGConstants::s_unicodeStrAlgorithm);

	if (algorithm == 0) {

		throw XSECException(XSECException::ExpectedDSIGChildNotFound,
					"Expected Algorithm attribute in <CanonicalizationMethod>");

	}

	safeBuffer tmpSB;

	tmpSB << (*mp_formatter << algorithm->getNodeValue());

	if (tmpSB.sbStrcmp(URI_ID_C14N_NOC) == 0) {

		m_canonicalizationMethod = CANON_C14N_NOC;

	}

	else if (tmpSB.sbStrcmp(URI_ID_C14N_COM) == 0) {

		m_canonicalizationMethod = CANON_C14N_COM;

	}

	else if (tmpSB.sbStrcmp(URI_ID_EXC_C14N_COM) == 0) {

		m_canonicalizationMethod = CANON_C14NE_COM;

	}

	else if (tmpSB.sbStrcmp(URI_ID_EXC_C14N_NOC) == 0) {

		m_canonicalizationMethod = CANON_C14NE_NOC;

	}

	else

		throw XSECException(XSECException::UnknownCanonicalization);

	// Now load the SignatureMethod

	tmpSI = tmpSI->getNextSibling();

	while (tmpSI != 0 && (tmpSI->getNodeType() != DOMNode::ELEMENT_NODE))
		// Skip text and comments
		tmpSI = tmpSI->getNextSibling();

	if (tmpSI == 0 || !strEquals(getDSIGLocalName(tmpSI), "SignatureMethod")) {

		throw XSECException(XSECException::ExpectedDSIGChildNotFound, 
				"Expected <SignatureMethod> as child of <SignedInfo>");
	}


	// Determine the algorithms used to sign this document

	tmpAtts = tmpSI->getAttributes();

	algorithm = tmpAtts->getNamedItem(DSIGConstants::s_unicodeStrAlgorithm);
	
	if (algorithm == 0) {

		throw XSECException(XSECException::ExpectedDSIGChildNotFound,
					"Expected Algorithm attribute in <SignatureMethod>");

	}


	tmpSB << (*mp_formatter << algorithm->getNodeValue());

	if (tmpSB.sbStrcmp(URI_ID_DSA_SHA1) == 0) {

		m_signatureMethod = SIGNATURE_DSA;
		m_hashMethod = HASH_SHA1;

	}

	else if (tmpSB.sbStrcmp(URI_ID_RSA_SHA1) == 0) {

		m_signatureMethod = SIGNATURE_RSA;
		m_hashMethod = HASH_SHA1;

	}

	else if (tmpSB.sbStrcmp(URI_ID_HMAC_SHA1) == 0) {

		m_signatureMethod = SIGNATURE_HMAC;
		m_hashMethod = HASH_SHA1;

		// Check to see if there is a maximum output value

		DOMNode *tmpSOV = tmpSI->getFirstChild();
		while (tmpSOV != NULL && 
			tmpSOV->getNodeType() != DOMNode::ELEMENT_NODE && 
			!strEquals(getDSIGLocalName(tmpSOV), "HMACOutputLength"))
			tmpSOV = tmpSOV->getNextSibling();

		if (tmpSOV != NULL) {

			// Have a max output value!
			tmpSOV = tmpSOV->getFirstChild();
			while (tmpSOV != NULL && tmpSOV->getNodeType() != DOMNode::TEXT_NODE)
				tmpSOV = tmpSOV->getNextSibling();

			if (tmpSOV != NULL) {

				safeBuffer val;
				val << (*mp_formatter << tmpSOV->getNodeValue());
				m_HMACOutputLength = atoi((char *) val.rawBuffer());

			}
		}
	
	}

	else

		throw XSECException(XSECException::UnknownSignatureAlgorithm);


	// Now look at references....

	tmpSI = tmpSI->getNextSibling();

	// Run through the rest of the elements until done

	while (tmpSI != 0 && (tmpSI->getNodeType() != DOMNode::ELEMENT_NODE))
		// Skip text and comments
		tmpSI = tmpSI->getNextSibling();

	if (tmpSI != NULL) {

		// Have an element node - should be a reference, so let's load the list

		mp_referenceList = DSIGReference::loadReferenceListFromXML(mp_env, tmpSI);

	}

}

