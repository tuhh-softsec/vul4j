/*
 * Copyright 2002-2005 The Apache Software Foundation.
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
 * limitations under the License.
 */

/*
 * XSEC
 *
 * TXFMSB := Class that takes an input from a safeBuffer to start a pipe
 *
 * $Id$
 *
 */

#include <xsec/transformers/TXFMBase.hpp>
#include <xsec/utils/XSECSafeBuffer.hpp>

/**
 * \brief Base transformer to start a chain from a safeBuffer
 * @ingroup internal
 */

class DSIG_EXPORT TXFMSB : public TXFMBase {

private:

	safeBuffer	sb;			// SafeBuffer to use
	xsecsize_t toOutput;	// Amount left to output
	xsecsize_t sbs;		    // Size of raw buffer

public:

	TXFMSB(XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument *doc);
	~TXFMSB();

	// Methods to set the inputs

	virtual void setInput(TXFMBase *newInput);
	void setInput(const safeBuffer sbIn);
	void setInput(const safeBuffer sbIn, unsigned int sbSize); 

	// Methods to get tranform output type and input requirement

	virtual TXFMBase::ioType getInputType(void);
	virtual TXFMBase::ioType getOutputType(void);
	virtual nodeType getNodeType(void);

	// Methods to get output data

	virtual unsigned int readBytes(XMLByte * const toFill, const unsigned int maxToFill);
	virtual XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument *getDocument();
	virtual XERCES_CPP_NAMESPACE_QUALIFIER DOMNode *getFragmentNode();
	virtual const XMLCh * getFragmentId();
	
private:
	TXFMSB();
};

