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
 * SimpleHMAC := An application to generate an XML document (via Xerces) and sign it
 *
 * Author(s): Berin Lautenbach
 *
 * $ID$
 *
 * $LOG$
 *
 */

#include "IOStreamOutputter.hpp"

// Xerces

#include <xercesc/util/PlatformUtils.hpp>

// XML-Security-C (XSEC)

#include <xsec/framework/XSECProvider.hpp>
#include <xsec/dsig/DSIGReference.hpp>
#include <xsec/enc/OpenSSL/OpenSSLCryptoKeyHMAC.hpp>
#include <xsec/framework/XSECException.hpp>

// Xalan

#ifndef XSEC_NO_XALAN
#include <xalanc/XalanTransformer/XalanTransformer.hpp>
XALAN_USING_XALAN(XalanTransformer)
#endif


XSEC_USING_XERCES(DOMImplementation);
XSEC_USING_XERCES(XMLPlatformUtils);
XSEC_USING_XERCES(XMLException);
XSEC_USING_XERCES(DOMImplementationRegistry);

DOMDocument *createLetter(DOMImplementation *impl) {

	DOMDocument *doc = impl->createDocument(
                0,
                MAKE_UNICODE_STRING("Letter"),             
                NULL);  

    DOMElement *rootElem = doc->getDocumentElement();

	// Add the ToAddress

	DOMElement *addressElem = doc->createElement(MAKE_UNICODE_STRING("ToAddress"));
	rootElem->appendChild(doc->createTextNode(MAKE_UNICODE_STRING("\n")));
	rootElem->appendChild(addressElem);
	addressElem->appendChild(doc->createTextNode(
		MAKE_UNICODE_STRING("The address of the Recipient")));

	// Add the FromAddress
	addressElem = doc->createElement(MAKE_UNICODE_STRING("FromAddress"));
	rootElem->appendChild(doc->createTextNode(MAKE_UNICODE_STRING("\n")));
	rootElem->appendChild(addressElem);
	addressElem->appendChild(doc->createTextNode(
		MAKE_UNICODE_STRING("The address of the Sender")));

	// Add some text
	DOMElement *textElem = doc->createElement(MAKE_UNICODE_STRING("Text"));
	rootElem->appendChild(doc->createTextNode(MAKE_UNICODE_STRING("\n")));
	rootElem->appendChild(textElem);
	textElem->appendChild(doc->createTextNode(
		MAKE_UNICODE_STRING("\nTo whom it may concern\n\n...\n")));

	return doc;

}

int main (int argc, char **argv) {

	try {
		XMLPlatformUtils::Initialize();
#ifndef XSEC_NO_XALAN
		XalanTransformer::initialize();
#endif
		XSECPlatformUtils::Initialise();
	}
	catch (const XMLException &e) {

		cerr << "Error during initialisation of Xerces" << endl;
		cerr << "Error Message = : "
		     << e.getMessage() << endl;

	}

	// Create a blank Document

    DOMImplementation *impl = 
		DOMImplementationRegistry::getDOMImplementation(MAKE_UNICODE_STRING("Core"));
	
	// Create a letter
	DOMDocument *doc = createLetter(impl);
    DOMElement *rootElem = doc->getDocumentElement();

	// The signature

	XSECProvider prov;
	DSIGSignature *sig;
	DOMElement *sigNode;

	try {
		
		// Create a signature object

		sig = prov.newSignature();
		sig->setDSIGNSPrefix(MAKE_UNICODE_STRING("ds"));

		// Use it to create a blank signature DOM structure from the doc

		sigNode = sig->createBlankSignature(doc, CANON_C14N_COM, SIGNATURE_HMAC, HASH_SHA1);

		// Inser the signature DOM nodes into the doc

		rootElem->appendChild(doc->createTextNode(MAKE_UNICODE_STRING("\n")));
		rootElem->appendChild(sigNode);
		rootElem->appendChild(doc->createTextNode(MAKE_UNICODE_STRING("\n")));

		// Create an envelope reference for the text to be signed
		DSIGReference * ref = sig->createReference(MAKE_UNICODE_STRING(""));
		ref->appendEnvelopedSignatureTransform();

		// Set the HMAC Key to be the string "secret"

		OpenSSLCryptoKeyHMAC * hmacKey = new OpenSSLCryptoKeyHMAC();
		hmacKey->setKey((unsigned char *) "secret", strlen("secret"));
		sig->setSigningKey(hmacKey);

		// Add a KeyInfo element
		sig->appendKeyName(MAKE_UNICODE_STRING("The secret key is \"secret\""));

		// Sign

		sig->sign();
	}

	catch (XSECException &e)
	{
		cerr << "An error occured during a signature load\n   Message: "
		<< e.getMsg() << endl;
		exit(1);
		
	}

	// Output

	docSetup(doc);
	cout << doc;

	return 0;

}
