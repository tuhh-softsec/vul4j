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
 * checkSig := tool to check a signature embedded in an XML file
 *
 * Author(s): Berin Lautenbach
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
#include <xsec/enc/OpenSSL/OpenSSLCryptoKeyHMAC.hpp>
#include <xsec/enc/XSECKeyInfoResolverDefault.hpp>

// ugly :<

#if defined(_WIN32)
#include <xsec/utils/winutils/XSECURIResolverGenericWin32.hpp>
#include <xsec/enc/WinCAPI/WinCAPICryptoProvider.hpp>
#else
#include <xsec/utils/unixutils/XSECURIResolverGenericUnix.hpp>
#endif

// General

#include <memory.h>
#include <string.h>
#include <iostream>
#include <stdlib.h>

#if defined(HAVE_UNISTD_H)
# include <unistd.h>
# define _MAX_PATH PATH_MAX
#else
# if defined(HAVE_DIRECT_H)
#  include <direct.h>
# endif
#endif

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

XSEC_USING_XERCES(XercesDOMParser);
XSEC_USING_XERCES(XMLException);
XSEC_USING_XERCES(XMLPlatformUtils);
XSEC_USING_XERCES(DOMException);
XSEC_USING_XERCES(XMLUri);
XSEC_USING_XERCES(Janitor);

using std::cerr;
using std::cout;
using std::endl;

#ifndef XSEC_NO_XALAN

// XALAN

#include <XPath/XPathEvaluator.hpp>
#include <XalanTransformer/XalanTransformer.hpp>

XALAN_USING_XALAN(XPathEvaluator)
XALAN_USING_XALAN(XalanTransformer)

#endif

// OpenSSL

#include <openssl/err.h>

#ifdef XSEC_NO_XALAN

ostream& operator<< (ostream& target, const XMLCh * s)
{
    char *p = XMLString::transcode(s);
    target << p;
    delete [] p;
    return target;
}

#endif

void printUsage(void) {

	cerr << "\nUsage: checksig [options] <input file name>\n\n";
	cerr << "     Where options are :\n\n";
	cerr << "     --skiprefs/-s\n";
	cerr << "         Skip checking references - check signature only\n\n";
	cerr << "     --hmackey/-h <string>\n";
	cerr << "         Set an hmac key using the <string>\n\n";
	cerr << "     --xsecresolver/-x\n";
	cerr << "         Use the xml-security test XMLDSig URI resolver\n\n";
#if defined(_WIN32)
	cerr << "     --wincapi/-w\n";
	cerr << "         Use the Windows CAPI crypto Provider\n\n";
#endif
	cerr << "     Exits with codes :\n";
	cerr << "         0 = Signature OK\n";
	cerr << "         1 = Signature Bad\n";
	cerr << "         2 = Processing error\n";

}

