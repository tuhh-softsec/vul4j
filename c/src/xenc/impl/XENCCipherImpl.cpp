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
 * XENCCipherImpl := Implementation of the main encryption worker class
 *
 * $Id$
 *
 */

// XSEC Includes

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/framework/XSECError.hpp>
#include <xsec/enc/XSECCryptoKey.hpp>
#include <xsec/transformers/TXFMChain.hpp>
#include <xsec/transformers/TXFMBase.hpp>
#include <xsec/transformers/TXFMBase64.hpp>
#include <xsec/transformers/TXFMC14n.hpp>
#include <xsec/transformers/TXFMCipher.hpp>
#include <xsec/transformers/TXFMDocObject.hpp>
#include <xsec/utils/XSECDOMUtils.hpp>

#include "XENCCipherImpl.hpp"
#include "XENCEncryptedDataImpl.hpp"

#include <xercesc/dom/DOMNode.hpp>
#include <xercesc/dom/DOMElement.hpp>
#include <xercesc/util/XMLUniDefs.hpp>
#include <xercesc/parsers/XercesDOMParser.hpp>
#include <xercesc/framework/MemBufInputSource.hpp>
#include <xercesc/util/Janitor.hpp>

// With all the characters - just uplift entire thing

XERCES_CPP_NAMESPACE_USE

#include <iostream>
using std::cout;

// --------------------------------------------------------------------------------
//			Constant Strings
// --------------------------------------------------------------------------------


const XMLCh s_tagname[] = {

	XERCES_CPP_NAMESPACE :: chLatin_f,
	XERCES_CPP_NAMESPACE :: chLatin_r,
	XERCES_CPP_NAMESPACE :: chLatin_a,
	XERCES_CPP_NAMESPACE :: chLatin_g,
	XERCES_CPP_NAMESPACE :: chLatin_m,
	XERCES_CPP_NAMESPACE :: chLatin_e,
	XERCES_CPP_NAMESPACE :: chLatin_n,
	XERCES_CPP_NAMESPACE :: chLatin_t,
	XERCES_CPP_NAMESPACE :: chNull
};

const XMLCh s_defaultXENCPrefix[] = {

	XERCES_CPP_NAMESPACE :: chLatin_x,
	XERCES_CPP_NAMESPACE :: chLatin_e,
	XERCES_CPP_NAMESPACE :: chLatin_n,
	XERCES_CPP_NAMESPACE :: chLatin_c,
	XERCES_CPP_NAMESPACE :: chNull

};

// --------------------------------------------------------------------------------
//			Constructors
// --------------------------------------------------------------------------------

XENCCipherImpl::XENCCipherImpl(DOMDocument * doc) :
mp_doc(doc),
mp_encryptedData(NULL),
mp_key(NULL) {

	mp_xencPrefixNS = XMLString::replicate(s_defaultXENCPrefix);

}

XENCCipherImpl::~XENCCipherImpl() {

	if (mp_encryptedData != NULL)
		delete mp_encryptedData;

	if (mp_key != NULL)
		delete mp_key;

	if (mp_xencPrefixNS != NULL)
		delete mp_xencPrefixNS;

}

// --------------------------------------------------------------------------------
//			Set/get the namespace prefix to be used when creating nodes
// --------------------------------------------------------------------------------

void XENCCipherImpl::setXENCNSPrefix(const XMLCh * prefix) {

	if (mp_xencPrefixNS != NULL)
		delete mp_xencPrefixNS;

	// Copy in new one
	mp_xencPrefixNS = XMLString::replicate(prefix);

}

const XMLCh * XENCCipherImpl::getXENCNSPrefix(void) const {

	return mp_xencPrefixNS;

}

// --------------------------------------------------------------------------------
//			Serialise/Deserialise an element
// --------------------------------------------------------------------------------

