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
 * XENCEncryptionMethod := Interface definition for EncryptionMethod element
 *
 * $Id$
 *
 */

#ifndef XENCENCRYPTIONMETHODIMPL_INCLUDE
#define XENCENCRYPTIONMETHODIMPL_INCLUDE

// XSEC Includes

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/xenc/XENCEncryptionMethod.hpp>

class XSECEnv;

XSEC_DECLARE_XERCES_CLASS(DOMNode);

class XENCEncryptionMethodImpl : public XENCEncryptionMethod {

public:

	XENCEncryptionMethodImpl(const XSECEnv * env);
	XENCEncryptionMethodImpl(
		const XSECEnv * env, 
		XERCES_CPP_NAMESPACE_QUALIFIER DOMNode * node
	);
	virtual ~XENCEncryptionMethodImpl();

	// Load elements
	void load();

	// Create from scratch
	XERCES_CPP_NAMESPACE_QUALIFIER DOMElement * createBlankEncryptedMethod(
						const XMLCh * algorithm);

	// Interface
	const XMLCh * getAlgorithm(void) {return mp_algorithm;}
	virtual XERCES_CPP_NAMESPACE_QUALIFIER DOMNode * getDOMNode(void)
		{return mp_encryptionMethodNode;}
	virtual const XMLCh * getDigestMethod(void);
	virtual const XMLCh * getOAEPparams(void);
	virtual void setDigestMethod(const XMLCh * method);
	virtual void setOAEPparams(const XMLCh * params);



private:

	// Unimplemented
	XENCEncryptionMethodImpl();
	XENCEncryptionMethodImpl(const XENCEncryptionMethodImpl &);
	XENCEncryptionMethodImpl & operator = (const XENCEncryptionMethodImpl &);

	const XSECEnv				* mp_env;
	XERCES_CPP_NAMESPACE_QUALIFIER DOMNode					
								* mp_encryptionMethodNode;	// Node at head of structure
	XMLCh						* mp_algorithm;
	XERCES_CPP_NAMESPACE_QUALIFIER DOMNode					
								* mp_digestAlgorithmAttributeNode;
	XERCES_CPP_NAMESPACE_QUALIFIER DOMNode					
								* mp_oaepParamsTextNode;
};

#endif /* XENCENCRYPTIONMETHODIMPL_INCLUDE */
