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
 * simpleValidate := An application to validate an in-memory signature
 *
 * Author(s): Berin Lautenbach
 *
 * $ID$
 *
 * $LOG$
 *
 */

#include "IOStreamOutputter.hpp"

// XML-Security-C (XSEC)

#include <xsec/framework/XSECProvider.hpp>
#include <xsec/dsig/DSIGReference.hpp>
#include <xsec/enc/OpenSSL/OpenSSLCryptoKeyHMAC.hpp>
#include <xsec/framework/XSECException.hpp>
#include <xsec/enc/OpenSSL/OpenSSLCryptoX509.hpp>
#include <xsec/enc/XSECCryptoException.hpp>


// Xerces

#include <xercesc/util/PlatformUtils.hpp>
#include <xercesc/parsers/XercesDOMParser.hpp>
#include <xercesc/framework/MemBufInputSource.hpp>

XSEC_USING_XERCES(XMLPlatformUtils);
XSEC_USING_XERCES(XercesDOMParser);
XSEC_USING_XERCES(MemBufInputSource);
XSEC_USING_XERCES(XMLException);

#ifndef XSEC_NO_XALAN

// Xalan

#include <xalanc/XalanTransformer/XalanTransformer.hpp>
XALAN_USING_XALAN(XalanTransformer)

#endif

char docToValidate [4096] = "\
<PurchaseOrder>\n\
<Company>Widgets.Org</Company>\n\
<Product>A large widget</Product>\n\
<Amount>$16.50</Amount>\n\
<Due>16 January 2010</Due>\n\
<ds:Signature xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\">\n\
<ds:SignedInfo>\n\
<ds:CanonicalizationMethod Algorithm=\"http://www.w3.org/TR/2001/REC-xml-c14n-20010315\"/>\n\
<ds:SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#dsa-sha1\"/>\n\
<ds:Reference URI=\"#xpointer(/)\">\n\
<ds:Transforms>\n\
<ds:Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\"/>\n\
<ds:Transform Algorithm=\"http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments\"/>\n\
</ds:Transforms>\n\
<ds:DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"/>\n\
<ds:DigestValue>n+6y945h/SvlVF9qBq+Lb4TrcOI=</ds:DigestValue>\n\
</ds:Reference>\n\
</ds:SignedInfo>\n\
<ds:SignatureValue>OmToLo8uEnK37nCFXDiZwgcsZGJ0aZ4AyECUy78DL91AHRRWdjllSQ==</ds:SignatureValue>\n\
<ds:KeyInfo>\n\
<ds:X509Data>\n\
<ds:X509SubjectName>C=AU, ST=Vic, O=XML-Security-C Project, CN=Samples Demo Certificate</ds:X509SubjectName>\n\
</ds:X509Data>\n\
</ds:KeyInfo>\n\
</ds:Signature>\n\
</PurchaseOrder>\n";

