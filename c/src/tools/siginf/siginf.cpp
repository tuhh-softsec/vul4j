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
 * siginf := Output information about a signature found in an XML file
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
#include <xsec/dsig/DSIGReference.hpp>
#include <xsec/framework/XSECException.hpp>
#include <xsec/enc/XSECCryptoException.hpp>
#include <xsec/utils/XSECDOMUtils.hpp>
#include <xsec/enc/XSECKeyInfoResolverDefault.hpp>

#include <xsec/dsig/DSIGTransformC14n.hpp>
#include <xsec/dsig/DSIGTransformBase64.hpp>
#include <xsec/dsig/DSIGTransformXSL.hpp>
#include <xsec/dsig/DSIGTransformXPath.hpp>
#include <xsec/dsig/DSIGTransformEnvelope.hpp>

#include <xsec/dsig/DSIGTransformList.hpp>

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

XSEC_USING_XERCES(XercesDOMParser);
XSEC_USING_XERCES(XMLException);
XSEC_USING_XERCES(XMLPlatformUtils);
XSEC_USING_XERCES(DOMException);
XSEC_USING_XERCES(XMLUri);
XSEC_USING_XERCES(Janitor);

using std::cerr;
using std::cout;
using std::endl;
using std::ostream;

#ifndef XSEC_NO_XALAN

// XALAN

#include <XPath/XPathEvaluator.hpp>
#include <XalanTransformer/XalanTransformer.hpp>

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

#ifdef XSEC_NO_XALAN

ostream& operator<< (ostream& target, const XMLCh * s)
{
    char *p = XMLString::transcode(s);
    target << p;
    delete [] p;
    return target;
}

#endif

class X2C {

public:

	X2C(const XMLCh * in) {
		mp_cStr = XMLString::transcode(in);
	}
	~X2C() {
		delete[] mp_cStr;
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

inline 
void outputHashMethod(hashMethod hm) {

	switch (hm) {

	case (HASH_SHA1) :

		cout << "SHA-1";
		break;

	case (HASH_MD5) :

		cout << "MD5";
		break;

	default :

		cout << "Unknown algorithm (or not yet set)";

	}
}

void outputTransform(DSIGTransform * t, int level) {

	switch (t->getTransformType()) {

	case (TRANSFORM_BASE64) :

		cout << "Base64 Decode" << endl;
		return;

	case (TRANSFORM_C14N) : 
		cout << "c14n canonicalisation ";
		if (((DSIGTransformC14n *) t)->getCanonicalizationMethod() == CANON_C14N_NOC)
			cout << "(without comments)" << endl;
		else
			cout << "(with comments)" << endl;
		return;

	case (TRANSFORM_EXC_C14N) :

		cout << "Exclusive c14n canonicalisation ";
		if (((DSIGTransformC14n *) t)->getCanonicalizationMethod() == CANON_C14NE_NOC)
			cout << "(without comments)" << endl;
		else
			cout << "(with comments)" << endl;

		// Check for inclusive namespaces

		if (((DSIGTransformC14n *) t)->getPrefixList() != NULL) {
			levelSet(level);
			cout << "Inclusive prefixes : " << 
				X2C(((DSIGTransformC14n *) t)->getPrefixList()).str() << endl;
		}
		return;

	case (TRANSFORM_ENVELOPED_SIGNATURE) :

		cout << "enveloped signature" << endl;
		return;

	case (TRANSFORM_XPATH) :
		{
			DSIGTransformXPath * xp = (DSIGTransformXPath *) t;
			
			cout << "XPath" << endl;
			// Check for namespaces
			DOMNamedNodeMap * atts = xp->getNamespaces();

			if (atts != 0) {

				unsigned int s = atts->getLength();
				for (unsigned int i = 0 ; i < s; ++i) {
					levelSet(level);
					cout << "Namespace : " << X2C(atts->item(i)->getNodeName()).str() <<
						"=\"" << X2C(atts->item(i)->getNodeValue()).str() << "\"\n";
				}
			}
			levelSet(level);
			// Hmm - this is really a bug.  This should return a XMLCh string
			cout << "Expr : " << xp->getExpression() << endl;
			return;
		}

	case (TRANSFORM_XSLT) :
		{
			DSIGTransformXSL *xslt = (DSIGTransformXSL *) t;

			cout << "XSLT" << endl;
			// Really should serialise and output stylesheet.
			return;
			
		}

	default :

		cout << "unknown transform type" << endl;

	}

}
		
void outputReferences(DSIGReferenceList *rl, int level) {

	int s = rl->getSize();

	for (int i = 0; i < s; ++i) {
	
		levelSet(level);
		cout << "Reference " << i + 1 << " : " << endl;
		levelSet(level + 1);
		cout << "URI : \"" << X2C(rl->item(i)->getURI()).str() << "\"" << endl;
		levelSet(level + 1);
		cout << "Digest Method : ";
		outputHashMethod(rl->item(i)->getHashMethod());
		cout << endl;

		// Now the transforms
		DSIGTransformList * tl = rl->item(i)->getTransforms();
		if (tl != NULL) {

			int tlSize = tl->getSize();
			for (int j = 0 ; j < tlSize; ++j) {

				levelSet(level+1);
				cout << "Transform " << j + 1 << " : ";
				outputTransform(tl->item(j), level + 2);

			}

		}

		if (rl->item(i)->isManifest() == true) {

			levelSet(level + 1);
			cout << "Manifest References : " << endl;
			outputReferences(rl->item(i)->getManifestReferenceList(), level + 2);
			levelSet(level + 1);
			cout << "End Manifest References" << endl;

		}

	}

}

void outputSignatureInfo(DSIGSignature *sig) {

	// First get some information about the main signature
	cout << "Signature (Signed Info) settings : " << endl;
	cout << "    Canonicalisation Method : ";
	
	switch (sig->getCanonicalizationMethod()) {

	case (CANON_C14N_NOC) :

		cout << "c14n (without comments)";
		break;

	case (CANON_C14N_COM) :

		cout << "c14n (with comments)";
		break;

	case (CANON_C14NE_NOC) :

		cout << "exclusive c14n (without comments)";
		break;

	case (CANON_C14NE_COM) :

		cout << "exclusive c14n (with comments)";
		break;

	default :

		cout << "none set";
		break;

	}

	cout << endl;

	cout << "    Digest Method : ";
	outputHashMethod(sig->getHashMethod());
	cout << endl;

	cout << "    Signature Algorithm : ";
	switch (sig->getSignatureMethod()) {

	case (SIGNATURE_DSA) :

		cout << "DSA";
		break;

	case (SIGNATURE_RSA) :

		cout << "RSA";
		break;

	case (SIGNATURE_HMAC) :

		cout << "HMAC";
		break;

	default :

		cout << "Unknown (or not yet set)";

	}

	cout << endl;

	// Read in the references and output

	DSIGReferenceList * rl = sig->getReferenceList();
	
	if (rl != NULL) {

		cout << endl << "Reference List : " << endl;
		outputReferences(rl, 1);
	
	}

}

void printUsage(void) {

	cerr << "\nUsage: siging [options] <input file name>\n\n";
	cerr << "     Where options are :\n\n";
	cerr << "     --skiprefs/-s\n";
	cerr << "         Skip information on references - output main sig info only\n\n";

}

int evaluate(int argc, char ** argv) {
	
	char					* filename = NULL;
	bool					skipRefs = false;

	if (argc < 2) {

		printUsage();
		return 2;
	}

	// Run through parameters
	int paramCount = 1;

	while (paramCount < argc - 1) {

		if (stricmp(argv[paramCount], "--skiprefs") == 0 || stricmp(argv[paramCount], "-s") == 0) {
			skipRefs = true;
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
    }

    catch (const XMLException& e)
    {
		char * msg = XMLString::transcode(e.getMessage());
        cerr << "An error occured during parsing\n   Message: "
             << msg << endl;
		delete[] msg;
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
	
	DOMNode *doc = parser->getDocument();
	DOMDocument *theDOM = parser->getDocument();

	// Find the signature node
	
	DOMNode *sigNode = findDSIGNode(doc, "Signature");

	// Create the signature checker

	if (sigNode == 0) {

		cerr << "Could not find <Signature> node in " << argv[argc-1] << endl;
		return 1;
	}

	XSECProvider prov;
	DSIGSignature * sig = prov.newSignatureFromDOM(theDOM, sigNode);

	try {

		sig->load();

		// If we didn't get an exception, things went well

		cout << "Filename : " << filename << endl;

		outputSignatureInfo(sig);
//		if (skipRefs == false)
//			result = sig->verifySignatureOnly();
//		else
//			result = sig->verify();
	}

	catch (XSECException &e) {
		char * msg = XMLString::transcode(e.getMsg());
		cerr << "An error occured during signature verification\n   Message: "
		<< msg << endl;
		delete [] msg;
		errorsOccured = true;
		return 2;
	}
	catch (...) {

		cerr << "Unknown Exception type occured.  Cleaning up and exiting\n" << endl;
		return 2;

	}

	// Clean up

	prov.releaseSignature(sig);
	// Janitor will clean up the parser
	return 0;

}


int main(int argc, char **argv) {

	int retResult;

	/* We output a version number to overcome a "feature" in Microsoft's memory
	   leak detection */

	cout << "DSIG Info (Using Apache XML-Security-C Library v" << XSEC_VERSION << ")\n";

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
