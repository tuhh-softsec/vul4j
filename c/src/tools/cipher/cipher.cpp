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
#include <xsec/utils/XSECBinTXFMInputStream.hpp>
#include <xsec/xenc/XENCEncryptedData.hpp>
#include <xsec/xenc/XENCEncryptedKey.hpp>

#include "MerlinFiveInteropResolver.hpp"

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
#include <fstream>
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
#include <xercesc/util/BinFileInputStream.hpp>
#include <xercesc/framework/XMLFormatter.hpp>
#include <xercesc/framework/StdOutFormatTarget.hpp>
#include <xercesc/framework/LocalFileFormatTarget.hpp>

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

#if !defined (HAVE_OPENSSL) && !defined(HAVE_WINCAPI)
#	error No available cryptoAPI
#endif

#if defined (HAVE_OPENSSL)
// OpenSSL

#	include <xsec/enc/OpenSSL/OpenSSLCryptoKeyHMAC.hpp>
#	include <xsec/enc/OpenSSL/OpenSSLCryptoKeyRSA.hpp>
#	include <openssl/err.h>
#	include <openssl/bio.h>
#	include <openssl/evp.h>
#	include <openssl/pem.h>

#endif

#if defined (HAVE_WINCAPI)

#	include <xsec/enc/WinCAPI/WinCAPICryptoProvider.hpp>
#	include <xsec/enc/WinCAPI/WinCAPICryptoSymmetricKey.hpp>
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
	cerr << "     --decrypt/-d\n";
	cerr << "         Operate in decrypt mode (default) - outputs the decrypted octet stream\n";
	cerr << "         Reads in the input file as an XML file, searches for an EncryptedData node\n";
	cerr << "         and decrypts the content\n";
	cerr << "     --decrypt-element/-de\n";
	cerr << "         Operate in decrypt and XML mode.\n";
	cerr << "         This will output the original XML document with the first encrypted\n";
	cerr << "         element decrypted.\n";
	cerr << "     --encrypt-file/-de\n";
	cerr << "         Encrypt the contents of the input file as raw data and create an\n";
	cerr << "         XML Encrypted Data outpu\n";
	cerr << "     --key/-k [kek] <KEY_TYPE> [options]\n";
	cerr << "         Set the key to use.\n";
	cerr << "             If the first parameter is \"kek\", the key arguments will be used\n";
	cerr << "                  as a Key EncryptionKey\n";
	cerr << "             KEY_TYPE defines what the key is.  Can be one of :\n";
	cerr << "                  X509, RSA, AES128, AES192, AES256 or 3DES\n";
	cerr << "             options are :\n";
	cerr << "                  <filename> - for X509 PEM files (must be an RSA KEK certificate\n";
	cerr << "                  <filename> <password> - for RSA private key files (MUST be a KEK)\n";
	cerr << "                  <key-string> - For a string to use as the key for AES or DES keys\n";
	cerr << "     --interop/-i\n";
	cerr << "         Use the interop resolver for Baltimore interop examples\n";
	cerr << "     --out-file/-o\n";
	cerr << "         Output the result to the indicated file (rather than stdout)\n";
#if defined (HAVE_OPENSSL) && defined (HAVE_WINCAPI)
	cerr << "     --wincapi/-w\n";
	cerr << "         Use Windows Crypto API rather than OpenSSL\n";
#endif

	cerr << "\n     Exits with codes :\n";
	cerr << "         0 = Decrypt/Encrypt OK\n";
	cerr << "         1 = Decrypt/Encrypt failed\n";
	cerr << "         2 = Processing error\n";

}

