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
 * xtest := basic test application to run through a series of tests of
 *			the XSEC library.
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */

#include <xsec/framework/XSECDefs.hpp> 

#include <cassert>

#include <memory.h>
#include <iostream>
#include <stdlib.h>

#include <xercesc/util/PlatformUtils.hpp>
#include <xercesc/util/XMLUniDefs.hpp>
#include <xercesc/util/XMLString.hpp>
#include <xercesc/parsers/XercesDOMParser.hpp>
#include <xercesc/framework/XMLFormatter.hpp>
#include <xercesc/framework/StdOutFormatTarget.hpp>
#include <xercesc/framework/MemBufFormatTarget.hpp>
#include <xercesc/framework/MemBufInputSource.hpp>

#include <xercesc/dom/DOM.hpp>
#include <xercesc/util/XMLException.hpp>

#include <xsec/transformers/TXFMOutputFile.hpp>
#include <xsec/dsig/DSIGTransformXPath.hpp>
#include <xsec/dsig/DSIGTransformXPathFilter.hpp>
#include <xsec/dsig/DSIGTransformC14n.hpp>

// XALAN

#ifndef XSEC_NO_XALAN

#include <XPath/XPathEvaluator.hpp>
#include <XalanTransformer/XalanTransformer.hpp>

XALAN_USING_XALAN(XPathEvaluator)
XALAN_USING_XALAN(XalanTransformer)

#endif

// XSEC

#include <xsec/utils/XSECPlatformUtils.hpp>
#include <xsec/framework/XSECProvider.hpp>
#include <xsec/canon/XSECC14n20010315.hpp>
#include <xsec/dsig/DSIGReference.hpp>
#include <xsec/framework/XSECError.hpp>
#include <xsec/dsig/DSIGSignature.hpp>
#include <xsec/utils/XSECNameSpaceExpander.hpp>
#include <xsec/utils/XSECDOMUtils.hpp>
#include <xsec/enc/XSECCryptoException.hpp>
#include <xsec/dsig/DSIGKeyInfoX509.hpp>
#include <xsec/dsig/DSIGKeyInfoName.hpp>
#include <xsec/dsig/DSIGKeyInfoPGPData.hpp>
#include <xsec/dsig/DSIGKeyInfoSPKIData.hpp>

#if defined (HAVE_OPENSSL)
#	include <xsec/enc/OpenSSL/OpenSSLCryptoKeyHMAC.hpp>
#endif
#if defined (HAVE_WINCAPI)
#	include <xsec/enc/WinCAPI/WinCAPICryptoKeyHMAC.hpp>
#endif

using std::ostream;
using std::cout;
using std::cerr;
using std::endl;
using std::flush;

/*
 * Because of all the characters, it's easiest to put the entire program
 * in the Xerces namespace
 */

XERCES_CPP_NAMESPACE_USE

// --------------------------------------------------------------------------------
//           Known "Good" Values
// --------------------------------------------------------------------------------

unsigned char createdDocRefs [9][20] = {
	{ 0x51, 0x3c, 0xb5, 0xdf, 0xb9, 0x1e, 0x9d, 0xaf, 0xd4, 0x4a,
	  0x95, 0x79, 0xf1, 0xd6, 0x54, 0xe, 0xb0, 0xb0, 0x29, 0xe3, },
	{ 0x51, 0x3c, 0xb5, 0xdf, 0xb9, 0x1e, 0x9d, 0xaf, 0xd4, 0x4a, 
	  0x95, 0x79, 0xf1, 0xd6, 0x54, 0xe, 0xb0, 0xb0, 0x29, 0xe3, },
	{ 0x52, 0x74, 0xc3, 0xe4, 0xc5, 0xf7, 0x20, 0xb0, 0xd9, 0x52, 
	  0xdb, 0xb3, 0xee, 0x46, 0x66, 0x8f, 0xe1, 0xb6, 0x30, 0x9d, },
	{ 0x5a, 0x14, 0x9c, 0x5a, 0x40, 0x34, 0x51, 0x4f, 0xef, 0x1d, 
	  0x85, 0x44, 0xc7, 0x2a, 0xd3, 0xd2, 0x2, 0xed, 0x67, 0xb4, },
	{ 0x88, 0xd1, 0x65, 0xed, 0x2a, 0xe7, 0xc0, 0xbd, 0xea, 0x3e, 
	  0xe6, 0xf3, 0xd4, 0x8c, 0xf7, 0xdd, 0xc8, 0x85, 0xa9, 0x6d, },
	{ 0x52, 0x74, 0xc3, 0xe4, 0xc5, 0xf7, 0x20, 0xb0, 0xd9, 0x52, 
	  0xdb, 0xb3, 0xee, 0x46, 0x66, 0x8f, 0xe1, 0xb6, 0x30, 0x9d, },
	{ 0x52, 0x74, 0xc3, 0xe4, 0xc5, 0xf7, 0x20, 0xb0, 0xd9, 0x52, 
	  0xdb, 0xb3, 0xee, 0x46, 0x66, 0x8f, 0xe1, 0xb6, 0x30, 0x9d, },
	{ 0x3c, 0x80, 0x4, 0x94, 0xa5, 0xbe, 0xf6, 0x16, 0x40, 0xe0, 
  	  0x24, 0xd5, 0x65, 0x39, 0xc, 0x18, 0x21, 0x3d, 0xa5, 0x51, },
  	{ 0x51, 0x3c, 0xb5, 0xdf, 0xb9, 0x1e, 0x9d, 0xaf, 0xd4, 0x4a, 
	  0x95, 0x79, 0xf1, 0xd6, 0x54, 0xe, 0xb0, 0xb0, 0x29, 0xe3, }

};