int evaluate(int argc, char ** argv) {
	
	char					* filename = NULL;
	char					* hmacKeyStr = NULL;
	OpenSSLCryptoKeyHMAC	* hmacKey;
	bool					useXSECURIResolver = false;
#if defined(_WIN32)
	HCRYPTPROV				win32CSP = 0;		// Crypto Provider
#endif

	bool skipRefs = false;

	if (argc < 2) {

		printUsage();
		return 2;
	}

	// Run through parameters
	int paramCount = 1;

	while (paramCount < argc - 1) {

		if (stricmp(argv[paramCount], "--hmackey") == 0 || stricmp(argv[paramCount], "-h") == 0) {
			paramCount++;
			hmacKeyStr = argv[paramCount++];
		}
		else if (stricmp(argv[paramCount], "--skiprefs") == 0 || stricmp(argv[paramCount], "-s") == 0) {
			skipRefs = true;
			paramCount++;
		}
		else if (stricmp(argv[paramCount], "--xsecresolver") == 0 || stricmp(argv[paramCount], "-x") == 0) {
			useXSECURIResolver = true;
			paramCount++;
		}
#if defined (_WIN32)
		else if (stricmp(argv[paramCount], "--wincapi") == 0 || stricmp(argv[paramCount], "-w") == 0) {
			WinCAPICryptoProvider * cp;
			// Obtain default PROV_DSS
			if (!CryptAcquireContext(&win32CSP,
				NULL,
				NULL,
				PROV_DSS,
				0)) {
					cerr << "Error acquiring DSS Crypto Service Provider" << endl;
					return 2;
			}

			// Use default DSS provider
			cp = new WinCAPICryptoProvider(win32CSP);
			XSECPlatformUtils::SetCryptoProvider(cp);
			paramCount++;
		
		}
#endif
		else {
			printUsage();
			return 2;
		}
	}

	if (paramCount >= argc) {
		printUsage();
		return 2;
	}

	filename = argv[paramCount];

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
    	parser->parse(filename);
        errorCount = parser->getErrorCount();
        if (errorCount > 0)
            errorsOccured = true;
    }

    catch (const XMLException& e)
    {
        cerr << "An error occured during parsing\n   Message: "
             << e.getMessage() << endl;
        errorsOccured = true;
    }


    catch (const DOMException& e)
    {
       cerr << "A DOM error occured during parsing\n   DOMException code: "
             << e.code << endl;
        errorsOccured = true;
    }

	if (errorsOccured) {

		cout << "Errors during parse" << endl;
		return (2);

	}

	/*

		Now that we have the parsed file, get the DOM document and start looking at it

	*/
	
	DOMNode *doc;		// The document that we parsed

	doc = parser->getDocument();
	DOMDocument *theDOM = parser->getDocument();

	// Find the signature node
	
	DOMNode *sigNode = findDSIGNode(doc, "Signature");

	// Create the signature checker

	if (sigNode == 0) {

		cerr << "Could not find <Signature> node in " << argv[argc-1] << endl;
		return 2;
	}

	XSECProvider prov;
	XSECKeyInfoResolverDefault theKeyInfoResolver;

	DSIGSignature * sig = prov.newSignatureFromDOM(theDOM, sigNode);

	// The only way we can verify is using keys read directly from the KeyInfo list,
	// so we add a KeyInfoResolverDefault to the Signature.

	sig->setKeyInfoResolver(&theKeyInfoResolver);

	// Check whether we should use the internal resolver

	
	if (useXSECURIResolver == true) {

#if defined(_WIN32)
		XSECURIResolverGenericWin32 
#else
		XSECURIResolverGenericUnix 
#endif
			theResolver;
		     
		// Map out base path of the file
		char path[_MAX_PATH];
		char baseURI[(_MAX_PATH * 2) + 10];
		getcwd(path, _MAX_PATH);

		strcpy(baseURI, "file:///");		

		// Ugly and nasty but quick
		if (filename[0] != '\\' && filename[0] != '/' && filename[1] != ':') {
			strcat(baseURI, path);
			strcat(baseURI, "/");
		} else if (path[1] == ':') {
			path[2] = '\0';
			strcat(baseURI, path);
		}

		strcat(baseURI, filename);

		// Find any ':' and "\" characters
		int lastSlash;
		for (unsigned int i = 8; i < strlen(baseURI); ++i) {
			if (baseURI[i] == '\\') {
				lastSlash = i;
				baseURI[i] = '/';
			}
			else if (baseURI[i] == '/')
				lastSlash = i;
		}

		// The last "\\" must prefix the filename
		baseURI[lastSlash + 1] = '\0';

		XMLUri uri(MAKE_UNICODE_STRING(baseURI));

		theResolver.setBaseURI(uri.getUriText());

		sig->setURIResolver(&theResolver);
	}



	bool result;

	try {

		// Load a key if necessary
		if (hmacKeyStr != NULL) {

			hmacKey = new OpenSSLCryptoKeyHMAC();
			hmacKey->setKey((unsigned char *) hmacKeyStr, strlen(hmacKeyStr));
			sig->setSigningKey(hmacKey);

		}

		sig->load();
		if (skipRefs)
			result = sig->verifySignatureOnly();
		else
			result = sig->verify();
	}

	catch (XSECException &e) {
		char * msg = XMLString::transcode(e.getMsg());
		cerr << "An error occured during signature verification\n   Message: "
		<< msg << endl;
		delete [] msg;
		errorsOccured = true;
		return 2;
	}
	catch (XSECCryptoException &e) {
		cerr << "An error occured during signature verification\n   Message: "
		<< e.getMsg() << endl;
		errorsOccured = true;

		ERR_load_crypto_strings();
		BIO * bio_err;
		if ((bio_err=BIO_new(BIO_s_file())) != NULL)
			BIO_set_fp(bio_err,stderr,BIO_NOCLOSE|BIO_FP_TEXT);

		ERR_print_errors(bio_err);
		return 2;
	}

	catch (...) {

		cerr << "Unknown Exception type occured.  Cleaning up and exiting\n" << endl;

		return 2;

	}

	int retResult;


	if (result) {
		cout << "Signature verified OK!" << endl;
		retResult = 0;
	}
	else {
		cout << "Signature failed verification" << endl;
		const char * e = XMLString::transcode(sig->getErrMsgs());
		cout << e << endl;
		delete [] (char *) e;
		retResult = 1;
	}

#if defined (_WIN32)
	if (win32CSP != 0) {
		CryptReleaseContext(win32CSP, 0);
	}
#endif
	prov.releaseSignature(sig);
	// Janitor will clean up the parser
	return retResult;

}


int main(int argc, char **argv) {

	int retResult;

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

	if ( _CrtMemDifference( &s3, &s1, &s2 ) ) {

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
