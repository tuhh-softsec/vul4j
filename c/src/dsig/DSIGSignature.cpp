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
 * DSIGSignature := Class for checking and setting up signature nodes in a DSIG signature					 
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */

// XSEC Includes
#include <xsec/dsig/DSIGSignature.hpp>
#include <xsec/dsig/DSIGConstants.hpp>
#include <xsec/dsig/DSIGKeyInfoX509.hpp>
#include <xsec/dsig/DSIGReference.hpp>
#include <xsec/dsig/DSIGTransformList.hpp>
#include <xsec/transformers/TXFMDocObject.hpp>
#include <xsec/transformers/TXFMOutputFile.hpp>
#include <xsec/transformers/TXFMSHA1.hpp>
#include <xsec/transformers/TXFMBase64.hpp>
#include <xsec/transformers/TXFMC14n.hpp>
#include <xsec/transformers/TXFMChain.hpp>
#include <xsec/framework/XSECError.hpp>
#include <xsec/enc/XSECCryptoKeyDSA.hpp>
#include <xsec/enc/XSECCryptoKeyRSA.hpp>
#include <xsec/utils/XSECDOMUtils.hpp>
#include <xsec/utils/XSECBinTXFMInputStream.hpp>
#include <xsec/framework/XSECURIResolver.hpp>
#include <xsec/enc/XSECKeyInfoResolver.hpp>
#include <xsec/dsig/DSIGKeyInfoValue.hpp>
#include <xsec/dsig/DSIGKeyInfoX509.hpp>
#include <xsec/dsig/DSIGKeyInfoName.hpp>

// Xerces includes

#include <xercesc/dom/DOMNamedNodeMap.hpp>
#include <xercesc/util/Janitor.hpp>

XSEC_USING_XERCES(DOMNamedNodeMap);
XSEC_USING_XERCES(Janitor);

// --------------------------------------------------------------------------------
//           Some useful utility functions
// --------------------------------------------------------------------------------


bool compareBase64StringToRaw(safeBuffer &b64SB, 
							  unsigned char * raw, 
							  unsigned int rawLen, 
							  unsigned int maxCompare = 0) {
	// Decode a base64 buffer and then compare the result to a raw buffer
	// Compare at most maxCompare bits (if maxComare > 0)
	// Note - whilst the other parameters are bytes, maxCompare is bits

	unsigned char outputStr[1024];
	unsigned char b64Str[1024];
	unsigned int outputLen = 0;
	
	XSECCryptoBase64 * b64 = XSECPlatformUtils::g_cryptoProvider->base64();
	
	if (!b64) {

		throw XSECException(XSECException::CryptoProviderError, 
				"Error requesting Base64 object from Crypto Provider");

	}

	Janitor<XSECCryptoBase64> j_b64(b64);

	strncpy((char *) b64Str, (char *) b64SB.rawBuffer(), 1023);
	b64Str[1023] = '\0';	// Just in case

	b64->decodeInit();
	outputLen = b64->decode((unsigned char *) b64Str, strlen((char *) b64Str), outputStr, 1024);
	outputLen += b64->decodeFinish(&outputStr[outputLen], 1024 - outputLen);

	// Compare

	div_t d;
	unsigned int maxCompareBytes, maxCompareBits;
	maxCompareBits = 0;

	unsigned int size;

	if (maxCompare > 0) {
		d = div(maxCompare, 8);
		maxCompareBytes = d.quot;
		if (d.rem != 0)
			maxCompareBytes++;

		if (rawLen < maxCompareBytes && outputLen < maxCompareBytes) {
			if (rawLen != outputLen)
				return false;
			size = rawLen;
		}
		else if (rawLen < maxCompareBytes || outputLen < maxCompareBytes) {
			return false;
		}
		else
			size = maxCompareBytes;
	}
	else {

		if (rawLen != outputLen)
			return false;

		size = rawLen;

	}

	// Compare bytes
	unsigned int i, j;
	for (i = 0; i < size; ++ i) {
		if (raw[i] != outputStr[i])
			return false;
	}

	// Compare bits

	char mask = 0x01;
	if (maxCompare != 0) {
	    for (j = 0 ; j < (unsigned int) d.rem; ++i) {

		    if ((raw[i] & mask) != (outputStr[i] & mask))
			    return false;

			mask = mask << 1;
		}
	}

	return true;

}