int evaluate(int argc, char ** argv) {
	
	char					* filename = NULL;
	char					* outfile = NULL;
	unsigned char			* keyStr = NULL;
	bool					doDecrypt = true;
	bool					errorsOccured = false;
	bool					doDecryptElement = false;
	bool					useInteropResolver = false;
	bool					encryptFileAsData = false;
	bool					parseXMLInput = true;
	bool					doXMLOutput = false;
	XSECCryptoKey			* kek = NULL;
	XSECCryptoKey			* key = NULL;
	int						keyLen;
	encryptionMethod		kekAlg;
	encryptionMethod		keyAlg;
	DOMDocument				*doc;
	unsigned char			keyBuf[24];
	XMLFormatTarget			*formatTarget ;

#if defined(_WIN32) && defined (HAVE_WINCAPI)
	HCRYPTPROV				win32DSSCSP = 0;		// Crypto Providers
	HCRYPTPROV				win32RSACSP = 0;

	CryptAcquireContext(&win32DSSCSP, NULL, NULL, PROV_DSS, CRYPT_VERIFYCONTEXT);
	CryptAcquireContext(&win32RSACSP, NULL, NULL, PROV_RSA_FULL, CRYPT_VERIFYCONTEXT);

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
			doDecrypt = true;
			doDecryptElement = true;
			doXMLOutput = true;
			parseXMLInput = true;
		}
		else if (stricmp(argv[paramCount], "--interop") == 0 || stricmp(argv[paramCount], "-i") == 0) {
			// Use the interop key resolver
			useInteropResolver = true;
			paramCount++;
		}
		else if (stricmp(argv[paramCount], "--encrypt-file") == 0) {
			// Use this file as the input
			doDecrypt = false;
			encryptFileAsData = true;
			doXMLOutput = true;
			parseXMLInput = false;
			paramCount++;
		}
		else if (stricmp(argv[paramCount], "--out-file") == 0 || stricmp(argv[paramCount], "-o") == 0) {
			if (paramCount +2 >= argc) {
				printUsage();
				return 1;
			}
			paramCount++;
			outfile = argv[paramCount];
			paramCount++;
		}

#if defined (HAVE_WINCAPI) && defined (HAVE_OPENSSL)
		else if (stricmp(argv[paramCount], "--wincapi") == 0 || stricmp(argv[paramCount], "-w") == 0) {
			// Use the interop key resolver
			WinCAPICryptoProvider * cp = new WinCAPICryptoProvider();
			XSECPlatformUtils::SetCryptoProvider(cp);
			paramCount++;
		}
