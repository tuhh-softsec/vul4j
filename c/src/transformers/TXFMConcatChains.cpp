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
 * TXFMConcatChain := Takes multiple input chains and then provides
 *					  BYTE_STREAM output for each chain in order.
 *
 * $Id$
 *
 */

#include <xsec/framework/XSECDefs.hpp>

#include "TXFMConcatChains.hpp"

#include <xsec/transformers/TXFMChain.hpp>
#include <xsec/framework/XSECError.hpp>

XERCES_CPP_NAMESPACE_USE

TXFMConcatChains::TXFMConcatChains(DOMDocument *doc) : 
TXFMBase(doc),
mp_currentTxfm(NULL),
m_currentChain(0),
m_complete(false) {

}

TXFMConcatChains::~TXFMConcatChains() {

	size_type i, s;
	s = m_chains.size();

	for (i = 0; i < s; ++i)
		delete m_chains[i];

	m_chains.clear();

}

void TXFMConcatChains::setInput(TXFMBase *newInput) {

	size_type i = m_chains.size();

	if (i == 0) {
		throw XSECException(XSECException::TransformInputOutputFail,
			"TXFMConcatChains::setInput - Cannot concatinate new base transform until at least one chain is added");
	}

	m_chains[i-1]->getLastTxfm()->setInput(newInput);

}

void TXFMConcatChains::setInput(TXFMChain *newInputChain) {

	m_chains.push_back(newInputChain);

}
	
TXFMBase::ioType TXFMConcatChains::getInputType(void) {

	return TXFMBase::BYTE_STREAM;

}

TXFMBase::ioType TXFMConcatChains::getOutputType(void) {

	return TXFMBase::BYTE_STREAM;

}

TXFMBase::nodeType TXFMConcatChains::getNodeType(void) {

	return TXFMBase::DOM_NODE_NONE;

}

// Methods to get output data

unsigned int TXFMConcatChains::readBytes(XMLByte * const toFill, const unsigned int maxToFill) {

	if (m_complete == true)
		return 0;

	// Is this the first one?
	if (mp_currentTxfm == NULL) {

		if (m_chains.size() == 0) {
			throw XSECException(XSECException::TransformInputOutputFail,
				"TXFMConcatChains::readBytes - Cannot read bytes until chains added");
		}

		m_currentChain = 0;
		mp_currentTxfm = m_chains[m_currentChain]->getLastTxfm();

	}

	unsigned int bytesRead = 0;
	unsigned int lastBytesRead = 0;

	while (!m_complete && bytesRead < maxToFill) {

		lastBytesRead = mp_currentTxfm->readBytes(&toFill[bytesRead], maxToFill - bytesRead);
		if (lastBytesRead == 0) {

			if (++m_currentChain == m_chains.size())
				m_complete = true;
			else
				mp_currentTxfm = m_chains[m_currentChain]->getLastTxfm();

		}
		else {
			bytesRead += lastBytesRead;
		}

	}

	return bytesRead;

}

DOMDocument *TXFMConcatChains::getDocument() {

	return NULL;

}
DOMNode *TXFMConcatChains::getFragmentNode() {

	return NULL;

}

const XMLCh * TXFMConcatChains::getFragmentId() {

	return NULL;

}
	
