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
 * templatesign := tool to sign a template XML signature file
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */

//XSEC includes
// XSEC

#include <xsec/utils/XSECPlatformUtils.hpp>
#include <xsec/framework/XSECProvider.hpp>
#include <xsec/canon/XSECC14n20010315.hpp>
#include <xsec/dsig/DSIGSignature.hpp>
#include <xsec/dsig/DSIGKeyInfoX509.hpp>
#include <xsec/framework/XSECException.hpp>
#include <xsec/utils/XSECDOMUtils.hpp>
#include <xsec/enc/OpenSSL/OpenSSLCryptoKeyDSA.hpp>
#include <xsec/enc/OpenSSL/OpenSSLCryptoKeyRSA.hpp>
#include <xsec/enc/OpenSSL/OpenSSLCryptoKeyHMAC.hpp>
#include <xsec/enc/OpenSSL/OpenSSLCryptoX509.hpp>

// OpenSSL

#include <openssl/bio.h>
#include <openssl/dsa.h>
#include <openssl/err.h>
#include <openssl/evp.h>
#include <openssl/pem.h>


#if defined(_WIN32)
#	include <xsec/enc/WinCAPI/WinCAPICryptoProvider.hpp>
#	include <xsec/enc/WinCAPI/WinCAPICryptoKeyDSA.hpp>
#endif

#include <memory.h>
#include <string.h>
#include <iostream>
#include <stdlib.h>

#include <xercesc/util/PlatformUtils.hpp>
#include <xercesc/util/XMLString.hpp>

#include <xercesc/dom/DOM.hpp>
#include <xercesc/parsers/XercesDOMParser.hpp>
#include <xercesc/util/XMLException.hpp>

#ifndef XSEC_NO_XALAN

// XALAN

#include <XPath/XPathEvaluator.hpp>
#include <XalanTransformer/XalanTransformer.hpp>

XALAN_USING_XALAN(XPathEvaluator)
XALAN_USING_XALAN(XalanTransformer)

using std::ostream;
using std::cout;
using std::cerr;
using std::endl;
using std::flush;

#endif

// Uplift entire program into Xerces namespace

XERCES_CPP_NAMESPACE_USE

// --------------------------------------------------------------------------------
//           Much code taken from the DOMPrint Xerces example
// --------------------------------------------------------------------------------

static XMLFormatter*            gFormatter             = 0;
static XMLCh*                   gEncodingName          = 0;
static XMLFormatter::UnRepFlags gUnRepFlags            = XMLFormatter::UnRep_CharRef;




static const XMLCh  gEndElement[] = { chOpenAngle, chForwardSlash, chNull };
static const XMLCh  gEndPI[] = { chQuestion, chCloseAngle, chNull};
static const XMLCh  gStartPI[] = { chOpenAngle, chQuestion, chNull };
static const XMLCh  gXMLDecl1[] =
{
        chOpenAngle, chQuestion, chLatin_x, chLatin_m, chLatin_l
    ,   chSpace, chLatin_v, chLatin_e, chLatin_r, chLatin_s, chLatin_i
    ,   chLatin_o, chLatin_n, chEqual, chDoubleQuote, chNull
};
static const XMLCh  gXMLDecl2[] =
{
        chDoubleQuote, chSpace, chLatin_e, chLatin_n, chLatin_c
    ,   chLatin_o, chLatin_d, chLatin_i, chLatin_n, chLatin_g, chEqual
    ,   chDoubleQuote, chNull
};
static const XMLCh  gXMLDecl3[] =
{
        chDoubleQuote, chSpace, chLatin_s, chLatin_t, chLatin_a
    ,   chLatin_n, chLatin_d, chLatin_a, chLatin_l, chLatin_o
    ,   chLatin_n, chLatin_e, chEqual, chDoubleQuote, chNull
};
static const XMLCh  gXMLDecl4[] =
{
        chDoubleQuote, chQuestion, chCloseAngle
    ,   chLF, chNull
};

static const XMLCh  gStartCDATA[] =
{
        chOpenAngle, chBang, chOpenSquare, chLatin_C, chLatin_D,
        chLatin_A, chLatin_T, chLatin_A, chOpenSquare, chNull
};

static const XMLCh  gEndCDATA[] =
{
    chCloseSquare, chCloseSquare, chCloseAngle, chNull
};
static const XMLCh  gStartComment[] =
{
    chOpenAngle, chBang, chDash, chDash, chNull
};