#endif
		else if (stricmp(argv[paramCount], "--key") == 0 || stricmp(argv[paramCount], "-k") == 0) {

			// Have a key!
			paramCount++;
			bool isKEK = false;
			XSECCryptoSymmetricKey::SymmetricKeyType loadKeyAs;

			if (stricmp(argv[paramCount], "kek") == 0) {
				isKEK = true;
				paramCount++;
				if (paramCount >= argc) {
					printUsage();
					return 2;
				}
			}

			if (stricmp(argv[paramCount], "3DES") == 0 ||
				stricmp(argv[paramCount], "AES128") == 0 ||
				stricmp(argv[paramCount], "AES192") == 0 ||
				stricmp(argv[paramCount], "AES256") == 0 ) {
				
				if (paramCount +2 >= argc) {
					printUsage();
					return 2;
				}

				switch(argv[paramCount][4]) {
				case '\0' :
					keyLen = 24;
					loadKeyAs = XSECCryptoSymmetricKey::KEY_3DES_CBC_192;
					keyAlg = ENCRYPT_3DES_CBC;
					break;
				case '2' :
					keyLen = 16;
					if (isKEK) {
						loadKeyAs = XSECCryptoSymmetricKey::KEY_AES_ECB_128;
						kekAlg = ENCRYPT_KW_AES128;
					}
					else {
						loadKeyAs = XSECCryptoSymmetricKey::KEY_AES_CBC_128;
						keyAlg = ENCRYPT_AES128_CBC;
					}
					break;
				case '9' :
					keyLen = 24;
					if (isKEK) {
						loadKeyAs = XSECCryptoSymmetricKey::KEY_AES_ECB_192;
						kekAlg = ENCRYPT_KW_AES192;
					}
					else {
						loadKeyAs = XSECCryptoSymmetricKey::KEY_AES_CBC_192;
						keyAlg = ENCRYPT_AES192_CBC;
					}
					break;
				case '5' :
					keyLen = 32;
					if (isKEK) {
						loadKeyAs = XSECCryptoSymmetricKey::KEY_AES_ECB_256;
						kekAlg = ENCRYPT_KW_AES256;
					}
					else {
						loadKeyAs = XSECCryptoSymmetricKey::KEY_AES_CBC_256;
						keyAlg = ENCRYPT_AES256_CBC;
					}
					break;
				}

				paramCount++;
				keyStr = (unsigned char *) argv[paramCount];
				paramCount++;
				XSECCryptoSymmetricKey * sk = 
					XSECPlatformUtils::g_cryptoProvider->keySymmetric(loadKeyAs);
				sk->setKey((unsigned char *) keyStr, keyLen);
				if (isKEK)
					kek = sk;
				else
					key = sk;
			}



#if defined (HAVE_OPENSSL)

			else if (stricmp(argv[paramCount], "RSA") == 0) {
				// RSA private key file

				if (paramCount + 3 >= argc) {

					printUsage();
					return 2;

				}

				if (!isKEK) {
					cerr << "RSA private keys may only be KEKs\n";
					return 2;
				}

				BIO * bioKey;
				if ((bioKey = BIO_new(BIO_s_file())) == NULL) {

					cerr << "Error opening private key file\n\n";
					return 1;

				}

				if (BIO_read_filename(bioKey, argv[paramCount + 1]) <= 0) {

					cerr << "Error opening private key file\n\n";
					return 1;

				}

				EVP_PKEY * pkey;
				pkey = PEM_read_bio_PrivateKey(bioKey,NULL,NULL,argv[paramCount + 2]);

				if (pkey == NULL) {

					cerr << "Error loading private key\n\n";
					return 1;

				}

				kek = new OpenSSLCryptoKeyRSA(pkey);
				kekAlg = ENCRYPT_RSA_15;
				EVP_PKEY_free(pkey);
				BIO_free(bioKey);
				paramCount += 3;
			}

			else if (stricmp(argv[paramCount], "X509") == 0) {

				// X509 cert used to load an encrypting key

				if (paramCount + 2 >= argc) {

					printUsage();
					exit (1);

				}

				if (!isKEK) {
					cerr << "X509 private keys may only be KEKs\n";
					return 2;
				}

				// Load the encrypting key
				// For now just read a particular file

				BIO * bioX509;

				if ((bioX509 = BIO_new(BIO_s_file())) == NULL) {

					cerr << "Error opening file\n\n";
					exit (1);

				}

				if (BIO_read_filename(bioX509, argv[paramCount + 1]) <= 0) {

					cerr << "Error opening X509 Certificate " << argv[paramCount + 1] << "\n\n";
					exit (1);

				}

				X509 * x
					;
				x = PEM_read_bio_X509_AUX(bioX509,NULL,NULL,NULL);

				if (x == NULL) {

					BIO * bio_err;
					
					if ((bio_err=BIO_new(BIO_s_file())) != NULL)
						BIO_set_fp(bio_err,stderr,BIO_NOCLOSE|BIO_FP_TEXT);

					cerr << "Error loading certificate key\n\n";
					ERR_print_errors(bio_err);
					BIO_free(bio_err);
					exit (1);

				}

				// Now load the key
				EVP_PKEY *pkey;

				pkey = X509_get_pubkey(x);

				if (pkey == NULL || pkey->type != EVP_PKEY_RSA) {
					cerr << "Error extracting RSA key from certificate" << endl;
				}

				kek = new OpenSSLCryptoKeyRSA(pkey);
				kekAlg = ENCRYPT_RSA_15;

				// Clean up

				EVP_PKEY_free (pkey);
				X509_free(x);
				BIO_free(bioX509);

				paramCount += 2;
				
			} /* argv[1] = "--x509cert" */
			else {
				printUsage();
				return 2;
			}
		}