void convertRawToBase64String(safeBuffer &b64SB, 
							  unsigned char * raw, 
							  unsigned int rawLen, 
							  unsigned int maxBits = 0) {

	// Translate the rawbuffer (at most maxBits or rawLen - whichever is smaller)
	// to a base64 string

	unsigned char b64Str[1024];
	unsigned int outputLen = 0;
	
	XSECCryptoBase64 * b64 = XSECPlatformUtils::g_cryptoProvider->base64();
	
	if (!b64) {

		throw XSECException(XSECException::CryptoProviderError, 
				"Error requesting Base64 object from Crypto Provider");

	}

	Janitor<XSECCryptoBase64> j_b64(b64);

	// Determine length to translate
	unsigned int size;

	if (maxBits > 0) {
		div_t d = div(maxBits, 8);
		size = d.quot;
		if (d.rem != 0)
			++size;
		
		if (size > rawLen)
			size = rawLen;
	}

	else
		size = rawLen;

	b64->encodeInit();
	outputLen = b64->encode((unsigned char *) raw, rawLen, b64Str, 1024);
	outputLen += b64->encodeFinish(&b64Str[outputLen], 1024 - outputLen);
	b64Str[outputLen] = '\0';

	// Copy out

	b64SB.sbStrcpyIn((char *) b64Str);

}
// --------------------------------------------------------------------------------
//           Get the Canonicalised BYTE_STREAM of the SignedInfo
// --------------------------------------------------------------------------------

XSECBinTXFMInputStream * DSIGSignature::makeBinInputStream(void) const {

	TXFMBase * txfm;

	// Create the starting point for the transform list

	XSECnew(txfm, TXFMDocObject(mp_doc));

	TXFMChain * chain;
	XSECnew(chain, TXFMChain(txfm));
	Janitor<TXFMChain> j_chain(chain);

	((TXFMDocObject *) txfm)->setInput(mp_doc, mp_signedInfo->getDOMNode());
	
	// canonicalise the SignedInfo content

	switch (mp_signedInfo->getCanonicalizationMethod()) {

	case CANON_C14N_NOC :

		XSECnew(txfm, TXFMC14n(mp_doc));
		chain->appendTxfm(txfm);
		txfm->stripComments();
		
		break;

	case CANON_C14N_COM :

		XSECnew(txfm, TXFMC14n(mp_doc));
		chain->appendTxfm(txfm);
		txfm->activateComments();

		break;

	case CANON_C14NE_NOC :

		XSECnew(txfm, TXFMC14n(mp_doc));
		chain->appendTxfm(txfm);
		((TXFMC14n *) txfm)->setExclusive();
		txfm->stripComments();
		
		break;

	case CANON_C14NE_COM :

		XSECnew(txfm, TXFMC14n(mp_doc));
		chain->appendTxfm(txfm);
		((TXFMC14n *) txfm)->setExclusive();
		txfm->activateComments();

		break;

	default :

		throw XSECException(XSECException::SigVfyError,
			"Canonicalisation method unknown in DSIGSignature::makeBinInputStream()");

	}

	// Now create the InputStream

	XSECBinTXFMInputStream * ret;

	XSECnew(ret, XSECBinTXFMInputStream(chain));
	j_chain.release();

	return ret;

}

// --------------------------------------------------------------------------------
//           Get the list of references
// --------------------------------------------------------------------------------

DSIGReferenceList * DSIGSignature::getReferenceList(void) {

	return mp_signedInfo->getReferenceList();

}

// --------------------------------------------------------------------------------
//           Set and Get Resolvers
// --------------------------------------------------------------------------------


void DSIGSignature::setURIResolver(XSECURIResolver * resolver) {

	if (mp_URIResolver != 0)
		delete mp_URIResolver;

	mp_URIResolver = resolver->clone();

}

XSECURIResolver * DSIGSignature::getURIResolver(void) {

	return mp_URIResolver;

}

void DSIGSignature::setKeyInfoResolver(XSECKeyInfoResolver * resolver) {

	if (mp_KeyInfoResolver != 0)
		delete mp_KeyInfoResolver;

	mp_KeyInfoResolver = resolver->clone();

}

XSECKeyInfoResolver * DSIGSignature::getKeyInfoResolver(void) {

	return mp_KeyInfoResolver;

}


// --------------------------------------------------------------------------------
//           Signature
// --------------------------------------------------------------------------------

// Constructors and Destructors

DSIGSignature::DSIGSignature(DOMDocument *doc, DOMNode *sigNode) :
m_keyInfoList(0),
m_errStr("") {

	mp_doc = doc;
	mp_sigNode = sigNode;
	mp_signingKey = NULL;
	mp_prefixNS = NULL;
	mp_URIResolver = NULL;
	mp_KeyInfoResolver = NULL;
	mp_KeyInfoNode = NULL;
	m_loaded = false;

	m_keyInfoList.setParentSignature(this);

	// Set up our formatter
	XSECnew(mp_formatter, XSECSafeBufferFormatter("UTF-8",XMLFormatter::NoEscapes, 
												XMLFormatter::UnRep_CharRef));


}

