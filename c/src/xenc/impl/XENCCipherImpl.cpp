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
#include <xsec/transformers/TXFMC14n.hpp>
#include <xsec/transformers/TXFMSB.hpp>
#include <xsec/transformers/TXFMDocObject.hpp>
#include <xsec/utils/XSECDOMUtils.hpp>
#include <xsec/framework/XSECEnv.hpp>
#include <xsec/enc/XSECKeyInfoResolver.hpp>
#include <xsec/framework/XSECAlgorithmMapper.hpp>
#include <xsec/framework/XSECAlgorithmHandler.hpp>
#include <xsec/utils/XSECPlatformUtils.hpp>

#include "XENCCipherImpl.hpp"
#include "XENCEncryptedDataImpl.hpp"
#include "XENCEncryptedKeyImpl.hpp"
#include "XENCEncryptionMethodImpl.hpp"
#include "XENCAlgorithmHandlerDefault.hpp"

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

	chLatin_f,
	chLatin_r,
	chLatin_a,
	chLatin_g,
	chLatin_m,
	chLatin_e,
	chLatin_n,
	chLatin_t,
	chNull
};

const XMLCh s_noData[] = {
	chLatin_n,
	chLatin_o,
	chLatin_D,
	chLatin_a,
	chLatin_t,
	chLatin_a,
	chNull
};

const XMLCh s_ds[] = {
	chLatin_d,
	chLatin_s,
	chNull
};

// --------------------------------------------------------------------------------
//			Constructors
// --------------------------------------------------------------------------------

XENCCipherImpl::XENCCipherImpl(DOMDocument * doc) :
mp_doc(doc),
mp_encryptedData(NULL),
mp_key(NULL),
mp_kek(NULL),
mp_keyInfoResolver(NULL) {

	XSECnew(mp_env, XSECEnv(doc));
	mp_env->setDSIGNSPrefix(s_ds);

}

XENCCipherImpl::~XENCCipherImpl() {

	if (mp_encryptedData != NULL)
		delete mp_encryptedData;

	if (mp_key != NULL)
		delete mp_key;

	if (mp_kek != NULL)
		delete mp_kek;

	if (mp_env != NULL)
		delete mp_env;

	if (mp_keyInfoResolver != NULL)
		delete mp_keyInfoResolver;

}

// --------------------------------------------------------------------------------
//			Initialiser
// --------------------------------------------------------------------------------

void XENCCipherImpl::Initialise(void) {

	XENCAlgorithmHandlerDefault def;
	
	// Register default encryption algorithm handlers

	XSECPlatformUtils::registerAlgorithmHandler(DSIGConstants::s_unicodeStrURI3DES_CBC, def);
	XSECPlatformUtils::registerAlgorithmHandler(DSIGConstants::s_unicodeStrURIKW_AES128, def);

}

// --------------------------------------------------------------------------------
//			Set/get the namespace prefix to be used when creating nodes
// --------------------------------------------------------------------------------

void XENCCipherImpl::setXENCNSPrefix(const XMLCh * prefix) {

	mp_env->setXENCNSPrefix(prefix);

}

const XMLCh * XENCCipherImpl::getXENCNSPrefix(void) const {

	return mp_env->getXENCNSPrefix();

}
// --------------------------------------------------------------------------------
//			Key Info resolvers
// --------------------------------------------------------------------------------

void XENCCipherImpl::setKeyInfoResolver(const XSECKeyInfoResolver * resolver) {

	if (mp_keyInfoResolver != NULL)
		delete mp_keyInfoResolver;

	mp_keyInfoResolver = resolver->clone();

}

// --------------------------------------------------------------------------------
//			Key Info resolvers
// --------------------------------------------------------------------------------

XENCEncryptedData * XENCCipherImpl::getEncryptedData(void) {

	return mp_encryptedData;

}
// --------------------------------------------------------------------------------
//			Keys
// --------------------------------------------------------------------------------

void XENCCipherImpl::setKey(XSECCryptoKey * key) {

	if (mp_key != NULL)
		delete mp_key;

	mp_key = key;

}

