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
 * TXFMBase64 := Class that performs a Base64 transforms
 *
 * $Id$
 *
 */

// XSEC Includes

#include <xsec/transformers/TXFMBase.hpp>
#include <xsec/enc/XSECCryptoBase64.hpp>
 
/**
 * \brief Transformer to handle base64 transforms
 * @ingroup internal
 */

class DSIG_EXPORT TXFMBase64 : public TXFMBase {

public:

	// Constructors and destructors

	TXFMBase64(XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument *doc, bool decode = true);
	~TXFMBase64();

	// Methods to get tranform output type and input requirement

	virtual TXFMBase::ioType getInputType(void);
	virtual TXFMBase::ioType getOutputType(void);
	virtual nodeType getNodeType(void);

	// Methods to set input data

	virtual void setInput(TXFMBase * newInput);

	// Methods to get output data

	virtual unsigned int readBytes(XMLByte * const toFill, const unsigned int maxToFill);
	virtual XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument *getDocument();
	virtual XERCES_CPP_NAMESPACE_QUALIFIER DOMNode *getFragmentNode();
	virtual const XMLCh * getFragmentId();
	
private:
	TXFMBase64();

	bool				m_complete;					// Is the work done
	unsigned char		m_outputBuffer[2050];		// Always keep 2K of data
	unsigned char		m_inputBuffer[1026];		// Always read 1026 bytes (encoding grows)
	unsigned int		m_remaining;				// How much data is left in the buffer?
	XSECCryptoBase64 *	mp_b64;
	bool				m_doDecode;					// Are we encoding or decoding?
};