DSIGSignature::DSIGSignature(void) :
m_keyInfoList(0),
m_errStr("") {
	mp_doc = NULL;
	mp_sigNode = NULL;
	mp_signingKey = NULL;
	mp_prefixNS = NULL;
	mp_URIResolver = NULL;
	mp_KeyInfoResolver = NULL;
	mp_KeyInfoNode = NULL;
	m_loaded = false;

	m_keyInfoList.setParentSignature(this);

	// Set up our formatter
	XSECnew(mp_formatter, XSECSafeBufferFormatter("UTF-8",XMLFormatter::NoEscapes, 
												XMLFormatter::UnRep_CharRef));


}

DSIGSignature::~DSIGSignature() {

	if (mp_signingKey != NULL) {

		delete mp_signingKey;
		mp_signingKey = NULL;

	}

	if (mp_signedInfo != NULL) {

		delete mp_signedInfo;
		mp_signedInfo = NULL;

	}

	if (mp_formatter != NULL) {

		delete mp_formatter;
		mp_formatter = NULL;
	}

	if (mp_prefixNS != NULL) {
		delete mp_prefixNS;
		mp_prefixNS = NULL;
	}

	if (mp_KeyInfoResolver != NULL) {
		delete mp_KeyInfoResolver;
		mp_KeyInfoResolver = NULL;
	}

	if (mp_URIResolver != NULL) {
		delete mp_URIResolver;
		mp_URIResolver = NULL;
	}

}

// Actions

const XMLCh * DSIGSignature::getErrMsgs() {

	return m_errStr.rawXMLChBuffer();

}
// --------------------------------------------------------------------------------
//           Creating signatures from blank
// --------------------------------------------------------------------------------

void DSIGSignature::setDSIGNSPrefix(const XMLCh * prefix) {

	if (mp_prefixNS != NULL)
		delete[] mp_prefixNS;

	mp_prefixNS = XMLString::replicate(prefix);

}

DOMElement *DSIGSignature::createBlankSignature(DOMDocument *doc,
			canonicalizationMethod cm,
			signatureMethod	sm,
			hashMethod hm) {

	mp_doc = doc;
	safeBuffer str;

	makeQName(str, mp_prefixNS, "Signature");

	DOMElement *sigNode = doc->createElementNS(DSIGConstants::s_unicodeStrURIDSIG, str.rawXMLChBuffer());

	if (mp_prefixNS[0] == '\0') {
		str.sbTranscodeIn("xmlns");
	}
	else {
		str.sbTranscodeIn("xmlns:");
		str.sbXMLChCat(mp_prefixNS);
	}

	sigNode->setAttributeNS(DSIGConstants::s_unicodeStrURIXMLNS, 
							str.rawXMLChBuffer(), 
							DSIGConstants::s_unicodeStrURIDSIG);

	mp_sigNode = sigNode;

	mp_sigNode->appendChild(mp_doc->createTextNode(DSIGConstants::s_unicodeStrNL));

	// Create the skeleton SignedInfo
	XSECnew(mp_signedInfo, DSIGSignedInfo(mp_doc, mp_formatter, this));
	
	mp_sigNode->appendChild(mp_signedInfo->createBlankSignedInfo(cm, sm, hm));
	mp_sigNode->appendChild(mp_doc->createTextNode(DSIGConstants::s_unicodeStrNL));

	// Create a dummy signature value (dummy until signed)

	makeQName(str, mp_prefixNS, "SignatureValue");
	DOMElement *sigValNode = doc->createElementNS(DSIGConstants::s_unicodeStrURIDSIG, 
												  str.rawXMLChBuffer());
	mp_signatureValueNode = sigValNode;
	mp_sigNode->appendChild(sigValNode);
	mp_sigNode->appendChild(doc->createTextNode(DSIGConstants::s_unicodeStrNL));

	// Some text to mark this as a template only
	sigValNode->appendChild(doc->createTextNode(MAKE_UNICODE_STRING("Not yet signed")));

	m_loaded = true;
	
	return sigNode;
}

// --------------------------------------------------------------------------------
//           Creating References
// --------------------------------------------------------------------------------

DSIGReference * DSIGSignature::createReference(const XMLCh * URI, 
								hashMethod hm, 
								char * type) {

	return mp_signedInfo->createReference(URI, hm, type);

}