void XENCCipherImpl::setKEK(XSECCryptoKey * key) {

	if (mp_kek != NULL)
		delete mp_kek;

	mp_kek = key;

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
//			Decrypt an Element and replace in original document
// --------------------------------------------------------------------------------

DOMDocument * XENCCipherImpl::decryptElement(DOMElement * element) {

	XSECAlgorithmHandler *handler;

	// First of all load the element
	if (mp_encryptedData != NULL)
		delete mp_encryptedData;

	XSECnew(mp_encryptedData, 
		XENCEncryptedDataImpl(mp_env, dynamic_cast<DOMNode *>(element)));

	// Load
	mp_encryptedData->load();

	// Make sure we have a key before we do anything else too drastic
	if (mp_key == NULL) {

		if (mp_keyInfoResolver != NULL)
			mp_key = mp_keyInfoResolver->resolveKey(mp_encryptedData->getKeyInfoList());

		if (mp_key == NULL) {

			// See if we can decrypt a key in the KeyInfo list
			DSIGKeyInfoList * kil = mp_encryptedData->getKeyInfoList();
			int kLen = kil->getSize();

			for (int i = 0; i < kLen ; ++ i) {

				if (kil->item(i)->getKeyInfoType() == DSIGKeyInfo::KEYINFO_ENCRYPTEDKEY) {

					XENCEncryptedKey * ek = dynamic_cast<XENCEncryptedKey*>(kil->item(i));
					volatile XMLByte buffer[1024];
					try {
						// Have to cast off volatile
						int keySize = decryptKey(ek, (XMLByte *) buffer, 1024);

						if (keySize > 0) {
							// Try to map the key

							XENCEncryptionMethod * encryptionMethod = 
								mp_encryptedData->getEncryptionMethod();

							if (encryptionMethod != NULL) {
			
								handler = 
									XSECPlatformUtils::g_algorithmMapper->mapURIToHandler(
										mp_encryptedData->getEncryptionMethod()->getAlgorithm());

								if (handler != NULL)
									mp_key = handler->createKeyForURI(
												mp_encryptedData->getEncryptionMethod()->getAlgorithm(),
												(XMLByte *) buffer,
												keySize);
							}
						}
					} catch (...) {
						memset((void *) buffer, 0, 1024);
						throw;
					}

					// Clear out the key buffer
					memset((void *) buffer, 0, 1024);
				}
			}
		}

		if (mp_key == NULL) {

			throw XSECException(XSECException::CipherError, 
				"XENCCipherImpl::decryptElement - No key set and cannot resolve");
		}
	}

	// Get the raw encrypted data
	TXFMChain * c = mp_encryptedData->createCipherTXFMChain();
	Janitor<TXFMChain> j_c(c);

	// Get the Algorithm handler for the algorithm
	XENCEncryptionMethod * encryptionMethod = mp_encryptedData->getEncryptionMethod();

	if (encryptionMethod != NULL) {
		
		handler = 
			XSECPlatformUtils::g_algorithmMapper->mapURIToHandler(
				mp_encryptedData->getEncryptionMethod()->getAlgorithm());
	
	}

	else {

		handler =
			XSECPlatformUtils::g_algorithmMapper->mapURIToHandler(
				XSECAlgorithmMapper::s_defaultEncryptionMapping);

	}

	safeBuffer sb("");

	if (handler != NULL) {

		handler->decryptToSafeBuffer(c, 
			mp_encryptedData->getEncryptionMethod(), 
			mp_key,
			mp_env->getParentDocument(),
			sb);
	}
	else {

		// Very strange if we get here - any problems should throw an
		// exception in the AlgorithmMapper.

		throw XSECException(XSECException::CipherError, 
			"XENCCipherImpl::decryptElement - Error retrieving a handler for algorithm");

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

	return mp_env->getParentDocument();

}

// --------------------------------------------------------------------------------
//			Decrypt a key in an XENCEncryptedKey element
// --------------------------------------------------------------------------------

int XENCCipherImpl::decryptKey(XENCEncryptedKey * encryptedKey, XMLByte * rawKey, int maxKeySize) {

	// Make sure we have a key before we do anything else too drastic
	if (mp_kek == NULL) {

		if (mp_keyInfoResolver != NULL)
			mp_kek = mp_keyInfoResolver->resolveKey(encryptedKey->getKeyInfoList());

		if (mp_kek == NULL) {

			throw XSECException(XSECException::CipherError, 
				"XENCCipherImpl::decryptKey - No KEK set and cannot resolve");
		}
	}

	// Get the raw encrypted data
	TXFMChain * c = dynamic_cast<XENCEncryptedKeyImpl *>(encryptedKey)->createCipherTXFMChain();
	Janitor<TXFMChain> j_c(c);

	// Get the Algorithm handler for the algorithm
	XENCEncryptionMethod * encryptionMethod = encryptedKey->getEncryptionMethod();
	XSECAlgorithmHandler *handler;

	if (encryptionMethod != NULL) {
		
		handler = 
			XSECPlatformUtils::g_algorithmMapper->mapURIToHandler(
				encryptedKey->getEncryptionMethod()->getAlgorithm());
	
	}

	else {

		handler =
			XSECPlatformUtils::g_algorithmMapper->mapURIToHandler(
				XSECAlgorithmMapper::s_defaultEncryptionMapping);

	}

	safeBuffer sb("");
	unsigned int keySize;

	if (handler != NULL) {

		keySize = handler->decryptToSafeBuffer(c, 
			encryptedKey->getEncryptionMethod(), 
			mp_kek,
			mp_env->getParentDocument(),
			sb);
	}
	else {

		// Very strange if we get here - any problems should throw an
		// exception in the AlgorithmMapper.

		throw XSECException(XSECException::CipherError, 
			"XENCCipherImpl::decryptElement - Error retrieving a handler for algorithm");

	}

	keySize = (keySize < maxKeySize ? keySize : maxKeySize);
	memcpy(rawKey, sb.rawBuffer(), keySize);

	return keySize;
}

// --------------------------------------------------------------------------------
//			Encrypt a key
// --------------------------------------------------------------------------------

XENCEncryptedKey * XENCCipherImpl::encryptKey(
		const unsigned char * keyBuffer,
		unsigned int keyLen,
		encryptionMethod em,
		const XMLCh * algorithmURI) {

	if (mp_kek == NULL) {
		throw XSECException(XSECException::CipherError, 
			"XENCCipherImpl::encryptKey - No KEK set");
	}

	// Map the encryption method to a URI
	safeBuffer algorithmSB;
	const XMLCh * algorithm;

	if (em == ENCRYPT_NONE) {
		algorithm = algorithmURI;
	}
	else {
		if (encryptionMethod2URI(algorithmSB, em) != true) {
			throw XSECException(XSECException::CipherError, 
				"XENCCipherImpl::encryptKey - Unknown encryption method");
		}
		algorithm = algorithmSB.sbStrToXMLCh();
	}

	// Create the element with a dummy encrypted value

	XENCEncryptedKeyImpl * encryptedKey;
	
	XSECnew(encryptedKey, XENCEncryptedKeyImpl(mp_env));
	Janitor<XENCEncryptedKeyImpl> j_encryptedKey(encryptedKey);

	encryptedKey->createBlankEncryptedKey(
		XENCCipherData::VALUE_TYPE,
		algorithm,
		s_noData);


	// Create a transform chain to do pass the key to the encrypto
	
	safeBuffer rawKey;
	rawKey.isSensitive();
	rawKey.sbMemcpyIn(keyBuffer, keyLen);

	TXFMSB * tsb;
	XSECnew(tsb, TXFMSB(mp_doc));

	TXFMChain * c;
	XSECnew(c, TXFMChain(tsb));
	Janitor<TXFMChain> j_c(c);
	
	tsb->setInput(rawKey, keyLen);

	// Perform the encryption
	XSECAlgorithmHandler *handler;

	if (algorithm != NULL) {
		
		handler = 
			XSECPlatformUtils::g_algorithmMapper->mapURIToHandler(algorithm);
	
	}

	else {

		handler =
			XSECPlatformUtils::g_algorithmMapper->mapURIToHandler(
				XSECAlgorithmMapper::s_defaultEncryptionMapping);

	}

	safeBuffer sb;

	if (handler != NULL) {

		handler->encryptToSafeBuffer(c, 
			encryptedKey->getEncryptionMethod(), 
			mp_kek,
			mp_env->getParentDocument(),
			sb);
	}
	else {

		// Very strange if we get here - any problems should throw an
		// exception in the AlgorithmMapper.

		throw XSECException(XSECException::CipherError, 
			"XENCCipherImpl::encryptKey - Error retrieving a handler for algorithm");

	}

	// Set the value
	XENCCipherValue * val = encryptedKey->getCipherData()->getCipherValue();

	val->setCipherString(sb.sbStrToXMLCh());

	j_encryptedKey.release();
	return encryptedKey;
}

// --------------------------------------------------------------------------------
//			Create an EncryptedData element
// --------------------------------------------------------------------------------

XENCEncryptedData * XENCCipherImpl::createEncryptedData(
						XENCCipherData::XENCCipherDataType type,
						const XMLCh * algorithm,
						const XMLCh * value) {

	// Clean out anything currently being used
	if (mp_encryptedData != NULL) {
		delete mp_encryptedData;
		mp_encryptedData = NULL;
	}
	// Create a new EncryptedData element

	XSECnew(mp_encryptedData, XENCEncryptedDataImpl(mp_env));
	mp_encryptedData->createBlankEncryptedData(type, algorithm, value);

	return mp_encryptedData;
}

// --------------------------------------------------------------------------------
//			Encrypt an element
// --------------------------------------------------------------------------------

DOMDocument * XENCCipherImpl::encryptElement(DOMElement * element, 
											 encryptionMethod em,
											 const XMLCh * algorithmURI) {

	// Make sure we have a key before we do anything too drastic
	if (mp_key == NULL) {
		throw XSECException(XSECException::CipherError, 
			"XENCCipherImpl::encryptElement - No key set");
	}

	// Map the encryption method to a URI
	safeBuffer algorithmSB;
	const XMLCh * algorithm;

	if (em == ENCRYPT_NONE) {
		algorithm = algorithmURI;
	}
	else {
		if (encryptionMethod2URI(algorithmSB, em) != true) {
			throw XSECException(XSECException::CipherError, 
				"XENCCipherImpl::encryptElement - Unknown encryption method");
		}
		algorithm = algorithmSB.sbStrToXMLCh();
	}

	// Create the element with a dummy encrypted value

	if (mp_encryptedData != NULL) {
		delete mp_encryptedData;
		mp_encryptedData = NULL;
	}
	
	XSECnew(mp_encryptedData, XENCEncryptedDataImpl(mp_env));
	mp_encryptedData->createBlankEncryptedData(
		XENCCipherData::VALUE_TYPE, 
		algorithm,
		s_noData);


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

	// Perform the encryption
	XSECAlgorithmHandler *handler;

	if (algorithm != NULL) {
		
		handler = 
			XSECPlatformUtils::g_algorithmMapper->mapURIToHandler(algorithm);
	
	}

	else {

		handler =
			XSECPlatformUtils::g_algorithmMapper->mapURIToHandler(
				XSECAlgorithmMapper::s_defaultEncryptionMapping);

	}

	safeBuffer sb;

	if (handler != NULL) {

		handler->encryptToSafeBuffer(c, 
			mp_encryptedData->getEncryptionMethod(), 
			mp_key,
			mp_env->getParentDocument(),
			sb);
	}
	else {

		// Very strange if we get here - any problems should throw an
		// exception in the AlgorithmMapper.

		throw XSECException(XSECException::CipherError, 
			"XENCCipherImpl::decryptElement - Error retrieving a handler for algorithm");

	}

	// Set the value
	XENCCipherValue * val = mp_encryptedData->getCipherData()->getCipherValue();

	val->setCipherString(sb.sbStrToXMLCh());

	// Replace original element
	DOMNode * p = element->getParentNode();
	
	if (p == NULL) {
		throw XSECException(XSECException::CipherError, 
			"XENCCipherImpl::encryptElement - Passed in element has no parent");
	}

	p->replaceChild(mp_encryptedData->getDOMNode(), element);

	return mp_doc;

}

// --------------------------------------------------------------------------------
//			Pretty Print functions
// --------------------------------------------------------------------------------

void XENCCipherImpl::setPrettyPrint(bool flag) {

	mp_env->setPrettyPrintFlag(flag);

}

bool XENCCipherImpl::getPrettyPrint(void) {

	return mp_env->getPrettyPrintFlag();

}