#endif /* HAVE_OPENSSL */.

		else {
			cerr << "Unknown option: " << argv[paramCount] << endl;
			printUsage();
			return 2;
		}
	}

	if (paramCount >= argc) {
		printUsage();
		return 2;
	}

	if (outfile != NULL) {
		formatTarget = new LocalFileFormatTarget(outfile);
	}
	else {
		formatTarget = new StdOutFormatTarget();
	}

	filename = argv[paramCount];

	if (parseXMLInput) {

		XercesDOMParser * parser = new XercesDOMParser;
		Janitor<XercesDOMParser> j_parser(parser);
		
		parser->setDoNamespaces(true);
		parser->setCreateEntityReferenceNodes(true);

		// Now parse out file

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
		
		doc = parser->adoptDocument();
	}

	else {
		// Create an empty document
		XMLCh tempStr[100];
		XMLString::transcode("Core", tempStr, 99);    
		DOMImplementation *impl = DOMImplementationRegistry::getDOMImplementation(tempStr);
		doc = impl->createDocument(
			0,                    // root element namespace URI.
			MAKE_UNICODE_STRING("ADoc"),            // root element name
			NULL);// DOMDocumentType());  // document type object (DTD).
	}


	XSECProvider prov;
	XENCCipher * cipher = prov.newCipher(doc);

	if (kek != NULL)
		cipher->setKEK(kek);
	if (key != NULL)
		cipher->setKey(key);

	try {

		if (doDecrypt) {

			if (useInteropResolver == true) {

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
				int lastSlash = 0;
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

				MerlinFiveInteropResolver ires(&(uri.getUriText()[8]));
				cipher->setKeyInfoResolver(&ires);

			}
			// Find the EncryptedData node
			DOMNode * n = findXENCNode(doc, "EncryptedData");

			if (doDecryptElement) {
				// Find the EncryptedData node
				cipher->decryptElement(static_cast<DOMElement *>(n));

			}
			else {
				XSECBinTXFMInputStream * bis = cipher->decryptToBinInputStream(static_cast<DOMElement *>(n));
	
				XMLByte buf[1024];			
				unsigned int read = bis->readBytes(buf, 1023);
				while (read > 0) {
					formatTarget->writeChars(buf, read, NULL);
					read = bis->readBytes(buf, 1023);
				}
				delete bis;
			}
		}
		else {

			XENCEncryptedData *xenc;

			// Encrypting
			if (kek != NULL && key == NULL) {
				XSECPlatformUtils::g_cryptoProvider->getRandom(keyBuf, 24);
				XSECCryptoSymmetricKey * k = 
					XSECPlatformUtils::g_cryptoProvider->keySymmetric(XSECCryptoSymmetricKey::KEY_3DES_CBC_192);
				k->setKey(keyBuf, 24);
				cipher->setKey(k);
				keyAlg = ENCRYPT_3DES_CBC;
				keyStr = keyBuf;
				keyLen = 24;
			}

			if (encryptFileAsData) {

				// Create a BinInputStream
#if defined(XSEC_XERCES_REQUIRES_MEMMGR)
				BinFileInputStream * is = new BinFileInputStream(filename, XMLPlatformUtils::fgMemoryManager);
#else
				BinFileInputStream * is = new BinFileInputStream(filename);
#endif
				xenc = cipher->encryptBinInputStream(is, keyAlg);

				// Replace the document element
				DOMElement * elt = doc->getDocumentElement();
				doc->replaceChild(xenc->getDOMNode(), elt);
				elt->release();
			}
			else {
				cerr << "Element encryption not yet supported" << endl;
				return (2);
			}

			// Do we encrypt a created key?
			if (kek != NULL) {
				XENCEncryptedKey *xkey = cipher->encryptKey(keyStr, keyLen, kekAlg);
				// Add to the EncryptedData
				xenc->appendEncryptedKey(xkey);
			}
		}

		if (doXMLOutput) {
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

			theSerializer->writeNode(formatTarget, *doc);
			
			cout << endl;

			delete theSerializer;
		}
	}

	catch (XSECException &e) {
		char * msg = XMLString::transcode(e.getMsg());
		cerr << "An error occured during encryption/decryption operation\n   Message: "
		<< msg << endl;
		delete [] msg;
		errorsOccured = true;
		return 2;
	}
	catch (XSECCryptoException &e) {
		cerr << "An error occured during encryption/decryption operation\n   Message: "
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
	
	if (formatTarget != NULL)
		delete formatTarget;

	doc->release();
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