// --------------------------------------------------------------------------------
//           Manipulation of KeyInfo elements
// --------------------------------------------------------------------------------

void DSIGSignature::clearKeyInfo(void) {

	if (mp_KeyInfoNode == 0)
		return;

	if (mp_sigNode->removeChild(mp_KeyInfoNode) != mp_KeyInfoNode) {

		throw XSECException(XSECException::ExpectedDSIGChildNotFound,
			"Attempted to remove KeyInfo node but it is no longer a child of <Signature>");

	}

	mp_KeyInfoNode->release();		// No longer required

	mp_KeyInfoNode = NULL;

	// Clear out the list
	m_keyInfoList.empty();

}

void DSIGSignature::createKeyInfoElement(void) {

	if (mp_KeyInfoNode != NULL)
		return;

	safeBuffer str;

	makeQName(str, mp_prefixNS, "KeyInfo");

	mp_KeyInfoNode = mp_doc->createElementNS(DSIGConstants::s_unicodeStrURIDSIG, str.rawXMLChBuffer());

	// Append the node to the end of the signature
	
	DOMNode * afterSignatureValue = mp_signatureValueNode->getNextSibling();
	while (afterSignatureValue != 0 && afterSignatureValue->getNodeType() != DOMNode::ELEMENT_NODE)
		afterSignatureValue = afterSignatureValue->getNextSibling();

	if (afterSignatureValue == 0) {
		mp_sigNode->appendChild(mp_KeyInfoNode);
		mp_sigNode->appendChild(mp_doc->createTextNode(DSIGConstants::s_unicodeStrNL));
	}
	else {
		mp_sigNode->insertBefore(mp_KeyInfoNode, afterSignatureValue);
		mp_sigNode->insertBefore(mp_doc->createTextNode(DSIGConstants::s_unicodeStrNL),
			afterSignatureValue);
	}

	mp_KeyInfoNode->appendChild(mp_doc->createTextNode(DSIGConstants::s_unicodeStrNL));
	

}


DSIGKeyInfoValue * DSIGSignature::appendDSAKeyValue(const XMLCh * P, 
						   const XMLCh * Q, 
						   const XMLCh * G, 
						   const XMLCh * Y) {

	createKeyInfoElement();

	// Create the new element
	DSIGKeyInfoValue * v;
	XSECnew(v, DSIGKeyInfoValue(this));

	mp_KeyInfoNode->appendChild(v->createBlankDSAKeyValue(P, Q, G, Y));
	mp_KeyInfoNode->appendChild(mp_doc->createTextNode(DSIGConstants::s_unicodeStrNL));

	// Add to the list
	m_keyInfoList.addKeyInfo(v);

	return v;

}

DSIGKeyInfoX509 * DSIGSignature::appendX509Data(void) {

	createKeyInfoElement();

	DSIGKeyInfoX509 * x;

	XSECnew(x, DSIGKeyInfoX509(this));

	mp_KeyInfoNode->appendChild(x->createBlankX509Data());
	mp_KeyInfoNode->appendChild(mp_doc->createTextNode(DSIGConstants::s_unicodeStrNL));

	// Add to the list
	m_keyInfoList.addKeyInfo(x);

	return x;

}

DSIGKeyInfoName * DSIGSignature::appendKeyName(const XMLCh * name) {

	createKeyInfoElement();

	DSIGKeyInfoName * n;

	XSECnew(n, DSIGKeyInfoName(this));

	mp_KeyInfoNode->appendChild(n->createBlankKeyName(name));
	mp_KeyInfoNode->appendChild(mp_doc->createTextNode(DSIGConstants::s_unicodeStrNL));

	// Add to the list
	m_keyInfoList.addKeyInfo(n);

	return n;

}


// --------------------------------------------------------------------------------
//           Working on Existing templates
// --------------------------------------------------------------------------------