static const XMLCh  gEndComment[] =
{
    chDash, chDash, chCloseAngle, chNull
};

static const XMLCh  gStartDoctype[] =
{
    chOpenAngle, chBang, chLatin_D, chLatin_O, chLatin_C, chLatin_T,
    chLatin_Y, chLatin_P, chLatin_E, chSpace, chNull
};
static const XMLCh  gPublic[] =
{
    chLatin_P, chLatin_U, chLatin_B, chLatin_L, chLatin_I,
    chLatin_C, chSpace, chDoubleQuote, chNull
};
static const XMLCh  gSystem[] =
{
    chLatin_S, chLatin_Y, chLatin_S, chLatin_T, chLatin_E,
    chLatin_M, chSpace, chDoubleQuote, chNull
};
static const XMLCh  gStartEntity[] =
{
    chOpenAngle, chBang, chLatin_E, chLatin_N, chLatin_T, chLatin_I,
    chLatin_T, chLatin_Y, chSpace, chNull
};
static const XMLCh  gNotation[] =
{
    chLatin_N, chLatin_D, chLatin_A, chLatin_T, chLatin_A,
    chSpace, chDoubleQuote, chNull
};



// ---------------------------------------------------------------------------
//  Local classes
// ---------------------------------------------------------------------------

class DOMPrintFormatTarget : public XMLFormatTarget
{
public:
    DOMPrintFormatTarget()  {};
    ~DOMPrintFormatTarget() {};

    // -----------------------------------------------------------------------
    //  Implementations of the format target interface
    // -----------------------------------------------------------------------

    void writeChars(const   XMLByte* const  toWrite,
                    const   unsigned int    count,
                            XMLFormatter * const formatter)
    {
        // Surprisingly, Solaris was the only platform on which
        // required the char* cast to print out the string correctly.
        // Without the cast, it was printing the pointer value in hex.
        // Quite annoying, considering every other platform printed
        // the string with the explicit cast to char* below.
        cout.write((char *) toWrite, (int) count);
    };

private:
    // -----------------------------------------------------------------------
    //  Unimplemented methods.
    // -----------------------------------------------------------------------
    DOMPrintFormatTarget(const DOMPrintFormatTarget& other);
    void operator=(const DOMPrintFormatTarget& rhs);
};


