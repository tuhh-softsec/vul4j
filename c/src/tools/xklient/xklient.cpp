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
#include <xsec/framework/XSECError.hpp>
#include <xsec/canon/XSECC14n20010315.hpp>
#include <xsec/dsig/DSIGSignature.hpp>
#include <xsec/dsig/DSIGKeyInfoX509.hpp>
#include <xsec/dsig/DSIGKeyInfoValue.hpp>
#include <xsec/framework/XSECException.hpp>
#include <xsec/enc/XSECCryptoException.hpp>
#include <xsec/enc/XSCrypt/XSCryptCryptoBase64.hpp>
#include <xsec/utils/XSECDOMUtils.hpp>
#include <xsec/enc/XSECKeyInfoResolverDefault.hpp>

#include <xsec/xkms/XKMSMessageAbstractType.hpp>
#include <xsec/xkms/XKMSLocateRequest.hpp>
#include <xsec/xkms/XKMSLocateResult.hpp>
#include <xsec/xkms/XKMSResult.hpp>
#include <xsec/xkms/XKMSQueryKeyBinding.hpp>
#include <xsec/xkms/XKMSKeyBinding.hpp>
#include <xsec/xkms/XKMSUseKeyWith.hpp>
#include <xsec/xkms/XKMSConstants.hpp>
#include <xsec/xkms/XKMSValidateRequest.hpp>
#include <xsec/xkms/XKMSValidateResult.hpp>

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
#include <xercesc/framework/LocalFileInputSource.hpp>
#include <xercesc/util/XMLUri.hpp>
#include <xercesc/util/Janitor.hpp>
#include <xercesc/sax/ErrorHandler.hpp>
#include <xercesc/sax/SAXParseException.hpp>
#include <xercesc/sax/EntityResolver.hpp>
#include <xercesc/sax/InputSource.hpp>

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
#	include <xsec/enc/OpenSSL/OpenSSLCryptoKeyDSA.hpp>
#	include <xsec/enc/OpenSSL/OpenSSLCryptoKeyRSA.hpp>
#	include <xsec/enc/OpenSSL/OpenSSLCryptoKeyHMAC.hpp>
#	include <xsec/enc/OpenSSL/OpenSSLCryptoX509.hpp>

#	include <openssl/bio.h>
#	include <openssl/dsa.h>
#	include <openssl/err.h>
#	include <openssl/evp.h>
#	include <openssl/pem.h>

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

#if defined (HAVE_OPENSSL)

XMLCh * BN2b64(BIGNUM * bn) {

	int bytes = BN_num_bytes(bn);
	unsigned char * binbuf = new unsigned char[bytes + 1];
	ArrayJanitor<unsigned char> j_binbuf(binbuf);

	bytes = BN_bn2bin(bn, binbuf);


	int bufLen = bytes * 4;
	int len = bufLen;
	unsigned char * buf;
	XSECnew(buf, unsigned char[bufLen]);
	ArrayJanitor<unsigned char> j_buf(buf);

	XSCryptCryptoBase64 *b64;
	XSECnew(b64, XSCryptCryptoBase64);
	Janitor<XSCryptCryptoBase64> j_b64(b64);

	b64->encodeInit();
	bufLen = b64->encode(binbuf, bytes, buf, bufLen);
	bufLen += b64->encodeFinish(&buf[bufLen], len-bufLen);
	buf[bufLen] = '\0';

	// Now translate to a bignum
	return XMLString::transcode((char *) buf);

}

#endif

DSIGKeyInfoX509 * findX509Data(DSIGKeyInfoList * lst) {

	if (lst == NULL)
		return NULL;

	int sz = lst->getSize();
	for (int i = 0; i < sz; ++i) {

		DSIGKeyInfo *ki = lst->item(i);
		if (ki->getKeyInfoType() == DSIGKeyInfo::KEYINFO_X509)
			return (DSIGKeyInfoX509*) ki;

	}

	return NULL;

}

// --------------------------------------------------------------------------------
//           ErrorHandler
// --------------------------------------------------------------------------------

class xkmsErrorHandler : public ErrorHandler {

public:
	
	xkmsErrorHandler() {}
	~xkmsErrorHandler() {}

	// Interface
	virtual void 	warning (const SAXParseException &exc);
	virtual void 	error (const SAXParseException &exc);
	virtual void 	fatalError (const SAXParseException &exc);
	virtual void 	resetErrors ();
	
private:

	void outputError(const SAXParseException &exc);

};

void xkmsErrorHandler::outputError(const SAXParseException &exc) {

	char * systemId = XMLString::transcode(exc.getSystemId());
	char * msg = XMLString::transcode(exc.getMessage());
	if (exc.getLineNumber() > 0 || exc.getColumnNumber() > 0) {
		cerr << "File: " << systemId << " Line : " << exc.getLineNumber() << " Column : " 
			<< exc.getColumnNumber() << ". " << msg << endl;
	}
	else {
		cerr << msg << endl;
	}
	XMLString::release(&msg);
	XMLString::release(&systemId);

}

void xkmsErrorHandler::warning(const SAXParseException &exc) {

	cerr << "Parser warning - ";
	outputError(exc);

}

void xkmsErrorHandler::error (const SAXParseException &exc) {

	cerr << "Parser error - ";
	outputError(exc);

}

void xkmsErrorHandler::fatalError (const SAXParseException &exc) {

	cerr << "Parser fatal error - ";
	outputError(exc);

}

void xkmsErrorHandler::resetErrors () {

}


// --------------------------------------------------------------------------------
//           Create a LocateRequest
// --------------------------------------------------------------------------------

void printLocateRequestUsage(void) {

	cerr << "\nUsage LocateRequest [--help|-h] <service URI> [options]\n";
	cerr << "   --help/-h                : print this screen and exit\n\n";
	cerr << "   --add-cert/-a <filename> : add cert in filename as a KeyInfo\n";
	cerr << "   --add-name/-n <name>     : Add name as a KeyInfoName\n\n";
	cerr << "   --add-usage-sig/-us      : Add Signature Key Usage\n";
	cerr << "   --add-usage-exc/-ux      : Add Excange Key Usage\n";
	cerr << "   --add-usage-enc/-ue      : Add Encryption Key Usage\n";
	cerr << "   --add-usekeywith/-u <Application URI> <Identifier>\n";
	cerr << "                            : Add a UseKeyWith element\n";
	cerr << "   --add-respondwith/-r <Identifier>\n";
	cerr << "                            : Add a RespondWith element\n";
	cerr << "   --sign-dsa/-sd <filename> <passphrase>\n";
	cerr << "           : Sign using the DSA key in file protected by passphrase\n\n";

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

			XKMSQueryKeyBinding * qkb = lr->getQueryKeyBinding();
			if (qkb == NULL) {
				qkb = lr->addQueryKeyBinding();
			}
			// See if there is already an X.509 element
			DSIGKeyInfoX509 * kix;
			if ((kix = findX509Data(qkb->getKeyInfoList())) == NULL)
				kix = qkb->appendX509Data();
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
		else if (stricmp(argv[paramCount], "--add-usage-sig") == 0 || stricmp(argv[paramCount], "-us") == 0) {
			XKMSQueryKeyBinding * qkb = lr->getQueryKeyBinding();
			if (qkb == NULL)
				qkb = lr->addQueryKeyBinding();
			qkb->setSignatureKeyUsage();
			paramCount++;
		}
		else if (stricmp(argv[paramCount], "--add-usage-exc") == 0 || stricmp(argv[paramCount], "-ux") == 0) {
			XKMSQueryKeyBinding * qkb = lr->getQueryKeyBinding();
			if (qkb == NULL)
				qkb = lr->addQueryKeyBinding();
			qkb->setExchangeKeyUsage();
			paramCount++;
		}
		else if (stricmp(argv[paramCount], "--add-usage-enc") == 0 || stricmp(argv[paramCount], "-ue") == 0) {
			XKMSQueryKeyBinding * qkb = lr->getQueryKeyBinding();
			if (qkb == NULL)
				qkb = lr->addQueryKeyBinding();
			qkb->setEncryptionKeyUsage();
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
		else if (stricmp(argv[paramCount], "--add-respondwith") == 0 || stricmp(argv[paramCount], "-r") == 0) {
			if (++paramCount >= argc) {
				printLocateRequestUsage();
				delete lr;
				return NULL;
			}
			lr->appendRespondWithItem(MAKE_UNICODE_STRING(argv[paramCount]));
			paramCount++;
		}
#if defined (HAVE_OPENSSL)
		else if (stricmp(argv[paramCount], "--sign-dsa") == 0 || stricmp(argv[paramCount], "-sd") == 0 ||
				stricmp(argv[paramCount], "--sign-rsa") == 0 || stricmp(argv[paramCount], "-sr") == 0) {
			if (paramCount >= argc + 2) {
				printLocateRequestUsage();
				delete lr;
				return NULL;
			}

			// DSA or RSA OpenSSL Key
			// For now just read a particular file

			BIO * bioKey;
			if ((bioKey = BIO_new(BIO_s_file())) == NULL) {

				cerr << "Error opening private key file\n\n";
				return NULL;

			}

			if (BIO_read_filename(bioKey, argv[paramCount+1]) <= 0) {

				cerr << "Error opening private key file : " << argv[paramCount+1] << endl;
				return NULL;

			}

			EVP_PKEY * pkey;
			pkey = PEM_read_bio_PrivateKey(bioKey,NULL,NULL,argv[paramCount + 2]);

			if (pkey == NULL) {

				BIO * bio_err;
	
				if ((bio_err=BIO_new(BIO_s_file())) != NULL)
					BIO_set_fp(bio_err,stderr,BIO_NOCLOSE|BIO_FP_TEXT);
				cerr << "Error loading private key\n\n";
				ERR_print_errors(bio_err);
				return NULL;

			}
			XSECCryptoKey *key;
			DSIGSignature * sig;
			if (stricmp(argv[paramCount], "--sign-dsa") == 0 || stricmp(argv[paramCount], "-sd") == 0) {

				// Check type is correct

				if (pkey->type != EVP_PKEY_DSA) {
					cerr << "DSA Key requested, but OpenSSL loaded something else\n";
					return NULL;
				}

				sig = lr->addSignature(CANON_C14N_NOC, SIGNATURE_DSA, HASH_SHA1);
				// Create the XSEC OpenSSL interface
				key = new OpenSSLCryptoKeyDSA(pkey);

				XMLCh * P = BN2b64(pkey->pkey.dsa->p);
				XMLCh * Q = BN2b64(pkey->pkey.dsa->q);
				XMLCh * G = BN2b64(pkey->pkey.dsa->g);
				XMLCh * Y = BN2b64(pkey->pkey.dsa->pub_key);

				sig->appendDSAKeyValue(P,Q,G,Y);

				XMLString::release(&P);
				XMLString::release(&Q);
				XMLString::release(&G);
				XMLString::release(&Y);
			}
			else {
				if (pkey->type != EVP_PKEY_RSA) {
					cerr << "RSA Key requested, but OpenSSL loaded something else\n";
					exit (1);
				}
				sig = lr->addSignature(CANON_C14N_NOC, SIGNATURE_RSA, HASH_SHA1);
				key = new OpenSSLCryptoKeyRSA(pkey);

				XMLCh * mod = BN2b64(pkey->pkey.rsa->n);
				XMLCh * exp = BN2b64(pkey->pkey.rsa->e);
				sig->appendRSAKeyValue(mod, exp);
				XMLString::release(&mod);
				XMLString::release(&exp);

			}

			sig->setSigningKey(key);
			sig->sign();

			EVP_PKEY_free(pkey);
			BIO_free(bioKey);

			paramCount += 3;

			
		} /* argv[1] = "dsa/rsa" */

#endif
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
//           Create a ValidateRequest
// --------------------------------------------------------------------------------

void printValidateRequestUsage(void) {

	cerr << "\nUsage ValidateRequest [--help|-h] <service URI> [options]\n";
	cerr << "   --help/-h                : print this screen and exit\n\n";
	cerr << "   --add-cert/-a <filename> : add cert in filename as a KeyInfo\n";
	cerr << "   --add-name/-n <name>     : Add name as a KeyInfoName\n";
	cerr << "   --add-usage-sig/-us      : Add Signature Key Usage\n";
	cerr << "   --add-usage-exc/-ux      : Add Excange Key Usage\n";
	cerr << "   --add-usage-enc/-ue      : Add Encryption Key Usage\n";
	cerr << "   --add-usekeywith/-u <Application URI> <Identifier>\n";
	cerr << "                            : Add a UseKeyWith element\n";
	cerr << "   --add-respondwith/-r <Identifier>\n";
	cerr << "                            : Add a RespondWith element\n";
	cerr << "   --sign-dsa/-sd <filename> <passphrase>\n";
	cerr << "           : Sign using the DSA key in file protected by passphrase\n\n";

}

XKMSMessageAbstractType * createValidateRequest(XSECProvider &prov, DOMDocument **doc, int argc, char ** argv, int paramCount) {

	if (paramCount >= argc || 
		(stricmp(argv[paramCount], "--help") == 0) ||
		(stricmp(argv[paramCount], "-h") == 0)) {

		printValidateRequestUsage();
		return NULL;
	}

	/* First create the basic request */
	XKMSMessageFactory * factory = 
		prov.getXKMSMessageFactory();
	XKMSValidateRequest * vr = 
		factory->createValidateRequest(MAKE_UNICODE_STRING(argv[paramCount++]), doc);

	while (paramCount < argc) {

		if (stricmp(argv[paramCount], "--add-cert") == 0 || stricmp(argv[paramCount], "-a") == 0) {
			if (++paramCount >= argc) {
				printValidateRequestUsage();
				delete vr;
				return NULL;
			}
			XSECCryptoX509 * x = loadX509(argv[paramCount]);
			if (x == NULL) {
				delete vr;
				(*doc)->release();
				cerr << "Error opening Certificate file : " << 
					argv[paramCount] << endl;
				return NULL;
			}

			Janitor<XSECCryptoX509> j_x(x);

			XKMSQueryKeyBinding * qkb = vr->getQueryKeyBinding();
			if (qkb == NULL) {
				qkb = vr->addQueryKeyBinding();
			}
			// See if there is already an X.509 element
			DSIGKeyInfoX509 * kix;
			if ((kix = findX509Data(qkb->getKeyInfoList())) == NULL)
				kix = qkb->appendX509Data();
			safeBuffer sb = x->getDEREncodingSB();
			kix->appendX509Certificate(sb.sbStrToXMLCh());
			paramCount++;

		}

		else if (stricmp(argv[paramCount], "--add-name") == 0 || stricmp(argv[paramCount], "-n") == 0) {
			if (++paramCount >= argc) {
				printValidateRequestUsage();
				delete vr;
				return NULL;
			}
			XKMSQueryKeyBinding * qkb = vr->addQueryKeyBinding();
			qkb->appendKeyName(MAKE_UNICODE_STRING(argv[paramCount]));
			paramCount++;
		}
		else if (stricmp(argv[paramCount], "--add-respondwith") == 0 || stricmp(argv[paramCount], "-r") == 0) {
			if (++paramCount >= argc) {
				printValidateRequestUsage();
				delete vr;
				return NULL;
			}
			vr->appendRespondWithItem(MAKE_UNICODE_STRING(argv[paramCount]));
			paramCount++;
		}
		else if (stricmp(argv[paramCount], "--add-usage-sig") == 0 || stricmp(argv[paramCount], "-us") == 0) {
			XKMSQueryKeyBinding * qkb = vr->getQueryKeyBinding();
			if (qkb == NULL)
				qkb = vr->addQueryKeyBinding();
			qkb->setSignatureKeyUsage();
			paramCount++;
		}
		else if (stricmp(argv[paramCount], "--add-usage-exc") == 0 || stricmp(argv[paramCount], "-ux") == 0) {
			XKMSQueryKeyBinding * qkb = vr->getQueryKeyBinding();
			if (qkb == NULL)
				qkb = vr->addQueryKeyBinding();
			qkb->setExchangeKeyUsage();
			paramCount++;
		}
		else if (stricmp(argv[paramCount], "--add-usage-enc") == 0 || stricmp(argv[paramCount], "-ue") == 0) {
			XKMSQueryKeyBinding * qkb = vr->getQueryKeyBinding();
			if (qkb == NULL)
				qkb = vr->addQueryKeyBinding();
			qkb->setEncryptionKeyUsage();
			paramCount++;
		}
		else if (stricmp(argv[paramCount], "--add-usekeywith") == 0 || stricmp(argv[paramCount], "-u") == 0) {
			if (++paramCount >= argc + 1) {
				printValidateRequestUsage();
				delete vr;
				return NULL;
			}
			XKMSQueryKeyBinding *qkb = vr->getQueryKeyBinding();
			if (qkb == NULL)
				qkb = vr->addQueryKeyBinding();

			qkb->appendUseKeyWithItem(MAKE_UNICODE_STRING(argv[paramCount]), MAKE_UNICODE_STRING(argv[paramCount + 1]));
			paramCount += 2;
		}
#if defined (HAVE_OPENSSL)
		else if (stricmp(argv[paramCount], "--sign-dsa") == 0 || stricmp(argv[paramCount], "-sd") == 0 ||
				stricmp(argv[paramCount], "--sign-rsa") == 0 || stricmp(argv[paramCount], "-sr") == 0) {
			if (paramCount >= argc + 2) {
				printValidateRequestUsage();
				delete vr;
				return NULL;
			}

			// DSA or RSA OpenSSL Key
			// For now just read a particular file

			BIO * bioKey;
			if ((bioKey = BIO_new(BIO_s_file())) == NULL) {

				cerr << "Error opening private key file\n\n";
				return NULL;

			}

			if (BIO_read_filename(bioKey, argv[paramCount+1]) <= 0) {

				cerr << "Error opening private key file : " << argv[paramCount+1] << endl;
				return NULL;

			}

			EVP_PKEY * pkey;
			pkey = PEM_read_bio_PrivateKey(bioKey,NULL,NULL,argv[paramCount + 2]);

			if (pkey == NULL) {

				BIO * bio_err;
	
				if ((bio_err=BIO_new(BIO_s_file())) != NULL)
					BIO_set_fp(bio_err,stderr,BIO_NOCLOSE|BIO_FP_TEXT);
				cerr << "Error loading private key\n\n";
				ERR_print_errors(bio_err);
				return NULL;

			}
			XSECCryptoKey *key;
			DSIGSignature * sig;
			if (stricmp(argv[paramCount], "--sign-dsa") == 0 || stricmp(argv[paramCount], "-sd") == 0) {

				// Check type is correct

				if (pkey->type != EVP_PKEY_DSA) {
					cerr << "DSA Key requested, but OpenSSL loaded something else\n";
					return NULL;
				}

				sig = vr->addSignature(CANON_C14N_NOC, SIGNATURE_DSA, HASH_SHA1);
				// Create the XSEC OpenSSL interface
				key = new OpenSSLCryptoKeyDSA(pkey);

				XMLCh * P = BN2b64(pkey->pkey.dsa->p);
				XMLCh * Q = BN2b64(pkey->pkey.dsa->q);
				XMLCh * G = BN2b64(pkey->pkey.dsa->g);
				XMLCh * Y = BN2b64(pkey->pkey.dsa->pub_key);

				sig->appendDSAKeyValue(P,Q,G,Y);

				XMLString::release(&P);
				XMLString::release(&Q);
				XMLString::release(&G);
				XMLString::release(&Y);
			}
			else {
				if (pkey->type != EVP_PKEY_RSA) {
					cerr << "RSA Key requested, but OpenSSL loaded something else\n";
					exit (1);
				}
				sig = vr->addSignature(CANON_C14N_NOC, SIGNATURE_RSA, HASH_SHA1);
				key = new OpenSSLCryptoKeyRSA(pkey);

				XMLCh * mod = BN2b64(pkey->pkey.rsa->n);
				XMLCh * exp = BN2b64(pkey->pkey.rsa->e);
				sig->appendRSAKeyValue(mod, exp);
				XMLString::release(&mod);
				XMLString::release(&exp);

			}

			sig->setSigningKey(key);
			sig->sign();

			EVP_PKEY_free(pkey);
			BIO_free(bioKey);

			paramCount += 3;

			
		} /* argv[1] = "dsa/rsa" */

#endif
		else {
			printValidateRequestUsage();
			delete vr;
			(*doc)->release();
			return NULL;
		}
	}

	return vr;
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


void doKeyBindingAbstractDump(XKMSKeyBindingAbstractType * msg, int level) {

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

	doKeyBindingAbstractDump((XKMSKeyBindingAbstractType *) ukb, level);

}

void doStatusReasonDump(XKMSStatus::StatusValue v, XKMSStatus *s, int level) {

	char * sr = XMLString::transcode(XKMSConstants::s_tagStatusValueCodes[v]);
	for (XKMSStatus::StatusReason i = XKMSStatus::Signature; i > XKMSStatus::ReasonUndefined; i = (XKMSStatus::StatusReason) (i-1)) {

		if (s->getStatusReason(v, i)) {
			levelSet(level);
			char * rc = XMLString::transcode(XKMSConstants::s_tagStatusReasonCodes[i]);
			cout << sr << "Reason = " << rc << endl;
			XMLString::release(&rc);
		}
	}
	XMLString::release(&sr);

}

void doKeyBindingDump(XKMSKeyBinding * kb, int level) {

	/* Dump the status */

	XKMSStatus * s = kb->getStatus();
	if (s == NULL)
		return;

	char * sr = XMLString::transcode(XKMSConstants::s_tagStatusValueCodes[s->getStatusValue()]);
	levelSet(level);
	cout << "Status = " << sr << endl;
	XMLString::release(&sr);

	/* Dump the status reasons */
	doStatusReasonDump(XKMSStatus::Valid, s, level+1);
	doStatusReasonDump(XKMSStatus::Invalid, s, level+1);
	doStatusReasonDump(XKMSStatus::Indeterminate, s, level+1);

	/* Now the actual key */
	doKeyBindingAbstractDump((XKMSKeyBindingAbstractType *) kb, level);

}

int doLocateRequestDump(XKMSLocateRequest *msg) {

	cout << endl << "This is a LocateRequest Message" << endl;
	int level = 1;
	
	doMessageAbstractTypeDump(msg, level);
	doRequestAbstractTypeDump(msg, level);

	XKMSQueryKeyBinding *qkb = msg->getQueryKeyBinding();
	if (qkb != NULL)
		doKeyBindingAbstractDump(qkb, level);

	return 0;
}

int doValidateRequestDump(XKMSValidateRequest *msg) {

	cout << endl << "This is a ValidateRequest Message" << endl;
	int level = 1;
	
	doMessageAbstractTypeDump(msg, level);
	doRequestAbstractTypeDump(msg, level);

	XKMSQueryKeyBinding *qkb = msg->getQueryKeyBinding();
	if (qkb != NULL)
		doKeyBindingAbstractDump(qkb, level);

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

int doValidateResultDump(XKMSValidateResult *msg) {

	cout << endl << "This is a ValidateResult Message" << endl;
	int level = 1;
	
	doMessageAbstractTypeDump(msg, level);
	doResultTypeDump(msg, level);

	int j;

	if ((j = msg->getKeyBindingSize()) > 0) {

		cout << endl;
		levelSet(level);
		cout << "Key Bindings" << endl << endl;

		for (int i = 0; i < j ; ++i) {

			doKeyBindingDump(msg->getKeyBindingItem(i), level + 1);

		}

	}

	return 0;
}

int doResultDump(XKMSResult *msg) {

	cout << endl << "This is a Result Message" << endl;
	int level = 1;
	
	doMessageAbstractTypeDump(msg, level);
	doResultTypeDump(msg, level);

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

		if (msg == NULL) {
			cerr << "Unable to create XKMS msg from parsed DOM\n" << endl;
			return 2;
		}

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

		case XKMSMessageAbstractType::Result :

			doResultDump(dynamic_cast<XKMSResult *>(msg));
			break;

		case XKMSMessageAbstractType::ValidateRequest :

			doValidateRequestDump(dynamic_cast<XKMSValidateRequest *>(msg));
			break;

		case XKMSMessageAbstractType::ValidateResult :

			doValidateResultDump(dynamic_cast<XKMSValidateResult *>(msg));
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

		cerr << "Unknown Exception type occured.  Cleaning up and exitting\n" << endl;
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

	cerr << "\nUsage request [options] {LocateRequest|ValidateRequest} [msg specific options]\n";
	cerr << "   --help/-h       : Print this screen and exit\n";
	cerr << "   --two-phase/-t  : Indicate Two-Phase support in the request message\n\n";

}

int doRequest(int argc, char ** argv, int paramCount) {

	XSECProvider prov;
	DOMDocument * doc;
	XKMSMessageAbstractType *msg;
	bool twoPhase = false;
	bool parmsDone = false;

	if (paramCount >= argc || 
		(stricmp(argv[paramCount], "--help") == 0) ||
		(stricmp(argv[paramCount], "-h") == 0)) {
		printDoRequestUsage();
		return -1;
	}

	while (!parmsDone) {
		if ((stricmp(argv[paramCount], "--two-phase") == 0) ||
			(stricmp(argv[paramCount], "-t") == 0)) {

			twoPhase = true;
			paramCount++;

		}
		if ((stricmp(argv[paramCount], "LocateRequest") == 0) ||
			(stricmp(argv[paramCount], "lr") == 0)) {

			XKMSLocateRequest * r = 
				dynamic_cast<XKMSLocateRequest *> (createLocateRequest(prov, &doc, argc, argv, paramCount + 1));

			if (r == NULL) {
				return -1;
			}

			if (twoPhase)
				r->appendRespondWithItem(XKMSConstants::s_tagRepresent);

			msg = r;
			parmsDone = true;

		}
		else if ((stricmp(argv[paramCount], "ValidateRequest") == 0) ||
			(stricmp(argv[paramCount], "vr") == 0)) {

			XKMSValidateRequest * r = 
				dynamic_cast<XKMSValidateRequest *> (createValidateRequest(prov, &doc, argc, argv, paramCount + 1));

			if (r == NULL) {
				return -1;
			}
			if (twoPhase)
				r->appendRespondWithItem(XKMSConstants::s_tagRepresent);

			msg = r;
			parmsDone = true;

		}
		else {

			printDoRequestUsage();
			return -1;

		}
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

		/* If two-phase - re-do the request */
		if (twoPhase) {

			XKMSMessageFactory * f = prov.getXKMSMessageFactory();
			XKMSResultType * r = f->toResultType(f->newMessageFromDOM(responseDoc->getDocumentElement()));
			if (r->getResultMajor() == XKMSResultType::Represent) {

				cerr << "Intermediate response of a two phase sequence received\n\n";

				if (g_txtOut) {
					outputDoc(responseDoc);
				}
				doParsedMsgDump(responseDoc);

				XKMSRequestAbstractType * request = f->toRequestAbstractType(msg);
				request->setOriginalRequestId(request->getId());
				request->setNonce(r->getNonce());

				responseDoc->release();
				delete r;

				responseDoc = req.doRequest(doc);

			}
		}

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


#if 0
class XMLSchemaDTDResolver : public EntityResolver { 

public: 

	XMLSchemaDTDResolver() {}
	~XMLSchemaDTDResolver() {}
	
	InputSource * resolveEntity (const XMLCh* const publicId, const XMLCh* const systemId);
	
};

InputSource * XMLSchemaDTDResolver::resolveEntity (const XMLCh* const publicId, 
												   const XMLCh* const systemId) { 
	

	
	if (strEquals(systemId, "http://www.w3.org/2001/XMLSchema")) { 	
		return new LocalFileInputSource(MAKE_UNICODE_STRING("C:\\prog\\SRC\\xml-security\\c\\Build\\Win32\\VC6\\Debug\\XMLSchema.dtd")); 
	} 
	else { 
		return NULL; 
	}

}
#endif
void printMsgDumpUsage(void) {

	cerr << "\nUsage msgdump [options] <filename>\n";
	cerr << "   --help/-h      : print this screen and exit\n";
	cerr << "   --validate/-v  : validate the input messages\n\n";
    cerr << "   filename = name of file containing XKMS msg to dump\n\n";

}

int doMsgDump(int argc, char ** argv, int paramCount) {

	char * inputFile = NULL;
	bool doValidate = false;

	if (paramCount >= argc || 
		(stricmp(argv[paramCount], "--help") == 0) ||
		(stricmp(argv[paramCount], "-h") == 0)) {
		printMsgDumpUsage();
		return -1;
	}

	while (paramCount < argc-1) {
		if ((stricmp(argv[paramCount], "--validate") == 0) ||
			(stricmp(argv[paramCount], "-v") == 0)) {

			doValidate = true;
			paramCount++;

		}
		else {

			printMsgDumpUsage();
			return -1;
		}
	}

	if (paramCount >= argc) {
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

	// Error handling
	xkmsErrorHandler xeh;
	parser->setErrorHandler(&xeh);

#if 0
	// Local load of XMLSchema.dtd
	XMLSchemaDTDResolver sdr;
	parser->setEntityResolver(&sdr);
#endif

	// Schema handling
	if (doValidate) {
		parser->setDoSchema(true);
		parser->setDoValidation(true);
		parser->setExternalSchemaLocation("http://www.w3.org/2002/03/xkms# http://www.w3.org/TR/xkms2/Schemas/xkms.xsd");
	}

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