void DSIGSignature::load(void) {

	// Load all the information from the source document into local variables for easier
	// manipulation by the other functions in the class

	if (mp_sigNode == NULL) {

		// Attempt to load an empty signature element
		throw XSECException(XSECException::LoadEmptySignature);

	}

	if (!strEquals(getDSIGLocalName(mp_sigNode), "Signature")) {

		throw XSECException(XSECException::LoadNonSignature);

	}

	m_loaded = true;

	// Find the prefix being used so that we can later use it to manipulate the signature
	mp_prefixNS = XMLString::replicate(mp_sigNode->getPrefix());

	// Now check for SignedInfo
	DOMNode *tmpElt = mp_sigNode->getFirstChild();

	while (tmpElt != 0 && (tmpElt->getNodeType() != DOMNode::ELEMENT_NODE))
		// Skip text and comments
		tmpElt = tmpElt->getNextSibling();

	if (tmpElt == 0 || !strEquals(getDSIGLocalName(tmpElt), "SignedInfo")) {

			throw XSECException(XSECException::ExpectedDSIGChildNotFound, 
					"Expected <SignedInfo> as first child of <Signature>");

	}

	// Have a signed info

	XSECnew(mp_signedInfo, DSIGSignedInfo(mp_doc, mp_formatter, tmpElt, this));
	mp_signedInfo->load();

	// Look at Signature Value
	tmpElt = tmpElt->getNextSibling();

	while (tmpElt != 0 && tmpElt->getNodeType() != DOMNode::ELEMENT_NODE)
		tmpElt = tmpElt->getNextSibling();

	if (tmpElt == 0 || !strEquals(getDSIGLocalName(tmpElt), "SignatureValue")) {

		throw XSECException(XSECException::ExpectedDSIGChildNotFound,
			"Expected <SignatureValue> node");

	}

	DOMNode *tmpSV = tmpElt->getFirstChild();
	while (tmpSV != 0 && tmpSV->getNodeType() != DOMNode::TEXT_NODE)
		tmpSV = tmpSV->getNextSibling();

	if (tmpSV == 0)
		throw XSECException(XSECException::ExpectedDSIGChildNotFound,
		"Expected TEXT child of <SignatureValue>");

	mp_signatureValueNode = tmpElt;
	
	// The signature value is transcoded to local code page, as it is easier
	// to work with, and should be low ASCII in any case (Base64)

	m_signatureValueSB.sbTranscodeIn(tmpSV->getNodeValue());


	// Now look at KeyInfo
	tmpElt = tmpElt->getNextSibling();

	while (tmpElt != 0 && !((tmpElt->getNodeType() == DOMNode::ELEMENT_NODE) && 
		strEquals(getDSIGLocalName(tmpElt), "KeyInfo")))
		tmpElt = tmpElt->getNextSibling();

	if (tmpElt != 0) {

		// Have a keyInfo

		mp_KeyInfoNode = tmpElt;		// In case we later want to manipulate it

		DOMNode *tmpKI = tmpElt->getFirstChild();

		while (tmpKI != 0 && (tmpKI->getNodeType() != DOMNode::ELEMENT_NODE))
			tmpKI = tmpKI->getNextSibling();


		while (tmpKI != 0) {

			// Find out what kind of KeyInfo child it is

			if (tmpKI != 0 && strEquals(getDSIGLocalName(tmpKI), "RetrievalMethod")) {

				// A reference to key information held elsewhere

				const XMLCh * URI = NULL;
				TXFMBase * currentTxfm;

				DOMNamedNodeMap *atts = tmpKI->getAttributes();
				const XMLCh * name;
				unsigned int size;

				if (atts == 0 || (size = atts->getLength()) == 0)
					return;

				for (unsigned int i = 0; i < size; ++i) {

					name = atts->item(i)->getNodeName();

					if (strEquals(name, "URI")) {
						URI  = atts->item(i)->getNodeValue();
					}

					else if (strEquals(name, "Type")) {

						// For now ignore

					}

					else if (strEquals(name, "Id")) {

						// For now ignore

					}

					else {
						safeBuffer tmp, error;

						error << (*mp_formatter << name);
						tmp.sbStrcpyIn("Unknown attribute in <RetrievalMethod> Element : ");
						tmp.sbStrcatIn(error);

						throw XSECException(XSECException::UnknownDSIGAttribute, tmp.rawCharBuffer());

					}

				}

				// Find base transform using the base URI
				currentTxfm = DSIGReference::getURIBaseTXFM(mp_doc, URI, mp_URIResolver);
				TXFMChain * chain;
				XSECnew(chain, TXFMChain(currentTxfm));
				Janitor<TXFMChain> j_chain(chain);

				// Now check for transforms
				tmpKI = tmpKI->getFirstChild();

				while (tmpKI != 0 && (tmpKI->getNodeType() != DOMNode::ELEMENT_NODE))
					// Skip text and comments
					tmpKI = tmpKI->getNextSibling();

				if (tmpKI == 0) {

					throw XSECException(XSECException::ExpectedDSIGChildNotFound, 
							"Expected <Transforms> within <KeyInfo>");

				}

				if (strEquals(getDSIGLocalName(tmpKI), "Transforms")) {


					// Process the transforms using the static function.
					// For the moment we don't really support remote KeyInfos, so
					// Just built the transform list, process it and then destroy it.

					DSIGTransformList * l = DSIGReference::loadTransforms(
						tmpKI,
						mp_formatter,
						this);

					DSIGTransformList::TransformListVectorType::size_type size, i;
					size = l->getSize();
					for (i = 0; i < size; ++ i) {
						try {
							l->item(i)->appendTransformer(chain);
						}
						catch (...) {
							delete l;
							throw;
						}
					}

					delete l;

				}

				// Find out the type of the final transform and process accordingly
				
				TXFMBase::nodeType type = chain->getLastTxfm()->getNodeType();

				XSECXPathNodeList lst;
				const DOMNode * element;

				switch (type) {

				case TXFMBase::DOM_NODE_DOCUMENT :

					break;

				case TXFMBase::DOM_NODE_DOCUMENT_FRAGMENT :

					break;

				case TXFMBase::DOM_NODE_XPATH_NODESET :

					lst = chain->getLastTxfm()->getXPathNodeList();
					element = lst.getFirstNode();

					while (element != NULL) {

						// Try to add each element - just call KeyInfoList add as it will
						// do the check to see if it is a valud KeyInfo

						m_keyInfoList.addXMLKeyInfo((DOMNode *) element);
						element = lst.getNextNode();

					}

					break;

				default :

					throw XSECException(XSECException::XPathError);

				}

				// Delete the transform chain
				chain->getLastTxfm()->deleteExpandedNameSpaces();

				// Janitor will clean up chain

			} /* if getNodeName == Retrieval Method */

			// Now just run through each node type in turn to process "local" KeyInfos

			else if (!m_keyInfoList.addXMLKeyInfo(tmpKI)) {

				throw XSECException(XSECException::KeyInfoError,
					"Unknown KeyInfo element found");

			}

			tmpKI = tmpKI->getNextSibling();

			while (tmpKI != 0 && (tmpKI->getNodeType() != DOMNode::ELEMENT_NODE))
				tmpKI = tmpKI->getNextSibling();

		}

	}

}

