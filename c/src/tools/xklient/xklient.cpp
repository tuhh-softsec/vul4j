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
 * xklient := Act as a client for an XKMS service
 *
 * $Id$
 *
 */

// XSEC

#include <xsec/utils/XSECPlatformUtils.hpp>
#include <xsec/framework/XSECProvider.hpp>
#include <xsec/canon/XSECC14n20010315.hpp>
#include <xsec/dsig/DSIGSignature.hpp>
#include <xsec/framework/XSECException.hpp>
#include <xsec/enc/XSECCryptoException.hpp>
#include <xsec/utils/XSECDOMUtils.hpp>
#include <xsec/enc/XSECKeyInfoResolverDefault.hpp>

#include <xsec/xkms/XKMSMessageAbstractType.hpp>
#include <xsec/xkms/XKMSLocateRequest.hpp>
#include <xsec/xkms/XKMSQueryKeyBinding.hpp>

// General

#include <memory.h>
#include <string.h>
#include <iostream>
#include <stdlib.h>

#if defined (_DEBUG) && defined (_MSC_VER)
#include <crtdbg.h>
#endif


#include <xercesc/util/PlatformUtils.hpp>
#include <xercesc/util/XMLString.hpp>

#include <xercesc/dom/DOM.hpp>
#include <xercesc/parsers/XercesDOMParser.hpp>
#include <xercesc/util/XMLException.hpp>
#include <xercesc/util/XMLUri.hpp>
#include <xercesc/util/Janitor.hpp>

XERCES_CPP_NAMESPACE_USE

using std::cerr;
using std::cout;
using std::endl;
using std::ostream;

#ifndef XSEC_NO_XALAN

// XALAN

#include <xalanc/XPath/XPathEvaluator.hpp>
#include <xalanc/XalanTransformer/XalanTransformer.hpp>

#if defined (HAVE_OPENSSL)
// OpenSSL

#	include <openssl/err.h>

#endif

XALAN_USING_XALAN(XPathEvaluator)
XALAN_USING_XALAN(XalanTransformer)

#endif

// --------------------------------------------------------------------------------
//           Global definitions
// --------------------------------------------------------------------------------

enum eProcessingMode {

	ModeNone,			// No mode yet set
	ModeMsgDump			// We are here to simply parse and dump an XKMS message

};

char * g_inputFile = NULL;

// --------------------------------------------------------------------------------
//           General functions
// --------------------------------------------------------------------------------


#ifdef XSEC_NO_XALAN

ostream& operator<< (ostream& target, const XMLCh * s)
{
    char *p = XMLString::transcode(s);
    target << p;
    XMLString::release(&p);
    return target;
}

#endif

class X2C {

public:

	X2C(const XMLCh * in) {
		mp_cStr = XMLString::transcode(in);
	}
	~X2C() {
		XMLString::release(&mp_cStr);
	}

	char * str(void) {
		return mp_cStr;
	}

private :

	char * mp_cStr;

};

ostream & operator<<(ostream& target, X2C &x) {
	target << x.str();
	return target;
}

inline
void levelSet(int level) {

	for (int i = 0; i < level; ++i)
		cout << "    ";

}

// --------------------------------------------------------------------------------
//           MsgDump
// --------------------------------------------------------------------------------

void doMessageAbstractTypeDump(XKMSMessageAbstractType *msg, int level) {

	cout << endl;
	levelSet(level);
	cout << "Base message information : " << endl << endl;

	char * s = XMLString::transcode(msg->getId());
	levelSet(level);
	cout << "Id = " << s << endl;
	XMLString::release(&s);

	s = XMLString::transcode(msg->getService());
	levelSet(level);
	cout << "Service URI = " << s << endl;
	XMLString::release(&s);

	s = XMLString::transcode(msg->getNonce());
	levelSet(level);
	if (s != NULL) {
		cout << "Nonce = " << s << endl;
		XMLString::release(&s);
	}
	else
		cout << "Nonce = <NONE SET>" << endl;
}

void doRequestAbstractTypeDump(XKMSRequestAbstractType *msg, int level) {

	levelSet(level);
	int i = msg->getRespondWithSize();

	cout << "Request message has " << i << " RespondWith elements" << endl << endl;

	for (int j = 0; j < i; ++j) {
		levelSet(level +1);
		char * s = XMLString::transcode(msg->getRespondWithItemStr(j));
		cout << "Item " << j+1 << " : " << s << endl;
		XMLString::release(&s);
	}
	
}

