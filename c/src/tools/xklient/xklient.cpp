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
#include <xsec/dsig/DSIGKeyInfoX509.hpp>
#include <xsec/dsig/DSIGKeyInfoValue.hpp>
#include <xsec/framework/XSECException.hpp>
#include <xsec/enc/XSECCryptoException.hpp>
#include <xsec/utils/XSECDOMUtils.hpp>
#include <xsec/enc/XSECKeyInfoResolverDefault.hpp>

#include <xsec/xkms/XKMSMessageAbstractType.hpp>
#include <xsec/xkms/XKMSLocateRequest.hpp>
#include <xsec/xkms/XKMSLocateResult.hpp>
#include <xsec/xkms/XKMSQueryKeyBinding.hpp>
#include <xsec/xkms/XKMSUseKeyWith.hpp>
#include <xsec/xkms/XKMSConstants.hpp>

#include <xsec/utils/XSECSOAPRequestorSimple.hpp>

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
#include <xercesc/framework/StdOutFormatTarget.hpp>
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

XALAN_USING_XALAN(XPathEvaluator)
XALAN_USING_XALAN(XalanTransformer)

#endif

#if defined (HAVE_OPENSSL)
// OpenSSL

#	include <openssl/err.h>

#endif

// --------------------------------------------------------------------------------
//           Global definitions
// --------------------------------------------------------------------------------

bool g_txtOut = false;

int doParsedMsgDump(DOMDocument * doc);

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

void outputDoc(DOMDocument * doc) {

	XMLCh tempStr[100];
	XMLString::transcode("Core", tempStr, 99);    
	DOMImplementation *impl = DOMImplementationRegistry::getDOMImplementation(tempStr);

	DOMWriter         *theSerializer = ((DOMImplementationLS*)impl)->createDOMWriter();

	theSerializer->setEncoding(MAKE_UNICODE_STRING("UTF-8"));
	if (theSerializer->canSetFeature(XMLUni::fgDOMWRTFormatPrettyPrint, false))
		theSerializer->setFeature(XMLUni::fgDOMWRTFormatPrettyPrint, false);


	XMLFormatTarget *formatTarget = new StdOutFormatTarget();

	cerr << endl;

	theSerializer->writeNode(formatTarget, *doc);
	
	cout << endl;

	cerr << endl;

	delete theSerializer;
	delete formatTarget;

}

XSECCryptoX509 * loadX509(const char * infile) {

	FILE * f = fopen(infile, "r");
	if (f == NULL)
		return NULL;

	safeBuffer sb;
	char buf[1024];

	int i = fread(buf, 1, 1024, f);
	int j = 0;
	while (i != 0) {
		sb.sbMemcpyIn(j, buf, i);
		j += i;
		i = fread(buf, 1, 1024, f);
	}

	sb[j] = '\0';

	XSECCryptoX509 * ret = 
		XSECPlatformUtils::g_cryptoProvider->X509();

	ret->loadX509PEM(sb.rawCharBuffer());

	return ret;

}

// --------------------------------------------------------------------------------
//           Create a LocateRequest
// --------------------------------------------------------------------------------

void printLocateRequestUsage(void) {

	cerr << "\nUsage LocateRequest [--help|-h] <service URI> [options]\n";
	cerr << "   --help/-h                : print this screen and exit\n\n";
	cerr << "   --add-cert/-a <filename> : add cert in filename as a KeyInfo\n";
	cerr << "   --add-name/-n <name>     : Add name as a KeyInfoName\n\n";
	cerr << "   --add-usekeywith/-u <Application URI> <Identifier>\n";
	cerr << "                            : Add a UseKeyWith element\n\n";

}