unsigned int DSIGSignature::calculateSignedInfoHash(unsigned char * hashBuf, 
													unsigned int hashBufLen) {

	// Calculate the hash and store in the hashBuf

	TXFMBase * txfm;
	TXFMChain * chain;

	// First we calculate the hash.  Start off by creating a starting point
	XSECnew(txfm, TXFMDocObject(mp_doc));
	XSECnew(chain, TXFMChain(txfm));
	Janitor<TXFMChain> j_chain(chain);

	((TXFMDocObject *) txfm)->setInput(mp_doc, mp_signedInfo->getDOMNode());
	
	// canonicalise the SignedInfo content

	switch (mp_signedInfo->getCanonicalizationMethod()) {

	case CANON_C14N_NOC :

		XSECnew(txfm, TXFMC14n(mp_doc));
		chain->appendTxfm(txfm);
		txfm->stripComments();
		
		break;

	case CANON_C14N_COM :

		XSECnew(txfm, TXFMC14n(mp_doc));
		chain->appendTxfm(txfm);
		txfm->activateComments();

		break;

	case CANON_C14NE_NOC :

		XSECnew(txfm, TXFMC14n(mp_doc));
		chain->appendTxfm(txfm);
		((TXFMC14n *) txfm)->setExclusive();
		txfm->stripComments();
		
		break;

	case CANON_C14NE_COM :

		XSECnew(txfm, TXFMC14n(mp_doc));
		chain->appendTxfm(txfm);
		((TXFMC14n *) txfm)->setExclusive();
		txfm->activateComments();

		break;

	default :

		throw XSECException(XSECException::SigVfyError,
			"Canonicalisation method unknown in DSIGSignature::verify()");

	}

	// Setup Hash

	switch (mp_signedInfo->getHashMethod()) {

	case HASH_SHA1 :

		if (mp_signedInfo->getSignatureMethod() == SIGNATURE_HMAC){
			XSECnew(txfm, TXFMSHA1(mp_doc, mp_signingKey));
		}
		else  {
			XSECnew(txfm, TXFMSHA1(mp_doc));
		}

		break;

	default :

		throw XSECException(XSECException::SigVfyError,
			"Hash method unknown in DSIGSignature::verify()");

	}

#if 0
	TXFMOutputFile * of = new TXFMOutputFile(mp_doc);

	of->setFile("Output");
	of->setInput(hashVal);
	hashVal=of;
#endif

	chain->appendTxfm(txfm);

	// Write hash to the buffer
	int hashLen;

	hashLen = chain->getLastTxfm()->readBytes((XMLByte *) hashBuf, hashBufLen);

	return hashLen;

}

// --------------------------------------------------------------------------------
//           Verify a signature
// --------------------------------------------------------------------------------