void doKeyBindingDump(XKMSKeyBindingAbstractType * msg, int level) {

	cout << endl;
	levelSet(level);
	cout << "Key Binding found." << endl;

	if (msg->getKeyInfoList() != NULL) {

		levelSet(level+1);
		cout << "Has KeyInfo element" << endl;

	}

	levelSet(level+1);
	cout << "KeyUsage Encryption : ";
	if (msg->getEncryptionKeyUsage())
		cout << "yes" << endl;
	else
		cout << "no" << endl;

	levelSet(level+1);
	cout << "KeyUsage Exchange   : ";
	if (msg->getExchangeKeyUsage())
		cout << "yes" << endl;
	else
		cout << "no" << endl;

	levelSet(level+1);
	cout << "KeyUsage Signature  : ";
	if (msg->getSignatureKeyUsage())
		cout << "yes" << endl;
	else
		cout << "no" << endl;

}

int doLocateRequestDump(XKMSLocateRequest *msg) {

	cout << endl << "This is a LocateRequest Message" << endl;
	int level = 1;
	
	doMessageAbstractTypeDump(msg, level);
	doRequestAbstractTypeDump(msg, level);

	XKMSQueryKeyBinding *qkb = msg->getQueryKeyBinding();
	if (qkb != NULL)
		doKeyBindingDump(qkb, level);

	return 0;
}


int doMsgDump(void) {

	// Dump the details of an XKMS message to the console
	cout << "Decoding XKMS Message contained in " << g_inputFile << endl;
	
	// Create and set up the parser

	XercesDOMParser * parser = new XercesDOMParser;
	Janitor<XercesDOMParser> j_parser(parser);

	parser->setDoNamespaces(true);
	parser->setCreateEntityReferenceNodes(true);

	// Now parse out file

	bool errorsOccured = false;
	int errorCount = 0;
    try
    {
    	parser->parse(g_inputFile);
        errorCount = parser->getErrorCount();
    }

    catch (const XMLException& e)
    {
		char * msg = XMLString::transcode(e.getMessage());
        cerr << "An error occured during parsing\n   Message: "
             << msg << endl;
		XMLString::release(&msg);
        errorsOccured = true;
    }


    catch (const DOMException& e)
    {
       cerr << "A DOM error occured during parsing\n   DOMException code: "
             << e.code << endl;
        errorsOccured = true;
    }

	if (errorCount > 0 || errorsOccured) {

		cout << "Errors during parse" << endl;
		return (2);

	}

	/*

		Now that we have the parsed file, get the DOM document and start looking at it

	*/
	
	DOMDocument *doc = parser->getDocument();

	// Get an XKMS Message Factory
	XSECProvider prov;
	XKMSMessageFactory * factory = prov.getXKMSMessageFactory();

	try {

		XKMSMessageAbstractType * msg =
			factory->newMessageFromDOM(doc->getDocumentElement());

		Janitor <XKMSMessageAbstractType> j_msg(msg);

		if (msg->isSigned()) {

			cout << "Message is signed.  Checking signature ... ";
			try {

				XSECKeyInfoResolverDefault theKeyInfoResolver;
				DSIGSignature * sig = msg->getSignature();

				// The only way we can verify is using keys read directly from the KeyInfo list,
				// so we add a KeyInfoResolverDefault to the Signature.

				sig->setKeyInfoResolver(&theKeyInfoResolver);

				if (sig->verify())
					cout << "OK!" << endl;
				else
					cout << "Bad!" << endl;

			}
		
			catch (XSECException &e) {
				cout << "Bad!.  Caught exception : " << endl;
				char * msg = XMLString::transcode(e.getMsg());
				cout << msg << endl;
				XMLString::release(&msg);
			}
		}

		switch (msg->getMessageType()) {

		case XKMSMessageAbstractType::LocateRequest :

			doLocateRequestDump(dynamic_cast<XKMSLocateRequest *>(msg));
			break;

		default :

			cout << "Unknown message type!" << endl;

		}


	}

	catch (XSECException &e) {
		char * msg = XMLString::transcode(e.getMsg());
		cerr << "An error occured during message loading\n   Message: "
		<< msg << endl;
		XMLString::release(&msg);
		errorsOccured = true;
		return 2;
	}
	catch (XSECCryptoException &e) {
		cerr << "An error occured during encryption/signature processing\n   Message: "
		<< e.getMsg() << endl;
		errorsOccured = true;

#if defined (HAVE_OPENSSL)
		ERR_load_crypto_strings();
		BIO * bio_err;
		if ((bio_err=BIO_new(BIO_s_file())) != NULL)
			BIO_set_fp(bio_err,stderr,BIO_NOCLOSE|BIO_FP_TEXT);

		ERR_print_errors(bio_err);
#endif

		return 2;
	}
	catch (...) {

		cerr << "Unknown Exception type occured.  Cleaning up and exiting\n" << endl;
		return 2;

	}

	// Clean up

	return 0;

}


