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
 * threadTest := Run up a number of threads signing and validating
 *				 the signatures.
 *
 * Author(s): Berin Lautenbach
 *
 * $ID$
 *
 * $LOG$
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
#include <xercesc/framework/MemBufInputSource.hpp>


#include <strstream>
#include <iostream>
#include <queue>

#define WIN32_LEAN_AND_MEAN
#include <windows.h>
#include <winbase.h>

using std::endl;
using std::cerr;
using std::cout;
using std::queue;
using std::vector;
using std::ostrstream;

XSEC_USING_XERCES(XMLMutex);
XSEC_USING_XERCES(DOMImplementation);
XSEC_USING_XERCES(DOMImplementationLS);
XSEC_USING_XERCES(DOMWriter);
XSEC_USING_XERCES(XMLUni);
XSEC_USING_XERCES(StdOutFormatTarget);
XSEC_USING_XERCES(MemBufFormatTarget);
XSEC_USING_XERCES(MemBufInputSource);
XSEC_USING_XERCES(DOMText);
XSEC_USING_XERCES(XercesDOMParser);
XSEC_USING_XERCES(XMLPlatformUtils);
XSEC_USING_XERCES(XMLException);
XSEC_USING_XERCES(DOMImplementationRegistry);

// --------------------------------------------------------------------------------
//           Globals used and read by all threads
// --------------------------------------------------------------------------------


#define numThreads	7
#define secretKey	"secret"
#define sleepTime	30

typedef queue<char *>	charQueueType; 

XSECProvider			* g_provider;

HANDLE					g_toVerifyQueueSemaphore;
HANDLE					g_toSignQueueSemaphore;
XMLMutex				g_toVerifyQueueMutex;
charQueueType			g_toVerifyQueue;

// Control markers

XMLMutex				g_initMutex;
bool					g_completed;
int						g_initVerifyCount;
int						g_initSignCount;
unsigned int			g_signCount[numThreads];
unsigned int			g_verifyCount[numThreads];
unsigned int			g_errors;


// --------------------------------------------------------------------------------
//           Document manipulation functions
// --------------------------------------------------------------------------------

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
	if (theSerializer->canSetFeature(XMLUni::fgDOMWRTFormatPrettyPrint, false))
		theSerializer->setFeature(XMLUni::fgDOMWRTFormatPrettyPrint, false);


	MemBufFormatTarget *formatTarget = new MemBufFormatTarget();

	theSerializer->writeNode(formatTarget, *doc);

	// Copy to a new buffer
	unsigned int len = formatTarget->getLen();
	char * buf = new char [len + 1];
	memcpy(buf, formatTarget->getRawBuffer(), len);
	buf[len] = '\0';

	// Add to the queue (but wait for queue to be small enough)
	WaitForSingleObject(g_toSignQueueSemaphore, INFINITE);

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

	//g_providerMutex.lock();
	DOMDocument *doc = impl->createDocument(
                0,
                MAKE_UNICODE_STRING("Document"),             
                NULL);  
	//g_providerMutex.unlock();

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

// --------------------------------------------------------------------------------
//           Signing Thread
// --------------------------------------------------------------------------------


DWORD WINAPI doSignThread (LPVOID Param) {

	// This is called to start up a new thread

	int	myId;
	ostrstream msg;
	ostrstream tid;
	DOMImplementation *impl;
	DOMDocument * myDoc;
	DOMElement * myRootElem;
	DOMText * myText;

	impl = reinterpret_cast<DOMImplementation *>(Param);

	const DWORD		theThreadID = GetCurrentThreadId();
	tid << theThreadID << '\0';

	// Obtain an thread number;
	g_initMutex.lock();
	myId = g_initSignCount++;
	g_initMutex.unlock();

	g_signCount[myId] = 0;	

	// Sign
	while (g_completed == false) {

		// Create a document to sign
		myText = createDocSkeleton(impl, tid.str());
		myDoc = myText->getOwnerDocument();
		myRootElem = myDoc->getDocumentElement();


		// The provider object internally manages multiple threads
		DSIGSignature * sig = g_provider->newSignature();

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

		g_provider->releaseSignature(sig);
		
		// Serialise and add to verify queue

		addDocToQueue(impl, myDoc);

		myDoc->release();
	
		// Tell the control thread what we have done
		g_signCount[myId] += 1;	

		// Sleep for a while
		Sleep (sleepTime + (rand() % sleepTime));
	
	}
		
	msg << "Ending signing thread - " << theThreadID << endl << '\0';
	cerr << msg.str();

	// Allow the output stream memory to be released.

	tid.freeze(false);
	msg.freeze(false);

	return 0;

}