// ---------------------------------------------------------------------------
//  ostream << DOMNode
//
//  Stream out a DOM node, and, recursively, all of its children. This
//  function is the heart of writing a DOM tree out as XML source. Give it
//  a document node and it will do the whole thing.
// ---------------------------------------------------------------------------
ostream& operator<<(ostream& target, DOMNode* toWrite)
{
    // Get the name and value out for convenience
    const XMLCh*   nodeName = toWrite->getNodeName();
    const XMLCh*   nodeValue = toWrite->getNodeValue();
    unsigned long lent = XMLString::stringLen(nodeValue);

    switch (toWrite->getNodeType())
    {
        case DOMNode::TEXT_NODE:
        {
            gFormatter->formatBuf(nodeValue,
                                  lent, XMLFormatter::CharEscapes);
            break;
        }


        case DOMNode::PROCESSING_INSTRUCTION_NODE :
        {
            *gFormatter << XMLFormatter::NoEscapes << gStartPI  << nodeName;
            if (lent > 0)
            {
                *gFormatter << chSpace << nodeValue;
            }
            *gFormatter << XMLFormatter::NoEscapes << gEndPI;
            break;
        }


        case DOMNode::DOCUMENT_NODE :
        {

            DOMNode *child = toWrite->getFirstChild();
            while( child != 0)
            {
                target << child;
                // add linefeed in requested output encoding
                *gFormatter << chLF;
                target << flush;
                child = child->getNextSibling();
            }
            break;
        }


        case DOMNode::ELEMENT_NODE :
        {
            // The name has to be representable without any escapes
            *gFormatter  << XMLFormatter::NoEscapes
                         << chOpenAngle << nodeName;

            // Output the element start tag.

            // Output any attributes on this element
            DOMNamedNodeMap *attributes = toWrite->getAttributes();
            int attrCount = attributes->getLength();
            for (int i = 0; i < attrCount; i++)
            {
                DOMNode  *attribute = attributes->item(i);

                //
                //  Again the name has to be completely representable. But the
                //  attribute can have refs and requires the attribute style
                //  escaping.
                //
                *gFormatter  << XMLFormatter::NoEscapes
                             << chSpace << attribute->getNodeName()
                             << chEqual << chDoubleQuote
                             << XMLFormatter::AttrEscapes
                             << attribute->getNodeValue()
                             << XMLFormatter::NoEscapes
                             << chDoubleQuote;
            }

            //
            //  Test for the presence of children, which includes both
            //  text content and nested elements.
            //
            DOMNode *child = toWrite->getFirstChild();
            if (child != 0)
            {
                // There are children. Close start-tag, and output children.
                // No escapes are legal here
                *gFormatter << XMLFormatter::NoEscapes << chCloseAngle;

                while( child != 0)
                {
                    target << child;
                    child = child->getNextSibling();
                }

                //
                // Done with children.  Output the end tag.
                //
                *gFormatter << XMLFormatter::NoEscapes << gEndElement
                            << nodeName << chCloseAngle;
            }
            else
            {
                //
                //  There were no children. Output the short form close of
                //  the element start tag, making it an empty-element tag.
                //
                *gFormatter << XMLFormatter::NoEscapes << chForwardSlash << chCloseAngle;
            }
            break;
        }


        case DOMNode::ENTITY_REFERENCE_NODE:
            {
                //DOMNode *child;
#if 0
                for (child = toWrite.getFirstChild();
                child != 0;
                child = child.getNextSibling())
                {
                    target << child;
                }
#else
                //
                // Instead of printing the refernece tree
                // we'd output the actual text as it appeared in the xml file.
                // This would be the case when -e option was chosen
                //
                    *gFormatter << XMLFormatter::NoEscapes << chAmpersand
                        << nodeName << chSemiColon;
#endif
                break;
            }


        case DOMNode::CDATA_SECTION_NODE:
            {
            *gFormatter << XMLFormatter::NoEscapes << gStartCDATA
                        << nodeValue << gEndCDATA;
            break;
        }


        case DOMNode::COMMENT_NODE:
        {
            *gFormatter << XMLFormatter::NoEscapes << gStartComment
                        << nodeValue << gEndComment;
            break;
        }


        case DOMNode::DOCUMENT_TYPE_NODE:
        {
            DOMDocumentType *doctype = (DOMDocumentType *)toWrite;;

            *gFormatter << XMLFormatter::NoEscapes  << gStartDoctype
                        << nodeName;

            const XMLCh* id = doctype->getPublicId();
            if (id != 0)
            {
                *gFormatter << XMLFormatter::NoEscapes << chSpace << gPublic
                    << id << chDoubleQuote;
                id = doctype->getSystemId();
                if (id != 0)
                {
                    *gFormatter << XMLFormatter::NoEscapes << chSpace
                       << chDoubleQuote << id << chDoubleQuote;
                }
            }
            else
            {
                id = doctype->getSystemId();
                if (id != 0)
                {
                    *gFormatter << XMLFormatter::NoEscapes << chSpace << gSystem
                        << id << chDoubleQuote;
                }
            }

            id = doctype->getInternalSubset();
            if (id !=0)
                *gFormatter << XMLFormatter::NoEscapes << chOpenSquare
                            << id << chCloseSquare;

            *gFormatter << XMLFormatter::NoEscapes << chCloseAngle;
            break;
        }


        case DOMNode::ENTITY_NODE:
        {
            *gFormatter << XMLFormatter::NoEscapes << gStartEntity
                        << nodeName;

            const XMLCh * id = ((DOMEntity *)toWrite)->getPublicId();
            if (id != 0)
                *gFormatter << XMLFormatter::NoEscapes << gPublic
                            << id << chDoubleQuote;

            id = ((DOMEntity *)toWrite)->getSystemId();
            if (id != 0)
                *gFormatter << XMLFormatter::NoEscapes << gSystem
                            << id << chDoubleQuote;

            id = ((DOMEntity *)toWrite)->getNotationName();
            if (id != 0)
                *gFormatter << XMLFormatter::NoEscapes << gNotation
                            << id << chDoubleQuote;

            *gFormatter << XMLFormatter::NoEscapes << chCloseAngle << chLF;

            break;
        }

/*
        case DOMNode::NOTATION_NODE:
        {
            const XMLCh *  str;

            *gFormatter << gXMLDecl1 << ((DOMXMLDecl *)toWrite)->getVersion();

            *gFormatter << gXMLDecl2 << gEncodingName;

            str = ((DOMXMLDecl *)toWrite)->getStandalone();
            if (str != 0)
                *gFormatter << gXMLDecl3 << str;

            *gFormatter << gXMLDecl4;

            break;
        }

*/
        default:
            cerr << "Unrecognized node type = "
                 << (long)toWrite->getNodeType() << endl;
    }
    return target;
}


