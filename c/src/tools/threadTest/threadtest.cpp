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
 * threadTest := Run up a number of threads signing and validating
 *				 the signatures.
 *
 */

// XSEC

#include <xsec/framework/XSECProvider.hpp>
#include <xsec/dsig/DSIGSignature.hpp>
#include <xsec/dsig/DSIGReference.hpp>
#include <xsec/enc/OpenSSL/OpenSSLCryptoKeyHMAC.hpp>

#include <xercesc/util/PlatformUtils.hpp>
#include <xercesc/dom/DOM.hpp>
#include <xercesc/parsers/XercesDOMParser.hpp>
#include <xercesc/util/XMLException.hpp>
#include <xercesc/util/Mutexes.hpp>
#include <xercesc/framework/StdOutFormatTarget.hpp>
#include <xercesc/framework/MemBufFormatTarget.hpp>

#ifndef XSEC_NO_XALAN

// XALAN

#include <XPath/XPathEvaluator.hpp>
#include <XalanTransformer/XalanTransformer.hpp>

XALAN_USING_XALAN(XPathEvaluator)
XALAN_USING_XALAN(XalanTransformer)

#endif

#include <strstream>
#include <iostream>
#include <queue>

using std::endl;
using std::cerr;
using std::cout;
using std::queue;
using std::vector;
using std::ostrstream;

#define numThreads	5
#define secretKey	"secret"

typedef queue<char *>	charQueueType; 

XSECProvider			* g_provider;
XMLMutex				g_providerMutex;

unsigned int			g_initCount;
XMLMutex				g_initCountMutex;

HANDLE					g_toVerifyQueueSemaphore;
XMLMutex				g_toVerifyQueueMutex;
charQueueType			g_toVerifyQueue;


void outputDoc (DOMImplementation *impl, DOMDocument * doc) {

	// Output a doc to stdout
	DOMWriter         *theSerializer = ((DOMImplementationLS*)impl)->createDOMWriter();

	theSerializer->setEncoding(MAKE_UNICODE_STRING("UTF-8"));
	if (theSerializer->canSetFeature(XMLUni::fgDOMWRTFormatPrettyPrint, true))
		theSerializer->setFeature(XMLUni::fgDOMWRTFormatPrettyPrint, true);


	XMLFormatTarget *formatTarget = new StdOutFormatTarget();

	theSerializer->writeNode(formatTarget, *doc);
	
	cout << endl;

	delete theSerializer;
	delete formatTarget;

}

void addDocToQueue (DOMImplementation *impl, DOMDocument * doc) {

	// Output a document to a memory buffer and add the buffer to
	// the queue

	DOMWriter         *theSerializer = ((DOMImplementationLS*)impl)->createDOMWriter();

	theSerializer->setEncoding(MAKE_UNICODE_STRING("UTF-8"));
	if (theSerializer->canSetFeature(XMLUni::fgDOMWRTFormatPrettyPrint, true))
		theSerializer->setFeature(XMLUni::fgDOMWRTFormatPrettyPrint, true);


	MemBufFormatTarget *formatTarget = new MemBufFormatTarget();

	theSerializer->writeNode(formatTarget, *doc);

	// Copy to a new buffer
	unsigned int len = formatTarget->getLen();
	char * buf = new char [len + 1];
	memcpy(buf, formatTarget->getRawBuffer(), len);
	buf[len] = '\0';

	// Add to the queue
	g_toVerifyQueueMutex.lock();
	g_toVerifyQueue.push(buf);
	g_toVerifyQueueMutex.unlock();

	// Signal a validate thread that this is ready to read
	ReleaseSemaphore(g_toVerifyQueueSemaphore, 1, NULL);

	delete theSerializer;
	delete formatTarget;

}


DOMText *createDocSkeleton(DOMImplementation *impl, char * tid) {

	// Create a new document skeleton that can be used by the
	// calling thread

	DOMDocument *doc = impl->createDocument(
                0,
                MAKE_UNICODE_STRING("Document"),             
                NULL);  

    DOMElement *rootElem = doc->getDocumentElement();

	// Add the thread ID element
	DOMElement * tidElem = doc->createElement(MAKE_UNICODE_STRING("ThreadID"));
	tidElem->appendChild(doc->createTextNode(MAKE_UNICODE_STRING(tid)));
	rootElem->appendChild(tidElem);
	rootElem->appendChild(doc->createTextNode(DSIGConstants::s_unicodeStrNL));

	// Create an element for the unique element
	DOMElement * uniqueElem = doc->createElement(MAKE_UNICODE_STRING("UniqueData"));
	DOMText * uniqueTextElem = doc->createTextNode(MAKE_UNICODE_STRING("preUnique"));

	rootElem->appendChild(uniqueElem);
	rootElem->appendChild(doc->createTextNode(DSIGConstants::s_unicodeStrNL));
	uniqueElem->appendChild(uniqueTextElem);

	return uniqueTextElem;
}