// --------------------------------------------------------------------------------
//           Verify Thread
// --------------------------------------------------------------------------------

DWORD WINAPI doVerifyThread (LPVOID Param) {

	// This is called to start up a new thread

	int	myId;
	ostrstream msg;
	DOMImplementation *impl;
	DOMDocument * myDoc;

	impl = reinterpret_cast<DOMImplementation *>(Param);

	const DWORD		theThreadID = GetCurrentThreadId();

	// Find my ID
	
	g_initMutex.lock();
	myId = g_initVerifyCount++;
	g_initMutex.unlock();

	g_verifyCount[myId] = 0;

	// Create a parser
	XercesDOMParser * parser = new XercesDOMParser;
	
	parser->setDoNamespaces(true);
	parser->setCreateEntityReferenceNodes(true);

	// Wait for a semaphore event to tell us that there is a buffer to validate

	WaitForSingleObject( 
		g_toVerifyQueueSemaphore ,   // handle to semaphore
		INFINITE);

	while (g_completed == false) {

		// Get the buffer
		g_toVerifyQueueMutex.lock();
		char * buf = g_toVerifyQueue.front();
		g_toVerifyQueue.pop();
		g_toVerifyQueueMutex.unlock();

		// Signal the signing proceses that there is room in the queue
		ReleaseSemaphore(g_toSignQueueSemaphore, 1, NULL);

		// Now parse and validate the signature
		MemBufInputSource* memIS = new MemBufInputSource ((const XMLByte*) buf, 
															strlen(buf), "XSECMem");

		parser->parse(*memIS);

		delete(memIS);

		myDoc = parser->adoptDocument();

		if ((rand() % 5) == 1) {

			// Reset the value of "UniqueData" to invalidate the signature

			DOMNode * n = myDoc->getDocumentElement()->getFirstChild();
			while (n != NULL && (n->getNodeType() != DOMNode::ELEMENT_NODE ||
				!strEquals(n->getNodeName(), "UniqueData")))
				n = n->getNextSibling();
			if (n != NULL) {
				n = n->getFirstChild();
				if (n->getNodeType() == DOMNode::TEXT_NODE) {
					n->setNodeValue(MAKE_UNICODE_STRING("bad unique data"));
				}
			}
		}

		DSIGSignature * sig = g_provider->newSignatureFromDOM(myDoc);

		OpenSSLCryptoKeyHMAC *hmacKey = new OpenSSLCryptoKeyHMAC();
		hmacKey->setKey((unsigned char *) secretKey, strlen(secretKey));
		sig->setSigningKey(hmacKey);
		sig->load();
		if (sig->verify() != true) {
			// Re-use the init Mutex to protect g_errors
			g_initMutex.lock();
			g_errors++;
			g_initMutex.unlock();
		}

		g_provider->releaseSignature(sig);

		// Delete the validated buffer
		delete[] buf;

		// Clean the doc
		myDoc->release();

		g_verifyCount[myId] += 1;

		Sleep (sleepTime + (rand() % sleepTime));

		// Wait for another object
		WaitForSingleObject( 
			g_toVerifyQueueSemaphore ,   // handle to semaphore
			INFINITE);	
	
	}

	msg << "Ending validate thread : " << theThreadID << endl << '\0';
	cerr << msg.str();
	msg.freeze(false);

	delete parser;

	return 0;

}

// --------------------------------------------------------------------------------
//           Control thread - used to shut down program
// --------------------------------------------------------------------------------