// --------------------------------------------------------------------------------
//           End of outputter
// --------------------------------------------------------------------------------

class DOMMemFormatTarget : public XMLFormatTarget
{
public:
    
	unsigned char * buffer;		// Buffer to write to

	DOMMemFormatTarget()  {};
    ~DOMMemFormatTarget() {};

	void setBuffer (unsigned char * toSet) {buffer = toSet;};


    // -----------------------------------------------------------------------
    //  Implementations of the format target interface
    // -----------------------------------------------------------------------

    void writeChars(const   XMLByte* const  toWrite,
                    const   unsigned int    count,
                            XMLFormatter * const formatter)
    {
        // Surprisingly, Solaris was the only platform on which
        // required the char* cast to print out the string correctly.
        // Without the cast, it was printing the pointer value in hex.
        // Quite annoying, considering every other platform printed
        // the string with the explicit cast to char* below.
        memcpy(buffer, (char *) toWrite, (int) count);
		buffer[count] = '\0';
    };

private:
    // -----------------------------------------------------------------------
    //  Unimplemented methods.
    // -----------------------------------------------------------------------
    DOMMemFormatTarget(const DOMMemFormatTarget& other);
    void operator=(const DOMMemFormatTarget& rhs);

	
};

// ---------------------------------------------------------------------------
//  ostream << DOMString
//
//  Stream out a DOM string. Doing this requires that we first transcode
//  to char * form in the default code page for the system
// ---------------------------------------------------------------------------


DOMPrintFormatTarget *DOMtarget;
DOMMemFormatTarget *MEMtarget;
XMLFormatter *formatter, *MEMformatter;
unsigned char *charBuffer;

void printUsage(void) {

	cerr << "\nUsage: templatesign <key options> <file to sign>\n\n";
	cerr << "    Where <key options> are one of :\n\n";
	cerr << "        --dsakey/-d  <dsa private key file> <password>\n";
	cerr << "                     <dsa private key file> contains a PEM encoded private key\n";
	cerr << "                     <password> is the password used to decrypt the key file\n";
#if defined (_WIN32)
	cerr << "                     NOTE: Not usable if --wincapi previously set\n";
#endif
	cerr << "        --rsakey/-r <rsa private key file> <password>\n";
	cerr << "                     <rsa privatekey file> contains a PEM encoded private key\n";
	cerr << "                     <password> is the password used to decrypt the key file\n";
	cerr << "        --hmackey/-h <string>\n";
	cerr << "                     <string> is the hmac key to set\n";
	cerr << "        --clearkeys/-c\n";
	cerr << "                      Clears out any current KeyInfo elements in the file\n";
	cerr << "        --x509cert/-x <filename>\n";
	cerr << "                      <filename> contains a PEM certificate to be added as a KeyInfo\n";
#if defined(_WIN32)
	cerr << "        --windss/-wd\n";
	cerr << "                      Use the default user AT_SIGNATURE key from default\n";
	cerr << "                      Windows DSS CSP\n";
	cerr << "        --windsskeyinfo/-wdi\n";
	cerr << "                      Clear KeyInfo elements and insert DSS parameters from windows key\n";
#endif


}