// --------------------------------------------------------------------------------
//           Some test data
// --------------------------------------------------------------------------------

// "CN=<Test,>,O=XSEC  "

XMLCh s_tstDName[] = {

	chLatin_C,
	chLatin_N,
	chEqual,
	chOpenAngle,
	chLatin_T,
	chLatin_e,
	chLatin_s,
	chLatin_t,
	chComma,
	chCloseAngle,
	chComma,
	chLatin_O,
	chEqual,
	chLatin_X,
	chLatin_S,
	chLatin_E,
	chLatin_C,
	chSpace,
	chSpace,
	chNull

};

XMLCh s_tstPGPKeyID[] = {

	chLatin_D, chLatin_u, chLatin_m, chLatin_m, chLatin_y, chSpace,
	chLatin_P, chLatin_G, chLatin_P, chSpace,
	chLatin_I, chLatin_D, chNull
};

XMLCh s_tstPGPKeyPacket[] = {

	chLatin_D, chLatin_u, chLatin_m, chLatin_m, chLatin_y, chSpace,
	chLatin_P, chLatin_G, chLatin_P, chSpace,
	chLatin_P, chLatin_a, chLatin_c, chLatin_k, chLatin_e, chLatin_t, chNull
};

XMLCh s_tstSexp1[] = {

	chLatin_D, chLatin_u, chLatin_m, chLatin_m, chLatin_y, chSpace,
	chLatin_S, chLatin_e, chLatin_x, chLatin_p, chDigit_1, chNull
};

XMLCh s_tstSexp2[] = {

	chLatin_D, chLatin_u, chLatin_m, chLatin_m, chLatin_y, chSpace,
	chLatin_S, chLatin_e, chLatin_x, chLatin_p, chDigit_2, chNull
};

// --------------------------------------------------------------------------------
//           Create a key
// --------------------------------------------------------------------------------

XSECCryptoKeyHMAC * createHMACKey(const unsigned char * str) {

	// Create the HMAC key
	static bool first = true;

#if defined (HAVE_OPENSSL)
	OpenSSLCryptoKeyHMAC * hmacKey = new OpenSSLCryptoKeyHMAC();
	if (first) {
		cerr << "Using OpenSSL as the cryptography provider" << endl;
		first = false;
	}
#else
#	if defined (HAVE_WINCAPI)
	WinCAPICryptoKeyHMAC * hmacKey = new WinCAPICryptoKeyHMAC();
	if (first) {
		cerr << "Using Windows Crypto API as the cryptography provider" << endl;
		first = false;
	}
#	endif
#endif
	hmacKey->setKey((unsigned char *) str, strlen((char *)str));

	return hmacKey;

}

// --------------------------------------------------------------------------------
//           Utility function for outputting hex data
// --------------------------------------------------------------------------------

void outputHex(unsigned char * buf, int len) {

	cout << std::hex;
	for (int i = 0; i < len; ++i) {
		cout << "0x" << (unsigned int) buf[i] << ", ";
	}
	cout << std::ios::dec << endl;

}

// --------------------------------------------------------------------------------
//           Main
// --------------------------------------------------------------------------------