XKMSMessageAbstractType * createLocateRequest(XSECProvider &prov, DOMDocument **doc, int argc, char ** argv, int paramCount) {

	if (paramCount >= argc || 
		(stricmp(argv[paramCount], "--help") == 0) ||
		(stricmp(argv[paramCount], "-h") == 0)) {

		printLocateRequestUsage();
		return NULL;
	}

	/* First create the basic request */
	XKMSMessageFactory * factory = 
		prov.getXKMSMessageFactory();
	XKMSLocateRequest * lr = 
		factory->createLocateRequest(MAKE_UNICODE_STRING(argv[paramCount++]), doc);

	while (paramCount < argc) {

		if (stricmp(argv[paramCount], "--add-cert") == 0 || stricmp(argv[paramCount], "-a") == 0) {
			if (++paramCount >= argc) {
				printLocateRequestUsage();
				delete lr;
				return NULL;
			}
			XSECCryptoX509 * x = loadX509(argv[paramCount]);
			if (x == NULL) {
				delete lr;
				(*doc)->release();
				cerr << "Error opening Certificate file : " << 
					argv[paramCount] << endl;
				return NULL;
			}

			Janitor<XSECCryptoX509> j_x(x);

			XKMSQueryKeyBinding * qkb = lr->addQueryKeyBinding();
			DSIGKeyInfoX509 * kix = qkb->appendX509Data();
			safeBuffer sb = x->getDEREncodingSB();
			kix->appendX509Certificate(sb.sbStrToXMLCh());
			paramCount++;
		}

		else if (stricmp(argv[paramCount], "--add-name") == 0 || stricmp(argv[paramCount], "-n") == 0) {
			if (++paramCount >= argc) {
				printLocateRequestUsage();
				delete lr;
				return NULL;
			}
			XKMSQueryKeyBinding * qkb = lr->addQueryKeyBinding();
			qkb->appendKeyName(MAKE_UNICODE_STRING(argv[paramCount]));
			paramCount++;
		}
		else if (stricmp(argv[paramCount], "--add-usekeywith") == 0 || stricmp(argv[paramCount], "-u") == 0) {
			if (++paramCount >= argc + 1) {
				printLocateRequestUsage();
				delete lr;
				return NULL;
			}
			XKMSQueryKeyBinding *qkb = lr->getQueryKeyBinding();
			if (qkb == NULL)
				qkb = lr->addQueryKeyBinding();

			qkb->appendUseKeyWithItem(MAKE_UNICODE_STRING(argv[paramCount]), MAKE_UNICODE_STRING(argv[paramCount + 1]));
			paramCount += 2;
		}
		else {
			printLocateRequestUsage();
			delete lr;
			(*doc)->release();
			return NULL;
		}
	}

	return lr;
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

void doResultTypeDump(XKMSResultType *msg, int level) {

	const XMLCh * rid = msg->getRequestId();
	char * s;

	if (rid != NULL) {
		levelSet(level);
		cout << "Result is in response to MsgID : ";
		s = XMLString::transcode(rid);
		cout << s << endl;
		XMLString::release(&s);
	}

	levelSet(level);
	cout << "Result Major code = ";
	s = XMLString::transcode(XKMSConstants::s_tagResultMajorCodes[msg->getResultMajor()]);
	cout << s << endl;
	XMLString::release(&s);

	XKMSResultType::ResultMinor rm = msg->getResultMinor();
	if (rm != XKMSResultType::NoneMinor) {
		levelSet(level);
		cout << "Result Minor code = ";
		char * s = XMLString::transcode(XKMSConstants::s_tagResultMinorCodes[rm]);
		cout << s << endl;
		XMLString::release(&s);
	}

}

void doKeyInfoDump(DSIGKeyInfoList * l, int level) {


	int size = l->getSize();

	for (int i = 0 ; i < size ; ++ i) {

		DSIGKeyInfoValue * kiv;
		char * b;

		DSIGKeyInfo * ki = l->item(i);

		switch (ki->getKeyInfoType()) {

		case DSIGKeyInfo::KEYINFO_VALUE_RSA :

			kiv = (DSIGKeyInfoValue *) ki;
			levelSet(level);
			cout << "RSA Key Value" << endl;

			levelSet(level+1);
			b = XMLString::transcode(kiv->getRSAExponent());
			cout << "Base64 encoded exponent = " << b << endl;
			delete[] b;

			levelSet(level+1);
			b = XMLString::transcode(kiv->getRSAModulus());
			cout << "Base64 encoded modulus  = " << b << endl;
			delete[] b;

			break;

		case DSIGKeyInfo::KEYINFO_VALUE_DSA :

			kiv = (DSIGKeyInfoValue *) ki;
			levelSet(level);
			cout << "DSA Key Value" << endl;

			levelSet(level+1);
			b = XMLString::transcode(kiv->getDSAG());
			cout << "G = " << b << endl;
			delete[] b;

			levelSet(level+1);
			b = XMLString::transcode(kiv->getDSAP());
			cout << "P = " << b << endl;
			delete[] b;

			levelSet(level+1);
			b = XMLString::transcode(kiv->getDSAQ());
			cout << "Q = " << b << endl;
			delete[] b;

			levelSet(level+1);
			b = XMLString::transcode(kiv->getDSAY());
			cout << "Y = " << b << endl;
			delete[] b;

			break;

		default:

			levelSet(level);
			cout << "Unknown KeyInfo type" << endl;

		}

	}
}


void doKeyBindingDump(XKMSKeyBindingAbstractType * msg, int level) {

	levelSet(level);
	cout << "Key Binding found." << endl;

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

	int n = msg->getUseKeyWithSize();
	levelSet(level+1);
	if (n == 0) {
		cout << "No UseKeyWith items found" << endl;
	}
	else {
		cout << "UseKeyWith items : \n";
	}

	for (int i = 0; i < msg->getUseKeyWithSize() ; ++i) {

		XKMSUseKeyWith * ukw = msg->getUseKeyWithItem(i);
		levelSet(level+2);
		char * a = XMLString::transcode(ukw->getApplication());
		char * i = XMLString::transcode(ukw->getIdentifier());
		cout << "Application : \"" << a << "\"\n";
		levelSet(level+2);
		cout << "Identifier  : \"" << i << "\"" << endl;
		XMLString::release(&a);
		XMLString::release(&i);

	}

	// Now dump any KeyInfo
	levelSet(level+1);
	cout << "KeyInfo information:" << endl << endl;

	doKeyInfoDump(msg->getKeyInfoList(), level + 2);

}

void doUnverifiedKeyBindingDump(XKMSUnverifiedKeyBinding * ukb, int level) {

	doKeyBindingDump((XKMSKeyBindingAbstractType *) ukb, level);

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

int doLocateResultDump(XKMSLocateResult *msg) {

	cout << endl << "This is a LocateResult Message" << endl;
	int level = 1;
	
	doMessageAbstractTypeDump(msg, level);
	doResultTypeDump(msg, level);

	int j;

	if ((j = msg->getUnverifiedKeyBindingSize()) > 0) {

		cout << endl;
		levelSet(level);
		cout << "Unverified Key Bindings" << endl << endl;

		for (int i = 0; i < j ; ++i) {

			doUnverifiedKeyBindingDump(msg->getUnverifiedKeyBindingItem(i), level + 1);

		}

	}

	return 0;
}


int doParsedMsgDump(DOMDocument * doc) {

	// Get an XKMS Message Factory
	XSECProvider prov;
	XKMSMessageFactory * factory = prov.getXKMSMessageFactory();
	int errorsOccured;

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

		case XKMSMessageAbstractType::LocateResult :

			doLocateResultDump(dynamic_cast<XKMSLocateResult *>(msg));
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
//           Base MessageCreate module
// --------------------------------------------------------------------------------

void printMsgCreateUsage(void) {

	cerr << "\nUsage messagecreate [options] {LocateRequest} [msg specific options]\n";
	cerr << "   --help/-h    : print this screen and exit\n\n";

}

int doMsgCreate(int argc, char ** argv, int paramCount) {

	XSECProvider prov;
	DOMDocument * doc;
	XKMSMessageAbstractType *msg;

	if (paramCount >= argc || 
		(stricmp(argv[paramCount], "--help") == 0) ||
		(stricmp(argv[paramCount], "-h") == 0)) {
		printMsgCreateUsage();
		return -1;
	}

	if ((stricmp(argv[paramCount], "LocateRequest") == 0) ||
		(stricmp(argv[paramCount], "lr") == 0)) {

		msg = createLocateRequest(prov, &doc, argc, argv, paramCount + 1);
		if (msg == NULL) {
			return -1;
		}

	}
	else {

		printMsgCreateUsage();
		return -1;

	}

	outputDoc(doc);
	
	// Cleanup message stuff
	delete msg;
	doc->release();

	return 0;

}


// --------------------------------------------------------------------------------
//           Base request module
// --------------------------------------------------------------------------------

void printDoRequestUsage(void) {

	cerr << "\nUsage request [options] {LocateRequest} [msg specific options]\n";
	cerr << "   --help/-h    : print this screen and exit\n\n";

}

int doRequest(int argc, char ** argv, int paramCount) {

	XSECProvider prov;
	DOMDocument * doc;
	XKMSMessageAbstractType *msg;

	if (paramCount >= argc || 
		(stricmp(argv[paramCount], "--help") == 0) ||
		(stricmp(argv[paramCount], "-h") == 0)) {
		printDoRequestUsage();
		return -1;
	}

	if ((stricmp(argv[paramCount], "LocateRequest") == 0) ||
		(stricmp(argv[paramCount], "lr") == 0)) {

		msg = createLocateRequest(prov, &doc, argc, argv, paramCount + 1);
		if (msg == NULL) {
			return -1;
		}

	}
	else {

		printDoRequestUsage();
		return -1;

	}

	try {
		if (g_txtOut) {
			outputDoc(doc);
		}
	}
	catch (...) {
		delete msg;
		doc->release();
		throw;
	}

	DOMDocument * responseDoc;

	try {
		XSECSOAPRequestorSimple req(msg->getService());
		responseDoc = req.doRequest(doc);
	}
	catch (XSECException &e) {

		char * m = XMLString::transcode(e.getMsg());
		cerr << "Error sending request: " << m;
		XMLString::release(&m);

		delete msg;
		doc->release();

		return -1;

	}
	catch (...) {
		delete msg;
		doc->release();

		throw;

	}

	// Cleanup request stuff
	delete msg;
	doc->release();

	// Now lets process the result

	int ret;
	
	try {
		if (g_txtOut) {
			outputDoc(responseDoc);
		}
		ret = doParsedMsgDump(responseDoc);
	}
	catch (...) {
		responseDoc->release();
		throw;
	}
	
	responseDoc->release();
	return ret;

}


// --------------------------------------------------------------------------------
//           Base msgdump module
// --------------------------------------------------------------------------------


void printMsgDumpUsage(void) {

	cerr << "\nUsage msgdump [options] <filename>\n";
	cerr << "   --help/-h    : print this screen and exit\n\n";
    cerr << "   filename = name of file containing XKMS msg to dump\n\n";

}

int doMsgDump(int argc, char ** argv, int paramCount) {

	char * inputFile = NULL;

	if (paramCount != (argc - 1) || 
		(stricmp(argv[paramCount], "--help") == 0) ||
		(stricmp(argv[paramCount], "-h") ==0)) {
		printMsgDumpUsage();
		return -1;
	}

	inputFile = argv[paramCount];

	// Dump the details of an XKMS message to the console
	cout << "Decoding XKMS Message contained in " << inputFile << endl;
	
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
    	parser->parse(inputFile);
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

	return doParsedMsgDump(doc);
}


// --------------------------------------------------------------------------------
//           Startup and main
// --------------------------------------------------------------------------------

void printUsage(void) {

	cerr << "\nUsage: xklient [base options] {msgdump|msgcreate|dorequest} [command specific options]\n\n";
	cerr << "     msgdump   : Read an XKMS message and print details\n";
	cerr << "     msgcreate : Create a message of type :\n";
	cerr << "                 LocateRequest\n";
	cerr << "     request   : Create message of type : \n";
	cerr << "                 LocateRequest\n";
	cerr << "                 send to service URI and output result\n\n";
	cerr << "     Where options are :\n\n";
	cerr << "     --text/-t\n";
	cerr << "         Print any created XML to screen\n";

}

int evaluate(int argc, char ** argv) {
	
	if (argc < 2) {

		printUsage();
		return 2;
	}

	int paramCount = 1;

	// Run through parameters

	while (paramCount < argc) {

		if (stricmp(argv[paramCount], "--text") == 0 || stricmp(argv[paramCount], "-t") == 0) {
			g_txtOut = true;
			paramCount++;
		}
		else if (stricmp(argv[paramCount], "MsgDump") == 0 || stricmp(argv[paramCount], "md") == 0) {
			
			// Perform a MsgDump operation
			return doMsgDump(argc, argv, paramCount +1);

		}
		else if (stricmp(argv[paramCount], "MsgCreate") == 0 || stricmp(argv[paramCount], "mc") == 0) {
			
			// Perform a MsgDump operation
			return doMsgCreate(argc, argv, paramCount +1);

		}
		else if (stricmp(argv[paramCount], "Request") == 0 || stricmp(argv[paramCount], "req") == 0) {
			
			// Perform a MsgDump operation
			return doRequest(argc, argv, paramCount +1);

		}
		else {
			printUsage();
			return 2;
		}
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