// --------------------------------------------------------------------------------
//           Startup and main
// --------------------------------------------------------------------------------

void printUsage(void) {

	cerr << "\nUsage: siging {msgdump} [options]\n\n";
	cerr << "     msgdump : Read an XKMS message and print details\n";
	cerr << "     Where options are :\n\n";
	cerr << "     --infile/-i {filename}\n";
	cerr << "         File to read as input\n\n";

}

int evaluate(int argc, char ** argv) {
	
	eProcessingMode			mode = ModeNone;

	if (argc < 2) {

		printUsage();
		return 2;
	}

	if (!stricmp (argv[1], "msgdump")) {

		mode = ModeMsgDump;

	}

	else {

		printUsage();
		return 2;

	}

	// Run through parameters
	int paramCount = 2;

	while (paramCount < argc) {

		if (stricmp(argv[paramCount], "--infile") == 0 || stricmp(argv[paramCount], "-i") == 0) {
			if (++paramCount >= argc) {
				printUsage();
				return 2;
			}
			g_inputFile = argv[paramCount];
			paramCount++;
		}
		else {
			printUsage();
			return 2;
		}
	}

	switch (mode) {

	case ModeMsgDump :

		return doMsgDump();
		
	default :

		cerr << "Catastrophic error - somehow I don't know what I'm doing!\n";
		return 3;

	}

	return 1;

}
	


int main(int argc, char **argv) {

	int retResult;

	/* We output a version number to overcome a "feature" in Microsoft's memory
	   leak detection */

	cout << "XKMS Client (Using Apache XML-Security-C Library v" << XSEC_VERSION_MAJOR <<
		"." << XSEC_VERSION_MEDIUM << "." << XSEC_VERSION_MINOR << ")\n";

#if defined (_DEBUG) && defined (_MSC_VER)

	// Do some memory debugging under Visual C++

	_CrtMemState s1, s2, s3;

	// At this point we are about to start really using XSEC, so
	// Take a "before" checkpoing

	_CrtMemCheckpoint( &s1 );

#endif

	// Initialise the XML system

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

	retResult = evaluate(argc, argv);

	XSECPlatformUtils::Terminate();
#ifndef XSEC_NO_XALAN
	XalanTransformer::terminate();
	XPathEvaluator::terminate();
#endif
	XMLPlatformUtils::Terminate();

#if defined (_DEBUG) && defined (_MSC_VER)

	_CrtMemCheckpoint( &s2 );

	if ( _CrtMemDifference( &s3, &s1, &s2 ) && s3.lCounts[1] > 1) {

		std::cerr << "Total count = " << (unsigned int) s3.lTotalCount << endl;

		// Send all reports to STDOUT
		_CrtSetReportMode( _CRT_WARN, _CRTDBG_MODE_FILE );
		_CrtSetReportFile( _CRT_WARN, _CRTDBG_FILE_STDOUT );
		_CrtSetReportMode( _CRT_ERROR, _CRTDBG_MODE_FILE );
		_CrtSetReportFile( _CRT_ERROR, _CRTDBG_FILE_STDOUT );
		_CrtSetReportMode( _CRT_ASSERT, _CRTDBG_MODE_FILE );
		_CrtSetReportFile( _CRT_ASSERT, _CRTDBG_FILE_STDOUT );

		// Dumpy memory stats

 		_CrtMemDumpAllObjectsSince( &s3 );
	    _CrtMemDumpStatistics( &s3 );
	}

	// Now turn off memory leak checking and end as there are some 
	// Globals that are allocated that get seen as leaks (Xalan?)

	int dbgFlag = _CrtSetDbgFlag(_CRTDBG_REPORT_FLAG);
	dbgFlag &= ~(_CRTDBG_LEAK_CHECK_DF);
	_CrtSetDbgFlag( dbgFlag );

#endif

	return retResult;
}