int main(int argc, char **argv) {


	// First initialise the XML system

	try {

		XMLPlatformUtils::Initialize();
#ifndef XSEC_NO_XALAN
		XPathEvaluator::initialize();
		XalanTransformer::initialize();
#endif
		XSECPlatformUtils::Initialise();

	}
	catch (const XMLException &e) {

		cerr << "Error during initialisation of Xerces" << endl;
		cerr << "Error Message = : "
		     << e.getMessage() << endl;

	}

	/*
	 * First we create a document from scratch
	 */

	cerr << "Creating a known doc and signing (HMAC-SHA1)" << endl;
	
	// Create a blank Document

    //DOMImplementation impl;

	XMLCh tempStr[100];
    XMLString::transcode("Core", tempStr, 99);    
    DOMImplementation *impl = DOMImplementationRegistry::getDOMImplementation(tempStr);
        

    DOMDocument *doc = impl->createDocument(
                0,                    // root element namespace URI.
                MAKE_UNICODE_STRING("ADoc"),            // root element name
                NULL);// DOMDocumentType());  // document type object (DTD).

    DOMElement *rootElem = doc->getDocumentElement();
	rootElem->setAttributeNS(DSIGConstants::s_unicodeStrURIXMLNS, 
		MAKE_UNICODE_STRING("xmlns:foo"), MAKE_UNICODE_STRING("http://www.foo.org"));

    DOMElement  * prodElem = doc->createElement(MAKE_UNICODE_STRING("product"));
    rootElem->appendChild(prodElem);

    DOMText    * prodDataVal = doc->createTextNode(MAKE_UNICODE_STRING("XMLSecurityC"));
    prodElem->appendChild(prodDataVal);

    DOMElement  *catElem = doc->createElement(MAKE_UNICODE_STRING("category"));
    rootElem->appendChild(catElem);
    catElem->setAttribute(MAKE_UNICODE_STRING("idea"), MAKE_UNICODE_STRING("great"));

    DOMText    *catDataVal = doc->createTextNode(MAKE_UNICODE_STRING("XML Security Tools"));
    catElem->appendChild(catDataVal);

	XSECProvider prov;
	DSIGSignature *sig;
	DSIGReference *ref[10];
	DOMElement *sigNode;
	int refCount;

	try {
		
		/*
		 * Now we have a document, create a signature for it.
		 */
		
		sig = prov.newSignature();
		sig->setDSIGNSPrefix(MAKE_UNICODE_STRING("ds"));

		sigNode = sig->createBlankSignature(doc, CANON_C14N_COM, SIGNATURE_HMAC, HASH_SHA1);
		rootElem->appendChild(doc->createTextNode(DSIGConstants::s_unicodeStrNL));
		rootElem->insertBefore(doc->createComment(MAKE_UNICODE_STRING(" a comment ")), prodElem);
		rootElem->appendChild(sigNode);
		rootElem->insertBefore(doc->createTextNode(DSIGConstants::s_unicodeStrNL), prodElem);

		/*
		 * Add some test references
		 */

		ref[0] = sig->createReference(MAKE_UNICODE_STRING(""));
		ref[0]->appendEnvelopedSignatureTransform();

		ref[1] = sig->createReference(MAKE_UNICODE_STRING("#xpointer(/)"));
		ref[1]->appendEnvelopedSignatureTransform();
		ref[1]->appendCanonicalizationTransform(CANON_C14N_NOC);

		ref[2] = sig->createReference(MAKE_UNICODE_STRING("#xpointer(/)"));
		ref[2]->appendEnvelopedSignatureTransform();
		ref[2]->appendCanonicalizationTransform(CANON_C14N_COM);

		ref[3] = sig->createReference(MAKE_UNICODE_STRING("#xpointer(/)"));
		ref[3]->appendEnvelopedSignatureTransform();
		ref[3]->appendCanonicalizationTransform(CANON_C14NE_NOC);

		ref[4] = sig->createReference(MAKE_UNICODE_STRING("#xpointer(/)"));
		ref[4]->appendEnvelopedSignatureTransform();
		ref[4]->appendCanonicalizationTransform(CANON_C14NE_COM);

		ref[5] = sig->createReference(MAKE_UNICODE_STRING("#xpointer(/)"));
		ref[5]->appendEnvelopedSignatureTransform();
		DSIGTransformC14n * ce = ref[5]->appendCanonicalizationTransform(CANON_C14NE_COM);
		ce->addInclusiveNamespace("foo");

		sig->setECNSPrefix(MAKE_UNICODE_STRING("ec"));
		ref[6] = sig->createReference(MAKE_UNICODE_STRING("#xpointer(/)"));
		ref[6]->appendEnvelopedSignatureTransform();
		ce = ref[6]->appendCanonicalizationTransform(CANON_C14NE_COM);
		ce->addInclusiveNamespace("foo");

#ifdef XSEC_NO_XALAN

		cerr << "WARNING : No testing of XPath being performed as Xalan not present" << endl;
		refCount = 7;

#else
		/*
		 * Create some XPath/XPathFilter references
		 */


		ref[7] = sig->createReference(MAKE_UNICODE_STRING(""));
		sig->setXPFNSPrefix(MAKE_UNICODE_STRING("xpf"));
		DSIGTransformXPathFilter * xpf = ref[7]->appendXPathFilterTransform();
		xpf->appendFilter(FILTER_INTERSECT, MAKE_UNICODE_STRING("//ADoc/category"));

		ref[8] = sig->createReference(MAKE_UNICODE_STRING(""));
		/*		ref[5]->appendXPathTransform("ancestor-or-self::dsig:Signature", 
				"xmlns:dsig=http://www.w3.org/2000/09/xmldsig#"); */

		DSIGTransformXPath * x = ref[8]->appendXPathTransform("count(ancestor-or-self::dsig:Signature | \
here()/ancestor::dsig:Signature[1]) > \
count(ancestor-or-self::dsig:Signature)");
		x->setNamespace("dsig", "http://www.w3.org/2000/09/xmldsig#");

		refCount = 9;

#endif
	
		/*
		 * Sign the document, using an HMAC algorithm and the key "secret"
		 */


		sig->appendKeyName(MAKE_UNICODE_STRING("The secret key is \"secret\""));

		// Append a test DNames

		DSIGKeyInfoX509 * x509 = sig->appendX509Data();
		x509->setX509SubjectName(s_tstDName);

		// Append a test PGPData element
		sig->appendPGPData(s_tstPGPKeyID, s_tstPGPKeyPacket);

		// Append an SPKIData element
		DSIGKeyInfoSPKIData * spki = sig->appendSPKIData(s_tstSexp1);
		spki->appendSexp(s_tstSexp2);

		sig->setSigningKey(createHMACKey((unsigned char *) "secret"));
		sig->sign();

		cerr << "Doc signed OK - Checking values against Known Good" << endl;

		unsigned char buf[128];
		int len;

		/*
		 * Validate the reference hash values from known good
		 */

		int i;
		for (i = 0; i < refCount; ++i) {

			cerr << "Calculating hash for reference " << i << " ... ";

			len = (int) ref[i]->calculateHash(buf, 128);

			cerr << " Done\nChecking -> ";

			if (len != 20) {
				cerr << "Bad (Length = " << len << ")" << endl;
				exit (1);
			}

			for (int j = 0; j < 20; ++j) {

				if (buf[j] != createdDocRefs[i][j]) {
					cerr << "Bad at location " << j << endl;
					exit (1);
				}
			
			}
			cerr << "Good.\n";

		}

		/*
		 * Verify the signature check works
		 */

		cerr << "Running \"verifySignatureOnly()\" on calculated signature ... ";
		if (sig->verifySignatureOnly()) {
			cerr << "OK" << endl;
		}
		else {
			cerr << "Failed" << endl;
			char * e = XMLString::transcode(sig->getErrMsgs());
			cout << e << endl;
			delete [] e;
			exit(1);
		}

		/*
		 * Change the document and ensure the signature fails.
		 */

		cerr << "Setting incorrect key in Signature object" << endl;
		sig->setSigningKey(createHMACKey((unsigned char *) "badsecret"));

		cerr << "Running \"verifySignatureOnly()\" on calculated signature ... ";
		if (!sig->verifySignatureOnly()) {
			cerr << "OK (Signature bad)" << endl;
		}
		else {
			cerr << "Failed (signature OK but should be bad)" << endl;
			exit(1);
		}

		// Don't need the signature now the DOM structure is in place
		prov.releaseSignature(sig);

		/*
		 * Now serialise the document to memory so we can re-parse and check from scratch
		 */

		cerr << "Serialising the document to a memory buffer ... ";

		DOMWriter         *theSerializer = ((DOMImplementationLS*)impl)->createDOMWriter();

		theSerializer->setEncoding(MAKE_UNICODE_STRING("UTF-8"));
		if (theSerializer->canSetFeature(XMLUni::fgDOMWRTFormatPrettyPrint, false))
			theSerializer->setFeature(XMLUni::fgDOMWRTFormatPrettyPrint, false);


		MemBufFormatTarget *formatTarget = new MemBufFormatTarget();

		theSerializer->writeNode(formatTarget, *doc);

		// Copy to a new buffer
		len = formatTarget->getLen();
		char * mbuf = new char [len + 1];
		memcpy(mbuf, formatTarget->getRawBuffer(), len);
		mbuf[len] = '\0';

		delete theSerializer;
		delete formatTarget;

		cerr << "done\nParsing memory buffer back to DOM ... ";

		// Also release the document so that we can re-load from scratch

		doc->release();

		/*
		 * Re-parse
		 */

		XercesDOMParser parser;
		
		parser.setDoNamespaces(true);
		parser.setCreateEntityReferenceNodes(true);

		MemBufInputSource* memIS = new MemBufInputSource ((const XMLByte*) mbuf, 
																len, "XSECMem");

		parser.parse(*memIS);
		doc = parser.adoptDocument();


		delete(memIS);
		delete[] mbuf;

		cerr << "done\nValidating signature ...";

		/*
		 * Validate signature
		 */

		sig = prov.newSignatureFromDOM(doc);
		sig->load();
		sig->setSigningKey(createHMACKey((unsigned char *) "secret"));

		if (sig->verify()) {
			cerr << "OK" << endl;
		}
		else {
			cerr << "Failed\n" << endl;
			char * e = XMLString::transcode(sig->getErrMsgs());
			cerr << e << endl;
			delete [] e;
			exit(1);
		}

		/*
		 * Ensure DNames are read back in and decoded properly
		 */

		DSIGKeyInfoList * kil = sig->getKeyInfoList();
		int nki = kil->getSize();

		cerr << "Checking Distinguished name is decoded correctly ... ";
		for (i = 0; i < nki; ++i) {

			if (kil->item(i)->getKeyInfoType() == DSIGKeyInfo::KEYINFO_X509) {

				if (strEquals(s_tstDName, ((DSIGKeyInfoX509 *) kil->item(i))->getX509SubjectName())) {
					cerr << "yes" << endl;
				}
				else {
					cerr << "decoded incorrectly" << endl;;
					exit (1);
				}
			}
			if (kil->item(i)->getKeyInfoType() == DSIGKeyInfo::KEYINFO_PGPDATA) {
				
				cerr << "Validating PGPData read back OK ... ";

				DSIGKeyInfoPGPData * p = (DSIGKeyInfoPGPData *)kil->item(i);

				if (!(strEquals(p->getKeyID(), s_tstPGPKeyID) &&
					strEquals(p->getKeyPacket(), s_tstPGPKeyPacket))) {

					cerr << "no!";
					exit(1);
				}

				cerr << "yes\n";
			}
			if (kil->item(i)->getKeyInfoType() == DSIGKeyInfo::KEYINFO_SPKIDATA) {
				
				cerr << "Validating SPKIData read back OK ... ";

				DSIGKeyInfoSPKIData * s = (DSIGKeyInfoSPKIData *)kil->item(i);

				if (s->getSexpSize() != 2) {
					cerr << "no - expected two S-expressions";
					exit(1);
				}

				if (!(strEquals(s->getSexp(0), s_tstSexp1) &&
					strEquals(s->getSexp(1), s_tstSexp2))) {

					cerr << "no!";
					exit(1);
				}

				cerr << "yes\n";
			}
		}
	}

	catch (XSECException &e)
	{
		cerr << "An error occured during signature processing\n   Message: ";
		char * ce = XMLString::transcode(e.getMsg());
		cerr << ce << endl;
		delete ce;
		exit(1);
		
	}	
	catch (XSECCryptoException &e)
	{
		cerr << "A cryptographic error occured during signature processing\n   Message: "
		<< e.getMsg() << endl;
		exit(1);
	}

	DOMWriter         *theSerializer = ((DOMImplementationLS*)impl)->createDOMWriter();

	theSerializer->setEncoding(MAKE_UNICODE_STRING("UTF-8"));
	if (theSerializer->canSetFeature(XMLUni::fgDOMWRTFormatPrettyPrint, false))
		theSerializer->setFeature(XMLUni::fgDOMWRTFormatPrettyPrint, false);


	XMLFormatTarget *formatTarget = new StdOutFormatTarget();

	theSerializer->writeNode(formatTarget, *doc);
	
	cout << endl;

	delete theSerializer;
	delete formatTarget;

	doc->release();

	cerr << "All tests passed" << endl;

	return 0;

}