int main(int argc, char **argv) {

	XSECCryptoKey				* key = NULL;
	DSIGKeyInfoX509				* keyInfoX509 = NULL;
	OpenSSLCryptoX509			* certs[128];
	int							certCount = 0;
	int							paramCount;
	bool						clearKeyInfo = false;
#if defined(_WIN32)
	HCRYPTPROV					win32CSP = 0;		// Crypto Provider
	bool						winDssKeyInfo = false;
	WinCAPICryptoKeyDSA			* winKey = NULL;
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

	// Initialise OpenSSL
	ERR_load_crypto_strings();
	BIO * bio_err;
	
	if ((bio_err=BIO_new(BIO_s_file())) != NULL)
		BIO_set_fp(bio_err,stderr,BIO_NOCLOSE|BIO_FP_TEXT);

	if (argc < 2) {

		printUsage();
		exit (1);
	}
	
	paramCount = 1;

	while (paramCount < argc - 1) {

		// Run through all parameters

		if (stricmp(argv[paramCount], "--dsakey") == 0 || stricmp(argv[paramCount], "-d") == 0 ||
			stricmp(argv[paramCount], "--rsakey") == 0 || stricmp(argv[paramCount], "-r") == 0) {

			// DSA or RSA OpenSSL Key

			if (paramCount + 3 >= argc) {

				printUsage();
				exit (1);

			}

			if (key != 0) {

				cerr << "\nError loading RSA or DSA key - another key already loaded\n\n";
				printUsage();
				exit(1);

			}

			// Load the signing key
			// For now just read a particular file

			BIO * bioKey;
			if ((bioKey = BIO_new(BIO_s_file())) == NULL) {

				cerr << "Error opening private key file\n\n";
				exit (1);

			}

			if (BIO_read_filename(bioKey, argv[paramCount + 1]) <= 0) {

				cerr << "Error opening private key file\n\n";
				exit (1);

			}

			EVP_PKEY * pkey;
			pkey = PEM_read_bio_PrivateKey(bioKey,NULL,NULL,argv[paramCount + 2]);

			if (pkey == NULL) {

				cerr << "Error loading private key\n\n";
				ERR_print_errors(bio_err);
				exit (1);

			}

			if (stricmp(argv[paramCount], "--dsakey") == 0 || stricmp(argv[paramCount], "-d") == 0) {

				// Check type is correct

				if (pkey->type != EVP_PKEY_DSA) {
					cerr << "DSA Key requested, but OpenSSL loaded something else\n";
					exit (1);
				}

				// Create the XSEC OpenSSL interface
				key = new OpenSSLCryptoKeyDSA(pkey);
			}
			else {
				if (pkey->type != EVP_PKEY_RSA) {
					cerr << "RSA Key requested, but OpenSSL loaded something else\n";
					exit (1);
				}
				key = new OpenSSLCryptoKeyRSA(pkey);
			}

			EVP_PKEY_free(pkey);
			BIO_free(bioKey);

			paramCount += 3;
			
		} /* argv[1] = "dsa/rsa" */


		else if (stricmp(argv[paramCount], "--x509cert") == 0 || stricmp(argv[paramCount], "-x") == 0) {

			// X509Data keyInfo

			if (paramCount + 2 >= argc) {

				printUsage();
				exit (1);

			}

			// Load the signing key
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

				cerr << "Error loading certificate key\n\n";
				ERR_print_errors(bio_err);
				exit (1);

			}

			// Create the XSEC OpenSSL interface - used only to translate to Base64

			certs[certCount++] = new OpenSSLCryptoX509(x);
			X509_free(x);
			BIO_free(bioX509);

			paramCount += 2;
			
		} /* argv[1] = "--x509cert" */

		else if (stricmp(argv[paramCount], "--hmackey") == 0 || stricmp(argv[paramCount], "-h") == 0) {

			OpenSSLCryptoKeyHMAC * hmacKey = new OpenSSLCryptoKeyHMAC();
			hmacKey->setKey((unsigned char *) argv[paramCount + 1], strlen(argv[paramCount + 1]));
			key = hmacKey;
			paramCount += 2;

		}

		else if (stricmp(argv[paramCount], "--clearkeys") == 0 || stricmp(argv[paramCount], "-c") == 0) {

			clearKeyInfo = true;
			paramCount += 1;

		}

#if defined (_WIN32)
		else if (stricmp(argv[paramCount], "--windss") == 0 || stricmp(argv[paramCount], "-wd") == 0) {
			WinCAPICryptoProvider * cp;
			// Obtain default PROV_DSS, with default user key container
			if (!CryptAcquireContext(&win32CSP,
				NULL,
				NULL,
				PROV_DSS,
				0)) {
					cerr << "Error acquiring DSS Crypto Service Provider" << endl;
					return 2;
			}
			cp = new WinCAPICryptoProvider(win32CSP);
			XSECPlatformUtils::SetCryptoProvider(cp);
			
			// Now get the key
			HCRYPTKEY k;
			BOOL fResult = CryptGetUserKey(
				win32CSP,
				AT_SIGNATURE,
				&k);

			if (!fResult || k == 0) {
				cerr << "Error obtaining default user AT_SIGNATURE key from windows DSS provider\n";
				exit(1);
			};
			winKey = new WinCAPICryptoKeyDSA(cp, k, true);
			key = winKey;
			paramCount++;
		}
		else if (stricmp(argv[paramCount], "--windsskeyinfo") == 0 || stricmp(argv[paramCount], "-wdi") == 0) {
			winDssKeyInfo = true;
			paramCount++;
		}

#endif

		else {

			printUsage();
			exit(1);

		}

	}

	// Create and set up the parser

	XercesDOMParser * parser = new XercesDOMParser;
	
	parser->setDoNamespaces(true);
	parser->setCreateEntityReferenceNodes(true);

	// Now parse out file

	bool errorsOccured = false;
	int errorCount = 0;
    try
    {
    	parser->parse(argv[argc - 1]);
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
		exit (1);

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
		exit(1);
	}


	XSECProvider prov;
	DSIGSignature * sig = prov.newSignatureFromDOM(theDOM, sigNode);

	int i;

	try {
		sig->load();
		if (clearKeyInfo == true)
			sig->clearKeyInfo();
		if (key != NULL)
			sig->setSigningKey(key);
		sig->sign();

		// Add any KeyInfo elements

#if defined(_WIN32)

		if (winDssKeyInfo == true && winKey != NULL) {
			char pBuf[1024];
			char qBuf[1024];
			char gBuf[1024];
			char yBuf[1024];

			unsigned int i;
			i = winKey->getPBase64BigNums((char *) pBuf, 1024);
			pBuf[i] = '\0';
			i = winKey->getQBase64BigNums((char *) qBuf, 1024);
			qBuf[i] = '\0';
			i = winKey->getGBase64BigNums((char *) gBuf, 1024);
			gBuf[i] = '\0';
			i = winKey->getYBase64BigNums((char *) yBuf, 1024);
			yBuf[i] = '\0';

			sig->clearKeyInfo();
			sig->appendDSAKeyValue(
				MAKE_UNICODE_STRING(pBuf),
				MAKE_UNICODE_STRING(qBuf),
				MAKE_UNICODE_STRING(gBuf),
				MAKE_UNICODE_STRING(yBuf));
		}

#endif

		if (certCount > 0) {

			// Have some certificates - see if there is already an X509 list
			DSIGKeyInfoList * kiList = sig->getKeyInfoList();
			int kiSize = kiList->getSize();

			for (i = 0; i < kiSize; ++i) {

				if (kiList->item(i)->getKeyInfoType() == DSIGKeyInfo::KEYINFO_X509) {
					keyInfoX509 = (DSIGKeyInfoX509 *) kiList->item(i);
					break;
				}
			}

			if (keyInfoX509 == 0) {

				// Not found - need to create
				keyInfoX509 = sig->appendX509Data();

			}

			for (i = 0; i < certCount; ++i) {

				keyInfoX509->appendX509Certificate(certs[i]->getDEREncodingSB().sbStrToXMLCh());

			}

		} /* certCount > 0 */
	}

	catch (XSECException &e) {
		char * m = XMLString::transcode(e.getMsg());
		cerr << "An error occured during signature verification\n   Message: "
		<< m << endl;
		delete m;
		errorsOccured = true;
		exit (1);
	}

	// Print out the result

	DOMPrintFormatTarget* formatTarget = new DOMPrintFormatTarget();
	
    const XMLCh* encNameStr = XMLString::transcode("UTF-8");
    DOMNode *aNode = doc->getFirstChild();
    if (aNode->getNodeType() == DOMNode::ENTITY_NODE)
    {
        const XMLCh* aStr = ((DOMEntity *)aNode)->getEncoding();
        if (!strEquals(aStr, ""))
        {
            encNameStr = aStr;
        }
    }
    unsigned int lent = XMLString::stringLen(encNameStr);
    gEncodingName = new XMLCh[lent + 1];
    XMLString::copyNString(gEncodingName, encNameStr, lent);
    gEncodingName[lent] = 0;

	
	
	gFormatter = new XMLFormatter("UTF-8", formatTarget,
                                          XMLFormatter::NoEscapes, gUnRepFlags);

	cout << doc;

	delete [] gEncodingName;
	delete [] (void *) encNameStr;
	delete formatTarget;

#if defined (_WIN32)
	if (win32CSP != 0)
		CryptReleaseContext(win32CSP,0);
#endif

	prov.releaseSignature(sig);
	delete parser;

	XSECPlatformUtils::Terminate();
#ifndef XSEC_NO_XALAN
	XalanTransformer::terminate();
	XPathEvaluator::terminate();
#endif
	XMLPlatformUtils::Terminate();

	
	return 0;
}