DWORD WINAPI doControlThread (LPVOID Param) {

	// Output stats and shutdown when done
	using std::cin;
	using std::cout;

	// Quick and dirty
	cin.peek();
	
	// Signal all other threads
	g_completed = true;
	ReleaseSemaphore(g_toVerifyQueueSemaphore, 5, NULL);
	ReleaseSemaphore(g_toSignQueueSemaphore, 5, NULL);

	return 0;

}

// --------------------------------------------------------------------------------
//           Output Thread
// --------------------------------------------------------------------------------


DWORD WINAPI doOutputThread (LPVOID Param) {

	// Output stats

	int i, total, lastSignTotal, lastVerifyTotal;

	lastSignTotal = lastVerifyTotal = 0;

	while (g_completed != true) {

		// Output some info
		cerr << "Signing Threads" << endl;
		cerr << "---------------" << endl << endl;
		total = 0;

		for (i = 0; i < numThreads; ++ i) {

			cerr << "Thread: " << g_signCount[i] << endl;
			total += g_signCount[i];

		}

		cerr << endl << "Total: " << total << endl;
		cerr << "Ops/Sec: " << total - lastSignTotal << endl << endl;
		lastSignTotal = total;
		
		cerr << "Verify Threads" << endl;
		cerr << "--------------" << endl << endl;
		total = 0;
		for (i = 0; i < numThreads; ++ i) {

			cerr << "Thread: " << g_verifyCount[i] << endl;
			total += g_verifyCount[i];

		}

		cerr << endl << "Total: " << total << endl;
		cerr << "Ops/Sec: " << total - lastVerifyTotal << endl << endl;
		lastVerifyTotal = total;
		cerr << "Total Errors : " << g_errors << endl;
		cerr << "Buffers in Queue : " << g_toVerifyQueue.size() << endl;
		
		// Go to sleep for a second

		Sleep(1000);
	}

	return 0;

}

// --------------------------------------------------------------------------------
//           Start up threads
// --------------------------------------------------------------------------------

void runThreads(DOMImplementation * impl, int nThreads) {


	// Set up the worker queue
	g_toVerifyQueueSemaphore = CreateSemaphore(NULL, 0, 20, "verifyQueue");
	g_toSignQueueSemaphore = CreateSemaphore(NULL, 20, 20, "signQueue");
	
	// Ensure nobody stops too soon
	g_completed = false;

	// How many signature errors do we have?
	g_errors = 0;

	int i;
	for (i = 0; i < numThreads; ++i) {
		g_signCount[i] = 0;
		g_verifyCount[i] = 0;
	}

	vector<HANDLE>	hThreads;

	hThreads.reserve(nThreads);

	i = 0;	

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

	// Start the control thread
	DWORD  threadID;

	const HANDLE	hThread = CreateThread(
			0, 
			4096,							// Stack size for thread.
			doControlThread,				// pointer to thread function
			reinterpret_cast<LPVOID>(impl),	// argument for new thread
			0,								// creation flags
			&threadID);

	assert(hThread != 0);

	hThreads.push_back(hThread);

	// Start the output thread


	const HANDLE h2Thread = CreateThread(
			0, 
			4096,							// Stack size for thread.
			doOutputThread,					// pointer to thread function
			reinterpret_cast<LPVOID>(impl),	// argument for new thread
			0,								// creation flags
			&threadID);

	assert(h2Thread != 0);

	hThreads.push_back(h2Thread);

	WaitForMultipleObjects(hThreads.size(), &hThreads[0], TRUE, INFINITE);

	for (i = 0; i < nThreads; ++i)
	{
		CloseHandle(hThreads[i]);
	}

	// Clear out the unverified buffers
	
	while (g_toVerifyQueue.size() != 0) {
		char * buf = g_toVerifyQueue.front();
		g_toVerifyQueue.pop();
		delete[] buf;
	}

}


// --------------------------------------------------------------------------------
//           Main
// --------------------------------------------------------------------------------


int main (int argc, char ** argv) {


	// Initialise the XML system

	try {

		XMLPlatformUtils::Initialize();
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
	XMLPlatformUtils::Terminate();

	return 0;

}
