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
 * DSIGSignedInfo := Class for checking and setting up signed Info nodes in a DSIG signature
 *
 * Author(s): Berin Lautenbach
 *
 * $ID$
 *
 * $LOG$
 *
 */

// XSEC Includes
#include <xsec/dsig/DSIGSignedInfo.hpp>
#include <xsec/dsig/DSIGReference.hpp>
#include <xsec/framework/XSECError.hpp>
#include <xsec/utils/XSECDOMUtils.hpp>
#include <xsec/dsig/DSIGSignature.hpp>

#include <xercesc/util/Janitor.hpp>

XSEC_USING_XERCES(DOMNamedNodeMap);
XSEC_USING_XERCES(Janitor);

// Constructors and Destructors

DSIGSignedInfo::DSIGSignedInfo(DOMDocument *doc, 
		XSECSafeBufferFormatter * pFormatter, 
		DOMNode *signedInfoNode, DSIGSignature * parentSignature) {

	mp_doc = doc;
	m_HMACOutputLength = 0;
	mp_formatter = pFormatter;
	mp_signedInfoNode = signedInfoNode;
	m_signatureMethod = SIGNATURE_NONE;
	mp_parentSignature = parentSignature;
	mp_referenceList = NULL;
	m_loaded = false;

}

DSIGSignedInfo::DSIGSignedInfo(DOMDocument *doc, 
		XSECSafeBufferFormatter * pFormatter, 
		DSIGSignature * parentSignature) {

	mp_doc = doc;
	m_HMACOutputLength = 0;
	mp_formatter = pFormatter;
	mp_signedInfoNode = NULL;
	m_signatureMethod = SIGNATURE_NONE;
	mp_parentSignature = parentSignature;
	mp_referenceList = NULL;
	m_loaded = false;

}

DSIGSignedInfo::~DSIGSignedInfo() {

	mp_formatter = NULL;
	mp_parentSignature = NULL;
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
	XSECnew(ref, DSIGReference(mp_parentSignature));
	Janitor<DSIGReference> j_ref(ref);

	DOMNode *refNode = ref->createBlankReference(URI, hm, type);

	// Add the node to the end of the childeren
	mp_signedInfoNode->appendChild(refNode);
	mp_signedInfoNode->appendChild(mp_doc->createTextNode(DSIGConstants::s_unicodeStrNL));

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
	const XMLCh * prefixNS = mp_parentSignature->getDSIGNSPrefix();

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

	mp_signedInfoNode->appendChild(mp_doc->createTextNode(DSIGConstants::s_unicodeStrNL));
	mp_signedInfoNode->appendChild(canMeth);
	mp_signedInfoNode->appendChild(mp_doc->createTextNode(DSIGConstants::s_unicodeStrNL));

	if (!canonicalizationMethod2URI(str, cm)) {
	
		throw XSECException(XSECException::SignatureCreationError,
			"Attempt to use undefined Canonicalisation Algorithm in SignedInfo Creation");

	}

	canMeth->setAttribute(DSIGConstants::s_unicodeStrAlgorithm, str.sbStrToXMLCh());

	// Now the SignatureMethod

	DOMElement *sigMeth = mp_doc->createElementNS(DSIGConstants::s_unicodeStrURIDSIG, 
			makeQName(str, prefixNS, "SignatureMethod").rawXMLChBuffer());

	mp_signedInfoNode->appendChild(sigMeth);
	mp_signedInfoNode->appendChild(mp_doc->createTextNode(DSIGConstants::s_unicodeStrNL));

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

		mp_referenceList = DSIGReference::loadReferenceListFromXML(mp_parentSignature, tmpSI);

	}

}

