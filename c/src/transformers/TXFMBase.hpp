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
 * TXFMBase := Base (virtual) class that defines a DSIG Transformer
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */

#ifndef TXFMBASE_INCLUDE
#define TXFMBASE_INCLUDE

#include <xercesc/dom/DOM.hpp>
#include <xsec/canon/XSECC14n20010315.hpp>
#include <xsec/utils/XSECNameSpaceExpander.hpp>
#include <xsec/utils/XSECXPathNodeList.hpp>

// Xalan

#include <xercesc/util/BinInputStream.hpp>

#include <stdlib.h>

class TXFMChain;

class DSIG_EXPORT TXFMBase {

protected:

	TXFMBase				*input;			// The input source that we read from
	bool					keepComments;	// Each transform needs to tell the next whether comments are still in
	XSECNameSpaceExpander	* mp_nse;		// For expanding document name spaces
	DOMDocument				* mp_expansionDoc;	// For expanding
	XSECXPathNodeList		m_XPathMap;		// For node lists if necessary

public:

	TXFMBase(DOMDocument *doc) {input = NULL; keepComments = true; mp_nse = NULL; mp_expansionDoc = doc;}
	virtual ~TXFMBase();

	// For getting/setting input/output type

	enum ioType {

		NONE			= 1,			// For when there is no Input
		BYTE_STREAM		= 2,
		DOM_NODES		= 3

	};

	enum nodeType {

		DOM_NODE_NONE				= 1,			// No nodes set
		DOM_NODE_DOCUMENT			= 2,			// This is a dom document
		DOM_NODE_DOCUMENT_FRAGMENT	= 3,			// This is a fragment only
		DOM_NODE_DOCUMENT_NODE		= 4,			// This is a fragment id;d by DOM_Node
		DOM_NODE_XPATH_NODESET		= 5 			// This is a set of nodes

	};


	// Methods to set the inputs
	// NOTE:  If this throws an exception, the implementation class
	// MUST have added the newInput to it's chain to ensure that
	// Deletion of the chain will include everything.

	virtual void setInput(TXFMBase *newInput) = 0;

	// Methods to get tranform output type and input requirement

	virtual ioType getInputType(void) = 0;
	virtual ioType getOutputType(void) = 0;
	virtual nodeType getNodeType(void) = 0;

	// Name space expansion handling
	virtual bool nameSpacesExpanded(void);
	virtual void expandNameSpaces(void);
	void deleteExpandedNameSpaces(void);

	// Comment handling

	virtual void stripComments(void) { keepComments = false;}
	virtual void activateComments(void);
	virtual bool getCommentsStatus(void) {return keepComments;}

	// Methods to get output data
	
	// BinInputStream methods:

	virtual unsigned int readBytes(XMLByte * const toFill, const unsigned int maxToFill) = 0;
	virtual DOMDocument *getDocument() = 0;
	virtual DOMNode *getFragmentNode() = 0;
	virtual const XMLCh * getFragmentId() = 0;
	virtual XSECXPathNodeList & getXPathNodeList() {return m_XPathMap;}

	// Friends and Statics

	friend TXFMChain;


private:

	TXFMBase();
};

#endif /* #define TXFMBASE_INCLUDE */