char cert[] = "\n\
MIIEETCCA9GgAwIBAgICEAEwCQYHKoZIzjgEAzB5MQswCQYDVQQGEwJBVTEMMAoG\n\
A1UECBMDVmljMRIwEAYDVQQHEwlNZWxib3VybmUxHzAdBgNVBAoTFlhNTC1TZWN1\n\
cml0eS1DIFByb2plY3QxEDAOBgNVBAsTB1hTRUMtQ0ExFTATBgNVBAMTDFhTRUMt\n\
Q0EgUm9vdDAeFw0wMjExMDUwMzE1NDFaFw0wMzExMDUwMzE1NDFaMF8xCzAJBgNV\n\
BAYTAkFVMQwwCgYDVQQIEwNWaWMxHzAdBgNVBAoTFlhNTC1TZWN1cml0eS1DIFBy\n\
b2plY3QxITAfBgNVBAMTGFNhbXBsZXMgRGVtbyBDZXJ0aWZpY2F0ZTCCAbgwggEs\n\
BgcqhkjOOAQBMIIBHwKBgQDj1jBku/y6COfkxmHMLS1behxr3ah8sFAk71EyuXLy\n\
2Ony989WUc52/5M3nNY9E/75KB3uKNcrnGY8Tfw85Wrehv7jSImCuxljtnomABTj\n\
9LBuGL9TfYBNBJI/0jNR0GOo0kQphoKFOvldtRIwRmtU5Mcamg9e5FOEjYJCSah5\n\
rwIVAOzWxorDrF4uwMIC/ss6PfibdNgHAoGBANLAOsJjpBQx43DgnNSkVJ518Tqz\n\
IHKpg9crAsCRd+Keipt/tVnOTA29uJZMo2wUSGC8Vj7tlreMJtxDUnLcRdX6EZwj\n\
WR9nBhLpzClndctjjLF5IkzCechQk7CNKmO2Z2gaD6K/hdfMixF/LH/1iHeYjTNZ\n\
vAhcExd1PRpV0207A4GFAAKBgQDNS3VPzSAL+I71/0EneTxLIyvAlROjnLVDd5LT\n\
vEAorjepo8v5/qgXNK4O32NlNZxSOD612Mr1Q8sLYDnx006t8x01A7St8f/jcd9y\n\
dIIomKMEs2hwahHt8p/jFdRJNXFFe4gQ2DM2cKRhZTEuL9qpv2AnPIIlGqnrlo1L\n\
o4gDb6OCAQEwgf4wCQYDVR0TBAIwADAsBglghkgBhvhCAQ0EHxYdT3BlblNTTCBH\n\
ZW5lcmF0ZWQgQ2VydGlmaWNhdGUwHQYDVR0OBBYEFA7Em1VK6/7qc88l7n8JnIOT\n\
QEArMIGjBgNVHSMEgZswgZiAFBKNX9CsAIsjUIFmVq4wE4wlOGC5oX2kezB5MQsw\n\
CQYDVQQGEwJBVTEMMAoGA1UECBMDVmljMRIwEAYDVQQHEwlNZWxib3VybmUxHzAd\n\
BgNVBAoTFlhNTC1TZWN1cml0eS1DIFByb2plY3QxEDAOBgNVBAsTB1hTRUMtQ0Ex\n\
FTATBgNVBAMTDFhTRUMtQ0EgUm9vdIIBADAJBgcqhkjOOAQDAy8AMCwCFDA7nNZe\n\
C6gSs+N7RRq7vLmx/IjjAhRJvfPZ/hvoN8fNpTmRoHtuzkSjcQ==";

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

	// Use xerces to parse the document
	XercesDOMParser * parser = new XercesDOMParser;
	parser->setDoNamespaces(true);
	parser->setCreateEntityReferenceNodes(true);
	parser->setDoSchema(true);

	// Create an input source

	MemBufInputSource* memIS = new MemBufInputSource ((const XMLByte*) docToValidate, strlen(docToValidate), "XSECMem");

	int errorCount = 0;

	parser->parse(*memIS);
    errorCount = parser->getErrorCount();
    if (errorCount > 0) {
		cerr << "Error parsing input document\n";
		exit (1);
	}

    DOMDocument *doc = parser->getDocument();

	// Find the Amount node
	DOMNode *amt = doc->getDocumentElement();

	if (amt != NULL)
		amt = amt->getFirstChild();

	while (amt != NULL && (amt->getNodeType() != DOMNode::ELEMENT_NODE || !strEquals(amt->getNodeName(), "Amount")))
		amt = amt->getNextSibling();

	if (amt != NULL)
		amt = amt->getFirstChild();

	if (amt == NULL || amt->getNodeType() != DOMNode::TEXT_NODE) {
		cerr << "Error finding amount in purchase order" << endl;
		exit (1);
	}

	docSetup(doc);

	// Now create a signature object to validate the document

	XSECProvider prov;

	DSIGSignature * sig = prov.newSignatureFromDOM(doc);


	try {
		// Use the OpenSSL interface objects to get a signing key

		OpenSSLCryptoX509 * x509 = new OpenSSLCryptoX509();
		x509->loadX509Base64Bin(cert, strlen(cert));
		
		sig->load();
		DSIGKeyInfoList * kinfList = sig->getKeyInfoList();
		
		// See if we can find a Key Name
		const XMLCh * kname;
		DSIGKeyInfoList::size_type size, i;
		size = kinfList->getSize();

		for (i = 0; i < size; ++i) {
			kname = kinfList->item(i)->getKeyName();
			if (kname != NULL) {
				char * n = XMLString::transcode(kname);
				cout << "Key Name = " << n << endl;
				delete[] n;
			}
		}

		sig->setSigningKey(x509->clonePublicKey());

		cout << "Amount = " << amt << " -> ";

		if (sig->verify()) {
			cout << "Signature Valid\n";
		}
		else {
			char * err = XMLString::transcode(sig->getErrMsgs());
			cout << "Incorrect Signature\n";
			cout << err << endl;
			delete[] err;
		}

		amt->setNodeValue(MAKE_UNICODE_STRING("$0.50"));
		
		cout << "Amount = " << amt << " -> ";

		if (sig->verify()) {
			cout << "Signature Valid\n";
		}
		else {
			char * err = XMLString::transcode(sig->getErrMsgs());
			cout << "Incorrect Signature\n";
			cout << err << endl;
			delete[] err;
		}

		amt->setNodeValue(MAKE_UNICODE_STRING("$16.50"));
		
		cout << "Amount = " << amt << " -> ";

		if (sig->verify()) {
			cout << "Signature Valid\n";
		}
		else {
			char * err = XMLString::transcode(sig->getErrMsgs());
			cout << "Incorrect Signature\n";
			cout << err << endl;
			delete[] err;
		}


	}

	catch (XSECException &e)
	{
		cerr << "An error occured during a signature load\n   Message: "
		<< e.getMsg() << endl;
		exit(1);
		
	}
	catch (XSECCryptoException &e) {
		cerr << "An error occured in the XML-Security-C Crypto routines\n   Message: "
		<< e.getMsg() << endl;
		exit(1);
	}
		
		// Clean up

	delete memIS;
	delete parser;

	return 0;
}