DOMDocumentFragment * XENCCipherImpl::deSerialise(safeBuffer &content, DOMNode * ctx) {

	DOMDocumentFragment * result;

	// Create the context to parse the document against
	safeBuffer sb;
	sb.sbXMLChIn(DSIGConstants::s_unicodeStrEmpty);
	sb.sbXMLChAppendCh(chUnicodeMarker);
	//sb.sbXMLChCat8("<?xml version=\"1.0\" encoding=\"UTF-16\"?><");
	sb.sbXMLChAppendCh(chOpenAngle);
	sb.sbXMLChCat(s_tagname);

	// Run through each node up to the document node and find any
	// xmlns: nodes that may be needed during the parse of the decrypted content

	DOMNode * ctxParent = ctx->getParentNode();
	DOMNode * wk = ctxParent;

	while (wk != NULL) {

		DOMNamedNodeMap * atts = wk->getAttributes();
		int length;
		if (atts != NULL)
			length = atts->getLength();
		else
			length = 0;

		for (int i = 0 ; i < length ; ++i) {
			DOMNode * att = atts->item(i);
			if (strEquals(att->getNodeName(), DSIGConstants::s_unicodeStrXmlns) ||
				(XMLString::compareNString(att->getNodeName(), DSIGConstants::s_unicodeStrXmlns, 5) &&
				att->getNodeName()[5] == chColon)) {
			
				// Check to see if this node has already been found
				DOMNode * p = ctxParent;
				bool found = false;
				while (p != wk) {
					DOMNamedNodeMap * tstAtts = p->getAttributes();
					if (tstAtts != NULL && 
						tstAtts->getNamedItem(att->getNodeName()) != NULL) {
						found = true;
						break;
					}
					p = p->getParentNode();
				}
				if (found == false) {
					
					// This is an attribute node that needs to be added
					sb.sbXMLChAppendCh(chSpace);
					sb.sbXMLChCat(att->getNodeName());
					sb.sbXMLChAppendCh(chEqual);
					sb.sbXMLChAppendCh(chDoubleQuote);
					sb.sbXMLChCat(att->getNodeValue());
					sb.sbXMLChAppendCh(chDoubleQuote);
				}
			}
		}
		wk = wk->getParentNode();
	}
	sb.sbXMLChAppendCh(chCloseAngle);

	// Now transform the content to UTF-8
	sb.sbXMLChCat8(content.rawCharBuffer());

	// Terminate the string

	sb.sbXMLChAppendCh(chOpenAngle);
	sb.sbXMLChAppendCh(chForwardSlash);
	sb.sbXMLChCat(s_tagname);
	sb.sbXMLChAppendCh(chCloseAngle);

	// Now we need to parse the document

	XercesDOMParser * parser = new XercesDOMParser;
	Janitor<XercesDOMParser> j_parser(parser);

	parser->setDoNamespaces(true);
	parser->setCreateEntityReferenceNodes(true);
	parser->setDoSchema(false);

	// Create an input source

	unsigned int bytes = XMLString::stringLen(sb.rawXMLChBuffer()) * sizeof(XMLCh);
	char * utf = XMLString::transcode(sb.rawXMLChBuffer());
	MemBufInputSource* memIS = new MemBufInputSource ((const XMLByte*) sb.rawBuffer(), bytes, "XSECMem");
	//MemBufInputSource* memIS = new MemBufInputSource ((const XMLByte*) utf, strlen(utf), "XSECMem");
	Janitor<MemBufInputSource> j_memIS(memIS);

	int errorCount = 0;


	parser->parse(*memIS);
    errorCount = parser->getErrorCount();
    if (errorCount > 0)
		throw XSECException(XSECException::CipherError, "Errors occured during de-serialisation of decrypted element content");

    DOMDocument * doc = parser->getDocument();

	// Create a DocumentFragment to hold the children of the parsed doc element
	DOMDocument *ctxDocument = ctx->getOwnerDocument();
	result = ctxDocument->createDocumentFragment();
	Janitor<DOMDocumentFragment> j_result(result);

	// Now get the children of the document into a DOC fragment
	DOMNode * fragElt = doc->getDocumentElement();
	DOMNode * child;

	if (fragElt != NULL) {
		child = fragElt->getFirstChild();
	}
	else {

		throw XSECException(XSECException::CipherError, 
			"XENCCipher::deSerialse - re-parsed document unexpectedly empty");
	}

	while (child != NULL) {
		result->appendChild(ctxDocument->importNode(child, true));
		child = child->getNextSibling();
	}

	// Done!

	j_result.release();
    return result;
}

// --------------------------------------------------------------------------------
//			Build a set of decryption transforms
// --------------------------------------------------------------------------------

TXFMChain * XENCCipherImpl::createDecryptionTXFMChain(void) {

	// First obtain the raw encrypted data
	if (mp_key == NULL) {
		throw XSECException(XSECException::CipherError, 
			"XENCCipherImpl::createDecryptionTXFMChain - No key set");
	}

	if (mp_encryptedData == NULL) {
		throw XSECException(XSECException::CipherError, 
			"XENCCipherImpl::createDecryptionTXFMChain - Encrypted Data");
	}

	// Get the raw encrypted data
	TXFMChain * c = mp_encryptedData->createCipherTXFMChain();

	Janitor<TXFMChain> j_c(c);

	// Now add the decryption TXFM
	TXFMCipher * tcipher;
	XSECnew(tcipher, TXFMCipher(mp_doc, mp_key, false));

	c->appendTxfm(tcipher);

	j_c.release();
	return c;

}

