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
 * cipher := Tool to handle basic encryption/decryption of XML documents
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */

// XSEC

#include <xsec/utils/XSECPlatformUtils.hpp>
#include <xsec/framework/XSECProvider.hpp>
#include <xsec/framework/XSECException.hpp>
#include <xsec/enc/XSECCryptoException.hpp>
#include <xsec/enc/OpenSSL/OpenSSLCryptoSymmetricKey.hpp>

// ugly :<

#if defined(_WIN32)
#	include <xsec/utils/winutils/XSECURIResolverGenericWin32.hpp>
#else
#	include <xsec/utils/unixutils/XSECURIResolverGenericUnix.hpp>
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
#include <xercesc/util/XMLUni.hpp>
#include <xercesc/util/Janitor.hpp>
#include <xercesc/framework/XMLFormatter.hpp>
#include <xercesc/framework/StdOutFormatTarget.hpp>

XERCES_CPP_NAMESPACE_USE

using std::cerr;
using std::cout;
using std::endl;

#ifndef XSEC_NO_XALAN

// XALAN

#include <xalanc/XPath/XPathEvaluator.hpp>
#include <xalanc/XalanTransformer/XalanTransformer.hpp>

XALAN_USING_XALAN(XPathEvaluator)
XALAN_USING_XALAN(XalanTransformer)

#endif

#if defined (HAVE_OPENSSL)
// OpenSSL

#	include <xsec/enc/OpenSSL/OpenSSLCryptoKeyHMAC.hpp>
#	include <openssl/err.h>

#endif

#if defined (HAVE_WINCAPI)

#	include <xsec/enc/WinCAPI/WinCAPICryptoProvider.hpp>
#	include <xsec/enc/WinCAPI/WinCAPICryptoKeyHMAC.hpp>

#endif

#include <time.h>

#ifdef XSEC_NO_XALAN

std::ostream& operator<< (std::ostream& target, const XMLCh * s)
{
    char *p = XMLString::transcode(s);
    target << p;
    delete [] p;
    return target;
}

#endif

// ----------------------------------------------------------------------------
//           Checksig
// ----------------------------------------------------------------------------


void printUsage(void) {

	cerr << "\nUsage: cipher [options] <input file name>\n\n";
	cerr << "     Where options are :\n\n";
	cerr << "     --decrypt-element/-de\n";
	cerr << "         Decrypt the first encrypted element found\n";
	cerr << "     --key/-k [key string]\n";
	cerr << "         Use the key provided in [key string] to encrypt/decrypt\n\n";

	cerr << "     Exits with codes :\n";
	cerr << "         0 = Decrypt/Encrypt OK\n";
	cerr << "         1 = Decrypt/Encrypt failed\n";
	cerr << "         2 = Processing error\n";

}

int evaluate(int argc, char ** argv) {
	
	char					* filename = NULL;
	char					* keyStr = NULL;
	bool					doDecryptElement = false;

#if defined(_WIN32) && defined (HAVE_WINCAPI)
	HCRYPTPROV				win32DSSCSP = 0;		// Crypto Providers
	HCRYPTPROV				win32RSACSP = 0;		
#endif

	if (argc < 2) {

		printUsage();
		return 2;
	}

	// Run through parameters
	int paramCount = 1;

	while (paramCount < argc - 1) {

		if (stricmp(argv[paramCount], "--decrypt-element") == 0 || stricmp(argv[paramCount], "-de") == 0) {
			paramCount++;
			doDecryptElement = true;
		}

		if (stricmp(argv[paramCount], "--key") == 0 || stricmp(argv[paramCount], "-k") == 0) {

			// Have set a key string
			paramCount++;
			keyStr = argv[paramCount];
			paramCount++;
		}

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

	XSECProvider prov;

	// Find the EncryptedData node
	DOMNode * n = findXENCNode(doc, "EncryptedData");

	XENCCipher * cipher = prov.newCipher(theDOM);

	try {

		OpenSSLCryptoSymmetricKey * k;
		if (keyStr != NULL) {
			k = new OpenSSLCryptoSymmetricKey(XSECCryptoSymmetricKey::KEY_3DES_CBC_192);
			k->setKey((unsigned char *) keyStr, strlen(keyStr));
			cipher->setKey(k);
		}
		cipher->decryptElement(static_cast<DOMElement *>(n));

		// Output the result

		XMLCh core[] = {
			XERCES_CPP_NAMESPACE :: chLatin_C,
			XERCES_CPP_NAMESPACE :: chLatin_o,
			XERCES_CPP_NAMESPACE :: chLatin_r,
			XERCES_CPP_NAMESPACE :: chLatin_e,
			XERCES_CPP_NAMESPACE :: chNull
		};

		DOMImplementation *impl = DOMImplementationRegistry::getDOMImplementation(core);
		DOMWriter         *theSerializer = ((DOMImplementationLS*)impl)->createDOMWriter();

		theSerializer->setEncoding(MAKE_UNICODE_STRING("UTF-8"));
		if (theSerializer->canSetFeature(XMLUni::fgDOMWRTFormatPrettyPrint, false))
			theSerializer->setFeature(XMLUni::fgDOMWRTFormatPrettyPrint, false);


		XMLFormatTarget *formatTarget = new StdOutFormatTarget();

		theSerializer->writeNode(formatTarget, *doc);
		
		cout << endl;

		delete theSerializer;
		delete formatTarget;


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

#if defined (HAVE_OPENSSL)
		ERR_load_crypto_strings();
		BIO * bio_err;
		if ((bio_err=BIO_new(BIO_s_file())) != NULL)
			BIO_set_fp(bio_err,stderr,BIO_NOCLOSE|BIO_FP_TEXT);

		ERR_print_errors(bio_err);
#endif
		return 2;
	}

	return 0;
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

	if ( _CrtMemDifference( &s3, &s1, &s2 ) && (
		s3.lCounts[0] > 0 ||
		s3.lCounts[1] > 1 ||
		// s3.lCounts[2] > 2 ||  We don't worry about C Runtime
		s3.lCounts[3] > 0 ||
		s3.lCounts[4] > 0)) {

		// Note that there is generally 1 Normal and 1 CRT block
		// still taken.  1 is from Xalan and 1 from stdio

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
