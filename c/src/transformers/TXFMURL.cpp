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
 * TXFMURL := Class that takes an input URL to start of a transform pipe
 *
 * Author(s): Berin Lautenbach
 *
 * $ID$
 *
 * $LOG$
 *
 */

#include <xsec/transformers/TXFMURL.hpp>
#include <xsec/framework/XSECError.hpp>

// To catch exceptions

#include <xercesc/util/XMLNetAccessor.hpp>

//#include <util/NetAccessors/libWWW/BinURLInputStream.hpp>

// General includes 

#include <stdlib.h>

TXFMURL::TXFMURL(DOMDocument *doc, XSECURIResolver * resolver) : TXFMBase(doc) {

	is = NULL;			// To ensure later able to delete if not used properly

	mp_resolver = resolver;

	done = false;
}


TXFMURL::~TXFMURL() {

	if (is != NULL)
		delete is;
}

	// Methods to set the inputs

void TXFMURL::setInput(TXFMBase *newInput) {

	// Not possible as this needs to be the start of a tranform pipe

	return;

}

void TXFMURL::setInput(const XMLCh * URL) {

	// Assume we have already checked that this is a valid URL

	
	is = mp_resolver->resolveURI(URL);

	if (is == NULL) {

		throw XSECException(XSECException::ErrorOpeningURI,
			"An error occurred in TXFMURL when resolving URL");

	}

}


	// Methods to get tranform output type and input requirement

TXFMBase::ioType TXFMURL::getInputType(void) {

	return TXFMBase::BYTE_STREAM;

}

TXFMBase::ioType TXFMURL::getOutputType(void) {

	return TXFMBase::BYTE_STREAM;

}

TXFMBase::nodeType TXFMURL::getNodeType(void) {

	return TXFMBase::DOM_NODE_NONE;

}


	// Methods to get output data

unsigned int TXFMURL::readBytes(XMLByte * const toFill, unsigned int maxToFill) {
	
	XSEC_USING_XERCES(NetAccessorException);

	// Simply read in from the input stream

	unsigned int ret;

	if (done || is == NULL) return 0;

	try {
		ret = is->readBytes(toFill, maxToFill);
	}
	catch (NetAccessorException e) {

		// Naieve - but assume this means the socket has closed (end of stream)
		ret = 0;

	}

	//if (ret < maxToFill)
	if (ret <= 0)
		done = true;

	return ret;

}

DOMDocument *TXFMURL::getDocument() {

	return NULL;

};

DOMNode *TXFMURL::getFragmentNode() {

	return NULL;

};

const XMLCh * TXFMURL::getFragmentId() {

	return NULL;	// Empty string

}