// --------------------------------------------------------------------------------
//			Decrypt an Element and replace in original document
// --------------------------------------------------------------------------------

DOMDocument * XENCCipherImpl::decryptElement(DOMElement * element) {

	// Make sure we have a key before we do anything too drastic
	if (mp_key == NULL) {
		throw XSECException(XSECException::CipherError, 
			"XENCCipherImpl::decryptElement - No key set");
	}
	
	// First of all load the element
	if (mp_encryptedData != NULL)
		delete mp_encryptedData;

	XSECnew(mp_encryptedData, 
		XENCEncryptedDataImpl(this, dynamic_cast<DOMNode *>(element)));

	// Load
	mp_encryptedData->load();

	// Get the raw encrypted data
	TXFMChain * c = createDecryptionTXFMChain();
	Janitor<TXFMChain> j_c(c);
	TXFMBase * b = c->getLastTxfm();

	// Read the result into a safeBuffer
	XMLByte buf[2050];
	safeBuffer sb("");

	int len;
	while ((len = b->readBytes(buf, 2048)) > 0) {
		buf[len] = '\0';
		sb.sbStrcatIn((char *) buf);
	}

	// Now de-serialise
	DOMDocumentFragment * frag = deSerialise(sb, element);

	if (frag != NULL) {

		// Have something to replace current element with
		DOMNode * p = element->getParentNode();
		DOMNode * c = frag->getFirstChild();

		// By inserting the DocumentFragment, we effectively insert the children
		p->replaceChild(frag, element);

		// Delete the frag and the old element
		frag->release();
		element->release();

	}

	return NULL;

}

// --------------------------------------------------------------------------------
//			Create an EncryptedData element
// --------------------------------------------------------------------------------

XENCEncryptedData * XENCCipherImpl::createEncryptedData(
						XENCCipherData::XENCCipherDataType type, 
						XMLCh * value) {

	// Clean out anything currently being used
	if (mp_encryptedData != NULL) {
		delete mp_encryptedData;
		mp_encryptedData = NULL;
	}
	// Create a new EncryptedData element

	XSECnew(mp_encryptedData, XENCEncryptedDataImpl(this));
	mp_encryptedData->createBlankEncryptedData(type, value);

	return mp_encryptedData;
}

// --------------------------------------------------------------------------------
//			Encrypt an element
// --------------------------------------------------------------------------------

DOMDocument * XENCCipherImpl::encryptElement(DOMElement * element) {

	// Make sure we have a key before we do anything too drastic
	if (mp_key == NULL) {
		throw XSECException(XSECException::CipherError, 
			"XENCCipherImpl::encryptElement - No key set");
	}

	// Create a transform chain to do the encryption
	TXFMDocObject * tdocObj;
	XSECnew(tdocObj, TXFMDocObject(mp_doc));
	TXFMChain * c;
	XSECnew(c, TXFMChain(tdocObj));

	Janitor<TXFMChain> j_c(c);

	tdocObj->setInput(mp_doc, element);

	// Now need to serialise the element - easiest to just use a canonicaliser
	TXFMC14n *tc14n;
	XSECnew(tc14n, TXFMC14n(mp_doc));
	c->appendTxfm(tc14n);

	tc14n->activateComments();
	tc14n->setExclusive();

	// Do the encryption
	TXFMCipher *tcipher;
	XSECnew(tcipher, TXFMCipher(mp_doc, mp_key, true));
	c->appendTxfm(tcipher);

	// Transform to Base64
	TXFMBase64 * tb64;
	XSECnew(tb64, TXFMBase64(mp_doc, false));
	c->appendTxfm(tb64);

	// Read into a safeBuffer
	safeBuffer sb;
	sb << c->getLastTxfm();

	// Create the element!

	if (mp_encryptedData != NULL) {
		delete mp_encryptedData;
		mp_encryptedData = NULL;
	}
	
	XSECnew(mp_encryptedData, XENCEncryptedDataImpl(this));
	mp_encryptedData->createBlankEncryptedData(XENCCipherData::VALUE_TYPE, sb.sbStrToXMLCh());

	// Replace original element
	DOMNode * p = element->getParentNode();
	
	if (p == NULL) {
		throw XSECException(XSECException::CipherError, 
			"XENCCipherImpl::encryptElement - Passed in element has no parent");
	}

	p->replaceChild(mp_encryptedData->getDOMNode(), element);

	return mp_doc;

}