bool DSIGSignature::verifySignatureOnlyInternal(void) {

	unsigned char hash[4096];
	int hashLen;

	if (!m_loaded) {

		// Need to call "load" prior to checking a signature
		throw XSECException(XSECException::SigVfyError,
					"DSIGSignature::verify() called prior to DSIGSignature::load()");

	}

	// Try to find a key
	if (mp_signingKey == NULL) {

//		// Try to load a key from the KeyInfo list
//		if ((mp_signingKey = m_keyInfoList.findKey()) == NULL) {

//			throw XSECException(XSECException::SigVfyError,
//				"DSIGSignature::verify() - no verification key loaded and cannot determine from KeyInfo list");
//		}

		if (mp_KeyInfoResolver == NULL) {
			
			throw XSECException(XSECException::SigVfyError,
				"DSIGSignature::verify() - no verification key loaded and no KeyInfoResolver loaded");

		}
		
		if ((mp_signingKey = mp_KeyInfoResolver->resolveKey(&m_keyInfoList)) == NULL) {

			throw XSECException(XSECException::SigVfyError,
				"DSIGSignature::verify() - no verification key loaded and cannot determine from KeyInfoResolver");
		}

	}


	hashLen = calculateSignedInfoHash(hash, 4096);

	// Now set up to verify
	bool sigVfyRet = false;

	switch (mp_signingKey->getKeyType()) {

	case (XSECCryptoKey::KEY_DSA_PUBLIC) :
	case (XSECCryptoKey::KEY_DSA_PAIR) :

		if (mp_signedInfo == NULL || mp_signedInfo->getSignatureMethod() != SIGNATURE_DSA) {

			throw XSECException(XSECException::SigVfyError,
				"Key type does not match <SignedInfo> signature type");

		}

		sigVfyRet = ((XSECCryptoKeyDSA *) mp_signingKey)->verifyBase64Signature(
			hash, 
			hashLen,
			(char *) m_signatureValueSB.rawBuffer(), 
			m_signatureValueSB.sbStrlen());

		if (!sigVfyRet)
			m_errStr.sbXMLChCat("DSA Validation of <SignedInfo> failed");

		break;

	case (XSECCryptoKey::KEY_RSA_PUBLIC) :
	case (XSECCryptoKey::KEY_RSA_PAIR) :

		if (mp_signedInfo == NULL || mp_signedInfo->getSignatureMethod() != SIGNATURE_RSA) {
		
			throw XSECException(XSECException::SigVfyError,
				"Key type does not match <SignedInfo> signature type");

		}

		sigVfyRet = ((XSECCryptoKeyRSA *) mp_signingKey)->verifySHA1PKCS1Base64Signature(
			hash,
			hashLen,
			m_signatureValueSB.rawCharBuffer(),
			m_signatureValueSB.sbStrlen());

		if (sigVfyRet == false) {

			m_errStr.sbXMLChCat("RSA Validation of <SignedInfo> failed");

		}

		break;

	case (XSECCryptoKey::KEY_HMAC) :

		// Already done - just compare calculated value with read value
		sigVfyRet = compareBase64StringToRaw(m_signatureValueSB, 
			hash, 
			hashLen,
			mp_signedInfo->getHMACOutputLength());
		if (!sigVfyRet)
			m_errStr.sbXMLChCat("HMAC Validation of <SignedInfo> failed");

		break;

	default :

		throw XSECException(XSECException::SigVfyError,
			"Key found, but don't know how to check the signature using it");

	}

	return sigVfyRet;

}

bool DSIGSignature::verifySignatureOnly(void) {

	m_errStr.sbTranscodeIn("");
	return verifySignatureOnlyInternal();

}

bool DSIGSignature::verify(void) {

	// We have a (hopefully) fully loaded signature.  Need to 
	// verify

	bool referenceCheckResult;

	if (!m_loaded) {

		// Need to call "load" prior to checking a signature
		throw XSECException(XSECException::SigVfyError,
					"DSIGSignature::verify() called prior to DSIGSignature::load()");

	}

	// Reset
	m_errStr.sbXMLChIn(DSIGConstants::s_unicodeStrEmpty);

	// First thing to do is check the references

	referenceCheckResult = mp_signedInfo->verify(m_errStr);

	// Check the signature

	bool sigVfyResult = verifySignatureOnlyInternal();

	return sigVfyResult & referenceCheckResult;
}

// --------------------------------------------------------------------------------
//           Sign the XML document that has been previously loaded
// --------------------------------------------------------------------------------