DWORD WINAPI doSignThread (LPVOID Param) {

	// This is called to start up a new thread

	int	theResult = 0;
	ostrstream msg;
	DOMImplementation *impl;
	DOMDocument * myDoc;
	DOMElement * myRootElem;
	DOMText * myText;

	// unsigned int counter = 0;


	impl = reinterpret_cast<DOMImplementation *>(Param);

	const DWORD		theThreadID = GetCurrentThreadId();
	msg << theThreadID << '\0';
	msg.freeze(false);
	msg.seekp(0);

	myText = createDocSkeleton(impl, msg.str());
	
	myDoc = myText->getOwnerDocument();
	myRootElem = myDoc->getDocumentElement();

	// Sign
	while (true) {

		g_providerMutex.lock();
		DSIGSignature * sig = g_provider->newSignature();
		g_providerMutex.unlock();
		DSIGReference * ref;
		DOMElement * sigNode;

		sig->setDSIGNSPrefix(MAKE_UNICODE_STRING("ds"));
		sigNode = sig->createBlankSignature(myDoc, CANON_C14N_COM, SIGNATURE_HMAC, HASH_SHA1);
		myRootElem->appendChild(sigNode);
		myRootElem->appendChild(myDoc->createTextNode(DSIGConstants::s_unicodeStrNL));
		ref = sig->createReference(MAKE_UNICODE_STRING(""));
		ref->appendEnvelopedSignatureTransform();

		sig->appendKeyName(MAKE_UNICODE_STRING("The secret key is \"secret\""));

		OpenSSLCryptoKeyHMAC * hmacKey = new OpenSSLCryptoKeyHMAC();
		hmacKey->setKey((unsigned char *) "secret", strlen("secret"));
		sig->setSigningKey(hmacKey);
		sig->sign();

		g_providerMutex.lock();
		g_provider->releaseSignature(sig);
		g_providerMutex.unlock();
		
		// Output
		addDocToQueue(impl, myDoc);
	}
	
	msg << "Sign - starting " << theThreadID << '\n' << '\0';
	cerr << msg.str();

	msg.freeze(false); 
	msg.seekp(0);
	
//	for (unsigned int i = 0; i < 10000000; ++i);

	msg << "Ending " << theThreadID << endl << '\0';
	cerr << msg.str();

	return 0;

}

DWORD WINAPI doVerifyThread (LPVOID Param) {

	// This is called to start up a new thread

	int	theResult = 0;
	ostrstream msg;
	DOMImplementation *impl;
	DOMDocument * myDoc;
	DOMText * myText;


	impl = reinterpret_cast<DOMImplementation *>(Param);

	const DWORD		theThreadID = GetCurrentThreadId();
	msg << theThreadID << '\0';
	msg.freeze(false);
	msg.seekp(0);

	//myText = createDocSkeleton(impl, msg.str());
	
	//myDoc = myText->getOwnerDocument();

	// Output
	//outputDoc(impl, myDoc);

	// Wait for a semaphore event
	WaitForSingleObject( 
        g_toVerifyQueueSemaphore ,   // handle to semaphore
        INFINITE);

	msg << "Verify - starting " << theThreadID << '\n' << '\0';
	cerr << msg.str();

	msg.freeze(false); 
	msg.seekp(0);
	
	// Get the buffer
	g_toVerifyQueueMutex.lock();
	char * buf = g_toVerifyQueue.front();
	g_toVerifyQueue.pop();
	g_toVerifyQueueMutex.unlock();
	cerr << buf << endl;
	delete[] buf;

	msg << "Ending validate thread : " << theThreadID << endl << '\0';
	cerr << msg.str();
	msg.freeze(false);

	return 0;

}


void runThreads(DOMImplementation * impl, int nThreads) {


	// Set up the worker queue
	g_toVerifyQueueSemaphore = CreateSemaphore(NULL, 0, 100, "verifyQueue");
	
	vector<HANDLE>	hThreads;

	hThreads.reserve(nThreads);

	cerr << endl << "Clock before starting threads: " << clock() << endl;

	int		i = 0;	

	for (; i < nThreads; ++i)
	{
		DWORD  threadID;

		const HANDLE	hThread = CreateThread(
				0, 
				4096,							// Stack size for thread.
				doSignThread,					// pointer to thread function
				reinterpret_cast<LPVOID>(impl),	// argument for new thread
				0,								// creation flags
				&threadID);

		assert(hThread != 0);

		hThreads.push_back(hThread);
	}

	for (; i < nThreads * 2; ++i)
	{
		DWORD  threadID;

		const HANDLE	hThread = CreateThread(
				0, 
				4096,							// Stack size for thread.
				doVerifyThread,					// pointer to thread function
				reinterpret_cast<LPVOID>(impl),	// argument for new thread
				0,								// creation flags
				&threadID);

		assert(hThread != 0);

		hThreads.push_back(hThread);
	}


	WaitForMultipleObjects(hThreads.size(), &hThreads[0], TRUE, INFINITE);

	cerr << endl << "Clock after threads: " << clock() << endl;

	for (i = 0; i < nThreads; ++i)
	{
		CloseHandle(hThreads[i]);
	}
}


int main (int argc, char ** argv) {


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

	// Create a single implementation
    DOMImplementation *impl = DOMImplementationRegistry::getDOMImplementation(MAKE_UNICODE_STRING("core"));

	// Initialise that which needs to be initialised prior to thread startup
	g_provider = new XSECProvider;

	runThreads(impl, numThreads);

	// Clean up

	delete g_provider;

	XSECPlatformUtils::Terminate();
#ifndef XSEC_NO_XALAN
	XalanTransformer::terminate();
	XPathEvaluator::terminate();
#endif
	XMLPlatformUtils::Terminate();

	return 0;

}
