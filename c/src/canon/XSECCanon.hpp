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
 * XSECCanon := Base (abstract) class for canonicalisation objects
 *
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 */

// XSEC includes
#include <xsec/framework/XSECDefs.hpp>
#include <xsec/utils/XSECSafeBuffer.hpp>

XSEC_DECLARE_XERCES_CLASS(DOMNode);
XSEC_DECLARE_XERCES_CLASS(DOMDocument);

// --------------------------------------------------------------------------------
//           Defines
// --------------------------------------------------------------------------------

#define XSECCANNON_BUFFER_START_SIZE		8192		/* Default size for the outBuffer */


// --------------------------------------------------------------------------------
//           XSECCanon Virtual Class definition
// --------------------------------------------------------------------------------

// Most of the interface work is done within this class.  However the actual
// processing is done via the processNextNode virtual function that must be
// implemented by all children classes.

class CANON_EXPORT XSECCanon {

protected:

	// Data structures

	XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument	* mp_doc;		// Xerces DOM Node that defines the document
	XERCES_CPP_NAMESPACE_QUALIFIER DOMNode		* mp_startNode,	// Node to start processing from
										*mp_nextNode;	// Next Node to be processeed
	safeBuffer							m_buffer;		// Buffer holding parsed output
	int									m_bufferLength,	// Length of input currently in buffer
										m_bufferPoint;	// Next "character" to copy out
	bool								m_allNodesDone;	// Have we completed?


public:

	// Constructors

	XSECCanon();
	XSECCanon(XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument *newDoc);
	XSECCanon(
		XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument *newDoc, 
		XERCES_CPP_NAMESPACE_QUALIFIER DOMNode *newStartNode);

	// Destructors

	virtual ~XSECCanon();

	// Public Methods

	// outputBuffer is used by all canonicalisers to output the next numBytes bytes of
	// canonicalised XML to the nominated buffer

	int outputBuffer(unsigned char *outBuffer, int numBytes);
	
	// setStartNode sets the starting point for the output if it is a sub-document 
	// that needs canonicalisation and we want to re-start
	
	bool setStartNode(XERCES_CPP_NAMESPACE_QUALIFIER DOMNode *newStartNode);

protected:

	// processNextNode is the pure virtual function that must be implemented by all canons

	virtual int processNextNode() = 0;

};