void DSIGSignature::sign(void) {

	// We have a (hopefully) fully loaded signature.  Need to 
	// sign

	if (!m_loaded) {

		// Need to call "load" prior to checking a signature
		throw XSECException(XSECException::SigVfyError,
					"DSIGSignature::sign() called prior to DSIGSignature::load()");

	}

	// Check we have a key
	if (mp_signingKey == NULL) {

		throw XSECException(XSECException::SigVfyError,
			"DSIGSignature::verify() - no signing key loaded");
		

	}

	// Reset error string in case we have any reference problems.
	m_errStr.sbXMLChIn(DSIGConstants::s_unicodeStrEmpty);

	// Set up the reference list hashes - including any manifests
	mp_signedInfo->hash();

	// Calculate the hash to be signed

	unsigned char hash[4096];
	int hashLen;

	hashLen = calculateSignedInfoHash(hash, 4096);
	
	// Now check the calculated hash

	char b64Buf[256];
	unsigned int b64Len;
	safeBuffer b64SB;
	
	switch (mp_signingKey->getKeyType()) {

	case (XSECCryptoKey::KEY_DSA_PRIVATE) :
	case (XSECCryptoKey::KEY_DSA_PAIR) :

		if (mp_signedInfo == NULL || mp_signedInfo->getSignatureMethod() != SIGNATURE_DSA) {

			throw XSECException(XSECException::SigningError,
				"Key type does not match <SignedInfo> signature type");

		}

		b64Len = ((XSECCryptoKeyDSA *) mp_signingKey)->signBase64Signature(
			hash, 
			hashLen,
			(char *) b64Buf, 
			256);

		if (b64Len <= 0) {

			throw XSECException(XSECException::SigningError,
				"Unknown error occured during a DSA Signing operation");

		}

		if (b64Buf[b64Len-1] == '\n')
			b64Buf[b64Len-1] = '\0';
		else
			b64Buf[b64Len] = '\0';

		break;

	case (XSECCryptoKey::KEY_RSA_PRIVATE) :
	case (XSECCryptoKey::KEY_RSA_PAIR) :

		if (mp_signedInfo == NULL || mp_signedInfo->getSignatureMethod() != SIGNATURE_RSA) {

			throw XSECException(XSECException::SigningError,
				"Key type does not match <SignedInfo> signature type");

		}

		b64Len = ((XSECCryptoKeyRSA *) mp_signingKey)->signSHA1PKCS1Base64Signature(
			hash, 
			hashLen,
			(char *) b64Buf, 
			256);

		if (b64Len <= 0) {

			throw XSECException(XSECException::SigningError,
				"Unknown error occured during a RSA Signing operation");

		}

		// Clean up some "funnies" and make sure the string is NULL terminated

		if (b64Buf[b64Len-1] == '\n')
			b64Buf[b64Len-1] = '\0';
		else
			b64Buf[b64Len] = '\0';

		break;

	case (XSECCryptoKey::KEY_HMAC) :

		if (mp_signedInfo == NULL || mp_signedInfo->getSignatureMethod() != SIGNATURE_HMAC) {

			throw XSECException(XSECException::SigningError,
				"Key type does not match <SignedInfo> signature type");

		}

		// Signature already created, so just translate to base 64 and enter string
		
		convertRawToBase64String(b64SB, 
								hash, 
								hashLen, 
								mp_signedInfo->getHMACOutputLength());
		
		strncpy(b64Buf, (char *) b64SB.rawBuffer(), 255);
		break;

	default :

		throw XSECException(XSECException::SigVfyError,
			"Key found, but don't know how to sign the document using it");

	}

	// Now we have the signature - place it in the DOM structures

	DOMNode *tmpElt = mp_signatureValueNode->getFirstChild();

	while (tmpElt != NULL && tmpElt->getNodeType() != DOMNode::TEXT_NODE)
		tmpElt = tmpElt->getNextSibling();

	if (tmpElt == NULL) {
		// Need to create the underlying TEXT_NODE
		DOMDocument * doc = mp_signatureValueNode->getOwnerDocument();
		tmpElt = doc->createTextNode(MAKE_UNICODE_STRING(b64Buf));
		mp_signatureValueNode->appendChild(tmpElt);
	}
	else {
		tmpElt->setNodeValue(MAKE_UNICODE_STRING(b64Buf));
	}

	// And copy to the local buffer
	m_signatureValueSB = b64Buf;
	
}

// --------------------------------------------------------------------------------
//           Key Management
// --------------------------------------------------------------------------------

void DSIGSignature::setSigningKey(XSECCryptoKey *k) {

	if (mp_signingKey != NULL)
		delete mp_signingKey;

	mp_signingKey = k;

}

