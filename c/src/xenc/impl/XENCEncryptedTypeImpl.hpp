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
 * XENCEncryptedTypeImpl := Implementation of the EncryptedType interface
 * element
 *
 * $Id$
 *
 */

#ifndef XENCENCRYPTEDTYPEIMPL_INCLUDE
#define XENCENCRYPTEDTYPEIMPL_INCLUDE

// XSEC Includes

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/xenc/XENCEncryptedType.hpp>
#include <xsec/dsig/DSIGKeyInfoList.hpp>

// Forward declarations

XSEC_DECLARE_XERCES_CLASS(DOMNode);

class XENCCipherDataImpl;
class XENCEncryptionMethodImpl;
class TXFMChain;
class XSECEnv;

class XENCEncryptedTypeImpl : public XENCEncryptedType {

public:

	XENCEncryptedTypeImpl(const XSECEnv * env);
	XENCEncryptedTypeImpl(
		const XSECEnv * env, 
		XERCES_CPP_NAMESPACE_QUALIFIER DOMElement * node
	);

	virtual ~XENCEncryptedTypeImpl();

	// Load elements
	void load();

	// Create from scratch.  LocalName is the name of the owning element

	XERCES_CPP_NAMESPACE_QUALIFIER DOMElement * createBlankEncryptedType(
						XMLCh * localName,
						XENCCipherData::XENCCipherDataType type, 
						const XMLCh * algorithm,
						const XMLCh * value);

	// Interface Methods
	virtual XENCCipherData * getCipherData(void) const;
	virtual DSIGKeyInfoList * getKeyInfoList(void) {return &m_keyInfoList;}
	virtual XENCEncryptionMethod * getEncryptionMethod(void) const;
	virtual void clearKeyInfo(void);
	virtual DSIGKeyInfoName * appendKeyName(const XMLCh * name, bool isDName = false);
	virtual void appendEncryptedKey(XENCEncryptedKey * encryptedKey);

	// Get methods
	virtual const XMLCh * getType(void) const;
	virtual const XMLCh * getMimeType(void) const;
	virtual const XMLCh * getEncoding(void) const;
	virtual XERCES_CPP_NAMESPACE_QUALIFIER DOMElement * getElement(void) const
		{return mp_encryptedTypeElement;};

	// Set methods
	virtual void setType(const XMLCh * uri);
	virtual void setMimeType(const XMLCh * mimeType);
	virtual void setEncoding(const XMLCh * uri);

protected:

	// Create the txfm list - gives as output a TXFM chain with
	// the output being the raw encrypted data

	TXFMChain * createCipherTXFMChain(void);

	// Worker function to start building the KeyInfo list
	void createKeyInfoElement(void);

	const XSECEnv				* mp_env;
	XERCES_CPP_NAMESPACE_QUALIFIER DOMElement					
								* mp_encryptedTypeElement;	// Node at head of structure
	XERCES_CPP_NAMESPACE_QUALIFIER DOMElement
								* mp_keyInfoElement;		// Any underlying KeyInfo
	XERCES_CPP_NAMESPACE_QUALIFIER DOMNode
								* mp_cipherDataElement;		// CipherData structure
	XENCCipherDataImpl			* mp_cipherData;
	XENCEncryptionMethodImpl	* mp_encryptionMethod;

	// Hold the XML Digital Signature KeyInfo list
	DSIGKeyInfoList			m_keyInfoList;

	// Type URI
	XERCES_CPP_NAMESPACE_QUALIFIER DOMNode
								* mp_typeAttr;
	XERCES_CPP_NAMESPACE_QUALIFIER DOMNode
								* mp_mimeTypeAttr;
	XERCES_CPP_NAMESPACE_QUALIFIER DOMNode
								* mp_encodingAttr;

	friend class XENCCipherImpl;

private:

	// Un-implemented

	XENCEncryptedTypeImpl();
	XENCEncryptedTypeImpl(const XENCEncryptedTypeImpl &);
	XENCEncryptedTypeImpl & operator = (const XENCEncryptedTypeImpl &);
};

#endif /* XENCENCRYPTEDTYPEIMPL_INCLUDE */
